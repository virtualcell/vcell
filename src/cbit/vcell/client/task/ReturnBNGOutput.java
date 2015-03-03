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

import java.text.DecimalFormat;
import java.util.Hashtable;

import cbit.vcell.bionetgen.BNGOutputSpec;
import cbit.vcell.mapping.BioNetGenUpdaterCallback;
import cbit.vcell.mapping.TaskCallbackMessage;
import cbit.vcell.mapping.TaskCallbackMessage.TaskCallbackStatus;
import cbit.vcell.server.bionetgen.BNGExecutorService;

public class ReturnBNGOutput extends AsynchClientTask {
	private static final String message = "Return BioNetGen output to requester...";
	
	private final BNGExecutorService bngService;
	public ReturnBNGOutput(BNGExecutorService bngService) {
		super(message, TASKTYPE_SWING_NONBLOCKING, false, false);
		this.bngService = bngService;
	}

public void run(Hashtable<String, Object> hashTable) throws Exception {
	if(!bngService.isStopped()) {
		broadcastRun();
	}
	BNGOutputSpec outputSpec = (BNGOutputSpec)hashTable.get("outputSpec");
	for(BioNetGenUpdaterCallback callback : bngService.getCallbacks()) {
		callback.updateBioNetGenOutput(outputSpec);
	}
	long endTime = System.currentTimeMillis();
	long delta = endTime - bngService.getStartTime();
	for(BioNetGenUpdaterCallback callback : bngService.getCallbacks()) {
		
		String t = new DecimalFormat("#.#").format((double)delta/1000);
		String s = "Total run time: " + t + " s.";
		TaskCallbackMessage tcm = new TaskCallbackMessage(TaskCallbackStatus.TaskEnd, s);
		callback.setNewCallbackMessage(tcm);
	}

//	BNGOutputFileParser.printBNGNetOutput(outputSpec);			// prints all output to console
}
private void broadcastRun() {
	for(BioNetGenUpdaterCallback callback : bngService.getCallbacks()) {
		TaskCallbackMessage tcm = new TaskCallbackMessage(TaskCallbackStatus.TaskStart, message);
		callback.setNewCallbackMessage(tcm);
	}
}

}
