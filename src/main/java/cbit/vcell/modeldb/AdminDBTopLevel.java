/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modeldb;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.vcell.db.ConnectionFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.SessionLog;
import org.vcell.util.UseridIDExistsException;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.message.server.ServiceStatus;
import cbit.vcell.messaging.db.ServiceStatusDbDriver;
import cbit.vcell.messaging.db.SimulationJobDbDriver;
import cbit.vcell.messaging.db.SimulationRequirements;
import cbit.vcell.modeldb.ApiAccessToken.AccessTokenStatus;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.server.SimpleJobStatusPersistent;
import cbit.vcell.server.SimpleJobStatusQuerySpec;
import cbit.vcell.server.SimulationJobStatusPersistent;
import cbit.vcell.server.SimulationStatusPersistent;
import cbit.vcell.server.UpdateSynchronizationException;

/**
 * This type was created in VisualAge.
 */
public class AdminDBTopLevel extends AbstractDBTopLevel{
	private UserDbDriver userDB = null;
	private SimulationJobDbDriver jobDB = null;
	private ServiceStatusDbDriver serviceStatusDB = null; 
	private static final int SQL_ERROR_CODE_BADCONNECTION = 1010; //??????????????????????????????????????

/**
 * DBTopLevel constructor comment.
 */
public AdminDBTopLevel(ConnectionFactory aConFactory,SessionLog newLog) throws SQLException{
	super(aConFactory,newLog);
	userDB = new UserDbDriver(log);
	jobDB = new SimulationJobDbDriver(log); 
	serviceStatusDB = new ServiceStatusDbDriver();
}


public ExternalDataIdentifier[] getExternalDataIdentifiers(User fieldDataOwner,boolean bEnableRetry) throws SQLException,DataAccessException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return DbDriver.fieldDataDBOperation(
				con, null,
				FieldDataDBOperationSpec.createGetExtDataIDsSpec(fieldDataOwner)).extDataIDArr;
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getExternalDataIdentifiers(fieldDataOwner,false);
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
 * Creation date: (10/6/2005 3:14:40 PM)
 */
SimulationJobStatusPersistent[] getActiveJobs(Connection con, VCellServerID serverID) throws SQLException {
	SimulationJobStatusPersistent[] jobStatusArray = jobDB.getActiveJobs(con, serverID);
	return jobStatusArray;
}

public SimulationJobStatusPersistent[] getActiveJobs(VCellServerID serverID, boolean bEnableRetry) throws java.sql.SQLException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return jobDB.getActiveJobs(con,serverID);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getActiveJobs(serverID, false);
		}else{
			handle_SQLException(e);
			return null; // never gets here;
		}
	} finally {
		conFactory.release(con,lock);
	}
}

public Map<KeyValue,SimulationRequirements> getSimulationRequirements(Collection<KeyValue> simKeys, boolean bEnableRetry) throws java.sql.SQLException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return jobDB.getSimulationRequirements(con, simKeys);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getSimulationRequirements(simKeys, false);
		}else{
			handle_SQLException(e);
			return null; // never gets here;
		}
	} finally {
		conFactory.release(con,lock);
	}
}



