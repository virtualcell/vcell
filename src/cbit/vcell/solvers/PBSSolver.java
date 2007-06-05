package cbit.vcell.solvers;
import cbit.htc.PBSUtils;
import cbit.vcell.server.PropertyLoader;
import java.io.File;
import cbit.vcell.solver.SolverStatus;

/**
 * Insert the type's description here.
 * Creation date: (4/14/2005 10:47:25 AM)
 * @author: Fei Gao
 */
public class PBSSolver extends HTCSolver {
	private static String PBS_SUBMIT_FILE_EXT = ".pbs.sub";
/**
 * CondorSolver constructor comment.
 * @param simTask cbit.vcell.messaging.server.SimulationTask
 * @param directory java.io.File
 * @param sessionLog cbit.vcell.server.SessionLog
 * @exception cbit.vcell.solver.SolverException The exception description.
 */
public PBSSolver(cbit.vcell.messaging.server.SimulationTask simTask, java.io.File directory, cbit.vcell.server.SessionLog sessionLog) throws cbit.vcell.solver.SolverException {
	super(simTask, directory, sessionLog);
}


/**
 * Insert the method's description here.
 * Creation date: (9/26/2003 2:23:53 PM)
 */
public String submit2PBS() {
	String jobid = null;
	
	try {
		initialize();		

		fireSolverStarting("submitting to job scheduler...");
		
		String exeSuffix = System.getProperty(PropertyLoader.exesuffixProperty);
		String exeFile = new File(getBaseName()).getPath() + exeSuffix;
		String subFile = new File(getBaseName()).getPath() + PBS_SUBMIT_FILE_EXT;
		
		jobid = PBSUtils.submitJob(simulationTask.getComputeResource(), subFile, exeFile, cmdArguments);
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