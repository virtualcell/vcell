package org.vcell.physics.math;
import java.util.Hashtable;
import java.util.Vector;

import org.vcell.physics.component.IndependentVariable;
import org.vcell.physics.component.Symbol;
import org.vcell.physics.component.Variable;
import org.vcell.physics.component.VariableDerivative;
import org.vcell.physics.component.VariableReference;

import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Node;
import cbit.vcell.parser.Expression;

import com.mhhe.clrs2e.Vertex;
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
// Symbol Convensions:
//        XXX.initial is the name of the initial conditions for variable XXX (e.g. XXX at time = 0)
//        XXX.prime is the name of the full (not partial) time derivative of variable XXX (e.g. d(XXX)/dt)
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
	public final static String INITIAL_SUFFIX = ".initial";
	public final static String DERIVATIVE_SUFFIX = ".prime"; 
	
	private Hashtable symbolHash = new Hashtable();
	private Vector equationList = new Vector();


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public MathSystem(){
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
		String symbols[] = exp.getSymbols();
		for (int i = 0; symbols!=null && i < symbols.length; i++){
			addSymbol(symbols[i]);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public void addSymbol(String symbolName) {
	if (symbolName==null){
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
	Symbol existingSymbol = (Symbol)symbolHash.get(symbolName);
	if (existingSymbol!=null){
		//
		// if symbol already exists, assume that it's consistent, return;
		//
		return;
	}else{
		if (symbolName.equals("t") || symbolName.equals("x") || symbolName.equals("y") || symbolName.equals("z")){
			symbolHash.put(symbolName,new IndependentVariable(symbolName));
		}else if (symbolName.endsWith(INITIAL_SUFFIX)){
			String varName = cbit.util.TokenMangler.replaceSubString(symbolName,INITIAL_SUFFIX,"");
			addSymbol(varName);
			//Variable var = (Variable)getSymbol(varName);
			//symbolHash.put(symbolName,new VariableInitial(var,symbolName));
		}else if (symbolName.endsWith(DERIVATIVE_SUFFIX)){
			String varName = cbit.util.TokenMangler.replaceSubString(symbolName,DERIVATIVE_SUFFIX,"");
			addSymbol(varName);
			Variable var = (Variable)getSymbol(varName);
			symbolHash.put(symbolName,new VariableDerivative(var,symbolName));
		}else{
			symbolHash.put(symbolName,new Variable(symbolName));
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/12/2002 3:45:17 PM)
 * @return cbit.vcell.math.Variable[]
 * @param variables cbit.vcell.math.Variable[]
 */
public Graph getDependencyGraph() {

	//
	// make a bipartite graph of symbols pointing to equations
	//
	
	//
	// add the nodes for each "Symbol" (excluding "t" and derivatives, initial values)
	//
	Graph graph = new Graph();
	Symbol symbols[] = getSymbols();
	for (int i = 0; i < symbols.length; i++){
		if (!(symbols[i] instanceof IndependentVariable) && !(symbols[i] instanceof VariableReference)){
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
		String[] refSymbols = equations[i].getSymbols();
		for (int j = 0; refSymbols!=null && j < refSymbols.length; j++){
			Node symbolNode = graph.getNode(refSymbols[j]);
			double weight = 100;
			if (symbolNode == null){
				Symbol refSymbol = getSymbol(refSymbols[j]);
				if (refSymbol instanceof IndependentVariable){
					continue;
				}else if (refSymbol instanceof VariableReference){
					symbolNode = graph.getNode(((VariableReference)refSymbol).getVariable().getName());
					if (refSymbol instanceof VariableDerivative){
						weight = 101;
					}
				}else{
					throw new RuntimeException("equation "+equations[i].infix()+" references unknown symbol '"+refSymbols[j]+"'");
				}
			}
			Edge dependency = new Edge(symbolNode,equationNode,new Double(weight));
			if (!graph.contains(dependency)){
				graph.addEdge(dependency);
			}
		}
	}

	return graph;
}


/**
 * Insert the method's description here.
 * Creation date: (11/10/2005 10:34:22 AM)
 * @return cbit.vcell.parser.Expression[]
 */
public Expression[] getEquations() {
	return (Expression[])cbit.util.BeanUtils.getArray(equationList,Expression.class);
}


/**
 * Insert the method's description here.
 * Creation date: (1/3/2006 11:35:55 AM)
 * @return com.mhhe.clrs2e.FlowNetwork
 */
public com.mhhe.clrs2e.FlowNetwork getFlowNetwork() {
	Graph dependencyGraph = getDependencyGraph();
	//
	// copy exiting edges and vertices, and add vertex from start or to end
	//
	com.mhhe.clrs2e.FlowNetwork flowNetwork = new com.mhhe.clrs2e.FlowNetwork(dependencyGraph.getNumNodes()+2);
	Vertex startVertex = flowNetwork.addVertex("flowStart");
	Vertex endVertex = flowNetwork.addVertex("flowEnd");
	Hashtable hash = new Hashtable();
	Node[] nodes = dependencyGraph.getNodes();
	Edge[] edges = dependencyGraph.getEdges();
	//
	// add graph nodes and connect to start and end vertices.
	//
	for (int i = 0; i < nodes.length; i++){
		Vertex newVertex = flowNetwork.addVertex(nodes[i].getName());
		hash.put(nodes[i],newVertex);
		if (nodes[i].getData() instanceof Symbol){
			flowNetwork.addEdge(startVertex,newVertex,1,0);
		}else if (nodes[i].getData() instanceof Expression){
			Expression exp = (Expression)nodes[i].getData();
			if (exp.infix().indexOf(VariableReference.DERIVATIVE_SUFFIX)>-1){
				flowNetwork.addEdge(newVertex,endVertex,1,0);
			}else{
				flowNetwork.addEdge(newVertex,endVertex,1,0);
			}
		}
	}
	//
	// add existing edges from graph into flowNetwork
	//
	for (int i = 0; i < edges.length; i++){
		Vertex vertex1 = (Vertex)hash.get(edges[i].getNode1());
		Vertex vertex2 = (Vertex)hash.get(edges[i].getNode2());
		double capacity = 1.0;
		//
		// if equation has derivative of this variable in expression then capacity is increased to 2 (similar to Structural Index reduction techniques).
		//
		Symbol symbol = (Symbol)edges[i].getNode1().getData();
		Expression exp = (Expression)edges[i].getNode2().getData();
		String expSymbols[] = exp.getSymbols();
		for (int j = 0; j < expSymbols.length; j++){
			if (expSymbols[j].equals(symbol.getName()+VariableDerivative.DERIVATIVE_SUFFIX)){
				capacity = 1.0;
			}
		}
		
		flowNetwork.addEdge(vertex1,vertex2,capacity,0);
	}
	
	return flowNetwork;
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public Symbol getSymbol(String symbolName){
	return (Symbol)symbolHash.get(symbolName);
}


/**
 * Insert the method's description here.
 * Creation date: (2/24/2002 9:18:38 AM)
 */
public Symbol[] getSymbols(){
	Vector symbolList = new Vector();
	symbolList.addAll(symbolHash.values());
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