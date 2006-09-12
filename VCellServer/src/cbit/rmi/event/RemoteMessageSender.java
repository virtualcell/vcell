package cbit.rmi.event;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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
