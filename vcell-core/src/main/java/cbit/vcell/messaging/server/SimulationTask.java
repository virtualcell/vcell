/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.messaging.server;
import org.vcell.util.Compare;
import org.vcell.util.ISize;
import org.vcell.util.Matchable;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MemVariable;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolVariable;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverDescription;

/**
 * Insert the type's description here.
 * Creation date: (10/24/2001 10:44:09 PM)
 * @author: Jim Schaff
 */
public class SimulationTask implements java.io.Serializable, Matchable {
	private SimulationJob simulationJob = null;
	private int taskID = 0;
	private String computeResource = null;
	private boolean isPowerUser = false;
/**
 * SimulationTask constructor comment.
 * @param argName java.lang.String
 * @param argEstimatedSizeMB double
 * @param argEstimatedTimeSec double
 * @param argUserid java.lang.String
 */
public SimulationTask(SimulationJob argSimulationJob, int tid) {
	this(argSimulationJob, tid, null,false);
}

public SimulationTask(SimulationJob argSimulationJob, int tid, String comres, boolean isPowerUser) {
	if (argSimulationJob == null || argSimulationJob.getSimulation() == null){
		throw new RuntimeException("simulation cannot be null");
	}
	simulationJob = argSimulationJob;
	taskID = tid;
	computeResource = comres;
	this.isPowerUser = isPowerUser;
}

public boolean isPowerUser() {
	return isPowerUser;
}
public double getEstimatedMemorySizeMB() {
	//
	// calculate number of PDE variables and total number of spatial variables
	//
	SimulationSymbolTable simSymbolTable = getSimulationJob().getSimulationSymbolTable();
	Simulation simulation = simSymbolTable.getSimulation();
	MathDescription mathDescription = simulation.getMathDescription();
	
	int pdeVarCount=0;
	int odeVarCount=0;
	Variable variables[] = simSymbolTable.getVariables();
	for (int i=0;i<variables.length;i++){
	  	if (variables[i] instanceof VolVariable) {
	  		if (mathDescription.isPDE((VolVariable)variables[i])) {
	  			pdeVarCount ++;
	  		} else {
	  			odeVarCount ++;
	  		}
	  	} else if (variables[i] instanceof MemVariable) {
	  		if (mathDescription.isPDE((MemVariable)variables[i])) {	  	
	  			pdeVarCount ++;
	  		} else {
	  			odeVarCount ++;
	  		}
	  	}
	}	
	long numMeshPoints = 1;
	if (simulation.isSpatial()) {
		ISize samplingSize = simulation.getMeshSpecification().getSamplingSize();
		numMeshPoints = samplingSize.getX()*samplingSize.getY()*samplingSize.getZ();
	}
	
	// 180 bytes per pde variable plus ode per mesh point + 15M overhead 
	// there is 70M PBS overhead which will be added when submitted to pbs
	double est = ((180 * pdeVarCount + 16 * odeVarCount) * numMeshPoints / 1e6 + 15);
	if (simulation.getSolverTaskDescription().getSolverDescription().equals(SolverDescription.SundialsPDE)) {
		est *= 2;
	}
	return est;	
}

/**
 * Insert the method's description here.
 * Creation date: (2/4/2004 1:32:27 PM)
 * @return cbit.sql.KeyValue
 */
public KeyValue getSimKey() {
	return getSimulationJob().getSimulation().getKey();
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:45:58 PM)
 * @return cbit.vcell.solver.Simulation
 */
public SimulationInfo getSimulationInfo() {
	return getSimulationJob().getSimulation().getSimulationInfo();
}


/**
 * Insert the method's description here.
 * Creation date: (10/10/2005 3:02:30 PM)
 * @return cbit.vcell.solver.SimulationJob
 */
public SimulationJob getSimulationJob() {
	return simulationJob;
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:45:58 PM)
 * @return cbit.vcell.solver.Simulation
 */
public String getSimulationJobID() {
	return simulationJob.getSimulationJobID();
}


/**
 * Insert the method's description here.
 * Creation date: (6/19/2003 2:07:37 PM)
 * @return int
 */
public int getTaskID() {
	return taskID;
}


/**
 * Insert the method's description here.
 * Creation date: (3/11/2004 8:51:11 AM)
 * @return java.lang.String
 */
public User getUser() {	
	return getSimulationJob().getSimulation().getVersion().getOwner();
}


/**
 * Insert the method's description here.
 * Creation date: (3/11/2004 8:51:11 AM)
 * @return java.lang.String
 */
public java.lang.String getUserName() {
	User user = getUser();
	
	if (user == null) {
		return null;
	}
	
	return user.getName();
}

/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 5:12:18 PM)
 * @return java.lang.String
 */
public String toString() {
	return "[" + getSimulationJobID() + "," + taskID + "]";
}

public String getComputeResource() {
	return computeResource;
}

public boolean compareEqual(Matchable obj) {
	if (obj instanceof SimulationTask){
		SimulationTask other = (SimulationTask)obj;
		if (!Compare.isEqual(getSimulationJob(), other.getSimulationJob())){
			return false;
		}
		if (!Compare.isEqual(getTaskID(), other.getTaskID())){
			return false;
		}
		if (!Compare.isEqualOrNull(getComputeResource(), other.getComputeResource())){
			return false;
		}
		return true;
	}
	return false;
}

public Simulation getSimulation() {
	return getSimulationJob().getSimulation();
}
}
