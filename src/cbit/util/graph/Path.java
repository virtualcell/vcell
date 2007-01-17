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
	}else if (edges.length == 2){
		if (edges[1].getNode1().equals(edges[0].getNode2())){
			return new Node[] { edges[0].getNode1(), edges[0].getNode2(), edges[1].getNode2() };
		}else{
			return new Node[] { edges[0].getNode2(), edges[0].getNode1(), edges[1].getNode2() };
		}
	}else{
		Vector nodeList = new Vector();
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


/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 1:27:57 AM)
 * @return boolean
 */
public boolean isClosed() {
	return bClosed;
}
}