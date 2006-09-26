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
 * Class for partitioning an array of {@link Comparable} objects.
 * Implements the Partition procedure from page 146 of
 * <i>Introduction to Algorithms</i>, Second edition.
 */

public class Partitioner
{
    /**
     * Partitions a subarray around its last element.
     *
     * @param array The array containing the subarray to be
     * partitioned.
     * @param p Index of the beginning of the subarray.
     * @param r Index of the end of the subarray.
     * @return An index, say <code>q</code>, such that
     * <ul>
     *   <li> <code>array[q]</code> = the original item in
     *   <code>array[r]</code>
     *   <li> <code>array[p..q-1]</code> <= <code>array[q]</code>, and
     *   <li> <code>array[q+1..r]</code> > <code>array[q]</code>.
     * </ul>
     * Works in place.
     */
    public int partition(Comparable[] array, int p, int r)
    {
	Comparable x = array[r]; // x is the pivot
	int i = p - 1;
	
	// Maintain the following invariant:
	//   array[p..i] <= x,
	//   array[i+1..j-1] > x, and
	//   array[r] = x.
	for (int j = p; j < r; j++) {
	    if (array[j].compareTo(x) <= 0) {
		i++;
		exchange(array, i, j);
	    }
	}

	// Put the pivot value in its correct place and return that
	// index.
	exchange(array, i+1, r);
	return i + 1;
    }

    /**
     * Exchanges the objects at two positions within an array.
     *
     * @param i The index of one object.
     * @param j The index of the other object.
     */
    public void exchange(Object[] array, int i, int j)
    {
	Object t = array[i];
	array[i] = array[j];
	array[j] = t;
    }
}

// $Id: Partitioner.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: Partitioner.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
