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
 * Implements an elementary stack from page 201 of <i>Introduction
 * to Algorithms</i>, Second edition.
 */

public class StackArray implements Stack
{
    /** The index of the top of the stack. */
    protected int top;

    /** The array implementing the stack. */
    protected Object[] stack;

    /** The index of the top when thestack is empty. */
    protected static final int EMPTY = -1;

    /** Makes an empty stack with 1 slot. */
    public StackArray()
    {
	top = EMPTY;
	stack = new Object[1];	// an arbitrary initial size
    }

    /**
     * Makes an empty stack with a given number of slots.
     *
     * @param size The number of slots.
     */
    public StackArray(int size)
    {
	top = EMPTY;
	stack = new Object[size];
    }

    /** Returns <code>true</code> if the stack is empty,
     * <code>false</code> otherwise. */
    public boolean isEmpty()
    {
	return top <= EMPTY;
    }

    /**
     * Pushes an object onto the stack.  If the stack is full, first
     * doubles its size.
     *
     * @param x Object to be pushed.
     */
    public void push(Object x)
    {
	top++;			// go to next position

	// Is the stack full?
	if (top >= stack.length) {
	    // Yes, so allocate a new array and copy the old one's
	    // contents into it.
	    Object[] temp = new Object[top * 2];
	    for (int i = 0; i < stack.length; i++)
		temp[i] = stack[i];
	    stack = temp;	// and now we use the new array
	}

	stack[top] = x;		// save the object
    }

    /**
     * Pops an object from the stack, returning the popped object.
     *
     * @throws StackUnderflowException if the stack was already empty.
     */
    public Object pop()
    {
	if (isEmpty())
	    throw new StackUnderflowException();
	else {
	    top--;
	    return stack[top + 1]; 
	}
    }
}

// $Id: StackArray.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: StackArray.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
