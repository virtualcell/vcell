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

/**
 * Implementation of a weighted graph, using an adjacency matrix.
 *
 * The representation and use are related to the superclass {@link
 * AdjacencyMatrixGraph}.  The primary difference is that here the
 * 2-dimensional matrix <code>a</code> is of <code>double</code>
 * rather than of <code>boolean</code>, where the matrix entry
 * <code>a[u][v]</code> contains the weight of edge (u,v).  There is
 * also an instance variable <code>absentValue</code>; if
 * <code>a[u][v]</code> equals <code>absentValue</code>, then edge
 * (u,v) is not present in the graph.  Also, the inner {@link
 * WeightedAdjacencyListGraph.EdgeIterator} class overrides the {@link
 * AdjacencyListGraph.EdgeIterator} class and implements the {@link
 * WeightedEdgeIterator} interface, so that edge weights can be get
 * and set during iterations through edges.
*/

public class WeightedAdjacencyMatrixGraph extends AdjacencyMatrixGraph
{
    /** Weighted adjacency matrix; <code>a[u][v]</code> is the weight
     * of edge (u,v). */
    protected double[][] a;

    /** The value indicating an absent edge; if <code>a[u][v]</code>
     * equals <code>absentValue</code>, then edge (u,v) is not present
     * in the graph. */
    protected double absentValue;

    /**
     * Creates an empty <code>WeightedAdjacencyMatrixGraph</code>.
     *
     * @param cardV How many vertices this graph will have.
     * @param directed Flag indicating whether this graph is directed.
     * @param absent The value that indicates an absent edge.
     */
    public WeightedAdjacencyMatrixGraph(int cardV, boolean directed,
					double absent)
    {
	super(cardV, directed);
	super.a = null;		// we won't be using the boolean matrix
	absentValue = absent;
	a = new double[cardV][cardV];

	for (int i = 0; i < cardV; i++)
	    for (int j = 0; j < cardV; j++)
		a[i][j] = absent; // no edge (i,j) yet
    }

    /**
     * Unsupported, since edges in a weighted graph must have weights.
     *
     * @throws UnsupportedOperationException always.
     */
    public void addEdge(Vertex u, Vertex v)
    {
	throw new UnsupportedOperationException();
    }

    /**
     * Unsupported, since edges in a weighted graph must have weights.
     *
     * @throws UnsupportedOperationException always.
     */
    public void addEdge(int u, int v)
    {
	throw new UnsupportedOperationException();
    }

    /**
     * Adds a weighted edge to this graph.  The edge is specified by a
     * pair of <code>Vertex</code> objects.
     *
     * @param u One vertex.
     * @param v The other vertex.
     * @param weight The weight of the edge.
     */
    public void addEdge(Vertex u, Vertex v, double weight)
    {
	int uIndex = u.getIndex();
	int vIndex = v.getIndex();

	a[uIndex][vIndex] = weight;
	if (!directed)
	    a[vIndex][uIndex] = weight;
    }

    /**
     * Adds a weighted edge to this graph.  The edge is specified by a
     * pair of vertex indices.
     *
     * @param u The index of one vertex.
     * @param v The index of the other vertex.
     * @param weight The weight of the edge.
     */
    public void addEdge(int u, int v, double weight)
    {
	a[u][v] = weight;
	if (!directed)
	    a[v][u] = weight;
    }

    /**
     * Returns an iterator that iterates through the weighted edges
     * incident on a given vertex.  Each incident edge is indicated by
     * the corresponding adjacent vertex.
     *
     * @param u The vertex whose incident edges are returned by the
     * iterator.
     */
    public Iterator edgeIterator(Vertex u)
    {
	return new EdgeIterator(u.getIndex());
    }

    /**
     * Returns an iterator that iterates through the weighted edges
     * incident on a given vertex.  Each incident edge is indicated by
     * the corresponding adjacent vertex.
     *
     * @param u The index of the vertex whose incident edges are
     * returned by the iterator.
     */
    public Iterator edgeIterator(int u)
    {
	return new EdgeIterator(u);
    }

