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

/** Implementation of a B-tree from Chapter 18 of <i>Introduction to
 * Algorithms</i>, Second edition.  Objects inserted into a B-Tree
 * must implement the {@link DynamicSetElement} interface. */

public class BTree implements Dictionary
{
    /** The minimum degree, i.e., the minimum number of keys in any
     * node other than the root. */
    final private int t;

    /** The maximum number of keys in any node, =
     * 2<code>t</code>-1. */
    final private int maxKeys;

    /** The root of the B-tree. */
    private Node root;
    
    /** Private inner class for a B-tree node. */
    private class Node
    {
	/** The number of keys stored in the node. */
	public int n;

	/** Array of the keys stored in the node. */
	public DynamicSetElement[] key;

	/** Pointers to the children, if this node is not a leaf.  If
	 * this node is a leaf, then <code>null</code>. */
	public Node[] c;

	/** <code>true</code> if this node is a leaf,
	 * <code>false</code> if this node is an interior node. */
	public boolean leaf;

	/**
	 * Initializes the instance variables, including allocating
	 * the <code>c</code> array if this node is not a leaf.
	 *
	 * @param n How many of keys are to be stored in this node.
	 * @param leaf <code>true</code> if this node is a leaf,
	 * <code>false</code> if this node is an interior node.
	 */
	public Node(int n, boolean leaf)
	{
	    this.n = n;
	    this.leaf = leaf;
	    key = new DynamicSetElement[maxKeys];
	    if (leaf)
		c = null;
	    else
		c = new Node[maxKeys+1];
	}

	/** Reads a disk block.  Does nothing in this implementation.
	 * You should customize it for your system. */
	private void diskRead()
	{
	}

	/** Writes a disk block.  Does nothing in this implementation.
	 * You should customize it for your system. */
	public void diskWrite()
	{
	}

	/** Frees this node.  Does nothing in this implementation.
	 * You should customize it for your system. */
	private void free()
	{
	}

	/**
	 * Searches for a dynamic set element with a given key,
	 * starting at this node.
	 *
	 * @param k The key.
	 * @return A handle to the object found, or <code>null</code>
	 * if there is no match.
	 */
	public BTreeHandle BTreeSearch(Comparable k)
	{
	    int i = 0;

	    while (i < n && key[i].compareTo(k) < 0)
		i++;

	    if (i < n && key[i].compareTo(k) == 0)
		return new BTreeHandle(this, i); // found it

	    if (leaf)
		return null;	// no child to search
	    else {		// search child i
		c[i].diskRead();
		return c[i].BTreeSearch(k);
	    }
	}

	/**
	 * Splits this node, which is a full child of its parent,
	 * which is in turn a nonfull internal node.  We assume that
	 * both this node and its parent are already in main memory.
	 * This method splits this node in two and adjusts the parent
	 * so that it has an additional child.
	 *
	 * @param x This node's parent.
	 * @param i This node is child <code>c[i]</code> of
	 * <code>x</code>.
	 */
	public void BTreeSplitChild(Node x, int i)
	{
	    Node z = new Node(t-1, leaf);

	    // Copy the t-1 keys in positions t to 2t-2 into z.
	    for (int j = 0; j < t-1; j++) {
		z.key[j] = key[j+t];
		key[j+t] = null; // remove the reference
	    }
	    
	    // If this node is not a leaf, copy the t children in
	    // positions t to 2t-1, too.
	    if (!leaf)
		for (int j = 0; j < t; j++) {
		    z.c[j] = c[j+t];
		    c[j+t] = null; // remove the reference
		}

	    n = t-1;

	    // Move the children in x that are to the right of y by
	    // one position to the right.
	    for (int j = x.n; j >= i+1; j--)
		x.c[j+1] = x.c[j];

	    // Drop z into x's child i+1.
	    x.c[i+1] = z;

	    // Move the keys in x that are to the right of y by one
	    // position to the right.
	    for (int j = x.n-1; j >= i; j--)
		x.key[j+1] = x.key[j];

	    // Move this node's median key into x, and remove the
	    // reference to the key in this node.
	    x.key[i] = key[t-1];
	    key[t-1] = null;

	    x.n++;		// one more key/child in x

	    // All done.  Write out the nodes.
	    diskWrite();
	    z.diskWrite();
	    x.diskWrite();
	}

