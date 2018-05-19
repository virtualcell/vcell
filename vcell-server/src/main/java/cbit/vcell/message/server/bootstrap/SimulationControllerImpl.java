/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.bootstrap;
import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;

import javax.jms.JMSException;
import javax.swing.event.EventListenerList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.AuthenticationException;
import org.vcell.util.ConfigurationException;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCellServerID;

import cbit.rmi.event.SimulationJobStatusEvent;
import cbit.rmi.event.SimulationJobStatusListener;
import cbit.rmi.event.WorkerEvent;
import cbit.rmi.event.WorkerEventListener;
import cbit.vcell.message.VCDestination;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessagingConstants;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.message.local.LocalVCMessageAdapter;
import cbit.vcell.message.local.LocalVCMessageAdapter.LocalVCMessageListener;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.messages.SimulationTaskMessage;
import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.message.server.dispatcher.SimulationDispatcherEngine;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.SimpleJobStatus;
import cbit.vcell.server.SimpleJobStatusQuerySpec;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.server.SimulationTaskID;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.solvers.LocalSolverController;

/**
 * Insert the type's description here.
 * Creation date: (6/28/01 12:50:36 PM)
 * @author: Jim Schaff
 */
public class SimulationControllerImpl implements WorkerEventListener {
	public static final Logger lg = LogManager.getLogger(SimulationControllerImpl.class);

	private java.util.Hashtable<SimulationTaskID, LocalSolverController> solverControllerHash = new java.util.Hashtable<SimulationTaskID, LocalSolverController>();
	private LocalVCellConnection localVCellConnection = null;
	private SimulationDatabase simulationDatabase = null;
	private EventListenerList listenerList = new javax.swing.event.EventListenerList();
	
