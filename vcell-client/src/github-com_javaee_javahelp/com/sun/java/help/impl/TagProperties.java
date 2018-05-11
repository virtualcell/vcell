/*
 * @(#)TagProperties.java	1.8 06/10/30
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * 
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

/*
 * @(#) TagProperties.java 1.8 - last change made 10/30/06
 */
package com.sun.java.help.impl;

/**
 * The <code>TagProperties</code> class represents a persistent set of 
 * properties. Each key and its corresponding value in 
 * the property list is a string. 
 *
 * @see java.help.Tag
 */

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

public class TagProperties implements Cloneable {

    protected Hashtable	hashtable;
    protected int	initialSize;

static int count1 = 0, count2 = 0;

    /**
     * Creates an empty property list with no default values. 
     *
     * @since   JDK1.0
     */
    public TagProperties() {
	this(7);		// Prime number
    }

    public TagProperties(int initialSize) {
	this.initialSize = initialSize;
    }


    /**
     * Searches for the property with the specified key in this property list.
     * The method returns
     * <code>null</code> if the property is not found.
     *
     * @param   key   the property key.
     * @return  the value in this property list with the specified key value.
     * @since   JDK1.0
     */
    public String getProperty(String key) {
	return (String)get(key);
    }

    /**
     * Searches for the property with the specified key in this property list.
     * The method returns the
     * default value argument if the property is not found.
     *
     * @param   key            the hashtable key.
     * @param   defaultValue   a default value.
     *
     * @return  the value in this property list with the specified key value.
     * @since   JDK1.0
     */
    public String getProperty(String key, String defaultValue) {
	String val = getProperty(key);
	return (val == null) ? defaultValue : val;
    }

    /**
     * Returns an enumeration of all the keys in this property list
     *
     * @return  an enumeration of all the keys in this property list
     * @see     java.util.Enumeration
     * @since   JDK1.0
     */
    public Enumeration propertyNames() {
	Hashtable h = new Hashtable(11);
	enumerate(h);
	return h.keys();
    }

    /**
     * Prints this property list out to the specified output stream. 
     * This method is useful for debugging. 
     *
     * @param   out   an output stream.
     * @since   JDK1.0
     */
    public void list(PrintStream out) {
	out.println("-- listing properties --");
	Hashtable h = new Hashtable(11);
	enumerate(h);
	for (Enumeration e = h.keys() ; e.hasMoreElements() ;) {
	    String key = (String)e.nextElement();
	    String val = (String)h.get(key);
	    if (val.length() > 40) {
		val = val.substring(0, 37) + "...";
	    }
	    out.println(key + "=" + val);
	}
    }

    /**
     * Prints this property list out to the specified output stream. 
     * This method is useful for debugging. 
     *
     * @param   out   an output stream.
     * @since   JDK1.1
     */
    /*
     * Rather than use an anonymous inner class to share common code, this
     * method is duplicated in order to ensure that a non-1.1 compiler can
     * compile this file.
     */
    public void list(PrintWriter out) {
	out.println("-- listing properties --");
	Hashtable h = new Hashtable(11);
	enumerate(h);
	for (Enumeration e = h.keys() ; e.hasMoreElements() ;) {
	    String key = (String)e.nextElement();
	    String val = (String)h.get(key);
	    if (val.length() > 40) {
		val = val.substring(0, 37) + "...";
	    }
	    out.println(key + "=" + val);
	}
    }

    /**
     * Enumerates all key/value pairs in the specified hastable.
     * @param h the hashtable
     */
    private synchronized void enumerate(Hashtable h) {
	for (Enumeration e = keys() ; e.hasMoreElements() ;) {
	    String key = (String)e.nextElement();
	    h.put(key, get(key));
	}
    }

    /***** Implementation of a deferred hashtable *****/

    public int size() {
	if (hashtable != null) {
	    return hashtable.size();
	} else {
	    return 0;
	}
    }

    public boolean isEmpty() {
	if (hashtable != null) {
	    return hashtable.isEmpty();
	} else {
	    return true;
	}
    }

    public synchronized Enumeration keys() {
	if (hashtable != null) {
	    return hashtable.keys();
	} else {
	    return new EmptyEnumerator();
	}
    }

    public synchronized Enumeration elements() {
	if (hashtable != null) {
	    return hashtable.elements();
	} else {
	    return new EmptyEnumerator();
	}
    }

    public synchronized boolean contains(Object value) {
	if (hashtable != null) {
	    return hashtable.contains(value);
	} else {
	    return false;
	}
    }

    public synchronized boolean containsKey(Object key) {
	if (hashtable != null) {
	    return hashtable.containsKey(key);
	} else {
	    return false;
	}
    }

    public synchronized Object get(Object key) {
	if (hashtable != null) {
	    return hashtable.get(key);
	} else {
	    return null;
	}
    }

    public synchronized Object put(Object key, Object value) {
	if (hashtable == null) {
	    hashtable = new Hashtable(initialSize);
	}
	return hashtable.put(key, value);
    }

    public synchronized Object remove(Object key) {
	if (hashtable != null) {
	    return hashtable.remove(key);
	} else {
	    return null;
	}
    }

    public synchronized void clear() {
	if (hashtable != null) {
	    hashtable.clear();
	}
    }

    protected void setHashtable(Hashtable t) {
	hashtable = t;
    }

    /**
     * HERE - we probably should use plain Hashtable instead of TagProperties
     */
    public Hashtable getHashtable() {
	return hashtable;
    }

    public synchronized Object clone() {
	try { 
	    TagProperties tp = (TagProperties)super.clone();
	    if (hashtable != null) {
		tp.setHashtable((Hashtable)hashtable.clone());
	    }
	    return tp;
	} catch (CloneNotSupportedException e) { 
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }

    public synchronized String toString() {
	if (hashtable != null) {
	    return hashtable.toString();
	} else {
	    return "{ }";
	}
    }

    class EmptyEnumerator implements Enumeration {
	
	public boolean hasMoreElements() {
	    return false;
    }
	
	public Object nextElement() {
	    throw new java.util.NoSuchElementException("EmptyEnumerator");
	}
    }

}

