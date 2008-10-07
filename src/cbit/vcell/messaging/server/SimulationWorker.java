package cbit.vcell.messaging.server;
import cbit.htc.PBSUtils;
import cbit.sql.KeyValue;
import java.io.File;
import javax.jms.*;
import cbit.vcell.server.PropertyLoader;
import cbit.vcell.server.DataAccessException;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.messaging.JmsUtils;
import cbit.vcell.messaging.MessageConstants;
import cbit.vcell.messaging.MessagePropertyNotFoundException;
import cbit.vcell.messaging.MessageConstants.ServiceType;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverStatus;
import cbit.vcell.solvers.PBSSolver;
/**
 * Insert the type's description here.
 * Creation date: (10/25/2001 4:14:09 PM)
 * @author: Jim Schaff
 */
public class SimulationWorker extends AbstractJmsWorker  {
/**
 * SimulationWorker constructor comment.
 * @param argName java.lang.String
 * @param argParentNode cbit.vcell.appserver.ComputationalNode
 * @param argInitialContext javax.naming.Context
 */
public SimulationWorker(ServiceType wt, int wo, int wm, String logdir) throws JMSException, DataAccessException, FileNotFoundException, UnknownHostException {
	super(wt, wo, wm, logdir);
}

/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 5:06:15 PM)
 */
protected void doJob() throws JMSException, SolverException, XmlParseException {	
	if (currentSolver != null){
		throw new RuntimeException("previous task incomplete (currentSolver!=null)");
	}
	
	log.print("Worker doing job [" + currentTask.getSimulationJob().getSimulationJobID() + "]");	

	File userdir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirProperty),currentTask.getUserName());
	switch (serviceType) {
		case LOCALCOMPUTE: {
			doSolverJob(userdir);
			break;
		}
		case PBSCOMPUTE: {
			doPBSJob(userdir);
			break;
		}
		default: {			
			throw new RuntimeException("subworker can't be PBSODE or LOCAL");
		}
	}
}

