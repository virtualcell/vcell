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

/** Implements Kruskal's algorithm for minimum spanning tree from
 * page 569 of <i>Introduction to Algorithms</i>, Second
 * edition. */

public class Kruskal implements MST
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
	// Our MST, a, starts with the same vertices as g.
	WeightedAdjacencyListGraph a =
	    (WeightedAdjacencyListGraph) g.useSameVertices();

	// Keep an array of handles to disjoint-set objects.  The
	// handle for vertex i is in handle[i].
	DisjointSetUnion forest = new DisjointSetForest();
	Object handle[] = new Object[a.getCardV()];
	Iterator vertexIter = g.vertexIterator();

	while (vertexIter.hasNext()) {
	    Vertex v = (Vertex) vertexIter.next();
	    handle[v.getIndex()] = forest.makeSet(v);
	}

	// Make an array of weighted edges and sort it by weight.
	WeightedEdge[] edge = new WeightedEdge[g.getCardE()];
	int i = 0;		// index into edge array
	vertexIter = g.vertexIterator();
	while (vertexIter.hasNext()) {
	    Vertex u = (Vertex) vertexIter.next();

	    WeightedEdgeIterator edgeIter = g.weightedEdgeIterator(u);
	    while (edgeIter.hasNext()) {
		Vertex v = (Vertex) edgeIter.next();

		// We want to add this edge only once, so we'll add it
		// only if u's index is less than v's index.
		if (u.getIndex() < v.getIndex()) {
		    double w = edgeIter.getWeight();
		    edge[i++] = new WeightedEdge(u, v, w);
		}
	    }
	}

	MaxHeap heap = new MaxHeap();
	(heap.makeSorter()).sort(edge);

	// Examine each edge, in nondecreasing order by weight.
	for (i = 0; i < edge.length; i++) {
	    Object uHandle = handle[edge[i].u.getIndex()];
	    Object vHandle = handle[edge[i].v.getIndex()];
	    if (forest.findSet(uHandle) != forest.findSet(vHandle)) {
		a.addEdge(edge[i].u, edge[i].v, edge[i].w);
		forest.union(uHandle, vHandle);
	    }
	}

	return a;		// return the MST
    }

    /** Inner class for weighted edges so that the edges can be sorted
     * and considered in nondecreasing order by weight. */
    private static class WeightedEdge implements Comparable
    {
	/** A vertex on which this edge is incident. */
	public Vertex u;

	/** Another vertex on which this edge is incident. */
	public Vertex v;

	/** The weight of the edge. */
	public double w;

	/**
	 * Stores the vertices and edge weight into the instance
	 * variables.
	 *
	 * @param a One vertex on which this edge is incident.
	 * @param b Another vertex on which this edge is incident.
	 * @param weight The weight of the edge.
	 */
	public WeightedEdge(Vertex a, Vertex b, double weight)
	{
	    u = a;
	    v = b;
	    w = weight;
	}

	/**
	 * Compares the weight of this <code>WeightedEdge</code> to
	 * that of another.
	 *
	 * @param e The other <code>WeightedEdge</code> object.
	 * @return A negative integer if this object's weight vertex
	 * is less; 0 if the weights are equal; a positive integer if
	 * this object's weight is greater.
	 */
	public int compareTo(Object o)
	{
	    WeightedEdge e = (WeightedEdge) o;
	    if (w < e.w)
		return -1;
	    else if (w == e.w)
		return 0;
	    else
		return 1;
	}

	/** Returns the <code>String</code> representation of this
	 * object. */
	public String toString()
	{
	    return "(" + u.getName() + "," + v.getName() + "," + w + ")";
	}
    }

}

// $Id: Kruskal.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: Kruskal.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
