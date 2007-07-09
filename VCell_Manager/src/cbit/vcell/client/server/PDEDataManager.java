package cbit.vcell.client.server;
import org.vcell.util.VCDataIdentifier;

import cbit.vcell.desktop.controls.*;
import cbit.vcell.simdata.DataManager;
/**
 * Insert the type's description here.
 * Creation date: (6/11/2004 5:35:12 AM)
 * @author: Ion Moraru
 */
public class PDEDataManager implements DataManager {
	private VCDataManager vcDataManager = null;
	private VCDataIdentifier vcDataIdentifier = null;
	private NewClientPDEDataContext newClientPDEDataContext = null;

/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 3:46:51 PM)
 * @param vcDataManager cbit.vcell.client.server.VCDataManager
 * @param vcDataIdentifier cbit.vcell.server.VCDataIdentifier
 */
public PDEDataManager(VCDataManager vcDataManager, VCDataIdentifier vcDataIdentifier) {
	setVcDataManager(vcDataManager);
	setVcDataIdentifier(vcDataIdentifier);
}


/**
 * adds a named <code>Function</code> to the list of variables that are availlable for this Simulation.
 * 
 * @param function named expression that is to be bound to dataset and whose name is added to variable list.
 * 
 * @throws org.vcell.util.DataAccessException if Function cannot be bound to this dataset or SimulationInfo not found.
 */
public void addFunction(cbit.vcell.math.AnnotatedFunction function) throws org.vcell.util.DataAccessException {
	getVcDataManager().addFunction(getVcDataIdentifier(), function);
}


/**
 * adds an array of named <code>Function</code>s to the list of variables that are availlable for this Simulation.
 * 
 * @param functions represent named expressions that are to be bound to dataset and whose names are added to variable list.
 * 
 * @throws org.vcell.util.DataAccessException if Functions cannot be bound to this dataset or SimulationInfo not found.
 */
public void addFunctions(cbit.vcell.math.AnnotatedFunction[] functions) throws org.vcell.util.DataAccessException {
	getVcDataManager().addFunctions(getVcDataIdentifier(), functions);
}


/**
 * retrieves a list of data names (state variables and functions) defined for this Simulation.
 * 
 * @param simulationInfo simulation database reference
 * 
 * @returns array of availlable data names.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
public cbit.vcell.math.DataIdentifier[] getDataIdentifiers() throws org.vcell.util.DataAccessException {
	return getVcDataManager().getDataIdentifiers(getVcDataIdentifier());
}


/**
 * gets all times at which simulation result data is availlable for this Simulation.
 * 
 * @returns double array of times of availlable data, or null if no data.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
public double[] getDataSetTimes() throws org.vcell.util.DataAccessException {
	return getVcDataManager().getDataSetTimes(getVcDataIdentifier());
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
	return getVcDataManager().getFunctions(getVcDataIdentifier());
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
	return false;
}


/**
 * retrieves a line scan (data sampled along a line in space) for the specified simulation.
 * 
 * @param variable name of variable to be sampled
 * @param time simulation time which is to be sampled.
 * @param begin i,j,k of start of line.
 * @param end i,j,k coordinate of end of line.
 * 
 * @returns annotated array of 'concentration vs. distance' in a plot ready format.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 * 
 * @see PlotData
 */
public cbit.plot.PlotData getLineScan(String variable, double time, org.vcell.util.CoordinateIndex begin, org.vcell.util.CoordinateIndex end) throws org.vcell.util.DataAccessException {
	return getVcDataManager().getLineScan(getVcDataIdentifier(), variable, time, begin, end);
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
public cbit.plot.PlotData getLineScan(String variable, double time, cbit.vcell.simdata.SpatialSelection spatialSelection) throws org.vcell.util.DataAccessException {
	return getVcDataManager().getLineScan(getVcDataIdentifier(), variable, time, spatialSelection);
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
public cbit.vcell.mesh.CartesianMesh getMesh() throws org.vcell.util.DataAccessException {
	return getVcDataManager().getMesh(getVcDataIdentifier());
}


/**
 * retrieves the non-spatial (ODE) results for this Simulation.  This is assumed not to change over the life
 * of the simulation
 * 
 * @returns non-spatial (ODE) data.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
public cbit.vcell.simdata.ODESolverResultSet getODESolverResultSet() throws org.vcell.util.DataAccessException {
	return null;
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
public cbit.vcell.simdata.ParticleDataBlock getParticleDataBlock(double time) throws org.vcell.util.DataAccessException {
	return getVcDataManager().getParticleDataBlock(getVcDataIdentifier(), time);
}


/**
 * determines if the result set for this Simulation contains particle data.
 * 
 * @returns <i>true</i> if there is particle data availlable.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
public boolean getParticleDataExists() throws org.vcell.util.DataAccessException {
	return getVcDataManager().getParticleDataExists(getVcDataIdentifier());
}


/**
 * Insert the method's description here.
 * Creation date: (6/13/2004 3:04:49 PM)
 * @return cbit.vcell.simdata.PDEDataContext
 */
public cbit.vcell.simdata.PDEDataContext getPDEDataContext() {
	if (newClientPDEDataContext == null) {
		newClientPDEDataContext = new NewClientPDEDataContext(this);
	}
	return newClientPDEDataContext;
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
public cbit.vcell.simdata.SimDataBlock getSimDataBlock(String varName, double time) throws org.vcell.util.DataAccessException {
	return getVcDataManager().getSimDataBlock(getVcDataIdentifier(), varName, time);
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
public org.vcell.util.TimeSeriesJobResults getTimeSeriesValues(org.vcell.util.TimeSeriesJobSpec timeSeriesJobSpec) throws org.vcell.util.DataAccessException {
	return getVcDataManager().getTimeSeriesValues(getVcDataIdentifier(),timeSeriesJobSpec);
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 3:53:21 PM)
 * @return cbit.vcell.server.VCDataIdentifier
 */
private VCDataIdentifier getVcDataIdentifier() {
	return vcDataIdentifier;
}


/**
 * Gets the simulationInfo property (cbit.vcell.solver.SimulationInfo) value.
 * @return The simulationInfo property value.
 */
public VCDataIdentifier getVCDataIdentifier() {
	return getVcDataIdentifier();
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 3:53:21 PM)
 * @return cbit.vcell.client.server.VCDataManager
 */
private VCDataManager getVcDataManager() {
	return vcDataManager;
}


/**
 * removes the specified <i>function</i> from this Simulation.
 * 
 * @param function function to be removed.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 * @throws org.vcell.util.PermissionException if not the owner of this dataset.
 */
public void removeFunction(cbit.vcell.math.AnnotatedFunction function) throws org.vcell.util.DataAccessException {
	getVcDataManager().removeFunction(function, getVcDataIdentifier());
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 3:53:21 PM)
 * @param newVcDataIdentifier cbit.vcell.server.VCDataIdentifier
 */
private void setVcDataIdentifier(VCDataIdentifier newVcDataIdentifier) {
	vcDataIdentifier = newVcDataIdentifier;
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 3:53:21 PM)
 * @param newVcDataManager cbit.vcell.client.server.VCDataManager
 */
private void setVcDataManager(VCDataManager newVcDataManager) {
	vcDataManager = newVcDataManager;
}
}