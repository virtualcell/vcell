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
 * Interface for a max-priority queue.  In classes that implement this
 * interface, the constructor should create an empty max-priority
 * queue.
 */

public interface MaxPriorityQueue
{
    /**
     * Inserts a dynamic-set element into the max-priority queue.
     *
     * @param x The dynamic-set element to be inserted.
     * @return A handle to the inserted item.  This handle is how the
     * item is accessed in an {@link #increaseKey} operation.
     */
    public Object insert(DynamicSetElement x);

    /**
     * Returns the largest element in the max-priority queue without
     * removing the element.
     */
    public DynamicSetElement maximum();
    
    /**
     * Removes and returns the largest element in the max-priority
     * queue.
     * <p>
     * In order for the handle of the element returned to be eligible
     * for garbage collection, the user of the
     * <code>MaxPriorityQueue</code> should include in a
     * <code>DynamicSetElement</code>'s satellite data a reference to
     * the handle.  This reference should be set to <code>null</code>
     * for the element returned by <code>extractMax</code>.
     */
    public DynamicSetElement extractMax();
    
    /**
     * Increases the key of a given element to a new value.
     *
     * @param element Handle identifying the element; this handle is
     * initially given as the return value of {@link #insert}.
     * @param newKey The new key value for this element.
     * @throws KeyUpdateException if the new key value is less than
     * the current key value.
     */
    public void increaseKey(Object element, Comparable newKey)
	throws KeyUpdateException;

    /**
     * Returns <code>true</code> if the max-priority queue is empty,
     * <code>false</code> if non-empty.
     */
    public boolean isEmpty();
}

// $Id: MaxPriorityQueue.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: MaxPriorityQueue.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
