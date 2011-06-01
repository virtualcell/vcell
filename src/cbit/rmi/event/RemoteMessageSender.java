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

/**
 * Insert the type's description here.
 * Creation date: (11/12/2000 8:16:16 PM)
 * @author: IIM
 */
public interface RemoteMessageSender extends java.rmi.Remote {
/**
 * Insert the method's description here.
 * Creation date: (11/12/2000 9:48:07 PM)
 * @param listener cbit.rmi.event.RemoteMessageListener
 */
public abstract void addRemoteMessageListener(RemoteMessageListener listener, long targetID) throws java.rmi.RemoteException;
/**
 * Insert the method's description here.
 * Creation date: (11/12/2000 9:48:07 PM)
 * @param listener cbit.rmi.event.RemoteMessageListener
 */
public abstract void removeRemoteMessageListener(RemoteMessageListener listener) throws java.rmi.RemoteException;
}
