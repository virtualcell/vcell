/*
 * @(#)HelpUI.java	1.21 06/10/30
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

package javax.help.plaf;

import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.*;
import javax.help.JHelpNavigator;
import java.util.Enumeration;

/**
 * UI factory interface for JHelp.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version   1.21     10/30/06
 */

public abstract class HelpUI extends ComponentUI {

    /**
     * Adds a Navigator.
     *
     * @param nav the Navigator to add
     */
   public abstract void addNavigator(JHelpNavigator nav);

    /**
     * Remove a Navigator.
     *
     * @param nav The Navigator to remove.
     */
   public abstract void removeNavigator(JHelpNavigator nav);

    /**
     * Sets the current Navigator.
     *
     * @param nav The current Navigator to show.
     */
    public abstract void setCurrentNavigator(JHelpNavigator nav);

    /**
     * Gets the current Navigator.
     *
     * @param nav The current Navigator to show.
     */
    public abstract JHelpNavigator getCurrentNavigator();

}
