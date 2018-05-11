/*
 * @(#)Presentation.java	1.5 06/10/30
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

import java.util.Locale;
import java.awt.Font;
import java.awt.Dimension;
import javax.help.Map.ID;
import java.net.URL;


/**
 * Presentation is an abstract class providing a generic interface for
 * the development of alternative Presentations. Each implementation of 
 * Presentation will need to override the static method getPresentation
 * according to it's own needs. For instance Popup creates a single object
 * whereas SecondaryWindow looks for an existing secondary window that matches
 * the parameters before creating a new SecondaryWindow and MainWindow will
 * always create a new Presentation.
 *
 * Presentation implements several generic methods required in all 
 * presentations.
 *
 * @author Roger D.Brinkley
 * @version	1.5	10/30/06
 * @since 2.0
 *
 * @see javax.help.HelpSet
 */

public abstract class Presentation {

    private HelpSet helpset = null;
    private TextHelpModel model = null;
    private Locale locale = null;
    private Font font = null;
    private int width = 645;
    private int height = 495;

    /**
     * Get a "name" Presentation given the passed HelpSet.
     * The presentation returned will vary depending on the implementing
     * class. All classes will set the HelpSetPresentation based on the
     * name if a HelpSet.Presentation matching that name exists in the
     * HelpSet. If no named HelpSet.Presentation exits the default
     * HelpSet.Presentation is used if present, otherwise the implementing
     * class defaults will be used.
     * Implementation of Presentation will
     * override this implementation to return their own type. Implementation 
     * also have the descression to reuse "name"d presentations or create
     * new Presentations. Presentation will return null unless otherwise 
     * overriden.
     * 
     * @param hs The helpset used to get the Presentation
     * @param name The name of the presentation.
     */
    static public Presentation getPresentation(HelpSet hs, String name) {
	return null;
    }

    /**
     * Set the Presentation attributes from a named presentation in a HelpSet.
     * Extension of this class should extend this class by adding additional
     * attributes 
     * 
     * @params hsPres - the HelpSet.Presentation to retrieve the presentation 
     *                  information from
     * 
     * @see HelpSet.Presentation
     */
    public void setHelpSetPresentation (HelpSet.Presentation hsPres) {
	debug("setHelpSetPresentation");
	if (hsPres == null) {
	    return;
	}

	// get the presentation size
	Dimension size = hsPres.getSize();
	if (size != null) {
	    setSize(size);
	}

    }

    /**
     * Determines which ID is displayed (if any).
     * @returns the current ID or null if there is none
     */
    public ID getCurrentID() {
	debug("getCurrentID");
	if (model != null) {
	    return model.getCurrentID();
	}
	return null;
    }

    /**
     * Shows this ID as content relative to the (top) HelpSet for the Presentation
     * instance--HelpVisitListeners are notified.
     *
     * @param id A string that identifies the topic to show for the loaded (top) HelpSet
     * @exception BadIDException The ID is not valid for the HelpSet
     */
    public void setCurrentID(String id) throws BadIDException {
	debug("setCurrentID - String");
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
	debug("setCurrentID - ID");

	createHelpModel();
	model.setCurrentID(id);
    }

    /**
     * Determines which URL is displayed.
     */
    public URL getCurrentURL() {
	debug("getCurrentURL");
	if (model != null) {
	    return model.getCurrentURL();
	}
	return null;
    }


    /**
     * sets the current URL.
     * HelpVisitListeners are notified.
     * The currentID changes if there is a mathing ID for this URL
     * @param url The url to display. A null URL is a valid url.
     */
    public void setCurrentURL(URL url) {
	debug("setCurrentURL");
	createHelpModel();
	model.setCurrentURL(url);
    }

    /**
     * Returns the default HelpSet
     */
    public HelpSet getHelpSet() {
	debug("getHelpSet");
	return helpset;
    }

    /**
     * Changes the HelpSet for this presentation.
     * @param hs The HelpSet to set for this presentation. 
     * A null hs is valid parameter.
     */
    public void setHelpSet(HelpSet hs) {
	debug("setHelpSet");
	// If we already have a model check if the HelpSet has changed.
	// If so change the model
	// This could be made smarter to cache the helpmodels per HelpSet
	if (hs != null && helpset != hs) {
	    model = new DefaultHelpModel(hs);
	    helpset = hs;
            
	}
    }

    /**
     * Displays the presentation to the user.
     */
    abstract public void setDisplayed(boolean b);

    /**
     * Determines if the presentation is displayed.
     */
    abstract public boolean isDisplayed();

    /**
     * Gets the font for this Presentation.
     */
    public Font getFont () {
	debug("getFont");
	return font;
    }

    /**
     * Sets the font for this this Presentation. Concrete implementations must
     * make sure that the font is properly set by extending this class.
     * @param f The font.
     */
    public void setFont (Font f) {
	debug("setFont");
	font = f;
    }

    /**
     * Gets the locale of this component.
     * @return This component's locale. If this component does not
     * have a locale, the defaultLocale is returned.
     * @see #setLocale
     */
    public Locale getLocale() {
	debug("getLocale");
	if (locale == null) {
	  return Locale.getDefault();
	}
	return locale;
    }

    /**
     * Sets the locale of this Presentation. The locale is propagated to
     * the presentation. Concrete implemenation must make sure they override
     * this class to properly set the locale.
     *
     * @param l The locale to become this component's locale. A null locale
     * is the same as the defaultLocale.
     * @see #getLocale
     */
    public void setLocale(Locale l) { 
	debug("setLocale");
	locale = l;
    }

    /**
     * Requests the size of the presentation.
     * @returns Point the location of the presentation.
     */
    public Dimension getSize() {
	debug("getSize");
	return new Dimension(width, height);
    }

    /**
     * Requests the presentation be set to a given size.
     * Concrete implementation must override this method to properly set the
     * size.
     * 
     * @param d - a Dimension to set the size to.
     */
    public void setSize(Dimension d) {
	debug("setSize");
	width = d.width;
	height = d.height;
    }

    /*
     * private implementations
     */
    private void createHelpModel() {
	if (model == null) {
	    model = new DefaultHelpModel(helpset);
	}
    }

    protected TextHelpModel getHelpModel () {
	if (model == null) {
	    createHelpModel();
	}
	return model;
    }

    /**
     * Debugging code...
     */

    private static final boolean debug = false;
    private static void debug(Object msg) {
	if (debug) {
	    System.err.println("Presentation: "+msg);
	}
    }
 
}


