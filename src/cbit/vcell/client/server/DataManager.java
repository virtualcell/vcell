package cbit.vcell.client.server;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.client.data.OutputContext;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.simdata.DataIdentifier;

public interface DataManager {

/**
 * retrieves a list of data names (state variables and functions) defined for this Simulation.
 * 
 * @param simulationInfo simulation database reference
 * 
 * @returns array of availlable data names.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
DataIdentifier[] getDataIdentifiers() throws DataAccessException;

/**
 * gets list of named Functions defined for the resultSet for this Simulation.
 * 
 * @returns array of functions, or null if no functions.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 * 
 * @see Function
 */
AnnotatedFunction[] getFunctions() throws DataAccessException;


/**
 * gets all times at which simulation result data is availlable for this Simulation.
 * 
 * @returns double array of times of availlable data, or null if no data.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
double[] getDataSetTimes() throws DataAccessException;


/**
 * Gets the simulationInfo property (cbit.vcell.solver.SimulationInfo) value.
 * @return The simulationInfo property value.
 */
VCDataIdentifier getVCDataIdentifier();

public void setOutputContext(OutputContext outputContext);

public OutputContext getOutputContext();

}