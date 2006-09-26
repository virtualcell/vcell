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

/**
 * Implements an order-statistic tree as described in Section 14.1 of
 * <i>Introduction to Algorithms</i>, Second edition.
 */

public class OrderStatisticTree extends RedBlackTree
{
    /**
     * Exception thrown by {@link #actualSize} if the size of the
     * subtree does not agree with the size field of the subtree's
     * root node.
     */
    public static class SizeException extends RuntimeException
    {
	/** Creates a <code>SizeException</code>. */
	public SizeException(String msg)
	{
	    super(msg);
	}
    }

    /**
     * Inner class for an order-statistic tree node, extending a
     * red-black tree node with an additional size field.
     */
    protected class Node extends RedBlackTree.Node
    {
	/** Number of nodes in the subtree rooted at this node. */
	protected int size;
	
	/**
	 * Initializes a new node in an order-statistic tree.
	 *
	 * @param data Data to save in the node.
	 */
	public Node(Comparable data)
	{
	    super(data);
	    size = 1;
	}

	/** Returns a string representation of this node's data and
	 * size. */
	public String toString()
	{
	    return super.toString() + ", size(" + size + ")";
	}
    }

    /**
     * Set the sentinel <code>nil</code> to a given node.
     *
     * @param node The node that <code>nil</code> is set to.
     */
    protected void setNil(Node node)
    {
	node.size = 0;
	super.setNil(node);
    }

    /**
     * Creates an order-statistic tree with just a <code>nil</code>,
     * which is the root.
     */
    public OrderStatisticTree()
    {
	setNil(new Node(null));
	root = nil;
    }

    /**
     * Finds the node in the tree that is at a given ordinal position
     * in an inorder walk of the tree.
     *
     * @param i The ordinal position.
     * @return An opaque handle to the node that is ith in an inorder
     * walk of the tree.
     */
    public Object select(int i)
    {
	return select(root, i);
    }

    /**
     * Finds the node in a subtree that is at a given ordinal position
     * in an inorder walk of the subtree.
     *
     * @param x Root of the subtree.
     * @param i The ordinal position.
     * @return An opaque handle to the node that is ith in an inorder
     * walk of the subtree.
     */
    protected Object select(BinarySearchTree.Node x, int i)
    {
	int r = 1 + ((Node) x.left).size;
	
	if (i == r)
	    return x;
	else if (i < r)
	    return select(x.left, i);
	else
	    return select(x.right, i - r);
    }

    /**
     * Determines, for a given node, in which ordinal position the
     * node would appear in an inorder walk of the tree.
     *
     * @param handle Handle to the node.
     * @return The ordinal position of the node in an inorder walk of
     * the tree.
     * @throws ClassCastException if <code>Handle</code> does not
     * reference a <code>Node</code> object.
     */
    public int rank(Object handle)
    {
	Node x = (Node) handle;
	Node y = x;
	int r = ((Node) x.left).size + 1;

	while(y != root) {
	    if (y == y.parent.right) 
		r += ((Node) y.parent.left).size + 1;
	 
	    y = (Node) y.parent;
	}

	return r;
    }

    /**
     * Calls {@link RedBlackTree}'s {@link RedBlackTree#leftRotate}
     * and then fixes the <code>size</code> fields.
     *
     * @param x handle The node being left rotated.
     */
    protected void leftRotate(RedBlackTree.Node x)
    {
	Node y = (Node) x.right;
	super.leftRotate(x);

	y.size = ((Node) x).size;
	((Node) x).size = ((Node) x.left).size + ((Node) x.right).size + 1;
    }

    /**
     * Calls {@link RedBlackTree}'s {@link RedBlackTree#rightRotate}
     * and then fixes the <code>size</code> fields.
     *
     * @param x handle The node being right rotated.
     */
    protected void rightRotate(RedBlackTree.Node x)
    {
	Node y = (Node) x.left;
	super.rightRotate(x);

	y.size = ((Node) x).size;
	((Node) x).size = ((Node) x.left).size + ((Node) x.right).size + 1;
    }

    /**
     * Inserts data into the tree, creating a new node for this data.
     *
     * @param data The data being inserted.
     * @return A handle to the <code>Node</code> that is created.
     * <code>Node</code> is opaque to methods outside this class.
     */
    public Object insert(Comparable data)
    {
	Node z = new Node(data);
	treeInsert(z);

	return z;
    }

    /**
     * Inserts a node, updating the <code>size</code> fields of its
     * ancestors before the superclass's <code>insertNode</code> is
     * called.
     *
     * @param z The node to insert.
     */
    protected void treeInsert(Node z)
    {
	// Update the size fields of the path down to where 
	// handle will be inserted in the tree.
	for (Node i = (Node) root;
	     i != nil;
	     i = (Node) ((i.compareTo(z) >= 0) ? i.left : i.right))
	    i.size++;
	
	// Insert the handle's node into the tree. 
	super.treeInsert(z);
    }

    /**
     * Deletes a node from the tree.
     *
     * @param handle Handle to the node being deleted.
     * @throws ClassCastException if <code>handle</code> is not a
     * <code>Node</code> object.
     */
    public void delete(Object handle)
    {
	// Walk up the tree by following parent pointers while
	// updating the size of each node along the path.
	Node x = (Node) handle;
	for (Node i = (Node) x.parent; i != nil; i = (Node) i.parent)
	    i.size--;

	// Now actually remove the node.
	super.delete(handle);
    }

    /**
     * Returns the actual size of the entire tree.
     *
     * @throws SizeException if any <code>size</code> instance
     * variable is incorrect.
     */ 
    public int actualSize() throws SizeException
    {
	return actualSize(root);
    }

    /**
     * Returns the actual size of the subtree rooted at a node.
     *
     * @param handle Handle to the node that is the root of the
     * subtree.
     * @throws SizeException if any <code>size</code> instance
     * variable is incorrect.
     * @throws ClassCastException if <code>handle</code> is not a
     * <code>Node</code> object.
     */ 
    public int actualSize(Object handle) throws SizeException
    {
	Node z = (Node) handle;
	int left = 0; 		// size of the left subtree
	int right = 0;		// size of the right subtree
	String msg = "";	// cumulative error message

	if (handle == nil)	// empty tree
	    return 0;

	// Compute the size of the left and right subtrees.
	try {
	    left = actualSize(z.left);
	}
	catch (SizeException ex) {
	    msg += ex.getMessage();
	}

	try {
	    right = actualSize(z.right);
	}
	catch (SizeException ex) {
	    msg += ex.getMessage();
	}

	// If an error occurred, throw an exception.
	if (msg.compareTo("") == 0)
	    if (z.size == left + right + 1)
		return z.size;
	    else
		throw new SizeException(z.toString() + "\n");
	else
	    throw new SizeException(msg);
    }
}

// $Id: OrderStatisticTree.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: OrderStatisticTree.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