//private void doLsfJob(int workerIndex, File userdir) throws XmlParseException, SolverException, JMSException {
//	currentSolvers[workerIndex] = new LsfSolver(currentTasks[workerIndex], userdir,log);
//	currentSolvers[workerIndex].addSolverListener(this);
//	String jobid = ((LsfSolver)currentSolvers[workerIndex]).submit2Lsf();
//
//	// if lsf has problem with dispatching jobs, jobs that have been submitted
//	// but are not running, will be redispatched after 5 minutes. Then we have duplicate
//	// jobs or "failed" jobs actually running in LSF.
//	// to avoid this, kill the job, ask the user to try again later if the jobs
//	// are not in running status 20 seconds after submission.
//	if (jobid != null) { 
//		long t = System.currentTimeMillis();
//		while (true) {
//			try {
//				Thread.sleep(50);
//			} catch (InterruptedException ex) {
//			}
//			
//			int status = LsfUtils.getJobStatus(jobid);
//			if (status == LsfConstants.LSF_STATUS_DONE) {
//				break;
//			} else if (status == LsfConstants.LSF_STATUS_RUN) {
//				// check to see if it exits soon after it runs
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException ex) {
//				}
//				status = LsfUtils.getJobStatus(jobid);
//				if (status == cbit.htc.LsfConstants.LSF_STATUS_EXITED) {
//					workerMessaging.sendFailed(workerIndex, "Job [" + jobid + "] exited unexpectedly: " + LsfUtils.getJobExitCode(jobid));					
//				}
//				break;
//			} else if  (status == LsfConstants.LSF_STATUS_EXITED) {
//				workerMessaging.sendFailed(workerIndex, "Job [" + jobid + "] exited unexpectedly: " + LsfUtils.getJobExitCode(jobid));
//				break;
//			} else if (System.currentTimeMillis() - t > 20 * MessageConstants.SECOND) {
//				String pendingReason = cbit.htc.LsfUtils.getPendingReason(jobid);
//				LsfUtils.killJob(jobid); // kill the job if it takes too long to dispatch the job.
//				workerMessaging.sendFailed(workerIndex, "LSF Job scheduler timed out. Please try again later. (Job [" + jobid + "]: " + pendingReason + ")");
//				break;
//			}
//		}
//	}
//}

private void doSolverJob(File userdir) throws SolverException {
	currentSolver = cbit.vcell.solver.SolverFactory.createSolver(log,userdir,currentTask.getSimulationJob());
	currentSolver.addSolverListener(this);
	currentSolver.startSolver();
	
	while (true){
		try { 
			Thread.sleep(5000); 
		} catch (InterruptedException e) {
		}

		if (!isRunning()) {
			log.print(currentTask + " is no longer running.");
			break;
		}
	}
}

public boolean isRunning() {
	if (currentSolver == null || currentTask == null) {
		return false;
	}	
	
	if (serviceType.equals(ServiceType.PBSCOMPUTE)) {
		return true;
	}
	
	cbit.vcell.solver.SolverStatus solverStatus = currentSolver.getSolverStatus();
	if (solverStatus != null){
		if (solverStatus.getStatus() != SolverStatus.SOLVER_STARTING &&
			solverStatus.getStatus() != SolverStatus.SOLVER_READY &&
			solverStatus.getStatus() != SolverStatus.SOLVER_RUNNING){
			return false;
		}
	}

	return true;
}

/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	if (args.length < 3) {
		System.out.println("Missing arguments: " + SimulationWorker.class.getName() + " {-local|-pbs} serviceOrdinal memorySizeMB [logdir]");
		System.exit(1);
	}
 		
	//
	// Create and install a security manager
	//
	try {
		PropertyLoader.loadProperties();
		
		ServiceType workerType = ServiceType.LOCALCOMPUTE;
		if (args[0].equalsIgnoreCase("-pbs")) { // submit everything to PBS
			PBSUtils.checkServerStatus();
			workerType = ServiceType.PBSCOMPUTE;
		} else if (args[0].equalsIgnoreCase("-local")) { // run everything locally
			workerType = ServiceType.LOCALCOMPUTE;
		} else {
			throw new IllegalArgumentException("wrong worker type argument : " + args[0]);
		}
		
		int serviceOrdinal = Integer.parseInt(args[1]);	
		int maxMemoryMB = Integer.parseInt(args[2]);		
		String logdir = null;
		if (args.length > 3) {
			logdir = args[3];
		}
		SimulationWorker worker = new SimulationWorker(workerType, serviceOrdinal, maxMemoryMB, logdir);
		worker.start();
	} catch (Throwable e) {
		e.printStackTrace(System.out);
		System.exit(-1);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (5/22/2003 2:49:54 PM)
 * @param message javax.jms.Message
 * @exception javax.jms.JMSException The exception description.
 */
public void onControlTopicMessage(Message message) throws JMSException {
	
	log.print("SimulationWorker::onControlTopicMessage(): " + JmsUtils.toString(message));
	try {
		String msgType = (String)JmsUtils.parseProperty(message, MessageConstants.MESSAGE_TYPE_PROPERTY, String.class);

		if (msgType != null && msgType.equals(MessageConstants.MESSAGE_TYPE_STOPSIMULATION_VALUE)) {			
			Long longkey = (Long)JmsUtils.parseProperty(message, MessageConstants.SIMKEY_PROPERTY, long.class);
			KeyValue simKey = new KeyValue(longkey + "");
			int jobIndex = ((Integer)JmsUtils.parseProperty(message, MessageConstants.JOBINDEX_PROPERTY, int.class)).intValue();
			
			if (currentSolver != null && currentTask != null && simKey.equals(currentTask.getSimKey()) 
					&& jobIndex == currentTask.getSimulationJob().getJobIndex()) {
				currentSolver.stopSolver();
			}
		} 	
	} catch (MessagePropertyNotFoundException ex) {
		log.exception(ex);
		return;
	}
}

/**
 * Insert the method's description here.
 * Creation date: (12/9/2003 8:07:04 AM)
 */
private void doPBSJob(File userdir) throws XmlParseException, SolverException, JMSException {
	currentSolver = new PBSSolver(currentTask, userdir,log);
	currentSolver.addSolverListener(this);
	((PBSSolver)currentSolver).startSolver();
}

/**
 * Insert the method's description here.
 * Creation date: (12/9/2003 8:07:04 AM)
 */
//private void doCondorJob(File userdir) throws XmlParseException, SolverException, JMSException {
//	currentSolver = new CondorSolver(currentTask, userdir,log);
//	currentSolver.addSolverListener(this);
//	String jobid = ((CondorSolver)currentSolver).submit2Condor();
//
//	// if condor has problem with dispatching jobs, jobs that have been submitted
//	// but are not running, will be redispatched after 5 minutes. Then we have duplicate
//	// jobs or "failed" jobs actually running in Condor.
//	// to avoid this, kill the job, ask the user to try again later if the jobs
//	// are not in running status 20 seconds after submission.
//	if (jobid != null) { 
//		long t = System.currentTimeMillis();
//		while (true) {
//			try {
//				Thread.sleep(50);
//			} catch (InterruptedException ex) {
//			}
//			
//			int status = CondorUtils.getJobStatus(jobid);
//			if (status == CondorConstants.CONDOR_STATUS_COMPLETED){
//				break;
//			} else if (status == CondorConstants.CONDOR_STATUS_RUNNING) {
//				// check to see if it exits soon after it runs
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException ex) {
//				}
//				status = CondorUtils.getJobStatus(jobid);
//				if (status == CondorConstants.CONDOR_STATUS_EXITED) {
//					workerMessaging.sendFailed("Job [" + jobid + "] exited unexpectedly, check Condor");					
//				}
//				break;
//			} else 	if (status == CondorConstants.CONDOR_STATUS_EXITED) {				
//				workerMessaging.sendFailed("Job [" + jobid + "] exited unexpectedly, check Condor");
//				break;
//			} else if (System.currentTimeMillis() - t > 20 * MessageConstants.SECOND) {
//				String pendingReason = CondorUtils.getPendingReason(jobid);
//				CondorUtils.killJob(jobid); // kill the job if it takes too long to dispatch the job.
//				workerMessaging.sendFailed("Condor Job scheduler timed out. Please try again later. (Job [" + jobid + "]: " + pendingReason + ")");
//				break;
//			}
//		}
//	}
//}

}