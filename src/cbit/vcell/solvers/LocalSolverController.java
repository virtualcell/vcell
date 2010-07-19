package cbit.vcell.solvers;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import javax.swing.event.EventListenerList;

import org.vcell.util.DataAccessException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;

import cbit.rmi.event.WorkerEvent;
import cbit.rmi.event.WorkerEventListener;
import cbit.rmi.event.WorkerEventSender;
import cbit.vcell.modeldb.ResultSetCrawler;
import cbit.vcell.server.LocalVCellConnection;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationMessage;
import cbit.vcell.solver.SolverEvent;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverListener;
import cbit.vcell.solver.SolverStatus;
import cbit.vcell.solver.VCSimulationDataIdentifier;
/**
 * This type was created in VisualAge.
 */
public class LocalSolverController extends java.rmi.server.UnicastRemoteObject implements SolverController, SolverListener, WorkerEventSender {
	private SolverControllerImpl solverControllerImpl = null;
	private EventListenerList listenerList = new EventListenerList();
	private SessionLog log = null;
	private LocalVCellConnection vcConn = null;
	private SimulationData.DataMoverThread dataMover = null;

	private HashSet<VCSimulationDataIdentifier> resultSetSavedSet = new HashSet<VCSimulationDataIdentifier>();
	private long timeOfLastProgressMessage = 0;
	private long timeOfLastDataMessage = 0;
	// how often do we send optional events (data, progress) to be dispatched to the client
	private int messagingInterval = 5; // seconds
	private int serialParameterScanJobIndex = -1;

/**
 * LocalMathController constructor comment.
 * @exception java.rmi.RemoteException The exception description.
 */
public LocalSolverController(LocalVCellConnection vcellConnection, SessionLog sessionLog, SimulationJob simJob, File localDirectory, File serverDirectory) throws java.rmi.RemoteException, SolverException {
	super(PropertyLoader.getIntProperty(PropertyLoader.rmiPortSolverController,0));
	this.log = sessionLog;
	this.vcConn = vcellConnection;
	solverControllerImpl = new SolverControllerImpl(vcellConnection, sessionLog, simJob, localDirectory);
	solverControllerImpl.getSolver().addSolverListener(this);
	if (!localDirectory.equals(serverDirectory)) {
		SimulationData simData = null;
		try {
			simData = new SimulationData(simJob.getVCDataIdentifier(), localDirectory, null);
		} catch (IOException ex) {
			log.exception(ex);
			throw new SolverException(ex.getMessage());
		} catch (DataAccessException ex) {
			log.exception(ex);
			throw new SolverException(ex.getMessage());
		}
		dataMover = simData.getDataMover();
		dataMover.setServerDirectory(serverDirectory);
		dataMover.setLocalSolverController(this);
	}
}


/**
 * addWorkerEventListener method comment.
 */
public void addWorkerEventListener(WorkerEventListener listener) {
	listenerList.add(WorkerEventListener.class, listener);
}


public void dataMoved(double timepoint, Double progress, SimulationMessage simulationMessage) {
	// called by data mover thread after successful move operations
	try {
		VCSimulationDataIdentifier vcSimDataID = getSimulationJob().getVCDataIdentifier();
		if (!resultSetSavedSet.contains(vcSimDataID)){
			try {
				ResultSetCrawler rsCrawler = vcConn.getResultSetCrawler();
				rsCrawler.updateSimResults(vcConn.getUserLoginInfo().getUser(),vcSimDataID);
				resultSetSavedSet.add(vcSimDataID);
			} catch (Throwable exc) {
				log.exception(exc);
			}
		}
		// don't log progress and data events; data events at larger interval, since more expensive on client side
		if (System.currentTimeMillis() - timeOfLastDataMessage > 4000 * getMessagingInterval()) {
			fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_DATA, this, getSimulationJob(), vcConn.getHost(), progress, new Double(timepoint), simulationMessage));
		}
	}catch (Throwable e){
		log.exception(e);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 2:44:30 PM)
 * @param event cbit.rmi.event.JobStartingEvent
 */
protected void fireWorkerEvent(WorkerEvent event) {
// ** only for data and progress **	setTimeOfLastMessage(System.currentTimeMillis());
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==WorkerEventListener.class) {
		((WorkerEventListener)listeners[i+1]).onWorkerEvent(event);
	    }	       
	}
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getHost() {
	return vcConn.getHost();
}


/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 5:03:31 PM)
 * @return int
 */
public int getMessagingInterval() {
	return messagingInterval;
}


/**
 * Insert the method's description here.
 * Creation date: (6/28/01 2:34:17 PM)
 * @return double
 */
public double getProgress() throws DataAccessException {
	try {
		return solverControllerImpl.getSolver().getProgress();
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (10/10/2002 10:38:55 AM)
 * @return cbit.vcell.server.SessionLog
 */
public SessionLog getSessionLog() {
	return log;
}


/**
 * getMathDescriptionVCML method comment.
 */
public SimulationJob getSimulationJob() throws DataAccessException {
	try {
		return solverControllerImpl.getSimulationJob();
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}	
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
public SolverStatus getSolverStatus() throws DataAccessException {
	try {
		return solverControllerImpl.getSolver().getSolverStatus();
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}	
}

/**
 * removeWorkerEventListener method comment.
 */
public void removeWorkerEventListener(WorkerEventListener listener) {
	listenerList.remove(WorkerEventListener.class, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 5:03:31 PM)
 * @param newMessagingInterval int
 */
public void setMessagingInterval(int newMessagingInterval) {
	messagingInterval = newMessagingInterval;
}

/**
 * Invoked when the solver aborts a calculation (abnormal termination).
 * @param event indicates the solver and the event type
 */
public void solverAborted(SolverEvent event) {
	try {
		log.print("LocalMathController Caught solverAborted("+event.getSource().toString()+",error='"+event.getSimulationMessage()+"')");
		if (dataMover != null) {
			dataMover.stopRunning();
		}
		SimulationJob simJob = getSimulationJob();
		if (serialParameterScanJobIndex >= 0) {
			SimulationJob newSimJob = new SimulationJob(simJob.getSimulation(), serialParameterScanJobIndex, simJob.getFieldDataIdentifierSpecs());
			fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_FAILURE, this, newSimJob, vcConn.getHost(), event.getSimulationMessage()));
		} else {
			fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_FAILURE, this, getSimulationJob(), vcConn.getHost(), event.getSimulationMessage()));
		}
	}catch (Throwable e){
		log.exception(e);
	}
}