public Set<KeyValue> getUnreferencedSimulations(boolean bEnableRetry) throws java.sql.SQLException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		Set<KeyValue> unreferencedSimulations = DBBackupAndClean.getUnreferencedSimulations(con);
		return unreferencedSimulations;
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getUnreferencedSimulations(false);
		}else{
			handle_SQLException(e);
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
public SimulationJobStatusPersistent getSimulationJobStatus(KeyValue simKey, int jobIndex, int taskID, boolean bEnableRetry) throws java.sql.SQLException, DataAccessException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		SimulationJobStatusPersistent jobStatus = getSimulationJobStatus(con, simKey, jobIndex, taskID);
		return jobStatus;
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getSimulationJobStatus(simKey,jobIndex,taskID,false);
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
public SimulationJobStatusPersistent[] getSimulationJobStatusArray(KeyValue simKey, boolean bEnableRetry) throws java.sql.SQLException, DataAccessException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		SimulationJobStatusPersistent[] jobStatus = getSimulationJobStatusArray(con, simKey);
		return jobStatus;
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getSimulationJobStatusArray(simKey,false);
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
public SimulationJobStatusPersistent[] getSimulationJobStatusArray(KeyValue simKey, int jobIndex, boolean bEnableRetry) throws java.sql.SQLException, DataAccessException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		SimulationJobStatusPersistent[] jobStatus = getSimulationJobStatusArray(con, simKey, jobIndex);
		return jobStatus;
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getSimulationJobStatusArray(simKey,jobIndex,false);
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
public java.util.List<SimpleJobStatusPersistent> getSimpleJobStatus(String conditions, int startRow, int maxNumRows, boolean bEnableRetry) throws java.sql.SQLException, org.vcell.util.DataAccessException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return jobDB.getSimpleJobStatus(con, conditions, startRow, maxNumRows);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getSimpleJobStatus(conditions,startRow,maxNumRows,false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	} finally {
		conFactory.release(con,lock);
	}
}



public java.util.List<SimpleJobStatusPersistent> getSimpleJobStatus(SimpleJobStatusQuerySpec simStatusQuerySpec, boolean bEnableRetry) throws SQLException, DataAccessException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return jobDB.getSimpleJobStatus(con, simStatusQuerySpec);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getSimpleJobStatus(simStatusQuerySpec,false);
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
SimulationJobStatusPersistent getSimulationJobStatus(Connection con, KeyValue simKey, int jobIndex, int taskID) throws SQLException {
	SimulationJobStatusPersistent jobStatus = jobDB.getSimulationJobStatus(con,simKey,jobIndex,taskID,false);
	return jobStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (10/6/2005 3:08:22 PM)
 */
SimulationJobStatusPersistent[] getSimulationJobStatusArray(Connection con, KeyValue simKey) throws SQLException {
	SimulationJobStatusPersistent[] jobStatus = jobDB.getSimulationJobStatusArray(con,simKey,false);
	return jobStatus;
}

/**
 * Insert the method's description here.
 * Creation date: (10/6/2005 3:08:22 PM)
 */
SimulationJobStatusPersistent[] getSimulationJobStatusArray(Connection con, KeyValue simKey, int jobIndex) throws SQLException {
	SimulationJobStatusPersistent[] jobStatus = jobDB.getSimulationJobStatusArray(con,simKey,jobIndex,false);
	return jobStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:35:44 PM)
 * @return cbit.vcell.solvers.SimulationJobStatus[]
 * @param bActiveOnly boolean
 * @param owner cbit.vcell.server.User
 */
SimulationJobStatusPersistent[] getSimulationJobStatus(Connection con, boolean bActiveOnly, User owner) throws java.sql.SQLException, DataAccessException {
	return jobDB.getSimulationJobStatus(con,bActiveOnly,owner);
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:35:44 PM)
 * @return cbit.vcell.solvers.SimulationJobStatus[]
 * @param bActiveOnly boolean
 * @param owner cbit.vcell.server.User
 */
SimulationJobStatusPersistent[] getSimulationJobStatus(boolean bActiveOnly, User owner, boolean bEnableRetry) throws java.sql.SQLException, DataAccessException {

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
SimulationStatusPersistent[] getSimulationStatus(KeyValue simulationKeys[], boolean bEnableRetry) throws java.sql.SQLException, DataAccessException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		SimulationJobStatusPersistent[] jobStatuses = jobDB.getSimulationJobStatus(con,simulationKeys);
		SimulationStatusPersistent[] simStatuses = new SimulationStatusPersistent[simulationKeys.length];
		for (int i = 0; i < simulationKeys.length; i++){
			Vector<SimulationJobStatusPersistent> v = new Vector<SimulationJobStatusPersistent>();
			for (int j = 0; j < jobStatuses.length; j++){
				if(jobStatuses[j].getVCSimulationIdentifier().getSimulationKey().equals(simulationKeys[i])) {
					v.add(jobStatuses[j]);
				}
			}
			if (v.isEmpty()) {
				simStatuses[i] = null;
			} else {
				simStatuses[i] = new SimulationStatusPersistent((SimulationJobStatusPersistent[])org.vcell.util.BeanUtils.getArray(v, SimulationJobStatusPersistent.class));
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
SimulationStatusPersistent getSimulationStatus(KeyValue simKey, boolean bEnableRetry) throws java.sql.SQLException, DataAccessException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		SimulationJobStatusPersistent[] jobStatuses = jobDB.getSimulationJobStatus(con,simKey);
		if (jobStatuses.length > 0) {
			return new SimulationStatusPersistent(jobStatuses);
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
 * @param userid username
 * @param digestedPassword
 * @param bEnableRetry try again if first attempt fails
 * @param isLocal TODO
 * @return User object
 * @throws DataAccessException
 * @throws java.sql.SQLException
 * @throws ObjectNotFoundException
 */
public User getUser(String userid, UserLoginInfo.DigestedPassword digestedPassword, boolean bEnableRetry, boolean isLocal) 
				throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return userDB.getUserFromUseridAndPassword(con, userid, digestedPassword, isLocal);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getUser(userid, digestedPassword, false, isLocal);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	} finally {
		conFactory.release(con,lock);
	}
}


public ApiAccessToken generateApiAccessToken(KeyValue apiClientKey, User user, Date expirationDate, boolean bEnableRetry) 
				throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		ApiAccessToken apiAccessToken = userDB.generateApiAccessToken(con, apiClientKey, user, expirationDate);
		con.commit();
		return apiAccessToken;
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
			return generateApiAccessToken(apiClientKey, user, expirationDate, false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	} finally {
		conFactory.release(con,lock);
	}
}


public void setApiAccessTokenStatus(ApiAccessToken accessToken, AccessTokenStatus newAccessTokenStatus, boolean bEnableRetry) throws SQLException, DataAccessException {
	
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		userDB.setApiAccessTokenStatus(con, accessToken.getKey(), newAccessTokenStatus);
		con.commit();
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
			setApiAccessTokenStatus(accessToken, newAccessTokenStatus, false);
		}else{
			handle_DataAccessException_SQLException(e);
		}
	} finally {
		conFactory.release(con,lock);
	}
}


