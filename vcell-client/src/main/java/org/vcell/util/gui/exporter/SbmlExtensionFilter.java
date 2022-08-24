package org.vcell.util.gui.exporter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;
import org.vcell.sbml.gui.ApplnSelectionAndStructureSizeInputPanel;
import org.vcell.sbml.gui.SimulationSelectionPanel;
import org.vcell.sbml.vcell.StructureSizeSolver;
import org.vcell.util.ProgrammingException;
import org.vcell.util.TokenMangler;
import org.vcell.util.UserCancelException;
import org.vcell.util.VCAssert;
import org.vcell.util.gui.DialogUtils;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.model.Structure;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.xml.XmlHelper;

@SuppressWarnings("serial")
public class SbmlExtensionFilter extends SelectorExtensionFilter {
	private final static String FNAMES[] = {".xml" ,".sbml"};
	
	private final int sbmlLevel;
	private final int sbmlVersion;

	private final boolean isSpatial;

	/**
	 * selected sim Whose Overrides Should Be Exported;
	 * optional; may be null
	 */
	private Simulation selectedSimWOSBE;
	private SimulationContext selectedSimContext;
	
	private static String makeDescription(int level, int version, boolean spatial) {
		StringBuilder sb = new StringBuilder();
		sb.append("SBML format<Level");
		sb.append(level);
		sb.append(",Version");
		sb.append(version);
		sb.append("> ");
		if (level >= 3) {
			if (spatial) {
				sb.append("Spatial ");
			}
			else{
				sb.append("Core ");
			}
		}
		sb.append("(.xml .sbml)");
		return sb.toString();
	}
	
	private static Selector[]  selectors(boolean spatial) {
		Selector selectors[] = new Selector[3];
		selectors[0] = Selector.DETERMINISTIC;
		selectors[1] = Selector.STOCHASTIC;
		if (spatial) {
			selectors[2] = Selector.SPATIAL;
		}
		else {
			selectors[2] = Selector.NONSPATIAL;
		}
		return selectors;
	}

	/**
	 * 
	 * @param level SBML level
	 * @param version SBML version
	 * @param spatial is spatial extension?
	 */
	SbmlExtensionFilter(int level, int version, boolean spatial) {
		super(FNAMES,makeDescription(level, version, spatial),selectors(spatial));
		this.sbmlLevel = level;
		this.sbmlVersion = version;
		this.isSpatial = spatial;
	}

	@Override
	public boolean requiresMoreChoices() {
		return true; 
	}

	@Override
	public void askUser(ChooseContext c) throws UserCancelException {
		BioModel bioModel = c.chosenContext.getBioModel();
		JFrame currentWindow = c.currentWindow;
		
		selectedSimWOSBE  = null;
		selectedSimContext = c.chosenContext;
		
		// get user choice of structure and its size and computes absolute sizes of compartments using the StructureSizeSolver.
		Structure[] structures = bioModel.getModel().getStructures();
		// get the nonspatial simulationContexts corresponding to names in applicableAppNameList 
		// This is needed in ApplnSelectionAndStructureSizeInputPanel

		String strucName = null;
		double structSize = 1.0;
		int structSelection = -1;
		int option = JOptionPane.CANCEL_OPTION;

		ApplnSelectionAndStructureSizeInputPanel applnStructInputPanel = null;
		while (structSelection < 0) {
			applnStructInputPanel = new ApplnSelectionAndStructureSizeInputPanel();
			applnStructInputPanel.setSimContext(c.chosenContext);
			applnStructInputPanel.setStructures(structures);
			if (applnStructInputPanel.isNeedStructureSizes()) {
				applnStructInputPanel.setPreferredSize(new java.awt.Dimension(350, 400));
				applnStructInputPanel.setMaximumSize(new java.awt.Dimension(350, 400));
				option = DialogUtils.showComponentOKCancelDialog(currentWindow, applnStructInputPanel, "Specify Structure Size to Export:");
				structSelection = applnStructInputPanel.getStructSelectionIndex();
				if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
					break;
				} else if (option == JOptionPane.OK_OPTION && structSelection < 0) {
					DialogUtils.showErrorDialog(currentWindow, "Please select a structure and set its size");
				}
			}
			else {
				structSelection = 0;  //adapt to legacy logic ...
				option = JOptionPane.OK_OPTION;
			}
		}

