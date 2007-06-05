package cbit.vcell.solvers;

import cbit.vcell.solver.*;

import java.io.*;
import cbit.vcell.messaging.server.SimulationTask;

/**
 * Insert the type's description here.
 * Creation date: (9/26/2003 2:08:08 PM)
 * @author: Fei Gao
 */
public class HTCSolver extends AbstractCompiledSolver {
	protected AbstractCompiledSolver realSolver = null;
	protected File stdoutFile = null;
	protected File stderrFile = null;
	protected java.lang.String cmdArguments = "";
	protected SimulationTask simulationTask = null;

/**
 * LSFSolver constructor comment.
 * @param simulation cbit.vcell.solver.Simulation
 * @param directory java.io.File
 * @param sessionLog cbit.vcell.server.SessionLog
 * @exception cbit.vcell.solver.SolverException The exception description.
 */
public HTCSolver(SimulationTask simTask, java.io.File directory, cbit.vcell.server.SessionLog sessionLog) throws cbit.vcell.solver.SolverException {
	super(simTask.getSimulationJob(), directory, sessionLog);
	simulationTask = simTask;
	realSolver = (AbstractCompiledSolver)cbit.vcell.solver.SolverFactory.createSolver(sessionLog, directory, simTask.getSimulationJob());
	realSolver.addSolverListener(new SolverListener() {
		public final void solverAborted(SolverEvent event) {		
			fireSolverAborted(event.getMessage());
		}


		public final void solverFinished(SolverEvent event) {
			fireSolverFinished();
		}


		public final void solverPrinted(SolverEvent event) {
			fireSolverPrinted(event.getTimePoint());
		}


		public final void solverProgress(SolverEvent event) {
			fireSolverProgress(event.getProgress());
		}


		public final void solverStarting(SolverEvent event) {
			fireSolverStarting(event.getMessage());
		}


		public final void solverStopped(SolverEvent event) {
			fireSolverStopped();
		}
		
	});	
	cmdArguments = String.valueOf(simTask.getTaskID());
}


/**
 * Insert the method's description here.
 * Creation date: (9/26/2003 2:08:08 PM)
 */
public void cleanup() {
	realSolver.cleanup();
}


/**
 * Insert the method's description here.
 * Creation date: (9/26/2003 2:08:08 PM)
 * @return cbit.vcell.solvers.ApplicationMessage
 * @param message java.lang.String
 */
public ApplicationMessage getApplicationMessage(String message) {	
	return realSolver.getApplicationMessage(message);
}


/**
 * Insert the method's description here.
 * Creation date: (10/2/2003 9:03:05 AM)
 */
public String getStderrFileName() {
	if (stderrFile != null) {
		return stderrFile.getAbsolutePath();
	}

	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (10/2/2003 9:02:46 AM)
 * @return java.lang.String
 */
public String getStdoutFileName() {
	if (stdoutFile != null) {
		return stdoutFile.getAbsolutePath();
	}

	return null;
}


/**
 *  This method takes the place of the old runUnsteady()...
 */
protected void initialize() throws cbit.vcell.solver.SolverException {
	realSolver.initialize();
}
}