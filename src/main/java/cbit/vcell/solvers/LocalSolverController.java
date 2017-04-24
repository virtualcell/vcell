/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solvers;
import java.io.File;
import java.util.HashSet;

import javax.swing.event.EventListenerList;

import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;

import cbit.rmi.event.WorkerEvent;
import cbit.rmi.event.WorkerEventListener;
import cbit.vcell.message.server.bootstrap.LocalVCellConnection;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.SolverEvent;
import cbit.vcell.solver.server.SolverListener;
import cbit.vcell.solver.server.SolverStatus;
/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class LocalSolverController implements SolverListener {
	private SolverControllerImpl solverControllerImpl = null;
	private EventListenerList listenerList = new EventListenerList();
	private SessionLog log = null;
	private LocalVCellConnection vcConn = null;

	private HashSet<VCSimulationDataIdentifier> resultSetSavedSet = new HashSet<VCSimulationDataIdentifier>();
	private long timeOfLastProgressMessage = 0;
	private long timeOfLastDataMessage = 0;
	// how often do we send optional events (data, progress) to be dispatched to the client
	private int messagingInterval = 5; // seconds
	private int serialParameterScanJobIndex = -1;

/**
 * LocalMathController constructor comment.
 * @exception java.rmi.RemoteException The exception description.
 * @throws SolverException 
 */
public LocalSolverController(LocalVCellConnection vcellConnection, SessionLog sessionLog, SimulationTask simTask, File dataDirectory) throws SolverException {
	this.log = sessionLog;
	this.vcConn = vcellConnection;
	solverControllerImpl = new SolverControllerImpl(vcellConnection, sessionLog, simTask, dataDirectory);
	solverControllerImpl.getSolver().addSolverListener(this);
}


/**
 * addWorkerEventListener method comment.
 */
public void addWorkerEventListener(WorkerEventListener listener) {
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
public SimulationTask getSimulationTask() throws DataAccessException {
	try {
		return solverControllerImpl.getSimulationTask();
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
		log.print("LocalSolverController Caught solverAborted("+event.getSource().toString()+",error='"+event.getSimulationMessage()+"')");
		SimulationJob simJob = getSimulationTask().getSimulationJob();
		if (serialParameterScanJobIndex >= 0) {
			SimulationTask newSimTask = new SimulationTask(new SimulationJob(simJob.getSimulation(), serialParameterScanJobIndex, simJob.getFieldDataIdentifierSpecs()),getSimulationTask().getTaskID());
			fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_FAILURE, this, newSimTask, vcConn.getHost(), event.getSimulationMessage()));
		} else {
			fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_FAILURE, this, getSimulationTask(), vcConn.getHost(), event.getSimulationMessage()));
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
		log.print("LocalSolverController Caught solverFinished("+event.getSource().toString()+")");
		SimulationJob simJob = getSimulationTask().getSimulationJob();
		if (serialParameterScanJobIndex >= 0) {
			SimulationTask newSimTask = new SimulationTask(new SimulationJob(simJob.getSimulation(), serialParameterScanJobIndex, simJob.getFieldDataIdentifierSpecs()),getSimulationTask().getTaskID());
			fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_COMPLETED, this, newSimTask, vcConn.getHost(), new Double(event.getProgress()), new Double(event.getTimePoint()), event.getSimulationMessage()));
		} else {
			fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_COMPLETED, this, getSimulationTask(), vcConn.getHost(), new Double(event.getProgress()), new Double(event.getTimePoint()), event.getSimulationMessage()));	
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
	try {
		// don't log progress and data events; data events at larger interval, since more expensive on client side
		if (System.currentTimeMillis() - timeOfLastDataMessage > 4000 * getMessagingInterval()) {
			fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_DATA, this, getSimulationTask(), vcConn.getHost(), event.getProgress(), new Double(event.getTimePoint()), event.getSimulationMessage()));
		}
	}catch (Throwable e){
		log.exception(e);
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
			if (serialParameterScanJobIndex >= 0) {
				SimulationTask newSimTask = new SimulationTask(new SimulationJob(getSimulationTask().getSimulation(), serialParameterScanJobIndex, getSimulationTask().getSimulationJob().getFieldDataIdentifierSpecs()),getSimulationTask().getTaskID());
				fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_PROGRESS, this, newSimTask, vcConn.getHost(), new Double(event.getProgress()), new Double(event.getTimePoint()), event.getSimulationMessage()));
				if (event.getProgress() >= 1) {
					fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_COMPLETED, this, newSimTask, vcConn.getHost(), new Double(event.getProgress()), new Double(event.getTimePoint()), SimulationMessage.MESSAGE_JOB_COMPLETED));
					serialParameterScanJobIndex ++;
				}
			} else {
				fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_PROGRESS, this, getSimulationTask(), vcConn.getHost(), new Double(event.getProgress()), new Double(event.getTimePoint()), event.getSimulationMessage()));
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
		log.print("LocalSolverController Caught solverStarting("+event.getSource().toString()+")");
		if (serialParameterScanJobIndex >= 0) {
			SimulationTask newSimTask = new SimulationTask(new SimulationJob(getSimulationTask().getSimulation(), serialParameterScanJobIndex, getSimulationTask().getSimulationJob().getFieldDataIdentifierSpecs()),getSimulationTask().getTaskID());
			fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_STARTING, this, newSimTask, vcConn.getHost(), event.getSimulationMessage()));
		} else {
			fireWorkerEvent(new WorkerEvent(WorkerEvent.JOB_STARTING, this, getSimulationTask(), vcConn.getHost(), event.getSimulationMessage()));
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
		log.print("LocalSolverController Caught solverStopped("+event.getSource().toString()+")");
	}catch (Throwable e){
		log.exception(e);
	}
}


/**
 * startSimulation method comment.
 */
public void startSimulationJob() throws SimExecutionException, DataAccessException {
	try {
		resultSetSavedSet.remove(getSimulationTask().getSimulationJob().getVCDataIdentifier());
		if (getSimulationTask().getSimulation().isSerialParameterScan()) {
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
