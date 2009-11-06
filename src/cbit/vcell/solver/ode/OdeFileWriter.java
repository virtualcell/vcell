package cbit.vcell.solver.ode;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;

import cbit.vcell.math.Constant;
import cbit.vcell.math.Equation;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.OdeEquation;
import cbit.vcell.math.ParameterVariable;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolVariable;
import cbit.vcell.parser.Discontinuity;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.VariableSymbolTable;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.ExplicitOutputTimeSpec;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverFileWriter;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;
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
public OdeFileWriter(PrintWriter pw, SimulationJob simJob, boolean messaging) {
	super(pw, simJob, messaging);
}


/**
 * OdeFileCoder constructor comment.
 */
private void addStateVariable (StateVariable variable) {
	fieldStateVariables.addElement(variable);
}

public Discontinuity getSubsitutedAndFlattened(Discontinuity discontinuity, SymbolTable st) throws ExpressionException {		
	Expression discontinuityExp = MathUtilities.substituteFunctions(discontinuity.getDiscontinuityExp(), st).flatten();
	Expression rootFindingExp = MathUtilities.substituteFunctions(discontinuity.getRootFindingExp(), st).flatten();
	return new Discontinuity(discontinuityExp,rootFindingExp);
}

/**
 * OdeFileCoder constructor comment.
 */
protected VariableSymbolTable createSymbolTable() {
	
	// Get the vector of sensVariables, needed for creating SensStateVariables 
	Vector<SensStateVariable> sensVars = new Vector<SensStateVariable>();
	for (int i = 0; i < getStateVariableCount(); i++) {
		if (simulationJob.getSimulation().getSolverTaskDescription().getSensitivityParameter() != null) {
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
	
	Variable variables[] = simulationJob.getSimulationSymbolTable().getVariables();
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
	Simulation simulation = simulationJob.getSimulation();
	if (!simulation.getIsValid()) {
		throw new MathException("invalid simulation : "+simulation.getWarning());
	}
	if (simulation.isSpatial()) {
		throw new MathException("solver does not support spatial models. Please change the solver.");
	}
	MathDescription mathDescription = simulation.getMathDescription();
	SolverTaskDescription solverTaskDescription = simulation.getSolverTaskDescription();
	if (mathDescription.hasFastSystems()) {
		if (!solverTaskDescription.getSolverDescription().solvesFastSystem()) {
			throw new MathException("solver does not support models containing fast system (algebraic constraints). Please change the solver.");
		}
	}


	// get Ode's from MathDescription and create ODEStateVariables
	Enumeration<Equation> enum1 = mathDescription.getSubDomains().nextElement().getEquations();
	SimulationSymbolTable simSymbolTable = simulationJob.getSimulationSymbolTable();
	while (enum1.hasMoreElements()) {
		Equation equation = enum1.nextElement();
		if (equation instanceof OdeEquation) {
			addStateVariable(new ODEStateVariable((OdeEquation) equation, simSymbolTable));
		} else {
			throw new MathException("encountered non-ode equation, unsupported");
		}
	}

	//  Get sensitivity variables
	Variable variables[] = simSymbolTable.getVariables(); 
	Vector<SensVariable> sensVariables = new Vector<SensVariable>();
	Constant sensitivityParameter = solverTaskDescription.getSensitivityParameter();
	if (sensitivityParameter != null) {
		Constant origSensParam = sensitivityParameter;
		Constant overriddenSensParam = (Constant)simSymbolTable.getVariable(origSensParam.getName());
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
					mathDescription.getRateSensitivity(), 
					mathDescription.getJacobian(),
					sensVariables, 
					simSymbolTable));
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
	
	Simulation simulation = simulationJob.getSimulation();
	
	if (simulation.getSolverTaskDescription().getUseSymbolicJacobian()){
		throw new RuntimeException("symbolic jacobian option not yet supported in interpreted Stiff solver");
	}
	
	writeJMSParamters();
	
	SolverTaskDescription solverTaskDescription = simulation.getSolverTaskDescription();
	TimeBounds timeBounds = solverTaskDescription.getTimeBounds();
	ErrorTolerance errorTolerance = solverTaskDescription.getErrorTolerance();
	printWriter.println("SOLVER " + getSolverName());
	printWriter.println("STARTING_TIME " + timeBounds.getStartingTime());
	printWriter.println("ENDING_TIME " + timeBounds.getEndingTime());
	printWriter.println("RELATIVE_TOLERANCE " + errorTolerance.getRelativeErrorTolerance());
	printWriter.println("ABSOLUTE_TOLERANCE " + errorTolerance.getAbsoluteErrorTolerance());
  	printWriter.println("MAX_TIME_STEP "+simulation.getSolverTaskDescription().getTimeStep().getMaximumTimeStep());
  	OutputTimeSpec ots = simulation.getSolverTaskDescription().getOutputTimeSpec();
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