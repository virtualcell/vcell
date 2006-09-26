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
 * Interface for dictionary data structures, defined on page 197 of
 * <i>Introduction to Algorithms</i>, Second edition.  Any object
 * inserted into a dictionary must implement the
 * <code>Comparable</code> interface.  In addition, for specific
 * implementations of the <code>Dictionary</code> interface, there may
 * be stronger restrictions on inserted objects, such as having to
 * implement the {@link DynamicSetElement} interface.
 */

public interface Dictionary
{
    /**
     * Inserts an element that implements <code>Comparable</code>.
     *
     * @param o The element to insert.
     * @return A handle to the inserted element.
     */
    public Object insert(Comparable o);

    /**
     * Removes an element.
     *
     * @param handle A handle to the element to remove.
     */
    public void delete(Object handle);

    /**
     * Searches for an element with a given key.  Depending on the
     * type of element inserted into the dictionary, the type of the
     * key given to this method may be the same as the type of the
     * objects inserted, or the type of the key given to this method
     * may be a different type than the type of the objects inserted
     * but still can be compared to the type of the inserted objects.
     * For example, see {@link DynamicSetElement.Helper#compareTo}.
     *
     * @param k The key being searched for.
     * @return A handle to the object found, or <code>null</code> if
     * there is no match.
     */
    public Object search(Comparable k);
}

// $Id: Dictionary.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: Dictionary.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
