/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.ode;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import org.vcell.util.SessionLog;

import cbit.vcell.mapping.FastSystemAnalyzer;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Equation;
import cbit.vcell.math.Function;
import cbit.vcell.math.FunctionColumnDescription;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.math.OdeEquation;
import cbit.vcell.math.PseudoConstant;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolVariable;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UserStopException;
/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:56:23 PM)
 * @author: John Wagner
 */
public abstract class DefaultODESolver extends AbstractJavaSolver implements ODESolver {
	//  Tools of the computation
	protected ValueVectors fieldValueVectors = null;
	private int fieldValueVectorCount = 0;
	//
	private ODESolverResultSet fieldODESolverResultSet = null;
	private Vector<Variable> fieldIdentifiers = null;
	private Vector<SensVariable> fieldSensVariables = null;
	private Vector<StateVariable> fieldStateVariables = null;
	private FastAlgebraicSystem fieldFastAlgebraicSystem = null;
	private int fieldVariableIndexes[] = null;
	// current state of the computation
	protected double fieldCurrentTime = 0.0;
	private transient RateSensitivity rateSensitivity = null;
	private transient Jacobian jacobian = null;

/**
 * Set sensitivityParameter to null if sensitivity analysis
 * is not to be performed...
 * @param simContext cbit.vcell.math.MathDescription
 */
public DefaultODESolver(SimulationTask simTask, File directory, SessionLog sessionLog, int valueVectorCount)  throws SolverException {
	super(simTask, directory, sessionLog);
	if (simTask.getSimulation().isSpatial()) {
		throw new SolverException("Cannot use DefaultODESolver on spatial simulation");
	}
	fieldValueVectorCount = valueVectorCount;
}


/**
 * This method was created by a SmartGuide.
 */
protected void check(double values[]) throws SolverException {
	//
	// quickly determine if any values are bad
	//
	boolean bAnyBad = false;
	for (int i = 0; i < values.length; i++) {
		if (Double.isNaN(values[i]) || Double.isInfinite(values[i])) {
			bAnyBad = true;
			break;
		}
	}
	if (!bAnyBad){
		return;
	}
	//
	// if some values are bad, summarize for user
	//
	StringBuffer buffer = new StringBuffer();
	for (int i = 0; i < values.length; i++) {
		if (Double.isNaN(values[i]) || Double.isInfinite(values[i])) {
			buffer.append(((Variable)fieldIdentifiers.elementAt(i)).getName()+" = "+values[i]+"\n");
		}
	}
	throw new SolverException("simulation failed at time="+getCurrentTime()+": consider using smaller default or max time step.\nvalues are:\n"+buffer);
}


/**
 * This method was created in VisualAge.
 */
private Vector<Variable> createIdentifiers() throws MathException, ExpressionException {
	SimulationSymbolTable simSymbolTable = simTask.getSimulationJob().getSimulationSymbolTable();
	
	// create list of possible identifiers (including reserved x,y,z,t)
	Vector<Variable> identifiers = new Vector<Variable>();
	// add reserved variables x,y,z,t
	identifiers.addElement(ReservedVariable.TIME);
	identifiers.addElement(ReservedVariable.X);
	identifiers.addElement(ReservedVariable.Y);
	identifiers.addElement(ReservedVariable.Z);
	// add regular variables
	Variable variables[] = simSymbolTable.getVariables();
	for (int i = 0; i < variables.length; i++){	
		if (variables[i] instanceof VolVariable) {
			identifiers.addElement(variables[i]);
		}
	}
	//  Add sensitivity variables (for sensitivity equations)...
	fieldSensVariables = new Vector<SensVariable>();
	if (getSensitivityParameter() != null) {
		for (int i = 0; i < variables.length; i++){
			if (variables[i] instanceof VolVariable){
				VolVariable volVariable = (VolVariable)variables[i];
				SensVariable sv = new SensVariable(volVariable, getSensitivityParameter());
				identifiers.addElement(sv);
			}
		}
	}
	//  Add pseudoConstants for fast system (if necessary)...
	if (getFastAlgebraicSystem() != null) {
		Enumeration<PseudoConstant> enum1 = fieldFastAlgebraicSystem.getPseudoConstants();
		while (enum1.hasMoreElements()) {
			identifiers.addElement(enum1.nextElement());
		}
	}
	//  Assign indices...
	for (int i = 0; i < identifiers.size(); i++) {
		Variable variable = (Variable) identifiers.elementAt(i);
		variable.setIndex(i);
	}
	return (identifiers);
}


/**
 */
private ODESolverResultSet createODESolverResultSet() throws ExpressionException {
	SimulationSymbolTable simSymbolTable = simTask.getSimulationJob().getSimulationSymbolTable();
	
	//
	// create symbol table for binding expression
	//
	String symbols[] = new String[fieldIdentifiers.size()];
	for (int i = 0; i < symbols.length; i++) {
		symbols[i] = ((Variable)fieldIdentifiers.elementAt(i)).getName();
	}
	
	//  Initialize the ResultSet...
	ODESolverResultSet odeSolverResultSet = new ODESolverResultSet();
	odeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(ReservedVariable.TIME.getName()));
	for (int i = 0; i < getStateVariableCount(); i++) {
		StateVariable stateVariable = getStateVariable(i);
		if (stateVariable instanceof SensStateVariable) {
			SensStateVariable sensStateVariable = (SensStateVariable) stateVariable;
			odeSolverResultSet.addDataColumn(
				new ODESolverResultSetColumnDescription(sensStateVariable.getVariable().getName(),
					sensStateVariable.getParameter().getName(), sensStateVariable.getVariable().getName()));
		} else {
			odeSolverResultSet.addDataColumn(
				new ODESolverResultSetColumnDescription(stateVariable.getVariable().getName()));
		}
	}

