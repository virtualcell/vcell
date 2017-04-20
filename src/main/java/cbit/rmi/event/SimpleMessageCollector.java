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
 * Creation date: (11/14/2000 12:04:30 AM)
 * @author: 
 */
public class SimpleMessageCollector implements MessageCollector {
	private javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();
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
 * Creation date: (3/29/2006 3:34:57 PM)
 * @param event cbit.rmi.event.ExportEvent
 */
public void dataJobMessage(DataJobEvent event) {
	fireMessageEvent(event);	
}
/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 6:27:20 PM)
 * @param event cbit.rmi.event.ExportEvent
 */
public void exportMessage(ExportEvent event) {
	fireMessageEvent(event);
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
 * onVCellMessageEvent method comment.
 */
public void onVCellMessageEvent(VCellMessageEvent event) {
	fireMessageEvent(event);
}
/**
 * Insert the method's description here.
 * Creation date: (3/11/2004 10:16:37 AM)
 * @param newJobStatus cbit.vcell.messaging.db.SimulationJobStatus
 * @param progress java.lang.Double
 * @param timePoint java.lang.Double
 */
public void onWorkerEvent(WorkerEvent event) {
	fireMessageEvent(event);
}
/**
 * Insert the method's description here.
 * Creation date: (9/17/2004 2:09:12 PM)
 * @param pme cbit.rmi.event.PerformanceMonitorEvent
 */
public void performanceMonitorEvent(PerformanceMonitorEvent pme) {
	fireMessageEvent(pme);
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
 * Creation date: (3/11/2004 9:53:41 AM)
 * @param newJobStatus cbit.vcell.messaging.db.SimulationJobStatus
 * @param progress java.lang.Double
 * @param timePoint java.lang.Double
 */
public void simulationJobStatusChanged(SimulationJobStatusEvent simJobStatusEvent) {
	fireMessageEvent(simJobStatusEvent);
}
}
