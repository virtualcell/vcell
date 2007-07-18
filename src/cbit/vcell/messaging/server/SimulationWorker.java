package cbit.vcell.messaging.server;
import cbit.htc.CondorConstants;
import cbit.htc.CondorUtils;
import cbit.htc.LsfConstants;
import cbit.htc.LsfUtils;
import cbit.htc.PBSConstants;
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
import cbit.vcell.solvers.LsfSolver;
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
public SimulationWorker(String argName, double maxMemoryMB, int workerType0) throws JMSException, DataAccessException, FileNotFoundException, UnknownHostException {
	super(argName, maxMemoryMB, workerType0);
}


/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 5:06:15 PM)
 */
protected void doJob() throws JMSException, SolverException, XmlParseException {	
	if (currentSolver != null){
		throw new RuntimeException("previous task incomplete (currentSolver!=null)");
	}
	
	log.print("Worker doing job [" + currentTask.getSimulationJobIdentifier() + "]");	

	File userdir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirProperty),currentTask.getUserName());
	switch (workerType) {
	case NOHTC_WORKER: {			
		doSolverJob(userdir);
		break;
	}
	
	case LSF_WORKER: {
		doLsfJob(userdir);
		break;
	}

	case CONDOR_WORKER: {
		doCondorJob(userdir);
		break;
	}

	case PBS_WORKER: {
		doPBSJob(userdir);
		break;
	}

	case JAVA_WORKER: 
		doSolverJob(userdir);
		break;			

	case LSFJAVA_WORKER: {
		if (currentTask.goodForHTC()) {
			doLsfJob(userdir);
		} else {
			doSolverJob(userdir);
		}
		break;
	}
	case CONDORJAVA_WORKER: {
		if (currentTask.goodForHTC()) {
			doCondorJob(userdir);
		} else {
			doSolverJob(userdir);
		}
		break;
	}
	case PBSJAVA_WORKER: {
		if (currentTask.goodForHTC()) {
			doPBSJob(userdir);
		} else {
			doSolverJob(userdir);
		}
		break;
	}
	}		
			
}


/**
 * Insert the method's description here.
 * Creation date: (12/9/2003 8:07:04 AM)
 */
