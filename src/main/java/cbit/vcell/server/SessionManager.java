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
import org.vcell.util.document.User;

import cbit.vcell.client.server.AsynchMessageManager;
import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.field.io.FieldDataFileOperationSpec;

/**
 * Insert the type's description here.
 * Creation date: (5/25/2004 12:50:01 PM)
 * @author: Ion Moraru
 */
public interface SessionManager {
	
FieldDataFileOperationResults fieldDataFileOperation(FieldDataFileOperationSpec fieldDataFielOperationSpec) throws DataAccessException;

	
/**
 * Insert the method's description here.
 * Creation date: (5/25/2004 12:50:55 PM)
 * @return cbit.vcell.server.User
 */
User getUser();
/**
 * Insert the method's description here.
 * Creation date: (5/25/2004 12:51:14 PM)
 * @return cbit.vcell.server.UserMetaDbServer
 */
UserMetaDbServer getUserMetaDbServer() throws DataAccessException;

SimulationController getSimulationController() throws DataAccessException;


AsynchMessageManager getAsynchMessageManager();
}
