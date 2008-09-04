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
public class AsynchRemoteMessageSupport extends RemoteMessageSupport {
	private SimpleMessageHandler simpleMessageHandler;
	private java.util.Vector listeners;
	private java.util.Hashtable connectedHandlerIDs = new java.util.Hashtable();
	private java.util.Hashtable rmhMessageQueues = new java.util.Hashtable();
	private Thread remoteDispatchThread = null;
	private boolean bClosed = false;

	private int MAX_NUM_SEND_FAILURES = 50;
	private long MINUTE_MS = 60000;
	private long MAX_TIME_WITHOUT_POLLING_MS = 10*MINUTE_MS;
/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 5:05:27 PM)
 * @param remoteMessageHandler cbit.rmi.event.RemoteMessageHandler
 */
public AsynchRemoteMessageSupport(SimpleMessageHandler argSimpleMessageHandler) {
	if (argSimpleMessageHandler == null) {
		throw new NullPointerException();
	}
	this.simpleMessageHandler = argSimpleMessageHandler;

	this.remoteDispatchThread = new Thread(new Runnable () { 
												public void run() {
													dispatch();
												}
											});
	this.remoteDispatchThread.setName("Asynchronous Remote Message Dispatcher");
	this.remoteDispatchThread.setDaemon(true);
//	this.remoteDispatchThread.setPriority(Thread.NORM_PRIORITY-1);
	this.remoteDispatchThread.start();
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 4:46:20 PM)
 * @param listener cbit.rmi.event.RemoteMessageListener
 */
public void addRemoteMessageListener(RemoteMessageListener listener, long targetID){
	if (hasListener(listener)){
		return;
	}
	synchronized (this){
	    connectedHandlerIDs.put(listener, new Long(targetID));
	    rmhMessageQueues.put(listener, new MessageQueue());
	    if (listeners == null) {
	        listeners = new java.util.Vector();
	    }
	    listeners.addElement(listener);
	}
	System.out.println("AsynchRemoteMessageSupport@"+Integer.toHexString(hashCode())+".addRemoteMessageListener() <<<ADD LISTENER>>> numListeners="+listeners.size()+", numQueues="+rmhMessageQueues.size()+", listener="+listener);
}
/**
 * Insert the method's description here.
 * Creation date: (5/9/2002 4:57:05 PM)
 */
public void close() {
	bClosed = true;
}
/**
 * Insert the method's description here.
 * Creation date: (4/15/2002 1:53:15 PM)
 */
private void dispatch() {
	while (!isClosed()){
		//
		// sleep 1 second
		//
		try {
			Thread.currentThread().sleep(1000);
		}catch (InterruptedException e){
		}

		java.util.Vector targets;
		synchronized (this) {
		    if (listeners == null) {
		    	targets = new Vector();
		    }else{
			    targets = (java.util.Vector) listeners.clone();
		    }
		}
		
		//
		// go through list of targets and send all queued messages
		//
		for (int i = 0; i < targets.size(); i++) {
			try {
				//
				// get next target and corresponding queue
				//
			    RemoteMessageListener target = (RemoteMessageListener)targets.elementAt(i);
			    MessageQueue messageQueue = (MessageQueue)rmhMessageQueues.get(target);  // atomic, no need for synchronization

			    //
			    // for this target, while more messages (and send hasn't failed more than MAX_NUM_SEND_FAILURE times), send one-by-one, skip rest if one fails
			    //
			    boolean bTargetIsReachable=true;
			    while (bTargetIsReachable && messageQueue!=null && messageQueue.getFailedToSendCount()<MAX_NUM_SEND_FAILURES){
					//
					// remove oldest message from queue
					//
					try {
						RemoteMessageEvent remoteMessageEvent = messageQueue.nonblockingPop(); // atomic
						if (remoteMessageEvent==null){
							break;
						}
						try {
							target.remoteMessage(remoteMessageEvent);
							messageQueue.clearFailedToSendCount();
							//System.out.println("AsynchRemoteMessageSupport@"+Integer.toHexString(hashCode())+".dispatch(): message delivered to target="+target+", event="+remoteMessageEvent);
						}catch (RemoteException e){
							//
							// delivery failed, add message back to oldest position (index 0)
							//
							e.printStackTrace(System.out);
							messageQueue.incrementFailedToSendCount();
							bTargetIsReachable = false;
							messageQueue.addToFront(remoteMessageEvent); // atomic
						    System.out.println("AsynchRemoteMessageSupport@"+Integer.toHexString(hashCode())+".dispatch(): <<<DELIVERY FAILED>>> numFailed="+messageQueue.getFailedToSendCount()+", target="+target+", exception="+e.getMessage());
						}
					}catch (ArrayIndexOutOfBoundsException e){
						e.printStackTrace(System.out);
					}
			    }
			    //
			    // if this target doesn't respond and hasn't polled in a long time, then remove this listener and corresponding queue.
			    //
			    if (messageQueue!=null){
				    boolean bTimeOut = (messageQueue.timeSinceLastPopAll() > MAX_TIME_WITHOUT_POLLING_MS);
				    boolean bTooManySendFailures = (messageQueue.getFailedToSendCount() >= MAX_NUM_SEND_FAILURES);
				    if (bTimeOut && bTooManySendFailures){
					    System.out.println("AsynchRemoteMessageSupport@"+Integer.toHexString(hashCode())+".dispatch(): <<<REMOVING REMOTE LISTENER AND MESSAGE QUEUE>>> target="+target+
						    							", numQueued="+messageQueue.size()+
						    							", numSendFailures="+messageQueue.getFailedToSendCount()+
						    							", timeSinceLastPoll="+(messageQueue.timeSinceLastPopAll()/1000)+" seconds");
					    removeRemoteMessageListener(target);
				    }
			    }
			}catch (Throwable e){
				e.printStackTrace(System.out);
			}
		}
	}		
	System.out.println("AsynchRemoteMessageSupport@"+Integer.toHexString(hashCode())+".dispatch(): <<<CLOSING>>>");
	listeners.clear();
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 4:46:20 PM)
 * @param messageEvent cbit.rmi.event.RemoteMessageEvent
 */
