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
import org.vcell.util.DataAccessException;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import cbit.plot.PlotData;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.math.Function;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solvers.CartesianMesh;
/**
 * Insert the type's description here.
 * Creation date: (10/3/00 3:21:23 PM)
 * @author: 
 */
public class ServerPDEDataContext extends PDEDataContext {
	private DataServerImpl dataServerImpl = null;
	private User user = null;
	private OutputContext outputContext = null;

/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 12:38:48 AM)
 * @param dataSetController cbit.vcell.server.DataSetController
 * @param simulationIdentifier java.lang.String
 */
public ServerPDEDataContext(OutputContext outputContext,User user0, DataServerImpl dataServerImpl, VCDataIdentifier vcdID) throws Exception { 
	super();
	user = user0;
	setDataServerImpl(dataServerImpl);
	setVCDataIdentifier(vcdID);
	setOutputContext(outputContext);
	initialize();
}


/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 12:36:13 AM)
 * @return cbit.vcell.server.DataSetController
 */
private DataServerImpl getDataServerImpl() {
	return dataServerImpl;
}


/**
 * gets list of named Functions defined for the resultSet for this Simulation.
 *
 * @returns array of functions, or null if no functions.
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 *
 * @see Function
 */
public AnnotatedFunction[] getFunctions() throws DataAccessException {
	return getDataServerImpl().getFunctions(outputContext,user, getVCDataIdentifier());
}


/**
 * retrieves a line scan (data sampled along a curve in space) for the specified simulation.
 *
 * @param variable name of variable to be sampled
 * @param time simulation time which is to be sampled.
 * @param spatialSelection spatial curve.
 *
 * @returns annotated array of 'concentration vs. distance' in a plot ready format.
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 *
 * @see PlotData
 */
public PlotData getLineScan(java.lang.String variable, double time, SpatialSelection spatialSelection) throws DataAccessException {
	return getDataServerImpl().getLineScan(getOutputContext(),user, getVCDataIdentifier(), variable, time, spatialSelection);
}


/**
 * Insert the method's description here.
 * Creation date: (3/19/2004 11:31:29 AM)
 * @return cbit.vcell.simdata.SimDataBlock
 * @param varName java.lang.String
 * @param time double
 */
protected ParticleDataBlock getParticleDataBlock(double time) throws DataAccessException {
	return getDataServerImpl().getParticleDataBlock(user, getVCDataIdentifier(), time);
}


/**
 * Insert the method's description here.
 * Creation date: (3/19/2004 11:31:29 AM)
 * @return cbit.vcell.simdata.SimDataBlock
 * @param varName java.lang.String
 * @param time double
 */
protected SimDataBlock getSimDataBlock(java.lang.String varName, double time) throws DataAccessException {
	return getDataServerImpl().getSimDataBlock(getOutputContext(),user, getVCDataIdentifier(), varName, time);
}

public DataOperationResults doDataOperation(DataOperation dataOperation) throws DataAccessException {
	return getDataServerImpl().doDataOperation(user, dataOperation);
}


/**
 * retrieves a time series (single point as a function of time) of a specified spatial data set.
 *
 * @param variable name of variable to be sampled
 * @param index identifies index into data array.
 *
 * @returns annotated array of 'concentration vs. time' in a plot ready format.
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 *
 * @see CartesianMesh for transformation between indices and coordinates.
 */
public org.vcell.util.document.TimeSeriesJobResults getTimeSeriesValues(TimeSeriesJobSpec timeSeriesJobSpec) throws DataAccessException {
	return getDataServerImpl().getTimeSeriesValues(getOutputContext(),user, getVCDataIdentifier(), timeSeriesJobSpec);
}

/**
 * Insert the method's description here.
 * Creation date: (10/3/00 5:16:19 PM)
 */
private void initialize() {
	try {
		setTimePoints(getDataServerImpl().getDataSetTimes(user, getVCDataIdentifier()));
		setDataIdentifiers(getDataServerImpl().getDataIdentifiers(getOutputContext(),user, getVCDataIdentifier()));
		setParticleData(getDataServerImpl().getParticleDataExists(user, getVCDataIdentifier()));
		setCartesianMesh(getDataServerImpl().getMesh(user, getVCDataIdentifier()));
		if (getTimePoints() != null && getTimePoints().length >0) {
			setTimePoint(getTimePoints()[0]);
		}
		if (getVariableNames() != null && getVariableNames().length > 0) {
			setVariableName(getVariableNames()[0]);
		}
	} catch (DataAccessException exc) {
		exc.printStackTrace(System.out);
	}
}


/**
 * This method was created in VisualAge.
 *
 * @param exportSpec cbit.vcell.export.server.ExportSpecs
 */
public void makeRemoteFile(ExportSpecs exportSpecs) throws DataAccessException {
	dataServerImpl.makeRemoteFile(getOutputContext(),user, exportSpecs);
}


/**
 * Insert the method's description here.
 * Creation date: (10/3/00 5:03:43 PM)
 */
public void refreshIdentifiers() {
	try {
		setDataIdentifiers(getDataServerImpl().getDataIdentifiers(getOutputContext(),user, getVCDataIdentifier()));
		if ( getVariableName() != null && !org.vcell.util.BeanUtils.arrayContains(getVariableNames(), getVariableName()) )  {
			// This condition occurs if a function has been removed from the dataset (esp. MergedDataset->compare).
			if (getDataIdentifiers() != null && getDataIdentifiers().length > 0) {
				setVariableName(getDataIdentifiers()[0].getName());
			}
		}		
		//
		//Added for cases where variable was set and server couldn't deliver data
		//if after referesh the variable is set again, the property won't propagate
		externalRefresh();
	} catch (DataAccessException exc) {
		exc.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/3/00 5:03:43 PM)
 */
public void refreshTimes() {
	try {
		setTimePoints(getDataServerImpl().getDataSetTimes(user, getVCDataIdentifier()));
	} catch (DataAccessException exc) {
		exc.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 12:36:13 AM)
 * @param newDataSetController cbit.vcell.server.DataSetController
 */
private void setDataServerImpl(DataServerImpl newDataServerImpl) {
	dataServerImpl = newDataServerImpl;
}

private OutputContext getOutputContext() {
	return outputContext;
}


private void setOutputContext(OutputContext outputContext) {
	this.outputContext = outputContext;
}
}
