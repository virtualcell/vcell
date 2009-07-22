package cbit.vcell.client.task;

import java.util.Hashtable;

import cbit.vcell.client.bionetgen.BNGOutputPanel;
import cbit.vcell.server.bionetgen.BNGInput;
import cbit.vcell.server.bionetgen.BNGOutput;
import cbit.vcell.server.bionetgen.BNGUtils;
/**
 * Insert the type's description here.
 * Creation date: (7/14/2006 5:11:43 PM)
 * @author: Anuradha Lakshminarayana
 */
public class RunBioNetGen extends AsynchClientTask {
	
	public RunBioNetGen() {
		super("Running BioNetGen ...", TASKTYPE_NONSWING_BLOCKING);
	}

/**
 * Insert the method's description here.
 * Creation date: (7/14/2006 5:11:43 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws Exception {
	BNGInput bngInput = (BNGInput)hashTable.get("bngInput");
	BNGOutput bngOutput = BNGUtils.executeBNG(bngInput);
	if (bngOutput != null) {
		hashTable.put("bngOutput", bngOutput);
	}	
}

}