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
 * Implements an interval whose endpoints are real numbers.
 */

public class Interval implements Comparable
{
    /** Low endpoint of the interval. */
    protected final double low;

    /** High endpoint of the interval. */
    protected final double high;

    /**
     * Initializes the endpoints of the interval.
     *
     * @param low The low endpoint.
     * @param high The high endpoint.
     */
    public Interval(double low, double high)
    {
	this.low = low;
	this.high = high;
    }

    /** Returns the low endpoint of the interval. */
    public double getLow()
    {
	return low;
    }

    /** Returns the high endpoint of the interval. */
    public double getHigh()
    {
	return high;
    }

    /**
     * Returns whether this interval overlaps another interval.
     *
     * @param i The other interval.
     * @return <code>true</code> if this interval overlaps
     * <code>i</code>, <code>false</code> otherwise.
     */
    public boolean overlaps(Interval i)
    {
	return this.low <= i.high && i.low <= this.high;
    }

    /**
     * Returns the {@link String} representation of this interval in
     * the form [low, high].
     */
    public String toString()
    {
	return "[" + low + ", " + high + "]";
    }

    /**
     * Compares this interval to another, based on their low
     * endpoints.
     *
     * @param o The other <code>Interval</code> object.
     * @return A negative integer if this <code>Interval</code> is
     * less; 0 if the objects are equal; a positive integer if this
     * <code>Interval</code> is greater.
     * @throws ClassCastException if <code>o</code> is not an
     * <code>Interval</code> object.
     */
    public int compareTo(Object o)
    {
	double otherLow = ((Interval) o).low;

	if (low < otherLow)
	    return -1;
	else if (low == otherLow)
	    return 0;
	else
	    return 1;
    }
}

// $Id: Interval.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: Interval.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
