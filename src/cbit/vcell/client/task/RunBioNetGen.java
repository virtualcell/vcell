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

import cbit.vcell.server.bionetgen.BNGOutput;
import cbit.vcell.server.bionetgen.BNGExecutorService;
/**
 * Insert the type's description here.
 * Creation date: (7/14/2006 5:11:43 PM)
 * @author: Anuradha Lakshminarayana
 */
public class RunBioNetGen extends AsynchClientTask {
	private final BNGExecutorService bngService;
	public RunBioNetGen(BNGExecutorService bngService) {
		super("Running BioNetGen ...", TASKTYPE_NONSWING_BLOCKING);
		this.bngService = bngService;
	}

/**
 * Insert the method's description here.
 * Creation date: (7/14/2006 5:11:43 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws Exception {
	BNGOutput bngOutput = bngService.executeBNG();
	if (bngOutput != null) {
		hashTable.put("bngOutput", bngOutput);
	}
}

}
