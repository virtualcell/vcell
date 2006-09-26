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

/** Class for solving a system of difference constraints, as defined
 * in Section&nbsp;24.4 of <i>Introduction to Algorithms</i>, Second
 * edition. */

public class DifferenceConstraints
{
    /** A list of the difference constraints. */
    SentinelDLL system;

    /** Creates an empty system of difference constraints. */
    public DifferenceConstraints()
    {
	system = new SentinelDLL();
    }

    /**
     * Adds a constraint <i>x<sub>j</sub></i> - <i>x<sub>i</sub></i>
     * &#x2264; <i>b<sub>k</sub></i> to the system of difference
     * constraints.
     *
     * @param j Index for the variable <i>x<sub>j</sub></i>.  Must be
     * at least 1.
     * @param i Index for the variable <i>x<sub>i</sub></i>.  Must be
     * at least 1.
     * @param b Value of <i>b<sub>k</sub></i>.
     * @throws BadConstraintException if either of <code>i</code> or
     * <code>j</code> is less than 1.
     */
    public void addConstraint(int j, int i, double b)
    {
	if (i < 1 || j < 1)
	    throw new BadConstraintException();
	else
	    system.insertAtTail(new Constraint(j, i, b));
    }

    /**
     * Finds a feasible solution to the system of difference
     * constraints, if a feasible solution exists.
     *
     * @return If the return value is <code>null</code>, then there is
     * no feasible solution.  Otherwise, the return value is an array
     * <code>x[0..n]</code>, where <code>x[0]</code> is undefined and
     * for <code>i</code> = 1, 2, ..., n, the appropriate value of the
     * feasible solution is in <code>x[i]</code>. */
    public double[] findFeasibleSolution()
    {
	// Find the greatest index in any of the constraints.  That
	// will be the highest vertex number.
	int n = 0;

	Iterator iter;
	iter = system.iterator();
	while (iter.hasNext()) {
	    Constraint cons = (Constraint) iter.next();
	    n = Math.max(n, cons.i);
	    n = Math.max(n, cons.j);
	}

	// Make the constraint graph.
	WeightedAdjacencyListGraph constraintGraph =
	    new WeightedAdjacencyListGraph(n+1, true);

	// Create and add the vertices.
	Vertex[] vertex = new Vertex[n+1];
	for (int i = 0; i <= n; i++) {
	    vertex[i] = new Vertex("x_" + i);
	    constraintGraph.addVertex(vertex[i]);
	}

	// For each constraint, add the appropriate edge.
	iter = system.iterator();
	while (iter.hasNext()) {
	    Constraint cons = (Constraint) iter.next();
	    constraintGraph.addEdge(vertex[cons.i], vertex[cons.j], cons.b);
	}

	// Add edges from vertex 0 to all other vertices.
	for (int i = 1; i <= n; i++)
	    constraintGraph.addEdge(vertex[0], vertex[i], 0);

	// Find shortest-path weights by running the Bellman-Ford
	// algorithm.
	BellmanFord bf = new BellmanFord(constraintGraph);
	bf.computeShortestPaths(vertex[0]);

	if (bf.hasNoNegativeWeightCycle()) {
	    // Return the shortest-path weights.
	    double solution[] = new double[n+1];
	    for (int i = 1; i <= n; i++)
		solution[i] = bf.getShortestPathInfo(i).getEstimate();

	    return solution;
	}
	else
	    return null;	// not feasible
    }

    /** Returns the <code>String</code> representation of this system
     * of difference constraints. */
    public String toString()
    {
	return system.toString();
    }

    /** Inner class for an individual constraint. */
    private static class Constraint
    {
	/** Index of the variable <i>x<sub>j</sub></i> in the
	 * constraint. */
	public int j;

	/** Index of the variable <i>x<sub>i</sub></i> in the
	 * constraint. */
	public int i;

	/** Value of <i>b<sub>k</sub></i>. */
	public double b;	// b_k

	/**
	 * Sets the values of the instance variables.
	 *
	 * @param j Index for the variable <i>x<sub>j</sub></i>.
	 * @param i Index for the variable <i>x<sub>i</sub></i>.
	 * @param b Value of <i>b<sub>k</sub></i>.
	 */
	public Constraint(int j, int i, double b)
	{
	    this.i = i;
	    this.j = j;
	    this.b = b;
	}

	/** Returns the <code>String</code> representation of this
	 * difference constraint. */
	public String toString()
	{
	    return "x_" + j + " - x_" + i + " <= " + b;
	}
    }

    /** Inner class for a bad constraint exception. */
    public static class BadConstraintException extends RuntimeException
    {
    }
}

// $Id: DifferenceConstraints.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: DifferenceConstraints.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
