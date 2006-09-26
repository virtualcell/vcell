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

/** Implements the Greedy-Activity-Selector algorithm from page 378
 * of <i>Introduction to Algorithms</i>, Second edition. */

public class GreedyActivitySelector implements ActivitySelector
{
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
	int n = activities.length-1; // number of activities other than 0th

	SentinelDLL a = new SentinelDLL(); // set of selected activities
	a.insert(activities[1]);
	int selected = 1;	           // how many activities in a
	int i = 1;		           // most recently selected activity

	for (int m = 2; m <= n; m++) {
	    if (activities[m].comesAfter(activities[i])) {
		a.insertAtTail(activities[m]);
		selected++;
		i = m;
	    }
	}

	// Convert the linked list to an array, and return the array.
	Activity[] result = new Activity[selected];
	a.toArray(result);
	return result;
    }
}

// $Id: GreedyActivitySelector.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: GreedyActivitySelector.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
