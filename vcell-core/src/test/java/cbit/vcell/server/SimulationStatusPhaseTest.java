package cbit.vcell.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import cbit.rmi.event.SimulationJobStatusEvent;
import cbit.vcell.server.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.server.SimulationJobStatus.SimulationQueueID;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;

/**
 * A remote FEniCSx run names its phase on each PROGRESS worker event, as the status message
 * {@code WORKEREVENT_PROGRESS|<phase>} (vcell-fenics ADR 011 §4), which {@code WorkerEventMessage} turns
 * into the job's SimulationMessage with {@link SimulationMessage#fromSerializedMessage}. The simulation
 * list then shows "meshing" or "solving 37%" in the progress bar instead of a bare "0%".
 */
@Tag("Fast")
public class SimulationStatusPhaseTest {

	private static final VCSimulationIdentifier SIM_ID = new VCSimulationIdentifier(new KeyValue("1585623750"), User.tempUser);
	private static final Date SUBMITTED = new Date(0);

	private static SimulationJobStatus running(SimulationMessage message) {
		return new SimulationJobStatus(VCellServerID.getServerID("TEST"), SIM_ID, 0, SUBMITTED, SchedulerStatus.RUNNING, 0, message,
				new SimulationQueueEntryStatus(SUBMITTED, 0, SimulationQueueID.QUEUE_ID_NULL),
				new SimulationExecutionStatus(SUBMITTED, "node07", SUBMITTED, null, false, null));
	}

	/** the job status the broker makes of a worker event's status message, as WorkerEventMessage does */
	private static SimulationJobStatus fromWorker(String statusMsg) {
		return running(SimulationMessage.fromSerializedMessage(statusMsg));
	}

	private static SimulationStatus update(SimulationStatus status, SimulationJobStatus jobStatus, Double progress) {
		return SimulationStatus.updateFromJobEvent(status,
				new SimulationJobStatusEvent(SimulationStatusPhaseTest.class, SIM_ID.getID(), jobStatus, progress, 0.0, "user"));
	}

	@Test
	public void phasesShowInTheSimulationList() {
		SimulationStatus status = update(null, running(SimulationMessage.workerStarting("Starting Job")), null);
		assertNull(status.getRunningPhaseDisplay());
		assertEquals("Starting Job", status.getDetails());

		status = update(status, fromWorker("WORKEREVENT_PROGRESS|loading model"), 0.0);
		assertEquals("loading model", status.getRunningPhaseDisplay());
		// the next phases arrive at the same 0%: each must still replace the last
		status = update(status, fromWorker("WORKEREVENT_PROGRESS|meshing"), 0.0);
		assertEquals("meshing", status.getRunningPhaseDisplay());
		status = update(status, fromWorker("WORKEREVENT_PROGRESS|compiling"), 0.0);
		assertEquals("compiling", status.getRunningPhaseDisplay());
		status = update(status, fromWorker("WORKEREVENT_PROGRESS|solving"), 0.0);
		assertEquals("solving", status.getRunningPhaseDisplay());
		status = update(status, fromWorker("WORKEREVENT_PROGRESS|solving"), 0.374);
		assertEquals("solving 37%", status.getRunningPhaseDisplay());
		assertEquals(0.374, status.getProgress(), 0.0);
		// a data event at the same progress does not replace the phase
		status = update(status, running(SimulationMessage.workerData(0.005)), 0.374);
		assertEquals("solving 37%", status.getRunningPhaseDisplay());
		status = update(status, fromWorker("WORKEREVENT_PROGRESS|solving"), 1.0);
		assertEquals("solving", status.getRunningPhaseDisplay());
		status = update(status, fromWorker("WORKEREVENT_PROGRESS|writing results"), 1.0);
		assertEquals("writing results", status.getRunningPhaseDisplay());
	}

	@Test
	public void anOlderSolverShowsTheBareProgress() {
		// before phases, a PROGRESS event carried no status message: WorkerEventMessage made workerProgress(p)
		SimulationStatus status = update(null, running(SimulationMessage.workerProgress(0.0)), 0.0);
		assertNull(status.getRunningPhaseDisplay());
		status = update(status, running(SimulationMessage.workerProgress(0.5)), 0.5);
		assertNull(status.getRunningPhaseDisplay());
		assertNull(SimulationMessage.MESSAGE_WORKEREVENT_PROGRESS.getProgressPhase());
		assertNull(SimulationMessage.solverProgress(0.25).getProgressPhase());
	}

	@Test
	public void equalProgressWithoutANewPhaseIsStillNotNews() {
		SimulationJobStatus meshing = fromWorker("WORKEREVENT_PROGRESS|meshing");
		assertTrue(meshing.isSupercededBy(fromWorker("WORKEREVENT_PROGRESS|compiling"), 0.0, 0.0));
		assertFalse(meshing.isSupercededBy(fromWorker("WORKEREVENT_PROGRESS|meshing"), 0.0, 0.0), "the same phase again");
		assertFalse(meshing.isSupercededBy(running(SimulationMessage.workerData(0.0)), 0.0, 0.0), "not a phase");
		assertFalse(running(SimulationMessage.workerProgress(0.5)).isSupercededBy(running(SimulationMessage.workerProgress(0.5)), 0.5, 0.5));
		assertFalse(fromWorker("WORKEREVENT_PROGRESS|solving").isSupercededBy(fromWorker("WORKEREVENT_PROGRESS|meshing"), 0.5, 0.2),
				"progress never goes back");
	}

	@Test
	public void describeProgressPhase() {
		assertEquals("meshing", SimulationMessage.describeProgressPhase("meshing", 0.0));
		assertEquals("solving 0%", SimulationMessage.describeProgressPhase("solving", 0.004));
		assertEquals("solving 99%", SimulationMessage.describeProgressPhase("solving", 0.999));
		assertEquals("writing results", SimulationMessage.describeProgressPhase("writing results", 1.0));
		assertEquals("solving", SimulationMessage.describeProgressPhase("solving", null));
	}
}
