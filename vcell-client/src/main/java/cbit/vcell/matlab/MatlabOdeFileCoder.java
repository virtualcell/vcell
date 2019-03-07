/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.matlab;

import java.util.ArrayList;
import java.util.Vector;

import org.vcell.util.BeanUtils;

import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VariableHash;
import cbit.vcell.math.VolVariable;
import cbit.vcell.matrix.RationalMatrix;
import cbit.vcell.matrix.RationalNumberMatrix;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.OutputFunctionContext;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
/**
 * Insert the type's description here.
 * Creation date: (3/8/00 10:29:24 PM)
 * @author: 
 */
public class MatlabOdeFileCoder {
	private Simulation simulation = null;
	private RationalMatrix stoichMatrix = null;
	private SimulationSymbolTable simulationSymbolTable = null;
/**
 * OdeFileCoder constructor comment.
 */
public MatlabOdeFileCoder(Simulation argSimulation) {
	this(argSimulation,null);
}
/**
 * OdeFileCoder constructor comment.
 */
public MatlabOdeFileCoder(Simulation argSimulation, RationalNumberMatrix argStoichMatrix) {
	this.simulation = argSimulation;
	this.stoichMatrix = argStoichMatrix;
	simulationSymbolTable = new SimulationSymbolTable(simulation, 0);
}
/**
 * Insert the method's description here.
 * Creation date: (3/8/00 10:31:52 PM)
 */
public void write_V6_MFile(java.io.PrintWriter pw, String functionName,OutputFunctionContext outputFunctionContext) throws MathException, ExpressionException {
	MathDescription mathDesc = simulation.getMathDescription();
	if (!mathDesc.isValid()){
		throw new MathException("invalid math description\n" + mathDesc.getWarning());
	}
	if (mathDesc.isSpatial()){
		throw new MathException("spatial math description, cannot create ode file");
	}
	if (mathDesc.hasFastSystems()){
		throw new MathException("math description contains algebraic constraints, cannot create .m file");
	}

	//
	// print function declaration
	//
	pw.println("function [T,Y,yinit,param, allNames, allValues] = "+functionName+"(argTimeSpan,argYinit,argParam)");
	pw.println("% [T,Y,yinit,param] = "+functionName+"(argTimeSpan,argYinit,argParam)");
	pw.println("%");
	pw.println("% input:");
	pw.println("%     argTimeSpan is a vector of start and stop times (e.g. timeSpan = [0 10.0])");
	pw.println("%     argYinit is a vector of initial conditions for the state variables (optional)");
	pw.println("%     argParam is a vector of values for the parameters (optional)");
	pw.println("%");
	pw.println("% output:");
	pw.println("%     T is the vector of times");
	pw.println("%     Y is the vector of state variables");
	pw.println("%     yinit is the initial conditions that were used");
	pw.println("%     param is the parameter vector that was used");
	pw.println("%     allNames is the output solution variable names");
	pw.println("%     allValues is the output solution variable values corresponding to the names");
	pw.println("%");
	pw.println("%     example of running this file: [T,Y,yinit,param,allNames,allValues] = myMatlabFunc; <-(your main function name)");
	pw.println("%");
	
	VariableHash varHash = new VariableHash();
	for(Variable var:simulationSymbolTable.getVariables()){
		varHash.addVariable(var);
	}
	Variable variables[] = varHash.getTopologicallyReorderedVariables();
	
	CompartmentSubDomain subDomain = (CompartmentSubDomain)mathDesc.getSubDomains().nextElement();

	//
	// collect "true" constants (Constants without identifiers)
	//
	//
	// collect "variables" (VolVariables only)
	//
	//
	// collect "functions" (Functions and Constants with identifiers)
	//
	Vector<Constant> constantList = new Vector<Constant>();
	Vector<VolVariable> volVarList = new Vector<VolVariable>();
	Vector<Variable> functionList = new Vector<Variable>();
	for (int i = 0; i < variables.length; i++){
		if (variables[i] instanceof Constant){
			Constant constant = (Constant)variables[i];
			String[] symbols = constant.getExpression().getSymbols();
			if (symbols==null || symbols.length==0){
				constantList.addElement(constant);
			} else {
				functionList.add(constant);
			}
		} else if (variables[i] instanceof VolVariable){
			volVarList.addElement((VolVariable)variables[i]);
		} else if (variables[i] instanceof Function){
			functionList.addElement(variables[i]);
		}
	}
	Constant constants[] = (Constant[])BeanUtils.getArray(constantList,Constant.class);
	VolVariable volVars[] = (VolVariable[])BeanUtils.getArray(volVarList,VolVariable.class);
	Variable functions[] = (Variable[])BeanUtils.getArray(functionList,Variable.class);

	int numVars = volVarList.size() + functionList.size();
	String varNamesForStringArray = "";
	String varNamesForValueArray = "";
	
	for(Variable var : volVarList){
		varNamesForStringArray = varNamesForStringArray + "'" + var.getName() + "';";
		varNamesForValueArray = varNamesForValueArray + var.getName() + " ";
	}
	for(Variable func : functionList){
		varNamesForStringArray = varNamesForStringArray + "'" + func.getName() + "';";
		varNamesForValueArray = varNamesForValueArray + func.getName() + " ";
	}
	int numOutputFunctions = 0;
	String outputFunctionNamesForStringArray = "";
	String outputFunctionNamesForValueArray = "";
	if(outputFunctionContext != null && outputFunctionContext.getOutputFunctionsList() != null) {
		ArrayList<AnnotatedFunction> annotFuncList = outputFunctionContext.getOutputFunctionsList();
		for (int i = 0; i < annotFuncList.size(); i++) {
			numOutputFunctions++;
			outputFunctionNamesForStringArray = outputFunctionNamesForStringArray+"'"+annotFuncList.get(i).getName()+"'"+(i == (annotFuncList.size()-1)?"":";");
			outputFunctionNamesForValueArray =  outputFunctionNamesForValueArray +    annotFuncList.get(i).getName() +   (i == (annotFuncList.size()-1)?"":" ");
		}
	}
	
	pw.println("");
	pw.println("%");
	pw.println("% Default time span");
	pw.println("%");
	double beginTime = 0.0;
	double endTime = simulation.getSolverTaskDescription().getTimeBounds().getEndingTime();
	pw.println("timeSpan = ["+beginTime+" "+endTime+"];");
	pw.println("");
	pw.println("% output variable lengh and names");
	pw.println("numVars = " + (numVars+numOutputFunctions) + ";");
	pw.println("allNames = {" + varNamesForStringArray + outputFunctionNamesForStringArray +"};");
	
	pw.println("");
	pw.println("if nargin >= 1");
	pw.println("\tif length(argTimeSpan) > 0");
	pw.println("\t\t%");
	pw.println("\t\t% TimeSpan overridden by function arguments");
	pw.println("\t\t%");
	pw.println("\t\ttimeSpan = argTimeSpan;");
	pw.println("\tend");
	pw.println("end");

	
	pw.println("%");
	pw.println("% Default Initial Conditions");
	pw.println("%");
	pw.println("yinit = [");
	for (int j=0;j<volVars.length;j++){
		Expression initial = subDomain.getEquation(volVars[j]).getInitialExpression();
		double defaultInitialCondition = 0;
		try {
			initial.bindExpression(mathDesc);
			defaultInitialCondition = initial.evaluateConstant();
			pw.println("\t"+defaultInitialCondition+";\t\t% yinit("+(j+1)+") is the initial condition for '"+cbit.vcell.parser.SymbolUtils.getEscapedTokenMatlab(volVars[j].getName())+"'");
		} catch (ExpressionException e) {
			e.printStackTrace(System.out);
			pw.println("\t"+initial.infix_Matlab()+";\t\t% yinit("+(j+1)+") is the initial condition for '"+cbit.vcell.parser.SymbolUtils.getEscapedTokenMatlab(volVars[j].getName())+"'");
//			throw new RuntimeException("error evaluating initial condition for variable "+volVars[j].getName());
		}
	}
	pw.println("];");
	pw.println("if nargin >= 2");
	pw.println("\tif length(argYinit) > 0");
	pw.println("\t\t%");
	pw.println("\t\t% initial conditions overridden by function arguments");
	pw.println("\t\t%");
	pw.println("\t\tyinit = argYinit;");
	pw.println("\tend");
	pw.println("end");


	pw.println("%");
	pw.println("% Default Parameters");
	pw.println("%   constants are only those \"Constants\" from the Math Description that are just floating point numbers (no identifiers)");
	pw.println("%   note: constants of the form \"A_init\" are really initial conditions and are treated in \"yinit\"");
	pw.println("%");
	pw.println("param = [");
	int paramIndex = 0;
	for (int i = 0; i < constants.length; i++){
		pw.println("\t"+constants[i].getExpression().infix_Matlab()+";\t\t% param("+(paramIndex+1)+") is '"+cbit.vcell.parser.SymbolUtils.getEscapedTokenMatlab(constants[i].getName())+"'");
		paramIndex++;
	}
	pw.println("];");
	pw.println("if nargin >= 3");
	pw.println("\tif length(argParam) > 0");
	pw.println("\t\t%");
	pw.println("\t\t% parameter values overridden by function arguments");
	pw.println("\t\t%");
	pw.println("\t\tparam = argParam;");
	pw.println("\tend");
    pw.println("end");
    

	pw.println("%");
	pw.println("% invoke the integrator");
	pw.println("%");
	pw.println("[T,Y] = ode15s(@f,timeSpan,yinit,odeset('OutputFcn',@odeplot),param,yinit);");
	pw.println("");
	pw.println("% get the solution");
	pw.println("all = zeros(length(T), numVars);");
	pw.println("for i = 1:length(T)");
	pw.println("\tall(i,:) = getRow(T(i), Y(i,:), yinit, param);");
	pw.println("end");
	pw.println("");
	pw.println("allValues = all;");
	pw.println("end");
	
	// get row data for solution
	pw.println("");
	pw.println("% -------------------------------------------------------");
	pw.println("% get row data");
	pw.println("function rowValue = getRow(t,y,y0,p)");
	//
	// print volVariables (in order and assign to var vector)
	//
	pw.println("\t% State Variables");
	for (int i = 0; i < volVars.length; i++){
		pw.println("\t"+cbit.vcell.parser.SymbolUtils.getEscapedTokenMatlab(volVars[i].getName())+" = y("+(i+1)+");");
	}
	//
	// print constants
	//
	pw.println("\t% Constants");
	paramIndex = 0;
	for (int i = 0; i < constants.length; i++){
		pw.println("\t"+cbit.vcell.parser.SymbolUtils.getEscapedTokenMatlab(constants[i].getName())+" = p("+(paramIndex+1)+");");
		paramIndex++;
	}
	//
	// print variables
	//
	pw.println("\t% Functions");
	for (int i = 0; i < functions.length; i++){
		pw.println("\t"+cbit.vcell.parser.SymbolUtils.getEscapedTokenMatlab(functions[i].getName())+" = "+functions[i].getExpression().infix_Matlab()+";");
	}
	//
	// print OutputFunctions
	//
	pw.println("\t% OutputFunctions");
	if(outputFunctionContext != null && outputFunctionContext.getOutputFunctionsList() != null) {
		for (AnnotatedFunction annotatedFunction:outputFunctionContext.getOutputFunctionsList()){
			pw.println("\t"+cbit.vcell.parser.SymbolUtils.getEscapedTokenMatlab(annotatedFunction.getName())+" = "+annotatedFunction.getExpression().infix_Matlab()+";");	
		}
	}
	pw.println("");
	pw.println("\trowValue = [" + varNamesForValueArray + outputFunctionNamesForValueArray + "];");
	pw.println("end");
		
	//
	// print ode-rate
	//
	pw.println("");
	pw.println("% -------------------------------------------------------");
	pw.println("% ode rate");
	pw.println("function dydt = f(t,y,p,y0)");
	//
	// print volVariables (in order and assign to var vector)
	//
	pw.println("\t% State Variables");
	for (int i = 0; i < volVars.length; i++){
		pw.println("\t"+cbit.vcell.parser.SymbolUtils.getEscapedTokenMatlab(volVars[i].getName())+" = y("+(i+1)+");");
	}
	//
	// print constants
	//
	pw.println("\t% Constants");
	paramIndex = 0;
	for (int i = 0; i < constants.length; i++){
		pw.println("\t"+cbit.vcell.parser.SymbolUtils.getEscapedTokenMatlab(constants[i].getName())+" = p("+(paramIndex+1)+");");
		paramIndex++;
	}
	//
	// print variables
	//
	pw.println("\t% Functions");
	for (int i = 0; i < functions.length; i++){
		pw.println("\t"+cbit.vcell.parser.SymbolUtils.getEscapedTokenMatlab(functions[i].getName())+" = "+functions[i].getExpression().infix_Matlab()+";");
	}
	pw.println("\t% Rates");
	pw.println("\tdydt = [");
	for (int i=0;i<volVars.length;i++){
		pw.println("\t\t"+subDomain.getEquation(volVars[i]).getRateExpression().infix_Matlab()+";    % rate for "+cbit.vcell.parser.SymbolUtils.getEscapedTokenMatlab(volVars[i].getName()));
	}
	pw.println("\t];");
	pw.println("end");
}

}//end of class

