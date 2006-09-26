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
 * Implements Minimum and Maximum from page 184 of <i>Introduction
 * to Algorithms</i>, Second edition.
 */

public class MinMax
{
    /**
     * Returns the smallest element in an array.
     *
     * @param array The array.
     */
    public static Comparable minimum(Comparable[] array)
    {
	Comparable min = array[0];

	for (int i = 1; i < array.length; i++)
	    if (min.compareTo(array[i]) < 0)
		min = array[i];
	
	return min;
    }

    /**
     * Returns the largest element in an array.
     *
     * @param array The array.
     */
    public static Comparable maximum(Comparable[] array)
    {
	Comparable max = array[0];
	
	for (int i = 1; i < array.length; i++)
	    if (max.compareTo(array[i]) > 0)
		max = array[i];
	return max;
    }
}

// $Id: MinMax.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: MinMax.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
