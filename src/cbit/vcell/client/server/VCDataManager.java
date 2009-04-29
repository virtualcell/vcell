package cbit.vcell.client.server;
import cbit.vcell.solver.*;
import cbit.vcell.simdata.gui.*;
import cbit.plot.*;
import cbit.vcell.solvers.*;
import cbit.vcell.math.*;
import java.rmi.*;
import cbit.vcell.desktop.controls.*;
import cbit.vcell.field.FieldDataFileOperationResults;
import cbit.vcell.field.FieldDataFileOperationSpec;
import cbit.vcell.server.*;
/**
 * Insert the type's description here.
 * Creation date: (6/11/2004 5:36:06 AM)
 * @author: Ion Moraru
 */
public class VCDataManager {
	private DataSetControllerProvider dataSetControllerProvider;

/**
 * Insert the method's description here.
 * Creation date: (6/11/2004 5:57:21 AM)
 * @param clientServerManager cbit.vcell.client.server.ClientServerManager
 */
public VCDataManager(DataSetControllerProvider dataSetControllerProvider) {
	this.dataSetControllerProvider = dataSetControllerProvider;
}


/**
 * adds an array of named <code>Function</code>s to the list of variables that are availlable for this Simulation.
 *
 * @param simulationInfo simulation database reference
 * @param functions represent named expressions that are to be bound to dataset and whose names are added to variable list.
 *
 * @throws cbit.vcell.server.DataAccessException if Functions cannot be bound to this dataset or SimulationInfo not found.
 */
public void addFunctions(cbit.vcell.server.VCDataIdentifier vcdID, cbit.vcell.math.AnnotatedFunction[] functions,boolean[] bReplaceArr) throws DataAccessException {
	try {
		getDataSetController().addFunctions(vcdID,functions,bReplaceArr);
	}catch (RemoteException e){
		handleRemoteException(e);
		try {
			getDataSetController().addFunctions(vcdID,functions,bReplaceArr);
		}catch (RemoteException e2){
			handleRemoteException(e2);
			throw new RuntimeException(e2.getMessage());
		}
	}
}

public FieldDataFileOperationResults fieldDataFileOperation(FieldDataFileOperationSpec fieldDataFielOperationSpec) throws DataAccessException {
	try {
		return getDataSetController().fieldDataFileOperation(fieldDataFielOperationSpec);
	}catch (RemoteException e){
		handleRemoteException(e);
		try {
			return getDataSetController().fieldDataFileOperation(fieldDataFielOperationSpec);
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
private DataSetControllerProvider getDataSetControllerProvider() {
	return dataSetControllerProvider;
}


/**
 * retrieves a list of data names (state variables and functions) defined for this Simulation.
 *
 * @param simulationInfo simulation database reference
 *
 * @returns array of availlable data names.
 *
 * @throws cbit.vcell.server.DataAccessException if SimulationInfo not found.
 */
public cbit.vcell.simdata.DataIdentifier[] getDataIdentifiers(cbit.vcell.server.VCDataIdentifier vcdID) throws DataAccessException {
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
 * @exception cbit.vcell.server.DataAccessException The exception description.
 */
private DataSetController getDataSetController() throws DataAccessException {
	// convenience method
	return getDataSetControllerProvider().getDataSetController();
}


/**
 * gets all times at which simulation result data is availlable for this Simulation.
 *
 * @param simulationInfo simulation database reference
 *
 * @returns double array of times of availlable data, or null if no data.
 *
 * @throws cbit.vcell.server.DataAccessException if SimulationInfo not found.
 */
public double[] getDataSetTimes(cbit.vcell.server.VCDataIdentifier vcdID) throws DataAccessException {
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
 * @throws cbit.vcell.server.DataAccessException if SimulationInfo not found.
 *
 * @see Function
 */
public cbit.vcell.math.AnnotatedFunction[] getFunctions(cbit.vcell.server.VCDataIdentifier vcdID) throws DataAccessException {
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
 * @throws cbit.vcell.server.DataAccessException if SimulationInfo not found.
 *
 * @see Function
 */
public boolean getIsODEData(cbit.vcell.server.VCDataIdentifier vcdID) throws DataAccessException {
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
 * @throws cbit.vcell.server.DataAccessException if SimulationInfo not found.
 *
 * @see PlotData
 */
public PlotData getLineScan(cbit.vcell.server.VCDataIdentifier vcdID, String variable, double time, CoordinateIndex begin, CoordinateIndex end) throws DataAccessException {
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
 * @throws cbit.vcell.server.DataAccessException if SimulationInfo not found.
 *
 * @see PlotData
 */
public PlotData getLineScan(cbit.vcell.server.VCDataIdentifier vcdID, String variable, double time, SpatialSelection spatialSelection) throws DataAccessException {
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
 * @throws cbit.vcell.server.DataAccessException if SimulationInfo not found.
 *
 * @see CartesianMesh
 */
public CartesianMesh getMesh(cbit.vcell.server.VCDataIdentifier vcdID) throws DataAccessException {
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
 * @throws cbit.vcell.server.DataAccessException if SimulationInfo not found.
 */
public cbit.vcell.solver.ode.ODESimData getODEData(cbit.vcell.server.VCDataIdentifier vcdID) throws DataAccessException {
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
 * @throws cbit.vcell.server.DataAccessException if SimulationInfo not found, or if no particle data.
 *
 * @see ParticleDataBlock
 */
public cbit.vcell.simdata.ParticleDataBlock getParticleDataBlock(cbit.vcell.server.VCDataIdentifier vcdID, double time) throws DataAccessException {
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

public DataProcessingOutput getDataProcessingOutput(cbit.vcell.server.VCDataIdentifier vcdID) throws DataAccessException {
	try {
		return getDataSetController().getDataProcessingOutput(vcdID);
	}catch (RemoteException e){
		handleRemoteException(e);
		try {
			return getDataSetController().getDataProcessingOutput(vcdID);
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
 * @throws cbit.vcell.server.DataAccessException if SimulationInfo not found.
 */
public boolean getParticleDataExists(cbit.vcell.server.VCDataIdentifier vcdID) throws DataAccessException {
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
 * @throws cbit.vcell.server.DataAccessException if SimulationInfo not found.
 */
public cbit.vcell.simdata.SimDataBlock getSimDataBlock(cbit.vcell.server.VCDataIdentifier vcdID, String varName, double time) throws DataAccessException {
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
 * @throws cbit.vcell.server.DataAccessException if SimulationInfo not found.
 *
 * @see CartesianMesh for transformation between indices and coordinates.
 */
public org.vcell.util.TimeSeriesJobResults getTimeSeriesValues(cbit.vcell.server.VCDataIdentifier vcdID,org.vcell.util.TimeSeriesJobSpec timeSeriesJobSpec) throws DataAccessException {
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
 * @throws cbit.vcell.server.DataAccessException if SimulationInfo not found.
 * @throws cbit.vcell.server.PermissionException if not the owner of this dataset.
 */
public void removeFunction(AnnotatedFunction function, VCDataIdentifier vcDataIdentifier) throws cbit.vcell.server.DataAccessException {
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