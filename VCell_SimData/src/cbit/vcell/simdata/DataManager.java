package cbit.vcell.simdata;
import org.vcell.util.CoordinateIndex;
import org.vcell.util.DataAccessException;
import org.vcell.util.PermissionException;
import org.vcell.util.VCDataIdentifier;

import cbit.plot.PlotData;
import cbit.vcell.math.Function;
import cbit.vcell.mesh.CartesianMesh;

public interface DataManager {

/**
 * adds a named <code>Function</code> to the list of variables that are availlable for this Simulation.
 * 
 * @param function named expression that is to be bound to dataset and whose name is added to variable list.
 * 
 * @throws org.vcell.util.DataAccessException if Function cannot be bound to this dataset or SimulationInfo not found.
 */
void addFunction(cbit.vcell.math.AnnotatedFunction function) throws DataAccessException;

/**
 * adds an array of named <code>Function</code>s to the list of variables that are availlable for this Simulation.
 * 
 * @param functions represent named expressions that are to be bound to dataset and whose names are added to variable list.
 * 
 * @throws org.vcell.util.DataAccessException if Functions cannot be bound to this dataset or SimulationInfo not found.
 */
void addFunctions(cbit.vcell.math.AnnotatedFunction[] functions) throws DataAccessException;

/**
 * retrieves a list of data names (state variables and functions) defined for this Simulation.
 * 
 * @param simulationInfo simulation database reference
 * 
 * @returns array of availlable data names.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
cbit.vcell.math.DataIdentifier[] getDataIdentifiers() throws DataAccessException;


/**
 * gets all times at which simulation result data is availlable for this Simulation.
 * 
 * @returns double array of times of availlable data, or null if no data.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
double[] getDataSetTimes() throws DataAccessException;


/**
 * gets list of named Functions defined for the resultSet for this Simulation.
 * 
 * @returns array of functions, or null if no functions.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 * 
 * @see Function
 */
cbit.vcell.math.AnnotatedFunction[] getFunctions() throws DataAccessException;


/**
 * tests if resultSet contains ODE data for the specified simulation.
 * 
 * @returns <i>true</i> if results are of type ODE, <i>false</i> otherwise.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 * 
 * @see Function
 */
boolean getIsODEData() throws DataAccessException;


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
PlotData getLineScan(String variable, double time, CoordinateIndex begin, CoordinateIndex end) throws DataAccessException;


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
PlotData getLineScan(String variable, double time, SpatialSelection spatialSelection) throws DataAccessException;


/**
 * retrieves the Mesh object for this Simulation.
 * 
 * @returns mesh associated with this data (allows spatial interpretation of indexed data).
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 * 
 * @see CartesianMesh
 */
CartesianMesh getMesh() throws DataAccessException;


/**
 * retrieves the non-spatial (ODE) results for this Simulation.  This is assumed not to change over the life
 * of the simulation
 * 
 * @returns non-spatial (ODE) data.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
ODESolverResultSet getODESolverResultSet() throws DataAccessException;


/**
 * retrieves the particle data for this Simulation.
 * 
 * @returns particle data for this result set.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found, or if no particle data.
 * 
 * @see ParticleDataBlock
 */
cbit.vcell.simdata.ParticleDataBlock getParticleDataBlock(double time) throws DataAccessException;


/**
 * determines if the result set for this Simulation contains particle data.
 * 
 * @returns <i>true</i> if there is particle data availlable.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
boolean getParticleDataExists() throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (6/13/2004 1:19:57 PM)
 * @return cbit.vcell.simdata.PDEDataContext
 */
public PDEDataContext getPDEDataContext();


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
cbit.vcell.simdata.SimDataBlock getSimDataBlock(String varName, double time) throws DataAccessException;


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
org.vcell.util.TimeSeriesJobResults getTimeSeriesValues(org.vcell.util.TimeSeriesJobSpec timeSeriesJobSpec) throws DataAccessException;


/**
 * Gets the simulationInfo property (cbit.vcell.solver.SimulationInfo) value.
 * @return The simulationInfo property value.
 */
VCDataIdentifier getVCDataIdentifier();


/**
 * removes the specified <i>function</i> from this Simulation.
 * 
 * @param function function to be removed.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 * @throws cbit.vcell.server.PermissionException if not the owner of this dataset.
 */
void removeFunction(cbit.vcell.math.AnnotatedFunction function) throws DataAccessException, PermissionException;
}