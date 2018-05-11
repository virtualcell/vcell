/*
 * @(#)JHelpTOCNavigator.java	1.44 06/10/30
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

import java.net.URL;
import java.util.Vector;
import java.util.Enumeration;


/**
 * A JHelpNavigator for a TOC.
 *
 * All of the tree navigation and selection has been delegated to the UI
 * where the JTree is created.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version	1.44	10/30/06
 */

public class JHelpTOCNavigator extends JHelpNavigator {
 
    /**
     * Create a TOC navigator
     *
     * @param view The NavigatorView. If view is null it creates a JHelpTOCNavigator
     * without a HelpModel and a null NavigatorView.
     */
    public JHelpTOCNavigator(NavigatorView view) {
	super(view, null);
    }

    /**
     * Creates a TOC navigator.
     *
     * @param view The NavigatorView. If <tt>view</tt> is null it creates a JHelpTOCNavigator
     * with a null NavigatorView.
     * @param model The model for the Navigator. If <tt>model</tt> is null it creates a
     * JHelpTOCNavigator witout a model.
     */

    public JHelpTOCNavigator(NavigatorView view, HelpModel model) {
	super(view, model);
    }



    /**
     * Creates a TOC navigator with explicit arguments.  Note that this should not throw
     * an InvalidNavigatorViewException since the type is implicitly passed.
     *
    * @param hs HelpSet
    * @param name The name indentifying this HelpSet.
    * @param label The label to use (for this locale).
    * @param data The "data" part of the parameters, a URL location of the TOC data.
     */
    public JHelpTOCNavigator(HelpSet hs,
			     String name, String label, URL data) 
	throws InvalidNavigatorViewException
    {
	super(new TOCView(hs, name, label, createParams(data)));
    }


    /**
     * The UID for this JComponent.
     */
    public String getUIClassID() {
	return "HelpTOCNavigatorUI";
    }

    /**
     * Determines if this instance of a JHelpNavigator can merge its data with another one.
     *
     * @param view The data to merge.
     * @return Whether it can be merged.
     * @see merge()
     * @see remove()
     */
    public boolean canMerge(NavigatorView view) {
	if (view instanceof TOCView &&
	    getNavigatorName().equals(view.getName())) {
	    debug("canMerge: true");
	    return true;
	}
	debug("canMerge: false");
	return false;
    }

    /**
     * Merges a NavigatorView into this instance.
     *
     * @param view The data to merge.  This must be a TOCView.
     * @see canMerge()
     * @see remove()
     * @exception IllegalArgumentException
     * @exception IllegalStateException
     */
    public void merge(NavigatorView view) {
	debug("merge: "+view);
	this.getUI().merge(view);
    }

    /**
     * Removes a NavigatorView from this instance.
     *
     * @param view The data to merge.
     * @see canMerge()
     * @see merge()
     * @exception IllegalArgumentException
     * @exception IllegalStateException
     */
    public void remove(NavigatorView view) {
	// this should recompute the view
	this.getUI().remove(view);
    }

    /**
     * Sets state of navigation entry for given target to expanded. Non-empty entry is expanded. Empty entry is visible. 
     *
     * @param target The target to expand
     */
     public void expandID(String target){
         firePropertyChange("expand"," ",target);
    }
    
    
    /**
     * Sets state of navigation entry for given target to collapsed if entry is visible. Parent is collapsed if entry is empty.
     * 
     * @param target The target to collapse
     */
    public void collapseID(String target){
        firePropertyChange("collapse"," ",target);
    }
    
    
    private static final boolean debug = false;
    private static void debug(String msg) {
	if (debug) {
	    System.err.println("JHelpTOCNavigator: "+msg);
	}
    }


}

