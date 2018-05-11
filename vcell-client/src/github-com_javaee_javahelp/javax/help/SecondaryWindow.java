/*
 * @(#)SecondaryWindow.java	1.5 06/10/30
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

import java.util.Hashtable;

/**
 * MainWindowPresentation is a class that will create a single main help 
 * window for an application. Although there is generally only one per
 * application there can  be multiple MainWindowPresentation.
 * By default it is a tri-paned fully decorated window
 * consisting of a toolbar, navigator pane, and help content view. By default
 * the class is not destroyed when the window exits.
 *
 * @author Roger D.Brinkley
 * @version	1.5	10/30/06
 * @since 2.0
 *
 * @see javax.help.WindowPresentation
 * @see javax.help.Presentation
 */

public class SecondaryWindow extends WindowPresentation {

    static private Hashtable windows = new Hashtable();
    private String name;

    private SecondaryWindow(HelpSet hs, String name) {
	super(hs);
	this.name = name;
    }

    /**
     * Get a named SecondaryWindow for a given HelpSet.
     * Named SecondaryWindows are stored. If a named 
     * SecondaryWindow exits then it is returned, otherwise a new
     * secondary window is created. If there is a HelpSet.Presentation of the
     * same name the presentation attibutes will be used, otherwise, the 
     * default HelpSet.Presentation is used.
     * 
     * @param hs The HelpSet used in this presentation
     * @param name The name of the Presentation to create - also the name
     *             of the HelpSet.Presentation to use.
     * @returns Presentation A unique MainWindowPresentation. 
     */
    static public Presentation getPresentation(HelpSet hs, String name) {
	debug ("getPresentation");
	SecondaryWindow swp;

	String winName = name;
	if (name == null) {
	    winName = "";
	}

	// Use the secondary window if one exists
	swp = (SecondaryWindow) windows.get(winName);
	if (swp != null) {
	    if (swp.getHelpSet() != hs) {
		swp.setHelpSet(hs);
	    }
	    return swp;
	}

	debug ("no Presentation - start again");
	swp = new SecondaryWindow(hs, winName);

	// Set the SecondaryWindow defaults
	swp.setViewDisplayed(false);
	swp.setToolbarDisplayed(false);
	swp.setDestroyOnExit(true);
	swp.setTitleFromDocument(true);

	if (hs != null) {
	    HelpSet.Presentation presentation = null;

	    // get a named presentation if one exists
	    if (name != null) {
		presentation = hs.getPresentation(name);
	    }

	    // get the default presentation if one exits
	    if (presentation == null) {
		presentation = hs.getDefaultPresentation();
	    }

	    // set the presentation
	    // a null is ok here as it will just return.
	    swp.setHelpSetPresentation(presentation);

	    windows.put(winName, swp);
	}
	return swp;
    }

    /**
     * Gets a SecondaryWindow if one exists. Does not 
     * create a Presentation if one does not exist.
     * 
     * @param name Name of the presentation to get
     * @return SecondaryWindow The found Presentation or null
     */
    static public SecondaryWindow getPresentation(String name) {
	debug ("getPresenation(name)");
	return (SecondaryWindow) windows.get(name);
    }

    /**
     * Destroy the SecondaryWindowPresentatin. Specifically remove it from
     * the list of SecondaryWindows.
     */
    public void destroy() {
	super.destroy();
	windows.remove(name);
    }

    /**
     * Debugging code...
     */

    private static final boolean debug = false;
    private static void debug(Object msg) {
	if (debug) {
	    System.err.println("SecondaryWindow: "+msg);
	}
    }
 
}
