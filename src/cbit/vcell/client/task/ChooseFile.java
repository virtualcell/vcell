/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.task;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;
import org.vcell.sbml.gui.ApplnSelectionAndStructureSizeInputPanel;
import org.vcell.sbml.gui.SimulationSelectionPanel;
import org.vcell.sbml.vcell.StructureSizeSolver;
import org.vcell.util.BeanUtils;
import org.vcell.util.TokenMangler;
import org.vcell.util.UserCancelException;
import org.vcell.util.VCAssert;
import org.vcell.util.document.VCDocument;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.exporter.ExtensionFilter;
import org.vcell.util.gui.exporter.FileFilters;
import org.vcell.util.gui.exporter.SelectorExtensionFilter;
import org.vcell.util.gui.exporter.SelectorExtensionFilter.Selector;
import org.vcell.util.gui.VCFileChooser;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.model.Structure;
import cbit.vcell.solver.Simulation;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class ChooseFile extends ExportTask {
	public static final String FORCE_FILE_FILTER = "FORCE_FILE_FILTER";
	/**
	 * extension user typed info file chooser
	 */
	private String extensionUserProvided = null;
	
	public ChooseFile() {
		super("Selecting export file destination", TASKTYPE_SWING_BLOCKING);
	}
	
	/**
	 * removes extension, if any and stores in {@link #extensionUserProvided}
	 * @param userProvided could be null 
	 * @return userProvided with extension removed if present
	 */
	private String recordAndRemoveExtension(String userProvided) {
		extensionUserProvided = FilenameUtils.getExtension(userProvided);
		return FilenameUtils.removeExtension(userProvided);
	}

	//reset the user preference for the default path, if needed.
	private void resetPreferredFilePath(File selectedFile, UserPreferences userPreferences) {

		String oldPath = userPreferences.getGenPref(UserPreferences.GENERAL_LAST_PATH_USED);
		String newPath = selectedFile.getParent();                 
		if (!newPath.equals(oldPath)) {
			userPreferences.setGenPref(UserPreferences.GENERAL_LAST_PATH_USED, newPath);
		}
		System.out.println("New preferred file path: " + newPath + ", Old preferred file path: " + oldPath);
	}
	
	/**
	 * warn user if file asks
	 * @param selectedFile may be null
	 * @param parent should not be null 
	 * @param userPreferences
	 */
	private void checkForOverwrites(File selectedFile, Component parent, UserPreferences userPreferences) {
		if (selectedFile != null && selectedFile.exists()) {
			String answer = PopupGenerator.showWarningDialog(parent, userPreferences, UserMessage.warn_OverwriteFile, selectedFile.getAbsolutePath());
			if (answer.equals(UserMessage.OPTION_CANCEL)){
				throw UserCancelException.CANCEL_FILE_SELECTION;
			}
		}
	}

/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws java.lang.Exception {
	VCDocument documentToExport = fetch(hashTable,DocumentToExport.EXPORT_DOCUMENT,VCDocument.class, true) ;
	File exportFile = null;
	if (documentToExport instanceof BioModel) {
		exportFile = showBioModelXMLFileChooser(hashTable);
	} else if (documentToExport instanceof MathModel) {
		exportFile = showMathModelXMLFileChooser(hashTable);
	} else if (documentToExport instanceof Geometry) {
		exportFile = showGeometryModelXMLFileChooser(hashTable);
	} else {
		throw new Exception("Unsupported document type for XML export: " + documentToExport.getClass());
	}
	//check to see if we've changed extension from what user entered
	if (extensionUserProvided  != null && !extensionUserProvided.isEmpty()) {
		String fp = exportFile.getAbsolutePath(); 
		String currentExt = FilenameUtils.getExtension( fp ); 
		if (!extensionUserProvided.equals(currentExt)) {
			hashTable.put(RENAME_KEY, fp); 
		}
	}
	
	hashTable.put(EXPORT_FILE, exportFile);
}

/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 */
private File showBioModelXMLFileChooser(Hashtable<String, Object> hashTable) throws java.lang.Exception {
	BioModel bioModel = (BioModel)hashTable.get("documentToExport");
	JFrame currentWindow = (JFrame)hashTable.get("currentWindow");
	UserPreferences userPreferences = (UserPreferences)hashTable.get("userPreferences");
	TopLevelWindowManager topLevelWindowManager = (TopLevelWindowManager)hashTable.get("topLevelWindowManager");
	SelectorExtensionFilter forceFileFilter = null; 
	{
		Object obj = hashTable.get(FORCE_FILE_FILTER);
		if (obj != null) {
			VCAssert.ofType(obj, SelectorExtensionFilter.class); 
			forceFileFilter = (SelectorExtensionFilter) obj;
			VCAssert.assertTrue(forceFileFilter.supports(Selector.ANY), "only " + Selector.ANY + " filters supported for force file filter");
		}
	}
	if (topLevelWindowManager == null) {
		throw new RuntimeException("toplLevelWindowManager required");
	}
	String defaultPath = userPreferences.getGenPref(UserPreferences.GENERAL_LAST_PATH_USED);
	VCFileChooser fileChooser = new VCFileChooser(defaultPath);
	fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	fileChooser.setMultiSelectionEnabled(false);
	SimulationContext simContexts[] = bioModel.getSimulationContexts();
	boolean spatialDeterministicSim = false;
	boolean nonspatialDeterministicSim = false;
	boolean stochasticSim = false;
	for (SimulationContext sc : simContexts) {
		if (sc.isStoch()) {
			stochasticSim = true;
		}
		else {
			if (sc.getGeometry().getDimension( ) > 0) {
				spatialDeterministicSim = true;
			}
			else {
				nonspatialDeterministicSim = true;
			}
		}
	}
	FileFilter defaultFileFilter;
	if(forceFileFilter == null){
		List<FileFilter> dlist = FileFilters.supports(SelectorExtensionFilter.Selector.DEFAULT);
		VCAssert.assertTrue(dlist.size( ) == 1, "Exactly one filter must be designated default");
		defaultFileFilter = dlist.get(0);
		Set<FileFilter> filters = new TreeSet<>();
		filters.addAll(FileFilters.supports(SelectorExtensionFilter.Selector.ANY));
		if (spatialDeterministicSim) {
			filters.addAll(FileFilters.supports(SelectorExtensionFilter.Selector.DETERMINISTIC,SelectorExtensionFilter.Selector.SPATIAL));
		}
		if (nonspatialDeterministicSim) {
			filters.addAll(FileFilters.supports(SelectorExtensionFilter.Selector.DETERMINISTIC,SelectorExtensionFilter.Selector.NONSPATIAL));
		}
		if (stochasticSim) {
			filters.addAll(FileFilters.supports(SelectorExtensionFilter.Selector.STOCHASTIC));
		}
		for (FileFilter f : filters) {
			fileChooser.addChoosableFileFilter(f);
		}
		
		/*
		fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_SBML_12);
		fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_SBML_21);
		fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_SBML_22);
		fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_SBML_23);
		fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_SBML_24);
		fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_SBML_31_CORE);
		fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_SBML_31_SPATIAL);
	//	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_CELLML);
		fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_SEDML);
		fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_VCML);
		fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_MATLABV6);
		fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_PDF); 
		fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_SMOLDYN_INPUT);
		*/
		
	} 
	else {
		defaultFileFilter = forceFileFilter;
	}
	// remove all selector
	fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
	// Set the default file filter...
	fileChooser.setFileFilter(defaultFileFilter);
    fileChooser.setSelectedFile(new java.io.File(TokenMangler.fixTokenStrict(bioModel.getName())));
	
	fileChooser.setDialogTitle("Export Virtual Cell BioModel As...");
	if (fileChooser.showSaveDialog(currentWindow) != JFileChooser.APPROVE_OPTION) {
		// user didn't choose save
		throw UserCancelException.CANCEL_FILE_SELECTION;
	} 
	File selectedFile = fileChooser.getSelectedFile();
	FileFilter gfileFilter = fileChooser.getFileFilter();
	VCAssert.ofType(gfileFilter, SelectorExtensionFilter.class);
	//only ExtensionFilters should have been added;
	SelectorExtensionFilter fileFilter =  (SelectorExtensionFilter) gfileFilter; 
	if (selectedFile == null) {
		// no file selected (no name given)
		throw UserCancelException.CANCEL_FILE_SELECTION;
	} 
	///
	String selectedFileName = recordAndRemoveExtension( selectedFile.getPath() );
	if (fileFilter.isValidExtension(extensionUserProvided)) {
		selectedFileName += extensionUserProvided;
	}
	else {
		selectedFileName += fileFilter.getPrimaryExtension(); 
	}
	selectedFile = new File(selectedFileName);

	checkForOverwrites(selectedFile, topLevelWindowManager.getComponent(), userPreferences);
	// put the filter in the hash so the export task knows what to do...
	hashTable.put(FILE_FILTER, fileFilter);
	if (fileFilter.supports(SelectorExtensionFilter.Selector.ANY)) {
		// nothing more to do in this case
		resetPreferredFilePath(selectedFile, userPreferences);
		return selectedFile;
	}

	//ArrayList<String> applicableAppNameList = new ArrayList<String>();
	ArrayList<SimulationContext> applicableSimContext = new ArrayList<>();
	for (SimulationContext sc : simContexts) {
		if (sc.getGeometry().getDimension() == 0 ) {
			if (!fileFilter.supports(SelectorExtensionFilter.Selector.NONSPATIAL)) {
				continue;
			}
		}
		else if (!fileFilter.supports(SelectorExtensionFilter.Selector.SPATIAL)) {
			continue;
		}
		if (sc.isStoch()) {
			if (!fileFilter.supports(SelectorExtensionFilter.Selector.STOCHASTIC)) {
				continue;
			}
		}
		else if (!fileFilter.supports(SelectorExtensionFilter.Selector.DETERMINISTIC)) {
			continue;
			}
		applicableSimContext.add(sc);
	}
	/*
		{} else {
			// all apps
			for (int i=0;i<simContexts.length;i++){
				applicableAppNameList.add(simContexts[i].getName());
			}
		}
	 */
	SimulationContext chosenSimContext = null;
	if (applicableSimContext.size() == 1){
		chosenSimContext = applicableSimContext.get(0);
	} 
	/*
		else if (!fileFilter.getDescription().equals(FileFilters.FILE_FILTER_PDF.getDescription()) && 
				!fileFilter.getDescription().equals(FileFilters.FILE_FILTER_SBML_12.getDescription()) &&
				!fileFilter.getDescription().equals(FileFilters.FILE_FILTER_SBML_21.getDescription()) &&
				!fileFilter.getDescription().equals(FileFilters.FILE_FILTER_SBML_22.getDescription()) &&
				!fileFilter.getDescription().equals(FileFilters.FILE_FILTER_SBML_23.getDescription()) &&
				!fileFilter.getDescription().equals(FileFilters.FILE_FILTER_SBML_24.getDescription()) &&
				!fileFilter.getDescription().equals(FileFilters.FILE_FILTER_SBML_31_CORE.getDescription()) &&
				!fileFilter.getDescription().equals(FileFilters.FILE_FILTER_SBML_31_SPATIAL.getDescription())) {
			if(fileFilter.getDescription().equals(FileFilters.FILE_FILTER_BNGL.getDescription())) {
				boolean hasReactions = bioModel.getModel().getReactionSteps().length > 0 ? true : false;
				System.out.println(hasReactions);
				if(hasReactions) {					// mixed
					String errMsg = "Simple Reactions cannot be exported to .bngl format.";
					errMsg += "<br>Some information will be lost.";
					errMsg += "<br><br>Continue anyway?";
					errMsg = "<html>" + errMsg + "</html>";
					int dialogButton = JOptionPane.YES_NO_OPTION;
					int returnCode = JOptionPane.showConfirmDialog(topLevelWindowManager.getComponent(), errMsg, "Exporting to .bngl", dialogButton);
					if (returnCode != JOptionPane.YES_OPTION) {
						throw UserCancelException.CANCEL_FILE_SELECTION;
					}
				}
			}

			//String[] applicationNames = (String[])org.vcell.util.BeanUtils.getArray(applicableAppNameList,String.class);
		}	 */
	else {
		//String[] applicationNames = applicableAppNameList.toArray(new String[applicableAppNameList.size()]);
		final int s = applicableSimContext.size();
		SimContextAdapter scarray[] = new SimContextAdapter[s];
		for (int i = 0; i < s; i++) {
			scarray[i] = new SimContextAdapter(applicableSimContext.get(i));
		}
		
		Object choice = PopupGenerator.showListDialog(topLevelWindowManager, scarray, "Please select Application");
		if (choice == null) {
			throw UserCancelException.CANCEL_FILE_SELECTION;
		}
		VCAssert.ofType(choice, SimContextAdapter.class);
		SimContextAdapter sca = (SimContextAdapter) choice;
		chosenSimContext =  sca.simCtx;
	}
	hashTable.put(SIM_CONTEXT, chosenSimContext);
	boolean isSbml = fileFilter.supports(SelectorExtensionFilter.Selector.SBML);
	if (fileFilter.requiresMoreChoices()) {
		ExtensionFilter.ChooseContext ctx = new ExtensionFilter.ChooseContext(hashTable,topLevelWindowManager,currentWindow,chosenSimContext,selectedFile,selectedFileName);
		fileFilter.askUser(ctx);
	}

//	// Check structure sized and select a simulation to export to SBML
//	if (isSbml) {
//		// get user choice of structure and its size and computes absolute sizes of compartments using the StructureSizeSolver.
//		Structure[] structures = bioModel.getModel().getStructures();
//		// get the nonspatial simulationContexts corresponding to names in applicableAppNameList 
//		// This is needed in ApplnSelectionAndStructureSizeInputPanel
//		
//		SimulationContext[] applicableSimContexts = applicableSimContext.toArray(new SimulationContext[applicableSimContext.size()]);
//
//		String strucName = null;
//		double structSize = 1.0;
//		int structSelection = -1;
//		int option = JOptionPane.CANCEL_OPTION;
//		Simulation chosenSimulation = null;
//
//		ApplnSelectionAndStructureSizeInputPanel applnStructInputPanel = null;
//		while (structSelection < 0) {
//			applnStructInputPanel = new ApplnSelectionAndStructureSizeInputPanel();
//			applnStructInputPanel.setSimContexts(applicableSimContexts);
//			applnStructInputPanel.setStructures(structures);
//			applnStructInputPanel.setPreferredSize(new java.awt.Dimension(350, 400));
//			applnStructInputPanel.setMaximumSize(new java.awt.Dimension(350, 400));
//			option = DialogUtils.showComponentOKCancelDialog(currentWindow, applnStructInputPanel, "Select Application and Specify Structure Size to Export:");
//			structSelection = applnStructInputPanel.getStructSelectionIndex();
//			if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
//				break;
//			} else if (option == JOptionPane.OK_OPTION && structSelection < 0) {
//				DialogUtils.showErrorDialog(currentWindow, "Please select a structure and set its size");
//			}
//		}
//
//		if (option == JOptionPane.OK_OPTION) {
//			applnStructInputPanel.applyStructureNameAndSizeValues();
//			strucName = applnStructInputPanel.getSelectedStructureName();
//			chosenSimContext = applnStructInputPanel.getSelectedSimContext();
//			hashTable.put("selectedSimContext", chosenSimContext);
//
//			GeometryContext geoContext = chosenSimContext.getGeometryContext();
//			if (!fileFilter.getDescription().equals(FileFilters.FILE_FILTER_SBML_31_SPATIAL.getDescription())) { 
//				// calculate structure Sizes only if appln is not spatial
//				structSize = applnStructInputPanel.getStructureSize();
//				// Invoke StructureSizeEvaluator to compute absolute sizes of compartments if all sizes are not set
//				if ( (geoContext.isAllSizeSpecifiedNull() && geoContext.isAllVolFracAndSurfVolSpecifiedNull()) ||
//						((strucName == null || structSize <= 0.0) && (geoContext.isAllSizeSpecifiedNull() && geoContext.isAllVolFracAndSurfVolSpecified())) ||
//						(!geoContext.isAllSizeSpecifiedPositive() && geoContext.isAllVolFracAndSurfVolSpecifiedNull()) ||
//						(!geoContext.isAllSizeSpecifiedPositive() && !geoContext.isAllVolFracAndSurfVolSpecified()) ||
//						(geoContext.isAllSizeSpecifiedNull() && !geoContext.isAllVolFracAndSurfVolSpecified()) ) {
//					DialogUtils.showErrorDialog(currentWindow, "Cannot export to SBML without compartment sizes being set. This can be automatically " +
//							" computed if the absolute size of at least one compartment and the relative sizes (Surface-to-volume-ratio/Volume-fraction) " +
//							" of all compartments are known. Sufficient information is not available to perform this computation." +
//							"\n\nThis can be fixed by going back to the application '" + chosenSimContext.getName() + "' and setting structure sizes in the 'StructureMapping' tab.");
//					throw UserCancelException.CANCEL_XML_TRANSLATION;
//				} 
//				if (!geoContext.isAllSizeSpecifiedPositive() && geoContext.isAllVolFracAndSurfVolSpecified()) {
//					Structure chosenStructure = chosenSimContext.getModel().getStructure(strucName);
//					StructureMapping chosenStructMapping = chosenSimContext.getGeometryContext().getStructureMapping(chosenStructure);
//					StructureSizeSolver.updateAbsoluteStructureSizes(chosenSimContext, chosenStructure, structSize, chosenStructMapping.getSizeParameter().getUnitDefinition());
//				}
//			} else {
//				if (!geoContext.isAllUnitSizeParameterSetForSpatial()) {
//					DialogUtils.showErrorDialog(currentWindow, "Cannot export to SBML without compartment size ratios being set."  +
//							"\n\nThis can be fixed by going back to the application '" + chosenSimContext.getName() + "' and setting structure" +
//							" size ratios in the 'StructureMapping' tab.");
//					throw UserCancelException.CANCEL_XML_TRANSLATION;
//				}
//			}
//
//			// Select simulation whose overrides need to be exported
//			// If simContext doesn't have simulations, don't pop up simulationSelectionPanel
//			Simulation[] sims = bioModel.getSimulations(chosenSimContext);
//			// display only those simulations that have overrides in the simulationSelectionPanel.
//			Vector<Simulation> orSims = new Vector<Simulation>();
//			for (int s = 0; (sims != null) && (s < sims.length); s++) {
//				if (sims[s].getMathOverrides().hasOverrides()) {
//					orSims.addElement(sims[s]);
//				}
//			}
//			Simulation[] overriddenSims = (Simulation[])BeanUtils.getArray(orSims, Simulation.class);
//			if (overriddenSims.length > 0) {
//				SimulationSelectionPanel simSelectionPanel = new SimulationSelectionPanel();
//				simSelectionPanel.setPreferredSize(new java.awt.Dimension(600, 400));
//				simSelectionPanel.setMaximumSize(new java.awt.Dimension(600, 400));
//				simSelectionPanel.setSimulations(overriddenSims);
//				int simOption = DialogUtils.showComponentOKCancelDialog(currentWindow, simSelectionPanel, "Select Simulation whose overrides should be exported:");
//				if (simOption == JOptionPane.OK_OPTION) {
//					chosenSimulation = simSelectionPanel.getSelectedSimulation();
//					if (chosenSimulation != null) {
//						hashTable.put("selectedSimulation", chosenSimulation);
//					}
//				} else if (simOption == JOptionPane.CANCEL_OPTION || simOption == JOptionPane.CLOSED_OPTION) {
//					// User did not choose a simulation whose overrides are required to be exported.
//					// Without that information, cannot export successfully into SBML, 
//					// Hence canceling the entire export to SBML operation.
//					throw UserCancelException.CANCEL_XML_TRANSLATION;
//				}
//			} 
//		} else if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
//			// User did not choose to set size for any structure.
//			// Without that information, cannot export successfully into SBML, 
//			// Hence canceling the entire export to SBML operation.
//			throw UserCancelException.CANCEL_XML_TRANSLATION;
//		}
//
//		if (chosenSimulation != null) {
//		// rename file to contain exported simulation.
//			String ext = FilenameUtils.getExtension(selectedFileName);
//			String base = FilenameUtils.getBaseName(selectedFileName);
//			String path = FilenameUtils.getPath(selectedFileName);
//			base += "_"  + TokenMangler.mangleToSName(chosenSimulation.getName());
//			selectedFileName = path + base + ext; 
//			selectedFile.renameTo(new File(selectedFileName));
//		}
//		resetPreferredFilePath(selectedFile, userPreferences);
//		return selectedFile;
//	}

	resetPreferredFilePath(selectedFile, userPreferences);
	return selectedFile;
}


/**
 * Export to Geometry
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
private File showGeometryModelXMLFileChooser(Hashtable<String, Object> hashTable) throws java.lang.Exception {

	Geometry geom = (Geometry)hashTable.get("documentToExport");
	JFrame currentWindow = (JFrame)hashTable.get("currentWindow");
	UserPreferences userPreferences = (UserPreferences)hashTable.get("userPreferences");
	TopLevelWindowManager topLevelWindowManager = (TopLevelWindowManager)hashTable.get("topLevelWindowManager");
	Component comp = (Component)hashTable.get("component");
	comp = (comp==null?(currentWindow==null?(topLevelWindowManager==null?null:topLevelWindowManager.getComponent()):currentWindow):comp);
	String defaultPath = userPreferences.getGenPref(UserPreferences.GENERAL_LAST_PATH_USED);
	VCFileChooser fileChooser = new VCFileChooser(defaultPath);
	fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	fileChooser.setMultiSelectionEnabled(false);
//	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_VCML);
	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_PDF);
	if (geom.getDimension()>0){
		fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_AVS);
		fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_STL);
		fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_PLY);
	}

	// remove all selector
	fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
	
	// set the default file filter...
	fileChooser.setFileFilter(FileFilters.FILE_FILTER_VCML);
	
    fileChooser.setSelectedFile(new java.io.File(TokenMangler.fixTokenStrict(geom.getName())));
	
	fileChooser.setDialogTitle("Export Virtual Cell Geometry As...");
	if (fileChooser.showSaveDialog(comp) != JFileChooser.APPROVE_OPTION) {
		// user didn't choose save
		throw UserCancelException.CANCEL_FILE_SELECTION;
	} else {
		File selectedFile = fileChooser.getSelectedFile();
		javax.swing.filechooser.FileFilter fileFilter = fileChooser.getFileFilter();
		if (selectedFile == null) {
			// no file selected (no name given)
			throw UserCancelException.CANCEL_FILE_SELECTION;
		} else {
			///
			String selectedFileName = recordAndRemoveExtension( selectedFile.getPath() );
			String n = selectedFile.getPath().toLowerCase();
			if (fileFilter == FileFilters.FILE_FILTER_VCML && !n.endsWith(".vcml")) {
				selectedFile = new File(selectedFileName + ".vcml");
			} else if (fileFilter == FileFilters.FILE_FILTER_PDF && !n.endsWith(".pdf")) {
				selectedFile = new File(selectedFileName + ".pdf");
			}
			
			if (fileFilter==null) {
				throw new Exception("No file save type was selected.");
			}
			checkForOverwrites(selectedFile, comp, userPreferences);
			
			// put the filter in the hash so the export task knows what to do...
			hashTable.put("fileFilter", fileFilter);
			resetPreferredFilePath(selectedFile, userPreferences);

			return selectedFile;
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
private File showMathModelXMLFileChooser(Hashtable<String, Object> hashTable) throws java.lang.Exception {
	// for mathmodels:
	MathModel mathModel = (MathModel)hashTable.get("documentToExport");
	JFrame currentWindow = (JFrame)hashTable.get("currentWindow");
	UserPreferences userPreferences = (UserPreferences)hashTable.get("userPreferences");
	TopLevelWindowManager topLevelWindowManager = (TopLevelWindowManager)hashTable.get("topLevelWindowManager");
	if (topLevelWindowManager == null) {
		throw new RuntimeException("toplLevelWindowManager required");
	}
	String defaultPath = userPreferences.getGenPref(UserPreferences.GENERAL_LAST_PATH_USED);
	VCFileChooser fileChooser = new VCFileChooser(defaultPath);
	fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	fileChooser.setMultiSelectionEnabled(false);
	// fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_SBML_21);		// Can export Mathmodel to L2V1 ??
	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_SBML_23);
	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_SBML_24);
//	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_CELLML);
	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_MATLABV6);
	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_PDF);
	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_SMOLDYN_INPUT);
	// remove all selector
	fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
	// set the default file filter...
	fileChooser.setFileFilter(FileFilters.FILE_FILTER_VCML);
    fileChooser.setSelectedFile(new java.io.File(TokenMangler.fixTokenStrict(mathModel.getName())));
	
	fileChooser.setDialogTitle("Export Virtual Cell MathModel As...");
	if (fileChooser.showSaveDialog(currentWindow) != JFileChooser.APPROVE_OPTION) {
		// user didn't choose save
		throw UserCancelException.CANCEL_FILE_SELECTION;
	} else {
		File selectedFile = fileChooser.getSelectedFile();
		javax.swing.filechooser.FileFilter fileFilter = fileChooser.getFileFilter();
		if (selectedFile == null) {
			// no file selected (no name given)
			throw UserCancelException.CANCEL_FILE_SELECTION;
		} else {
			///
			String selectedFileName = recordAndRemoveExtension( selectedFile.getPath() );
			String n = selectedFile.getPath().toLowerCase();
			if (fileFilter == FileFilters.FILE_FILTER_CELLML && !n.endsWith(".xml")) {
				selectedFile = new File(selectedFileName + ".xml");
			} else if (fileFilter == FileFilters.FILE_FILTER_VCML && !n.endsWith(".vcml")) {
				selectedFile = new File(selectedFileName + ".vcml");
			} else if ((fileFilter == FileFilters.FILE_FILTER_SBML_23 || (fileFilter == FileFilters.FILE_FILTER_SBML_24)) && !n.endsWith(".xml")) {
				selectedFile = new File(selectedFileName + ".xml");
			} else if (fileFilter == FileFilters.FILE_FILTER_MATLABV6 && !n.endsWith(".m")) {
				selectedFile = new File(selectedFileName + ".m");
			} else if (fileFilter == FileFilters.FILE_FILTER_PDF && !n.endsWith(".pdf")) {
				selectedFile = new File(selectedFileName + ".pdf");
			} else if (fileFilter == FileFilters.FILE_FILTER_SMOLDYN_INPUT && !(n.endsWith(".smoldynInput") ||(n.endsWith(".txt")))) {
				selectedFile = new File(selectedFileName + ".smoldynInput");
			}
			checkForOverwrites(selectedFile, topLevelWindowManager.getComponent(), userPreferences);
			// put the filter in the hash so the export task knows what to do...
			hashTable.put("fileFilter", fileFilter);
			//only non-spatial math models can be exported to CellML.
			if (fileFilter == FileFilters.FILE_FILTER_CELLML &&
				mathModel.getMathDescription().getGeometry().getDimension() != 0) {
				throw new Exception("No non-spatial applications, can only export to this format the math from a non-spatial application");
			}
			// Select a spatial stochastic simulation to export 
			else if(fileFilter.getDescription().equals(FileFilters.FILE_FILTER_SMOLDYN_INPUT.getDescription()))
			{
				Simulation[] sims = mathModel.getSimulations();
				String[] simNames = new String[sims.length];
				for(int i=0; i< sims.length; i++)
				{
					simNames[i] = sims[i].getName();
				}
				Object choice = PopupGenerator.showListDialog(topLevelWindowManager, simNames, "Please select a simulation to export");
				if(choice == null)
				{
					throw UserCancelException.CANCEL_FILE_SELECTION;
				}
				String chosenSimulationName = (String)choice;
				Simulation chosenSimulation = mathModel.getSimulation(chosenSimulationName);
				if(chosenSimulation != null)
				{
					hashTable.put("selectedSimulation", chosenSimulation);
					
					
					File tempOutputFile = new File(selectedFileName);
		    		if(!FileFilters.FILE_FILTER_SMOLDYN_INPUT.accept(tempOutputFile)){
    					if(tempOutputFile.getName().indexOf(".") == -1){
    						tempOutputFile = new File(tempOutputFile.getParentFile(),tempOutputFile.getName() + ".smoldynInput");
    					}else{
    						throw new Exception("Smoldyn input file has to be a text document with extension of either 'smoldynInput' or 'txt'");
    					}
    				}
		    		if(tempOutputFile.exists())
		    		{
		    			String overwriteChoice = PopupGenerator.showWarningDialog(topLevelWindowManager, topLevelWindowManager.getUserPreferences(), UserMessage.warn_OverwriteFile, selectedFileName);
		    			if(overwriteChoice.equals(UserMessage.OPTION_CANCEL)){
		    				throw UserCancelException.CANCEL_FILE_SELECTION;
		    			}
		    		}
		    		selectedFile = tempOutputFile;
				}
				
			}
			
			resetPreferredFilePath(selectedFile, userPreferences);

			return selectedFile;
		}
	}
}

private static class SimContextAdapter {
	final SimulationContext simCtx;

	SimContextAdapter(SimulationContext simCtx) {
		VCAssert.assertValid(simCtx);
		this.simCtx = simCtx;
	}

	@Override
	public String toString() {
		return simCtx.getName(); 
	}
	
	
}

}
