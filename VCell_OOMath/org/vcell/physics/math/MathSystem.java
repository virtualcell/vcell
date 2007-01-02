package org.vcell.physics.math;
import java.util.Hashtable;
import java.util.Vector;

import jscl.plugin.Expression;
import jscl.plugin.ParseException;
import jscl.plugin.Variable;

import org.vcell.physics.component.IndependentVariable;
import org.vcell.physics.component.Parameter;
import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Node;

/**
 * Insert the type's description here.
 * Creation date: (2/24/2002 9:12:36 AM)
 * @author: Jim Schaff
 */
//
// gather all equations (and associated symbols)
// if found time derivatives, look for initial conditions for that variable or explicit function for that variable
// then look for causality, algebraic loops, sorting, ...etc
//
// equation convensions:
//        all expressions are of the form f() = 0;
//
// .... find out how computer algebra systems keep track of boundary conditions (e.g. like initial conditions ... ... by equation or by symbol?)
//
// assigning causality of ?edges? (from "Object-Oriented Modeling of Hybrid Technical Systems", Jonas Eborn)
//    1. assign mandatory causality to sources.
//
//    2. propagate causality through junctions:
//        a. common effort (multiple devices in parallel ... same voltage).
//        b. common flow (multiple devices in series ... same current).
//        c. transformer: same causality in and out ()
//        d: gyrator (switched causality) ... effort-->flow and flow-->effort.
//
//    3. assign desirable causality for storage devices and return to (2).
//       repeat for other storage devices (e.g. capacitors ... want to integrate rather than differentiate).
//
//    4. remaining elements can be given arbitrary causality, choose one and propagate as in (2).
//       repeat until all elements have been assigned.
//
//
// 
public class MathSystem {
	private Hashtable<String,OOMathSymbol> symbolHash = new Hashtable<String,OOMathSymbol>();
	private Vector<Expression> equationList = new Vector<Expression>();


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public MathSystem(){
}

public void removeEquation(Expression exp){
	if (equationList.contains(exp)){
		equationList.remove(exp);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public void addEquation(Expression exp) {
	if (exp==null){
		throw new IllegalArgumentException("expression can't be null");
	}
	//if (symbol.getExpression() != null){
		//String tokens[] = symbol.getExpression().getSymbols();
		//for (int i = 0; tokens!=null && i < tokens.length; i++){
			//if (tokens[i].indexOf(".")>=0){
				//System.out.println("MathSystem warning: "+symbol.toString()+" symbol '"+tokens[i]+"' is scoped");
			//}
		//}
	//}
	if (equationList.contains(exp)){
		//
		// this is a very weak form of redundancy checking (only does a string check ... not really an equivalence measure unless truely cannonical)
		//
		throw new RuntimeException("expression \""+exp.infix()+"\" already in system");
	}else{
		//
		// add in an atomic way???? not right now.
		//
		equationList.add(exp);
		Variable[] variables = Expression.getVariables(exp);
		for (int i = 0; variables!=null && i < variables.length; i++){
			if (symbolHash.get(variables[i].getUndifferentiated().infix())==null){
				throw new RuntimeException("expression contains undefined symbol "+variables[i].infix());
			}
		}
	}
}



public void addSymbol(OOMathSymbol symbol){
	if (symbolHash.get(symbol.getName())!=null){
		throw new RuntimeException("symbol \""+symbol+"\"already defined");
	}else{
		symbolHash.put(symbol.getName(),symbol);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/12/2002 3:45:17 PM)
 * @return cbit.vcell.math.Variable[]
 * @param variables cbit.vcell.math.Variable[]
 */
public Graph getDependencyGraph(int baseWeight, boolean bIgnoreParameters) {

	//
	// make a bipartite graph of symbols pointing to equations
	//
	
	//
	// add the nodes for each "Symbol" (excluding "t" and derivatives, initial values)
	//
	Graph graph = new Graph();
	OOMathSymbol symbols[] = getSymbols();
	for (int i = 0; i < symbols.length; i++){
		if (!(symbols[i].getPhysicalSymbol() instanceof IndependentVariable) && 
			!(bIgnoreParameters && symbols[i].getPhysicalSymbol() instanceof Parameter)){
			Node symbolNode = new Node(symbols[i].getName(),symbols[i]);
			graph.addNode(symbolNode);
		}
	}

	//
	// add the nodes for each "Equation"
	//
	Expression[] equations = getEquations();
	for (int i = 0; i < equations.length; i++){
		Node equationNode = new Node(equations[i].infix(),equations[i]);
		graph.addNode(equationNode);
		//
		// add edge from each referenced symbol to this equation
		// add in any missing edges for VariableReferences (e.g. Derivative references)
		//
		Variable[] variables = Expression.getVariables(equations[i]);
		for (int j = 0; variables!=null && j < variables.length; j++){
			Node symbolNode = graph.getNode(variables[j].getUndifferentiated().infix());
			if (symbolNode == null){
				OOMathSymbol mathSymbol = symbolHash.get(variables[j].getUndifferentiated().infix());
				if (mathSymbol.getPhysicalSymbol() instanceof IndependentVariable || (bIgnoreParameters && mathSymbol.getPhysicalSymbol() instanceof Parameter)){
					continue;
				}else{
					throw new RuntimeException("equation "+equations[i].infix()+" references unknown symbol '"+mathSymbol.getName()+"'");
				}
			}else{
				int weight = baseWeight;
				try {
					weight += equations[i].getHighestTimeDerivative(variables[j].getUndifferentiated().infix());
				} catch (ParseException e) {
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());
				}
				Edge dependency = new Edge(symbolNode,equationNode,new Integer(weight));
				if (!graph.contains(dependency)){
					graph.addEdge(dependency);
				}
			}
		}
	}

	return graph;
}


/**
 * Insert the method's description here.
 * Creation date: (11/10/2005 10:34:22 AM)
 * @return jscl.plugin.Expression[]
 */
public Expression[] getEquations() {
	return equationList.toArray(new Expression[equationList.size()]);
}



/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public OOMathSymbol[] getSymbols(){
	Vector<OOMathSymbol> symbolList = new Vector<OOMathSymbol>();
	symbolList.addAll(symbolHash.values());
	OOMathSymbol symbols[] = new OOMathSymbol[symbolList.size()];
	symbolList.copyInto(symbols);
	return symbols;
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public void show() {
	OOMathSymbol symbols[] = getSymbols();
	for (int i = 0; i < symbols.length; i++){
		System.out.println(i+") "+symbols[i].getName());
	}
	Expression[] expressions = (Expression[])cbit.util.BeanUtils.getArray(equationList,Expression.class);
	for (int i = 0; i < expressions.length; i++){
		System.out.println(i+") "+expressions[i].infix()+" == 0.0");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:20:38 AM)
 * @return java.lang.String
 */
public String toString() {
	return "MathSystem2: numSymbols="+symbolHash.size()+", numEquations="+equationList.size();
}
}