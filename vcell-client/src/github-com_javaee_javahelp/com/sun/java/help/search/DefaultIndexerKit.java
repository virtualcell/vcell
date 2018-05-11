/*
 * @(#)DefaultIndexerKit.java	1.17 06/10/30
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
 * @(#) DefaultIndexerKit.java 1.17 - last change made 10/30/06
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
public class DefaultIndexerKit extends IndexerKit {

    protected boolean documentStarted;


    public DefaultIndexerKit() {
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
        return new DefaultIndexerKit();
    }

    /**
     * Gets the MIME type of the data that this
     * kit represents support for.  The default
     * is <code>text/plain</code>.
     *
     * @return the type
     */
    public String getContentType() {
        return "";
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
	// Do nothing in the default implementation
    }

    public int parseIntoTokens (String source, int pos) {
	BreakIterator boundary;
	int start;
	String word;
	int currentPos = pos;

	try {
	    boundary = BreakIterator.getWordInstance(locale);
	    boundary.setText(source);
	    start = boundary.first();
	    for (int end = boundary.next();
		 end != BreakIterator.DONE;
		 start = end, end = boundary.next()) {
		word = new String(source.substring(start,end));
		word = word.trim();
		word = word.toLowerCase(locale);
		if (word.length() > 1) {
		    storeToken(word, currentPos + start);
		} else if (word.length() == 1) {
		    int charType = Character.getType(word.charAt(0));
		    if ((charType == Character.DECIMAL_DIGIT_NUMBER) || 
			(charType == Character.LETTER_NUMBER) || 
			(charType == Character.LOWERCASE_LETTER) || 
			(charType == Character.OTHER_LETTER) || 
			(charType == Character.OTHER_NUMBER) || 
			(charType == Character.TITLECASE_LETTER) || 
			(charType == Character.UNASSIGNED) || 
			(charType == Character.UPPERCASE_LETTER)) {
			storeToken (word, currentPos + start);
		    }
		}
	    }
	    currentPos += source.length();
	}
    catch (RuntimeException e) {
        throw e;
    }
	catch (Exception e) {
	    e.printStackTrace();
	}
	return currentPos;
    }

    protected void startStoreDocument (String file) throws Exception {
	if ((config == null) || (builder == null)) {
	    throw new IllegalStateException("ConfigFile and/or IndexBuilder not set");
	}
	builder.openDocument(config.getURLString(file));
    }

    protected void endStoreDocument () throws Exception {
	if ((config == null) || (builder == null)) {
	    throw new IllegalStateException("ConfigFile and/or IndexBuilder not set");
	}
	builder.closeDocument();
    }

    protected void storeToken (String token, int pos) throws Exception {
	if ((config == null) || (builder == null)) {
	    throw new IllegalStateException("ConfigFile and/or IndexBuilder not set");
	}
	if (!documentStarted) {
	    try {
		startStoreDocument(file);
		documentStarted = true;
	    } catch (Exception e) {
		if (debugFlag) e.printStackTrace();
		throw new IOException("Can't store Document");
	    }
	}
	builder.storeLocation(token, pos);
    }

    protected void storeTitle (String title) throws Exception {
	if ((config == null) || (builder == null)) {
	    throw new IllegalStateException("ConfigFile and/or IndexBuilder not set");
	}
	builder.storeTitle(title);
    }


    /**
     * Debug code
     */

    private boolean debugFlag=false;
    private void debug(String msg) {
        if (debugFlag) {
            System.err.println("DefaultIndexKit: "+msg);
        }
    }
}
