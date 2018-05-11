/*
 * @(#)JHelp.java	1.78 06/10/30
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
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.LookAndFeel;
import javax.accessibility.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Locale;
import java.io.InputStream;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.help.plaf.HelpUI;
import javax.help.event.HelpSetListener;
import javax.help.event.HelpSetEvent;
import javax.help.Map.ID;

/**
 * Displays HelpSet data with navigators and a content viewer.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @author Stepan Marek
 * @author Richard Gregor
 * @version	1.78	10/30/06
 */
public class JHelp extends JComponent implements HelpSetListener, Accessible {
    protected TextHelpModel helpModel;
    protected HelpHistoryModel historyModel;
    protected Vector navigators;
    protected boolean navDisplayed=true;
    protected boolean toolbarDisplayed=true;
    protected HelpSet.Presentation hsPres;

    protected JHelpContentViewer contentViewer;
    

    /**
     * Create a JHelp with a JHelpContentViewer and all Navigators 
     * requested in the HelpSet.
     *
     * @param hs The HelpSet. If hs is null the JHelp is created with a 
     * TextHelpModel with no HelpSet.
     */
    public JHelp(HelpSet hs) {
	this(new DefaultHelpModel(hs), 
	     null, 
	     hs==null? null : hs.getDefaultPresentation());
    }
    
    /**
     * Create a JHelp component without a TextHelpModel.
     */
    public JHelp() {
	this((TextHelpModel)null);
    }

    /**
     * Create a JHelp using the TextHelpModel.
     *
     * @param model A model to use for the content and all the navigators. If <tt>model</tt>
     * is null it is the same as creating without a TextHelpModel
     */
    public JHelp(TextHelpModel model){
        this(model, null, null);
    }
    
    /**
     * Create a JHelp using the TextHelpModel and HelpHistoryModel
     *
     * @param model A model to use for the content and all the navigators. If <tt>model</tt>
     * is null it is the same as creating without a TextHelpModel
     * @param history A history model. If <<tt>history</tt> is null it is the same
     * as creating without HelpHistoryModel
     */
    public JHelp(TextHelpModel model, HelpHistoryModel history, 
		 HelpSet.Presentation hsPres){
        super();
        
        if(history == null)
            this.historyModel = new DefaultHelpHistoryModel(this);
        else
            this.historyModel = history;

	this.hsPres = hsPres;
        
        navigators = new Vector();
	navDisplayed = true;

	// HERE -- need to do something about doc title changes....

	this.contentViewer = new JHelpContentViewer(model);

	setModel(model);
	if (model != null) {
	    setupNavigators();            
        }

        updateUI();
    }

    protected void setupNavigators() {
	HelpSet hs = helpModel.getHelpSet();
	// Simply return if the hs is null
	if (hs == null) {
	    return;
	}

	// Now add all the navigators
	NavigatorView views[] = hs.getNavigatorViews();

	debug("views: "+views);

	for (int i=0; i<views.length; i++) {
	    debug("  processing info: "+views[i]);

	    // We are currently assuming all the Navigators are JComponents
	    JHelpNavigator nav
		= (JHelpNavigator) views[i].createNavigator(helpModel);
	    
	    if (nav == null) {
		// For now...
		debug("no JHelpNavigator for given info");
	    } else {
		debug("  adding the navigator");
		navigators.addElement(nav);
		// HERE -- I don't think we want to change again the model
		//		    this.addHelpNavigator(nav);
            }
	}
    }
    
    /**
     * Sets the HelpModel that provides the data.
     *
     * @param newModel The new Model. If <tt>newModel</tt> is null the internal model is set
     * to null.
     */
    public void setModel(TextHelpModel newModel) {
	TextHelpModel oldModel = helpModel;
        if (newModel != oldModel) {
	    if (oldModel != null) {
                oldModel.getHelpSet().removeHelpSetListener(this);
	    }
            helpModel = newModel;
	    if (newModel != null) {                
		HelpSet hs = newModel.getHelpSet();
		if (hs != null) {
		    hs.addHelpSetListener(this);
		}
	    }
            firePropertyChange("helpModel", oldModel, helpModel);

	    // Now tell all the components we control
	    contentViewer.setModel((TextHelpModel) newModel);
            
            getHistoryModel().setHelpModel((HelpModel) newModel);

	    // We'll have to destroy all of the navigators and
	    // reload them from the HelpSet
	    HelpUI help = getUI();

	    // Skip the navigators if the ui hasn't been setup yet.
	    if (help == null) {
		return;
	    }

	    for (Enumeration e = getHelpNavigators();
		 e.hasMoreElements(); ) {
		JHelpNavigator nav = (JHelpNavigator) e.nextElement();
		help.removeNavigator(nav);
	    }
	    navigators.removeAllElements();
	    setupNavigators();
	    updateUI();
        }
    }

