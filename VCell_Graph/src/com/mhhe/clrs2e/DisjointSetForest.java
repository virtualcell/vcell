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

/** Disjoint-set forest implementation of disjoint-set union, as given
 * on page 508 of <i>Introduction to Algorithms</i>, Second
 * edition. */

public class DisjointSetForest implements DisjointSetUnion
{
    // No instance variables or constructor needed.

    /** Private inner class to serve as opaque handles.  Each node has
     * a reference to its object.  It also has a reference to its
     * parent node and the value of its rank, made public so that they
     * are accessible to the methods of
     * <code>DisjointSetForest</code>.
     */
    private static class Node
    {
	/** Reference to the object. */
	public Object theObject;

	/** Reference to this node's parent. */
	public Node p;

	/** This node's rank. */
	public int rank;

	/**
	 * Makes a node for a given object.
	 *
	 * @param x The object in this node.
	 */
	public Node(Object x)
	{
	    theObject = x;
	    p = this;
	    rank = 0;
	}

	/** Returns the <code>String</code> representation of this
	 * node. */
	public String toString()
	{
	    return theObject.toString() + ": p = " + p.theObject.toString() +
		", rank = " + rank;
	}
    }

    /**
     * Makes a singleton set containing an object.
     *
     * @param x The object in the singleton set.
     * @return A handle that serves to identify the set in future
     * operations.
     */
    public Object makeSet(Object x)
    {
	return new Node(x);
    }

    /**
     * Unites two sets, identified by handles to objects in the sets.
     * The original sets are destroyed.
     *
     * @param x Handle to an object in one set.
     * @param y Handle to an object in the other set.
     * @throws ClassCastException if either <code>x</code> or
     * <code>y</code> does not reference a <code>Node</code> object.
     */
    public void union(Object x, Object y)
    {
	link((Node) findSet(x), (Node) findSet(y));
    }

    /**
     * Returns the object that serves as the representative of the set
     * containing an object.
     *
     * @param x Handle to the object.
     * @return A handle to the representative of the set containing x.
     * @throws ClassCastException if <code>x</code> is not a reference
     * to a <code>Node</code> object.
     */
    public Object findSet(Object x)
    {
	Node nodeX = (Node) x;	// force x to be interpreted as a Node
	if (nodeX != nodeX.p)
	    nodeX.p = (Node) findSet(nodeX.p);
	return nodeX.p;
    }

    /**
     * Links together two sets, given their root nodes.  The root with
     * the larger rank becomes the parent of the root with the smaller
     * rank.  In case of a tie, we arbitrarily choose one root as the
     * parent of the other.
     *
     * @param x The root node of one set.
     * @param y The root node of the other set.
     */
    private void link(Node x, Node y)
    {
	if (x.rank > y.rank)
	    y.p = x;
	else {
	    x.p = y;
	    if (x.rank == y.rank)
		y.rank++;
	}
    }
}

// $Id: DisjointSetForest.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: DisjointSetForest.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
