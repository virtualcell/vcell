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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.DataAccessException;

import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.DataJobListener;
import cbit.rmi.event.DataJobListenerHolder;
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
import cbit.vcell.resource.VCellExecutorService;
import cbit.vcell.server.VCellConnection;
import edu.uchc.connjur.wb.ExecutionTrace;

/**
 * {@link AsynchMessageManager} polls from {@link VCellConnection} to get remote messages. Remote Messages include the following:
 * {@link SimulationJobStatusEvent} : simulation running event.
 * {@link ExportEvent} : export data event.
 * {@link DataJobEvent} : spatial plot, kymograph, etc
 * {@link VCellMessageEvent} : broadcase messages.
 * {@link AsynchMessageManager} also listens to {@link ClientJobManager} if user stops the simulation, then it will notify TopLevelWindowManager
 * to update the status.
 */
public class AsynchMessageManager implements SimStatusListener, DataAccessException.Listener,DataJobListenerHolder {
    private static final long BASE_POLL_SECONDS = 3;
    private static final long ATTEMPT_POLL_SECONDS = 3;
	private static Logger lg = LogManager.getLogger(AsynchMessageManager.class);

	private EventListenerList listenerList = new EventListenerList();
    private ClientServerManager clientServerManager;
    private int failureCount = 0;
    private ScheduledExecutorService executorService = null;
    private long counter = 0;
    private long pollTime = BASE_POLL_SECONDS;
	private AtomicBoolean isPollingEnabled = new AtomicBoolean(false);
	private ScheduledFuture<?> pollingHandle = null;
	/**
	 * for {@link #schedule(long)} method
	 */
	private final ReentrantLock scheduleLock = new ReentrantLock();
	private final ReentrantLock connectionLock = new ReentrantLock();

public AsynchMessageManager(ClientServerManager csm) {
	this.clientServerManager = csm;
}
/**
 * start polling for connection. Should be called after connect
 * no-op if already called
 */
public synchronized void startPolling() {
	if (isPollingEnabled.get()) return;
	isPollingEnabled.set(true);
	if (executorService == null) {
		executorService = VCellExecutorService.get();
		DataAccessException.addListener(this);
	}
	schedule(pollTime);

}

public void stopPolling() {
//	lg.trace("stopping polling");
	isPollingEnabled.set(false);
}

@Override
public void created(DataAccessException dae) {
//	if (lg.isTraceEnabled()) {
//		lg.trace("scheduling now due to " + dae.getMessage());
//	}
	schedule(0);
}
private void poll( )  {
	if (!isPollingEnabled.get()) {
		lg.debug("polling stopped");
		return;
	}

	lg.debug("polling");
	boolean shouldReport = counter % 50 == 0;
	long begin = 0;
	long end = 0;

    // ask remote message listener (really should be "message producer") for any queued events.
    try {
    	MessageEvent[] queuedEvents;
    	if (shouldReport) begin = System.currentTimeMillis(); // time the call

		if (!clientServerManager.isStatusConnected()){
			if (!connectionLock.tryLock()) return;
			clientServerManager.attemptReconnect();
			return;
		}
		pollTime = BASE_POLL_SECONDS;
		queuedEvents = clientServerManager.getMessageEvents();

    	if (shouldReport) end = System.currentTimeMillis();

	    failureCount = 0; //this is skipped if the connection has failed:w
	    // deal with events, if any
	    if (queuedEvents != null) for (MessageEvent messageEvent : queuedEvents) onMessageEvent(messageEvent);
		if (!shouldReport) return;

		// report polling call performance
        double duration = ((double) (end - begin)) / 1000;
        PerformanceMonitorEvent performanceMonitorEvent = new PerformanceMonitorEvent(
            this, null, new PerformanceData(
                "AsynchMessageManager.poll()",
                MessageEvent.POLLING_STAT,
                new PerformanceDataEntry[] {new PerformanceDataEntry("remote call duration", Double.toString(duration))}
            )
        );
    } catch (Exception exc) {
        lg.error(">> POLLING FAILURE <<", exc);
	    pollTime = ATTEMPT_POLL_SECONDS;
	    failureCount ++;
	    if (failureCount % 3 == 0) {
	    	isPollingEnabled.set(false);
	    	clientServerManager.setDisconnected();
	    }
    } finally {
		connectionLock.unlock();
		lg.debug(ExecutionTrace.justClassName(this) + " poll time " + pollTime + " seconds");
    	if (isPollingEnabled.get()) schedule(pollTime);
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

public void addDataJobListener(DataJobListener listener) {
	synchronized (listenerList){
		listenerList.add(DataJobListener.class, listener);
	}
}


public void addExportListener(ExportListener listener) {
	synchronized (listenerList){
		listenerList.add(ExportListener.class, listener);
	}
}

public void addSimStatusListener(SimStatusListener listener) {
	synchronized (listenerList){
		listenerList.add(SimStatusListener.class, listener);
	}
}


public void addSimulationJobStatusListener(SimulationJobStatusListener listener) {
	synchronized (listenerList){
		listenerList.add(SimulationJobStatusListener.class, listener);
	}
}


public void addVCellMessageEventListener(VCellMessageEventListener listener) {
	synchronized (listenerList){
		listenerList.add(VCellMessageEventListener.class, listener);
	}
}

protected void fireDataJobEvent(DataJobEvent event) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Reset the source to allow proper wiring
	event.setSource(this);
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
		if (listeners[i] != DataJobListener.class) continue;
        fireDataJobEvent(event, (DataJobListener) listeners[i + 1]);
    }
}

private void fireDataJobEvent(final DataJobEvent event, final DataJobListener listener) {
	SwingUtilities.invokeLater(() -> listener.dataJobMessage(event));
}

protected void fireExportEvent(ExportEvent event) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Reset the source to allow proper wiring
	event.setSource(this);
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
		if (listeners[i] != ExportListener.class) continue;
        fireExportEvent(event, (ExportListener) listeners[i + 1]);
    }
}

