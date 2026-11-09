package cbit.vcell.client.task;

import java.util.Hashtable;

import cbit.vcell.client.DocumentWindowManager;

/**
 * Added after a save's own steps when the user chose "Save, then Export": once the save has finished, opens the
 * usual Export / Save to File dialog for the (now saved) model. Skipped if the save failed or was cancelled.
 */
public class ExportAfterSave extends AsynchClientTask {

	public ExportAfterSave() {
		super("Preparing to export", TASKTYPE_SWING_BLOCKING, false, true, true); // no progress popup; only after a successful save
	}

	@Override
	public void run(Hashtable<String, Object> hashTable) {
		if (!(hashTable.get(CommonTask.DOCUMENT_WINDOW_MANAGER.name) instanceof DocumentWindowManager documentWindowManager)) return;
		// as the save's last act, once its window is unblocked: export runs as a task chain of its own
		ClientTaskDispatcher.setFinalWindow(hashTable, (ClientTaskDispatcher.FinalWindow) () ->
				documentWindowManager.getRequestManager().exportDocument(documentWindowManager, null));
	}
}
