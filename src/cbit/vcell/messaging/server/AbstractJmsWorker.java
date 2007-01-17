package cbit.vcell.messaging.server;
import javax.jms.*;
import java.util.HashSet;
import cbit.vcell.server.User;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.server.ObjectNotFoundException;
import cbit.vcell.solver.SolverListener;
import cbit.vcell.modeldb.ResultSetCrawler;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.Date;
import cbit.vcell.solver.SolverException;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.solver.SolverEvent;
import cbit.vcell.messaging.WorkerMessaging;
import cbit.vcell.messaging.MessageConstants;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:30:31 PM)
 * @author: Jim Schaff
 */
public abstract class AbstractJmsWorker extends AbstractJmsServiceProvider implements Worker {
	protected static final int LSF_WORKER = 0;
	protected static final int JAVA_WORKER = 1;
	protected static final int LSFJAVA_WORKER = 2;
	protected static final int NOLSF_WORKER = 3;

	protected double maxMemoryMB = 100;
	protected SimulationTask currentTask = null;
	protected cbit.vcell.solver.Solver currentSolver = null;

	protected boolean bStopped = true;
	protected cbit.vcell.server.SessionLog log = null;

	protected WorkerMessaging workerMessaging = null;

	protected int workerType = LSFJAVA_WORKER;

	
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 4:33:08 PM)
 * @param nodeName java.lang.String
 * @param parentNode cbit.vcell.appserver.ComputationalNode
 */
public AbstractJmsWorker(String workerName, double maxMemoryMB0, int workerType0) throws JMSException, FileNotFoundException, UnknownHostException {
	this.maxMemoryMB = maxMemoryMB0;
	workerType = workerType0;
	log = new cbit.vcell.server.StdoutSessionLog(workerName);	
	
	String hostName = cbit.vcell.messaging.admin.ManageUtils.getLocalHostName();	
	serviceInfo = new cbit.vcell.messaging.admin.VCServiceInfo(hostName, MessageConstants.SERVICETYPE_COMPUTE_VALUE, workerName);
	serviceInfo.setAlive(true);
	serviceInfo.setBootTime(new Date());	

	workerMessaging = new WorkerMessaging(this, log);
}


protected abstract void doJob() throws JMSException, SolverException, XmlParseException;


/**
 * Insert the method's description here.
 * Creation date: (12/9/2003 8:28:40 AM)
 * @return java.lang.String
 */
public final String getJobSelector() {
	switch (workerType) {
		case LSF_WORKER: {
			return "(" + MessageConstants.MESSAGE_TYPE_PROPERTY + "='" + MessageConstants.MESSAGE_TYPE_SIMULATION_JOB_VALUE + "') AND (" + MessageConstants.SOLVER_TYPE_PROPERTY 
				+ "='" + MessageConstants.SOLVER_TYPE_LSF_PROPERTY + "')";
		}

		case JAVA_WORKER: {
			return "(" + MessageConstants.MESSAGE_TYPE_PROPERTY + "='" + MessageConstants.MESSAGE_TYPE_SIMULATION_JOB_VALUE + "') AND (" + MessageConstants.SOLVER_TYPE_PROPERTY 
				+ "='" + MessageConstants.SOLVER_TYPE_JAVA_PROPERTY + "') AND (" + MessageConstants.SIZE_MB_PROPERTY + "<" + maxMemoryMB + ") ";
		}

		case NOLSF_WORKER: {
			return "(" + MessageConstants.MESSAGE_TYPE_PROPERTY + "='" + MessageConstants.MESSAGE_TYPE_SIMULATION_JOB_VALUE + "') AND (" + MessageConstants.SIZE_MB_PROPERTY + "<" + maxMemoryMB + ") ";
		}
		
		default: {
			return MessageConstants.MESSAGE_TYPE_PROPERTY + "='" + MessageConstants.MESSAGE_TYPE_SIMULATION_JOB_VALUE + "'";
		}
	}	
}


/**
 * Invoked when the solver aborts a calculation (abnormal termination).
 * @param event indicates the solver and the event type
 */
public final void solverAborted(SolverEvent event) {		
	String failMsg = event.getMessage();
	if (failMsg == null) {
		failMsg = "Solver aborted";
	}
		
	workerMessaging.sendFailed(failMsg);
}


/**
 * Invoked when the solver finishes a calculation (normal termination).
 * @param event indicates the solver and the event type
 */
public final void solverFinished(SolverEvent event) {
	workerMessaging.sendCompleted(event.getProgress(), event.getTimePoint());
}


/**
 * Invoked when the solver stores values in the result set.
 * @param event indicates the solver and the event type
 */
public final void solverPrinted(SolverEvent event) {
	if (!isRunning()) {
		return;
	}

	workerMessaging.sendNewData(event.getProgress(), event.getTimePoint());
}


/**
 * Invoked when the solver stores values in the result set.
 * @param event indicates the solver and the event type
 */
public final void solverProgress(SolverEvent event) {
	if (!isRunning()) {
		return;
	}
		
	workerMessaging.sendProgress(event.getProgress(), event.getTimePoint());
}


/**
 * Invoked when the solver begins a calculation.
 * @param event indicates the solver and the event type
 */
public final void solverStarting(SolverEvent event) {
	String startMsg = event.getMessage();
	if (startMsg == null) {
		startMsg = "Solver starting";
	}
	
	workerMessaging.sendStarting(startMsg);
}


/**
 * Invoked when the solver stops a calculation, usually because
 * of a user-initiated stop call.
 * @param event indicates the solver and the event type
 */
public final void solverStopped(SolverEvent event) {		
	log.print("Caught solverStopped(" + event.getSource() + ")");
	// Don't send message anymore because the dispatcher will update the database anyway no matter if the worker responds
	//workerMessaging.sendStopped(event.getProgress(), event.getTimePoint());
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:21:02 PM)
 */
public final void start() {		
	bStopped = false;
	
	log.print("Start PropertyLoader thread...");
	new PropertyLoaderThread().start();
	
	while (!bStopped){
		currentTask = null;
		currentSolver = null;
		
		try {
			workerMessaging.startReceiving();
			currentTask = workerMessaging.getNextTask();
			
			if (currentTask == null || !(currentTask instanceof SimulationTask)){
				try {
					Thread.currentThread().sleep(MessageConstants.SECOND);
				} catch (Exception ex) {
				}
				continue;				
			}
			workerMessaging.stopReceiving();
			doJob();
			
		} catch (Exception ex) {
			log.print("Worker: run() -- " + ex.getMessage());
			workerMessaging.sendFailed(ex.getMessage());
		}			
	}

	log.print("Ending");
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:21:10 PM)
 */
public final void stop() {
	bStopped = true;
}
}