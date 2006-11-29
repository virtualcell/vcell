package cbit.util.graph;
import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (2/10/2002 10:47:30 PM)
 * @author: Jim Schaff
 */
public class Graph {
	private Vector<Node> nodeList = new Vector<Node>();
	private Vector<Edge> edgeList = new Vector<Edge>();

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
	nodeList = new Vector<Node>(graph.nodeList);
	edgeList = new Vector<Edge>(graph.edgeList);
}

/**
 * Graph constructor comment.
 */
public Graph(com.mhhe.clrs2e.Graph algGraph) {
	super();
	int numVertices = algGraph.getCardV();
	int numEdges = algGraph.getCardE();
	for (int i = 0; i < numVertices; i++){
		com.mhhe.clrs2e.Vertex vertex = algGraph.getVertex(i);
		addNode(new Node(vertex.getName(),vertex));
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 10:49:12 PM)
 * @param newNode cbit.vcell.mapping.potential.Node
 */
public void addEdge(Edge newEdge) {
	if (edgeList.contains(newEdge)){
		throw new RuntimeException("edge "+newEdge+" already exists in list");
	}
	if (!nodeList.contains(newEdge.getNode1())){
		addNode(newEdge.getNode1());
	}
	if (!nodeList.contains(newEdge.getNode2())){
		addNode(newEdge.getNode2());
	}
	edgeList.addElement(newEdge);
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 10:49:12 PM)
 * @param newNode cbit.vcell.mapping.potential.Node
 */
public void addNode(Node newNode) {
	if (nodeList.contains(newNode)){
		throw new RuntimeException("node "+newNode+" already exists in list");
	}
	if (newNode == null){
		throw new RuntimeException("cannot add a null node to a graph");
	}
	nodeList.addElement(newNode);
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 10:51:46 PM)
 * @return boolean
 * @param node cbit.vcell.mapping.potential.Node
 */
public boolean contains(Edge edge) {
	return edgeList.contains(edge);
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 10:51:46 PM)
 * @return boolean
 * @param node cbit.vcell.mapping.potential.Node
 */
public boolean contains(Node node) {
	return nodeList.contains(node);
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
	Node thisNode = (Node)nodeList.get(index);
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
	Vector adjacentList = new Vector();
	for (int i = 0; i < edgeList.size(); i++){
		Edge e = (Edge)edgeList.elementAt(i);
		if (e.equals(edge)){
			continue;
		}
		if (e.isAdjacent(edge)){
			adjacentList.add(edge);
		}
	}
	Edge adjacentArray[] = new Edge[adjacentList.size()];
	adjacentList.copyInto(adjacentArray);
	
	return adjacentArray;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2002 11:05:12 PM)
 * @return cbit.vcell.mapping.potential.Edge[]
 * @param node cbit.vcell.mapping.potential.Node
 */
public Edge[] getAdjacentEdges(Node node) {
	Vector adjacentList = new Vector();
	for (int i = 0; i < edgeList.size(); i++){
		Edge edge = (Edge)edgeList.elementAt(i);
		if (edge.hasEndPoint(node)){
			adjacentList.add(edge);
		}
	}
	Edge adjacentArray[] = new Edge[adjacentList.size()];
	adjacentList.copyInto(adjacentArray);
	
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
		Edge edge = (Edge)edgeList.elementAt(i);
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
	Vector<Edge> availlableEdgeList = (Vector<Edge>)edgeList.clone();
	Vector<Node> attractorNodeList = new Vector<Node>();

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
		for (int i = 0; i < availlableEdgeList.size(); i++){
			Edge edge = (Edge)availlableEdgeList.elementAt(i);
			if (attractorNodeList.contains(edge.getNode2()) && !attractorNodeList.contains(edge.getNode1())){
				attractorNodeList.add(edge.getNode1());
				availlableEdgeList.remove(edge);
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
	Vector<Edge> availlableEdgeList = (Vector<Edge>)edgeList.clone();
	Vector<Node> reachableNodeList = new Vector<Node>();

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
		for (int i = 0; i < availlableEdgeList.size(); i++){
			Edge edge = (Edge)availlableEdgeList.elementAt(i);
			if (reachableNodeList.contains(edge.getNode1()) && !reachableNodeList.contains(edge.getNode2())){
				reachableNodeList.add(edge.getNode2());
				availlableEdgeList.remove(edge);
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
	for (int i = 0; i < edgeList.size(); i++){
		Edge edge = (Edge)edgeList.elementAt(i);
		if (edge.getNode1().index == index1 && edge.getNode2().index == index2){
			return edge;
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
	edgeList.copyInto(edges);
	return edges;
}


/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 12:11:08 AM)
 * @return cbit.vcell.mapping.potential.Edge[]
 * @param node1 cbit.vcell.mapping.potential.Node
 * @param node2 cbit.vcell.mapping.potential.Node
 */
public Path[] getFundamentalCycles() {
	
	Vector fundamentalCycleList = new Vector();
		
	Tree spanningTrees[] = getSpanningForest();
	//
	// one-by-one, add the edges not present in any of the spanning trees.
	// each of these edges will make a single cycle.
	//
	for (int i = 0; i < spanningTrees.length; i++){
		Tree spanningTree = spanningTrees[i];
		for (int j = 0; j < edgeList.size(); j++){
			Edge edge = (Edge)edgeList.elementAt(j);
			//
			// check if this edge is connected to the nodes of this spanning tree.
			// only have to check one node, both nodes of an edge are always contained in a single spanning tree.
			//
			if (spanningTree.contains(edge.getNode1())){
				if (!spanningTree.contains(edge)){
					Path path = spanningTree.getTreePath(edge.getNode1(),edge.getNode2());
					path.addEdge(edge);
					fundamentalCycleList.add(path);
				}
			}
		}
	}
	Path fundamentalCycles[] = new Path[fundamentalCycleList.size()];
	fundamentalCycleList.copyInto(fundamentalCycles);

	return fundamentalCycles;
}


/**
 * Insert the method's description here.
 * Creation date: (2/13/2002 4:14:06 PM)
 * @return int
 * @param edge cbit.vcell.mapping.potential.Edge
 */
public int getIndex(Edge edge) {
	for (int i = 0; i < edgeList.size(); i++){
		if (((Edge)edgeList.elementAt(i)).equals(edge)){
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
	for (int i = 0; i < nodeList.size(); i++){
		if (((Node)nodeList.elementAt(i)).equals(node)){
			return i;
		}
	}
	return -1;
}


/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 12:54:08 PM)
 * @return cbit.vcell.mapping.potential.Node
 * @param name java.lang.String
 */
public Node getNode(String name) {
	for (int i = 0; i < nodeList.size(); i++){
		Node node = (Node)nodeList.elementAt(i);
		if (node.getName().equals(name)){
			return node;
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (2/12/2002 12:52:47 PM)
 * @return cbit.vcell.mapping.potential.Edge[]
 */
public Node[] getNodes() {
	Node nodes[] = new Node[nodeList.size()];
	nodeList.copyInto(nodes);
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
	Vector usedEdgeList = new Vector();
	Vector availlableNodeList = (Vector)nodeList.clone();
	Vector spanningTreeList = new Vector();

	while (availlableNodeList.size()>=1){
		Tree spanningTree = new Tree();
		//
		// pick a node
		//
		spanningTree.addNode((Node)availlableNodeList.remove(0));
		boolean bFoundEdges = true;
		while (bFoundEdges){
			//
			// pick an edge where one vertex is included in "spanningTree" and the other vertext is not.
			//
			Edge newEdge = null;
			for (int i = 0; i < edgeList.size(); i++){
				Edge edge = (Edge)edgeList.elementAt(i);
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
				availlableNodeList.remove(newEdge.getNode1());
				availlableNodeList.remove(newEdge.getNode2());
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
	spanningTreeList.copyInto(spanningTrees);
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
		Node node = (Node)nodeList.elementAt(nodeInfos[i].getNodeIndex());
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
		stringBuffer.append((Node)nodeList.elementAt(i));
	}
	for (int i = 0; i < edgeList.size(); i++){
		stringBuffer.append(","+(Edge)edgeList.elementAt(i));
	}
	stringBuffer.append("}");
	return stringBuffer.toString();
}
}