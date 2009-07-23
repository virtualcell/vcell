package cbit.vcell.messaging.server;
import java.sql.Connection;

import org.vcell.util.DataAccessException;

import cbit.vcell.server.AdminDatabaseServerXA;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.messaging.db.UpdateSynchronizationException;
import cbit.vcell.solver.SimulationMessage;

/**
 * Insert the type's description here.
 * Creation date: (2/23/2004 10:42:53 AM)
 * @author: Fei Gao
 */
public interface MessagingDispatcherDbManager extends DispatcherDbManager {
	SimulationJobStatus updateDispatchedStatus(SimulationJobStatus oldJobStatus, AdminDatabaseServerXA adminDbXA, Connection con, 
			String computeHost, VCSimulationIdentifier vcSimID, int jobIndex, SimulationMessage startMsg) throws DataAccessException, UpdateSynchronizationException;


	SimulationJobStatus updateEndStatus(SimulationJobStatus oldJobStatus, AdminDatabaseServerXA adminDbXA, Connection con, 
			VCSimulationIdentifier vcSimID, int jobIndex, String hostName, int status, SimulationMessage solverMsg) throws DataAccessException, UpdateSynchronizationException;


	void updateLatestUpdateDate(SimulationJobStatus oldJobStatus, AdminDatabaseServerXA adminDbXA, Connection con, 
			VCSimulationIdentifier vcSimID, int jobIndex, SimulationMessage simulationMessage) throws DataAccessException, UpdateSynchronizationException;


	SimulationJobStatus updateQueueStatus(SimulationJobStatus oldJobStatus, AdminDatabaseServerXA adminDb, Connection con, 
			VCSimulationIdentifier vcSimID, int jobIndex, int queueID, int taskID, boolean firstSubmit) throws DataAccessException, UpdateSynchronizationException;


	SimulationJobStatus updateRunningStatus(SimulationJobStatus oldJobStatus, AdminDatabaseServerXA adminDbXA, Connection con, String hostName, 
			VCSimulationIdentifier vcSimID, int jobIndex, boolean hasData, SimulationMessage solverMsg)	throws DataAccessException, UpdateSynchronizationException;
}