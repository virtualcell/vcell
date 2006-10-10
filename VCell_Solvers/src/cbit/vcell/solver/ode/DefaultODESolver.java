package cbit.vcell.solver.ode;
import cbit.util.SessionLog;
import cbit.vcell.parser.*;
import java.io.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;

import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;
import org.vcell.expression.SimpleSymbolTable;

import cbit.vcell.math.*;
import cbit.vcell.simdata.FunctionColumnDescription;
import cbit.vcell.simdata.FunctionFileGenerator;
import cbit.vcell.simdata.ODESolverResultSet;
import cbit.vcell.simdata.ODESolverResultSetColumnDescription;
import cbit.vcell.simulation.*;
import cbit.vcell.solvers.SimulationJob;
import cbit.vcell.solvers.SolverException;
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
	private Vector fieldIdentifiers = null;
	private Vector fieldSensVariables = null;
	private Vector fieldStateVariables = null;
	private FastAlgebraicSystem fieldFastAlgebraicSystem = null;
	private int fieldVariableIndexes[] = null;
	// current state of the computation
	protected double fieldCurrentTime = 0.0;

/**
 * Set sensitivityParameter to null if sensitivity analysis
 * is not to be performed...
 * @param simContext cbit.vcell.math.MathDescription
 */
public DefaultODESolver(SimulationJob simJob, File directory, SessionLog sessionLog, int valueVectorCount)  throws SolverException {
	super(simJob, directory, sessionLog);
	if (getSimulation().getIsSpatial()) {
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
	throw new SolverException("simulation failed at time="+getCurrentTime()+": consider using smaller time step.\nvalues are:\n"+buffer);
}


/**
 * This method was created in VisualAge.
 */
private Vector createIdentifiers() throws MathException, ExpressionException {
	// create list of possible identifiers (including reserved x,y,z,t)
	Vector identifiers = new Vector();
	// add reserved variables x,y,z,t
	identifiers.addElement(ReservedVariable.fromString("t"));
	identifiers.addElement(ReservedVariable.fromString("x"));
	identifiers.addElement(ReservedVariable.fromString("y"));
	identifiers.addElement(ReservedVariable.fromString("z"));
	// add regular variables
	Variable variables[] = getSimulation().getVariables();
	for (int i = 0; i < variables.length; i++){	
		if (variables[i] instanceof VolVariable) {
			identifiers.addElement(variables[i]);
		}
	}
	//  Add sensitivity variables (for sensitivity equations)...
	fieldSensVariables = new Vector();
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
		Enumeration enum1 = getSubDomain().getFastSystem().getPseudoConstants();
		while (enum1.hasMoreElements()) {
			identifiers.addElement((PseudoConstant) enum1.nextElement());
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
	//
	// create symbol table for binding expression
	//
	String symbols[] = new String[fieldIdentifiers.size()];
	for (int i = 0; i < symbols.length; i++) {
		symbols[i] = ((Variable)fieldIdentifiers.elementAt(i)).getName();
	}
	SimpleSymbolTable varsSymbolTable = new SimpleSymbolTable(symbols);

	
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

	Variable variables[] = getSimulation().getVariables();
	for (int i = 0; i < variables.length; i++){
		if (variables[i] instanceof Function && FunctionFileGenerator.isFunctionSaved((Function)variables[i])){
			Function function = (Function)variables[i];
			IExpression exp1 = ExpressionFactory.createExpression(function.getExpression());
			try {
				exp1 = getSimulation().substituteFunctions(exp1);
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
			FunctionColumnDescription fcd = new FunctionColumnDescription(ExpressionFactory.createExpression(getSensitivityParameter().getConstantValue()), getSensitivityParameter().getName(), null, getSensitivityParameter().getName(), false);
			odeSolverResultSet.addFunctionColumn(fcd);
		}
		StateVariable stateVars[] = (StateVariable[])cbit.util.BeanUtils.getArray(fieldStateVariables,StateVariable.class);
		for (int i = 0; i < variables.length; i++){
			if (variables[i] instanceof Function && FunctionFileGenerator.isFunctionSaved((Function)variables[i])){
				Function depSensFunction = (Function)variables[i];
				IExpression depSensFnExpr = ExpressionFactory.createExpression(depSensFunction.getExpression());
				try {
					depSensFnExpr = getSimulation().substituteFunctions(depSensFnExpr);
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
private Vector createSensitivityVariables() throws MathException, ExpressionException {
	Vector sensVariables = new Vector();
	if (getSensitivityParameter() != null) {
		for (int i = 0; i < fieldIdentifiers.size(); i++) {
			if (fieldIdentifiers.elementAt(i) instanceof SensVariable) {
				sensVariables.addElement(fieldIdentifiers.elementAt(i));
			}
		}
	}
	return (sensVariables);
}


/**
 * This method was created in VisualAge.
 */
private Vector createStateVariables() throws MathException, ExpressionException {
	Vector stateVariables = new Vector();
	// get Ode's from MathDescription and create ODEStateVariables
	Enumeration enum1 = getSubDomain().getEquations();
	while (enum1.hasMoreElements()) {
		Equation equation = (Equation) enum1.nextElement();
		if (equation instanceof OdeEquation) {
			stateVariables.addElement(new ODEStateVariable((OdeEquation) equation, getSimulation()));
		} else {
			throw new MathException("encountered non-ode equation, unsupported");
		}
	}
	// get Jacobian and RateSensitivities from MathDescription and create SensStateVariables
	for (int v = 0; v < fieldSensVariables.size(); v++) {
		stateVariables.addElement(
			new SensStateVariable((SensVariable) fieldSensVariables.elementAt(v),
									getSimulation().getMathDescription().getRateSensitivity(), 
									getSimulation().getMathDescription().getJacobian(),
									fieldSensVariables, 
									getSimulation()));
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
	double currTime = getCurrentTime();
	double startTime = getSimulation().getSolverTaskDescription().getTimeBounds().getStartingTime();
	double endTime = getSimulation().getSolverTaskDescription().getTimeBounds().getEndingTime();
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
public cbit.vcell.math.Constant getSensitivityParameter() {
	Constant origSensParam = getSimulation().getSolverTaskDescription().getSensitivityParameter();
	//
	// sensitivity parameter from solverTaskDescription will have the non-overridden nominal value.
	// ask the Simulation for the updated Constant object (with the proper overridden value).
	//
	if (origSensParam!=null){
		return (Constant)getSimulation().getVariable(origSensParam.getName());
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
	return ((SubDomain) getSimulation().getMathDescription().getSubDomains().nextElement());
}


/**
 * Gets the timeIndex property (int) value.
 * @return The timeIndex property value.
 */
public int getTimeIndex() {
	return (ReservedVariable.fromString("t").getIndex());
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
	try {
		// create a fast system if necessary
		fieldFastAlgebraicSystem = null;
		if (getSubDomain().getFastSystem() != null) {
			fieldFastAlgebraicSystem = new FastAlgebraicSystem(getSimulation(), getSubDomain().getFastSystem());
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
				constant.bind(getSimulation());
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
		fieldCurrentTime = getSimulation().getSolverTaskDescription().getTimeBounds().getStartingTime();
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
		SolverTaskDescription taskDescription = getSimulation().getSolverTaskDescription();
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
	} catch (ExpressionException expressionException) {
		throw new SolverException(expressionException.getMessage());
	} catch (MathException mathException) {
		throw new SolverException(mathException.getMessage());
	}
}


/**
 * THIS METHOD HAS BEEN REPLACED BY createIdentifiers().
 * I am keeping it around until that new method has been
 * sufficiently tested.  JMW
 */
private void refreshXXXIdentifiers() throws MathException, ExpressionException {
	fieldIdentifiers = createIdentifiers();
	fieldSensVariables = createSensitivityVariables();
	// create list of possible identifiers (including reserved x,y,z,t)
	fieldIdentifiers = new Vector();
	// add reserved variables x,y,z,t
	fieldIdentifiers.addElement(ReservedVariable.fromString("t"));
	fieldIdentifiers.addElement(ReservedVariable.fromString("x"));
	fieldIdentifiers.addElement(ReservedVariable.fromString("y"));
	fieldIdentifiers.addElement(ReservedVariable.fromString("z"));
	// add regular variables and constants
	Variable variables[] = getSimulation().getVariables();
	for (int i=0;i<variables.length;i++){
		if (variables[i] instanceof VolVariable || variables[i] instanceof Constant) {
			fieldIdentifiers.addElement(variables[i]);
		}
	}
	//  Add sensitivity variables (for sensitivity equations)...
	fieldSensVariables = new Vector();
	if (getSensitivityParameter() != null) {
		for (int i = 0; i < variables.length; i++){
			if (variables[i] instanceof VolVariable){
				VolVariable volVariable = (VolVariable)variables[i];
				SensVariable sv = new SensVariable(volVariable, getSensitivityParameter());
				fieldIdentifiers.addElement(sv);
				fieldSensVariables.addElement(sv);
			}
		}
	}
	//  Add pseudoConstants for fast system (if necessary)...
	if (getFastAlgebraicSystem() != null) {
		Enumeration enum1 = getSubDomain().getFastSystem().getPseudoConstants();
		while (enum1.hasMoreElements()) {
			fieldIdentifiers.addElement((PseudoConstant) enum1.nextElement());
		}
	}
	//  Assign indices...
	for (int i = 0; i < fieldIdentifiers.size(); i++) {
		Variable variable = (Variable) fieldIdentifiers.elementAt(i);
		variable.setIndex(i);
	}
}


/**
 * THIS METHOD HAS BEEN REPLACED BY createStateVariables().
 * I am keeping it around until that new method has been
 * sufficiently tested.  JMW
 */
private void refreshXXXStateVariables() throws MathException, ExpressionException {
	fieldStateVariables = new Vector();
	// get Ode's from MathDescription and create ODEStateVariables
	Enumeration enum1 = getSubDomain().getEquations();
	while (enum1.hasMoreElements()) {
		Equation equation = (Equation) enum1.nextElement();
		if (equation instanceof OdeEquation) {
			fieldStateVariables.addElement(new ODEStateVariable((OdeEquation) equation, getSimulation()));
		} else {
			throw new MathException("encountered non-ode equation, unsupported");
		}
	}
	// get Jacobian and RateSensitivities from MathDescription and create SensStateVariables
	for (int v = 0; v < fieldSensVariables.size(); v++) {
		fieldStateVariables.addElement(
			new SensStateVariable((SensVariable) fieldSensVariables.elementAt(v),
				getSimulation().getMathDescription().getRateSensitivity(), 
				getSimulation().getMathDescription().getJacobian(),
				fieldSensVariables, getSimulation()));
	}
	if (fieldStateVariables.size() == 0) {
		throw new MathException("there are no equations defined");
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
	double t = getCurrentTime();
	double t0 = getSimulation().getSolverTaskDescription().getTimeBounds().getStartingTime();
	double t1 = getSimulation().getSolverTaskDescription().getTimeBounds().getEndingTime();
	printToFile((t - t0) / (t1 - t0));
}
}