package cbit.vcell.messaging.server;
import javax.jms.*;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.StringTokenizer;
import cbit.vcell.server.PropertyLoader;
import cbit.vcell.solver.Solver;
import cbit.vcell.solver.SolverException;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.solver.SolverEvent;
import cbit.vcell.messaging.MessageConstants;
import cbit.vcell.messaging.WorkerMessaging;
import cbit.vcell.messaging.admin.ManageUtils;
import cbit.vcell.messaging.admin.ServiceInstanceStatus;
import cbit.vcell.messaging.db.VCellServerID;
import static cbit.vcell.messaging.MessageConstants.*;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:30:31 PM)
 * @author: Jim Schaff
 */
public abstract class AbstractJmsWorker extends AbstractJmsServiceProvider implements Worker {
	protected int maxMemoryMB = 100;
	protected SimulationTask[] currentTasks = null;
	protected Solver[] currentSolvers = null;
	protected boolean bStopped = true;
	protected WorkerMessaging workerMessaging = null;
	protected WorkerType workerType;
	int numSubWorkers = 1;
	protected WorkerType[] subworkerTypes = null;
	
public AbstractJmsWorker(WorkerType wType, int workerOrdinal, int workerMem, String logdir) throws JMSException, FileNotFoundException {
	workerType = wType;
	maxMemoryMB = workerMem;	
	numSubWorkers = 1;
	
	ServiceType servicetype = ServiceType.LOCALCOMPUTE; 
	switch (workerType) {
	case ODE_WORKER:
		servicetype = ServiceType.ODECOMPUTE;		
		break;
	case PDE_WORKER:
		servicetype = ServiceType.PDECOMPUTE;		
		break;
//	case LSF_WORKER:
//	case CONDOR_WORKER:	
	case PBS_WORKER:	
		servicetype = ServiceType.PBSCOMPUTE;
		break;
//	case LSFODE_WORKER:
//	case CONDORODE_WORKER:
	case PBSODE_WORKER:
		numSubWorkers = 2;
		servicetype = ServiceType.PBSODECOMPUTE;
		break;
	case LOCAL_WORKER:
		numSubWorkers = 2;
		servicetype = ServiceType.LOCALCOMPUTE;
		break;
	}
	
	// determine subworkers
	subworkerTypes = new WorkerType[numSubWorkers];
	switch (workerType) {
	case ODE_WORKER:
	case PDE_WORKER:
//	case LSF_WORKER:
//	case CONDOR_WORKER:	
	case PBS_WORKER:	// only one worker
		subworkerTypes[0] = workerType;
		break;
//	case LSFODE_WORKER:
//	case CONDORODE_WORKER:
	case PBSODE_WORKER:
		subworkerTypes[0] = WorkerType.ODE_WORKER;
		subworkerTypes[1] = WorkerType.PBS_WORKER; // now we are using PBS
		break;
	case LOCAL_WORKER:
		subworkerTypes[0] = WorkerType.ODE_WORKER;
		subworkerTypes[1] = WorkerType.PDE_WORKER; // now we are using PBS
		break;
	}	
	
	serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID().toString(), servicetype, workerOrdinal, ManageUtils.getHostName(), new Date(), true);
	initLog(logdir);
	
	log = new cbit.vcell.server.StdoutSessionLog(serviceInstanceStatus.getID());
	workerMessaging = new WorkerMessaging(this, log);
}

public int getNumSubworkers() {	
	return numSubWorkers;
}
protected abstract void doJob(int workerIndex) throws JMSException, SolverException, XmlParseException;

public final String[] getJobSelectors() {
	String[] jobSelectors = new String[numSubWorkers];
	for (int i = 0; i < numSubWorkers; i ++) {
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
		
		switch (subworkerTypes[i]) {
//			case LSF_WORKER: 
//			case CONDOR_WORKER :
			case PDE_WORKER :
			case PBS_WORKER :{
				jobSelector += " AND (" + MessageConstants.SOLVER_TYPE_PROPERTY + "='" + MessageConstants.SOLVER_TYPE_PDE_PROPERTY + "')";
				break;
			}	
			case ODE_WORKER: {
				jobSelector += " AND (" + MessageConstants.SOLVER_TYPE_PROPERTY	+ "='" + MessageConstants.SOLVER_TYPE_ODE_PROPERTY + "')" ;
				break;
			}
			case LOCAL_WORKER:
			case PBSODE_WORKER:
				throw new RuntimeException("subworker can't be PBSODE or LOCAL");			
		}
		jobSelectors[i] = jobSelector;
	}
	return jobSelectors;
}

