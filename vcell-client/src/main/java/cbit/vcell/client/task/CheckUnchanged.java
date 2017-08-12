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


import java.util.Hashtable;

import org.vcell.util.DataAccessException;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.document.Version;

import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.MathModelWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.clientdb.DocumentManager;
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
	super("Checking changes to document", TASKTYPE_NONSWING_BLOCKING);
	this.whileSavingForRunningSims = whileSavingForRunningSims;
}

/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws java.lang.Exception {
	DocumentWindowManager documentWindowManager = (DocumentWindowManager)hashTable.get(CommonTask.DOCUMENT_WINDOW_MANAGER.name);
	DocumentManager documentManager = (DocumentManager)hashTable.get(CommonTask.DOCUMENT_MANAGER.name);
//	JFrame currentDocumentWindow = (JFrame)hashTable.get("currentDocumentWindow");
	if (documentWindowManager.getVCDocument().getDocumentType() == VCDocumentType.MATHMODEL_DOC) {
		if (((MathModelWindowManager)documentWindowManager).hasUnappliedChanges()) {
			String msg = "Changes have been made in VCML Editor, please click \"Apply Changes\" or \"Cancel\" to proceed.";
			PopupGenerator.showErrorDialog(documentWindowManager, msg);
			throw UserCancelException.CANCEL_UNAPPLIED_CHANGES;			
		}
	}
	boolean isChanged = true;
	Version v = documentWindowManager.getVCDocument().getVersion();
	if (v == null || !v.getOwner().equals(documentManager.getSessionManager().getUser())){
		isChanged = true;
	} else {
		try {
			isChanged = documentManager.isChanged(documentWindowManager.getVCDocument());
		} catch (DataAccessException exc) {
			String choice = PopupGenerator.showWarningDialog(documentWindowManager, documentWindowManager.getUserPreferences(), UserMessage.warn_UnableToCheckForChanges,null);
			if (!choice.equals(UserMessage.OPTION_CONTINUE)){
				throw UserCancelException.WARN_UNABLE_CHECK;
			}
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
}