    /**
     * @return The HelpModel that is providing the data.
     */
    public TextHelpModel getModel() {
	return helpModel;
    }

    /**
     * Returns The HelpHistoryModel
     *
     * @return The HelpHistoryModel which provides history data
     */
    public HelpHistoryModel getHistoryModel(){
        return historyModel;
    }

    /**
     * Set the HelpSet.Presentation.
     * @see HelpSet.Presentation
     */
    public void setHelpSetPresentation(HelpSet.Presentation hsPres) {
	this.hsPres = hsPres;
	return;
    }


    /**
     * @return The HelpSet.Presentation.
     * @see HelpSet.Presentation
     */
    public HelpSet.Presentation getHelpSetPresentation() {
	return hsPres;
    }


    // HERE - need customizers, etc... -epll
    // HERE - this is probably broken
    /**
     * Set the URL to the HelpSet.  This forces the HelpSet to be reloaded.
     *
     * @param spec Where to locate the HelpSet. A null spec is valid
     */
    public void setHelpSetSpec(String spec) {
	URL url;
	HelpSet hs;
	ClassLoader loader = this.getClass().getClassLoader();
	try {
	    url = new URL(spec);
	    hs = new HelpSet(loader, url);
	} catch (Exception ex) {
	    System.err.println("Trouble setting HelpSetSpec to spec |"+spec+"|");
	    System.err.println("  ex: "+ex);
	    hs = null;
	}
	contentViewer.setModel(new DefaultHelpModel(hs));
	setModel(contentViewer.getModel());
	updateUI();
    }

    /**
     * @return The URL to the HelpSet.
     */
    public URL getHelpSetURL() {
	HelpSet hs = contentViewer.getModel().getHelpSet();
	if (hs == null) {
	    return null;
	}
	return hs.getHelpSetURL();
    }

    // === The JComponent methods

    /**
     * Sets the HelpUI that will provide the current look and feel.
     * @param ui The HelpUI to set for this component. A null value for ui
     * is valid.
     */
    public void setUI(HelpUI ui) {
	if ((HelpUI)this.ui != ui) {
	    super.setUI(ui);
	}
    }

    /**
     * Returns the HelpUI that is providing the current look and feel.
     */
    public HelpUI getUI() {
	return (HelpUI)ui;
    }

    /**
     * Replaces the UI with the latest version from the default 
     * UIFactory.
     *
     * @overrides updateUI in class JComponent
     */
    public void updateUI() {
        SwingHelpUtilities.installUIDefaults();
        setUI((HelpUI)UIManager.getUI(this));
        invalidate();
    }

    /**
     * @return "HelpUI"
     */
    public String getUIClassID()
    {
        return "HelpUI";
    }

    /*
     * Findd the navigator with a given name.
     */
    private JHelpNavigator findNavigator(String name) {
	debug("findNavigator("+name+")");
	for (Enumeration e = getHelpNavigators();
	     e.hasMoreElements(); ) {
	    JHelpNavigator nav = (JHelpNavigator) e.nextElement();
	    debug("  nav: "+nav);
	    debug("  nav.getName: "+nav.getNavigatorName());
	    if (nav.getNavigatorName().equals(name)) {
		return nav;
	    }
	}
	return null;
    }

    /**
     * Adds a new HelpSet to "our" HelpSet.
     *
     * @param e HelpSetEvent
     * @see javax.help.event.HelpSetEvent
     * @see javax.help.event.HelpSetListener
     */
    public void helpSetAdded(HelpSetEvent e) {
	debug("helpSetAdded("+e+")");
	HelpSet ehs = e.getHelpSet();
	addHelpSet(ehs);
    }
    
