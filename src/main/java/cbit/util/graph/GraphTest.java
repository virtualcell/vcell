/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.util.graph;

import java.util.HashSet;

/**
 * Insert the type's description here.
 * Creation date: (2/10/2002 11:48:49 PM)
 * @author: Jim Schaff
 */
public class GraphTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		Graph graph = getExample();
		System.out.println(graph);
		System.out.println("spanning tree(s):");
		Graph spanningTrees[] = graph.getSpanningForest();
		for (int i = 0; i < spanningTrees.length; i++){
			System.out.println(spanningTrees[i]);
		}

		System.out.println("fundamental cycles:");
		Path fundamentalCycles[] = graph.getFundamentalCycles();
		for (int i = 0; i < fundamentalCycles.length; i++){
			System.out.println("   "+fundamentalCycles[i]);
		}
		
		testTopologicalSort();
		
	}catch (Throwable e){
		e.printStackTrace(System.out);
		System.exit(1);
	}
}

public static Graph getExample(){
	Graph graph = new Graph();
	Node A = new Node("A");
	Node B = new Node("B");
	Node C = new Node("C");
	Node D = new Node("D");
	Node E = new Node("E");
	Node F = new Node("F");
	Node nodes[] = {A,B,C,D,E};
	Edge AB = new Edge(A,B);
	Edge AC = new Edge(A,C);
	Edge AD = new Edge(A,D);
	Edge BC = new Edge(B,C);
	Edge BE = new Edge(B,E);
	Edge CD = new Edge(C,D);
	Edge CE = new Edge(C,E);
	Edge DE = new Edge(D,E);
	Edge edges[] = {AB,AC,AD,BC,BE,CD,CE,DE};

	for (int i = 0; i < nodes.length; i++){
		graph.addNode(nodes[i]);
	}
	for (int i = 0; i < edges.length; i++){
		graph.addEdge(edges[i]);
	}
	return graph;
}