protected void fireRemoteMessageEvent(RemoteMessageEvent event) {
	//
	// NON-BLOCKING message posting.
	//
	java.util.Vector targets;
	synchronized (this) {
	    if (listeners == null) {
	    	return;
	    }
	    targets = (java.util.Vector) listeners.clone();
	}
	//
    // we need to reset the source of the event before we queue the event
    //
	RemoteMessageEvent relayedEvent = new RemoteMessageEvent(simpleMessageHandler.getInstanceID(), event.getMessageEvent());
	
	for (int i = 0; i < targets.size(); i++) {
	    RemoteMessageListener target = (RemoteMessageListener)targets.elementAt(i);
	    //
	    // do not send the message back to the sender...
	    //
	    if (event.getRemoteSource() != ((Long)connectedHandlerIDs.get(target)).longValue()) {
		    //
		    // add message to target's queue (send to the target in the dispatch thread)
		    //
		    MessageQueue messageQueue = (MessageQueue)rmhMessageQueues.get(target);
		    if (messageQueue!=null){
		    	messageQueue.push(relayedEvent);
			    //System.out.println("AsynchRemoteMessageSupport@"+Integer.toHexString(hashCode())+".fireRemoteMessageEvent(): message queued (#"+messageQueue.size()+"), target="+target+", event="+event);
		    }
	    }
	}
	//
	// return immediately (don't wait for message to be sent)
	//
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (10/29/2001 11:32:24 PM)
 * @return cbit.rmi.event.RemoteMessageEvent[]
 * @param destination cbit.rmi.event.RemoteMessageListener
 */
public RemoteMessageEvent[] flushMessageQueue(RemoteMessageListener destination) {
	synchronized (this){
		MessageQueue messageQueue = (MessageQueue)rmhMessageQueues.get(destination);
		if (messageQueue!=null){
			RemoteMessageEvent[] queuedMessages = messageQueue.popAll();
			return queuedMessages;
		}else{
			return new RemoteMessageEvent[0];
		}
	}
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
 * Creation date: (5/9/2002 4:57:30 PM)
 * @return boolean
 */
public boolean isClosed() {
	return bClosed;
}
/**
 * Insert the method's description here.
 * Creation date: (4/15/2002 1:53:15 PM)
 */
protected boolean isTimeOut() {
	if (isClosed() || listeners == null){
		return true;
	}
		
	Vector targets = (Vector) listeners.clone();
		
	//
	// go through list of targets
	//
	for (int i = 0; i < targets.size(); i++) {
	    RemoteMessageListener target = (RemoteMessageListener)targets.elementAt(i);
	    MessageQueue messageQueue = (MessageQueue)rmhMessageQueues.get(target);

	    if (messageQueue != null){
		    if (messageQueue.timeSinceLastPopAll() <= MAX_TIME_WITHOUT_POLLING_MS) {
			    return false;
		    }
	    }
	}

	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 4:46:20 PM)
 */
protected void poll(boolean reportPerf) {
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
	    //
	    // ask remote message listener (really should be "message producer") for any queued events.
	    //
	    try {
		    // time the call
		    long l1 = System.currentTimeMillis();
		    queuedEvents = target.flushMessageQueue(simpleMessageHandler);
		    long l2 = System.currentTimeMillis();
		    double duration = ((double)(l2 - l1)) / 1000;
		    // deal with events, if any
		    if (queuedEvents != null) {
			    for (int j=0;j<queuedEvents.length;j++){
			    	simpleMessageHandler.remoteMessage(queuedEvents[j]);
			    }
		    }
		    // report polling call performance
		    if (reportPerf) {
			    fireRemoteMessageEvent(
				    new RemoteMessageEvent(
					    simpleMessageHandler.getInstanceID(),
					    new PerformanceMonitorEvent(
						    this, null, new PerformanceData(
							    "RemoteMessageListener.flushMessageQueue()",
							    MessageEvent.POLLING_STAT,
							    new PerformanceDataEntry[] {new PerformanceDataEntry("remote call duration", Double.toString(duration))}
						    )
					    )
				    )
			    );
		    }
	    } catch (RemoteException exc) {
		    System.out.println(">> polling failure << " + exc.getMessage());
	    }
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/2000 4:46:20 PM)
 * @param listener cbit.rmi.event.RemoteMessageListener
 */
public void removeRemoteMessageListener(RemoteMessageListener listener) {
    if (listeners == null) {
        listeners = new java.util.Vector();
    }
    synchronized (this){
	    listeners.removeElement(listener);
	    connectedHandlerIDs.remove(listener);
	    rmhMessageQueues.remove(listener);
    }
	System.out.println("AsynchRemoteMessageSupport@"+Integer.toHexString(hashCode())+".removeRemoteMessageListener() <<<REMOVE LISTENER>>> numListeners="+listeners.size()+", numQueues="+rmhMessageQueues.size()+", listener="+listener);
}
}
