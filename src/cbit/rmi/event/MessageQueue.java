package cbit.rmi.event;

import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (4/15/2002 2:23:22 PM)
 * @author: Jim Schaff
 */
public class MessageQueue {
	private LinkedList<RemoteMessageEvent> q = new LinkedList<RemoteMessageEvent>();
	private Date dateWhenLastPopAll = new Date();   // is receiver polling?
	private int failedToSendCount = 0;
/**
 * MessageQueue constructor comment.
 */
public MessageQueue() {
	super();
}
public synchronized void addToFront(RemoteMessageEvent remoteMessageEvent){
	q.addFirst(remoteMessageEvent);		// prepend to start of list
	this.notify();						// tell waiting threads that data is ready
}
public synchronized RemoteMessageEvent blockingPop() {
	while (q.size()==0){
		try {
			this.wait();
		}catch (InterruptedException e){}
	}
	return (RemoteMessageEvent)q.remove(0);
}
/**
 * Insert the method's description here.
 * Creation date: (5/9/2002 2:22:06 PM)
 */
public void clearFailedToSendCount() {
	failedToSendCount=0;
}
/**
 * Insert the method's description here.
 * Creation date: (9/23/2002 1:32:25 PM)
 * @return java.util.Date
 */
private java.util.Date getDateWhenLastPopAll() {
	return dateWhenLastPopAll;
}
/**
 * Insert the method's description here.
 * Creation date: (5/9/2002 2:18:05 PM)
 * @return int
 */
public int getFailedToSendCount() {
	return failedToSendCount;
}
/**
 * Insert the method's description here.
 * Creation date: (5/9/2002 2:22:34 PM)
 */
public void incrementFailedToSendCount() {
	failedToSendCount++;
}
public synchronized RemoteMessageEvent nonblockingPop() {
	if (q.size()>0){
		return (RemoteMessageEvent)q.remove(0);
	}else{
		return null;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/15/2002 2:37:42 PM)
 * @return cbit.rmi.event.RemoteMessageEvent[]
 */
public synchronized RemoteMessageEvent[] popAll() {
	setDateWhenLastPopAll(new Date());
	if (q.size()>0){
		RemoteMessageEvent[] queuedMessages = (RemoteMessageEvent[])q.toArray(new RemoteMessageEvent[q.size()]);
		q.clear();
		return queuedMessages;
	}else{
		return new RemoteMessageEvent[0];
	}
}
public synchronized void push(RemoteMessageEvent remoteMessageEvent){
	// first clear any consumable events of the same type that may be in the queue coming from the same originator
	ListIterator<RemoteMessageEvent> itr = q.listIterator();
	while (itr.hasNext()) {
		RemoteMessageEvent rme = itr.next();
		if (remoteMessageEvent.getMessageEvent().getMessageSource().equals(rme.getMessageEvent().getMessageSource())) {
			if (rme.getMessageEvent().isSupercededBy(remoteMessageEvent.getMessageEvent())) {
				itr.remove();
			} 
			if (remoteMessageEvent.getMessageEvent().isSupercededBy(rme.getMessageEvent())) {
				return;
			}
		}
	}
	// now go ahead...
	q.add(remoteMessageEvent);		// append to end of list
	this.notify();					// tell waiting threads that data is ready
}
/**
 * Insert the method's description here.
 * Creation date: (9/23/2002 1:32:25 PM)
 * @param newDateWhenLastPopAll java.util.Date
 */
private void setDateWhenLastPopAll(java.util.Date newDateWhenLastPopAll) {
	dateWhenLastPopAll = newDateWhenLastPopAll;
}
/**
 * Insert the method's description here.
 * Creation date: (4/15/2002 2:34:45 PM)
 * @return int
 */
public synchronized int size() {
	return q.size();
}
/**
 * Insert the method's description here.
 * Creation date: (5/9/2002 2:20:33 PM)
 * @return long
 */
public long timeSinceLastPopAll() {
	Date currTime = new Date();
	return currTime.getTime()-getDateWhenLastPopAll().getTime();
}
}
