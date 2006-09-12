package cbit.rmi.event;


import java.rmi.*;/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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
