package cbit.vcell.simdata;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.solver.SimulationInfo;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.server.*;
/**
 * Insert the type's description here.
 * Creation date: (10/3/00 3:21:23 PM)
 * @author: 
 */
public class ServerPDEDataContext extends PDEDataContext {
	private DataServerImpl dataServerImpl = null;
	private VCDataIdentifier vcDataID = null;
	private User user = null;

/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 12:38:48 AM)
 * @param dataSetController cbit.vcell.server.DataSetController
 * @param simulationIdentifier java.lang.String
 */
public ServerPDEDataContext(User user0, DataServerImpl dataServerImpl, VCDataIdentifier vcdID) throws Exception { 
	super();
	user = user0;
	setDataServerImpl(dataServerImpl);
	setVCDataIdentifier(vcdID);
	initialize();
}


/**
 * adds a named <code>Function</code> to the list of variables that are availlable for this Simulation.
 *
 * @param function named expression that is to be bound to dataset and whose name is added to variable list.
 *
 * @throws org.vcell.util.DataAccessException if Function cannot be bound to this dataset or SimulationInfo not found.
 */
public void addFunctions(cbit.vcell.math.AnnotatedFunction[] functionArr,boolean[] bReplaceArr) throws org.vcell.util.DataAccessException {
	getDataServerImpl().addFunctions(user, vcDataID, functionArr,bReplaceArr);
	firePropertyChange(PROP_CHANGE_FUNC_ADDED, null, functionArr);
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
public cbit.vcell.math.AnnotatedFunction[] getFunctions() throws org.vcell.util.DataAccessException {
	return getDataServerImpl().getFunctions(user, vcDataID);
}


/**
 * tests if resultSet contains ODE data for the specified simulation.
 *
 * @returns <i>true</i> if results are of type ODE, <i>false</i> otherwise.
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 *
 * @see Function
 */
public boolean getIsODEData() throws org.vcell.util.DataAccessException {
	return getDataServerImpl().getIsODEData(user, vcDataID);
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
public cbit.plot.PlotData getLineScan(java.lang.String variable, double time, cbit.vcell.simdata.gui.SpatialSelection spatialSelection) throws org.vcell.util.DataAccessException {
	return getDataServerImpl().getLineScan(user, vcDataID, variable, time, spatialSelection);
}


/**
 * Insert the method's description here.
 * Creation date: (3/19/2004 11:31:29 AM)
 * @return cbit.vcell.simdata.SimDataBlock
 * @param varName java.lang.String
 * @param time double
 */
protected ParticleDataBlock getParticleDataBlock(double time) throws org.vcell.util.DataAccessException {
	return getDataServerImpl().getParticleDataBlock(user, vcDataID, time);
}


/**
 * Insert the method's description here.
 * Creation date: (3/19/2004 11:31:29 AM)
 * @return cbit.vcell.simdata.SimDataBlock
 * @param varName java.lang.String
 * @param time double
 */
protected SimDataBlock getSimDataBlock(java.lang.String varName, double time) throws org.vcell.util.DataAccessException {
	return getDataServerImpl().getSimDataBlock(user, vcDataID, varName, time);
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
public org.vcell.util.document.TimeSeriesJobResults getTimeSeriesValues(org.vcell.util.document.TimeSeriesJobSpec timeSeriesJobSpec) throws org.vcell.util.DataAccessException {
	return getDataServerImpl().getTimeSeriesValues(user, vcDataID, timeSeriesJobSpec);
}


/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 12:39:13 AM)
 * @return java.lang.String
 */
public VCDataIdentifier getVCDataIdentifier() {
	return vcDataID;
}


/**
 * Insert the method's description here.
 * Creation date: (10/3/00 5:16:19 PM)
 */
private void initialize() {
	try {
		setTimePoints(getDataServerImpl().getDataSetTimes(user, getVCDataIdentifier()));
		setDataIdentifiers(getDataServerImpl().getDataIdentifiers(user, getVCDataIdentifier()));
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
public void makeRemoteFile(cbit.vcell.export.server.ExportSpecs exportSpecs) throws org.vcell.util.DataAccessException {
	dataServerImpl.makeRemoteFile(user, exportSpecs);
}


/**
 * Insert the method's description here.
 * Creation date: (10/3/00 5:03:43 PM)
 */
public void refreshIdentifiers() {
	try {
		setDataIdentifiers(getDataServerImpl().getDataIdentifiers(user, vcDataID));
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
 * removes the specified <i>function</i> from this Simulation.
 *
 * @param function function to be removed.
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 * @throws org.vcell.util.PermissionException if not the owner of this dataset.
 */
public void removeFunction(cbit.vcell.math.AnnotatedFunction function) throws org.vcell.util.DataAccessException, org.vcell.util.PermissionException {
	getDataServerImpl().removeFunction(user, vcDataID, function);
	firePropertyChange(PROP_CHANGE_FUNC_REMOVED, function, null);
}


/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 12:36:13 AM)
 * @param newDataSetController cbit.vcell.server.DataSetController
 */
private void setDataServerImpl(DataServerImpl newDataServerImpl) {
	dataServerImpl = newDataServerImpl;
}


/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 12:39:13 AM)
 * @param newSimulationIdentifier java.lang.String
 */
private void setVCDataIdentifier(VCDataIdentifier newVcdID) {
	vcDataID = newVcdID;
}
}