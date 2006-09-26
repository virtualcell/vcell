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

/** Abstract class for computing single-source shortest paths.
 * Defines methods for algorithms in Chapter 24 of <i>Introduction to
 * Algorithms</i>, Second edition. */

abstract public class SingleSourceShortestPaths
{
    /** The graph for which we are computing single-source shortest
     * paths. */
    protected WeightedAdjacencyListGraph g;

    /** <code>true</code> if no negative-weight cycles were detected,
     * <code>false</code> if a negative-weight cycle was detected. */
    protected boolean noNegWeightCycle;

    /** The result of running a single-source shortest-paths
     * algorithm.  Each object in this array corresponds to a vertex
     * of the graph, and you can query its answers by calling methods
     * of the {@link ShortestPathInfo} class. */
    private ShortestPathInfo[] spInfo;

    /**
     * Sets up the instance variables, including allocation of the
     * <code>spInfo</code> array but <em>not</em> allocation of the
     * <code>ShortestPathInfo</code> objects referenced by the array.
     * (That's {@link #initializeSingleSource}'s job.)
     *
     * @param theGraph The graph for which we are computing
     * single-source shortest paths.
     */
    protected SingleSourceShortestPaths(WeightedAdjacencyListGraph theGraph)
    {
	g = theGraph;
	noNegWeightCycle = true; // haven't found one yet
	spInfo = new ShortestPathInfo[g.getCardV()];
    }

    /**
     * Computes single-source shortest paths from a given source
     * vertex, filling in weights and predecessors in the
     * <code>spInfo</code> array.
     *
     * @param s The source vertex.
     */
    abstract public void computeShortestPaths(Vertex s);

    /**
     * Initializes a single-source shortest-paths algorithm.
     *
     * @param s The source vertex.
     */
    public void initializeSingleSource(Vertex s)
    {
	for (int i = 0; i < spInfo.length; i++)
	    spInfo[i] = newShortestPathInfo();

	getShortestPathInfo(s).setEstimate(0);
    }

    /** Returns a new <code>ShortestPathInfo</code> object.  This
     * method may be overridden in subclasses. */
    protected ShortestPathInfo newShortestPathInfo()
    {
	return new ShortestPathInfo();
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
     * for a given vertex.
     *
     * @param v The vertex for which the corresponding
     * <code>ShortestPathInfo</code> is returned.
     */
    public ShortestPathInfo getShortestPathInfo(Vertex v)
    {
	return getShortestPathInfo(v.getIndex());
    }

    /**
     * Returns a reference to the <code>ShortestPathInfo</code> object
     * for a given vertex.
     *
     * @param v The index of the vertex for which the corresponding
     * <code>ShortestPathInfo</code> is returned.
     */
    public ShortestPathInfo getShortestPathInfo(int v)
    {
	return spInfo[v];
    }
}

// $Id: SingleSourceShortestPaths.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: SingleSourceShortestPaths.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
