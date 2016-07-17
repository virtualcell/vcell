/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.server;
import java.rmi.RemoteException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.vcell.util.DataAccessException;
import org.vcell.util.DataJobListenerHolder;

import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.DataJobListener;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportListener;
import cbit.rmi.event.MessageEvent;
import cbit.rmi.event.PerformanceData;
import cbit.rmi.event.PerformanceDataEntry;
import cbit.rmi.event.PerformanceMonitorEvent;
import cbit.rmi.event.SimulationJobStatusEvent;
import cbit.rmi.event.SimulationJobStatusListener;
import cbit.rmi.event.VCellMessageEvent;
import cbit.rmi.event.VCellMessageEventListener;
import cbit.vcell.client.SimStatusListener;
import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.resource.VCellExecutorService;
import cbit.vcell.server.VCellConnection;
import edu.uchc.connjur.wb.ExecutionTrace;

/**
 * {@link AsynchMessageManager} polls from {@link VCellConnection} to get remote messages. Remote Messages include the following:
 * {@link SimulationJobStatusEvent} : simulation running event.
 * {@link ExportEvent} : export data event.
 * {@link DataJobEvent} : spatial plot, kymograph, etc
 * {@link VCellMessageEvent} : broadcase messages.
 * {@link AsynchMessageManager} also listens to {@link ClientJobManager} if user stops the simulation, then it will notify {@link TopLevelWindowManager}
 * to update the status.
 */
public class AsynchMessageManager implements SimStatusListener, DataAccessException.Listener,DataJobListenerHolder {
    private static final long BASE_POLL_SECONDS = 15;
    private static final long ATTEMPT_POLL_SECONDS = 3;
	private static Logger lg = Logger.getLogger(AsynchMessageManager.class);

	private EventListenerList listenerList = new EventListenerList();
    private ClientServerManager clientServerManager = null;
    private int failureCount = 0;
    private ScheduledExecutorService executorService = null;
    private long counter = 0;
    private long pollTime = BASE_POLL_SECONDS;
	private AtomicBoolean bPoll = new AtomicBoolean(false);
	private ScheduledFuture<?> pollingHandle = null;
	/**
	 * for {@link #schedule(long)} method
	 */
	private final ReentrantLock scheduleLock = new ReentrantLock();

/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 4:55:22 PM)
 */
public AsynchMessageManager(ClientServerManager csm) {
	this.clientServerManager = csm;
}
/**
 * start polling for connection. Should be called after connect
 * no-op if already called
 */
public synchronized void startPolling() {
	if (!bPoll.get()) {
		bPoll.set(true);
		if (executorService == null) {
			executorService = VCellExecutorService.get();
			DataAccessException.addListener(this);
		}
		schedule(pollTime);
	}
}

public void stopPolling() {
	lg.trace("stopping polling");
	bPoll.set(false);
}

