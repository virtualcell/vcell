package cbit.vcell.solver.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import cbit.vcell.resource.PropertyLoader;

/**
 * A solver exit code is one of the few things a user actually reads when a run fails, so the
 * text is the feature here, not an implementation detail.
 *
 * Exit 124 is GNU {@code timeout} killing the process it wraps, which is how every Langevin
 * (SpringSaLaD) task is launched. Reported as a bare "solver exited (code=124)" it reads as a
 * crash: two multi-day runs were killed at exactly their 4-day mark by the configured per-task
 * limit, and the message gave no hint that a limit existed, let alone what it was.
 */
@Tag("Fast")
public class SimulationMessageExitCodeTest {

	private String previous;
	private boolean saved = false;

	private void setLimit(String seconds) {
		if (!saved) {
			previous = System.getProperty(PropertyLoader.slurm_langevin_timeoutPerTaskSeconds);
			saved = true;
		}
		if (seconds == null) {
			System.clearProperty(PropertyLoader.slurm_langevin_timeoutPerTaskSeconds);
		} else {
			System.setProperty(PropertyLoader.slurm_langevin_timeoutPerTaskSeconds, seconds);
		}
	}

	@AfterEach
	public void restore() {
		if (!saved) {
			return;
		}
		if (previous == null) {
			System.clearProperty(PropertyLoader.slurm_langevin_timeoutPerTaskSeconds);
		} else {
			System.setProperty(PropertyLoader.slurm_langevin_timeoutPerTaskSeconds, previous);
		}
	}

	@Test
	public void aTimedOutSolverSaysSoAndNamesTheLimit() {
		setLimit("345600"); // the deployed value: 4 days
		String message = SimulationMessage.WorkerExited(124).getDisplayMessage();

		assertTrue(message.contains("time limit"), "must say it hit a time limit: " + message);
		assertTrue(message.contains("4 days"), "must name the configured limit: " + message);
		assertTrue(message.contains("124"), "keep the exit code for support: " + message);
		assertFalse(message.equals("solver exited (code=124)"), "the bare form is what misled a user");
	}

	/** The client has no such property set; the message must still be useful, just less specific. */
	@Test
	public void theLimitIsOmittedRatherThanGuessedWhenUnknown() {
		setLimit(null);
		String message = SimulationMessage.WorkerExited(124).getDisplayMessage();

		assertTrue(message.contains("time limit"), "still explains what happened: " + message);
		assertFalse(message.contains("time limit of"),
				"must not invent a limit it cannot read: " + message);
	}

	/** A malformed property must not turn a solver status message into an exception. */
	@Test
	public void aMalformedLimitDegradesInsteadOfThrowing() {
		setLimit("not-a-number");
		String message = SimulationMessage.WorkerExited(124).getDisplayMessage();
		assertTrue(message.contains("time limit"), message);
	}

	/** Every other exit code keeps the wording that support and the docs already know. */
	@Test
	public void otherExitCodesAreUnchanged() {
		setLimit("345600");
		assertEquals("solver exited (code=1)", SimulationMessage.WorkerExited(1).getDisplayMessage());
		assertEquals("solver exited (code=0)", SimulationMessage.WorkerExited(0).getDisplayMessage());
		assertEquals("solver exited (code=137)", SimulationMessage.WorkerExited(137).getDisplayMessage());
	}

	/** The persisted twin is what the user sees later, so it must not drift from the live one. */
	@Test
	public void thePersistentMessageMatches() {
		setLimit("345600");
		assertEquals(SimulationMessage.WorkerExited(124).getDisplayMessage(),
				SimulationMessagePersistent.WorkerExited(124).getDisplayMessage(),
				"persisted and live messages must agree -- they are the same event");
	}

	/**
	 * The dispatcher asks this before prefixing "solver stopped unexpectedly", which would
	 * contradict a message that says the solver stopped exactly when it was told to.
	 */
	@Test
	public void aTimeLimitMessageIsRecognisedSoItIsNotPrefixedAsUnexpected() {
		setLimit("345600");
		assertTrue(SimulationMessage.describesTimeLimit(
				SimulationMessage.WorkerExited(124).getDisplayMessage()));
		assertFalse(SimulationMessage.describesTimeLimit(
				SimulationMessage.WorkerExited(1).getDisplayMessage()));
		assertFalse(SimulationMessage.describesTimeLimit(null));
	}
}
