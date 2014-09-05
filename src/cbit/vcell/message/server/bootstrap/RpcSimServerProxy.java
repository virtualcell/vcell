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
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.server.ServiceSpec.ServiceType;
import cbit.vcell.messaging.db.SimpleJobStatus;
import cbit.vcell.modeldb.SimpleJobStatusQuerySpec;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.solver.VCSimulationIdentifier;

/**
 * Insert the type's description here.
 * Creation date: (12/5/2001 12:00:10 PM)
 * @author: Jim Schaff
 *
 * stateless database service for any user (should be thread safe ... reentrant)
 *
 */
public class RpcSimServerProxy extends AbstractRpcServerProxy implements SimulationService {
/**
 * DataServerProxy constructor comment.
 */
public RpcSimServerProxy(UserLoginInfo userLoginInfo, VCMessageSession vcMessageSession, SessionLog log) {
	super(userLoginInfo, vcMessageSession, VCellQueue.SimReqQueue, log);
}


/**
 * Insert the method's description here.
 * Creation date: (12/5/2001 9:39:03 PM)
 * @return java.lang.Object
 * @param methodName java.lang.String
 * @param args java.lang.Object[]
 * @exception java.lang.Exception The exception description.
 */
private Object rpc(String methodName, Object[] args) throws DataAccessException {
	try {
		return rpc(ServiceType.DISPATCH, methodName, args, true);
	} catch (DataAccessException ex) {
		log.exception(ex);
		throw ex;
	} catch (Exception e){
		log.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public SimulationStatus startSimulation(User user, VCSimulationIdentifier vcSimID, int numSimulationScanJobs) {
	try {
		return (SimulationStatus)rpc("startSimulation",new Object[]{user, vcSimID, new Integer(numSimulationScanJobs)});
	}catch (DataAccessException e){
		log.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public SimulationStatus stopSimulation(User user, VCSimulationIdentifier vcSimID) {
	try {
		return (SimulationStatus)rpc("stopSimulation",new Object[]{user, vcSimID});
	} catch (DataAccessException e) {
		log.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


@Override
public SimulationStatus[] getSimulationStatus(User user, KeyValue[] simKeys) throws DataAccessException {
	try {
		return (SimulationStatus[])rpc("getSimulationStatus",new Object[]{user, simKeys});
	} catch (DataAccessException e) {
		log.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


@Override
public SimulationStatus getSimulationStatus(User user, KeyValue simulationKey) throws DataAccessException {
	try {
		return (SimulationStatus)rpc("getSimulationStatus",new Object[]{user, simulationKey});
	} catch (DataAccessException e) {
		log.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}

@Override
public SimpleJobStatus[] getSimpleJobStatus(User user, SimpleJobStatusQuerySpec simJobStatusQuerySpec) throws DataAccessException {
	try {
		return (SimpleJobStatus[])rpc("getSimpleJobStatus",new Object[]{user, simJobStatusQuerySpec});
	} catch (DataAccessException e) {
		log.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}
}
