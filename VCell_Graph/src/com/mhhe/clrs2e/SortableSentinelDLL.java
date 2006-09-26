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
 * Circular, doubly linked list with a sentinel, that also has a sort
 * method.  Uses insertion sort, but directly on the linked list.
 */

public class SortableSentinelDLL
    extends SentinelDLLDictionary implements Dictionary
{
    /**
     * Runs insertion sort on a circular, doubly linked list with a
     * sentinel.
     *
     * @throws ClassCastException if the objects in the list do not
     * implement the {@link Comparable} interface.
     */
    public void sort()
    {
	for (Node i = nil.next.next; i != nil; i = i.next) {
	    Comparable key = (Comparable) i.info;

	    // Find where to put node i.info.
	    Node j;
	    for (j = i.prev;
		 j != nil && key.compareTo((Comparable) (j.info)) < 0;
		 j = j.prev)
		j.next.info = j.info;

	    // At this point, either j is the sentinel, or j is the
	    // rightmost node whose key is less than or equal to i's
	    // key.  Either way, we should place i.info immediately to
	    // the right of node j.
	    j.next.info = key;
	}
    }
}

// $Id: SortableSentinelDLL.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: SortableSentinelDLL.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
