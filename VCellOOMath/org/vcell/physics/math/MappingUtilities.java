package org.vcell.physics.math;
import java.beans.PropertyVetoException;

import org.vcell.expression.ExpressionBindingException;
import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.ExpressionUtilities;
import org.vcell.expression.IExpression;
import org.vcell.expression.ISymbolicProcessor;
import org.vcell.physics.component.Connection;
import org.vcell.physics.component.Connector;
import org.vcell.physics.component.ModelComponent;
import org.vcell.physics.component.OOModel;
import org.vcell.physics.component.Symbol;

import cbit.vcell.math.MathException;

/**
 * Insert the type's description here.
 * Creation date: (1/12/2004 1:35:08 AM)
 * @author: Jim Schaff
 */
public class MappingUtilities {
/**
 * Insert the method's description here.
 * Creation date: (1/28/2006 10:49:36 AM)
 * @return cbit.vcell.parser.Expression
 * @param sccArray ncbc.physics2.component.StronglyConnectedComponent[]
 * @param partitionGraph cbit.util.graph.Graph
 * @param symbolToTear ncbc.physics2.component.Symbol
 * @throws MathException 
 * @throws ExpressionBindingException 
 */

public static cbit.vcell.math.MathDescription getMathDescription(VarEquationAssignment[] varEqnAssign) throws PropertyVetoException, ExpressionBindingException, MathException {
	cbit.vcell.math.MathDescription mathDesc = new cbit.vcell.math.MathDescription("generated");
	mathDesc.setGeometry(new cbit.vcell.geometry.Geometry("newgeometry",0));
	cbit.vcell.math.CompartmentSubDomain compartmentSubDomain = new cbit.vcell.math.CompartmentSubDomain("compartment",0);
	mathDesc.addSubDomain(compartmentSubDomain);
	//
	// first add variables, then add equations
	//
	cbit.vcell.math.Variable[] varArray = new cbit.vcell.math.Variable[varEqnAssign.length];
	for (int i = 0; i < varEqnAssign.length; i++) {
		if (varEqnAssign[i].getSolution()==null){
			throw new RuntimeException("system not completely solved, cannot generation math ... could generate DAE at some point");
		}
		if (varEqnAssign[i].isStateVariable()){
			cbit.vcell.math.VolVariable volVariable = new cbit.vcell.math.VolVariable(varEqnAssign[i].getSymbol().getName());
			varArray[i] = volVariable;
		}else{
			//
			// if solution has no symbols, make this a constant
			//
			if (varEqnAssign[i].getSolution().getSymbols()==null || varEqnAssign[i].getSolution().getSymbols().length==0){
				cbit.vcell.math.Constant constant = new cbit.vcell.math.Constant(varEqnAssign[i].getSymbol().getName(),ExpressionFactory.createExpression(varEqnAssign[i].getSolution()));
				varArray[i] = constant;
			//
			// if it has a symbol, then make it a function
			//
			}else{
				cbit.vcell.math.Function function = new cbit.vcell.math.Function(varEqnAssign[i].getSymbol().getName(),ExpressionFactory.createExpression(varEqnAssign[i].getSolution()));
				varArray[i] = function;
			}
		}
	}
	mathDesc.setAllVariables(varArray);
	//
	// now add equations
	//
	for (int i = 0; i < varEqnAssign.length; i++) {
		if (varEqnAssign[i].isStateVariable() && varEqnAssign[i].getSolution()!=null){
			cbit.vcell.math.OdeEquation odeEquation = new cbit.vcell.math.OdeEquation(varArray[i],ExpressionFactory.createExpression(0.0),ExpressionFactory.createExpression(varEqnAssign[i].getSolution()));
			compartmentSubDomain.addEquation(odeEquation);
		}
	}
	return mathDesc;
}
	
	
public static void breakSCC(StronglyConnectedComponent[] sccArray, cbit.util.graph.Graph partitionGraph, StronglyConnectedComponent sccToBreak) {
	//
	// get all of the VarEquationAssignments 
	//
//	 TODO: implement this

	//
	// choose a variable to tear ... (select variable with largest degree first)
	//
	
	
	//
	// find out which strongly connected component this symbol belongs to
	//
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:47:03 PM)
 * @return ncbc.physics2.MathSystem2
 * @param oOModel ncbc.physics2.component.Model
 */
public static org.vcell.physics.math.MathSystem getMathSystem(OOModel oOModel) throws org.vcell.expression.ExpressionException {
	org.vcell.physics.math.MathSystem mathSystem = new org.vcell.physics.math.MathSystem();

	//
	// for each model component, add symbols and equations
	//

	//
	// for each connection, add equations to couple variables
	//
	// effort variables are made equal, flow variables sum to zero
	//
	ModelComponent[] modelComponents = oOModel.getModelComponents();
	Connection[] connections = oOModel.getConnections();
	for (int i = 0; i < modelComponents.length; i++){
		Symbol[] symbols = modelComponents[i].getSymbols();
		for (int j = 0; j < symbols.length; j++){
			mathSystem.addSymbol(modelComponents[i].getName()+"."+symbols[j].getName());
		}
		IExpression[] equations = modelComponents[i].getEquations();
		for (int j = 0; j < equations.length; j++){
			String syms[] = equations[j].getSymbols();
			IExpression exp = ExpressionFactory.createExpression(equations[j]);
			for (int k = 0; k < syms.length; k++){
				exp.substituteInPlace(ExpressionFactory.createExpression(syms[k]),ExpressionFactory.createExpression(modelComponents[i].getName()+"."+syms[k]));
			}
			mathSystem.addEquation(exp);
		}
	}
	for (int i = 0; i < connections.length; i++){
		//
		// for connections, just add coupling equations, variables should already be there
		//
		StringBuffer effortBuffer = new StringBuffer();
		StringBuffer flowBuffer = new StringBuffer();
		Connector[] connectors = connections[i].getConnectors();
		for (int j = 0; j < connectors.length; j++){
			ModelComponent parent = connectors[j].getParent();
			if (j==0){
				effortBuffer.append(parent.getName()+"."+connectors[j].getEffortVariable().getName());
				if (connectors[j].getFlowVariable()!=null){
					flowBuffer.append(parent.getName()+"."+connectors[j].getFlowVariable().getName());
				}
			}else{
				//
				// need n-1 equations to indicate that all efforts are equal
				//
				mathSystem.addEquation(ExpressionFactory.createExpression(effortBuffer.toString()+"-"+parent.getName()+"."+connectors[j].getEffortVariable().getName()));
				//
				// sum up mass flux
				//
				if (connectors[j].getFlowVariable()!=null){
					flowBuffer.append("+"+parent.getName()+"."+connectors[j].getFlowVariable().getName());
				}
			}
		}
		if (flowBuffer.length()>0){
			mathSystem.addEquation(ExpressionFactory.createExpression(flowBuffer.toString())); // one flow equation (conservation of mass)
		}

	}
	return mathSystem;
}


/**
 * Starts the application.
 * @param args an array of command-line arguments
 * @throws ExpressionException 
 */
public static ModelAnalysisResults analyzeMathSystem(MathSystem mathSystem) throws ExpressionException{

	ModelAnalysisResults modelAnalysisResults = new ModelAnalysisResults();
	
	cbit.util.graph.Graph graph = mathSystem.getDependencyGraph();

	int[] colors = new int[graph.getNumNodes()];
	cbit.util.graph.Node[] nodes = graph.getNodes();
	for (int i = 0; i < nodes.length; i++){
		nodes[i].index = i;
		if (nodes[i].getData() instanceof Symbol){
			colors[i] = 0;
		}else{
			colors[i] = 1;
		}
	}

	com.mhhe.clrs2e.WeightedAdjacencyListGraph weightedGraph = new com.mhhe.clrs2e.WeightedAdjacencyListGraph(nodes.length,false);	
	for (int i = 0; i < nodes.length; i++){
		weightedGraph.addVertex(nodes[i].getName());
	}
	final cbit.util.graph.Edge[] edges = graph.getEdges();
	for (int i = 0; i < edges.length; i++){
		weightedGraph.addEdge(edges[i].getNode1().index,edges[i].getNode2().index,((Double)edges[i].getData()).doubleValue());
	}
	
	final org.vcell.physics.math.BipartiteMatchings.Matching matching = org.vcell.physics.math.BipartiteMatchings.findMaximumWeightedMaximumCardinalityMatching(weightedGraph, colors);


	int coveredVertices = matching.countCoveredVertices();

	if (coveredVertices == nodes.length){
			
		cbit.util.graph.Graph partitionGraph = new cbit.util.graph.Graph();
		ISymbolicProcessor symbolicProcessor = ExpressionUtilities.getDefaultSymbolicProcessor();
		//
		// build vertices make a directed graph with vertices (Var | equation)
		//
		for (int i = 0; i < nodes.length; i++){
			if (nodes[i].getData() instanceof Symbol){
				Symbol symbol = (Symbol)nodes[i].getData();
				int matchingIndex = matching.getMatch(nodes[i].index);
				cbit.util.graph.Node eqnNode = graph.getNodes()[matchingIndex];
				IExpression equation = (IExpression)eqnNode.getData();
				VarEquationAssignment varEqnAssignment = new VarEquationAssignment(symbol,equation);
				String[] expSymbols = equation.getSymbols();
				for (int j = 0; j < expSymbols.length; j++){
					if (expSymbols[j].equals(symbol.getName()+Symbol.DERIVATIVE_SUFFIX)){
						//
						// matching a variable to an ODE with this variable, variable is a state variable
						//
						varEqnAssignment.setStateVariable(true);
					}
				}
				//
				// solve for this variable if possible.
				//
				String symbolToSolveFor = symbol.getName();
				if (varEqnAssignment.isStateVariable()){
					symbolToSolveFor = symbol.getName()+Symbol.DERIVATIVE_SUFFIX;
				}
				IExpression solvedExp = symbolicProcessor.solve(equation,symbolToSolveFor);
				varEqnAssignment.setSolution(solvedExp); // may be null;
					
				cbit.util.graph.Node mergedNode = new cbit.util.graph.Node(varEqnAssignment.toString(),varEqnAssignment);
				partitionGraph.addNode(mergedNode);
			}
		}
		cbit.util.graph.Node[] partitionNodes = partitionGraph.getNodes();
		
		//
		// add edges to each node from other variables used in this equation
		// add edges from each node for other equations that need this variable
		//
		for (int i = 0; i < partitionNodes.length; i++){
			partitionNodes[i].index = i;
		}
		for (int i = 0; i < partitionNodes.length; i++){
			VarEquationAssignment varEqnAssignment = (VarEquationAssignment)partitionNodes[i].getData();
			if (!varEqnAssignment.isStateVariable()){
				//
				// state variables are considered to be "known" at all times, so don't consider them dependencies
				//
				com.mhhe.clrs2e.AdjacencyListGraph.EdgeIterator edgeIterator = (com.mhhe.clrs2e.AdjacencyListGraph.EdgeIterator)weightedGraph.edgeIterator(i);
				while (edgeIterator.hasNext()){
					com.mhhe.clrs2e.Vertex otherVertex = (com.mhhe.clrs2e.Vertex)edgeIterator.next();
					if (!matching.contains(i,otherVertex.getIndex())){
						//
						// the current symbol references this equation, now it should reference its "matched" symbol
						//
						int otherSymbolIndex = matching.getMatch(otherVertex.getIndex());
						partitionGraph.addEdge(new cbit.util.graph.Edge(partitionNodes[i],partitionNodes[otherSymbolIndex]));
					}
				}
			}
		}
		System.out.println(partitionGraph.toString());

		//
		// get convenient list of VarEquationAssignments
		//
		VarEquationAssignment[] varEqnAssignments = new VarEquationAssignment[partitionGraph.getNumNodes()];
		for (int i = 0; i < partitionNodes.length; i++){
			varEqnAssignments[i] = (VarEquationAssignment)partitionNodes[i].getData();
		}

		//
		// do BLT partitioning ... via tarjan's algorithm ... 
		//
		cbit.util.graph.TarjansAlgorithm tarjanAlgorithm = new cbit.util.graph.TarjansAlgorithm(partitionGraph);
		int[] roots = tarjanAlgorithm.getRoots(); // roots of strongly connected graph for each vertex.

		java.util.Hashtable hash = new java.util.Hashtable();
		for (int i = 0; i < roots.length; i++){
			StronglyConnectedComponent scc = (StronglyConnectedComponent)hash.get(new Integer(roots[i]));
			if (scc == null){
				scc = new StronglyConnectedComponent("scc_"+roots[i]); // named by its root
				hash.put(new Integer(roots[i]),scc);
			}
			scc.addVertex(i);
		}
		StronglyConnectedComponent sccArray[] = new StronglyConnectedComponent[hash.size()];
		hash.values().toArray(sccArray);

		//
		// build graph with strongly connected components as vertices and edges representing any connectivity from scc_i to scc_j
		//
		cbit.util.graph.Graph sccGraph = new cbit.util.graph.Graph();
		for (int i = 0; i < sccArray.length; i++){
			sccGraph.addNode(new cbit.util.graph.Node(sccArray[i].toString(varEqnAssignments),sccArray[i]));
		}
		cbit.util.graph.Node[] sccNodes = sccGraph.getNodes();
		cbit.util.graph.Edge partitionEdges[] = partitionGraph.getEdges();
		for (int i = 0; i < partitionEdges.length; i++){
			int index1 = partitionEdges[i].getNode1().index;
			int index2 = partitionEdges[i].getNode2().index;
			int sccIndex1 = -1;
			int sccIndex2 = -1;
			for (int j = 0; j < sccArray.length; j++){
				if (sccArray[j].contains(roots[index1])){
					sccIndex1 = j;
				}
				if (sccArray[j].contains(roots[index2])){
					sccIndex2 = j;
				}
			}
			if (sccIndex1 != sccIndex2){
				//
				// edge spans components
				//
				// redundant edges are ok for topological sorting and dependency analysis
				//
				sccGraph.addEdge(new cbit.util.graph.Edge(sccNodes[sccIndex1],sccNodes[sccIndex2],partitionEdges[i]));
			}
		}
		//
		// perform topological sort on entire partitioned graph
		// then, reverse order of sorted array (points from object to reference instead of the other way around).
		//
		cbit.util.graph.Node[] sortedPartitionNodes = partitionGraph.topologicalSort();
		for (int i = 0; i < sortedPartitionNodes.length/2; i++){
			cbit.util.graph.Node temp = sortedPartitionNodes[i];
			sortedPartitionNodes[i] = sortedPartitionNodes[sortedPartitionNodes.length-1-i];
			sortedPartitionNodes[sortedPartitionNodes.length-1-i] = temp;
		}
		
		//
		// perform topological sort on scc graph
		// then, reverse order of sorted array (points from object to reference instead of the other way around).
		//
		cbit.util.graph.Node[] sortedSccNodes = sccGraph.topologicalSort();
		for (int i = 0; i < sortedSccNodes.length/2; i++){
			cbit.util.graph.Node temp = sortedSccNodes[i];
			sortedSccNodes[i] = sortedSccNodes[sortedSccNodes.length-1-i];
			sortedSccNodes[sortedSccNodes.length-1-i] = temp;
		}

		StronglyConnectedComponent[] sortedSCCs = new StronglyConnectedComponent[sortedSccNodes.length];
		for (int i = 0; i < sortedSCCs.length; i++){
			sortedSCCs[i] = (StronglyConnectedComponent)sortedSccNodes[i].getData();
		}

		//
		// print out BLT-partitioned equation list (grouped first by SCC then by intra-SCC dependency)
		//
		System.out.println("sorted strongly connected components");
		for (int i = 0; i < sortedSccNodes.length; i++){
			StronglyConnectedComponent scc = (StronglyConnectedComponent)sortedSccNodes[i].getData();
			System.out.println("component("+scc.getName()+") = "+scc.toString(varEqnAssignments));
			for (int j = 0; j < sortedPartitionNodes.length; j++){
				if (scc.contains(sortedPartitionNodes[j].index)){
					VarEquationAssignment varEqnAssignment = (VarEquationAssignment)sortedPartitionNodes[j].getData();
					System.out.println("\t\t"+varEqnAssignment.getSymbol().getName()+" || "+varEqnAssignment.getEquation().infix());
				}
			}
		}
		//
		// try to symbolically simplify
		//
		System.out.println("simplifying strongly connected components");
		for (int i = 0; i < sortedSccNodes.length; i++){
			StronglyConnectedComponent scc = (StronglyConnectedComponent)sortedSccNodes[i].getData();
			System.out.println("component("+scc.getName()+") = "+scc.toString(varEqnAssignments));
			
			//
			// identify state variables (has time derivative) within this SCC
			//
			//
			// substitute all known symbols
			//

			//
			// identify state variables (has time derivative) within this SCC
			//

			//
			// for those symbols with only one "unknown", solve for it and it becomes "known"
			//

			//
			// print final equations for this SCC
			//
			for (int j = 0; j < sortedPartitionNodes.length; j++){
				if (scc.contains(sortedPartitionNodes[j].index)){
					VarEquationAssignment varEqnAssignment = (VarEquationAssignment)sortedPartitionNodes[j].getData();
					if (varEqnAssignment.getSolution()!=null){
						if (varEqnAssignment.isStateVariable()){
							System.out.println("\t\t\t"+varEqnAssignment.getSymbol().getName()+Symbol.DERIVATIVE_SUFFIX+" = "+varEqnAssignment.getSolution().infix());
						}else{
							System.out.println("\t\t\t"+varEqnAssignment.getSymbol().getName()+" = "+varEqnAssignment.getSolution().infix());
						}
					}else{
						System.out.println("\t\t\tNOT SOLVED\t\t"+varEqnAssignment.getSymbol().getName()+" || "+varEqnAssignment.getEquation().infix());
					}	
				}
			}
		}


		//
		// for those SCC's of size>1 where each element was symbolically solved, try to symbolically remove the loop
		//
		for (int i = 0; i < sccArray.length; i++){
			if (sccArray[i].size()>1){
				//
				// check that all elements have already been solved for the corresponding variable
				//
				java.util.HashSet sccSymbolHash = new java.util.HashSet();
				int bestTearingNodeIndex = -1;
				int degreeOfBestNode = -1;
				boolean bAllSolved = true;
				for (int j = 0; j < partitionNodes.length; j++){
					if (sccArray[i].contains(j)){
						sccSymbolHash.add(varEqnAssignments[j].getSymbol().getName());
						VarEquationAssignment varEqnAssignment = (VarEquationAssignment)partitionNodes[j].getData();
						if (varEqnAssignment.getSolution()==null){
							bAllSolved = false;
						}
						int degreeOfCurrentNode = 0;
						cbit.util.graph.Edge[] adjacentEdges = partitionGraph.getAdjacentEdges(partitionGraph.getNodes()[j]);
						for (int k = 0; k < adjacentEdges.length; k++){
							if (sccArray[i].contains(adjacentEdges[k].getNode1().index) && sccArray[i].contains(adjacentEdges[k].getNode2().index)){
								degreeOfCurrentNode++;
							}
						}
						if (degreeOfCurrentNode > degreeOfBestNode){
							bestTearingNodeIndex = j;
							degreeOfBestNode = degreeOfCurrentNode;
						}
					}
				}
				if (bAllSolved){
					//
					// gather list of "known" variables for this SCC
					// use selected variable to sequentially substitute expressions until remaining symbols are "known"
					// then solve equation for this variable
					// remove edge in partition graph if successfully solved
					// recompute the SCCs (this process should have "torn" at least one cycle).
					//

					//
					// collect dependent symbols (from outside of this SCC)
					//
					java.util.HashSet knownSymbolHash = new java.util.HashSet();
					for (int j = 0; j < partitionEdges.length; j++){
						if (sccArray[i].contains(partitionEdges[j].getNode2().index) && !sccArray[i].contains(partitionEdges[j].getNode1().index)){
							knownSymbolHash.add( ((VarEquationAssignment)partitionEdges[j].getNode1().getData()).getSymbol() );
						}
					}
					String symbolToSolveFor = varEqnAssignments[bestTearingNodeIndex].getSymbol().getName();
					IExpression exp = ExpressionFactory.createExpression(varEqnAssignments[bestTearingNodeIndex].getSolution().infix() + " - " + symbolToSolveFor);
					boolean done = false;
					while (!done){
						String[] symbols = exp.getSymbols();
						boolean bSubstituted = false;
						for (int j = 0; j < symbols.length; j++){
							if (sccSymbolHash.contains(symbols[j]) && !symbols[j].equals(varEqnAssignments[bestTearingNodeIndex].getSymbol().getName())){
								//
								// this symbols is another symbol within the same SCC (but not the current one, try to substitute)
								//
								for (int k = 0; k < varEqnAssignments.length; k++){
									if (varEqnAssignments[k].getSymbol().getName().equals(symbols[j])){
										exp.substituteInPlace(ExpressionFactory.createExpression(symbols[j]),ExpressionFactory.createExpression(varEqnAssignments[k].getSolution()));
										bSubstituted = true;
										break;
									}
								}
							}
						}
						if (!bSubstituted){
							done = true;
						}
					}
					IExpression explicitlySolvedSolution = symbolicProcessor.solve(exp,symbolToSolveFor);
					if (explicitlySolvedSolution!=null){
						varEqnAssignments[bestTearingNodeIndex].setEquation(exp);
						varEqnAssignments[bestTearingNodeIndex].setSolution(explicitlySolvedSolution);
						partitionNodes[bestTearingNodeIndex].setName("TORN: "+symbolToSolveFor+" = "+explicitlySolvedSolution.infix());
					}else{
						varEqnAssignments[bestTearingNodeIndex].setEquation(exp.flatten());
						varEqnAssignments[bestTearingNodeIndex].setSolution(null);
						partitionNodes[bestTearingNodeIndex].setName("TORN: "+symbolToSolveFor+" | "+exp.flatten().infix());
					}
					//
					// if this worked, then recompute dependency edges within this SCC
					// remove edges showing this nodes dependencies within this SCC
					//
					for (int j = 0; j < partitionEdges.length; j++){
						if (sccArray[i].contains(partitionEdges[j].getNode1().index) && partitionEdges[j].getNode2().index == bestTearingNodeIndex){
							partitionGraph.remove(partitionEdges[j]);
							partitionEdges = partitionGraph.getEdges();
							j--;
						}
					}
					//
					// add edges to other edges in the SCC's "dependency list" as necessary
					//
					String[] symbols = exp.getSymbols();
					for (int j = 0; j < symbols.length; j++){
						for (int k = 0; k < varEqnAssignments.length; k++){
							if (varEqnAssignments[k].getSymbol().getName().equals(symbols[j])){
								if (!sccArray[i].contains(k)){
									cbit.util.graph.Edge dependencyEdge = partitionGraph.getEdge(k,bestTearingNodeIndex);
									if (dependencyEdge==null){
										double weight = 100;
										System.out.println("inserting bogus weight of "+weight);
										partitionGraph.addEdge(new cbit.util.graph.Edge(partitionNodes[k],partitionNodes[bestTearingNodeIndex],new Double(weight)));
									}
								}
							}
						}
					}
				}
			}
		}
		modelAnalysisResults.partitionGraph = partitionGraph;
		modelAnalysisResults.sccGraph = sccGraph;
		modelAnalysisResults.sccArray = sortedSCCs;
		modelAnalysisResults.varEqnAssignments = varEqnAssignments;
		try {
			modelAnalysisResults.mathDescription = getMathDescription(varEqnAssignments);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}else{
		System.out.println("maximal matching not of full rank");
	}
// */
	modelAnalysisResults.mathSystem = mathSystem;
	modelAnalysisResults.connectivityGraph = graph;
	modelAnalysisResults.matching = matching;
	
	return modelAnalysisResults;
}


/**
 * Insert the method's description here.
 * Creation date: (1/28/2006 10:49:36 AM)
 * @return cbit.vcell.parser.Expression
 * @param sccArray ncbc.physics2.component.StronglyConnectedComponent[]
 * @param partitionGraph cbit.util.graph.Graph
 * @param symbolToTear ncbc.physics2.component.Symbol
 */
public static IExpression tear(StronglyConnectedComponent scc, cbit.util.graph.Graph partitionGraph, int indexToTear) {
	//
	// get all of the VarEquationAssignments within this scc
	//
// TODO: implement this
	//
	// find out which strongly connected component this symbol belongs to
	//
//	StronglyConnectedComponent scc = null;
//	for (int i = 0; i < sccArray.length; i++){
//		if (sccArray[i].contains(varEquToTear.
//	}
	return null;
}

}