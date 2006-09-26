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
 * optimal order in which to multiply a chain of matrices, as
 * described in Section 15.2 of <i>Introduction to Algorithms</i>,
 * Second edition. */

public class MatrixChainMultiply
{
    /** The value of an optimal solution to a subproblem.
     * <code>m[i][j]</code> is the minimum number of scalar
     * multiplications needed to compute <i>A</i><sub>i</sub>
     * <i>A</i><sub>i+1</sub> ... <i>A</i><sub>j</sub>, for 1 &#x2264;
     * <i>i</i> &#x2264; <i>j</i> &#x2264; <i>n</i>.  Entries
     * <code>m[i][j]</code> for <i>i</i> = 0, <i>j</i> = 0, or
     * <i>i</i> &gt; <i>j</i> are unused. */
    private int[][] m;

    /** The position to split an optimal solution to a subproblem.
     * <code>s[i][j]</code> is an index <i>k</i> such that
     * <code>m[i][j]</code> = <code>m[i][k]</code> +
     * <code>m[k+1][j]</code> + <code>p[i-1] * p[k] * p[j]</code>, for
     * <i>i</i> = 1, 2, ..., <i>n</i>-1 and <i>j</i> = 2, 3, ...,
     * <i>n</i>.  Entries <code>s[i][j]</code> for <i>i</i> = 0,
     * <i>i</i> = <i>n</i>, <i>j</i> &#x2264; 1, or <i>i</i> &gt;
     * <i>j</i> are unused.  */
    private int[][] s;

    /** How many matrices are in the chain. */
    private int n;

    /**
     * Computes an optimal parenthesization of a matrix-chain product,
     * allocating the instance variables and storing the result in
     * them.
     *
     * @param p An array of dimensions, where matrix
     * <i>A</i><sub>i</sub> is <code>p[i-1]</code> x
     * <code>p[i]</code>, for <i>i</i> = 1, 2, ..., <i>n</i>.
     */
    public MatrixChainMultiply(int[] p)
    {
	n = p.length - 1;	// how many matrices are in the chain
	m = new int[n+1][n+1];	// overallocate m, so that we don't use index 0
	s = new int[n+1][n+1];	// same for s
	matrixChainOrder(p);	// run the dynamic-programming algorithm
    }

    /**
     * Computes an optimal parenthesization of a matrix-chain product,
     * storing the result in the instance variables.  The instance
     * variables are assumed to be already allocated.  Implements the
     * Matrix-Chain-Order procedure on page 336.
     *
     * @param p An array of dimensions, where matrix
     * <i>A</i><sub>i</sub> is <code>p[i-1]</code> x
     * <code>p[i]</code>, for <i>i</i> = 1, 2, ..., <i>n</i>.
     */
    private void matrixChainOrder(int[] p)
    {
	// Initial the cost for the empty subproblems.
	for (int i = 1; i <= n; i++)
	    m[i][i] = 0;

	// Solve for chains of increasing length l.
	for (int l = 2; l <= n; l++) {
	    for (int i = 1; i <= n-l+1; i++) {
		int j = i + l - 1;
		m[i][j] = Integer.MAX_VALUE;

		// Check each possible split to see if it's better
		// than all seen so far.
		for (int k = i; k < j; k++) {
		    int q = m[i][k] + m[k+1][j] + p[i-1] * p[k] * p[j];
		    if (q < m[i][j]) {
			// q is the best split for this subproblem so far.
			m[i][j] = q;
			s[i][j] = k;
		    }
		}
	    }
	}
    }

    /**
     * Returns a <code>String</code> describing an optimal
     * parenthesization of a subproblem.  Implements the
     * Print-Optimal-Parens procedure on page 338.
     *
     * @param i Index of one array.
     * @param j Index of another array.
     * @return A <code>String</code> describing an optimal
     * parenthesization of <i>A</i><sub>i</sub> <i>A</i><sub>i+1</sub>
     * ... <i>A</i><sub>j</sub>. */
    private String printOptimalParens(int i, int j)
    {
	if (i == j)
	    return "A[" + i + "]";
	else
	    return "(" + printOptimalParens(i, s[i][j]) +
		printOptimalParens(s[i][j] + 1, j) + ")";
    }

    /** Returns a <code>String</code> describing an optimal
     * parenthesization of the entire matrix chain. */
    public String toString()
    {
	return printOptimalParens(1, n);
    }
}

// $Id: MatrixChainMultiply.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: MatrixChainMultiply.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
