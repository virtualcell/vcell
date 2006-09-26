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

/** Abstract class for mergeable heap data structures, defined on
 * page 455 of <i>Introduction to Algorithms</i>, Second
 * edition.
 *
 * <p>
 *
 * The constructor of any implementing class should create an empty
 * mergeable heap.
 */

abstract public class MergeableHeap
{
    /**
     * Inserts a dynamic set element into the mergeable heap.
     *
     * @param x The dynamic set element to be inserted.
     * @return A handle to the inserted item.
     */
    abstract public Object insert(DynamicSetElement x);

    /** Returns the dynamic set element whose key is minimum. */
    abstract public DynamicSetElement minimum();

    /** Removes and returns the smallest dynamic set element in the
     * mergeable heap. */
    abstract public DynamicSetElement extractMin();

    /**
     * Creates a new mergeable heap that contains all the elements of
     * two mergeable heaps.  The two original mergeable heaps should
     * no longer be used after this operation.
     *
     * @param h1 One of the mergeable heaps to be merged.
     * @param h2 The other mergeable heap to be merged.
     * @return The new mergeable heap that contains all the elements
     * of <code>h1</code> and <code>h2</code>.
     */
    public static MergeableHeap union(MergeableHeap h1, MergeableHeap h2)
    {
	return h1.union(h2);
    }

    /**
     * Creates a new mergeable heap that contains all the elements of
     * two mergeable heaps.  One of the original mergeable heaps is
     * the object on which this method is called; the other is
     * specified by the parameter.  The two original mergeable heaps
     * should no longer be used after this operation.
     *
     * @param h2 The mergeable heap to be merged with this one.
     * @return The new mergeable heap that contains all the elements
     * of this mergeable heap and <code>h2</code>.
     */
    abstract public MergeableHeap union(MergeableHeap h2);
}

// $Id: MergeableHeap.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: MergeableHeap.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
