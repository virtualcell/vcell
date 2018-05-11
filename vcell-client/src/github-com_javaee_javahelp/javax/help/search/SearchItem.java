/*
 * @(#)SearchItem.java	1.21 06/10/30
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

import java.util.Vector;
import java.util.Enumeration;
import java.net.URL;

/**
 * A SearchItem corresponds to one specific item found in a search query.
 * SearchItems are used in the default Search navigator.
 * 
 * @see javax.help.SearchTOCItem
 * @see javax.help.search.SearchEvent
 * @see javax.help.search.SearchListener
 *
 * @author Roger D. Brinkley
 * @version	1.21	10/30/06
 */

public class SearchItem { 
    private URL base;
    private String title;
    private String lang;
    private String filename;
    private double confidence; 
    private int begin;
    private int end;
    private Vector concepts;

    /**
     * Constructs a SearchItem
     *
     * @param base The URL to the base from which file is a spec.
     * @param title Title of the item
     * @param lang A string representation of the locale. A null lang is valid
     * and represents the default locale.
     * @param filename FileName for the item.
     * @param confidence How closely this matches the params.
     * @param begin Starting position where the (requested) match has been found.
     * @param end Ending position.
     * @param concepts A vector of concepts.
     */
    public SearchItem(URL base,
		      String title, String lang, String filename,
		      double confidence,
		      int begin, int end, Vector concepts) {
	if (base == null) {
	    throw new NullPointerException("base");
	}
	this.base = base;
	if (title == null) {
	    throw new NullPointerException("title");
	}
	this.title = title;
	this.lang = lang;
	if (filename == null) {
	    throw new NullPointerException("fileName");
	}
	this.filename = filename;
	this.confidence = confidence;
	this.begin = begin;
	this.end = end;
	if (concepts == null) {
	    throw new NullPointerException("concepts");
	}
	this.concepts = concepts;
    }

    /**
     * Gets the base of the SearchItem.
     *
     * @return The base for this SearchItem.  Should be used with filename to
     * 	obtain a URL to the desired hit.
     */
    public URL getBase() {
	return base;
    }

    /**
     * Gets the title of the SearchItem.
     *
     * @return The title of the document.  Used to present the hit in the navigator.
     */
    public String getTitle() {
	return title;
    }

    /**
     * Gets the lang of the SearchItem.
     *
     * @return The title of the document.  Used to present the hit in the navigator.
     */
    public String getLang() {
	return lang;
    }

    /**
     * Gets the spec (as a URL relative to getBase() ) to the document.
     *
     * @return The spec, relative to getBase(), to the document containing the hit.
     */
    public String getFilename() {
	return filename;
    }
    
    /**
     * Gets the confidence value for the hit.
     *
     * @return The confidence value for the hit.  
     * This measures how "good" the hit is. The lower the value the better.
     */
    public double getConfidence() {
	return confidence;
    }

    /**
     * Gets the begin pointer position for the hit.
     *
     * @return The starting position for the area in the document where a hit is found.
     */
    public int getBegin() {
	return begin;
    }

    /**
     * Gets the ending pointer position.
     * 
     * @return The ending position for the area in the document where a hit is found.
     */
    public int getEnd() {
	return end;
    }

    /**
     * If there are "concepts" against which the query is made, this is an enumeration
     * of the concepts.  Otherwise null.
     *
     * @return An enumeration of the concepts found in this query.
     */
    public Enumeration getConcepts() {
	return concepts.elements();
    }

    public String toString() {
	StringBuffer result;
	result = new StringBuffer(confidence + " " + title + ":" + 
				  base + filename + " [" + begin + "," + end + 
				  "], {");
	if (concepts == null) {
	    result.append("}");
	    return result.toString();
	}
	Enumeration enum1 = concepts.elements();
	while(enum1.hasMoreElements()) {
	    String concept = (String)enum1.nextElement();
	    result.append(concept);
	    if (enum1.hasMoreElements()) {
		result.append(",");
	    }
	}
	result.append("}");
	return result.toString();
    }
}
