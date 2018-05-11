/*
 * @(#)SearchEngine.java	1.8 06/10/30
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

import javax.help.event.EventListenerList;
import javax.help.search.SearchEvent;
import javax.help.search.SearchListener;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Locale;
import java.security.InvalidParameterException;

/**
 * Defines the methods used to access a search engine.
 * Each instance is created by a engine factory.
 * 
 * Extensions of SearchEngine can perform the search or negotiate the search
 * results with an outside agent. A server search engine is an an example
 * of an outside agent.
 *
 * Search results are returned through SearchEvents to listeners that
 * register with a SearchQuery instance. The SearchQuery
 * is returned from the method createQuery.
 *
 * @author Roger D. Brinkley
 * @version	1.6	03/19/99
 *
 * @see javax.help.search.SearchEvent
 * @see javax.help.search.SearchListener
 */

public abstract class SearchEngine {

    protected URL base;		// the base for resolving URLs against
    protected Hashtable params;	// other parameters to the engine

    /**
     * Creates a SearchEngine using the standard JavaHelp SearchEngine
     * parameters. Only this constructor is used to create a SearchEngine
     * from within a search view.
     *
     * @param base The base address of the data.
     * @param params A hashtable of parameters from the search view.
     */
    public SearchEngine(URL base, Hashtable params) 
	throws InvalidParameterException
    {
	this.base = base;
	this.params = params;
    }

    /**
     * Creates a SearchEngine.
     */
    public SearchEngine() {
    }

    /**
     * Creates a new search query.
     */
    public abstract SearchQuery createQuery() throws IllegalStateException;

    private static final boolean debug = false;
    private static void debug(String msg) {
	if (debug) {
	    System.err.println("SearchEngine: "+msg);
	}
    }
}
