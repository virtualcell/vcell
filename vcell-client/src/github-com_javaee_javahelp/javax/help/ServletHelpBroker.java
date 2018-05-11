/*
 * @(#)ServletHelpBroker.java	1.3 06/10/30
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

import java.util.Enumeration;
import java.util.Hashtable;
import java.net.URL;
import java.net.MalformedURLException;
import java.awt.*;
import java.awt.event.*;
import javax.help.Map.ID;
import java.util.Locale;
import java.awt.Font;
import java.awt.Dimension;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.IOException;

/**
 * An implmentation of the HelpBroker interface for Servlets
 *
 * @author Roger Brinkley
 * @version	1.3	10/30/06
 */

public class ServletHelpBroker implements HelpBroker {

    protected HelpSet helpset = null;
    protected DefaultHelpModel model = null;
    protected NavigatorView curNav = null;
    protected boolean viewDisplayed = true;
    protected Locale locale=null;
    protected Font font;

    /**
     * Zero-argument constructor.
     * It should be followed by a setHelpSet() invocation.
     */

    public ServletHelpBroker() {
    }

    /**
     * Returns the default HelpSet
     */
    public HelpSet getHelpSet() {
	return helpset;
    }

    /**
     * Changes the HelpSet for this broker.
     * @param hs The HelpSet to set for this broker. 
     * A null hs is valid parameter.
     */
    public void setHelpSet(HelpSet hs) {
	if (hs != null && helpset != hs) {
	    model = new DefaultHelpModel(hs);
	    helpset = hs;
	}
    }


    /**
     * Gets the locale of this component.
     * @return This component's locale. If this component does not
     * have a locale, the defaultLocale is returned.
     * @see #setLocale
     */
    public Locale getLocale() {
	if (locale == null) {
	  return Locale.getDefault();
	}
	return locale;
    }

    /**
     * Sets the locale of this HelpBroker. The locale is propagated to
     * the presentation.
     * @param l The locale to become this component's locale. A null locale
     * is the same as the defaultLocale.
     * @see #getLocale
     */
    public void setLocale(Locale l) {
	locale = l;
    }

    /**
     * Gets the font for this HelpBroker.
     */
    public Font getFont () {
	return font;
    }

    /**
     * Sets the font for this this HelpBroker.
     * @param f The font.
     */
    public void setFont (Font f) {
	font = f;
    }

    /**
     * Set the currentView to the navigator with the same 
     * name as the <tt>name</tt> parameter.
     *
     * @param name The name of the navigator to set as the 
     * current view. If nav is null or not a valid Navigator 
     * in this HelpBroker then an 
     * IllegalArgumentException is thrown.
     * @throws IllegalArgumentException if nav is null or not a valid Navigator.
     */
    public void setCurrentView(String name) {

	NavigatorView views[] = helpset.getNavigatorViews();
	for (int i=0; i<views.length; i++) {
	    if (views[i].getName().equals(name)) {
		curNav = views[i];
		return;
	    }
	}
	// did find a suitable navigator
	throw new IllegalArgumentException("Invalid view name");
    }

    /**
     * Determines the current navigator.
     */
    public String getCurrentView() {
	// if the current Nav isn't set then use the first Nav from the
	// the helpset.
	if (curNav == null) {
	    if (helpset != null) {
		NavigatorView views[] = helpset.getNavigatorViews();
		curNav = views[0];
	    } else {
		// Argh! there werent' any navigators in the helpset 
		// return null;
		return null;
	    }
	}	
	return curNav.getName();
    }


    /**
     * Returns the current navigator as a NavigatorView.
     */
    public NavigatorView getCurrentNavigatorView() {
	// if the current Nav isn't set then use the first Nav from the
	// the helpset.
	if (curNav == null) {
	    if (helpset != null) {
		NavigatorView views[] = helpset.getNavigatorViews();
		curNav = views[0];
	    } else {
		// Argh! there werent' any navigators in the helpset 
		// return null;
		return null;
	    }
	}	
	return curNav;
    }


    /**
     * Initializes the presentation.
     * Not implemented in ServletHelpBroker.
     */
    public void initPresentation() {
    }

    /**
     * Displays the presentation to the user.
     */
    public void setDisplayed(boolean b) {
	// ignore this is always displayed
    }

    /**
     * Determines if the presentation is displayed.
     */
    public boolean isDisplayed() {
	return true;
    }

    /**
     * Requests the presentation be located at a given position.
     * This operation throws an UnsupportedOperationException
     * in ServletHelpBroker
     */
    public void setLocation(Point p) throws javax.help.UnsupportedOperationException {
	throw new javax.help.UnsupportedOperationException("Not implemented in ServeltHelpBroker");
    }

    /**
     * Requests the location of the presentation.
     * This operation throws an UnsupportedOperationException
     * in ServletHelpBroker
     */
    public Point getLocation() throws javax.help.UnsupportedOperationException {
	throw new javax.help.UnsupportedOperationException("Not implemented in ServeltHelpBroker");
    }

