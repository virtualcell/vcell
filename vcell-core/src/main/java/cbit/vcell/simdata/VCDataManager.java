/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.solver.nfsim.NFSimMolecularConfigurations;
import org.vcell.util.DataAccessException;
import org.vcell.util.VCellThreadChecker;
import org.vcell.util.document.TimeSeriesJobResults;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuVarInfo;

import cbit.plot.PlotData;
import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.math.Function;
import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory.RemoteProxyException;
import cbit.vcell.server.DataSetController;
import cbit.vcell.server.DataSetControllerProvider;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solvers.CartesianMesh;
/**
 * Insert the type's description here.
 * Creation date: (6/11/2004 5:36:06 AM)
 * @author: Ion Moraru
 */
public class VCDataManager {
	private final static Logger lg = LogManager.getLogger(VCDataManager.class);

	private DataSetControllerProvider dataSetControllerProvider;

public VCDataManager(DataSetControllerProvider dataSetControllerProvider) {
	this.dataSetControllerProvider = dataSetControllerProvider;
}


public synchronized FieldDataFileOperationResults fieldDataFileOperation(FieldDataFileOperationSpec fieldDataFielOperationSpec) throws DataAccessException {
	try {
		return getDataSetController().fieldDataFileOperation(fieldDataFielOperationSpec);
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		try {
			return getDataSetController().fieldDataFileOperation(fieldDataFielOperationSpec);
		}catch (RemoteProxyException e2){
			handleRemoteProxyException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}

/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 5:56:57 AM)
 * @return cbit.vcell.client.server.ClientServerManager
 */
private DataSetControllerProvider getDataSetControllerProvider() {
	return dataSetControllerProvider;
}


/**
 * retrieves a list of data names (state variables and functions) defined for this Simulation.
 **
 * @returns array of availlable data names.
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
public synchronized DataIdentifier[] getDataIdentifiers(OutputContext outputContext, VCDataIdentifier vcdID) throws DataAccessException {
	try {
		return getDataSetController().getDataIdentifiers(outputContext,vcdID);
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		try {
			return getDataSetController().getDataIdentifiers(outputContext,vcdID);
		}catch (RemoteProxyException e2){
			handleRemoteProxyException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 5:58:43 AM)
 * @return cbit.vcell.server.DataSetController
 * @exception org.vcell.util.DataAccessException The exception description.
 */
private DataSetController getDataSetController() throws DataAccessException {
	// convenience method
	VCellThreadChecker.checkRemoteInvocation();
	
	return getDataSetControllerProvider().getDataSetController();
}


/**
 * gets all times at which simulation result data is availlable for this Simulation.
 *
 * @returns double array of times of availlable data, or null if no data.
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
public synchronized double[] getDataSetTimes(VCDataIdentifier vcdID) throws DataAccessException {
	try {
		return getDataSetController().getDataSetTimes(vcdID);
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		try {
			return getDataSetController().getDataSetTimes(vcdID);
		}catch (RemoteProxyException e2){
			handleRemoteProxyException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * gets all times at which simulation result data is availlable for this Simulation.
 *
 * @returns double array of times of availlable data, or null if no data.
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
public synchronized double[] getVtuTimes(VCDataIdentifier vcdID) throws DataAccessException {
	try {
		return getDataSetController().getVtuTimes(vcdID);
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		try {
			return getDataSetController().getVtuTimes(vcdID);
		}catch (RemoteProxyException e2){
			handleRemoteProxyException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * gets list of named Functions defined for the simulation.
 *
 * @returns array of functions, or null if no functions.
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 *
 * @see Function
 */
public synchronized cbit.vcell.solver.AnnotatedFunction[] getFunctions(OutputContext outputContext, org.vcell.util.document.VCDataIdentifier vcdID) throws DataAccessException {
	try {
		return getDataSetController().getFunctions(outputContext,vcdID);
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		try {
			return getDataSetController().getFunctions(outputContext,vcdID);
		}catch (RemoteProxyException e2){
			handleRemoteProxyException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * retrieves a line scan (data sampled along a curve in space) for the specified simulation.
 *
  * @param variable name of variable to be sampled
 * @param time simulation time which is to be sampled.
 * @param spatialSelection spatial curve used for sampling.
 *
 * @returns annotated array of 'concentration vs. distance' in a plot ready format.
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 *
 * @see PlotData
 */
public synchronized PlotData getLineScan(OutputContext outputContext, VCDataIdentifier vcdID, String variable, double time, SpatialSelection spatialSelection) throws DataAccessException {
	try {
		return getDataSetController().getLineScan(outputContext,vcdID,variable,time,spatialSelection);
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		try {
			return getDataSetController().getLineScan(outputContext,vcdID,variable,time,spatialSelection);
		}catch (RemoteProxyException e2){
			handleRemoteProxyException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * retrieves the Mesh object for this Simulation.
 *
 * @returns mesh associated with this data (allows spatial interpretation of indexed data).
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 *
 * @see CartesianMesh
 */
public synchronized CartesianMesh getMesh(VCDataIdentifier vcdID) throws DataAccessException {
	try {
		return getDataSetController().getMesh(vcdID);
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		try {
			return getDataSetController().getMesh(vcdID);
		}catch (RemoteProxyException e2){
			handleRemoteProxyException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * retrieves the non-spatial (ODE) results for this Simulation.
 **
 * @returns non-spatial (ODE) data.
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
public synchronized ODESimData getODEData(VCDataIdentifier vcdID) throws DataAccessException {
	try {
		return getDataSetController().getODEData(vcdID);
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		try {
			return getDataSetController().getODEData(vcdID);
		}catch (RemoteProxyException e2){
			handleRemoteProxyException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


public synchronized NFSimMolecularConfigurations getNFSimMolecularConfigurations(VCDataIdentifier vcdID) throws DataAccessException {
	try {
		return getDataSetController().getNFSimMolecularConfigurations(vcdID);
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		try {
			return getDataSetController().getNFSimMolecularConfigurations(vcdID);
		}catch (RemoteProxyException e2){
			handleRemoteProxyException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * retrieves the particle data for this Simulation.
 *
 * @returns particle data for this result set.
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found, or if no particle data.
 *
 * @see ParticleDataBlock
 */
public synchronized ParticleDataBlock getParticleDataBlock(VCDataIdentifier vcdID, double time) throws DataAccessException {
	try {
		return getDataSetController().getParticleDataBlock(vcdID,time);
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		try {
			return getDataSetController().getParticleDataBlock(vcdID,time);
		}catch (RemoteProxyException e2){
			handleRemoteProxyException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}

public synchronized DataOperationResults doDataOperation(DataOperation dataOperation) throws DataAccessException {
	try {
		return getDataSetController().doDataOperation(dataOperation);
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		try {
			return getDataSetController().doDataOperation(dataOperation);
		}catch (RemoteProxyException e2){
			handleRemoteProxyException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}



/**
 * determines if the result set for this Simulation contains particle data.
 *
 * @returns <i>true</i> if there is particle data availlable.
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
public synchronized boolean getParticleDataExists(VCDataIdentifier vcdID) throws DataAccessException {
	try {
		return getDataSetController().getParticleDataExists(vcdID);
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		try {
			return getDataSetController().getParticleDataExists(vcdID);
		}catch (RemoteProxyException e2){
			handleRemoteProxyException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * retrieves the spatial (PDE) data for this Simulation, Variable, and Time.
 *
 * @param varName name of dataSet (state variable or function).
 * @param time simulation time of data.
 *
 * @returns spatial (PDE) data for this result set associated with the specified variable name and time, 
 *          or <i>null</i> if no data availlable.
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
public synchronized SimDataBlock getSimDataBlock(OutputContext outputContext, VCDataIdentifier vcdID, String varName, double time) throws DataAccessException {
	try {
		return getDataSetController().getSimDataBlock(outputContext,vcdID,varName,time);
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		try {
			return getDataSetController().getSimDataBlock(outputContext,vcdID,varName,time);
		}catch (RemoteProxyException e2){
			handleRemoteProxyException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * retrieves a time series (single point as a function of time) of a specified spatial data set.
 *
 *
 * @returns annotated array of 'concentration vs. time' in a plot ready format.
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 *
 * @see CartesianMesh for transformation between indices and coordinates.
 */
public synchronized TimeSeriesJobResults getTimeSeriesValues(OutputContext outputContext, VCDataIdentifier vcdID, TimeSeriesJobSpec timeSeriesJobSpec) throws DataAccessException {
	try {
		return getDataSetController().getTimeSeriesValues(outputContext,vcdID,timeSeriesJobSpec);
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		try {
			return getDataSetController().getTimeSeriesValues(outputContext,vcdID,timeSeriesJobSpec);
		}catch (RemoteProxyException e2){
			handleRemoteProxyException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}

private void handleRemoteProxyException(RemoteProxyException remoteProxyException) {
	lg.error("Handling RemoteProxyException", remoteProxyException);
}


public synchronized VtuFileContainer getEmptyVtuMeshFiles(VCDataIdentifier vcDataIdentifier, int timeIndex) throws DataAccessException {
	try {
		return getDataSetController().getEmptyVtuMeshFiles(vcDataIdentifier, timeIndex);
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		try {
			return getDataSetController().getEmptyVtuMeshFiles(vcDataIdentifier, timeIndex);
		}catch (RemoteProxyException e2){
			handleRemoteProxyException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}

public synchronized double[] getVtuMeshData(OutputContext outputContext, VCDataIdentifier vcDataIdentifier, VtuVarInfo var, double time) throws DataAccessException {
	try {
		return getDataSetController().getVtuMeshData(outputContext, vcDataIdentifier, var, time);
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		try {
			return getDataSetController().getVtuMeshData(outputContext, vcDataIdentifier, var, time);
		}catch (RemoteProxyException e2){
			handleRemoteProxyException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


public VtuVarInfo[] getVtuVarInfos(OutputContext outputContext,	VCDataIdentifier vcDataIdentifier) throws DataAccessException {
	try {
		return getDataSetController().getVtuVarInfos(outputContext, vcDataIdentifier);
	}catch (RemoteProxyException e){
		handleRemoteProxyException(e);
		try {
			return getDataSetController().getVtuVarInfos(outputContext, vcDataIdentifier);
		}catch (RemoteProxyException e2){
			handleRemoteProxyException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}

}
