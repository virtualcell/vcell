/*
 * @(#)DefaultHelpHistoryModel.java	1.4 06/10/30
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

import java.beans.*;
import java.util.Vector;
import javax.help.event.*;
import javax.help.Map.ID;
import java.util.Enumeration;
import java.net.URL;
import java.util.Stack;
/**
 * DefaultHelpHistoryModel is default implementation of HelpHistoryModel interface
 *
 * @author  Richard Gregor
 * @version   1.4     10/30/06
 */

public class DefaultHelpHistoryModel implements HelpHistoryModel{
    
    protected Vector history = new Vector();
    protected int historyIndex = -1;
    protected HelpModel helpModel;
    protected EventListenerList listenerList = new EventListenerList();
    protected JHelp help;
    
    /** 
     * Creates new DefaultHelpHistoryModel for given JHelp
     *
     * @param help The JHelp
     */
    public DefaultHelpHistoryModel(JHelp help) {
        this.help = help;
        if (help != null){
            setHelpModel(help.getModel());            
        }
    }
  
    /**
     * Sets a new HelpModel
     *
     * @param model The new HelpModel
     */
    public void setHelpModel(HelpModel model){
        if(model == helpModel)
            return;
        else
            discard();
        if(helpModel != null)
            helpModel.removeHelpModelListener(this);
        if(model != null)
            model.addHelpModelListener(this);
         helpModel = model;        
    }
        
            
    /**
     * Removes a listener previously added with <tt>addHelpHistoryModelListener</tt>
     *
     * @param l The listener to remove.
     * @see javax.help.HelpHistoryModel#addHelpHistoryModelListener
     */
    public void removeHelpHistoryModelListener(HelpHistoryModelListener l) {
        listenerList.remove(HelpHistoryModelListener.class, l);
    }
    
    /**
     * Adds a listener for the HelpHistoryModelEvent posted after the model has
     * changed.
     *
     * @param l The listener to add.
     * @see javax.help.HelpHistoryModel#removeHelpHistoryModelListener
     */
    public void addHelpHistoryModelListener(HelpHistoryModelListener l) {
        listenerList.add(HelpHistoryModelListener.class, l);
    }
    
    /**
     * Discards a history
     */
    public void discard() {
        history.setSize(0);
        historyIndex = -1;
        fireHistoryChanged(this,false,false);
    }
    
    /**
     * Returns a history
     *
     * @return The vector of history entries
     */
    public Vector getHistory(){
        return history;
    }
    
    /**
     * Removes the last history entry
     *
     */
    public void removeLastEntry(){
        int size = history.size();
        if(size > 0)
            history.removeElementAt(history.size() -1);
    }
    /**
     * Returns a current history position
     *
     * @return The history index
     */
    public int getIndex(){
        return historyIndex;
    }
    
    /**
     * Sets the next history entry
     */
    public void goForward(){
        setHistoryEntry(historyIndex+1);
    }
    
    /**
     * Sets the previous histroy entry
     */
    public void goBack(){
        setHistoryEntry(historyIndex-1);
    }
    
    /**
     * Returns a forward history vector
     *
     * @return The vector of forward history entries
     */
    public Vector getForwardHistory() {
        Vector historyCopy = (Vector)history.clone();
        Vector forwardHistory = new Vector();
        int size = historyCopy.size();
        debug(" forward size : "+size);
        for(int i = historyIndex + 1 ; i < size; i++){
            forwardHistory.addElement(historyCopy.elementAt(i)); 
        }
        return forwardHistory;
    }
    
    /**
     * Returns a backward history vector
     *
     * @return The vector of backward history entries
     */
    public Vector getBackwardHistory() {        
        Vector backwardHistory = new Vector();
        Vector historyCopy = (Vector)history.clone();
        debug(" backward size : "+historyCopy.size());
        debug(" backward index : "+historyIndex);
        if(historyCopy != null){
            for(int i = 0 ; i < historyIndex; i++){            
                backwardHistory.addElement(historyCopy.elementAt(i));
            }
        }
        
        return backwardHistory;        
    }
    
