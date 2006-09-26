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
 * Interface for an element in a dynamic set.  We can get and set a
 * key, which is a {@link Comparable} object, and we can compare
 * dynamic-set elements to other dynamic-set elements and, depending
 * on the implmentation, to arbitrary <code>Comparable</code> objects.
 */

public interface DynamicSetElement extends Comparable
{
    /**
     * Sets the key of an element.
     *
     * @param key Key to be set.
     */
    public void setKey(Comparable key);

    /**
     * Returns the key of an element.
     */
    public Comparable getKey();

    /**
     * Compares this <code>DynamicSetElement</code> to another object.
     * The normal implementation is simply <code>return
     * DynamicSetElement.Helper.compareTo(this, e);</code>
     *
     * @param e The other object.
     * @return A negative integer if this
     * <code>DynamicSetElement</code> is less; 0 if the objects are
     * equal; a positive integer if this
     * <code>DynamicSetElement</code> is greater.
     * @throws ClassCastException if the implementation calls
     * <code>DynamicSetElement.Helper.compareTo(this, e)</code> and
     * <code>e</code> does not implement <code>Comparable</code>.
     */
    public int compareTo(Object e);

    /** Inner class to define static helper methods. */
    public static class Helper
    {
	/**
	 * Compares a <code>DynamicSetElement</code> to another
	 * object.  If the other object is also a
	 * <code>DynamicSetElement</code>, the keys of both are
	 * compared.  If the other object is not a
	 * <code>DynamicSetElement</code> object but does implement
	 * <code>Comparable</code>, then the key of of the
	 * <code>DynamicSetElement</code> is compared to the other
	 * object.  Otherwise, the other object does not implement
	 * <code>Comparable</code>, and a
	 * <code>ClassCastException</code> is thrown.
	 *
	 * @param e The <code>DynamicSetElement</code>.
	 * @param o The other object.
	 * @return A negative integer if <code>e</code> is less; 0 if
	 * the objects are equal; a positive integer if <code>e</code>
	 * is greater.
	 * @throws ClassCastException if <code>o</code> does not
	 * implement <code>Comparable</code>.
	 */
	public static int compareTo(DynamicSetElement e, Object o)
	{
	    if (o instanceof DynamicSetElement)
		return e.getKey().compareTo(((DynamicSetElement) o).getKey());
	    else if (o instanceof Comparable)
		return e.getKey().compareTo(o);
	    else
		throw new ClassCastException("Attempt to compare a DynamicSetElement to an object that does not implement Comparable.");
	}

	/**
	 * Casts an object to <code>DynamicSetElement</code>, throwing
	 * a <code>ClassCastException</code> if the object fails to
	 * implement the <code>DynamicSetElement</code> interface.
	 *
	 * @param o The object.
	 * @return The object <code>o</code>, but cast as a
	 * <code>DynamicSetElement</code>.
	 * @throws ClassCastException if <code>o</code> does not
	 * implement the <code>DynamicSetElement</code> interface.
	 */
	public static DynamicSetElement cast(Object o)
	{
	    if (o instanceof DynamicSetElement)
		return (DynamicSetElement) o;
	    else
		throw new ClassCastException("Object fails to implement DynamicSetElement interface.");
	}
    }
}

// $Id: DynamicSetElement.java,v 1.1 2003/10/14 16:56:20 thc Exp $
// $Log: DynamicSetElement.java,v $
// Revision 1.1  2003/10/14 16:56:20  thc
// Initial revision.
//