	Variable variables[] = simSymbolTable.getVariables();
	for (int i = 0; i < variables.length; i++){
		if (variables[i] instanceof Function && SimulationSymbolTable.isFunctionSaved((Function)variables[i])){
			Function function = (Function)variables[i];
			Expression exp1 = new Expression(function.getExpression());
			try {
				exp1 = simSymbolTable.substituteFunctions(exp1);
			} catch (MathException e) {
				e.printStackTrace(System.out);
				throw new RuntimeException("Substitute function failed on function "+function.getName()+" "+e.getMessage());
			}
			odeSolverResultSet.addFunctionColumn(new FunctionColumnDescription(exp1.flatten(),function.getName(), null, function.getName(), false));
		}
	}

	//
	// add dependent sensitivity function columns to result set
	//
	if (getSensitivityParameter() != null) {
		if (odeSolverResultSet.findColumn(getSensitivityParameter().getName()) == -1) {
			FunctionColumnDescription fcd = new FunctionColumnDescription(new Expression(getSensitivityParameter().getConstantValue()), getSensitivityParameter().getName(), null, getSensitivityParameter().getName(), false);
			odeSolverResultSet.addFunctionColumn(fcd);
		}
		StateVariable stateVars[] = (StateVariable[])org.vcell.util.BeanUtils.getArray(fieldStateVariables,StateVariable.class);
		for (int i = 0; i < variables.length; i++){
			if (variables[i] instanceof Function && SimulationSymbolTable.isFunctionSaved((Function)variables[i])){
				Function depSensFunction = (Function)variables[i];
				Expression depSensFnExpr = new Expression(depSensFunction.getExpression());
				try {
					depSensFnExpr = simSymbolTable.substituteFunctions(depSensFnExpr);
				} catch (MathException e) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Substitute function failed on function "+depSensFunction.getName()+" "+e.getMessage());
				}
				depSensFnExpr = getFunctionSensitivity(depSensFnExpr, getSensitivityParameter(), stateVars);
				// depSensFnExpr = depSensFnExpr.flatten(); 	// already bound and flattened in getFunctionSensitivity, no need here ...
				
				String depSensFnName = new String("sens_"+depSensFunction.getName()+"_wrt_"+getSensitivityParameter().getName());
				if (depSensFunction != null) {
					FunctionColumnDescription cd = new FunctionColumnDescription(depSensFnExpr.flatten(),depSensFnName,getSensitivityParameter().getName(),depSensFnName, false);
					odeSolverResultSet.addFunctionColumn(cd);
				}
			}
		}
	}
	return (odeSolverResultSet);
}


/**
 * This method was created in VisualAge.
 */
