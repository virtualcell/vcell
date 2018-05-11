/*
 * @(#)ParserEvent.java	1.12 06/10/30
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

package com.sun.java.help.impl;

/**
 * An event in the HTML/XML Parser
 *
 * @author Roger D. Brinkley
 * @version	1.12	10/30/06
 */

import java.net.URL;
import java.util.Vector;
import java.util.Enumeration;

public class ParserEvent extends java.util.EventObject {
    private Tag tag;
    private String text;
    private String target;
    private String data;
    private String root;
    private String publicId;
    private String systemId;

    /**
     * Represents a parsed Tag in the Parser
     * @see java.help.basic.Parser
     * 
     * @param source The Parser this came from
     * @param tag The parsed Tag.
     */
    public ParserEvent(Object source, Tag tag) {
	super (source);
	this.tag = tag;
    }

    /**
     * Represents a parsed continous block of text, a comment, or an error.
     * @see java.help.basic.Parser
     *
     * @param source The Parser this came from
     * @param String The text, comment, or error
     */
    public ParserEvent(Object source, String text) {
	super (source);
	this.text = text;
    }

    /**
     * Represents a PI (processing instruction)
     * @see java.help.basic.Parser
     *
     * @param source The Parser this came from
     * @param target The PI target
     * @param data The rest of the PI
     */
    public ParserEvent(Object source, String target, String data) {
	super (source);
	this.target = target;
	this.data = data;
    }

    /**
     * Represents a DOCTYPE
     * @see java.help.basic.Parser
     *
     * @param source The Parser this came from
     * @param root The root
     * @param publicId The publicID (may be null)
     * @param systemID The  systemID (may be null)
     */
    public ParserEvent(Object source, String root, String publicId, String systemId) {
	super (source);
	this.root = root;
	this.publicId = publicId;
	this.systemId = systemId;
    }

    /**
     * @return the Tag
     */
    public Tag getTag() {
	return tag;
    }

    /**
     * @return the text
     */
    public String getText() {
	return text;
    }

    /**
     * @return the target
     */
    public String getTarget() {
	return target;
    }

    /**
     * @return the data
     */
    public String getData() {
	return data;
    }

    /**
     * @return the root
     */
    public String getRoot() {
	return root;
    }

    /**
     * @return the publicId
     */
    public String getPublicId() {
	return publicId;
    }

    /**
     * @return the systemId
     */
    public String getSystemId() {
	return systemId;
    }
}
