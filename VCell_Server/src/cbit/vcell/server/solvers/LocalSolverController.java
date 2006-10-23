package cbit.vcell.server.solvers;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import javax.swing.event.EventListenerList;

import cbit.rmi.event.VCSimulationDataIdentifier;
import cbit.rmi.event.WorkerEvent;
import cbit.rmi.event.WorkerEventListener;
import cbit.rmi.event.WorkerEventSender;
import cbit.util.DataAccessException;
import cbit.util.PropertyLoader;
import cbit.util.SessionLog;
import cbit.vcell.server.LocalVCellConnection;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solvers.SimExecutionException;
import cbit.vcell.solvers.SimulationJob;
import cbit.vcell.solvers.SolverController;
import cbit.vcell.solvers.SolverControllerImpl;
import cbit.vcell.solvers.SolverException;
/**
 * This type was created in VisualAge.
 */
public class LocalSolverController extends java.rmi.server.UnicastRemoteObject implements SolverController, cbit.vcell.solvers.SolverListener, WorkerEventSender {
	private SolverControllerImpl solverControllerImpl = null;
	private EventListenerList listenerList = new EventListenerList();
	private SessionLog log = null;
	private LocalVCellConnection vcConn = null;

	private HashSet resultSetSavedSet = new HashSet();
	private long timeOfLastProgressMessage = 0;
	private long timeOfLastDataMessage = 0;
	// how often do we send optional events (data, progress) to be dispatched to the client
	private int messagingInterval = 5; // seconds

/**
 * LocalMathController constructor comment.
 * @exception java.rmi.RemoteException The exception description.
 */
public LocalSolverController(LocalVCellConnection vcellConnection, SessionLog sessionLog, SimulationJob simJob, File localDirectory, File serverDirectory) throws java.rmi.RemoteException, SolverException {
	super(PropertyLoader.getIntProperty(PropertyLoader.rmiPortSolverController,0));
	this.log = sessionLog;
	this.vcConn = vcellConnection;
	solverControllerImpl = new SolverControllerImpl(sessionLog, simJob, localDirectory);
	solverControllerImpl.getSolver().addSolverListener(this);
	if (!localDirectory.equals(serverDirectory)) {
		SimulationData simData = null;
		try {
			simData = new SimulationData(simJob.getVCDataIdentifier(), localDirectory);
		} catch (IOException ex) {
			log.exception(ex);
			throw new SolverException(ex.getMessage());
		} catch (DataAccessException ex) {
			log.exception(ex);
			throw new SolverException(ex.getMessage());
		}
	}
}


/**
 * addWorkerEventListener method comment.
 */
public void addWorkerEventListener(cbit.rmi.event.WorkerEventListener listener) {
	listenerList.add(WorkerEventListener.class, listener);
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
public double getProgress() throws cbit.util.DataAccessException {
	try {
		return solverControllerImpl.getSolver().getProgress();
	}catch (Throwable e){
		log.exception(e);
		throw new cbit.util.DataAccessException(e.getMessage());
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
private cbit.vcell.simulation.Simulation getSimulation() throws cbit.util.DataAccessException {
	return getSimulationJob().getWorkingSim();
}


/**
 * getMathDescriptionVCML method comment.
 */
public cbit.vcell.solvers.SimulationJob getSimulationJob() throws cbit.util.DataAccessException {
	try {
		return solverControllerImpl.getSimulationJob();
	}catch (Throwable e){
		log.exception(e);
		throw new cbit.util.DataAccessException(e.getMessage());
	}	
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.solvers.SolverStatus getSolverStatus() throws cbit.util.DataAccessException {
	try {
		return solverControllerImpl.getSolver().getSolverStatus();
	}catch (Throwable e){
		log.exception(e);
		throw new cbit.util.DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2002 12:49:21 PM)
 * @return long
 */
private long getTimeOfLastDataMessage() {
	return timeOfLastDataMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2002 12:49:49 PM)
 * @return long
 */
private long getTimeOfLastProgressMessage() {
	return timeOfLastProgressMessage;
}


/**
 * removeWorkerEventListener method comment.
 */
public void removeWorkerEventListener(cbit.rmi.event.WorkerEventListener listener) {
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
 * Insert the method's description here.
 * Creation date: (10/17/2002 12:49:21 PM)
 * @param newTimeOfLastDataMessage long
 */
private void setTimeOfLastDataMessage(long newTimeOfLastDataMessage) {
	timeOfLastDataMessage = newTimeOfLastDataMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2002 12:49:49 PM)
 * @param newTimeOfLastProgressMessage long
 */
private void setTimeOfLastProgressMessage(long newTimeOfLastProgressMessage) {
	timeOfLastProgressMessage = newTimeOfLastProgressMessage;
}


/**
 * Invoked when the solver aborts a calculation (abnormal termination).
 * @param event indicates the solver and the event type
 */
public void solverAborted(cbit.vcell.solvers.SolverEvent event) {
	try {
		log.print("LocalMathController Caught solverAborted("+event.getSource().toString()+",error='"+event.getMessage()+"')");
		fireWorkerEvent(new WorkerEvent(this, getSimulationJob().getVCDataIdentifier().getVcSimID(), getSimulationJob().getVCDataIdentifier().getJobIndex(), WorkerEvent.JOB_FAILURE, vcConn.getHost(), 0, event.getMessage()));
	}catch (Throwable e){
		log.exception(e);
	}
}


/**
 * Invoked when the solver finishes a calculation (normal termination).
 * @param event indicates the solver and the event type
 */
public void solverFinished(cbit.vcell.solvers.SolverEvent event) {
	try {
		log.print("LocalMathController Caught solverFinished("+event.getSource().toString()+")");
		fireWorkerEvent(new WorkerEvent(this, getSimulationJob().getVCDataIdentifier().getVcSimID(), getSimulationJob().getVCDataIdentifier().getJobIndex(), WorkerEvent.JOB_COMPLETED, vcConn.getHost(), 0, new Double(event.getProgress()), new Double(event.getTimePoint())));
	}catch (Throwable e){
		log.exception(e);
	}
}


/**
 * Invoked when the solver stores values in the result set.
 * @param event indicates the solver and the event type
 */
public void solverPrinted(cbit.vcell.solvers.SolverEvent event) {
	// only if local storage, otherwise defer to DataMover
}


/**
 * Invoked when the solver stores values in the result set.
 * @param event indicates the solver and the event type
 */
public void solverProgress(cbit.vcell.solvers.SolverEvent event) {
	try {
		// don't log progress and data events
		if (System.currentTimeMillis() - getTimeOfLastProgressMessage() > 1000 * getMessagingInterval()) {
			fireWorkerEvent(new WorkerEvent(this, getSimulationJob().getVCDataIdentifier().getVcSimID(), getSimulationJob().getVCDataIdentifier().getJobIndex(), WorkerEvent.JOB_PROGRESS, vcConn.getHost(), 0, new Double(event.getProgress()), new Double(event.getTimePoint())));
			//fireJobProgressEvent(new JobProgressEvent(this,new MessageSource(this,getSimulationIdentifier()),getSimulation().getSimulationInfo(),event.getProgress(),event.getTimePoint()));
		}
	}catch (Throwable e){
		log.exception(e);
	}
}


/**
 * Invoked when the solver begins a calculation.
 * @param event indicates the solver and the event type
 */
public void solverStarting(cbit.vcell.solvers.SolverEvent event) {
	try {
		log.print("LocalMathController Caught solverStarting("+event.getSource().toString()+")");
		fireWorkerEvent(new WorkerEvent(this, getSimulationJob().getVCDataIdentifier().getVcSimID(), getSimulationJob().getVCDataIdentifier().getJobIndex(), WorkerEvent.JOB_STARTING, vcConn.getHost(), 0, event.getMessage()));
		//fireJobStartingEvent(new JobStartingEvent(this, new MessageSource(this, getSimulationIdentifier()), getSimulation().getSimulationInfo(), event.getMessage()));
	}catch (Throwable e){
		log.exception(e);
	}
}


/**
 * Invoked when the solver stops a calculation, usually because
 * of a user-initiated stop call.
 * @param event indicates the solver and the event type
 */
public void solverStopped(cbit.vcell.solvers.SolverEvent event) {
	try {
		log.print("LocalMathController Caught solverStopped("+event.getSource().toString()+")");
		//fireJobCompletedEvent(new JobCompletedEvent(this,new MessageSource(this,getSimulationIdentifier()),getSimulation().getSimulationInfo(),true,event.getProgress(),event.getTimePoint()));
	}catch (Throwable e){
		log.exception(e);
	}
}


/**
 * startSimulation method comment.
 */
public void startSimulationJob() throws SimExecutionException, cbit.util.DataAccessException {
	try {
		resultSetSavedSet.remove(getSimulationJob().getVCDataIdentifier());
		solverControllerImpl.startSimulationJob();
	}catch (Throwable e){
		log.exception(e);
		throw new cbit.util.DataAccessException(e.getMessage());
	}	
}


/**
 * stopSimulation method comment.
 */
public void stopSimulationJob() throws cbit.util.DataAccessException {
	try {
		solverControllerImpl.stopSimulationJob();
	}catch (Throwable e){
		log.exception(e);
		throw new cbit.util.DataAccessException(e.getMessage());
	}
}
}