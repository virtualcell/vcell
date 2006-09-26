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

/** Implements a hash table with linear probing as described on page
 * 239 of <i>Introduction to Algorithms</i>, Second edition. */

public class LinearProbingHashTable extends OpenAddressingHashTable
{
    /**
     * Creates a new open-addressed hash table with linear probing
     * with 16 entries.
     */
    public LinearProbingHashTable()
    {
	super();
    }

    /**
     * Creates a new open-addressed hash table with linear probing of
     * a given size.
     *
     * @param m The size of the open-addressed hash table.
     */
    public LinearProbingHashTable(int size)
    {
	super(size);
    }

    /**
     * Computes a hash function for an open-addressing hash table,
     * uslng linear probing.
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

	return (o.hashCode() + i) % m;
    }
}

// $Id: LinearProbingHashTable.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: LinearProbingHashTable.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
