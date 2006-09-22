package cbit.util.graph;
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
	Graph graph = new Graph();
	Node constraints = new Node("constraints");
//	Node events = new Node("events");
//	Node expression = new Node("expression");
	Node geometry = new Node("geometry");
	Node image = new Node("image");
//	Node manager = new Node("manager");
	Node math = new Node("math");
//	Node matrix = new Node("matrix");
	Node model = new Node("model");
	Node ncbcPhysics = new Node("ncbc.physics");
	Node optimization = new Node("optimization");
	Node physics = new Node("physics");
//	Node render = new Node("render");
//	Node server = new Node("server");
	Node simdata = new Node("simdata");
	Node simulation = new Node("simulation");
	Node solvers = new Node("solvers");
	Node spatial = new Node("spatial");
	//Node test = new Node("test");
//	Node ui = new Node("ui");
//	Node units = new Node("units");
	//Node util = new Node("util");
//	Node vcml = new Node("vcml");
	//Node xmlCommon = new Node("xml.common");
	//Node xmlCompare = new Node("xml.compare");
	Node nodes[] = {
		constraints,
//		events,
//		expression,
		geometry,
		image,
//		manager,
		math,
//		matrix,
		model,
		ncbcPhysics,
		optimization,
		physics,
//		render,
//		server,
		simdata,
		simulation,
		solvers,
		spatial,
		//test,
//		ui,
//		units,
		//util,
//		vcml,
		//xmlCommon,
		//xmlCompare
	};
	Edge edges[] = {
//			new Edge(constraints,expression),
//			new Edge(constraints,units),
			//new Edge(constraints,util),
//			new Edge(events,simdata),
//			new Edge(events,simulation),
//			new Edge(events,solvers),
//			//new Edge(events,util),
//			new Edge(expression,units),
			//new Edge(expression,util),
			//new Edge(expression,xmlCommon),
//			new Edge(geometry,expression),
			new Edge(geometry,image),
			new Edge(geometry,spatial),
//			new Edge(geometry,units),
			//new Edge(geometry,util),
			//new Edge(image,util),
//			new Edge(manager,expression),
//			new Edge(manager,events),
//			new Edge(manager,geometry),
//			new Edge(manager,image),
//			new Edge(manager,math),
//			new Edge(manager,model),
//			new Edge(manager,server),
//			new Edge(manager,simdata),
//			new Edge(manager,solvers),
//			//new Edge(manager,util),
//			new Edge(manager,vcml),
//			//new Edge(manager,xmlCommon),
//			new Edge(math,expression),
			new Edge(math,geometry),
//			new Edge(math,matrix),
//			new Edge(math,units),   // ??? didn't think so
			//new Edge(math,util),
//			new Edge(matrix,expression),
			//new Edge(matrix,util),
			new Edge(model,constraints),
//			new Edge(model,expression),
			new Edge(model,geometry),
			new Edge(model,image),
			new Edge(model,math),
//			new Edge(model,matrix),
			new Edge(model,simulation),
//			new Edge(model,units),
			//new Edge(model,util),
			//new Edge(model,xmlCompare),
//			new Edge(ncbcPhysics,expression),
			new Edge(ncbcPhysics,geometry),
			new Edge(ncbcPhysics,image),
			new Edge(ncbcPhysics,math),
//			new Edge(ncbcPhysics,matrix),
			new Edge(ncbcPhysics,model),
//			new Edge(ncbcPhysics,units),
			//new Edge(ncbcPhysics,util),
//			new Edge(optimization,expression),
			new Edge(optimization,geometry),
			new Edge(optimization,math),
			new Edge(optimization,model),
			new Edge(optimization,simdata),
			new Edge(optimization,simulation),
			new Edge(optimization,solvers),
			//new Edge(optimization,util),
//			new Edge(physics,expression),
			new Edge(physics,math),
//			new Edge(physics,matrix),
			new Edge(physics,model),
//			new Edge(physics,units),
			//new Edge(physics,util),
			//new Edge(physics,xmlCommon),
//			new Edge(render,spatial),
			//new Edge(render,util),
//			new Edge(server,events),
//			new Edge(server,expression),
//			new Edge(server,geometry),
//			new Edge(server,image),
//			new Edge(server,math),
//			new Edge(server,model),
//			new Edge(server,simdata),
//			new Edge(server,simulation),
//			new Edge(server,solvers),
//			new Edge(server,units),
//			//new Edge(server,util),
//			new Edge(server,vcml),
			//new Edge(server,xmlCommon),
//			new Edge(simdata,expression),
			new Edge(simdata,geometry),
			new Edge(simdata,image),
			new Edge(simdata,math),
			new Edge(simdata,model),
			new Edge(simdata,simulation),
			new Edge(simdata,spatial),
//			new Edge(simdata,units),
			//new Edge(simdata,util),
			new Edge(simulation,geometry),
			new Edge(simulation,math),
			//new Edge(simulation,util),
//			new Edge(solvers,expression),
			new Edge(solvers,geometry),
			new Edge(solvers,image),
			new Edge(solvers,math),
			new Edge(solvers,model),
			new Edge(solvers,simdata),
			new Edge(solvers,simulation),
			//new Edge(solvers,util),
//			new Edge(spatial,units),
			//new Edge(spatial,util),
//			new Edge(ui,events),
//			new Edge(ui,expression),
//			new Edge(ui,geometry),
//			new Edge(ui,image),
//			new Edge(ui,manager),
//			new Edge(ui,math),
//			new Edge(ui,matrix),
//			new Edge(ui,model),
//			new Edge(ui,render),
//			new Edge(ui,server),
//			new Edge(ui,simdata),
//			new Edge(ui,simulation),
//			new Edge(ui,solvers),
//			new Edge(ui,spatial),
//			new Edge(ui,units),
//			//new Edge(ui,util),
//			new Edge(ui,vcml),
			//new Edge(ui,xmlCommon),
			//new Edge(ui,xmlCompare),
//			new Edge(vcml,constraints),
//			new Edge(vcml,expression),
//			new Edge(vcml,geometry),
//			new Edge(vcml,image),
//			new Edge(vcml,math),
//			new Edge(vcml,model),
//			new Edge(vcml,simulation),
//			new Edge(vcml,units),
//			//new Edge(vcml,util),
//			//new Edge(vcml,xmlCommon),
//			//new Edge(vcml,xmlCompare),
			//new Edge(xmlCompare,util),
			//new Edge(xmlCompare,xmlCommon),
	};
	for (int i = 0; i < nodes.length; i++){
		graph.addNode(nodes[i]);
	}
	for (int i = 0; i < edges.length; i++){
		graph.addEdge(edges[i]);
	}
	return graph;
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