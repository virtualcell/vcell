/*
 * @(#)SearchTOCItemTag.java	1.3 06/10/30
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

package javax.help.tagext;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.util.*;
import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.Map;
import javax.help.Map.ID;
import javax.help.NavigatorView;
import javax.help.SearchTOCItem;
import javax.help.SearchView;
import javax.help.SearchHit;
import javax.help.search.MergingSearchEngine;
import javax.help.search.SearchQuery;
import javax.help.search.SearchListener;
import javax.help.search.SearchEvent;
import javax.help.search.SearchItem;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * The JSP tag extra info class for an SearchTOCItem
 *
 * @author Roger D. Brinkley
 * @version	1.3	10/30/06
 * @see javax.help.SearchTOCItem
 */

public class SearchTOCItemTag extends BodyTagSupport implements SearchListener{
    private Enumeration treeEnum;
    private Vector nodes;
    private SearchView view;
    private HelpBroker hb;
    private String query;
    private MergingSearchEngine helpsearch;
    private SearchQuery searchquery;
    private boolean searchFinished;

    public void setSearchView(SearchView view) {
	this.view = view;
    }

    public void setHelpBroker(HelpBroker hb) {
	this.hb = hb;
    }

    public void setQuery(String query) {
	this.query = query;
    }


    public synchronized int doStartTag() {
	if (helpsearch == null) {
	    helpsearch = new MergingSearchEngine(view);
	    searchquery = helpsearch.createQuery();
	    searchquery.addSearchListener(this);

	    // Make sure all the subhelpsets have a search engine
	    addSubHelpSets(view.getHelpSet());
	}

	if (searchquery.isActive()) {
	    searchquery.stop();
	}

	searchquery.start(query, Locale.getDefault());

	if (!searchFinished) {
	    try {
		wait();
	    } catch (InterruptedException e) {
		// ignore
	    }
	}


	if(treeEnum.hasMoreElements()) {
	    SearchTOCItem item = (SearchTOCItem) treeEnum.nextElement();
	    setNodeAttributes(item);
	    return EVAL_BODY_TAG;
	} else {
	    return SKIP_BODY;
	}
    }

    /** Adds subhelpsets
     *
     * @param hs The HelpSet which subhelpsets will be added
     */
    private void addSubHelpSets(HelpSet hs){
        for( Enumeration e = hs.getHelpSets(); e.hasMoreElements(); ) {
	    HelpSet ehs = (HelpSet) e.nextElement();
	    if (ehs == null) {
		continue;
	    }
            // merge views
            NavigatorView[] views = ehs.getNavigatorViews();
            for(int i = 0; i < views.length; i++){
		if (views[i] instanceof SearchView) {
		    helpsearch.merge(views[i]);
		}
            }
            addSubHelpSets( ehs );
	}
    }    

    public int doAfterBody() throws JspException {
	BodyContent body = getBodyContent();
	try {
	    body.writeOut(getPreviousOut());
	} catch (IOException e) {
	    throw new JspTagException("SearchTOCItemTag: " + e.getMessage());
	}

	// clear up so the next time the body content is empty
	body.clearBody();
	if(treeEnum.hasMoreElements()) {
	    SearchTOCItem item = (SearchTOCItem) treeEnum.nextElement();
	    setNodeAttributes(item);
	    return EVAL_BODY_TAG;
	} else {
	    return SKIP_BODY;
	}
    }

    private void setNodeAttributes(SearchTOCItem item) {
	pageContext.setAttribute("name", item.getName());
	pageContext.setAttribute("helpID", getMapID(item));
	pageContext.setAttribute("confidence", Double.toString(item.getConfidence()));
	pageContext.setAttribute("hits", Integer.toString(item.hitCount()));
	pageContext.setAttribute("contentURL", item.getURL().toExternalForm());
	pageContext.setAttribute("hitBoundries", getSearchHits(item));
    }

    /**
     * return an Map.ID if one exists for the content URL for a given SearchTOCItem
     * 
     * returns an empty String if no content exists.
     */
    private String getMapID(SearchTOCItem item) {
	URL url = item.getURL();
	HelpSet hs = hb.getHelpSet();
	Map map = hs.getCombinedMap();
	ID id = map.getIDFromURL(url);
	if (id == null) {
	    return "";
	}
	return id.id;
    }

    /**
     * Get a string representing the array of SearchHit's begin and end locations
     * for each hit
     * 
     * @return String a sring representation of the values
     */
    private String getSearchHits(SearchTOCItem item) {
	String retval = "{ ";
	for (Enumeration enum1 = item.getSearchHits();
	     enum1.hasMoreElements();) {
	    SearchHit info = (SearchHit) enum1.nextElement();
	    retval = retval + "{" + info.getBegin() + "," + info.getEnd() + 
		"}";
	    if (enum1.hasMoreElements()) {
		retval = retval + ", ";
	    }
	}
	retval = retval + " }";
	return retval;
    }
	

    public synchronized void itemsFound(SearchEvent e) {
	SearchTOCItem tocitem;
	Enumeration itemEnum = e.getSearchItems();
	// Iterate through each search item in the searchEvent
	while (itemEnum.hasMoreElements()) {
	    SearchItem item = (SearchItem) itemEnum.nextElement();
	    URL url;
	    try {
		url = new URL(item.getBase(), item.getFilename());
	    } catch (MalformedURLException me) {
		debug ("Failed to create URL from " + item.getBase() + "|" +
		       item.getFilename());
		continue;
	    }
	    boolean foundNode = false;

	    // see if this search item matches that of one we currently have
	    // if so just do an update
	    Enumeration nodesEnum = nodes.elements();
	    while (nodesEnum.hasMoreElements()) {
		tocitem = (SearchTOCItem) nodesEnum.nextElement();
		URL testURL = tocitem.getURL();
		if (testURL != null && url != null && url.sameFile(testURL)) {
		    tocitem.addSearchHit(new SearchHit(item.getConfidence(),
						       item.getBegin(),
						       item.getEnd()));
		    foundNode = true;
		    break;
		}
	    }

	    // No match. 
	    // OK then add a new one.
	    if (!foundNode) {
		tocitem = new SearchTOCItem(item);
		nodes.addElement(tocitem);
	    }
	}
    }

    public synchronized void searchStarted(SearchEvent e) {
	nodes = new Vector();
	searchFinished = false;
    }

    public synchronized void searchFinished(SearchEvent e) {
	searchFinished = true;
	treeEnum = nodes.elements();
	notifyAll();
    }

    private static final boolean debug = false;
    private static void debug(String msg) {
	if (debug) {
	    System.err.println("SearchTOCItemTag: "+msg);
	}
    }
}

