package cbit.vcell.client.server;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import org.vcell.util.DataAccessException;

import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.DataJobListener;
import cbit.rmi.event.DataJobSender;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.ExportListener;
import cbit.rmi.event.ExportSender;
import cbit.rmi.event.PerformanceMonitorListener;
import cbit.rmi.event.RemoteMessageHandler;
import cbit.rmi.event.SimpleMessageService;
import cbit.rmi.event.SimulationJobStatusEvent;
import cbit.rmi.event.SimulationJobStatusListener;
import cbit.rmi.event.SimulationJobStatusSender;
import cbit.rmi.event.VCellMessageEvent;
import cbit.rmi.event.VCellMessageEventListener;
import cbit.rmi.event.VCellMessageEventSender;
/**
 * Insert the type's description here.
 * Creation date: (6/9/2004 2:17:35 PM)
 * @author: Ion Moraru
 */
public class AsynchMessageManager
    implements
        SimStatusListener,
        SimStatusSender,
        PerformanceMonitorListener,
        SimulationJobStatusListener,
        ExportListener,
        SimulationJobStatusSender,
        ExportSender,
        DataJobListener,
        DataJobSender, 
        VCellMessageEventListener,
        VCellMessageEventSender {
    private EventListenerList listenerList = new EventListenerList();
    private SimpleMessageService simpleMessageService = new SimpleMessageService();
    private RemoteMessageHandler connectedRemoteMessageHandler = null;

/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 4:55:22 PM)
 */
public AsynchMessageManager() {
	getSimpleMessageService().getMessageDispatcher().addSimulationJobStatusListener(this);
	getSimpleMessageService().getMessageDispatcher().addExportListener(this);
	getSimpleMessageService().getMessageDispatcher().addDataJobListener(this);
	getSimpleMessageService().getMessageDispatcher().addVCellMessageEventListener(this);
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
public void addVCellMessageEventListener(cbit.rmi.event.VCellMessageEventListener listener) {
	listenerList.add(VCellMessageEventListener.class, listener);
}


/**
 */
void close() {
	disconnect();
	getSimpleMessageService().close();
}


/**
 * Insert the method's description here.
 * Creation date: (1/5/01 3:00:52 PM)
 * @return cbit.rmi.event.RemoteMessageHandler
 */
void connect(RemoteMessageHandler remoteMessageHandler) throws DataAccessException {
	disconnect();
	try {
		// record connection
		setConnectedRemoteMessageHandler(remoteMessageHandler);
		// add me to remote message server
		getSimpleMessageService().getMessageHandler().addRemoteMessageListener(remoteMessageHandler, remoteMessageHandler.getRemoteMesssageListenerID());
		// add remote message server to me.
		remoteMessageHandler.addRemoteMessageListener(getSimpleMessageService().getMessageHandler(), getSimpleMessageService().getMessageHandler().getRemoteMesssageListenerID());
		// in case of firewalls, etc
		getSimpleMessageService().getMessageHandler().enablePolling(10); // seconds
	} catch (Throwable exc) {
		// no go, dump
		disconnect();
		// let caller know...
		throw new DataAccessException(exc.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2006 3:05:48 PM)
 * @param event cbit.rmi.event.ExportEvent
 */
public void dataJobMessage(cbit.rmi.event.DataJobEvent event) {
	// refire for swing
	fireDataJobEvent(event);

	}


/**
 * Insert the method's description here.
 * Creation date: (1/5/01 3:00:52 PM)
 * @return cbit.rmi.event.RemoteMessageHandler
 */
private void disconnect() {
	if (getConnectedRemoteMessageHandler() == null) {
		// nobody there
		return;
	}
	/* trying gracefully */
	// remove me from remote message server.
	try {
		getSimpleMessageService().getMessageHandler().removeRemoteMessageListener(getConnectedRemoteMessageHandler());
	} catch (java.rmi.RemoteException exc) {
		exc.printStackTrace(System.out);
	}
	// remove remote message server from me.
	try {
		getConnectedRemoteMessageHandler().removeRemoteMessageListener(getSimpleMessageService().getMessageHandler());
	} catch (java.rmi.RemoteException exc) {
		exc.printStackTrace(System.out);
	}
	/* no matter what, now dump the current handler */
	getSimpleMessageService().resetHandler();
	setConnectedRemoteMessageHandler(null);
		
}


/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 2:27:28 PM)
 * @param event cbit.rmi.event.ExportEvent
 */
public void exportMessage(cbit.rmi.event.ExportEvent event) {
	// refire for swing
	fireExportEvent(event);
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
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		    listener.simStatusChanged(event);
	    }
	});
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
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		    listener.simulationJobStatusChanged(event);
	    }
	});
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
 * Creation date: (6/9/2004 11:33:31 PM)
 * @return cbit.rmi.event.RemoteMessageHandler
 */
private cbit.rmi.event.RemoteMessageHandler getConnectedRemoteMessageHandler() {
	return connectedRemoteMessageHandler;
}


/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 4:55:54 PM)
 * @return cbit.rmi.event.SimpleMessageService
 */
private cbit.rmi.event.SimpleMessageService getSimpleMessageService() {
	return simpleMessageService;
}


/**
 * onVCellMessageEvent method comment.
 */
public void onVCellMessageEvent(cbit.rmi.event.VCellMessageEvent event) {
	fireVCellMessageEvent(event);
}


/**
 * Insert the method's description here.
 * Creation date: (9/17/2004 3:13:55 PM)
 * @param pme cbit.rmi.event.PerformanceMonitorEvent
 */
public void performanceMonitorEvent(cbit.rmi.event.PerformanceMonitorEvent pme) {
	// just pass it to the the messaging service
	getSimpleMessageService().getMessageCollector().performanceMonitorEvent(pme);
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
public void removeVCellMessageEventListener(cbit.rmi.event.VCellMessageEventListener listener) {
	listenerList.remove(VCellMessageEventListener.class, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 11:33:31 PM)
 * @param newConnectedRemoteMessageHandler cbit.rmi.event.RemoteMessageHandler
 */
private void setConnectedRemoteMessageHandler(cbit.rmi.event.RemoteMessageHandler newConnectedRemoteMessageHandler) {
	connectedRemoteMessageHandler = newConnectedRemoteMessageHandler;
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


/**
 * Insert the method's description here.
 * Creation date: (6/9/2004 2:27:28 PM)
 * @param newJobStatus cbit.vcell.messaging.db.SimulationJobStatus
 * @param progress java.lang.Double
 * @param timePoint java.lang.Double
 */
public void simulationJobStatusChanged(SimulationJobStatusEvent simJobStatusEvent) {
	// refire for swing
	fireSimulationJobStatusEvent(simJobStatusEvent);
}
}