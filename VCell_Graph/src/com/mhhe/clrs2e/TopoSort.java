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

/** Class that topologically sorts a DAG.  Based on the
 * Topological-Sort procedure on page 550 of <i>Introduction to
 * Algorithms</i>, Second edition. */

public class TopoSort
{
    /** A linked list of vertices. */
    private SentinelDLL list;

    /** Inner class to do DFS but with inserting finished vertices
     * onto the front of the linked list. */
    private class TopoSortDFS extends DFS
    {
	/**
	 * Overrides the {@link DFS#finish} method to insert the
	 * finished vertex onto the front of the linked list.
	 *
	 * @param g The graph.
	 * @param u The vertex just finished.
	 */
	protected void finish(AdjacencyListGraph g, Vertex u)
	{
	    list.insert(u);
	}
    }    

    /**
     * Performs a topological sort on a graph.
     *
     * @param g The graph.
     * @return An array of the vertices of <code>g</code> in which the
     * vertices appear in the array in topologically sorted order.
     */
    public Vertex[] topologicalSort(AdjacencyListGraph g)
    {
	list = new SentinelDLL(); // start with an empty list
	
	// Call DFS, inserting each vertex onto the front of the list
	// as it is finished.
	(new TopoSortDFS()).search(g);

	// Return an array corresponding to the list.
	Vertex[] result = new Vertex[g.getCardV()];
	list.toArray(result);

	return result;
    }
}

// $Id: TopoSort.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: TopoSort.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
