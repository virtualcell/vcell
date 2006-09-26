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

/** Defines a queue based on a {@link SentinelDLL}. */

public class QueueList
{
    /** The list implementing the queue. */
    private SentinelDLL list;

    /** Makes an empty queue. */
    public QueueList()
    {
	list = new SentinelDLL();
    }

    /**
     * Inserts an element at the tail of the queue.
     *
     * @param o The element inserted.
     * @return A handle to the new element.
     */
    public Object enqueue(Object o)
    {
	return list.insertAtTail(o);
    }

    /**
     * Removes an element from the head of the queue.
     *
     * @return The removed element.
     * @throws IllegalStateException if the queue is empty at the time
     * of the call.
     */
    public Object dequeue()
    {
	Iterator iter = list.iterator();
	if (iter.hasNext()) {	       // is there a head of the queue?
	    Object head = iter.next(); // yes, so that's what we'll return
	    iter.remove();	       // but remove it first
	    return head;
	}
	else
	    throw new IllegalStateException("Called Dequeue on an empty queue");
    }

    /** Returns <code>true</code> if the list is empty (containing
     * only its sentinel), and <code>false</code> otherwise. */
    public boolean isEmpty()
    {
	return list.isEmpty();
    }

    /** Returns the <code>String</code> representation of this
     * queue. */
    public String toString()
    {
	return list.toString();
    }
}

// $Id: QueueList.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: QueueList.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
