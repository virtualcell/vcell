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

public class ReturnBNGOutput extends AsynchClientTask {
	private final BioNetGenUpdaterCallback callback;
	public ReturnBNGOutput(BioNetGenUpdaterCallback callback) {
		super("Return BioNetGen output to requester", TASKTYPE_SWING_NONBLOCKING, false, false);
		this.callback = callback;
	}

public void run(Hashtable<String, Object> hashTable) throws Exception {
	BNGOutputSpec outputSpec = (BNGOutputSpec)hashTable.get("outputSpec");
	if (callback != null) {
		callback.updateBioNetGenOutput(outputSpec);
	}
	BNGOutputFileParser.printBNGNetOutput(outputSpec);			// prints all output to console
}

}
