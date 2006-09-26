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

/** Abstract base class for hash tables that use open addressing, as
 * described on pages 237-241 of <i>Introduction to Algorithms</i>,
 * Second edition.  Handles returned by the <code>insert</code> and
 * <code>search</code> methods are simply indices into the hash
 * table. */

abstract public class OpenAddressingHashTable implements Dictionary
{
    /** Indicator that a slot used to hold an object but that object
     * has been deleted from the hash table. */
    private final String DELETED = new String("This slot has been deleted");

    /** The hash table. */
    private Comparable[] table;

    /** How many slots are in the hash table. */
    protected int m;

    /**
     * Creates a new open-addressed hash table with 16 entries.
     */
    public OpenAddressingHashTable()
    {
	initOpenAddressingHashTable(16);
    }

    /**
     * Creates a new open-addressed hash table of a given size.
     *
     * @param m The size of the open-addressed hash table.
     */
    public OpenAddressingHashTable(int m)
    {
	initOpenAddressingHashTable(m);
    }

    /**
     * Initializes an open-addressed hash table of a given size.
     *
     * @param m The size of the open-addressed hash table.
     */
    private void initOpenAddressingHashTable(int m)
    {
	this.m = m;
	table = new Comparable[m];
	for (int i = 0; i < m; i++)
	    table[i] = null;
    }    

    /**
     * Computes a hash function for an open-addressing hash table,
     * dependent on an object and a probe number.  This method is
     * defined by subclasses to implement different forms of open
     * addressing.
     *
     * <p>
     *
     * The probe sequence <hash(o, 0), hash(o, 1), hash(o, 2), ...,
     * hash(o, m-1)> must be a permutation of <0, 1, 2, ..., m-1>, so
     * that every slot in the hash table is probed once in the first m
     * probes.
     *
     * @param o The object.  If the object implements
     * <code>DynamicSetElement</code>, the hash value is of its key.
     * @param i The probe number.
     * @return An index into the open-addressed hash table.
     */
    abstract protected int hash(Object o, int i);

    /**
     * Inserts an element that implements <code>Comparable</code>.
     *
     * @param o The element to insert.
     * @return A handle to the inserted element.  The handle is the
     * index in the hash table at which the element was inserted.
     * @throws HashTableOverflow if the hash table is full.
     */
    public Object insert(Comparable o)
    {
	int i = 0;

	do {
	    int j = hash(o, i);

	    if (table[j] == null || table[j] == DELETED) {
		table[j] = o;
		return new Integer(j);
	    }
	    else
		i++;
	}
	while (i < m);

	// None of the first m probes found an empty slot, and so the
	// hash table is full.
	throw new HashTableOverflow();
    }

    /**
     * Removes an element.
     *
     * @param handle A handle to the element to remove.  The handle is
     * just the index of the element to remove.
     * @throws ClassCastException if <code>handle</code> does not
     * reference an <code>Integer</code>.
     * @throws ArrayIndexOutOfBoundsException if the integer value of
     * <code>handle</code> is not in the range 0 to m-1.
     */
    public void delete(Object handle)
    {
	table[((Integer) handle).intValue()] = DELETED;
    }

    /**
     * Searches for an element with a given key.
     *
     * @param k The key being searched for.
     * @return A handle to the object found, or <code>null</code> if
     * there is no match.
     */
    public Object search(Comparable k)
    {
	int i = 0;
	int j;

	do {
	    j = hash(k, i);

	    if (table[j] != null && table[j] != DELETED &&
		table[j].compareTo(k) == 0)
		return new Integer(j); // found it in slot j

	    i++;
	}
	while (table[j] != null && i < m);

	// If we get here, then either we found an empty slot before
	// finding an object with key k, or we made m unsuccessful
	// probes.  In either case, we conclude that key k is not in
	// this hash table.
	return null;
    }

    /** Returns the <code>String</code> representation of this
     * hash table, with one entry per line. */
    public String toString()
    {
	String result = "";

	for (int i = 0; i < m; i++) {
	    result += "table[" + i + "] = ";
	    if (table[i] == null)
		result += "null\n";
	    else if (table[i] instanceof DynamicSetElement)
		result +=
		    ((DynamicSetElement) table[i]).getKey().toString() + "\n";
	    else
		result += table[i].toString() + "\n";
	}

	return result;
    }
}

// $Id: OpenAddressingHashTable.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: OpenAddressingHashTable.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