public ApiAccessToken getApiAccessToken(String accessToken, boolean bEnableRetry) throws SQLException, DataAccessException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return userDB.getApiAccessToken(con, accessToken);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getApiAccessToken(accessToken, false);
		}else{
			handle_DataAccessException_SQLException(e);
			return null; // never gets here;
		}
	} finally {
		conFactory.release(con,lock);
	}
}


public ApiClient getApiClient(String clientId, boolean bEnableRetry) throws SQLException, DataAccessException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return userDB.getApiClient(con, clientId);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getApiClient(clientId, false);
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
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
public User getUser(String userid, boolean bEnableRetry) throws DataAccessException, java.sql.SQLException {

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
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception cbit.sql.RecordChangedException The exception description.
 */
public User getUserFromSimulationKey(KeyValue simKey, boolean bEnableRetry) throws DataAccessException, java.sql.SQLException {

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
 * @exception org.vcell.util.DataAccessException The exception description.
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

void sendLostPassword(String userid, boolean bEnableRetry) throws DataAccessException, java.sql.SQLException, ObjectNotFoundException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		userDB.sendLostPassword(con,userid);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			sendLostPassword(userid, false);
		}else{
			handle_DataAccessException_SQLException(e);
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
 * @exception org.vcell.util.DataAccessException The exception description.
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
public void insertSimulationJobStatus(SimulationJobStatusPersistent simulationJobStatus, boolean bEnableRetry) throws SQLException, DataAccessException, UpdateSynchronizationException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		insertSimulationJobStatus(con, simulationJobStatus);
		con.commit();
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
		}else{
			handle_DataAccessException_SQLException(e);
		}
	}finally{
		conFactory.release(con,lock);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/3/2005 3:33:09 PM)
 */
