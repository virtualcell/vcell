package cbit.vcell.client;
import cbit.util.UserCancelException;
import cbit.vcell.client.server.UserMessage;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.task.DisplayBNGOutput;
import cbit.vcell.client.task.RunBioNetGen;
import java.util.Hashtable;
import java.io.PrintWriter;
import java.io.File;
import cbit.gui.VCFileChooser;
import cbit.vcell.server.bionetgen.BNGInput;
import cbit.vcell.server.bionetgen.BNGService;
import cbit.vcell.client.bionetgen.BNGOutputPanel;
import cbit.vcell.desktop.controls.AsynchClientTask;

import javax.swing.JFileChooser;


/**
 * Insert the type's description here.
 * Creation date: (7/18/2006 2:18:31 PM)
 * @author: Anuradha Lakshminarayana
 */
public class BNGWindowManager extends TopLevelWindowManager {
	private cbit.vcell.client.bionetgen.BNGOutputPanel fieldBngOutputPanel = null;

/**
 * BNGWindowManager constructor comment.
 * @param requestManager cbit.vcell.client.RequestManager
 */
public BNGWindowManager(BNGOutputPanel argBngOutputPanel, RequestManager requestManager) {
	super(requestManager);
	setBngOutputPanel(argBngOutputPanel);
}


/**
 * Gets the bngOutputPanel property (cbit.vcell.client.bionetgen.BNGOutputPanel) value.
 * @return The bngOutputPanel property value.
 */
public cbit.vcell.client.bionetgen.BNGOutputPanel getBngOutputPanel() {
	return fieldBngOutputPanel;
}


/**
 * Gets the bngOutputPanel property (cbit.vcell.client.bionetgen.BNGOutputPanel) value.
 * @return The bngOutputPanel property value.
 */
public BNGService getBngService() {
	return getRequestManager().getBNGService();
}


/**
 * Insert the method's description here.
 * Creation date: (7/18/2006 2:18:31 PM)
 * @return java.lang.String
 */
java.awt.Component getComponent() {
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (7/18/2006 2:18:31 PM)
 * @return java.lang.String
 */
public String getManagerID() {
	return ClientMDIManager.BIONETGEN_WINDOW_ID;
}


/**
 * Comment
 */
public void importSbml(String bngSbmlStr) {
	cbit.util.document.VCDocument vcDoc = null;
	try {
		// import the xml string from bng output into a biomodel using XMLHelper
		vcDoc = cbit.vcell.xml.XmlHelper.importXML(bngSbmlStr, cbit.vcell.xml.XmlDialect.SBML_L2V1, new TranslationLogger(this));
	} catch (Exception e) {
		e.printStackTrace(System.out);
		cbit.gui.DialogUtils.showErrorDialog("Could not import BNG sbml output into Biomodel : " + e.getMessage());
		throw new RuntimeException("Could not import BNG sbml output into Biomodel : " + e.getMessage());
	}

	if (vcDoc != null && vcDoc instanceof cbit.vcell.biomodel.BioModel) {
		// Create a vcDocInfo for the imported document, and get the RequestManager to open it.
		try {
			cbit.util.document.VCDocumentInfo vcDocInfo = getRequestManager().getDocumentManager().getBioModelInfo(vcDoc.getVersion().getVersionKey());
			if (vcDocInfo != null) {
				getRequestManager().openDocument(vcDocInfo, this, true);
			}
		} catch (cbit.util.DataAccessException e) {
			e.printStackTrace(System.out);
			cbit.gui.DialogUtils.showErrorDialog("Error : Could not open SBML file as biomodel : " + e.getMessage());
			throw new RuntimeException("Error : Could not open SBML file as biomodel : " + e.getMessage());
		}
	} else {
		cbit.gui.DialogUtils.showErrorDialog("Imported Document is not a Biomodel - currently, there is support only for importing into Biomodels.");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/18/2006 2:18:31 PM)
 * @return boolean
 */
public boolean isRecyclable() {
	return true;
}


/**
 * Gets the bngOutputPanel property (cbit.vcell.client.bionetgen.BNGOutputPanel) value.
 * @return The bngOutputPanel property value.
 */
public void runBioNetGen(BNGInput bngInput) {

	// Create a hash and put in the details required to run the ClientTaskDispatcher
	Hashtable hash = new java.util.Hashtable();
	hash.put("bngInput", bngInput);
	hash.put("bngService", getBngService());
	hash.put("bngOutputPanel", getBngOutputPanel());

	// Create the AsynchClientTasks : in this case, running the BioNetGen (non-swing) and then displaying the output (swing) tasks.
	AsynchClientTask[] tasksArray = new AsynchClientTask[2];
	tasksArray[0] = new RunBioNetGen();
	tasksArray[1] = new DisplayBNGOutput();

	// Dispatch the tasks using the ClientTaskDispatcher.
	cbit.vcell.client.task.ClientTaskDispatcher.dispatch(getBngOutputPanel(), hash, tasksArray, false, true, null);
}


/**
 * Comment
 */
public void saveOutput(String bngOutputStr) {

	// Ask user for save location
	String defaultPath = getUserPreferences().getGenPref(UserPreferences.GENERAL_LAST_PATH_USED);
	VCFileChooser fileChooser = new VCFileChooser(defaultPath);
	fileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
	fileChooser.setMultiSelectionEnabled(false);
	// remove all selector
	fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
    fileChooser.setSelectedFile(new java.io.File(getBngOutputPanel().getSelectedOutputFileName()));
	
	fileChooser.setDialogTitle("Save Selected BNG Output Format ...");
	if (fileChooser.showSaveDialog(getBngOutputPanel()) != JFileChooser.APPROVE_OPTION) {
		// user didn't choose save
		throw UserCancelException.CANCEL_FILE_SELECTION;
	} else {
		File selectedFile = fileChooser.getSelectedFile();
		javax.swing.filechooser.FileFilter fileFilter = fileChooser.getFileFilter();
		if (selectedFile == null) {
			// no file selected (no name given)
			throw UserCancelException.CANCEL_FILE_SELECTION;
		} else {
			// we have a file selection, check for overwrites
			if (selectedFile.exists()) {
				String answer = PopupGenerator.showWarningDialog(this, getUserPreferences(), UserMessage.warn_OverwriteFile, selectedFile.getAbsolutePath());
				if (answer.equals(UserMessage.OPTION_CANCEL)){
					throw UserCancelException.CANCEL_FILE_SELECTION;
				}
			}

			// write out text into file using FileOutputStream and PrintWriter
			java.io.FileOutputStream outputStream = null;
			String selectedFileName = selectedFile.getPath();
			try {
				outputStream = new java.io.FileOutputStream(selectedFileName);
			}catch (java.io.IOException e){
				e.printStackTrace(System.out);
				throw new RuntimeException("Error opening file '"+ selectedFileName + " to write BioNetGen output" +": " + e.getMessage());
			}	
				
			PrintWriter bngOutputFile = new PrintWriter(outputStream);
			bngOutputFile.print(bngOutputStr);
			bngOutputFile.close();
		}
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (7/18/2006 2:18:31 PM)
 * @return boolean
 */
private void setBngOutputPanel(BNGOutputPanel newBngOutputPanel) {
	fieldBngOutputPanel = newBngOutputPanel;
}
}