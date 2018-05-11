/*
 * @(#)JHelpNavigator.java	1.68 06/10/30
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

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.accessibility.*;
import java.net.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Hashtable;
import java.beans.*;
import javax.help.event.*;
import javax.help.plaf.HelpNavigatorUI;
import javax.help.Map.ID;

/**
 * A JHelpNavigator is a control that presents navigational help data.
 * It is identified by a type and it interacts with a HelpModel.
 *
 * There are three JHelpNavigators that most JavaHelp implementations should
 * support:
 *
 * @see javax.help.JHelpTOCNavigator
 * @see javax.help.JHelpIndexNavigator
 * @see javax.help.JHelpSearchNavigator
 *
 * This class is intended to be extended. To use this class by itself
 * requires a platform look and feel (plaf) class that extends 
 * <tt>javax.help.plaf.HelpNavigatorUI</tt>. Additionally,
 * the UIDefaults table requires an entry for "HelpViewerUI" that points to
 * the plaf class.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @author Richard Gregor
 * @version	1.68	10/30/06
 */

public class JHelpNavigator extends JComponent implements Accessible{
    protected HelpModel helpModel;
    protected String type;
    private static String jhPackageName;
    private NavigatorView view;
    private TreeItem[] selectedItems;
    
 
    /**
     * Returns a JHelpNavigator with an instance of DefaultHelpModel as its 
     * data model.
     *
     * @param view The NavigatorView to use. If <tt>view</tt> is null it creates a JHelpTOCNavigator
     * with a null NavigatorView.
     */
    public JHelpNavigator(NavigatorView view) {
	super();
	this.view = view;
	if (view != null) {
	    setModel(new DefaultHelpModel(view.getHelpSet()));
	} else {
	    setModel(null);
	}
	updateUI();
    }

    /**
     * Constructs a JHelpNavigator from some view and with a preexisting model and in default initial state of navigation
     * entries.
     *
     * @param view The NavigatorView. If <tt>view</tt> is null it creates a JHelpNavigator
     * with a null NavigatorView.
     * @param model The model that generates changes. 
     * If <tt>model</tt> is null it creates a JHelpNavigator without a model.
     */
    public JHelpNavigator(NavigatorView view, HelpModel model) {
	super();
	this.view = view;
	setModel(model);
	updateUI();
        
    }

    /**
     * Returns a list of selected items
     */
    public TreeItem[] getSelectedItems() {
	if(selectedItems == null) {
	    return new TreeItem[0];
	} else {
	    return (TreeItem[]) selectedItems.clone();
	}
    }

    /**
     * Sets the list of selected items
     */
    public void setSelectedItems(TreeItem[] selectedItems) {
	TreeItem[] oldValue = this.selectedItems;
	this.selectedItems = selectedItems;
	firePropertyChange("SelectedItemsChangedProperty", oldValue, this.selectedItems);
    }

    /**
     * @return "HelpNavigatorUI"
     */
    public String getUIClassID()
    {
        return "HelpNavigatorUI";
    }

    /**
     * Determines if this instance of a JHelpNavigator can merge its data with another one.
     *
     * @param view The data to merge.
     * @return Whether it can be merged.
     *
     * @see merge(NavigatorView)
     * @see remove(NavigatorView)
     */
    public boolean canMerge(NavigatorView view) {
	return false;
    }

    /**
     * Merged a NavigatorView into this instance.
     *
     * @param view The data to merge
     * @exception UnsupportedOperationException
     *
     * @see canMerge(NavigatorView)
     * @see remove(NavigatorView)
     */
    public void merge(NavigatorView view) {
	throw new UnsupportedOperationException();
    }

    /**
     * Removes a NavigatorView from this instance.
     *
     * @param view The data to merge
     * @exception UnsupportedOperationException
     *
     * @see canMerge(NavigatorView)
     * @see merge(NavigatorView)
     */
    public void remove(NavigatorView view) {
	throw new UnsupportedOperationException();
    }

    // ========= Navigator Identification ========

