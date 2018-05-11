/*
 * @(#)Rule.java	1.3 06/10/30
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
 * @(#) Rule.java 1.3 - last change made 10/30/06
 */

package com.sun.java.help.search;

import java.util.Vector;
import java.util.StringTokenizer;

/**
 * A Rule matches a pattern at the right ends of a word, removes an indicated
 * number of characters from the end to produce a stem, and generate a list
 * of alternative forms of the word by adding each of a specified list of
 * endings to the stem.  
 * <p>
 * Each rule specifies an ending pattern and a list
 * of endings to be added to the stem to produce different variant forms
 * of the input word.  The ending pattern consists of a string of letters
 * or letter groups separated by spaces to be matched against corresponding
 * letters at the end of the word.  
 * <ul>
 * <li>
 * A letter group (e.g., aeiou) will match any of the letters in the group.  
 * <li> 
 * A letter group prefixed with a period (.) may be matched anywhere 
 * preceding the match of its subsequent letter group.  A letter group 
 * of this type is referred to as "unanchored." 
 * <li>
 * A group that is to be matched at a specified position is "anchored." 
 * <li>
 * A plus sign (+) in the pattern, in place of a letter group, marks the
 * point in the pattern after which the matching letters will be removed
 * to form the stem.  There should be no unanchored letter groups after
 * the plus sign, and there should be at most one plus sign in the pattern
 * (otherwise only the leftmost will count).  
 * <li>
 * An ampersand (&) in place of
 * a letter group in an ending pattern will match a letter that is the same
 * as its preceeding letter in the word.  
 * <li>
 * An ampersand in the list of alternative endings indicates a repeat of
 * the letter that ends the stem. 
 * <li>
 * An under bar (_) as an alternative ending indicates that nothing is to
 * be added to the stem for that alternative.  
 * <li>
 * An ending beginning with an
 * asterisk (*) indicates that the rules are to be reapplied recursively to
 * the form obtained from using this ending.
 * </ul>
 * <p>
 * Rule(s) are grouped in blocks and labeled (usually by a common final
 * sequence) and are ordered within each group so that after a matching
 * rule is found no further rules are to be tried (except when invoked
 * explicitly on a new word by a redo (*) operator in an alternative ending).
 *
 * @author Roger D. Brinkley
 * @author Jacek Ambroziak
 * @version	1.3	10/30/06
 *
 * @see LiteMorph
 */

public class Rule {
    private String[] pattern;
    private int killnum=0;
    private String[] expansions;
    private Vector words;
    private LiteMorph morph;

    /**
     * Create a Rule
     * @param expression A String representing the ending patern described previously.
     * @param expansionString A String of space delimeted expansions as described previously.
     */
    public Rule(String expression, String expansionString, LiteMorph morph) {
	String chars;
	boolean passedPlus = false;

	this.morph = morph;

	//set up pattern array:

	if (expression.length() > 0) {
	    Vector patternBuffer = new Vector(expression.length());
	    StringTokenizer temp = new StringTokenizer(expression, " \t\n\r");
	    while (temp.hasMoreTokens()) {
		if (passedPlus) {
		    // count number of characters after +
		    killnum++; 
		}
		chars = temp.nextToken();
		if (chars.equals("+")) {
		    passedPlus = true;
		} else {
		    patternBuffer.addElement(chars);
		}
	    }
	    pattern = new String[patternBuffer.size()];
	    patternBuffer.copyInto(pattern);
	}
	else pattern =  new String[0];

	//set up expansions array:
	if (expansionString.length() > 0) {
	    Vector expansionsBuffer = new Vector(expansionString.length());
	    StringTokenizer temp = new StringTokenizer(expansionString, ", \t\n\r");
	    while (temp.hasMoreTokens()) {
		expansionsBuffer.addElement(temp.nextToken());
	    }
	    expansions = new String[expansionsBuffer.size()];
	    expansionsBuffer.copyInto(expansions);
	}
	else {
	    expansions = new String[0];
	}
    }
    
    /**
     * Determines if a word matches the rule
     */
    public String [] match(String word, int depth, int skipnum) {
	words = new Vector();
	boolean matched = true;
	
	//skipnum positions have already been
	//tested by the dispatch method
	int position = word.length()-1-skipnum;
	int i = pattern.length-1-skipnum;
	while (i > -1) {
	    debug("   trying "+pattern[i]+" at "+position+
		  " for i = "+i);
	    // There isn't anything left to test
	    // the match failed
	    if (position<0) {
		matched = false;
		break;
	    }

	    //"&" match duplicate of previous letter
	    if (pattern[i].equals("&")) {
		if (position < 1 || word.charAt(position) != word.charAt(position-1)) {
		    matched = false;
		    break;
		}
		else i--;
	    }

	    //"." pattern can match anywhere
	    else if (pattern[i].startsWith(".")) { 
		if (pattern[i].indexOf(word.charAt(position), 1) >= 0) {
		    //it matches here, so go to next pattern element
		    i--; 
		}
	    } else if (pattern[i].indexOf(word.charAt(position))<0) {
		// doesn't match here
		matched = false;
		break;
	    } else  {
		i--;
	    }
	    position--;
	}

	// All done with the compares. If we've got a match then
	// build the list words from the expansion list
	if (matched)
	    {
		String stem = word.substring(0, word.length() - killnum);
		for (i = 0; i < expansions.length; i++) {
		    makeForm(stem, expansions[i], depth);
		}
	    }
	String[] result = new String[words.size()];
	words.copyInto(result);
	words = null;
	return result;
    }

    
    private void makeForm(String stem, String expansion, int depth) {
	switch (expansion.charAt(0)) {
	case '_':
	    // just use the stem; nothing to add
	    words.addElement(stem);
	    break;
	
	case '&':
	    // double last letter of stem
	    words.addElement(stem + stem.charAt(stem.length() - 1) + expansion.substring(1));
	    break;
	
	case '*':
	    // redo MorphWord on the resulting form
	    debug(" starting redo: with "+stem+" + "+expansion+
		  " from depth "+depth);
	    if (expansion.charAt(1) == '_')
		morph.morphWord(stem, depth + 1);
	    else
		morph.morphWord(stem + expansion.substring(1), depth + 1);
	    break;

	default:
	    words.addElement(stem + expansion);
	    break;
	}
    }
    /**
     * For printf debugging.
     */
    private static final boolean debugFlag = false;
    private static void debug(String str) {
        if( debugFlag ) {
            System.out.println("Rule: " + str);
        }
    }
}
  
