package org.vcell.util.gui.exporter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import javax.swing.JOptionPane;

import cbit.vcell.xml.XmlHelper;
import org.vcell.sedml.SEDMLExporter;
import org.vcell.util.FileUtils;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public class OmexExtensionFilter extends SedmlExtensionFilter {
	private static final String FNAMES = ".omex";
	
	public OmexExtensionFilter() {
		super(FNAMES,"COMBINE archive (.omex)",SelectorExtensionFilter.Selector.FULL_MODEL);
	}

	@Override
	public void writeBioModel(DocumentManager documentManager, BioModel bioModel, File exportFile, SimulationContext ignored) throws Exception {
		String resultString;
		// export the entire biomodel to a SEDML file (all supported applications)
		int sedmlLevel = 1;
		int sedmlVersion = 2;
		String sPath = FileUtils.getFullPathNoEndSeparator(exportFile.getAbsolutePath());
		String sFile = FileUtils.getBaseName(exportFile.getAbsolutePath());
		String sExt = FileUtils.getExtension(exportFile.getAbsolutePath());
		
		SEDMLExporter sedmlExporter = null;
		if (bioModel != null) {
			
			Object[] options = {"VCML","SBML"};
			int choice = JOptionPane.showOptionDialog(null,	// parent component
					"VCML or SBML?",			// message,
					"Choose an option",			// title
					JOptionPane.YES_NO_OPTION,	// optionType
					JOptionPane.QUESTION_MESSAGE,	// messageType
					null,						// Icon
					options,
					"VCML");					// initialValue 
			boolean bForceVCML = choice == 0 ? true : false;
			
			sedmlExporter = new SEDMLExporter(bioModel, sedmlLevel, sedmlVersion, null);
			resultString = sedmlExporter.getSEDMLFile(sPath, sFile, bForceVCML, false, true);

			// convert biomodel to vcml and save to file.
			String vcmlString = XmlHelper.bioModelToXML(bioModel);
			String vcmlFileName = Paths.get(sPath, sFile + ".vcml").toString();
			File vcmlFile = new File(vcmlFileName);
			XmlUtil.writeXMLStringToFile(vcmlString, vcmlFile.getAbsolutePath(), true);
		} else {
			throw new RuntimeException("unsupported Document Type " + Objects.requireNonNull(bioModel).getClass().getName() + " for SedML export");
		}
		if (sExt.equals("omex")) {
			doSpecificWork(sedmlExporter, resultString, sPath, sFile);
			return;
		}
		else {
			XmlUtil.writeXMLStringToFile(resultString, exportFile.getAbsolutePath(), true);
		}
	}
	
	@Override
	public void doSpecificWork(SEDMLExporter sedmlExporter, String resultString, String sPath, String sFile) throws Exception {
		super.doSpecificWork(sedmlExporter, resultString, sPath, sFile);
		sedmlExporter.createOmexArchive(sPath, sFile);
	}

}