private Vector<SensVariable> createSensitivityVariables() throws MathException, ExpressionException {
	Vector<SensVariable> sensVariables = new Vector<SensVariable>();
	if (getSensitivityParameter() != null) {
		for (int i = 0; i < fieldIdentifiers.size(); i++) {
			if (fieldIdentifiers.elementAt(i) instanceof SensVariable) {
				sensVariables.addElement((SensVariable)fieldIdentifiers.elementAt(i));
			}
		}
	}
	return (sensVariables);
}


/**
 * This method was created in VisualAge.
 */
private Vector<StateVariable> createStateVariables() throws MathException, ExpressionException {
	SimulationSymbolTable simSymbolTable = simTask.getSimulationJob().getSimulationSymbolTable();
	Simulation sim = simSymbolTable.getSimulation();
	
	Vector<StateVariable> stateVariables = new Vector<StateVariable>();
	// get Ode's from MathDescription and create ODEStateVariables
	Enumeration<Equation> enum1 = getSubDomain().getEquations();
	while (enum1.hasMoreElements()) {
		Equation equation = enum1.nextElement();
		if (equation instanceof OdeEquation) {
			stateVariables.addElement(new ODEStateVariable((OdeEquation) equation, simSymbolTable));
		} else {
			throw new MathException("encountered non-ode equation, unsupported");
		}
	}
	MathDescription mathDescription = sim.getMathDescription();
	if (rateSensitivity==null){
		rateSensitivity = new RateSensitivity(mathDescription, mathDescription.getSubDomains().nextElement());
	}
	if (jacobian==null){
		jacobian = new Jacobian(mathDescription, mathDescription.getSubDomains().nextElement());
	}
	// get Jacobian and RateSensitivities from MathDescription and create SensStateVariables
	for (int v = 0; v < fieldSensVariables.size(); v++) {
		stateVariables.addElement(
			new SensStateVariable(fieldSensVariables.elementAt(v),
									rateSensitivity, 
									jacobian,
									fieldSensVariables, 
									simSymbolTable));
	}
	if (stateVariables.size() == 0) {
		throw new MathException("there are no equations defined");
	}
	return(stateVariables);
}


/**
 * This method was created by a SmartGuide.
 */
protected double[] createWorkArray() {
	double[] workArray = new double[fieldIdentifiers.size()];
	for (int i = 0; i < fieldIdentifiers.size(); i++) workArray[i] = 0.0;
	return (workArray);
}


/**
 * This method was created in VisualAge.
 */
protected final double evaluate(double[] y, int i) throws ExpressionException {
	return (getStateVariable(i).evaluateRate(y));
}


/**
 * This method was created in VisualAge.
 */
protected final double evaluate(double t, double[] y, int i) throws ExpressionException {
	y[getTimeIndex()] = t;
	return (getStateVariable(i).evaluateRate(y));
}


/**
 * Gets the currentTime property (java.lang.String) value.
 * @return The currentTime property value.
 */
public final double getCurrentTime() {
	return fieldCurrentTime;
}


/**
 * Gets the timeIndex property (int) value.
 * @return The timeIndex property value.
 */
public final FastAlgebraicSystem getFastAlgebraicSystem() {
	return fieldFastAlgebraicSystem;
}


/**
 * This method was created in VisualAge.
 * @return double[]
 * @param vectorIndex int
 */
