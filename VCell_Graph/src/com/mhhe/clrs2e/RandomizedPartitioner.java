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

import java.util.Random;

/**
 * Class for doing random partitioning on an array of {@link
 * Comparable} objects.  Implements the Randomized-Partition procedure
 * from page 154 of <i>Introduction to Algorithms</i>, Second
 * edition.
 */

public class RandomizedPartitioner extends Partitioner
{
    /** A random-number generator. */
    private Random generator;

    /** Makes a new generator. */
    public RandomizedPartitioner()
    {
	this.generator = new Random();
    }

    /** Saves the generator it is given into the instance variable. */
    public RandomizedPartitioner(Random generator)
    {
	this.generator = generator;
    }

    /**
     * Partitions a subarray around a randomly chosen element in the
     * subarray.
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
    // element in array[p..r].
    public int partition(Comparable[] array, int p, int r)
    {
	// Pick an index between p and r, inclusive, and swap it with
	// array[r].
	int i = generator.nextInt(r - p + 1) + p;
	exchange(array, r, i);

	// Partition normally.
	return super.partition(array, p, r);
    }	
}

// $Id: RandomizedPartitioner.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: RandomizedPartitioner.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
