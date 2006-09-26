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

import java.awt.Color;

/**
 * Implements the {@link Dictionary} interface as a red-black tree
 * from Chapter 13 of <i>Introduction to Algorithms</i>, Second
 * edition.  Objects inserted into a red-black tree must implement the
 * <code>Comparable</code> interface.
 */

public class RedBlackTree extends BinarySearchTree
{
    /** Color for a red node. */
    protected static final Color RED = Color.red;

    /** Color for a black node. */
    protected static final Color BLACK = Color.black;

    /**
     * Exception thrown by {@link #blackHeight} if the black-height of
     * a node is ill-defined.
     */
    public static class BlackHeightException extends RuntimeException
    {
    }

    /**
     * Inner class for a red-black tree node.  Extends a binary search
     * tree node with an additional color field.
     */
    protected class Node extends BinarySearchTree.Node
    {
	/** The node's color, either RED or BLACK. */
	protected Color color;

	/**
	 * Initializes a node with the data, makes other pointers nil,
	 * and makes the node red.
	 *
	 * @param data Data to save in the node.
	 */
	public Node(Comparable data)
	{
	    super(data);
	    this.color = RED;
	}

	/**
	 * Returns the <code>data</code> instance variable of this
	 * node and this node's color as a <code>String</code>.
	 */
	public String toString()
	{
	    return super.toString() + ", " + (color == RED ? "red" : "black");
	}
    }

    /**
     * Set the sentinel <code>nil</code> to a given node, and make the
     * sentinel black.
     *
     * @param node The node that <code>nil</code> is set to.
     */
    protected void setNil(Node node)
    {
	node.color = BLACK;
	super.setNil(node);
    }

    /**
     * Creates a red-black tree with just a <code>nil</code>, which is
     * the root.
     */
    public RedBlackTree()
    {
	setNil(new Node(null));
	root = nil;
    }

    /**
     * Performs a left rotation on a node, making the node's right
     * child its parent.
     *
     * @param x The node.
     */
    protected void leftRotate(Node x)
    {
	Node y = (Node) x.right;

	// Swap the in-between subtree from y to x.
	x.right = y.left;
	if (y.left != nil)
	    y.left.parent = x;

	// Make y the root of the subtree for which x was the root.
	y.parent = x.parent;
	
	// If x is the root of the entire tree, make y the root.
	// Otherwise, make y the correct child of the subtree's
	// parent.
	if (x.parent == nil)
	    root = y;
	else 
	    if (x == x.parent.left)
		x.parent.left = y;
	    else
		x.parent.right = y;

	// Relink x and y.
	y.left = x;
	x.parent = y;
    }

    /**
     * Performs a right rotation on a node, making the node's left
     * child its parent.
     *
     * @param x The node.
     */
    protected void rightRotate(Node x)
    {
	Node y = (Node) x.left;

	x.left = y.right;
	if (x.left != null)
	    y.right.parent = x;

	y.parent = x.parent;

	y.right = x;
	x.parent = y;

	if (root == x)
	    root = y;
	else
	    if (y.parent.left == x)
		y.parent.left = y;
	    else
		y.parent.right = y;
    }

    /**
     * Inserts data into the tree, creating a new node for this data.
     *
     * @param data Data to be inserted into the tree.
     * @return A reference to the <code>Node</code> object created.
     * The <code>Node</code> class is opaque to methods outside this
     * class.
     */
    public Object insert(Comparable data)
    {
	Node z = new Node(data);
	treeInsert(z);

	return z;
    }

    /**
     * Inserts a node into the tree.
     *
     * @param z The node to insert.
     */
    protected void treeInsert(Node z)
    {
	super.treeInsert(z);
	insertFixup(z);
    }

    /**
     * Restores the red-black conditions of the tree after inserting a
     * node.
     *
     * @param z The node inserted.
     */
    protected void insertFixup(Node z)
    {
	Node y = null;

	while (((Node) z.parent).color == RED) {
	    if (z.parent == z.parent.parent.left) {
		y = (Node) z.parent.parent.right;
		if (y.color == RED) {
		    ((Node) z.parent).color = BLACK;
		    y.color = BLACK;
		    ((Node) z.parent.parent).color = RED;
		    z = (Node) z.parent.parent;
		}
		else {
		    if (z ==  z.parent.right) {
			z = (Node) z.parent;
			leftRotate(z);
		    }
		    
		    ((Node) z.parent).color = BLACK;
		    ((Node) z.parent.parent).color = RED;
		    rightRotate((Node) z.parent.parent);
		}
	    }
	    else {
		y = (Node) z.parent.parent.left;
		if (y.color == RED) {
		    ((Node) z.parent).color = BLACK;
		    y.color = BLACK;
		    ((Node) z.parent.parent).color = RED;
		    z = (Node) z.parent.parent;
		}
		else {
		    if (z ==  z.parent.left) {
			z = (Node) z.parent;
			rightRotate(z);
		    }
		    
		    ((Node) z.parent).color = BLACK;
		    ((Node) z.parent.parent).color = RED;
		    leftRotate((Node) z.parent.parent);
		}
	    }
	}
	((Node) root).color = BLACK;
    }

