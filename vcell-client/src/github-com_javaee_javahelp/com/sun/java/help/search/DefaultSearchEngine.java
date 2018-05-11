/*
 * @(#)DefaultSearchEngine.java	1.21 06/10/30
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
 * @(#) DefaultSearchEngine.java 1.21 - last change made 10/30/06
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
 * DefaultSearchEngine is the default search engine. 
 *
 * Search results are returned through SearchEvents to
 * listeners that
 * register with a SearchEngine instance through the Search Query.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version	1.21	10/30/06
 *
 * @see javax.help.search.SearchEngine
 * @see javax.help.search.SearchEvent
 * @see javax.help.search.SearchListener
 */
public class DefaultSearchEngine extends SearchEngine {

    private String urldata;	// just for debugging really
    protected QueryEngine qe;

    /**
     * Create a DefaultSearchEngine 
     */
    public DefaultSearchEngine(URL base, Hashtable params) 
	throws InvalidParameterException
    {
	super(base, params);

	debug("Loading Search Database");
	debug("  base: "+base);
	debug("  params: "+params);

	// Load the Query Engine and Search DB here
	try {
	    urldata = (String) params.get("data");
	    qe = new QueryEngine(urldata, base);
	} catch (Exception e) {
	    if (debugFlag) {
		System.err.println(" =========== ");
		e.printStackTrace();
	    }
	    throw new InvalidParameterException();
	}
    }

    public SearchQuery createQuery() {
	return new DefaultSearchQuery(this);
    }

    protected QueryEngine getQueryEngine() {
	return qe;
    }

    /**
     * For printf debugging.
     */
    private static final boolean debugFlag = false;
    private static void debug(String str) {
        if( debugFlag ) {
            System.out.println("DefaultSearchEngine: " + str);
        }
    }

}
