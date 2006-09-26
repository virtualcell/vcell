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

/** Implementation of assembly-line scheduling for two lines, as
 * described in Section 15.1 of <i>Introduction to Algorithms</i>,
 * Second edition.  Because this is Java code, numbering starts from
 * 0, so that the lines are 0 and 1 and station numbers are 0, 1, ...,
 * <i>n</i>-1. */

public class AssemblyLine
{
    /** The number of stations on each line. */
    private int n;

    /** The value of an optimal solution to a subproblem.
     * <code>f[i][j]</code> is the fastest possible time to get from
     * the starting point through station
     * <i>S</i><sub><i>i</i>,<i>j</i></sub>, for <code>i</code> = 0, 1
     * and <code>j</code> = 0, 1, ..., <code>n</code>-1. */
    private double[][] f;

    /** The fastest way to get all the way through the factory. */
    private double fStar;

    /** The station used in an optimal solution to a subproblem.
     * <code>l[i][j]</code> is the line number whose station precedes
     * <i>S</i><sub><i>i</i>,<i>j</i></sub> on a fastest way from the
     * starting point through station
     * <i>S</i><sub><i>i</i>,<i>j</i></sub>, for <code>i</code> = 0, 1
     * and <code>j</code> = 0, 1, ..., <code>n</code>-1. */
    private int[][] l;

    /** The line whose station <i>n</i> is used in a fastest way
     * through the factory. */
    private int lStar;

    /**
     * Computes the fastest way through the factory, allocating the
     * instance variables and storing the result in them.
     *
     * @param a <code>a[i][j]</code> is the assembly time at station
     * <i>S</i><sub><i>i</i>,<i>j</i></sub>.
     * @param t <code>t[i][j]</code> is the time to transfer from one
     * assembly line to the other after going through station
     * <i>S</i><sub><i>i</i>,<i>j</i></sub>.
     * @param e <code>e[i]</code> is the entry time for line
     * <code>i</code>, for <code>i</code> = 0, 1.
     * @param x <code>e[i]</code> is the exit time for line
     * <code>i</code>, for <code>i</code> = 0, 1.
     * @param n The number of stations on each line.
     */
    public AssemblyLine(double[][] a,
			double[][] t,
			double[] e,
			double[] x,
			int n)
    {
	this.n = n;

	// Allocate arrays to hold the computation.
	f = new double[2][n];
	l = new int[2][n];

	//  Compute the fastest way.
	fastestWay(a, t, e, x, n);
    }

    /**
     * Computes the fastest way through the factory, storing the
     * result in the instance variables.  The instance variables are
     * assumed to be already allocated.  Implements the Fastest-Way
     * procedure on page 329.
     *
     * @param a <code>a[i][j]</code> is the assembly time at station
     * <i>S</i><sub><i>i</i>,<i>j</i></sub>.
     * @param t <code>t[i][j]</code> is the time to transfer from one
     * assembly line to the other after going through station
     * <i>S</i><sub><i>i</i>,<i>j</i></sub>.
     * @param e <code>e[i]</code> is the entry time for line
     * <code>i</code>, for <code>i</code> = 0, 1.
     * @param x <code>e[i]</code> is the exit time for line
     * <code>i</code>, for <code>i</code> = 0, 1.
     * @param n The number of stations on each line.
     */
    private void fastestWay(double[][] a,
			      double[][] t,
			      double[] e,
			      double[] x,
			      int n)
    {
	f[0][0] = e[0] + a[0][0];
	f[1][0] = e[1] + a[1][0];
	
	for (int j = 1; j < n; j++) {
	    if (f[0][j-1] + a[0][j] <= f[1][j-1] + t[1][j-1] + a[0][j]) {
		f[0][j] = f[0][j-1] + a[0][j];
		l[0][j] = 0;
	    } 
	    else {
		f[0][j] = f[1][j-1] + t[1][j-1] + a[0][j];
		l[0][j] = 1;
	    }

	    if (f[1][j-1] + a[1][j] <= f[0][j-1] + t[0][j-1] + a[1][j]) {
		f[1][j] = f[1][j-1] + a[1][j];
		l[1][j] = 1;
	    } 
	    else {
		f[1][j] = f[0][j-1] + t[0][j-1] + a[1][j];
		l[1][j] = 0;
	    }
	}
	
	if (f[0][n-1] + x[0] <= f[1][n-1] + x[1]) {
	    fStar = f[0][n-1] + x[0];
	    lStar = 0;
	} 
	else {
	    fStar = f[1][n-1] + x[1];
	    lStar = 1;
	}
    }

    /** Returns the time taken by the fastest way to get all the way
     * through the factory. */
    public double getFastestTime()
    {
	return fStar;
    }

    /**
     * Returns the line numbers used in a fastest way through the
     * factory.  Based on the Print-Stations procedure on page 330.
     *
     * @return An array, say <code>r</code>, such that station
     * <i>S</i><sub><code>r[j]</code>,<code>j</code></sub> is used in
     * a fastest way through the factory.
     */
    public int[] getFastestRoute()
    {
	int[] r = new int[n];	// the array to return
	int i = lStar;
	
	r[n-1] = i;

	for (int j = n-1; j >= 1; j--) {
	    i = l[i][j];
	    r[j-1] = i;
	}

	return r;
    }

    /** Returns the <code>String</code> representation of a fastest
     * way through the factory. */
    public String toString()
    {
	int[] route = getFastestRoute();
	String way = "";

	for (int i = 0; i < route.length; i++)
	    way += "Line " + (route[i] + 1) + ", Station " + (i + 1) + "\n";

	return way;
    }
}

// $Id: AssemblyLine.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: AssemblyLine.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
