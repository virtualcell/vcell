/*
 * @(#)SearchEvent.java	1.22 06/10/30
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

package javax.help.search;

import java.util.Vector;
import java.util.Enumeration;

/**
 * Encapsulates information that describes changes to a SearchQuery.  It is used
 * to notify listeners of the change.
 *
 * @author Roger D. Brinkley
 * @version	1.16	03/19/99
 */

public class SearchEvent extends java.util.EventObject
{
    private String params;
    private boolean searching;
    private Vector items;

    /**
     * Represents a change in the SearchEngine. Used for starting the search
     * or ending the search.
     *
     * @param source The source of this event.
     * @param params The search parameters.
     * @param searching A boolean operator that indicates if searching is 
     * executing (true) or stopped (false).
     * @throws IllegalArgumentException if source, or params is NULL.
     */
    public SearchEvent(Object source, String params, boolean searching) {
	super (source);
	if (params == null) {
	    throw new IllegalArgumentException("null params");
	}
	this.params = params;
	this.searching = searching;
    }

    /**
     * Represents a change in the SearchEngine. Used to indicate that either a single
     * item or a group of items have matched the params.
     *
     * @param source The source of this event.
     * @param params The search parameters.
     * @param searching A boolean operator that indicates if a search is 
     * executing (true) or stopped (false).
     * @param items A Vector of SearchItems matching the the search params.
     *
     * @throws IllegalArgumentException if source, params, or items is NULL.
     * @see java.javahelp.SearchItems
     */
    public SearchEvent(Object source, String params, boolean searching, Vector items) {
	super(source);
	if (params == null) {
	    throw new IllegalArgumentException("null params");
	}
	this.params = params;
	this.searching = searching;
	if (items == null) {
	    throw new IllegalArgumentException("null items");
	}
	this.items = items;
    }


    /**
     * Returns the parameters to the query.
     */
    public String getParams() {
	return params;
    }

    /**
     * A boolean value that indicates if the search is completed.
     */
    public boolean isSearchCompleted() {
	return searching;
    }

    /**
     * An enumerated list of SearchItems that match parameters of the query.
     */
    public Enumeration getSearchItems() {
	if (items == null) {
	    return null;
	}
	return items.elements();
    }
}
