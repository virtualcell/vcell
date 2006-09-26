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

/** Implementation of a Fibonacci heap from Chapter 20 of
 * <i>Introduction to Algorithms</i>, Second edition. */

public class FibHeap extends MergeableHeap
{
    /** The node in root list with the minimum key. */
    private Node min;

    /** How many nodes are in this Fibonacci heap. */
    int n;
    
    /** Creates an empty Fibonacci heap. */
    public FibHeap()
    {
	min = null;		// make an empty root list
	n = 0;			// no nodes yet
    }
    
    /** Inner class for a node within a Fibonaaci heap. */
    private static class Node
    {
	/** The object stored in this node. */
	public DynamicSetElement object;

	/** This node's parent, or <code>null</code> if this node is a
	 * root. */
	public Node p;

	/** Some child of this node. */
	public Node child;

	/** This node's right sibling. */
	public Node right;

	/** This node's left sibling. */
	public Node left;

	/** How many children this node has. */
	public int degree;

	/** <code>true</code> if this node is marked,
	 * <code>false</code> if not marked. */
	public boolean mark;

	/**
	 * Creates a new node.
	 *
	 * @param e The dynamic set element to store in the node.
	 */
	public Node(DynamicSetElement e)
	{
	    object = e;
	    p = null;
	    child = null;
	    right = this;
	    left = this;
	    degree = 0;
	    mark = false;
	}

	/** Returns the <code>String</code> representation of this
	 * node's object, along with its degree and mark. */
	public String toString()
	{
	    return "key = " + object.toString() +
		", degree = " + degree + ", mark = " + mark;
	}

	/**
	 * Returns the <code>String</code> representation of the
	 * subtree rooted at this node, based on the objects in the
	 * nodes.  It represents the depth of each node by two spaces
	 * per depth preceding the <code>String</code> representation
	 * of the node.
	 *
	 * @param depth Depth of this node.
	 */
	public String walk(int depth)
	{
	    String result = "";

	    for (int i = 0; i < depth; i++)
		result += "  ";

	    result += toString() + "\n";

	    if (child != null) {
		result += child.walk(depth+1);

		for (Node x = child.right; x != child; x = x.right)
		    result += x.walk(depth+1);
	    }

	    return result;
	}
    }

    /**
     * Returns the <code>String</code> representation of this
     * Fibonacci heap, based on the objects in the nodes.  It
     * represents the depth of each node by two spaces per depth
     * preceding the <code>String</code> representation of the node.
     */
    public String toString()
    {
	String result = "n = " + n + "\n";

	if (min != null) {
	    result += min.walk(0);

	    for (Node x = min.right; x != min; x = x.right)
		result += x.walk(0);
	}

	return result;
    }

    /**
     * Inserts a dynamic set element into the Fibonacci heap.
     * Implements the Fib-Heap-Insert procedure on page 480.
     *
     * @param e The dynamic set element to be inserted.
     * @return A handle to the inserted item.
     */
    public Object insert(DynamicSetElement e)
    {
	Node x = new Node(e);
	
	// Splice this new node into the root list.  The return value
	// will be the value of min before the call, if min was
	// already non-null, or x, if min was null.
	min = spliceIn(min, x);

	// Update min if necessary.
	if (x.object.compareTo(min.object) < 0)
	    min = x;

	n++;

	return x;
    }

    /**
     * Splices the node list containing one node into the node list
     * containing another node, just to the left of it.
     *
     * @param x The node whose list is to be spliced into the other.
     * @param y The node whose list is to be spliced into.
     * @return If <code>x</code> is <code>null</code>, then
     * <code>x</code>'s list is empty, and we just return
     * <code>y</code>.  If <code>y</code> is <code>null</code>, then
     * <code>y</code>'s list is empty and we just return
     * <code>x</code>.  If neither <code>x</code> nor <code>y</code>
     * is <code>null</code>, we return <code>x</code> as a pointer
     * into the list.
     */
    private Node spliceIn(Node x, Node y)
    {
	if (x == null)
	    return y;
	else if (y == null)
	    return x;
	else {
	    Node xPred = x.left;
	    Node yTail = y.left;

	    x.left = yTail;
	    yTail.right = x;
	    y.left = xPred;
	    xPred.right = y;
	    return x;
	}
    }	

