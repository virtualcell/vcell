package org.vcell.physics.component;
import org.vcell.physics.component.gui.OOModelingPanel;

import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (1/16/2006 11:33:57 PM)
 * @author: Jim Schaff
 */
public class ModelTest {
	/**
 * Insert the method's description here.
 * Creation date: (1/28/2006 10:49:36 AM)
 * @return cbit.vcell.parser.Expression
 * @param sccArray ncbc.physics2.component.StronglyConnectedComponent[]
 * @param partitionGraph cbit.util.graph.Graph
 * @param symbolToTear ncbc.physics2.component.Symbol
 */
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
 * Creation date: (1/16/2006 11:34:20 PM)
 * @return ncbc.physics2.component.Model
 */
public static Model getExample() {

	//
	//
	//                 R1
	//         c1____/\/\/\____c2
	//         |                |
	//         |                |
	//         |                |
	//         |              _____
	//       [ Vs ]           _____ C1
	//         |                |
	//         |                |
	//         |                |
	//         |________________|
	//                  |
	//                  O gnd
	//                  |
	//               ______
	//                ____   
	//                 __
	//
	//
	//
	Model model = new Model();
	VoltageSource vs = new VoltageSource("VS",10);
	Resistor r = new Resistor("R1",1);
	Capacitor c = new Capacitor("C1",1);
	Ground gnd = new Ground("gnd");
	Connection c1 = new Connection(new Connector[] { vs.getConnectors(0), r.getConnectors(1) });
	Connection c2 = new Connection(new Connector[] { r.getConnectors(0), c.getConnectors(1) });
	Connection c3 = new Connection(new Connector[] { vs.getConnectors(1), c.getConnectors(0), gnd.getConnectors(0) });

	model.addModelComponent(vs);
	model.addModelComponent(r);
	model.addModelComponent(c);
	model.addModelComponent(gnd);
	model.addConnection(c1);
	model.addConnection(c2);
	model.addConnection(c3);
	
	return model;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:34:20 PM)
 * @return ncbc.physics2.component.Model
 */
public static Model getExample2() {

	//
	//
	//                 R1           L1
	//         c1____/\/\/\__c2___UUUUU___c3
	//         |                          |
	//         |                          |
	//         |                          |
	//         |                        _____
	//       [ Vs ]                     _____ C1
	//         |                          |
	//         |                          |
	//         |                          |
	//         |________c4________________|
	//                  |
	//                  O gnd
	//                  |
	//               ______
	//                ____   
	//                 __
	//
	//
	//
	Model model = new Model();
	VoltageSource vs = new VoltageSource("VS",10);
	Resistor R = new Resistor("R1",1);
	Capacitor C = new Capacitor("C1",1);
	Inductor L = new Inductor("L1",1);
	Ground gnd = new Ground("gnd");
	Connection c1 = new Connection(new Connector[] { vs.getConnectors(0), R.getConnectors(1) });
	Connection c2 = new Connection(new Connector[] { R.getConnectors(0), L.getConnectors(1) });
	Connection c3 = new Connection(new Connector[] { L.getConnectors(0), C.getConnectors(1) });
	Connection c4 = new Connection(new Connector[] { vs.getConnectors(1), C.getConnectors(0), gnd.getConnectors(0) });

	model.addModelComponent(vs);
	model.addModelComponent(R);
	model.addModelComponent(L);
	model.addModelComponent(C);
	model.addModelComponent(gnd);
	model.addConnection(c1);
	model.addConnection(c2);
	model.addConnection(c3);
	model.addConnection(c4);
	
	return model;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:34:20 PM)
 * @return ncbc.physics2.component.Model
 */
public static Model getExample3() {

	//
	//
	//                     
	//          ___________________c1_____
	//         |                   |      |
	//         |                   |      |
	//         |                   |      |
	//         |                   \    _____
	//       [ Vs ]             R1 /    _____ C1
	//         |                   \      |
	//         |                   /      |
	//         |                   |      |
	//         |________c2_________|______|
	//                  |
	//                  O gnd
	//                  |
	//               ______
	//                ____   
	//                 __
	//
	//
	//
	Model model = new Model();
	VoltageSource vs = new VoltageSource("VS",10);
	Resistor R = new Resistor("R1",1);
	Capacitor C = new Capacitor("C1",1);
	Ground gnd = new Ground("gnd");
	Connection c1 = new Connection(new Connector[] { vs.getConnectors(1), R.getConnectors(1), C.getConnectors(1) });
	Connection c2 = new Connection(new Connector[] { vs.getConnectors(0), R.getConnectors(0), C.getConnectors(0), gnd.getConnectors(0) });

	model.addModelComponent(vs);
	model.addModelComponent(R);
	model.addModelComponent(C);
	model.addModelComponent(gnd);
	model.addConnection(c1);
	model.addConnection(c2);
	
	return model;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:34:20 PM)
 * @return ncbc.physics2.component.Model
 */
public static Model getExample4() throws cbit.vcell.parser.ExpressionException {

	//
	//
	//                     
	//        c1   ____________               
	//     A ===> |            |      
	//            |    R1      |      c3       __________
	//        c2  |            | ===> C  ===> |          |  c5
	//     B ===> |____________|              |    R2    | ===> E
	//                                    c4  |          |
	//                                D  ===> |          |
	//                                        |__________|
	//
	//
	//
	Model model = new Model();
	Reaction R1 = new Reaction("R1",new String[] { "r1","r2","p1" }, new int[] { -1, -1, 1 }, new Expression[] { }, false);
	Reaction R2 = new Reaction("R2",new String[] { "r1","r2","p1" }, new int[] { -1, -1, 1 }, new Expression[] { }, true);
	Species A = new Species("A");
	Species B = new Species("B");
	Species C = new Species("C");
	Species D = new Species("D");
	Species E = new Species("E");
	Connection c1 = new Connection(new Connector[] { R1.getConnectors(0), A.getConnectors(0) });
	Connection c2 = new Connection(new Connector[] { R1.getConnectors(1), B.getConnectors(0) });
	Connection c3 = new Connection(new Connector[] { R1.getConnectors(2), C.getConnectors(0), R2.getConnectors(0) });
	Connection c4 = new Connection(new Connector[] { R2.getConnectors(1), D.getConnectors(0) });
	Connection c5 = new Connection(new Connector[] { R2.getConnectors(2), E.getConnectors(0) });

	model.addModelComponent(R1);
	model.addModelComponent(R2);
	model.addModelComponent(A);
	model.addModelComponent(B);
	model.addModelComponent(C);
	model.addModelComponent(D);
	model.addModelComponent(E);
	model.addConnection(c1);
	model.addConnection(c2);
	model.addConnection(c3);
	model.addConnection(c4);
	model.addConnection(c5);
	
	return model;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:34:20 PM)
 * @return ncbc.physics2.component.Model
 */
public static Model getExample5() {

	//
	//
	//                     
	//          ___________________c1_____________
	//         |                   |              |
	//         |                   |              |+
	//         |                   |+            C_
	//         |                   \             C_
	//         |                R1 /             C_  L1
	//         |                   \             C_
	//         |                   /              |
	//         |+                  |              |
	//       [ Vs ]                |      R2      |
	//         |                 c3|____/\/\/\____|
	//         |                   |          +   |
	//         |                  +|              |
	//         |                   \              |
	//         |                R3 /              |
	//         |                   \              |
	//         |                   /              |
	//         |                   |              |
	//         |________c2_________|______________|
	//                  |
	//                  | gnd
	//                  |
	//               ______
	//                ____   
	//                 __
	//
	//
	//
	Model model = new Model();
	VoltageSource vs = new VoltageSource("VS",10);
	Resistor R1 = new Resistor("R1",1);
	Resistor R2 = new Resistor("R2",1);
	Resistor R3 = new Resistor("R3",1);
	Inductor L1 = new Inductor("L1",1);
	Ground gnd = new Ground("gnd");
	Connection c1 = new Connection(new Connector[] { vs.getConnectors(1), R1.getConnectors(1), L1.getConnectors(1) });
	Connection c2 = new Connection(new Connector[] { vs.getConnectors(0), R3.getConnectors(0), R2.getConnectors(1), L1.getConnectors(0), gnd.getConnectors(0) });
	Connection c3 = new Connection(new Connector[] { R3.getConnectors(1), R2.getConnectors(0), R1.getConnectors(0) });

	model.addModelComponent(vs);
	model.addModelComponent(R1);
	model.addModelComponent(R2);
	model.addModelComponent(R3);
	model.addModelComponent(L1);
	model.addModelComponent(gnd);
	model.addConnection(c1);
	model.addConnection(c2);
	model.addConnection(c3);
	
	return model;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:34:20 PM)
 * @return ncbc.physics2.component.Model
 */
public static Model getExampleTriangle_a_b() throws cbit.vcell.parser.ExpressionException {

	//
	//
	//                     
	//                               |
	//                              b|
	//                               |
	//                      a   ----------   area
	//                  -------| triangle |-------
	//                         |__________|
	//                               |
	//                              h|
	//                               |
	//
	//
	Model model = new Model();

	ModelComponent triangle = new ModelComponent("triangle");
	Variable tri_a = new Variable("a");
	Variable tri_b = new Variable("b");
	Variable tri_h = new Variable("h");
	Variable tri_area = new Variable("area");
	triangle.addSymbol(tri_a);
	triangle.addSymbol(tri_b);
	triangle.addSymbol(tri_h);
	triangle.addSymbol(tri_area);
	triangle.addEquation(new Expression("a*a+b*b-h*h"));
	triangle.addEquation(new Expression("area - a*b/2"));
	triangle.setConnectors(new Connector[] { 
		new Connector(triangle,"pin_a",tri_a,null),
		new Connector(triangle,"pin_b",tri_b,null),
		new Connector(triangle,"pin_h",tri_h,null),
		new Connector(triangle,"pin_area",tri_area,null) } );

	PointSource source_a = new PointSource("source_a",3);
	PointSource source_b = new PointSource("source_b",4);
	
	Connection c1 = new Connection(new Connector[] { source_a.getConnectors(0), triangle.getConnectors(0) });
	Connection c2 = new Connection(new Connector[] { source_b.getConnectors(0), triangle.getConnectors(1) });

	model.addModelComponent(triangle);
	model.addModelComponent(source_a);
	model.addModelComponent(source_b);
	model.addConnection(c1);
	model.addConnection(c2);
	
	return model;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:34:20 PM)
 * @return ncbc.physics2.component.Model
 */
public static Model getExampleTriangle_h_a() throws cbit.vcell.parser.ExpressionException {

	//
	//
	//                     
	//                               |
	//                              b|
	//                               |
	//                      a   ----------   area
	//                  -------| triangle |-------
	//                         |__________|
	//                               |
	//                              h|
	//                               |
	//
	//
	Model model = new Model();

	ModelComponent triangle = new ModelComponent("triangle");
	Variable tri_a = new Variable("a");
	Variable tri_b = new Variable("b");
	Variable tri_h = new Variable("h");
	Variable tri_area = new Variable("area");
	triangle.addSymbol(tri_a);
	triangle.addSymbol(tri_b);
	triangle.addSymbol(tri_h);
	triangle.addSymbol(tri_area);
	triangle.addEquation(new Expression("a*a+b*b-h*h"));
	triangle.addEquation(new Expression("area - a*b/2"));
	triangle.setConnectors(new Connector[] { 
		new Connector(triangle,"pin_a",tri_a,null),
		new Connector(triangle,"pin_b",tri_b,null),
		new Connector(triangle,"pin_h",tri_h,null),
		new Connector(triangle,"pin_area",tri_area,null) } );

	PointSource source_h = new PointSource("source_h",5);
	PointSource source_area = new PointSource("source_area",6);
	
	Connection c1 = new Connection(new Connector[] { source_h.getConnectors(0), triangle.getConnectors(2) });
	Connection c2 = new Connection(new Connector[] { source_area.getConnectors(0), triangle.getConnectors(3) });

	model.addModelComponent(triangle);
	model.addModelComponent(source_h);
	model.addModelComponent(source_area);
	model.addConnection(c1);
	model.addConnection(c2);
	
	return model;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:47:03 PM)
 * @return ncbc.physics2.MathSystem2
 * @param model ncbc.physics2.component.Model
 */
public static org.vcell.physics.math.MathSystem getMathSystem2(Model model) throws cbit.vcell.parser.ExpressionException {
	org.vcell.physics.math.MathSystem mathSystem = new org.vcell.physics.math.MathSystem();

	//
	// for each model component, add symbols and equations
	//

	//
	// for each connection, add equations to couple variables
	//
	// effort variables are made equal, flow variables sum to zero
	//
	ModelComponent[] modelComponents = model.getModelComponents();
	Connection[] connections = model.getConnections();
	for (int i = 0; i < modelComponents.length; i++){
		Symbol[] symbols = modelComponents[i].getSymbols();
		for (int j = 0; j < symbols.length; j++){
			mathSystem.addSymbol(modelComponents[i].getName()+"."+symbols[j].getName());
		}
		Expression[] equations = modelComponents[i].getEquations();
		for (int j = 0; j < equations.length; j++){
			String syms[] = equations[j].getSymbols();
			Expression exp = new Expression(equations[j]);
			for (int k = 0; k < syms.length; k++){
				exp.substituteInPlace(new Expression(syms[k]),new Expression(modelComponents[i].getName()+"."+syms[k]));
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
				mathSystem.addEquation(new Expression(effortBuffer.toString()+"-"+parent.getName()+"."+connectors[j].getEffortVariable().getName()));
				//
				// sum up mass flux
				//
				if (connectors[j].getFlowVariable()!=null){
					flowBuffer.append("+"+parent.getName()+"."+connectors[j].getFlowVariable().getName());
				}
			}
		}
		if (flowBuffer.length()>0){
			mathSystem.addEquation(new Expression(flowBuffer.toString())); // one flow equation (conservation of mass)
		}

	}
	return mathSystem;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 11:34:20 PM)
 * @return ncbc.physics2.component.Model
 */
public static Model getVCellAppExample() throws Exception {

	String filename = "c:\\temp\\BioModel1.xml";
	String vcmlString = cbit.util.xml.XmlUtil.getXMLString(filename);
	cbit.vcell.biomodel.BioModel bioModel = cbit.vcell.xml.XmlHelper.XMLToBioModel(vcmlString);
	cbit.vcell.mapping.SimulationContext simContext = bioModel.getSimulationContexts(0);
	Model model = new Model();
	org.vcell.physics.math.MappingUtilities.addChemicalDevices(simContext,model);

	return model;
}


/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		//Model model = ModelReader.parse(null);
		//Model model = getExampleTriangle_h_a();
		Model model = getExample3();
		
