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
 * Sorts an array of {@link NonNegativeInteger} via the radix sort
 * algorithm from page 172 of <i>Introduction to Algorithms</i>,
 * Second edition.
 */

public class RadixSort 
{
    /** Indicates that no radix was set. */
    final private int NO_RADIX = -1;

    /** Indicates that the number of digits was not set. */
    final private int NO_DIGITS = -1;

    /** The radix being used. */
    private int radix;

    /** How many digits per key. */
    private int digits;

    /**
     * Interface that allows us to pull out the sort key from an
     * object.
     */
    private static class RadixKeyExtractor implements CountingSort.KeyExtractor
    {
	/** The radix for extracting. */
	private int extractorRadix;

	/** The divisor for extracting. */
	private int divisor;
	
	/**
	 * Initializes the radix and divisor.
	 *
	 * @param radix Radix used.
	 * @param divisor Divisor used.
	 */
	public RadixKeyExtractor(int radix, int divisor)
	{
	    extractorRadix = radix;
	    this.divisor = divisor;
	}

	/**
	 * Extracts the key from an <code>int</code>, where the key is
	 * the ith digit in base <code>extractorRadix</code>, and
	 * assuming that <oode>divisor</code> equals
	 * <code>extractorRadix</code>^i.
	 *
	 * @param value The <code>int</code> whose key is to be
	 * extracted.
	 * @return The extracted key.
	 */
	public int extract(int value)
	{
	    return (value / divisor) % extractorRadix;
	}

	/**
	 * Extracts the key from an <code>NonNegativeInteger</code>,
	 * where the key is the ith digit in base
	 * <code>extractorRadix</code>, and assuming that
	 * <oode>divisor</code> equals <code>extractorRadix</code>^i.
	 *
	 * @param value The <code>NonNegativeInteger</code> whose key
	 * is to be extracted.
	 * @return The extracted key.
	 */
	public int extract(NonNegativeInteger value)
	{
	    return extract(value.getKey());
	}
    }

    /** Initializes the instance variables to default nonsense
     * values. */
    public RadixSort()
    {
	radix = NO_RADIX;
	digits = NO_DIGITS;
    }

    /**
     * Initializes the <code>radix</code> instance variables to its
     * argument and and <code>digits</code> to a default value.
     *
     * @param radix Value to use for the radix.
     */
    public RadixSort(int radix)
    {
	this.radix = radix;
	this.digits = NO_DIGITS;
    }

    /**
     * Initializes the instance variables to its arguments.
     *
     * @param radix Value to use for the radix.
     * @param digits Number of digits to use.
     */
    public RadixSort(int radix, int digits)
    {
	this.radix = radix;
	this.digits = digits;
    }

    /**
     * Determines how many digits of a given radix are necessary.
     *
     * @param radix The radix.
     * @param max Maximum value of any key.
     * @return How many digits are necessary.
     */
    private static int calculateDigits(int radix, int max)
    {
	int i = 0;		// number of digits
	int ri = 1;		// radix^i

	while (ri <= max) {	// keep going until we overshoot max
	    i++;		// use one more digit
	    ri *= radix;	// update ri to be radix^i
	}

	return i;
    }	

    /**
     * Sorts an array of <code>NonNegativeInteger</code>.  Wrapper for
     * <code>radixSort</code>.  Calculates the optimal radix if the
     * <code>radix</code> instance variable has not been set.
     *
     * @param array The array of <code>NonNegativeInteger</code> to be
     * sorted.
     */
    public void sort(NonNegativeInteger[] array)
    {
	if (radix == NO_RADIX) {
	    // If radix is unspecified then calculate the optimal
	    // radix (last paragraph on page 172).
	    CountingSort cs = new CountingSort();
	    int max = cs.findMax(array); // uses the full key
	    int bits = calculateDigits(2, max);
	    int lgn =
		(int) (Math.floor(Math.log(array.length) / Math.log(2.0)));

	    if (bits < lgn)
		radixSort(array, bits, calculateDigits(bits, max));
	    else
		radixSort(array, lgn, calculateDigits(lgn, max));
	} 
	else if (digits == NO_DIGITS)
	    radixSort(array, radix);
	else
	    radixSort(array, radix, digits);
    }

    /**
     * Implements radix sort, calculating the number of digits to
     * sort.
     *
     * @param array The array of <code>NonNegativeInteger</code> to be
     * sorted.
     * @param radix The radix to use.
     */
    public void radixSort(NonNegativeInteger[] array, int radix)
    {
	CountingSort cs = new CountingSort();
	int max = cs.findMax(array); // uses the full key

	radixSort(array, radix, calculateDigits(radix, max));
    }

     /**
     * Implements radix sort.
     *
     * @param array The array of <code>NonNegativeInteger</code> to be
     * sorted.
     * @param r The radix to use.
     * @param d The number of digits to use.
     */
    public void radixSort(NonNegativeInteger[] array, int r, int d)
    {
	CountingSort cs = new CountingSort(r-1);
	int divisor = 1;	// divisor = radix^i
	
	for (int i = 0; i < d; i++) {
	    cs.setExtractor(new RadixKeyExtractor(r, divisor));
	    cs.sort(array);
	    divisor *= r;
	}
    }
} 

// $Id: RadixSort.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: RadixSort.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
