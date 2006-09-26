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
 * Sorts an array of {@link NonNegativeInteger} via the counting sort
 * algorithm from page 168 of <i>Introduction to Algorithms</i>,
 * Second edition.
 */

public class CountingSort
{
    /** Indicates that no maximum was set. */
    final private int NO_MAX = -1;

    /** Maximum value in the array. */
    private int max;

    /** Used to generate the actual key. */
    private KeyExtractor extractor;

    /**
     * Interface that allows us to pull out the sort key from an
     * object.  <code>CountingSort</code> and {@link RadixSort} have
     * inner classes that implement this interface.
     */
    public interface KeyExtractor
    {
	/**
	 * Extracts the key from an <code>int</code>.
	 *
	 * @param value The <code>int</code> whose key is to be extracted.
	 * @return The extracted key.
	 */
	public int extract(int value);

	/**
	 * Extracts the key from a <code>NonNegativeInteger</code>.
	 *
	 * @param value The <code>NonNegativeInteger</code> whose key
	 * is to be extracted.
	 * @return The extracted key.
	 */
	public int extract(NonNegativeInteger value);
    }

    /**
     * Inner class implementing <code>KeyExtractor</code>, in which
     * each <code>extract</code> method just returns its argument.
     */
    private static class CountingSortKeyExtractor implements KeyExtractor
    {
	/**
	 * Extracts the key from an <code>int</code>; an identity
	 * function.
	 *
	 * @param value The <code>int</code> whose key is to be extracted.
	 * @return <code>value</code>.
	 */
	public int extract(int value)
	{
	    return value;
	}

	/**
	 * Extracts the key from a <code>NonNegativeInteger</code>; an
	 * identity function.
	 *
	 * @param value The <code>NonNegativeInteger</code> whose key
	 * is to be extracted.
	 * @return <code>value</code>.
	 */
	public int extract(NonNegativeInteger value)
	{
	    return extract(value.getKey());
	}
    }

    /**
     * Initializes <code>max</code> and <code>extractor</code> to
     * defaults.
     */
    public CountingSort()
    {
	max = NO_MAX;
	extractor = new CountingSortKeyExtractor();
    }

    /**
     * Initializes <code>max</code> to the default and
     * <code>extractor</code> to its argument.
     *
     * @param extractor The <code>KeyExtractor</code> to use.
     */
    public CountingSort(KeyExtractor extractor)
    {
	max = NO_MAX;
	this.extractor = extractor;
    }

    /**
     * Initializes <code>max</code> to its argument and
     * <code>extractor</code> to the default.
     *
     * @param max Value of <code>max</code> to use.
     */
    public CountingSort(int max)
    {
	this.max = max;
	extractor = new CountingSortKeyExtractor();
    }

    /**
     * Initializes <code>max</code> and <code>extractor</code> to its
     * arguments.
     *
     * @param max Value of <code>max</code> to use.
     * @param extractor The <code>KeyExtractor</code> to use.
     */
    public CountingSort(KeyExtractor extractor, int max)
    {
	this.max = max;
	this.extractor = extractor;
    }

    /**
     * Changes the extractor to its argument.
     *
     * @param extractor The new <code>KeyExtractor</code> to use.
     */
    public void setExtractor(KeyExtractor extractor)
    {
	this.extractor = extractor;
    }

    /**
     * Changes the maximum to its argument.
     *
     * @param max The new value of <code>max</code> to use.
     */
    // Change the maximum.
    public void setMax(int max)
    {
	this.max = max;
    }

    /**
     * Returns the maximum value in an array of
     * <code>NonNegativeInteger</code>.
     *
     * @param array The array of <code>NonNegativeInteger</code>.
     */
    public int findMax(NonNegativeInteger[] array)
    {
	int maxValue = -1;	// maximum

	for (int i = 0; i < array.length; i++)
	    maxValue = Math.max(maxValue, extractor.extract(array[i]));

	return maxValue;
    }
    
    /**
     * Sorts an array of <code>NonNegativeInteger</code>.
     *
     * @param array The array of <code>NonNegativeInteger</code> to be
     * sorted.
     */
    public void sort(NonNegativeInteger[] array)
    {
	countingSort(array);
    }

    /**
     * Sorts an array of <code>NonNegativeInteger</code>.  If
     * <code>max</code> has not been set, calculates it by finding the
     * maximum value in the array in a preprocessing step.
     *
     * @param array The array of <code>NonNegativeInteger</code> to be
     * sorted.
     * @throws ArrayIndexOutOfBoundsException if the array to be
     * sorted contains a negative value or a value greater than the
     * presumed maximum value.
     */
    public void countingSort(NonNegativeInteger[] array)
    {
	// If no maximum value was set, search the array for it,
	// and call the actual sorting function.
	if (max == NO_MAX) {
	    countingSort(array, findMax(array));
	}
	else
	    countingSort(array, max);
    }

    /**
     * Sorts an array of <code>NonNegativeInteger</code>, given the
     * maximum value in the array.
     *
     * @param array The array of <code>NonNegativeInteger</code> to be
     * sorted.
     * @param k Maximum value in the array.
     * @throws ArrayIndexOutOfBoundsException if the array to be
     * sorted contains a negative value or a value greater than the
     * presumed maximum value.
     */
    public void countingSort(NonNegativeInteger[] array, int k)
    {
	// Since the caller does not supply an output array, we create
	// one.
	NonNegativeInteger[] temp = new NonNegativeInteger[array.length];

	countingSort(array, temp, k);

	for (int i = 0; i < array.length; i++)
	    array[i] = temp[i];
    }

    /**
     *
     * Sorts an array of <code>NonNegativeInteger</code>, given the
     * maximum value in the array and array to sort into.
     *
     * @param array The array of <code>NonNegativeInteger</code> to be
     * sorted.
     * @param sorted Output array; corresponds to the output array B
     * in the pseudocode on page 168 and contains a sorted version
     * of <code>array</code>.
     * @param k Maximum value in the array.
     * @throws ArrayIndexOutOfBoundsException if the array to be
     * sorted contains a negative value or a value greater than the
     * presumed maximum value.
     */
    public void countingSort(NonNegativeInteger[] array, 
			     NonNegativeInteger[] sorted, int k)
    {
	// Instantiate and initialize lookup array for calculating
	// sorted positions of each element.
	int[] lookup = new int[k+1];
	for (int i = 0; i <= k; i++)
	    lookup[i] = 0;

	// Count the occurrences of each number.
	for (int j = 0; j < array.length; j++)
	    lookup[extractor.extract(array[j])]++;
	
	// For each i from 0 to k, calculate the sorted position of
	// the last element of array with a key equal to i.
	for (int i = 1; i <= k; i++)
	    lookup[i] += lookup[i-1];
	
	// Copy the elements of array to their sorted positions in
	// sorted.
	for (int j = array.length - 1; j >= 0; j--) {
	    int key = extractor.extract(array[j]);
	    sorted[lookup[key] - 1] = array[j];
	    lookup[key]--;
	}
    }
}

// $Id: CountingSort.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: CountingSort.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
