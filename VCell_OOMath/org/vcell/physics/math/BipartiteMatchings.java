package org.vcell.physics.math;
import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Node;
/**
 * Insert the type's description here.
 * Creation date: (1/4/2006 3:56:44 PM)
 * @author: Jim Schaff
 */
public class BipartiteMatchings {

	public static class Matching {
		private int[] matches = null;
		private double[] weights = null;
		public Matching(int argCardinality) {
			matches = new int[argCardinality];
			java.util.Arrays.fill(matches,-1);
			weights = new double[argCardinality];
			java.util.Arrays.fill(weights,Double.MIN_VALUE);
		}
		public void add(int u, int v, double argWeight) {
			matches[u] = v;
			matches[v] = u;
			weights[u] = argWeight;
			weights[v] = argWeight;
		}
		public boolean contains(int u){
			return (matches[u] != -1);
		}
		public int getMatch(int u){
			return matches[u];
		}
		public double getWeight(int u){
			return weights[u];
		}
		public void setWeight(int u, double argWeight){
			weights[u] = argWeight;
		}
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			double sum = 0.0;
			buffer.append(super.toString()+": matchings = ");
			for (int i=0;i<matches.length;i++){
				if (weights[i] != Double.MIN_VALUE){
					buffer.append("("+i+","+matches[i]+")="+weights[i]+"  ");
				}else{
					buffer.append("("+i+","+matches[i]+")  ");
				}
			}
			return buffer.toString();
		}
		public boolean contains(int u, int v){
			return matches[u]==v;
		}
		public int countCoveredVertices() {
			int count = 0;
			for (int i=0;i<matches.length;i++){
				if (matches[i] > -1){
					count++;
				}
			}
			return count;
		}
		public double sumOfWeights() {
			double sum = 0;
			for (int i=0;i<matches.length;i++){
				sum += weights[i];
			}
			return sum;
		}
		public void flipAugmentingPath(int[] path){
			//
			// check that it really is an augmenting path
			//

			// first vertex and last vertices should be free and rest should be included in current matching
			for (int i = 0; i < path.length; i++){
				if (i==0 || i==path.length-1){
					if (contains(path[i])){
						throw new RuntimeException("vertex "+i+" not free");
					}
				}else{
					if (!contains(path[i])){
						throw new RuntimeException("vertex "+i+" must be included in matching");
					}
				}
			}
			for (int i = 0; i < path.length-1; i+=2){
				int oldMatching_i = matches[path[i]];
				if (oldMatching_i > -1){
					matches[oldMatching_i] = -1;
				}
				matches[path[i]] = path[i+1];
				
				int oldMatching_ip1 = matches[path[i+1]];
				if (oldMatching_ip1 > -1){
					matches[oldMatching_ip1] = -1;
				}
				matches[path[i+1]] = path[i];
			}
		}
	};

/**
 * BipartiteMatchings constructor comment.
 */
private BipartiteMatchings() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (1/5/2006 4:01:27 PM)
 * @return cbit.util.graph.BipartiteMatchings.Matching
 */
