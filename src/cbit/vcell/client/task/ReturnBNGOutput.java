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
import java.util.List;

import cbit.vcell.bionetgen.BNGOutputFileParser;
import cbit.vcell.bionetgen.BNGOutputSpec;
import cbit.vcell.mapping.BioNetGenUpdaterCallback;
import cbit.vcell.server.bionetgen.BNGOutput;

public class ReturnBNGOutput extends AsynchClientTask {
	public ReturnBNGOutput() {
		super("Return BioNetGen output to requester", TASKTYPE_SWING_NONBLOCKING, false, false);
	}

public void run(Hashtable<String, Object> hashTable) throws Exception {
	List<BioNetGenUpdaterCallback> callbacks = (List<BioNetGenUpdaterCallback>)hashTable.get("bngCallbacks");
	BNGOutputSpec outputSpec = (BNGOutputSpec)hashTable.get("outputSpec");
	if (callbacks != null) {
		for (BioNetGenUpdaterCallback callback : callbacks) {
			callback.updateBioNetGenOutput(outputSpec);
		}
		BNGOutputFileParser.printBNGNetOutput(outputSpec);			// prints all output to console
	}
}

}
