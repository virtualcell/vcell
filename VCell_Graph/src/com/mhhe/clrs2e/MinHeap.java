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
 * Implements a binary min-heap, based on Chapter 6 of <i>Introduction
 * to Algorithms</i>, Second edition.
 */

public class MinHeap extends Heap
{
    /** Creates an empty min-heap. */
    MinHeap()
    {
	super();
    }

    /**
     * Makes a min-heap in place from the argument, and ensures that
     * the min-heap property holds.
     *
     * @param array Array from which a min-heap is made.
     */
   MinHeap(Comparable[] array)
    {
	super(array);
    }

    /**
     * Restores the min-heap property.  Assumes that at the time of
     * call, the min-heap property holds everywhere, with the possible
     * exception of one position and its children.
     *
     * @param i Index of the position at which the min-heap property
     * might not hold.
     */
    public void heapify(int i)
    {
	int l = left(i);
	int r = right(i);
	int smallest = i;
	if (l < heapSize && array[l].compareTo(array[i]) < 0)
	    smallest = l;
	if (r < heapSize && array[r].compareTo(array[smallest]) < 0)
	    smallest = r;
	if (smallest != i) {
	    exchange(i, smallest);
	    heapify(smallest);
	}
    }
}

// $Id: MinHeap.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: MinHeap.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
