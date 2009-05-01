package cbit.vcell.messaging.server;
import org.vcell.util.DataAccessException;
import org.vcell.util.MessageConstants;
import org.vcell.util.document.KeyValue;

import cbit.vcell.messaging.db.VCellServerID;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.server.AdminDatabaseServer;
import cbit.vcell.messaging.db.UpdateSynchronizationException;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.messaging.db.SimulationQueueEntryStatus;
import cbit.vcell.messaging.db.SimulationExecutionStatus;

/**
 * Insert the type's description here.
 * Creation date: (2/20/2004 3:45:37 PM)
 * @author: Fei Gao
 */
public class LocalDispatcherDbManager extends AbstractDispatcherDbManager {
/**
 * NonJmsDispatcherDbDriver constructor comment.
 */
public LocalDispatcherDbManager() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2003 3:39:37 PM)
 * @param simKey cbit.sql.KeyValue
 */
public SimulationJobStatus updateDispatchedStatus(SimulationJobStatus oldJobStatus, AdminDatabaseServer adminDb, String computeHost, VCSimulationIdentifier vcSimID, int jobIndex, String startMsg) throws DataAccessException, UpdateSynchronizationException {
	try {
		if (oldJobStatus == null || oldJobStatus.isDone()) {	
			KeyValue simKey = vcSimID.getSimulationKey();
			int taskID = 0;
			VCellServerID serverID = VCellServerID.getSystemServerID();
			if (oldJobStatus != null) {
				taskID = oldJobStatus.getTaskID() + 1;
				//serverID = oldJobStatus.getServerID();
			}
				
			// no job for the same simulation running
				
			// update the job status in the database and local memory
			SimulationJobStatus newJobStatus = new SimulationJobStatus(serverID, vcSimID, jobIndex, null, SimulationJobStatus.SCHEDULERSTATUS_DISPATCHED, taskID, null, 
				new SimulationQueueEntryStatus(null, MessageConstants.PRIORITY_DEFAULT, MessageConstants.QUEUE_ID_NULL), null);
				
			if (oldJobStatus == null) {
				newJobStatus = adminDb.insertSimulationJobStatus(newJobStatus);
			} else {
				newJobStatus = adminDb.updateSimulationJobStatus(oldJobStatus, newJobStatus);
			}

			return newJobStatus;
		}

		return oldJobStatus;
		
	} catch (java.rmi.RemoteException ex) {
		throw new DataAccessException("updateDispatchedStatus " + ex.getMessage());
	}
}
}