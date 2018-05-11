/*
 * @(#)MainWindow.java	1.3 06/10/30
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
 * MainWindow is a class that will create a single main help 
 * window for an application. Although there is generally only one per
 * application there can  be multiple MainWindow.
 * By default it is a tri-paned fully decorated window
 * consisting of a toolbar, navigator pane, and help content view. By default
 * the class is not destroyed when the window exits.
 *
 * @author Roger D.Brinkley
 * @version	1.3	10/30/06
 * @since 2.0
 *
 * @see javax.help.WindowPresentation
 * @see javax.help.Presentation
 */

public class MainWindow extends WindowPresentation {

    private MainWindow(HelpSet hs) {
	super(hs);
    }

    /**
     * Creates a new MainWindow for a given HelpSet and 
     * HelpSet.Presentation "name". If the "name"d HelpSet.Presentation
     * does not exist in HelpSet then the default HelpSet.Presentation
     * is used.
     * 
     * @param hs The HelpSet used in this presentation
     * @param name The name of the Presentation to create - also the name
     *             of the HelpSet.Presentation to use.
     * @returns Presentation A unique MainWindow. 
     */
    static public Presentation getPresentation(HelpSet hs, String name) {
	MainWindow mwp = new MainWindow(hs);
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
	    mwp.setHelpSetPresentation(presentation);
	}
	return mwp;
    }

    /**
     * Debugging code...
     */

    private static final boolean debug = false;
    private static void debug(Object msg) {
	if (debug) {
	    System.err.println("MainWindow: "+msg);
	}
    }
 
}


