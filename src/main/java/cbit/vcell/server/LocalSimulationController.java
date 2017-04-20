/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server;
import java.rmi.RemoteException;

import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.messaging.db.SimpleJobStatus;
import cbit.vcell.modeldb.SimpleJobStatusQuerySpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationIdentifier;
/**
 * Insert the type's description here.
 * Creation date: (6/28/01 12:55:29 PM)
 * @author: Jim Schaff
 */
public class LocalSimulationController implements SimulationController {
	private SessionLog sessionLog = null;
	private SimulationControllerImpl simulationControllerImpl = null;
	private User user = null;

/**
 * LocalSimulationController constructor comment.
 * @exception java.rmi.RemoteException The exception description.
 */
protected LocalSimulationController(User user, SimulationControllerImpl simulationControllerImpl, SessionLog argSessionLog) {
	this.sessionLog = argSessionLog;
	this.simulationControllerImpl = simulationControllerImpl;
	this.user = user;
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public SimulationStatus startSimulation(VCSimulationIdentifier vcSimulationIdentifier, int numSimulationScanJobs) {
	sessionLog.print("LocalSimulationController.startSimulation(simInfo="+vcSimulationIdentifier+")");
	try {
		Simulation simulation = simulationControllerImpl.getSimulationDatabase().getSimulation(user,vcSimulationIdentifier.getSimulationKey());
		simulationControllerImpl.startSimulation(simulation,sessionLog);
		return simulationControllerImpl.getSimulationDatabase().getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
	}catch (Exception e){
		sessionLog.exception(e);
		throw new RuntimeException("startSimluation" + vcSimulationIdentifier.getID() + " " + numSimulationScanJobs + " scan jobs",e);
	}
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public SimulationStatus stopSimulation(VCSimulationIdentifier vcSimulationIdentifier) {
	sessionLog.print("LocalSimulationController.getSolverStatus(simInfo="+vcSimulationIdentifier+")");
	try {
		Simulation simulation = simulationControllerImpl.getSimulationDatabase().getSimulation(user,vcSimulationIdentifier.getSimulationKey());
		simulationControllerImpl.stopSimulation(simulation);
		return simulationControllerImpl.getSimulationDatabase().getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
	}catch (DataAccessException e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


@Override
public SimulationStatus[] getSimulationStatus(KeyValue[] simKeys) throws DataAccessException {
	sessionLog.print("LocalSimulationController.getSimulationStatus(simKeys="+simKeys+")");
	try {
		return simulationControllerImpl.getSimulationStatus(simKeys);
	}catch (Exception e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


@Override
public SimulationStatus getSimulationStatus(KeyValue simulationKey) throws DataAccessException {
	sessionLog.print("LocalSimulationController.getSimulationStatus(simKey="+simulationKey+")");
	try {
		return simulationControllerImpl.getSimulationStatus(simulationKey);
	}catch (Exception e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


@Override
public SimpleJobStatus[] getSimpleJobStatus(SimpleJobStatusQuerySpec simStatusQuerySpec) throws DataAccessException, RemoteException {
	sessionLog.print("LocalSimulationController.getSimulationStatus(simStatusQuerySpec="+simStatusQuerySpec+")");
	try {
		return simulationControllerImpl.getSimpleJobStatus(simStatusQuerySpec);
	}catch (Exception e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}

}