	/**
	 * Inserts a new element in this node, which is assumed to be
	 * nonfull.
	 *
	 * @param k The new element to be inserted.
	 */
	public BTreeHandle BTreeInsertNonfull(DynamicSetElement k)
	{
	    int i = n-1;
	    Comparable kKey = k.getKey();

	    if (leaf) {
		// Move all keys greater than k's by one position to
		// the right.
		while (i >= 0 && key[i].compareTo(kKey) > 0) {
		    key[i+1] = key[i];
		    i--;
		}

		// Either i is -1 or key[i] is the rightmost key <=
		// k's key.  In either case, drop k into position i+1.
		key[i+1] = k;
		n++;
		diskWrite();

		// Return the handle saying where we dropped k.
		return new BTreeHandle(this, i+1);
	    }
	    else {
		// Find which child we descend into.
		while (i >= 0 && key[i].compareTo(kKey) > 0)
		    i--;

		// Either i is -1 or key[i] is the rightmost key <=
		// k's key.  In either case, descend into position
		// i+1.
		i++;
		c[i].diskRead();
		if (c[i].n == maxKeys) {
		    // That child is full, so split it, and possibly
		    // update i to descend into the new child.
		    c[i].BTreeSplitChild(this, i);
		    if (key[i].compareTo(kKey) < 0)
			i++;
		}

		return c[i].BTreeInsertNonfull(k);
	    }
	}

	/**
	 * Deletes an element with a given key from the subtree rooted
	 * at this node.
	 *
	 * @param k The key of an element to be deleted.
	 */
	public void delete(Comparable k)
	{
	    if (leaf)
		deleteFromLeaf(k);
	    else {
		// Determine if k is found in this node.
		int i = 0;

		while (i < n && key[i].compareTo(k) < 0)
		    i++;
		if (i < n && key[i].compareTo(k) == 0)
		    deleteFromInternalNode(i); // found it, so delete it
		else {
		    // If it's in the subtree rooted at this node,
		    // it's in the subtree rooted in child i.
		    Node child = c[i];
		    child.diskRead();    // read the child into memory
		    ensureFullEnough(i); // ensure the child has >= t keys
		    child.delete(k);     // now OK to recurse
		}
	    }
	}		    

	/**
	 * Deletes an element with a given key from this node, which
	 * is assumed to be a leaf and already in memory.  Does
	 * nothing if this node does not contain an element with the
	 * given key.
	 *
	 * @param k The key of the element to be deleted.
	 */
	private void deleteFromLeaf(Comparable k)
	{
	    // Determine if k is found in this node.
	    int i = 0;

	    while (i < n && key[i].compareTo(k) < 0)
		i++;
	    if (i < n && key[i].compareTo(k) == 0) {
		// It's here and in position i.  Move all keys in
		// positions greater than i by one position to the
		// left.
		for (int j = i+1; j < n; j++)
		    key[j-1] = key[j];
		key[n-1] = null; // remove the reference
		n--;		 // one fewer key

		diskWrite();	 // we've changed this node
	    }
	}	    

