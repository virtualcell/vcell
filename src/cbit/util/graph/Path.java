package cbit.util.graph;
import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (2/11/2002 12:19:18 AM)
 * @author: Jim Schaff
 */
public class Path extends Trail {
	private boolean bClosed = false;

/**
 * Path constructor comment.
 */
public Path() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 1:15:04 AM)
 * @param path cbit.vcell.mapping.potential.Path
 */
public Path(Path path) {
	super(path);
}


/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 1:15:04 AM)
 * @param path cbit.vcell.mapping.potential.Path
 */
public Path(Path path, Edge edge) {
	super(path);
	addEdge(edge);
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 10:49:12 PM)
 * @param newNode cbit.vcell.mapping.potential.Node
 */
public void addEdge(Edge newEdge) {
	if (bClosed){
		throw new RuntimeException("path is already closed, can't add another edge");
	}
	//
	// "Paths" are "Trails" without repeated vertices (except if path is closed).
	//
	if (contains(newEdge.getNode1()) && contains(newEdge.getNode2())){
		//
		// if newEdge is adjacent to edge[0] and edge[N-1], then it's ok, mark closed.
		//
		if (newEdge.isAdjacent(getFirstEdge()) && newEdge.isAdjacent(getLastEdge())){
			bClosed = true;
		}else{
			throw new RuntimeException("edge "+newEdge+" already exists in list");
		}
	}
	super.addEdge(newEdge);
}


/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 2:49:29 PM)
 * @return cbit.vcell.mapping.potential.Node[]
 */
public Node[] getNodesTraversed() {
	Edge edges[] = getEdges();
	if (edges.length==1){
		return new Node[] { edges[0].getNode1(), edges[0].getNode2() };
	}else{
		Vector<Node> nodeList = new Vector<Node>();
		//
		// look ahead to see which node is in common with the next edge, choose the other.
		//
		for (int i = 0; i < edges.length-1; i++){
			if (edges[i+1].hasEndPoint(edges[i].getNode2())){
				nodeList.add(edges[i].getNode1());
			}else{
				nodeList.add(edges[i].getNode2());
			}
		}
		//
		// look back to see which node is in common with the previous edge, choose that one.
		//
		if (edges[edges.length-2].hasEndPoint(edges[edges.length-1].getNode1())){
			nodeList.add(edges[edges.length-1].getNode1());
			nodeList.add(edges[edges.length-1].getNode2());
		}else{
			nodeList.add(edges[edges.length-1].getNode2());
			nodeList.add(edges[edges.length-1].getNode1());
		}
		Node nodes[] = new Node[nodeList.size()];
		nodeList.copyInto(nodes);
		return nodes;
	}
}

public static void test(){
	cbit.util.graph.Node node1 = new cbit.util.graph.Node("A");
	cbit.util.graph.Node node2 = new cbit.util.graph.Node("B");
	cbit.util.graph.Node node3 = new cbit.util.graph.Node("C");
	cbit.util.graph.Node node4 = new cbit.util.graph.Node("D");
	cbit.util.graph.Edge edge12 = new cbit.util.graph.Edge(node1,node2);
	cbit.util.graph.Edge edge21 = new cbit.util.graph.Edge(node2,node1);
	cbit.util.graph.Edge edge23 = new cbit.util.graph.Edge(node2,node3);
	cbit.util.graph.Edge edge32 = new cbit.util.graph.Edge(node3,node2);
	cbit.util.graph.Edge edge34 = new cbit.util.graph.Edge(node3,node4);
	cbit.util.graph.Edge edge43 = new cbit.util.graph.Edge(node4,node3);
	//
	// edges arranged with nodes in alphabetical order, all combinations of edge reversals up to path length 4
	//
	cbit.util.graph.Edge[][] edges = new cbit.util.graph.Edge[][] {
			 // ==> A,B
			 { edge12 },
			 // ==> A,B,C
			 { edge12, edge23 },
			 { edge12, edge32 },
			 { edge21, edge23 },
			 { edge21, edge32 },
			 // ==> A,B,C,D
			 { edge12, edge23, edge34 },
			 { edge12, edge32, edge34 },
			 { edge21, edge23, edge34 },
			 { edge21, edge32, edge34 },
			 { edge12, edge23, edge43 },
			 { edge12, edge32, edge43 },
			 { edge21, edge23, edge43 },
			 { edge21, edge32, edge43 },
	};
	System.out.println("==== Path.test() ====");
	for (int trial=0; trial<edges.length; trial++){
		//
		// print each path and computed nodesTraversed[] ... should be in alphabetical order
		//
		cbit.util.graph.Path path = new cbit.util.graph.Path();
		cbit.util.graph.Edge[] pathEdges = edges[trial];
		for (int i=0;i<pathEdges.length;i++){
			System.out.print("("+pathEdges[i].getNode1().getName()+","+pathEdges[i].getNode2().getName()+") ");
			path.addEdge(pathEdges[i]);
		}
		cbit.util.graph.Node[] nodesTraversed = path.getNodesTraversed();
		System.out.print(" ==> ");
		for (int i=0;i<nodesTraversed.length; i++){
			System.out.print(" "+nodesTraversed[i].getName());
		}
		System.out.println();
	}
}

/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 1:27:57 AM)
 * @return boolean
 */
public boolean isClosed() {
	return bClosed;
}
}