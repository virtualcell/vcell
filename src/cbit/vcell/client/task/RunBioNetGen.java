package cbit.vcell.client.task;
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
	try {
		cbit.vcell.server.bionetgen.BNGInput bngInput = (cbit.vcell.server.bionetgen.BNGInput)hashTable.get("bngInput");
		cbit.vcell.server.bionetgen.BNGOutput bngOutput = cbit.vcell.server.bionetgen.BNGUtils.executeBNG(bngInput);
		if (bngOutput != null) {
			hashTable.put("bngOutput", bngOutput);
		}
	} finally {
		cbit.vcell.client.bionetgen.BNGOutputPanel bngOutputPanel = (cbit.vcell.client.bionetgen.BNGOutputPanel)hashTable.get("bngOutputPanel");
		bngOutputPanel.refreshButton(false);
	}
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