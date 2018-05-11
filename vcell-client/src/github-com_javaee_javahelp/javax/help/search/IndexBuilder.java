/*
 * @(#)IndexBuilder.java	1.8 06/10/30
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
 * @(#) IndexBuilder.java 1.8 - last change made 10/30/06
 */

package javax.help.search;

import java.io.*;
import java.util.Enumeration;

/**
 * Abstract base class that builds an index for a search database.
 *
 * @author Roger D. Brinkley
 * @version	1.8	10/30/06
 */

public abstract class IndexBuilder
{

    protected String indexDir;

    /**
     * Builds an index at indexDir. If indexDir already exists
     * the index is opened and the new doucments are merged into
     * the existing document.
     */
    public IndexBuilder(String indexDir) throws Exception
    {
	debug("indexDir=" + indexDir);
	this.indexDir = indexDir;
	File test = new File(indexDir);	
	try {
	    if (!test.exists()) {
		debug ("file " + indexDir + " didn't exist - creating");
		test.mkdirs();
	    }
	} catch (java.lang.SecurityException e) {
	}
    }

    /**
     * Closes the index. 
     */
    public abstract void close() throws Exception;

    /**
     * Sets the stopwords in an index. If the stopwords are already 
     * defined for an index, the stop words are merged with the existing
     * set of stopwords.
     * @params stopWords An Enumeration of Strings.
     */
    public abstract void storeStopWords(Enumeration stopWords);

    /**
     * Returns the list of stopwords for an index.
     * @returns Enumeration An enumeration of Strings. Returns null if there are no stopwords.
     */
    public abstract Enumeration getStopWords();

    /**
     * Opens a document to store information.
     */
    public abstract void openDocument(String name) throws Exception;
  
    /**
     * Closes the document. This prevents any additional information from being
     * stored.
     */
    public abstract void closeDocument() throws Exception;

    /**
     * Stores a concept at a given position.
     */
    public abstract void storeLocation(String text, int position) throws Exception;
    
    /**
     * Stores the title for the document.
     */
    public abstract void storeTitle(String title) throws Exception;

    private boolean debug=false;
    private void debug(String msg) {
	if (debug) {
	    System.err.println("IndexBuilder: "+msg);
	}
    }

}
