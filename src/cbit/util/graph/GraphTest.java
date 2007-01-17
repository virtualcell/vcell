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