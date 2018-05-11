/*
 * @(#)HelpModel.java	1.27 06/10/30
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
import javax.help.event.*;
import javax.help.Map.ID;
import java.beans.*;

/**
 * The interface to the model of a JHelp that represents the 
 * HelpSet being presented to the user.
 * 
 * Note that a HelpSet can contain nested HelpSets within it; IDs
 * include both a String and the HelpSet to which the String applies.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @author Richard Gregor
 * @version   1.27     10/30/06
 */
public interface HelpModel {
    /**
     * Sets the loaded (aka "top") HelpSet for this model.
     */
    public void setHelpSet(HelpSet hs);

    /**
     * Gets the loaded (aka "top") HelpSet for this model.
     */
    public HelpSet getHelpSet();

    /**
     * Sets the current ID relative to some HelpSet
     * HelpModelListeners and HelpVisitListeners are notified
     *
     * @param id the ID used to set
     * @exception InvalidHelpSetContextException The HelpSet of the ID is not
     * valid for the HelpSet currently loaded in the model
     */
    public void setCurrentID(ID id) throws InvalidHelpSetContextException;

    /**
     * Sets the current ID relative to some HelpSet
     * HelpModelListeners and HelpVisitListeners are notified
     *
     * @param id the ID used to set
     * @param historyName The name for history storage
     * @param navigator The JHelpNavigator
     * @exception InvalidHelpSetContextException The HelpSet of the ID is not
     * valid for the HelpSet currently loaded in the model
     */
    public void setCurrentID(ID id, String historyName, JHelpNavigator navigator) throws InvalidHelpSetContextException;
    
    /**
     * Gets the current ID.
     *
     * @return The current ID.
     */
    public ID getCurrentID();

    /**
     * Sets the current URL. 
     * HelpModelListeners are notified.
     * The current ID changes if there is a matching id for this URL
     *
     * @param The URL to set.
     */
    public void setCurrentURL(URL url);

    /**
     * Sets the current URL and the name wich will appear in history list.
     * HelpModelListeners are notified.
     * The current ID changes if there is a matching id for this URL
     *
     * @param url The URL to set.
     * @param historyName The name to set for history
     * @param navigator The JHelpNavigator
     */
    public void setCurrentURL(URL url, String historyName, JHelpNavigator navigator);
    
    /**
     * Returns The current URL.
     *
     * @return The current URL.
     */
    public URL getCurrentURL();

    /**
     * Adds a listener for the HelpModelEvent posted after the model has
     * changed.
     * 
     * @param l The listener to add.
     * @see javax.help.HelpModel#removeHelpModelListener
     */
    public void addHelpModelListener(HelpModelListener l);

    /**
     * Removes a listener previously added with <tt>addHelpModelListener</tt>
     *
     * @param l The listener to remove.
     * @see javax.help.HelpModel#addHelpModelListener
     */
    public void removeHelpModelListener(HelpModelListener l);

    /**
     * Adds a listener to monitor changes to the properties in this model
     *
     * @param l  The listener to add.
     */
    public void addPropertyChangeListener(PropertyChangeListener l);

    /**
     * Removes a listener monitoring changes to the properties in this model
     *
     * @param l  The listener to remove.
     */
    public void removePropertyChangeListener(PropertyChangeListener l);
}
