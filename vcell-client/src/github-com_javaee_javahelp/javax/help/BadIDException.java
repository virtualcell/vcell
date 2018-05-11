/*
 * @(#)BadIDException.java	1.15 06/10/30
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

package javax.help;

/**
 * An ID was attempted to be created with incorrect arguments
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version	1.15	10/30/06
 */

public class BadIDException extends IllegalArgumentException {
    private Map map;
    private String id;
    private HelpSet hs;

    /**
     * Create the exception. Null values are allowed for each parameter.
     * 
     * @param map The Map in which the ID wasn't found
     * @param msg A generic message
     * @param id The ID in Map that wasn't found
     * @see javax.help.Map
     */
    public BadIDException(String msg, Map map, String id, HelpSet hs) {
	super(msg);
	this.map = map;
	this.id = id;
	this.hs = hs;
    }

    /**
     * The HelpSet in which the ID wasn't found
     */
    public Map getMap() {
	return map;
    }

    /**
     * The ID that wasn't found in the Map
     */
    public String getID() {
	return id;
    }

    /**
     * The HelpSet that wasn't found in the Map
     */
    public HelpSet getHelpSet() {
	return hs;
    }
}

