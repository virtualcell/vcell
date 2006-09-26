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

/** Implements the Recursive-Activity-Selector algorithm from page 376
 * of <i>Introduction to Algorithms</i>, Second edition. */

public class RecursiveActivitySelector implements ActivitySelector
{
    /** How many activities have been selected. */
    private int selected;

    /**
     * Determines a maximum set of mutually compatible activities.
     *
     * @param activities Array of activities, assumed to be sorted by
     * finish time.  <code>activities[0]</code> must have a finish
     * time of 0.  All other activities must have nonnegative start
     * times and positive finish times.
     * @return An array consisting of a maximum set of mutually
     * compatible activities from <code>activities</code>.
     */
    public Activity[] selector(Activity[] activities)
    {
	selected = 0;		// nothing selected yet

	// Call the recursive method to place each selected activity
	// in a linked list.
	SentinelDLL selectedSet
	    = recursiveActivitySelector(activities, 0, activities.length-1);

	// Convert the linked list to an array, and return the array.
	Activity[] result = new Activity[selected];
	selectedSet.toArray(result);
	return result;
    }

    /**
     * Determines a maximum set of mutually compatible activities in a
     * subset of the set of activities.  Implements the
     * Recursive-Activity-Selector procedure on page 376.
     *
     * @param activities Array of activities, assumed to be sorted by
     * finish time.  <code>activities[0]</code> must have a finish
     * time of 0.  All other activities must have nonnegative start
     * times and positive finish times.
     * @param i Index of the last activity selected.
     * @param n Number of activities other than the 0th activity.
     * @return A linked list of the selected activities.
     */
    private SentinelDLL
	recursiveActivitySelector(Activity[] activities, int i, int n)
    {
	int m = i + 1;		// candidate activity

	while (m <= n && !activities[m].comesAfter(activities[i]))
	    m++;

	if (m <= n) {
	    SentinelDLL result = recursiveActivitySelector(activities, m, n);
	    result.insert(activities[m]);
	    selected++;		// one more activity is selected
	    return result;
	}
	else
	    return new SentinelDLL(); // empty set
    }
}

// $Id: RecursiveActivitySelector.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: RecursiveActivitySelector.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
