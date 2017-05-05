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

import javax.swing.event.EventListenerList;

import org.vcell.util.DataAccessException;

import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.server.ExportJobStatus;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.solver.VCSimulationIdentifier;
/**
 * Insert the type's description here.
 * Creation date: (6/1/2004 11:07:51 PM)
 * @author: Ion Moraru
 */
public class ClientJobManager implements JobManager {
	private ClientServerManager clientServerManager = null;
	private EventListenerList listenerList = new EventListenerList();

/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 11:12:34 PM)
 * @param csm cbit.vcell.client.server.ClientServerManager
 */
public ClientJobManager(ClientServerManager csm) {
	clientServerManager = csm;
}


/**
 * addSimulationStatusEventListener method comment.
 */
public synchronized void addSimStatusListener(SimStatusListener listener) {
	listenerList.add(SimStatusListener.class, listener);
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
private void fireSimStatusEvent(SimStatusEvent event, SimStatusListener listener) {
    listener.simStatusChanged(event);
}


/**
 * Insert the method's description here.
 * Creation date: (6/2/2004 1:58:19 AM)
 * @return cbit.vcell.client.server.ClientServerManager
 */
private ClientServerManager getClientServerManager() {
	return clientServerManager;
}


/**
 * Insert the method's description here.
 * Creation date: (6/4/2004 3:22:42 PM)
 */
public ExportJobStatus getExportJobStatus(ExportSpecs exportSpecs) throws org.vcell.util.DataAccessException {
	try {
		return getClientServerManager().getExportController().getExportJobStatus(exportSpecs);
	} catch (RemoteException rexc) {
		handleRemoteException(rexc);
		// once more before we fail
		try {
			return getClientServerManager().getExportController().getExportJobStatus(exportSpecs);
		} catch (RemoteException rexc2) {
			handleRemoteException(rexc2);
			throw new DataAccessException("ExportJobStatus inquiry for '"+exportSpecs.getVCDataIdentifier()+"' failed\n"+rexc2.getMessage());
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/4/2004 3:22:42 PM)
 */
public SimulationStatus getServerSimulationStatus(VCSimulationIdentifier vcSimulationIdentifier) throws org.vcell.util.DataAccessException {
	return getClientServerManager().getDocumentManager().getServerSimulationStatus(vcSimulationIdentifier);
}


/**
 * Insert the method's description here.
 * Creation date: (1/4/01 1:38:14 PM)
 * @param remoteException java.rmi.RemoteException
 */
private void handleRemoteException(RemoteException remoteException) {
	remoteException.printStackTrace(System.out);
}


/**
 * removeSimulationStatusEventListener method comment.
 */
public synchronized void removeSimStatusListener(SimStatusListener listener) {
	listenerList.remove(SimStatusListener.class, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/2005 4:36:57 PM)
 * @param newJobStatus cbit.vcell.messaging.db.SimulationJobStatus
 * @param progress java.lang.Double
 * @param timePoint java.lang.Double
 */
public void simulationJobStatusChanged(cbit.rmi.event.SimulationJobStatusEvent simJobStatusEvent) {
	try {
		getClientServerManager().getDocumentManager().updateServerSimulationStatusFromJobEvent(simJobStatusEvent);
		fireSimStatusEvent(new SimStatusEvent(this, simJobStatusEvent.getVCSimulationIdentifier(), simJobStatusEvent.getTimepoint() != null, simJobStatusEvent.getJobStatus().getSchedulerStatus().isFailed(), simJobStatusEvent.getJobStatus().getJobIndex()));
	} catch (Exception e) {
		e.printStackTrace();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/4/2004 3:22:42 PM)
 */
public void startExport(OutputContext outputContext,ExportSpecs exportSpecs) throws DataAccessException {
	try {
		getClientServerManager().getExportController().startExport(outputContext,exportSpecs);
	} catch (RemoteException rexc) {
		handleRemoteException(rexc);
		// once more before we fail
//		try {
//			getClientServerManager().getExportController().startExport(outputContext,exportSpecs);
//		} catch (RemoteException rexc2) {
//			handleRemoteException(rexc2);
//			throw new DataAccessException("Start export for '"+exportSpecs.getVCDataIdentifier()+"' failed\n"+rexc2.getMessage());
//		}
	}
}


/**
 * @Override
 */
public SimulationStatus startSimulation(VCSimulationIdentifier vcSimulationIdentifier, int numSimulationScanJobs) throws DataAccessException {
	try {
		SimulationStatus simulationStatus = getClientServerManager().getSimulationController().startSimulation(vcSimulationIdentifier, numSimulationScanJobs);
		return simulationStatus;
	} catch (RemoteException rexc) {
		handleRemoteException(rexc);
		// once more before we fail
		try {
			SimulationStatus simulationStatus = getClientServerManager().getSimulationController().startSimulation(vcSimulationIdentifier, numSimulationScanJobs);
			return simulationStatus;
		} catch (RemoteException rexc2) {
			handleRemoteException(rexc2);
			throw new DataAccessException("Start simulation '"+vcSimulationIdentifier+"' failed\n"+rexc2.getMessage());
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/4/2004 3:22:42 PM)
 */
public SimulationStatus stopSimulation(VCSimulationIdentifier vcSimulationIdentifier) throws DataAccessException {
	try {
		SimulationStatus simulationStatus = getClientServerManager().getSimulationController().stopSimulation(vcSimulationIdentifier);
		return simulationStatus;
	} catch (RemoteException rexc) {
		handleRemoteException(rexc);
		// once more before we fail
		try {
			SimulationStatus simulationStatus = getClientServerManager().getSimulationController().stopSimulation(vcSimulationIdentifier);
			return simulationStatus;
		} catch (RemoteException rexc2) {
			handleRemoteException(rexc2);
			throw new DataAccessException("Stop simulation '"+vcSimulationIdentifier+"' failed\n"+rexc2.getMessage());
		}
	}
}
}
