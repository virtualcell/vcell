package cbit.vcell.client.server;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.math.Function;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.solver.ode.ODESolverResultSet;
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
 * @throws DataAccessException 
 */
public ODEDataManager(VCDataManager vcDataManager, VCDataIdentifier vcDataIdentifier) throws DataAccessException {
	setVcDataManager(vcDataManager);
	setVcDataIdentifier(vcDataIdentifier);
	connect();
}


/**
 * adds an array of named <code>Function</code>s to the list of variables that are availlable for this Simulation.
 * 
 * @param functions represent named expressions that are to be bound to dataset and whose names are added to variable list.
 * 
 * @throws org.vcell.util.DataAccessException if Functions cannot be bound to this dataset or SimulationInfo not found.
 */
public void addFunctions(AnnotatedFunction[] functions,boolean[] bReplaceArr) throws DataAccessException {
	getVCDataManager().addFunctions(getVCDataIdentifier(), functions,bReplaceArr);
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
	return getVCDataManager().getDataIdentifiers(getVCDataIdentifier());
}


/**
 * gets all times at which simulation result data is availlable for this Simulation.
 * 
 * @returns double array of times of availlable data, or null if no data.
 * 
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
public double[] getDataSetTimes() throws DataAccessException {
	return getVCDataManager().getDataSetTimes(getVCDataIdentifier());
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
	return getVCDataManager().getFunctions(getVCDataIdentifier());
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
 * Gets the simulationInfo property (cbit.vcell.solver.SimulationInfo) value.
 * @return The simulationInfo property value.
 */
public VCDataIdentifier getVCDataIdentifier() {
	return vcDataIdentifier;
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 3:53:33 PM)
 * @return cbit.vcell.client.server.VCDataManager
 */
public VCDataManager getVCDataManager() {
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
	getVCDataManager().removeFunction(function, getVCDataIdentifier());
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

private void connect() throws DataAccessException {
	odeSolverResultSet = getVCDataManager().getODEData(getVCDataIdentifier());
}

}