package cbit.vcell.solvers;
import cbit.vcell.server.*;
import cbit.vcell.solver.*;
import java.io.*;
import cbit.vcell.messaging.server.SimulationTask;

/**
 * Insert the type's description here.
 * Creation date: (9/26/2003 2:08:08 PM)
 * @author: Fei Gao
 */
public class LsfSolver extends HTCSolver {
/**
 * LSFSolver constructor comment.
 * @param simulation cbit.vcell.solver.Simulation
 * @param directory java.io.File
 * @param sessionLog cbit.vcell.server.SessionLog
 * @exception cbit.vcell.solver.SolverException The exception description.
 */
public LsfSolver(SimulationTask simTask, java.io.File directory, cbit.vcell.server.SessionLog sessionLog) throws cbit.vcell.solver.SolverException {
	super(simTask, directory, sessionLog);
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
		
		String cmd = realSolver.getMathExecutable().getCommand();		
		jobid = cbit.htc.LsfUtils.submitJob(cmd + " " + cmdArguments);
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