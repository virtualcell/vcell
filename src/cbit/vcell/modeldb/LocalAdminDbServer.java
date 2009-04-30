package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.rmi.*;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.sql.*;
import cbit.vcell.server.SessionLog;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.server.PropertyLoader;
import cbit.vcell.simdata.ExternalDataIdentifier;

/**
 * This type was created in VisualAge.
 */
public class LocalAdminDbServer extends java.rmi.server.UnicastRemoteObject implements cbit.vcell.server.AdminDatabaseServer {
	private SessionLog log = null;
	private AdminDBTopLevel adminDbTop = null;

/**
 * LocalAdminDbServer constructor comment.
 * @exception java.rmi.RemoteException The exception description.
 */
public LocalAdminDbServer(ConnectionFactory conFactory, KeyFactory keyFactory, SessionLog sessionLog) 
		throws RemoteException, DataAccessException {

	super(PropertyLoader.getIntProperty(PropertyLoader.rmiPortAdminDbServer,0));
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
public SimulationJobStatus getSimulationJobStatus(KeyValue simKey, int jobIndex) throws DataAccessException {
	try {
		return adminDbTop.getSimulationJobStatus(simKey,jobIndex,true);
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure getting SimulationJobStatus");
	}
}


/**
 * getSimulationJobStatus method comment.
 */
public java.util.List getSimulationJobStatus(java.lang.String conditions) throws DataAccessException {
	try {
		return adminDbTop.getSimulationJobStatus(conditions,true);
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
public SimulationJobStatus[] getSimulationJobStatus(boolean bActiveOnly, org.vcell.util.document.User userOnly) throws DataAccessException {
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
public User getUser(String userid, String password) throws DataAccessException {
	try {
		return adminDbTop.getUser(userid,password,true);
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
public SimulationJobStatus insertSimulationJobStatus(SimulationJobStatus simulationJobStatus) throws DataAccessException {
	try {
		return adminDbTop.insertSimulationJobStatus(simulationJobStatus,true);
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
public cbit.sql.UserInfo insertUserInfo(UserInfo newUserInfo) throws DataAccessException {
	try {
		KeyValue key = adminDbTop.insertUserInfo(newUserInfo,true);
		return adminDbTop.getUserInfo(key,true);
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure inserting user '"+newUserInfo.userid+"'\n"+e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:34:12 PM)
 * @return cbit.vcell.solvers.SimulationJobStatus
 * @param simulationJobStatus cbit.vcell.solvers.SimulationJobStatus
 * @exception java.rmi.RemoteException The exception description.
 */
public SimulationJobStatus updateSimulationJobStatus(SimulationJobStatus oldSimulationJobStatus, SimulationJobStatus simulationJobStatus) throws DataAccessException {
	try {
		return adminDbTop.updateSimulationJobStatus(oldSimulationJobStatus,simulationJobStatus,true);
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

public void updateUserStat(String userID) throws DataAccessException {
	try {
		adminDbTop.updateUserStat(userID,true);
	}catch (Throwable e){
		log.exception(e);
		throw new DataAccessException("failure updating user stat '"+userID+"'\n"+e.getMessage());
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