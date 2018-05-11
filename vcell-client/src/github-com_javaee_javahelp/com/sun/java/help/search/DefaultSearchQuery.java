/*
 * @(#)DefaultSearchQuery.java	1.7 06/10/30
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
 * @(#) DefaultSearchQuery.java 1.7 - last change made 10/30/06
 */

package com.sun.java.help.search;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.EventListener;
import java.util.Locale;
import javax.help.search.*;
import com.sun.java.help.search.*;
import java.security.InvalidParameterException;

/**
 * DefaultSearchQuery is the query using the default search engine. 
 *
 * Search results are returned through SearchEvents to
 * listeners that register with this instance.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version	1.7	10/30/06
 *
 * @see javax.help.search.SearchEngine
 * @see javax.help.search.SearchQuery
 * @see javax.help.search.SearchEvent
 * @see javax.help.search.SearchListener
 */
public class DefaultSearchQuery extends SearchQuery implements Runnable {

    private Thread thread = null;
    private DefaultSearchEngine dhs;

    /**
     * Create a DefaultSearchEngine 
     */
    public DefaultSearchQuery(SearchEngine hs) {
	super(hs);
	if (hs instanceof DefaultSearchEngine) {
	    dhs = (DefaultSearchEngine) hs;
	}
    }


    /**
     * Starts the search. The implementation is up to subclasses of SearchEngine.
     * This method will invoke searchStarted on SearchListeners.
     * @exception IllegalArgumentException The parameters are not 
     * understood by this engine
     * @exception IllegalStateException There is an active search in progress in this instance
     */
    public void start(String searchparams, Locale l) 
	 throws IllegalArgumentException, IllegalStateException 
    {
	debug ("Starting Search");
	if (isActive()) {
	    throw new IllegalStateException();
	}

	// initialization
	super.start(searchparams, l);

	// Actually do the search
	thread = new Thread(this, "QueryThread");
	thread.start();
    }

    /**
     * Stops the search. The implementation is up to the subcalsses of 
     * SearchEngine. This method will invoke searchStopped on 
     * SearchListeners.
     */
    public void stop() throws IllegalArgumentException, IllegalStateException {
	debug ("Stop Search");
	// Can no longer do a stop
	// Let it continue to operate until it's completed
	// on it's own. This is due to to the enherent problem 
	// with thread.stop
    }

    public boolean isActive() {
	if (thread == null) { 
	    return false;
	}
	return thread.isAlive();
    }

    public void run() throws IllegalArgumentException{
	QueryEngine qe = dhs.getQueryEngine();
	try {
	    qe.processQuery(searchparams, l, this);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new IllegalArgumentException();
	}
	fireSearchFinished();
	thread = null;
    }

    /**
     * For printf debugging.
     */
    private static final boolean debugFlag = false;
    private static void debug(String str) {
        if( debugFlag ) {
            System.out.println("DefaultSearchQuery: " + str);
        }
    }

}
