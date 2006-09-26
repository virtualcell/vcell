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
 * A class for vertices in graphs.  Every vertex has a name and an
 * index in its graph.
 *
 * @see Graph
 * @see AdjacencyListGraph
 * @see AdjacencyMatrixGraph
 * @see WeightedAdjacencyListGraph
 * @see WeightedAdjacencyMatrixGraph
 * @see FlowNetwork
 */

public class Vertex
{
    /** Value that indicates that this vertex does not yet have an
     * index, i.e., the index is unknown. */
    public static final int UNKNOWN_INDEX = -1;

    /** Index of this vertex in its graph, 0 to cardV-1. */
    private int index;

    /** This vertex's name. */
    private String name;

    /**
     * Creates a vertex whose index is unknown.
     *
     * @param name This vertex's name.
     */
    public Vertex(String name)
    {
	index = UNKNOWN_INDEX;
	this.name = name;
    }

    /** Creates a vertex with a given index and name.
     *
     * @param index This vertex's index.
     * @param name This vertex's name.
     */
    public Vertex(int index, String name)
    {
	this.index = index;
	this.name = name;
    }

    /**
     * Sets this vertex's index.
     *
     * @param index New value for this vertex's index.
     */
    public void setIndex(int index)
    {
	this.index = index;
    }

    /** Returns this vertex's index. */
    public int getIndex()
    {
	return index;
    }

    /**
     * Sets this vertex's name.
     *
     * @param name New value for this vertex's name.
     */
    public void setName(String name)
    {
	this.name = name;
    }

    /** Returns this vertex's name. */
    public String getName()
    {
	return name;
    }

    /** Returns the <code>String</code> representation of this
     * vertex. */
    public String toString()
    {
	return name + " (index = " + index + ")"; 
    }
}

// $Id: Vertex.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: Vertex.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
