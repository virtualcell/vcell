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

/** Implementation of Dijkstra's algorithm on page 595 of
 * <i>Introduction to Algorithms</i>, Second edition. */

public class Dijkstra extends SingleSourceShortestPaths
{
    /**
     * Sets up the instance variables, including allocation of the
     * <code>spInfo</code> array but <em>not</em> allocation of the
     * <code>ShortestPathInfo</code> objects referenced by the array.
     * (That's {@link #initializeSingleSource}'s job.)
     *
     * @param theGraph The graph for which we are computing
     * single-source shortest paths.
     */
    public Dijkstra(WeightedAdjacencyListGraph theGraph)
    {
	super(theGraph);
    }

    /**
     * Computes single-source shortest paths from a given source
     * vertex, filling in weights and predecessors in the
     * <code>spInfo</code> array.
     *
     * @param s The source vertex.
     */
    public void computeShortestPaths(Vertex s)
    {
	initializeSingleSource(s);

	// Create a min-priority queue.
	MinPriorityQueue q = new MinHeapPriorityQueue();

	// The information we're keeping for each vertex is a
	// DijkstraInfo object.  We need to set the theVertex and
	// handle fields.  By inserting each DijkstraInfo object into
	// the min-priority queue, we get the handle that we store in
	// the object.
	int cardV = g.getCardV();
	for (int i = 0; i < cardV; i++) {
	    DijkstraInfo info = (DijkstraInfo) getShortestPathInfo(i);
	    info.theVertex = g.getVertex(i);
	    info.handle = q.insert(info);
	}

	q.decreaseKey(((DijkstraInfo) getShortestPathInfo(s)).handle,
		      new Double(0));

	while (!q.isEmpty()) {
	    // Find the vertex in the queue with the smallest key.
	    DijkstraInfo uInfo = (DijkstraInfo) q.extractMin();
	    uInfo.handle = null; // no longer in the queue
	    Vertex u = uInfo.theVertex;
	    double du = getShortestPathInfo(u).getEstimate();

	    // Check each incident edge.
	    WeightedEdgeIterator edgeIter = g.weightedEdgeIterator(u);

	    while (edgeIter.hasNext()) {
		Vertex v = (Vertex) edgeIter.next();
		DijkstraInfo vInfo = (DijkstraInfo) getShortestPathInfo(v);
		double weight = edgeIter.getWeight();
		if (vInfo.relax(u, du, weight)) {
		    // v's shortest-path estimate has changed, so
		    // update the min-priority queue.
		    q.decreaseKey(vInfo.handle,
				  new Double(vInfo.getEstimate()));
		}
	    }
	}
    }

    /** Returns a new <code>DijkstraInfo</code> object, overriding
     * {@link SingleSourceShortestPaths#newShortestPathInfo}. */
    // Override newShortestPathInfo.
    protected ShortestPathInfo newShortestPathInfo()
    {
	return new DijkstraInfo();
    }

    /** Inner class to maintain the <code>Vertex</code> object, key,
     * parent, and handle into the priority queue for each vertex.
     * The key (shortest-path estimate) and parent are inherited from
     * the superclass {@link ShortestPathInfo}. */
    private static class DijkstraInfo extends ShortestPathInfo
	implements DynamicSetElement
    {
	/** The vertex. */
	public Vertex theVertex;

	/** A handle to the vertex's information in the priority
	 * queue, or <code>null</code> if the vertex is not in the
	 * priority queue. */
	public Object handle;

	/** Creates a <code>DijkstraInfo</code> object.  The instance
	 * variables <code>theVertex</code> and <code>handle</code>
	 * fields will have to be set by the caller. */
	public DijkstraInfo()
	{
	    super();
	}

	/**
	 * Sets the key.
	 *
	 * @param key The new key value.
	 */
	public void setKey(Comparable key)
	{
	    setEstimate(((Double) key).doubleValue());
	}

	/** Returns the value of the key. */
	public Comparable getKey()
	{
	    return new Double(getEstimate());
	}

	/**
	 * Compares the key of this object's vertex to that of
	 * another.
	 *
	 * @param e The other <code>DijkstraInfo</code> object.
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

// $Id: Dijkstra.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: Dijkstra.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
