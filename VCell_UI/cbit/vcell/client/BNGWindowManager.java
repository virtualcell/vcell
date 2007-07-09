package cbit.vcell.client;
import cbit.vcell.client.server.UserMessage;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.task.DisplayBNGOutput;
import cbit.vcell.client.task.RunBioNetGen;
import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.File;

import cbit.vcell.server.bionetgen.BNGInput;
import cbit.vcell.server.bionetgen.BNGService;
import cbit.vcell.client.bionetgen.BNGOutputPanel;
import cbit.vcell.desktop.controls.AsynchClientTask;

import javax.swing.JFileChooser;

import org.vcell.util.UserCancelException;
import org.vcell.util.gui.FileFilters;
import org.vcell.util.gui.VCFileChooser;


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
	return getBngOutputPanel();
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
	cbit.vcell.xml.XMLInfo xmlInfo = new cbit.vcell.xml.XMLInfo(bngSbmlStr);

	if (xmlInfo != null) {
		getRequestManager().openDocument(xmlInfo, this, true);
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

/**
 * Gets the bngOutputPanel property (cbit.vcell.client.bionetgen.BNGOutputPanel) value.
 * @return The bngOutputPanel property value.
 */
public void stopBioNetGen() {
	try {
		cbit.vcell.server.bionetgen.BNGUtils.stopBNG();
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}
}

/**
 * Comment
 */
public String uploadBNGLFile() throws java.io.FileNotFoundException, java.io.IOException {
	// BNG input file (.bngl) contents
	String bngInputStr = null;
	// Ask user for upload location
	String defaultPath = getUserPreferences().getGenPref(UserPreferences.GENERAL_LAST_PATH_USED);
	VCFileChooser fileChooser = new VCFileChooser(defaultPath);
	fileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
	fileChooser.setMultiSelectionEnabled(false);
	// remove all selector
	fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());

	// set default file filter
	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_BNGL);
	fileChooser.setFileFilter(FileFilters.FILE_FILTER_BNGL);

	// Set file chooser dialog title
	fileChooser.setDialogTitle("Upload Selected BNG file ...");
	if (fileChooser.showOpenDialog(getBngOutputPanel()) != JFileChooser.APPROVE_OPTION) {
		// user didn't choose Open
		throw UserCancelException.CANCEL_FILE_SELECTION;
	} else {
		File selectedFile = fileChooser.getSelectedFile();
		javax.swing.filechooser.FileFilter fileFilter = fileChooser.getFileFilter();
		if (selectedFile == null) {
			// no file selected (no name given)
			throw UserCancelException.CANCEL_FILE_SELECTION;
		} else {
			long selectedFileLength = selectedFile.length();
			// Check if file exists
			if (!selectedFile.exists()){
				throw new RuntimeException("File "+selectedFile.getPath()+" not found");
			}
			// Check if file has .bngl extension
			if (!selectedFile.getPath().endsWith(".bngl")) {
				org.vcell.util.gui.DialogUtils.showErrorDialog("File " + selectedFile.getPath() + " is not a .bngl file");
				throw new RuntimeException("File " + selectedFile.getPath() + " is not a .bngl file");
			}
			// Read characters from file into character array and transfer into string buffer.
			StringBuffer stringBuffer = new StringBuffer();
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(selectedFile);
				InputStreamReader reader = new InputStreamReader(fis);
				BufferedReader br = new BufferedReader(reader);
				char charArray[] = new char[10000];
				while (true) {
					int numRead = br.read(charArray, 0, charArray.length);
					if (numRead > 0) {
						stringBuffer.append(charArray,0,numRead);
					} else if (numRead == -1) {
						break;
					}
				}
			} finally{
				if (fis != null){
					fis.close();
				}
			}

			if (stringBuffer.length() != selectedFileLength){
				System.out.println("<<<SYSOUT ALERT>>> Reading from bng file: read "+stringBuffer.length()+" of "+selectedFileLength+" bytes of input file");
			}
			bngInputStr = stringBuffer.toString();
		}
		return bngInputStr;
	}	
}
}