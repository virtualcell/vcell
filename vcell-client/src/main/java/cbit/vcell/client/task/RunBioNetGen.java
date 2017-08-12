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

import cbit.vcell.mapping.BioNetGenUpdaterCallback;
import cbit.vcell.mapping.TaskCallbackMessage;
import cbit.vcell.mapping.TaskCallbackMessage.TaskCallbackStatus;
import cbit.vcell.server.bionetgen.BNGExecutorService;
import cbit.vcell.server.bionetgen.BNGOutput;
/**
 * Insert the type's description here.
 * Creation date: (7/14/2006 5:11:43 PM)
 * @author: Anuradha Lakshminarayana
 */
public class RunBioNetGen extends AsynchClientTask {
	private static final String message = "Running BioNetGen ...";
	
	private final BNGExecutorService bngService;
	public RunBioNetGen(BNGExecutorService bngService) {
		super(message, TASKTYPE_NONSWING_BLOCKING);
		this.bngService = bngService;
	}

public void run(Hashtable<String, Object> hashTable) throws Exception {
	broadcastRun();
	BNGOutput bngOutput = bngService.executeBNG();
	if (bngOutput != null) {
		hashTable.put("bngOutput", bngOutput);
	}
}
private void broadcastRun() {
	for(BioNetGenUpdaterCallback callback : bngService.getCallbacks()) {
		TaskCallbackMessage tcm = new TaskCallbackMessage(TaskCallbackStatus.TaskStart, message);
		callback.setNewCallbackMessage(tcm);
	}
}

}