public static int[] findBipartiteAugmentingPath(com.mhhe.clrs2e.AdjacencyListGraph graph, Matching M, int[] color) {
	//
	// color[] is 0 for "left" vertex set and 1 for "right" vertex set
	// for perfect matching, number of 0s and 1s should be same
	//
	
	com.mhhe.clrs2e.BFSInfo[] bfsInfos = new com.mhhe.clrs2e.BFSInfo[graph.getCardV()];
	for (int i = 0; i < bfsInfos.length; i++){
	    bfsInfos[i] = new com.mhhe.clrs2e.BFSInfo();
	}
	//
	// start with arbitrary matching (in order, match one at a time) ... should be maximal?, not maximum
	//
	for (int startVertexIndex = 0; startVertexIndex < graph.getCardV(); startVertexIndex++){
		if (!M.contains(startVertexIndex) && color[startVertexIndex]==0){
			//
			// for this root, clear the info
			//
			for (int j = 0; j < bfsInfos.length; j++){
			    bfsInfos[j].setPredecessor(null);
			    bfsInfos[j].setDistance(Integer.MAX_VALUE);
			    bfsInfos[j].setColor(java.awt.Color.white);
			}
			
			com.mhhe.clrs2e.Vertex s = graph.getVertex(startVertexIndex);
			com.mhhe.clrs2e.BFSInfo sInfo = bfsInfos[startVertexIndex];
			sInfo.setColor(java.awt.Color.gray);
			sInfo.setDistance(0);

			com.mhhe.clrs2e.QueueList q = new com.mhhe.clrs2e.QueueList();
			q.enqueue(s);

			while (!q.isEmpty()){
				com.mhhe.clrs2e.Vertex u = (com.mhhe.clrs2e.Vertex) q.dequeue();
				com.mhhe.clrs2e.BFSInfo uInfo = bfsInfos[u.getIndex()];
				int uDistance = uInfo.getDistance();

				//
				// Enqueue each undiscovered vertex adjacent to u (as long as it is part of an alternating path)
				// if distance is odd, must not be part of matching, if even must be part of matching.
				//
				java.util.Iterator iter = graph.edgeIterator(u);
				while (iter.hasNext()) {
					com.mhhe.clrs2e.Vertex v = (com.mhhe.clrs2e.Vertex)iter.next();
					com.mhhe.clrs2e.BFSInfo vInfo = bfsInfos[v.getIndex()];

					if (vInfo.getColor() == java.awt.Color.white){
						if (color[u.getIndex()] == 0 && !M.contains(u.getIndex(),v.getIndex())){
							//
							// starting from left set, only use edge if not already part of matching
							//
							vInfo.setColor(java.awt.Color.gray);
							vInfo.setDistance(uDistance+1);
							vInfo.setPredecessor(u);
							q.enqueue(v);
						}else if (color[u.getIndex()] == 1 && M.contains(u.getIndex(),v.getIndex())){
							//
							// starting from left set, only use edge if not already part of matching
							//
							vInfo.setColor(java.awt.Color.gray);
							vInfo.setDistance(uDistance+1);
							vInfo.setPredecessor(u);
							q.enqueue(v);
						}
					}
				}
				uInfo.setColor(java.awt.Color.black);
			} // end while(!q.isEmpty())
		} // endif
			
		//
		// find largest odd numbered distance that is not Integer.MAX_VALUE
		//
		int endOfPathIndex = startVertexIndex;
		for (int k = 0; k < bfsInfos.length; k++){
			if (bfsInfos[k].getPredecessor()!=null && !M.contains(k) && bfsInfos[k].getDistance()>bfsInfos[endOfPathIndex].getDistance() && bfsInfos[k].getDistance()%2==1){
				endOfPathIndex = k;
			}
		}
		if (endOfPathIndex != startVertexIndex){
			int length = bfsInfos[endOfPathIndex].getDistance()+1;
			int[] path = new int[length];
			path[length-1] = endOfPathIndex;
			com.mhhe.clrs2e.BFSInfo bfsInfo = bfsInfos[endOfPathIndex];
			for (int k = length-2; k >= 0; k--){
				path[k] = bfsInfos[path[k+1]].getPredecessor().getIndex();
			}
			return path;
		}
	} // end for
	return new int[0];
}


/**
 * Insert the method's description here.
 * Creation date: (1/5/2006 4:01:27 PM)
 * @return cbit.util.graph.BipartiteMatchings.Matching
 */
public static BipartiteMatchings.Matching findMaximumBipartiteMatchingHopcroftKarp(com.mhhe.clrs2e.AdjacencyListGraph agraph, int[] colors) {
	//
	// color[] is 0 for "left" vertex set and 1 for "right" vertex set
	// for perfect matching, number of 0s and 1s should be same
	//
	// uses the Hopcroft-Karp algorithm
	//
	Matching newMatching = new Matching(agraph.getCardV());
	int[] augmentedPath = null;

	do {
		augmentedPath = findBipartiteAugmentingPath(agraph,newMatching,colors);
		if (augmentedPath.length>0){
			newMatching.flipAugmentingPath(augmentedPath);
		}
		//System.out.println(newMatching);
	} while (augmentedPath.length>0);

	return newMatching;
}


/**
 * Insert the method's description here.
 * Creation date: (1/5/2006 4:01:27 PM)
 * @return cbit.util.graph.BipartiteMatchings.Matching
 */
