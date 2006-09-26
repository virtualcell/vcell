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

/** Implements the dynamic-programming algorithm to determine the
 * structure of an optimal binary search tree, as described in Section
 * 15.5 of <i>Introduction to Algorithms</i>, Second edition.  No
 * method to print an optimal solution is provided, since that would
 * give away the solution to Exercise 15.5-1. */

public class OptimalBinarySearchTree
{
    /** How many keys are in the binary search tree. */
    private int n;

    /** The root of an optimal binary search tree for subproblem.
     * <code>root[i][j]</code> is the root of g an optimal binary
     * search tree containing the keys <i>k</i><sub><i>i</i></sub>,
     * ..., <i>k</i><sub><i>j</i></sub>, for 1 &#x2264; <i>i</i>
     * &#x2264; <i>j</i> &#x2264; <i>n</i>.  Other positions of
     * <code>root</code> are unused.*/
    private int[][] root;	// table of roots of subtrees

    /**
     * Determines the structure of an optimal binary search tree,
     * given an array of probabilities of successful searches for keys
     * and an array of probabilities of unsuccessful searches.
     * Allocates the instance variable <code>root</code> and stores
     * the subproblem roots in it.  Keys are numbered 1 to <i>n</i>.
     *
     * @param p The array of probabilities of successful searches.
     * <code>p[i]</code> is the probability that a search is for
     * <i>k</i><sub><i>i</i></sub>.  Entry <code>p[0]</code> is
     * unused.
     * @param q The array of probabilities of unsuccessful searches.
     * <code>q[i]</code> is the probability that a search falls
     * between keys <i>k</i><sub><i>i</i></sub> and
     * <i>k</i><sub><i>i</i>+1</sub>.  <code>q[0]</code> is the
     * probability that a search falls to the left of
     * <i>k</i><sub>1</sub>, and <code>q[n]</code> is the probability
     * that a search falls to the right of
     * <i>k</i><sub><i>n</i></sub>.
     */
    public OptimalBinarySearchTree(double[] p, double[] q)
    {
	n = p.length - 1;
	root = new int[n+1][n+1];
	optimalBST(p, q, n);
    }

    /**
     * Determines the structure of an optimal binary search tree,
     * given an array of probabilities of successful searches for keys
     * and an array of probabilities of unsuccessful searches.  The
     * instance variable <code>root</code> is assumed to be already
     * allocated.  Keys are numbered 1 to <i>n</i>.  Implements the
     * Optimal-BST procedure on page 361.
     *
     * @param p The array of probabilities of successful searches.
     * <code>p[i]</code> is the probability that a search is for
     * <i>k</i><sub><i>i</i></sub>.  Entry <code>p[0]</code> is
     * unused.
     * @param q The array of probabilities of unsuccessful searches.
     * <code>q[i]</code> is the probability that a search falls
     * between keys <i>k</i><sub><i>i</i></sub> and
     * <i>k</i><sub><i>i</i>+1</sub>.  <code>q[0]</code> is the
     * probability that a search falls to the left of
     * <i>k</i><sub>1</sub>, and <code>q[n]</code> is the probability
     * that a search falls to the right of
     * <i>k</i><sub><i>n</i></sub>.
     */
    public void optimalBST(double[] p, double[] q, int n)
    {
	// e[i][j] is the cost ofan optimal solution for keys ki to
	// kj.  The entries used are e[1..n+1][0..n], where j >= i-1.
	double[][] e = new double[n+2][n+1];

	// w[i][j] is the sum of probabilities p[i] through p[j] and
	// q[i-1] through q[j].  The entries used are w[1..n+1][0..n],
	// where j >= i-1.
	double[][] w = new double[n+2][n+1];

	for (int i = 1; i <= n + 1; i++) {
	    e[i][i-1] = q[i-1];
	    w[i][i-1] = q[i-1];
	}

	for (int l = 1; l <= n; l++) {
	    for (int i = 1; i <= n-l+1; i++) {
		int j = i + l - 1;	
		
		e[i][j] = Double.POSITIVE_INFINITY;
		w[i][j] = w[i][j-1] + p[j] + q[j];

		for (int r = i; r <= j; r++) {
		    double t = e[i][r-1] + e[r+1][j] + w[i][j];
		    if (t < e[i][j]) {
			e[i][j] = t;
			root[i][j] = r;
		    }
		}
	    }
	}
    }

    /** Returns the value of n. */
    public int getNumberOfKeys()
    {
	return n;
    }

    /** Returns the table of roots of subtrees. */
    public int[][] getRootTable()
    {
	return root;
    }
}

// $Id: OptimalBinarySearchTree.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: OptimalBinarySearchTree.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
