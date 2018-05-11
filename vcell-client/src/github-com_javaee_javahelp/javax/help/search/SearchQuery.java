/*
 * @(#)SearchQuery.java	1.9 06/10/30
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
import java.util.Vector;
import java.util.Locale;
import java.util.Enumeration;

/**
 * The instance of a query on a search database. It is 
 * instantiated by SearchEngine.startQuery.
 *
 * Subclasses of SearchQuery can perform the search or negotiate the search
 * results with an outside agent as setup in the SearchEngine class. A server
 * search engine is an an example of an outside agent. 
 *
 * Search results are returned through SearchEvents to listeners that
 * register with a SearchEngine instance.
 *
 * @author Roger D. Brinkley
 * @version	1.22	09/16/98
 *
 * @see javax.help.search.SearchEvent
 * @see javax.help.search.SearchListener
 */

public abstract class SearchQuery {

    protected EventListenerList listenerList = new EventListenerList();
    protected SearchEngine hs;
    protected String searchparams;
    protected Locale l;

    /**
     * Creates a SearchQuery.
     */
    public SearchQuery(SearchEngine hs) {
	this.hs = hs;
    }

    /**
     * Adds a listener for the SearchEngine posted after the search has
     * started, stopped, or search parameters have been defined.
     * 
     * @param l The listener to add.
     * @see java.javahelp.SearchEngine#removeSearchListener
     */
    public void addSearchListener(SearchListener l) {
	listenerList.add(SearchListener.class, l);
    }

    /**
     * Removes a listener previously added with <tt>addSearchListener</tt>.
     *
     * @param l The listener to remove.
     * @see java.javahelp.SearchEngine#addSearchListener
     */
    public void removeSearchListener(SearchListener l) {
	listenerList.remove(SearchListener.class, l);
    }

    /**
     * Starts the search. This method invokes searchStarted on 
     * SearchListeners and stores the searchparams. Extensions
     * of SearchQuery should fully implement this method according
     * to the needs of the SearchQuery and its corresponding SearchEngine.
     * 
     *
     * @param searchparams The search string.
     * @param locale The locale of the search string.
     * @exception IllegalArgumentException The parameters are not 
     * understood by this engine.
     * @exception IllegalStateException There is an active search in 
     * progress in this instance.
     */
    public void start(String searchparams, Locale l)
	throws IllegalArgumentException, IllegalStateException
    {
	this.searchparams = searchparams;
	this.l = l;
	fireSearchStarted();
    }

    /**
     * Stops the search. This method invokes searchStopped on 
     * SearchListeners. Extensions of 
     * SearchQuery should fully implement this method according to needs
     * of the SearchQuery and its corresponding SearchEngine. 
     *
     * @exception IllegalStateException The search engine is not in a state in which it can be started.
     */
    public void stop() throws IllegalStateException {
	fireSearchFinished();
    }

    /**
     * Returns the SearchEngine associated with this SearchQuery.
     */
    public SearchEngine getSearchEngine() {
	return hs;
    }

    /**
     * Determines if this SearchQuery is active.
     *
     * @returns True if active, false otherwise
     */
    public abstract boolean isActive();

    /**
     * Notifies that query of items is found in the search.
     *
     * @param docs A vector of SearchItem.
     * @param inSearch Is the search completed?
     */
    public void itemsFound(boolean inSearch, Vector docs) {
	fireItemsFound(inSearch, docs);
    }

    /**
     * Notifies that a SearchItem has been found.
     *
     * @param params The parameters to the search.
     * @param inSearch Is the search completed?
     * @param docs A vector of SearchItem.
     * @see javax.help.search.SearchItem
     */
    protected void fireItemsFound(boolean inSearch, Vector docs) {
	debug("fireItemsFound");
	debug("  params: " + searchparams);
	debug("  insearch: " + inSearch);
	debug("  docs: " + docs);
	Object[] listeners = listenerList.getListenerList();
	SearchEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == SearchListener.class) {
		if (e == null) {
		    e = new SearchEvent(this, searchparams,
					    inSearch, docs);
		}
		((SearchListener)listeners[i+1]).itemsFound(e);
	    }	       
	}
    }

    /**
     * Passs through that a SearchEvent has happened.
     * This is useful for SearchEngine engines that encapsulate others.
     *
     * @param e The SearchEvent to pass through.
     */
    protected void fireItemsFound(SearchEvent e) {
	Object[] listeners = listenerList.getListenerList();

	Vector newItems = new Vector();
	for (Enumeration enum1 = e.getSearchItems();
	     enum1.hasMoreElements(); ) {
	    newItems.addElement((SearchItem) enum1.nextElement());
	}

	SearchEvent e2 = new SearchEvent(this, e.getParams(), 
						 e.isSearchCompleted(),
						 newItems);
	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == SearchListener.class) {
		((SearchListener)listeners[i+1]).itemsFound(e2);
	    }	       
	}
    }

    /**
     * Notifies that a search has started.
     *
     * @param params The parameters to the search.
     */
    protected void fireSearchStarted() {
	debug("fireSearchStarted");
	Object[] listeners = listenerList.getListenerList();
	SearchEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == SearchListener.class) {
		if (e == null) {
		    e = new SearchEvent(this, searchparams, true);
		}
		((SearchListener)listeners[i+1]).searchStarted(e);
	    }	       
	}
    }

    /**
     * Notifies that a search has completed.
     *
     * @param params The parameters to the search.
     */
    protected void fireSearchFinished() {
	debug("fireSearchFinished");
	Object[] listeners = listenerList.getListenerList();
	SearchEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == SearchListener.class) {
		if (e == null) {
		    e = new SearchEvent(this, searchparams, false);
		}
		((SearchListener)listeners[i+1]).searchFinished(e);
	    }	       
	}
    }
    
    private static final boolean debug = false;
    private static void debug(String msg) {
	if (debug) {
	    System.err.println("SearchQuery: "+msg);
	}
    }
}
