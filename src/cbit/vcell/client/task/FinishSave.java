package cbit.vcell.client.task;
import cbit.vcell.client.desktop.*;
import cbit.vcell.client.*;
import java.beans.*;

import org.vcell.util.UserCancelException;

import cbit.vcell.mapping.*;
import cbit.vcell.math.*;
import cbit.vcell.biomodel.*;
import cbit.vcell.desktop.controls.*;
import cbit.vcell.document.*;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class FinishSave extends AsynchClientTask {
/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @return java.lang.String
 */
public java.lang.String getTaskName() {
	return "Updating the workspace";
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
public void run(java.util.Hashtable hashTable) throws java.lang.Exception {
	MDIManager mdiManager= (MDIManager)hashTable.get("mdiManager");
	DocumentWindowManager documentWindowManager = (DocumentWindowManager)hashTable.get("documentWindowManager");
	if (hashTable.containsKey("savedDocument")) {
		VCDocument savedDocument = (VCDocument)hashTable.get("savedDocument");
		documentWindowManager.resetDocument(savedDocument);
	}
	mdiManager.unBlockWindow(documentWindowManager.getManagerID());
	mdiManager.showWindow(documentWindowManager.getManagerID());
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 8:44:16 PM)
 * @return boolean
 */
public boolean skipIfAbort() {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 4:39:38 PM)
 * @return boolean
 */
public boolean skipIfCancel(UserCancelException exc) {
	return false;
}
}