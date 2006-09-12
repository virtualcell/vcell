package cbit.rmi.event;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.EventQueue;
/**
 * Insert the type's description here.
 * Creation date: (11/12/2000 9:31:08 PM)
 * @author: IIM
 */
public class SimpleMessageDispatcher implements MessageDispatcher {
	private javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();

	//
	// thread DataJobEvents
	//
	protected class DataJobEventNotifier implements Runnable {
		private DataJobListener listener = null;
		private DataJobEvent event = null;
		public DataJobEventNotifier(DataJobListener argListener, DataJobEvent argEvent){
			listener = argListener;
			event = argEvent;
		}
		public void run() {
			try {
				listener.dataJobMessage(event);
			}catch (Throwable e){
			}
		}
	}
	//
	// thread DataJobEvents
	//
	protected class VCellMessageEventNotifier implements Runnable {
		private VCellMessageEventListener listener = null;
		private VCellMessageEvent event = null;
		public VCellMessageEventNotifier(VCellMessageEventListener argListener, VCellMessageEvent argEvent){
			listener = argListener;
			event = argEvent;
		}
		public void run() {
			try {
				listener.onVCellMessageEvent(event);
			}catch (Throwable e){
			}
		}
	}
	//
	// thread ExportEvents
	//
	protected class ExportEventNotifier implements Runnable {
		private ExportListener listener = null;
		private ExportEvent event = null;
		public ExportEventNotifier(ExportListener argListener, ExportEvent argEvent){
			listener = argListener;
			event = argEvent;
		}
		public void run() {
			try {
				listener.exportMessage(event);
			}catch (Throwable e){
			}
		}
	}
	//
	// thread PerformanceMonitorEvents
	//
	protected class PerformanceMonitorEventNotifier implements Runnable {
		private PerformanceMonitorListener listener = null;
		private PerformanceMonitorEvent event = null;
		public PerformanceMonitorEventNotifier(PerformanceMonitorListener argListener, PerformanceMonitorEvent argEvent){
			listener = argListener;
			event = argEvent;
		}
		public void run() {
			try {
				listener.performanceMonitorEvent(event);
			}catch (Throwable e){
			}
		}
	}
	//
	// thread WorkerEvents
	//
	protected class WorkerEventNotifier implements Runnable {
		private WorkerEventListener listener = null;
		private WorkerEvent event = null;
		public WorkerEventNotifier(WorkerEventListener argListener, WorkerEvent argEvent){
			listener = argListener;
			event = argEvent;
		}
		public void run() {
			try {
				listener.onWorkerEvent(event);
			}catch (Throwable e){
				e.printStackTrace(System.out);
			}
		}
	}	
	//
	// thread SimulationJobStatusEvents
	//	
	protected class SimulationJobStatusEventNotifier implements Runnable {
		private SimulationJobStatusListener listener = null;
		private SimulationJobStatusEvent event = null;
		public SimulationJobStatusEventNotifier(SimulationJobStatusListener argListener, SimulationJobStatusEvent argEvent){
			listener = argListener;
			event = argEvent;
		}
		public void run() {
			try {
				listener.simulationJobStatusChanged(event);
			}catch (Throwable e){
			}
		}
	}
	//
	// thread MessageEvents
	//
	protected class MessageEventNotifier implements Runnable {
		private MessageListener listener = null;
		private MessageEvent event = null;
		public MessageEventNotifier(MessageListener argListener, MessageEvent argEvent){
			listener = argListener;
			event = argEvent;
		}
		public void run() {
			try {
				listener.messageEvent(event);
			}catch (Throwable e){
			}
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
 * Insert the method's description here.
 * Creation date: (11/12/2000 9:48:07 PM)
 * @param listener cbit.rmi.event.MessageListener
 */
public synchronized void addMessageListener(MessageListener listener) {
	listenerList.add(MessageListener.class, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (9/17/2004 2:15:58 PM)
 * @param pml cbit.rmi.event.PerformanceMonitorListener
 */
public synchronized void addPerformanceMonitorListener(PerformanceMonitorListener pml) {
	listenerList.add(PerformanceMonitorListener.class, pml);
}


/**
 * addSimulationStatusEventListener method comment.
 */
public synchronized void addSimulationJobStatusListener(SimulationJobStatusListener listener) {
	listenerList.add(SimulationJobStatusListener.class, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (6/19/2006 11:13:41 AM)
 * @param listener cbit.vcell.desktop.controls.ExportListener
 */
public void addVCellMessageEventListener(VCellMessageEventListener listener) {
	listenerList.add(VCellMessageEventListener.class, listener);
}


/**
 * addWorkerEventListener method comment.
 */
public synchronized void addWorkerEventListener(WorkerEventListener listener) {
	listenerList.add(WorkerEventListener.class, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (11/17/2000 11:43:22 AM)
 * @param event cbit.rmi.event.JobCompletedEvent
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
		    EventQueue.invokeLater(new SimpleMessageDispatcher.DataJobEventNotifier((DataJobListener)listeners[i+1],event));
	    }	       
	}
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
		    EventQueue.invokeLater(new SimpleMessageDispatcher.ExportEventNotifier((ExportListener)listeners[i+1],event));
	    }	       
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
		    EventQueue.invokeLater(new SimpleMessageDispatcher.MessageEventNotifier((MessageListener)listeners[i+1],event));
	    }	       
	}
}	


/**
 * Insert the method's description here.
 * Creation date: (11/17/2000 11:43:22 AM)
 * @param event cbit.rmi.event.ExportEvent
 */
protected void firePerformanceMonitorEvent(PerformanceMonitorEvent event) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Reset the source to allow proper wiring
	event.setSource(this);
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==PerformanceMonitorListener.class) {
		    EventQueue.invokeLater(new SimpleMessageDispatcher.PerformanceMonitorEventNotifier((PerformanceMonitorListener)listeners[i+1],event));
	    }	       
	}
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
		    EventQueue.invokeLater(new SimpleMessageDispatcher.SimulationJobStatusEventNotifier((SimulationJobStatusListener)listeners[i+1],event));
	    }	       
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/17/2000 11:43:22 AM)
 * @param event cbit.rmi.event.JobCompletedEvent
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
		    EventQueue.invokeLater(new SimpleMessageDispatcher.VCellMessageEventNotifier((VCellMessageEventListener)listeners[i+1],event));
	    }	       
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/17/2000 11:43:22 AM)
 * @param event cbit.rmi.event.JobCompletedEvent
 */
