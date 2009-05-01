package cbit.vcell.solvers;
import cbit.htc.PBSConstants;
import cbit.htc.PBSUtils;
import java.io.File;

import org.vcell.util.ExecutableException;

import cbit.vcell.messaging.MessageConstants;
import cbit.vcell.solver.SolverException;
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
 * @throws SolverException 
 * @throws ExecutableException 
 */
private void submit2PBS() throws SolverException, ExecutableException {
	fireSolverStarting("submitting to job scheduler...");
	String cmd = getExecutableCommand();
	String subFile = new File(getBaseName()).getPath() + PBS_SUBMIT_FILE_EXT;
	String jobname = "S_" + simulationTask.getSimKey() + "_" + simulationTask.getSimulationJob().getJobIndex();
	
	String jobid = PBSUtils.submitJob(simulationTask.getComputeResource(), jobname, subFile, cmd, cmdArguments, 1, simulationTask.getEstimatedMemorySizeMB(), PBSConstants.PBS_ARCH_LINUX);
	if (jobid == null) {
		fireSolverAborted("Failed. (error message: submitting to job scheduler failed).");
		return;
	}
	fireSolverStarting("submitted to job scheduler, job id is " + jobid);

	// if PBS has problem with dispatching jobs, jobs that have been submitted
	// but are not running, will be redispatched after 5 minutes. Then we have duplicate
	// jobs or "failed" jobs actually running in PBS.
	// to avoid this, kill the job, ask the user to try again later if the jobs
	// are not in running status 2 minutes after submission.
	long t = System.currentTimeMillis();
	int status;
	while (true) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
		}
		
		status = PBSUtils.getJobStatus(jobid);
		if (PBSUtils.isJobExiting(status)){
			// pbs command tracejob takes more than 1 minute to get exit status after the job exists. 
			// we don't want to spend so much time on a job, especially when the job is very short. 
			// However, if dispatcher restarted the simulation, which means the first run failed, 
			// we have to find out why.
			if ((simulationTask.getTaskID() & MessageConstants.TASKID_RETRYCOUNTER_MASK) != 0) {
				try {
					Thread.sleep(MessageConstants.MINUTE); // have to sleep at least one minute to get tracejob exist status;
				} catch (InterruptedException ex) {
				}
				if (!PBSUtils.isJobExecOK(jobid)) {
					throw new SolverException("Job [" + jobid + "] exited unexpectedly: [" + PBSUtils.getJobExecStatus(jobid));			
				}
			}
			break;
		} else if (PBSUtils.isJobRunning(status)) {
			//check to see if it exits soon after it runs
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
			}
			status = PBSUtils.getJobStatus(jobid);
			if (PBSUtils.isJobExiting(status)) {
				if ((simulationTask.getTaskID() & MessageConstants.TASKID_RETRYCOUNTER_MASK) != 0) {
					try {
						Thread.sleep(MessageConstants.MINUTE); // have to sleep at least one minute to get tracejob exist status;
					} catch (InterruptedException ex) {
					}
					if (!PBSUtils.isJobExecOK(jobid)) {
						throw new SolverException("Job [" + jobid + "] exited unexpectedly: " + PBSUtils.getJobExecStatus(jobid));			
					}
				}
			}
			break;
		} else if (System.currentTimeMillis() - t > 4 * MessageConstants.MINUTE) {
			String pendingReason = PBSUtils.getPendingReason(jobid);
			PBSUtils.killJob(jobid); // kill the job if it takes too long to dispatch the job.
			throw new SolverException("PBS Job scheduler timed out. Please try again later. (Job [" + jobid + "]: " + pendingReason + ")");
		}
	}
	System.out.println("It took " + (System.currentTimeMillis() - t) + " ms to verify pbs job status" + PBSUtils.getJobStatusDescription(status));
}

@Override
public double getCurrentTime() {
	return 0;
}

@Override
public double getProgress() {
	return 0;
}

public void startSolver() {
	try {
		initialize();
		submit2PBS();
	} catch (Throwable throwable) {
		getSessionLog().exception(throwable);
		setSolverStatus(new SolverStatus (SolverStatus.SOLVER_ABORTED, throwable.getMessage()));
		fireSolverAborted(throwable.getMessage());		
	}
}

public void stopSolver() {
}
}