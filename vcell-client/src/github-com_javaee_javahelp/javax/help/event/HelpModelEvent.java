/*
 * @(#)HelpModelEvent.java	1.21 06/10/30
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

package javax.help.event;

import java.net.URL;
import java.util.Vector;
import java.util.Enumeration;
import javax.help.HelpSet;
import javax.help.Map.ID;
import javax.help.JHelpNavigator;


/**
 * Notifies interested parties that a change in a
 * Help Model source has occurred.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @author Richard Gregor
 * @version	1.16	03/10/99
 */

public class HelpModelEvent extends java.util.EventObject {
    private ID id;
    private URL url;
    private String historyName;
    private JHelpNavigator navigator;

    /**
     * Represents a change in the JavaHelp in the current ID or URL.
     * @see javax.help.JavaHelp
     * 
     * @param source The source for this event.
     * @param id The ID that has changed. Should be null if URL is specified.
     * @param url The URL that has changed. Should be null if ID is specified.
     * @throws IllegalArgumentException if source is null.
     * @throws IllegalArgumentException of both ID and URL are null.
     */
    public HelpModelEvent(Object source, ID id, URL url) {
	this(source,id,url,(String)null, (JHelpNavigator)null);
    }
    
    /**
     * Represents a change in the JavaHelp in the current ID or URL.
     * @see javax.help.JavaHelp
     * 
     * @param source The source for this event.
     * @param id The ID that has changed. Should be null if URL is specified.
     * @param url The URL that has changed. Should be null if ID is specified.
     * @param historyName The name of selected entry
     * @param navigator The JHelpNavigator
     * @throws IllegalArgumentException if source is null.
     * @throws IllegalArgumentException of both ID and URL are null.
     */
    public HelpModelEvent(Object source, ID id, URL url, String historyName, JHelpNavigator navigator){
        super(source);
	if ((id == null) && (url == null)) {
	  throw new IllegalArgumentException("ID or URL must not be null");
	}
        this.id = id;
        this.url = url;
        this.historyName = historyName;
        this.navigator = navigator;
    }
        

    /**
     * Creates a HelpModelEvent for highlighting.
     *
     * @param source The source for this event.
     * @param pos0 Start position.
     * @param pos1 End position.
     * @throws IllegalArgumentException if source is null.
     */
    public HelpModelEvent(Object source, int pos0, int pos1) {
	super (source);
	this.pos0 = pos0;
	this.pos1 = pos1;
    }
    /**
     * Returns the current ID in the HelpModel.
     * @return The current ID.
     */
    public ID getID() {
	return id;
    }

    /**
     * Returns the current URL in the HelpModel.
     * @return The current URL.
     */
    public URL getURL() {
	return url;
    }
    
    /**
     * Returns the name of this entry
     *
     * @return The entry name
     */
    public String getHistoryName() {
        return historyName;
    }

    /**
     * Returns the navigator of this entry
     *
     * @return The navigator name
     */
    public JHelpNavigator getNavigator() {
        return navigator;
    }
    
    private int pos0, pos1;

    // HERE - Review this highlighting; it is a different type of beast than the rest - epll
    /**
     * @return The start position of this (highlighting) event.
     */
    public int getPos0() {
	return pos0;
    }

    /**
     * @return The end position of this (highlighting) event.
     */
    public int getPos1() {
	return pos1;
    }
}
