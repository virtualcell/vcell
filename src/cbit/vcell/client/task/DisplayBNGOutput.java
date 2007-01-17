package cbit.vcell.client.task;
/**
 * Insert the type's description here.
 * Creation date: (7/19/2006 2:19:07 PM)
 * @author: Anuradha Lakshminarayana
 */
public class DisplayBNGOutput extends AsynchClientTask {
/**
 * Insert the method's description here.
 * Creation date: (7/19/2006 2:19:07 PM)
 * @return java.lang.String
 */
public String getTaskName() {
	return "Displaying BioNetGen output";
}


/**
 * Insert the method's description here.
 * Creation date: (7/19/2006 2:19:07 PM)
 * @return int
 */
public int getTaskType() {
	return TASKTYPE_SWING_NONBLOCKING;
}


/**
 * Insert the method's description here.
 * Creation date: (7/19/2006 2:19:07 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(java.util.Hashtable hashTable) throws Exception {
	cbit.vcell.client.bionetgen.BNGOutputPanel bngOutputPanel = (cbit.vcell.client.bionetgen.BNGOutputPanel)hashTable.get("bngOutputPanel");
	cbit.vcell.server.bionetgen.BNGOutput bngOutput = (cbit.vcell.server.bionetgen.BNGOutput)hashTable.get("bngOutput");
	if (bngOutput != null) {
		bngOutputPanel.changeBNGPanelTab();
		bngOutputPanel.setBngOutput(bngOutput);		
	}
	bngOutputPanel.refreshButton(false);
}


/**
 * Insert the method's description here.
 * Creation date: (7/19/2006 2:19:07 PM)
 * @return boolean
 */
public boolean skipIfAbort() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (7/19/2006 2:19:07 PM)
 * @return boolean
 */
public boolean skipIfCancel(UserCancelException exc) {
	return true;
}
}