public static BipartiteMatchings.Matching findMaximumCardinalityMatchingUsingMaxFlow(Graph bipartiteGraph, int[] color) {
	//
	// color[] is 0 for "left" vertex set and 1 for "right" vertex set
	// for perfect matching, number of 0s and 1s should be same
	//
	if (color==null || color.length!=bipartiteGraph.getNumNodes()){
		throw new RuntimeException("node assignment missing or wrong");
	}
	int numZeros = 0;
	int numOnes = 0;
	for (int i = 0; i < color.length; i++){
		if (color[i] == 0){
			numZeros++;
		}else if (color[i] == 1){
			numOnes++;
		}
	}
	if (numZeros+numOnes != bipartiteGraph.getNumNodes()){
		throw new RuntimeException("not all colors assigned to 0,1");
	}
	if (numZeros != numOnes){
		throw new RuntimeException("perfect matching is impossible, unequal number of vertices with colors 0 and 1");
	}

	
	com.mhhe.clrs2e.FlowNetwork flowNetwork = new com.mhhe.clrs2e.FlowNetwork(bipartiteGraph.getNumNodes()+2);
	//
	// add verticies in same order as original graph.
	//
	Node[] nodes = bipartiteGraph.getNodes();
	for (int i = 0; i < nodes.length; i++){
		flowNetwork.addVertex(nodes[i].getName());
	}
	//
	// add start and end verticies
	//
	com.mhhe.clrs2e.Vertex startVertex = flowNetwork.addVertex("startVertex");
	int startIndex = nodes.length;
	com.mhhe.clrs2e.Vertex endVertex = flowNetwork.addVertex("endVertex");
	int endIndex = nodes.length+1;

	//
	// add original edges according to original graph connectivity with unitary capacity from color-0 to color-1
	//
	Edge[] edges = bipartiteGraph.getEdges();
	for (int i = 0; i < edges.length; i++){
		int index1 = bipartiteGraph.getIndex(edges[i].getNode1());
		int index2 = bipartiteGraph.getIndex(edges[i].getNode2());
		if (color[index1] == color[index2]){
			throw new RuntimeException("bipartite graphs must connect only from one color to opposite color");
		}
		if (color[index1] == 0 && color[index2] == 1){
			flowNetwork.addEdge(index1,index2,1,0);
		}else if (color[index1] == 1 && color[index2] == 0){
			flowNetwork.addEdge(index2,index1,1,0);
		}else{
			throw new RuntimeException("wrong colors, edge connecting vertices with same color");
		}
	}

	//
	// add connections to start/end depending on color (0 connects to start and 1 connects to end).
	//
	for (int i = 0; i < nodes.length; i++){
		if (color[i] == 0){
			flowNetwork.addEdge(startIndex,i,1,0);
		}else{
			flowNetwork.addEdge(i,endIndex,1,0);
		}
	}

	//
	// compute max flow ... will be a maximal matching of this bipartite graph ... if full cardinality, then a perfect match.
	//
	
	com.mhhe.clrs2e.EdmondsKarp edmondsKarp = new com.mhhe.clrs2e.EdmondsKarp();
	edmondsKarp.computeMaxFlow(flowNetwork,startVertex,endVertex);
	if (flowNetwork.getFlowValue(startVertex)*2 != bipartiteGraph.getNumNodes()){
		throw new RuntimeException("couldn't find a perfect matching");
	}

	
	//
	// perfect matching ... form Matching structure and return
	//
	Matching M = new Matching(flowNetwork.getCardV());
	for (int i = 0; i < flowNetwork.getCardV()-2; i++){
		if (color[i] == 0){
			com.mhhe.clrs2e.FlowNetworkEdgeIterator flowNetworkEdgeIter = flowNetwork.flowNetworkEdgeIterator(i,false);
			while(flowNetworkEdgeIter.hasNext()){
				flowNetworkEdgeIter.next();
				double netFlow = flowNetworkEdgeIter.getNetFlow();
				if (netFlow==1.0){
					com.mhhe.clrs2e.AdjacencyListGraph.Edge edge = (com.mhhe.clrs2e.AdjacencyListGraph.Edge)flowNetworkEdgeIter.getEdge();
					int index1 = i;	
					int index2 = edge.vertex.getIndex();
					M.add(index1,index2,1.0);
				}
			}
		}
	}
	return M;
}


/**
 * Insert the method's description here.
 * Creation date: (1/16/2006 4:54:08 PM)
 * @return int[][]
 * @param graph com.mhhe.clrs2e.WeightedAdjacencyListGraph
 * @param colors int[]
 */
