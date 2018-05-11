/*
 * @(#)LiteMorph.java	1.7 06/10/30
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
 * @(#) LiteMorph.java 1.7 - last change made 10/30/06
 */

package com.sun.java.help.search;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * This class will generate an array of morphological variants of a word
 * to use in search-and-retrieval applications where a user wants to find 
 * other words morphologically related to words in a query.  For example, 
 * a request such as "electrical fixtures" should also retrieve "electric
 * fixtures," "electrical fixture," etc.  Given a word of a query, these 
 * rules generate alternative variations of that word that should also be 
 * considered.  This generation of variant forms of a word fills a role 
 * similar to that often filled by the use of wild card characters or by 
 * stemming rules that produce truncated stems in traditional information
 * retrieval systems.  The approach of generating alternative variations
 * has advantages over a truncated stemming approach for many applications,
 * because it does not require stemming operations during the indexing
 * process, does not require extra indexing space for stems, nor does it
 * lose information by storing only stems.  Rather, the variation rules
 * are applied to the query to produce additional forms to check against
 * the index.
 * <p>
 * Compared to the use of wild card characters, this approach has two 
 * advantages: first, it does not require the user to think about where 
 * the wild cards should be placed, and secondly, it deals with irregular
 * variations such as irregular verbs (e.g., "break," "broke," "broken"),
 * and with stem ending effects such as silent e's and doubling of final 
 * consonants (e.g., "dig," "digs," "digging").  The rules presented here, 
 * together with a table of exceptions, provided at the end, deal with 
 * a wide range of such effects, without requiring any more attention on 
 * the part of the user than to turn on the facility.
 * <p>
 * These rules generate regular morphological variants of words using the 
 * suffixes s, er, ers, est, ed, ing, ly, ful, less, ness, and ment.  Rules 
 * are included for dealing with some words ending in -ize, -ise, -ic and 
 * -ical, and for some words requiring irregular forms, such as -leaf and
 * -man compounds (flyleaf, footman), and Latin words ending in -um and -a, 
 * such as datum.  The rules are supplemented by a list of exceptions for
 * words that do not inflect regularly.  They are not intended to apply to
 * function words or to proper names.  When expanding queries, you may not
 * want to apply them to capitalized words or to hyphenated words like
 * day-to-day and low-level.
 * <p>
 * The rules treat almost all words as if they were multiply meaningful
 * as nouns, verbs, and adjectives.  Hence, the rules will often generate
 * spurious forms that should never occur in a text -- e.g., fix ->
 * fixest, happy -> happied.  The rules are suitable for applications
 * such as searching text using inverted files, where a quick test
 * suffices to determine that a given candidate does not occur in the
 * corpus.  In such applications, it is preferable to overgenerate
 * candidates than to miss possible retrievals.
 * <p>
 * The program uses rules developed by W. A. Woods and Ellen Hays in 1992.
 * An original C program for using them was developed by Jacek Ambroziak
 * and was included in Sun's SearchIt product.
 *
 * @author Roger D. Brinkley
 * @author W. A. Woods
 * @author Jacek Ambroziak
 * @version	1.7	10/30/06
 * 
 * @see Rule
 */

public abstract class LiteMorph {

    protected static Vector variants;
    protected static Hashtable rulesTable;
    protected static Hashtable blockedVariants;
    protected static Hashtable exceptions;

    public LiteMorph() {
	initialize();
    }


    public static LiteMorph getMorph() {
	return null;
    }

    /**
     * Subclasses of this class (generally locale specific)
     * need to set up exceptions and rules. At a minium 
     * implementations
     * need to initialize the size of the exceptions HashTable
     * and the establish the rules HashTable.
     * Implementations have the option of filling exceptions
     * directly in this method or calling intialize(String []).
     * After initialization the exceptionTable shoudl be garbage
     * collected
     */
    protected abstract void initialize();

