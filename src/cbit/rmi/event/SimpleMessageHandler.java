package cbit.rmi.event;
import java.rmi.*;
import cbit.vcell.server.PropertyLoader;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (11/13/2000 4:46:20 PM)
 * @author: IIM
 */
public class SimpleMessageHandler extends MessageHandler implements java.rmi.Remote {
	private javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();
	private RemoteMessageSupport remoteMessageSupport;
	private long instanceID = (new java.util.Random()).nextLong();
	private int pollingInterval = 60; // seconds
	private Thread pollingThread = null;
	private boolean bClosed = false;

/**
 * SimpleRemoteMessageService constructor comment.
 * @exception java.rmi.RemoteException The exception description.
 */
public SimpleMessageHandler(boolean bAsynchronous) throws java.rmi.RemoteException {
	super(PropertyLoader.getIntProperty(PropertyLoader.rmiPortMessageHandler,0));
	if (bAsynchronous){
		remoteMessageSupport = new AsynchRemoteMessageSupport(this);
	}else{
		remoteMessageSupport = new SimpleRemoteMessageSupport(this);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/12/2000 9:48:07 PM)
 * @param listener cbit.rmi.event.MessageListener
 */
public synchronized void addMessageListener(MessageListener listener) {
	listenerList.add(MessageListener.class, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 4:46:20 PM)
 * @param listener cbit.rmi.event.RemoteMessageListener
 */
public synchronized void addRemoteMessageListener(RemoteMessageListener listener, long targetID) {
	getRemoteMessageSupport().addRemoteMessageListener(listener, targetID);
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2002 5:00:54 PM)
 */
public void close() {
	if (remoteMessageSupport instanceof AsynchRemoteMessageSupport){
		((AsynchRemoteMessageSupport)remoteMessageSupport).close();
	}
	bClosed = true;
}


/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 4:50:48 PM)
 * @param remoteMessageHandler cbit.rmi.event.RemoteMessageHandler
 * @exception java.rmi.RemoteException The exception description.
 */
public void connectDEPRECATED(RemoteMessageHandler remoteMessageHandler, long targetID) {
	//addRemoteMessageListener(remoteMessageHandler, targetID);
	//try {
		////
		//// try to connect back (but fail gracefully ... can always poll the other way).
		////
		//if (!remoteMessageHandler.isConnected(this)) {
			//remoteMessageHandler.connect(this, getInstanceID());
		//}
	//}catch (RemoteException e){
		////
		//// connect back failed, enable polling instead
		////
		//e.printStackTrace(System.out);
		//System.out.println("SimpleMessageHandler.connect("+remoteMessageHandler+") failed to connect");
		//if (pollingThread==null){
			//final int POLLING_INTERVAL_SEC = 30;
			//System.out.println("SimpleMessageHandler.connect("+remoteMessageHandler+") Enabled polling (every "+POLLING_INTERVAL_SEC+" seconds)");
			//enablePolling(POLLING_INTERVAL_SEC);
		//}
	//}
}


/**
 * Insert the method's description here.
 * Creation date: (10/29/2001 10:22:41 PM)
 * @param seconds int
 */
public void enablePolling(int seconds) {
	setPollingInterval(seconds * 1000);
	if (pollingThread == null) {
		pollingThread = new Thread(
			new Runnable() {
				public void run() {
					int counter = 0;
					while (!isClosed()) {
						try { pollingThread.sleep(getPollingInterval()); } catch (InterruptedException exc) {}
						counter += 1;
						getRemoteMessageSupport().poll(counter%5 == 0); // send performance report every now and then...
					}
					//
					// message handler was closed, then don't poll either
					//
					System.out.println("SimpleMessageHandler@"+Integer.toHexString(hashCode())+": polling thread closing");
				}
			},
			"PollingThread" + SimpleMessageHandler.this
		);
		pollingThread.setDaemon(true);
		pollingThread.start();
	}
}
		
/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 2:44:30 PM)
 * @param event cbit.rmi.event.MessageEvent
 */
protected void fireMessageEvent(MessageEvent event) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Reset the source to allow proper wiring
	event.setSource(this);
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==MessageListener.class) {
		((MessageListener)listeners[i+1]).messageEvent(event);
	    }	       
	}
}	


/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 4:46:20 PM)
 * @param messageEvent cbit.rmi.event.RemoteMessageEvent
 */
