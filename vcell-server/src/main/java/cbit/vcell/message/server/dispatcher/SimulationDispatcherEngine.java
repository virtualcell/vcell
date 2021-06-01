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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import cbit.rmi.event.WorkerEvent;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.messages.StatusMessage;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.server.UpdateSynchronizationException;
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
	public static final Logger lg = LogManager.getLogger(SimulationDispatcherEngine.class);

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

	public void onDispatch(Simulation simulation, SimulationJobStatus simJobStatus, SimulationDatabase simulationDatabase, VCMessageSession dispatcherQueueSession) throws VCMessagingException, DataAccessException, SQLException{
		KeyValue simulationKey = simJobStatus.getVCSimulationIdentifier().getSimulationKey();
		SimulationStateMachine simStateMachine = getSimulationStateMachine(simulationKey, simJobStatus.getJobIndex());
		
		simStateMachine.onDispatch(simulation, simJobStatus, simulationDatabase, dispatcherQueueSession);
	}
	/**
	 * @param vcMessage
	 * @param session
	 * @throws VCMessagingException 
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	public void onStartRequest(VCSimulationIdentifier vcSimID, User user, int simulationScanCount, SimulationDatabase simulationDatabase, VCMessageSession session, VCMessageSession dispatcherQueueSession) throws VCMessagingException, DataAccessException, SQLException {
		KeyValue simKey = vcSimID.getSimulationKey();

		boolean isAdmin = false;
		User myUser = simulationDatabase.getUser(user.getName());
		if(myUser instanceof User.SpecialUser) {
			//'special0' assigned to users who are VCell project admins
			isAdmin = Arrays.asList(((User.SpecialUser)myUser).getMySpecials()).contains(User.SPECIALS.special1);
		}

		SimulationInfo simulationInfo = null;
		try {
			simulationInfo = simulationDatabase.getSimulationInfo(user, simKey);
		} catch (DataAccessException ex) {
			if (lg.isWarnEnabled()) lg.warn("Bad simulation " + vcSimID);
			StatusMessage message = new StatusMessage(new SimulationJobStatus(VCellServerID.getSystemServerID(), vcSimID, -1, null, 
					SchedulerStatus.FAILED, 0, SimulationMessage.workerFailure("Failed to dispatch simulation: "+ ex.getMessage()), null, null), user.getName(), null, null);
			message.sendToClient(session);
			return;
		}
		if (simulationInfo == null) {
			if (lg.isWarnEnabled()) lg.warn("Can't start, simulation [" + vcSimID + "] doesn't exist in database");
			StatusMessage message = new StatusMessage(new SimulationJobStatus(VCellServerID.getSystemServerID(), vcSimID, -1, null, 
					SchedulerStatus.FAILED, 0, SimulationMessage.workerFailure("Can't start, simulation [" + vcSimID + "] doesn't exist"), null, null), user.getName(), null, null);
			message.sendToClient(session);
			return;
		}

		if (!isAdmin && simulationScanCount > Integer.parseInt(cbit.vcell.resource.PropertyLoader.getRequiredProperty(cbit.vcell.resource.PropertyLoader.maxJobsPerScan))) {
			if (lg.isWarnEnabled()) lg.warn("Too many simulations (" + simulationScanCount + ") for parameter scan." + vcSimID);
			StatusMessage message = new StatusMessage(new SimulationJobStatus(VCellServerID.getSystemServerID(), vcSimID, -1, null, 
					SchedulerStatus.FAILED, 0, SimulationMessage.workerFailure("Too many simulations (" + simulationScanCount + ") for parameter scan."), null, null), user.getName(), null, null);
			message.sendToClient(session);
			return;
		}

		for (int jobIndex = 0; jobIndex < simulationScanCount; jobIndex++){
			SimulationStateMachine simStateMachine = getSimulationStateMachine(simKey, jobIndex);
			try {
				simStateMachine.onStartRequest(user, vcSimID, simulationDatabase, session);
			}catch (UpdateSynchronizationException e){
				simStateMachine.onStartRequest(user, vcSimID, simulationDatabase, session);
			}
		}
	}

	
	public void onStopRequest(VCSimulationIdentifier vcSimID, User user, SimulationDatabase simulationDatabase, VCMessageSession session) throws DataAccessException, VCMessagingException, SQLException {
		KeyValue simKey = vcSimID.getSimulationKey();

		SimulationJobStatus[] allActiveSimJobStatusArray = simulationDatabase.getActiveJobs(VCellServerID.getSystemServerID());
		ArrayList<SimulationJobStatus> simJobStatusArray = new ArrayList<SimulationJobStatus>();
		for (SimulationJobStatus activeSimJobStatus : allActiveSimJobStatusArray){
			if (activeSimJobStatus.getVCSimulationIdentifier().getSimulationKey().equals(vcSimID.getSimulationKey())){
				simJobStatusArray.add(activeSimJobStatus);
			}
		}
		for (SimulationJobStatus simJobStatus : simJobStatusArray){
			SimulationStateMachine simStateMachine = getSimulationStateMachine(simKey, simJobStatus.getJobIndex());
			try {
				simStateMachine.onStopRequest(user, simJobStatus, simulationDatabase, session);
			}catch (UpdateSynchronizationException e){
				simStateMachine.onStopRequest(user, simJobStatus, simulationDatabase, session);
			}
		}
	}

	

	/**
	 * @param vcMessage
	 * @param session
	 */
	public void onWorkerEvent(WorkerEvent workerEvent, SimulationDatabase simulationDatabase, VCMessageSession session) {
		try {
			KeyValue simKey = workerEvent.getVCSimulationDataIdentifier().getSimulationKey();
			int jobIndex = workerEvent.getJobIndex();
			SimulationStateMachine simStateMachine = getSimulationStateMachine(simKey, jobIndex);
			simStateMachine.onWorkerEvent(workerEvent, simulationDatabase, session);
		} catch (Exception ex) {
			lg.error(ex.getMessage(),ex);
		}
	}


	public void onSystemAbort(SimulationJobStatus jobStatus, String failureMessage, SimulationDatabase simulationDatabase, VCMessageSession session) {
		try {
			KeyValue simKey = jobStatus.getVCSimulationIdentifier().getSimulationKey();
			int jobIndex = jobStatus.getJobIndex();
			SimulationStateMachine simStateMachine = getSimulationStateMachine(simKey, jobIndex);
			simStateMachine.onSystemAbort(jobStatus, failureMessage, simulationDatabase, session);
		} catch (Exception ex) {
			lg.error(ex.getMessage(),ex);
		}
	}

}
