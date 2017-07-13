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
import java.util.List;

import org.vcell.db.ConnectionFactory;
import org.vcell.db.KeyFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.UseridIDExistsException;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.server.AdminDatabaseServer;
import cbit.vcell.server.SimpleJobStatusPersistent;
import cbit.vcell.server.SimulationJobStatusPersistent;
import cbit.vcell.server.UpdateSynchronizationException;

/**
 * This type was created in VisualAge.
 */
public class LocalAdminDbServer implements AdminDatabaseServer {
	private SessionLog log = null;
	private AdminDBTopLevel adminDbTop = null;

/**
 * LocalAdminDbServer constructor comment.
 * @exception java.rmi.RemoteException The exception description.
 */
public LocalAdminDbServer(ConnectionFactory conFactory, KeyFactory keyFactory, SessionLog sessionLog) 
		throws DataAccessException {

	this.log = sessionLog;
	DbDriver.setKeyFactory(keyFactory);
	try {
		adminDbTop = new AdminDBTopLevel(conFactory,log);
	} catch (Throwable e) {
		log.exception(e);
		throw new DataAccessException("Error creating AdminDbTop " + e.getMessage());
	}		
}

public ExternalDataIdentifier[] getExternalDataIdentifiers(User fieldDataOwner) throws DataAccessException {
	try {
		return adminDbTop.getExternalDataIdentifiers(fieldDataOwner,true);
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure getExternalDataIdentifierKeys");
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
public SimulationJobStatusPersistent[] getSimulationJobStatusArray(KeyValue simKey, int jobIndex) throws DataAccessException {
	try {
		return adminDbTop.getSimulationJobStatusArray(simKey,jobIndex,true);
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure getting SimulationJobStatus");
	}
}

public SimulationJobStatusPersistent getSimulationJobStatus(KeyValue simKey, int jobIndex, int taskID) throws DataAccessException {
	try {
		return adminDbTop.getSimulationJobStatus(simKey,jobIndex,taskID,true);
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure getting SimulationJobStatus");
	}
}

/**
 * getSimulationJobStatus method comment.
 */
public List<SimpleJobStatusPersistent> getSimulationJobStatus(java.lang.String conditions, int startRow, int maxNumRows) throws DataAccessException {
	try {
		return adminDbTop.getSimpleJobStatus(conditions, startRow, maxNumRows, true);
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure getting SimulationJobStatus");
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
public SimulationJobStatusPersistent[] getSimulationJobStatus(boolean bActiveOnly, org.vcell.util.document.User userOnly) throws DataAccessException {
	try {
		return adminDbTop.getSimulationJobStatus(bActiveOnly,userOnly,true);
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure getting SimulationJobStatus");
	}
}


/**
 * getUser method comment.
 */
public User getUser(String userid) throws DataAccessException {
	try {
		return adminDbTop.getUser(userid,true);
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure getting user "+userid);
	}
}


/**
 * getUser method comment.
 */
public User getUser(String userid, UserLoginInfo.DigestedPassword digestedPassword, boolean runningLocally) throws DataAccessException {
	try {
		return adminDbTop.getUser(userid,digestedPassword,true, runningLocally);
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure authenticating user "+userid);
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.User
 * @param userid java.lang.String
 * @param password java.lang.String
 */
public org.vcell.util.document.User getUserFromSimulationKey(org.vcell.util.document.KeyValue simKey) throws DataAccessException {
	try {
		return adminDbTop.getUserFromSimulationKey(simKey,true);
	} catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure authenticating user ");
	}
}


/**
 * getUser method comment.
 */
public UserInfo getUserInfo(KeyValue userKey) throws DataAccessException {
	try {
		return adminDbTop.getUserInfo(userKey,true);
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure getting userInfo with key="+userKey);
	}
}


/**
 * getUser method comment.
 */
public UserInfo[] getUserInfos() throws DataAccessException {
	try {
		return adminDbTop.getUserInfos(true);
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure getting userInfos");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:34:12 PM)
 * @return cbit.vcell.solvers.SimulationJobStatus
 * @param simulationJobStatus cbit.vcell.solvers.SimulationJobStatus
 * @exception java.rmi.RemoteException The exception description.
 */
public void insertSimulationJobStatus(SimulationJobStatusPersistent simulationJobStatus) throws DataAccessException, UpdateSynchronizationException {
	try {
		adminDbTop.insertSimulationJobStatus(simulationJobStatus,true);
	}catch (UpdateSynchronizationException ex){
		throw ex;
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure inserting SimulationJobStatus: "+simulationJobStatus);
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.UserInfo
 * @param newUserInfo cbit.sql.UserInfo
 */
public org.vcell.util.document.UserInfo insertUserInfo(UserInfo newUserInfo) throws DataAccessException,UseridIDExistsException {
	try {
		KeyValue key = adminDbTop.insertUserInfo(newUserInfo,true);
		return adminDbTop.getUserInfo(key,true);
	}catch (UseridIDExistsException e){
		log.exception(e);
		throw e;
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:34:12 PM)
 * @return cbit.vcell.solvers.SimulationJobStatus
 * @param simulationJobStatus cbit.vcell.solvers.SimulationJobStatus
 * @exception java.rmi.RemoteException The exception description.
 */
public void updateSimulationJobStatus(SimulationJobStatusPersistent simulationJobStatus) throws DataAccessException {
	try {
		adminDbTop.updateSimulationJobStatus(simulationJobStatus,true);
	} catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure updating SimulationJobStatus: "+simulationJobStatus);
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.UserInfo
 * @param newUserInfo cbit.sql.UserInfo
 */
public UserInfo updateUserInfo(UserInfo newUserInfo) throws DataAccessException {
	try {
		KeyValue key = adminDbTop.updateUserInfo(newUserInfo,true);
		return adminDbTop.getUserInfo(key,true);
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure updating user '"+newUserInfo.userid+"'\n"+e.getMessage());
	}
}

public void updateUserStat(UserLoginInfo userLoginInfo) throws DataAccessException {
	try {
		adminDbTop.updateUserStat(userLoginInfo,true);
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure updating user stat '"+userLoginInfo.getUserName()+"'\n"+e.getMessage());
	}
}


public void sendLostPassword(String userid) throws DataAccessException {
	try {
		adminDbTop.sendLostPassword(userid,true);
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure sending password for user '"+userid+"'\n"+e.getMessage());
	}
}

}