private int getWorkerIndexFromSolver(Solver s){
	for (int i = 0; i < numSubWorkers; i ++) {
		if (s == currentSolvers[i]) {
			return i;
		}
	}
	return -1;
}
/**
 * Invoked when the solver aborts a calculation (abnormal termination).
 * @param event indicates the solver and the event type
 */
public final void solverAborted(SolverEvent event) {
	int workerIndex = getWorkerIndexFromSolver((Solver)event.getSource());
	if (workerIndex == -1) {
		return;
	}
	String failMsg = event.getMessage();
	if (failMsg == null) {
		failMsg = "Solver aborted";
	}
		
	workerMessaging.sendFailed(workerIndex, failMsg);
}


/**
 * Invoked when the solver finishes a calculation (normal termination).
 * @param event indicates the solver and the event type
 */
public final void solverFinished(SolverEvent event) {
	int workerIndex = getWorkerIndexFromSolver((Solver)event.getSource());
	if (workerIndex == -1) {
		return;
	}
	workerMessaging.sendCompleted(workerIndex, event.getProgress(), event.getTimePoint());
}


/**
 * Invoked when the solver stores values in the result set.
 * @param event indicates the solver and the event type
 */
public final void solverPrinted(SolverEvent event) {
	int workerIndex = getWorkerIndexFromSolver((Solver)event.getSource());
	if (workerIndex == -1) {
		return;
	}
	if (!isRunning(workerIndex)) {
		return;
	}

	workerMessaging.sendNewData(workerIndex, event.getProgress(), event.getTimePoint());
}


/**
 * Invoked when the solver stores values in the result set.
 * @param event indicates the solver and the event type
 */
public final void solverProgress(SolverEvent event) {
	int workerIndex = getWorkerIndexFromSolver((Solver)event.getSource());
	if (workerIndex == -1) {
		return;
	}
	if (!isRunning(workerIndex)) {
		return;
	}
		
	workerMessaging.sendProgress(workerIndex, event.getProgress(), event.getTimePoint());
}


/**
 * Invoked when the solver begins a calculation.
 * @param event indicates the solver and the event type
 */
public final void solverStarting(SolverEvent event) {
	int workerIndex = getWorkerIndexFromSolver((Solver)event.getSource());
	if (workerIndex == -1) {
		return;
	}
	String startMsg = event.getMessage();
	if (startMsg == null) {
		startMsg = "Solver starting";
	}
	
	workerMessaging.sendStarting(workerIndex, startMsg);
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
public final void startSubworker(int workerIndex) {		
	log.print("starting subworker " + workerIndex);
	while (!bStopped){
		currentTasks[workerIndex] = null;
		currentSolvers[workerIndex] = null;
		
		try {
			currentTasks[workerIndex] = workerMessaging.getNextTask(workerIndex);
			
			if (currentTasks[workerIndex] == null || !(currentTasks[workerIndex] instanceof SimulationTask)){
				try {
					Thread.sleep(MessageConstants.SECOND);
				} catch (Exception ex) {
				}
				continue;				
			}
			doJob(workerIndex);			
		} catch (Exception ex) {			
			workerMessaging.sendFailed(workerIndex, ex.getMessage());
		}			
	}	
}

public final void start() {
	bStopped = false;
	
	log.print("Start PropertyLoader thread...");
	new PropertyLoaderThread().start();

	currentTasks = new SimulationTask[numSubWorkers];
	currentSolvers = new Solver[numSubWorkers];
	for (int i = 1; i < numSubWorkers; i ++) {
		final int workerIndex = i;
		new Thread() {
			public void run() {
				startSubworker(workerIndex);
			}
		}.start();		
	}
	startSubworker(0);
	
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