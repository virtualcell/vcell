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

/** Implementation of a binomial heap from Chapter 19 of
 * <i>Introduction to Algorithms</i>, Second edition. */

public class BinomialHeap extends MergeableHeap
{
    /** The head of the singly linked root list. */
    private Node head;
    
    /** Creates an empty binomial heap. */
    public BinomialHeap()
    {
	head = null;		// make an empty root list
    }
    
    /** Inner class for a node within a binomial heap. */
    private static class Node
    {
	/** The object stored in this node. */
	public DynamicSetElement object;

	/** This node's parent, or <code>null</code> if this node is a
	 * root. */
	public Node p;

	/** This node's leftmost child, or <code>null</code> if this
	 * node has no children. */
	public Node child;

	/** This node's right sibling, or <code>null</code> if this
	 * node has no right sibling. */
	public Node sibling;

	/** The number of children that this node has. */
	public int degree;

	/** A handle to this node. */
	public Handle handle;

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
	    sibling = null;
	    degree = 0;
	    handle = new Handle(this);
	}

	/** Returns the <code>String</code> representation of this
	 * node's object. */
	public String toString()
	{
	    return "key = " + object.toString() + ", degree = " + degree;
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

	    Node x = child;
	    while (x != null) {
		result += x.walk(depth+1);
		x = x.sibling;
	    }

	    return result;
	}
    }

    /** Inner class for the handle given back to the caller upon an
     * insertion.  We cannot just use a pointer to a <code>Node</code>
     * because the {@link #decreaseKey} and {@link #delete} operations
     * move information around, and <code>Node</code> pointers may go
     * stale.  A <code>Handle</code> and a <code>Node</code> reference
     * each other. */
    private static class Handle
    {
	/** The <code>Node</code> referenced by this
	 * <code>Handle</code>. */
	public Node node;

	/** Saves the node in this <code>Handle</code>. */
	public Handle(Node n)
	{
	    node = n;
	}
    }	

    /**
     * Returns the <code>String</code> representation of this binomial
     * heap, based on the objects in the nodes.  It represents the
     * depth of each node by two spaces per depth preceding the
     * <code>String</code> representation of the node.
     */
    public String toString()
    {
	String result = "";

	Node x = head;
	while (x != null) {
	    result += x.walk(0);
	    x = x.sibling;
	}

	return result;
    }

    /**
     * Inserts a dynamic set element into the binomial heap.
     *
     * @param e The dynamic set element to be inserted.
     * @return A handle to the inserted item.
     */
    public Object insert(DynamicSetElement e)
    {
	Node x = new Node(e);
	BinomialHeap hPrime = new BinomialHeap();
	hPrime.head = x;
	BinomialHeap newHeap = (BinomialHeap) this.union(hPrime);
	head = newHeap.head;
	return x.handle;
    }

    /** Returns the object whose key is minimum.  Implements the
     * Binomial-Heap-Minimum procedure on page 462. */
    public DynamicSetElement minimum()
    {
	// Since we do not have a value for infinity that is inherent
	// in the Comparable interface, we simply start off with the
	// minimum element being the one pointed to by head.  We have
	// to first check that head is not null.
	if (head == null)
	    return null;	// empty heap, hence no minimum
	else {
	    Node min = head;	// min takes the role of both min and y
	    Node x = min.sibling;

	    while (x != null) {
		if (x.object.compareTo(min.object) < 0)
		    min = x;
		x = x.sibling;
	    }

	    return min.object;
	}
    }

    /** Removes and returns the smallest object in the binomial
     * heap. */
    public DynamicSetElement extractMin()
    {
	// Special case for an empty binomial heap.
	if (head == null)
	    return null;

	// Find the root x with the minimum key in the root list.
	Node x = head;		// node with minimum key
	Node y = x.sibling;	// current node being examined
	Node pred = x;		// y's predecessor
	Node xPred = null;	// predecessor of x

	while (y != null) {
	    if (y.object.compareTo(x.object) < 0) {
		x = y;
		xPred = pred;
	    }
	    pred = y;
	    y = y.sibling;
	}

	removeFromRootList(x, xPred);
	x.handle = null;
	return x.object;
    }

    /**
     * Helper method to remove a node from the root list.
     *
     * @param x The node to remove from the root list.
     * @param pred The predecessor of <code>x</code> in the root list,
     * or <code>null</code> if <code>x</code> is the first node in the
     * root list.
     */
    private void removeFromRootList(Node x, Node pred)
    {
	// Splice out x.
	if (x == head)
	    head = x.sibling;
	else
	    pred.sibling = x.sibling;

	BinomialHeap hPrime = new BinomialHeap();

	// Reverse the order of x's children, setting hPrime.head to
	// point to the head of the resulting list.
	Node z = x.child;
	while (z != null) {
	    Node next = z.sibling;
	    z.sibling = hPrime.head;
	    hPrime.head = z;
	    z = next;
	}

	BinomialHeap newHeap = (BinomialHeap) this.union(hPrime);
	head = newHeap.head;
    }

    /**
     * Creates a new binomial heap that contains all the elements of
     * two binomial heaps.  One of the original binomial heaps is
     * the object on which this method is called; the other is
     * specified by the parameter.  The two original binomial heaps
     * should no longer be used after this operation.
     *
     * <p>
     * 
     * Implements the Binomial-Heap-Union procedure on page 463,
     * with h1 being this object.
     *
     * @param h2 The binomial heap to be merged with this one.
     * @return The new binomial heap that contains all the elements
     * of this binomial heap and <code>h2</code>.
     */
    public MergeableHeap union(MergeableHeap h2)
    {
	BinomialHeap h = new BinomialHeap();
	h.head = binomialHeapMerge(this, (BinomialHeap) h2);
	head = null;		         // no longer using the...
	((BinomialHeap) h2).head = null; // ...two input lists

	if (h.head == null)
	    return h;

	Node prevX = null;
	Node x = h.head;
	Node nextX = x.sibling;

	while (nextX != null) {
	    if (x.degree != nextX.degree ||
		(nextX.sibling != null && nextX.sibling.degree == x.degree)) {
		// Cases 1 and 2.
		prevX = x;
		x = nextX;
	    }
	    else {
		if (x.object.compareTo(nextX.object) < 0) {
		    // Case 3.
		    x.sibling = nextX.sibling;
		    binomialLink(nextX, x);
		}
		else {
		    // Case 4.
		    if (prevX == null)
			h.head = nextX;
		    else
			prevX.sibling = nextX;

		    binomialLink(x, nextX);
		    x = nextX;
		}
	    }

	    nextX = x.sibling;
	}

	return h;	
    }

    /**
     * Links one binomial tree to another.
     *
     * @param y The root of one binomial tree.
     * @param z The root of another binomial tree; this root becomes
     * the parent of <code>y</code>.
     */
    private void binomialLink(Node y, Node z)
    {
	y.p = z;
	y.sibling = z.child;
	z.child = y;
	z.degree++;
    }

    /**
     * Merges the root lists of two binomial heaps together into a
     * single root list.  The degrees in the merged root list appear
     * in monotonically increasing order.
     *
     * @param h1 One binomial heap.
     * @param h2 The other binomial heap.
     * @return The head of the merged list.
     */
    private static Node binomialHeapMerge(BinomialHeap h1, BinomialHeap h2)
    {
	// If either root list is empty, just return the other.
	if (h1.head == null)
	    return h2.head;
	else if (h2.head == null)
	    return h1.head;
	else {
	    // Neither root list is empty.  Scan through both, always
	    // using the node whose degree is smallest of those not
	    // yet taken.
	    Node head;		  // head of merged list
	    Node tail;		  // last node added to merged list
	    Node h1Next = h1.head,
		h2Next = h2.head; // next nodes to be examined in h1 and h2

	    if (h1.head.degree <= h2.head.degree) {
		head = h1.head;
		h1Next = h1Next.sibling;
	    }
	    else {
		head = h2.head;
		h2Next = h2Next.sibling;
	    }

	    tail = head;

	    // Go through both root lists until one of them is
	    // exhausted.
	    while (h1Next != null && h2Next != null) {
		if (h1Next.degree <= h2Next.degree) {
		    tail.sibling = h1Next;
		    h1Next = h1Next.sibling;
		}
		else {
		    tail.sibling = h2Next;
		    h2Next = h2Next.sibling;
		}

		tail = tail.sibling;
	    }

	    // The above loop ended because exactly one of the root
	    // lists was exhuasted.  Splice the remainder of whichever
	    // root list was not exhausted onto the list we're
	    // constructing.
	    if (h1Next != null)
		tail.sibling = h1Next;
	    else
		tail.sibling = h2Next;

	    return head;	// all done!
	}
    }

    /**
     * Decreases the key of a node.  Implements the
     * Binomial-Heap-Decrease-Key procedure on page 470.
     *
     * @param handle Handle to the node whose key is to be decreased.
     * @param k The new key.
     * @throws KeyUpdateException if the new key is greater than the
     * current key.
     */
    public void decreaseKey(Object handle, Comparable k)
	throws KeyUpdateException
    {
	Node x = ((Handle) handle).node;

	// Make sure that the key value does not increase.
	if (k.compareTo(x.object.getKey()) > 0)
	    throw new KeyUpdateException();

	x.object.setKey(k);	// update x's key
	bubbleUp(x, false);	// bubble it up until it's in the right place
    }

    /**
     * Bubbles the value in node up in the binomial heap.  Because
     * this procedure moves objects around within nodes, it has to
     * update handles, too, so that the caller's idea of where a
     * handle points is still accurate.
     *
     * @param x The node whose value is to be bubbled up.
     * @param toRoot If <code>true</code>, the value is bubbled all
     * the way to the root.  If <code>false</code>, the value bubbles
     * up until its key is less than or equal to its parent's key, or
     * until it becomes the root.
     * @return A reference to the node in which the value originally
     * in <code>x</code> ends up.
     */
    public Node bubbleUp(Node x, boolean toRoot)
    {
	Node y = x;
	Node z = y.p;

	while (z != null && (toRoot || y.object.compareTo(z.object) < 0)) {
	    // Exchange the contents of y and z, and update their
	    // handles.
	    DynamicSetElement yObject = y.object;
	    y.object = z.object;
	    z.object = yObject;

	    y.handle.node = z;
	    z.handle.node = y;

	    Handle yHandle = y.handle;
	    y.handle = z.handle;
	    z.handle = yHandle;

	    // Go up one more level.
	    y = z;
	    z = y.p;
	}

	return y;		// this is where x's object ended up
    }

    /**
     * Deletes a node.  Because we do not have a negative infinity in
     * the {@link Comparable} interface, we indirectly emulate the
     * action of the Binomial-Heap-Delete procedure on page 470.
     *
     * @param handle Handle to the node to be deleted.
     */
    public void delete(Object handle)
    {
	Node x = ((Handle) handle).node;

	// Bubble x up to be a root, which is what would happen if we
	// could decrease its key to negative infinity.  Because the
	// contents of nodes change during bubbleUp, the node
	// containing x's contents may change, and so we have to
	// update x.
	x = bubbleUp(x, true);

	// Now remove x from the root list.
	if (head == x)
	    removeFromRootList(x, null); // easy case
	else {
	    // Find x's predecessor.
	    Node pred = head;
	    while (pred.sibling != x)
		pred = pred.sibling;

	    // At this point, pred.sibling is x, so pred is x's
	    // predecessor.
	    removeFromRootList(x, pred);
	}
    }

    /**
     * Returns the <code>String</code> representation of a node's
     * object.
     *
     * @param handle Handle to the node.
     * @throws ClassCastException if <code>handle</code> is not a
     * reference to a <code>Handle</code> object.
     */
    public String dereference(Object handle)
    {
	return ((Handle) handle).node.object.toString();
    }
}

// $Id: BinomialHeap.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: BinomialHeap.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