public final ODESolverResultSet getODESolverResultSet() {
	if (fieldODESolverResultSet == null) {
		return new ODESolverResultSet();
	} else {
		return (fieldODESolverResultSet);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/28/01 2:37:27 PM)
 * @return double
 */
public double getProgress() {
	Simulation sim = simTask.getSimulation();

	double currTime = getCurrentTime();
	TimeBounds timeBounds = sim.getSolverTaskDescription().getTimeBounds();
	double startTime = timeBounds.getStartingTime();
	double endTime = timeBounds.getEndingTime();
	if ((endTime-startTime)==0.0){
		throw new RuntimeException("DefaultODESolver.getProgress(), endTime==startTime");
	}else{
		return currTime/(endTime-startTime);
	}
}


/**
 * Gets the sensitivityParameter property (cbit.vcell.math.Constant) value.
 * THIS IS ONLY A CONVENIENCE FUNCTION...AND IT WOULD BE GOOD TO GET RID OF IT
 * EVENTUALLY...WHEN YOU HAVE TIME!!!
 * @return The sensitivityParameter property value.
 * @see #setSensitivityParameter
 */
public Constant getSensitivityParameter() {
	SimulationSymbolTable simSymbolTable = simTask.getSimulationJob().getSimulationSymbolTable();
	
	Constant origSensParam = simSymbolTable.getSimulation().getSolverTaskDescription().getSensitivityParameter();
	//
	// sensitivity parameter from solverTaskDescription will have the non-overridden nominal value.
	// ask the Simulation for the updated Constant object (with the proper overridden value).
	//
	if (origSensParam!=null){
		return (Constant)simSymbolTable.getVariable(origSensParam.getName());
	}else{
		return null;
	}
}


/**
 * This method was created in VisualAge.
 */
protected final StateVariable getStateVariable(int i) {
	return ((StateVariable) fieldStateVariables.elementAt(i));
}


/**
 * This method was created in VisualAge.
 */
protected final int getStateVariableCount() {
	return (fieldStateVariables.size());
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param variableName java.lang.String
 */
public int getStateVariableIndex(String variableName) {
	// if we have a StateVariable, get it's index
	for (int i = 0; i < getStateVariableCount(); i++) {
		if (getStateVariable(i).getVariable().getName().equals(variableName)) {
			return i;
		}
	}
	// otherwise
	return -1;
}


/**
 * Gets the timeIndex property (int) value.
 * @return The timeIndex property value.
 */
protected SubDomain getSubDomain() {
	return ((SubDomain) simTask.getSimulation().getMathDescription().getSubDomains().nextElement());
}


/**
 * Gets the timeIndex property (int) value.
 * @return The timeIndex property value.
 */
public int getTimeIndex() {
	return (ReservedVariable.TIME.getIndex());
}


/**
 * This method was created in VisualAge.
 * @return double[]
 * @param vectorIndex int
 */
protected final double[] getValueVector(int vectorIndex) {
	return fieldValueVectors.getValues(vectorIndex);
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param variableName java.lang.String
 */
public int getValueVectorCount() {
	return fieldValueVectorCount;
}


/**
 * This method was created in VisualAge.
 * @return double[]
 * @param vectorIndex int
 */
protected final int getVariableIndex(int i) {
	return fieldVariableIndexes[i];
}


/**
 * This method was created by a SmartGuide.
 * @exception SolverException The exception description.
 */
protected void initialize() throws SolverException {
	SimulationSymbolTable simSymbolTable = simTask.getSimulationJob().getSimulationSymbolTable();
	Simulation sim = simSymbolTable.getSimulation();
	try {
		// create a fast system if necessary
		fieldFastAlgebraicSystem = null;
		if (getSubDomain().getFastSystem() != null) {
			fieldFastAlgebraicSystem = new FastAlgebraicSystem(new FastSystemAnalyzer(getSubDomain().getFastSystem(), simSymbolTable));
		}
		//refreshIdentifiers();
		fieldIdentifiers = createIdentifiers();
		fieldSensVariables = createSensitivityVariables();
		//refreshStateVariables();
		fieldStateVariables = createStateVariables();
		//
		// allocate ValueVectors object
		fieldValueVectors = new ValueVectors(getValueVectorCount(), fieldIdentifiers.size());
		// initialize indexes of variables
		fieldVariableIndexes = new int[getStateVariableCount()];
		for (int i = 0; i < getStateVariableCount(); i++) {
			fieldVariableIndexes[i] = getStateVariable(i).getVariable().getIndex();
		}
		// initialize constants
		double initialValues[] = getValueVector(0);
		for (int i = 0; i < fieldIdentifiers.size(); i++) {
			if (fieldIdentifiers.elementAt(i) instanceof Constant) {
				Constant constant = (Constant) fieldIdentifiers.elementAt(i);
				constant.bind(simSymbolTable);
				if (constant.isConstant()){
					initialValues[constant.getIndex()] = constant.getExpression().evaluateConstant(); // constant.getValue();
				}else{
					throw new SolverException("cannot evaluate constant '"+constant.getName()+"' = "+constant.getExpression()); 
				}
			}
		}
		// initialize variables
		for (int i = 0; i < getStateVariableCount(); i++) {
			initialValues[getVariableIndex(i)] = getStateVariable(i).evaluateIC(initialValues);
		}
		fieldODESolverResultSet = createODESolverResultSet();
		// reset - in the ** default ** solvers we don't pick up from where we left off, we can override that behaviour in integrate() if ever necessary
		fieldCurrentTime = sim.getSolverTaskDescription().getTimeBounds().getStartingTime();
	} catch (ExpressionException expressionException) {
		expressionException.printStackTrace(System.out);
		throw new SolverException(expressionException.getMessage());
	} catch (MathException mathException) {
		mathException.printStackTrace(System.out);
		throw new SolverException(mathException.getMessage());
	}
}


/**
 * This method was created by a SmartGuide.
 */
protected void integrate() throws SolverException, UserStopException, IOException {
	try {
		SolverTaskDescription taskDescription = simTask.getSimulation().getSolverTaskDescription();
		double timeStep = taskDescription.getTimeStep().getDefaultTimeStep();
		fieldCurrentTime = taskDescription.getTimeBounds().getStartingTime();
		// before computation begins, settle fast equilibrium
		if (getFastAlgebraicSystem() != null) {
			fieldValueVectors.copyValues(0, 1);
			getFastAlgebraicSystem().initVars(getValueVector(0), getValueVector(1));
			getFastAlgebraicSystem().solveSystem(getValueVector(0), getValueVector(1));
			fieldValueVectors.copyValues(1, 0);
		}
		// check for failure
		check(getValueVector(0));
		updateResultSet();
		//
		int iteration = 0;
		while (fieldCurrentTime < taskDescription.getTimeBounds().getEndingTime()) {
			checkForUserStop();
			step(fieldCurrentTime, timeStep);
			// update (old = new)
			fieldValueVectors.copyValuesDown();
			// compute fast system
			if (getFastAlgebraicSystem() != null) {
				fieldValueVectors.copyValues(0, 1);
				getFastAlgebraicSystem().initVars(getValueVector(0), getValueVector(1));
				getFastAlgebraicSystem().solveSystem(getValueVector(0), getValueVector(1));
				fieldValueVectors.copyValues(1, 0);
			}
			// check for failure
			check(getValueVector(0));
			//fieldCurrentTime += timeStep;
			iteration++;
			fieldCurrentTime = taskDescription.getTimeBounds().getStartingTime() + iteration*timeStep;
			//  Print results if it coincides with a save interval...
			if (taskDescription.getOutputTimeSpec().isDefault()) {
				int keepEvery = ((DefaultOutputTimeSpec)taskDescription.getOutputTimeSpec()).getKeepEvery();
				if ((iteration % keepEvery) == 0) {
					updateResultSet();
				}
			}
		}
		// store last time point
		if (taskDescription.getOutputTimeSpec().isDefault()) {
			int keepEvery = ((DefaultOutputTimeSpec)taskDescription.getOutputTimeSpec()).getKeepEvery();
			if ((iteration % keepEvery) != 0) updateResultSet();
		}
	} catch (ExpressionException | MathException e) {
		throw new SolverException("Solver failed: "+e.getMessage(),e);
	}
}


/**
 * This method was created in VisualAge.
 */
protected abstract void step(double t, double h) throws SolverException;


/**
 * This method was created by a SmartGuide.
 */
protected final void updateResultSet() throws IOException, ExpressionException {
	ODESolverResultSet results = getODESolverResultSet();
	synchronized (results) { // so that we don't mess up when saving or reading intermediate results; performance penalty minimal
		double[] valueVector = getValueVector(0);
		double[] values = new double[getStateVariableCount()+1];  // one extra index for time.
		for (int i = 0; i < getStateVariableCount(); i++) {
			int c = results.findColumn(getStateVariable(i).getVariable().getName());
			// cbit.util.Assertion.assert(c >= 0 && c < results.getDataColumnCount());
			values[c] = valueVector[getVariableIndex(i)];
		}
		values[ReservedVariable.TIME.getIndex()] = getCurrentTime();
		results.addRow (values);
	}
	//setSolverStatus(new SolverStatus (SolverStatus.SOLVER_RUNNING));
	Simulation sim = simTask.getSimulation();
	double t = getCurrentTime();
	TimeBounds timeBounds = sim.getSolverTaskDescription().getTimeBounds();
	double t0 = timeBounds.getStartingTime();
	double t1 = timeBounds.getEndingTime();
	printToFile((t - t0) / (t1 - t0));
}
}
