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
 * Implements the {@link Sorter} interface via quicksort from
 * page 146 of <i>Introduction to Algorithms</i>, Second edition.
 */

public class Quicksort implements Sorter
{
    /** An object for partitioning; may be either deterministic or
     * randomized. */
    protected Partitioner part;

    /**
     * Recursive quicksort procedure to sort the subarray
     * <code>array[p..r]</code>.  Uses whatever partitioner the
     * instance variable <code>part</code> is set to.
     *
     * @param array The array containing the subarray to be sorted.
     * @param p Index of the beginning of the subarray.
     * @param r Index of the end of the subarray.
     */
    protected void quicksort(Comparable[] array, int p, int r)
    {
	if (p < r) {
	    int q = part.partition(array, p, r);
	    quicksort(array, p, q - 1);
	    quicksort(array, q + 1, r);
	}
    }

    /**
     * Sorts an array of <code>Comparable</code> objects.
     *
     * @param array The array of <code>Comparable</code> objects to be
     * sorted.
     */
    public void sort(Comparable[] array)
    {
	part = new Partitioner(); // use a deterministic partitioner
	quicksort(array, 0, array.length - 1); // sort using it
    }
}

// $Id: Quicksort.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: Quicksort.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
