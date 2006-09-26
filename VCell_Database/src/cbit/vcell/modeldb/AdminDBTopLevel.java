package cbit.vcell.modeldb;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import cbit.rmi.event.SimulationJobStatus;
import cbit.rmi.event.VCellServerID;
import cbit.sql.ConnectionFactory;
import cbit.util.DataAccessException;
import cbit.util.KeyValue;
import cbit.util.ObjectNotFoundException;
import cbit.util.SessionLog;
import cbit.util.User;
import cbit.util.UserInfo;
import cbit.vcell.messaging.db.SimulationJobDbDriver;
import cbit.vcell.messaging.db.SimulationJobStatusInfo;
import cbit.vcell.messaging.db.UpdateSynchronizationException;
import cbit.vcell.solvers.SimulationStatus;

/**
 * This type was created in VisualAge.
 */
public class AdminDBTopLevel extends AbstractDBTopLevel{
	private UserDbDriver userDB = null;
	private SimulationJobDbDriver jobDB = null;
	private static final int SQL_ERROR_CODE_BADCONNECTION = 1010; //??????????????????????????????????????

/**
 * DBTopLevel constructor comment.
 */
AdminDBTopLevel(ConnectionFactory aConFactory,SessionLog newLog) throws SQLException{
	super(aConFactory,newLog);
	userDB = new UserDbDriver(log);
	jobDB = new SimulationJobDbDriver(log); 
}


/**
 * Insert the method's description here.
 * Creation date: (10/6/2005 3:14:40 PM)
 */
SimulationJobStatusInfo[] getActiveJobs(Connection con, VCellServerID[] serverIDs) throws SQLException {
	SimulationJobStatusInfo[] jobStatusArray = jobDB.getActiveJobs(con, serverIDs);
	return jobStatusArray;
}


/**
 * Insert the method's description here.
 * Creation date: (10/6/2005 3:03:51 PM)
 */
SimulationJobStatus getNextObsoleteSimulation(Connection con, long interval) throws SQLException {
	SimulationJobStatus jobStatus = jobDB.getNextObsoleteSimulation(con, interval);
	return jobStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:35:44 PM)
 * @return cbit.vcell.solvers.SimulationJobStatus[]
 * @param bActiveOnly boolean
 * @param owner cbit.vcell.server.User
 */
SimulationJobStatus getSimulationJobStatus(KeyValue simKey, int jobIndex, boolean bEnableRetry) throws java.sql.SQLException, DataAccessException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		SimulationJobStatus jobStatus = getSimulationJobStatus(con, simKey, jobIndex);
		return jobStatus;
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getSimulationJobStatus(simKey,jobIndex,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	} finally {
		conFactory.release(con,lock);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/3/2003 8:59:46 AM)
 * @return java.util.List
 * @param conditions java.lang.String
 */
public java.util.List getSimulationJobStatus(String conditions, boolean bEnableRetry) throws java.sql.SQLException, cbit.util.DataAccessException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return jobDB.getSimulationJobStatus(con, conditions);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getSimulationJobStatus(conditions,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	} finally {
		conFactory.release(con,lock);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/6/2005 3:08:22 PM)
 */
SimulationJobStatus getSimulationJobStatus(Connection con, KeyValue simKey, int jobIndex) throws SQLException {
	SimulationJobStatus jobStatus = jobDB.getSimulationJobStatus(con,simKey,jobIndex,false);
	return jobStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:35:44 PM)
 * @return cbit.vcell.solvers.SimulationJobStatus[]
 * @param bActiveOnly boolean
 * @param owner cbit.vcell.server.User
 */
SimulationJobStatus[] getSimulationJobStatus(Connection con, boolean bActiveOnly, User owner) throws java.sql.SQLException, DataAccessException {
	return jobDB.getSimulationJobStatus(con,bActiveOnly,owner);
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:35:44 PM)
 * @return cbit.vcell.solvers.SimulationJobStatus[]
 * @param bActiveOnly boolean
 * @param owner cbit.vcell.server.User
 */
SimulationJobStatus[] getSimulationJobStatus(boolean bActiveOnly, User owner, boolean bEnableRetry) throws java.sql.SQLException, DataAccessException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return jobDB.getSimulationJobStatus(con,bActiveOnly,owner);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getSimulationJobStatus(bActiveOnly,owner,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	} finally {
		conFactory.release(con,lock);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:35:44 PM)
 * @return cbit.vcell.solvers.SimulationJobStatus[]
 * @param bActiveOnly boolean
 * @param owner cbit.vcell.server.User
 */
SimulationStatus[] getSimulationStatus(KeyValue simulationKeys[], boolean bEnableRetry) throws java.sql.SQLException, DataAccessException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		SimulationJobStatus[] jobStatuses = jobDB.getSimulationJobStatus(con,simulationKeys);
		SimulationStatus[] simStatuses = new SimulationStatus[simulationKeys.length];
		for (int i = 0; i < simulationKeys.length; i++){
			Vector v = new Vector();
			for (int j = 0; j < jobStatuses.length; j++){
				if(jobStatuses[j].getVCSimulationIdentifier().getSimulationKey().equals(simulationKeys[i])) {
					v.add(jobStatuses[j]);
				}
			}
			if (v.isEmpty()) {
				simStatuses[i] = null;
			} else {
				simStatuses[i] = new SimulationStatus((SimulationJobStatus[])cbit.util.BeanUtils.getArray(v, SimulationJobStatus.class));
			}
		}
		return simStatuses;
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getSimulationStatus(simulationKeys,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	} finally {
		conFactory.release(con,lock);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:35:44 PM)
 * @return cbit.vcell.solvers.SimulationJobStatus[]
 * @param bActiveOnly boolean
 * @param owner cbit.vcell.server.User
 */
SimulationStatus getSimulationStatus(KeyValue simKey, boolean bEnableRetry) throws java.sql.SQLException, DataAccessException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		SimulationJobStatus[] jobStatuses = jobDB.getSimulationJobStatus(con,simKey);
		if (jobStatuses.length > 0) {
			return new SimulationStatus(jobStatuses);
		} else {
			return null;
		}
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getSimulationStatus(simKey,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	} finally {
		conFactory.release(con,lock);
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param object cbit.sql.Versionable
 * @param name java.lang.String
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
User getUser(String userid, String password, boolean bEnableRetry) 
				throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return userDB.getUserFromUseridAndPassword(con, userid, password);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getUser(userid, password, false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	} finally {
		conFactory.release(con,lock);
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param object cbit.sql.Versionable
 * @param name java.lang.String
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
User getUser(String userid, boolean bEnableRetry) throws DataAccessException, java.sql.SQLException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return userDB.getUserFromUserid(con, userid);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getUser(userid, false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	} finally {
		conFactory.release(con,lock);
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param object cbit.sql.Versionable
 * @param name java.lang.String
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
User getUserFromSimulationKey(KeyValue simKey, boolean bEnableRetry) throws DataAccessException, java.sql.SQLException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return jobDB.getUserFromSimulationKey(con, simKey);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getUserFromSimulationKey(simKey,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	} finally {
		conFactory.release(con,lock);
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param object cbit.sql.Versionable
 * @param name java.lang.String
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
UserInfo getUserInfo(KeyValue key, boolean bEnableRetry) 
				throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return userDB.getUserInfo(con, key);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getUserInfo(key, false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	} finally {
		conFactory.release(con,lock);
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param object cbit.sql.Versionable
 * @param name java.lang.String
 * @param bVersion boolean
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
UserInfo[] getUserInfos(boolean bEnableRetry) 
				throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return userDB.getUserInfos(con);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getUserInfos(false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	} finally {
		conFactory.release(con,lock);
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.UserInfo
 * @param newUserInfo cbit.sql.UserInfo
 */
SimulationJobStatus insertSimulationJobStatus(SimulationJobStatus simulationJobStatus, boolean bEnableRetry) throws SQLException, DataAccessException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		SimulationJobStatus newSimulationJobStatus = insertSimulationJobStatus(con, simulationJobStatus);
		con.commit();
		return newSimulationJobStatus;
	} catch (Throwable e) {
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return insertSimulationJobStatus(simulationJobStatus,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	}finally{
		conFactory.release(con,lock);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/3/2005 3:33:09 PM)
 */
SimulationJobStatus insertSimulationJobStatus(Connection con, SimulationJobStatus simulationJobStatus) throws SQLException, UpdateSynchronizationException {
	SimulationJobStatus currentSimulationJobStatus = jobDB.getSimulationJobStatus(con,simulationJobStatus.getVCSimulationIdentifier().getSimulationKey(), simulationJobStatus.getJobIndex(),false);
	if (currentSimulationJobStatus != null){
		con.rollback();
		log.alert("AdminDbTopLevel.insertSimulationJobStatus() : current Job Status = " + currentSimulationJobStatus + ", job status database record already exists");
		throw new UpdateSynchronizationException("Job Status database record already exists:" + currentSimulationJobStatus.getVCSimulationIdentifier().getSimulationKey()+" job: "+currentSimulationJobStatus.getJobIndex());
	}
	jobDB.insertSimulationJobStatus(con,simulationJobStatus, DbDriver.getNewKey(con));
	SimulationJobStatus newSimulationJobStatus = jobDB.getSimulationJobStatus(con,simulationJobStatus.getVCSimulationIdentifier().getSimulationKey(), simulationJobStatus.getJobIndex(),false);
	return newSimulationJobStatus;
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.UserInfo
 * @param newUserInfo cbit.sql.UserInfo
 */
KeyValue insertUserInfo(UserInfo newUserInfo, boolean bEnableRetry) throws SQLException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		KeyValue key = userDB.insertUserInfo(con,newUserInfo);
		con.commit();
		return key;
	} catch (Throwable e) {
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return insertUserInfo(newUserInfo,false);
		}else{
			handle_SQLException(e);
			return null; // never gets here;
		}
	}finally{
		conFactory.release(con,lock);
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.UserInfo
 * @param newUserInfo cbit.sql.UserInfo
 */
SimulationJobStatus updateSimulationJobStatus(SimulationJobStatus oldSimulationJobStatus, SimulationJobStatus newSimulationJobStatus, boolean bEnableRetry) throws SQLException, DataAccessException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		SimulationJobStatus updatedSimulationJobStatus = updateSimulationJobStatus(con, oldSimulationJobStatus, newSimulationJobStatus);
		con.commit();
		return updatedSimulationJobStatus;
	} catch (Throwable e) {
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return updateSimulationJobStatus(oldSimulationJobStatus,newSimulationJobStatus,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	}finally{
		conFactory.release(con,lock);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/6/2005 3:20:41 PM)
 */
SimulationJobStatus updateSimulationJobStatus(Connection con, SimulationJobStatus oldSimulationJobStatus, SimulationJobStatus newSimulationJobStatus) throws SQLException, UpdateSynchronizationException {
	SimulationJobStatus currentSimulationJobStatus = jobDB.getSimulationJobStatus(con,newSimulationJobStatus.getVCSimulationIdentifier().getSimulationKey(),newSimulationJobStatus.getJobIndex(),true);
	if (!currentSimulationJobStatus.compareEqual(oldSimulationJobStatus)){
		log.print("AdminDbTopLevel.updateSimulationJobStatus() : current Job Status = "+currentSimulationJobStatus+", old Job Status = "+oldSimulationJobStatus);
		throw new UpdateSynchronizationException("current Job Status doesn't match argument for Simulation :"+currentSimulationJobStatus.getVCSimulationIdentifier().getSimulationKey()+" job: "+currentSimulationJobStatus.getJobIndex());
	}
	jobDB.updateSimulationJobStatus(con,newSimulationJobStatus);
	SimulationJobStatus updatedSimulationJobStatus = jobDB.getSimulationJobStatus(con,newSimulationJobStatus.getVCSimulationIdentifier().getSimulationKey(),newSimulationJobStatus.getJobIndex(),false);
	return updatedSimulationJobStatus;
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.UserInfo
 * @param newUserInfo cbit.sql.UserInfo
 */
KeyValue updateUserInfo(UserInfo newUserInfo, boolean bEnableRetry) throws SQLException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		userDB.updateUserInfo(con,newUserInfo);
		con.commit();
		return newUserInfo.id;
	} catch (Throwable e) {
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return updateUserInfo(newUserInfo,false);
		}else{
			handle_SQLException(e);
			return null; // never gets here;
		}
	}finally{
		conFactory.release(con,lock);
	}
}
}