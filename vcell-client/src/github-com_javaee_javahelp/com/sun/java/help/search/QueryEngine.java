/*
 * @(#)QueryEngine.java	1.25 06/10/30
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
 * @version	1.25	10/30/06
 */

package com.sun.java.help.search;

import java.text.BreakIterator;
import java.util.Vector;
import java.util.Locale;
import java.util.Enumeration;
import java.lang.reflect.Method;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.net.URL;
import javax.help.search.SearchQuery;
import javax.help.HelpUtilities;

/**
 * This class is the initial interface into the search engine. It can be 
 * run as a standalone search engine or instantiated as a class.
 *
 * @author Jacek R. Ambroziak
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version	1.25	10/30/06
 */

public class QueryEngine
{
    private SearchEnvironment _env;

    public QueryEngine(String indexDir, URL hsBase) throws Exception {
	_env = new SearchEnvironment(indexDir, hsBase);
    }

    public void processQuery(String query, Locale l, SearchQuery searchQuery) throws Exception
    {
	BreakIterator boundary;
	int start;
	String term;
	Vector ids = new Vector();;
	LiteMorph morph = getMorphForLocale(l);
	int col=-1;

	try {
	    boundary = BreakIterator.getWordInstance(l);
	    boundary.setText(query);
	    start = boundary.first();
	    for (int end = boundary.next();
		 end != BreakIterator.DONE;
		 start = end, end = boundary.next()) {
		term = new String(query.substring(start,end));
		term = term.trim();
		term = term.toLowerCase(l);
		if (term.length() > 1) {
		    col += 1;
		    int id = _env.fetch(term);
		    if (id > 0) {
			ids.addElement(new SearchIds(col, id, 0.0));
		    }
		    if (morph != null) {
			String [] morphs = morph.variantsOf(term);
			for (int i=0; i < morphs.length ; i++) {
			    int id2 = _env.fetch(morphs[i]);
			    if (id2 > 0) {
				ids.addElement(new SearchIds(col, id2, 0.1));
			    }
			}
		    }
		} else if (term.length() == 1) {
		    int charType = Character.getType(term.charAt(0));
		    if ((charType == Character.DECIMAL_DIGIT_NUMBER) || 
			(charType == Character.LETTER_NUMBER) || 
			(charType == Character.LOWERCASE_LETTER) || 
			(charType == Character.OTHER_LETTER) || 
			(charType == Character.OTHER_NUMBER) || 
			(charType == Character.TITLECASE_LETTER) || 
			(charType == Character.UNASSIGNED) || 
			(charType == Character.UPPERCASE_LETTER)) {
			col += 1;
			int id = _env.fetch(term);
			if (id > 0) {
			    ids.addElement(new SearchIds(col, id, 0.0));
			}
			if (morph != null) {
			    String [] morphs = morph.variantsOf(term);
			    for (int i=0; i < morphs.length ; i++) {
				int id2 = _env.fetch(morphs[i]);
				if (id2 > 0) {
				    ids.addElement(new SearchIds(col, id2, 0.1));
				}
			    }
			}
		    }
		}
	    }
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	int size = ids.size();
	Search search = new Search(_env, col + 1);
	IntegerArray children = new IntegerArray();

	// add the terms and any children of a given term to the list of 
	// searched items. Penalize the children slightly
	for (int i = 0; i < size; i++)
	    {
		SearchIds id = (SearchIds) ids.elementAt(i);
		search.addTerm(id.col, id.concept, id.score, 0);
		children.clear();
		_env.getChildren(id.concept, children);
		if (children.cardinality() > 0)
		    for (int j = 0; j < children.cardinality(); j++)
			{
			    search.addTerm(id.col, children.at(j), 
					   id.score + 0.1, 0);
			    // appending (grand)+children
			    //!!! as it is too many duplicates are added
			    _env.getChildren(children.at(j), children);
			}
	    }

	search.startSearch(searchQuery);
    }

    private LiteMorph getMorphForLocale(Locale l) {
	// In the event that nolocale has been defined try English
	if (l == null) {
	    l = Locale.ENGLISH;
	}
	//Try to find a locale version of LiteMorph
	Enumeration enum1 = HelpUtilities.getCandidates(l);
	String front = "com.sun.java.help.search.LiteMorph";
	ClassLoader cl = QueryEngine.class.getClassLoader();
	while (enum1.hasMoreElements()) {
	    String tail = (String) enum1.nextElement();
	    String name = new String(front + tail);
	    try {
		Class klass;
		if (cl == null) {
		    klass = Class.forName(name);
		} else {
		    klass = cl.loadClass(name);
		}
		Method method = klass.getMethod ("getMorph", 
						 (java.lang.Class[]) null);
		return (LiteMorph) method.invoke(null, 
						 (java.lang.Object[]) null);
	    } catch (Exception e) {
		continue;
	    }
	}
	// couldn't find a match 
	return null;
    }


    public static void main(String[] args)
    {
	try {
	    // BufferedReader is needed for readLine() method
	    BufferedReader in =
		// but we'll make it a degenerate buffer of 1
		// to workaround a problem with some PC implementation
		// which wouldn't start to read before a lot of chars
		// were typed
		new BufferedReader(new InputStreamReader(System.in), 1);
	    String file = new String (args[0]);
	    QueryEngine qe = new QueryEngine(file, null);
	    System.out.println("initialized; enter query");
	    while(true)
		{
		    String line = in.readLine();
		    if (line.equals("."))
			break;
		    else
		      {
			long start = System.currentTimeMillis();
			qe.processQuery(line, Locale.getDefault(), null);
			System.out.println((System.currentTimeMillis()-start)
					   +" msec search");
		      }
		    System.out.println("enter next query or . to quit");
		}
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private class SearchIds {
	public int col;
	public int concept;
	public double score;

	public SearchIds (int col, int concept, double score) {
	    this.col = col;
	    this.concept = concept;
	    this.score = score;
	}

	public String toString() {
	    return "col=" + col + " concept=" + concept + " score=" + score;
	}
    }
}
