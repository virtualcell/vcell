/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Node;
/**
 * Insert the type's description here.
 * Creation date: (2/24/2002 9:12:36 AM)
 * @author: Jim Schaff
 */
//
// temporarily place all variables in a hashtable (before binding) and discarding duplicates (check for equality)
//
public class VariableHash {
	private Vector<Variable> variableList = new Vector<Variable>();
	private Hashtable<String, Variable> hash = new Hashtable<String, Variable>();

	private class UnresolvedException extends RuntimeException {
		private String name = null;
		UnresolvedException(String argName, String message){
			super(message);
			name = argName;
		}
	}

/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public VariableHash(){
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public void addVariable(Variable var) throws MathException {
	if (var==null){
		throw new IllegalArgumentException("var can't be null");
	}
//if (var.getName().indexOf(".")>=0){
	//System.out.println(var.toString()+" found");
//}
	//if (var.getName().indexOf(".")>=0){
		//System.out.println(var.toString()+" name is scoped");
	//}
	//if (var.getExpression() != null){
		//String tokens[] = var.getExpression().getSymbols();
		//for (int i = 0; tokens!=null && i < tokens.length; i++){
			//if (tokens[i].indexOf(".")>=0){
				//System.out.println("MathDescription warning: "+var.toString()+" symbol '"+tokens[i]+"' is scoped");
			//}
		//}
	//}
	Variable existingVar = (Variable)hash.get(var.getName());
	if (existingVar!=null){
		//
		// if var already exists, check that it is "compareEqual()"
		//
		if (!var.compareEqual(existingVar)){
			throw new MathException("new variable ("+var.toString()+") conflicts with existing var ("+existingVar.toString());
		}
	}else{
		hash.put(var.getName(),var);
		if (!(var instanceof InsideVariable) && !(var instanceof OutsideVariable)) {
			variableList.add(var);
		}
		if (var instanceof cbit.vcell.math.VolVariable || var instanceof VolumeRegionVariable){
			//
			// for Volume Variables, also create an InsideVariable and an OutsideVariable for use in JumpConditions
			//
			InsideVariable inVar = new cbit.vcell.math.InsideVariable(var.getName()+InsideVariable.INSIDE_VARIABLE_SUFFIX, var.getName());
			addVariable(inVar);
			OutsideVariable outVar = new cbit.vcell.math.OutsideVariable(var.getName()+OutsideVariable.OUTSIDE_VARIABLE_SUFFIX, var.getName());
			addVariable(outVar);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public String getFirstUnresolvedSymbol(){
	try {
		reorderVariables(getVariables());
		return null;
	}catch (VariableHash.UnresolvedException e){
		return e.name;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
//public Variable[] getReorderedVariables(){
//	return getTopologicallyReorderedVariables();
//}

public Variable[] getTopologicallyReorderedVariables(){
	Vector<Variable> vlist = new Vector<Variable>();
	vlist.addAll(hash.values());
	Variable[] vars = new Variable[vlist.size()];
	vlist.copyInto(vars);	
	return reorderVariables(vars);
}


public Variable[] getAlphabeticallyOrderedVariables() {
	Comparator<Variable> comparator = new java.util.Comparator<Variable>() {
		public int compare(Variable obj1, Variable obj2){
			return obj1.getName().compareToIgnoreCase(obj2.getName());
		}
		public boolean equals(Object obj){
			return this==obj;
		}
	};
	
	List<Variable> pvList = new ArrayList<Variable>();
	List<Variable> constList = new ArrayList<Variable>();
	List<Variable> pspList = new ArrayList<Variable>();
	List<Variable> poList = new ArrayList<Variable>();
	List<Variable> varList = new ArrayList<Variable>();
	List<Variable> funcList = new ArrayList<Variable>();
	
	for (Variable var : variableList) {
		if (var instanceof ParameterVariable){
			pvList.add(var);
		} else if (var instanceof Constant) {
			constList.add(var);
		} else if (var instanceof ParticleSpeciesPattern) {
			pspList.add(var);
		} else if (var instanceof ParticleObservable) {
			poList.add(var);
		} else if (var instanceof Function) {
			funcList.add(var);
		} else {
			varList.add(var);
		}
	}
	Collections.sort(pvList, comparator);
	Collections.sort(constList, comparator);
	Collections.sort(pspList, comparator);
	Collections.sort(poList, comparator);
	Collections.sort(varList, comparator);
	Collections.sort(funcList, comparator);	
	Vector<Variable> orderedVars = new Vector<Variable>();
	orderedVars.addAll(pvList);
	orderedVars.addAll(constList);
	orderedVars.addAll(pspList);
	orderedVars.addAll(poList);
	orderedVars.addAll(varList);
	orderedVars.addAll(funcList);
	return orderedVars.toArray(new Variable[orderedVars.size()]);
}
/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public Variable getVariable(String varName){
	return (Variable)hash.get(varName);
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
Variable[] getVariables(){
//	Vector varList = new Vector();
//	varList.addAll(hash.values());
	Variable variables[] = new Variable[variableList.size()];
	variableList.copyInto(variables);
	return variables;
}


	public void removeVariable(Variable var) {

		if (var == null) {
			throw new IllegalArgumentException("Invalid argument: null Variable");
		}
		hash.remove(var.getName());
		variableList.remove(var);
	}


	public void removeVariable(String variableName) {

		if (variableName == null || variableName.length() == 0) {
			throw new IllegalArgumentException("Invalid variable name: " + variableName);
		}
		variableList.remove(getVariable(variableName));
		hash.remove(variableName);		
	}


/**
 * Insert the method's description here.
 * Creation date: (4/12/2002 3:45:17 PM)
 * @return cbit.vcell.math.Variable[]
 * @param variables cbit.vcell.math.Variable[]
 */
private Variable[] reorderVariables(Variable[] variables) throws VariableHash.UnresolvedException {
	
	//
	// add the nodes for each "Variable" (VolVariable, etc...) ... no dependencies
	//
	Graph variableGraph = new Graph();
	Graph constantGraph = new Graph();
	Graph functionGraph = new Graph();
	for (int i = 0; i < variables.length; i++){
		Node node = new Node(variables[i].getName(),variables[i]);
		if (variables[i] instanceof Constant) {
			constantGraph.addNode(node);
		} else if (variables[i] instanceof Function) {
			functionGraph.addNode(node);
		} else {			
			variableGraph.addNode(node);			
		}
	}

	//
	// add an edge for each dependency of a Constant
	//
	Node constantNodes[] = constantGraph.getNodes();
	for (int i = 0; i < constantNodes.length; i++){
		Constant constant = (Constant)constantNodes[i].getData();
		String symbols[] = constant.getExpression().getSymbols();
		if (symbols != null) {
			for (int j = 0; j < symbols.length; j++){
				//
				// find node that this symbol references (it better find this in the Constants).
				//
				Node symbolNode = constantGraph.getNode(symbols[j]);
				if (symbolNode == null){
					if (functionGraph.getNode(symbols[j])!=null){
						throw new RuntimeException("Constant "+constant.getName()+" references function '"+symbols[j]+"'");
					}else if (variableGraph.getNode(symbols[j])!=null){
						throw new RuntimeException("Constant "+constant.getName()+" references variable '"+symbols[j]+"'");
					}else{
						throw new RuntimeException("Constant "+constant.getName()+" references unknown symbol '"+symbols[j]+"'");
					}
				} 
				Edge dependency = new Edge(constantNodes[i],symbolNode,constant.getName()+"->"+((Variable)symbolNode.getData()).getName());
				constantGraph.addEdge(dependency);
			}
		}
	}
	Node sortedConstantNodes[] = constantGraph.topologicalSort();
	
	//
	// add an edge for each dependency of a Function
	//
	Node functionNodes[] = functionGraph.getNodes();
	for (int i = 0; i < functionNodes.length; i++){
		Function function = (Function)functionNodes[i].getData();
		String symbols[] = function.getExpression().getSymbols();
		if (symbols != null) {
			for (int j = 0; j < symbols.length; j++){
				//
				// find node that this symbol references (better find it in FunctionGraph, ConstantGraph, or VariableGraph).
				//
				Node symbolNode = functionGraph.getNode(symbols[j]);
				if (symbolNode!=null){
					Edge dependency = new Edge(functionNodes[i],symbolNode,function.getName()+"->"+((Variable)symbolNode.getData()).getName());
					functionGraph.addEdge(dependency);
				}else if (ReservedMathSymbolEntries.getReservedVariableEntry(symbols[j])==null && constantGraph.getNode(symbols[j])==null && variableGraph.getNode(symbols[j])==null){
					throw new VariableHash.UnresolvedException(symbols[j],"Unable to sort, unknown identifier "+symbols[j]);
				}
			}
		}
	}
	Node sortedFunctionNodes[] = functionGraph.topologicalSort();

	//
	// reassemble in the proper order
	//		Constants
	//		Variables
	//		Functions
	//
	Vector newVariableOrder = new Vector();
	for (int i = 0; i < sortedConstantNodes.length; i++){
		newVariableOrder.add(sortedConstantNodes[i].getData());
	}
	
	Node variableNodes[] = variableGraph.getNodes();
	for (int i = 0; i < variableNodes.length; i++){
		Object var = variableNodes[i].getData();
		if (!(var instanceof InsideVariable) && !(var instanceof OutsideVariable)){
			newVariableOrder.add(var);
		}
	}
	for (int i = 0; i < sortedFunctionNodes.length; i++){
		newVariableOrder.add(sortedFunctionNodes[i].getData());
	}

	Variable sortedVars[] = new Variable[newVariableOrder.size()];
	newVariableOrder.copyInto(sortedVars);
	
	return sortedVars;
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public void show() {
	Variable vars[] = getVariables();
	for (int i = 0; i < vars.length; i++){
		System.out.println(i+") "+vars[i].toString());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:20:38 AM)
 * @return java.lang.String
 */
public String toString() {
	return "VariableHash: numEntries="+hash.size();
}
}
