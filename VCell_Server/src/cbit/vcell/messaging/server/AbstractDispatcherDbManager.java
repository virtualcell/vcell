package cbit.vcell.messaging.server;
import cbit.util.DataAccessException;
import cbit.util.KeyValue;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.messaging.db.UpdateSynchronizationException;
import cbit.vcell.server.AdminDatabaseServer;
import cbit.vcell.simulation.VCSimulationIdentifier;
import cbit.vcell.messaging.db.SimulationQueueEntryStatus;
import cbit.vcell.messaging.db.SimulationExecutionStatus;
import java.util.Date;
import cbit.vcell.messaging.MessageConstants;

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
SimulationJobStatus getNewStatus_updateDispatchedStatus(SimulationJobStatus oldJobStatus, java.lang.String computeHost, cbit.vcell.simulation.VCSimulationIdentifier vcSimID, int jobIndex, java.lang.String startMsg) throws DataAccessException, cbit.vcell.messaging.db.UpdateSynchronizationException {

	// new queue status
	SimulationQueueEntryStatus oldQueueStatus = oldJobStatus.getSimulationQueueEntryStatus();
	SimulationQueueEntryStatus newQueueStatus = new SimulationQueueEntryStatus(oldQueueStatus.getQueueDate(), 
		oldQueueStatus.getQueuePriority(), MessageConstants.QUEUE_ID_NULL);

	// new exe status
	SimulationExecutionStatus newExeStatus = new SimulationExecutionStatus(null, computeHost, null,	null, false);

	// new job status
	SimulationJobStatus newJobStatus = new SimulationJobStatus(oldJobStatus.getServerID(), vcSimID, jobIndex, oldJobStatus.getSubmitDate(), SimulationJobStatus.SCHEDULERSTATUS_DISPATCHED,
			oldJobStatus.getTaskID(), startMsg, newQueueStatus, newExeStatus);

	return newJobStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
SimulationJobStatus getNewStatus_updateEndStatus(SimulationJobStatus oldJobStatus, VCSimulationIdentifier vcSimID, int jobIndex, String hostName, int status, String solverMsg) throws DataAccessException, UpdateSynchronizationException {

	// new queue status
	SimulationQueueEntryStatus oldQueueStatus = oldJobStatus.getSimulationQueueEntryStatus();
	SimulationQueueEntryStatus newQueueStatus = oldQueueStatus;
	if (oldQueueStatus.getQueueID() != MessageConstants.QUEUE_ID_NULL) {		
		newQueueStatus = new SimulationQueueEntryStatus(oldQueueStatus.getQueueDate(), oldQueueStatus.getQueuePriority(), MessageConstants.QUEUE_ID_NULL);
	}

	// new exe status
	SimulationExecutionStatus oldExeStatus = oldJobStatus.getSimulationExecutionStatus();
	SimulationExecutionStatus newExeStatus = null;
	boolean hasData = false;
	
	if (oldExeStatus == null) {
		if (status == SimulationJobStatus.SCHEDULERSTATUS_COMPLETED) {
			hasData = true;
		}
		newExeStatus = new SimulationExecutionStatus(null, hostName, null, null, hasData);				
	} else {
		if (status == SimulationJobStatus.SCHEDULERSTATUS_COMPLETED) {
			hasData = true;
		} else {
			hasData = oldExeStatus.hasData();
		}
		newExeStatus = new SimulationExecutionStatus(oldExeStatus.getStartDate(), (hostName != null) ? hostName : oldExeStatus.getComputeHost(), null, null, hasData);
	}

	// new job status
	SimulationJobStatus newJobStatus = new SimulationJobStatus(oldJobStatus.getServerID(), vcSimID, jobIndex, oldJobStatus.getSubmitDate(), status, oldJobStatus.getTaskID(), solverMsg,
		newQueueStatus, newExeStatus);
	
	return newJobStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
SimulationJobStatus getNewStatus_updateLatestUpdateDate(SimulationJobStatus oldJobStatus, VCSimulationIdentifier vcSimID, int jobIndex) throws DataAccessException, UpdateSynchronizationException {

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
	SimulationExecutionStatus newExeStatus = new SimulationExecutionStatus(oldExeStatus.getStartDate(), oldExeStatus.getComputeHost(), null, 
			oldExeStatus.getEndDate(), oldExeStatus.hasData());
	
	SimulationJobStatus newJobStatus = new SimulationJobStatus(oldJobStatus.getServerID(), vcSimID, jobIndex, oldJobStatus.getSubmitDate(), 
		oldJobStatus.getSchedulerStatus(),	oldJobStatus.getTaskID(), oldJobStatus.getStatusMessage(), oldJobStatus.getSimulationQueueEntryStatus(), newExeStatus);

	return newJobStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
SimulationJobStatus getNewStatus_updateRunningStatus(SimulationJobStatus oldJobStatus, String hostName, VCSimulationIdentifier vcSimID, int jobIndex, boolean hasData, String solverMsg)	throws DataAccessException, UpdateSynchronizationException {

	// new queue status		
	SimulationQueueEntryStatus oldQueueStatus = oldJobStatus.getSimulationQueueEntryStatus();
	SimulationQueueEntryStatus newQueueStatus = oldQueueStatus;
	if (oldQueueStatus.getQueueID() != MessageConstants.QUEUE_ID_NULL) {
		newQueueStatus = new SimulationQueueEntryStatus(oldQueueStatus.getQueueDate(), oldQueueStatus.getQueuePriority(), MessageConstants.QUEUE_ID_NULL);
	}

	// new exe status
	SimulationExecutionStatus oldExeStatus = oldJobStatus.getSimulationExecutionStatus();
	SimulationExecutionStatus newExeStatus = null;
	if (oldExeStatus == null) {
		newExeStatus = new SimulationExecutionStatus(null, hostName, null, null, hasData);
		
	} else if (!oldJobStatus.isRunning() || oldExeStatus != null && !oldExeStatus.hasData() && hasData) {
		newExeStatus = new SimulationExecutionStatus(oldExeStatus.getStartDate(), (hostName != null) ? hostName : oldExeStatus.getComputeHost(), null, null, hasData);
		
	} else {
		return oldJobStatus;
	}
	
	// new job status
	SimulationJobStatus newJobStatus = new SimulationJobStatus(oldJobStatus.getServerID(), vcSimID, jobIndex, oldJobStatus.getSubmitDate(), 
		SimulationJobStatus.SCHEDULERSTATUS_RUNNING, oldJobStatus.getTaskID(), solverMsg, newQueueStatus, newExeStatus);

	return newJobStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
public SimulationJobStatus getSimulationJobStatus(AdminDatabaseServer adminDb, KeyValue simKey, int jobIndex) throws DataAccessException {
	try {		
		return adminDb.getSimulationJobStatus(simKey, jobIndex);
	} catch (java.rmi.RemoteException ex) {
		throw new DataAccessException("updateDispatchedStatus " + ex.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
public cbit.vcell.messaging.db.SimulationJobStatus updateDispatchedStatus(cbit.vcell.messaging.db.SimulationJobStatus oldJobStatus, cbit.vcell.server.AdminDatabaseServer adminDb, java.lang.String computeHost, cbit.vcell.simulation.VCSimulationIdentifier vcSimID, int jobIndex, java.lang.String startMsg) throws DataAccessException, cbit.vcell.messaging.db.UpdateSynchronizationException {
	try {

		if (oldJobStatus != null && !oldJobStatus.isDone()) {
			
			SimulationJobStatus newJobStatus = getNewStatus_updateDispatchedStatus(oldJobStatus, computeHost, vcSimID, jobIndex, startMsg);

			adminDb.updateSimulationJobStatus(oldJobStatus, newJobStatus);

			return newJobStatus;
		}

		return oldJobStatus;
		
	} catch (java.rmi.RemoteException ex) {
		throw new DataAccessException("updateEndStatus " + ex.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
public SimulationJobStatus updateEndStatus(SimulationJobStatus oldJobStatus, AdminDatabaseServer adminDb, VCSimulationIdentifier vcSimID, int jobIndex, String hostName, int status, String solverMsg) throws DataAccessException, UpdateSynchronizationException {
	try {
		if (oldJobStatus != null && !oldJobStatus.isDone()) {		

			SimulationJobStatus newJobStatus = getNewStatus_updateEndStatus(oldJobStatus, vcSimID, jobIndex, hostName, status, solverMsg);
			
			adminDb.updateSimulationJobStatus(oldJobStatus, newJobStatus);

			return newJobStatus;
		}

		return oldJobStatus;
	} catch (java.rmi.RemoteException ex) {
		throw new DataAccessException("updateEndStatus " + ex.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
public void updateLatestUpdateDate(SimulationJobStatus oldJobStatus, AdminDatabaseServer adminDb, VCSimulationIdentifier vcSimID, int jobIndex) throws DataAccessException, UpdateSynchronizationException {
	try {
		if (oldJobStatus != null && !oldJobStatus.isDone()) {

			SimulationJobStatus	newJobStatus = getNewStatus_updateLatestUpdateDate(oldJobStatus, vcSimID, jobIndex);
			
			if (newJobStatus != null) {
				adminDb.updateSimulationJobStatus(oldJobStatus, newJobStatus);
			}
		}
	} catch (java.rmi.RemoteException ex) {
		throw new DataAccessException("updateLatestUpdateDate " + ex.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
public SimulationJobStatus updateRunningStatus(SimulationJobStatus oldJobStatus, AdminDatabaseServer adminDb, String hostName, VCSimulationIdentifier vcSimID, int jobIndex, boolean hasData, String solverMsg)	throws DataAccessException, UpdateSynchronizationException {
	try {
		if (oldJobStatus != null && !oldJobStatus.isDone()) {

			SimulationJobStatus newJobStatus = getNewStatus_updateRunningStatus(oldJobStatus, hostName, vcSimID, jobIndex, hasData, solverMsg);

			if (newJobStatus == oldJobStatus) {
				updateLatestUpdateDate(oldJobStatus, adminDb, vcSimID, jobIndex);
				return oldJobStatus;
			} else {
				adminDb.updateSimulationJobStatus(oldJobStatus, newJobStatus);
				return newJobStatus;
			}
		}

		return oldJobStatus;
	} catch (java.rmi.RemoteException ex) {
		throw new DataAccessException("updateRunningStatus " + ex.getMessage());
	}		
}
}