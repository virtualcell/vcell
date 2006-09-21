package cbit.rmi.event;


import java.rmi.*;/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (11/13/2000 2:41:03 PM)
 * @author: IIM
 */
public interface RemoteMessageListener extends Remote, java.util.EventListener {
/**
 * Insert the method's description here.
 * Creation date: (10/30/2001 12:50:33 AM)
 * @return cbit.rmi.event.RemoteMessageEvent[]
 * @param destination cbit.rmi.event.RemoteMessageListener
 * @exception java.rmi.RemoteException The exception description.
 */
RemoteMessageEvent[] flushMessageQueue(RemoteMessageListener destination) throws java.rmi.RemoteException;
/**
 * Insert the method's description here.
 * Creation date: (5/15/2001 5:44:22 PM)
 * @return long
 * @exception java.rmi.RemoteException The exception description.
 */
long getRemoteMesssageListenerID() throws RemoteException;
/**
 * 
 * @param event cbit.rmi.event.MessageEvent
 */
void remoteMessage(cbit.rmi.event.RemoteMessageEvent event) throws java.rmi.RemoteException;
}
