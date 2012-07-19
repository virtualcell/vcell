/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solvers;
import java.io.File;

import org.vcell.util.ExecutableException;
import org.vcell.util.MessageConstants;
import org.vcell.util.SessionLog;

import cbit.htc.PBSUtils;
import cbit.htc.PbsJobID;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.solver.SimulationMessage;
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
public PBSSolver(SimulationTask simTask, java.io.File directory, SessionLog sessionLog) throws cbit.vcell.solver.SolverException {
	super(simTask, directory, sessionLog);
}

/**
 * Insert the method's description here.
 * Creation date: (9/26/2003 2:23:53 PM)
 * @throws SolverException 
 * @throws ExecutableException 
 */
private PbsJobID submit2PBS() throws SolverException, ExecutableException {
	fireSolverStarting(SimulationMessage.MESSAGE_SOLVEREVENT_STARTING_SUBMITTING);
	String cmd = getExecutableCommand();
	String subFile = new File(getBaseName()).getPath() + PBS_SUBMIT_FILE_EXT;
	String jobname = "S_" + simulationTask.getSimKey() + "_" + simulationTask.getSimulationJob().getJobIndex();
	
	PbsJobID jobid = PBSUtils.submitJob(simulationTask.getComputeResource(), jobname, subFile, cmd, cmdArguments, 1, simulationTask.getEstimatedMemorySizeMB());
	if (jobid == null) {
		fireSolverAborted(SimulationMessage.jobFailed("Failed. (error message: submitting to job scheduler failed)."));
		return null;
	}
	fireSolverStarting(SimulationMessage.solverEvent_Starting_Submit("submitted to job scheduler, job id is " + jobid, jobid));
	
	// babysitPBSSubmission(jobid);
	
	return jobid;
}

/**
 * the code below was called synchronously within submit2PBS();
 * 
 */
@Deprecated
private void babysitPBSSubmission(PbsJobID jobid) throws SolverException{

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
		
		VCMongoMessage.sendPBSWorkerMessage(simulationTask,jobid,"done waiting 1 second, getting pbs status");
		status = PBSUtils.getJobStatus(jobid);
		VCMongoMessage.sendPBSWorkerMessage(simulationTask,jobid,"pbsStatus = "+PBSUtils.getJobStatusDescription(status));
		if (PBSUtils.isJobExiting(status)){
			// pbs command tracejob takes more than 1 minute to get exit status after the job exists. 
			// we don't want to spend so much time on a job, especially when the job is very short. 
			// However, if dispatcher restarted the simulation, which means the first run failed, 
			// we have to find out why.
			if ((simulationTask.getTaskID() & MessageConstants.TASKID_RETRYCOUNTER_MASK) != 0) {
				VCMongoMessage.sendPBSWorkerMessage(simulationTask,jobid,"status indicates exiting and retry>0, waiting 1 minute");
				try {
					Thread.sleep(MessageConstants.MINUTE_IN_MS); // have to sleep at least one minute to get tracejob exist status;
				} catch (InterruptedException ex) {
				}
				VCMongoMessage.sendPBSWorkerMessage(simulationTask,jobid,"getting pbs status");
				if (!PBSUtils.isJobExecOK(jobid)) {
					VCMongoMessage.sendPBSWorkerMessage(simulationTask,jobid,"pbs status indicates exit status");
					throw new SolverException("Job [" + jobid + "] exited unexpectedly: [" + PBSUtils.getJobExecStatus(jobid));			
				}
			}
			VCMongoMessage.sendPBSWorkerMessage(simulationTask,jobid,"pbs status Okay");
			break;
		} else if (PBSUtils.isJobRunning(status)) {
			//check to see if it exits soon after it runs
			VCMongoMessage.sendPBSWorkerMessage(simulationTask,jobid,"Job is running, waiting 1 second before getting pbs status");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
			}
			status = PBSUtils.getJobStatus(jobid);
			VCMongoMessage.sendPBSWorkerMessage(simulationTask,jobid,"pbs status = "+PBSUtils.getJobStatusDescription(status));
			if (PBSUtils.isJobExiting(status)) {
				if ((simulationTask.getTaskID() & MessageConstants.TASKID_RETRYCOUNTER_MASK) != 0) {
					VCMongoMessage.sendPBSWorkerMessage(simulationTask,jobid,"status indicates exiting and retry>0, waiting 1 minute");
					try {
						Thread.sleep(MessageConstants.MINUTE_IN_MS); // have to sleep at least one minute to get tracejob exist status;
					} catch (InterruptedException ex) {
					}
					if (!PBSUtils.isJobExecOK(jobid)) {
						VCMongoMessage.sendPBSWorkerMessage(simulationTask,jobid,"pbs status indicates exit status");
						throw new SolverException("Job [" + jobid + "] exited unexpectedly: " + PBSUtils.getJobExecStatus(jobid));			
					}
				}
			}
			VCMongoMessage.sendPBSWorkerMessage(simulationTask,jobid,"pbs status Okay");
			break;
		} else if (System.currentTimeMillis() - t > 4 * MessageConstants.MINUTE_IN_MS) {
			String pendingReason = PBSUtils.getPendingReason(jobid);
			PBSUtils.killJob(jobid); // kill the job if it takes too long to dispatch the job.
			throw new SolverException("PBS Job scheduler timed out. Please try again later. (Job [" + jobid + "]: " + pendingReason + ")");
		}
	}
	System.out.println("It took " + (System.currentTimeMillis() - t) + " ms to verify pbs job status " + PBSUtils.getJobStatusDescription(status));
	VCMongoMessage.sendPBSWorkerMessage(simulationTask,jobid,"It took " + (System.currentTimeMillis() - t) + " ms to verify pbs job status " + PBSUtils.getJobStatusDescription(status));
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
		VCMongoMessage.sendPBSWorkerMessage(simulationTask,null, "calling PBSSolver.initialize()");
		initialize();
		VCMongoMessage.sendPBSWorkerMessage(simulationTask,null, "calling PBSSolver.submit2PBS()");
		PbsJobID jobID = submit2PBS();
		VCMongoMessage.sendPBSWorkerMessage(simulationTask,jobID, "PBSSolver.submit2PBS() returned");
	} catch (Throwable throwable) {
		VCMongoMessage.sendPBSWorkerMessage(simulationTask,null, "PBSSolver.startSolver() exception: "+throwable.getClass().getName()+" "+throwable.getMessage());
		getSessionLog().exception(throwable);
		setSolverStatus(new SolverStatus (SolverStatus.SOLVER_ABORTED, SimulationMessage.solverAborted(throwable.getMessage())));
		fireSolverAborted(SimulationMessage.solverAborted(throwable.getMessage()));		
	}
}

public void stopSolver() {
}
}
