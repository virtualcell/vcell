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

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.MDIManager;
import cbit.vcell.client.TopLevelWindowManager;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class FinishExport extends ExportTask {
	
	public FinishExport() {
		super("Updating the workspace", TASKTYPE_SWING_BLOCKING, false, false);
	}

/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws java.lang.Exception {
	MDIManager mdiManager= (MDIManager)hashTable.get("mdiManager");
	TopLevelWindowManager topLevelWindowManager = (TopLevelWindowManager)hashTable.get("topLevelWindowManager");
		Object obj = hashTable.get(RENAME_KEY);
		if (obj != null) {
			String fn = obj.toString();
			DialogUtils.showInfoDialog(topLevelWindowManager.getComponent(),"Export saved as  " + fn);
		}
	mdiManager.unBlockWindow(topLevelWindowManager.getManagerID());
	mdiManager.showWindow(topLevelWindowManager.getManagerID());
}

}
