/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server;

import org.vcell.util.DataAccessException;
import org.vcell.util.UseridIDExistsException;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.messaging.db.UpdateSynchronizationException;

/**
 * This type was created in VisualAge.
 */
public interface AdminDatabaseServer {
	
ExternalDataIdentifier[] getExternalDataIdentifiers(User fielddataOwner) throws DataAccessException;

/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:33:54 PM)
 * @return cbit.vcell.solvers.SimulationJobStatus[]
 * @param bActiveOnly boolean
 * @param userOnly cbit.vcell.server.User
 * @exception java.rmi.RemoteException The exception description.
 */

SimulationJobStatusPersistent[] getSimulationJobStatusArray(KeyValue simKey, int jobIndex) throws DataAccessException;

SimulationJobStatusPersistent getSimulationJobStatus(KeyValue simKey, int jobIndex, int taskID) throws DataAccessException;


java.util.List<SimpleJobStatusPersistent> getSimulationJobStatus(String conditions, int startRow, int maxNumRows) throws DataAccessException;
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:33:54 PM)
 * @return cbit.vcell.solvers.SimulationJobStatus[]
 * @param bActiveOnly boolean
 * @param userOnly cbit.vcell.server.User
 * @exception java.rmi.RemoteException The exception description.
 */
SimulationJobStatusPersistent[] getSimulationJobStatus(boolean bActiveOnly, User userOnly) throws DataAccessException;
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.User
 * @param userid java.lang.String
 * @param password java.lang.String
 */
User getUser(String userid) throws DataAccessException;
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.User
 * @param userid java.lang.String
 * @param runningLocally TODO
 * @param password java.lang.String
 */
User getUser(String userid, UserLoginInfo.DigestedPassword digestedPassword, boolean runningLocally) throws DataAccessException;
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.User
 * @param userid java.lang.String
 * @param password java.lang.String
 */
User getUserFromSimulationKey(KeyValue simKey) throws DataAccessException;
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.User
 * @param userid java.lang.String
 * @param password java.lang.String
 */
UserInfo getUserInfo(KeyValue userKey) throws DataAccessException;
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.User
 * @param userid java.lang.String
 * @param password java.lang.String
 */
UserInfo[] getUserInfos() throws DataAccessException;
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:30:21 PM)
 * @return cbit.vcell.solvers.SimulationJobStatus
 * @param simulationJobStatus cbit.vcell.solvers.SimulationJobStatus
 * @exception java.rmi.RemoteException The exception description.
 */
void insertSimulationJobStatus(SimulationJobStatusPersistent simulationJobStatus) throws DataAccessException, UpdateSynchronizationException;
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.User
 * @param userid java.lang.String
 * @param password java.lang.String
 */
org.vcell.util.document.UserInfo insertUserInfo(org.vcell.util.document.UserInfo newUserInfo) throws DataAccessException,UseridIDExistsException;
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:30:21 PM)
 * @return cbit.vcell.solvers.SimulationJobStatus
 * @param simulationJobStatus cbit.vcell.solvers.SimulationJobStatus
 * @exception java.rmi.RemoteException The exception description.
 */
void updateSimulationJobStatus(SimulationJobStatusPersistent simulationJobStatus) throws DataAccessException, UpdateSynchronizationException;
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.User
 * @param userid java.lang.String
 * @param password java.lang.String
 */
org.vcell.util.document.UserInfo updateUserInfo(org.vcell.util.document.UserInfo newUserInfo) throws DataAccessException;

void sendLostPassword(String userid) throws DataAccessException;
void updateUserStat(UserLoginInfo userLoginInfo) throws DataAccessException;
}
