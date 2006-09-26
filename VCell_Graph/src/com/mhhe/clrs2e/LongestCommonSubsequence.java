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

/** Implements the dynamic-programming algorithm to find a longest
 * common subsequence of two strings, as described in Section 15.4 of
 * <i>Introduction to Algorithms</i>, Second edition. */

public class LongestCommonSubsequence
{
    private static final Direction UP = new Direction("UP");
    private static final Direction LEFT = new Direction("LEFT");
    private static final Direction UP_AND_LEFT = new Direction("UP AND LEFT");

    /** Inner class for a typesafe enum pattern for directions in a
     * two-dimensional array. */
    private static class Direction
    {
	/** The direction indicated. */
	private final String name;

	/**
	 * Creates a new <code>Direction</code>.
	 *
	 * @param name The name to store and use.
	 */
	public Direction(String name)
	{
	    this.name = name;
	}

	/** Returns the <code>String</code> representation of this
	 * <code>Direction</code>. */
	public String toString()
	{
	    return name;
	}
    }

    /** The length of an LCS of a subproblem.  <code>c[i][j]</code> is
     * the length of an LCS of prefixes <i>X</i><sub><i>i</i></sub>
     * and <i>Y</i><sub><i>j</i></sub>, for 0 &#x2264; <i>i</i>
     * &#x2264; <i>m</i>-1 and 0 &#x2264; <i>j</i> &#x2264;
     * <i>n</i>-1. */
    private int[][] c;

    /** The table entry used in constructing an LCS of prefixes
     * <i>X</i><sub><i>i</i></sub> and <i>Y</i><sub><i>j</i></sub>,
     * for 0 &#x2264; <i>i</i> &#x2264; <i>m</i>-1 and 0 &#x2264;
     * <i>j</i> &#x2264; <i>n</i>-1. */
    private Direction[][] b;

    /** How many entries are in <i>X</i>. */
    private final int m;

    /** How many entries are in <i>Y</i>. */
    private final int n;

    /** The input X.  Needed for reconstructing an optimal solution. */
    private final String x;

    /**
     * Computes an LCS of two strings, allocating the instance
     * variables and storing the result in them.
     *
     * @param x The string <i>X</i>.
     * @param y The string <i>Y</i>.
     */
    public LongestCommonSubsequence(String x, String y)
    {
	m = x.length();
	n = y.length();
	this.x = x;

	c = new int[m+1][n+1];
	b = new Direction[m+1][n+1];

	lcsLength(x, y);
    }

    /**
     * Computes an LCS of two strings, storing the result in the
     * instance variables.  The instance variables are assumed to be
     * already allocated.  Implements the LCS-Length procedure on page
     * 353.
     *
     * @param x The string <i>X</i>.
     * @param y The string <i>Y</i>.
     */
    private void lcsLength(String x, String y)
    {
	for (int i = 0; i <= m; i++)
	    c[i][0] = 0;
	
	for (int j = 0; j <= n; j++)
	    c[0][j] = 0;

	for (int i = 1; i <= m; i++) {
	    for (int j = 1; j <= n; j++) {
		if (index(x, i) == index(y, j)) {
		    c[i][j] = c[i-1][j-1] + 1;
		    b[i][j] = UP_AND_LEFT;
		}
		else {
		    if (c[i-1][j] >= c[i][j-1]) {
			c[i][j] = c[i-1][j];
			b[i][j] = UP;
		    }
		    else {
			c[i][j] = c[i][j-1];
			b[i][j] = LEFT;
		    }
		}
	    }
	}
    }

    /**
     * Returns a given character from a <code>String</code>.
     * Compensates for character positions in a <code>String</code>
     * being indexed from 0, rather than from 1 as in Section 15.4.
     *
     * @param z A <code>String</code>.
     * @param k An index into <code>z</code>, but starting from 1.
     * @return Returns the <code>k</code>th character of
     * <code>z</code>.
     */
    private char index(String z, int k)
    {
	return z.charAt(k-1);
    }

    /**
     * Returns an LCS of prefixes <i>X</i><sub><i>i</i></sub> and
     * <i>Y</i><sub><i>j</i></sub>.  Implements the Print-LCS
     * procedure on page 355.
     *
     * @param i Index into <i>X</i>.
     * @param j Index into <i>Y</i>.
     * @return A <code>String</code> that is an LCS of
     * <i>X</i><sub><i>i</i></sub> and <i>Y</i><sub><i>j</i></sub>.
     */
    private String printLCS(int i, int j)
    {
	if (i == 0 || j == 0)
	    return "";

	if (b[i][j] == UP_AND_LEFT)
	    return printLCS(i-1, j-1) + index(x, i);
	else if (b[i][j] == UP)
	    return printLCS(i-1, j);
	else
	    return printLCS(i, j-1);
    }

    /** Returns an LCS of <i>X</i> and <i>Y</i> as a
     * <code>String</code>. */
    public String toString()
    {
	return printLCS(m, n);
    }
}

// $Id: LongestCommonSubsequence.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: LongestCommonSubsequence.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
