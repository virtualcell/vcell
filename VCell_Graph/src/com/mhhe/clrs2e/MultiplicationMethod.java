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

/** Implements the multiplication method of hashing on pages 231-232
 * of <i>Introduction to Algorithms</i>, Second edition. */

public class MultiplicationMethod
{
    /** The size of the hash table being used. */
    private int tableSize;

    /** <code>true</code> if the table size is a power of 2,
     * <code>false</code> otherwise. */
    private boolean isPowerOf2;

    /** If the table size is a power of 2, the shift amount used. */
    private int shiftAmount;

    /** If the table size is a power of 2, the bit mask used. */
    private long bitMask;

    /**
     * Creates a hash function that uses the multiplication method.
     *
     * @param size The size of the hash table.
     */
    public MultiplicationMethod(int size)
    {
	tableSize = size;
	isPowerOf2 = (size & (size-1)) == 0;

	if (isPowerOf2) {
	    // Determine the lg of the table size, denoted by p.
	    int p = 0;
	    int x = 1;
	    while (x < size) {
		x *= 2;
		p++;
	    }

	    shiftAmount = 32 - p;
	    bitMask = size - 1;
	}
    }

    /**
     * Returns the hash value of an object, based on its Java
     * <code>hashCode</code> value and the multiplication method.
     *
     * @param o The object being hashed.  If the object implements
     * <code>DynamicSetElement</code>, the hash value is of its key.
     */
    public int hash(Object o)
    {
	// If the object implements <code>DynamicSetElement</code>,
	// hash its key.  Otherwise, just hash the object itself.
	if (o instanceof DynamicSetElement)
	    o = ((DynamicSetElement) o).getKey();

	// If the table size is a power of 2, we can use the faster
	// method shown in Figure 11.4 on page 232.
	if (isPowerOf2) {
	    final long S = 2654435769L;

	    int k = o.hashCode();
	    long r = S * k;

	    return (int) ((r >> shiftAmount) & bitMask);
	    }
	else {
	    // Use the less efficient method, which uses
	    // floating-point arithmetic.

	    // The constant used in the multiplication method.
	    final double MULTIPLIER = 0.6180339887;
	    
	    double x = o.hashCode() * MULTIPLIER;
	    x -= Math.floor(x);	// take the fractional part
	    x *= tableSize;	// multiply by the table size
	    return (int) x;	// return the integer version
	}
    }
}

// $Id: MultiplicationMethod.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: MultiplicationMethod.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
