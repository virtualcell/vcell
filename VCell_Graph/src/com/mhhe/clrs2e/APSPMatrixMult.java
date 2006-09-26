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

/**
 * Abstract class that defines the <code>extendShortestPaths</code>
 * method used by subclasses of {@link AllPairsShortestPaths}.
 */

public abstract class APSPMatrixMult extends AllPairsShortestPaths
{
    /**
     * Extends the shortest paths given in one matrix by the edge
     * weights given in another matrix.
     *
     * @param l Matrix of current shortest-path weights.
     * @param w Matrix of edge weights.
     * @return A matrix of shortest-path weights of the weights in
     * <code>l</code> extended by the weights of the edges in
     * <code>w</code>.
     */
    protected double[][] extendShortestPaths(double[][] l, double[][] w)
    {
	int n = l.length;

	double[][] lPrime = new double[n][n];

	for (int i = 0; i < n; i++)
	    for (int j = 0; j < n; j++) {
		lPrime[i][j] = Double.POSITIVE_INFINITY;

		for (int k = 0; k < n; k++)
		    lPrime[i][j] = Math.min(lPrime[i][j],
					    l[i][k] + w[k][j]);
	    }

	return lPrime;
    }
}

// $Id: APSPMatrixMult.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: APSPMatrixMult.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