protected void fireRemoteMessageEvent(RemoteMessageEvent event) {
	getRemoteMessageSupport().fireRemoteMessageEvent(event);
}


/**
 * Insert the method's description here.
 * Creation date: (10/30/2001 12:55:32 AM)
 * @return cbit.rmi.event.RemoteMessageEvent[]
 * @param destination cbit.rmi.event.RemoteMessageListener
 * @exception java.rmi.RemoteException The exception description.
 */
public cbit.rmi.event.RemoteMessageEvent[] flushMessageQueue(RemoteMessageListener destination) throws java.rmi.RemoteException {
	RemoteMessageEvent events[] = getRemoteMessageSupport().flushMessageQueue(destination);
	if (events!=null && events.length>0){
		System.out.println("SimpleMessageHandler.flushMessageQueue("+destination+"), polling returning "+events.length+" entries");
	}
	return events;
}


/**
 * Insert the method's description here.
 * Creation date: (5/15/2001 5:37:07 PM)
 * @return long
 */
protected long getInstanceID() {
	return instanceID;
}


/**
 * Insert the method's description here.
 * Creation date: (10/29/2001 10:32:39 PM)
 * @return int
 */
private int getPollingInterval() {
	return pollingInterval;
}


/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 5:03:00 PM)
 * @return cbit.rmi.event.SimpleRemoteMessageSupport
 */
private RemoteMessageSupport getRemoteMessageSupport() {
	return remoteMessageSupport;
}


/**
 * Insert the method's description here.
 * Creation date: (9/23/2002 11:07:08 AM)
 * @return long
 * @exception java.rmi.RemoteException The exception description.
 */
public long getRemoteMesssageListenerID() throws java.rmi.RemoteException {
	return getInstanceID();
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}


/**
 * Insert the method's description here.
 * Creation date: (5/9/2002 5:26:41 PM)
 * @return boolean
 */
public boolean isClosed() {
	return bClosed;
}


/**
 * Insert the method's description here.
 * Creation date: (1/8/2001 10:19:40 AM)
 * @return boolean
 * @param remoteMessageHandler cbit.rmi.event.RemoteMessageHandler
 * @exception java.rmi.RemoteException The exception description.
 */
public boolean isConnected(RemoteMessageHandler remoteMessageHandler) throws java.rmi.RemoteException {
	return getRemoteMessageSupport().hasListener(remoteMessageHandler);
}


/**
 * Insert the method's description here.
 * Creation date: (4/29/2004 10:23:28 AM)
 * @return boolean
 */
public boolean isTimeout() {
	if (isClosed()) {
		return true;
	}
	
	if (getRemoteMessageSupport() instanceof AsynchRemoteMessageSupport) {
		return ((AsynchRemoteMessageSupport)getRemoteMessageSupport()).isTimeOut();
	}

	return false;
}


/**
 * 
 * @param message cbit.rmi.event.MessageEvent
 */
public void messageEvent(MessageEvent message) {
	fireRemoteMessageEvent(new RemoteMessageEvent(getInstanceID(), message));
}


/**
 * 
 * @param event cbit.rmi.event.MessageEvent
 */
public void remoteMessage(RemoteMessageEvent remoteMessage) {
	fireMessageEvent(remoteMessage.getMessageEvent());
	if (remoteMessage.getMessageEvent() instanceof PerformanceMonitorEvent) {
		// do not multicast these; only primary server is interested
		return;
	} else {
		 // multicasting; the support class will check in the listener list for not firing back at source
		fireRemoteMessageEvent(remoteMessage);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/12/2000 9:48:07 PM)
 * @param listener cbit.rmi.event.MessageListener
 */
public synchronized void removeMessageListener(MessageListener listener) {
	listenerList.remove(MessageListener.class, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 4:46:20 PM)
 * @param listener cbit.rmi.event.RemoteMessageListener
 */
public synchronized void removeRemoteMessageListener(RemoteMessageListener listener) throws java.rmi.RemoteException {
	getRemoteMessageSupport().removeRemoteMessageListener(listener);
}


/**
 * Insert the method's description here.
 * Creation date: (10/29/2001 10:32:39 PM)
 * @param newPollingInterval int
 */
private void setPollingInterval(int newPollingInterval) {
	pollingInterval = newPollingInterval;
}
}