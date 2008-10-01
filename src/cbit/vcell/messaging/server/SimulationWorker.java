package cbit.vcell.messaging.server;
import cbit.htc.CondorConstants;
import cbit.htc.CondorUtils;
import cbit.htc.PBSUtils;
import cbit.sql.KeyValue;
import java.io.File;
import javax.jms.*;
import cbit.vcell.server.PropertyLoader;
import cbit.vcell.server.DataAccessException;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.solver.SolverStatus;
import cbit.vcell.messaging.JmsUtils;
import cbit.vcell.messaging.MessageConstants;
import cbit.vcell.messaging.MessagePropertyNotFoundException;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solvers.CondorSolver;
import cbit.vcell.solvers.PBSSolver;
import static cbit.vcell.messaging.MessageConstants.*;
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
public SimulationWorker(WorkerType wt, int wo, int wm, String logdir) throws JMSException, DataAccessException, FileNotFoundException, UnknownHostException {
	super(wt, wo, wm, logdir);
}


/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 5:06:15 PM)
 */
protected void doJob(int workerIndex) throws JMSException, SolverException, XmlParseException {	
	if (currentSolvers[workerIndex] != null){
		throw new RuntimeException("previous task incomplete (currentSolver!=null)");
	}
	
	log.print("Worker doing job [" + currentTasks[workerIndex].getSimulationJobIdentifier() + "]");	

	File userdir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirProperty),currentTasks[workerIndex].getUserName());
	switch (subworkerTypes[workerIndex]) {
		case PDE_WORKER:
		case ODE_WORKER: {
			doSolverJob(workerIndex, userdir);
			break;
		}
	//	case LSF_WORKER: {
	//		doLsfJob(userdir);
	//		break;
	//	}
	//
	//	case CONDOR_WORKER: {
	//		doCondorJob(userdir);
	//		break;
	//	}
		case PBS_WORKER: {
			doPBSJob(workerIndex, userdir);
			break;
		}
	//	case LSFODE_WORKER: {
	//		if (currentTasks[workerIndex].goodForHTC()) {
	//			doLsfJob(workerIndex, userdir);
	//		} else {
	//			doSolverJob(workerIndex, userdir);
	//		}
	//		break;
	//	}
	//	case CONDORODE_WORKER: {
	//		if (currentTasks[workerIndex].goodForHTC()) {
	//			doCondorJob(workerIndex, userdir);
	//		} else {
	//			doSolverJob(workerIndex, userdir);
	//		}
	//		break;
	//	}
		case PBSODE_WORKER:
		case LOCAL_WORKER: {			
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

private void doSolverJob(int workerIndex, File userdir) throws SolverException {
	currentSolvers[workerIndex] = cbit.vcell.solver.SolverFactory.createSolver(log,userdir,currentTasks[workerIndex].getSimulationJob());
	currentSolvers[workerIndex].addSolverListener(this);
	currentSolvers[workerIndex].startSolver();
	
	while (true){
		try { 
			Thread.sleep(5000); 
		} catch (InterruptedException e) {
		}

		if (!isRunning(workerIndex)) {
			log.print(currentTasks[workerIndex] + " is no longer running.");
			break;
		}
	}
}

public boolean isRunning(int workerIndex) {
	if (currentSolvers[workerIndex] == null || currentTasks[workerIndex] == null) {
		return false;
	}
	
//	if (subworkerTypes[workerIndex].equals(WorkerType.LSF_WORKER) || subworkerTypes[workerIndex].equals(WorkerType.CONDOR_WORKER) || subworkerTypes[workerIndex].equals(WorkerType.PBS_WORKER) ||
//		((subworkerTypes[workerIndex].equals(WorkerType.LSFODE_WORKER) || subworkerTypes[workerIndex].equals(WorkerType.CONDORODE_WORKER) || subworkerTypes[workerIndex].equals(WorkerType.PBSODE_WORKER)) && currentTask.goodForHTC())) {		
//		return true;
//	}	
	
	if (subworkerTypes[workerIndex].equals(WorkerType.PBS_WORKER)) {
		return true;
	}
	
	cbit.vcell.solver.SolverStatus solverStatus = currentSolvers[workerIndex].getSolverStatus();
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
	if (args.length < 2 || !args[0].startsWith("-")) {
		//System.out.println("Missing arguments: " + SimulationWorker.class.getName() + " [-ode|-pde|-lsf|-lsfode|-local|-pbs|-pbsode|-condor|-condorode] serviceOrdinal memorySizeMB [logdir]");
		System.out.println("Missing arguments: " + SimulationWorker.class.getName() + " {-ode|-pde|-local|-pbs|-pbsode} serviceOrdinal memorySizeMB [logdir]");
		System.exit(1);
	}
 		
	//
	// Create and install a security manager
	//
	try {
		PropertyLoader.loadProperties();
		
		WorkerType workerType = WorkerType.LOCAL_WORKER;		
		if (args[0].equalsIgnoreCase("-pbs")) { // one session only, pbs pde
			PBSUtils.checkServerStatus();
			workerType = WorkerType.PBS_WORKER;
//		} else if (args[0].equalsIgnoreCase("-lsf")) { // one session only, lsf pde
//			cbit.htc.LsfUtils.getBINDIR();
//			workerType = WorkerType.LSF_WORKER;
//		} else	if (args[0].equalsIgnoreCase("-condor")) { // one session only, condor pde
//			workerType = WorkerType.CONDOR_WORKER;
		} else if (args[0].equalsIgnoreCase("-ode")) { // one session only, ode
			workerType = WorkerType.ODE_WORKER;
		} else if (args[0].equalsIgnoreCase("-pde")) { // one session only, local pde
			workerType = WorkerType.PDE_WORKER;
		} else  if (args[0].equalsIgnoreCase("-pbsode")) { // two sessions, one pbs, one ode
			PBSUtils.checkServerStatus();
			workerType = WorkerType.PBSODE_WORKER;
//		} else  if (args[0].equalsIgnoreCase("-lsfode")) { // two sessions, one lsf, one ode
//			cbit.htc.LsfUtils.getBINDIR();			
//			workerType = WorkerType.LSFODE_WORKER;
//		} else  if (args[0].equalsIgnoreCase("-condorode")) { // two sessions, one condor, one ode
//			workerType = WorkerType.CONDORODE_WORKER;
		} else  if (args[0].equalsIgnoreCase("-local")) { // two sessions, but one local pde, one ode
			workerType = WorkerType.LOCAL_WORKER;
		} else {
			throw new IllegalArgumentException("wrong argument");
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
			
			for (int i = 0; i < numSubWorkers; i ++) {
				if (currentSolvers[i] != null && currentTasks[i] != null && simKey.equals(currentTasks[i].getSimKey()) 
						&& jobIndex == currentTasks[i].getSimulationJob().getJobIndex()) {
					currentSolvers[i].stopSolver();
				}
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
private void doPBSJob(int workerIndex, File userdir) throws XmlParseException, SolverException, JMSException {
	currentSolvers[workerIndex] = new PBSSolver(currentTasks[workerIndex], userdir,log);
	currentSolvers[workerIndex].addSolverListener(this);
	String jobid = ((PBSSolver)currentSolvers[workerIndex]).submit2PBS();

	// if PBS has problem with dispatching jobs, jobs that have been submitted
	// but are not running, will be redispatched after 5 minutes. Then we have duplicate
	// jobs or "failed" jobs actually running in PBS.
	// to avoid this, kill the job, ask the user to try again later if the jobs
	// are not in running status 2 minutes after submission.
	if (jobid != null) { 
		long t = System.currentTimeMillis();
		int status;
		while (true) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException ex) {
			}
			
			status = PBSUtils.getJobStatus(jobid);
			if (PBSUtils.isJobExisting(status)){
				if (!PBSUtils.isJobExecOK(jobid)) {
					workerMessaging.sendFailed(workerIndex, "Job [" + jobid + "] exited unexpectedly: [" + PBSUtils.getJobExecStatus(jobid));			
				}
				break;
			} else if (PBSUtils.isJobRunning(status)) {
				//check to see if it exits soon after it runs
				try {
					Thread.sleep(5000);
				} catch (InterruptedException ex) {
				}
				status = PBSUtils.getJobStatus(jobid);
				if (PBSUtils.isJobExisting(status)) {
					if (!PBSUtils.isJobExecOK(jobid)) {
						workerMessaging.sendFailed(workerIndex, "Job [" + jobid + "] exited unexpectedly: " + PBSUtils.getJobExecStatus(jobid));			
					}				
				}
				break;
			} else if (System.currentTimeMillis() - t > 2 * MessageConstants.MINUTE) {
				String pendingReason = PBSUtils.getPendingReason(jobid);
				PBSUtils.killJob(jobid); // kill the job if it takes too long to dispatch the job.
				workerMessaging.sendFailed(workerIndex, "PBS Job scheduler timed out. Please try again later. (Job [" + jobid + "]: " + pendingReason + ")");
				break;
			}
		}
		System.out.println("It took " + (System.currentTimeMillis() - t) + " ms to verify pbs job status" + PBSUtils.getJobStatusDescription(status));
	}
}


/**
 * Insert the method's description here.
 * Creation date: (12/9/2003 8:07:04 AM)
 */
private void doCondorJob(int workerIndex, File userdir) throws XmlParseException, SolverException, JMSException {
	currentSolvers[workerIndex] = new CondorSolver(currentTasks[workerIndex], userdir,log);
	currentSolvers[workerIndex].addSolverListener(this);
	String jobid = ((CondorSolver)currentSolvers[workerIndex]).submit2Condor();

	// if condor has problem with dispatching jobs, jobs that have been submitted
	// but are not running, will be redispatched after 5 minutes. Then we have duplicate
	// jobs or "failed" jobs actually running in Condor.
	// to avoid this, kill the job, ask the user to try again later if the jobs
	// are not in running status 20 seconds after submission.
	if (jobid != null) { 
		long t = System.currentTimeMillis();
		while (true) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException ex) {
			}
			
			int status = CondorUtils.getJobStatus(jobid);
			if (status == CondorConstants.CONDOR_STATUS_COMPLETED){
				break;
			} else if (status == CondorConstants.CONDOR_STATUS_RUNNING) {
				// check to see if it exits soon after it runs
				try {
					Thread.sleep(5000);
				} catch (InterruptedException ex) {
				}
				status = CondorUtils.getJobStatus(jobid);
				if (status == CondorConstants.CONDOR_STATUS_EXITED) {
					workerMessaging.sendFailed(workerIndex, "Job [" + jobid + "] exited unexpectedly, check Condor");					
				}
				break;
			} else 	if (status == CondorConstants.CONDOR_STATUS_EXITED) {				
				workerMessaging.sendFailed(workerIndex, "Job [" + jobid + "] exited unexpectedly, check Condor");
				break;
			} else if (System.currentTimeMillis() - t > 20 * MessageConstants.SECOND) {
				String pendingReason = CondorUtils.getPendingReason(jobid);
				CondorUtils.killJob(jobid); // kill the job if it takes too long to dispatch the job.
				workerMessaging.sendFailed(workerIndex, "Condor Job scheduler timed out. Please try again later. (Job [" + jobid + "]: " + pendingReason + ")");
				break;
			}
		}
	}
}

}