private void fireExportEvent(final ExportEvent event, final ExportListener listener) {
	SwingUtilities.invokeLater(() -> listener.exportMessage(event));
}


protected void fireSimStatusEvent(SimStatusEvent event) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
		if (listeners[i] != SimStatusListener.class) continue;
		fireSimStatusEvent(event, (SimStatusListener)listeners[i+1]);
	}
}

private void fireSimStatusEvent(final SimStatusEvent event, final SimStatusListener listener) {
	listener.simStatusChanged(event);
}

protected void fireSimulationJobStatusEvent(SimulationJobStatusEvent event) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Reset the source to allow proper wiring
	event.setSource(this);
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
		if (listeners[i] != SimulationJobStatusListener.class) continue;
		fireSimulationJobStatusEvent(event, (SimulationJobStatusListener)listeners[i+1]);
	}
}

private void fireSimulationJobStatusEvent(final SimulationJobStatusEvent event, final SimulationJobStatusListener listener) {
    listener.simulationJobStatusChanged(event);
}

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

private void fireVCellMessageEvent(final VCellMessageEvent event, final VCellMessageEventListener listener) {
	SwingUtilities.invokeLater(() -> listener.onVCellMessageEvent(event));
}

public void removeDataJobListener(DataJobListener listener) {
	synchronized (listenerList){
		listenerList.remove(DataJobListener.class, listener);
	}
}

public void removeExportListener(ExportListener listener) {
	synchronized (listenerList){
		listenerList.remove(ExportListener.class, listener);
	}
}


/**
 * removeSimulationStatusEventListener method comment.
 */
public void removeSimStatusListener(SimStatusListener listener) {
	synchronized (listenerList){
		listenerList.remove(SimStatusListener.class, listener);
	}
}


/**
 * removeSimulationStatusEventListener method comment.
 */
public void removeSimulationJobStatusListener(SimulationJobStatusListener listener) {
	synchronized (listenerList){
		listenerList.remove(SimulationJobStatusListener.class, listener);
	}
}


public void removeVCellMessageEventListener(VCellMessageEventListener listener) {
	synchronized (listenerList){
		listenerList.remove(VCellMessageEventListener.class, listener);
	}
}

public void simStatusChanged(SimStatusEvent simStatusEvent) {
	// refire for swing
	fireSimStatusEvent(simStatusEvent);
}

}