    /** Returns the object whose key is minimum. */
    public DynamicSetElement minimum()
    {
	if (min == null)
	    return null;
	else
	    return min.object;
    }

    /** Removes and returns the smallest object in the Fibonacci heap.
     * Implements the Fib-Heap-Extract-Min procedure on
     * page 483. */
    public DynamicSetElement extractMin()
    {
	Node z = min;

	if (z == null)
	    return null;	// empty Fibonacci heap
	else {
	    // Add each child of z to the root list.  We can just
	    // splice in z's children, but first we have to set their
	    // parent pointers to null.
	    if (z.child != null) {
		z.child.p = null;

		for (Node x = z.child.right; x != z.child; x = x.right)
		    x.p = null;

		min = spliceIn(min, z.child);
	    }

	    // Remove z from the root list.
	    z.left.right = z.right;
	    z.right.left = z.left;

	    // Update min appropriately.
	    if (z == z.right)
		min = null;
	    else {
		min = z.right;
		consolidate();
	    }

	    n--;

	    // Clear out z's pointers.
	    z.p = null;
	    z.left = null;
	    z.right = null;

	    return z.object;
	}
    }

    /** Consolidates the root list of this Fibonacci heap.  Implements
     * the Consolidate procedure on page 486. */
    private void consolidate()
    {
	Node[] a = new Node[computeD() + 1];
	for (int i = 0; i < a.length; i++)
	    a[i] = null;

	// We can use a do-while loop because we know that consolidate
	// is called only when the root list is not empty.
	Node w = min;
	Node start = min;	// stop when w gets back to start
	do {
	    Node x = w;
	    Node nextW = w.right; // because we might move w, save its
				  // right sibling
	    int d = x.degree;

	    while (a[d] != null) {
		Node y = a[d];
		if (x.object.compareTo(y.object) > 0) {
		    // Exchange x and y.
		    Node temp = x;
		    x = y;
		    y = temp;
		}

		// If we're removing the starting point of the root
		// list, make the starting point be its right sibling.
		if (y == start)
		    start = start.right;

		fibHeapLink(y, x);
		a[d] = null;
		d++;
	    }

	    a[d] = x;
	    w = nextW;
	}
	while (w != start);

	min = null;

	for (int i = 0; i < a.length; i++) {
	    if (a[i] != null) {
		// Add a[i] to the root list.
		a[i].right = a[i];
		a[i].left = a[i];
		min = spliceIn(min, a[i]);
		if (a[i].object.compareTo(min.object) < 0)
		    min = a[i];
	    }
	}
    }

    /**
     * Links one root to another.  Implements the Fib-Heap-Link
     * procedure on page 486.
     *
     * @param y The root to be linked to <code>x</code>.
     * @param x The root that <code>y</code> is linked to.
     */
    private void fibHeapLink(Node y, Node x)
    {
	// Remove y from its root list.
	y.left.right = y.right;
	y.right.left = y.left;

	// Make y a child of x, incrementing degree x.
	y.left = y;
	y.right = y;
	x.child = spliceIn(x.child, y);
	x.degree++;

	y.mark = false;
    }

    /** Returns D(n) = floor(log base phi of n), where phi = (1 +
     * sqrt(5)) / 2.  Assumes that n > 0. */
    private int computeD()
    {
	final double phi = (1.0 + Math.sqrt(5.0)) / 2.0;

	// log base phi of n = (ln n) / (ln phi).
	return (int) Math.floor(Math.log(n) / Math.log(phi));
    }

    /**
     * Creates a new Fibonacci heap that contains all the elements of
     * two Fibonacci heaps.  One of the original Fibonacci heaps is
     * the object on which this method is called; the other is
     * specified by the parameter.  The two original Fibonacci heaps
     * should no longer be used after this operation.
     *
     * <p>
     * 
     * Implements the Fib-Heap-Union procedure on page 482,
     * with h1 being this object.
     *
     * @param h2 The Fibonacci heap to be merged with this one.
     * @return The new Fibonacci heap that contains all the elements
     * of this Fibonacci heap and <code>h2</code>.
     */
    public MergeableHeap union(MergeableHeap h2)
    {
	FibHeap h = new FibHeap();
	FibHeap h2Heap = (FibHeap) h2;

	// Concatenate the root list of h2 with the root list of h1.
	h.min = spliceIn(this.min, h2Heap.min);

	if (this.min != null && h2Heap.min != null &&
	    h2Heap.min.object.compareTo(this.min.object) < 0)
	    h.min = h2Heap.min;

	h.n = this.n + h2Heap.n;

	// "Free" h1 and h2.
	this.min = null;
	this.n = 0;
	h2Heap.min = null;
	h2Heap.n = 0;

	return h;
    }

