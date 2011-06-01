/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.rmi.event;

import java.rmi.*;

/**
 * Insert the type's description here.
 * Creation date: (11/12/2000 8:16:16 PM)
 * @author: IIM
 */
public interface RemoteMessageHandler extends Remote, RemoteMessageSender, RemoteMessageListener {
/**
 * Insert the method's description here.
 * Creation date: (1/8/2001 10:24:45 AM)
 * @return boolean
 * @param remoteMessageHandler cbit.rmi.event.RemoteMessageHandler
 * @exception java.rmi.RemoteException The exception description.
 */
boolean isConnected(RemoteMessageHandler remoteMessageHandler) throws java.rmi.RemoteException;
}
