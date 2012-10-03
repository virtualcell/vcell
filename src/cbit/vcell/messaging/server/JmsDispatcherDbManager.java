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
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.server.AdminDatabaseServerXA;
import cbit.vcell.messaging.db.UpdateSynchronizationException;
import cbit.vcell.messaging.db.SimulationQueueEntryStatus;
import cbit.vcell.messaging.db.SimulationJobStatus.SchedulerStatus;

import java.sql.Connection;
import java.util.Date;
import cbit.vcell.solver.SimulationMessage;

import org.vcell.util.DataAccessException;
import org.vcell.util.MessageConstants;
import org.vcell.util.MessageConstants.SimulationQueueID;
import org.vcell.util.document.VCellServerID;


/**
 * Insert the type's description here.
 * Creation date: (2/20/2004 3:45:15 PM)
 * @author: Fei Gao
 */
public class JmsDispatcherDbManager extends AbstractDispatcherDbManager implements MessagingDispatcherDbManager {
/**
 * JmsSimulationDispatcherDbDriver constructor comment.
 */
public JmsDispatcherDbManager() {
	super();
}


/**
 * updateDispatchedStatus method comment.
 */
public SimulationJobStatus updateDispatchedStatus(SimulationJobStatus oldJobStatus, AdminDatabaseServerXA adminDbXA, Connection con, 
		String computeHost, VCSimulationIdentifier vcSimID, int jobIndex, int taskID, SimulationMessage startMsg) 
			throws DataAccessException, UpdateSynchronizationException {

	if (oldJobStatus != null && !oldJobStatus.getSchedulerStatus().isDone()) {
		
		SimulationJobStatus newJobStatus = getNewStatus_updateDispatchedStatus(oldJobStatus, computeHost, vcSimID, jobIndex, taskID, startMsg);

		newJobStatus = adminDbXA.updateSimulationJobStatus(con, oldJobStatus, newJobStatus);

		return newJobStatus;
	}

	return oldJobStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
public SimulationJobStatus updateEndStatus(SimulationJobStatus oldJobStatus, AdminDatabaseServerXA adminDbXA, Connection con, 
		VCSimulationIdentifier vcSimID, int jobIndex, int taskID, String hostName, SchedulerStatus status, SimulationMessage solverMsg) 
			throws DataAccessException, UpdateSynchronizationException {
	if (oldJobStatus == null ||  oldJobStatus != null && !oldJobStatus.getSchedulerStatus().isDone()) {		

		SimulationJobStatus newJobStatus = getNewStatus_updateEndStatus(oldJobStatus, vcSimID, jobIndex, taskID, hostName, status, solverMsg);

		if (oldJobStatus == null) {
			newJobStatus = adminDbXA.insertSimulationJobStatus(con, newJobStatus);
		} else {
			newJobStatus = adminDbXA.updateSimulationJobStatus(con, oldJobStatus, newJobStatus);
		}

		return newJobStatus;
	}

	return oldJobStatus;
}


/**
 * updateLatestUpdateDate method comment.
 */
public void updateLatestUpdateDate(SimulationJobStatus oldJobStatus, AdminDatabaseServerXA adminDbXA, Connection con, 
		VCSimulationIdentifier vcSimID, int jobIndex, int taskID, SimulationMessage simulationMessage) throws DataAccessException, UpdateSynchronizationException {

	if (oldJobStatus != null && !oldJobStatus.getSchedulerStatus().isDone()) {

		SimulationJobStatus	newJobStatus = getNewStatus_updateLatestUpdateDate(oldJobStatus, vcSimID, jobIndex, taskID, simulationMessage);
		
		if (newJobStatus != null) {
			adminDbXA.updateSimulationJobStatus(con, oldJobStatus, newJobStatus);
		}
	}

}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
public SimulationJobStatus updateQueueStatus(SimulationJobStatus oldJobStatus, AdminDatabaseServerXA adminDb, Connection con, 
		VCSimulationIdentifier vcSimID, int jobIndex, int taskID, SimulationQueueID queueID, boolean firstSubmit) 
			throws DataAccessException, UpdateSynchronizationException {
	if (oldJobStatus == null || oldJobStatus.getSchedulerStatus().isDone() || oldJobStatus.getSchedulerStatus().isWaiting()) {	
		// no job for the same simulation running						
		Date submitDate = firstSubmit ? null : oldJobStatus.getSubmitDate();
		SchedulerStatus schedulerStatus = SchedulerStatus.WAITING;
		SimulationMessage simulationMessage = SimulationMessage.MESSAGE_JOB_WAITING;
		if (queueID == SimulationQueueID.QUEUE_ID_SIMULATIONJOB) {
			schedulerStatus = SchedulerStatus.QUEUED;
			simulationMessage = SimulationMessage.MESSAGE_JOB_QUEUED;
		}

		// update the job status in the database and local memory
		SimulationJobStatus newJobStatus = new SimulationJobStatus(VCellServerID.getSystemServerID(), vcSimID, jobIndex, submitDate, schedulerStatus, taskID, 
				simulationMessage,	new SimulationQueueEntryStatus(null, MessageConstants.PRIORITY_DEFAULT, queueID), null);
		
		if (oldJobStatus == null) {
			newJobStatus = adminDb.insertSimulationJobStatus(con, newJobStatus);
		} else {
			newJobStatus = adminDb.updateSimulationJobStatus(con, oldJobStatus, newJobStatus);
		}

		return newJobStatus;
	}

	return oldJobStatus;
		
}


/**
 * updateRunningStatus method comment.
 */
public SimulationJobStatus updateRunningStatus(SimulationJobStatus oldJobStatus, AdminDatabaseServerXA adminDbXA, Connection con, String hostName, 
		VCSimulationIdentifier vcSimID, int jobIndex, int taskID, boolean hasData, SimulationMessage solverMsg) throws DataAccessException, UpdateSynchronizationException {
	if (oldJobStatus != null && !oldJobStatus.getSchedulerStatus().isDone()) {

		SimulationJobStatus newJobStatus = getNewStatus_updateRunningStatus(oldJobStatus, hostName, vcSimID, jobIndex, hasData, solverMsg);

		if (oldJobStatus == newJobStatus) { // running statuses, don't always store into the database		
			updateLatestUpdateDate(oldJobStatus, adminDbXA, con, vcSimID, jobIndex, taskID, solverMsg);
			return oldJobStatus;
		} else {
			newJobStatus = adminDbXA.updateSimulationJobStatus(con, oldJobStatus, newJobStatus);
			return newJobStatus;
		}
	}

	return oldJobStatus;
}
}
