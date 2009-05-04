package cbit.vcell.client.task;

import java.util.Hashtable;

/**
 * Insert the type's description here.
 * Creation date: (7/19/2006 2:19:07 PM)
 * @author: Anuradha Lakshminarayana
 */
public class DisplayBNGOutput extends AsynchClientTask {
	public DisplayBNGOutput() {
		super("Displaying BioNetGen output", TASKTYPE_SWING_NONBLOCKING);
	}

/**
 * Insert the method's description here.
 * Creation date: (7/19/2006 2:19:07 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws Exception {
	cbit.vcell.client.bionetgen.BNGOutputPanel bngOutputPanel = (cbit.vcell.client.bionetgen.BNGOutputPanel)hashTable.get("bngOutputPanel");
	cbit.vcell.server.bionetgen.BNGOutput bngOutput = (cbit.vcell.server.bionetgen.BNGOutput)hashTable.get("bngOutput");
	if (bngOutput != null) {
		bngOutputPanel.changeBNGPanelTab();
		bngOutputPanel.setBngOutput(bngOutput);		
	}
	bngOutputPanel.refreshButton(false);
}

}