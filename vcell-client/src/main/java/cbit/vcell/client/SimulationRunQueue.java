package cbit.vcell.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;

import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.solver.Simulation;

/**
 * The simulation runs requested from one model window, newest first: each simulation's latest run on the VCell server
 * (whoever started it), and each run started on this computer. Runs on other computers aren't known here, so aren't
 * listed. Its runs change as their statuses do; listeners hear of each change on the event thread.
 */
public class SimulationRunQueue {

	/** Where a run runs. */
	public enum Where { SERVER, LOCAL }

	/** One run, as it stands. */
	public static final class Run {
		private final Simulation simulation;
		private final Where where;
		private volatile Date dispatched;
		private volatile String status;
		private volatile Double progress; // 0..1, or null when there's none to show
		private volatile boolean active;
		private volatile boolean failed;

		Run(Simulation simulation, Where where, Date dispatched, String status) {
			this.simulation = simulation;
			this.where = where;
			this.dispatched = dispatched;
			this.status = status;
			this.active = true;
		}

		public Simulation getSimulation() { return this.simulation; }
		public Where getWhere() { return this.where; }
		public Date getDispatched() { return this.dispatched; }
		public String getStatus() { return this.status; }
		public Double getProgress() { return this.progress; }
		/** Waiting, queued or running: not finished yet. */
		public boolean isActive() { return this.active; }
		public boolean isFailed() { return this.failed; }

		/** The simulation's application (or model): what owns it. */
		public String getOwnerName() {
			return this.simulation.getSimulationOwner() == null ? "" : this.simulation.getSimulationOwner().getName();
		}
	}

	private final List<Run> runs = new CopyOnWriteArrayList<>();
	private final Map<Simulation, Run> serverRuns = new IdentityHashMap<>(); // each simulation's latest server run
	private final List<Runnable> listeners = new CopyOnWriteArrayList<>();
	private volatile boolean changeQueued = false;

	/** The runs, most recently dispatched first. */
	public List<Run> getRuns() {
		List<Run> sorted = new ArrayList<>(this.runs);
		sorted.sort(Comparator.comparing(Run::getDispatched, Comparator.nullsLast(Comparator.reverseOrder())));
		return sorted;
	}

	/** Told (on the event thread) whenever a run is added or changes. */
	public void addChangeListener(Runnable listener) {
		this.listeners.add(listener);
	}

	public void removeChangeListener(Runnable listener) {
		this.listeners.remove(listener);
	}

	/**
	 * A simulation's server status, as it changes (a start request, the server's updates, a stop): its latest server
	 * run is added or brought up to date. A simulation that never ran on the server isn't listed.
	 */
	public synchronized void serverStatus(Simulation simulation, SimulationStatus status) {
		if (simulation == null || status == null) return;
		if (status.isNeverRan() || status.isUnknown()) return;
		Run run = this.serverRuns.get(simulation);
		Date submitted = SimulationRunQueue.submitDate(status, simulation.getJobCount());
		if (run == null) {
			run = new Run(simulation, Where.SERVER, submitted != null ? submitted : new Date(), status.getStatusString());
			this.serverRuns.put(simulation, run);
			this.runs.add(run);
		} else if (status.isStartRequested() && !run.active) {
			run.dispatched = new Date(); // run again: a new dispatch, at the front until the server says when
		}
		if (submitted != null && !status.isStartRequested()) run.dispatched = submitted;
		run.status = SimulationRunQueue.capitalized(status.getStatusString()); // the server's are lower case: "completed"
		run.progress = status.isRunning() ? status.getProgress() : null;
		run.failed = status.isFailed();
		run.active = status.isActive() || status.isStartRequested();
		this.changed();
	}

	/**
	 * Forgets the server runs listed, for a model whose simulations were replaced (reopened, saved, converted): their
	 * statuses are read again, for its new simulations. Runs on this computer stay listed.
	 */
	public synchronized void forgetServerRuns() {
		this.runs.removeAll(this.serverRuns.values());
		this.serverRuns.clear();
		this.changed();
	}

	/** A run started on this computer: listed now, and brought up to date by the calls on the returned run. */
	public Run localRunStarted(Simulation simulation) {
		Run run = new Run(simulation, Where.LOCAL, new Date(), "Starting...");
		this.runs.add(run);
		this.changed();
		return run;
	}

	/** A local run's progress (0..1) and what it's doing. */
	public void localProgress(Run run, double progress, String status) {
		run.progress = Math.max(0, Math.min(1, progress));
		if (status != null) run.status = status;
		this.changed();
	}

	/** A local run's end: finished, failed (with why) or stopped. */
	public void localEnded(Run run, boolean finished, String failure) {
		run.active = false;
		run.progress = null;
		run.failed = !finished && failure != null;
		run.status = finished ? "Completed" : failure != null ? "Failed: " + failure : "Stopped";
		this.changed();
	}

	// the earliest of its jobs' submissions to the server: when it was dispatched
	static Date submitDate(SimulationStatus status, int jobCount) {
		Date earliest = null;
		for (int i = 0; i < Math.max(1, jobCount); i++) {
			SimulationJobStatus job = status.getJobStatus(i); // null for a job the server hasn't reported
			if (job == null || job.getSubmitDate() == null) continue;
			if (earliest == null || job.getSubmitDate().before(earliest)) earliest = job.getSubmitDate();
		}
		return earliest;
	}

	static String capitalized(String text) {
		if (text == null || text.isEmpty()) return text;
		return Character.toUpperCase(text.charAt(0)) + text.substring(1);
	}

	// a burst of changes heard once, on the event thread
	private void changed() {
		if (this.changeQueued) return;
		this.changeQueued = true;
		SwingUtilities.invokeLater(() -> {
			this.changeQueued = false;
			for (Runnable listener : this.listeners) listener.run();
		});
	}
}
