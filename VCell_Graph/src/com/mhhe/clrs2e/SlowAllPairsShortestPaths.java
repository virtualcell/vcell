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

/** Implements the Slow-All-Pairs-Shortest-Paths procedure on
 * page 625 of <i>Introduction to Algorithms</i>, Second
 * edition. */

public class SlowAllPairsShortestPaths extends APSPMatrixMult
{
    /** Computes all-pairs shortest paths.
     *
     * @param g A weighted graph represented as an adjacency matrix.
     * @return A matrix of shortest-path weights.
     */
    public double[][] computeShortestPaths(WeightedAdjacencyMatrixGraph g)
    {
	int n = g.getCardV();

	double[][] w = graphToMatrix(g);

	// Array l is triply indexed.  The first index is the
	// superscript, and the last two indices are the vertex
	// indices i and j.  Think of l as indexed by l[m][i][j].  We
	// do not use l[0].

	double[][][] l = new double[n][][];
	l[1] = w;

	for (int m = 2; m < n; m++)
	    l[m] = extendShortestPaths(l[m-1], w);

	return l[n-1];
    }
}

// $Id: SlowAllPairsShortestPaths.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: SlowAllPairsShortestPaths.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
