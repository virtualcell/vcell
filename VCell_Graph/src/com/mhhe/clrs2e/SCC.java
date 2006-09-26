package com.mhhe.clrs2e;
import java.util.Iterator;
import java.awt.Color;

/** Class to perform the Strongly-Connected-Components procedure on
 * page 554 of <i>Introduction to Algorithms</i>, Second
 * edition. */

public class SCC
{
    /** A list of vertices. */
    private SentinelDLL list;

    /** The SCC number of each vertex.  <code>sccNumber[i]</code> is
     * the SCC number of the vertex whose index is <code>i</code>.
     * Valid SCC numbers start at 0. */
    private int[] sccNumber;

    /** Inner class to do the first DFS.  It inserts finished vertices
     * onto the front of the linked list, so that the most recently
     * finished vertices are toward the head of the list. */
    private class FirstDFS extends DFS
    {
	/**
	 * Overrides the {@link DFS#finish} method to insert the
	 * finished vertex onto the front of the linked list.
	 *
	 *
	 * @param g The directed graph.
	 * @param u The vertex just finished.
	 */
	protected void finish(AdjacencyListGraph g, Vertex u)
	{
	    list.insert(u);
	}
    }

    /** Inner class to do the second DFS.  It considers the vertices
     * in order of decreasing finish times, which means that they are
     * visited in the order in which they appear in the linked list.
     * It also numbers them so that the vertices in each tree of the
     * resulting depth-first forest get the same SCC number. */
    private class SecondDFS extends DFS
    {
	/** The number of the current SCC. */
	private int currentSCC;

	/** Value indicating that a vertex is not yet in an SCC. */
	private static final int NO_SCC = -1;

	/** Initializes currentSCC and all SCC numbers to
	 * <code>NO_SCC</code>. */
	public SecondDFS()
	{
	    currentSCC = NO_SCC;
	    for (int i = 0; i < sccNumber.length; i++)
		sccNumber[i] = NO_SCC;
	}

	/**
	 * Performs the main loop of the second DFS.  This DFS
	 * considers the vertices in order of decreasing finish times,
	 * based on their positions in the linked list, as created by
	 * the first DFS.  This method calls {@link DFS#dfsVisit},
	 * which in turn calls {@link #finish}, which sets the SCC
	 * numbers in the <code>sccNumber</code> array.
	 *
	 * @param g The directed graph.
	 */
	public void search(AdjacencyListGraph g)
	{
	    // Set each vertex to white with no parent in the
	    // predecessor subgraph.
	    dfsInfo = new DFSInfo[g.getCardV()];
	    for (int i = 0; i < dfsInfo.length; i++)
		dfsInfo[i] = new DFSInfo();

	    time = 0;		// global time is 0

	    // Call dfsVisit on each unvisited vertex, but consider
	    // them in the order in which they appear in the linked
	    // list.
	    Iterator iter = list.iterator();

	    while (iter.hasNext()) {
		Vertex u = (Vertex) iter.next();
		if (getDFSInfo(u).getColor() == Color.white) {
		    currentSCC++;	// we have a new SCC
		    dfsVisit(g, u);
		}
	    }
	}

	/**
	 * Overrides the {@link DFS#finish} method to assign the
	 * current SCC number to the vertex.
	 *
	 * @param g The graph.
	 * @param u The vertex just finished.
	 */
	// Finishing a vertex also entails setting its SCC number.
	protected void finish(AdjacencyListGraph g, Vertex u)
	{
	    sccNumber[u.getIndex()] = currentSCC;
	}
    }

    /**
     * Returns the SCC number for a given vertex.
     *
     * @param v The index of the vertex for which the corresponding
     * SCC number is returned.
     */
    public int getSCCNumber(int v)
    {
	return sccNumber[v];
    }


    /**
     * Returns the SCC number for a given vertex.
     *
     * @param v The vertex for which the corresponding SCC number is
     * returned.
     */
    public int getSCCNumber(Vertex v)
    {
	return getSCCNumber(v.getIndex());
    }


    /**
     * Computes the strongly connected components of a directed graph,
     * filling in SCC numbers in the <code>sccNumber</code> array so
     * that <code>sccNumber[i]</code> is the SCC number of the vertex
     * whose index is <code>i</code>.
     *
     * @param g The directed graph.
     */
    public void stronglyConnectedComponents(AdjacencyListGraph g)
    {
	// Allocate the array of SCC numbers.
	sccNumber = new int[g.getCardV()];

	// Initialize the linked list to be empty.
	list = new SentinelDLL();

	// Do the first DFS, which puts vertices onto a linked list in
	// order of decreasing finishing times.
	DFS firstDFS = new FirstDFS();
	firstDFS.search(g);

	// Compute the transpose of g.
	AdjacencyListGraph gt = transpose(g);

	// Do the second DFS, but on gt.  It assigns SCC numbers to
	// the vertices.
	(new SecondDFS()).search(gt);
    }


    /**
     * Transposes a graph.  The transpose has the same
     * <code>Vertex</code> objects as the input graph but different
     * edges.
     *
     * @param g The directed graph to transpose.
     * @return The transpose of g.
     */
    private AdjacencyListGraph transpose(AdjacencyListGraph g)
    {
	// Create a graph gt with the same Vertex objects as g but no
	// edges yet.
	AdjacencyListGraph gt = g.useSameVertices();

	// Create a reverse edge in gt for each edge in g.
	Iterator vertexIter = g.vertexIterator();
	while (vertexIter.hasNext()) {
	    Vertex u = (Vertex) vertexIter.next();

	    Iterator edgeIter = g.edgeIterator(u);
	    while (edgeIter.hasNext()) {
		Vertex v = (Vertex) edgeIter.next();
		gt.addEdge(v, u); // add (v,u), the reverse of (u,v)
	    }
	}

	return gt;
    }
}