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

/**
 * Abstract class for a doubly linked list in Section 10.2 of
 * <i>Introduction to Algorithms</i>, Second edition.
 */

abstract public class LinkedList
{
    /** Inner class for nodes of a linked list. */
    protected static class Node
    {
	/** Next node in the list.*/
	public Node next;

	/** Previous node in the list. */
	public Node prev;

	/** An object stored in the node. */
	public Object info;
	
	/** Makes an empty node. */
	public Node()
	{
	    next = null;
	    prev = null;
	    info = null;
	}

	/**
	 * Makes a node storing an object.
	 *
	 * @param o The object to store.
	 */
	public Node(Object o)
	{
	    next = null;
	    prev = null;
	    info = o;
	}
    }

    /**
     * Returns the object stored in a node.
     *
     * @param node The node.
     * @throws ClassCastException if <code>node</code> does not
     * reference a <code>Node</code> object.
     */
    public static Object dereference(Object node)
    {
	return ((Node) node).info;
    }

    /** Abstract inner class for an iterator. */
    abstract public class ListIterator implements Iterator
    {
	/** Returns <code>true</code> if this iterator has more
	 * elements, <code>false</code> otherwise. */
	abstract public boolean hasNext();

	/** Returns the next element in the iteration. */
	abstract public Object next();

	/** Removes the last element returned by the iterator. */
	abstract public void remove();
    }

    /**
     * Inserts an element at the head of the list.
     *
     * @param o The element to be inserted.
     * @return A handle to the new element.
     */
    abstract public Object insert(Object o);

    /**
     * Inserts an element after a given element.
     *
     * @param o The element to be inserted.
     * @param after The element after which the new element is to be
     * inserted.  If <code>null</code>, the new element is inserted at
     * the head of the list.
     * @return A handle to the new element.
     */
    abstract public Object insertAfter(Object o, Object after);

    /** Removes an element.
     *
     * @param handle Handle to the element to remove.
     */
    abstract public void delete(Object handle);

    /** Returns <code>true</code> if this list is empty,
     * <code>false</code> otherwise. */
    abstract public boolean isEmpty();

    /** Creates and returns an <code>Iterator</code> object for this
     * list. */
    abstract public Iterator iterator();

    /**
     * Concatenates another linked list onto the end of this list,
     * destroying the other linked list.
     *
     * @param l The linked list to be concatenated onto the end of
     * this list.
     */
    abstract public void concatenate(LinkedList l);

    /** Returns the <code>String</code> representation of this list. */
    public String toString()
    {
	String result = "";
	Iterator iter = iterator();

	while (iter.hasNext())
	    result += iter.next() + "\n";

	return result;
    }

    /**
     * Copies each element of this list into an array.
     *
     * @param array The array, assumed to be already allocated.
     */
    public void toArray(Object[] array)
    {
	Iterator iter = iterator();
	int i = 0;

	while (iter.hasNext())
	    array[i++] = iter.next();
    }
}

// $Id: LinkedList.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: LinkedList.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
