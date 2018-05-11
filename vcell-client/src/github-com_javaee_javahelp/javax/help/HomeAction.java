/*
 * @(#)HomeAction.java	1.4 06/10/30
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
public class HomeAction extends AbstractHelpAction implements ActionListener {
    
    private static final String NAME = "HomeAction";
    
    /** Creates new HomeAction */
    public HomeAction(Object control) {
        super(control, NAME);
        
        putValue("icon", UIManager.getIcon(NAME + ".icon"));
        
        if (control instanceof JHelp) {
	    JHelp help = (JHelp) control;
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
    
    public void actionPerformed(java.awt.event.ActionEvent event) {
        try {
            JHelp help = (JHelp)getControl();
            HelpSet hs = help.getModel().getHelpSet();
            Map.ID homeID = hs.getHomeID();
            Locale locale = hs.getLocale();
            String string = HelpUtilities.getString(locale, "history.homePage");
            help.setCurrentID(homeID, string, help.getCurrentNavigator());
        } catch (Exception e) {
        }
    }
}
