package org.vcell.util.gui.exporter;

import java.awt.Component;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.vcell.sedml.ModelFormat;
import org.vcell.sedml.SEDMLExporter;
import org.vcell.util.FileUtils;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.exporter.ExtensionFilter.ChooseContext;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.export.SpringSaLaDExporter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.Application;

@SuppressWarnings("serial")
public class SpringSaLaDExtensionFilter extends SelectorExtensionFilter {
	private static final String FNAMES = ".ssld";
	private Component parentComponent = null;
	protected ModelFormat modelFormat = ModelFormat.SPRINGSALAD;
	private SimulationContext simContext = null;
	
	public SpringSaLaDExtensionFilter() {
		this(FNAMES, "SpringSaLaD format<Version2.2> (.ssld)", SelectorExtensionFilter.Selector.FULL_MODEL);
	}
	public SpringSaLaDExtensionFilter(String fNames, String name, Selector selector) {
		super(fNames, name, selector);
	}

	@Override
	public void askUser(ChooseContext c) throws UserCancelException {
		if(c.chosenContext == null) {
			return;
		}
		BioModel bioModel = c.chosenContext.getBioModel();
		parentComponent = c.currentWindow;	// will need it later, hack to center another window in ssldExporter.writeDocumentStringToFile()
		LinkedHashMap<String, SimulationContext> springSaLaDApplications = new LinkedHashMap<> ();
		if(bioModel != null) {
			for(SimulationContext candidate : bioModel.getSimulationContexts()) {
				if(candidate.getApplicationType() == Application.SPRINGSALAD) {
					springSaLaDApplications.put(candidate.getName(), candidate);
				}
			}
			if(springSaLaDApplications.size() == 0) {
				;	// do nothing, simContext is null
			} else if(springSaLaDApplications.size() == 1) {
				Entry<String, SimulationContext> entry = springSaLaDApplications.entrySet().iterator().next();
				simContext = entry.getValue();
			} else {
				Object[] namesArray = springSaLaDApplications.keySet().toArray();
				Object choice = DialogUtils.showListDialog(c.currentWindow, namesArray, "Please select Application to export");
				if(choice == null)
				{
					throw UserCancelException.CANCEL_FILE_SELECTION;
				}
				simContext = springSaLaDApplications.get((String)choice);
			}
		}
		System.out.println(simContext);
	}
	
	@Override
	public void writeBioModel(DocumentManager documentManager, BioModel bioModel, File exportFile, SimulationContext ignored) throws Exception {
		String resultString;
		// export the entire biomodel to a SEDML file (for now, only non-spatial,non-stochastic applns)
		int ssldMajor = 2;
		int ssldMinor = 2;
		String sPath = FileUtils.getFullPathNoEndSeparator(exportFile.getAbsolutePath());
		String sFile = FileUtils.getBaseName(exportFile.getAbsolutePath());
		String sExt = FileUtils.getExtension(exportFile.getAbsolutePath());
		
		SpringSaLaDExporter ssldExporter = null;
		if (bioModel == null) {
			throw new RuntimeException("unsupported Document Type " + Objects.requireNonNull(bioModel).getClass().getName() + " for SpringSaLaD export");
		}
		if(simContext == null) {
			throw new RuntimeException("SpringSaLaD application required");
		}
		ssldExporter = new SpringSaLaDExporter(sFile, ssldMajor, ssldMinor);
		resultString = ssldExporter.getDocumentAsString(bioModel, simContext);
		doSpecificWork(ssldExporter, resultString, sPath, sFile);
		return;
	}
	
	public void doSpecificWork(SpringSaLaDExporter ssldExporter, String resultString, String sPath, String sFile) throws Exception {
		ssldExporter.writeDocumentStringToFile(resultString, sPath, sFile, parentComponent);
//		sedmlExporter.addSedmlFileToList(sFile + ".sedml");
	}
	
	@Override
	public boolean requiresMoreChoices() {	  //tells ChooseFile to call askUser() above
		return true;
	}

}
