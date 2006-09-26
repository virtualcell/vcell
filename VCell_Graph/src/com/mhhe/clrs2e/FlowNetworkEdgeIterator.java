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

import java.util.Iterator;

/** Interface for an iterator that returns edges of a flow network. */

public interface FlowNetworkEdgeIterator extends Iterator
{
    /** Returns the edge found by the most recent call to
     * <code>next</code>.  Unlike <code>next</code>, this method
     * returns an object corresponding to the edge itself rather than
     * just the adjacent vertex. */
    public Object getEdge();

    /** Returns the capacity of the edge returned by the most recent
     * call to <code>next</code>. */
    public double getCapacity();

    /** Returns the net flow of the edge returned by the most recent
     * call to <code>next</code>. */
    public double getNetFlow();
	
    /** Returns the residual capacity of the edge returned by the most
     * recent call to <code>next</code>. */
    public double getResidualCapacity();

    /** Zeros out the net flow of the edge returned by the most recent
     * call to <code>next</code>. */
    public void zeroNetFlow();

    /**
     * Increases the net flow of the edge returned by the most recent
     * call to <code>next</code>.
     *
     * @param amount The amount by which this edge's flow is
     * increased.
     */
    public void increaseNetFlow(double amount);
}

// $Id: FlowNetworkEdgeIterator.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: FlowNetworkEdgeIterator.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
