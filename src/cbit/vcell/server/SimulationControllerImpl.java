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
import org.vcell.util.PermissionException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import cbit.rmi.event.SimulationJobStatusEvent;
import cbit.rmi.event.SimulationJobStatusListener;
import cbit.rmi.event.WorkerEvent;
import cbit.rmi.event.WorkerEventListener;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.messaging.db.UpdateSynchronizationException;
import cbit.vcell.messaging.server.DispatcherDbManager;
import cbit.vcell.messaging.server.LocalDispatcherDbManager;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationMessage;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverStatus;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.LocalSolverController;
import cbit.vcell.solvers.SimExecutionException;
import cbit.vcell.solvers.SolverController;

/**
 * Insert the type's description here.
 * Creation date: (6/28/01 12:50:36 PM)
 * @author: Jim Schaff
 */
public class SimulationControllerImpl implements WorkerEventListener {
	private java.util.Hashtable<String, SolverController> solverControllerHash = new java.util.Hashtable<String, SolverController>();
	private SessionLog adminSessionLog = null;
	private LocalVCellServer fieldLocalVCellServer = null;
	private AdminDatabaseServer adminDbServer = null;
	private EventListenerList listenerList = new javax.swing.event.EventListenerList();

	private DispatcherDbManager dispatcherDbManager = new LocalDispatcherDbManager();

/**
 * SimulationControllerImpl constructor comment.
 */
public SimulationControllerImpl(SessionLog argAdminSessionLog, AdminDatabaseServer argAdminDbServer, LocalVCellServer argLocalVCellServer) {
	super();
	adminSessionLog = argAdminSessionLog;
	fieldLocalVCellServer = argLocalVCellServer;
	adminDbServer = argAdminDbServer;
}

/**
 * addSimulationStatusEventListener method comment.
 */
public void addSimulationJobStatusListener(SimulationJobStatusListener listener) {
	listenerList.add(SimulationJobStatusListener.class, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (6/28/01 1:19:54 PM)
 * @return cbit.vcell.solvers.SolverController
 * @param simulation cbit.vcell.solver.Simulation
 * @throws JMSException 
 * @throws AuthenticationException 
 * @throws DataAccessException 
 * @throws SQLException 
 * @throws FileNotFoundException 
 */
private SolverController createNewSolverController(UserLoginInfo userLoginInfo, SimulationJob simulationJob, SessionLog userSessionLog) throws RemoteException, SimExecutionException, SolverException, FileNotFoundException, SQLException, DataAccessException, AuthenticationException, JMSException {
	//
	// either no appropriate slave server or THIS IS A SLAVE SERVER (can't pass the buck).
	//
	User user = userLoginInfo.getUser();
	LocalVCellConnection localVCellConnection = (LocalVCellConnection)getLocalVCellServer().getVCellConnection(userLoginInfo);
	LocalSolverController localSolverController = new LocalSolverController(
		localVCellConnection,
		userSessionLog,
		simulationJob,
		getUserSimulationDirectory(user, PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirProperty))
		);

	localSolverController.addWorkerEventListener(this);
	userSessionLog.alert("returning local SolverController for "+simulationJob.getSimulationJobID());
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
 * Insert the method's description here.
 * Creation date: (6/28/01 4:33:49 PM)
 * @return cbit.vcell.server.LocalVCellServer
 */
public LocalVCellServer getLocalVCellServer() {
	return fieldLocalVCellServer;
}

/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 * @throws JMSException 
 * @throws AuthenticationException 
 * @throws SQLException 
 * @throws FileNotFoundException 
 */
SolverController getSolverController(UserLoginInfo userLoginInfo, SimulationJob simulationJob, SessionLog userSessionLog) throws RemoteException, SimExecutionException, SolverException, PermissionException, DataAccessException, FileNotFoundException, SQLException, AuthenticationException, JMSException {
	User user = userLoginInfo.getUser();
	Simulation simulation = simulationJob.getSimulation();
	VCSimulationIdentifier vcSimID = simulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier();
	if (vcSimID == null){
		throw new IllegalArgumentException("cannot run an unsaved simulation");
	}
	if (!simulation.getVersion().getOwner().equals(user)){
		throw new PermissionException("insufficient privilege: startSimulation()");
	}
	SolverController solverController = solverControllerHash.get(simulationJob.getSimulationJobID());
	if (solverController==null){
		solverController = createNewSolverController(userLoginInfo,simulationJob,userSessionLog);
		solverControllerHash.put(simulationJob.getSimulationJobID(),solverController);
	}
	return solverController;
}

/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
public SolverStatus getSolverStatus(User user, SimulationInfo simulationInfo, int jobIndex) throws RemoteException, PermissionException, DataAccessException {
	SolverController solverController = solverControllerHash.get(SimulationJob.createSimulationJobID(Simulation.createSimulationID(simulationInfo.getAuthoritativeVCSimulationIdentifier().getSimulationKey()),jobIndex));
	if (solverController==null){
		return new SolverStatus(SolverStatus.SOLVER_READY, SimulationMessage.MESSAGE_SOLVER_READY);
	}
	return solverController.getSolverStatus();
}


private File getUserSimulationDirectory(User user, String simDataRoot) {
	File directory = new File(new File(simDataRoot), user.getName());
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
 * Creation date: (2/11/2004 11:26:21 AM)
 * @param ex java.lang.Exception
 */
private void handleException(VCSimulationIdentifier vcSimulationIdentifier, int jobIndex, Exception ex) {
	VCellServerID serverID = VCellServerID.getSystemServerID();
	try {
		SimulationJobStatus oldJobStatus = adminDbServer.getSimulationJobStatus(vcSimulationIdentifier.getSimulationKey(), jobIndex);
		if (oldJobStatus != null) {
			serverID = oldJobStatus.getServerID();
		}
		SimulationJobStatus newJobStatus = updateFailedJobStatus(oldJobStatus, vcSimulationIdentifier, jobIndex, SimulationMessage.solverAborted(ex.getMessage()));
		if (newJobStatus == null) {
			newJobStatus = new SimulationJobStatus(serverID, vcSimulationIdentifier, jobIndex, null, SimulationJobStatus.SCHEDULERSTATUS_FAILED, -1, SimulationMessage.jobFailed(ex.getMessage()), null, null);
		}
		
		SimulationJobStatusEvent event = new SimulationJobStatusEvent(this, Simulation.createSimulationID(vcSimulationIdentifier.getSimulationKey()), newJobStatus, null, null);
		fireSimulationJobStatusEvent(event);
	} catch (DataAccessException e) {
		SimulationJobStatus newJobStatus = new SimulationJobStatus(serverID, vcSimulationIdentifier, jobIndex, null, SimulationJobStatus.SCHEDULERSTATUS_FAILED, -1, SimulationMessage.jobFailed(e.getMessage()), null, null);
		SimulationJobStatusEvent event = new SimulationJobStatusEvent(this, Simulation.createSimulationID(vcSimulationIdentifier.getSimulationKey()), newJobStatus, null, null);
		fireSimulationJobStatusEvent(event);
	} catch (RemoteException e) {
		SimulationJobStatus newJobStatus = new SimulationJobStatus(serverID, vcSimulationIdentifier, jobIndex, null, SimulationJobStatus.SCHEDULERSTATUS_FAILED, -1, SimulationMessage.jobFailed(e.getMessage()), null, null);
		SimulationJobStatusEvent event = new SimulationJobStatusEvent(this, Simulation.createSimulationID(vcSimulationIdentifier.getSimulationKey()), newJobStatus, null, null);
		fireSimulationJobStatusEvent(event);
	}	
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
		VCSimulationIdentifier vcSimulationIdentifier = workerEvent.getVCSimulationDataIdentifier().getVcSimID();
		int jobIndex = workerEvent.getJobIndex();
		SimulationJobStatus oldJobStatus = adminDbServer.getSimulationJobStatus(vcSimulationIdentifier.getSimulationKey(), jobIndex);
		SimulationJobStatus newJobStatus = null;

		if (oldJobStatus == null || oldJobStatus.isDone()) {
			return;
		}
		
		if (workerEvent.isCompletedEvent()) {
			newJobStatus = updateCompletedJobStatus(oldJobStatus, vcSimulationIdentifier, jobIndex, workerEvent.getSimulationMessage());			
			
		} else if (workerEvent.isFailedEvent()) {
			newJobStatus = updateFailedJobStatus(oldJobStatus, vcSimulationIdentifier, jobIndex, workerEvent.getSimulationMessage());			
			
		} else if (workerEvent.isNewDataEvent()) {
			newJobStatus = updateRunningJobStatus(oldJobStatus, vcSimulationIdentifier, jobIndex, true, workerEvent.getSimulationMessage());
			
		} else if (workerEvent.isProgressEvent()) {
			newJobStatus = updateRunningJobStatus(oldJobStatus, vcSimulationIdentifier, jobIndex, false, workerEvent.getSimulationMessage());
			
		} else if (workerEvent.isStartingEvent()) {
			if (oldJobStatus.isQueued() || oldJobStatus.isDispatched()) {
				newJobStatus = updateRunningJobStatus(oldJobStatus, vcSimulationIdentifier, jobIndex, false, workerEvent.getSimulationMessage());
			} else if (oldJobStatus.isRunning()) {
				newJobStatus = new SimulationJobStatus(oldJobStatus.getServerID(), oldJobStatus.getVCSimulationIdentifier(), oldJobStatus.getJobIndex(), oldJobStatus.getSubmitDate(), 
					oldJobStatus.getSchedulerStatus(), oldJobStatus.getTaskID(), workerEvent.getSimulationMessage(), oldJobStatus.getSimulationQueueEntryStatus(), oldJobStatus.getSimulationExecutionStatus());
			}				
		}
		if (workerEvent.isStartingEvent() && newJobStatus != null) {
			SimulationJobStatusEvent newEvent = new SimulationJobStatusEvent(this, Simulation.createSimulationID(vcSimulationIdentifier.getSimulationKey()), newJobStatus, null, null);
			fireSimulationJobStatusEvent(newEvent);
		} else 	if (newJobStatus != null && (!newJobStatus.compareEqual(oldJobStatus) || workerEvent.isProgressEvent() || workerEvent.isNewDataEvent())) {
			Double progress = workerEvent.getProgress();
			Double timepoint = workerEvent.getTimePoint();
			SimulationJobStatusEvent newEvent = new SimulationJobStatusEvent(this, Simulation.createSimulationID(vcSimulationIdentifier.getSimulationKey()), newJobStatus, progress, timepoint);
			fireSimulationJobStatusEvent(newEvent);
		}	
	} catch (DataAccessException ex) {
		adminSessionLog.exception(ex);
	} catch (RemoteException ex) {
		adminSessionLog.exception(ex);
	}
}


/**
 * removeSimulationStatusEventListener method comment.
 */
public void removeSimulationJobStatusListener(SimulationJobStatusListener listener) {
	listenerList.remove(SimulationJobStatusListener.class, listener);
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public void startSimulation(UserLoginInfo userLoginInfo, Simulation simulation, SessionLog userSessionLog) throws RemoteException, Exception {
	User user = userLoginInfo.getUser();
	LocalVCellConnection localVCellConnection = (LocalVCellConnection)getLocalVCellServer().getVCellConnection(userLoginInfo);
	removeSimulationJobStatusListener(localVCellConnection.getMessageCollector());
	addSimulationJobStatusListener(localVCellConnection.getMessageCollector());
	
	FieldFunctionArguments[] fieldFuncArgs = simulation.getMathDescription().getFieldFunctionArguments();
	FieldDataIdentifierSpec[] fieldDataIDs = new FieldDataIdentifierSpec[fieldFuncArgs.length];
	if (fieldFuncArgs.length != 0) {
		ExternalDataIdentifier[]  qualifiedSpecs =
			getLocalVCellServer().
				getVCellConnection(userLoginInfo).
					getUserMetaDbServer().
						fieldDataDBOperation(
								FieldDataDBOperationSpec.createGetExtDataIDsSpec(user)).extDataIDArr;
		for(int i=0;i<fieldFuncArgs.length;i+= 1){
			for(int j=0;j<qualifiedSpecs.length;j+= 1){
				if(fieldFuncArgs[i].getFieldName().equals(qualifiedSpecs[j].getName())){
					fieldDataIDs[i] = new FieldDataIdentifierSpec(fieldFuncArgs[i],qualifiedSpecs[j]);
					break;
				}
			}
			if(fieldDataIDs[i] == null){
				throw new DataAccessException("Failed to resolve FieldData name "+fieldFuncArgs[i].getFieldName()+" for sim "+simulation.getName());
			}
		}
	} 
	
	boolean serialParameterScan = simulation.isSerialParameterScan();
	int scanCount = simulation.getScanCount();
	for (int i = 0; i < scanCount; i++){
		SimulationJob simJob = new SimulationJob(simulation, i, fieldDataIDs);
		VCSimulationIdentifier vcSimID = simJob.getVCDataIdentifier().getVcSimID();
		try {

			SolverController solverController = getSolverController(userLoginInfo,simJob,userSessionLog);
			SimulationJobStatus oldJobStatus = adminDbServer.getSimulationJobStatus(simulation.getKey(),i);	
			SimulationJobStatus newJobStatus = updateDispatchedJobStatus(oldJobStatus, vcSimID, i);
			
			if (newJobStatus != null) {
				SimulationJobStatusEvent event = new SimulationJobStatusEvent(this, simulation.getSimulationID(), newJobStatus, null, null);
				fireSimulationJobStatusEvent(event);
			}

			if (!serialParameterScan || i == 0 ) {
				solverController.startSimulationJob(); // can only start after updating the database is done
			}
		} catch (Exception ex) {
			handleException(vcSimID,i,ex);
		}	
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
 */
public void stopSimulation(UserLoginInfo userLoginInfo, Simulation simulation) throws RemoteException, FileNotFoundException, SQLException, DataAccessException, AuthenticationException, JMSException {	
	User user = userLoginInfo.getUser();
	LocalVCellConnection localVCellConnection = (LocalVCellConnection)getLocalVCellServer().getVCellConnection(userLoginInfo);
	removeSimulationJobStatusListener(localVCellConnection.getMessageCollector());
	addSimulationJobStatusListener(localVCellConnection.getMessageCollector());
	for (int i = 0; i < simulation.getScanCount(); i++){
		VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simulation.getKey(),simulation.getVersion().getOwner());
		try {
			if (!vcSimID.getOwner().equals(user)){
				throw new PermissionException("insufficient privilege: stopSimulation()");
			}
			SimulationJobStatus oldJobStatus = adminDbServer.getSimulationJobStatus(vcSimID.getSimulationKey(), i);	
			SimulationJobStatus newJobStatus = updateStoppedJobStatus(oldJobStatus, vcSimID, i);
			if (newJobStatus != null) {
				SimulationJobStatusEvent event = new SimulationJobStatusEvent(this, simulation.getSimulationID(), newJobStatus, null, null);
				fireSimulationJobStatusEvent(event);
			}
				
			SolverController solverController = solverControllerHash.get(SimulationJob.createSimulationJobID(Simulation.createSimulationID(vcSimID.getSimulationKey()), i));
			if (solverController != null){
				solverController.stopSimulationJob();
			}
		} catch (Exception ex) {
			handleException(vcSimID,i,ex);
		}
	}
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public void stopSimulation(UserLoginInfo userLoginInfo, VCSimulationIdentifier vcSimID, int jobIndex, SimulationMessage simulationMessage) {	
	try {
		LocalVCellConnection localVCellConnection = (LocalVCellConnection)getLocalVCellServer().getVCellConnection(userLoginInfo);
		removeSimulationJobStatusListener(localVCellConnection.getMessageCollector());
		addSimulationJobStatusListener(localVCellConnection.getMessageCollector());
		if (!vcSimID.getOwner().equals(userLoginInfo.getUser())){
			throw new PermissionException("insufficient privilege: stopSimulation()");
		}
		SimulationJobStatus oldJobStatus = adminDbServer.getSimulationJobStatus(vcSimID.getSimulationKey(),jobIndex);	
		SimulationJobStatus newJobStatus = updateStoppedJobStatus(oldJobStatus, vcSimID, jobIndex);
		if (newJobStatus != null) {
			SimulationJobStatusEvent event = new SimulationJobStatusEvent(this, Simulation.createSimulationID(vcSimID.getSimulationKey()), newJobStatus, null, null);
			fireSimulationJobStatusEvent(event);
		}
			
		SolverController solverController = solverControllerHash.get(SimulationJob.createSimulationJobID(Simulation.createSimulationID(vcSimID.getSimulationKey()), jobIndex));
		if (solverController != null){
			solverController.stopSimulationJob();
		}
	} catch (Exception ex) {
		handleException(vcSimID,jobIndex,ex);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/5/2003 11:50:34 AM)
 * @param sim cbit.vcell.solver.Simulation
 * @param jobStatus cbit.vcell.messaging.db.SimulationJobStatus
 */
private SimulationJobStatus updateCompletedJobStatus(SimulationJobStatus oldJobStatus, VCSimulationIdentifier vcSimulationIdentifier, int jobIndex, SimulationMessage simulationMessage) throws DataAccessException, RemoteException {
	SolverController solverController = solverControllerHash.get(SimulationJob.createSimulationJobID(Simulation.createSimulationID(vcSimulationIdentifier.getSimulationKey()), jobIndex));
	if (solverController == null) {
		return null;
	}

	synchronized (solverController) {
		String host = (solverController != null) ? solverController.getHost() : null;
		
		return dispatcherDbManager.updateEndStatus(oldJobStatus, adminDbServer, vcSimulationIdentifier, jobIndex, host, SimulationJobStatus.SCHEDULERSTATUS_COMPLETED, simulationMessage);		
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
private SimulationJobStatus updateDispatchedJobStatus(SimulationJobStatus oldJobStatus, VCSimulationIdentifier vcSimulationIdentifier, int jobIndex) throws RemoteException, DataAccessException, UpdateSynchronizationException {
	SolverController solverController = solverControllerHash.get(SimulationJob.createSimulationJobID(Simulation.createSimulationID(vcSimulationIdentifier.getSimulationKey()), jobIndex));
	if (solverController == null) {
		return null;
	}

	synchronized (solverController) {	
		String host = (solverController != null) ? solverController.getHost() : null;
		
		return dispatcherDbManager.updateDispatchedStatus(oldJobStatus, adminDbServer, host, vcSimulationIdentifier, jobIndex, SimulationMessage.MESSAGE_JOB_DISPATCHED);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/5/2003 11:50:34 AM)
 * @param sim cbit.vcell.solver.Simulation
 * @param jobStatus cbit.vcell.messaging.db.SimulationJobStatus
 */
private SimulationJobStatus updateFailedJobStatus(SimulationJobStatus oldJobStatus, VCSimulationIdentifier vcSimulationIdentifier, int jobIndex, SimulationMessage solverMsg) throws DataAccessException, RemoteException {
	SolverController solverController = solverControllerHash.get(SimulationJob.createSimulationJobID(Simulation.createSimulationID(vcSimulationIdentifier.getSimulationKey()), jobIndex));
	if (solverController == null) {
		return null;
	}
	
	synchronized (solverController) {		
		String host = (solverController != null) ? solverController.getHost() : null;
		
		return dispatcherDbManager.updateEndStatus(oldJobStatus, adminDbServer, vcSimulationIdentifier, jobIndex, host, SimulationJobStatus.SCHEDULERSTATUS_FAILED, solverMsg);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/5/2003 11:50:34 AM)
 * @param sim cbit.vcell.solver.Simulation
 * @param jobStatus cbit.vcell.messaging.db.SimulationJobStatus
 */
private SimulationJobStatus updateRunningJobStatus(SimulationJobStatus oldJobStatus, VCSimulationIdentifier vcSimulationIdentifier, int jobIndex, boolean hasData, SimulationMessage solverMsg) throws DataAccessException, RemoteException {
	SolverController solverController = solverControllerHash.get(SimulationJob.createSimulationJobID(Simulation.createSimulationID(vcSimulationIdentifier.getSimulationKey()), jobIndex));
	if (solverController == null) {
		return null;
	}

	synchronized (solverController) {
		String host = (solverController != null) ? solverController.getHost() : null;
		
		return dispatcherDbManager.updateRunningStatus(oldJobStatus, adminDbServer, host, vcSimulationIdentifier, jobIndex, hasData, solverMsg);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/5/2003 11:50:34 AM)
 * @param sim cbit.vcell.solver.Simulation
 * @param jobStatus cbit.vcell.messaging.db.SimulationJobStatus
 */
private SimulationJobStatus updateStoppedJobStatus(SimulationJobStatus oldJobStatus, VCSimulationIdentifier vcSimID, int jobIndex) throws DataAccessException, RemoteException {	
	SolverController solverController = solverControllerHash.get(SimulationJob.createSimulationJobID(Simulation.createSimulationID(vcSimID.getSimulationKey()), jobIndex));
	if (solverController != null) {
		synchronized (solverController) {
			return dispatcherDbManager.updateEndStatus(oldJobStatus, adminDbServer, vcSimID, jobIndex, null, SimulationJobStatus.SCHEDULERSTATUS_STOPPED, SimulationMessage.MESSAGE_JOB_STOPPED);
		}
	} else {
		return dispatcherDbManager.updateEndStatus(oldJobStatus, adminDbServer, vcSimID, jobIndex, null, SimulationJobStatus.SCHEDULERSTATUS_STOPPED, SimulationMessage.MESSAGE_JOB_STOPPED);
	}	
}
}