public static Graph getDependencyExample(){
	Graph dependencyGraph = new Graph();
	Node biomodel = new Node("biomodel");
	Node cartoon = new Node("cartoon");
	Node clienttest = new Node("clienttest");
	Node common = new Node("common");
	Node constraints = new Node("constraints");
	Node database = new Node("database");
	Node eclipseRuntime = new Node("eclipseRuntime");
	Node events = new Node("events");
	Node experiment = new Node("experiment");
	Node expression = new Node("expression");
	Node geometry = new Node("geometry");
	Node graph = new Node("graph");
	Node image = new Node("image");
	Node manager = new Node("manager");
	Node mapping = new Node("mapping");
	Node math = new Node("math");
	Node mathmodel = new Node("mathmodel");
	Node matlab = new Node("matlab");
	Node matrix = new Node("matrix");
	Node modelapp = new Node("modelapp");
	Node modelapp_constraints = new Node("modelapp.constraints");
	Node modelapp_parest = new Node("modelapp.parest");
	Node modelapp_physics = new Node("modelapp.physics");
	Node ncbcPhysics = new Node("ncbc.physics");
	Node optimization = new Node("optimization");
	Node physics = new Node("physics");
	Node physiology = new Node("physiology");
	Node plotting = new Node("plotting");
	Node quicktime = new Node("quicktime");
	Node render = new Node("render");
	Node server = new Node("server");
	Node serveradmin = new Node("serveradmin");
	Node simdata = new Node("simdata");
	Node simulation = new Node("simulation");
	Node solvers = new Node("solvers");
	Node spatial = new Node("spatial");
	Node test = new Node("test");
	Node ui = new Node("ui");
	Node units = new Node("units");
	Node util = new Node("util");
	Node vcml = new Node("vcml");
	Node xmlCommon = new Node("xml.common");
	Node xmlCompare = new Node("xml.compare");
	Node nodes[] = {
		biomodel,
		cartoon,
		clienttest,
		common,
		constraints,
		database,
		events,
		experiment,
		expression,
		geometry,
		graph,
		image,
		manager,
		mapping,
		math,
		mathmodel,
		matlab,
		matrix,
		modelapp,
		modelapp_constraints,
		modelapp_parest,
		modelapp_physics,
		ncbcPhysics,
		optimization,
		physics,
		physiology,
		plotting,
		quicktime,
		render,
		server,
		serveradmin,
		simdata,
		simulation,
		solvers,
		spatial,
		test,
		ui,
		units,
		util,
		vcml,
		xmlCommon,
		xmlCompare
	};
	Edge edges[] = {
			//new Edge(biomodel,common),
			//new Edge(biomodel,expression),
			new Edge(biomodel,geometry),
			//new Edge(biomodel,image),
			new Edge(biomodel,math),
			new Edge(biomodel,modelapp),
			new Edge(biomodel,physiology),
			new Edge(biomodel,simulation),
			new Edge(biomodel,util),
			new Edge(cartoon,common),
			new Edge(cartoon,graph),
			new Edge(cartoon,util),
			new Edge(clienttest,biomodel),
			//new Edge(clienttest,common),
			new Edge(clienttest,database),
			new Edge(clienttest,events),
			//new Edge(clienttest,expression),
			new Edge(clienttest,manager),
			//new Edge(clienttest,math),
			new Edge(clienttest,server),
			new Edge(clienttest,simdata),
			new Edge(clienttest,simulation),
			new Edge(clienttest,solvers),
			//new Edge(clienttest,units),
			new Edge(clienttest,util),
			new Edge(clienttest,vcml),
			//new Edge(clienttest,xmlCommon),
			new Edge(constraints,cartoon),
			new Edge(constraints,common),
			new Edge(constraints,expression),
			new Edge(constraints,graph),
			new Edge(constraints,units),
			new Edge(constraints,util),
			new Edge(database,biomodel),
			new Edge(database,common),
			new Edge(database,events),
			new Edge(database,expression),
			new Edge(database,geometry),
			new Edge(database,image),
			new Edge(database,math),
			new Edge(database,mathmodel),
			new Edge(database,modelapp),
			new Edge(database,physiology),
			new Edge(database,simdata),
			new Edge(database,simulation),
			new Edge(database,solvers),
			new Edge(database,units),
			new Edge(database,util),
			new Edge(database,vcml),
			new Edge(database,xmlCommon),
			new Edge(events,simulation),
			new Edge(events,util),
			new Edge(experiment,constraints),
			new Edge(experiment,expression),
			new Edge(experiment,units),
			new Edge(experiment,util),
			new Edge(expression,common),
			new Edge(expression,units),
			new Edge(expression,util),
			new Edge(expression,xmlCommon),
			new Edge(geometry,common),
			new Edge(geometry,expression),
			new Edge(geometry,graph),
			new Edge(geometry,image),
			new Edge(geometry,spatial),
			new Edge(geometry,units),
			new Edge(geometry,util),
			new Edge(graph,common), // didn't actually show up but two third party packages are in this plugin
			new Edge(image,common),
			new Edge(image,util),
			new Edge(manager,biomodel),
			new Edge(manager,cartoon),
			new Edge(manager,common),
			new Edge(manager,database),
			new Edge(manager,events),
			new Edge(manager,expression),
			new Edge(manager,geometry),
			new Edge(manager,image),
			new Edge(manager,math),
			new Edge(manager,mathmodel),
			new Edge(manager,modelapp),
			new Edge(manager,physiology),
			new Edge(manager,plotting),
			new Edge(manager,server),
			new Edge(manager,simdata),
			new Edge(manager,simulation),
			new Edge(manager,solvers),
			new Edge(manager,units),
			new Edge(manager,util),
			new Edge(manager,vcml),
			new Edge(manager,xmlCommon),
			new Edge(mapping,cartoon),
			//new Edge(mapping,common),
			new Edge(mapping,expression),
			new Edge(mapping,geometry),
			new Edge(mapping,graph),
			new Edge(mapping,math),
			new Edge(mapping,matrix),
			new Edge(mapping,modelapp),
			new Edge(mapping,physiology),
			//new Edge(mapping,simulation),
			new Edge(mapping,units),
			new Edge(mapping,util),
			new Edge(math,expression),
			new Edge(math,geometry),
			new Edge(math,graph),
			//new Edge(math,image),
			new Edge(math,matrix),
			new Edge(math,units),
			new Edge(math,util),
			//new Edge(mathmodel,expression),
			//new Edge(mathmodel,geometry),
			new Edge(mathmodel,math),
			new Edge(mathmodel,simulation),
			new Edge(mathmodel,util),
			new Edge(matlab,common),
			new Edge(matrix,expression),
			new Edge(matrix,common),  // because we "donated" RationalNumber to UCAR
			new Edge(matrix,util),
			new Edge(modelapp,eclipseRuntime),
			new Edge(modelapp,common),
			new Edge(modelapp,expression),
			new Edge(modelapp,geometry),
			new Edge(modelapp,image),
			new Edge(modelapp,math),
			new Edge(modelapp,physiology),
			new Edge(modelapp,simulation),
			new Edge(modelapp,units),
			new Edge(modelapp,util),
			new Edge(modelapp_constraints,common),
			new Edge(modelapp_constraints,constraints),
			new Edge(modelapp_constraints,expression),
			//new Edge(modelapp_constraints,geometry),
			new Edge(modelapp_constraints,mapping),
			new Edge(modelapp_constraints,math),
			new Edge(modelapp_constraints,modelapp),
			new Edge(modelapp_constraints,physiology),
			//new Edge(modelapp_constraints,simulation),
			//new Edge(modelapp_constraints,util),
			new Edge(modelapp_parest,common),
			new Edge(modelapp_parest,expression),
			new Edge(modelapp_parest,graph),
			new Edge(modelapp_parest,mapping),
			new Edge(modelapp_parest,math),
			new Edge(modelapp_parest,modelapp),
			new Edge(modelapp_parest,optimization),
			new Edge(modelapp_parest,physiology),
			new Edge(modelapp_parest,plotting),
			new Edge(modelapp_parest,simdata),
			new Edge(modelapp_parest,simulation),
			new Edge(modelapp_parest,util),
			new Edge(modelapp_physics,common),
			new Edge(modelapp_physics,expression),
			//new Edge(modelapp_physics,graph),
			//new Edge(modelapp_physics,math),
			new Edge(modelapp_physics,modelapp),
			new Edge(modelapp_physics,physics),
			new Edge(modelapp_physics,physiology),
			new Edge(modelapp_physics,util),
			//new Edge(modelapp_physics,xmlCommon),
			new Edge(ncbcPhysics,cartoon),
			new Edge(ncbcPhysics,common),
			new Edge(ncbcPhysics,expression),
			new Edge(ncbcPhysics,geometry),
			new Edge(ncbcPhysics,graph),
			new Edge(ncbcPhysics,image),
			new Edge(ncbcPhysics,math),
			new Edge(ncbcPhysics,matrix),
			new Edge(ncbcPhysics,modelapp),
			new Edge(ncbcPhysics,physiology),
			new Edge(ncbcPhysics,units),
			new Edge(ncbcPhysics,util),
			new Edge(optimization,common),
			new Edge(optimization,expression),
			new Edge(optimization,geometry),
			//new Edge(optimization,image),
			new Edge(optimization,math),
			new Edge(optimization,simdata),
			new Edge(optimization,simulation),
			new Edge(optimization,solvers),
			new Edge(optimization,util),
			new Edge(physics,cartoon),
			new Edge(physics,common),
			new Edge(physics,expression),
			new Edge(physics,geometry),
			new Edge(physics,graph),
			//new Edge(physics,image),
			new Edge(physics,math),
			new Edge(physics,units),
			new Edge(physics,util),
			new Edge(physics,xmlCommon),
			new Edge(physiology,common),
			new Edge(physiology,expression),
			new Edge(physiology,units),
			new Edge(physiology,util),
			new Edge(plotting,util),
			new Edge(render,common),
			new Edge(render,spatial),
			new Edge(render,util),
			new Edge(server,biomodel),
			new Edge(server,cartoon),
			new Edge(server,common),
			new Edge(server,database),
			new Edge(server,events),
			//new Edge(server,expression),
			new Edge(server,geometry),
			new Edge(server,image),
			new Edge(server,math),
			new Edge(server,mathmodel),
			new Edge(server,modelapp),
			new Edge(server,physiology),
			new Edge(server,plotting),
			new Edge(server,simdata),
			new Edge(server,simulation),
			new Edge(server,solvers),
			//new Edge(server,units),
			new Edge(server,util),
			new Edge(server,vcml),
			new Edge(server,xmlCommon),
			//new Edge(serveradmin,common),
			new Edge(serveradmin,database),
			new Edge(serveradmin,events),
			new Edge(serveradmin,server),
			new Edge(serveradmin,simulation),
			new Edge(serveradmin,solvers),
			new Edge(serveradmin,util),
			new Edge(serveradmin,vcml),
			new Edge(serveradmin,xmlCommon),
			new Edge(simdata,common),
			new Edge(simdata,events),
			new Edge(simdata,expression),
			new Edge(simdata,geometry),
			new Edge(simdata,image),
			new Edge(simdata,math),
			new Edge(simdata,plotting),
			new Edge(simdata,quicktime),
			new Edge(simdata,simulation),
			new Edge(simdata,spatial),
			new Edge(simdata,units),
			new Edge(simdata,util),
			new Edge(simulation,expression),
			new Edge(simulation,geometry),
			new Edge(simulation,math),
			new Edge(simulation,util),
			new Edge(solvers,common),
			new Edge(solvers,events),
			new Edge(solvers,expression),
			new Edge(solvers,geometry),
			new Edge(solvers,image),
			new Edge(solvers,math),
			new Edge(solvers,simdata),
			new Edge(solvers,simulation),
			//new Edge(solvers,spatial),
			//new Edge(solvers,units),
			new Edge(solvers,util),
			new Edge(spatial,graph),
			new Edge(spatial,units),
			new Edge(spatial,util),
			new Edge(test,biomodel),
			new Edge(test,clienttest),
			new Edge(test,common),
			new Edge(test,database),
			new Edge(test,expression),
			new Edge(test,geometry),
			//new Edge(test,image),
			new Edge(test,manager),
			new Edge(test,mapping),
			new Edge(test,math),
			new Edge(test,mathmodel),
			new Edge(test,modelapp),
			new Edge(test,physiology),
			//new Edge(test,server),
			new Edge(test,simdata),
			new Edge(test,simulation),
			new Edge(test,solvers),
			new Edge(test,ui),
			//new Edge(test,units),
			new Edge(test,util),
			new Edge(test,vcml),
			new Edge(test,xmlCommon),
			new Edge(ui,eclipseRuntime), 
			new Edge(ui,biomodel), 
			new Edge(ui,cartoon), 
			new Edge(ui,clienttest), 
			new Edge(ui,common), 
			new Edge(ui,database), 
			new Edge(ui,events), 
			new Edge(ui,expression), 
			new Edge(ui,geometry), 
			new Edge(ui,image), 
			new Edge(ui,manager), 
			new Edge(ui,mapping), 
			new Edge(ui,math), 
			new Edge(ui,mathmodel), 
			new Edge(ui,matrix), 
			new Edge(ui,modelapp), 
			new Edge(ui,physiology), 
			new Edge(ui,plotting), 
			new Edge(ui,render), 
			new Edge(ui,server), 
			new Edge(ui,simdata), 
			new Edge(ui,simulation), 
			new Edge(ui,solvers), 
			new Edge(ui,spatial), 
			new Edge(ui,units), 
			new Edge(ui,util), 
			new Edge(ui,vcml), 
			new Edge(ui,xmlCommon), 
			new Edge(ui,xmlCompare), 
			new Edge(units,common),
			new Edge(util,common),
			new Edge(vcml,biomodel),
			new Edge(vcml,common),
			new Edge(vcml,constraints),
			new Edge(vcml,expression),
			new Edge(vcml,geometry),
			new Edge(vcml,image),
			new Edge(vcml,math),
			new Edge(vcml,mathmodel),
			new Edge(vcml,modelapp),
			new Edge(vcml,physiology),
			new Edge(vcml,simulation),
			new Edge(vcml,units),
			new Edge(vcml,util),
			new Edge(vcml,xmlCommon),
			new Edge(vcml,xmlCompare),
			new Edge(xmlCommon,common),
			new Edge(xmlCompare,common),
			new Edge(xmlCompare,util),
			new Edge(xmlCompare,xmlCommon),
	};
	HashSet<Node> nodesToIgnore = new HashSet<Node>();
//	nodesToIgnore.add(database);
//	nodesToIgnore.add(manager);
//	nodesToIgnore.add(ui);
	nodesToIgnore.add(util);
	nodesToIgnore.add(xmlCommon);
//	nodesToIgnore.add(xmlCompare);

	for (int i = 0; i < nodes.length; i++){
		if (!nodesToIgnore.contains(nodes[i])){
			dependencyGraph.addNode(nodes[i]);
		}
	}
	for (int i = 0; i < edges.length; i++){
		if (!nodesToIgnore.contains(edges[i].getNode1()) && !nodesToIgnore.contains(edges[i].getNode2())){
			dependencyGraph.addEdge(edges[i]);
		}
	}
	return dependencyGraph;
}

