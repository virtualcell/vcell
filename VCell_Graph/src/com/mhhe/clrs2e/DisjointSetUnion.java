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

/** Interface for a disjoint-set-union data structure from Chapter 21
 * of <i>Introduction to Algorithms</i>, Second edition. */

public interface DisjointSetUnion
{
    /**
     * Makes a singleton set containing an object.
     *
     * @param x The object in the singleton set.
     * @return A handle that serves to identify the set in future
     * operations.
     */
    public Object makeSet(Object x);

    /**
     * Unites two sets, identified by handles to objects in the sets.
     * The original sets are destroyed.
     *
     * @param x Handle to an object in one set.
     * @param y Handle to an object in the other set.
     */
     public void union(Object x, Object y);

    /**
     * Returns the object that serves as the representative of the set
     * containing an object.
     *
     * @param x Handle to the object.
     * @return A handle to the representative of the set containing x.
     */
    public Object findSet(Object x);
}

// $Id: DisjointSetUnion.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: DisjointSetUnion.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
