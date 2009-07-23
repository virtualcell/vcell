package cbit.vcell.messaging.server;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;

import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.server.AdminDatabaseServer;
import cbit.vcell.messaging.db.UpdateSynchronizationException;
import cbit.vcell.solver.SimulationMessage;

/**
 * Insert the type's description here.
 * Creation date: (2/20/2004 3:38:59 PM)
 * @author: Fei Gao
 */
public interface DispatcherDbManager {
	SimulationJobStatus getSimulationJobStatus(AdminDatabaseServer adminDb, KeyValue simKey, int jobIndex) throws DataAccessException;


	SimulationJobStatus updateDispatchedStatus(SimulationJobStatus oldJobStatus, AdminDatabaseServer adminDb, String computeHost, 
			VCSimulationIdentifier vcSimID, int jobIndex, SimulationMessage startMsg) throws DataAccessException, UpdateSynchronizationException;


	SimulationJobStatus updateEndStatus(SimulationJobStatus oldJobStatus, AdminDatabaseServer adminDb, VCSimulationIdentifier vcSimID, 
		int jobIndex, String hostName, int status, SimulationMessage solverMsg) throws DataAccessException, UpdateSynchronizationException;


	void updateLatestUpdateDate(SimulationJobStatus oldJobStatus, AdminDatabaseServer adminDb, VCSimulationIdentifier vcSimID, 
			int jobIndex, SimulationMessage simulationMessage) throws DataAccessException, UpdateSynchronizationException;


	SimulationJobStatus updateRunningStatus(SimulationJobStatus oldJobStatus, AdminDatabaseServer adminDb, String hostName, 
			VCSimulationIdentifier vcSimID, int jobIndex, boolean hasData, SimulationMessage solverMsg)	throws DataAccessException, UpdateSynchronizationException;
}