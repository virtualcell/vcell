/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.bootstrap;
import java.rmi.RemoteException;

import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.message.VCMessageSession;
import cbit.vcell.messaging.db.SimpleJobStatus;
import cbit.vcell.modeldb.SimpleJobStatusQuerySpec;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.gui.SimulationStatus;

/**
 * Insert the type's description here.
 * Creation date: (2/4/2003 11:08:14 PM)
 * @author: Jim Schaff
 */
public class LocalSimulationControllerMessaging extends java.rmi.server.UnicastRemoteObject implements cbit.vcell.server.SimulationController {
	private org.vcell.util.SessionLog fieldSessionLog = null;
	private RpcSimServerProxy simServerProxy = null;

/**
 * MessagingSimulationController constructor comment.
 * @exception java.rmi.RemoteException The exception description.
 */
public LocalSimulationControllerMessaging(UserLoginInfo userLoginInfo, VCMessageSession vcMessageSession, SessionLog log, int rmiPort) throws java.rmi.RemoteException {
	super(rmiPort);
	this.fieldSessionLog = log;
	simServerProxy = new RpcSimServerProxy(userLoginInfo, vcMessageSession, fieldSessionLog);
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public SimulationStatus startSimulation(VCSimulationIdentifier vcSimID, int numSimulationScanJobs) {
	fieldSessionLog.print("LocalSimulationControllerMessaging.startSimulation(" + vcSimID + ")");
	return simServerProxy.startSimulation(simServerProxy.userLoginInfo.getUser(),vcSimID,numSimulationScanJobs);
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public SimulationStatus stopSimulation(VCSimulationIdentifier vcSimID) {
	fieldSessionLog.print("LocalSimulationControllerMessaging.stopSimulation(" + vcSimID + ")");
	return simServerProxy.stopSimulation(simServerProxy.userLoginInfo.getUser(),vcSimID);
}


@Override
public SimulationStatus[] getSimulationStatus(KeyValue[] simKeys) throws DataAccessException, RemoteException {
	fieldSessionLog.print("LocalSimulationControllerMessaging.getSimulationStatus(" + simKeys + ")");
	return simServerProxy.getSimulationStatus(simServerProxy.userLoginInfo.getUser(),simKeys);
}


@Override
public SimulationStatus getSimulationStatus(KeyValue simulationKey) throws DataAccessException, RemoteException {
	fieldSessionLog.print("LocalSimulationControllerMessaging.getSimulationStatus(" + simulationKey + ")");
	return simServerProxy.getSimulationStatus(simServerProxy.userLoginInfo.getUser(),simulationKey);
}

@Override
public SimpleJobStatus[] getSimpleJobStatus(SimpleJobStatusQuerySpec simJobStatusQuerySpec) throws DataAccessException, RemoteException {
	fieldSessionLog.print("LocalSimulationControllerMessaging.getSimulationJobStatus(" + simJobStatusQuerySpec + ")");
	return simServerProxy.getSimpleJobStatus(simServerProxy.userLoginInfo.getUser(),simJobStatusQuerySpec);
}
}
