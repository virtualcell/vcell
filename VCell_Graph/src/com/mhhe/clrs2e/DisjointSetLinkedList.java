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

/** Linked-list implementation of disjoint-set union, using the
 * weighted-union heuristic, as given on pages 502-503 of
 * <i>Introduction to Algorithms</i>, Second edition.
 *
 * <p>
 *
 * The representative pointer is different here from in the book.  In
 * order to be able to get to the information for a set, the
 * representative here references the set, rather than the first node
 * in the set's list.
 */

public class DisjointSetLinkedList implements DisjointSetUnion
{
    // No instance variables or constructor needed.

    /** Private inner class for nodes of the list. */
    private static class Node
    {
	/** Reference to the object. */
	public Object theObject;

	/** This node's successor in the list. */
	public Node next;

	/** The set containing this node. */
	public DisjointSet representative;

	/**
	 * Makes a node for a given object.
	 *
	 * @param x The object in this node.
	 */
	public Node(Object x)
	{
	    theObject = x;
	    next = null;
	    representative = null;
	}

	/** Returns the <code>String</code> representation of this
	 * node. */
	public String toString()
	{
	    return theObject.toString();
	}
    }

    /** Private inner class for each disjoint set. */
    private static class DisjointSet
    {
	/** The head of the list. */
	public Node head;

	/** The tail of the list. */
	public Node tail;

	/** How many elements are in this set. */
	public int size;

	/**
	 * Makes a singleton set with a given node.
	 *
	 * @param x The node in this singleton set.
	 */
	public DisjointSet(Node x)
	{
	    head = x;
	    tail = x;
	    size = 1;
	}

	/** Returns the <code>String</code> representation of this
	 * set, with each element appearing on a separate line. */
	public String toString()
	{
	    String result = "";

	    for (Node x = head; x != null; x = x.next)
		result += x.toString() + "\n";

	    return result;
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
	Node node = new Node(x);
	node.representative = new DisjointSet(node);
	return node;
    }

    /**
     * Unites two sets, identified by handles to objects in the sets.
     * The original sets are destroyed.
     *
     * @param x Handle to an object in one set.
     * @param y Handle to an object in the other set.
     * @throws ClassCastException if either <code>x</code> or
     * <code>y</code> does not reference a <code>DisjointSet</code>
     * object.
     */
    public void union(Object x, Object y)
    {
	// Append the smaller list onto the longer.
	DisjointSet xSet = (DisjointSet) findSet(x);
	DisjointSet ySet = (DisjointSet) findSet(y);
	if (xSet.size >= ySet.size)
	    append(xSet, ySet);
	else
	    append(ySet, xSet);
    }

    /**
     * Appends one set's list to another set's list.
     *
     * @param first The set to which the other will be appended.
     * @param second The set that will be appended to the other.
     * @throws DisjointSetUnionException if either set is empty.
     */
    private void append(DisjointSet first, DisjointSet second)
    {
	// Check that neither set is empty.
	if (first.size == 0 || second.size == 0)
	    throw new DisjointSetUnionException();

	// Set the representative of each node in the second set to be
	// the first set.
	for (Node x = second.head; x != null; x = x.next)
	    x.representative = first;

	// Splice the second set onto the end of the first.
	first.tail.next = second.head;
	first.tail = second.tail;
	first.size += second.size;

	// Invalidate the second set, just to be sure.
	second.head = null;
	second.tail = null;
	second.size = 0;
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
	return ((Node) x).representative;
    }

    /**
     * Returns the <code>String</code> representation of the set
     * containing a given object.
     *
     * @param x The object whose set's <code>String</code>
     * representation is returned.
     */
    public String printSet(Object x)
    {
	return findSet(x).toString();
    }

    /** Inner class for an exception that occurs if either set to be
     * united is empty. */
    public static class DisjointSetUnionException extends RuntimeException
    {
    }
}

// $Id: DisjointSetLinkedList.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: DisjointSetLinkedList.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