/**
 * Insert the method's description here.
 * Creation date: (4/12/2002 10:41:40 AM)
 */
public static void testDigraphMethods() {
	//
	//
	//
	System.out.println("\n========================== Testing ReachableSet ======================");
	try {
		Graph graph = new Graph();
		Node t = new Node("t");
		Node V1 = new Node("V1=f(t)");
		Node V2 = new Node("V2=f(t)");
		Node A = new Node("A=f(V1,C)");
		Node B = new Node("B=f(A,C)");
		Node C = new Node("C=f(D)");
		Node D = new Node("D=f(t)");
		Node nodes[] = {t,V1,V2,A,B,C,D};
		Edge V1t = new Edge(V1,t);
		Edge V2t = new Edge(V2,t);
		Edge AV1 = new Edge(A,V1);
		Edge AC = new Edge(A,C);
		Edge BA = new Edge(B,A);
		Edge BC = new Edge(B,C);
		Edge CD = new Edge(C,D);
		Edge edges[] = {V1t,V2t,AV1,AC,BA,BC,CD/*, new Edge(D,t)*/};

		for (int i = 0; i < nodes.length; i++){
			graph.addNode(nodes[i]);
		}
		for (int i = 0; i < edges.length; i++){
			graph.addEdge(edges[i]);
		}
		System.out.println(graph);
		
		System.out.println("attractor set for A:");
		Node attractorNodes[] = graph.getDigraphAttractorSet(A);
		for (int i = 0; i < attractorNodes.length; i++){
			System.out.println("   "+attractorNodes[i]);
		}
		
		System.out.println("reachable set for A:");
		Node reachableNodes[] = graph.getDigraphReachableSet(A);
		for (int i = 0; i < reachableNodes.length; i++){
			System.out.println("   "+reachableNodes[i]);
		}
		
	}catch (Throwable e){
		e.printStackTrace(System.out);
		System.exit(1);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/12/2002 10:41:40 AM)
 */
public static void testTopologicalSort() {
	//
	//
	//
	System.out.println("\n========================== Testing Topological sort ======================");
	try {
		Graph graph = new Graph();
		Node A = new Node("A=C+D");
		Node B = new Node("B=D+5");
		Node C = new Node("C=5");
		Node D = new Node("D=4");
		Node E = new Node("E=C-1");
		Node nodes[] = {A,B,C,D,E};
		Edge AC = new Edge(A,C);
		Edge AD = new Edge(A,D);
		Edge BD = new Edge(B,D);
		Edge EC = new Edge(E,C);
		Edge edges[] = {AC,AD,BD,EC};

		for (int i = 0; i < nodes.length; i++){
			graph.addNode(nodes[i]);
		}
		for (int i = 0; i < edges.length; i++){
			graph.addEdge(edges[i]);
		}
		System.out.println(graph);
		
		System.out.println("topological sort:");
		Node sortedNodes[] = graph.topologicalSort();
		for (int i = 0; i < sortedNodes.length; i++){
			System.out.println("   "+sortedNodes[i]);
		}
		
	}catch (Throwable e){
		e.printStackTrace(System.out);
		System.exit(1);
	}
}
}
