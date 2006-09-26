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
 * A simple linear, doubly linked list without a sentinel but with a
 * search method.  All elements inserted must implement the
 * <code>Comparable</code> interface.
 */

public class LinearDLLDictionary extends LinearDLL implements Dictionary
{
    /**
     * Inserts an element at the head of the list.  This form of
     * <code>insert</code> is required by the {@link Dictionary}
     * interface.
     *
     * @param o The element to be inserted.
     * @return A handle to the new element.
     */
    public Object insert(Comparable o)
    {
	return super.insert(o);
    }

    /**
     * Inserts an element at the head of the list.  The element
     * inserted must be an object that implements the
     * <code>Comparable</code> interface.
     *
     * @param o The element to be inserted.
     * @return A handle to the new element.
     * @throws ClassCastException if the element does not implement
     * <code>Comparable</code>.
     */
    public Object insert(Object o)
    {
	if (o instanceof Comparable)
	    return super.insert(o);
	else
	    throw new ClassCastException("Object inserted in LinearDLLDictionary does not implement Comparable.");
    }

    /**
     * Inserts an element after a given element.  The element inserted
     * must be an object that implements the <code>Comparable</code>
     * interface.
     *
     * @param o The element to be inserted.
     * @param after The element after which the new element is to be
     * inserted.  If <code>null</code>, the new element is inserted at
     * the head of the list.
     * @return A handle to the new element.
     * @throws ClassCastException if the element does not implement
     * <code>Comparable</code>.
     */
    public Object insertAfter(Object o, Object after)
    {
	if (o instanceof Comparable)
	    return super.insertAfter(o, after);
	else
	    throw new ClassCastException("Object inserted in LinaerDLLDictionary does not implement Comparable.");
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
	Node x = head;

	while (x != null && ((Comparable) x.info).compareTo(k) != 0)
	    x = x.next;

	return x;
    }
}

// $Id: LinearDLLDictionary.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: LinearDLLDictionary.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
