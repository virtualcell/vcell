/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server;
import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;

import javax.jms.JMSException;
import javax.swing.event.EventListenerList;

import org.vcell.util.ConfigurationException;
import org.vcell.util.DataAccessException;
import org.vcell.util.MessageConstants;
import org.vcell.util.PermissionException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCellServerID;

import cbit.rmi.event.SimulationJobStatusEvent;
import cbit.rmi.event.SimulationJobStatusListener;
import cbit.rmi.event.WorkerEvent;
import cbit.rmi.event.WorkerEventListener;
import cbit.vcell.message.VCDestination;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.message.local.LocalVCMessageAdapter;
import cbit.vcell.message.local.LocalVCMessageAdapter.LocalVCMessageListener;
import cbit.vcell.message.messages.SimulationTaskMessage;
import cbit.vcell.message.messages.StatusMessage;
import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.message.server.dispatcher.SimulationDispatcherEngine;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.messaging.db.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.messaging.server.LocalDispatcherDbManager;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationMessage;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverStatus;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.LocalSolverController;
import cbit.vcell.solvers.SolverController;

/**
 * Insert the type's description here.
 * Creation date: (6/28/01 12:50:36 PM)
 * @author: Jim Schaff
 */
public class SimulationControllerImpl implements WorkerEventListener {
	public class SimulationTaskInfo {
		public final KeyValue simKey;
		public final int jobIndex;
		public final int taskID;
		public SimulationTaskInfo(KeyValue simKey,int jobIndex,int taskID){
			this.simKey = simKey;
			this.jobIndex = jobIndex;
			this.taskID = taskID;
		}
		public SimulationTaskInfo(SimulationTask simTask){
			this.simKey = simTask.getSimulation().getKey();
			this.jobIndex = simTask.getSimulationJob().getJobIndex();
			this.taskID = simTask.getTaskID();
		}
		public SimulationTaskInfo(SimulationJob simulationJob ,int taskID){
			this.simKey = simulationJob.getSimulation().getKey();
			this.jobIndex = simulationJob.getJobIndex();
			this.taskID = taskID;
		}
		public SimulationTaskInfo(SimulationInfo simulationInfo, int jobIndex ,int taskID){
			this.simKey = simulationInfo.getSimulationVersion().getVersionKey();
			this.jobIndex = jobIndex;
			this.taskID = taskID;
		}
		@Override
		public boolean equals(Object obj){
			if (obj instanceof SimulationTaskInfo){
				return toString().equals(((SimulationTaskInfo)obj).toString());
			}
			return false;
		}
		@Override
		public int hashCode(){
			return toString().hashCode();
		}
		@Override
		public String toString(){
			return "SimTaskInfo("+simKey.toString()+","+jobIndex+","+taskID+")";
		}
	}
	private java.util.Hashtable<SimulationTaskInfo, SolverController> solverControllerHash = new java.util.Hashtable<SimulationTaskInfo, SolverController>();
	private SessionLog adminSessionLog = null;
	private LocalVCellConnection localVCellConnection = null;
	private SimulationDatabase simulationDatabase = null;
	private EventListenerList listenerList = new javax.swing.event.EventListenerList();
	
	LocalDispatcherDbManager dispatcherDbManager;
	