	/**
	 * Deletes a given key from this node, which is assumed to be
	 * an internal node and already in memory.
	 *
	 * @param i <code>key[i]</code> is deleted from this node.
	 */
	private void deleteFromInternalNode(int i)
	{
	    DynamicSetElement k = key[i]; // key i
	    Node y = c[i];	          // child preceding k
	    y.diskRead();	          // get this child into memory
	    if (y.n >= t) {	          // does y have at least t keys?
		// If so, find the predecessor kPrime of k within the
		// subtree rooted at y.  We find kPrime by starting at
		// y and always taking the rightmost child, until we
		// get to a leaf; kPrime is the element with the
		// greatest key in that leaf.  Recursively delete
		// kPrime, and replace k by kPrime in this node.

		// Note: Although page 451 says that finding kPrime and
		// deleting it can be performed in a single downward
		// pass, for simplicity, we make separate passes to
		// find and delete it.

		DynamicSetElement kPrime = y.findGreatestInSubtree();
		y.diskRead();	// in case we lost it during
				// findGreatestInSubtree
		y.delete(kPrime.getKey());
		diskRead();	// in case we lost this node while deleting
		key[i] = kPrime;
	    }
	    else {
		// Do the same for the child z that follows key i,
		// where kPrime is k's successor.  Use the same
		// simplification as above.
		Node z = c[i+1];
		z.diskRead();
		if (z.n >= t) {
		    DynamicSetElement kPrime = z.findSmallestInSubtree();
		    z.diskRead(); // in case we lost it during
				  // findSmallestInSubtree
		    z.delete(kPrime.getKey());
		    diskRead();	  // in case we lost this node while deleting
		    key[i] = kPrime;
		}
		else {
		    // Both y and z have t-1 keys.  Merge k and all of
		    // z into y.  This node loses both k and the
		    // pointer to z, and y then contains 2t-1 keys.
		    // Then recursively delete k from y.  Since this
		    // node, y, and z are the three most recently read
		    // nodes, we assume that they are in memory.

		    y.key[y.n] = k;
		    for (int j = 0; j < z.n; j++)
			y.key[y.n+j+1] = z.key[j];

		    // If y and z are not leaves, copy z's child
		    // pointers.
		    if (!y.leaf)
			for (int j = 0; j <= z.n; j++)
			    y.key[y.n+j+1] = z.key[j];

		    y.n += z.n + 1;

		    // Remove k and z from this node.
		    for (int j = i+1; j < n; j++) {
			key[j-1] = key[j];
			c[j] = c[j+1];
		    }
		    key[n-1] = null;
		    c[n] = null;
		    n--;

		    diskWrite();          // this node changed
		    y.diskWrite();        // as did y
		    z.free();	          // all done with z

		    y.delete(k.getKey()); // recursively delete k from y
		}
	    }
	}

	/**
	 * Finds the dynamic set element with the greatest key in the
	 * subtree rooted at this node.  Works by always taking the
	 * rightmost child until we get to a leaf.  Assumes that this
	 * node is already in memory.
	 *
	 * @return The dynamic set element with the greatest key in
	 * the subtree rooted at this node.
	 */
	private DynamicSetElement findGreatestInSubtree()
	{
	    if (leaf)
		return key[n-1];
	    else {
		c[n].diskRead();
		return c[n].findGreatestInSubtree();
	    }
	}

	/**
	 * Finds the dynamic set element with the smallest key in the
	 * subtree rooted at this node.  Works by always taking the
	 * leftmost child until we get to a leaf.  Assumes that this
	 * node is already in memory.
	 *
	 * @return The dynamic set element with the smallest key in
	 * the subtree rooted at this node.
	 */
	private DynamicSetElement findSmallestInSubtree()
	{
	    if (leaf)
		return key[0];
	    else {
		c[0].diskRead();
		return c[0].findSmallestInSubtree();
	    }
	}

