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

/** Implements a hash table with quadratic probing as described on
 * pages 239-240 of <i>Introduction to Algorithms</i>, Second
 * edition. */

public class QuadraticProbingHashTable extends OpenAddressingHashTable
{
    /** Constant used for quadratic probing. */
    private double c1, c2;

    /**
     * Creates a new open-addressed hash table with quadratic probing
     * with 16 entries.  The constants are set to <code>c1</code> =
     * 0.5 and <code>c2</code> = 0.5.
     */
    public QuadraticProbingHashTable()
    {
	super();
	c1 = 0.5;
	c2 = 0.5;
    }

    /**
     * Creates a new open-addressed hash table with quadratic probing
     * of a given size.
     *
     * @param m The size of the open-addressed hash table.
     * @param c1 One of the constants used for probing.
     * @param c2 The other constant used for probing.
     */
    public QuadraticProbingHashTable(int size, double c1, double c2)
    {
	super(size);
	this.c1 = c1;
	this.c2 = c2;
    }

    /**
     * Computes a hash function for an open-addressing hash table,
     * uslng quadratic probing.
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

 	return ((int) (o.hashCode() + (c1 * i) + (c2 * i * i))) % m;
    }
}

// $Id: QuadraticProbingHashTable.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: QuadraticProbingHashTable.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