    /**
     * Requests the presentation be set to a given size.
     * This operation throws an UnsupportedOperationException
     * in ServletHelpBroker
     */
    public void setSize(Dimension d) throws javax.help.UnsupportedOperationException {
	throw new javax.help.UnsupportedOperationException("Not implemented in ServeltHelpBroker");
    }

    /**
     * Requests the size of the presentation.
     * throws UnsupportedOperationException in ServletHelpBroker.
     */
    public Dimension getSize() throws javax.help.UnsupportedOperationException {
	throw new javax.help.UnsupportedOperationException("Not implemented in ServeltHelpBroker");
    }

    /**
     * Requests the presentation be set to a given screen.
     * This operation throws an UnsupportedOperationException
     * in ServletHelpBroker
     */
    public void setScreen(int screen) throws javax.help.UnsupportedOperationException {
	throw new javax.help.UnsupportedOperationException("Not implemented in ServeltHelpBroker");
    }

    /**
     * Requests the screen of the presentation.
     * throws UnsupportedOperationException in ServletHelpBroker.
     */
    public int getScreen() throws javax.help.UnsupportedOperationException {
	throw new javax.help.UnsupportedOperationException("Not implemented in ServeltHelpBroker");
    }

    /**
     * Hides/Shows view.
     */
    public void setViewDisplayed(boolean displayed) {
	viewDisplayed = displayed;
    }

    /**
     * Determines if the current view is visible.
     */
    public boolean isViewDisplayed() {
	return viewDisplayed;
    }

    /**
     * Shows this ID as content relative to the (top) HelpSet for the HelpBroker
     * instance--HelpVisitListeners are notified.
     *
     * @param id A string that identifies the topic to show for the loaded (top) HelpSet
     * @exception BadIDException The ID is not valid for the HelpSet
     */
    public void setCurrentID(String id) throws BadIDException {
	try {
	    setCurrentID(ID.create(id, helpset));
	} catch (InvalidHelpSetContextException ex) {
	    // this should not happen
	    new Error("internal error?");
	}
    }

    /**
     * Displays this ID--HelpVisitListeners are notified.
     *
     * @param id a Map.ID indicating the URL to display
     * @exception InvalidHelpSetContextException if the current helpset does not contain
     * id.helpset
     */
    public void setCurrentID(ID id) throws InvalidHelpSetContextException {
	debug("setCurrentID");

	model.setCurrentID(id);
    }

    /**
     * Determines which ID is displayed (if any).
     */
    public ID getCurrentID() {
	return model.getCurrentID();
    }

    /**
     * Displays this URL.
     * HelpVisitListeners are notified.
     * The currentID changes if there is a mathing ID for this URL
     * @param url The url to display. A null URL is a valid url.
     */
    public void setCurrentURL(URL url) {
	model.setCurrentURL(url);
    }

    /**
     * Determines which URL is displayed.
     */
    public URL getCurrentURL() {
	return model.getCurrentURL();
    }


    // Context-Senstive methods
    /**
     * Enables the Help key on a Component. 
     * Not implemented in ServletHelpBroker
     */
    public void enableHelpKey(Component comp, String id, HelpSet hs) {
    }

    /**
     * Enables help for a Component. 
     * Not implemented in ServletHelpBroker
     */
    public void enableHelp(Component comp, String id, HelpSet hs) 
    {
    }

    /**
     * Enables help for a MenuItem. 
     * Not implemented in ServletHelpBroker
     */
    public void enableHelp(MenuItem comp, String id, HelpSet hs) 
    {
    }

    /**
     * Enables help for a Component.
     * Not implemented in ServletHelpBroker
     */
    public void enableHelpOnButton(Component comp, String id, HelpSet hs) 
    {
    }

    /**
     * Enables help for a MenuItem.
     * Not implemented in ServletHelpBroker.
     */
    public void enableHelpOnButton(MenuItem comp, String id, HelpSet hs) 
    {
    }

    // Investigate if the following need to be implemented
    public void setHelpSetPresentation(HelpSet.Presentation pres) {
    }
    public void showID(String id, String presentation, 
		       String presentationName) throws BadIDException {
    }
    public void showID(ID id, String presentation, String presentationName) 
	throws InvalidHelpSetContextException {
    }
    public void enableHelpKey(Component comp, String id, HelpSet hs,
			      String presentation, String presentationName) {
    }
    public void enableHelpOnButton(Object obj, String id, HelpSet hs,
				   String presentation, 
				   String presentationName) 
    {
    }
    /**
     * Debugging code...
     */

    private static final boolean debug = false;
    private static void debug(Object msg) {
	if (debug) {
	    System.err.println("ServletHelpBroker: "+msg);
	}
    }
 
}