	/**
	 * Ensures that a given child of this node child has at least
	 * <code>t</code> keys.  Assumes that this node and the child
	 * are already in memory.
	 *
	 * @param i The <code>i</code>th child of this node is the one
	 * that will have at last <code>t</code> keys.
	 */
	private void ensureFullEnough(int i)
	{
	    Node child = c[i];
	    if (child.n < t) {
		Node leftSibling; // child's left sibling
		int leftN;	  // left sibling's n value

		if (i > 0) {
		    leftSibling = c[i-1];
		    leftSibling.diskRead();
		    leftN = leftSibling.n;
		}
		else {
		    leftSibling = null;
		    leftN = 0;
		}

		if (leftN >= t) { // does left sibling have at least t keys?
		    // Move all the child's keys (and its child
		    // pointers) to the right by one position.
		    for (int j = child.n-1; j >= 0; j--)
			child.key[j+1] = child.key[j];
		    if (!child.leaf)
			for (int j = child.n; j >= 0; j--)
			    child.c[j+1] = child.c[j];

		    // Move a key down from this node into child and
		    // from the left sibling into this node.
		    child.key[0] = key[i-1];
		    key[i-1] = leftSibling.key[leftN-1];
		    leftSibling.key[leftN-1] = null;

		    // If not leaves, move a pointer from the left
		    // sibling into child.
		    if (!child.leaf) {
			child.c[0] = leftSibling.c[leftN];
			leftSibling.c[leftN] = null;
		    }

		    leftSibling.n--; // one fewer key in left sibling
		    child.n++;	     // and one more in child

		    // 3 nodes changed.
		    diskWrite();
		    child.diskWrite();
		    leftSibling.diskWrite();
		}
		else {		// do the symmetric thing with right sibling
		    Node rightSibling; // child's right sibling
		    int rightN;	       // right sibling's n value

		    if (i < n) {
			rightSibling = c[i+1];
			rightSibling.diskRead();
			rightN = rightSibling.n;
		    }
		    else {
			rightSibling = null;
			rightN = 0;
		    }

		    if (rightN >= t) {
			// Move a key down from this node into child
			// and from the right sibling into this node.
			child.key[child.n] = key[i];
			key[i] = rightSibling.key[0];

			// If not leaves, move a pointer from the
			// right sibling into child.
			if (!child.leaf)
			    child.c[child.n] = rightSibling.c[0];

			// Move all the right sibling's keys (and its child
			// pointers) to the left by one position.
			for (int j = 1; j < rightN; j++)
			    rightSibling.key[j-1] = rightSibling.key[j];
			rightSibling.key[rightN-1] = null;
			if (!rightSibling.leaf) {
			    for (int j = 1; j <= rightN; j++)
				rightSibling.c[j-1] = rightSibling.c[j];
			    rightSibling.c[rightN] = null;
			}

			rightSibling.n--; // one fewer key in right sibling
			child.n++;	     // and one more in child

			// 3 nodes changed.
			diskWrite();
			child.diskWrite();
			rightSibling.diskWrite();
		    }
		    else {
			// The child and both of its siblings have t-1
			// keys.  Merge one of the siblings into the
			// child, moving a key from this node down
			// into the child.
			if (leftN > 0) {
			    // Merge the left sibling into the child.
			    // Start by moving everything in the child
			    // right by t positions.
			    for (int j = child.n-1; j >= 0; j--)
				child.key[j+t] = child.key[j];
			    if (!child.leaf)
				for (int j = child.n; j >= 0; j--)
				    child.c[j+t] = child.c[j];

			    // Take everything from the left sibling.
			    for (int j = 0; j < leftN; j++) {
				child.key[j] = leftSibling.key[j];
				leftSibling.key[j] = null;
			    }
			    if (!child.leaf)
				for (int j = 0; j <= leftN; j++) {
				    child.c[j] = leftSibling.c[j];
				    leftSibling.c[j] = null;
				}

			    // Move a key down from this node into the child.
			    child.key[t-1] = key[i-1];

			    child.n += leftN + 1;

			    // Since this node is losing key i-1 and
			    // child pointer i-1, move keys i to n-1
			    // and children i to n left by one
			    // position in this node.
			    for (int j = i; j < n; j++) {
				key[j-1] = key[j];
				c[j-1] = c[j];
			    }
			    c[n-1] = c[n];
			    key[n-1] = null;
			    c[n] = null;
			    n--;

			    leftSibling.free();	// all done with left sibling
			    diskWrite();        // this node changed
			    child.diskWrite();  // as did the child
			}
			else {
			    // Merge the right sibling into the child.
			    // Start by taking everything from the
			    // right sibling.
			    for (int j = 0; j < rightN; j++) {
				child.key[j+child.n+1] = rightSibling.key[j];
				rightSibling.key[j] = null;
			    }
			    if (!child.leaf)
				for (int j = 0; j <= rightN; j++) {
				    child.c[j+child.n+1] = rightSibling.c[j];
				    rightSibling.c[j] = null;
				}

			    // Move a key down from this node into the child.
			    child.key[t-1] = key[i];

			    child.n += rightN + 1;

			    // Since this node is losing key i and
			    // child pointer i, move keys i+1 to n-1
			    // and children i+2 to n left by one
			    // position in this node.
			    for (int j = i+1; j < n; j++) {
				key[j-1] = key[j];
				c[j] = c[j+1];
			    }
			    key[n-1] = null;
			    c[n] = null;
			    n--;

			    rightSibling.free(); // all done with right sibling
			    diskWrite();         // this node changed
			    child.diskWrite();   // as did the child
			}			    
		    }
		}
	    }
	}		

