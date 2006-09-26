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
 * Implements the {@link OrderStatistics} interface via the
 * Randomized-Select procedure (which runs in linear expected time)
 * from page 186 of <i>Introduction to Algorithms</i>, Second
 * edition.
 */

public class RandomizedSelect implements OrderStatistics
{
    /** For partitioning. */
    private RandomizedPartitioner part;

    /** Sets the partitioner to be a randomized one. */
    public RandomizedSelect()
    {
	part = new RandomizedPartitioner();
    }

    /**
     * Returns the ith smallest element in a subarray
     * <code>array[p..r]</code>.
     *
     * @param array The array containing the subarray to be sorted.
     * @param p Index of the beginning of the subarray.
     * @param r Index of the end of the subarray.
     * @param i Which order statistic we want.
     */
    private Comparable randomizedSelect(Comparable[] array,
					int p, int r, int i)
    {
	if (p == r)
	    return array[p];

	int q = part.partition(array, p, r); // use randomized partitioner
	int k = q - p + 1;	// number of elements that are <= array[q]
				// (the pivot)
	if (i == k)
	    return array[q];	// the pivot is the answer
	else if (i < k)
	    return randomizedSelect(array, p, q-1, i);
	else
	    return randomizedSelect(array, q+1, r, i-k);
    }

    /**
     * Returns the ith smallest element in an array.
     *
     * @param array The array.
     * @param i Which order statistic we want.
     */
    public Comparable select(Comparable[] array, int i)
    {
	return randomizedSelect(array, 0, array.length-1, i);
    }
}

// $Id: RandomizedSelect.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: RandomizedSelect.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
