package org.vcell.physics.math;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import jscl.plugin.Expression;
import jscl.plugin.ParseException;
import jscl.plugin.Variable;

import org.vcell.physics.component.PhysicalSymbol;
import org.vcell.physics.math.BipartiteMatchings.Matching;

import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Node;
import cbit.vcell.units.VCUnitDefinition;

import com.mhhe.clrs2e.WeightedAdjacencyListGraph;

public class IndexReduction {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static void reduceDAEIndexPantelides(MathSystem mathSystem) throws ParseException {
	/**
	 * (see page 680 of "Principles of Object Oriented Modeling and Simulation with Modelica 2.1")
	 * 
	 * when determining the DAE structural index, all coefficients can be ignored 
	 *    (those edges and equations can be removed from the bipartite graph)
	 *    we might not do this immediately due to the additional complexity.
	 *    (e.g. in an expression k*vx, the coefficient k is ignored when constructing the graph).
	 * 
	 */
		boolean done = false;
		ArrayList<Expression> differentiatedEquations = new ArrayList<Expression>();
		ArrayList<String> substitutedVariables = new ArrayList<String>();
		ArrayList<String> dummyVariables = new ArrayList<String>();
		while(!done){
			done = true;
			int offset = 10000;
			mathSystem.show();
			Graph dependencyGraph = mathSystem.getDependencyGraph(offset,false);
			int[] colors = new int[dependencyGraph.getNumNodes()];
			WeightedAdjacencyListGraph agraph = getBipartiteGraph(dependencyGraph, colors);
			
			Matching fullMatching = BipartiteMatchings.findMaximumWeightedMaximumCardinalityMatching(agraph, colors);
			int fullMatchingCovering = fullMatching.countCoveredVertices()/2;
			if (fullMatchingCovering < dependencyGraph.getNumNodes()/2){
				System.out.println("can't find a full cardinality matching");
				return;
			}
			
			Matching bestReducedMatching = null;
			int best_indexToRemove = -1;
			for (int i=0;i<colors.length;i++){
				if (colors[i] == 1){
					if (fullMatching.contains(i)){
						// remove equation at index i
						Graph reducedGraph = new Graph(dependencyGraph);
						reducedGraph.remove(reducedGraph.getNodes()[i]);
						int[] reducedColors = new int[reducedGraph.getNumNodes()];
						WeightedAdjacencyListGraph reducedALGraph = getBipartiteGraph(reducedGraph, reducedColors);
						Matching currentReducedMatching = BipartiteMatchings.findMaximumWeightedMaximumCardinalityMatching(reducedALGraph, reducedColors);
						//System.out.println(currentReducedMatching);
						int currentCardinality = currentReducedMatching.countCoveredVertices()/2;
						int desiredCardinality = dependencyGraph.getNumNodes()/2-1;
						System.out.println("testing equation \""+dependencyGraph.getNodes()[i].getData().toString()+"\", cardinality="+currentCardinality+", sumOfWeights="+currentReducedMatching.sumOfWeights()+", already used = "+differentiatedEquations.contains(dependencyGraph.getNodes()[i].getData()));
						if (currentCardinality==desiredCardinality){ // is proper cardinality
							if (!differentiatedEquations.contains(dependencyGraph.getNodes()[i].getData())){ // check if already differentiated this one
								if (bestReducedMatching==null || bestReducedMatching.sumOfWeights()<=currentReducedMatching.sumOfWeights()){
									best_indexToRemove = i;
									bestReducedMatching = currentReducedMatching;
								}
							}else{
								System.out.println("didn't remove equation \""+dependencyGraph.getNodes()[i].getData().toString()+"\" has already been differentiated, skipping it now");
							}
						}else{
							System.out.println("removing equation \""+dependencyGraph.getNodes()[i].getData().toString()+"\", looking for matching cardinality of "+desiredCardinality+", but found "+currentCardinality);
						}
					}
				}
			}
			System.out.println("fullmatching("+fullMatching.countCoveredVertices()+") = "+fullMatching.sumOfWeights()+", maxReducedMatching("+bestReducedMatching.countCoveredVertices()+")  = "+bestReducedMatching.sumOfWeights());
			int DAEindex = ((int)bestReducedMatching.sumOfWeights()) + offset - ((int)fullMatching.sumOfWeights()) + 1;
			System.out.println("DAE index = "+DAEindex);
			if (DAEindex>0){
				Expression equationToDifferentiate = (Expression)dependencyGraph.getNodes()[best_indexToRemove].getData();
				System.out.println("should differentiate equation "+equationToDifferentiate.infix());
				Expression differentiatedExp = equationToDifferentiate.derivative(Variable.valueOf("t")).expand().simplify();
				System.out.println("... resulting in: "+differentiatedExp.infix());
				System.out.println("but we need a dummy derivative variable");
				Variable[] vars = Expression.getVariables(differentiatedExp);
				Variable varToRemove = null;
				Variable dummyVar = null;
				int highestDerivative = 0;
				for (int i = 0; i < vars.length; i++) {
					if (vars[i].getVarType() == Variable.VARTYPE_IMPLICITFUNCTION){
						int derivativeOrder = Expression.valueOf(vars[i].infix()).getHighestTimeDerivative(vars[i].infix());
						if (derivativeOrder > highestDerivative){
							highestDerivative = derivativeOrder;
							if (vars[i].getParameters().length!=1){
								throw new RuntimeException("expecting one parameter (should be time)");
							}
							varToRemove = vars[i];
							dummyVar = Variable.valueOf(vars[i].getName()+".prime(t)");
						}
					}
				}
				if (varToRemove!=null){
					System.out.println("replacing \""+varToRemove.infix()+"\" with \""+dummyVar.infix()+"\" (and their time derivatives)");
					
					dummyVariables.add(dummyVar.infix());
					substitutedVariables.add(varToRemove.infix());
					
					dummyVariables.add(Expression.valueOf("d("+dummyVar.infix()+",t)").expand().infix());
					substitutedVariables.add(Expression.valueOf("d("+varToRemove.infix()+",t)").expand().infix());
					
					dummyVariables.add(Expression.valueOf("d(d("+dummyVar.infix()+",t),t)").expand().infix());
					substitutedVariables.add(Expression.valueOf("d(d("+varToRemove.infix()+",t),t)").expand().infix());
					//
					// 1) add dummy derivative to mathSystem
					// 2) replace equation in mathSystem with differentiated expression to mathSystem
					// 3) substitute dummy variable in all expressions, recomputing weights.
					//
					mathSystem.addSymbol(new OOMathSymbol(dummyVar.infix(),new org.vcell.physics.component.Variable(dummyVar.infix(),VCUnitDefinition.UNIT_TBD)));
					differentiatedEquations.add(equationToDifferentiate);
//					mathSystem.removeEquation(equationToDifferentiate);  // only removing temporarily ... will add back at the end.
					mathSystem.addEquation(differentiatedExp);
					Expression[] equations = mathSystem.getEquations();
					for (int i = 0; i < equations.length; i++) {
						Expression newEquation = equations[i];
						for (int j = 0; j < substitutedVariables.size(); j++) {
							if (newEquation.infix().contains(substitutedVariables.get(j))){
								newEquation = newEquation.substitute(Variable.valueOf(substitutedVariables.get(j)), Expression.valueOf(dummyVariables.get(j)));
							}
						}
						if (equations[i]!=newEquation){
							mathSystem.removeEquation(equations[i]);
							mathSystem.addEquation(newEquation);
						}
					}
					done = false;
				}else{
					throw new RuntimeException("unable to reduce the DAE index");
				}
			}
		}
//		for (Iterator iter = removedEquations.iterator(); iter.hasNext();) {
//			Expression removedExpression = (Expression) iter.next();
//			mathSystem.addEquation(removedExpression);
//		}
		mathSystem.show();
	}

