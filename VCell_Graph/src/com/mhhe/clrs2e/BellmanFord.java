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

/** Implementation of the Bellman-Ford algorithm on page 588 of
 * <i>Introduction to Algorithms</i>, Second edition. */

public class BellmanFord extends SingleSourceShortestPaths
{
    /**
     * Sets up the instance variables, including allocation of the
     * <code>spInfo</code> array but <em>not</em> allocation of the
     * <code>ShortestPathInfo</code> objects referenced by the array.
     * (That's {@link #initializeSingleSource}'s job.)
     *
     * @param theGraph The graph for which we are computing
     * single-source shortest paths.
     */
    public BellmanFord(WeightedAdjacencyListGraph theGraph)
    {
	super(theGraph);
    }

    /**
     * Computes single-source shortest paths from a given source
     * vertex, filling in weights and predecessors in the
     * <code>spInfo</code> array.  Also sets the instance variable
     * <code>noNegWeightCycle</code> appropriately.
     *
     * @param s The source vertex.
     */
    public void computeShortestPaths(Vertex s)
    {
	initializeSingleSource(s);

	int cardV = g.getCardV();

	// Run through all edges |V|-1 times.
	for (int i = 1; i <= cardV-1; i++) {
	    Iterator vertexIter = g.vertexIterator();

	    while (vertexIter.hasNext()) {
		Vertex u = (Vertex) vertexIter.next();
		double du = getShortestPathInfo(u).getEstimate();
		WeightedEdgeIterator edgeIter = g.weightedEdgeIterator(u);

		while (edgeIter.hasNext()) {
		    Vertex v = (Vertex) edgeIter.next();
		    double w = edgeIter.getWeight();
		    getShortestPathInfo(v).relax(u, du, w);
		}
	    }
	}

	// One more pass to see if a relaxation would have changed an
	// edge.
	Iterator vertexIter = g.vertexIterator();

	while (vertexIter.hasNext()) {
	    Vertex u = (Vertex) vertexIter.next();
	    double du = getShortestPathInfo(u).getEstimate();
	    WeightedEdgeIterator edgeIter = g.weightedEdgeIterator(u);

	    while (edgeIter.hasNext()) {
		Vertex v = (Vertex) edgeIter.next();
		double w = edgeIter.getWeight();
		if (getShortestPathInfo(v).getEstimate() > du + w) {
		    noNegWeightCycle = false;
		    return;
		}
	    }
	}
    }
}

// $Id: BellmanFord.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: BellmanFord.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
