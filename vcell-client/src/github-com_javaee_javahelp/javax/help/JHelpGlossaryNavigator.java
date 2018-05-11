/*
 * @(#)JHelpGlossaryNavigator.java	1.2 06/10/30
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

import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.UIDefaults;
import javax.swing.LookAndFeel;
import javax.help.*;
import java.beans.*;

/**
 * JHelpGlossaryNavigator is a JHelpNavigator for a Glossary
 *
 * @author Roger Brinkley
 * @author Richard Gregor
 * @version   1.2     10/30/06
 */

public class JHelpGlossaryNavigator extends JHelpNavigator {

    /**
     * Creates JHelpGlossaryNavigator from given GlossaryView
     *
     * @param info The GlossaryView
     */
    public JHelpGlossaryNavigator(NavigatorView info) {
	super(info);
    }
    /**
     * Creates JHelpGlossaryNavigator from given GlossaryView and HelpModel
     *
     * @param info The GlossaryView
     * @param model The HelpModel
     */
    public JHelpGlossaryNavigator(NavigatorView info, HelpModel model) {
	super(info, model);
    }

    /**
     * Creates JHelpGlossaryNavigator from given HelpSet, name and title
     *
     * @param hs The HelpSet
     * @param name The name of GlossaryView
     * @param title The title
     */

    public JHelpGlossaryNavigator(HelpSet hs, String name, String title)
	throws InvalidNavigatorViewException
    {
	super(new GlossaryView(hs, name, title, null));
    }


    /**
     * Returns UIClassID
     *
     * @return The ID of UIClass representing JHelpGlossaryNavigator
     */
    public String getUIClassID() {
	return "HelpGlossaryNavigatorUI";
    }



    private static final boolean debug = false;
    private static void debug(Object m1, Object m2, Object m3) {
	if (debug) {
	    System.err.println("JHelpGlossaryNavigator: "+m1+m2+m3);
	}
    }
    private static void debug(Object m1) { debug(m1,null,null); }
    private static void debug(Object m1, Object m2) { debug(m1,m2,null); }
}
