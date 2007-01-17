package cbit.vcell.client.desktop.simulation;
import cbit.vcell.solver.ode.gui.SimulationStatus;
import cbit.vcell.solver.Simulation;
import javax.swing.JProgressBar;

/**
 * Insert the type's description here.
 * Creation date: (8/21/2006 9:01:53 AM)
 * @author: Jim Schaff
 */
public class SimulationStatusDetails {
	private Simulation sim = null;
	private SimulationWorkspace simWorkspace = null;
	private JProgressBar[] statusBars = null;

/**
 * ClientSimulationStatusDetails constructor comment.
 */
public SimulationStatusDetails(SimulationWorkspace arg_simWorkspace, Simulation arg_sim) {
	super();
	simWorkspace = arg_simWorkspace;
	sim = arg_sim;
	statusBars = new JProgressBar[sim.getScanCount()];
	for (int i = 0; i < sim.getScanCount(); i ++) {
		statusBars[i] = new JProgressBar();
		statusBars[i].setStringPainted(true);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/21/2006 9:55:49 AM)
 * @return cbit.vcell.solver.ode.gui.SimulationStatus
 * @param index int
 */
public Simulation getSimulation() {
	return sim;
}


/**
 * Insert the method's description here.
 * Creation date: (8/21/2006 9:55:49 AM)
 * @return cbit.vcell.solver.ode.gui.SimulationStatus
 * @param index int
 */
public SimulationStatus getSimulationStatus() {
	return getSimulationWorkspace().getSimulationStatus(sim);
}


/**
 * Comment
 */
Object getSimulationStatusDisplay(int index) {
	SimulationStatus simStatus = simWorkspace.getSimulationStatus(sim);
	if (simStatus != null) {
		cbit.vcell.messaging.db.SimulationJobStatus jobStatus = simStatus.getJobStatus(index);
		Double progress = simStatus.getProgressAt(index);
		if (jobStatus != null) {
			if (progress != null && jobStatus.isRunning() && progress.doubleValue() > 0 ) {
				statusBars[index].setValue((int)(progress.doubleValue() * 100));
				return statusBars[index];
			} else {
				return jobStatus.getStatusMessage();
			}
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (8/21/2006 9:55:49 AM)
 * @return cbit.vcell.solver.ode.gui.SimulationStatus
 * @param index int
 */
public SimulationWorkspace getSimulationWorkspace() {
	return simWorkspace;
}
}