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

/** Class that performs a breadth-first search on a graph.  Based on
 * BFS code on page 532 of <i>Introduction to Algorithms</i>,
 * Second edition. */

public class BFS
{
    /** Array of <code>BFSInfo</code> objects, one per vertex, to hold
     * the result of the breadth-first search. */
    private BFSInfo[] bfsInfo;

    /**
     * Performs a breadth-first search on a graph from a given source
     * vertex, filling in distances and parents in the predecessor
     * graph in the <code>bfsInfo</code> array.
     *
     * @param g The graph.
     * @param s The source vertex.
     */
    public void search(AdjacencyListGraph g, Vertex s)
    {
	// Set each vertex to white with distance = infinity and no
	// parent in the predecessor subgraph.
	bfsInfo = new BFSInfo[g.getCardV()];
	for (int i = 0; i < bfsInfo.length; i++)
	    bfsInfo[i] = new BFSInfo();

	// Set the source's attributes correctly.
	BFSInfo sInfo = getBFSInfo(s);
	sInfo.setColor(Color.gray);
	sInfo.setDistance(0);

	QueueList q = new QueueList();
	q.enqueue(s);

	while (!q.isEmpty()) {
	    Vertex u = (Vertex) q.dequeue();
	    BFSInfo uInfo = getBFSInfo(u);
	    int uDistance = uInfo.getDistance();
	    
	    // Enqueue each undiscovered vertex adjacent to u.
	    Iterator iter = g.edgeIterator(u);

	    while (iter.hasNext()) {
		Vertex v = (Vertex) iter.next();
		BFSInfo vInfo = getBFSInfo(v);

		if (vInfo.getColor() == Color.white) {
		    vInfo.setColor(Color.gray);
		    vInfo.setDistance(uDistance + 1);
		    vInfo.setPredecessor(u);
		    q.enqueue(v);
		}
	    }

	    uInfo.setColor(Color.black);
	}
    }

   /**
     * Returns a reference to the <code>BFSInfo</code> object for a
     * given vertex.
     *
     * @param v The vertex for which the corresponding
     * <code>BFSInfo</code> is returned.
     */
     public BFSInfo getBFSInfo(Vertex v)
    {
	return getBFSInfo(v.getIndex());
    }

   /**
     * Returns a reference to the <code>BFSInfo</code> object for a
     * given vertex.
     *
     * @param v The index of the vertex for which the corresponding
     * <code>BFSInfo</code> is returned.
     */
    public BFSInfo getBFSInfo(int v)
    {
	return bfsInfo[v];
    }
}

// $Id: BFS.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: BFS.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
