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
 * Implementation of a flow network, using adjacency lists.
 *
 * The representation is based on the superclass {@link
 * AdjacencyListGraph}, but with some significant differences.  The
 * inner class {@link FlowNetwork.FlowNetworkEdge} is a subclass of
 * {@link AdjacencyListGraph.Edge}, with additional instance variables
 * for the edge's capacity, net flow, and residual capacity.  Whenever
 * edge (u,v) is present in the flow network, the reverse edge (v,u)
 * must also be present; the reason is that, in order to maintain skew
 * symmetry, when updating the flow on edge (u,v), we must also update
 * the flow on the reverse edge (v,u).  The reverse edge must be
 * present in order to do so.  Moreover, each
 * <code>FlowNetworkEdge</code> object also includes a reference to
 * its reverse edge.  If edge (u,v) should be in the flow network but
 * the reverse edge (v,u) should not, then set the capacity of (v,u)
 * to 0.  The <code>FlowNetworkEdge</code> class includes methods to
 * access an edge's net flow and capacity.
 *
 * <p>
 *
 * The iterator for flow network edges, {@link
 * FlowNetwork.EdgeIterator}, implements the {@link
 * FlowNetworkEdgeIterator} interface.  When creating this type of
 * iterator, we specify whether the iterator should return only edges
 * in the residual network (i.e., those with positive residual
 * capacity), or all edges.
 *
 * <p>
 *
 * Finally, the <code>FlowNetwork</code> class contains a method,
 * {@link #getFlowValue}, which returns the value of the current flow,
 * given the net flows on the edges.
*/

public class FlowNetwork extends AdjacencyListGraph
{
    /**
     * Creates an empty <code>FlowNetwork</code>.  It is a directed
     * graph.
     *
     * @param cardV How many vertices this graph will have.
     */
    public FlowNetwork(int cardV)
    {
	super(cardV, true);
    }

    /**
     * Unsupported, since edges in a flow network must have
     * capacities.
     *
     * @throws UnsupportedOperationException always.
     */
    public void addEdge(Vertex u, Vertex v)
    {
	throw new UnsupportedOperationException();
    }

    /**
     * Unsupported, since edges in a flow network must have
     * capacities.
     *
     * @throws UnsupportedOperationException always.
     */
    public void addEdge(int u, int v)
    {
	throw new UnsupportedOperationException();
    }
    
    /**
     * Adds an edge (u,v) and its reverse edge (v,u) to this graph.
     * The edge is specified by a pair of <code>Vertex</code> objects.
     *
     * @param u One vertex.
     * @param v The other vertex.
     * @param cuv The capacity of edge (u,v); 0 if (u,v) is not in
     * this flow network.
     * @param cvu The capacity of edge (v,u); 0 if (v,u) is not in
     * this flow network.
     */
    public void addEdge(Vertex u, Vertex v, double cuv, double cvu)
    {
	// Put v on u's list.
	int uIndex = u.getIndex();
	FlowNetworkEdge uv = new FlowNetworkEdge(v, adj[uIndex].head, cuv);
	adj[uIndex].head = uv;

	// Put u on v's list.
	int vIndex = v.getIndex();
	FlowNetworkEdge vu = new FlowNetworkEdge(u, adj[vIndex].head, cvu);
	adj[vIndex].head = vu;

	uv.setReverseEdge(vu);
	vu.setReverseEdge(uv);

	e++;
    }

    /**
     * Adds an edge (u,v) and its reverse edge (v,u) to this graph.
     * The edge is specified by a pair of vertex indices.
     *
     * @param u One vertex.
     * @param v The other vertex.
     * @param cuv The capacity of edge (u,v); 0 if (u,v) is not in
     * this flow network.
     * @param cvu The capacity of edge (v,u); 0 if (v,u) is not in
     * this flow network.
     */
    public void addEdge(int u, int v, double cuv, double cvu)
    {
	// Put v on u's list.
	FlowNetworkEdge uv =
	    new FlowNetworkEdge(adj[v].thisVertex, adj[u].head, cuv);
	adj[u].head = uv;

	// Put u on v's list.
	FlowNetworkEdge vu =
	    new FlowNetworkEdge(adj[u].thisVertex, adj[v].head, cvu);
	adj[v].head = vu;

	uv.setReverseEdge(vu);
	vu.setReverseEdge(uv);

	e++;
    }

    /**
     * Inner class for flow network edges in adjacency lists.
     * Adjacency lists are singly linked.
     */
    protected static class FlowNetworkEdge extends Edge
    {
	/** The capacity c(u,v) of this edge (u,v). */
	final private double capacity;

	/** The net flow f(u,v) for edge (u,v). */
	private double netFlow;

	/** The residual capacity cHat(u,v) = c(u,v) - f(u,v) for edge
	 * (u,v). */
	private double residualCapacity;

	/** A reference to the reverse edge (v,u) for edge (u,v). */
	private FlowNetworkEdge reverseEdge;

	/**
	 * Creates a new edge.  Leaves the <code>reverseEdge</code>
	 * instance variable alone.
	 *
	 * @param v The adjacent vertex.
	 * @param successor Successor edge to this one.
	 * @param cap The capacity of the new edge.
	 */
	public FlowNetworkEdge(Vertex v, Edge successor, double cap)
	{
	    super(v, successor);
	    capacity = cap;
	    netFlow = 0;
	    residualCapacity = capacity;
	}

	/** Returns the capacity of this edge. */
	public double getCapacity()
	{
	    return capacity;
	}

	/** Returns the net flow for this edge. */
	public double getNetFlow()
	{
	    return netFlow;
	}

	/** Returns the residual capacity of this edge. */
	public double getResidualCapacity()
	{
	    return residualCapacity;
	}

	/**
	 * Sets the reference to the reverse edge.
	 *
	 * @param e The reverse edge.
	 */
	public void setReverseEdge(FlowNetworkEdge e)
	{
	    reverseEdge = e;
	}

	/** Zeros out the net flow on this edge and its reverse and
	 * updates residual capacities on both edges. */
	public void zeroNetFlow()
	{
	    netFlow = 0;
	    reverseEdge.netFlow = 0;
	    residualCapacity = capacity;
	    reverseEdge.residualCapacity = reverseEdge.capacity;
	}

	/**
	 * Increases the net flow on this edge by the given amount,
	 * decreases the net flow on the reverse edge by the same
	 * amount, and updates the residual capacities of this edge
	 * and the reverse edge.  This method does not check that the
	 * residual capacity remains nonnegative.
	 *
	 * @param amount The amount by which this edge's flow is
	 * increased and the reverse edge's flow is decreased.
	 */
	public void increaseNetFlow(double amount)
	{
	    netFlow += amount;
	    reverseEdge.netFlow -= amount;
	    residualCapacity -= amount;
	    reverseEdge.residualCapacity += amount;
	}
    }

    /**
     * Returns an iterator that iterates through all the edges
     * (regardless of residual capacity) incident on a given vertex in
     * a flow network.  Each incident edge is indicated by the
     * corresponding adjacent vertex.
     *
     * @param u The vertex whose incident edges are returned by the
     * iterator.
     */
    public Iterator edgeIterator(Vertex u)
    {
	return new EdgeIterator(u.getIndex(), false);
    }

    /**
     * Returns an iterator that iterates through all the edges
     * (regardless of residual capacity) incident on a given vertex in
     * a flow network.  Each incident edge is indicated by the
     * corresponding adjacent vertex.
     *
     * @param u The index of the vertex whose incident edges are
     * returned by the iterator.
     */
    public Iterator edgeIterator(int u)
    {
	return new EdgeIterator(u, false);
    }

    /**
     * Returns an iterator, of type
     * <code>FlowNetworkEdgeIterator</code> (so that the caller does
     * not need to cast the result), that iterates through the edges
     * incident on a given vertex in a flow network.  Each incident
     * edge is indicated by the corresponding adjacent vertex.
     *
     * @param u The vertex whose incident edges are returned by the
     * iterator.
     * @param residual <code>true</code> if this iterator is to return
     * only edges in the residual network, <code>false</code> if it is
     * to return all edges.
     */
    public FlowNetworkEdgeIterator
	flowNetworkEdgeIterator(Vertex u, boolean residual)
    {
	return flowNetworkEdgeIterator(u.getIndex(), residual);
    }

    /**
     * Returns an iterator, of type
     * <code>FlowNetworkEdgeIterator</code> (so that the caller does
     * not need to cast the result), that iterates through the edges
     * incident on a given vertex in a flow network.  Each incident
     * edge is indicated by the corresponding adjacent vertex.
     *
     * @param u The index of the vertex whose incident edges are
     * returned by the iterator.
     * @param residual <code>true</code> if this iterator is to return
     * only edges in the residual network, <code>false</code> if it is
     * to return all edges.
     */
    public FlowNetworkEdgeIterator
	flowNetworkEdgeIterator(int u, boolean residual)
    {
	return new EdgeIterator(u, residual);
    }

    /** Inner class that overrides
     * <code>AdjacencyListGraph.EdgeIterator</code> to implement
     * <code>FlowNetworkEdgeIterator</code>. */
    public class EdgeIterator extends AdjacencyListGraph.EdgeIterator
	implements FlowNetworkEdgeIterator
    {
	/** <code>true</code> if this iterator is to return only edges
	 * in the residual network, <code>false</code> if it is to
	 * return all edges (even those whose residual capacity is not
	 * positive. */
	private boolean residualOnly;

	/**
	 * Starts an iteration through the edges incident on a given
	 * vertex in a flow network.
	 *
	 * @param v The index of the vertex.
	 * @param residual <code>true</code> if this iterator is to
	 * return only edges in the residual network,
	 * <code>false</code> if it is to return all edges.
	 */
	public EdgeIterator(int v, boolean residual)
	{
	    super(v);
	    residualOnly = residual;
	}

	/** Returns <code>true</code> if this edge iterator has more
	 * edges, <code>false</code> otherwise.  Depending on the
	 * instance variable <code>residualOnly</code>, this method
	 * may consider only edges that are present in the residual
	 * network. */
	public boolean hasNext()
	{
	    if (residualOnly) {
		FlowNetworkEdge e;
		if (current == null)
		    e = (FlowNetworkEdge) adj[index].head;
		else
		    e = (FlowNetworkEdge) current.next;

		while (e != null && e.getResidualCapacity() <= 0)
		    e = (FlowNetworkEdge) e.next;

		return e != null;
	    }
	    else
		return super.hasNext();
	}

	/** Returns the next edge in the iteration.  Depending on the
	 * instance variable <code>residualOnly</code>, this method
	 * may consider only edges that are present in the residual
	 * network. */
	public Object next()
	{
	    if (residualOnly) {
		if (current == null)
		    current = adj[index].head;
		else
		    current = current.next;

		while (((FlowNetworkEdge) current).getResidualCapacity() <= 0)
		    current = current.next;

		return current.vertex;
	    }
	    else
		return super.next();
	}

	/** Returns the edge found by the most recent call to
	 * <code>next</code>.  Unlike <code>next</code>, this method
	 * returns an object corresponding to the edge itself rather
	 * than just the adjacent vertex. */
	public Object getEdge()
	{
	    return current;
	}

	/** Returns the capacity of the edge returned by the most
	 * recent call to <code>next</code>. */
	public double getCapacity()
	{
	    return ((FlowNetworkEdge) current).getCapacity();
	}

	/** Returns the net flow of the edge returned by the most
	 * recent call to <code>next</code>. */
	public double getNetFlow()
	{
	    return ((FlowNetworkEdge) current).getNetFlow();
	}
	
	/** Returns the residual capacity of the edge returned by the
	 * most recent call to <code>next</code>. */
	public double getResidualCapacity()
	{
	    return ((FlowNetworkEdge) current).getResidualCapacity();
	}

	/** Zeros out the net flow of the edge returned by the most
	 * recent call to <code>next</code>. */
	public void zeroNetFlow()
	{
	    ((FlowNetworkEdge) current).zeroNetFlow();
	}

	/**
	 * Increases the net flow of the edge returned by the most
	 * recent call to <code>next</code>.
	 *
	 * @param amount The amount by which this edge's flow is
	 * increased.
	 */
	public void increaseNetFlow(double amount)
	{
	    ((FlowNetworkEdge) current).increaseNetFlow(amount);
	}
    }


    /** Returns the value of the flow, which is the sum of the net
     * flows out of the source.
     *
     * @param s The source vertex.
     */
    public double getFlowValue(Vertex s)
    {
	double flowValue = 0;

	FlowNetworkEdgeIterator iter = flowNetworkEdgeIterator(s, false);
	while (iter.hasNext()) {
	    iter.next();
	    flowValue += iter.getNetFlow();
	}

	return flowValue;
    }

    /** Returns the <code>String</code> representation of this
     * flow network. */
     public String toString()
    {
	String result = "";

	Iterator vertexIter = vertexIterator();
	while (vertexIter.hasNext()) {
	    Vertex u = (Vertex) vertexIter.next();
	    result += u + ":\n";

	    FlowNetworkEdgeIterator edgeIter =
		flowNetworkEdgeIterator(u, false);
	    while (edgeIter.hasNext()) {
		Vertex v = (Vertex) edgeIter.next();
		double cap = edgeIter.getCapacity();
		double flow = edgeIter.getNetFlow();
		result += "    " + v + ", " + flow + "/" + cap +
		    " (residual capacity = " + edgeIter.getResidualCapacity() +
		    ") \n";
	    }
	}

	return result;
    }
}

// $Id: FlowNetwork.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: FlowNetwork.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
