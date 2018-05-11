/*
 * @(#)Parser.java	1.13 06/10/30
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
 * @(#) Parser.java 1.13 - last change made 10/30/06
 */

package com.sun.java.help.impl;

/**
 * This class parses an HTML or XML document.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version	1.13	10/30/06
 */
import java.util.Vector;
import java.io.Reader;
import java.io.InputStream;
import java.util.EventListener;

public class Parser extends DocumentParser {
    protected ParserListener listenerList;

    public Parser(Reader src) {
        super(src);
    }

    public Parser(InputStream in) {
        super(in);
    }

    protected void tag(String name, TagProperties atts, boolean endTag, boolean emptyTag) {
	Tag tag = new Tag(name, atts, endTag, emptyTag);
	listenerList.tagFound(new ParserEvent(this, tag));
    }

    protected void pi(String target, String data) {
	listenerList.piFound(new ParserEvent(this, target, data));
    }

    protected void doctype(String root, String publicId, String systemId) {
	listenerList.doctypeFound(new ParserEvent(this, root, publicId, systemId));
    }


    protected void flush(char [] buf, int offset, int length) { 
	if (length == 1 &&((buf[offset] == DocPConst.NEWLINE) ||
			   (buf[offset] == DocPConst.RETURN))) {
	    return;
	}
	String text = new String(buf, offset, length);
	listenerList.textFound(new ParserEvent(this, text));
    }

    protected void comment(String s) { 
	listenerList.commentFound(new ParserEvent(this, s));
    }

    protected void errorString(String s) { 
	listenerList.errorFound(new ParserEvent(this, s));
    } 

    // Not need for our version of Parser
    protected String documentAttribute(String name) {return null;}

    public void addParserListener(ParserListener l) {
        listenerList = ParserMulticaster.add(listenerList, l);
    }

    public void removeParserListener(ParserListener l) {
        listenerList = ParserMulticaster.remove(listenerList, l);
    }

    static protected class ParserMulticaster implements ParserListener {
	protected final EventListener a, b;

	protected ParserMulticaster(EventListener newA, EventListener newB) {
	    this.a = newA; this.b = newB;
	}

	protected EventListener remove(EventListener oldl) {
	    if (oldl == a)  return b;
	    if (oldl == b)  return a;
	    EventListener a2 = removeInternal(a, oldl);
	    EventListener b2 = removeInternal(b, oldl);
	    if (a2 == a && b2 == b) {
		return this;	// it's not here
	    }
	    return addInternal(a2, b2);
	}

	protected static EventListener addInternal(EventListener a, EventListener b) {
	    if (a == null)  return b;
	    if (b == null)  return a;
	    return new ParserMulticaster(a, b);
	}

	protected static EventListener removeInternal(EventListener l, EventListener oldl) {
	    if (l == oldl || l == null) {
		return null;
	    } else if (l instanceof ParserMulticaster) {
		return ((ParserMulticaster)l).remove(oldl);
	    } else {
		return l;		// it's not here
	    }
	}

	public void tagFound(ParserEvent e) {
	    ((ParserListener)a).tagFound(e);
	    ((ParserListener)b).tagFound(e);
	}

	public void piFound(ParserEvent e) {
	    ((ParserListener)a).piFound(e);
	    ((ParserListener)b).piFound(e);
	}

	public void doctypeFound(ParserEvent e) {
	    ((ParserListener)a).doctypeFound(e);
	    ((ParserListener)b).doctypeFound(e);
	}

	public void textFound(ParserEvent e) {
	    ((ParserListener)a).textFound(e);
	    ((ParserListener)b).textFound(e);
	}

	public void commentFound(ParserEvent e) {
	    ((ParserListener)a).commentFound(e);
	    ((ParserListener)b).commentFound(e);
	}

	public void errorFound(ParserEvent e) {
	    ((ParserListener)a).errorFound(e);
	    ((ParserListener)b).errorFound(e);
	}

	public static ParserListener add(ParserListener a, ParserListener b) {
	    return (ParserListener)addInternal(a, b);
	}

	public static ParserListener remove(ParserListener l, ParserListener oldl) {
	    return (ParserListener)removeInternal(l, oldl);
	}
    } // End of class Parser.ParserMulticaster
}