	/** Returns the <code>String</code> representation of the
	 * subtree rooted at this node.  Represents the depth of each
	 * node by two spaces per depth preceding the
	 * <code>String</code> representation of the node. */
	public String walk(int depth)
	{
	    String result = "";

	    for (int i = 0; i < n; i++) {
		if (!leaf)
		    result += c[i].walk(depth+1);
		for (int j = 0; j < depth; j++)
		    result += "  ";
		result += "Node at " + this + ", key " + i + ": " +
		    key[i] + "\n";
	    }

	    if (!leaf)
		result += c[n].walk(depth+1);

	    return result;
	}
    }

    /** Class to define a handle returned by searches.  This class is
     * opaque, in that outside of the <code>BTree</code> class,
     * <code>BTreeHandle</code> objects cannot be examined. */
    private static class BTreeHandle
    {
	/** A node in the B-tree. */
	Node node;

	/** Index of the key in the node. */
	int i;

	/**
	 * Saves the node and index in the instance variables.
	 *
	 * @param node The node.
	 * @param i The index of the key in the node.
	 */
	public BTreeHandle(Node node, int i)
	{
	    this.node = node;
	    this.i = i;
	}
    }

    /**
     * Creates an empty B-tree.  Emulates B-Tree-Create on page 442.
     *
     * @param t The minimum degree of this B-tree.
     */
    public BTree(int t)
    {
	this.t = t;
	maxKeys = 2 * t - 1;
	root = new Node(0, true); // root is a leaf
	root.diskWrite();	  // write the root to disk
    }

    /**
     * Searches for an element with a given key.
     *
     * @param k The key being searched for.
     * @return A handle to the object found, or <code>null</code> if
     * there is no match.
     */
    public Object search(Comparable k)
    {
	return root.BTreeSearch(k);
    }

    /**
     * Inserts an element.
     *
     * @param o The element to insert.
     * @return A handle to the new element.
     * @throws ClassCastException if <code>o</code> does not reference
     * a {@link DynamicSetElement}.
     */
    public Object insert(Comparable o)
    {
	Node r = root;
	DynamicSetElement e = DynamicSetElement.Helper.cast(o);

	if (r.n == maxKeys)
	    {
		// Split the root.
		Node s = new Node(0, false);
		root = s;
		s.c[0] = r;
		r.BTreeSplitChild(s, 0);
		return s.BTreeInsertNonfull(e);
	    }
	else
	    return r.BTreeInsertNonfull(e);
    }

    /**
     * Removes an element.
     *
     * <p>
     *
     * The element to remove is specified by its key, rather than by a
     * handle giving its location in the B-tree.  We need to specify
     * the object by its key because deletion from a B-tree works
     * top-down, emulating a search.  We need to use the path from the
     * root down to the node containing the element to be deleted, and
     * if we were given the element's node and index instead of its
     * key, we would not be able to determine this path efficiently.
     *
     * @param key The key of the element to remove.
     * @throws ClassCastException if <code>key</code> does not
     * implement <code>Comparable</code>.
     */
    public void delete(Object key)
    {
	root.delete((Comparable) key); // start at the root, and go down

	// Special case for when the root is an internal node with no
	// keys.
	if (!root.leaf && root.n == 0)
	    root = root.c[0];	// the root's only child becomes the root
    }

    /** Returns the <code>String</code> representation of this B-tree
     * by walking it. */
    public String toString()
    {
	return root.walk(0);
    }
}

// $Id: BTree.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: BTree.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