	private SimulationDispatcherEngine simulationDispatcherEngine = new SimulationDispatcherEngine();
	

/**
 * SimulationControllerImpl constructor comment.
 */
public SimulationControllerImpl(SessionLog argAdminSessionLog, SimulationDatabase simulationDatabase, LocalVCellConnection localVCellConnection) {
	super();
	adminSessionLog = argAdminSessionLog;
	this.localVCellConnection = localVCellConnection;
	this.simulationDatabase = simulationDatabase;
}

/**
 * addSimulationStatusEventListener method comment.
 */
public void addSimulationJobStatusListener(SimulationJobStatusListener listener) {
	listenerList.add(SimulationJobStatusListener.class, listener);
}

private void onClientStatusTopic_SimulationJobStatus(VCMessage simJobStatusMessage){
	Double progress = null;
	if (simJobStatusMessage.propertyExists(StatusMessage.SIMULATION_STATUS_PROGRESS_PROPERTY)){
		progress = simJobStatusMessage.getDoubleProperty(StatusMessage.SIMULATION_STATUS_PROGRESS_PROPERTY);
	}
	Double timepoint = null;
	if (simJobStatusMessage.propertyExists(StatusMessage.SIMULATION_STATUS_TIMEPOINT_PROPERTY)){
		timepoint = simJobStatusMessage.getDoubleProperty(StatusMessage.SIMULATION_STATUS_TIMEPOINT_PROPERTY);
	}
	SimulationJobStatus simJobStatus = (SimulationJobStatus)simJobStatusMessage.getObjectContent();
	
	SimulationJobStatusEvent simulationJobStatusEvent = new SimulationJobStatusEvent(
			SimulationControllerImpl.this, simJobStatus.getVCSimulationIdentifier().getID(), 
			simJobStatus, progress, timepoint);

	fireSimulationJobStatusEvent(simulationJobStatusEvent);
}

public SimulationDatabase getSimulationDatabase(){
	return this.simulationDatabase;
}
/**
 * Insert the method's description here.
 * Creation date: (6/28/01 1:19:54 PM)
 * @return cbit.vcell.solvers.SolverController
 * @param simulation cbit.vcell.solver.Simulation
 * @throws RemoteException 
 * @throws JMSException 
 * @throws AuthenticationException 
 * @throws DataAccessException 
 * @throws SQLException 
 * @throws FileNotFoundException 
 * @throws SolverException 
 * @throws ConfigurationException 
 */
private SolverController createNewSolverController(SimulationTask simTask, SessionLog userSessionLog) throws RemoteException, FileNotFoundException, DataAccessException, AuthenticationException, SQLException, ConfigurationException, SolverException  {
	//
	// either no appropriate slave server or THIS IS A SLAVE SERVER (can't pass the buck).
	//
	LocalSolverController localSolverController = new LocalSolverController(
		localVCellConnection,
		userSessionLog,
		simTask,
		getUserSimulationDirectory(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirProperty))
		);

	localSolverController.addWorkerEventListener(this);
	userSessionLog.alert("returning local SolverController for "+simTask.getSimulationJobID());
	return localSolverController;
}


/**
 * Insert the method's description here.
 * Creation date: (11/17/2000 11:43:22 AM)
 * @param event cbit.rmi.event.JobCompletedEvent
 */
protected void fireSimulationJobStatusEvent(SimulationJobStatusEvent event) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Reset the source to allow proper wiring
	event.setSource(this);
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==SimulationJobStatusListener.class) {
		  ((SimulationJobStatusListener)listeners[i+1]).simulationJobStatusChanged(event);
	    }	       
	}
}


/**
 * This method was created by a SmartGuide.
 * @throws SolverException 
 * @throws DataAccessException 
 * @throws ConfigurationException 
 * @exception java.rmi.RemoteException The exception description.
 * @throws JMSException 
 * @throws AuthenticationException 
 * @throws SQLException 
 * @throws FileNotFoundException 
 */
SolverController getSolverController(SimulationTask simTask, SessionLog userSessionLog) throws RemoteException, FileNotFoundException, ConfigurationException, DataAccessException, AuthenticationException, SQLException, SolverException  {
	Simulation simulation = simTask.getSimulation();
	VCSimulationIdentifier vcSimID = simulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
	if (vcSimID == null){
		throw new IllegalArgumentException("cannot run an unsaved simulation");
	}
	if (!simulation.getVersion().getOwner().equals(localVCellConnection.getUserLoginInfo().getUser())){
		throw new PermissionException("insufficient privilege: startSimulation()");
	}
	SimulationTaskInfo simTaskInfo = new SimulationTaskInfo(simTask);
	SolverController solverController = solverControllerHash.get(simTaskInfo);
	if (solverController==null){
		solverController = createNewSolverController(simTask,userSessionLog);
		solverControllerHash.put(simTaskInfo,solverController);
	}
	return solverController;
}

/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
public SolverStatus getSolverStatus(SimulationInfo simulationInfo, int jobIndex, int taskID) throws RemoteException, PermissionException, DataAccessException {
	SimulationTaskInfo simTaskInfo = new SimulationTaskInfo(simulationInfo, jobIndex, taskID);
	SolverController solverController = solverControllerHash.get(simTaskInfo);
	if (solverController==null){
		return new SolverStatus(SolverStatus.SOLVER_READY, SimulationMessage.MESSAGE_SOLVER_READY);
	}
	return solverController.getSolverStatus();
}


