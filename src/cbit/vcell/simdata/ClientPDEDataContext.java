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
import org.vcell.util.document.TimeSeriesJobResults;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.VCDataIdentifier;

import cbit.plot.PlotData;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.math.Function;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solvers.CartesianMesh;

/**
 * Insert the type's description here.
 * Creation date: (3/19/2004 10:42:31 AM)
 * @author: Fei Gao
 */
public class ClientPDEDataContext extends PDEDataContext implements DataListener {
		//
	private PDEDataManager dataManager = null;

/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 3:18:21 PM)
 * @param simManager cbit.vcell.desktop.controls.SimulationManager
 */
public ClientPDEDataContext(PDEDataManager argDataManager) {
	super();
	if (argDataManager != null) {
		this.dataManager = argDataManager;
		initialize();
	} else {
		throw new RuntimeException("Data Manager can not be null in ClientPDEDataContext");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 3:19:48 PM)
 * @return cbit.vcell.desktop.controls.SimulationManager
 */
public PDEDataManager getDataManager() {
	return dataManager;
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
	return dataManager.getFunctions();
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
	return dataManager.getLineScan(variable, time, spatialSelection);
}


/**
 * Insert the method's description here.
 * Creation date: (3/19/2004 11:27:04 AM)
 * @return cbit.vcell.simdata.SimDataBlock
 * @param varName java.lang.String
 * @param time double
 */
protected ParticleDataBlock getParticleDataBlock(double time) throws DataAccessException {
	return getDataManager().getParticleDataBlock(time);
}


/**
 * Insert the method's description here.
 * Creation date: (3/19/2004 11:27:04 AM)
 * @return cbit.vcell.simdata.SimDataBlock
 * @param varName java.lang.String
 * @param time double
 */
protected SimDataBlock getSimDataBlock(java.lang.String varName, double time) throws DataAccessException {
	return getDataManager().getSimDataBlock(varName, time);
}

public DataOperationResults doDataOperation(DataOperation dataOperation) throws DataAccessException {
	return getDataManager().doDataOperation(dataOperation);
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
public TimeSeriesJobResults getTimeSeriesValues(TimeSeriesJobSpec timeSeriesJobSpec) throws DataAccessException {
	return dataManager.getTimeSeriesValues(timeSeriesJobSpec);
}

/**
 * Insert the method's description here.
 * Creation date: (10/3/00 5:16:19 PM)
 */
private void initialize() {
	try {
		setVCDataIdentifier(getDataManager().getVCDataIdentifier());
		setParticleData(getDataManager().getParticleDataExists());
		setCartesianMesh(getDataManager().getMesh());
		setTimePoints(getDataManager().getDataSetTimes());
		setDataIdentifiers(getDataManager().getDataIdentifiers());
		double tp = getTimePoint();
		double[] timePoints = getTimePoints();
		if (timePoints != null && timePoints.length >0) {
			tp = timePoints[0];
		}
		DataIdentifier variable = getDataIdentifier();
		DataIdentifier[] dataIdentifiers = getDataIdentifiers();
		if (dataIdentifiers != null && dataIdentifiers.length > 0) {
			variable = dataIdentifiers[0];
		}
		setVariableAndTime(variable, tp);
	} catch (DataAccessException exc) {
		exc.printStackTrace(System.out);
		throw new RuntimeException(exc.getMessage(),exc);
	}
}


/**
 * This method was created in VisualAge.
 *
 * @param exportSpec cbit.vcell.export.server.ExportSpecs
 */
//public abstract void makeRemoteFile(ExportSpecs exportSpecs) throws DataAccessException;


/**
 * 
 * @param event cbit.vcell.desktop.controls.SimulationEvent
 */
public void newData(DataEvent event) {
	try {
		setTimePoints(getDataManager().getDataSetTimes());
		//
		//Added for cases where time was set and server couldn't deliver data
		//if after referesh the time is set again, the property won't propagate
		//refreshData();
	} catch (DataAccessException exc) {
		exc.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/3/00 5:03:43 PM)
 */
//public abstract void refreshIdentifiers();


/**
 * This method is called from SimResultsViewer.updateScanParamChoices(),
 * which is going to update results view upon a choice of scanned parameters.
 */
public void setDataManager(PDEDataManager newDataManager) throws DataAccessException{
	VCDataIdentifier oldid = dataManager.getVCDataIdentifier();
	VCDataIdentifier newid = newDataManager.getVCDataIdentifier();
	if (oldid instanceof VCSimulationDataIdentifier &&
		newid instanceof VCSimulationDataIdentifier &&
		((VCSimulationDataIdentifier)oldid).getVcSimID().equals(((VCSimulationDataIdentifier)newid).getVcSimID())
	) {	
		PDEDataManager oldPDataManager = dataManager;
		dataManager = newDataManager;
		DataIdentifier[] dis = getDataManager().getDataIdentifiers();
		double[] times = getDataManager().getDataSetTimes();
		setDataIdentifiers(dis);
		setTimePoints(times);
		externalRefresh();
		setVCDataIdentifier(dataManager.getVCDataIdentifier());
		firePropertyChange(SimDataConstants.PDE_DATA_MANAGER_CHANGED, oldPDataManager, newDataManager);
	} else {
		throw new RuntimeException("DataManager change not allowed: oldID = "+oldid+" newID = "+newid);
	}
}


/**
 * This method was created in VisualAge.
 *
 * @param exportSpec cbit.vcell.export.server.ExportSpecs
 */
@Override
public void makeRemoteFile(ExportSpecs exportSpecs) throws DataAccessException {
	throw new RuntimeException("should not use this method in NewClientPDEDataContext");
}


/**
 * Insert the method's description here.
 * Creation date: (10/3/00 5:03:43 PM)
 */
@Override
public void refreshIdentifiers() {
	try {
		DataIdentifier[] newDataIdentifiers = getDataManager().getDataIdentifiers();
		setDataIdentifiers(newDataIdentifiers);
	} catch (DataAccessException exc) {
		exc.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/3/00 5:03:43 PM)
 */
public void refreshTimes() throws DataAccessException {
	setTimePoints(getDataManager().getDataSetTimes());
}
}
