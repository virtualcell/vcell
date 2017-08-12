/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modelopt;
import java.util.Hashtable;
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
public class MathSystemHash {
	private Hashtable hash = new Hashtable();

	private class UnresolvedException extends RuntimeException {
		private String name = null;
		UnresolvedException(String argName, String message){
			super(message);
			name = argName;
		}
	}

	public static abstract class Symbol implements cbit.vcell.parser.SymbolTableEntry {
		String name = null;
		cbit.vcell.parser.Expression expression = null;
		
		public Symbol(String argName, cbit.vcell.parser.Expression argExpression){
			this.name = argName;
			this.expression = argExpression;
		}
		public String getName() {
			return name;
		}
		public int getIndex() {
			throw new RuntimeException("shouldn't evaluate these equations");
		}
		public boolean isConstant(){
			if (expression == null){
				return false;
			}else{
				try {
					expression.evaluateConstant();
					return true;
				}catch (cbit.vcell.parser.ExpressionException e){
					return false;
				}
			}
		}
		public cbit.vcell.parser.NameScope getNameScope() {
			return null;
		}
		public cbit.vcell.units.VCUnitDefinition getUnitDefinition() {
			return null;
		}
		public double getConstantValue() throws cbit.vcell.parser.ExpressionException {
			if (expression == null){
				throw new RuntimeException("no value");
			}else{
				return expression.evaluateConstant();
			}
		}
		public cbit.vcell.parser.Expression getExpression() {
			return expression;
		}
	};
	
	public static class Variable extends Symbol {
		public Variable(String argName, cbit.vcell.parser.Expression exp){
			super(argName,exp);
		}
	};

	public static class IndependentVariable extends Symbol {
		public IndependentVariable(String argName){
			super(argName,null);
		}
	};

	public static abstract class VariableReference extends Symbol {
		Variable variable = null;
		protected VariableReference(Variable argVariable, String argName, cbit.vcell.parser.Expression exp){
			super(argName, exp);
			variable = argVariable;
		} 
	}
	
	public static class VariableDerivative extends VariableReference {
		public VariableDerivative(Variable argVariable, cbit.vcell.parser.Expression exp){
			super(argVariable, argVariable.getName()+"___d_dt", exp);
		} 
	}
	
	public static class VariableInitial extends VariableReference {
		public VariableInitial(Variable argVariable, cbit.vcell.parser.Expression exp){
			super(argVariable, argVariable.getName()+"___at_t0", exp);
		} 
	}

/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public MathSystemHash(){
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public void addSymbol(Symbol symbol) {
	if (symbol==null){
		throw new IllegalArgumentException("symbol can't be null");
	}
	//if (symbol.getExpression() != null){
		//String tokens[] = symbol.getExpression().getSymbols();
		//for (int i = 0; tokens!=null && i < tokens.length; i++){
			//if (tokens[i].indexOf(".")>=0){
				//System.out.println("MathSystem warning: "+symbol.toString()+" symbol '"+tokens[i]+"' is scoped");
			//}
		//}
	//}
	Symbol existingSymbol = (Symbol)hash.get(symbol.getName());
	if (existingSymbol!=null){
		//
		// if symbol already exists, assume that it's consistent, return;
		//
		return;
	}else{
		hash.put(symbol.getName(),symbol);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/12/2002 3:45:17 PM)
 * @return cbit.vcell.math.Variable[]
 * @param variables cbit.vcell.math.Variable[]
 */
public Graph getDependencyGraph(Symbol[] symbols) throws MathSystemHash.UnresolvedException {
	
	//
	// add the nodes for each "Symbol"
	//
	Graph graph = new Graph();
	for (int i = 0; i < symbols.length; i++){
		Node node = new Node(symbols[i].getName(),symbols[i]);
		graph.addNode(node);
	}

	//
	// add an edge for each dependency of a Symbol (due to it's associated expression implied equation ... e.g. Rate, IC).
	//
	Node nodes[] = graph.getNodes();
	for (int i = 0; i < nodes.length; i++){
		Symbol symbol = (Symbol)nodes[i].getData();
		if (symbol.getExpression()!=null){
			String symbolNames[] = symbol.getExpression().getSymbols();
			for (int j = 0; symbolNames!=null && j < symbolNames.length; j++){
				//
				// find node that this symbol references
				//
				Node symbolNode = graph.getNode(symbolNames[j]);
				if (symbolNode == null){
					throw new RuntimeException("symbol "+symbol.getName()+" references unknown symbol '"+symbolNames[j]+"'");
				} 
				Edge dependency = new Edge(nodes[i],symbolNode,symbol.getName()+"->"+((Symbol)symbolNode.getData()).getName());
				graph.addEdge(dependency);
			}
		}
		if (symbol instanceof VariableReference){
			VariableReference varRef = (VariableReference)symbol;
			Node referencingVarNode = graph.getNode(varRef.variable.getName());
			if (referencingVarNode == null){
				throw new RuntimeException("symbol "+symbol.getName()+" references unknown variable '"+varRef.variable.getName()+"'");
			} 
			Edge dependency = new Edge(referencingVarNode,nodes[i],varRef.variable.getName()+"->"+symbol.getName());
			graph.addEdge(dependency);
		}
	}

	return graph;
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public Symbol getSymbol(String symbolName){
	return (Symbol)hash.get(symbolName);
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public Symbol[] getSymbols(){
	Vector symbolList = new Vector();
	symbolList.addAll(hash.values());
	Symbol symbols[] = new Symbol[symbolList.size()];
	symbolList.copyInto(symbols);
	return symbols;
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public void show() {
	Symbol symbols[] = getSymbols();
	for (int i = 0; i < symbols.length; i++){
		System.out.println(i+") "+symbols[i].toString());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:20:38 AM)
 * @return java.lang.String
 */
public String toString() {
	return "MathSystemHash: numEntries="+hash.size();
}
}
