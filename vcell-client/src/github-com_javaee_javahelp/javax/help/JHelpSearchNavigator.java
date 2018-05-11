/*
 * @(#)JHelpSearchNavigator.java	1.52 06/10/30
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
import javax.help.search.SearchEngine;
import javax.help.search.MergingSearchEngine;

/**
 * A JHelpNavigator for search data.
 * All of the tree navigation and selection has been delegated to the UI.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version	1.52	10/30/06
 */

public class JHelpSearchNavigator extends JHelpNavigator {
    private MergingSearchEngine search;	// our search engine


    /**
     * Creates a Search navigator
     *
     * @param view The NavigatorView. If view is null it creates a JHelpSearchNavigator
     * with a null NavigatorView.
     */
    public JHelpSearchNavigator(NavigatorView view) {
	super(view, null);
    }

    /**
     * Creates a Search navigator.
     *
     * @param view The NavigatorView. If <tt>view</tt> is null it creates a JHelpSearchNavigator
     * with a null NavigatorView.
     * @param model The HelpModel this Navigator is presenting. If <tt>model</tt> is null it 
     * creates a JHelpSearchNavigator witout a model.
     */
    public JHelpSearchNavigator(NavigatorView view, HelpModel model) {
	super(view, model);
    }
    


    // HERE - label & Locale?
    /**
    * Creates a TOC navigator with explicit arguments.  Note that this should not throw
    * an InvalidNavigatorViewException since the type is passed implicitly.
    *
    * @param hs HelpSet
    * @param name The name indentifying this HelpSet.
    * @param label The label to use (for this locale).
    * @param data The "data" part of the parameters, a URL to the location of the TOC data.
    */
    public JHelpSearchNavigator(HelpSet hs,
				String name, String label, URL data) 
	throws InvalidNavigatorViewException
    {
	super(new SearchView(hs, name, label, createParams(data)));
    }


    /**
     * The UID for this JComponent.
     */
    public String getUIClassID() {
	return "HelpSearchNavigatorUI";
    }

    /**
     * Search Database methods.
     */

    /**
     * Instantiates and returns a SearchEngine class. 
     * The default query engine to use is <tt>com.sun.java.help.search.SearchEngine</tt>,
     * but this can be changed through the &lt;engine&gt;&lt;/engine&gt; attribute
     * of the view.
     *
     * @return The SearchEngine instantiation.
     */
    public SearchEngine getSearchEngine() {
	if (search == null) {
	    search = new MergingSearchEngine(getNavigatorView());
	}
	return search;
    }

    /**
     * Explicitly changes the default (overriding what is in the HelpSet).
     *
     * @param search A SearchEngine instantiation.
     */
    public void setSearchEngine(SearchEngine search) {
	this.search = new MergingSearchEngine(search);
    }

    /**
     * Default for the search engine.
     */

    protected String getDefaultQueryEngine() {
	return HelpUtilities.getDefaultQueryEngine();
    }

    /**
     * Determines if this instance of a JHelpNavigator can merge its data with another one.
     *
     * @param view The data to merge.
     * @return Whether it can be merged.
     *
     * @see merge(NavigatorView)
     * @see remove(NavigatorView)
     */
    public boolean canMerge(NavigatorView view) {
	if (view instanceof SearchView &&
	    getNavigatorName().equals(view.getName())) {
	    debug("canMerge: true");
	    return true;
	}
	debug("canMerge: false");
	return false;
    }

    /**
     * Merges a NavigatorView into this instance.
     *
     * @param view The data to merge.
     * @exception IllegalArgumentException
     * @exception IllegalStateException
     *
     * @see canMerge(NavigatorView)
     * @see remove(NavigatorView)
     */
    public void merge(NavigatorView view) {
	// Add the requested query engine to our list of engines
	debug("JHelpSearchNavigator.merge invoked");
	debug("  params: "+view.getParameters());
	if (search == null) {
	    search = (MergingSearchEngine) getSearchEngine();
	}
	search.merge(view);
	debug("merge: "+view);
	this.getUI().merge(view);
    }

    /**
     * Removes a NavigatorView from this instance.
     *
     * @param view The data to merge.
     * @exception IllegalArgumentException
     * @exception IllegalStateException
     *
     * @see canMerge(NavigatorView)
     * @see merge(NavigatorView)
     */
    public void remove(NavigatorView view) {
	// Remove the requested query engine from our list of engines
	debug("JHelpSearchNavigator.remove invoked");
	debug("  params: "+view.getParameters());
	if (search == null) {
	    search = (MergingSearchEngine) getSearchEngine();
	}
	search.remove(view);
	debug("remove: "+view);
	this.getUI().remove(view);
    }

    private static final boolean debug = false;
    private static void debug(String msg) {
	if (debug) {
	    System.err.println("JHelpSearchNavigator: "+msg);
	}
    }


}

