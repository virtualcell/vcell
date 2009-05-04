package cbit.vcell.client.task;

import java.util.Hashtable;
import cbit.vcell.client.MDIManager;
import cbit.vcell.client.TopLevelWindowManager;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class FinishExport extends AsynchClientTask {
	
	public FinishExport() {
		super("Updating the workspace", TASKTYPE_SWING_BLOCKING, false, false);
	}

/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws java.lang.Exception {
	MDIManager mdiManager= (MDIManager)hashTable.get("mdiManager");
	TopLevelWindowManager topLevelWindowManager = (TopLevelWindowManager)hashTable.get("topLevelWindowManager");
	mdiManager.unBlockWindow(topLevelWindowManager.getManagerID());
	mdiManager.showWindow(topLevelWindowManager.getManagerID());
}

}
