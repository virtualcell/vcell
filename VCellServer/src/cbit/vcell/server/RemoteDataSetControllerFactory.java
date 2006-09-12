package cbit.vcell.server;

import cbit.util.DataAccessException;

/**
 * Insert the type's description here.
 * Creation date: (10/22/01 11:46:42 AM)
 * @author: Jim Schaff
 */
public interface RemoteDataSetControllerFactory {
/**
 * Insert the method's description here.
 * Creation date: (10/22/01 11:49:33 AM)
 * @return cbit.vcell.server.DataSetController
 */
DataSetController getRemoteDataSetController() throws DataAccessException, java.rmi.RemoteException;
}