    /*
     * Adds a HelpSet.
     */
    private void addHelpSet(HelpSet ehs) {
        debug("helpset :"+ehs);
	NavigatorView eviews[] = ehs.getNavigatorViews();
        
        //if master help set is created using new HelpSet() -without arguments it hasn't got any navigators to merge
        //we will create new navigators

        int count = 0;
        for (Enumeration e = getHelpNavigators() ; e.hasMoreElements() ;e.nextElement()) {
            ++count;
        }
        if(count == 0){
            debug("master helpset without navigators");            
            HelpModel newModel= new DefaultHelpModel(ehs);
            setModel((TextHelpModel)newModel);
            setupNavigators();
            return;
        }
        
             
    	for (int i=0; i<eviews.length; i++) {
	    String n = eviews[i].getName();
	    debug("addHelpSet: looking for navigator for "+n);
	    JHelpNavigator nav = findNavigator(n);
	    if (nav != null) {
		debug("   found");
		if (nav.canMerge(eviews[i])) {
		    debug("  canMerge: true; merging...");
		    nav.merge(eviews[i]);
		} else {
		    debug("  canMerge: false");
		}
	    } else {
		debug("   not found");
	    }
	}
	// In this version, we can only add views that appear at the top
    }

    /**
     * Removes a HelpSet from "our" HelpSet.
     */
    public void helpSetRemoved(HelpSetEvent e) {
	debug("helpSetRemoved("+e+")");
	HelpSet ehs = e.getHelpSet();
	removeHelpSet(ehs);
    }

    private void removeHelpSet(HelpSet ehs) {
	NavigatorView eviews[] = ehs.getNavigatorViews();

	for (int i=0; i<eviews.length; i++) {
	    String n = eviews[i].getName();
	    debug("removeHelpSet: looking for navigator for "+n);
	    JHelpNavigator nav = findNavigator(n);
	    if (nav != null) {
		debug("   found");
		if (nav.canMerge(eviews[i])) {
		    debug("  canMerge: true; removing...");
		    nav.remove(eviews[i]);
		} else {
		    debug("  canMerge: false");
		}
	    } else {
		debug("   not found");
	    }
	}
        
        // set the last displayed URL from Help. Set other than removed HelpSet, recount history 
        //helpModel.removeFromHistory(ehs);               
        //firePropertyChange("removeHelpSet",null,ehs);
        getHistoryModel().removeHelpSet(ehs);
    }

    /**
     * Visits a given ID.  Propagates down into the model.
     *
     * @param id The ID to visit. Null id is valid for TextHelpModel.setCurrentID.
     * @exception InvalidHelpSetContextException if id.hs is not contained in getHelpSet()
     */
    public void setCurrentID(ID id) throws InvalidHelpSetContextException {
	if (helpModel != null) {
	    helpModel.setCurrentID(id);
	}
    }
    
    /**
     * Visits a given ID.  Propagates down into the model.
     *
     * @param id The ID to visit. Null id is valid for TextHelpModel.setCurrentID.
     * @param historyName The name for history entry
     * @param navigator The JHelpNavigator
     * @exception InvalidHelpSetContextException if id.hs is not contained in getHelpSet()
     */ 
    public void setCurrentID(ID id, String historyName, JHelpNavigator navigator)throws InvalidHelpSetContextException{
        if (helpModel != null) {
	    helpModel.setCurrentID(id,historyName,navigator);
	}
    }
        
    /**
     * Convenience version of the above. The implicit HelpSet is
     * the current HelpSet.
     *
     * @param id The String to visit. Null id is valid for TextHelpModel.setCurrentID.
     * @exception BadIDException if the string is not in the map for the HelpSet.
     */
    public void setCurrentID(String id) throws BadIDException {
	try {
	    helpModel.setCurrentID(ID.create(id, getModel().getHelpSet()));
	} catch (InvalidHelpSetContextException ex) {
	    // cannot happen
	}
    }

    /**
     * Visits a given URL.  Propagates down into the model.
     *
     * @param url The URL to visit
     */
    public void setCurrentURL(URL url) {
	helpModel.setCurrentURL(url);
    }
    
    /**
     * Visits a given URL.  Propagates down into the model.
     *
     * @param url The URL to visit
     */
    public void setCurrentURL(URL url, String historyName, JHelpNavigator navigator) {
	helpModel.setCurrentURL(url, historyName, navigator);
    }

