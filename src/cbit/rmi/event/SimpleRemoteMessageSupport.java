package cbit.rmi.event;

import java.util.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.rmi.*;
/**
 * Insert the type's description here.
 * Creation date: (11/13/2000 2:54:24 PM)
 * @author: IIM
 */
public class SimpleRemoteMessageSupport extends RemoteMessageSupport {
	private SimpleMessageHandler simpleMessageHandler;
	private java.util.Vector listeners;
	private java.util.Hashtable connectedHandlerIDs = new java.util.Hashtable();
	private java.util.Hashtable rmhMessageQueues = new java.util.Hashtable();
/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 5:05:27 PM)
 * @param remoteMessageHandler cbit.rmi.event.RemoteMessageHandler
 */
public SimpleRemoteMessageSupport(SimpleMessageHandler argSimpleMessageHandler) {
	if (argSimpleMessageHandler == null) {
		throw new NullPointerException();
	}
	this.simpleMessageHandler = argSimpleMessageHandler;
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 4:46:20 PM)
 * @param listener cbit.rmi.event.RemoteMessageListener
 */
public synchronized void addRemoteMessageListener(RemoteMessageListener listener, long targetID){
	if (hasListener(listener)){
		return;
	}
    connectedHandlerIDs.put(listener, new Long(targetID));
    rmhMessageQueues.put(listener, new Vector());
    if (listeners == null) {
        listeners = new java.util.Vector();
    }
    listeners.addElement(listener);
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 4:46:20 PM)
 * @param messageEvent cbit.rmi.event.RemoteMessageEvent
 */
protected void fireRemoteMessageEvent(RemoteMessageEvent event) {
	//
	// BLOCKING message posting (messages get sent or fails before method returns).
	//
	java.util.Vector targets;
	synchronized (this) {
	    if (listeners == null) {
	    	return;
	    }
	    targets = (java.util.Vector) listeners.clone();
	}
	for (int i = 0; i < targets.size(); i++) {
	    RemoteMessageListener target = (RemoteMessageListener)targets.elementAt(i);
	    // do not send back to the sender...
	    if (event.getRemoteSource() != ((Long)connectedHandlerIDs.get(target)).longValue()) {
			RemoteMessageEvent relayedEvent = null;
			try {
			    // we need to reset the source of the event before we send the event, DUH !
			    relayedEvent = new RemoteMessageEvent(simpleMessageHandler.getInstanceID(), event.getMessageEvent());
			    target.remoteMessage(relayedEvent);
				System.out.println("SimpleRemoteMessageSupport: message delivered to target="+target+", event="+event);
		    } catch (RemoteException exc) {
			    Vector messageQueue = (Vector)rmhMessageQueues.get(target);
			    if (messageQueue!=null){
				    messageQueue.add(relayedEvent);
				    System.out.println("SimpleRemoteMessageSupport: message queued (#"+messageQueue.size()+"), target="+target+", event="+event+", exception="+exc.getMessage());
			    }else{
				    System.out.println("SimpleRemoteMessageSupport: message discarded, queue not found for target="+target+", event="+event+", exception="+exc.getMessage());
			    }
		    }
	    }
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/29/2001 11:32:24 PM)
 * @return cbit.rmi.event.RemoteMessageEvent[]
 * @param destination cbit.rmi.event.RemoteMessageListener
 */
public synchronized RemoteMessageEvent[] flushMessageQueue(RemoteMessageListener destination) {
	Vector vector = (Vector)rmhMessageQueues.get(destination);
	RemoteMessageEvent[] queuedMessages = (RemoteMessageEvent[])org.vcell.util.BeanUtils.getArray(vector, RemoteMessageEvent.class);
	vector.clear();
	return queuedMessages;
}
/**
 * Insert the method's description here.
 * Creation date: (1/8/2001 10:22:55 AM)
 * @return boolean
 * @param remoteMessageListener cbit.rmi.event.RemoteMessageListener
 */
public boolean hasListener(RemoteMessageListener remoteMessageListener) {
	if (listeners == null){
		return false;
	}else{
		return listeners.contains(remoteMessageListener);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 4:46:20 PM)
 */
protected void poll(boolean reportPerf) {
	// ignore performance reporting in this implementation..
	java.util.Vector targets;
	synchronized (this) {
	    if (listeners == null) {
	    	return;
	    }
	    targets = (java.util.Vector) listeners.clone();
	}
	for (int i = 0; i < targets.size(); i++) {
	    RemoteMessageListener target = (RemoteMessageListener)targets.elementAt(i);
	    RemoteMessageEvent[] queuedEvents = null;
	    try {
		    queuedEvents = target.flushMessageQueue(simpleMessageHandler);
	    } catch (RemoteException exc) {
		    System.out.println(">> polling failure << " + exc.getMessage());
	    }
	    if (queuedEvents != null) {
		    for (int j=0;j<queuedEvents.length;j++){
		    	simpleMessageHandler.remoteMessage(queuedEvents[j]);
		    }
	    }
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 4:46:20 PM)
 * @param listener cbit.rmi.event.RemoteMessageListener
 */
public synchronized void removeRemoteMessageListener(RemoteMessageListener listener) {
    if (listeners == null) {
        listeners = new java.util.Vector();
    }
    listeners.removeElement(listener);
    connectedHandlerIDs.remove(listener);
    rmhMessageQueues.remove(listener);
}
}
