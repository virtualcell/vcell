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
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.User;

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
public void startSimulation(VCSimulationIdentifier vcSimulationIdentifier) {
	sessionLog.print("LocalSimulationController.startSimulation(simInfo="+vcSimulationIdentifier+")");
	try {
		Simulation simulation = simulationControllerImpl.getSimulationDatabase().getSimulation(user,vcSimulationIdentifier.getSimulationKey());
		simulationControllerImpl.startSimulation(simulation,sessionLog);
	}catch (DataAccessException e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public void stopSimulation(VCSimulationIdentifier vcSimulationIdentifier) {
	sessionLog.print("LocalSimulationController.getSolverStatus(simInfo="+vcSimulationIdentifier+")");
	try {
		Simulation simulation = simulationControllerImpl.getSimulationDatabase().getSimulation(user,vcSimulationIdentifier.getSimulationKey());
		simulationControllerImpl.stopSimulation(simulation);
	}catch (DataAccessException e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}
}
