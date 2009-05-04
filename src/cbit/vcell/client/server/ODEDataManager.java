package cbit.vcell.client.server;
import org.vcell.util.CoordinateIndex;
import org.vcell.util.DataAccessException;
import org.vcell.util.TimeSeriesJobResults;
import org.vcell.util.TimeSeriesJobSpec;
import org.vcell.util.VCDataIdentifier;

import cbit.plot.PlotData;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.solver.DataProcessingOutput;
import cbit.vcell.solver.ode.*;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.desktop.controls.*;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.ParticleDataBlock;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.gui.SpatialSelection;
/**
 * Insert the type's description here.
 * Creation date: (6/11/2004 5:34:33 AM)
 * @author: Ion Moraru
 */
public class ODEDataManager implements DataManager {
	private VCDataManager vcDataManager = null;
	private VCDataIdentifier vcDataIdentifier = null;
	private ODESolverResultSet odeSolverResultSet = null;

/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 3:46:51 PM)
 * @param vcDataManager cbit.vcell.client.server.VCDataManager
 * @param vcDataIdentifier cbit.vcell.server.VCDataIdentifier
 */
public ODEDataManager(VCDataManager vcDataManager, VCDataIdentifier vcDataIdentifier) {
	setVcDataManager(vcDataManager);
	setVcDataIdentifier(vcDataIdentifier);
}


/**
 * adds an array of named <code>Function</code>s to the list of variables that are availlable for this Simulation.
 * 
 * @param functions represent named expressions that are to be bound to dataset and whose names are added to variable list.
 * 
 * @throws org.vcell.util.DataAccessException if Functions cannot be bound to this dataset or SimulationInfo not found.
 */
public void addFunctions(AnnotatedFunction[] functions,boolean[] bReplaceArr) throws DataAccessException {
	getVcDataManager().addFunctions(getVcDataIdentifier(), functions,bReplaceArr);
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
public DataIdentifier[] getDataIdentifiers() throws DataAccessException {
	return getVcDataManager().getDataIdentifiers(getVcDataIdentifier());
}


/**
 * gets all times at which simulation result data is availlable for this Simulation.
 * 
 * @returns double array of times of availlable data, or null if no data.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
public double[] getDataSetTimes() throws DataAccessException {
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
public AnnotatedFunction[] getFunctions() throws DataAccessException {
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
public boolean getIsODEData() throws DataAccessException {
	return true;
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
public PlotData getLineScan(String variable, double time, CoordinateIndex begin, CoordinateIndex end) throws DataAccessException {
	throw new RuntimeException("Not implemented");
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
public cbit.plot.PlotData getLineScan(String variable, double time, SpatialSelection spatialSelection) throws DataAccessException {
	throw new RuntimeException("Not implemented");
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
public CartesianMesh getMesh() throws DataAccessException {
	return null;
}


/**
 * retrieves the non-spatial (ODE) results for this Simulation.  This is assumed not to change over the life
 * of the simulation
 * 
 * @returns non-spatial (ODE) data.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
public ODESolverResultSet getODESolverResultSet() throws DataAccessException {	
	return odeSolverResultSet;
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
public ParticleDataBlock getParticleDataBlock(double time) throws DataAccessException {
	return null;
}


public DataProcessingOutput getDataProcessingOutput() throws DataAccessException {
	return getVcDataManager().getDataProcessingOutput(getVcDataIdentifier());
}


/**
 * determines if the result set for this Simulation contains particle data.
 * 
 * @returns <i>true</i> if there is particle data availlable.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
public boolean getParticleDataExists() throws DataAccessException {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (6/13/2004 3:04:49 PM)
 * @return cbit.vcell.simdata.PDEDataContext
 */
public PDEDataContext getPDEDataContext() {
	return null;
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
public SimDataBlock getSimDataBlock(String varName, double time) throws DataAccessException {
	return null;
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
	throw new RuntimeException("Not implemented");
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 3:53:33 PM)
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
 * Creation date: (6/11/2004 3:53:33 PM)
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
public void removeFunction(AnnotatedFunction function) throws DataAccessException {
	getVcDataManager().removeFunction(function, getVcDataIdentifier());
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 3:53:33 PM)
 * @param newVcDataIdentifier cbit.vcell.server.VCDataIdentifier
 */
private void setVcDataIdentifier(VCDataIdentifier newVcDataIdentifier) {
	vcDataIdentifier = newVcDataIdentifier;
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 3:53:33 PM)
 * @param newVcDataManager cbit.vcell.client.server.VCDataManager
 */
private void setVcDataManager(VCDataManager newVcDataManager) {
	vcDataManager = newVcDataManager;
}

public void connect() throws DataAccessException {
	odeSolverResultSet = getVcDataManager().getODEData(getVcDataIdentifier());
}

}