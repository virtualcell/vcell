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

import cbit.vcell.bionetgen.BNGOutputFileParser;
import cbit.vcell.bionetgen.BNGOutputSpec;
import cbit.vcell.mapping.BioNetGenUpdaterCallback;
import cbit.vcell.mapping.TaskCallbackMessage;
import cbit.vcell.mapping.TaskCallbackMessage.TaskCallbackStatus;
import cbit.vcell.server.bionetgen.BNGExecutorService;
import cbit.vcell.server.bionetgen.BNGOutput;

public class CreateBNGOutputSpec extends AsynchClientTask {
	private static final String message = "Creating BNG output spec ...";
	
	private final BNGExecutorService bngService;
	public CreateBNGOutputSpec(BNGExecutorService bngService) {
		super(message, TASKTYPE_NONSWING_BLOCKING);
		this.bngService = bngService;
	}

public void run(Hashtable<String, Object> hashTable) throws Exception {
	if(!bngService.isStopped()) {
		broadcastRun();
	}
	BNGOutput bngOutput = (BNGOutput)hashTable.get("bngOutput");
	String bngNetString = bngOutput.getNetFileContent();
	BNGOutputSpec outputSpec = BNGOutputFileParser.createBngOutputSpec(bngNetString);

	if (outputSpec != null) {
		hashTable.put("outputSpec", outputSpec);
	}	
}
private void broadcastRun() {
	for(BioNetGenUpdaterCallback callback : bngService.getCallbacks()) {
		TaskCallbackMessage tcm = new TaskCallbackMessage(TaskCallbackStatus.TaskStart, message);
		callback.setNewCallbackMessage(tcm);
	}
}

}
