package cbit.vcell.server;
import java.rmi.RemoteException;
import java.util.Date;

import org.vcell.util.CacheStatus;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;
/**
 * This type was created in VisualAge.
 */
public interface VCellServer extends java.rmi.Remote {

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
 * @return CacheStatus
 */
ServerInfo getServerInfo() throws RemoteException;

/**
 * This method was created in VisualAge.
 * @exception java.rmi.RemoteException The exception description.
 */
Date getBootTime() throws RemoteException;
}