package cbit.vcell.solver;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.server.VCellConnection;
import cbit.vcell.server.SessionLog;
/**
 * Any kind of a solver.  Its implementors implement specific
 * solver types (ODE or PDE solvers, for example), as well as
 * specific algorithms (Runge-Kutta method).
 * Creation date: (8/16/2000 11:10:33 PM)
 * @author: John Wagner
 */
public interface Solver {
	/**
	 *
	 */
	public void addSolverListener(SolverListener newListener);
/**
 * Insert the method's description here.
 * Creation date: (6/28/01 2:54:56 PM)
 * @return double
 */
double getCurrentTime();
/**
 * Insert the method's description here.
 * Creation date: (6/28/01 2:54:39 PM)
 * @return double
 */
double getProgress();
	public Simulation getSimulation();
	public SolverStatus getSolverStatus();
	/**
	 *
	 */
	public void removeSolverListener(SolverListener newListener);
	public void startSolver();
	public void stopSolver();
}