public static BipartiteMatchings.Matching findMaximumWeightedMaximumCardinalityMatching(com.mhhe.clrs2e.WeightedAdjacencyListGraph agraph, int[] colors) {
	//
	// try maximum weight maximum cardinality bipartite matching
	//
	// matrix has rows of color 0 and columns of color 1
	//
	// assume that vertices of the adjacency graph are in color order (0, 0, 0 ....0, 1, 1, 1, ...1)
	//
	int numZeros = 0;
	for (int i = 0; i < agraph.getCardV(); i++){
		if (colors[i]==0){
			numZeros++;
		}
	}
	int numOnes = agraph.getCardV()-numZeros;

	double[][] weightMatrix = new double[numZeros][numOnes];
	for (int i = 0; i < numZeros; i++){
		com.mhhe.clrs2e.WeightedAdjacencyListGraph.EdgeIterator edgeIter = (com.mhhe.clrs2e.WeightedAdjacencyListGraph.EdgeIterator)agraph.edgeIterator(i);
		while (edgeIter.hasNext()){
			com.mhhe.clrs2e.Vertex v = (com.mhhe.clrs2e.Vertex)edgeIter.next();
			weightMatrix[i][v.getIndex()-numZeros] = edgeIter.getWeight();
		}
	}
	Matching matching = new Matching(agraph.getCardV());
	double sum = 0;
	int[][] assignment = edu.maine.graphalgorithms.HungarianAlgorithm.hgAlgorithm(weightMatrix, "max");
	for (int i=0; i<assignment.length; i++) {
		int u = assignment[i][0];
		int v = assignment[i][1];
		matching.add(u,v+numZeros,weightMatrix[u][v]);
		System.out.println("array("+(assignment[i][0])+","+((assignment[i][1]+numZeros))+") = "+weightMatrix[assignment[i][0]][assignment[i][1]]);
		sum = sum + weightMatrix[u][v];
	}
		
	System.out.println("The maximum matching has a sum of: "+sum);
	return matching;
}


/**
 * Insert the method's description here.
 * Creation date: (1/5/2006 5:25:20 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {}


/**
 * Insert the method's description here.
 * Creation date: (1/5/2006 5:25:20 PM)
 * @param args java.lang.String[]
 */
public static void test() {
	Graph graph = new Graph();
	Node f1 = new Node("f1");
	Node f2 = new Node("f2");
	Node f3 = new Node("f3");
	Node z1 = new Node("z1");
	Node z2 = new Node("z2");
	Node z3 = new Node("z3");
	Node nodes[] = { f1, f2, f3, z1, z2, z3 };
	Edge edges[] = {
					new Edge(z1,f1,new Double(1.0)), 
					new Edge(z1,f2,new Double(1.0)), 
					//new Edge(z1,f3,new Double(1.0)), 
					new Edge(z2,f1,new Double(5.0)), 
					//new Edge(z2,f2,new Double(1.0)), 
					new Edge(z2,f3,new Double(1.0)), 
					new Edge(z3,f1,new Double(1.0)), 
					//new Edge(z3,f2,new Double(1.0)),  
					new Edge(z3,f3,new Double(1.0)),  
	};
	int colors[] = { 0, 0, 0, 1, 1, 1 };
	for (int i = 0; i < nodes.length; i++){
		nodes[i].index = i;
		graph.addNode(nodes[i]);
	}
	for (int i = 0; i < edges.length; i++){
		graph.addEdge(edges[i]);
	}

	//
	// create a weighted Adjacency Graph
	//
	com.mhhe.clrs2e.WeightedAdjacencyListGraph agraph = new com.mhhe.clrs2e.WeightedAdjacencyListGraph(nodes.length,false);
	for (int i = 0; i < nodes.length; i++){
		agraph.addVertex(nodes[i].getName());
	}
	for (int i = 0; i < edges.length; i++){
		agraph.addEdge(edges[i].getNode1().index,edges[i].getNode2().index,((Double)edges[i].getData()).doubleValue());
	}

	//
	// get any maximal cardinality matching (ignore weights) using Hopcroft-Karp algorithm
	//
	Matching newMatching = findMaximumBipartiteMatchingHopcroftKarp(agraph,colors);
	System.out.println(newMatching);

	//
	// try maximum weight maximum cardinality bipartite matching (Hungarian algorithm)
	//
	// assume that vertices of the adjacency graph are in color order (0, 0, 0 ... 0, 1, 1, 1, ... 1)
	//
	newMatching = findMaximumWeightedMaximumCardinalityMatching(agraph,colors);
	System.out.println(newMatching);
	
}
}