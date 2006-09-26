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
 * deterministic Select procedure (which runs in linear time in the
 * worst case) from pages 189-190 of <i>Introduction to
 * Algorithms</i>, Second edition.
 */

public class DeterministicSelect implements OrderStatistics
{
    /** For partitioning. */
    private Partitioner part;

    /**
     * Creates a Partitioner.
     */
    public DeterministicSelect()
    {
	part = new Partitioner();
    }

    /**
     * Returns the ith smallest element in an array.
     *
     * @param array The array.
     * @param i Which order statistic we want.
     */
    public Comparable select(Comparable[] array, int i)
    {
	return select(array, 0, array.length-1, i);
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
    private Comparable select(Comparable[] array, int p, int r, int i)
    {
	int n = r - p + 1;	// number of elements in the subarray

	if (n == 1)
	    return array[p];	// base case: return the only element
	else {
	    // Divide the subarray into ceil(n / GROUP_SIZE) groups,
	    // and find the median of each group by insertion sorting
	    // the group and picking the median from the sorted list.
	    final int GROUP_SIZE = 5; // size of each group
	    int groups;		// how many groups
	    if (n % GROUP_SIZE == 0)
		groups = n / GROUP_SIZE;
	    else
		groups = (n / GROUP_SIZE) + 1;

	    // Create an array of medians.
	    Comparable[] medians = new Comparable[groups];

	    // Fill in medians to contain the medians of the groups.
	    for (int groupStart = p, groupNumber = 0;
		 groupStart <= r;
		 groupStart += GROUP_SIZE, groupNumber++) {
		int thisGroupSize = Math.min(r-groupStart+1, GROUP_SIZE);
		insertionSortSubarray(array, groupStart, thisGroupSize);
		medians[groupNumber] =
		    array[groupStart + ((thisGroupSize-1) / 2)];
	    }

	    // Recursively find the median of the medians.
	    Comparable theMedian = select(medians, 0, groups-1, (groups+1)/2);

	    // We need to figure out where in the array the median of
	    // the medians is.  Go through the array, comparing
	    // elements to theMedian until we find they're equal.  We
	    // are guaranteed that we will find an element equal to
	    // the median, so we do not need to check for running off
	    // the end of the subarray.  Because we are doing no
	    // arithmetic on the elements, it is safe to compare the
	    // elements even if they are floating-point values.  Note
	    // also that running through the subarray does not
	    // increase the asymptotic running time.
	    int medianIndex = p;
	    while (theMedian.compareTo(array[medianIndex]) != 0)
		medianIndex++;

	    // Partition the input array around the median of the
	    // medians.
	    part.exchange(array, r, medianIndex);
	    int q = part.partition(array, p, r);

	    // The low side of the partition is array[p..q-1].  Set k
	    // to the number of elements in array[p..q], so that we
	    // include the median of the medians in our count.
	    int k = q - p + 1;

	    if (i == k)
		return array[q]; // ith smallest is at index q
	    else if (i < k)
		return select(array, p, q-1, i); // ith smallest is ith
				                 // smallest in the low side
	    else
		return select(array, q+1, r, i-k); // ith smallest is (i-k)th
				                   // smallest in high side
	}
    }

    /**
     * Sorts a small subarray.
     *
     * @param array The array containing the subarray to be sorted.
     * @param start Index of the start of the subarray.
     * @param size Number of elements in the subarray.
     */
    private void insertionSortSubarray(Comparable[] array, int start,
				       int size)
    {
	int end = start + size - 1;

	for (int j = start+1; j <= end; j++) {
	    Comparable k = array[j];

	    // Insert array[j] into the sorted sequence array[start..j-1].
	    int i = j-1;

	    while (i >= start && array[i].compareTo(k) > 0) {
		array[i+1] = array[i];
		i--;
	    }

	    array[i+1] = k;
	}
    }
}

// $Id: DeterministicSelect.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: DeterministicSelect.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
