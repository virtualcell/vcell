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
 * Abstract base class for both binary min-heaps and binary max-heaps.
 * Implements methods common to both, based on Chapter 6 of
 * <i>Introduction to Algorithms</i>, Second edition.  Objects in the
 * heap must implement the {@link Comparable} interface.
 * <p>
 * All operations in this class maintain the invariant that the heap
 * has the heap property.
 * <p>
 * Subclasses must implement {@link #heapify} to be concrete.
 * Extended by {@link MinHeap} and {@link MaxHeap}.
 */

abstract public class Heap 
{
    /** The array holding the heap. */
    protected Comparable[] array;

    /** How many elements are actually in the heap. */
    protected int heapSize;
    
    /**
     * Restores the heap property.  Assumes that at the time of call,
     * the heap property holds everywhere, with the possible exception
     * of one position and its children.  Must be implemented in
     * subclasses.
     *
     * @param i Index of the position at which the heap property might
     * not hold.
     */
    abstract public void heapify(int i);
    
    /**
     * Creates an empty heap.
     */
    public Heap()
    {
	array = null;
	heapSize = 0;
    }

    /**
     * Makes a heap in place from the argument, and ensures that the
     * heap property holds.
     *
     * @param array Array from which a heap is made.
     */
    public Heap(Comparable[] array)
    {
	this.array = array;
	heapSize = array.length;
	buildHeap();
    }

    /**
     * Swaps the elements at two indices.
     *
     * @param i One index.
     * @param j The other index.
     */
    protected void exchange(int i, int j)
    {
	Comparable c = array[i];
	array[i] = array[j];
	array[j] = c;
    }

    /**
     * Returns the index of the parent of a node.
     *
     * @param i The node whose parent's index is returned.
     */
    public static final int parent(int i)
    {
	return (i-1) / 2;
    }

    /**
     * Returns the index of the left child of a node.
     *
     * @param i The node whose left child's index is returned.
     */
    public static final int left(int i)
    {
	return 2 * i + 1;
    }

    /**
     * Returns the index of the right child of a node.
     *
     * @param i The node whose right child's index is returned.
     */
    public static final int right(int i)
    {
	return 2 * i + 2;
    }

    /**
     * Returns <code>true</code> if the heap is empty,
     * <code>false</code> otherwise.
     */
    public final boolean isEmpty()
    {
	return heapSize < 1;
    }

    /**
     * Returns the first element in the heap without removing it.  For
     * a max-heap, the largest element, and for a min-heap, the
     * smallest element.
     */
    public Comparable head()
    {
	return array[0];
    }

    /**
     * Rearranges the array within the heap so that it complies with
     * the heap property.
     */
    public void buildHeap()
    {
	// Heapify from the bottom up. 
	for (int i = array.length/2; i >= 0; i--)
	    heapify(i);
    }

    /**
     * Returns a {@link Heapsort} object that will sort this heap.
     */
    public Sorter makeSorter()
    {
	return new Heapsort();
    }

    /**
     * Implements the {@link Sorter} interface via heapsort.
     */
    public class Heapsort implements Sorter
    {
	/**
	 * Sorts an array of <code>Comparable</code> objects.
	 *
	 * @param array The array of <code>Comparable</code> objects
	 * to be sorted.
	 */
	public void sort(Comparable[] arrayToSort)
	{
	    // Since this method is within a class nested within Heap,
	    // there must be an existing Heap object that we can use.
	    array = arrayToSort;
	    heapSize = array.length;

	    // Enforce the heap property.
	    buildHeap();

	    // Do the rest of the heapsort algorithm.
	    for (int i = array.length-1; i >= 1; i--) {
		exchange(0, i);
		heapSize--;
		heapify(0);
	    }
	}
    }

    /**
     * Nested class for handles within a heap.  To be used by the
     * subclasses {@link MinHeapPriorityQueue} and {@link
     * MaxHeapPriorityQueue}.  <code>Handle</code> objects are stored
     * in the heap's array, and they are opaque objects given by
     * <code>MinHeapPriorityQueue</code> and
     * <code>MaxHeapPriorityQueue</code> to users of priority queues.
     */
    protected static class Handle implements Comparable
    {
	/** Index into the heap array. */
	protected int index;

	/** The information actually stored. */
	protected DynamicSetElement info;

	/**
	 * Initializes a <code>Handle</code>.
	 *
	 * @param index Index into the heap's array.
	 * @param info Information stored.
	 */
	protected Handle(int index, DynamicSetElement info)
	{
	    this.index = index;
	    this.info = info;
	}

	/**
	 * Compares the <code>DynamicSetElement</code> stored in this
	 * <code>Handle</code> to that stored in another.
	 *
	 * @param e The other <code>Handle</code> object.
	 * @return A negative integer if this <code>Handle</code>'s
	 * <code>DynamicSetElement</code> is less; 0 if the objects are
	 * equal; a positive integer if this <code>Handle</code>'s
	 * <code>DynamicSetElement</code> is greater.
	 */
	public int compareTo(Object e)
	{
	    return info.compareTo(((Handle) e).info);
	}
    }
}

// $Id: Heap.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: Heap.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