	private SimulationDispatcherEngine simulationDispatcherEngine = new SimulationDispatcherEngine();
	

/**
 * SimulationControllerImpl constructor comment.
 */
public SimulationControllerImpl(SimulationDatabase simulationDatabase, LocalVCellConnection localVCellConnection) {
	super();
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
	if (simJobStatusMessage.propertyExists(MessageConstants.SIMULATION_STATUS_PROGRESS_PROPERTY)){
		progress = simJobStatusMessage.getDoubleProperty(MessageConstants.SIMULATION_STATUS_PROGRESS_PROPERTY);
	}
	Double timepoint = null;
	if (simJobStatusMessage.propertyExists(MessageConstants.SIMULATION_STATUS_TIMEPOINT_PROPERTY)){
		timepoint = simJobStatusMessage.getDoubleProperty(MessageConstants.SIMULATION_STATUS_TIMEPOINT_PROPERTY);
	}
	SimulationJobStatus simJobStatus = (SimulationJobStatus)simJobStatusMessage.getObjectContent();
	
	SimulationJobStatusEvent simulationJobStatusEvent = new SimulationJobStatusEvent(
			SimulationControllerImpl.this, simJobStatus.getVCSimulationIdentifier().getID(), 
			simJobStatus, progress, timepoint, simJobStatus.getVCSimulationIdentifier().getOwner().getName());

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
private LocalSolverController createNewSolverController(SimulationTask simTask) throws FileNotFoundException, DataAccessException, AuthenticationException, SQLException, ConfigurationException, SolverException  {
	//
	// either no appropriate slave server or THIS IS A SLAVE SERVER (can't pass the buck).
	//
	LocalSolverController localSolverController = new LocalSolverController(
		localVCellConnection,
		simTask,
		getUserSimulationDirectory(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty))
		);

	localSolverController.addWorkerEventListener(this);
	if (lg.isTraceEnabled()) lg.trace("returning local SolverController for "+simTask.getSimulationJobID());
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
LocalSolverController getOrCreateSolverController(SimulationTask simTask) throws FileNotFoundException, ConfigurationException, DataAccessException, AuthenticationException, SQLException, SolverException  {
	Simulation simulation = simTask.getSimulation();
	VCSimulationIdentifier vcSimID = simulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
	if (vcSimID == null){
		throw new IllegalArgumentException("cannot run an unsaved simulation");
	}
	if (!simulation.getVersion().getOwner().equals(localVCellConnection.getUserLoginInfo().getUser())){
		throw new PermissionException("insufficient privilege: startSimulation()");
	}
	SimulationTaskID simTaskInfo = new SimulationTaskID(simTask);
	LocalSolverController solverController = solverControllerHash.get(simTaskInfo);
	if (solverController==null){
		solverController = createNewSolverController(simTask);
		solverControllerHash.put(simTaskInfo,solverController);
	}
	return solverController;
}

/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
public SolverStatus getSolverStatus(SimulationInfo simulationInfo, int jobIndex, int taskID) throws PermissionException, DataAccessException {
	SimulationTaskID simTaskInfo = new SimulationTaskID(simulationInfo, jobIndex, taskID);
	LocalSolverController solverController = solverControllerHash.get(simTaskInfo);
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
			if (lg.isWarnEnabled()) lg.warn(msg);
			throw new ConfigurationException(msg);
		}
		//
		// directory create from container (possibly) as root, make this user directory accessible from user "vcell" 
		//
		directory.setWritable(true,false);
		directory.setExecutable(true,false);
		directory.setReadable(true,false);
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
		simulationDispatcherEngine.onWorkerEvent(workerEvent, simulationDatabase, vcMessageSession);
		vcMessageSession.deliverAll();
	}catch (Exception e){
		lg.error(e.getMessage(), e);
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
		
		LocalSolverController solverController = getOrCreateSolverController(simTask);
		
		solverController.startSimulationJob(); // can only start after updating the database is done
		
	} catch (Exception e) {
		lg.error(e.getMessage(), e);
		KeyValue simKey = simTask.getSimKey();
		VCSimulationIdentifier vcSimID = simTask.getSimulationJob().getVCDataIdentifier().getVcSimID();
		int jobIndex = simTask.getSimulationJob().getJobIndex();
		int taskID = simTask.getTaskID();
		SimulationJobStatus newJobStatus = new SimulationJobStatus(VCellServerID.getSystemServerID(), vcSimID, jobIndex, null, SchedulerStatus.FAILED, taskID, SimulationMessage.jobFailed(e.getMessage()), null, null);
		SimulationJobStatusEvent event = new SimulationJobStatusEvent(this, Simulation.createSimulationID(simKey), newJobStatus, null, null,vcSimID.getOwner().getName());
		fireSimulationJobStatusEvent(event);
	}
}