    /**
     * Removes a node from the tree.
     *
     * @param node The node to be removed.
     * @throws DeleteSentinelException if there is an attempt to
     * delete the sentinel <code>nil</code>.
     * @throws ClassCastException if <code>node</code> does not
     * reference a <code>Node</code> object.
     */
    public void delete(Object handle)
    {
	Node z = (Node) handle;
	Node y = z;
	Node x = (Node) nil;

	// Do not allow the sentinel to be deleted.
	if (z == nil)
	    throw new DeleteSentinelException();
	    
	if (z.left != nil && z.right != nil)
	    y = (Node) successor(z);

	if (z.left != nil)
	    x = (Node) y.left;
	else
	    x = (Node) y.right;

	x.parent = y.parent;

	if (y.parent == nil)
	    root = x;
	else
	    if (y == y.parent.left)
		y.parent.left = x;
	    else
		y.parent.right = x;

	if (y != z) {
	    y.left = z.left;
	    y.left.parent = y;
	    y.right = z.right;
	    y.right.parent = y;
	    y.parent = z.parent;
	    if (z == root)
		root = y;
	    else
		if (z == z.parent.left)
		    z.parent.left = y;
		else
		    z.parent.right = y;
	}

	if (y.color == BLACK)
	    deleteFixup(x);
    }
    
    /**
     * Restores the red-black properties of the tree after a deletion.
     *
     * @param x Node at which there may be a violation.
     */
    protected void deleteFixup(Node x)
    {
	while (x != root && x.color == BLACK) {
	    if (x.parent.left == x) {
		Node w = (Node) x.parent.right;

		if (w.color == RED) {
		    w.color = BLACK;
		    ((Node) x.parent).color = RED;
		    leftRotate((Node) x.parent);
		    w = (Node) x.parent.right;
		}

		if (((Node) w.left).color == BLACK 
		    && ((Node) w.right).color == BLACK) {
		    w.color = RED;
		    x = (Node) x.parent;
		}
		else {
		    if (((Node) w.right).color == BLACK) {
			((Node) w.left).color = BLACK;
			w.color = RED;
			rightRotate(w);
			w = (Node) x.parent.right;
		    }

		    w.color = ((Node) x.parent).color;
		    ((Node) x.parent).color = BLACK;
		    ((Node) w.right).color = BLACK;
		    leftRotate((Node) x.parent);
		    x = (Node) root;
		}
	    }
	    else {
		Node w = (Node) x.parent.left;

		if (w.color == RED) {
		    w.color = BLACK;
		    ((Node) x.parent).color = RED;
		    rightRotate((Node) x.parent);
		    w = (Node) x.parent.left;
		}

		if (((Node) w.right).color == BLACK 
		    && ((Node) w.left).color == BLACK) {
		    w.color = RED;
		    x = (Node) x.parent;
		}
		else {
		    if (((Node) w.left).color == BLACK) {
			((Node) w.right).color = BLACK;
			w.color = RED;
			leftRotate(w);
			w = (Node) x.parent.left;
		    }

		    w.color = ((Node) x.parent).color;
		    ((Node) x.parent).color = BLACK;
		    ((Node) w.left).color = BLACK;
		    rightRotate((Node) x.parent);
		    x = (Node) root;
		}		
	    }
	}
	x.color = BLACK;
    }

    /**
     * Returns the number of black nodes from a given node down to any
     * leaf.  The value should be the same for all paths.
     *
     * @param z The node.
     * @throws BlackHeightException if the number of black nodes on a
     * path from the left child down to a leaf differs from the number
     * of black nodes on a path from the right child down to a leaf.
     */
    public int blackHeight(Node z)
    {
	if (z == nil)
	    return 0;

	int left = blackHeight((Node) z.left);
	int right = blackHeight((Node) z.right);
	if (left == right)
	    if (z.color == BLACK)
		return left + 1;
	    else
		return left;
	else
	    throw new BlackHeightException();
    }

    /**
     * Returns the number of black nodes from the root down to any
     * leaf.  The value should be the same for all paths.
     *
     * @param z The node.
     * @throws BlackHeightException if the number of black nodes on a
     * path from the left child down to a leaf differs from the number
     * of black nodes on a path from the right child down to a leaf.
     */
    public int blackHeight()
    {
	return blackHeight((Node) root);
    }
}

// $Id: RedBlackTree.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: RedBlackTree.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
