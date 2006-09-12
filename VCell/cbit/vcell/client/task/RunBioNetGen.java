package cbit.vcell.client.task;

import cbit.util.UserCancelException;
import cbit.vcell.desktop.controls.AsynchClientTask;

/**
 * Insert the type's description here.
 * Creation date: (7/14/2006 5:11:43 PM)
 * @author: Anuradha Lakshminarayana
 */
public class RunBioNetGen extends AsynchClientTask {
/**
 * Insert the method's description here.
 * Creation date: (7/14/2006 5:11:43 PM)
 * @return java.lang.String
 */
public String getTaskName() {
	return "Running BioNetGen ...";
}


/**
 * Insert the method's description here.
 * Creation date: (7/14/2006 5:11:43 PM)
 * @return int
 */
public int getTaskType() {
	return TASKTYPE_NONSWING_BLOCKING;
}


/**
 * Insert the method's description here.
 * Creation date: (7/14/2006 5:11:43 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(java.util.Hashtable hashTable) throws Exception {
	cbit.vcell.server.bionetgen.BNGInput bngInput = (cbit.vcell.server.bionetgen.BNGInput)hashTable.get("bngInput");
	cbit.vcell.server.bionetgen.BNGService bngService = (cbit.vcell.server.bionetgen.BNGService)hashTable.get("bngService");
	// execute BioNetGen
	cbit.vcell.server.bionetgen.BNGOutput bngOutput = bngService.executeBNG(bngInput);
	hashTable.put("bngOutput", bngOutput);
}


/**
 * Insert the method's description here.
 * Creation date: (7/14/2006 5:11:43 PM)
 * @return boolean
 */
public boolean skipIfAbort() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (7/14/2006 5:11:43 PM)
 * @return boolean
 */
public boolean skipIfCancel(UserCancelException exc) {
	return true;
}
}