    /**
     * Decreases the key of a node.  Implements the
     * Fib-Heap-Decrease-Key procedure on page 489.
     *
     * @param node The node whose key is to be decreased.
     * @param k The new key.
     * @throws KeyUpdateException if the new key is greater than the
     * current key.
     */
    public void decreaseKey(Object node, Comparable k)
	throws KeyUpdateException
    {
	Node x = (Node) node;

	// Make sure that the key value does not increase.
	if (k.compareTo(x.object.getKey()) > 0)
	    throw new KeyUpdateException();

	// Finish the procedure.
	x.object.setKey(k);	// update x's key
	updateForDecreaseKey(x, false);
    }

    /**
     * Changes the structure of the Fibonacci heap as part of a
     * <code>decreaseKey</code> operation.
     *
     * @param x The node whose key is being decreased.
     * @param delete If <code>true</code>, ignore <code>x</code>'s key
     * and treat it as though it were negative infinity, because we're
     * decreasing the key as part of a <code>delete</code> operation.
     */
    private void updateForDecreaseKey(Node x, boolean delete)
    {
	Node y = x.p;

	if (y != null && (delete || x.object.compareTo(y.object) < 0)) {
	    cut(x, y);
	    cascadingCut(y);
	}

	if (delete || x.object.compareTo(min.object) < 0)
	    min = x;
    }

    /**
     * Cuts the link between a node and its parent.  Implements the
     * Cut procedure on page 489.
     *
     * @param x The node.
     * @param y <code>x</code>'s parent.
     */
    private void cut(Node x, Node y)
    {
	// Remove x from y's child list, decrementing y's degree.
	Node xRight = x.right;
	x.left.right = x.right;
	x.right.left = x.left;
	y.degree--;
	if (y.degree == 0)
	    y.child = null;
	else if (y.child == x)
	    y.child = xRight;

	// Add x to the root list.
	x.right = x;
	x.left = x;
	min = spliceIn(min, x);

	x.p = null;
	x.mark = false;
    }

    /**
     * Performs a cascading cut operation.  Implements the
     * Cascading-Cut procedure on page 490.
     *
     * @param y The node being cut.
     */
    private void cascadingCut(Node y)
    {
	Node z = y.p;

	if (z != null) {
	    if (!y.mark)
		y.mark = true;
	    else {
		cut(y, z);
		cascadingCut(z);
	    }
	}
    }

    /**
     * Deletes a node.  Implements the Fib-Heap-Delete procedure on
     * page 492.
     *
     * @param node The node to be deleted.
     */
    public void delete(Object node)
    {
	Node x = (Node) node;
	updateForDecreaseKey(x, true);
	extractMin();
    }

    /**
     * Returns the <code>String</code> representation of a node's
     * object.
     *
     * @param handle The node.
     * @throws ClassCastException if <code>handle</code> is not a
     * reference to a <code>node</code> object.
     */
    public String dereference(Object handle)
    {
	return ((Node) handle).object.toString();
    }

    /**
     * Adds a child with a dynamic set element to a node, inserting
     * the child into the Fibonacci heap.  We use this method just so
     * we can create examples exactly like those in the text.
     *
     * @param node The node to which the child is added.
     * @param e The dynamic set element to appear in the new node.
     * @param marked <code>true</code> if the new node is to be
     * marked, <code>false</code> otherwise.
     * @return A handle to the inserted item.
     */
    public Object addChild(Object node, DynamicSetElement e, boolean marked)
    {
	Node y = new Node(e);
	y.mark = marked;
	Node x = (Node) node;
	x.child = spliceIn(x.child, y);
	x.degree++;
	y.p = x;
	n++;
	return y;
    }

    /**
     * Marks a node.  We use this method just so we can create
     * examples exactly like those in the text.
     *
     * @param node The node to be marked.
     */
    public void mark(Object node)
    {
	((Node) node).mark = true;
    }
}

// $Id: FibHeap.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: FibHeap.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
