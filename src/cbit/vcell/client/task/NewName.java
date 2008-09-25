package cbit.vcell.client.task;

import java.util.Hashtable;
import javax.swing.*;
import cbit.vcell.client.*;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class NewName extends AsynchClientTask {
/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @return java.lang.String
 */
public java.lang.String getTaskName() {
	return "Getting document name";
}


/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @return int
 */
public int getTaskType() {
	return TASKTYPE_SWING_BLOCKING;
}


/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws java.lang.Exception {
	DocumentWindowManager documentWindowManager = (DocumentWindowManager)hashTable.get("documentWindowManager");
	MDIManager mdiManager = (MDIManager)hashTable.get("mdiManager");
	String oldName = documentWindowManager.getVCDocument().getName();
	String newName = mdiManager.getDatabaseWindowManager().showSaveDialog(documentWindowManager.getVCDocument().getDocumentType(), (JFrame)hashTable.get("currentDocumentWindow"), oldName);
	if (newName == null || newName.trim().length()==0){
		throw new Exception("A name must be given to save");
	} else if (newName.contains("'")){
		throw new Exception("Apostrophe is not allowed in names");
	} else if (oldName.equals(newName)) {
		throw new Exception("A model with name '" + newName + "' already exists. Please give a different name.");
	}
	documentWindowManager.getVCDocument().setName(newName);
	hashTable.put("newName", newName);
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 8:44:20 PM)
 * @return boolean
 */
public boolean skipIfAbort() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 4:39:49 PM)
 * @return boolean
 */
public boolean skipIfCancel(UserCancelException exc) {
	return true;
}
}