void insertSimulationJobStatus(Connection con, SimulationJobStatusPersistent simulationJobStatus) throws SQLException, UpdateSynchronizationException {
	jobDB.insertSimulationJobStatus(con,simulationJobStatus, DbDriver.getNewKey(con));
	VCMongoMessage.sendSimJobStatusInsert(simulationJobStatus);
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.UserInfo
 * @param newUserInfo cbit.sql.UserInfo
 */
KeyValue insertUserInfo(UserInfo newUserInfo, boolean bEnableRetry) throws SQLException,UseridIDExistsException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		if(userDB.getUserFromUserid(con,newUserInfo.userid) != null){
			throw new UseridIDExistsException("Insert new user failed: username '"+newUserInfo.userid+"' already exists");
		}
		KeyValue key = userDB.insertUserInfo(con,newUserInfo);
		con.commit();
		return key;
	} catch (Throwable e) {
		log.exception(e);
		if(e instanceof UseridIDExistsException){
			throw (UseridIDExistsException)e;
		}
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
public void updateSimulationJobStatus(SimulationJobStatusPersistent newSimulationJobStatus, boolean bEnableRetry) throws SQLException, DataAccessException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		updateSimulationJobStatus(con, newSimulationJobStatus);
		con.commit();
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
			updateSimulationJobStatus(newSimulationJobStatus,false);
		}else{
			handle_DataAccessException_SQLException(e);
		}
	}finally{
		conFactory.release(con,lock);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/6/2005 3:20:41 PM)
 */
void updateSimulationJobStatus(Connection con, SimulationJobStatusPersistent newSimulationJobStatus) throws SQLException, UpdateSynchronizationException {
	jobDB.updateSimulationJobStatus(con,newSimulationJobStatus);
	VCMongoMessage.sendSimJobStatusUpdate(newSimulationJobStatus);
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

void updateUserStat(UserLoginInfo userLoginInfo,boolean bEnableRetry) throws SQLException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		userDB.updateUserStat(con,userLoginInfo);
		con.commit();
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
			updateUserStat(userLoginInfo,false);
		}else{
			handle_SQLException(e);
		}
	}finally{
		conFactory.release(con,lock);
	}
}


public interface TransactionalServiceOperation {
	public ServiceStatus doOperation(ServiceStatus oldStatus) throws Exception;
}

public ServiceStatus insertServiceStatus(ServiceStatus serviceStatus, boolean bEnableRetry) throws SQLException, UpdateSynchronizationException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {		
		ServiceStatus currentServiceStatus = serviceStatusDB.getServiceStatus(con,serviceStatus.getServiceSpec().getServerID(), 
				serviceStatus.getServiceSpec().getType(), serviceStatus.getServiceSpec().getOrdinal(), false);
		if (currentServiceStatus != null){
			throw new UpdateSynchronizationException("service already exists:" + currentServiceStatus);
		}
		serviceStatusDB.insertServiceStatus(con, serviceStatus, DbDriver.getNewKey(con));
		con.commit();
		ServiceStatus newServiceStatus = serviceStatusDB.getServiceStatus(con,serviceStatus.getServiceSpec().getServerID(), 
				serviceStatus.getServiceSpec().getType(), serviceStatus.getServiceSpec().getOrdinal(), false);		
		return newServiceStatus;
	}  catch (Throwable e) {
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return insertServiceStatus(serviceStatus, false);
		}else{
			handle_SQLException(e);
			return null; // never gets here;
		}
	}finally{
		conFactory.release(con,lock);
	}
}