	public static WeightedAdjacencyListGraph getBipartiteGraph(Graph graph, int[] colors){
		if (colors==null || colors.length != graph.getNumNodes()){
			throw new RuntimeException("expecting a color array with one color per node");
		}
		WeightedAdjacencyListGraph agraph = new WeightedAdjacencyListGraph(graph.getNumNodes(),false);
		int numVertexA = 0;
		int numVertexB = 0;
		Node[] nodes = graph.getNodes();
		for (int i=0;i<nodes.length;i++){
			if (nodes[i].getData() instanceof OOMathSymbol){
				numVertexA++;
				colors[i]=0;
				agraph.addVertex(nodes[i].getName());
				if (numVertexB>0){
					throw new RuntimeException("node "+nodes[i].getName()+" is out of order, expecting Symbols then Expressions");
				}
			}else if (nodes[i].getData() instanceof Expression){
				numVertexB++;
				colors[i]=1;
				agraph.addVertex(nodes[i].getName());
			}else{
				throw new RuntimeException("node "+nodes[i].getName()+" has unexpected type, expected Symbol or Expression");
			}
		}
		Edge[] edges = graph.getEdges();
		for (int i=0;i<edges.length;i++){
			int index1 = -1;
			int index2 = -1;
			for (int j = 0; j < nodes.length; j++) {
				if (nodes[j]==edges[i].getNode1()){
					index1 = j;
				}else if (nodes[j]==edges[i].getNode2()){
					index2 = j;
				}
			}
			agraph.addEdge(index1, index2, ((Integer)edges[i].getData()).intValue());
		}
		return agraph;
	}


}