private File getUserSimulationDirectory(String simDataRoot) {
	String userName = localVCellConnection.getUserLoginInfo().getUserName();
	File directory = new File(new File(simDataRoot), userName);
	if (!directory.exists()){
		if (!directory.mkdirs()){
			String msg = "could not create directory "+directory;
			adminSessionLog.alert(msg);
			throw new ConfigurationException(msg);
		}
	}		 
	return directory;
}

/**
 * Insert the method's description here.
 * Creation date: (3/11/2004 10:44:18 AM)
 * @param newJobStatus cbit.vcell.messaging.db.SimulationJobStatus
 * @param progress java.lang.Double
 * @param timePoint java.lang.Double
 */
public void onWorkerEvent(WorkerEvent workerEvent) {
	try {
		
		LocalVCMessageListener localVCMessageListener = new LocalVCMessageListener(){
			
			public void onLocalVCMessage(VCDestination destination, VCMessage objectMessage) {
				if (destination == VCellTopic.ClientStatusTopic && objectMessage.getObjectContent() instanceof SimulationJobStatus){
					onClientStatusTopic_SimulationJobStatus(objectMessage);
				}else{
					throw new RuntimeException("SimulationControllerImpl.onWorkerEvent().localMessageListener::  expecting object message with SimulationJobStatus to topic "+VCellTopic.ClientStatusTopic.getName()+": received \""+objectMessage.show()+"\"");
				}
			}
			
		};
		
		LocalVCMessageAdapter vcMessageSession = new LocalVCMessageAdapter(localVCMessageListener);
		simulationDispatcherEngine.onWorkerEvent(workerEvent, simulationDatabase, vcMessageSession, adminSessionLog);
		vcMessageSession.deliverAll();
	}catch (Exception e){
		adminSessionLog.exception(e);
	}
}

/**
 * removeSimulationStatusEventListener method comment.
 */
public void removeSimulationJobStatusListener(SimulationJobStatusListener listener) {
	listenerList.remove(SimulationJobStatusListener.class, listener);
}

private void onSimJobQueue_SimulationTask(VCMessage vcMessage) {
	SimulationTask simTask = null;
	try {
		
		SimulationTaskMessage simTaskMessage = new SimulationTaskMessage(vcMessage);
		simTask = simTaskMessage.getSimulationTask();
		
		SolverController solverController = getSolverController(simTask,adminSessionLog);
		
		solverController.startSimulationJob(); // can only start after updating the database is done
		
	} catch (Exception e) {
		adminSessionLog.exception(e);
		KeyValue simKey = simTask.getSimKey();
		VCSimulationIdentifier vcSimID = simTask.getSimulationJob().getVCDataIdentifier().getVcSimID();
		int jobIndex = simTask.getSimulationJob().getJobIndex();
		int taskID = simTask.getTaskID();
		SimulationJobStatus newJobStatus = new SimulationJobStatus(VCellServerID.getSystemServerID(), vcSimID, jobIndex, null, SchedulerStatus.FAILED, taskID, SimulationMessage.jobFailed(e.getMessage()), null, null);
		SimulationJobStatusEvent event = new SimulationJobStatusEvent(this, Simulation.createSimulationID(simKey), newJobStatus, null, null);
		fireSimulationJobStatusEvent(event);
	}
}