public void deleteServiceStatus(ServiceStatus serviceStatus, boolean bEnableRetry) throws SQLException, UpdateSynchronizationException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {		
		ServiceStatus currentServiceStatus = serviceStatusDB.getServiceStatus(con,serviceStatus.getServiceSpec().getServerID(), 
				serviceStatus.getServiceSpec().getType(), serviceStatus.getServiceSpec().getOrdinal(), false);
		if (currentServiceStatus == null){
			throw new UpdateSynchronizationException("service doesn't exist:" + currentServiceStatus);
		}
		serviceStatusDB.deleteServiceStatus(con, serviceStatus, DbDriver.getNewKey(con));
		con.commit();
	}  catch (Throwable e) {
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			deleteServiceStatus(serviceStatus, false);
		}else{
			handle_SQLException(e);
		}
	}finally{
		conFactory.release(con,lock);
	}
}

public ServiceStatus modifyServiceStatus(ServiceStatus oldServiceStatus, ServiceStatus newServiceStatus, boolean bEnableRetry) throws SQLException, UpdateSynchronizationException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {		
		ServiceStatus currentServiceStatus = serviceStatusDB.getServiceStatus(con,oldServiceStatus.getServiceSpec().getServerID(), 
				oldServiceStatus.getServiceSpec().getType(), oldServiceStatus.getServiceSpec().getOrdinal(), false);
		if (!currentServiceStatus.compareEqual(oldServiceStatus)){
			throw new UpdateSynchronizationException("service doesn't exist:" + currentServiceStatus);
		}
		serviceStatusDB.updateServiceStatus(con,newServiceStatus);
		con.commit();
		ServiceStatus updatedServiceStatus = serviceStatusDB.getServiceStatus(con, oldServiceStatus.getServiceSpec().getServerID(), 
				oldServiceStatus.getServiceSpec().getType(), oldServiceStatus.getServiceSpec().getOrdinal(), false);
		return updatedServiceStatus;
	}  catch (Throwable e) {
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return modifyServiceStatus(oldServiceStatus, newServiceStatus, false);
		}else{
			handle_SQLException(e);
			return null;
		}
	}finally{
		conFactory.release(con,lock);
	}
}

public ServiceStatus updateServiceStatus(ServiceStatus oldServiceStatus, TransactionalServiceOperation serviceOP, boolean bEnableRetry) throws SQLException, UpdateSynchronizationException {
	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {	
		ServiceStatus currentServiceStatus = serviceStatusDB.getServiceStatus(con,oldServiceStatus.getServiceSpec().getServerID(), 
				oldServiceStatus.getServiceSpec().getType(), oldServiceStatus.getServiceSpec().getOrdinal(), true);
		if (!currentServiceStatus.compareEqual(oldServiceStatus)){			
			throw new UpdateSynchronizationException("current service status " + currentServiceStatus + " doesn't match argument for "+ oldServiceStatus);
		}
		ServiceStatus newServiceStatus = null;
		try {
			newServiceStatus = serviceOP.doOperation(oldServiceStatus);
		} catch (Exception ex) {
			log.exception(ex);
			throw new RuntimeException("transactional operation failed for " + newServiceStatus + " : " + ex.getMessage());
		}
		serviceStatusDB.updateServiceStatus(con,newServiceStatus);
		con.commit();
		ServiceStatus updatedServiceStatus = serviceStatusDB.getServiceStatus(con, oldServiceStatus.getServiceSpec().getServerID(), 
				oldServiceStatus.getServiceSpec().getType(), oldServiceStatus.getServiceSpec().getOrdinal(), false);
		return updatedServiceStatus;
	}  catch (Throwable e) {
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return updateServiceStatus(oldServiceStatus,serviceOP,false);
		}else{
			handle_SQLException(e);
			return null; // never gets here;
		}
	}finally{
		conFactory.release(con,lock);
	}
}

public java.util.List<ServiceStatus> getAllServiceStatus(boolean bEnableRetry) throws java.sql.SQLException, org.vcell.util.DataAccessException {

	Object lock = new Object();
	Connection con = conFactory.getConnection(lock);
	try {
		return serviceStatusDB.getAllServiceStatus(con);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getAllServiceStatus(false);
		}else{
			handle_SQLException(e);
			return null; // never gets here;
		}
	} finally {
		conFactory.release(con,lock);
	}
}
}
