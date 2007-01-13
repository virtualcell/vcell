package cbit.vcell.messaging.server;
import java.util.Date;

import cbit.rmi.event.SimulationJobStatus;
import cbit.rmi.event.SimulationQueueEntryStatus;
import cbit.rmi.event.VCellServerID;
import cbit.util.DataAccessException;
import cbit.util.MessageConstants;
import cbit.vcell.messaging.db.UpdateSynchronizationException;
import cbit.vcell.modeldb.AdminDatabaseServerXA;
import cbit.vcell.simulation.VCSimulationIdentifier;

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
public cbit.rmi.event.SimulationJobStatus updateDispatchedStatus(cbit.rmi.event.SimulationJobStatus oldJobStatus, cbit.vcell.modeldb.AdminDatabaseServerXA adminDbXA, java.sql.Connection con, java.lang.String computeHost, cbit.vcell.simulation.VCSimulationIdentifier vcSimID, int jobIndex, java.lang.String startMsg) throws DataAccessException, cbit.vcell.messaging.db.UpdateSynchronizationException {

	if (oldJobStatus != null && !oldJobStatus.isDone()) {
		
		SimulationJobStatus newJobStatus = getNewStatus_updateDispatchedStatus(oldJobStatus, computeHost, vcSimID, jobIndex, startMsg);

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
public SimulationJobStatus updateEndStatus(SimulationJobStatus oldJobStatus, AdminDatabaseServerXA adminDbXA, java.sql.Connection con, VCSimulationIdentifier vcSimID, int jobIndex, String hostName, int status, String solverMsg) throws DataAccessException, UpdateSynchronizationException {
	if (oldJobStatus == null ||  oldJobStatus != null && !oldJobStatus.isDone()) {		

		SimulationJobStatus newJobStatus = getNewStatus_updateEndStatus(oldJobStatus, vcSimID, jobIndex, hostName, status, solverMsg);
		

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
public void updateLatestUpdateDate(cbit.rmi.event.SimulationJobStatus oldJobStatus, cbit.vcell.modeldb.AdminDatabaseServerXA adminDbXA, java.sql.Connection con, cbit.vcell.simulation.VCSimulationIdentifier vcSimID, int jobIndex) throws DataAccessException, cbit.vcell.messaging.db.UpdateSynchronizationException {

	if (oldJobStatus != null && !oldJobStatus.isDone()) {

		SimulationJobStatus	newJobStatus = getNewStatus_updateLatestUpdateDate(oldJobStatus, vcSimID, jobIndex);
		
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
public SimulationJobStatus updateQueueStatus(SimulationJobStatus oldJobStatus, AdminDatabaseServerXA adminDb, java.sql.Connection con, VCSimulationIdentifier vcSimID, int jobIndex, int queueID, int taskID, boolean firstSubmit) throws DataAccessException, UpdateSynchronizationException {
	if (oldJobStatus == null || oldJobStatus.isDone() || oldJobStatus.isWaiting()) {	
		// no job for the same simulation running						
		Date submitDate = firstSubmit ? null : oldJobStatus.getSubmitDate();	
		int schedulerStatus = (queueID == MessageConstants.QUEUE_ID_SIMULATIONJOB) ? SimulationJobStatus.SCHEDULERSTATUS_QUEUED : SimulationJobStatus.SCHEDULERSTATUS_WAITING;

		// update the job status in the database and local memory
		SimulationJobStatus newJobStatus = new SimulationJobStatus(VCellServerID.getSystemServerID(), vcSimID, jobIndex, submitDate, schedulerStatus, taskID, null, 
			new SimulationQueueEntryStatus(null, MessageConstants.PRIORITY_DEFAULT, queueID), null);
		
		
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
public cbit.rmi.event.SimulationJobStatus updateRunningStatus(cbit.rmi.event.SimulationJobStatus oldJobStatus, cbit.vcell.modeldb.AdminDatabaseServerXA adminDbXA, java.sql.Connection con, java.lang.String hostName, cbit.vcell.simulation.VCSimulationIdentifier vcSimID, int jobIndex, boolean hasData, java.lang.String solverMsg) throws DataAccessException, cbit.vcell.messaging.db.UpdateSynchronizationException {
	if (oldJobStatus != null && !oldJobStatus.isDone()) {

		SimulationJobStatus newJobStatus = getNewStatus_updateRunningStatus(oldJobStatus, hostName, vcSimID, jobIndex, hasData, solverMsg);

		if (newJobStatus == oldJobStatus) {
			updateLatestUpdateDate(oldJobStatus, adminDbXA, con, vcSimID, jobIndex);
			return oldJobStatus;
		} else {
			newJobStatus = adminDbXA.updateSimulationJobStatus(con, oldJobStatus, newJobStatus);
			return newJobStatus;
		}
	}

	return oldJobStatus;
}
}