/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public void startSimulation(Simulation simulation) throws Exception {

	LocalVCMessageListener localVCMessageListener = new LocalVCMessageListener(){
		
		public void onLocalVCMessage(VCDestination destination, VCMessage vcMessage) {
			if (destination == VCellTopic.ClientStatusTopic && vcMessage.getObjectContent() instanceof SimulationJobStatus){
				onClientStatusTopic_SimulationJobStatus(vcMessage);
			}else if (destination == VCellQueue.SimJobQueue && vcMessage.getStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY).equals(MessageConstants.MESSAGE_TYPE_SIMULATION_JOB_VALUE)){
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
	simulationDispatcherEngine.onStartRequest(vcSimID, localVCellConnection.getUserLoginInfo().getUser(), simulation.getScanCount(), simulationDatabase, vcMessageSession, vcMessageSession);
	vcMessageSession.deliverAll();
	for (int jobIndex = 0; jobIndex < simulation.getScanCount(); jobIndex++){
		SimulationJobStatus latestSimJobStatus = simulationDatabase.getLatestSimulationJobStatus(simulation.getKey(), jobIndex);
		simulationDispatcherEngine.onDispatch(simulation, latestSimJobStatus, simulationDatabase, vcMessageSession);
		vcMessageSession.deliverAll();
	}
}

private void onServiceControlTopic_StopSimulation(VCMessage message){
	KeyValue simKey = new KeyValue(String.valueOf(message.getLongProperty(MessageConstants.SIMKEY_PROPERTY)));
	int jobIndex = message.getIntProperty(MessageConstants.JOBINDEX_PROPERTY);
	int taskID = message.getIntProperty(MessageConstants.TASKID_PROPERTY);
	
	try {
		SimulationTaskID simTaskInfo = new SimulationTaskID(simKey, jobIndex, taskID);
		LocalSolverController solverController = solverControllerHash.get(simTaskInfo);
		if (solverController!=null){
			solverController.stopSimulationJob(); // can only start after updating the database is done
		}
		
	} catch (Exception e) {
		lg.error(e.getMessage(), e);
		VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, localVCellConnection.getUserLoginInfo().getUser());
		SimulationJobStatus newJobStatus = new SimulationJobStatus(VCellServerID.getSystemServerID(), vcSimID, jobIndex, null, SchedulerStatus.FAILED, taskID, SimulationMessage.jobFailed(e.getMessage()), null, null);
		SimulationJobStatusEvent event = new SimulationJobStatusEvent(this, Simulation.createSimulationID(simKey), newJobStatus, null, null,vcSimID.getOwner().getName());
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
public void stopSimulation(Simulation simulation) throws FileNotFoundException, SQLException, DataAccessException, AuthenticationException, JMSException, VCMessagingException {	
	LocalVCMessageListener localVCMessageListener = new LocalVCMessageListener(){
		
		public void onLocalVCMessage(VCDestination destination, VCMessage objectMessage) {
			String messageTypeProperty = VCMessagingConstants.MESSAGE_TYPE_PROPERTY;
			String stopSimulationValue = MessageConstants.MESSAGE_TYPE_STOPSIMULATION_VALUE;
			if (destination == VCellTopic.ClientStatusTopic && objectMessage.getObjectContent() instanceof SimulationJobStatus){
				onClientStatusTopic_SimulationJobStatus(objectMessage);
			}else if (destination == VCellTopic.ServiceControlTopic && objectMessage.getStringProperty(messageTypeProperty).equals(stopSimulationValue)){
				lg.error("SimulationControllerImpl.stopSimulation() exercising serviceControl topic ... should be removed");
				onServiceControlTopic_StopSimulation(objectMessage);
			}else{
				throw new RuntimeException("SimulationControllerImpl.startSimulation().objectMessageListener:: expecting message with SimulationJobStatus to topic "+VCellTopic.ClientStatusTopic.getName()+": received \""+objectMessage.show()+"\" on destination \""+destination+"\"");
			}
		}
		
	};
	
	LocalVCMessageAdapter vcMessageSession = new LocalVCMessageAdapter(localVCMessageListener);

	VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simulation.getKey(), simulation.getVersion().getOwner());
	simulationDispatcherEngine.onStopRequest(vcSimID, localVCellConnection.getUserLoginInfo().getUser(), simulationDatabase, vcMessageSession);
	vcMessageSession.deliverAll();
}

public SimulationStatus[] getSimulationStatus(KeyValue[] simKeys) throws ObjectNotFoundException, DataAccessException {
	return simulationDatabase.getSimulationStatus(simKeys);
}

public SimulationStatus getSimulationStatus(KeyValue simulationKey) throws ObjectNotFoundException, DataAccessException {
	return simulationDatabase.getSimulationStatus(simulationKey);
}

public SimpleJobStatus[] getSimpleJobStatus(SimpleJobStatusQuerySpec simStatusQuerySpec) throws ObjectNotFoundException, DataAccessException {
	return simulationDatabase.getSimpleJobStatus(localVCellConnection.getUserLoginInfo().getUser(), simStatusQuerySpec);
}

}
