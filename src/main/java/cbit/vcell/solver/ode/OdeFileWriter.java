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
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import cbit.vcell.math.Constant;
import cbit.vcell.math.Equation;
import cbit.vcell.math.Event;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.OdeEquation;
import cbit.vcell.math.ParameterVariable;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolVariable;
import cbit.vcell.math.Event.Delay;
import cbit.vcell.math.Event.EventAssignment;
import cbit.vcell.messaging.server.SimulationTask;
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
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.server.SolverFileWriter;
/**
 * Insert the type's description here.
 * Creation date: (3/8/00 10:29:24 PM)
 * @author: John Wagner
 */
public abstract class OdeFileWriter extends SolverFileWriter {
	private Vector<StateVariable> fieldStateVariables = new Vector<StateVariable>();
	protected String ROOT_VARIABLE_PREFIX = "__D_B_";
	private transient RateSensitivity rateSensitivity = null;
	private transient Jacobian jacobian = null;
	protected VariableSymbolTable varsSymbolTable = null;

/**
 * OdeFileCoder constructor comment.
 */
public OdeFileWriter(PrintWriter pw, SimulationTask simTask, boolean messaging) {
	super(pw, simTask, messaging);
}

public Discontinuity getSubsitutedAndFlattened(Discontinuity discontinuity, SymbolTable st) throws ExpressionException {		
	Expression discontinuityExp = MathUtilities.substituteFunctions(discontinuity.getDiscontinuityExp(), st).flatten();
	Expression rootFindingExp = MathUtilities.substituteFunctions(discontinuity.getRootFindingExp(), st).flatten();
	return new Discontinuity(discontinuityExp,rootFindingExp,discontinuity.getASTRelationOperator());
}

/**
 * OdeFileCoder constructor comment.
 * @throws Exception 
 */
private void createSymbolTable() throws Exception {
	
	//
	// Create symbol table for binding sensitivity variable expressions. (Cannot bind to simulation, 
	// since it does not have the sensitivity variables corresponding to the volume variables).
	//

	varsSymbolTable = new VariableSymbolTable();
	varsSymbolTable.addVar(ReservedVariable.TIME); // SymbolTableEntry.index doesn't matter ... just code generating binding by var names not index.
	int count = 0;
	
	Variable variables[] = simTask.getSimulationJob().getSimulationSymbolTable().getVariables();
	
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
	// Get the vector of sensVariables, needed for creating SensStateVariables 
	Vector<SensStateVariable> sensVars = new Vector<SensStateVariable>();
	for (int i = 0; i < getStateVariableCount(); i++) {
		if (simTask.getSimulation().getSolverTaskDescription().getSensitivityParameter() != null) {
			if (getStateVariable(i) instanceof SensStateVariable) {
				sensVars.addElement((SensStateVariable)getStateVariable(i));
			}
		}
	}
	for (int j = count; j < (count+sensVars.size()); j++) {
		SensVariable sVar = (SensVariable)(sensVars.elementAt(j-count).getVariable());
		sVar.setIndex(j);
		varsSymbolTable.addVar(sVar);		
	}
}

/**
 * OdeFileCoder constructor comment.
 */
public StateVariable getStateVariable (int i) {
	return fieldStateVariables.elementAt(i);
}


/**
 * OdeFileCoder constructor comment.
 */
public int getStateVariableCount () {
	return (fieldStateVariables.size());
}


private void createStateVariables() throws Exception {
	Simulation simulation = simTask.getSimulation();

	MathDescription mathDescription = simulation.getMathDescription();
	SolverTaskDescription solverTaskDescription = simulation.getSolverTaskDescription();

	// get Ode's from MathDescription and create ODEStateVariables
	Enumeration<Equation> enum1 = mathDescription.getSubDomains().nextElement().getEquations();
	SimulationSymbolTable simSymbolTable = simTask.getSimulationJob().getSimulationSymbolTable();
	while (enum1.hasMoreElements()) {
		Equation equation = enum1.nextElement();
		if (equation instanceof OdeEquation) {
			fieldStateVariables.addElement(new ODEStateVariable((OdeEquation) equation, simSymbolTable));
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
	if (rateSensitivity==null){
		rateSensitivity = new RateSensitivity(mathDescription, mathDescription.getSubDomains().nextElement());
	}
	if (jacobian==null){
		jacobian = new Jacobian(mathDescription, mathDescription.getSubDomains().nextElement());
	}
	// get Jacobian and RateSensitivities from MathDescription and create SensStateVariables
	for (int v = 0; v < sensVariables.size(); v++) {
		fieldStateVariables.addElement(
			new SensStateVariable(sensVariables.elementAt(v), rateSensitivity, jacobian, sensVariables, simSymbolTable));
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/8/00 10:31:52 PM)
 */
protected abstract String writeEquations(HashMap<Discontinuity, String> discontinuityNameMap) throws MathException, ExpressionException;

/**
 * Insert the method's description here.
 * Creation date: (3/8/00 10:31:52 PM)
 */
public void write(String[] parameterNames) throws Exception {
	createStateVariables();
	createSymbolTable();
	
	Simulation simulation = simTask.getSimulation();
	
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
  	
  	HashMap<Discontinuity, String> discontinuityNameMap = new HashMap<Discontinuity, String>();
  	String eventString = null;
  	if (simulation.getMathDescription().hasEvents()) {
  		eventString = writeEvents(discontinuityNameMap);
  	}  	
	String equationString = writeEquations(discontinuityNameMap);
	
	if (discontinuityNameMap.size() > 0) {
		printWriter.println("DISCONTINUITIES " + discontinuityNameMap.size());
		for (Discontinuity od : discontinuityNameMap.keySet()) {
			printWriter.println(discontinuityNameMap.get(od) + " " + od.getDiscontinuityExp().flatten().infix() + "; " + od.getRootFindingExp().flatten().infix() + ";");
		}
	}
	if (eventString != null) {
  		printWriter.print(eventString);
	}
	printWriter.println("NUM_EQUATIONS " + getStateVariableCount());
	printWriter.println(equationString);
}

private String writeEvents(HashMap<Discontinuity, String> discontinuityNameMap) throws ExpressionException {
	Simulation simulation = simTask.getSimulation();
	
	StringBuffer sb = new StringBuffer();
	MathDescription mathDescription = simulation.getMathDescription();
	Iterator<Event> iter = mathDescription.getEvents();
	sb.append("EVENTS " + mathDescription.getNumEvents() + "\n");
  	while (iter.hasNext()) {
  		Event event = iter.next();
  		sb.append("EVENT " + event.getName() + "\n");
  		Expression triggerExpression = event.getTriggerExpression();
  		triggerExpression = MathUtilities.substituteFunctions(triggerExpression, varsSymbolTable).flatten();
  		
  		Vector<Discontinuity> v = triggerExpression.getDiscontinuities();
		for (Discontinuity od : v) {
			od = getSubsitutedAndFlattened(od,varsSymbolTable);
			String dname = discontinuityNameMap.get(od);
			if (dname == null) {
				dname = ROOT_VARIABLE_PREFIX + discontinuityNameMap.size();
				discontinuityNameMap.put(od, dname);
			}
			triggerExpression.substituteInPlace(od.getDiscontinuityExp(), new Expression("(" + dname + "==1)"));
		}
		
		sb.append("TRIGGER " + triggerExpression.infix() + ";\n");
  		Delay delay = event.getDelay();
  		if (delay != null) {
	  		Expression durationExpression = delay.getDurationExpression();
	  		durationExpression = MathUtilities.substituteFunctions(durationExpression, varsSymbolTable).flatten();
	  		sb.append("DELAY " + delay.useValuesFromTriggerTime() + " " + durationExpression.infix() + ";\n");
  		}
  		sb.append("EVENTASSIGNMENTS " + event.getNumEventAssignments() + "\n");
  		Iterator<EventAssignment> iter2 = event.getEventAssignments();
  		while (iter2.hasNext()) {
  			EventAssignment eventAssignment = iter2.next();
			Expression assignmentExpression = eventAssignment.getAssignmentExpression();
  			assignmentExpression = MathUtilities.substituteFunctions(assignmentExpression, varsSymbolTable).flatten();
  			Variable assignmentTarget = eventAssignment.getVariable();
  			for (int i = 0; i < fieldStateVariables.size(); i ++) {
  				if (assignmentTarget.getName().equals(fieldStateVariables.get(i).getVariable().getName())) {
  					sb.append(i + " " + assignmentExpression.infix() + ";\n");
  					break;
  				}
  			}
  		}
  	}
  	return sb.toString();
}

abstract String getSolverName();
}
