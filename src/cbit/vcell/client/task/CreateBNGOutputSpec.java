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
import cbit.vcell.server.bionetgen.BNGInput;
import cbit.vcell.server.bionetgen.BNGOutput;
import cbit.vcell.server.bionetgen.BNGExecutorService;

public class CreateBNGOutputSpec extends AsynchClientTask {
	
	public CreateBNGOutputSpec() {
		super("Creating BNG output spec ...", TASKTYPE_NONSWING_BLOCKING);
	}

public void run(Hashtable<String, Object> hashTable) throws Exception {
	BNGOutput bngOutput = (BNGOutput)hashTable.get("bngOutput");
	String bngNetString = bngOutput.getNetFileContent();
	BNGOutputSpec outputSpec = BNGOutputFileParser.createBngOutputSpec(bngNetString);

	if (outputSpec != null) {
		hashTable.put("outputSpec", outputSpec);
	}	
}

}
