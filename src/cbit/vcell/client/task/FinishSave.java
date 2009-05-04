package cbit.vcell.client.task;
import org.vcell.util.document.VCDocument;

import java.util.Hashtable;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.MDIManager;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class FinishSave extends AsynchClientTask {
	
	public FinishSave() {
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
	DocumentWindowManager documentWindowManager = (DocumentWindowManager)hashTable.get("documentWindowManager");
	if (hashTable.containsKey("savedDocument")) {
		VCDocument savedDocument = (VCDocument)hashTable.get("savedDocument");
		documentWindowManager.resetDocument(savedDocument);
	}
	mdiManager.unBlockWindow(documentWindowManager.getManagerID());
	mdiManager.showWindow(documentWindowManager.getManagerID());
}

}