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

import cbit.vcell.solver.SimulationJob;

/**
 * Any kind of solver.  Its implementors implement specific
 * solver types (ODE or PDE solvers, for example), as well as
 * specific algorithms (Runge-Kutta method).
 * Creation date: (8/16/2000 11:10:33 PM)
 * @author: John Wagner
 */
public interface Solver {

	void addSolverListener(SolverListener newListener);

	double getCurrentTime();

	double getProgress();
	SimulationJob getSimulationJob();
	SolverStatus getSolverStatus();
	String translateSimulationMessage(String simulationMessage);
	/**
	 *
	 */
	void removeSolverListener(SolverListener newListener);
	void startSolver();
	void stopSolver();

	void setUnixMode( );
}
