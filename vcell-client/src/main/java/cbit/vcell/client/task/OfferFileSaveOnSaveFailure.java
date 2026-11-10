package cbit.vcell.client.task;

import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.exporter.FileFilters;

import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.PopupGenerator;

/**
 * The last step of a save to the database. If the save failed, it shows the error itself, with a way to keep the
 * work: save the model as a VCML file on the user's computer (issue #1451). VCML export skips the checks a save
 * makes, so this works even when the model's own errors are what stopped the save.
 * <br/>
 * It runs even after an earlier step failed, which is the point; it does nothing when the save succeeded, or the
 * user cancelled it. Once it has shown the error, the dispatcher's usual error popup is skipped, so there's one dialog.
 */
public class OfferFileSaveOnSaveFailure extends AsynchClientTask {
	private static final Logger lg = LogManager.getLogger(OfferFileSaveOnSaveFailure.class);

	public static final String EXPLANATION = "To keep your work, save the model as a VCML file on your computer. "
			+ "You can open it again later to fix it, or send it to the VCell team for help.";
	public static final String BUTTON_LABEL = "Save to File...";

	public OfferFileSaveOnSaveFailure() {
		super("Checking the save", TASKTYPE_SWING_BLOCKING, false, false, true); // no progress popup; runs after a failure
	}

	@Override
	public void run(Hashtable<String, Object> hashTable) {
		if (!(hashTable.get(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR) instanceof Throwable saveFailure)) return;
		if (!(hashTable.get(CommonTask.DOCUMENT_WINDOW_MANAGER.name) instanceof DocumentWindowManager documentWindowManager)) return; // the usual popup shows it

		hashTable.remove(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR); // shown here instead, once
		OfferFileSaveOnSaveFailure.lg.error("Saving to the database failed; offering to save to a file", saveFailure);
		DialogUtils.ErrorAction saveToFile = new DialogUtils.ErrorAction(OfferFileSaveOnSaveFailure.EXPLANATION,
				OfferFileSaveOnSaveFailure.BUTTON_LABEL,
				() -> ClientTaskDispatcher.setFinalWindow(hashTable, (ClientTaskDispatcher.FinalWindow) () -> // once this save has finished
						documentWindowManager.getRequestManager().exportDocument(documentWindowManager, FileFilters.FILE_FILTER_VCML)));
		PopupGenerator.showErrorDialog(documentWindowManager, ClientTaskDispatcher.describeFailure(saveFailure), saveFailure, saveToFile);
	}
}
