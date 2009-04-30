package cbit.vcell.server;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.rmi.*;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.sql.*;
import cbit.vcell.messaging.admin.SimpleJobStatus;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.simdata.ExternalDataIdentifier;

/**
 * This type was created in VisualAge.
 */
public interface AdminDatabaseServer extends java.rmi.Remote {
	
ExternalDataIdentifier[] getExternalDataIdentifiers(User fielddataOwner) throws RemoteException, DataAccessException;

/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:33:54 PM)
 * @return cbit.vcell.solvers.SimulationJobStatus[]
 * @param bActiveOnly boolean
 * @param userOnly cbit.vcell.server.User
 * @exception java.rmi.RemoteException The exception description.
 */

SimulationJobStatus getSimulationJobStatus(KeyValue simKey, int jobIndex) throws RemoteException, DataAccessException;
	public java.util.List<SimpleJobStatus> getSimulationJobStatus(String conditions) throws RemoteException, DataAccessException;
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:33:54 PM)
 * @return cbit.vcell.solvers.SimulationJobStatus[]
 * @param bActiveOnly boolean
 * @param userOnly cbit.vcell.server.User
 * @exception java.rmi.RemoteException The exception description.
 */
SimulationJobStatus[] getSimulationJobStatus(boolean bActiveOnly, User userOnly) throws RemoteException, DataAccessException;
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.User
 * @param userid java.lang.String
 * @param password java.lang.String
 */
User getUser(String userid) throws RemoteException, DataAccessException;
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.User
 * @param userid java.lang.String
 * @param password java.lang.String
 */
User getUser(String userid, String password) throws RemoteException, DataAccessException;
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.User
 * @param userid java.lang.String
 * @param password java.lang.String
 */
User getUserFromSimulationKey(KeyValue simKey) throws RemoteException, DataAccessException;
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.User
 * @param userid java.lang.String
 * @param password java.lang.String
 */
UserInfo getUserInfo(KeyValue userKey) throws RemoteException, DataAccessException;
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.User
 * @param userid java.lang.String
 * @param password java.lang.String
 */
UserInfo[] getUserInfos() throws RemoteException, DataAccessException;
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:30:21 PM)
 * @return cbit.vcell.solvers.SimulationJobStatus
 * @param simulationJobStatus cbit.vcell.solvers.SimulationJobStatus
 * @exception java.rmi.RemoteException The exception description.
 */
SimulationJobStatus insertSimulationJobStatus(SimulationJobStatus simulationJobStatus) throws RemoteException, DataAccessException;
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.User
 * @param userid java.lang.String
 * @param password java.lang.String
 */
cbit.sql.UserInfo insertUserInfo(cbit.sql.UserInfo newUserInfo) throws RemoteException, DataAccessException;
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 2:30:21 PM)
 * @return cbit.vcell.solvers.SimulationJobStatus
 * @param simulationJobStatus cbit.vcell.solvers.SimulationJobStatus
 * @exception java.rmi.RemoteException The exception description.
 */
SimulationJobStatus updateSimulationJobStatus(SimulationJobStatus oldSimulationJobStatus, SimulationJobStatus newSimulationJobStatus) throws RemoteException, DataAccessException;
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.User
 * @param userid java.lang.String
 * @param password java.lang.String
 */
cbit.sql.UserInfo updateUserInfo(cbit.sql.UserInfo newUserInfo) throws RemoteException, DataAccessException;

void sendLostPassword(String userid) throws RemoteException,DataAccessException;
void updateUserStat(String userID) throws RemoteException,DataAccessException;
}
