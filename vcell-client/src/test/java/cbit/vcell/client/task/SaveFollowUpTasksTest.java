package cbit.vcell.client.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Hashtable;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Fast")
public class SaveFollowUpTasksTest {

	@Test
	public void describeFailureListsEachCauseInTurn() {
		Exception failure = new IllegalStateException("could not save", new java.sql.SQLException("value too large"));
		assertEquals("IllegalStateException-could not save\nSQLException-value too large\n", ClientTaskDispatcher.describeFailure(failure));
		assertEquals("RuntimeException-\n", ClientTaskDispatcher.describeFailure(new RuntimeException()), "a missing message reads as empty");
	}

	@Test
	public void offerFileSaveRunsAfterAFailureButNotAfterACancel() {
		OfferFileSaveOnSaveFailure offer = new OfferFileSaveOnSaveFailure();
		assertFalse(offer.skipIfAbort(), "it has to run once a save step has failed");
	}

	@Test
	public void offerFileSaveDoesNothingWhenTheSaveSucceeded() {
		Hashtable<String, Object> hash = new Hashtable<>();
		new OfferFileSaveOnSaveFailure().run(hash);
		assertTrue(hash.isEmpty(), "nothing shown, nothing scheduled");
	}

	@Test
	public void offerFileSaveLeavesTheUsualPopupWithoutAModelWindow() {
		Hashtable<String, Object> hash = new Hashtable<>();
		RuntimeException failure = new RuntimeException("database unavailable");
		hash.put(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR, failure);
		new OfferFileSaveOnSaveFailure().run(hash);
		assertSame(failure, hash.get(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR), "still there for the dispatcher's own error popup");
	}

	@Test
	public void exportAfterSaveOnlyRunsAfterASuccessfulSave() {
		ExportAfterSave exportAfterSave = new ExportAfterSave();
		assertEquals(true, exportAfterSave.skipIfAbort(), "skipped when the save failed");
		Hashtable<String, Object> hash = new Hashtable<>();
		exportAfterSave.run(hash); // no model window: nothing to export
		assertTrue(hash.isEmpty(), "no export scheduled");
	}
}
