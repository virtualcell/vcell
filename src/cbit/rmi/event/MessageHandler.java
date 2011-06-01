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
 * All implementations of RemoteMessageHandler should subclass from here and implement java.rmi.Remote.
 * Creation date: (11/13/2000 4:46:20 PM)
 * @author: IIM
 */
public abstract class MessageHandler extends java.rmi.server.UnicastRemoteObject implements RemoteMessageHandler, MessageListener, MessageSender {
/**
 * SimpleRemoteMessageService constructor comment.
 * @exception java.rmi.RemoteException The exception description.
 */
protected MessageHandler(int port) throws java.rmi.RemoteException {
	super(port);
}
/**
 * Insert the method's description here.
 * Creation date: (10/29/2001 10:18:52 PM)
 * @param seconds int
 */
public abstract void enablePolling(int seconds);
}