/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public void startSimulation(Simulation simulation, SessionLog userSessionLog) throws RemoteException, Exception {

	LocalVCMessageListener localVCMessageListener = new LocalVCMessageListener(){
		
		public void onLocalVCMessage(VCDestination destination, VCMessage vcMessage) {
			if (destination == VCellTopic.ClientStatusTopic && vcMessage.getObjectContent() instanceof SimulationJobStatus){
				onClientStatusTopic_SimulationJobStatus(vcMessage);
			}else if (destination == VCellQueue.SimJobQueue && vcMessage.getStringProperty(MessageConstants.MESSAGE_TYPE_PROPERTY).equals(MessageConstants.MESSAGE_TYPE_SIMULATION_JOB_VALUE)){
				onSimJobQueue_SimulationTask(vcMessage);
			}else{
				throw new RuntimeException("SimulationControllerImpl.startSimulation().objectMessageListener:: expecting object message with SimulationJobStatus to topic "+VCellTopic.ClientStatusTopic.getName()+": received \""+vcMessage.show()+"\"");
			}
		}
		
	};
	
	LocalVCMessageAdapter vcMessageSession = new LocalVCMessageAdapter(localVCMessageListener);

	removeSimulationJobStatusListener(localVCellConnection.getMessageCollector());
	addSimulationJobStatusListener(localVCellConnection.getMessageCollector());

	VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simulation.getKey(), simulation.getVersion().getOwner());
	simulationDispatcherEngine.onStartRequest(vcSimID, localVCellConnection.getUserLoginInfo().getUser(), simulationDatabase, vcMessageSession, vcMessageSession, adminSessionLog);
	vcMessageSession.deliverAll();
}

private void onServiceControlTopic_StopSimulation(VCMessage message){
	KeyValue simKey = new KeyValue(String.valueOf(message.getLongProperty(MessageConstants.SIMKEY_PROPERTY)));
	int jobIndex = message.getIntProperty(MessageConstants.JOBINDEX_PROPERTY);
	int taskID = message.getIntProperty(MessageConstants.TASKID_PROPERTY);
	SimulationTask simTask = null;
	
	try {
		simTask = new SimulationTask(simulationDatabase.getSimulationJob(simKey,jobIndex),taskID);
		
		SolverController solverController = getSolverController(simTask,adminSessionLog);
		
		solverController.stopSimulationJob(); // can only start after updating the database is done
		
	} catch (Exception e) {
		adminSessionLog.exception(e);
		VCSimulationIdentifier vcSimID = simTask.getSimulationJob().getVCDataIdentifier().getVcSimID();
		SimulationJobStatus newJobStatus = new SimulationJobStatus(VCellServerID.getSystemServerID(), vcSimID, jobIndex, null, SchedulerStatus.FAILED, taskID, SimulationMessage.jobFailed(e.getMessage()), null, null);
		SimulationJobStatusEvent event = new SimulationJobStatusEvent(this, Simulation.createSimulationID(simKey), newJobStatus, null, null);
		fireSimulationJobStatusEvent(event);
	}
	
}
/**
 * This method was created by a SmartGuide.
 * @throws JMSException 
 * @throws AuthenticationException 
 * @throws DataAccessException 
 * @throws SQLException 
 * @throws FileNotFoundException 
 * @exception java.rmi.RemoteException The exception description.
 * @throws VCMessagingException 
 */
public void stopSimulation(Simulation simulation) throws RemoteException, FileNotFoundException, SQLException, DataAccessException, AuthenticationException, JMSException, VCMessagingException {	
	LocalVCMessageListener localVCMessageListener = new LocalVCMessageListener(){
		
		public void onLocalVCMessage(VCDestination destination, VCMessage objectMessage) {
			String messageTypeProperty = MessageConstants.MESSAGE_TYPE_PROPERTY;
			String stopSimulationValue = MessageConstants.MESSAGE_TYPE_STOPSIMULATION_VALUE;
			if (destination == VCellTopic.ClientStatusTopic && objectMessage.getObjectContent() instanceof SimulationJobStatus){
				onClientStatusTopic_SimulationJobStatus(objectMessage);
			}else if (destination == VCellTopic.ServiceControlTopic && objectMessage.getStringProperty(messageTypeProperty).equals(stopSimulationValue)){
				onServiceControlTopic_StopSimulation(objectMessage);
			}else{
				throw new RuntimeException("SimulationControllerImpl.startSimulation().objectMessageListener:: expecting message with SimulationJobStatus to topic "+VCellTopic.ClientStatusTopic.getName()+": received \""+objectMessage.show()+"\" on destination \""+destination+"\"");
			}
		}
		
	};
	
	LocalVCMessageAdapter vcMessageSession = new LocalVCMessageAdapter(localVCMessageListener);

	VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simulation.getKey(), simulation.getVersion().getOwner());
	simulationDispatcherEngine.onStopRequest(vcSimID, localVCellConnection.getUserLoginInfo().getUser(), simulationDatabase, vcMessageSession, adminSessionLog);
	vcMessageSession.deliverAll();
}

}
