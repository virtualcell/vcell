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
