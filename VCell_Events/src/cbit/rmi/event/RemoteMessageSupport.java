package cbit.rmi.event;

/**
 * Insert the type's description here.
 * Creation date: (11/16/2001 2:38:44 PM)
 * @author: Jim Schaff
 */
public abstract class RemoteMessageSupport {
/**
 * RemoteMessageSupport constructor comment.
 */
public RemoteMessageSupport() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/2001 2:40:15 PM)
 * @param listener cbit.rmi.event.RemoteMessageListener
 * @param targetID long
 */
public abstract void addRemoteMessageListener(RemoteMessageListener listener, long targetID);
/**
 * Insert the method's description here.
 * Creation date: (11/16/2001 2:40:47 PM)
 * @param event cbit.rmi.event.RemoteMessageEvent
 */
protected abstract void fireRemoteMessageEvent(RemoteMessageEvent event);
/**
 * Insert the method's description here.
 * Creation date: (11/16/2001 2:41:25 PM)
 * @return cbit.rmi.event.RemoteMessageEvent[]
 * @param destination cbit.rmi.event.RemoteMessageListener
 */
public abstract RemoteMessageEvent[] flushMessageQueue(RemoteMessageListener destination);
/**
 * Insert the method's description here.
 * Creation date: (11/16/2001 2:41:46 PM)
 * @return boolean
 * @param remoteMessageListener cbit.rmi.event.RemoteMessageListener
 */
public abstract boolean hasListener(RemoteMessageListener remoteMessageListener);
/**
 * Insert the method's description here.
 * Creation date: (11/16/2001 2:39:32 PM)
 */
protected abstract void poll(boolean reportPerf);
/**
 * Insert the method's description here.
 * Creation date: (11/16/2001 2:42:21 PM)
 * @param listener cbit.rmi.event.RemoteMessageListener
 */
public abstract void removeRemoteMessageListener(RemoteMessageListener listener);
}
