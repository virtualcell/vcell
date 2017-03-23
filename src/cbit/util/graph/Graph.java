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
import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (2/10/2002 10:47:30 PM)
 * @author: Jim Schaff
 */
public class Graph {
	protected ArrayList<Node> nodeList = new ArrayList<Node>();
	protected ArrayList<Edge> edgeList = new ArrayList<Edge>();
	private HashMap<String, Node> nodeNameHash = new HashMap<String, Node>();
	private HashMap<String, ArrayList<Edge> > adjacentEdgeHash = new HashMap<String, ArrayList<Edge> >();
	private HashMap<String, Integer> nodeIndexHash = new HashMap<String, Integer>();

	private class Counter {
		private int count = 0;
		Counter(){
			count = 0;
		}
		int getCount(){
			return count;
		}
		void increment(){
			count++;
		}
	}

/**
 * Graph constructor comment.
 */
public Graph() {
	super();
}

/**
 * Graph constructor comment.
 */
public Graph(Graph graph) {
	super();
	Iterator<Node> nodeIter = graph.nodeList.iterator();
	while (nodeIter.hasNext()) {
		Node node = (Node) nodeIter.next();
		addNode(node);
	}
	Iterator<Edge> edgeIter = graph.edgeList.iterator();
	while (edgeIter.hasNext()) {
		Edge edge = (Edge) edgeIter.next();
		addEdge(edge);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 10:49:12 PM)
 * @param newNode cbit.vcell.mapping.potential.Node
 */
public void addEdge(Edge newEdge) {
	if (contains(newEdge)){
		throw new RuntimeException("edge "+newEdge+" already exists in list");
	}
	if (!contains(newEdge.getNode1())){
		addNode(newEdge.getNode1());
	}
	if (!contains(newEdge.getNode2())){
		addNode(newEdge.getNode2());
	}
	edgeList.add(newEdge);
	
	adjacentEdgeHash.get(newEdge.getNode1().getName()).add(newEdge);
	adjacentEdgeHash.get(newEdge.getNode2().getName()).add(newEdge);
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 10:49:12 PM)
 * @param newNode cbit.vcell.mapping.potential.Node
 */
public void addNode(Node newNode) {
	if (contains(newNode)){
		throw new RuntimeException("node "+newNode+" already exists in list");
	}
	nodeList.add(newNode);
	
	if (nodeNameHash.get(newNode.getName()) != null) {
		throw new RuntimeException("node " + newNode + " with same name already exists in list");
	}
	nodeNameHash.put(newNode.getName(), newNode);
	
	adjacentEdgeHash.put(newNode.getName(), new ArrayList<Edge>());
	nodeIndexHash.put(newNode.getName(), nodeList.size() - 1);
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 10:51:46 PM)
 * @return boolean
 * @param node cbit.vcell.mapping.potential.Node
 */
public boolean contains(Edge edge) {
	ArrayList<Edge> edgeList = adjacentEdgeHash.get(edge.getNode1().getName());
	return edgeList != null && edgeList.contains(edge);
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 10:51:46 PM)
 * @return boolean
 * @param node cbit.vcell.mapping.potential.Node
 */
public boolean contains(Node node) {
	Node anode = nodeNameHash.get(node.getName());
	if (node.equals(anode)) {
		return true;
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 3:12:11 PM)
 * @param nodeInfos cbit.vcell.mapping.potential.NodeInfo[]
 */
private NodeInfo[] depthFirstSearch() {
	NodeInfo nodeInfos[] = new NodeInfo[nodeList.size()];
	for (int i = 0; i < nodeInfos.length; i++){
		nodeInfos[i] = new NodeInfo(i);
	}
	Counter counter = new Counter();
	for (int i = 0; i < nodeInfos.length; i++){
		if (nodeInfos[i].getColor() == NodeInfo.WHITE){
			depthFirstSearchVisit(nodeInfos,i,counter);
		}
	}
	return nodeInfos;
}


/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 3:12:11 PM)
 * @param nodeInfos cbit.vcell.mapping.potential.NodeInfo[]
 */
private void depthFirstSearchVisit(NodeInfo nodeInfos[], int index, Counter counter) {
	nodeInfos[index].setColor(NodeInfo.GREY);
	counter.increment();
	nodeInfos[index].setDiscoverTime(counter.getCount());
	Node thisNode = nodeList.get(index);
	Edge adjacentEdges[] = getAdjacentEdges(thisNode);
	for (int i = 0; i < adjacentEdges.length; i++){
		if (adjacentEdges[i].getNode1() == thisNode){
			int adjacentNodeIndex = getIndex(adjacentEdges[i].getNode2());
			if (nodeInfos[adjacentNodeIndex].getColor() == NodeInfo.WHITE){
				nodeInfos[adjacentNodeIndex].setPredecessorIndex(index);
				depthFirstSearchVisit(nodeInfos,adjacentNodeIndex,counter);
			}
		}
	}
	nodeInfos[index].setColor(NodeInfo.BLACK);
	counter.increment();
	nodeInfos[index].setFinishTime(counter.getCount());
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 11:05:12 PM)
 * @return cbit.vcell.mapping.potential.Edge[]
 * @param node cbit.vcell.mapping.potential.Node
 */
public Edge[] getAdjacentEdges(Edge edge) {
	ArrayList<Edge> adjacentList1 = adjacentEdgeHash.get(edge.getNode1().getName());	
	ArrayList<Edge> adjacentList2 = adjacentEdgeHash.get(edge.getNode2().getName());
	
	HashSet<Edge> set = new HashSet<Edge>(adjacentList1);
	set.addAll(adjacentList2);
	set.remove(edge);
	
	Edge adjacentArray[] = new Edge[set.size()];
	set.toArray(adjacentArray);
	
	return adjacentArray;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 11:05:12 PM)
 * @return cbit.vcell.mapping.potential.Edge[]
 * @param node cbit.vcell.mapping.potential.Node
 */
public Edge[] getAdjacentEdges(Node node) {
	ArrayList<Edge> adjacentList = adjacentEdgeHash.get(node.getName());
	Edge adjacentArray[] = new Edge[adjacentList.size()];
	adjacentList.toArray(adjacentArray);
	return adjacentArray;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 11:02:22 PM)
 * @return int
 * @param node cbit.vcell.mapping.potential.Node
 */
public int getDegree(Node node) {
	//
	// count number of edges that intersect this node (loops count twice)
	//
	int degree = 0;
	for (int i = 0; i < edgeList.size(); i++){
		Edge edge = edgeList.get(i);
		if (edge.hasEndPoint(node)){
			degree++;
			if (edge.isLoop()){
				degree++;
			}
		}
	}
	return degree;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 10:55:30 PM)
 * @return cbit.vcell.mapping.potential.Graph
 */
public Node[] getDigraphAttractorSet(Node seedNode) {
	ArrayList<Edge> availableEdgeList = (ArrayList<Edge>)edgeList.clone();
	ArrayList<Node> attractorNodeList = new ArrayList<Node>();

	//
	// add seed node to includedNodeList
	//
	attractorNodeList.add(seedNode);

	boolean bFoundEdges = true;	
	while (bFoundEdges){
		//
		// add new "Attractor" node if an edge connects from this node to a node that is already "Attractor" set.
		//
		bFoundEdges = false;
		for (int i = 0; i < availableEdgeList.size(); i++){
			Edge edge = availableEdgeList.get(i);
			if (attractorNodeList.contains(edge.getNode2()) && !attractorNodeList.contains(edge.getNode1())){
				attractorNodeList.add(edge.getNode1());
				availableEdgeList.remove(edge);
				i--;
				bFoundEdges = true;
			}
		}
	}

	return attractorNodeList.toArray(new Node[attractorNodeList.size()]);
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 10:55:30 PM)
 * @return cbit.vcell.mapping.potential.Graph
 */
public Node[] getDigraphReachableSet(Node seedNode) {
	ArrayList<Edge> availableEdgeList = (ArrayList<Edge>)edgeList.clone();
	ArrayList<Node> reachableNodeList = new ArrayList<Node>();

	//
	// add seed node to includedNodeList
	//
	reachableNodeList.add(seedNode);

	boolean bFoundEdges = true;	
	while (bFoundEdges){
		//
		// add new "Reachable" node if an edge connects from this node to a node that is already "Reachable"
		//
		bFoundEdges = false;
		for (int i = 0; i < availableEdgeList.size(); i++){
			Edge edge = availableEdgeList.get(i);
			if (reachableNodeList.contains(edge.getNode1()) && !reachableNodeList.contains(edge.getNode2())){
				reachableNodeList.add(edge.getNode2());
				availableEdgeList.remove(edge);
				i--;
				bFoundEdges = true;
			}
		}
	}

	return reachableNodeList.toArray(new Node[reachableNodeList.size()]);
}

/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 10:51:46 PM)
 * @return boolean
 * @param node cbit.vcell.mapping.potential.Node
 */
public Edge getEdge(int index1, int index2){
	Edge[] edges = getAdjacentEdges(nodeList.get(index1));
	for (int i = 0; (edges!=null) && (i < edges.length); i++) {
		if (edges[i].getNode1() == nodeList.get(index1) && edges[i].getNode2() == nodeList.get(index2)){
			return edges[i];
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 12:52:47 PM)
 * @return cbit.vcell.mapping.potential.Edge[]
 */
public Edge[] getEdges() {
	Edge edges[] = new Edge[edgeList.size()];
	edgeList.toArray(edges);
	return edges;
}


public Path[] getFundamentalCycles() {
	return checkFundamentalCycles(false);
}
public static class NoPathBetweenNodesForEdge extends RuntimeException {
	public NoPathBetweenNodesForEdge(String message){
		super(message);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 12:11:08 AM)
 * @return cbit.vcell.mapping.potential.Edge[]
 * @param node1 cbit.vcell.mapping.potential.Node
 * @param node2 cbit.vcell.mapping.potential.Node
 */
public Path[] checkFundamentalCycles(boolean bCheckOnly) {
	
	ArrayList<Path> fundamentalCycleList = new ArrayList<Path>();
		
	Tree spanningTrees[] = getSpanningForest();
	//
	// one-by-one, add the edges not present in any of the spanning trees.
	// each of these edges will make a single cycle.
	//
	for (int i = 0; i < spanningTrees.length; i++){
		Tree spanningTree = spanningTrees[i];
		for (int j = 0; j < edgeList.size(); j++){
			Edge edge = edgeList.get(j);
			//
			// check if this edge is connected to the nodes of this spanning tree.
			// only have to check one node, both nodes of an edge are always contained in a single spanning tree.
			//
			if (spanningTree.contains(edge.getNode1())){
				if (!spanningTree.contains(edge)){
					Path path = spanningTree.getTreePath(edge.getNode1(),edge.getNode2());
					if(path == null){
						throw new NoPathBetweenNodesForEdge("No path found for edge '"+edge.toString()+"' between nodes '"+edge.getNode1()+"' and '"+edge.getNode2()+"'");
					}
					if(!bCheckOnly){
						path.addEdge(edge);
						fundamentalCycleList.add(path);
					}
				}
			}
		}
	}
	if(!bCheckOnly){
		Path fundamentalCycles[] = new Path[fundamentalCycleList.size()];
		fundamentalCycleList.toArray(fundamentalCycles);
		return fundamentalCycles;
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (2/13/2002 4:14:06 PM)
 * @return int
 * @param edge cbit.vcell.mapping.potential.Edge
 */
public int getIndex(Edge edge) {
	for (int i = 0; i < edgeList.size(); i++){
		if (edgeList.get(i).equals(edge)){
			return i;
		}
	}
	return -1;
}


/**
 * Insert the method's description here.
 * Creation date: (2/13/2002 4:14:06 PM)
 * @return int
 * @param edge cbit.vcell.mapping.potential.Edge
 */
public int getIndex(Node node) {
	Integer idx = nodeIndexHash.get(node.getName());
	return idx == null ? -1 : idx;
}


/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 12:54:08 PM)
 * @return cbit.vcell.mapping.potential.Node
 * @param name java.lang.String
 */
public Node getNode(String name) {
	return nodeNameHash.get(name);
}


/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 12:52:47 PM)
 * @return cbit.vcell.mapping.potential.Edge[]
 */
public Node[] getNodes() {
	Node nodes[] = new Node[nodeList.size()];
	nodeList.toArray(nodes);
	return nodes;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 11:23:42 PM)
 * @return int
 */
public int getNumEdges() {
	return edgeList.size();
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 11:23:20 PM)
 * @return int
 */
public int getNumNodes() {
	return nodeList.size();
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 10:55:30 PM)
 * @return cbit.vcell.mapping.potential.Graph
 */
public Tree[] getSpanningForest() {
	ArrayList<Edge> usedEdgeList = new ArrayList<Edge>();
	ArrayList<Node> availableNodeList = (ArrayList<Node>)nodeList.clone();
	ArrayList<Tree> spanningTreeList = new ArrayList<Tree>();

	while (availableNodeList.size()>=1){
		Tree spanningTree = new Tree();
		//
		// pick a node
		//
		spanningTree.addNode(availableNodeList.remove(0));
		boolean bFoundEdges = true;
		while (bFoundEdges){
			//
			// pick an edge where one vertex is included in "spanningTree" and the other vertext is not.
			//
			Edge newEdge = null;
			for (int i = 0; i < edgeList.size(); i++){
				Edge edge = edgeList.get(i);
				int includedNodeCount = 0;
				if (spanningTree.contains(edge.getNode1())){
					includedNodeCount++;
				}
				if (spanningTree.contains(edge.getNode2())){
					includedNodeCount++;
				}
				if (includedNodeCount==1){
					newEdge = edge;
					break;
				}
			}
			if (newEdge!=null){
				spanningTree.addEdge(newEdge);
				// only one of these removes actually removes a node (other node is already removed).
				availableNodeList.remove(newEdge.getNode1());
				availableNodeList.remove(newEdge.getNode2());
			}else{
				//
				// can't find another connected node, either done or try next component
				//
				bFoundEdges = false;
			}
		}
		spanningTreeList.add(spanningTree);
	}
	Tree spanningTrees[] = new Tree[spanningTreeList.size()];
	spanningTreeList.toArray(spanningTrees);
	return spanningTrees;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 10:49:12 PM)
 * @param newNode cbit.vcell.mapping.potential.Node
 */
public void remove(Edge newEdge) {
	if (!edgeList.contains(newEdge)){
		throw new RuntimeException("edge "+newEdge+" not found");
	}
	ArrayList<Edge> adjacentEdges = adjacentEdgeHash.get(newEdge.getNode1().getName());
	adjacentEdges.remove(newEdge);
	adjacentEdges = adjacentEdgeHash.get(newEdge.getNode2().getName());
	adjacentEdges.remove(newEdge);
	edgeList.remove(newEdge);
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 10:49:12 PM)
 * @param newNode cbit.vcell.mapping.potential.Node
 */
public void remove(Node node) {
	if (!nodeList.contains(node)){
		throw new RuntimeException("node "+node+" not found");
	}
	Edge[] adjacentEdges = getAdjacentEdges(node);
	for (int i = 0; i < adjacentEdges.length; i++) {
		remove(adjacentEdges[i]);
	}
	nodeList.remove(node);
}


/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 2:53:25 PM)
 * @return cbit.vcell.mapping.potential.Node[]
 */
public Node[] topologicalSort() {
	//
	// assumes a directed graph (edges are directed).
	//
	NodeInfo nodeInfos[] = depthFirstSearch();
//for (int i = 0; i < nodeInfos.length; i++){
//	System.out.println(nodeInfos[i].toString());
//}
	
	//
	// sort by completion time
	//
		
	Arrays.sort(nodeInfos,new NodeInfo.FinishTimeComparator());

	Node sortedNodes[] = new Node[nodeList.size()];
	for (int i = 0; i < nodeInfos.length; i++){
		Node node = nodeList.get(nodeInfos[i].getNodeIndex());
		sortedNodes[i] = node;
	}
	return sortedNodes;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 11:41:19 PM)
 * @return java.lang.String
 */
public String toString() {
	StringBuffer stringBuffer = new StringBuffer();
	stringBuffer.append("Graph{");
	boolean bFirst = true;
	for (int i = 0; i < nodeList.size(); i++){
		if (bFirst){
			bFirst=false;
		}else{
			stringBuffer.append(",");
		}
		stringBuffer.append(nodeList.get(i));
	}
	for (int i = 0; i < edgeList.size(); i++){
		stringBuffer.append(","+edgeList.get(i));
	}
	stringBuffer.append("}");
	return stringBuffer.toString();
}
}