    /**
     * Returns a list of selected items from the current navigator
     */
    public TreeItem[] getSelectedItems() {
	    return getCurrentNavigator().getSelectedItems();
    }

    /**
     * Sets the list of selected items
     */
    /*
    public void setSelectedItems(TreeItem[] selectedItems) {
        getCurrentNavigator().setSelectedItems(selectedItems);
	firePropertyChange("SelectedItemsChangedProperty", oldValue, this.selectedItems);
    }
     */

    /**
     * A JHelp can have a number of navigators.
     * One of navigators is active.
     * How they are presented depends on the UI, but they may be a collection
     * of tabs, with the active tab being at the front.
     * <br>
     * Each navigator listens to changes to the HelpModel.
     * A navigator can also tell the model to change--the change
     * is propagated to the other navigators, this component, and
     * the content viewer if they all use the same
     * HelpModel instance.
     *
     * @param navigator The Navigator to explicitly add to the JHelp.
     */
    public void addHelpNavigator(JHelpNavigator navigator) {
	debug("addHelpNavigator("+navigator+")");
	navigators.addElement(navigator);
	HelpUI help = getUI();
	help.addNavigator(navigator);
	
	// force a common model
	navigator.setModel(getModel());
    }

    /**
     * Removes a navigator.
     *
     * @param navigator The Navigator to explicitly add to the JHelp. 
     */
    public void removeHelpNavigator(JHelpNavigator navigator) {
	debug("removeHelpNavigator("+navigator+")");
	if (navigator == null) {
	    throw new NullPointerException("navigator");
	}
	navigators.removeElement(navigator);
	HelpUI help = getUI();
	help.removeNavigator(navigator);
    }

    /**
     * @return An Enumeration of HelpNavigators in the HelpUI.
     */
    public Enumeration getHelpNavigators() {
	return navigators.elements();
    }

    /**
     * Sets the current navigator in the HelpUI.
     *
     * @param navigator The navigator
     * @exception throws InvalidNavigatorException if not a one of HELPUI
     *   navigators.
     */
    public void setCurrentNavigator(JHelpNavigator navigator) {
	HelpUI help = getUI();
	help.setCurrentNavigator(navigator);
    }

    /**
     * @return The current navigator in the HelpUI
     */
    public JHelpNavigator getCurrentNavigator() {
	HelpUI help = getUI();
	return help.getCurrentNavigator();
    }

    /**
     * Hidess/Displays the Navigators in the HelpUI.
     *
     * @displayed Whether to display or not
     */
    public void setNavigatorDisplayed(boolean displayed) {
	if (navDisplayed != displayed) {
	    navDisplayed = displayed;
	    firePropertyChange("navigatorDisplayed", !displayed, displayed);
	}
    }

    /**
     * Determines if the Navigators are hidden/displayed in the HelpUI.
     *
     * @return Are the navigators displayed?
     * @since 2.0
     */
    public boolean isNavigatorDisplayed() {
	return navDisplayed;
    }

    /**
     * Hidess/Displays the Toolbar in the HelpUI.
     *
     * @displayed Whether to display or not
     * @since 2.0
     */
    public void setToolbarDisplayed(boolean displayed) {
	if (toolbarDisplayed != displayed) {
	    toolbarDisplayed = displayed;
	    firePropertyChange("toolbarDisplayed", !displayed, displayed);
	}
    }

    /**
     * Determines if the Navigators are hidden/displayed in the HelpUI.
     *
     * @return is the toolbar displayed?
     * @since 2.0
     */
    public boolean isToolbarDisplayed() {
	return toolbarDisplayed;
    }

    /**
     * Retrieves what is the current content viewer
     * Read-Only property?
     */
    public JHelpContentViewer getContentViewer() {
	return contentViewer;
    }

    /**
     * Debug code
     */

    private boolean debug = false;
    private void debug(String msg) {
	if (debug) {
	    System.err.println("JHelp: "+msg);
	}
    }

    /*
     * Make sure the Look and Feel will be set for the Help Component
     */
    static {
	SwingHelpUtilities.installLookAndFeelDefaults();
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
            accessibleContext = new AccessibleJHelp();
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
    protected class AccessibleJHelp extends AccessibleJComponent {

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the
         * object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PANEL;
        }
    }
}

