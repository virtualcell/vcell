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

/** Implements the Floyd-Warshall algorithm for all-pairs shortest
 * paths from page 630 and the Transitive-Closure algorithm from
 * page 633 of <i>Introduction to Algorithms</i>, Second
 * edition. */

public class FloydWarshall extends AllPairsShortestPaths
{
    /** Computes all-pairs shortest paths.
     *
     * @param g A weighted graph represented as an adjacency matrix.
     * @return A matrix of shortest-path weights.
     */
    public double[][] computeShortestPaths(WeightedAdjacencyMatrixGraph g)
    {
	int n = g.getCardV();

	// Array d is triply indexed.  The first index is the
	// iteration number, and the last two indices are the vertex
	// indices i and j.  Think of d as indexed by d[k][i][j].

	// Warning: Since i and j index vertices, they run from 0 to
	// n-1.  Since k indexes an iteration, it runs from 0 to n.
	// But when we use k to index a vertex, we need to subtract 1.
	double d[][][] = new double[n+1][n][n];

	// d[0] is the weight matrix.
	d[0] = graphToMatrix(g);

	// Run the main loop.
	for (int k = 1; k <= n; k++) {
	    for (int i = 0; i < n; i++)
		for (int j = 0; j < n; j++)
		    d[k][i][j] = Math.min(d[k-1][i][j],
					  d[k-1][i][k-1] + d[k-1][k-1][j]);
	}

	return d[n];
    }

    /** Computes the transitive closure of a directed graph.
     *
     * @param g A graph represented as an adjacency matrix.
     * @return A transitive-closure matrix in which the [i][j] entry
     * is <code>true</code> if there is a path from vertex
     * <code>i</code> to vertex <code>j</code>, <code>false</code>
     * otherwise.
     */
    public boolean[][] computeTransitiveClosure(AdjacencyMatrixGraph g)
    {
	int n = g.getCardV();

	// Array t is triply indexed.  The first index is the
	// iteration number, and the last two indices are the vertex
	// indices i and j.  Think of t as indexed by t[k][i][j].

	// Warning: Since i and j index vertices, they run from 0 to
	// n-1.  Since k indexes an iteration, it runs from 0 to n.
	// But when we use k to index a vertex, we need to subtract 1.
	boolean t[][][] = new boolean[n+1][n][n];

	// Start by computing t[0].
	for (int i = 0; i < n; i++)
	    for (int j = 0; j < n; j++)
		t[0][i][j] = (i == j) || g.edgeExists(i, j);

	// Run the main loop.
	for (int k = 1; k <= n; k++) {
	    for (int i = 0; i < n; i++)
		for (int j = 0; j < n; j++)
		    t[k][i][j] = t[k-1][i][j] ||
			(t[k-1][i][k-1] && t[k-1][k-1][j]);
	}

	return t[n];
    }
}

// $Id: FloydWarshall.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: FloydWarshall.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
