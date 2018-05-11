/*
 * @(#)SearchTOCItem.java	1.9 06/10/30
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
 * @(#) SearchTOCItem.java 1.9 - last change made 10/30/06
 */

package javax.help;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.Vector;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Locale;
import javax.help.Map.ID;
import javax.help.search.SearchItem;

/**
 * Stores Search TOC items. This class extends TOCItems with additonal
 * search hits. Can be used as part of the TOC tree or as an appendage to
 * the tree for items not contained in the tree.
 *
 * @author Roger D. Brinkley
 * @version   1.9     10/30/06
 */

public class SearchTOCItem extends TOCItem {

    private URL url;
    private Vector sivec;
    private boolean inTOC;
    private double confidence;

    public SearchTOCItem(ID id, ID imageID, HelpSet hs, Locale locale) {
	super (id, imageID, hs, locale);
	inTOC = true;
	this.url = null;
	sivec = new Vector();
	confidence = 0;
    }

    // SearchItem should handle Locale, no Lang
    public SearchTOCItem(SearchItem item) {
	super (null, null, null, HelpUtilities.localeFromLang(item.getLang()));
	inTOC = false;
	setName(item.getTitle());
	try {
	    this.url = new URL(item.getBase(), item.getFilename());
	} catch (MalformedURLException me) {
	    this.url = null;
	}
	sivec = new Vector();
	SearchHit info = new SearchHit(item.getConfidence(),
					 item.getBegin(),
					 item.getEnd());
	confidence = item.getConfidence();
	sivec.addElement(info);
    }

    /**
     * Adds a SearchHit.
     */
    public void addSearchHit(SearchHit si) {
	// these are in sorted order so look at the bottom
	// entry first
	//
	// This sorted order needs to be looked at for FCS
	if (sivec.isEmpty()) {
	    sivec.addElement(si);
	    confidence = si.getConfidence();
	} else {
	    for (int i = sivec.size() - 1; i >= 0 ; i--) {
		SearchHit test = (SearchHit)sivec.elementAt(i);
		if (test.getConfidence() <= si.getConfidence()) {
		    sivec.insertElementAt(si, i+1);
		    break;
		}
		if (i == 0) {
		    sivec.insertElementAt(si, 0);
		    confidence = si.getConfidence();
		}
	    }
	}
    }

    public URL getURL() {
	return url;
    }

    public double getConfidence() {
	// The confidence level is the max confidence value
	// The max value is determined at the time a Search Hit is
	// added
	return confidence;
    }

    public Enumeration getConfidences() {
	return new Enumeration() {
	    int count = 0;
		
	    public boolean hasMoreElements() {
		return count < sivec.size();
	    }
		
	    public Object nextElement() {
		synchronized (sivec) {
		    if (count < sivec.size()) {
			return new Double(((SearchHit)sivec.elementAt(count++)).getConfidence());
		    }
		}
		throw new NoSuchElementException("Vector Enumeration");
	    }
	};
    }

    public boolean inTOC() {
	return inTOC;
    }

    public Enumeration getSearchHits() {
	return sivec.elements();
    }

    public int hitCount() {
	return sivec.size();
    }
}

