package cbit.vcell.server;
import java.rmi.RemoteException;

import cbit.util.CacheStatus;
import cbit.util.DataAccessException;
import cbit.util.document.User;
import cbit.vcell.modeldb.AdminDatabaseServer;
import cbit.vcell.solvers.SimulationJob;
import cbit.vcell.solvers.SolverController;
/**
 * This type was created in VisualAge.
 */
public interface VCellServer extends java.rmi.Remote {
/**
 * Insert the method's description here.
 * Creation date: (6/28/01 6:03:04 PM)
 * @exception java.rmi.RemoteException The exception description.
 */
SolverController createSolverController(User user, SimulationJob simulationJob) throws java.rmi.RemoteException, cbit.vcell.solvers.SimExecutionException, cbit.vcell.solvers.SolverException;


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.AdminDatabaseServer
 * @exception java.rmi.RemoteException The exception description.
 */
AdminDatabaseServer getAdminDatabaseServer() throws RemoteException;


/**
 * This method was created in VisualAge.
 * @return CacheStatus
 */
CacheStatus getCacheStatus() throws RemoteException;


/**
 * This method was created in VisualAge.
 * @return java.lang.String[]
 */
User[] getConnectedUsers() throws RemoteException;


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.ConnectionPool
 * @exception java.rmi.RemoteException The exception description.
 */
ConnectionPoolStatus getConnectionPoolStatus() throws RemoteException;


/**
 * This method was created in VisualAge.
 * @return ProcessStatus
 * @exception java.rmi.RemoteException The exception description.
 */
ProcessStatus getProcessStatus() throws RemoteException;


/**
 * This method was created in VisualAge.
 * @return CacheStatus
 */
ServerInfo getServerInfo() throws RemoteException;


/**
 * This method was created in VisualAge.
 * @return CacheStatus
 */
ServerInfo[] getSlaveServerInfos() throws RemoteException;


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.VCellServer
 * @param host java.lang.String
 * @exception cbit.util.DataAccessException The exception description.
 * @exception cbit.vcell.server.AuthenticationException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 * @exception cbit.vcell.server.ConnectionException The exception description.
 */
VCellServer getSlaveVCellServer(String host) throws DataAccessException, AuthenticationException, RemoteException, ConnectionException;


/**
 * Insert the method's description here.
 * Creation date: (7/18/01 12:20:01 PM)
 * @return cbit.vcell.solvers.SolverControllerInfo
 */
cbit.vcell.solvers.SolverControllerInfo[] getSolverControllerInfos() throws RemoteException;


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.server.VCellConnection
 * @exception java.rmi.RemoteException The exception description.
 */
public VCellConnection getVCellConnection(User user) throws RemoteException, DataAccessException;


/**
 * This method was created in VisualAge.
 * @return boolean
 * @exception java.rmi.RemoteException The exception description.
 */
boolean isPrimaryServer() throws RemoteException;


/**
 * This method was created in VisualAge.
 * @exception java.rmi.RemoteException The exception description.
 */
void shutdown() throws RemoteException;
}