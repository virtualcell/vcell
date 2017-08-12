/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.simulation;
import javax.swing.JProgressBar;

import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.solver.Simulation;

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
		SimulationJobStatus jobStatus = simStatus.getJobStatus(index);
		Double progress = simStatus.getProgressAt(index);
		if (jobStatus != null) {
			if (progress != null && jobStatus.getSchedulerStatus().isRunning() && progress.doubleValue() > 0 ) {
				statusBars[index].setValue((int)(progress.doubleValue() * 100));
				return statusBars[index];
			} else {
				return jobStatus.getSimulationMessage().getDisplayMessage();
			}
		} else {
			return simStatus.getDetails();
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
