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

import java.awt.Color;

/** Class for information determined about a vertex by depth-first
 * search. */

public class DFSInfo
{
    /** This vertex's discovery time. */
    private int d;

    /** This vertex's finish time. */
    private int f;

    /** This vertex's color. */
    private Color color;

    /** This vertex's parent in the predecessor graph. */
    private Vertex pi;

    /** Initializes the discovery and finish times to -1, the color to
     * white, and the parent to <code>null</code>. */
    public DFSInfo()
    {
	d = -1;
	f = -1;
	color = Color.white;
	pi = null;
    }

    /** Sets the vertex's discovery time.
     *
     * @param time The discovery time.
     */
    public void setDiscoveryTime(int time)
    {
	d = time;
    }

    /** Returns the vertex's discovery time. */
    public int getDiscoveryTime()
    {
	return d;
    }

    /**
     * Sets the vertex's finish time.
     *
     * @param time The finish time.
     */
    public void setFinishTime(int time)
    {
	f = time;
    }

    /** Returns the vertex's finish time. */
    public int getFinishTime()
    {
	return f;
    }

    /**
     * Sets the vertex's color
     *
     * @param c The color.
     */
    public void setColor(Color c)
    {
	color = c;
    }

    /** Returns the vertex's color. */
    public Color getColor()
    {
	return color;
    }

    /**
     * Sets the vertex's parent in the predecessor graph.
     *
     * @param v The parent.
     */
    public void setPredecessor(Vertex v)
    {
	pi = v;
    }

    /** Returns the vertex's parent in the predecessor graph. */
    public Vertex getPredecessor()
    {
	return pi;
    }

    /** Returns the <code>String</code> representation of this object. */
    public String toString()
    {
	String parentName;

	if (pi == null)
	    parentName = "(null)";
	else
	    parentName = pi.getName();

	return "d = " + d + ", f = " + f + ", pi = " + parentName;
    }
}

// $Id: DFSInfo.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: DFSInfo.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
