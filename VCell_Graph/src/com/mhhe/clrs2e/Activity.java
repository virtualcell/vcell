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

/** A class for activities in the activity-selection problem of
 * Section 16.1 of <i>Introduction to Algorithms</i>, Second
 * edition. */

public class Activity implements Comparable
{
    /** Start time. */
    private double s;

    /** Finish time. */
    private double f;

    /**
     * Creates a new activity.
     *
     * @param startTime Start time.
     * @param finishTime Finish time.
     */
    public Activity(double startTime, double finishTime)
    {
	s = startTime;
	f = finishTime;
    }

    /**
     * Compares this activity to another, based on their finish times.
     *
     * @param o The other activity.
     * @return -1 if this activity finishes first, 0 if the two
     * activities have the same finish time, 1 if the other activity
     * finishes first.
     * @throws ClassCastException if <code>o</code> does not reference
     * an <code>Activity</code> object.
     */
    public int compareTo(Object o)
    {
	Activity act = (Activity) o;

	if (f < act.f)
	    return -1;
	else if (f == act.f)
	    return 0;
	else
	    return 1;
    }

    /**
     * Returns a <code>boolean</code> value indicating whether this
     * activity starts no earlier than another finishes.
     *
     * @param act The other activity.
     * @return <code>true</code> if this activity starts no earlier
     * than <code>act</code> finishes.
     */
    public boolean comesAfter(Activity act)
    {
	return s >= act.f;
    }

    /** Returns the <code>String</code> representation of this
     * activity, using half-open interval notation. */
    public String toString()
    {
	return "[" + s + ", " + f + ")";
    }
}

// $Id: Activity.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: Activity.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
