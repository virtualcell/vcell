package org.vcell.util.gui.exporter;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.mapping.SimulationContext;
import org.vcell.sedml.ModelFormat;
import org.vcell.sedml.SEDMLExporter;
import org.vcell.util.FileUtils;
import org.vcell.util.UserCancelException;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

@SuppressWarnings("serial")
public class SedmlExtensionFilter extends SelectorExtensionFilter {
	private static final String FNAMES = ".sedml";
	private Component parentComponent = null;
	protected ModelFormat modelFormat = ModelFormat.SBML;

	
	public SedmlExtensionFilter() {
		this(FNAMES, "SedML format<Level1,Version2> (.sedml)", SelectorExtensionFilter.Selector.FULL_MODEL);
	}
	public SedmlExtensionFilter(String fNames, String name, Selector selector) {
		super(fNames, name, selector);
	}

	@Override
	public void askUser(ChooseContext ctx) throws UserCancelException {
		Object[] options = {"VCML","SBML"};
		int choice = JOptionPane.showOptionDialog(
				ctx.topLevelWindowManager.getComponent(),
				"VCML or SBML?",			// message,
				"Choose an option",			// title
				JOptionPane.YES_NO_OPTION,	// optionType
				JOptionPane.QUESTION_MESSAGE,	// messageType
				null,						// Icon
				options,
				"SBML");					// initialValue 
		if (choice == 0) modelFormat = ModelFormat.VCML;
		System.out.println(modelFormat);
	}
	
	@Override
	public void writeBioModel(DocumentManager documentManager, BioModel bioModel, File exportFile, SimulationContext ignored) throws Exception {
		String resultString;
		// export the entire biomodel to a SEDML file (for now, only non-spatial,non-stochastic applns)
		int sedmlLevel = 1;
		int sedmlVersion = 2;
		String sPath = FileUtils.getFullPathNoEndSeparator(exportFile.getAbsolutePath());
		String sFile = FileUtils.getBaseName(exportFile.getAbsolutePath());
		String sExt = FileUtils.getExtension(exportFile.getAbsolutePath());
		
		SEDMLExporter sedmlExporter = null;
		if (bioModel != null) {
			sedmlExporter = new SEDMLExporter(sFile, bioModel, sedmlLevel, sedmlVersion, null);
			boolean bRoundTripSBMLValidation = true;
			Map<String, String> unsupportedApplications = SEDMLExporter.getUnsupportedApplicationMap(bioModel, modelFormat);
			Predicate<SimulationContext> simContextFilter = (SimulationContext sc) -> !unsupportedApplications.containsKey(sc.getName());
			resultString = sedmlExporter.getSEDMLDocument(sPath, sFile, modelFormat, bRoundTripSBMLValidation, simContextFilter).writeDocumentToString();
			// gather unsupported applications with messages
		} else {
			throw new RuntimeException("unsupported Document Type " + Objects.requireNonNull(bioModel).getClass().getName() + " for SedML export");
		}
		if (sExt.equals("sedml")) {
			doSpecificWork(sedmlExporter, resultString, sPath, sFile);
			return;
		}
		else {
			XmlUtil.writeXMLStringToFile(resultString, exportFile.getAbsolutePath(), true);
		}
	}
	
	public void doSpecificWork(SEDMLExporter sedmlExporter, String resultString, String sPath, String sFile) throws Exception {
		String sedmlFileName = Paths.get(sPath, sFile + ".sedml").toString();
		XmlUtil.writeXMLStringToFile(resultString, sedmlFileName, true);
		sedmlExporter.addSedmlFileToList(sFile + ".sedml");
	}
	
	@Override
	public boolean requiresMoreChoices() {	  //tells ChooseFile to call askUser() above
		return true;
	}

}
