/*
 * @(#)TryMap.java	1.19 06/10/30
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

package javax.help;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.*;
import java.io.*;
import java.beans.*;
import javax.help.event.*;
import javax.help.Map.ID;

/**
 * A Map that can combine a number of other Maps in an 
 * efficient manner.
 *
 * Currently this is a brute-force implementation.
 *
 * @author Eduardo Pelegri-Llopart
 * @version	1.11	03/10/99
 */
public class TryMap implements Map, Serializable {
    private Vector maps;	// All the maps

    /**
     * Creates an empty Map.
     * This is useful for filtering and to add/remove to/from it.
     */
    public TryMap() {
	maps = new Vector();
    }

    /**
     * Adds a map to a "filter" Map.
     * Adding a composed map to another is equivalent to
     * adding the entire Map individually.
     *
     * @param map The new Map to add. If Map is null it is not added.
     */
    public void add(Map map) {
	maps.addElement(map);
    }

    /**
     * Removes a Map from this "filter" Map.
     *
     * @param map The Map to add.
     * @return Whether the Map is already present. If the Map is
     * null or was not previously added, returns "false".
     */
    public boolean remove(Map map) {
	return maps.removeElement(map);
    }

    /**
     * Enumerates all the Maps in this TryMap.
     *
     * @return An enumeration of the Maps added.
     */
    public Enumeration getMaps() {
	return maps.elements();
    }

    /**
     * Determines if the ID is valid (known to in the project file).
     * 
     * @param id The ID to check. A null ID is a valid parameter
     * @param hs The HelpSet against which to resolve the string.
     * @return True if id is valid, false if not valid.
     */

    public boolean isValidID(String id, HelpSet hs) {
	debug("isValidID "+id);
	for (Enumeration e = maps.elements();
	     e.hasMoreElements();) {
	    Map m = (Map) e.nextElement();
	    if (m.isValidID(id, hs)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Gets an enumeration of all the IDs in a Map.
     *
     * @param An enumeration of all the IDs in a Map.
     */
    public Enumeration getAllIDs() {
	return new TryEnumeration(maps.elements(), null);
    }

    /**
     * Gets the URL that corresponds to a given ID in the Map.
     *
     * @param id The ID for which to get the URL. If <tt>id</tt> is null it is
     * treated as an unresolved ID and returns null.
     * @return URL The matching URL.  Null if this Map cannot resolve the ID.
     * @exception MalformedURLException if the URL specification found is malformed
     */
    public URL getURLFromID(ID id) throws MalformedURLException {
	debug("getURLFromID("+id+")");
	URL back = null;
	for (Enumeration e = maps.elements();
	     e.hasMoreElements(); ) {
	    Map m = (Map) e.nextElement();
	    back = m.getURLFromID(id);
	    if (back != null) {
		return back;
	    }
	}
	return back;
    }

    /**
     * Determines if the URL corresponds to an ID in the Map.
     *
     * @param url The URL to check on.
     * @return True if this is an ID, false otherwise.
     */
    public boolean isID(URL url) {
	for (Enumeration e = maps.elements();
	     e.hasMoreElements(); ) {
	    Map m = (Map) e.nextElement();
	    if (m.isID(url)) {
		return true;
	    }
	}
	return false;
    }


    /**
     * Determines the ID for this URL.
     * 
     * @param url The URL to get the ID for.
     * @return The ID (Map.ID), or null if URL is not an ID
     */
    public ID getIDFromURL(URL url) {
	debug("getIDFromURL("+url+")");
	ID back = null;
	for (Enumeration e = maps.elements();
	     e.hasMoreElements(); ) {
	    Map m = (Map) e.nextElement();
	    back = m.getIDFromURL(url);
	    if (back != null) {
		return back;
	    }
	}
	return null;
    }

    /**
     * Determines the ID that is "closest" to this URL (with a given anchor).
     *
     * @param url A URL
     * @return The closest ID in this map to the given URL
     */
    public ID getClosestID(URL url) {
	ID back = null;
	// See if there is an exact match
	back = getIDFromURL(url);
	if (back != null) {
	    return back;
	}

	// for backwards compatability return a null if the URL is null
	if (back == null && url == null) {
	    return null;
	}
	 
	// no exact match try removing the ref if there is one
	String ref = url.getRef();
	if (ref != null) {
	    String urlString = url.toExternalForm();
	    urlString = urlString.substring(0,urlString.lastIndexOf(ref)-1);
	    try {
		URL newURL = new URL(urlString);
		for (Enumeration e = maps.elements();
		     e.hasMoreElements(); ) {
		    Map m = (Map) e.nextElement();
		    back = m.getIDFromURL(newURL);
		    if (back != null) {
			return back;
		    }
		}
	    } catch (MalformedURLException mue) {
	    }
	}
	return null;
    }

    /**
     * Gets the the IDs related to this URL.
     *
     * @param URL The URL to compare the Map IDs to.
     * @return Enumeration of IDs (Strings)
     */
    public Enumeration getIDs(URL url) {
	return new TryEnumeration(maps.elements(), url);
    }

    private static class TryEnumeration implements Enumeration {
	private Enumeration e;	// the maps
	private Enumeration k;	// the IDs within a map
	private URL url;

	public TryEnumeration(Enumeration e, URL url) {
	    this.e = e;
	    this.k = null;
	    this.url = url;
	}

	public boolean hasMoreElements() {
	    while (k == null ||
		   !k.hasMoreElements()) {
		if (! e.hasMoreElements()) {
		    return false;
		}
		Map m = (Map) e.nextElement();
		if (url == null) {
		    k = m.getAllIDs();
		} else {
		    k = m.getIDs(url);
		}
	    }
	    return k.hasMoreElements();
	}

	public Object nextElement() {
	    return k.nextElement(); // this is an ID
	}

    }

    /**
     * For printf debugging.
     */
    private static final boolean debug = false;
    private static void debug(String str) {
        if (debug) {
            System.out.println("TryMap: " + str);
        }
    }

}
