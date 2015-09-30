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

import org.vcell.util.document.VCDocument;

import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.MDIManager;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class FinishSave extends AsynchClientTask {
	
	public FinishSave() {
		super("Updating the workspace", TASKTYPE_SWING_BLOCKING, false, true);
	}

/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws java.lang.Exception {
	MDIManager mdiManager= (MDIManager)hashTable.get("mdiManager");
	DocumentWindowManager documentWindowManager = (DocumentWindowManager)hashTable.get(CommonTask.DOCUMENT_WINDOW_MANAGER.name);
	if (hashTable.containsKey(SaveDocument.DOC_KEY)) {
		VCDocument savedDocument = (VCDocument)hashTable.get(SaveDocument.DOC_KEY);
		documentWindowManager.resetDocument(savedDocument);
	}
	mdiManager.unBlockWindow(documentWindowManager.getManagerID());
	mdiManager.showWindow(documentWindowManager.getManagerID());
}

}
