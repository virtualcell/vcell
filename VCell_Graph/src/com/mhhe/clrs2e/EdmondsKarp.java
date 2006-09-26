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
import java.awt.Color;

/** Implements the Edmonds-Karp algorithm for maximum flow from
 * Section 26.2 of <i>Introduction to Algorithms</i>, Second
 * edition. */

public class EdmondsKarp extends MaxFlow
{
    /**
     * Finds a maximum flow in a flow network from a given source to a
     * given sink.
     *
     * @param g The flow network.
     * @param s The source vertex.
     * @param t The sink vertex.
     */
    public void computeMaxFlow(FlowNetwork g, Vertex s, Vertex t)
    {
	// Start by setting the net flow on all edges to 0.  After we
	// do so, the residual network and the original flow network
	// are the same.
	zeroFlow(g);

	// Do a breadth-first search of the residual network.
	EKInfo[] bfsInfo = breadthFirstSearch(g, s, t);

	// There is a path from s to t in the residual network if t's
	// color is not still white.  Keep going as long as this is
	// the case.
	EKInfo tInfo = bfsInfo[t.getIndex()];
	while (tInfo.getColor() != Color.white) {
	    // Find the residual capacity of the s-t path.
	    double residualCapacity = Double.POSITIVE_INFINITY;
	    Vertex v = t;
	    EKInfo vInfo = tInfo;

	    while (v != s) {
		residualCapacity =
		    Math.min(residualCapacity,
			     ((FlowNetwork.FlowNetworkEdge) vInfo.getEdge())
			     .getResidualCapacity());
		v = vInfo.getPredecessor();
		vInfo = bfsInfo[v.getIndex()];
	    }

	    // Update the flow for each edge on the path.  Updating
	    // the flow automatically updates the residual graph.
	    v = t;
	    vInfo = tInfo;

	    while (v != s) {
		((FlowNetwork.FlowNetworkEdge) vInfo.getEdge()).
		    increaseNetFlow(residualCapacity);
		v = vInfo.getPredecessor();
		vInfo = bfsInfo[v.getIndex()];
	    }

	    // Do a breadth-first search on the new residual graph.
	    bfsInfo = breadthFirstSearch(g, s, t);
	    tInfo = bfsInfo[t.getIndex()];
	}
    }

    /** Inner class for the information found by a breadth-first
     * search in the Edmonds-Karp algorithm.  There is one
     * <code>EKInfo</code> object for each vertex in the flow
     * network. */
    private static class EKInfo extends BFSInfo
    {
	/** The edge leading into this vertex in the breadth-first
	 * search tree. */
	private Object edge;

	/** Initializes this object to have no edge entering the
	 * vertex yet. */
	public EKInfo()
	{
	    super();
	    edge = null;
	}

	/**
	 * Sets the edge entering the vertex.
	 * 
	 * @param e The entering edge.
	 */
	public void setEdge(Object e)
	{
	    edge = e;
	}

	/** Returns the edge entering the vertex. */
	public Object getEdge()
	{
	    return edge;
	}
    }

    /**
     * Performs a breadth-first search in the Edmonds-Karp algorithm.
     *
     * <p>
     *
     * This method is written as distinct code from the {@link
     * BFS#search} version of breadth-first search because
     * <ol>
     *   <li> We have to maintain additional information upon visiting
     *   a vertex, namely the edge that is taken into the vertex.
     *   <li> We stop once we find the sink.
     * </ol>
     *
     * @param g The flow network.
     * @param s The source vertex.
     * @param t The sink vertex.
     * @return An array of <code>EKInfo</code> objects, one per
     * vertex.
     */
    public EKInfo[] breadthFirstSearch(FlowNetwork g, Vertex s, Vertex t)
    {
	// Create and initialize the EKInfo objects.  Each one is
	// white, has distance = infinity, and no predecessor.  We
	// won't be changing the distance, since we don't need it
	// here.
	int cardV = g.getCardV();
	EKInfo[] info = new EKInfo[cardV];
	for (int i = 0; i < cardV; i++)
	    info[i] = new EKInfo();

	// Set the source's attributes correctly.
	EKInfo sInfo = info[s.getIndex()];
	sInfo.setColor(Color.gray);
	
	QueueList q = new QueueList();
	q.enqueue(s);

	EKInfo tInfo = info[t.getIndex()];
	while (tInfo.getColor() == Color.white && !q.isEmpty()) {
	    Vertex u = (Vertex) q.dequeue();
	    
	    // Enqueue each undiscovered vertex adjacent to u in the
	    // residual graph.
	    FlowNetworkEdgeIterator iter = g.flowNetworkEdgeIterator(u, true);

	    while (iter.hasNext()) {
		Vertex v = (Vertex) iter.next();
		EKInfo vInfo = info[v.getIndex()];

		if (vInfo.getColor() == Color.white) {
		    vInfo.setColor(Color.gray);
		    vInfo.setPredecessor(u);
		    vInfo.setEdge(iter.getEdge());
		    q.enqueue(v);
		}
	    }

	    info[u.getIndex()].setColor(Color.black);
	}

	return info;
    }
}

// $Id: EdmondsKarp.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: EdmondsKarp.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
