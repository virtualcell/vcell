package cbit.vcell.client.data;

import cbit.rmi.event.DataJobListener;
import cbit.rmi.event.DataJobListenerHolder;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.simdata.DataOperation;
import cbit.vcell.simdata.DataOperationResults;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.simdata.ParticleDataBlock;
import cbit.vcell.simdata.SimDataBlock;
import org.vcell.util.document.TSJobResultsNoStats;
import org.vcell.util.document.TimeSeriesJobResults;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataJobID;
import cbit.plot.PlotData;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.DataAccessException;
import org.vcell.util.UserCancelException;

import java.util.Hashtable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers the failure contract of {@link PDEDataViewer.TimeSeriesDataRetrievalTask}.
 *
 * The task used to catch every exception, record it under a hashtable key and return normally.
 * Four of its six callers never read that key, so the next task in the chain ran against a null
 * result and the user saw an NPE in an unrelated plotting task while the real cause was thrown
 * away (issue #2063). Failing the chain is now the default, and the one caller that reports
 * failures in its own UI opts out through recordingFailure().
 */
@Tag("Fast")
public class TimeSeriesDataRetrievalTaskTest {

	@Test
	public void aFailureAbortsTheChainByDefault() {
		DataAccessException failure = new DataAccessException("no data for this time range");
		Hashtable<String, Object> hash = newHash();

		Exception thrown = assertThrows(Exception.class,
			() -> new PDEDataViewer.TimeSeriesDataRetrievalTask("t", new RecordingHolder(), failingContext(failure))
				.run(hash));

		assertSame(failure, thrown, "the original cause must reach ClientTaskDispatcher, not a substitute");
		assertFalse(hash.containsKey(PDEDataViewer.StringKey_timeSeriesJobResults),
			"no results should be published when the retrieval failed");
	}

	/**
	 * A cancellation has to propagate too: ClientTaskDispatcher.recordException routes
	 * UserCancelException to TASK_ABORTED_BY_USER and stays quiet, where any other exception
	 * raises an error dialog. Swallowing it turned a cancel into an NPE.
	 */
	@Test
	public void aCancellationAlsoAbortsTheChain() {
		UserCancelException cancel = UserCancelException.CANCEL_GENERIC;
		Exception thrown = assertThrows(Exception.class,
			() -> new PDEDataViewer.TimeSeriesDataRetrievalTask("t", new RecordingHolder(), failingContext(cancel))
				.run(newHash()));
		assertSame(cancel, thrown);
	}

	@Test
	public void recordingFailureKeepsTheChainRunningForKymograph() throws Exception {
		DataAccessException failure = new DataAccessException("no data for this time range");
		Hashtable<String, Object> hash = newHash();

		PDEDataViewer.TimeSeriesDataRetrievalTask.recordingFailure("t", new RecordingHolder(), failingContext(failure))
			.run(hash);

		assertSame(failure, hash.get(PDEDataViewer.StringKey_timeSeriesJobException),
			"the opted-out caller reports the failure itself, so it must still be recorded");
		assertFalse(hash.containsKey(PDEDataViewer.StringKey_timeSeriesJobResults));
	}

	@Test
	public void aSuccessfulRetrievalPublishesItsResults() throws Exception {
		TimeSeriesJobResults results = new TSJobResultsNoStats(
			new String[]{"s0"}, new int[][]{{0}}, new double[]{0.0}, new double[][][]{{{0.0}}});
		Hashtable<String, Object> hash = newHash();

		new PDEDataViewer.TimeSeriesDataRetrievalTask("t", new RecordingHolder(), succeedingContext(results))
			.run(hash);

		assertSame(results, hash.get(PDEDataViewer.StringKey_timeSeriesJobResults));
		assertFalse(hash.containsKey(PDEDataViewer.StringKey_timeSeriesJobException));
	}

	/** The data-job listener must be removed however the task ends, or listeners accumulate. */
	@Test
	public void theDataJobListenerIsAlwaysRemoved() {
		RecordingHolder holder = new RecordingHolder();
		assertThrows(Exception.class,
			() -> new PDEDataViewer.TimeSeriesDataRetrievalTask("t", holder, failingContext(new DataAccessException("x")))
				.run(newHash()));
		assertTrue(holder.added <= holder.removed + 1,
			"a listener added before the failure must be removed again");
	}

	/**
	 * The server fires DATA_PROGRESS for this job while getTimeSeriesValues() blocks, so the
	 * listener has to be registered before the call. It was registered after it for years - ever
	 * since getTimeSeriesValues became a blocking rpc in 1c16cce - so the progress was computed,
	 * serialised, sent and then dropped, and the task dialog never advanced.
	 */
	@Test
	public void theProgressListenerIsRegisteredBeforeTheBlockingCall() throws Exception {
		RecordingHolder holder = new RecordingHolder();
		boolean[] wasRegisteredWhenTheCallRan = new boolean[1];
		PDEDataContext context = new StubPDEDataContext() {
			@Override
			public TimeSeriesJobResults getTimeSeriesValues(TimeSeriesJobSpec spec) {
				wasRegisteredWhenTheCallRan[0] = holder.added > 0;
				return new TSJobResultsNoStats(
					new String[]{"s0"}, new int[][]{{0}}, new double[]{0.0}, new double[][][]{{{0.0}}});
			}
		};

		new PDEDataViewer.TimeSeriesDataRetrievalTask("t", holder, context).run(newHash());

		assertTrue(wasRegisteredWhenTheCallRan[0],
			"progress events fired during the call are dropped unless the listener is already registered");
		assertEquals(holder.added, holder.removed, "the listener must be removed again");
	}

	private static Hashtable<String, Object> newHash() {
		Hashtable<String, Object> hash = new Hashtable<>();
		hash.put(PDEDataViewer.StringKey_timeSeriesJobSpec, new TimeSeriesJobSpec(
			new String[]{"s0"}, new int[][]{{0}}, null, 0.0, 1, 1.0,
			VCDataJobID.createVCDataJobID(new User("test", null), true)));
		return hash;
	}

	private static PDEDataContext failingContext(Exception toThrow) {
		return new StubPDEDataContext() {
			@Override
			public TimeSeriesJobResults getTimeSeriesValues(TimeSeriesJobSpec spec) throws DataAccessException {
				if (toThrow instanceof DataAccessException) {
					throw (DataAccessException) toThrow;
				}
				throw (RuntimeException) toThrow;
			}
		};
	}

	private static PDEDataContext succeedingContext(TimeSeriesJobResults results) {
		return new StubPDEDataContext() {
			@Override
			public TimeSeriesJobResults getTimeSeriesValues(TimeSeriesJobSpec spec) {
				return results;
			}
		};
	}

	private static class RecordingHolder implements DataJobListenerHolder {
		int added;
		int removed;
		public void addDataJobListener(DataJobListener l) { added++; }
		public void removeDataJobListener(DataJobListener l) { removed++; }
	}

	private static abstract class StubPDEDataContext extends PDEDataContext {
		public AnnotatedFunction[] getFunctions() { return new AnnotatedFunction[0]; }
		public PlotData getLineScan(String variable, double time, SpatialSelection sel) { return null; }
		public DataOperationResults doDataOperation(DataOperation op) { return null; }
		public void makeRemoteFile(ExportSpecs exportSpecs) { }
		protected ParticleDataBlock getParticleDataBlock(double time) { return null; }
		protected SimDataBlock getSimDataBlock(String varName, double time) { return null; }
		public void refreshIdentifiers() { }
		public void refreshTimes() { }
	}
}
