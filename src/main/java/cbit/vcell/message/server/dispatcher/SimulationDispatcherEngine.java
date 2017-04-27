/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.dispatcher;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import cbit.rmi.event.WorkerEvent;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.messages.StatusMessage;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.UpdateSynchronizationException;
import cbit.vcell.server.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:31:11 PM)
 * @author: Jim Schaff
 */
public class SimulationDispatcherEngine {

	private HashMap<KeyValue,List<SimulationStateMachine>> simStateMachineHash = new HashMap<KeyValue, List<SimulationStateMachine>>();

	/**
	 * Scheduler constructor comment.
	 */
	public SimulationDispatcherEngine() {
	}
	
	/**
	 * reset simulation state time stamps in case of transient error in getting running status
	 */
	void resetTimeStamps( ) {
		long now = System.currentTimeMillis();
		for (List<SimulationStateMachine> lst : simStateMachineHash.values()) {
			for (SimulationStateMachine ssm: lst) {
				ssm.setSolverProcessTimestamp(now);
			}
		}
	}

	public SimulationStateMachine getSimulationStateMachine(KeyValue simulationKey, int jobIndex) {
		List<SimulationStateMachine> stateMachineList = simStateMachineHash.get(simulationKey);
		if (stateMachineList==null){
			stateMachineList = new ArrayList<SimulationStateMachine>();
			simStateMachineHash.put(simulationKey,stateMachineList);
		}
		for (SimulationStateMachine stateMachine : stateMachineList){
			if (stateMachine.getJobIndex() == jobIndex){
				return stateMachine;
			}
		}
		SimulationStateMachine newStateMachine = new SimulationStateMachine(simulationKey, jobIndex);
		stateMachineList.add(newStateMachine);
		return newStateMachine;
	}

	public void onDispatch(Simulation simulation, SimulationJobStatus simJobStatus, SimulationDatabase simulationDatabase, VCMessageSession dispatcherQueueSession, SessionLog log) throws VCMessagingException, DataAccessException, SQLException{
		KeyValue simulationKey = simJobStatus.getVCSimulationIdentifier().getSimulationKey();
		SimulationStateMachine simStateMachine = getSimulationStateMachine(simulationKey, simJobStatus.getJobIndex());
		
		simStateMachine.onDispatch(simulation, simJobStatus, simulationDatabase, dispatcherQueueSession, log);
	}
	/**
	 * @param vcMessage
	 * @param session
	 * @throws VCMessagingException 
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	public void onStartRequest(VCSimulationIdentifier vcSimID, User user, int simulationScanCount, SimulationDatabase simulationDatabase, VCMessageSession session, VCMessageSession dispatcherQueueSession, SessionLog log) throws VCMessagingException, DataAccessException, SQLException {
		KeyValue simKey = vcSimID.getSimulationKey();

		SimulationInfo simulationInfo = null;
		try {
			simulationInfo = simulationDatabase.getSimulationInfo(user, simKey);
		} catch (DataAccessException ex) {
			log.alert("Bad simulation " + vcSimID);
			StatusMessage message = new StatusMessage(new SimulationJobStatus(VCellServerID.getSystemServerID(), vcSimID, -1, null, 
					SchedulerStatus.FAILED, 0, SimulationMessage.workerFailure("Failed to dispatch simulation: "+ ex.getMessage()), null, null), user.getName(), null, null);
			message.sendToClient(session);
			return;
		}
		if (simulationInfo == null) {
			log.alert("Can't start, simulation [" + vcSimID + "] doesn't exist in database");
			StatusMessage message = new StatusMessage(new SimulationJobStatus(VCellServerID.getSystemServerID(), vcSimID, -1, null, 
					SchedulerStatus.FAILED, 0, SimulationMessage.workerFailure("Can't start, simulation [" + vcSimID + "] doesn't exist"), null, null), user.getName(), null, null);
			message.sendToClient(session);
			return;
		}

		if (simulationScanCount > Integer.parseInt(org.vcell.util.PropertyLoader.getRequiredProperty(org.vcell.util.PropertyLoader.maxJobsPerScan))) {
			log.alert("Too many simulations (" + simulationScanCount + ") for parameter scan." + vcSimID);
			StatusMessage message = new StatusMessage(new SimulationJobStatus(VCellServerID.getSystemServerID(), vcSimID, -1, null, 
					SchedulerStatus.FAILED, 0, SimulationMessage.workerFailure("Too many simulations (" + simulationScanCount + ") for parameter scan."), null, null), user.getName(), null, null);
			message.sendToClient(session);
			return;
		}

		for (int jobIndex = 0; jobIndex < simulationScanCount; jobIndex++){
			SimulationStateMachine simStateMachine = getSimulationStateMachine(simKey, jobIndex);
			try {
				simStateMachine.onStartRequest(user, vcSimID, simulationDatabase, session, log);
			}catch (UpdateSynchronizationException e){
				simStateMachine.onStartRequest(user, vcSimID, simulationDatabase, session, log);
			}
		}
	}

	
	public void onStopRequest(VCSimulationIdentifier vcSimID, User user, SimulationDatabase simulationDatabase, VCMessageSession session, SessionLog log) throws DataAccessException, VCMessagingException, SQLException {
		KeyValue simKey = vcSimID.getSimulationKey();

		SimulationJobStatus[] allActiveSimJobStatusArray = simulationDatabase.getActiveJobs();
		ArrayList<SimulationJobStatus> simJobStatusArray = new ArrayList<SimulationJobStatus>();
		for (SimulationJobStatus activeSimJobStatus : allActiveSimJobStatusArray){
			if (activeSimJobStatus.getVCSimulationIdentifier().getSimulationKey().equals(vcSimID.getSimulationKey())){
				simJobStatusArray.add(activeSimJobStatus);
			}
		}
		for (SimulationJobStatus simJobStatus : simJobStatusArray){
			SimulationStateMachine simStateMachine = getSimulationStateMachine(simKey, simJobStatus.getJobIndex());
			try {
				simStateMachine.onStopRequest(user, simJobStatus, simulationDatabase, session, log);
			}catch (UpdateSynchronizationException e){
				simStateMachine.onStopRequest(user, simJobStatus, simulationDatabase, session, log);
			}
		}
	}

	

	/**
	 * @param vcMessage
	 * @param session
	 */
	public void onWorkerEvent(WorkerEvent workerEvent, SimulationDatabase simulationDatabase, VCMessageSession session, SessionLog log) {
		try {
			KeyValue simKey = workerEvent.getVCSimulationDataIdentifier().getSimulationKey();
			int jobIndex = workerEvent.getJobIndex();
			SimulationStateMachine simStateMachine = getSimulationStateMachine(simKey, jobIndex);
			simStateMachine.onWorkerEvent(workerEvent, simulationDatabase, session, log);
		} catch (Exception ex) {
			log.exception(ex);
		}
	}


	public void onSystemAbort(SimulationJobStatus jobStatus, String failureMessage, SimulationDatabase simulationDatabase, VCMessageSession session, SessionLog log) {
		try {
			KeyValue simKey = jobStatus.getVCSimulationIdentifier().getSimulationKey();
			int jobIndex = jobStatus.getJobIndex();
			SimulationStateMachine simStateMachine = getSimulationStateMachine(simKey, jobIndex);
			simStateMachine.onSystemAbort(jobStatus, failureMessage, simulationDatabase, session, log);
		} catch (Exception ex) {
			log.exception(ex);
		}
	}

}
