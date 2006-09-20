package cbit.vcell.client.task;
import cbit.util.DataAccessException;
import cbit.util.UserCancelException;
import cbit.vcell.server.*;
import javax.swing.*;

import cbit.vcell.client.database.DocumentManager;
import cbit.vcell.client.desktop.*;
import cbit.vcell.client.*;
import java.beans.*;
import cbit.vcell.mapping.*;
import cbit.vcell.math.*;
import cbit.vcell.biomodel.*;
import cbit.vcell.desktop.controls.*;
import cbit.vcell.document.*;
import cbit.vcell.client.server.*;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class CheckUnchanged extends AsynchClientTask {
	private boolean whileSavingForRunningSims;

/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 9:56:27 AM)
 * @param whileSavingForRunningSims boolean
 */
public CheckUnchanged(boolean whileSavingForRunningSims) {
	this.whileSavingForRunningSims = whileSavingForRunningSims;
}


/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @return java.lang.String
 */
public java.lang.String getTaskName() {
	return "Checking changes to document";
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
	DocumentWindowManager documentWindowManager = (DocumentWindowManager)hashTable.get("documentWindowManager");
	DocumentManager documentManager = (DocumentManager)hashTable.get("documentManager");
	JFrame currentDocumentWindow = (JFrame)hashTable.get("currentDocumentWindow");
	boolean isChanged = true;
	try {
		isChanged = documentManager.isChanged(documentWindowManager.getVCDocument());
	} catch (DataAccessException exc) {
		String choice = PopupGenerator.showWarningDialog(documentWindowManager, documentWindowManager.getUserPreferences(), UserMessage.warn_UnableToCheckForChanges,null);
		if (!choice.equals(UserMessage.OPTION_CONTINUE)){
			throw UserCancelException.WARN_UNABLE_CHECK;
		}
	}
	if (! isChanged) {
		if (whileSavingForRunningSims) {
			// just skip the saves and go ahead
			hashTable.put("conditionalSkip", new String[] {SaveDocument.class.getName(), CheckBeforeDelete.class.getName(), DeleteOldDocument.class.getName()});
		} else {
			String choice = PopupGenerator.showWarningDialog(documentWindowManager, documentWindowManager.getUserPreferences(), UserMessage.warn_UnchangedDocument,null);
			if (choice.equals(UserMessage.OPTION_SAVE_AS_NEW)){
				// user chose to Save As
				throw UserCancelException.CHOOSE_SAVE_AS;
			} else {
				// user canceled, just show existing document
				throw UserCancelException.WARN_NO_CHANGES;
			}
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 8:44:05 PM)
 * @return boolean
 */
public boolean skipIfAbort() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 4:38:36 PM)
 * @return boolean
 */
public boolean skipIfCancel(UserCancelException exc) {
	return true;
}
}