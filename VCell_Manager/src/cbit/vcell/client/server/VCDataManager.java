package cbit.vcell.client.server;
import java.rmi.RemoteException;

import cbit.plot.PlotData;
import cbit.util.CoordinateIndex;
import cbit.util.DataAccessException;
import cbit.util.VCDataIdentifier;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.math.Function;
import cbit.vcell.mesh.CartesianMesh;
import cbit.vcell.server.DataSetController;
import cbit.vcell.simdata.ParticleDataBlock;
import cbit.vcell.simdata.SpatialSelection;
/**
 * Insert the type's description here.
 * Creation date: (6/11/2004 5:36:06 AM)
 * @author: Ion Moraru
 */
public class VCDataManager {
	private ClientServerManager clientServerManager = null;

/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 5:57:21 AM)
 * @param clientServerManager cbit.vcell.client.server.ClientServerManager
 */
public VCDataManager(ClientServerManager clientServerManager) {
	this.clientServerManager = clientServerManager;
}


/**
 * adds a named <code>Function</code> to the list of variables that are availlable for this Simulation.
 *
 * @param simulationInfo simulation database reference
 * @param function named expression that is to be bound to dataset and whose name is added to variable list.
 *
 * @throws cbit.util.DataAccessException if Function cannot be bound to this dataset or SimulationInfo not found.
 */
