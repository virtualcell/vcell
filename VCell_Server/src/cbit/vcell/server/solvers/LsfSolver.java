package cbit.vcell.server.solvers;
import cbit.vcell.solver.*;
import cbit.vcell.solvers.AbstractCompiledSolver;
import cbit.vcell.solvers.ApplicationMessage;
import cbit.vcell.solvers.SolverFactory;

import java.io.*;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.gui.PropertyLoader;
import cbit.util.ExecutableException;
import cbit.util.SessionLog;

/**
 * Insert the type's description here.
 * Creation date: (9/26/2003 2:08:08 PM)
 * @author: Fei Gao
 */
public class LsfSolver extends AbstractCompiledSolver {
	private AbstractCompiledSolver realSolver = null;
	private File stdoutFile = null;
	private File stderrFile = null;
	private java.lang.String cmdArguments = "";

/**
 * LSFSolver constructor comment.
 * @param simulation cbit.vcell.solver.Simulation
 * @param directory java.io.File
 * @param sessionLog cbit.vcell.server.SessionLog
 * @exception cbit.vcell.solver.SolverException The exception description.
 */
public LsfSolver(SimulationTask simTask, java.io.File directory, SessionLog sessionLog) throws cbit.vcell.solver.SolverException {
	super(simTask.getSimulationJob(), directory, sessionLog);
	realSolver = (AbstractCompiledSolver)cbit.vcell.solvers.SolverFactory.createSolver(sessionLog, directory, simTask.getSimulationJob());
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
	cmdArguments = " " + simTask.getTaskID();
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
public void initialize() throws cbit.vcell.solver.SolverException {
	realSolver.initialize();
}


/**
 * Insert the method's description here.
 * Creation date: (9/26/2003 2:23:53 PM)
 */
public String submit2Lsf() {
	String jobid = null;
	
	try {
		initialize();		

		fireSolverStarting("submitting to job scheduler...");
		
		String exeSuffix = System.getProperty(PropertyLoader.exesuffixProperty);
		String exeFile = new File(getBaseName()).getPath() + exeSuffix;
		
		jobid = cbit.vcell.lsf.LsfUtils.submitJob(exeFile + cmdArguments);
		if (jobid == null) {
			fireSolverAborted("Failed. (error message: submitting to job scheduler failed).");
		}

	} catch (Throwable throwable) {
		cleanup();
		getSessionLog().exception(throwable);
		setSolverStatus(new SolverStatus (SolverStatus.SOLVER_ABORTED, throwable.getMessage()));
		fireSolverAborted(throwable.getMessage());
	}

	return jobid;
}
}