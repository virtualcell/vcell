package org.vcell.ncbc.physics.engine;

import org.vcell.ncbc.physics.engine.ElectricalDevice;
import org.vcell.ncbc.physics.engine.SimpleElectricalDevice;

import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.modelapp.SimulationContextTest;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;


import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Node;
import cbit.util.graph.Path;


/**
 * Insert the type's description here.
 * Creation date: (5/10/01 8:48:38 AM)
 * @author: Jim Schaff
 */
public class PotentialMappingTest {
/**
 * Insert the method's description here.
 * Creation date: (5/10/01 9:32:34 AM)
 * @return Jama.Matrix
 */
public static Graph getBetaCell() throws ExpressionException {
	//
	// Epithelial cell model
	//
	System.out.println("//  Epithelial Cell Model                                                                     ");
	System.out.println("//                                                                      ");
	System.out.println("//                               extra                                  ");
	System.out.println("//                                 |                                    ");
	System.out.println("//                                 |                                    ");
	System.out.println("//                                 | PM                                 ");
	System.out.println("//                                 |                                    ");
	System.out.println("//                                 |                                    ");
	System.out.println("//                                cyt                                   ");
	System.out.println("//                                                                      ");

	Graph graph = new Graph();
	Node EXTRA = new Node("extra");
	Node CYT   = new Node("cyt");
	Node nodes[] = {EXTRA,CYT};
	Edge PM  = new Edge(EXTRA,CYT);
	Edge edges[] = {PM};
	//
	// set electrical circuit parameters
	//
	PM.setData(new SimpleElectricalDevice("PM","V_PM",new Function("V_PM_INIT",new Expression("1.0")),1.0,new Expression("V*1e-8"),false,true,false));

	for (int i = 0; i < nodes.length; i++){
		graph.addNode(nodes[i]);
	}
	for (int i = 0; i < edges.length; i++){
		graph.addEdge(edges[i]);
	}
	
	System.out.println(graph);
	return graph;
}
/**
 * Insert the method's description here.
 * Creation date: (5/10/01 9:32:34 AM)
 * @return Jama.Matrix
 */
public static Graph getBetaCellWithCurrentClamp() throws ExpressionException {
	//
	// Epithelial cell model
	//
	System.out.println("//  Epithelial Cell Model with current clamp                            ");
	System.out.println("//                                                                      ");
	System.out.println("//                               extra                                  ");
	System.out.println("//                                 |  \\                                ");
	System.out.println("//                                 |    \\                              ");
	System.out.println("//                               PM|     | PROBE                        ");
	System.out.println("//                                 |    /                               ");
	System.out.println("//                                 |  /                                 ");
	System.out.println("//                                cyt                                   ");
	System.out.println("//                                                                      ");

	Graph graph = new Graph();
	Node EXTRA = new Node("extra");
	Node CYT   = new Node("cyt");
	Node nodes[] = {EXTRA,CYT};
	Edge PM  = new Edge(EXTRA,CYT);
	Edge PROBE = new Edge(EXTRA,CYT);
	Edge edges[] = {PROBE,PM};
	//
	// set electrical circuit parameters
	//
	PM.setData(new SimpleElectricalDevice("PM","V_PM",new Function("V_PM_INIT",new Expression("1.0")),10.0,new Expression("V*1e-8"),false,true,false));
	PROBE.setData(new SimpleElectricalDevice("PROBE","V_PROBE",null,0.0,new Expression("(t>1)&&(t<1.5)"),false,true,false));

	for (int i = 0; i < nodes.length; i++){
		graph.addNode(nodes[i]);
	}
	for (int i = 0; i < edges.length; i++){
		graph.addEdge(edges[i]);
	}
	
	System.out.println(graph);
	return graph;
}
/**
 * Insert the method's description here.
 * Creation date: (5/10/01 9:32:34 AM)
 * @return Jama.Matrix
 */
public static Graph getBetaCellWithERandVoltageClamp() throws ExpressionException {
	//
	// Epithelial cell model
	//
	System.out.println("//  Epithelial Cell Model with Voltage clamp                            ");
	System.out.println("//                                                                      ");
	System.out.println("//                               extra                                  ");
	System.out.println("//                                 |  \\                                ");
	System.out.println("//                                 |    \\                              ");
	System.out.println("//                               PM|     | PROBE                        ");
	System.out.println("//                                 |    /                               ");
	System.out.println("//                                 |  /                                 ");
	System.out.println("//                                cyt                                   ");
	System.out.println("//                                 |                                    ");
	System.out.println("//                                 |                                    ");
	System.out.println("//                                 |                                    ");
	System.out.println("//                                 er                                   ");
	System.out.println("//                                                                      ");
	

	Graph graph = new Graph();
	Node EXTRA = new Node("extra");
	Node CYT   = new Node("cyt");
	Node ER   = new Node("er");
	Node nodes[] = {EXTRA,CYT,ER};
	Edge PM  = new Edge(EXTRA,CYT);
	Edge ERMEM  = new Edge(CYT,ER);
	Edge PROBE = new Edge(EXTRA,CYT);
	Edge edges[] = {PM,ERMEM,PROBE};
	//
	// set electrical circuit parameters
	//
	PM.setData(new SimpleElectricalDevice("PM","V_PM",new Function("V_PM_INIT",new Expression("1.0")),10.0,new Expression("V*1e-8"),false,true,false));
	ERMEM.setData(new SimpleElectricalDevice("ERMEM","V_ERMEM",new Function("V_ERMEM_INIT",new Expression("2.0")),10.0,new Expression("V*2e-10"),false,false,false));
	PROBE.setData(new SimpleElectricalDevice("PROBE","V_PROBE",new Function("V_PROBE_INIT",new Expression("t*(t<0.01 && t>0.04)")),0.0,null,false,true,true));

	for (int i = 0; i < nodes.length; i++){
		graph.addNode(nodes[i]);
	}
	for (int i = 0; i < edges.length; i++){
		graph.addEdge(edges[i]);
	}
	
	System.out.println(graph);
	return graph;
}
/**
 * Insert the method's description here.
 * Creation date: (5/10/01 9:32:34 AM)
 * @return Jama.Matrix
 */
public static Graph getEpithelialCell() throws ExpressionException {
	//
	// Epithelial cell model
	//
	System.out.println("//  Epithelial Cell Model                                                                     ");
	System.out.println("//                                                                      ");
	System.out.println("//                               lumen                                  ");
	System.out.println("//                            /         \\                              ");
	System.out.println("//                       pm1/             \\ pm2                        ");
	System.out.println("//                        /                 \\                          ");
	System.out.println("//                      /        pm3          \\                        ");
	System.out.println("//                  cyt1 --------------------- cyt2                     ");
	System.out.println("//                      \\                     /                        ");
	System.out.println("//                        \\                 /                          ");
	System.out.println("//                      pm4 \\             /pm5                         ");
	System.out.println("//                            \\         /                              ");
	System.out.println("//                               basal                                  ");
	System.out.println("//                                                                      ");
	System.out.println("//                                                                      ");

	Graph graph = new Graph();
	Node LUMEN = new Node("lumen");
	Node CYT1  = new Node("cyt1");
	Node CYT2  = new Node("cyt2");
	Node BASAL = new Node("basal");
	Node nodes[] = {LUMEN,CYT1,CYT2,BASAL};
	Edge PM1 = new Edge(LUMEN,CYT1);
	Edge PM2 = new Edge(LUMEN,CYT2);
	Edge PM3 = new Edge(CYT1,CYT2);
	Edge PM4 = new Edge(CYT1,BASAL);
	Edge PM5 = new Edge(CYT2,BASAL);
	Edge edges[] = {PM1,PM2,PM3,PM4,PM5};
	//
	// set electrical circuit parameters
	//
	PM1.setData(new SimpleElectricalDevice("PM1","V_PM1",new Function("V_PM1_INIT",new Expression("1.0")),1.0,new Expression("V*1e-8"),false,true,false));
	PM2.setData(new SimpleElectricalDevice("PM2","V_PM2",new Function("V_PM2_INIT",new Expression("1.0")),1.0,new Expression("V*1e-8"),false,true,false));
	PM3.setData(new SimpleElectricalDevice("PM3","V_PM3",new Function("V_PM3_INIT",new Expression("1.0")),1.0,new Expression("V*1e-8"),false,true,false));
	PM4.setData(new SimpleElectricalDevice("PM4","V_PM4",new Function("V_PM4_INIT",new Expression("1.0")),1.0,new Expression("V*1e-8"),false,true,false));
	PM5.setData(new SimpleElectricalDevice("PM5","V_PM5",new Function("V_PM5_INIT",new Expression("1.0")),1.0,new Expression("V*1e-8"),false,true,false));

	for (int i = 0; i < nodes.length; i++){
		graph.addNode(nodes[i]);
	}
	for (int i = 0; i < edges.length; i++){
		graph.addEdge(edges[i]);
	}
	
	System.out.println(graph);
	return graph;
}
/**
 * Insert the method's description here.
 * Creation date: (5/10/01 9:32:34 AM)
 * @return Jama.Matrix
 */
public static Graph getEpithelialCellWithTightJunction() throws ExpressionException {
	//
	// Epithelial cell model
	//
	System.out.println("//  Epithelial Cell Model With Tight Junction                           ");
	System.out.println("//                                                                      ");
	System.out.println("//                                      ________                        ");
	System.out.println("//                                     /        \\                      ");
	System.out.println("//                               lumen            \\                    ");
	System.out.println("//                            /         \\           \\                 ");
	System.out.println("//                       pm1/             \\ pm2       \\               ");
	System.out.println("//                        /                 \\          |               ");
	System.out.println("//                      /        pm3          \\        |               ");
	System.out.println("//                  cyt1 --------------------- cyt2    |tight           ");
	System.out.println("//                      \\                     /        |               ");
	System.out.println("//                        \\                 /          |               ");
	System.out.println("//                      pm4 \\             /pm5        /                ");
	System.out.println("//                            \\         /           /                  ");
	System.out.println("//                               basal            /                     ");
	System.out.println("//                                     \\________/                      ");
	System.out.println("//                                                                      ");
	System.out.println("//                                                                      ");

	Graph graph = new Graph();
	Node LUMEN = new Node("lumen");
	Node CYT1  = new Node("cyt1");
	Node CYT2  = new Node("cyt2");
	Node BASAL = new Node("basal");
	Node nodes[] = {LUMEN,CYT1,CYT2,BASAL};
	Edge PM1 = new Edge(LUMEN,CYT1);
	Edge PM2 = new Edge(LUMEN,CYT2);
	Edge PM3 = new Edge(CYT1,CYT2);
	Edge PM4 = new Edge(CYT1,BASAL);
	Edge PM5 = new Edge(CYT2,BASAL);
	Edge TIGHT = new Edge(LUMEN,BASAL);
	Edge edges[] = {PM1,PM2,PM3,PM4,PM5,TIGHT};
	//
	// set electrical circuit parameters
	//
	PM1.setData(new SimpleElectricalDevice("PM1","V_PM1",new Function("V_PM1_INIT",new Expression("1.0")),6,new Expression("V*1e-8"),false,true,false));
	PM2.setData(new SimpleElectricalDevice("PM2","V_PM2",new Function("V_PM2_INIT",new Expression("1.0")),7,new Expression("V*1e-8"),false,true,false));
	PM3.setData(new SimpleElectricalDevice("PM3","V_PM3",new Function("V_PM3_INIT",new Expression("1.0")),8,new Expression("V*1e-8"),false,true,false));
	PM4.setData(new SimpleElectricalDevice("PM4","V_PM4",new Function("V_PM4_INIT",new Expression("1.0")),9,new Expression("V*1e-8"),false,true,false));
	PM5.setData(new SimpleElectricalDevice("PM5","V_PM5",new Function("V_PM5_INIT",new Expression("1.0")),11,new Expression("V*1e-8"),false,true,false));
	TIGHT.setData(new SimpleElectricalDevice("TIGHT","V_TIGHT",new Function("V_TIGHT_INIT",new Expression("1.0")),0.0,new Expression("1"),false,true,false));

	for (int i = 0; i < nodes.length; i++){
		graph.addNode(nodes[i]);
	}
	for (int i = 0; i < edges.length; i++){
		graph.addEdge(edges[i]);
	}
	
	System.out.println(graph);
	return graph;
}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		Graph circuitGraph = null;
		MathDescription mathDesc = null;
		//
		// test mapping from a generic circuit
		//
		//circuitGraph = getBetaCellWithERandVoltageClamp();
		//circuitGraph = getBetaCellWithVoltageClamp();
		//circuitGraph = getBetaCellWithCurrentClamp();
		//circuitGraph = getEpithelialCellWithTightJunction();
		//mathDesc = PotentialMapping.getMathDescription(circuitGraph,300.0);
		//System.out.println(mathDesc.getVCML());

		//
		// test mapping from SimulationContext
		//
		System.out.println("\n\n---------------- creating electrical graph ------------------");
		cbit.vcell.modelapp.SimulationContext simContext = SimulationContextTest.getExampleElectrical(0);
		circuitGraph = ElectricalGraphGenerator.getCircuitGraph(simContext);
		System.out.println("\n\n---------------- Generating Potential Mapping ------------------");
		PotentialMapping pm = new PotentialMapping(circuitGraph);
		//mathDesc = PotentialMapping.getMathDescription(circuitGraph,simContext.getTemperatureKelvin());
		//System.out.println(mathDesc.getVCML());
		
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
}
