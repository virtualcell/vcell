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

/**
 * Implements a queue from page 203 of <i>Introduction to
 * Algorithms</i>, Second edition.
 * <p>
 * This implementation does not check for overflow or underflow, since
 * Exercise 10.1-4 asks you to supply code checking for these
 * conditions.
 */

public class QueueArray implements Queue
{
    /** The index of the head of the queue. */
    protected int head;

    /** The index at which the next object will be added. */
    protected int tail;

    /** The array implementing the queue. */
    protected Object[] queue;

    /** Creates an empty queue with 100 slots. */
    public QueueArray()
    {
	queue = new Object[100];
	head = 0;
	tail = 0;
    }

    /**
     * Creates an empty queue with a given number of slots.
     *
     * @param size The number of slots.
     */
    public QueueArray(int size)
    {
	queue = new Object[size];
	head = 0;
	tail = 0;
    }
    
    /**
     * Adds an object to the tail of the queue.  Performs no overflow
     * checking.
     *
     * @param x Object to be enqueued.
     */
    public void enqueue(Object x)
    {
	queue[tail] = x;
	if (tail == queue.length-1)
	    tail = 0;
	else
	    tail++;
    }

    /** Returns and removes the object at the head of the queue.
     * Performs no underflow checking. */
    public Object dequeue()
    {
	Object x = queue[head];

	if (head == queue.length-1)
	    head = 0;
	else
	    head++;

	return x;
    }

    /** Returns <code>true</code> if the queue is empty,
     * <code>false</code> otherwise. */
    public boolean isEmpty()
    {
	return head == tail;
    }
}

// $Id: QueueArray.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: QueueArray.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
