/*
 * @(#)Map.java	1.13 06/10/30
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
import java.io.Serializable;

/**
 * A Map is the interface to ID<->URL mapping.
 *
 * @author Eduardo Pelegri-Llopart
 * @author Stepan Marek
 * @version	1.13	10/30/06
 */
public interface Map {
    /**
     * Determines if the ID is valid (defined in the map file).
     * 
     * @param id The String ID.
     * @param hs The HelpSet against which to resolve the string.
     * @return True if the ID is valid, false if not valid.
     */

    public boolean isValidID(String id, HelpSet hs);

    /**
     * Gets an enumeration of all the IDs in a Map.
     *
     * @return An enumeration of all the IDs in a Map.
     */
    // We need this so we can add FlatMaps to TryMaps - epll
    public Enumeration getAllIDs();

    /**
     * Gets the URL that corresponds to a given ID in the Map.
     *
     * @param id The ID to get the URL for.
     * @return URL The matching URL.  Null if this map cannot solve the ID.
     * @exception MalformedURLException if the URL is malformed
     */
    public URL getURLFromID(ID id) throws MalformedURLException;

    /**
     * Determines if the URL corresponds to an ID in the Map.
     *
     * @param url The URL to check on.
     * @return True if this is an ID, false otherwise.
     */
    public boolean isID(URL url);

    /**
     * Determines the ID for this URL.
     * 
     * @param url The URL to get the ID for.
     * @return The ID (or null if URL does not correspond to an ID).
     */
    public ID getIDFromURL(URL url);

    // HERE - do we want this.  It is *very* expensive to do  - epll

    /**
     * Determines the ID that is "closest" to this URL (with a given anchor).
     *
     * The definition of this is up to the implementation of Map.  In particular,
     * it may be the same as getIDFromURL().
     *
     * @param url A URL
     * @return The closest ID in this map to the given URL.
     */
    public ID getClosestID(URL url);

    /**
     * Determines the IDs related to this URL.
     *
     * @param URL The URL to which to compare the Map IDs.
     * @return Enumeration of Map.Key (Strings/HelpSet)
     */
    public Enumeration getIDs(URL url);

    /**
     * An ID is a pair of String, HelpSet.
     *
     * An ID fully identifies a "location" within a HelpSet.
     */
    
    final public class ID implements Serializable{
	public String id;
	public HelpSet hs;
	
	/**
	 * A location within a HelpSet.  If id or hs are null, a null ID is returned.
	 *
	 * @param id The String
	 * @param hs The HelpSet
	 * @exception BadIDException if String is not within the Map of the
	 * HelpSet.
	 */

	public static ID create(String id, HelpSet hs) throws BadIDException {
	    if (hs == null ||
		id == null) {
		return null;
	    }

	    Map map = hs.getCombinedMap();
	    if (! map.isValidID(id, hs)) {
		throw new BadIDException("Not valid ID: "+id, map, id, hs);
	    }
	    return new ID(id, hs);
	}

	/**
	 * Creates an ID object.
	 */

	private ID(String id, HelpSet hs) throws BadIDException {
	    this.id = id;
	    this.hs = hs;
	}

        /** Getter for property hs.
         * @return Value of property hs.
         */
        public HelpSet getHelpSet() {
            return hs;
        }

        /** Getter for property id.
         * @return Value of property id.
         */
        public String getIDString() {
            return id;
        }
            
        /**
         * Returns the URL that the Map.ID refers to.
         *
         * @return URL The matching URL. Null if the map cannot solve the ID.
         * @exception MalformedURLException if the URL is malformed
         * @see javax.help.Map#getURLFromID(javax.help.Map.ID id)
         */
        public URL getURL() throws MalformedURLException {
            if (hs == null || id == null) {
                return null;
            }
            try {
                return hs.getCombinedMap().getURLFromID(this);
            } catch (MalformedURLException e) {
                throw e;
            }
        }
        
	/**
	 * Determines if two IDs are equal.
	 * @param o The object to compare.
	 */

	public boolean equals(Object o) {
	    if (o instanceof ID) {
		ID id2 = (ID) o;
		return (id2.id.equals(id) && (id2.hs.equals(hs)));
	    }
	    return false;
	}

	/**
	 * Gets an external represenation of an ID.
	 */
	public String toString() {
	    return("ID: "+id+", "+hs);
	}
    }
}
