package cbit.vcell.modeldb;
import cbit.sql.KeyFactory;
import cbit.util.DataAccessException;
import cbit.util.SessionLog;
import cbit.vcell.messaging.db.SimulationJobStatusInfo;
import cbit.vcell.server.AdminDatabaseServerXA;
import cbit.vcell.solvers.SimulationJobStatus;
import cbit.vcell.solvers.VCellServerID;
/**
 * Insert the type's description here.
 * Creation date: (10/5/2005 5:20:07 PM)
 * @author: Ion Moraru
 */
public class AdminDatabaseServerXAImpl implements AdminDatabaseServerXA {
	private SessionLog log = null;
	private AdminDBTopLevel adminDbTop = null;

public AdminDatabaseServerXAImpl(KeyFactory keyFactory, SessionLog sessionLog) throws DataAccessException {

	this.log = sessionLog;
	DbDriver.setKeyFactory(keyFactory);
	try {
		adminDbTop = new AdminDBTopLevel(null,log);
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException("Error creating AdminDbTop " + e.getMessage());
	}		
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/2005 5:20:07 PM)
 * @return cbit.vcell.messaging.db.SimulationJobStatus[]
 * @param con java.sql.Connection
 * @exception cbit.util.DataAccessException The exception description.
 */
public cbit.vcell.messaging.db.SimulationJobStatusInfo[] getActiveJobs(java.sql.Connection con, VCellServerID[] serverIDs) throws DataAccessException {
	try {
		SimulationJobStatusInfo[] jobStatuses = adminDbTop.getActiveJobs(con, serverIDs);
		return jobStatuses;
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure getting active jobs");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/2005 5:20:07 PM)
 * @return cbit.vcell.messaging.db.SimulationJobStatus
 * @param con java.sql.Connection
 * @param intervalSeconds long
 * @exception cbit.util.DataAccessException The exception description.
 */
public SimulationJobStatus getNextObsoleteSimulation(java.sql.Connection con, long intervalSeconds) throws DataAccessException {
	try {
		SimulationJobStatus jobStatus = adminDbTop.getNextObsoleteSimulation(con, intervalSeconds);
		return jobStatus;
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure getting NextObsoleteSimulation");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/2005 5:20:07 PM)
 * @return cbit.vcell.messaging.db.SimulationJobStatus
 * @param con java.sql.Connection
 * @param simKey cbit.sql.KeyValue
 * @param jobIndex int
 * @exception cbit.util.DataAccessException The exception description.
 */
public SimulationJobStatus getSimulationJobStatus(java.sql.Connection con, cbit.util.KeyValue simKey, int jobIndex) throws DataAccessException {
	try {
		SimulationJobStatus jobStatus = adminDbTop.getSimulationJobStatus(con, simKey, jobIndex);
		return jobStatus;
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure getting SimulationJobStatus for ["+simKey+"]["+jobIndex+"]");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:34:12 PM)
 * @return cbit.vcell.solvers.SimulationJobStatus[]
 * @param bActiveOnly boolean
 * @param userOnly cbit.vcell.server.User
 * @exception java.rmi.RemoteException The exception description.
 */
public SimulationJobStatus[] getSimulationJobStatus(java.sql.Connection con, boolean bActiveOnly, cbit.util.User userOnly) throws DataAccessException {
	try {
		return adminDbTop.getSimulationJobStatus(con, bActiveOnly,userOnly);
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure getting SimulationJobStatus");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/2005 5:20:07 PM)
 * @return cbit.vcell.messaging.db.SimulationJobStatus
 * @param con java.sql.Connection
 * @param simulationJobStatus cbit.vcell.messaging.db.SimulationJobStatus
 * @exception cbit.util.DataAccessException The exception description.
 */
public SimulationJobStatus insertSimulationJobStatus(java.sql.Connection con, SimulationJobStatus simulationJobStatus) throws DataAccessException {
	try {
		SimulationJobStatus jobStatus = adminDbTop.insertSimulationJobStatus(con, simulationJobStatus);
		return jobStatus;
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure inserting SimulationJobStatus: "+simulationJobStatus);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/2005 5:20:07 PM)
 * @return cbit.vcell.messaging.db.SimulationJobStatus
 * @param con java.sql.Connection
 * @param oldSimulationJobStatus cbit.vcell.messaging.db.SimulationJobStatus
 * @param newSimulationJobStatus cbit.vcell.messaging.db.SimulationJobStatus
 * @exception cbit.util.DataAccessException The exception description.
 */
public SimulationJobStatus updateSimulationJobStatus(java.sql.Connection con, SimulationJobStatus oldSimulationJobStatus, SimulationJobStatus newSimulationJobStatus) throws DataAccessException {
	try {
		SimulationJobStatus jobStatus = adminDbTop.updateSimulationJobStatus(con, oldSimulationJobStatus, newSimulationJobStatus);
		return jobStatus;
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure updating SimulationJobStatus: "+oldSimulationJobStatus);
	}
}
}