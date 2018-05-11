/*
 * @(#)MergingSearchEngine.java	1.8 06/10/30
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

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Locale;
import java.net.URL;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.NavigatorView;
import javax.help.search.SearchListener;
import javax.help.search.SearchEvent;
import javax.help.search.SearchEngine;
import javax.help.search.SearchQuery;

/*
 * A class that provides a merging/removing layer for the search.
 */
public class MergingSearchEngine extends SearchEngine {
    
    private Vector engines;
    private Hashtable enginePerView = new Hashtable();
    private boolean stopQuery = false;

    public MergingSearchEngine(NavigatorView view) {
	if (view == null) {
	    throw new IllegalArgumentException("view must not be null");
	}
	engines = new Vector();
	// HERE - the makeEngine() should be delayed until the actual query
	SearchEngine engine = makeEngine(view);
	engines.addElement(engine);
    }
	
    public MergingSearchEngine(SearchEngine engine) {
	if (engine == null) {
	    throw new IllegalArgumentException("engine must not be null");
	}
	engines = new Vector();
	engines.addElement(engine);
    }

    /**
     * Creates the query for this helpset.
     */
    public SearchQuery createQuery() {
	return new MergingSearchQuery(this);
    }

    /**
     * Adds/Removes a Search Engine to/from list.
     *
     * Possibly the makeEngine should be delayed until the actual query.
     */

    public void merge(NavigatorView view) {
	if (view == null) {
	    throw new IllegalArgumentException("view must not be null");
	}
	SearchEngine engine = makeEngine(view);
	if (engine == null) {
	    throw new IllegalArgumentException("view is invalid");
	}
	engines.addElement(engine);
	enginePerView.put(view, engine);
    }

    /*
     * Remove a Navigator View
     * Throws an IllegalArgumentException if view is null or if there
     * is no search engine for a view.
     */
    public void remove(NavigatorView view) {
	if (view == null) {
	    throw new IllegalArgumentException("view is either null or invalid");
	}
	SearchEngine engine = (SearchEngine) enginePerView.get(view);
	if (engine != null) {
	    engines.removeElement(engine);
	    enginePerView.remove(engine);
	} else {
	    throw new IllegalArgumentException("view is either null or invalid");
	}

    }

    public Enumeration getEngines() {
	return engines.elements();
    }

    private SearchEngine makeEngine(NavigatorView view) {
	Hashtable params = view.getParameters();

	// if there were no parameters or there were parameters but
	// no data then return a null SearchEngine
	if (params == null || 
	    (params != null && !params.containsKey("data"))) {
	    return null;
	}
	String engineName = (String) params.get("engine");
	HelpSet hs = view.getHelpSet();
	URL base = hs.getHelpSetURL();
	ClassLoader loader = hs.getLoader();

	if (engineName == null) {
	    engineName = HelpUtilities.getDefaultQueryEngine();
	    params.put("engine", engineName);
	}
	
	SearchEngine back = null;

	Constructor konstructor;
	Class types[] = {URL.class, Hashtable.class};
	Object args[] = {base, params};
	Class klass;

	debug("makeEngine");
	debug("  base: "+base);
	debug("  params: "+params);

	try {
	    if (loader == null) {
		klass = Class.forName(engineName);
	    } else {
		klass = loader.loadClass(engineName);
	    }
	} catch (Throwable t) {
	    throw new Error("Could not load engine named "+engineName+" for view: "+view);
	}

	try {
	    konstructor = klass.getConstructor(types);
	} catch (Throwable t) {
	    throw new Error("Could not find constructor for "+engineName+". For view: "+view);
	}
	try {
	    back = (SearchEngine) konstructor.newInstance(args);
	} catch (InvocationTargetException e) {
            System.err.println("Exception while creating engine named "+engineName+" for view: "+view);
            e.printStackTrace();
	} catch (Throwable t) {
	    throw new Error("Could not create engine named "+engineName+" for view: "+view);
	}
	return back;
    }

    private class MergingSearchQuery extends SearchQuery implements SearchListener {

	private MergingSearchEngine mhs;
	private Vector queries;
	private String searchparams;

	public MergingSearchQuery(SearchEngine hs) {
	    super(hs);
	    if (hs instanceof MergingSearchEngine) {
		this.mhs = (MergingSearchEngine) hs;
	    }
	}