		ModelAnalysisResults modelAnalysisResults = analyzeMathSystem(model);

		OOModelingPanel ooModelPanel = new OOModelingPanel();
		
		ooModelPanel.setPartitionGraphPanelGraph(modelAnalysisResults.partitionGraph);
		ooModelPanel.setSccGraphModelPanelGraph(modelAnalysisResults.sccGraph);
		ooModelPanel.setStronglyConnectedComponents(modelAnalysisResults.sccArray);
		ooModelPanel.setVarEquationAssignments(modelAnalysisResults.varEqnAssignments);

		ooModelPanel.setPhysicalModelGraphPanelModel(model);
		ooModelPanel.setConnectivityGraphPanelGraph(modelAnalysisResults.connectivityGraph);
		ooModelPanel.setBipartiteMatchings(modelAnalysisResults.matching);

		ooModelPanel.setPreferredSize(new java.awt.Dimension(800,800));
		ooModelPanel.setMinimumSize(new java.awt.Dimension(800,800));

		cbit.gui.DialogUtils.showComponentOKCancelDialog(null,ooModelPanel,"graph");

	} catch (Throwable e) {
		e.printStackTrace(System.out);
	}
}


/**
 * Starts the application.
 * @param args an array of command-line arguments
 * @throws ExpressionException 
 */
