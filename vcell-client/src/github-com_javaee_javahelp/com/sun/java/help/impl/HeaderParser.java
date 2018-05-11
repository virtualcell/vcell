/*
 * @(#)HeaderParser.java	1.4 06/10/30
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
 * @(#) HeaderParser.java 1.4 - last change made 10/30/06
 */

package com.sun.java.help.impl;

/**
 * This class provides is useful for the nightmare of parsing multi-part 
 * HTTP/RFC822 headers sensibly:
 * <p>
 * <pre>
 * From a String like: 'timeout=15, max=5'
 * create an array of Strings:
 * { {"timeout", "15"},
 *   {"max", "5"}
 * }
 * From one like: 'Basic Realm="FuzzFace" Foo="Biz Bar Baz"'
 * create one like (no quotes in literal):
 * { {"basic", null},
 *   {"realm", "FuzzFace"}
 *   {"foo", "Biz Bar Baz"}
 * }
 * keys are converted to lower case, vals are left as is....
 * </pre>
 *
 * @version	1.4	10/30/06
 * @author	Dave Brown
 * @author	Roger D. Brinkley
 */ 


public class HeaderParser {

    /* table of key/val pairs - maxes out at 10!!!!*/
    String raw;
    String[][] tab;
    
    public HeaderParser(String raw) {
	this.raw = raw;
	tab = new String[10][2];
	parse();
    }

    private void parse() {
	
	if (raw != null) {
	    raw = raw.trim();
	    char[] ca = raw.toCharArray();
	    int beg = 0, end = 0, i = 0;
	    boolean inKey = true;
	    boolean inQuote = false;
	    int len = ca.length;
	    while (end < len) {
		char c = ca[end];
		if (c == '=') { // end of a key
		    tab[i][0] = new String(ca, beg, end-beg).toLowerCase();
		    inKey = false;
		    end++;
		    beg = end;
		} else if (c == '\"') {
		    if (inQuote) {
			tab[i++][1]= new String(ca, beg, end-beg);
			inQuote=false;
			do {
			    end++;
			} while (end < len && (ca[end] == ' ' || ca[end] == ','));
			inKey=true;
			beg=end;
		    } else {
			inQuote=true;
			end++;
			beg=end;
		    }
		} else if (c == ' ' || c == ',') { // end key/val, of whatever we're in
		    if (inQuote) {
			end++;
			continue;
		    } else if (inKey) {
			tab[i++][0] = (new String(ca, beg, end-beg)).toLowerCase();
		    } else {
			tab[i++][1] = (new String(ca, beg, end-beg));
		    }
		    while (end < len && (ca[end] == ' ' || ca[end] == ',')) {
			end++;
		    }
		    inKey = true;
		    beg = end;
		} else {
		    end++;
		}
	    } 
	    // get last key/val, if any
	    if (--end > beg) {
		if (!inKey) {
		    if (ca[end] == '\"') {
			tab[i++][1] = (new String(ca, beg, end-beg));
		    } else {
			tab[i++][1] = (new String(ca, beg, end-beg+1));
		    }
		} else {
		    tab[i][0] = (new String(ca, beg, end-beg+1)).toLowerCase();
		}
	    } else if (end == beg) {
		if (!inKey) {
		    if (ca[end] == '\"') {
			tab[i++][1] = String.valueOf(ca[end-1]);
		    } else {
			tab[i++][1] = String.valueOf(ca[end]);
		    }
		} else {
		    tab[i][0] = String.valueOf(ca[end]).toLowerCase();
		}
	    } 
	}
	
    }

    public String findKey(int i) {
	if (i < 0 || i > 10)
	    return null;
	return tab[i][0];
    }

    public String findValue(int i) {
	if (i < 0 || i > 10)
	    return null;
	return tab[i][1];
    }

    public String findValue(String key) {
	return findValue(key, null);
    }

    public String findValue(String k, String Default) {
	if (k == null)
	    return Default;
	k.toLowerCase();
	for (int i = 0; i < 10; ++i) {
	    if (tab[i][0] == null) {
		return Default;
	    } else if (k.equals(tab[i][0])) {
		return tab[i][1];
	    }
	}
	return Default;
    }

    public int findInt(String k, int Default) {
	try {
	    return Integer.parseInt(findValue(k, String.valueOf(Default)));
	} catch (Throwable t) {
	    return Default;
	}
    }
 }
