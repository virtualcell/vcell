package cbit.vcell.messaging.server;
import javax.jms.*;

import org.vcell.util.MessageConstants;
import org.vcell.util.PropertyLoader;
import org.vcell.util.document.VCellServerID;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.StringTokenizer;
import cbit.vcell.solver.Solver;
import cbit.vcell.solver.SolverException;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.solver.SolverEvent;
import cbit.vcell.messaging.WorkerMessaging;
import cbit.vcell.messaging.admin.ManageUtils;
import cbit.vcell.messaging.admin.ServiceInstanceStatus;
import static org.vcell.util.MessageConstants.*;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:30:31 PM)
 * @author: Jim Schaff
 */
public abstract class AbstractJmsWorker extends AbstractJmsServiceProvider implements Worker {
	protected int maxMemoryMB = 100;
	protected SimulationTask currentTask = null;
	protected Solver currentSolver = null;
	protected boolean bStopped = true;
	protected WorkerMessaging workerMessaging = null;
	protected ServiceType serviceType;
	
public AbstractJmsWorker(ServiceType wt, int workerOrdinal, int workerMem, String logdir) throws JMSException, FileNotFoundException {
	serviceType = wt;
	maxMemoryMB = workerMem;	
			
	serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID().toString(), serviceType, workerOrdinal, ManageUtils.getHostName(), new Date(), true);
	initLog(logdir);
	
	log = new org.vcell.util.StdoutSessionLog(serviceInstanceStatus.getID());
	workerMessaging = new WorkerMessaging(this, log);
}

protected abstract void doJob() throws JMSException, SolverException, XmlParseException;

public final String getJobSelector() {
	String jobSelector = "(" + MessageConstants.MESSAGE_TYPE_PROPERTY + "='" + MessageConstants.MESSAGE_TYPE_SIMULATION_JOB_VALUE + "')";
	String computeResources =  PropertyLoader.getRequiredProperty(PropertyLoader.htcComputeResources);
	StringTokenizer st = new StringTokenizer(computeResources, " ,");	
	jobSelector += " AND ((" + MessageConstants.COMPUTE_RESOURCE_PROPERTY + " IS NULL) OR (" + MessageConstants.COMPUTE_RESOURCE_PROPERTY + " IN (";
	int count = 0;
	while (st.hasMoreTokens()) {
		if (count > 0) {
			jobSelector = ", ";
		}
		jobSelector += "'" + st.nextToken() + "'";
		count ++;
	}
	jobSelector += ")))";
	
	return jobSelector;
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

public final void start() {
	bStopped = false;
	
	log.print("Start PropertyLoader thread...");
	new PropertyLoaderThread().start();

	while (!bStopped){
		currentTask = null;
		currentSolver = null;
		
		try {
			currentTask = workerMessaging.getNextTask();
			
			if (currentTask == null || !(currentTask instanceof SimulationTask)){
				try {
					Thread.sleep(MessageConstants.SECOND);
				} catch (Exception ex) {
				}
				continue;				
			}
			doJob();			
		} catch (Exception ex) {			
			workerMessaging.sendFailed(ex.getMessage());
		}			
	}	
	
	log.print(serviceInstanceStatus.getSpecID() + " stopped");
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:21:10 PM)
 */
public final void stop() {
	bStopped = true;
}
}