public static ModelAnalysisResults analyzeMathSystem(Model model) throws ExpressionException{

	ModelAnalysisResults modelAnalysisResults = new ModelAnalysisResults();
	
	org.vcell.physics.math.MathSystem mathSystem = getMathSystem2(model);
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
		
		//
		// build vertices make a directed graph with vertices (Var | equation)
		//
		for (int i = 0; i < nodes.length; i++){
			if (nodes[i].getData() instanceof Symbol){
				Symbol symbol = (Symbol)nodes[i].getData();
				cbit.util.graph.Node symbolNode = nodes[i];
				int matchingIndex = matching.getMatch(nodes[i].index);
				cbit.util.graph.Node eqnNode = graph.getNodes()[matchingIndex];
				Expression equation = (Expression)eqnNode.getData();
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
				Expression solvedExp = solve(equation,symbolToSolveFor);
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
			com.mhhe.clrs2e.Vertex vertex = weightedGraph.getVertex(i);
			VarEquationAssignment varEqnAssignment = (VarEquationAssignment)partitionNodes[i].getData();
			if (!varEqnAssignment.isStateVariable()){
				//
				// state variables are considered to be "known" at all times, so don't consider them dependencies
				//
				Symbol symbol = varEqnAssignment.getSymbol();
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
		java.util.HashSet globallyKnownSymbols = new java.util.HashSet();
		for (int i = 0; i < sortedSccNodes.length; i++){
			StronglyConnectedComponent scc = (StronglyConnectedComponent)sortedSccNodes[i].getData();
			System.out.println("component("+scc.getName()+") = "+scc.toString(varEqnAssignments));
			
			java.util.HashSet locallyKnownSymbols = new java.util.HashSet(globallyKnownSymbols);
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
					Expression exp = new Expression(varEqnAssignments[bestTearingNodeIndex].getSolution().infix() + " - " + symbolToSolveFor);
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
										exp.substituteInPlace(new Expression(symbols[j]),new Expression(varEqnAssignments[k].getSolution()));
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
					Expression explicitlySolvedSolution = solve(exp,symbolToSolveFor);
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
	}else{
		System.out.println(cbit.util.xml.XmlUtil.xmlToString(ModelReader.print(model)));
	}
// */
	modelAnalysisResults.model = model;
	modelAnalysisResults.mathSystem = mathSystem;
	modelAnalysisResults.connectivityGraph = graph;
	modelAnalysisResults.matching = matching;

	return modelAnalysisResults;
}


/**
 * Insert the method's description here.
 * Creation date: (1/20/2006 10:19:09 AM)
 * @return cbit.vcell.parser.Expression
 * @param equation cbit.vcell.parser.Expression
 * @param variable java.lang.String
 */
public static Expression solve(Expression equation, String variable) throws cbit.vcell.parser.ExpressionException {
	jscl.math.Expression jsclExpression = null;
	String jsclExpressionString = "solve("+equation.flatten().infix_JSCL()+","+new Expression(variable).infix_JSCL()+")";
	try {
		jsclExpression = jscl.math.Expression.valueOf(jsclExpressionString);
	}catch (jscl.text.ParseException e){
		e.printStackTrace(System.out);
		System.out.println("JSCL couldn't parse \""+jsclExpressionString+"\"");
		return null;
	}
	cbit.vcell.parser.Expression solution = null;
	jscl.math.Generic jsclSolution = jsclExpression.expand().simplify();
	try {
		solution = new cbit.vcell.parser.Expression(jsclSolution.toString());
	}catch (Throwable e){
		try {
			e.printStackTrace(System.out);
			String mathML = jsclSolution.toMathML(null);
			cbit.vcell.parser.ExpressionMathMLParser expMathMLParser = new cbit.vcell.parser.ExpressionMathMLParser(null);
			solution = expMathMLParser.fromMathML(mathML);
		}catch (Throwable e2){
			e2.printStackTrace(System.out);
		}
	}
	if (solution!=null){
		String[] jsclSymbols = solution.getSymbols();
		for (int i = 0;jsclSymbols!=null && i < jsclSymbols.length; i++){
			String restoredSymbol = cbit.util.TokenMangler.getRestoredStringJSCL(jsclSymbols[i]);
			if (!restoredSymbol.equals(jsclSymbols[i])){
				solution.substituteInPlace(new Expression(jsclSymbols[i]),new Expression(restoredSymbol));
			}
		}
	}else{
		
	}
	return solution;
}


/**
 * Insert the method's description here.
 * Creation date: (1/28/2006 10:49:36 AM)
 * @return cbit.vcell.parser.Expression
 * @param sccArray ncbc.physics2.component.StronglyConnectedComponent[]
 * @param partitionGraph cbit.util.graph.Graph
 * @param symbolToTear ncbc.physics2.component.Symbol
 */
public static Expression tear(StronglyConnectedComponent scc, cbit.util.graph.Graph partitionGraph, int indexToTear) {
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