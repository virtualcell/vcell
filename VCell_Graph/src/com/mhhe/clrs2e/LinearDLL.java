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
 * A simple linear, doubly linked list without a sentinel from
 * pages 205-206 of <i>Introduction to Algorithms</i>, Second
 * edition.
 */

public class LinearDLL extends LinkedList
{
    /** The first node in the list. */
    protected Node head;

    /** Makes an empty list. */
    public LinearDLL()
    {
	head = null;
    }

    /** Inner class for an iterator. */
    public class LinearDLLIterator extends ListIterator
    {
	/**
	 * A reference to the <code>Node</code> returned by the most
	 * recent call to <code>next</code>.  Initially, it is
	 * <code>null</code>.
	 */
	private Node current;

	/** Starts an iteration. */
	public LinearDLLIterator()
	{
	    current = null;
	}

	/** Returns <code>true</code> if this iterator has more
	 * elements, <code>false</code> otherwise. */
	public boolean hasNext()
	{
	    if (current == null)
		return head != null;
	    else
		return current.next != null;
	}

	/** Returns the next element in the iteration. */
	public Object next()
	{
	    if (current == null)
		current = head;
	    else
		current = current.next;

	    return current.info;
	}

	/** Removes the last element returned by the iterator.
	 *
	 * @throws IllegalStateException if <code>next</code> has not
	 * been called.
	 */
	public void remove()
	{
	    if (current == null)
		throw new IllegalStateException("Called remove before iterator returned an object.");
	    else {
		// Save current's predecessor, since after we remove
		// current, it will be the most recently returned node
		// in the iteration.
		Node newCurrent = current.prev;

		delete(current); // delete the current node

		current = newCurrent;
	    }
	}
    }

    /**
     * Inserts an element at the head of the list.
     *
     * @param o The element to be inserted.
     * @return A handle to the new element.
     */
    public Object insert(Object o)
    {
	Node x = new Node(o);
	x.next = head;
	if (head != null)
	    head.prev = x;
	head = x;
	x.prev = null;
	return x;
    }

    /**
     * Inserts an element after a given element.
     *
     * @param o The element to be inserted.
     * @param after The element after which the new element is to be
     * inserted.  If <code>null</code>, the new element is inserted at
     * the head of the list.
     * @return A handle to the new element.
     */
    public Object insertAfter(Object o, Object after)
    {
	if (after == null)
	    return insert(o);
	else {
	    Node x = new Node(o);
	    Node predecessor = (Node) after;
	    x.next = predecessor.next;
	    x.prev = predecessor;
	    if (predecessor.next != null)
		predecessor.next.prev = x;
	    predecessor.next = x;
	    return x;
	}
    }

    /** Removes an element.
     *
     * @param handle Handle to the element to remove.
     */
    public void delete(Object handle)
    {
	Node x = (Node) handle;
	if (x.prev != null)
	    x.prev.next = x.next;
	else
	    head = x.next;
	if (x.next != null)
	    x.next.prev = x.prev;
    }

    /** Returns <code>true</code> if this list is empty,
     * <code>false</code> otherwise. */
    public boolean isEmpty()
    {
	return head == null;
    }

    /** Creates and returns an <code>Iterator</code> object for this
     * list. */
    public Iterator iterator()
    {
	return new LinearDLLIterator();
    }

    /**
     * Concatenates another linked list onto the end of this list,
     * destroying the other linked list.
     *
     * @param l The linked list to be concatenated onto the end of
     * this list.
     * @throws ClassCastException if <code>l</code> is not a
     * <code>LinearDLL</code> object.
     */
    public void concatenate(LinkedList l)
    {
	LinearDLL other = (LinearDLL) l;

	if (head == null)
	    head = other.head;
	else if (other.head == null)
	    return;
	else {
	    // We have to find the end of this list.
	    Node x = head;

	    while (x.next != null)
		x = x.next;

	    // x is now the last Node in the list.
	    x.next = other.head;
	    other.head.prev = x;
	}
    }
}

// $Id: LinearDLL.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: LinearDLL.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
