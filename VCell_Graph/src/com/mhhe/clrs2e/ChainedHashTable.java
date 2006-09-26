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

/** Implements a hash table with chaining, as described on page 226 of
 * <i>Introduction to Algorithms</i>, Second edition.  Elements
 * inserted must implement the <code>Comparable</code> interface.
 * Hash keys are based on the Java <code>hashCode</code> method
 * applied to objects and then run through the multiplication method
 * on pages 231-232. */

public class ChainedHashTable implements Dictionary
{
    /** The hash table is an array of linked lists.  They are doubly
     * linked in order to make deletion fast. */
    private SentinelDLLDictionary[] table;

    /** The class for the hash function used. */
    private MultiplicationMethod hasher;

    /**
     * Creates a new chained hash table with 16 entries.
     */
    public ChainedHashTable()
    {
	initChainedHashTable(16);
    }

    /**
     * Creates a new chained hash table of a given size.
     *
     * @param m The size of the chained hash table.
     */
    public ChainedHashTable(int m)
    {
	initChainedHashTable(m);
    }

    /**
     * Initializes a chained hash table of a given size.
     *
     * @param m The size of the chained hash table.
     */
    private void initChainedHashTable(int m)
    {
	hasher = new MultiplicationMethod(m);
	table = new SentinelDLLDictionary[m];
	for (int i = 0; i < m; i++)
	    table[i] = new SentinelDLLDictionary();
    }

     /**
     * Inserts an element into a chained hash table.
     *
     * @param o The element to insert.
     * @return A handle to the inserted element.
     */
    public Object insert(Comparable o)
    {
	return table[hasher.hash(o)].insert(o);
    }

    /**
     * Removes an element from a chained hash table.
     *
     * @param handle A handle to the element to remove.
     */
    public void delete(Object handle)
    {
	// Before we can delete an object from one of the linked
	// lists, we have to find out which linked list it is in.
	int key = hasher.hash(SentinelDLLDictionary.dereference(handle));

	// Now we can delete it from its list.
	table[key].delete(handle);
    }

     /**
     * Searches a chained hash table for an element with a given key.
     *
     * @param k The key being searched for.
     * @return A handle to the object found, or <code>null</code> if
     * there is no match.
     */
    public Object search(Comparable k)
    {
	return table[hasher.hash(k)].search(k);
    }
}

// $Id: ChainedHashTable.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: ChainedHashTable.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