		if (option == JOptionPane.OK_OPTION) {
			applnStructInputPanel.applyStructureNameAndSizeValues();
			strucName = applnStructInputPanel.getSelectedStructureName();
			selectedSimContext = applnStructInputPanel.getSelectedSimContext();
			GeometryContext geoContext = selectedSimContext.getGeometryContext();
			if (!isSpatial) {
				// calculate structure Sizes only if appln is not spatial
				structSize = applnStructInputPanel.getStructureSize();
				// Invoke StructureSizeEvaluator to compute absolute sizes of compartments if all sizes are not set
				if ( (geoContext.isAllSizeSpecifiedNull() && geoContext.isAllVolFracAndSurfVolSpecifiedNull()) ||
						((strucName == null || structSize <= 0.0) && (geoContext.isAllSizeSpecifiedNull() && geoContext.isAllVolFracAndSurfVolSpecified())) ||
						(!geoContext.isAllSizeSpecifiedPositive() && geoContext.isAllVolFracAndSurfVolSpecifiedNull()) ||
						(!geoContext.isAllSizeSpecifiedPositive() && !geoContext.isAllVolFracAndSurfVolSpecified()) ||
						(geoContext.isAllSizeSpecifiedNull() && !geoContext.isAllVolFracAndSurfVolSpecified()) ) {
					DialogUtils.showErrorDialog(currentWindow, "Cannot export to SBML without compartment sizes being set. This can be automatically " +
							" computed if the absolute size of at least one compartment and the relative sizes (Surface-to-volume-ratio/Volume-fraction) " +
							" of all compartments are known. Sufficient information is not available to perform this computation." +
							"\n\nThis can be fixed by going back to the application '" + selectedSimContext.getName() + "' and setting structure sizes in the 'StructureMapping' tab.");
					throw UserCancelException.CANCEL_XML_TRANSLATION;
				} 
				if (!geoContext.isAllSizeSpecifiedPositive() && geoContext.isAllVolFracAndSurfVolSpecified()) {
					Structure chosenStructure = selectedSimContext.getModel().getStructure(strucName);
					StructureMapping chosenStructMapping = selectedSimContext.getGeometryContext().getStructureMapping(chosenStructure);
					try {
						StructureSizeSolver.updateAbsoluteStructureSizes(selectedSimContext, chosenStructure, structSize, chosenStructMapping.getSizeParameter().getUnitDefinition());
					} catch (Exception e) {
						throw new ProgrammingException("exception updating sizes",e);
					}
				}
			} else {
				if (!geoContext.isAllUnitSizeParameterSetForSpatial()) {
					DialogUtils.showErrorDialog(currentWindow, "Cannot export to SBML without compartment size ratios being set."  +
							"\n\nThis can be fixed by going back to the application '" + selectedSimContext.getName() + "' and setting structure" +
							" size ratios in the 'StructureMapping' tab.");
					throw UserCancelException.CANCEL_XML_TRANSLATION;
				}
			}

			// Select simulation whose overrides need to be exported
			// If simContext doesn't have simulations, don't pop up simulationSelectionPanel
			Simulation[] sims = bioModel.getSimulations(selectedSimContext);
			// display only those simulations that have overrides in the simulationSelectionPanel.
			Vector<Simulation> orSims = new Vector<Simulation>();
			for (int s = 0; (sims != null) && (s < sims.length); s++) {
				if (sims[s].getMathOverrides().hasOverrides()) {
					orSims.addElement(sims[s]);
				}
			}
			Simulation[] overriddenSims = orSims.toArray(new Simulation[orSims.size()]);
			if (overriddenSims.length > 0) {
				SimulationSelectionPanel simSelectionPanel = new SimulationSelectionPanel();
				simSelectionPanel.setPreferredSize(new java.awt.Dimension(600, 400));
				simSelectionPanel.setMaximumSize(new java.awt.Dimension(600, 400));
				simSelectionPanel.setSimulations(overriddenSims);
				int simOption = DialogUtils.showComponentOKCancelDialog(currentWindow, simSelectionPanel, "Select Simulation whose overrides should be exported:");
				if (simOption == JOptionPane.OK_OPTION) {
					selectedSimWOSBE = simSelectionPanel.getSelectedSimulation();
//					if (chosenSimulation != null) {
//CARRY						hashTable.put("selectedSimulation", chosenSimulation);
//					}
				} else if (simOption == JOptionPane.CANCEL_OPTION || simOption == JOptionPane.CLOSED_OPTION) {
					// User did not choose a simulation whose overrides are required to be exported.
					// Without that information, cannot export successfully into SBML, 
					// Hence canceling the entire export to SBML operation.
					throw UserCancelException.CANCEL_XML_TRANSLATION;
				}
			} 
		} else if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
			// User did not choose to set size for any structure.
			// Without that information, cannot export successfully into SBML, 
			// Hence canceling the entire export to SBML operation.
			throw UserCancelException.CANCEL_XML_TRANSLATION;
		}

		if (selectedSimWOSBE != null) {
			String selectedFileName = c.filename;
		// rename file to contain exported simulation.
			String ext = FilenameUtils.getExtension(selectedFileName);
			String base = FilenameUtils.getBaseName(selectedFileName);
			String path = FilenameUtils.getPath(selectedFileName);
			base += "_"  + TokenMangler.mangleToSName(selectedSimWOSBE.getName());
			selectedFileName = path + base + ext; 
			c.selectedFile.renameTo(new File(selectedFileName));
		}
	}
	public boolean isExportingOverrides() {
		if(selectedSimWOSBE != null) {
			return true;
		}
		return false;
	}
	public int getScanCount() {
		if (selectedSimWOSBE != null) {
			return selectedSimWOSBE.getScanCount();
		}
		return 0;
	}
	public String getSimulationOverrideName() {
		if (selectedSimWOSBE != null) {
			return selectedSimWOSBE.getName();
		}
		return "";
	}
	
	@Override
	public void writeBioModel(DocumentManager documentManager, BioModel bioModel, File exportFile, SimulationContext simulationContext) throws Exception {
		VCAssert.assertValid(selectedSimContext);
		final int sbmlPkgVersion = 0;
		boolean bRoundTripValidation = false;
		if (selectedSimWOSBE == null) {
			String resultString = XmlHelper.exportSBML(bioModel, sbmlLevel, sbmlVersion, sbmlPkgVersion, isSpatial, selectedSimContext, null, bRoundTripValidation);
			XmlUtil.writeXMLStringToFile(resultString, exportFile.getAbsolutePath(), true);
			return;
		} else {
			String originalExportFilename = exportFile.getPath();
			Files.deleteIfExists(Paths.get(originalExportFilename));
			for (int sc = 0; sc < selectedSimWOSBE.getScanCount(); sc++) {
				SimulationJob simJob = new SimulationJob(selectedSimWOSBE, sc, null);
				String resultString = XmlHelper.exportSBML(bioModel, sbmlLevel, sbmlVersion, sbmlPkgVersion, isSpatial, selectedSimContext, simJob, bRoundTripValidation);
				// Need to export each parameter scan into a separate file
				String newExportFileName = exportFile.getPath().substring(0, exportFile.getPath().indexOf(".xml")) + "_" + sc + ".xml";
				Files.deleteIfExists(Paths.get(newExportFileName));
				XmlUtil.writeXMLStringToFile(resultString, exportFile.getAbsolutePath(), true);
				exportFile.renameTo(new File(newExportFileName));
				Files.deleteIfExists(Paths.get(originalExportFilename));
			}
			return;
		}
	}
}