/**
 * Invoked when the solver finishes a calculation (normal termination).
 * @param event indicates the solver and the event type
 */
public void solverFinished(SolverEvent event) {
	try {
		log.print("LocalMathController Caught solverFinished("+event.getSource().toString()+")");
		if (dataMover != null) {
			dataMover.stopRunning();
		}
		SimulationJob simJob = getSimulationJob();
		if (serialParameterScanJobIndex >= 0) {
			SimulationJob newSimJob = new SimulationJob(simJob.getSimulation(), serialParameterScanJobIndex, simJob.getFieldDataIdentifierSpecs());
			fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_COMPLETED, this, newSimJob, vcConn.getHost(), new Double(event.getProgress()), new Double(event.getTimePoint()), event.getSimulationMessage()));
		} else {
			fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_COMPLETED, this, simJob, vcConn.getHost(), new Double(event.getProgress()), new Double(event.getTimePoint()), event.getSimulationMessage()));	
		}		
	}catch (Throwable e){
		log.exception(e);
	}
}


/**
 * Invoked when the solver stores values in the result set.
 * @param event indicates the solver and the event type
 */
public void solverPrinted(SolverEvent event) {
	// only if local storage, otherwise defer to DataMover
	if (dataMover == null) {
		dataMoved(event.getTimePoint(), new Double(event.getProgress()), event.getSimulationMessage());
	}
}


/**
 * Invoked when the solver stores values in the result set.
 * @param event indicates the solver and the event type
 */
public void solverProgress(SolverEvent event) {
	try {
		// don't log progress and data events
		if (System.currentTimeMillis() - timeOfLastProgressMessage > 1000 * getMessagingInterval()) {
			SimulationJob simJob = getSimulationJob();
			if (serialParameterScanJobIndex >= 0) {
				SimulationJob newSimJob = new SimulationJob(simJob.getSimulation(), serialParameterScanJobIndex, simJob.getFieldDataIdentifierSpecs());
				fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_PROGRESS, this, newSimJob, vcConn.getHost(), new Double(event.getProgress()), new Double(event.getTimePoint()), event.getSimulationMessage()));
				if (event.getProgress() >= 1) {
					fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_COMPLETED, this, newSimJob, vcConn.getHost(), new Double(event.getProgress()), new Double(event.getTimePoint()), SimulationMessage.MESSAGE_JOB_COMPLETED));
					serialParameterScanJobIndex ++;
				}
			} else {
				fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_PROGRESS, this, getSimulationJob(), vcConn.getHost(), new Double(event.getProgress()), new Double(event.getTimePoint()), event.getSimulationMessage()));
			}
		}
	}catch (Throwable e){
		log.exception(e);
	}
}


/**
 * Invoked when the solver begins a calculation.
 * @param event indicates the solver and the event type
 */
public void solverStarting(SolverEvent event) {
	try {
		log.print("LocalMathController Caught solverStarting("+event.getSource().toString()+")");
		SimulationJob simJob = getSimulationJob();
		if (serialParameterScanJobIndex >= 0) {
			SimulationJob newSimJob = new SimulationJob(simJob.getSimulation(), serialParameterScanJobIndex, simJob.getFieldDataIdentifierSpecs());
			fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_STARTING, this, newSimJob, vcConn.getHost(), event.getSimulationMessage()));
		} else {
			fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_STARTING, this, simJob, vcConn.getHost(), event.getSimulationMessage()));
		}
	}catch (Throwable e){
		log.exception(e);
	}
}


/**
 * Invoked when the solver stops a calculation, usually because
 * of a user-initiated stop call.
 * @param event indicates the solver and the event type
 */
public void solverStopped(SolverEvent event) {
	try {
		log.print("LocalMathController Caught solverStopped("+event.getSource().toString()+")");
		if (dataMover != null) {
			dataMover.stopRunning();
		}
	}catch (Throwable e){
		log.exception(e);
	}
}


/**
 * startSimulation method comment.
 */
public void startSimulationJob() throws SimExecutionException, DataAccessException {
	try {
		resultSetSavedSet.remove(getSimulationJob().getVCDataIdentifier());
		if (dataMover != null) {
			// we have remote master simData directory, start the mover thread
			dataMover.reset();
			Thread dm = new Thread(dataMover);
			dm.setDaemon(true);
			dm.setName("DataMover("+dataMover.getSimulationDataIdentifier()+")");
			dm.start();
		}
		if (getSimulationJob().getSimulation().isSerialParameterScan()) {
			serialParameterScanJobIndex = 0;
		}
		solverControllerImpl.startSimulationJob();
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}	
}

/**
 * stopSimulation method comment.
 */
public void stopSimulationJob() throws DataAccessException {
	try {
		solverControllerImpl.stopSimulationJob();
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}
}