    /**
     * One time initialization of exceptions Hashtable using an
     * array of Strings. Each String is a list of variation groups.
     * The words in the groups are  space delimited. Any matching
     * word in the exceptions will cause all of the words in the
     * group to be added to the variant list
     */
    protected void initialize (String [] exceptionTable) {

	// Firewall
	if (exceptions == null || exceptionTable == null) {
	    return;
	}
	String tempWord, tempVal;
	for (int i = 0; i < exceptionTable.length; i++) {
	    StringTokenizer tokens = new StringTokenizer(exceptionTable[i], " ");
	    while (tokens.hasMoreTokens()) {
		tempWord = tokens.nextToken();
		tempVal = (String)exceptions.get(tempWord);
		if (tempVal == null) {
		    exceptions.put(tempWord, exceptionTable[i]);
		} else {
		    //the same form can occur in several groups that must be appended
		    exceptions.put(tempWord, tempVal + " " + exceptionTable[i]);
		}
	    }
	}
    }

    /**
     * Get the variants of given word. This is locale
     * specific variants of a word as supplied by the locale
     * implementation of this class
     * 
     * @return String[] an array of words that are variations of word
     */
    public synchronized String[] variantsOf(String word) {

	// intialize variants and blockedVariants
	variants = new Vector();
	blockedVariants = new Hashtable ();
	// this blocks adding the input word itself
	blockedVariants.put(word, word);
	    
	    // Go get the morphological variantes of the word.
	morphWord(word, 0);
	    
	// don't need this anymore; release it for gc
	blockedVariants = null; 
	String[] result = new String[variants.size()];
	variants.copyInto(result);
	//release this for garbage collection
	variants = null;
	return result;
    }
  
    /**
     * Morph the word into other words if possible
     */
    protected void morphWord(String word, int depth) {

	debug(" analyzing: " +word+" at depth "+depth);

	if (depth > 2)
	    return;

	// if a word is found among exceptions, don't try rules

	String exceptionList = (String)exceptions.get(word);
	if (exceptionList == null) {
	    exceptionList = "";
	}
	if (exceptionList.length() > 0) {
	    StringTokenizer tokens = new StringTokenizer(exceptionList, " ");
	    while (tokens.hasMoreTokens())
		addVariant(tokens.nextToken());
	    debug("   "+word+": found match in exceptions -- "+
		  exceptionList+", at depth "+depth);
	    return;
	}
    
	if (word.indexOf("-") >= 0)
	    return;
	//don't apply rules to words with internal hyphens (but check exceptions)

	Rule[] rules = null;
	int skipnum = 0;

	// See if the word ends with one of the keys in the rulesTable
	Enumeration keys = rulesTable.keys();
	while (keys.hasMoreElements()) {
	    String key = (String) keys.nextElement();
	    if (word.endsWith(key) && !key.equals("default")) {
		rules = (Rule[]) rulesTable.get(key);
		skipnum = key.length();
		break;
	    }
	}
	if (rules == null) {
	    // no match try to get the "default" rules.
	    rules = (Rule[]) rulesTable.get("default");
	    skipnum = 0;
	}


	for (int i = 0; i < rules.length; i++) {
	    debug("  "+word+": trying rule: " + rules[i]+
		  ", at depth "+depth);
	    String [] results = rules[i].match(word, depth, skipnum);
	    if (results.length > 0) {
		debug("  "+word+": found match for: "+rules[i]+
		      ", at depth "+depth);
		addVariant(word); //do this here -- i.e., only when a rule matches
		for (int j=0; j < results.length; j++) {
		    addVariant(results[j]);
		}
		break;
	    }
	}
    }
  
    /**
     * Add the variant of the word to the list of words
     */
    private void addVariant(String word) {
	if (blockedVariants.get(word) == null) { // word is not blocked
	    variants.addElement(word);
	    blockedVariants.put(word, word); // block it from being added again
	}
    }
  
    /**
     * Convenience method for creating Rules
     */
    protected static Rule r(String expression, String expansions, LiteMorph morph) {
	return new Rule(expression, expansions, morph);
    }

    /**
     * For printf debugging.
     */
    private static final boolean debugFlag = false;
    private static void debug(String str) {
        if( debugFlag ) {
            System.out.println("LiteMorph: " + str);
        }
    }
}
