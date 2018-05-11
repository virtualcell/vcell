/*
 * @(#)PlainTextIndexerKit.java	1.3 06/10/30
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
 * @(#) PlainTextIndexerKit.java 1.3 - last change made 10/30/06
 */

package com.sun.java.help.search;

import java.io.*;
import java.util.Locale;
import java.text.*;
import javax.help.search.IndexerKit;
import javax.help.search.IndexBuilder;
import javax.help.search.ConfigFile;

/**
 * This is the set of things needed by an indexing object
 * to be a reasonably functioning indexer for some <em>type</em>
 * of text document.  This implementation provides a default
 * implementation which treats text as plain text 
 *
 * @author  Roger D. Brinkley
 * @version %I	%G
 */
public class PlainTextIndexerKit extends DefaultIndexerKit {

    public PlainTextIndexerKit() {
	locale = Locale.getDefault();
    }

    /**
     * Creates a copy of the indexer kit.  This
     * allows an implementation to serve as a prototype
     * for others, so that they can be quickly created.
     *
     * @return the copy
     */
    public Object clone() {
        return new PlainTextIndexerKit();
    }

    /**
     * Gets the MIME type of the data that this
     * kit represents support for.  The default
     * is <code>text/plain</code>.
     *
     * @return the type
     */
    public String getContentType() {
        return "text/plain";
    }

    /**
     * Inserts content from the given stream, which will be 
     * treated as plain text.
     * 
     * @param in  The stream to read from
     * @param file The file name being parsed
     * @param ignoreCharset Ignore the CharacterSet when parsing
     * @param builder The IndexBuilder for the full text insertion.
     * @param config The indexer configuration information
     * @exception IOException on any I/O error
     */
    public void parse(Reader in, String file, boolean ignoreCharset,
		      IndexBuilder builder, 
		      ConfigFile config) throws IOException 
    {
	debug ("parsing " + file);
			  
	this.builder = builder;
	this.config = config;
	this.file = file;
	documentStarted = false;
	int currentPos = 1;

        char[] buff = new char[4096];
        int nch;
        while ((nch = in.read(buff, 0, buff.length)) != -1) {
	    currentPos = parseIntoTokens(new String(buff), currentPos);
        }

	try {
	    storeTitle("No Title");
	    endStoreDocument();
	} catch (Exception e2) {
	    throw new IOException("Can't store title");
	}

	this.builder = null;
	this.config = null;
    }


    /**
     * Debug code
     */

    private boolean debugFlag=false;
    private void debug(String msg) {
        if (debugFlag) {
            System.err.println("PlainTextIndexerKit: "+msg);
        }
    }
}