protected void fireWorkerEvent(WorkerEvent event) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Reset the source to allow proper wiring
	event.setSource(this);
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==WorkerEventListener.class) {
		    EventQueue.invokeLater(new SimpleMessageDispatcher.WorkerEventNotifier((WorkerEventListener)listeners[i+1],event));
	    }	       
	}
}


/**
 * 
 * @param event cbit.rmi.event.MessageEvent
 */
public void messageEvent(MessageEvent event) {
	fireMessageEvent(event);
	if (event instanceof SimulationJobStatusEvent) {
		fireSimulationJobStatusEvent((SimulationJobStatusEvent)event);
	} else if (event instanceof ExportEvent) {
		fireExportEvent((ExportEvent)event);
	} else if (event instanceof WorkerEvent) {
		fireWorkerEvent((WorkerEvent)event);
	} else if (event instanceof PerformanceMonitorEvent) {
		firePerformanceMonitorEvent((PerformanceMonitorEvent)event);
	} else if (event instanceof DataJobEvent) {
		fireDataJobEvent((DataJobEvent)event);
	} else if (event instanceof VCellMessageEvent) {
		fireVCellMessageEvent((VCellMessageEvent)event);
	} 
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
 * Insert the method's description here.
 * Creation date: (11/12/2000 9:48:07 PM)
 * @param listener cbit.rmi.event.MessageListener
 */
public synchronized void removeMessageListener(MessageListener listener) {
	listenerList.remove(MessageListener.class, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (9/17/2004 2:15:58 PM)
 * @param pml cbit.rmi.event.PerformanceMonitorListener
 */
public synchronized void removePerformanceMonitorListener(PerformanceMonitorListener pml) {
	listenerList.remove(PerformanceMonitorListener.class, pml);
}


/**
 * removeSimulationStatusEventListener method comment.
 */
public synchronized void removeSimulationJobStatusListener(SimulationJobStatusListener listener) {
	listenerList.remove(SimulationJobStatusListener.class, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (6/19/2006 11:13:41 AM)
 * @param listener cbit.vcell.desktop.controls.ExportListener
 */
public void removeVCellMessageEventListener(VCellMessageEventListener listener) {
	listenerList.remove(VCellMessageEventListener.class, listener);
}


/**
 * removeWorkerEventListener method comment.
 */
public synchronized void removeWorkerEventListener(WorkerEventListener listener) {
	listenerList.remove(WorkerEventListener.class, listener);
}
}