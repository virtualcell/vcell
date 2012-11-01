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
import java.rmi.RemoteException;
import java.util.Date;

import org.vcell.util.CacheStatus;
import org.vcell.util.document.User;
/**
 * This type was created in VisualAge.
 */
public interface VCellServer extends java.rmi.Remote {

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
