package cbit.vcell.client.server;

import java.util.Vector;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.DataProcessingOutput;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.AnnotatedFunction.FunctionCategory;
/**
 * Insert the type's description here.
 * Creation date: (11/4/2003 4:48:32 PM)
 * @author: Anuradha Lakshminarayana
 */
public class DataManagerTest extends PDEDataManager {
	private cbit.vcell.math.AnnotatedFunction functions[] = new cbit.vcell.math.AnnotatedFunction[0];
	private double times[] = null;
	private cbit.vcell.solvers.CartesianMesh mesh = null;
	private org.vcell.util.document.VCDataIdentifier dataID = null;
/**
 * DataManagerTest constructor comment.
 * @param argVcdID cbit.vcell.server.VCDataIdentifier
 * @param argSimulationCollectionManager cbit.vcell.desktop.controls.ClientSimulationCollectionManager
 */
public DataManagerTest(cbit.vcell.math.AnnotatedFunction argFunctions[], double argTimes[], cbit.vcell.solvers.CartesianMesh argMesh, org.vcell.util.document.VCDataIdentifier argVCDataIdentifier) {
	super(null,null,null);
	this.functions = argFunctions;
	this.times = argTimes;
	this.mesh = argMesh;
	this.dataID = argVCDataIdentifier;
}
/**
 * adds a named <code>Function</code> to the list of variables that are availlable for this Simulation.
 *
 * @param function named expression that is to be bound to dataset and whose name is added to variable list.
 *
 * @throws org.vcell.util.DataAccessException if Function cannot be bound to this dataset or SimulationInfo not found.
 */
public void addFunction(cbit.vcell.math.AnnotatedFunction argFunction) throws org.vcell.util.DataAccessException {
	functions = (cbit.vcell.math.AnnotatedFunction[])org.vcell.util.BeanUtils.addElement(functions,argFunction);
}
/**
 * adds an array of named <code>Function</code>s to the list of variables that are availlable for this Simulation.
 *
 * @param functions represent named expressions that are to be bound to dataset and whose names are added to variable list.
 *
 * @throws org.vcell.util.DataAccessException if Functions cannot be bound to this dataset or SimulationInfo not found.
 */
public void addFunctions(cbit.vcell.math.AnnotatedFunction[] argFunctions) throws org.vcell.util.DataAccessException {
	functions = (cbit.vcell.math.AnnotatedFunction[])org.vcell.util.BeanUtils.addElements(functions,argFunctions);
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
public cbit.vcell.simdata.DataIdentifier[] getDataIdentifiers() throws org.vcell.util.DataAccessException {
	cbit.vcell.simdata.DataIdentifier[] dataIdentifiers = new cbit.vcell.simdata.DataIdentifier[functions.length];
	for (int i = 0; i < dataIdentifiers.length; i++){
		dataIdentifiers[i] = new cbit.vcell.simdata.DataIdentifier(functions[i].getName(),cbit.vcell.simdata.VariableType.VOLUME, true, functions[i].getName());
	}
	return dataIdentifiers;
}
/**
 * gets all times at which simulation result data is availlable for this Simulation.
 *
 * @returns double array of times of availlable data, or null if no data.
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
public double[] getDataSetTimes() throws org.vcell.util.DataAccessException {
	return times;
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
public cbit.vcell.math.AnnotatedFunction[] getFunctions() {
	return functions;
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
	throw new RuntimeException("not yet implemented");
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
public cbit.plot.PlotData getLineScan(String variable, double time, cbit.vcell.simdata.gui.SpatialSelection spatialSelection) throws org.vcell.util.DataAccessException {
	throw new RuntimeException("not yet implemented");
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
public cbit.vcell.solvers.CartesianMesh getMesh() throws org.vcell.util.DataAccessException {
	return mesh;
}
/**
 * retrieves the non-spatial (ODE) results for this Simulation.  This is assumed not to change over the life
 * of the simulation
 *
 * @returns non-spatial (ODE) data.
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
public cbit.vcell.solver.ode.ODESolverResultSet getODESolverResultSet() throws org.vcell.util.DataAccessException {
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
	return null;
}

public DataProcessingOutput getDataProcessingOutput() throws org.vcell.util.DataAccessException {
	return null;
}



/**
 * determines if the result set for this Simulation contains particle data.
 *
 * @returns <i>true</i> if there is particle data availlable.
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
public boolean getParticleDataExists() throws org.vcell.util.DataAccessException {
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (11/4/2003 5:27:47 PM)
 * @return cbit.vcell.desktop.controls.DataManagerTest
 */
public static DataManagerTest getPDEExample1() {
	try {
		Vector<AnnotatedFunction> functionList = new Vector<AnnotatedFunction>();
		functionList.add(new cbit.vcell.math.AnnotatedFunction("F1",new Expression("sin(x+2*y)"),"",cbit.vcell.simdata.VariableType.VOLUME, FunctionCategory.PREDEFINED));
		// functionList.add(new cbit.vcell.math.Function("F2",new Expression("cos(x*y*z)")));
		// functionList.add(new cbit.vcell.math.Function("F3",new Expression("t*sin(x*y*z)")));
		cbit.vcell.math.AnnotatedFunction functions[] = (cbit.vcell.math.AnnotatedFunction[])org.vcell.util.BeanUtils.getArray(functionList,cbit.vcell.math.Function.class);

		final double END_TIME = 1.0;
		final int NUM_TIMES = 101;
		double times[] = new double[NUM_TIMES];
		for (int i = 0; i < NUM_TIMES; i++){
			times[i] = END_TIME*(((double)i)/(NUM_TIMES-1));
		}

		// read the mesh file into this string .... DO A FILE READ
		java.io.File meshFile = new java.io.File("\\\\fs2\\RAID\\vcell\\users\\vcelltestaccount\\size10241024.mesh");
		cbit.vcell.solvers.CartesianMesh mesh = CartesianMesh.readFromFiles(meshFile, null);
		org.vcell.util.document.VCDataIdentifier vcDataIdentifier = new org.vcell.util.document.VCDataIdentifier() {
										public org.vcell.util.document.User getOwner() {
											return new org.vcell.util.document.User("anu",new org.vcell.util.document.KeyValue("123"));
										}
										public String getID() {
											return "PDEExample1";
										}
		};
		return new DataManagerTest(functions,times,mesh,vcDataIdentifier);
	}catch (Throwable e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/4/2003 5:27:47 PM)
 * @return cbit.vcell.desktop.controls.DataManagerTest
 */
public static DataManagerTest getPDEExample2() {
	try {
		Vector<AnnotatedFunction> functionList = new Vector<AnnotatedFunction>();
		functionList.add(new cbit.vcell.math.AnnotatedFunction("F1",new Expression("sin(x+2*y)"),"",cbit.vcell.simdata.VariableType.VOLUME, FunctionCategory.PREDEFINED));
		// functionList.add(new cbit.vcell.math.Function("F2",new Expression("cos(x*y*z)")));
		// functionList.add(new cbit.vcell.math.Function("F3",new Expression("t*sin(x*y*z)")));
		cbit.vcell.math.AnnotatedFunction functions[] = (cbit.vcell.math.AnnotatedFunction[])org.vcell.util.BeanUtils.getArray(functionList,cbit.vcell.math.Function.class);

		final double END_TIME = 1.0;
		final int NUM_TIMES = 101;
		double times[] = new double[NUM_TIMES];
		for (int i = 0; i < NUM_TIMES; i++){
			times[i] = END_TIME*(((double)i)/(NUM_TIMES-1));
		}

		// read the mesh file into this string .... DO A FILE READ
		java.io.File meshFile = new java.io.File("\\\\fs2\\RAID\\vcell\\users\\vcelltestaccount\\size44.mesh");
		cbit.vcell.solvers.CartesianMesh mesh = CartesianMesh.readFromFiles(meshFile, null);
		org.vcell.util.document.VCDataIdentifier vcDataIdentifier = new org.vcell.util.document.VCDataIdentifier() {
										public org.vcell.util.document.User getOwner() {
											return new org.vcell.util.document.User("anu",new org.vcell.util.document.KeyValue("123"));
										}
										public String getID() {
											return "PDEExample2";
										}
		};
		return new DataManagerTest(functions,times,mesh,vcDataIdentifier);
	}catch (Throwable e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
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
	//
	// get correct function
	//
	cbit.vcell.math.Function function = null;
	for (int i = 0; i < functions.length; i++){
		if (functions[i].getName().equals(varName)){
			function = functions[i];
		}
	}
	if (function==null){
		throw new RuntimeException("function '"+varName+"' not found");
	}
	
	cbit.vcell.simdata.PDEDataInfo pdeDataInfo = new cbit.vcell.simdata.PDEDataInfo(dataID.getOwner(), dataID.getID(), varName, time, 0);
	double data[] = new double[mesh.getSizeX()*mesh.getSizeY()*mesh.getSizeZ()];
	String symbols[] = new String[4];
	symbols[ReservedVariable.TIME.getIndex()] = ReservedVariable.TIME.getName();
	symbols[ReservedVariable.X.getIndex()] = ReservedVariable.X.getName();
	symbols[ReservedVariable.Y.getIndex()] = ReservedVariable.Y.getName();
	symbols[ReservedVariable.Z.getIndex()] = ReservedVariable.Z.getName();
	cbit.vcell.parser.SimpleSymbolTable simpleSymbolTable = new cbit.vcell.parser.SimpleSymbolTable(symbols);

	try {
		function.bind(simpleSymbolTable);

		double values[] = new double[4];
		values[cbit.vcell.math.ReservedVariable.TIME.getIndex()] = time;

		for (int i = 0; i < data.length; i++){
			org.vcell.util.Coordinate coord = mesh.getCoordinateFromVolumeIndex(i);
			values[ReservedVariable.X.getIndex()] = coord.getX();
			values[ReservedVariable.Y.getIndex()] = coord.getY();
			values[ReservedVariable.Z.getIndex()] = coord.getZ();
			data[i] = function.getExpression().evaluateVector(values);
		}
	}catch (cbit.vcell.parser.ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}

	
	cbit.vcell.simdata.SimDataBlock simDataBlock = new cbit.vcell.simdata.SimDataBlock(pdeDataInfo, data, cbit.vcell.simdata.VariableType.VOLUME);
	
	return simDataBlock;
}
/**
 * retrieves the resultset info for this simulation
 *
 * @returns resultSetInfo (metadata about the simulation results)
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 */
public cbit.vcell.solver.SolverResultSetInfo getSolverResultSetInfo() throws org.vcell.util.DataAccessException {
	throw new RuntimeException("not appropriate ...");
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
public double[][] getTimeSeriesValues(String varName, int[] indices) throws org.vcell.util.DataAccessException {
	throw new RuntimeException("not yet implemented");
}
/**
 * Gets the simulationInfo property (cbit.vcell.solver.SimulationInfo) value.
 * @return The simulationInfo property value.
 */
public org.vcell.util.document.VCDataIdentifier getVCDataIdentifier() {
	return dataID;
}
/**
 * removes the specified <i>function</i> from this Simulation.
 *
 * @param function function to be removed.
 *
 * @throws org.vcell.util.DataAccessException if SimulationInfo not found.
 * @throws org.vcell.util.PermissionException if not the owner of this dataset.
 */
public void removeFunction(cbit.vcell.math.AnnotatedFunction argFunction) throws org.vcell.util.DataAccessException, org.vcell.util.PermissionException {
	functions = (cbit.vcell.math.AnnotatedFunction[])org.vcell.util.BeanUtils.removeElement(functions,argFunction);
}
}