void addFunction(VCDataIdentifier vcdID, cbit.vcell.math.AnnotatedFunction function) throws DataAccessException {
	try {
		getDataSetController().addFunction(vcdID,function);
	}catch (RemoteException e){
		handleRemoteException(e);
		try {
			getDataSetController().addFunction(vcdID,function);
		}catch (RemoteException e2){
			handleRemoteException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * adds an array of named <code>Function</code>s to the list of variables that are availlable for this Simulation.
 *
 * @param simulationInfo simulation database reference
 * @param functions represent named expressions that are to be bound to dataset and whose names are added to variable list.
 *
 * @throws cbit.util.DataAccessException if Functions cannot be bound to this dataset or SimulationInfo not found.
 */
void addFunctions(VCDataIdentifier vcdID, cbit.vcell.math.AnnotatedFunction[] functions) throws DataAccessException {
	try {
		getDataSetController().addFunctions(vcdID,functions);
	}catch (RemoteException e){
		handleRemoteException(e);
		try {
			getDataSetController().addFunctions(vcdID,functions);
		}catch (RemoteException e2){
			handleRemoteException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 5:56:57 AM)
 * @return cbit.vcell.client.server.ClientServerManager
 */
private ClientServerManager getClientServerManager() {
	return clientServerManager;
}


/**
 * retrieves a list of data names (state variables and functions) defined for this Simulation.
 *
 * @param simulationInfo simulation database reference
 *
 * @returns array of availlable data names.
 *
 * @throws cbit.util.DataAccessException if SimulationInfo not found.
 */
cbit.vcell.math.DataIdentifier[] getDataIdentifiers(VCDataIdentifier vcdID) throws DataAccessException {
	try {
		return getDataSetController().getDataIdentifiers(vcdID);
	}catch (RemoteException e){
		handleRemoteException(e);
		try {
			return getDataSetController().getDataIdentifiers(vcdID);
		}catch (RemoteException e2){
			handleRemoteException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 5:58:43 AM)
 * @return cbit.vcell.server.DataSetController
 * @exception cbit.util.DataAccessException The exception description.
 */
private DataSetController getDataSetController() throws DataAccessException {
	// convenience method
	return getClientServerManager().getDataSetController();
}


/**
 * gets all times at which simulation result data is availlable for this Simulation.
 *
 * @param simulationInfo simulation database reference
 *
 * @returns double array of times of availlable data, or null if no data.
 *
 * @throws cbit.util.DataAccessException if SimulationInfo not found.
 */
double[] getDataSetTimes(VCDataIdentifier vcdID) throws DataAccessException {
	try {
		return getDataSetController().getDataSetTimes(vcdID);
	}catch (RemoteException e){
		handleRemoteException(e);
		try {
			return getDataSetController().getDataSetTimes(vcdID);
		}catch (RemoteException e2){
			handleRemoteException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * gets list of named Functions defined for the simulation.
 *
 * @param simulationInfo simulation set database reference
 *
 * @returns array of functions, or null if no functions.
 *
 * @throws cbit.util.DataAccessException if SimulationInfo not found.
 *
 * @see Function
 */
cbit.vcell.math.AnnotatedFunction[] getFunctions(VCDataIdentifier vcdID) throws DataAccessException {
	try {
		return getDataSetController().getFunctions(vcdID);
	}catch (RemoteException e){
		handleRemoteException(e);
		try {
			return getDataSetController().getFunctions(vcdID);
		}catch (RemoteException e2){
			handleRemoteException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * tests if resultSet contains ODE data for the specified simulation.
 *
 * @param simulationInfo simulation database reference
 *
 * @returns <i>true</i> if results are of type ODE, <i>false</i> otherwise.
 *
 * @throws cbit.util.DataAccessException if SimulationInfo not found.
 *
 * @see Function
 */
boolean getIsODEData(VCDataIdentifier vcdID) throws DataAccessException {
	try {
		return getDataSetController().getIsODEData(vcdID);
	}catch (RemoteException e){
		handleRemoteException(e);
		try {
			return getDataSetController().getIsODEData(vcdID);
		}catch (RemoteException e2){
			handleRemoteException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * retrieves a line scan (data sampled along a line in space) for the specified simulation.
 *
 * @param simulationInfo simulation database reference
 * @param variable name of variable to be sampled
 * @param time simulation time which is to be sampled.
 * @param begin i,j,k of start of line.
 * @param end i,j,k coordinate of end of line.
 *
 * @returns annotated array of 'concentration vs. distance' in a plot ready format.
 *
 * @throws cbit.util.DataAccessException if SimulationInfo not found.
 *
 * @see PlotData
 */
PlotData getLineScan(VCDataIdentifier vcdID, String variable, double time, CoordinateIndex begin, CoordinateIndex end) throws DataAccessException {
	try {
		return getDataSetController().getLineScan(vcdID,variable,time,begin,end);
	}catch (RemoteException e){
		handleRemoteException(e);
		try {
			return getDataSetController().getLineScan(vcdID,variable,time,begin,end);
		}catch (RemoteException e2){
			handleRemoteException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * retrieves a line scan (data sampled along a curve in space) for the specified simulation.
 *
 * @param simulationInfo simulation database reference
 * @param variable name of variable to be sampled
 * @param time simulation time which is to be sampled.
 * @param spatialSelection spatial curve used for sampling.
 *
 * @returns annotated array of 'concentration vs. distance' in a plot ready format.
 *
 * @throws cbit.util.DataAccessException if SimulationInfo not found.
 *
 * @see PlotData
 */
PlotData getLineScan(VCDataIdentifier vcdID, String variable, double time, SpatialSelection spatialSelection) throws DataAccessException {
	try {
		return getDataSetController().getLineScan(vcdID,variable,time,spatialSelection);
	}catch (RemoteException e){
		handleRemoteException(e);
		try {
			return getDataSetController().getLineScan(vcdID,variable,time,spatialSelection);
		}catch (RemoteException e2){
			handleRemoteException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * retrieves the Mesh object for this Simulation.
 *
 * @param simulationInfo simulation database reference
 *
 * @returns mesh associated with this data (allows spatial interpretation of indexed data).
 *
 * @throws cbit.util.DataAccessException if SimulationInfo not found.
 *
 * @see CartesianMesh
 */
CartesianMesh getMesh(VCDataIdentifier vcdID) throws DataAccessException {
	try {
		return getDataSetController().getMesh(vcdID);
	}catch (RemoteException e){
		handleRemoteException(e);
		try {
			return getDataSetController().getMesh(vcdID);
		}catch (RemoteException e2){
			handleRemoteException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * retrieves the non-spatial (ODE) results for this Simulation.
 *
 * @param simulationInfo simulation database reference
 *
 * @returns non-spatial (ODE) data.
 *
 * @throws cbit.util.DataAccessException if SimulationInfo not found.
 */
cbit.vcell.simdata.ODESimData getODEData(VCDataIdentifier vcdID) throws DataAccessException {
	try {
		return getDataSetController().getODEData(vcdID);
	}catch (RemoteException e){
		handleRemoteException(e);
		try {
			return getDataSetController().getODEData(vcdID);
		}catch (RemoteException e2){
			handleRemoteException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * retrieves the particle data for this Simulation.
 *
 * @param simulationInfo simulation database reference
 *
 * @returns particle data for this result set.
 *
 * @throws cbit.util.DataAccessException if SimulationInfo not found, or if no particle data.
 *
 * @see ParticleDataBlock
 */
cbit.vcell.simdata.ParticleDataBlock getParticleDataBlock(VCDataIdentifier vcdID, double time) throws DataAccessException {
	try {
		return getDataSetController().getParticleDataBlock(vcdID,time);
	}catch (RemoteException e){
		handleRemoteException(e);
		try {
			return getDataSetController().getParticleDataBlock(vcdID,time);
		}catch (RemoteException e2){
			handleRemoteException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * determines if the result set for this Simulation contains particle data.
 *
 * @param simulationInfo simulation database reference
 *
 * @returns <i>true</i> if there is particle data availlable.
 *
 * @throws cbit.util.DataAccessException if SimulationInfo not found.
 */
boolean getParticleDataExists(VCDataIdentifier vcdID) throws DataAccessException {
	try {
		return getDataSetController().getParticleDataExists(vcdID);
	}catch (RemoteException e){
		handleRemoteException(e);
		try {
			return getDataSetController().getParticleDataExists(vcdID);
		}catch (RemoteException e2){
			handleRemoteException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * retrieves the spatial (PDE) data for this Simulation, Variable, and Time.
 *
 * @param simulationInfo simulation database reference.
 * @param varName name of dataSet (state variable or function).
 * @param time simulation time of data.
 *
 * @returns spatial (PDE) data for this result set associated with the specified variable name and time, 
 *          or <i>null</i> if no data availlable.
 *
 * @throws cbit.util.DataAccessException if SimulationInfo not found.
 */
cbit.vcell.simdata.SimDataBlock getSimDataBlock(VCDataIdentifier vcdID, String varName, double time) throws DataAccessException {
	try {
		return getDataSetController().getSimDataBlock(vcdID,varName,time);
	}catch (RemoteException e){
		handleRemoteException(e);
		try {
			return getDataSetController().getSimDataBlock(vcdID,varName,time);
		}catch (RemoteException e2){
			handleRemoteException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * retrieves a time series (single point as a function of time) of a specified spatial data set.
 *
 * @param simulationInfo simulation database reference
 * @param variable name of variable to be sampled
 * @param index identifies index into data array.
 *
 * @returns annotated array of 'concentration vs. time' in a plot ready format.
 *
 * @throws cbit.util.DataAccessException if SimulationInfo not found.
 *
 * @see CartesianMesh for transformation between indices and coordinates.
 */
cbit.util.TimeSeriesJobResults getTimeSeriesValues(VCDataIdentifier vcdID,cbit.util.TimeSeriesJobSpec timeSeriesJobSpec) throws DataAccessException {
	try {
		return getDataSetController().getTimeSeriesValues(vcdID,timeSeriesJobSpec);
	}catch (RemoteException e){
		handleRemoteException(e);
		try {
			return getDataSetController().getTimeSeriesValues(vcdID,timeSeriesJobSpec);
		}catch (RemoteException e2){
			handleRemoteException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/4/01 1:38:14 PM)
 * @param remoteException java.rmi.RemoteException
 */
private void handleRemoteException(RemoteException remoteException) {
	System.out.println("\n\n.... Handling RemoteException ...\n");
	remoteException.printStackTrace(System.out);
	System.out.println("\n\n");
}


/**
 * removes the specified <i>function</i> from this Simulation.
 * 
 * @param function function to be removed.
 * 
 * @throws cbit.util.DataAccessException if SimulationInfo not found.
 * @throws cbit.util.PermissionException if not the owner of this dataset.
 */
void removeFunction(AnnotatedFunction function, VCDataIdentifier vcDataIdentifier) throws cbit.util.DataAccessException {
		try {
		getDataSetController().removeFunction(vcDataIdentifier,function);
	}catch (RemoteException e){
		handleRemoteException(e);
		try {
			getDataSetController().removeFunction(vcDataIdentifier,function);
		}catch (RemoteException e2){
			handleRemoteException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}
}