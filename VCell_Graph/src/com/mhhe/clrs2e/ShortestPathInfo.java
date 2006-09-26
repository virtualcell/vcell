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

/** Class for information determined about a vertex during a
 * shortest-path algorithm. */

public class ShortestPathInfo
{
    /** The current shortest-path estimate for this vertex. */
    private double d;

    /** The current predecessor (parent) for this vertex. */
    private Vertex pi;

    /** Initializes the shortest-path estimate to infinity and the
     * predecessor to <code>null</code>. */
    public ShortestPathInfo()
    {
	d = Double.POSITIVE_INFINITY;
	pi = null;
    }

    /**
     * Sets the shortest-path estimate.
     *
     * @param newEstimate The new shortest-path estimate.
     */
    public void setEstimate(double newEstimate)
    {
	d = newEstimate;
    }

    /** Returns the shortest-path estimate. */
    public double getEstimate()
    {
	return d;
    }

    /**
     * Sets the predecessor.
     *
     * @param v The new predecessor.
     */
    public void setPredecessor(Vertex v)
    {
	pi = v;
    }

    /** Returns the predecessor. */
    public Vertex getPredecessor()
    {
	return pi;
    }


    /**
     * Relaxes an edge entering this vertex, say <code>v</code>, based
     * on the Relax procedure on page 586 of <i>Introduction to
     * Algorithms</i>, Second edition.
     *
     * @param u Vertex that the edge leaves.
     * @param du <code>u</code>'s shortest-path estimate.
     * @param w The weight of the edge (u,v).
     * @return <code>true</code> if the shortest-path estimate for
     * <code>v</code> changes, <code>false</code> if the shortest-path
     * estimate remains unchanged.
     */
    public boolean relax(Vertex u, double du, double w)
    {
	double newWeight = du + w;
	if (newWeight < d) {
	    d = newWeight;
	    pi = u;
	    return true;
	}
	else
	    return false;
    }	

    /** Returns the <code>String</code> representation of this object. */
    public String toString()
    {
	String parentName;

	if (pi == null)
	    parentName = "(null)";
	else
	    parentName = pi.getName();

	return "d = " + d + ", pi = " + parentName;
    }
}

// $Id: ShortestPathInfo.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: ShortestPathInfo.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
