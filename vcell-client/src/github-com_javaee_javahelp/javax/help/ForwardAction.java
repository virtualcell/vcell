/*
 * @(#)ForwardAction.java	1.4 06/10/30
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

import java.awt.*;
import java.awt.event.*;
import javax.help.*;
import javax.help.event.*;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Stack;
import javax.swing.*;

/**
 *
 * @author Stepan Marek
 * @version	1.4	10/30/06
 */
public class ForwardAction extends AbstractHelpAction implements MouseListener, HelpHistoryModelListener {

    private static final String NAME = "ForwardAction";
    
    private static final int DELAY = 500;    
    private Timer timer;
    private HelpHistoryModel historyModel;
    
    /** Creates new ForwardAction */
    public ForwardAction(Object control) {
        super(control, NAME);
        if (control instanceof JHelp) {
            JHelp help = (JHelp)control;            
            historyModel = help.getHistoryModel();
            historyModel.addHelpHistoryModelListener(this);
            
            setEnabled(historyModel.getIndex() > 0);
            
            putValue("icon", UIManager.getIcon(NAME + ".icon"));
            
	    Locale locale = null;
	    try {
		locale = help.getModel().getHelpSet().getLocale();
	    } catch (NullPointerException npe) {
		locale = Locale.getDefault();
	    }
            putValue("tooltip", HelpUtilities.getString(locale, "tooltip." + NAME));
            putValue("access", HelpUtilities.getString(locale, "access." + NAME));            
        }
    }

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {
    }    
    
    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
        if (timer != null) {
            timer.stop();
        }
    }
    
    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {
        timer = new Timer(DELAY, new TimeListener(e));
        timer.start();
    }
    
    /**
     * Invoked when the mouse has been clicked on a component.
     */
    public void mouseClicked(MouseEvent e) {
        if ((historyModel != null) && isEnabled()) {
            historyModel.goForward();
        }
    }
    
    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {
    }

    private class TimeListener implements ActionListener {
        
        private MouseEvent e;
        
        public TimeListener(MouseEvent e) {
            this.e = e;
        }
        
        public void actionPerformed(ActionEvent evt){
            timer.stop();
            if (ForwardAction.this.isEnabled()) {
                ForwardAction.this.showForwardHistory(e);
            }
        }
    }

    private class HistoryActionListener implements ActionListener {
        
        private int index;
        
        public HistoryActionListener(int index) {
            this.index = index;          
        }
        
        public void actionPerformed(java.awt.event.ActionEvent event) {
            if(historyModel != null) {
                historyModel.setHistoryEntry(index);
            }
        }        
    }

    /**
     * Shows popup with forward history entries
     */
    private void showForwardHistory(MouseEvent e) {
        
        JPopupMenu forwardMenu = new JPopupMenu("Forward History");
        
        if (historyModel == null) {
            return;
        }
        
        Locale locale = ((JHelp)getControl()).getModel().getHelpSet().getLocale();
        Enumeration items = historyModel.getForwardHistory().elements();
        JMenuItem mi = null;
        int index = historyModel.getIndex() + 1;
        //while(items.hasMoreElements()){
        for(int i = 0; items.hasMoreElements(); i++) {
            HelpModelEvent item = (HelpModelEvent) items.nextElement();
            if(item != null) {
                String title = item.getHistoryName();
                if (title == null) {                    
                    title = HelpUtilities.getString(locale, "history.unknownTitle");
                }
                mi = new JMenuItem(title);
                //mi.setToolTipText(item.getURL().getPath());
                mi.addActionListener(new HistoryActionListener(i + index));
                forwardMenu.add(mi);
            }
        }        
       // if(e.isPopupTrigger())
        forwardMenu.show(e.getComponent(),e.getX(),e.getY());
        
    }

    /**
     * Tells the listener that the history has changed.
     * Will enable/disable the Action depending on the events previous flag
     *
     * @param e The HelpHistoryModelEvent
     */
    public void historyChanged(HelpHistoryModelEvent e) {
        setEnabled(e.isNext());
    }
    
}
