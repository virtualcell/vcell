package cbit.vcell.client.server;
import cbit.plot.PlotData;
import cbit.vcell.math.Function;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.ParticleDataBlock;
import cbit.vcell.simdata.gui.SpatialSelection;
import cbit.vcell.solver.DataProcessingOutput;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solvers.CartesianMesh;

import org.vcell.util.CoordinateIndex;
import org.vcell.util.DataAccessException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.VCDataIdentifier;

public interface DataManager {

	/**
 * adds an array of named <code>Function</code>s to the list of variables that are availlable for this Simulation.
 * 
 * @param functions represent named expressions that are to be bound to dataset and whose names are added to variable list.
 * 
 * @throws org.vcell.util.DataAccessException if Functions cannot be bound to this dataset or SimulationInfo not found.
 */
void addFunctions(cbit.vcell.math.AnnotatedFunction[] functions,boolean[] bReplaceArr) throws DataAccessException;

/**
 * retrieves a list of data names (state variables and functions) defined for this Simulation.
 * 
 * @param simulationInfo simulation database reference
 * 
 * @returns array of availlable data names.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
cbit.vcell.simdata.DataIdentifier[] getDataIdentifiers() throws DataAccessException;


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
 * @throws org.vcell.util.PermissionException if not the owner of this dataset.
 */
void removeFunction(cbit.vcell.math.AnnotatedFunction function) throws DataAccessException, PermissionException;
}