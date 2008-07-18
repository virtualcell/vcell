package cbit.vcell.solvers;
import cbit.vcell.simdata.*;
import java.io.*;
import cbit.vcell.server.*;
import cbit.vcell.solver.*;
import java.util.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.server.LocalVCellConnection;
import cbit.rmi.event.*;
import javax.swing.event.*;
/**
 * This type was created in VisualAge.
 */
public class LocalSolverController extends java.rmi.server.UnicastRemoteObject implements SolverController, cbit.vcell.solver.SolverListener, WorkerEventSender {
	private SolverControllerImpl solverControllerImpl = null;
	private EventListenerList listenerList = new EventListenerList();
	private cbit.vcell.server.SessionLog log = null;
	private LocalVCellConnection vcConn = null;
	private SimulationData.DataMoverThread dataMover = null;

	private HashSet<VCSimulationDataIdentifier> resultSetSavedSet = new HashSet<VCSimulationDataIdentifier>();
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
public void addWorkerEventListener(cbit.rmi.event.WorkerEventListener listener) {
	listenerList.add(WorkerEventListener.class, listener);
}


public void dataMoved(double timepoint, Double progress) {
	// called by data mover thread after successful move operations
	try {
		VCSimulationDataIdentifier vcSimDataID = getSimulationJob().getVCDataIdentifier();
		if (!resultSetSavedSet.contains(vcSimDataID)){
			try {
				cbit.vcell.modeldb.ResultSetCrawler rsCrawler = vcConn.getResultSetCrawler();
				rsCrawler.updateSimResults(vcConn.getUser(),vcSimDataID);
				resultSetSavedSet.add(vcSimDataID);
			} catch (Throwable exc) {
				log.exception(exc);
			}
		}
		// don't log progress and data events; data events at larger interval, since more expensive on client side
		if (System.currentTimeMillis() - timeOfLastDataMessage > 4000 * getMessagingInterval()) {
			fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_DATA, this, getSimulationJob(), vcConn.getHost(), progress, new Double(timepoint)));
			//fireJobDataEvent(new JobDataEvent(this,new MessageSource(this,getSimulationIdentifier()),getSimulation().getSimulationInfo(),timepoint));
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
public double getProgress() throws cbit.vcell.server.DataAccessException {
	try {
		return solverControllerImpl.getSolver().getProgress();
	}catch (Throwable e){
		log.exception(e);
		throw new cbit.vcell.server.DataAccessException(e.getMessage());
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (10/10/2002 10:38:55 AM)
 * @return cbit.vcell.server.SessionLog
 */
public cbit.vcell.server.SessionLog getSessionLog() {
	return log;
}


/**
 * getMathDescriptionVCML method comment.
 */
public cbit.vcell.solver.SimulationJob getSimulationJob() throws cbit.vcell.server.DataAccessException {
	try {
		return solverControllerImpl.getSimulationJob();
	}catch (Throwable e){
		log.exception(e);
		throw new cbit.vcell.server.DataAccessException(e.getMessage());
	}	
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.vcell.solver.SolverStatus getSolverStatus() throws cbit.vcell.server.DataAccessException {
	try {
		return solverControllerImpl.getSolver().getSolverStatus();
	}catch (Throwable e){
		log.exception(e);
		throw new cbit.vcell.server.DataAccessException(e.getMessage());
	}	
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
 * Invoked when the solver aborts a calculation (abnormal termination).
 * @param event indicates the solver and the event type
 */
public void solverAborted(cbit.vcell.solver.SolverEvent event) {
	try {
		log.print("LocalMathController Caught solverAborted("+event.getSource().toString()+",error='"+event.getMessage()+"')");
		if (dataMover != null) {
			dataMover.stopRunning();
		}
		fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_FAILURE, this, getSimulationJob(), vcConn.getHost(), event.getMessage()));
	}catch (Throwable e){
		log.exception(e);
	}
}


/**
 * Invoked when the solver finishes a calculation (normal termination).
 * @param event indicates the solver and the event type
 */
public void solverFinished(cbit.vcell.solver.SolverEvent event) {
	try {
		log.print("LocalMathController Caught solverFinished("+event.getSource().toString()+")");
		if (dataMover != null) {
			dataMover.stopRunning();
		}
		fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_COMPLETED, this, getSimulationJob(), vcConn.getHost(), new Double(event.getProgress()), new Double(event.getTimePoint())));
		//fireJobProgressEvent(new JobProgressEvent(this,new MessageSource(this,getSimulationIdentifier()),sim.getSimulationInfo(),event.getProgress(),event.getTimePoint()));
		//fireJobCompletedEvent(new JobCompletedEvent(this,new MessageSource(this,getSimulationIdentifier()),sim.getSimulationInfo(),false,event.getProgress(),event.getTimePoint()));
	}catch (Throwable e){
		log.exception(e);
	}
}


/**
 * Invoked when the solver stores values in the result set.
 * @param event indicates the solver and the event type
 */
public void solverPrinted(cbit.vcell.solver.SolverEvent event) {
	// only if local storage, otherwise defer to DataMover
	if (dataMover == null) {
		dataMoved(event.getTimePoint(), new Double(event.getProgress()));
	}
}


/**
 * Invoked when the solver stores values in the result set.
 * @param event indicates the solver and the event type
 */
public void solverProgress(cbit.vcell.solver.SolverEvent event) {
	try {
		// don't log progress and data events
		if (System.currentTimeMillis() - timeOfLastProgressMessage > 1000 * getMessagingInterval()) {
			fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_PROGRESS, this, getSimulationJob(), vcConn.getHost(), new Double(event.getProgress()), new Double(event.getTimePoint())));
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
public void solverStarting(cbit.vcell.solver.SolverEvent event) {
	try {
		log.print("LocalMathController Caught solverStarting("+event.getSource().toString()+")");
		fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_STARTING, this, getSimulationJob(), vcConn.getHost(), event.getMessage()));
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
public void solverStopped(cbit.vcell.solver.SolverEvent event) {
	try {
		log.print("LocalMathController Caught solverStopped("+event.getSource().toString()+")");
		if (dataMover != null) {
			dataMover.stopRunning();
		}
		//fireJobCompletedEvent(new JobCompletedEvent(this,new MessageSource(this,getSimulationIdentifier()),getSimulation().getSimulationInfo(),true,event.getProgress(),event.getTimePoint()));
	}catch (Throwable e){
		log.exception(e);
	}
}


/**
 * startSimulation method comment.
 */
public void startSimulationJob() throws SimExecutionException, cbit.vcell.server.DataAccessException {
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
		solverControllerImpl.startSimulationJob();
	}catch (Throwable e){
		log.exception(e);
		throw new cbit.vcell.server.DataAccessException(e.getMessage());
	}	
}


/**
 * stopSimulation method comment.
 */
public void stopSimulationJob() throws cbit.vcell.server.DataAccessException {
	try {
		solverControllerImpl.stopSimulationJob();
	}catch (Throwable e){
		log.exception(e);
		throw new cbit.vcell.server.DataAccessException(e.getMessage());
	}
}
}