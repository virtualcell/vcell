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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.server.SimpleJobStatus;
import cbit.vcell.server.SimpleJobStatusQuerySpec;
import cbit.vcell.server.SimulationController;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationIdentifier;
/**
 * Insert the type's description here.
 * Creation date: (6/28/01 12:55:29 PM)
 * @author: Jim Schaff
 */
public class LocalSimulationController implements SimulationController {
	public static final Logger lg = LogManager.getLogger(LocalSimulationController.class);

	private SimulationControllerImpl simulationControllerImpl = null;
	private User user = null;

/**
 * LocalSimulationController constructor comment.
 */
protected LocalSimulationController(User user, SimulationControllerImpl simulationControllerImpl) {
	this.simulationControllerImpl = simulationControllerImpl;
	this.user = user;
}


/**
 * This method was created by a SmartGuide.
 */
public SimulationStatus startSimulation(VCSimulationIdentifier vcSimulationIdentifier, int numSimulationScanJobs) {
	if (lg.isTraceEnabled()) lg.trace("LocalSimulationController.startSimulation(simInfo="+vcSimulationIdentifier+")");
	try {
		Simulation simulation = simulationControllerImpl.getSimulationDatabase().getSimulation(user,vcSimulationIdentifier.getSimulationKey());
		simulationControllerImpl.startSimulation(simulation);
		return simulationControllerImpl.getSimulationDatabase().getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
	}catch (Exception e){
		lg.error(e.getMessage(),e);
		throw new RuntimeException("startSimluation" + vcSimulationIdentifier.getID() + " " + numSimulationScanJobs + " scan jobs",e);
	}
}


/**
 * This method was created by a SmartGuide.
 */
public SimulationStatus stopSimulation(VCSimulationIdentifier vcSimulationIdentifier) {
	if (lg.isTraceEnabled()) lg.trace("LocalSimulationController.getSolverStatus(simInfo="+vcSimulationIdentifier+")");
	try {
		Simulation simulation = simulationControllerImpl.getSimulationDatabase().getSimulation(user,vcSimulationIdentifier.getSimulationKey());
		simulationControllerImpl.stopSimulation(simulation);
		return simulationControllerImpl.getSimulationDatabase().getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
	}catch (DataAccessException e){
		lg.error(e.getMessage(),e);
		throw new RuntimeException(e.getMessage());
	}catch (Throwable e){
		lg.error(e.getMessage(),e);
		throw new RuntimeException(e.getMessage());
	}
}


@Override
public SimulationStatus[] getSimulationStatus(KeyValue[] simKeys) throws DataAccessException {
	if (lg.isTraceEnabled()) lg.trace("LocalSimulationController.getSimulationStatus(simKeys="+simKeys+")");
	try {
		return simulationControllerImpl.getSimulationStatus(simKeys);
	}catch (Exception e){
		lg.error(e.getMessage(),e);
		throw new RuntimeException(e.getMessage());
	}
}


@Override
public SimulationStatus getSimulationStatus(KeyValue simulationKey) throws DataAccessException {
	if (lg.isTraceEnabled()) lg.trace("LocalSimulationController.getSimulationStatus(simKey="+simulationKey+")");
	try {
		return simulationControllerImpl.getSimulationStatus(simulationKey);
	}catch (Exception e){
		lg.error(e.getMessage(),e);
		throw new RuntimeException(e.getMessage());
	}
}


@Override
public SimpleJobStatus[] getSimpleJobStatus(SimpleJobStatusQuerySpec simStatusQuerySpec) throws DataAccessException {
	if (lg.isTraceEnabled()) lg.trace("LocalSimulationController.getSimulationStatus(simStatusQuerySpec="+simStatusQuerySpec+")");
	try {
		return simulationControllerImpl.getSimpleJobStatus(simStatusQuerySpec);
	}catch (Exception e){
		lg.error(e.getMessage(),e);
		throw new RuntimeException(e.getMessage());
	}
}

}