@Override
public void created(DataAccessException dae) {
	if (lg.isTraceEnabled()) {
		lg.trace("scheduling now due to " + dae.getMessage());
	}
	schedule(0);
}
private void poll( )  {
	if (!bPoll.get()) {
		lg.trace("polling stopped");
		return;
	}
	lg.trace("polling");
	boolean report = counter%50 == 0;
	long begin = 0;
	long end = 0;
	 //
    // ask remote message listener (really should be "message producer") for any queued events.
    //
    try {
    	MessageEvent[] queuedEvents = null;
    	if (report) {
	    	// time the call
		    begin = System.currentTimeMillis();
    	}
	    synchronized (this) {
	    	if (!clientServerManager.isStatusConnected()) {
	    		clientServerManager.attemptReconnect( );
	    		return;
	    	}
		    pollTime = BASE_POLL_SECONDS;
	    	queuedEvents = clientServerManager.getMessageEvents();
		}
    	if (report) {
		    end = System.currentTimeMillis();
    	}
	    failureCount = 0; //this is skipped if the connection has failed:w
	    // deal with events, if any
	    if (queuedEvents != null) {
		    for (MessageEvent messageEvent : queuedEvents){
		    	onMessageEvent(messageEvent);
		    }
	    }
	    // report polling call performance
	    if (report) {
		    double duration = ((double)(end - begin)) / 1000;
	    	PerformanceMonitorEvent performanceMonitorEvent = new PerformanceMonitorEvent(
			    this, null, new PerformanceData(
				    "AsynchMessageManager.poll()",
				    MessageEvent.POLLING_STAT,
				    new PerformanceDataEntry[] {new PerformanceDataEntry("remote call duration", Double.toString(duration))}
			    )
			);
			reportPerformanceMonitorEvent(performanceMonitorEvent);
	    }
    } catch (Exception exc) {
	    System.out.println(">> polling failure << " + exc.getMessage());
	    pollTime = ATTEMPT_POLL_SECONDS;
	    failureCount ++;
	    if (failureCount % 3 == 0) {
	    	bPoll.set(false);
	    	clientServerManager.setDisconnected();
	    }
    }
    finally {
    	if (lg.isTraceEnabled( )) {
    		lg.trace(ExecutionTrace.justClassName(this) + " poll time " + pollTime + " seconds");
    	}
    	if (bPoll.get()){
    		schedule(pollTime);
    	}
    }
}

/**
 * schedule poll, replacing previously scheduled instance, if any
 * @param delay
 */
private void schedule(long delay) {
	scheduleLock.lock();
	try {
		if (pollingHandle != null && !pollingHandle.isDone()) {
			pollingHandle.cancel(true);
		}
		pollingHandle =  executorService.schedule(this::poll, delay,TimeUnit.SECONDS);
	}
	finally {
		scheduleLock.unlock();
	}
}


private void onMessageEvent(MessageEvent event) {
	if (event instanceof SimulationJobStatusEvent) {
		fireSimulationJobStatusEvent((SimulationJobStatusEvent)event);
	} else if (event instanceof ExportEvent) {
		fireExportEvent((ExportEvent)event);
	} else if (event instanceof DataJobEvent) {
		fireDataJobEvent((DataJobEvent)event);
	} else if (event instanceof VCellMessageEvent) {
		fireVCellMessageEvent((VCellMessageEvent)event);
	} else {
		System.err.println("AsynchMessageManager.onMessageEvent() : unknown message event " + event);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 5:18:16 PM)
 * @param listener ExportListener
 */
public synchronized void addDataJobListener(DataJobListener listener) {
	listenerList.add(DataJobListener.class, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 5:18:16 PM)
 * @param listener ExportListener
 */
public synchronized void addExportListener(ExportListener listener) {
	listenerList.add(ExportListener.class, listener);
}


/**
 * addSimulationStatusEventListener method comment.
 */
public synchronized void addSimStatusListener(SimStatusListener listener) {
	listenerList.add(SimStatusListener.class, listener);
}


/**
 * addSimulationStatusEventListener method comment.
 */
public synchronized void addSimulationJobStatusListener(SimulationJobStatusListener listener) {
	listenerList.add(SimulationJobStatusListener.class, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (6/19/2006 12:51:56 PM)
 * @param listener cbit.vcell.desktop.controls.ExportListener
 */
public void addVCellMessageEventListener(VCellMessageEventListener listener) {
	listenerList.add(VCellMessageEventListener.class, listener);
}

/**
 * Insert the method's description here.
 * Creation date: (11/17/2000 11:43:22 AM)
 * @param event cbit.rmi.event.ExportEvent
 */
protected void fireDataJobEvent(DataJobEvent event) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Reset the source to allow proper wiring
	event.setSource(this);
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==DataJobListener.class) {
		    fireDataJobEvent(event, (DataJobListener)listeners[i+1]);
	    }
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 3:49:15 PM)
 */
private void fireDataJobEvent(final DataJobEvent event, final DataJobListener listener) {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		    listener.dataJobMessage(event);
	    }
	});
}


