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

/** Runs the connected components algorithm on page 500 of
 * <i>Introduction to Algorithms</i>, Second edition. */

public class ConnectedComponents
{
    /** A disjoint-set union structure for components. */
    private DisjointSetUnion sets;

    /** An array of handles mapping vertices to sets.  The vertex
     * whose index is <code>i</code> has a reference to the set of its
     * connected component in <code>handle[i]</code>. */
    private Object[] handle;

    /**
     * Creates disjoint sets for the connected components of a graph.
     * When done, <code>handle[i]</code> references the disjoint set
     * corresponding to the connected component containing the vertex
     * whose index is <code>i</code>.
     *
     * @param g The graph.
     */
    public ConnectedComponents(Graph g)
    {
	// No sets just yet.
	sets = new DisjointSetForest();

	// Make the array of handles.  Each handle will map a vertex
	// to a set.
	handle = new Object[g.getCardV()];	

	// Make a set for each vertex in g.
	Iterator vertexIter = g.vertexIterator();

	while (vertexIter.hasNext()) {
	    Vertex v = (Vertex) vertexIter.next();
	    handle[v.getIndex()] = sets.makeSet(v);
	}

	// Now unite the sets containing the vertices on the ends of
	// each edge.
	vertexIter = g.vertexIterator();

	while (vertexIter.hasNext()) {
	    Vertex u = (Vertex) vertexIter.next();
	    int uIndex = u.getIndex();

	    Iterator edgeIter = g.edgeIterator(u);

	    while (edgeIter.hasNext()) {
		Vertex v = (Vertex) edgeIter.next();
		int vIndex = v.getIndex();

		if (sets.findSet(handle[uIndex]) !=
		    sets.findSet(handle[vIndex]))
		    sets.union(handle[uIndex], handle[v.getIndex()]);
	    }
	}
    }

    /**
     * Returns a boolean value indicating whether two vertices are in
     * the same connected component.
     *
     * @param u One vertex.
     * @param v The other vertex.
     * @return <code>true</code> if <code>u</code> and <code>v</code>
     * are in the same connected component, <code>false</code>
     * otherwise.
     */
    public boolean sameComponent(Vertex u, Vertex v)
    {
	return sets.findSet(handle[u.getIndex()]) ==
	    sets.findSet(handle[v.getIndex()]);
    }
}

// $Id: ConnectedComponents.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: ConnectedComponents.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
