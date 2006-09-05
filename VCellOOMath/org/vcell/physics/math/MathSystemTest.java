package org.vcell.physics.math;


import cbit.vcell.parser.Expression;
/**
 * Insert the type's description here.
 * Creation date: (11/9/2005 5:30:21 PM)
 * @author: Jim Schaff
 */
public class MathSystemTest {
/**
 * Insert the method's description here.
 * Creation date: (11/9/2005 5:33:55 PM)
 * @return cbit.vcell.mapping.MathSystem2
 */
public static MathSystem getExample() {
	MathSystem mathSystem = new MathSystem();
	try {

		//mathSystem.addEquation(new Expression("VS.V + capacitor.V + inductor.V + resistor.V"));
		//mathSystem.addEquation(new Expression("VS.i - capacitor.i"));
		//mathSystem.addEquation(new Expression("capacitor.i - inductor.i"));
		//mathSystem.addEquation(new Expression("inductor.i - resistor.i"));
		//mathSystem.addEquation(new Expression("resistor.i - VS.i"));
		//mathSystem.addEquation(new Expression("sin(t)-VS.V"));
		//mathSystem.addEquation(new Expression("inductor.L * inductor.i.prime - inductor.V"));
		//mathSystem.addEquation(new Expression("capacitor.C * capacitor.V.prime - capacitor.i"));
		//mathSystem.addEquation(new Expression("resistor.i*resistor.R - resistor.V"));
		//mathSystem.addEquation(new Expression("capacitor.C - 1.0"));
		//mathSystem.addEquation(new Expression("inductor.L - 1.0"));
		//mathSystem.addEquation(new Expression("resistor.R - 1.0"));

		mathSystem.addEquation(new Expression("VS.V + capacitor.V + inductor.V + resistor.V"));
		mathSystem.addEquation(new Expression("VS.i - capacitor.i"));
		mathSystem.addEquation(new Expression("capacitor.i - inductor.i"));
		mathSystem.addEquation(new Expression("inductor.i - resistor.i"));
		//mathSystem.addEquation(new Expression("resistor.i - VS.i"));
		mathSystem.addEquation(new Expression("sin(t)-VS.V"));
		mathSystem.addEquation(new Expression("inductor.i.prime - inductor.V"));
		mathSystem.addEquation(new Expression("capacitor.V.prime - capacitor.i"));
		mathSystem.addEquation(new Expression("resistor.i - resistor.V"));
		//mathSystem.addEquation(new Expression("capacitor.C - 1.0"));
		//mathSystem.addEquation(new Expression("inductor.L - 1.0"));
		//mathSystem.addEquation(new Expression("resistor.R - 1.0"));

	}catch (cbit.vcell.parser.ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}	

	
	return mathSystem;
}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		MathSystem mathSystem = getExample();

		mathSystem.show();

		cbit.util.graph.Graph dependencyGraph = mathSystem.getDependencyGraph();
		System.out.println(dependencyGraph.toString());

		com.mhhe.clrs2e.FlowNetwork flowNetwork = mathSystem.getFlowNetwork();
		
		System.out.println("Before Edmonds-Karp:");
		System.out.print(flowNetwork);

		com.mhhe.clrs2e.Vertex startVertex = flowNetwork.getVertex(0);
		com.mhhe.clrs2e.Vertex endVertex = flowNetwork.getVertex(1);
		if (!startVertex.getName().equals("flowStart") || !endVertex.getName().equals("flowEnd")){
			throw new RuntimeException("couldn't find start and end");
		}
		(new com.mhhe.clrs2e.EdmondsKarp()).computeMaxFlow(flowNetwork, flowNetwork.getVertex(0), flowNetwork.getVertex(1));

		System.out.println("After Edmonds-Karp:");
		System.out.print(flowNetwork);
		System.out.println("\nFlow value is " + flowNetwork.getFlowValue(startVertex));

		cbit.gui.graph.SimpleGraphModelPanelTest.renderGraph(dependencyGraph);
		
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
}
