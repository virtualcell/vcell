/*
 * @(#)HelpHistoryModel.java	1.3 06/10/30
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
 * History Model for HelpModel
 *
 * @author  Richard Gregor
 * @version   1.3     10/30/06
 */

import java.awt.event.ActionListener;
import javax.help.event.*;
import java.io.Serializable;
import java.beans.*;
import java.util.Vector;
import javax.help.Map.ID;
import java.util.Enumeration;

/**
 * The interface to the history model.
 */
public interface HelpHistoryModel extends HelpModelListener, Serializable{
    /**
     * Adds a listener for the HelpHistoryModelEvent posted after the model has
     * changed.
     * 
     * @param l The listener to add.
     * @see javax.help.HelpHistoryModel#removeHelpHistoryModelListener
     */
    public void addHelpHistoryModelListener(HelpHistoryModelListener l);

    /**
     * Removes a listener previously added with <tt>addHelpHistoryModelListener</tt>
     *
     * @param l The listener to remove.
     * @see javax.help.HelpHistoryModel#addHelpHistoryModelListener
     */
    public void removeHelpHistoryModelListener(HelpHistoryModelListener l);
       
    /**
     * Discards a history
     */
    public void discard();
    /**
     * Sets a next history entry
     */
    public void goForward();
    
    /**
     * Sets a previous history entry
     */
    public void goBack();
    
    /**
     * Returns a backward history list
     */
    public Vector getBackwardHistory();
    
    /**
     * Returns a forward history list
     */
    public Vector getForwardHistory();
    
    /**
     * Sets the current history entry
     *
     * @param index The index of history entry
     */
    public void setHistoryEntry(int index);
    
    /**
     * Removes entries related to removed HelpSet from history
     *
     * @param hs The removed HelpSet
     */
    public void removeHelpSet(HelpSet hs);
    
    /**
     * Returns a history
     */
    public Vector getHistory();
    
    /**
     * Returns a current history position
     *
     * @return The history index
     */
    public int getIndex();
    
    /**
     * Sets the HelpModel
     *
     * @param model The HeplModel
     */
    public void setHelpModel(HelpModel model);
    

}

