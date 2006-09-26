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

/** Implements a direct-address table from page 222 of <i>Introduction
 * to Algorithms</i>, Second edition.  Indices into the direct-address
 * table are based on the Java <code>hashCode</code> method applied to
 * objects, modulo the table size. */

public class DirectAddressTable implements Dictionary
{
    /** The direct-address table.  An object with key <code>k</code>
     * is stored in <code>table[k]</code>. */
    private Object[] table;

    /** The number of entries in the direct-address table. */
    private int m;

    /**
     * Creates a new direct-address table of a given size.
     *
     * @param m The size of the direct-address table.
     */
    public DirectAddressTable(int m)
    {
	this.m = m;
	table = new Object[m];
    }

    /**
     * Inserts an element into a direct-address table.
     *
     * @param o The element to insert.
     * @return A handle to the new element.  The handle is the object
     * that is inserted.
     */
    public Object insert(Comparable o)
    {
	table[indexOf(o)] = o;
	return o;
    }

    /**
     * Removes an element from a direct-address table.
     *
     * @param handle A handle to the element to remove.
     * @throws ClassCastException if handle does not reference an
     * object that implements <code>Comparable</code>.
     */
    public void delete(Object handle)
    {
	table[indexOf((Comparable) handle)] = null;
    }

    /**
     * Searches for an element with a given key in a direct-address
     * table.
     *
     * @param k The key being searched for.
     * @return A handle to the object found, or <code>null</code> if
     * there is no match.
     */
    public Object search(Comparable k)
    {
	return table[indexOf(k)];
    }   

    /**
     * Returns the index into the direct-address table at which an
     * element is placed.  If the element implements
     * <code>DynamicSetElement</code>, the hash code of the element's
     * key is used.  Otherwise, the hash code of the element itself is
     * used.  In any case, the hash code is taken modulo the table
     * size.
     *
     * @param e The element.
     * @return The index into the direct-address table.
     */
    private int indexOf(Comparable k)
    {
	if (k instanceof DynamicSetElement)
	    return ((DynamicSetElement) k).getKey().hashCode() % m;
	else
	    return k.hashCode() % m;
    }
}

// $Id: DirectAddressTable.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: DirectAddressTable.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
