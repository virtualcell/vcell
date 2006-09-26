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
 * Implements a min-priority queue by a min-heap, based on Chapter 6
 * of <i>Introduction to Algorithms</i>, Second edition.
 */

public class MinHeapPriorityQueue extends MinHeap implements MinPriorityQueue
{
    /** Creates an empty min-priority queue. */
    public MinHeapPriorityQueue()
    {
	super();
    }

    /**
     * Overrides the <code>exchange</code> method to update the index
     * part of each <code>Handle</code>.
     *
     * @param i One index.
     * @param j The other index.
     */
    protected void exchange(int i, int j)
    {
	((Handle) array[i]).index = j;
	((Handle) array[j]).index = i;
	super.exchange(i,j);
    }

    /**
     * Inserts a dynamic-set element into the min-priority queue.
     *
     * @param x The dynamic-set element to be inserted.
     * @return A handle to the inserted item.  This handle is how the
     * item is accessed in a {@link #decreaseKey} operation.
     */
    public Object insert(DynamicSetElement x)
    {
	// If the array doesn't yet exist, create it.
	if (array == null) {
	    array = new Comparable[1];
	    heapSize = 0;
	}
	// If there's not enough room for the new element, double the
	// array size.
	else if (heapSize >= array.length) {
	    Comparable[] temp = new Comparable[heapSize * 2];

	    for (int i = 0; i < heapSize; i++)
		temp[i] = array[i];

	    array = temp;
	}

	// Create a new Handle, and put it in the next available index.
	Handle handle = new Handle(heapSize, x);
	array[heapSize] = handle;
	heapSize++;

	// Bubble it up the heap.
	bubbleUp(heapSize-1);

	// Return the Handle created.
	return handle;
    }

    /**
     * Returns the smallest element in the min-priority queue without
     * removing the element.
     *
     * @throws HeapUnderflowException if the min-priority queue is
     * empty.
     */
    public DynamicSetElement minimum() throws HeapUnderflowException
    {
	if (heapSize > 0)
	    return ((Handle) array[0]).info;
	else {
	    throw new HeapUnderflowException();
	}
    }
    
    /**
     * Removes and returns the smallest element in the min-priority
     * queue.
     * <p>
     * In order for the handle of the element returned to be eligible
     * for garbage collection, the user of the
     * <code>MinPriorityQueue</code> should include in a
     * <code>DynamicSetElement</code>'s satellite data a reference to
     * the handle.  This reference should be set to <code>null</code>
     * for the element returned by <code>extractMin</code>.
     *
     * @throws HeapUnderflowException if the min-priority queue is
     * empty.
     */
    public DynamicSetElement extractMin()
    {
	if (heapSize < 1)
	    throw new HeapUnderflowException();

	// Get a reference to the smallest element.
	DynamicSetElement min = ((Handle) array[0]).info;

	// Move the last element in the heap to the root, and clear
	// out the reference in its current array position.
	array[0] = array[heapSize-1];
	((Handle) array[0]).index = 0;
	array[heapSize-1] = null;
	heapSize--;

	// Restore the min-heap property.
	heapify(0);

	return min;		// all done
    }
    
    /**
     * Decreases the key of a given element to a new value.
     *
     * @param element Handle identifying the element; this handle is
     * initially given as the return value of {@link #insert}.
     * @param newKey The new key value for this element.
     * @throws KeyUpdateException if the new key value is greater than
     * the current key value.
     */
    public void decreaseKey(Object element, Comparable newKey)
	throws KeyUpdateException
    {
	Handle handle = (Handle) element;

	if (newKey.compareTo(handle.info.getKey()) > 0)
	    throw new KeyUpdateException();

	handle.info.setKey(newKey); // set the new key
	bubbleUp(handle.index);	    // restore the min-heap property
    }

    /**
     * Bubbles the element at a given index up in the heap until it is
     * greater than or equal to its parent.
     *
     * @param i Index of the element to bubble up.
     */
    private void bubbleUp(int i)
    {
	while (i > 0 && array[parent(i)].compareTo(array[i]) > 0) {
	    exchange(i, parent(i));
	    i = parent(i);
	}
    }
}

// $Id: MinHeapPriorityQueue.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: MinHeapPriorityQueue.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
