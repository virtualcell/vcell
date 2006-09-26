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

/** Abstract class for computing all-pairs shortest paths. */

abstract public class AllPairsShortestPaths
{
    /** Computes all-pairs shortest paths.
     *
     * @param g A weighted graph represented as an adjacency matrix.
     * @return A matrix of shortest-path weights.
     */
    abstract public double[][]
	computeShortestPaths(WeightedAdjacencyMatrixGraph g);

    /** Converts a {@link WeightedAdjacencyMatrixGraph} to a matrix of
     * edge weights.
     *
     * @param g A weighted graph represented as an adjacency matrix.
     * @return A matrix of edge weights.  Regardless of what the
     * adjacency matrix has along the diagonal, the returned matrix
     * has all zeros along the diagonal.
     */
    protected double[][] graphToMatrix(WeightedAdjacencyMatrixGraph g)
    {
	int n = g.getCardV();

	double[][] w = new double[n][n];
	for (int i = 0; i < n; i++)
	    for (int j = 0; j < n; j++) {
		if (i == j)
		    w[i][j] = 0;
		else
		    w[i][j] = g.getWeight(i, j);
	    }

	return w;
    }

    /**
     * Prints out a 2-dimensional array of <code>double</code>.
     *
     * @param matrix The array to print.
     */
    public static void printMatrix(double[][] matrix)
    {
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix[i].length; j++)
		System.out.print(matrix[i][j] + "  ");
	    System.out.println();
	}
    }	

    /**
     * Prints out a 2-dimensional array of <code>boolean</code> as 0s
     * and 1s.
     *
     * @param matrix The array to print.
     */
    public static void printMatrix(boolean[][] matrix)
    {
	for (int i = 0; i < matrix.length; i++) {
	    for (int j = 0; j < matrix[i].length; j++)
		System.out.print((matrix[i][j] ? 1 : 0) + "  ");
	    System.out.println();
	}
    }	
}

// $Id: AllPairsShortestPaths.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: AllPairsShortestPaths.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