    /**
     * Names this Navigator.
     *
     * @return The name of this Navigator.  This is locale independent and can be
     * 	used by the application to identify the view.
     */
    public String getNavigatorName() {
	return view.getName();
    }

    /**
     * Gets the NavigatorView that created this Navigator View.
     *
     * @return the NavigatorView
     */
    public NavigatorView getNavigatorView() {
	return view;
    }

    // HERE -- Do we want these, or just the getNavigatorView()? - epll

    /**
     * Gets the name of this navigator view.
     *
     * @return The label for this NavigatorView.
     */
    public String getNavigatorLabel() {
	return view.getLabel();
    }

    /**
     * Gets locale-dependent name for this navigator view.
     *
     * @return the label for this NavigatorView. If locale is null it is 
     * treated as the default Locale.
     */
    public String getNavigatorLabel(Locale locale) {
	return view.getLabel(locale);
    }

    /**
     * Gets an icon to identify this Navigator.  Currently this is a read-only
     * property.
     *
     * @return An icon to identify this navigator.
     */
    public Icon getIcon() {
	return getUI().getIcon();
    }


    // =========== Model and UI methods ===========

    /**
     * Sets the HelpModel that provides the data.
     * @param newModel The HelpModel for this component. A null for newModel
     * is valid.
     */
    public void setModel(HelpModel newModel) {
	HelpModel oldModel = helpModel;
        if (newModel != oldModel) {
            helpModel = newModel;
            firePropertyChange("helpModel", oldModel, helpModel);
            invalidate();
        }
    }

    /**
     * Returns the HelpModel that provides the data.
     */
    public HelpModel getModel() {
	return helpModel;
    }

    /**
     * Sets the HelpUI that provides the current look and feel.
     */
    public void setUI(HelpNavigatorUI ui) {
	if ((HelpNavigatorUI)this.ui != ui) {
	    super.setUI(ui);
	}
    }

    /**
     * Returns the HelpUI that provides the current look and feel.
     */
    public HelpNavigatorUI getUI() {
	return (HelpNavigatorUI)ui;
    }

    /**
     * Replaces the UI with the latest version from the default 
     * UIFactory.
     *
     * @overrides updateUI in class JComponent
     */
    public void updateUI() {
        SwingHelpUtilities.installUIDefaults();
	setUI((HelpNavigatorUI)UIManager.getUI(this));
        invalidate();
    }

    /*
     * Makes sure the Look and Feel will be set for the Help Component.
     */
    static {
	SwingHelpUtilities.installLookAndFeelDefaults();
    }

    // Be a source for HelpModelEvents

    /**
     * Adds a listener for the HelpModelEvent posted after the model has
     * changed.
     * 
     * @param l - The listener to add.
     * @see javax.help.HelpModel#removeHelpModelListener
     */
    public void addHelpModelListener(HelpModelListener l) {
	getModel().addHelpModelListener(l);
    }

    /**
     * Removes a listener previously added with <tt>addHelpModelListener</tt>.
     *
     * @param l - The listener to remove.
     * @see javax.help.HelpModel#addHelpModelListener
     */
    public void removeHelpModelListener(HelpModelListener l) {
	getModel().removeHelpModelListener(l);
    }

    /**
     * Creates the parameters for a Navigator from data stored in a URL.
     *
     * @return A Hashtable of parameters
     */

    protected static Hashtable createParams(URL data) {
	Hashtable back = new Hashtable();
	back.put("data", data.toString());
	return back;
    }


    
    /**
     * For printf debugging.
     */
    private static final boolean debug = false;
    private static void debug(String str) {
        if (debug) {
            System.err.println("JHelpNavigator: " + str);
        }
    }

/////////////////
// Accessibility support
////////////////

    /**
     * Get the AccessibleContext associated with this JComponent.
     *
     * @return The AccessibleContext of this JComponent
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJHelpNavigator();
        }
        return accessibleContext;
    }

    /**
     * The class used to obtain the accessible role for this object.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class AccessibleJHelpNavigator extends AccessibleJComponent {

        /**
         * Get the role of this object.
         *
         * @return An instance of AccessibleRole describing the role of the
         * object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PANEL;
        }
    }
}
