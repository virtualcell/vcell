/*
 * @(#)Tag.java	1.8 06/10/30
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
 * @(#) Tag.java 1.8 - last change made 10/30/06
 */

package com.sun.java.help.impl;

/**
 * This class contains a reference to a tag in a parsed document, the type of
 * tag, and the tag's attributes.
 *
 * @author Roger D. Brinkley
 * @version	1.8	10/30/06
 *
 * @see Parser
 * @see HTMLParser
 */


public class Tag {

    /** The name of the tag */
    public String name;

    /** The type of tag, false if an opening tag or true if a closing tag. */
    public boolean isEnd;

    /** The type of tag, true if an empty tag. */
    public boolean isEmpty;

    /** The tag attributes, in identifier, value pairs. */
    public TagProperties atts;


    /**
     * Sets the tag, position, and type.
     * @param tag	   the tag descriptor
     * @param pos	   the position in the text
     * @param isEnd   true if a &lt;/tag&gt; or &lt;tag/&gt; tag 
     * @param isEmpty   true if a &lt;tag/&gt; tag
     * @see Tag
     **/

    public Tag(String name, TagProperties atts, boolean isEnd, boolean isEmpty) {
	this.name = name;
	this.atts = atts;
	this.isEnd = isEnd;
	this.isEmpty = isEmpty;
    }

}