    /**
     * Sets the current history entry
     *
     * @param index The index of history entry
     */
    public void setHistoryEntry(int index) {
        debug("setHistoryEntry("+index+")");
        
        
        if (helpModel == null) {
            return;
        }
        
        if (index < 0 || index >= history.size()) {
            // invalid index
            discard();
            return;
        }
        
        HelpModelEvent e = (HelpModelEvent) history.elementAt(index);
        // set the historyIndex so it is ready to take the next event...
        historyIndex = index-1;
        ID id = e.getID();
        URL url = e.getURL();
        JHelpNavigator navigator = e.getNavigator();
        if (id != null) {
            // try to set the ID
            try {
                debug("  setCurrentID"+id);
                helpModel.setCurrentID(id, e.getHistoryName(),e.getNavigator());
                if(navigator != null)
                    help.setCurrentNavigator(navigator);
                return;
            } catch (Exception ex) {
                // fall through
            }
        }
        if (url != null) {
            // try to set the URL
            try {
                debug("  setCurrentURL"+url);
                helpModel.setCurrentURL(url,e.getHistoryName(),e.getNavigator());
                if(navigator != null)
                    help.setCurrentNavigator(navigator);                    
                return;
            } catch (Exception ex) {
                // fall through
            }
        }       
            
        // this really should not happen but...
        discard();    
    }
    /**
     * Fires the history change
     */
    protected void fireHistoryChanged(Object source, boolean previous, boolean next) {
	Object[] listeners = listenerList.getListenerList();
	HelpHistoryModelEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == HelpHistoryModelListener.class) {
		if (e == null) {
		    e = new HelpHistoryModelEvent(source, previous, next);
		}
		debug("fireHistoryChanged: ");
		debug("  "+listeners[i+1]);
		debug("  previous="+e.isPrevious() + " next="+e.isNext());
		((HelpHistoryModelListener)listeners[i+1]).historyChanged(e);
	    }	       
	}
    }
    
    
    /**
     * Tells the listener that the current ID in the HelpModel has
     * changed.
     * All highlights from the previous location change at this point.
     *
     * @param e The event
     */
    public void idChanged(HelpModelEvent e) {
        debug("idChanged("+e+")");
        debug("  historyIndex=="+historyIndex);
        debug("  history.size=="+history.size());
        
        if (historyIndex == history.size()-1) {
            // we are at the end
            // (this covers the initial case of historyIndex == -1
            history.addElement(e);
            historyIndex += 1;
            fireHistoryChanged(this,(historyIndex > 0),(historyIndex < history.size()-1));           
            return;
        }
        
        if (historyIndex >= -1 &&
        historyIndex < history.size()-1) {
            // check the next slot where to record
            
            historyIndex += 1;	// advance
            HelpModelEvent h
            = (HelpModelEvent) history.elementAt(historyIndex);
            
            if (h == null) {
                // this really should not happen
                discard();
                return;
            }
            
            // compare ID's first
            if (h.getID() != null &&	e.getID() != null &&
            h.getID().equals(e.getID())) {
                // we are were we wanted to be, just return
                fireHistoryChanged(this,(historyIndex > 0),(historyIndex < history.size()-1));
                return;
            }
            
            // compare URL's now
            if (h.getURL() != null && e.getURL() != null &&
            h.getURL().sameFile(e.getURL())) {
                // we are were we wanted to be, just return
                fireHistoryChanged(this,(historyIndex > 0),(historyIndex < history.size()-1));
                return;
            }
            
            // new location is different, so throw away object and the rest
            history.setSize(historyIndex);
            // add the new element
            history.addElement(e);
            fireHistoryChanged(this,(historyIndex > 0),(historyIndex < history.size()-1));
        }
    }
    
    /**
     * Removes entries related to removed HelpSet from history
     *
     * @param hs The removed HelpSet
     */
    public void removeHelpSet(HelpSet hs){
        Enumeration e = history.elements();
        debug(" size before " +history.size());
        
        if(debug){
            System.err.println("before : ");
            for(int j = 0; j < history.size(); j++){
                System.err.println(((HelpModelEvent)history.elementAt(j)).getID());
            }
        }
        int size = history.size();
        Vector newHistory = new Vector();
        int index = historyIndex;
        
        for(int i = 0; i < size; i++){
            HelpModelEvent evt = (HelpModelEvent)history.elementAt(i);
            ID id = evt.getID();
            debug(" update id "+id);
            URL url = evt.getURL();
            debug(" update url " +url);
            if ((id != null) && (id.hs != hs)) {
                debug(" remain - "+id);
                newHistory.addElement(history.elementAt(i));
            }
            else if (url != null) {
                //find out if there is a matching ID for this URL
                ID idcan = hs.getCombinedMap().getIDFromURL(url);
                if(idcan == null){
                    debug(" remain > "+idcan);
                    newHistory.addElement(history.elementAt(i));
                }
            }
        }
        
        history = newHistory;
        historyIndex = history.size() -1;
        
        debug(" size after " + history.size());
        if(debug){
            System.err.println("after : ");
            for(int j = 0; j < history.size(); j++){
                System.err.println(((HelpModelEvent)history.elementAt(j)).getID());
            }
        }
        setHistoryEntry(historyIndex);
        //computeHistoryButtons();
        
    }   

    /**
     * Returns HelpModel
     *
     * @return The HelpModel
     */
    protected HelpModel getModel(){
        return helpModel;
    }
    /**
     * For printf debugging.
     */
    private static boolean debug = false;
    private static void debug(String str) {
        if (debug) {
            System.out.println("BasicHelpUI: " + str);
        }
    }    
}
