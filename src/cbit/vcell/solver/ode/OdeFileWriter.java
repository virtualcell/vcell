package cbit.vcell.solver.ode;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.parser.*;
import java.util.*;
import java.io.*;
import cbit.vcell.math.*;
import cbit.vcell.solver.*;
/**
 * Insert the type's description here.
 * Creation date: (3/8/00 10:29:24 PM)
 * @author: John Wagner
 */
public abstract class OdeFileWriter extends SolverFileWriter {
	private Vector<StateVariable> fieldStateVariables = new Vector<StateVariable>();
	protected String ROOT_VARIABLE_PREFIX = "__D_B_";

/**
 * OdeFileCoder constructor comment.
 */
public OdeFileWriter(PrintWriter pw, Simulation sim, int ji, boolean messaging) {
	super(pw, sim, ji, messaging);
}


/**
 * OdeFileCoder constructor comment.
 */
private void addStateVariable (StateVariable variable) {
	fieldStateVariables.addElement(variable);
}


/**
 * OdeFileCoder constructor comment.
 */
protected VariableSymbolTable createSymbolTable() {
	
	// Get the vector of sensVariables, needed for creating SensStateVariables 
	Vector<SensStateVariable> sensVars = new Vector<SensStateVariable>();
	for (int i = 0; i < getStateVariableCount(); i++) {
		if (getSimulation().getSolverTaskDescription().getSensitivityParameter() != null) {
			if (getStateVariable(i) instanceof SensStateVariable) {
				sensVars.addElement((SensStateVariable)getStateVariable(i));
			}
		}
	}

	//
	// Create symbol table for binding sensitivity variable expressions. (Cannot bind to simulation, 
	// since it does not have the sensitivity variables corresponding to the volume variables).
	//

	VariableSymbolTable varsSymbolTable = new VariableSymbolTable();
	varsSymbolTable.addVar(ReservedVariable.TIME); // SymbolTableEntry.index doesn't matter ... just code generating binding by var names not index.
	int count = 0;
	
	Variable variables[] = getSimulation().getVariables();
	for (int i = 0; i < variables.length; i++) {
		if (variables[i] instanceof VolVariable) {
			VolVariable vVar = (VolVariable)variables[i];
			vVar.setIndex(count);
			varsSymbolTable.addVar(vVar);
			count++;
		} else if (variables[i] instanceof Function) {
			Function func = (Function)variables[i];
			func.setIndex(count);
			varsSymbolTable.addVar(func);
			count++;
		} else if (variables[i] instanceof Constant) {
			Constant constant = (Constant)variables[i];
			constant.setIndex(count);
			varsSymbolTable.addVar(constant);
			count++;
		} else if (variables[i] instanceof ParameterVariable) {
			ParameterVariable param = (ParameterVariable)variables[i];
			param.setIndex(count);
			varsSymbolTable.addVar(param);
			count ++;
		}
	}
	for (int j = count; j < (count+sensVars.size()); j++) {
		SensVariable sVar = (SensVariable)(sensVars.elementAt(j-count).getVariable());
		sVar.setIndex(j);
		varsSymbolTable.addVar(sVar);		
	}
	
	return varsSymbolTable;
}


/**
 * OdeFileCoder constructor comment.
 */
public Simulation getSimulation() {
	return simulation;
}


/**
 * OdeFileCoder constructor comment.
 */
public StateVariable getStateVariable (int i) {
	return ((StateVariable) fieldStateVariables.elementAt(i));
}


/**
 * OdeFileCoder constructor comment.
 */
public int getStateVariableCount () {
	return (fieldStateVariables.size());
}


private void initialize() throws Exception {
	if (!getSimulation().getIsValid()) {
		throw new MathException("invalid simulation : "+getSimulation().getWarning());
	}
	if (getSimulation().getIsSpatial()) {
		throw new MathException("solver does not support spatial models. Please change the solver.");
	}
	if (getSimulation().getMathDescription().hasFastSystems()) {
		if (!getSimulation().getSolverTaskDescription().getSolverDescription().solvesFastSystem()) {
			throw new MathException("solver does not support models containing fast system (algebraic constraints). Please change the solver.");
		}
	}


	// get Ode's from MathDescription and create ODEStateVariables
	Enumeration<Equation> enum1 = getSimulation().getMathDescription().getSubDomains().nextElement().getEquations();
	while (enum1.hasMoreElements()) {
		Equation equation = enum1.nextElement();
		if (equation instanceof OdeEquation) {
			addStateVariable(new ODEStateVariable((OdeEquation) equation, getSimulation()));
		} else {
			throw new MathException("encountered non-ode equation, unsupported");
		}
	}

	//  Get sensitivity variables
	Variable variables[] = getSimulation().getVariables(); 
	Vector<SensVariable> sensVariables = new Vector<SensVariable>();
	if (getSimulation().getSolverTaskDescription().getSensitivityParameter() != null) {
		Constant origSensParam = getSimulation().getSolverTaskDescription().getSensitivityParameter();
		Constant overriddenSensParam = (Constant)getSimulation().getVariable(origSensParam.getName());
		for (int i = 0; i < variables.length; i++){
			if (variables[i] instanceof VolVariable){
				VolVariable volVariable = (VolVariable)variables[i];
				SensVariable sv = new SensVariable(volVariable, overriddenSensParam);
				sensVariables.addElement(sv);
			}
		}
	}
	
	// get Jacobian and RateSensitivities from MathDescription and create SensStateVariables
	for (int v = 0; v < sensVariables.size(); v++) {
		addStateVariable(
			new SensStateVariable((SensVariable) sensVariables.elementAt(v),
									getSimulation().getMathDescription().getRateSensitivity(), 
									getSimulation().getMathDescription().getJacobian(),
									sensVariables, 
									getSimulation()));
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/8/00 10:31:52 PM)
 */
protected abstract void writeEquations() throws MathException, ExpressionException;

/**
 * Insert the method's description here.
 * Creation date: (3/8/00 10:31:52 PM)
 */
public void write(String[] parameterNames) throws Exception {
	initialize();
	
	if (getSimulation().getSolverTaskDescription().getUseSymbolicJacobian()){
		throw new RuntimeException("symbolic jacobian option not yet supported in interpreted Stiff solver");
	}
	
	writeJMSParamters();
	
	SolverTaskDescription solverTaskDescription = getSimulation().getSolverTaskDescription();
	TimeBounds timeBounds = solverTaskDescription.getTimeBounds();
	ErrorTolerance errorTolerance = solverTaskDescription.getErrorTolerance();
	printWriter.println("SOLVER " + getSolverName());
	printWriter.println("STARTING_TIME " + timeBounds.getStartingTime());
	printWriter.println("ENDING_TIME " + timeBounds.getEndingTime());
	printWriter.println("RELATIVE_TOLERANCE " + errorTolerance.getRelativeErrorTolerance());
	printWriter.println("ABSOLUTE_TOLERANCE " + errorTolerance.getAbsoluteErrorTolerance());
  	printWriter.println("MAX_TIME_STEP "+getSimulation().getSolverTaskDescription().getTimeStep().getMaximumTimeStep());
  	OutputTimeSpec ots = getSimulation().getSolverTaskDescription().getOutputTimeSpec();
  	if (ots.isDefault()) {
		printWriter.println("KEEP_EVERY "+((DefaultOutputTimeSpec)ots).getKeepEvery());
  	} else if (ots.isUniform()) {
	  	printWriter.println("OUTPUT_TIME_STEP "+((UniformOutputTimeSpec)ots).getOutputTimeStep());
  	} else if (ots.isExplicit()) {
	  	printWriter.println("OUTPUT_TIMES " + ((ExplicitOutputTimeSpec)ots).getNumTimePoints());
	  	printWriter.println(((ExplicitOutputTimeSpec)ots).toSpaceSeperatedMultiLinesOfString());
  	}
  	if (parameterNames != null && parameterNames.length != 0) {
	  	printWriter.println("NUM_PARAMETERS " + parameterNames.length);
	  	for (int i = 0; i < parameterNames.length; i ++) {
		  	printWriter.println(parameterNames[i]);
	  	}
  	}
	writeEquations();
	
	printWriter.flush();
}

abstract String getSolverName();
}