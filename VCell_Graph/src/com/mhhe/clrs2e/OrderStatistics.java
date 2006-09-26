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
 * Interface for a select method that finds the ith order statistic in
 * an array of {@link Comparable} objects.
 */

public interface OrderStatistics
{
    /**
     * Returns the ith smallest element in an array.
     *
     * @param array The array.
     * @param i Which order statistic we want.
     */
    public Comparable select(Comparable[] array, int i);
}

// $Id: OrderStatistics.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: OrderStatistics.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
