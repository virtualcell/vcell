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
import java.util.Iterator;

/** Class that performs a depth-first search on a graph.  Based on DFS
 * code on page 541 of <i>Introduction to Algorithms</i>, Second
 * edition. */

public class DFS
{
    /** The global timestamp. */
    protected int time;

    /** Array of <code>DFSInfo</code> objects, one per vertex, to hold
     * the result of the depth-first search. */
    protected DFSInfo[] dfsInfo; // result of the depth-first search

    /**
     * Performs a depth-first search on a graph from a given source
     * vertex, filling in discovery times, finish times, and parents
     * in the predecessor graph in the <code>dfsInfo</code> array.
     *
     * @param g The graph.
     * @param s The source vertex.
     */
    public void search(AdjacencyListGraph g)
    {
	// Set each vertex to white with no parent in the predecessor
	// subgraph.
	dfsInfo = new DFSInfo[g.getCardV()];
	for (int i = 0; i < dfsInfo.length; i++)
	    dfsInfo[i] = new DFSInfo();

	time = 0;		// global time is 0

	// Call dfsVisit on each unvisited vertex.
	Iterator iter = g.vertexIterator();

	while (iter.hasNext()) {
	    Vertex u = (Vertex) iter.next();
	    if (getDFSInfo(u).getColor() == Color.white)
		dfsVisit(g, u);
	}
    }

    /**
     * Performs a depth-first search on a graph starting from a given
     * vertex, filling in discovery times, finish times, and parents
     * in the predecessor graph in the <code>dfsInfo</code> array.
     *
     * @param g The graph.
     * @param u The vertex being searched from.
     */
    protected void dfsVisit(AdjacencyListGraph g, Vertex u)
    {
	DFSInfo uInfo = getDFSInfo(u);
	uInfo.setColor(Color.gray); // white vertex u has just been discovered
	time++;
	uInfo.setDiscoveryTime(time);
	discover(g, u);		    // if there is something to do now

	// Explore each adjacent edge (u,v).
	Iterator iter = g.edgeIterator(u);

	while (iter.hasNext()) {
	    Vertex v = (Vertex) iter.next();
	    DFSInfo vInfo = getDFSInfo(v);

	    if (vInfo.getColor() == Color.white) {
		vInfo.setPredecessor(u);
		dfsVisit(g, v);
	    }
	}

	uInfo.setColor(Color.black); // blacken u; it is finished
	time++;
	uInfo.setFinishTime(time);
	finish(g, u);		// if there is something to do now
    }

   /**
     * Returns a reference to the <code>DFSInfo</code> object for a
     * given vertex.
     *
     * @param v The vertex for which the corresponding
     * <code>DFSInfo</code> is returned.
     */
    public DFSInfo getDFSInfo(Vertex v)
    {
	return getDFSInfo(v.getIndex());
    }

   /**
     * Returns a reference to the <code>DFSInfo</code> object for a
     * given vertex.
     *
     * @param v The index of the vertex for which the corresponding
     * <code>DFSInfo</code> is returned.
     */
    public DFSInfo getDFSInfo(int v)
    {
	return dfsInfo[v];
    }

    /**
     * Performs an action upon discovering a vertex in a graph.  This
     * method does nothing and is designed to be overridden in
     * subclasses.
     *
     * @param g The graph.
     * @param u The vertex just discovered.
     */
    protected void discover(AdjacencyListGraph g, Vertex u)
    {
    }

    /**
     * Performs an action upon finishing a vertex in a graph.  This
     * method does nothing and is designed to be overridden in
     * subclasses.
     *
     * @param g The graph.
     * @param u The vertex just finished.
     */
    protected void finish(AdjacencyListGraph g, Vertex u)
    {
    }
}

// $Id: DFS.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: DFS.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