private void doLsfJob(File userdir) throws XmlParseException, SolverException, JMSException {
	currentSolver = new LsfSolver(currentTask, userdir,log);
	currentSolver.addSolverListener(this);
	String jobid = ((LsfSolver)currentSolver).submit2Lsf();

	// if lsf has problem with dispatching jobs, jobs that have been submitted
	// but are not running, will be redispatched after 5 minutes. Then we have duplicate
	// jobs or "failed" jobs actually running in LSF.
	// to avoid this, kill the job, ask the user to try again later if the jobs
	// are not in running status 20 seconds after submission.
	if (jobid != null) { 
		long t = System.currentTimeMillis();
		while (true) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException ex) {
			}
			
			int status = LsfUtils.getJobStatus(jobid);
			if (status == LsfConstants.LSF_STATUS_DONE) {
				break;
			} else if (status == LsfConstants.LSF_STATUS_RUN) {
				// check to see if it exits soon after it runs
				try {
					Thread.sleep(5000);
				} catch (InterruptedException ex) {
				}
				status = LsfUtils.getJobStatus(jobid);
				if (status == cbit.htc.LsfConstants.LSF_STATUS_EXITED) {
					workerMessaging.sendFailed("Job [" + jobid + "] exited unexpectedly: " + LsfUtils.getJobExitCode(jobid));					
				}
				break;
			} else if  (status == LsfConstants.LSF_STATUS_EXITED) {
				workerMessaging.sendFailed("Job [" + jobid + "] exited unexpectedly: " + LsfUtils.getJobExitCode(jobid));
				break;
			} else if (System.currentTimeMillis() - t > 20 * MessageConstants.SECOND) {
				String pendingReason = cbit.htc.LsfUtils.getPendingReason(jobid);
				LsfUtils.killJob(jobid); // kill the job if it takes too long to dispatch the job.
				workerMessaging.sendFailed("LSF Job scheduler timed out. Please try again later. (Job [" + jobid + "]: " + pendingReason + ")");
				break;
			}
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (12/9/2003 8:07:04 AM)
 */
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


/**
 * Insert the method's description here.
 * Creation date: (10/14/2003 10:36:37 AM)
 * @return boolean
 */
public boolean isRunning() {
	if (currentSolver == null || currentTask == null) {
		return false;
	}
	
	if (workerType == LSF_WORKER || workerType == CONDOR_WORKER || workerType == PBS_WORKER ||
		((workerType == LSFJAVA_WORKER || workerType == CONDORJAVA_WORKER || workerType == PBSJAVA_WORKER) && currentTask.goodForHTC())) {		
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
	if (args.length < 2 || !args[0].startsWith("-")) {
		System.out.println("Missing arguments: " + SimulationWorker.class.getName() + " [-lsf|-java|-lsfjava|-nohtc|-pbs|-pbsjava|-condor|-condorjava] serviceName memorySizeMB [logfile]");
		System.exit(1);
	}
 		
	//
	// Create and install a security manager
	//
	try {
		String logfile = null;
		if (args.length >= 4) {
			logfile = args[3];
		}
		mainInit(logfile);

		int workerType = NOHTC_WORKER;		
		if (args[0].equalsIgnoreCase("-lsf")) {
			cbit.htc.LsfUtils.getBINDIR();
			workerType = LSF_WORKER;
		} else	if (args[0].equalsIgnoreCase("-pbs")) {
			PBSUtils.checkServerStatus();
			workerType = PBS_WORKER;			
		} else	if (args[0].equalsIgnoreCase("-condor")) {
			workerType = CONDOR_WORKER;
		} else if (args[0].equalsIgnoreCase("-java")) {
			workerType = JAVA_WORKER;
		} else  if (args[0].equalsIgnoreCase("-lsfjava")) {
			cbit.htc.LsfUtils.getBINDIR();			
			workerType = LSFJAVA_WORKER;
		} else  if (args[0].equalsIgnoreCase("-pbsjava")) {
			PBSUtils.checkServerStatus();
			workerType = PBSJAVA_WORKER;
		} else  if (args[0].equalsIgnoreCase("-condorjava")) {
			workerType = CONDORJAVA_WORKER;
		} else  if (args[0].equalsIgnoreCase("-nohtc")) {
			workerType = NOHTC_WORKER;
		}

		String workerName = args[1];			
		double maxMemoryMB = Double.parseDouble(args[2]);		
		SimulationWorker worker = new SimulationWorker(workerName, maxMemoryMB, workerType);
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
			
			if (currentSolver != null && currentTask != null && simKey.equals(currentTask.getSimKey()) && jobIndex == currentTask.getSimulationJob().getJobIndex()) {
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
	String jobid = ((PBSSolver)currentSolver).submit2PBS();

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
			if (status == PBSConstants.PBS_STATUS_EXITING){
				int exitCode = PBSUtils.getJobExitCode(jobid);
				if (exitCode < 0) {
					workerMessaging.sendFailed("Job [" + jobid + "] exited unexpectedly: [" + exitCode + ":" + PBSConstants.PBS_JOB_EXEC_STATUS[-exitCode] + "]");			
				} else if (exitCode > 0) {
					workerMessaging.sendFailed("Job [" + jobid + "] was killed with system signal " + exitCode);
				}
				break;
			} else if (status == PBSConstants.PBS_STATUS_RUNNING) {
				//check to see if it exits soon after it runs
				try {
					Thread.sleep(5000);
				} catch (InterruptedException ex) {
				}
				status = PBSUtils.getJobStatus(jobid);
				if (status == PBSConstants.PBS_STATUS_EXITING) {
					int exitCode = PBSUtils.getJobExitCode(jobid);
					if (exitCode < 0) {
						workerMessaging.sendFailed("Job [" + jobid + "] exited unexpectedly: [" + exitCode + ":" + PBSConstants.PBS_JOB_EXEC_STATUS[-exitCode] + "]");			
					} else if (exitCode > 0) {
						workerMessaging.sendFailed("Job [" + jobid + "] was killed with system signal " + exitCode);
					}				
				}
				break;
			} else if (System.currentTimeMillis() - t > 2 * MessageConstants.MINUTE) {
				String pendingReason = PBSUtils.getPendingReason(jobid);
				PBSUtils.killJob(jobid); // kill the job if it takes too long to dispatch the job.
				workerMessaging.sendFailed("PBS Job scheduler timed out. Please try again later. (Job [" + jobid + "]: " + pendingReason + ")");
				break;
			}
		}
		System.out.println("It took " + (System.currentTimeMillis() - t) + " ms to verify pbs job status=" + PBSConstants.PBS_JOB_STATUS[status]);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (12/9/2003 8:07:04 AM)
 */
private void doCondorJob(File userdir) throws XmlParseException, SolverException, JMSException {
	currentSolver = new CondorSolver(currentTask, userdir,log);
	currentSolver.addSolverListener(this);
	String jobid = ((CondorSolver)currentSolver).submit2Condor();

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
					workerMessaging.sendFailed("Job [" + jobid + "] exited unexpectedly, check Condor");					
				}
				break;
			} else 	if (status == CondorConstants.CONDOR_STATUS_EXITED) {				
				workerMessaging.sendFailed("Job [" + jobid + "] exited unexpectedly, check Condor");
				break;
			} else if (System.currentTimeMillis() - t > 20 * MessageConstants.SECOND) {
				String pendingReason = CondorUtils.getPendingReason(jobid);
				CondorUtils.killJob(jobid); // kill the job if it takes too long to dispatch the job.
				workerMessaging.sendFailed("Condor Job scheduler timed out. Please try again later. (Job [" + jobid + "]: " + pendingReason + ")");
				break;
			}
		}
	}
}
}