	// Start all the search engines
	public synchronized void start(String searchparams, Locale l)
	    throws IllegalArgumentException, IllegalStateException
	{
	    MergingSearchEngine.this.debug("startSearch()");

	    // if we're already alive you can't start again
	    if (isActive()) {
		throw new IllegalStateException();
	    }

	    stopQuery = false;

	    // setup everthing to get started
	    super.start(searchparams, l);
	    queries = new Vector();
		
		// Get a query for each engine
	    for (Enumeration e = mhs.getEngines();
		 e.hasMoreElements(); ) {
		SearchEngine engine = (SearchEngine) e.nextElement();
		if (engine != null) {
		    queries.addElement(engine.createQuery());
		}
	    }
		
	    // Set the listener to this class and start the query
	    for (Enumeration e = queries.elements(); e.hasMoreElements(); ) {
		SearchQuery query = (SearchQuery) e.nextElement();
		query.addSearchListener(this);
		query.start(searchparams, l);
	    }
	}

	// Stop all the search engines
	// This is an override of the SearchQuery.stop
	// Donnot call super.stop in this method as an
	// extra fireSearchStopped will be genertated
	public synchronized void stop() throws IllegalStateException {
	    // Can't stop what is already stopped silly
	    if (queries == null) {
		return;
	    }

	    stopQuery = true;

	    // Loop through all the queries and and make sure they have
	    // all inActive. If any query is active wait a small period of
	    // time 
	    boolean queriesActive = true;
	    while (queriesActive) {
		queriesActive = false;

		// Throughout this process the queries will disappear so
		// protect against a null pointer
		if (queries == null) {
		    continue;
		}
		for (Enumeration e = queries.elements();
		     e.hasMoreElements(); ) {
		    SearchQuery query = (SearchQuery) e.nextElement();
		    if (query.isActive()) {
			debug ("queries are active waiting to stop");
			queriesActive = true;
		    }
		}
		if (queriesActive) {
		    try {
			wait(250);
		    } catch (InterruptedException ex) {
			ex.printStackTrace();
		    }
		}
	    }

	    queries = null;
	}

	public boolean isActive() {

	    // if there aren't any queries we aren't alive
	    if (queries == null) {
		return false;
	    }

	    // Loop through all the queries and see if anyone is alive
	    for (Enumeration e = queries.elements();
		 e.hasMoreElements(); ) {
		SearchQuery query = (SearchQuery) e.nextElement();
		if (query.isActive()) {
		    return true;
		}
	    }

	    // Didn't find anyone alive so we're not alive
	    return false;
	}

	public SearchEngine getSearchEngine() {
	    return mhs;
	}

	public synchronized void itemsFound(SearchEvent e) {
	    SearchQuery queryin = (SearchQuery) e.getSource();

	    if (stopQuery) {
		return;
	    }

	    // Loop through all the queries and match this one
	    if (queries != null) {
		Enumeration enum1 = queries.elements();
		while (enum1.hasMoreElements()) {
		    SearchQuery query = (SearchQuery) enum1.nextElement();
		    if (query == queryin) {
			// Redirect any Events as if they were from me
			fireItemsFound(e);
		    }
		}
	    }
	}

	public void searchStarted(SearchEvent e) {
	    // Ignore these events as this class already informed
	    // the listeners the search was started so we don't have 
	    // to do anything else
	}

	public synchronized void searchFinished(SearchEvent e) {
	    SearchQuery queryin = (SearchQuery) e.getSource();
		
	    // Loop through all the queries and match this one
	    if (queries != null) {
		Enumeration enum1 = queries.elements();
		while (enum1.hasMoreElements()) {
		    SearchQuery query = (SearchQuery) enum1.nextElement();
		    if (query == queryin) {
			queryin.removeSearchListener(this);
			queries.removeElement(query);
		    }
		}
		// If all the queries are done then send a searchFinished
		if (queries.isEmpty()) {
		    queries = null;
		    if (!stopQuery) {
			fireSearchFinished();
		    }
		}
	    }
		
	}

    }    // This needs to be public to deal with inner classes...

    private static final boolean debug = false;
    private static void debug(String msg) {
	if (debug) {
	    System.err.println("MergineSearchEngine: "+msg);
	}
    }

}