/**
 * Insert the method's description here.
 * Creation date: (11/17/2000 11:43:22 AM)
 * @param event cbit.rmi.event.ExportEvent
 */
protected void fireExportEvent(ExportEvent event) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Reset the source to allow proper wiring
	event.setSource(this);
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==ExportListener.class) {
		    fireExportEvent(event, (ExportListener)listeners[i+1]);
	    }
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 3:49:15 PM)
 */
private void fireExportEvent(final ExportEvent event, final ExportListener listener) {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		    listener.exportMessage(event);
	    }
	});
}


/**
 * Insert the method's description here.
 * Creation date: (11/17/2000 11:43:22 AM)
 * @param event cbit.rmi.event.JobCompletedEvent
 */
protected void fireSimStatusEvent(SimStatusEvent event) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==SimStatusListener.class) {
		    fireSimStatusEvent(event, (SimStatusListener)listeners[i+1]);
	    }
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 3:49:15 PM)
 */
private void fireSimStatusEvent(final SimStatusEvent event, final SimStatusListener listener) {
	listener.simStatusChanged(event);
}


/**
 * Insert the method's description here.
 * Creation date: (11/17/2000 11:43:22 AM)
 * @param event cbit.rmi.event.JobCompletedEvent
 */
protected void fireSimulationJobStatusEvent(SimulationJobStatusEvent event) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Reset the source to allow proper wiring
	event.setSource(this);
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==SimulationJobStatusListener.class) {
		    fireSimulationJobStatusEvent(event, (SimulationJobStatusListener)listeners[i+1]);
	    }
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 3:49:15 PM)
 */
private void fireSimulationJobStatusEvent(final SimulationJobStatusEvent event, final SimulationJobStatusListener listener) {
    listener.simulationJobStatusChanged(event);
}


/**
 * Insert the method's description here.
 * Creation date: (11/17/2000 11:43:22 AM)
 * @param event cbit.rmi.event.ExportEvent
 */
protected void fireVCellMessageEvent(VCellMessageEvent event) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Reset the source to allow proper wiring
	event.setSource(this);
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==VCellMessageEventListener.class) {
		    fireVCellMessageEvent(event, (VCellMessageEventListener)listeners[i+1]);
	    }
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 3:49:15 PM)
 */
private void fireVCellMessageEvent(final VCellMessageEvent event, final VCellMessageEventListener listener) {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		    listener.onVCellMessageEvent(event);
	    }
	});
}

/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 5:18:16 PM)
 * @param listener ExportListener
 */
public synchronized void removeDataJobListener(DataJobListener listener) {
	listenerList.remove(DataJobListener.class, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 5:18:16 PM)
 * @param listener ExportListener
 */
public synchronized void removeExportListener(ExportListener listener) {
	listenerList.remove(ExportListener.class, listener);
}


/**
 * removeSimulationStatusEventListener method comment.
 */
public synchronized void removeSimStatusListener(SimStatusListener listener) {
	listenerList.remove(SimStatusListener.class, listener);
}


/**
 * removeSimulationStatusEventListener method comment.
 */
public synchronized void removeSimulationJobStatusListener(SimulationJobStatusListener listener) {
	listenerList.remove(SimulationJobStatusListener.class, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (6/19/2006 12:54:05 PM)
 * @param listener cbit.vcell.desktop.controls.ExportListener
 */
public void removeVCellMessageEventListener(VCellMessageEventListener listener) {
	listenerList.remove(VCellMessageEventListener.class, listener);
}

/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 2:27:28 PM)
 * @param newJobStatus cbit.vcell.messaging.db.SimulationJobStatus
 * @param progress java.lang.Double
 * @param timePoint java.lang.Double
 */
public void simStatusChanged(SimStatusEvent simStatusEvent) {
	// refire for swing
	fireSimStatusEvent(simStatusEvent);
}


public void reportPerformanceMonitorEvent(PerformanceMonitorEvent pme) throws RemoteException {
	// just pass it to the the messaging service
	clientServerManager.reportPerformanceMonitorEvent(pme);
}
}
