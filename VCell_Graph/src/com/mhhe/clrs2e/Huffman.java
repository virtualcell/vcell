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

/** Implements Huffman's algorithm as described in Section 16.3 of
 * <i>Introduction to Algorithms</i>, Second edition. */

public class Huffman
{
    /** The root of the Huffman tree. */
    private Node root;

    /** Inner class for a node in a Huffman tree. */
    private static class Node implements DynamicSetElement
    {
	/** The sum of the frequencies in all leaves of the subtree of
	 * which this node is the root. */
	private final double frequency;

	/** Sets the frequency, based on the parameter. */
	public Node(double freq)
	{
	    frequency = freq;
	}

	/**
	 * Cannot set the key.
	 *
	 * @param key The key, but it cannot be set.
	 * @throws UnsupportedOperationException always.
	 */
	public void setKey(Comparable key)
	{
	    throw new UnsupportedOperationException();
	}

	/** Returns the frequency as the key. */
	public Comparable getKey()
	{
	    return new Double(frequency);
	}

	/**
	 * Compares this node to another, based on their frequencies.
	 *
	 * @param o The other node.
	 * @return A negative integer if this node's frequency is
	 * less, 0 if the nodes have equal frequencies, or a positive
	 * integer if this node's frequency is greater.
	 * @throws ClassCastException if <code>o</code> does not
	 * reference a <code>Node</code> object.
	 */
	public int compareTo(Object o)
	{
	    Node other = (Node) o;

	    if (frequency < other.frequency)
		return -1;
	    else if (frequency == other.frequency)
		return 0;
	    else
		return 1;
	}
    }

    /** Inner class for an item in a prefix code.  Objects of this
     * class serve as leaves in a Huffman tree, once it has been
     * constructed. */
    public static class PrefixCodeItem extends Node
	implements DynamicSetElement
    {
	/** The character that this entry represents. */
	private final char character;

	/** This character's codeword, once the Huffman tree has been
	 * constructed. */
	private String codeWord;

	/**
	 * Creates a new item in a prefix code.
	 *
	 * @param c This entry's character.
	 * @param freq This entry's frequency.
	 */
	public PrefixCodeItem(char c, double freq)
	{
	    super(freq);
	    character = c;
	    codeWord = null;
	}

	/** Returns the character. */
	public char getChar()
	{
	    return character;
	}

	/** Returns the codeword. */
	public String getCodeWord()
	{
	    return codeWord;
	}
    }

    /** Inner class for an internal node in a Huffman tree. */
    private static class InternalNode extends Node implements DynamicSetElement
    {
	/** This node's left child. */
	private final Node left;

	/** This node's right child. */
	private final Node right;

	/**
	 * Creates a new internal node.
	 *
	 * @param left This node's left child.
	 * @param right This node's right child.
	 */
	public InternalNode(Node left, Node right)
	{
	    super(((Double) right.getKey()).doubleValue()
		  + ((Double) left.getKey()).doubleValue());
	    this.left = left;
	    this.right = right;
	}
    }

    /**
     * Creates a Huffman tree from an array of
     * <code>PrefixCodeItem</code>.  Sets the instance variable
     * <code>root</code> to reference the root of this tree, and
     * stores the codewords into all the <code>PrefixCodeItem</code>s
     * at the leaves of the tree.
     *
     * @param items The array of <code>PrefixCodeItem</code>.
     */
    public Huffman(PrefixCodeItem[] items)
    {
	root = constructTree(items);
	encodeLeaves(root, "");
    }

    /**
     * Creates a Huffman tree from an array of
     * <code>PrefixCodeItem</code>.  Implements the Huffman procedure
     * from page 388.
     *
     * @param items The array of <code>PrefixCodeItem</code>.
     * @return The root of the Huffman tree.
     */
    private Node constructTree(PrefixCodeItem[] items)
    {
	int n = items.length;
	MinPriorityQueue queue = new MinHeapPriorityQueue();

	// Add the members of items to the min-priority queue.
	for (int i = 0; i < n; i++)
	    queue.insert(items[i]);

	// Build the tree, putting the more frequently used leaves
	// closer to the root.
	for (int i = 1; i < n; i++) {
	    Node x = (Node) queue.extractMin();
	    Node y = (Node) queue.extractMin();
	    Node z = new InternalNode(x, y);

	    queue.insert(z);
	}

	// Return the resulting tree.
	return (Node) queue.extractMin();
    }

    /**
     * Labels all leaves in a subtree with their appropriate codewords.
     *
     * @param x Root of the subtree.
     * @param code <code>x</code>'s codeword, which is a prefix of all
     * codewords for leaves within the subtree rooted at
     * <code>x</code>.
     */
    private void encodeLeaves(Node x, String code)
    {
	if (x instanceof PrefixCodeItem)
	    ((PrefixCodeItem) x).codeWord = code;
	else {
	    encodeLeaves(((InternalNode) x).left, code + "0");
	    encodeLeaves(((InternalNode) x).right, code + "1");
	}
    }

    /**
     * Decodes an encoded string, where the encoding is a
     * <code>String</code> consisting of 0s and 1s.
     *
     * @param encoding The encoded string, assumed to consist only of
     * 0s and 1s.
     * @return The decoded string.
     */
    public String decode(String encoding)
    {
	String result = "";

	int bitIndex = 0;
	while (bitIndex < encoding.length()) {
	    Node x = root;	// start decoding at the root

	    // Keep going down until we hit a leaf.
	    while (x instanceof InternalNode) {
		if (encoding.charAt(bitIndex++) == '0')
		    x = ((InternalNode) x).left;
		else
		    x = ((InternalNode) x).right;
	    }

	    // Having hit a leaf, append the character in this leaf to
	    // the result.
	    result += ((PrefixCodeItem) x).character;
	}

	return result;
    }

    /**
     * Decodes an encoded string, where the encoding is bits packed
     * into an array of <code>int</code>.
     *
     * @param encoding The encoded string, as bits packed into an
     * array of <code>int</code>.
     * @param n The total number of bits in the encoding.
     * @return The decoded string.
     */
    public String decode(int[] encoding, int n)
    {
	String result = "";

	int bitIndex = 0;
	while (bitIndex < n) {
	    Node x = root;	// start decoding at the root

	    // Keep going down until we hit a leaf.
	    while (x instanceof InternalNode) {
		if (getBit(encoding, bitIndex++) == 0)
		    x = ((InternalNode) x).left;
		else
		    x = ((InternalNode) x).right;
	    }

	    // Having hit a leaf, append the character in this leaf to
	    // the result.
	    result += ((PrefixCodeItem) x).character;
	}

	return result;
    }

    /**
     * Returns a specific bit in an array of <code>int</code>.
     *
     * @param array The array of <code>int</code>.
     * @param i Which bit we want.
     * @return The <code>i</code>th bit of the array.
     */
    private int getBit(int[] array, int i)
    {
	final int WORD_SIZE = 32;
	final int index = i / WORD_SIZE;
	final int bitNumber = i % WORD_SIZE;
	return (array[index] >> bitNumber) & 1;
    }
}

// $Id: Huffman.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: Huffman.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