    /**
     * Returns an iterator, of type <code>WeightedEdgeIterator</code>
     * (so that the caller does not need to cast the result), that
     * iterates through the weighted edges incident on a given vertex.
     * Each incident edge is indicated by the corresponding adjacent
     * vertex.
     *
     * @param u The vertex whose incident edges are returned by the
     * iterator.
     */
    public WeightedEdgeIterator weightedEdgeIterator(Vertex u)
    {
	return weightedEdgeIterator(u.getIndex());
    }

    /**
     * Returns an iterator, of type <code>WeightedEdgeIterator</code>
     * (so that the caller does not need to cast the result), that
     * iterates through the weighted edges incident on a given vertex.
     * Each incident edge is indicated by the corresponding adjacent
     * vertex.
     *
     * @param u The index of the vertex whose incident edges are
     * returned by the iterator.
     */
    public WeightedEdgeIterator weightedEdgeIterator(int u)
    {
	return new EdgeIterator(u);
    }

    /** Inner class that overrides
     * <code>AdjacencyListGraph.EdgeIterator</code> to implement
     * <code>WeightedEdgeIterator</code>. */
    public class EdgeIterator extends AdjacencyMatrixGraph.EdgeIterator
	implements WeightedEdgeIterator
    {
	/**
	 * Starts an iteration through the weighted edges incident on
	 * a given vertex.
	 *
	 * @param v The index of the vertex.
	 */
	public EdgeIterator(int v)
	{
	    super(v);
	}

	/** Returns <code>true</code> if this edge iterator has more
	 * edges, <code>false</code> otherwise. */
	public boolean hasNext()
	{
	    int v = current + 1; // next vertex to visit

	    // Keep going until we find a non-absent entry or run out
	    // of columns.
	    while (v < a[u].length && a[u][v] == absentValue)
		v++;

	    return v < a[u].length;
	}

	/** Returns the next edge in the iteration. */
	public Object next()
	{
	    current++;		// start with next vertex

	    // Keep going until we find a non-absent entry.
	    while (a[u][current] == absentValue )
		current++;

	    return vertices[current];
	}
	
	/** Returns the weight of the edge returned by the most recent
	 * call to <code>next</code>. */
	public double getWeight()
	{
	    return a[u][current];
	}

	/**
	 * Sets the weight of the edge returned by the most recent
	 * call to <code>next</code>.
	 *
	 * @param wgt The new weight for the edge.
	 */
	public void setWeight(double weight)
	{
	    a[u][current] = weight;
	}
    }

    /**
     * Returns a flag indicating whether an edge exists.  The edge is
     * specified as a pair of <code>Vertex</code> objects.
     *
     * @param u One endpoint of the edge.
     * @param v The other endpoint of the edge.
     */
    public boolean edgeExists(Vertex u, Vertex v)
    {
	return edgeExists(u.getIndex(), v.getIndex());
    }

    /**
     * Returns a flag indicating whether an edge exists.  The edge is
     * specified as a pair of vertex indices.
     *
     * @param u One endpoint of the edge.
     * @param v The other endpoint of the edge.
     */
    public boolean edgeExists(int u, int v)
    {
	return a[u][v] != absentValue;
    }

   /**
     * Returns the weight of an edge.  The edge is specified as a pair
     * of <code>Vertex</code> objects.
     *
     * @param u One endpoint of the edge.
     * @param v The other endpoint of the edge.
     */
    public double getWeight(Vertex u, Vertex v)
    {
	return getWeight(u.getIndex(), v.getIndex());
    }

    /**
     * Returns the weight of an edge.  The edge is specified as a pair
     * of vertex indices.
     *
     * @param u One endpoint of the edge.
     * @param v The other endpoint of the edge.
     */
    public double getWeight(int u, int v)
    {
	return a[u][v];
    }

    /** Returns the <code>String</code> representation of this
     * graph. */
    public String toString()
    {
	String result = "";

	Iterator vertexIter = vertexIterator();
	while (vertexIter.hasNext()) {
	    Vertex u = (Vertex) vertexIter.next();
	    result += u + ":\n";

	    WeightedEdgeIterator edgeIter = weightedEdgeIterator(u);
	    while (edgeIter.hasNext()) {
		Vertex v = (Vertex) edgeIter.next();
		double w = edgeIter.getWeight();
		result += "    " + v + ", weight = " + w + "\n";
	    }
	}

	return result;
    }
}

// $Id: WeightedAdjacencyMatrixGraph.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: WeightedAdjacencyMatrixGraph.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
