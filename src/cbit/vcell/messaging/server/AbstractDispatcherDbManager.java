/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.messaging.server;
import cbit.htc.PbsJobID;
import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.messaging.db.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.messaging.db.UpdateSynchronizationException;
import cbit.vcell.server.AdminDatabaseServer;
import cbit.vcell.solver.SimulationMessage;
import cbit.vcell.messaging.db.SimulationQueueEntryStatus;
import cbit.vcell.messaging.db.SimulationExecutionStatus;

import java.sql.SQLException;
import java.util.Date;

import org.vcell.util.DataAccessException;
import org.vcell.util.MessageConstants;
import org.vcell.util.MessageConstants.SimulationQueueID;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.solver.VCSimulationIdentifier;

/**
 * Insert the type's description here.
 * Creation date: (2/16/2004 10:52:07 AM)
 * @author: Fei Gao
 */
public abstract class AbstractDispatcherDbManager implements DispatcherDbManager {
/**
 * SimulationDispatcherHelper constructor comment.
 */
public AbstractDispatcherDbManager() {
	super();
}


/**
 * updateDispatchedStatus method comment.
 */
SimulationJobStatus getNewStatus_updateDispatchedStatus(SimulationJobStatus oldJobStatus, String computeHost, VCSimulationIdentifier vcSimID, 
		int jobIndex, int taskID, SimulationMessage startMsg) throws DataAccessException, UpdateSynchronizationException {

	// new queue status
	SimulationQueueEntryStatus oldQueueStatus = oldJobStatus.getSimulationQueueEntryStatus();
	SimulationQueueEntryStatus newQueueStatus = new SimulationQueueEntryStatus(oldQueueStatus.getQueueDate(), 
		oldQueueStatus.getQueuePriority(), SimulationQueueID.QUEUE_ID_NULL);
	
	// new exe status
	SimulationExecutionStatus newExeStatus = new SimulationExecutionStatus(null, computeHost, null,	null, false, startMsg.getPbsJobId());

	// new job status
	SimulationJobStatus newJobStatus = new SimulationJobStatus(oldJobStatus.getServerID(), vcSimID, jobIndex, oldJobStatus.getSubmitDate(), SchedulerStatus.DISPATCHED,
			taskID, startMsg, newQueueStatus, newExeStatus);

	return newJobStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
SimulationJobStatus getNewStatus_updateEndStatus(SimulationJobStatus oldJobStatus, VCSimulationIdentifier vcSimID, int jobIndex, int taskID,
		String hostName, SchedulerStatus status, SimulationMessage solverMsg) throws DataAccessException, UpdateSynchronizationException {

	// new queue status
	SimulationQueueEntryStatus oldQueueStatus = oldJobStatus == null ? null : oldJobStatus.getSimulationQueueEntryStatus();
	SimulationQueueEntryStatus newQueueStatus = oldQueueStatus;
	if (oldQueueStatus != null && oldQueueStatus.getQueueID() != SimulationQueueID.QUEUE_ID_NULL) {		
		newQueueStatus = new SimulationQueueEntryStatus(oldQueueStatus.getQueueDate(), oldQueueStatus.getQueuePriority(), SimulationQueueID.QUEUE_ID_NULL);
	}

	// new exe status
	SimulationExecutionStatus oldExeStatus = oldJobStatus == null ? null : oldJobStatus.getSimulationExecutionStatus();
	SimulationExecutionStatus newExeStatus = null;
	boolean hasData = false;
	
	if (oldExeStatus == null) {
		if (status == SchedulerStatus.COMPLETED) {
			hasData = true;
		}
		newExeStatus = new SimulationExecutionStatus(null, hostName, null, null, hasData, solverMsg.getPbsJobId());				
	} else {
		if (status == SchedulerStatus.COMPLETED) {
			hasData = true;
		} else {
			hasData = oldExeStatus.hasData();
		}
		PbsJobID pbsJobID = oldExeStatus.getPbsJobID();
		if (solverMsg.getPbsJobId()!=null){
			pbsJobID = solverMsg.getPbsJobId();
		}
		newExeStatus = new SimulationExecutionStatus(oldExeStatus.getStartDate(), (hostName != null) ? hostName : oldExeStatus.getComputeHost(), null, null, hasData, pbsJobID);
	}

	// new job status
	SimulationJobStatus newJobStatus = new SimulationJobStatus(VCellServerID.getSystemServerID(), vcSimID, jobIndex, 
		oldJobStatus == null ? null : oldJobStatus.getSubmitDate(), status, taskID, solverMsg,
		newQueueStatus, newExeStatus);
	
	return newJobStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
SimulationJobStatus getNewStatus_updateLatestUpdateDate(SimulationJobStatus oldJobStatus, VCSimulationIdentifier vcSimID, 
		int jobIndex, int taskID, SimulationMessage simulationMessage) throws DataAccessException, UpdateSynchronizationException {

	SimulationExecutionStatus oldExeStatus = oldJobStatus.getSimulationExecutionStatus();
	if (oldExeStatus == null) {
		return null;
	}
	
	Date latestUpdate = oldExeStatus.getLatestUpdateDate();
	Date sysDate = oldJobStatus.getTimeDateStamp();
	if (sysDate.getTime() - latestUpdate.getTime() < MessageConstants.INTERVAL_PING_SERVER * 3 / 5) {
		return null;
	}
	
	// new exe status
	PbsJobID pbsJobID = oldExeStatus.getPbsJobID();
	if (simulationMessage.getPbsJobId()!=null){
		pbsJobID = simulationMessage.getPbsJobId();
	}
	SimulationExecutionStatus newExeStatus = new SimulationExecutionStatus(oldExeStatus.getStartDate(), oldExeStatus.getComputeHost(), null, 
			oldExeStatus.getEndDate(), oldExeStatus.hasData(), pbsJobID);
	
	SimulationJobStatus newJobStatus = new SimulationJobStatus(oldJobStatus.getServerID(), vcSimID, jobIndex, oldJobStatus.getSubmitDate(), 
		oldJobStatus.getSchedulerStatus(),	oldJobStatus.getTaskID(), simulationMessage, oldJobStatus.getSimulationQueueEntryStatus(), newExeStatus);

	return newJobStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
SimulationJobStatus getNewStatus_updateRunningStatus(SimulationJobStatus oldJobStatus, String hostName, VCSimulationIdentifier vcSimID, 
		int jobIndex, boolean hasData, SimulationMessage solverMsg)	throws DataAccessException, UpdateSynchronizationException {

	// new queue status		
	SimulationQueueEntryStatus oldQueueStatus = oldJobStatus.getSimulationQueueEntryStatus();
	SimulationQueueEntryStatus newQueueStatus = oldQueueStatus;
	if (oldQueueStatus.getQueueID() != SimulationQueueID.QUEUE_ID_NULL) {
		newQueueStatus = new SimulationQueueEntryStatus(oldQueueStatus.getQueueDate(), oldQueueStatus.getQueuePriority(), SimulationQueueID.QUEUE_ID_NULL);
	}

	// new exe status
	SimulationExecutionStatus oldExeStatus = oldJobStatus.getSimulationExecutionStatus();
	SimulationExecutionStatus newExeStatus = null;
	if (oldExeStatus == null) {
		newExeStatus = new SimulationExecutionStatus(null, hostName, null, null, hasData, solverMsg.getPbsJobId());
	} else if (!oldJobStatus.getSchedulerStatus().isRunning() || !oldExeStatus.hasData() && hasData) {
		PbsJobID pbsJobID = oldExeStatus.getPbsJobID();
		if (solverMsg.getPbsJobId()!=null){
			pbsJobID = solverMsg.getPbsJobId();
		}
		newExeStatus = new SimulationExecutionStatus(oldExeStatus.getStartDate(), (hostName != null) ? hostName : oldExeStatus.getComputeHost(), null, null, hasData, pbsJobID);		
	} else {
		return oldJobStatus;
	}
	
	// new job status
	SimulationJobStatus newJobStatus = new SimulationJobStatus(oldJobStatus.getServerID(), vcSimID, jobIndex, oldJobStatus.getSubmitDate(), 
			SchedulerStatus.RUNNING, oldJobStatus.getTaskID(), solverMsg, newQueueStatus, newExeStatus);

	return newJobStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
public SimulationJobStatus[] getSimulationJobStatusArray(SimulationDatabase simDb, KeyValue simKey, int jobIndex) throws DataAccessException {
	try {		
		return simDb.getSimulationJobStatusArray(simKey, jobIndex);
	} catch (SQLException ex) {
		throw new DataAccessException("updateDispatchedStatus " + ex.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
public SimulationJobStatus updateDispatchedStatus(SimulationJobStatus oldJobStatus,	SimulationDatabase simDb, String computeHost, 
		VCSimulationIdentifier vcSimID, int jobIndex, int taskID, SimulationMessage startMsg) throws DataAccessException, UpdateSynchronizationException {
	try {

		if (oldJobStatus != null && !oldJobStatus.getSchedulerStatus().isDone()) {
			
			SimulationJobStatus newJobStatus = getNewStatus_updateDispatchedStatus(oldJobStatus, computeHost, vcSimID, jobIndex, taskID, startMsg);

			newJobStatus = simDb.updateSimulationJobStatus(oldJobStatus, newJobStatus);

			return newJobStatus;
		}

		return oldJobStatus;
		
	} catch (SQLException ex) {
		throw new DataAccessException("updateEndStatus " + ex.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
public SimulationJobStatus updateEndStatus(SimulationJobStatus oldJobStatus, SimulationDatabase simDb, VCSimulationIdentifier vcSimID, 
		int jobIndex, int taskID, String hostName, SchedulerStatus status, SimulationMessage solverMsg) throws DataAccessException, UpdateSynchronizationException {
	try {
		if (oldJobStatus != null && !oldJobStatus.getSchedulerStatus().isDone()) {		

			SimulationJobStatus newJobStatus = getNewStatus_updateEndStatus(oldJobStatus, vcSimID, jobIndex, taskID, hostName, status, solverMsg);
			
			newJobStatus = simDb.updateSimulationJobStatus(oldJobStatus, newJobStatus);

			return newJobStatus;
		}

		return oldJobStatus;
	} catch (SQLException ex) {
		throw new DataAccessException("updateEndStatus " + ex.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
public void updateLatestUpdateDate(SimulationJobStatus oldJobStatus, SimulationDatabase simDb, VCSimulationIdentifier vcSimID, 
		int jobIndex, int taskID, SimulationMessage simulationMessage) throws DataAccessException, UpdateSynchronizationException {
	try {
		if (oldJobStatus != null && !oldJobStatus.getSchedulerStatus().isDone()) {

			SimulationJobStatus	newJobStatus = getNewStatus_updateLatestUpdateDate(oldJobStatus, vcSimID, jobIndex, taskID, simulationMessage);
			
			if (newJobStatus != null) {
				simDb.updateSimulationJobStatus(oldJobStatus, newJobStatus);
			}
		}
	} catch (SQLException ex) {
		throw new DataAccessException("updateLatestUpdateDate " + ex.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
public SimulationJobStatus updateRunningStatus(SimulationJobStatus oldJobStatus, SimulationDatabase simDb, String hostName, 
		VCSimulationIdentifier vcSimID, int jobIndex, int taskID, boolean hasData, SimulationMessage solverMsg)	throws DataAccessException, UpdateSynchronizationException {
	try {
		if (oldJobStatus != null && !oldJobStatus.getSchedulerStatus().isDone()) {

			SimulationJobStatus newJobStatus = getNewStatus_updateRunningStatus(oldJobStatus, hostName, vcSimID, jobIndex, hasData, solverMsg);
			if (oldJobStatus == newJobStatus) { // running statuses, don't always store into the database				
				updateLatestUpdateDate(oldJobStatus, simDb, vcSimID, jobIndex, taskID, solverMsg);
				return oldJobStatus;
			} else {
				newJobStatus = simDb.updateSimulationJobStatus(oldJobStatus, newJobStatus);
				return newJobStatus;
			}
		}

		return oldJobStatus;
	} catch (SQLException ex) {
		throw new DataAccessException("updateRunningStatus " + ex.getMessage());
	}		
}
}
