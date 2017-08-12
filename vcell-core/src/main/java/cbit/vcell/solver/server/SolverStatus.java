/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.server;


/**
 * Indicates the status of a solver.
 * Creation date: (8/16/2000 11:10:33 PM)
 * @author: John Wagner
 */
public class SolverStatus implements java.io.Serializable {
	//  Should we have a SOLVER_STOPPED as well, indicating
	//  that the solver was intentionally stopped?
	public static final int SOLVER_READY = 0;
	public static final int SOLVER_RUNNING = 1;
	public static final int SOLVER_FINISHED = 2;
	public static final int SOLVER_ABORTED = 3;
	public static final int SOLVER_STOPPED = 4;
	public static final int SOLVER_STARTING = 5;
	private static final String SOLVER_STATUS[] = {"Ready", "Running", "Finished", "Aborted", "Stopped", "Starting"}; 
	public int fieldStatus = 0;
	private SimulationMessage fieldSimulationMessage = null;

	/**
	 * SolverStatus constructor comment.
	 */
	public SolverStatus(int status, SimulationMessage simulationMessage) {
		super();
		fieldStatus = status;
		fieldSimulationMessage = simulationMessage;
	}
	/**
	 * This method was created in VisualAge.
	 * @return boolean
	 * @param executableStatus cbit.vcell.solvers.ExecutableStatus
	 */
	public boolean equals(SolverStatus solverStatus) {
		return (getStatus() == solverStatus.getStatus() && getSimulationMessage().equals(solverStatus.getSimulationMessage()));
	}
	/**
	 * Gets the message property (java.lang.String) value.
	 * @return The message property value.
	 */
	public SimulationMessage getSimulationMessage() {
		return fieldSimulationMessage;
	}
	/**
	 * Gets the status property (int) value.
	 * @return The status property value.
	 */
	public int getStatus() {
		return fieldStatus;
	}
/**
 * Insert the method's description here.
 * Creation date: (11/13/2001 11:03:37 AM)
 * @return boolean
 */
public boolean isRunning() {
	return (fieldStatus == SOLVER_STARTING || fieldStatus == SOLVER_RUNNING);
}
	/**
	 * Gets the message property (java.lang.String) value.
	 * @return The string representation of the status.
	 */
	public java.lang.String toString() {
		return SOLVER_STATUS[getStatus()] + ": " + fieldSimulationMessage;
	}
}
