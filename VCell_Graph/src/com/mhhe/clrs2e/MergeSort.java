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
 * Implements the {@link Sorter} interface via merge sort from
 * pages 29 and 32 of Introduction to Algorithms, Second edition.
 */

public class MergeSort implements Sorter
{
    /**
     * Sorts an array of <code>Comparable</code> objects.
     *
     * @param array The array of <code>Comparable</code> objects to be
     * sorted.
     */
    public void sort(Comparable[] array)
    {
	mergeSort(array, 0, array.length-1);
    }

    /**
     * Recursive merge sort procedure to sort the subarray
     * <code>array[p..r]</code>.
     *
     * @param array The array containing the subarray to be sorted.
     * @param p Index of the beginning of the subarray.
     * @param r Index of the end of the subarray.
     */
    private void mergeSort(Comparable[] array, int p, int r)
    {
	if (p < r) {
	    int q = (p + r) / 2;
	    mergeSort(array, p, q);
	    mergeSort(array, q+1, r);
	    merge(array, p, q, r);
	}
    }

    /**
     * Merges two sorted subarrays <code>array[p..q]</code> and
     * <code>array[q+1..r]</code>.  Uses sentinels.
     *
     * @param array The array containing the subarrays to be merged.
     * @param p Index of the beginning of the first subarray.
     * @param q Index of the end of the first subarray; the second
     * subarray starts at index <code>q+1</code>.
     * @param r Index of the end of the second subarray.
     */
    private void merge(Comparable[] array, int p, int q, int r)
    {
	int n1 = q - p + 1;
	int n2 = r - q;
	Comparable[] left = new Comparable[n1 + 1];
	Comparable[] right = new Comparable[n2 + 1];

	for (int i = 0; i < n1; i++)
	    left[i] = array[p + i];

	for (int j = 0; j < n2; j++)
	    right[j] = array[q + j + 1];

	left[n1] = null;	// null indicates infinity
	right[n2] = null;

	for (int i = 0, j = 0, k = p; k <= r; k++) {
	    if (compare(left[i], right[j]) <= 0)
		array[k] = left[i++];
	    else
		array[k] = right[j++];
	}
    }

    /**
     * Compares two objects, returning their relationship.  If an
     * object is given by a <code>null</code> reference, the object's
     * value is assumed to be infinity.
     *
     * @param x One object.
     * @param y The other object.
     * @return A negative integer if <code>x</code> < <code>y</code>;
     * 0 if <code>x</code> equals <code>y</code>; a positive integer
     * if <code>x</code> > <code>y</code>.
     */
    private int compare(Comparable x, Comparable y)
    {
	if (x == null) {
	    if (y == null)
		return 0;	   // both x and y are infinity
	    else
		return 1;	   // x is infinity, y is not
	}
	else if (y == null)
	    return -1;		   // y is infinity, x is not
	else
	    return x.compareTo(y); // neither x nor y is infinity
    }
}

// $Id: MergeSort.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: MergeSort.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
