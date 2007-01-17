package cbit.vcell.messaging.server;
import cbit.sql.KeyValue;
import cbit.vcell.server.ConfigurationException;
import java.io.File;
import java.util.Date;
import javax.jms.*;
import cbit.vcell.server.PropertyLoader;
import cbit.vcell.server.DataAccessException;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.solver.SolverStatus;
import cbit.vcell.messaging.JmsUtils;
import cbit.vcell.solver.SolverFactory;
import cbit.vcell.messaging.MessageConstants;
import cbit.vcell.messaging.MessagePropertyNotFoundException;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solvers.LsfSolver;
import cbit.vcell.lsf.LsfConstants;
import cbit.vcell.lsf.LsfUtils;

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
	
	//File maindir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.localSimDataDirProperty));
	//if (!maindir.isDirectory()){
		//throw new ConfigurationException(maindir.toString()+" is not a directory");
	//}
	//File userdir = new File(maindir,currentTask.getUserName());
	//if (!userdir.isDirectory()){
		//throw new ConfigurationException(userdir.toString()+" is not a directory");
	//}

	File userdir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.serverSimDataDirProperty),currentTask.getUserName());
	switch (workerType) {
		case LSF_WORKER: {
			doLsfJob(userdir);
			break;
		}

		case JAVA_WORKER: 
			doSolverJob(userdir);
			break;
			
		case NOLSF_WORKER: {			
			doSolverJob(userdir);
			break;
		}

		default: {
			if (currentTask.goodForLSF()) {
				doLsfJob(userdir);
			} else {
				doSolverJob(userdir);
			}
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
	// are not in running status 1 minute after submission.
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
				if (status == cbit.vcell.lsf.LsfConstants.LSF_STATUS_EXITED) {
					workerMessaging.sendFailed("Job [" + jobid + "] exited unexpectedly: " + LsfUtils.getJobExitCode(jobid));					
				}
				break;
			} else if  (status == LsfConstants.LSF_STATUS_EXITED) {
				workerMessaging.sendFailed("Job [" + jobid + "] exited unexpectedly: " + LsfUtils.getJobExitCode(jobid));
				break;
			} else if (System.currentTimeMillis() - t > 20 * MessageConstants.SECOND) {
				String pendingReason = cbit.vcell.lsf.LsfUtils.getPendingReason(jobid);
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
	
	if (workerType == LSF_WORKER || workerType == LSFJAVA_WORKER && currentTask.goodForLSF()) {		
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
		System.out.println("Missing arguments: " + SimulationWorker.class.getName() + " [-lsf|-java|-lsfjava|-nolsf] serviceName memorySizeMB [logfile]");
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

		int workerType = LSFJAVA_WORKER;		
		if (args[0].equalsIgnoreCase("-lsf")) {
			if (cbit.vcell.lsf.LsfUtils.BINDIR == null) {
				System.out.println("LSF not installed");
				System.exit(1);
			}
			workerType = LSF_WORKER;
		} else if (args[0].equalsIgnoreCase("-java")) {
			workerType = JAVA_WORKER;
		} else  if (args[0].equalsIgnoreCase("-lsfjava")) {
			if (cbit.vcell.lsf.LsfUtils.BINDIR == null) {
				System.out.println("LSF not installed");
				System.exit(1);
			}				
			workerType = LSFJAVA_WORKER;
		} else  if (args[0].equalsIgnoreCase("-nolsf")) {
			workerType = NOLSF_WORKER;
		}

		String workerName = args[1];			
		double maxMemoryMB = Double.parseDouble(args[2]);		
		SimulationWorker worker = new SimulationWorker(workerName, maxMemoryMB, workerType);
		worker.start();
	} catch (Throwable e) {
		e.printStackTrace(System.out);
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
}