/************************************************************************
 *
 * 1. This software is for the purpose of demonstrating one of many
 * ways to implement the algorithms in Introduction to Algorithms,
 * Second edition, by Thomas H. Cormen, Charles E. Leiserson, Ronald
 * L. Rivest, and Clifford Stein.  This software has been tested on a
 * limited set of test cases, but it has not been exhaustively tested.
 * It should not be used for mission-critical applications without
 * further testing.
 *
 * 2. McGraw-Hill licenses and authorizes you to use this software
 * only on a microcomputer located within your own facilities.
 *
 * 3. You will abide by the Copyright Law of the United Sates.
 *
 * 4. You may prepare a derivative version of this software provided
 * that your source code indicates that it based on this software and
 * also that you have made changes to it.
 *
 * 5. If you believe that you have found an error in this software,
 * please send email to clrs-java-bugs@mhhe.com.  If you have a
 * suggestion for an improvement, please send email to
 * clrs-java-suggestions@mhhe.com.
 *
 ***********************************************************************/

package com.mhhe.clrs2e;

import java.util.Iterator;

/** Class to implement Johnson's all-pairs shortest-paths algorithm on
 * page 639 of <i>Introduction to Algorithms</i>, Second
 * edition. */

public class Johnson
{
    /** The weighted, directed graph. */
    private WeightedAdjacencyListGraph g;

    /** <code>true</code> if no negative-weight cycles were detected,
     * <code>false</code> if a negative-weight cycle was detected. */
    private boolean noNegWeightCycle;

    /** The result of running a Johnson's algorithm.  The {@link
     * ShortestPathInfo} object in <code>spInfo[u][v]</code> contains
     * the shortest-path weight from <code>u</code> to <code>v</code>,
     * where <code>u</code> and <code>v</code> are vertex indices,
     * along with <code>v</code>'s predecessor on a shortest path from
     * <code>u</code>.  You can query this information by calling
     * methods of the {@link ShortestPathInfo} class. */
    private ShortestPathInfo[][] spInfo;

    /**
     * Initializes the instance variables, except for allocating the
     * acutal <code>ShortestPathInfo</code> objects in
     * <code>spInfo</code>.
     *
     * @param theGraph The weighted, directed graph.
     */
    public Johnson(WeightedAdjacencyListGraph theGraph)
    {
	g = theGraph;
	noNegWeightCycle = true;
	
	int n = g.getCardV();
	spInfo = new ShortestPathInfo[n][n];
    }

    /** After running <code>computeShortestPaths</code>, returns
     * <code>true</code> if no negative-weight cycles were detected,
     * <code>false</code> if a negative-weight cycle was detected. */
    public boolean hasNoNegativeWeightCycle()
    {
	return noNegWeightCycle;
    }

    /**
     * Returns a reference to the <code>ShortestPathInfo</code> object
     * for a given pair of vertices.
     *
     * @param u One of the vertices.
     * @param v The other vertex.
     */
    public ShortestPathInfo getShortestPathInfo(Vertex u, Vertex v)
    {
	return getShortestPathInfo(u.getIndex(), v.getIndex());
    }

    /**
     * Returns a reference to the <code>ShortestPathInfo</code> object
     * for a given pair of vertices.
     *
     * @param u The index of one of the vertices.
     * @param v The index of the other vertex.
     */
    public ShortestPathInfo getShortestPathInfo(int u, int v)
    {
	return spInfo[u][v];
    }

    /** Computes all-pairs shortest paths using Johnson's algorithm.
     * The results are the <code>ShortestPathInfo</code> objects
     * referenced by the <code>spInfo</code> array, along with the
     * <code>noNegWeightCycle</code> flag. */
    public void computeShortestPaths()
    {
	// Make graph gPrime, which is the same as g but with an extra
	// vertex and 0-weight edges from the extra vertex to all
	// other vertices.  In the text, the extra vertex s is v_0,
	// but here we'll make it the next available index.  The
	// Vertex objects in g will be shared with gPrime.  By making
	// s be at the next available index, every vertex in g will
	// have the same index in both g and gPrime, which is
	// necessary.

	int cardV = g.getCardV();
	WeightedAdjacencyListGraph gPrime =
	    new WeightedAdjacencyListGraph(cardV+1, true);

	Vertex[] vertices = new Vertex[cardV+1];
	Iterator vertexIter = g.vertexIterator();
	while (vertexIter.hasNext())
	    gPrime.addVertex((Vertex) vertexIter.next());
	Vertex s = new Vertex("s");
	gPrime.addVertex(s);

	vertexIter = g.vertexIterator();
	while (vertexIter.hasNext()) {
	    Vertex u = (Vertex) vertexIter.next();
	    WeightedEdgeIterator edgeIter = g.weightedEdgeIterator(u);
	    while (edgeIter.hasNext())
		gPrime.addEdge(u, (Vertex) edgeIter.next(),
			       edgeIter.getWeight());
	    
	    gPrime.addEdge(s, u, 0);
	}

	// Run the Bellman-Ford algorithm on gPrime from the source.
	BellmanFord bf = new BellmanFord(gPrime);
	bf.computeShortestPaths(s);
	if (bf.hasNoNegativeWeightCycle()) {
	    // The graph does not contain a negative-weight cycle, so
	    // we can proceed.  Compute the h values for all vertices
	    // in gPrime.
	    double[] h = new double[cardV+1];
	    for (int i = 0; i <= cardV; i++)
		h[i] = bf.getShortestPathInfo(i).getEstimate();

	    // Reweight all edges of gPrime.
	    vertexIter = gPrime.vertexIterator();
	    while (vertexIter.hasNext()) {
		Vertex u = (Vertex) vertexIter.next();
		int uIndex = u.getIndex();
		double hu = h[uIndex];

		WeightedEdgeIterator edgeIter = gPrime.weightedEdgeIterator(u);
		while (edgeIter.hasNext()) {
		    Vertex v = (Vertex) edgeIter.next();
		    double w = edgeIter.getWeight();
		    double hv = h[v.getIndex()];
		    edgeIter.setWeight(w + hu - hv);
		}
	    }

	    // Run Dijkstra's algorithm from each vertex of G in the
	    // reweighted graph.  "Un-reweight" the results.
	    Iterator uIter = g.vertexIterator();
	    while (uIter.hasNext()) {
		Vertex u = (Vertex) uIter.next();
		int uIndex = u.getIndex();
		double hu = h[uIndex];
		Dijkstra dijk = new Dijkstra(gPrime);
		dijk.computeShortestPaths(u);

		Iterator vIter = g.vertexIterator();
		while (vIter.hasNext()) {
		    Vertex v = (Vertex) vIter.next();
		    int vIndex = v.getIndex();
		    double hv = h[vIndex];

		    spInfo[uIndex][vIndex] =
			dijk.getShortestPathInfo(vIndex);
		    spInfo[uIndex][vIndex].
			setEstimate(spInfo[uIndex][vIndex].getEstimate() +
				    hv - hu);
		}
	    }	    
	}
    }
}

// $Id: Johnson.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: Johnson.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
