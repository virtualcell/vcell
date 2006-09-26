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

/** Implements a hash table with double hashing as described on pages
 * 240-241 of <i>Introduction to Algorithms</i>, Second edition.  The
 * auxiliary hash function <code>h2</code> always returns an odd
 * value, which works well if the table size is a power of 2.  Make a
 * subclass of this class and override <code>h2</code> to use a
 * different auxiliary hash function. */

public class DoubleHashingHashTable extends OpenAddressingHashTable
{
    /** The class for the auxiliary hash function <code>h2</code>. */
    protected MultiplicationMethod hasher;

    /**
     * Creates a new open-addressed hash table with double hashing
     * with 16 entries.
     */
    public DoubleHashingHashTable()
    {
	super();
	hasher = new MultiplicationMethod(m);
    }

    /**
     * Creates a new open-addressed hash table with double hashing of
     * a given size.
     *
     * @param m The size of the open-addressed hash table.
     */
    public DoubleHashingHashTable(int size)
    {
	super(size);
	hasher = new MultiplicationMethod(size);
    }

    // An auxiliary hash function used by hash(o, i) to determine the
    // probe sequence when there is a collision.
    // The multiplication method (m * [PHI * k (mod 1)]) is used where
    // m is the table size, PHI is some constant, k is an integer
    // representation of the key and (mod 1) means that the fractional
    // part of the number is used.
    /**
     * An auxiliary hash function.  In this implementation, it returns
     * an odd number, so that it is relatively prime to the size of
     * the hash table, if the hash-table size is a power of 2.  If the
     * hash-table size is not a power of 2, override this
     * implementation.
     *
     * @param o An object.  If the object implements
     * <code>DynamicSetElement</code>, the hash value is of its key.
     * @return An integer, which is odd in this implementation.  In
     * any case, the value returned should be nonzero but need not be
     * a valid index into the hash table.
     */
    protected int h2(Object o)
    {
	// Return the result of the multiplication method, but set the
	// least significant bit to 1 to ensure that the return value
	// is odd.
	return hasher.hash(o) | 1;
    }

    /**
     * Computes a hash function for an open-addressing hash table,
     * uslng double hashing.
     *
     * @param o An object.  If the object implements
     * <code>DynamicSetElement</code>, the hash value is of its key.
     * @param i The probe number.
     * @return An index into the open-addressed hash table.
     */
    protected int hash(Object o, int i)
    {
	// If the object implements <code>DynamicSetElement</code>,
	// hash its key.  Otherwise, just hash the object itself.
	if (o instanceof DynamicSetElement)
	    o = ((DynamicSetElement) o).getKey();

	return (o.hashCode() + (i * h2(o))) % m;
    }    
}

// $Id: DoubleHashingHashTable.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: DoubleHashingHashTable.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
