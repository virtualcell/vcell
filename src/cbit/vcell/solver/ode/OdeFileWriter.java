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
public abstract class OdeFileWriter {
	private Vector<StateVariable> fieldStateVariables = new Vector<StateVariable>();
	private Simulation fieldSimulation = null;

/**
 * OdeFileCoder constructor comment.
 */
public OdeFileWriter(Simulation simulation) {
	fieldSimulation = simulation;
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
	return (fieldSimulation);
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


public void initialize() throws Exception {
	if (!getSimulation().getIsValid()) {
		throw new MathException("invalid simulation : "+getSimulation().getWarning());
	}
	if (getSimulation().getIsSpatial()) {
		throw new MathException("solver doesn't support spatial models. Please change the solver.");
	}
	if (getSimulation().getMathDescription().hasFastSystems()) {
		if (!getSimulation().getSolverTaskDescription().getSolverDescription().solvesFastSystem()) {
			throw new MathException("solver doesn't support models containing fast system (algebraic constraints). Please change the solver.");
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
protected abstract void writeEquations(java.io.PrintWriter pw) throws MathException, ExpressionException;

/**
 * Insert the method's description here.
 * Creation date: (3/8/00 10:31:52 PM)
 */
public void writeInputFile(PrintWriter pw) throws Exception {
	writeInputFile(pw, null);
}


/**
 * Insert the method's description here.
 * Creation date: (3/8/00 10:31:52 PM)
 */
public void writeInputFile(PrintWriter pw, String[] parameterNames) throws Exception {
	
	if (getSimulation().getSolverTaskDescription().getUseSymbolicJacobian()){
		throw new RuntimeException("symbolic jacobian option not yet supported in interpreted Stiff solver");
	}
	
	SolverTaskDescription solverTaskDescription = getSimulation().getSolverTaskDescription();
	TimeBounds timeBounds = solverTaskDescription.getTimeBounds();
	ErrorTolerance errorTolerance = solverTaskDescription.getErrorTolerance();
	pw.println("STARTING_TIME " + timeBounds.getStartingTime());
	pw.println("ENDING_TIME " + timeBounds.getEndingTime());
	pw.println("RELATIVE_TOLERANCE " + errorTolerance.getRelativeErrorTolerance());
	pw.println("ABSOLUTE_TOLERANCE " + errorTolerance.getAbsoluteErrorTolerance());
  	pw.println("MAX_TIME_STEP "+getSimulation().getSolverTaskDescription().getTimeStep().getMaximumTimeStep());
  	OutputTimeSpec ots = getSimulation().getSolverTaskDescription().getOutputTimeSpec();
  	if (ots.isDefault()) {
		pw.println("KEEP_EVERY "+((DefaultOutputTimeSpec)ots).getKeepEvery());
  	} else if (ots.isUniform()) {
	  	pw.println("OUTPUT_TIME_STEP "+((UniformOutputTimeSpec)ots).getOutputTimeStep());
  	} else if (ots.isExplicit()) {
	  	pw.println("OUTPUT_TIMES " + ((ExplicitOutputTimeSpec)ots).getNumTimePoints());
	  	pw.println(((ExplicitOutputTimeSpec)ots).toSpaceSeperatedMultiLinesOfString());
  	}
  	if (parameterNames != null && parameterNames.length != 0) {
	  	pw.println("NUM_PARAMETERS " + parameterNames.length);
	  	for (int i = 0; i < parameterNames.length; i ++) {
		  	pw.println(parameterNames[i]);
	  	}
  	}
	writeEquations(pw);
	
	pw.flush();
}
}