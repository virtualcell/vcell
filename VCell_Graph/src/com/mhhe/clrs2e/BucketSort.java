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
 * Sorts an array of {@link DoubleValued} in the range [0, 1) via the
 * bucket sort algorithm from page 174 of <i>Introduction to
 * Algorithms</i>, Second edition.
 */

public class BucketSort
{
    /**
     * Sorts an array of <code>DoubleValued</code>.
     *
     * @param array The array of <code>DoubleValued</code> objects to
     * be sorted.
     * @throws ArrayIndexOutOfBoundsException if any element of
     * <code>array</code> has a value outside the half-open interval
     * [0,1).
     */
    public void sort (DoubleValued[] array) 
    {
	// Determine length of array and instantiate buckets.
	int n = array.length;
	SortableSentinelDLL[] buckets = new SortableSentinelDLL[n];

	// Initialize each bucket with a new linked list.
	for (int i = 0; i < n; i++)
	    buckets[i] = new SortableSentinelDLL();
	
	// Add each element in array to its specific bucket.
	for (int i = 0; i < n; i++)
	    buckets[(int) (n * array[i].getKey())].
		insert(array[i]);

	// Sort the buckets.
	for (int i = 0; i < n; i++)
	    buckets[i].sort();

	// Concatenate the buckets together into buckets[0].
	for (int i = 1; i < n; i++)
	    buckets[0].concatenate(buckets[i]);

	// Convert the LinkedList of sorted values back into array.
	buckets[0].toArray(array);
    }
}

// $Id: BucketSort.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: BucketSort.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
