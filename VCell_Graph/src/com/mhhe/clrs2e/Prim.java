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

/** Implements Prim's algorithm for minimum spanning tree from
 * page 572 of <i>Introduction to Algorithms</i>, Second
 * edition. */

public class Prim implements MST
{
    /**
     * Computes the minimum spanning tree of a weighted, undirected
     * graph.
     *
     * @param g The undirected graph.
     * @return The subgraph of <code>g</code> that is a minimum
     * spanning tree.
     */
    public WeightedAdjacencyListGraph computeMST(WeightedAdjacencyListGraph g)
    {
	// Create a min-priority queue.
	MinPriorityQueue q = new MinHeapPriorityQueue();

	// Create a PrimInfo object for each vertex, adding each
	// vertex to the min-priority queue.
	int cardV = g.getCardV();
	PrimInfo[] vertex = new PrimInfo[cardV];
	for (int i = 0; i < cardV; i++)
	    vertex[i] = new PrimInfo(g.getVertex(i), q);

	// Arbitrarily pick the vertex with index 0 as the root.
	q.decreaseKey(vertex[0].handle, new Double(0));

	while (!q.isEmpty()) {
	    // Find the vertex in the queue with the smallest key.
	    PrimInfo uInfo = (PrimInfo) q.extractMin();
	    uInfo.handle = null; // no longer in the queue
	    Vertex u = uInfo.theVertex;

	    // Check each incident edge.
	    WeightedEdgeIterator edgeIter = g.weightedEdgeIterator(u);

	    while (edgeIter.hasNext()) {
		Vertex v = (Vertex) edgeIter.next();
		PrimInfo vInfo = vertex[v.getIndex()];
		double weight = edgeIter.getWeight();
		if (vInfo.handle != null && weight < vInfo.key.doubleValue()) {
		    // Add this edge to the MST and decrease v's key.
		    vInfo.pi = u;
		    q.decreaseKey(vInfo.handle, new Double(weight));
		}
	    }
	}

	// From the pi values, make the MST.
	WeightedAdjacencyListGraph mst =
	    (WeightedAdjacencyListGraph) g.useSameVertices();
	
	for (int i = 0; i < cardV; i++) {
	    PrimInfo vInfo = vertex[i];
	    if (vInfo.pi != null)
		mst.addEdge(vInfo.pi, vInfo.theVertex,
			    vInfo.key.doubleValue());
	}

	return mst;	
    }

    /** Inner class to maintain the <code>Vertex</code> object, key,
     * parent, and handle into the priority queue for each vertex. */
    private static class PrimInfo implements DynamicSetElement
    {
	/** The vertex. */
	public Vertex theVertex;

	/** Vertex's key, representing the weight of the lightest edge
	 * between this vertex and some vertex known to be in the
	 * minimum spanning tree. */
	public Double key;

	/** The current parent for this vertex. */
	public Vertex pi;

	/** A handle to the vertex's information in the priority
	 * queue, or <code>null</code> if the vertex is not in the
	 * priority queue. */
	public Object handle;

	/**
	 * Sets the instance variables so that there is no known edge
	 * between this vertex and any vertex in the minimum spanning
	 * tree (i.e., the key is infinity), and inserts this object
	 * into the min-priority queue.
	 *
	 * @param v The vertex.
	 * @param q The min-priority queue.
	 */
	public PrimInfo(Vertex v, MinPriorityQueue q)
	{
	    theVertex = v;
	    key = new Double(Double.POSITIVE_INFINITY);
	    pi = null;
	    handle = q.insert(this);
	}

	/**
	 * Sets the key.
	 *
	 * @param key The new key value.
	 */
	public void setKey(Comparable key)
	{
	    this.key = (Double) key;
	}

	/** Returns the value of the key. */
	public Comparable getKey()
	{
	    return key;
	}

	/**
	 * Compares this object's key to that of another
	 * <code>PrimInfo</code> object.
	 *
	 * @param e The other <code>PrimInfo</code> object.
	 * @return A negative integer if the key of this object's
	 * vertex is less; 0 if the keys are equal; a positive integer
	 * if the key of this object's vertex is greater.
	 */
	public int compareTo(Object e)
	{
	    return DynamicSetElement.Helper.compareTo(this, e);
	}
    }
}

// $Id: Prim.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: Prim.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
