/*
 * @(#)JHelpPrintHandler1_2.java	1.12 06/10/30
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

package com.sun.java.help.impl;

import javax.help.JHelp;
import javax.help.HelpModel;
import java.awt.Component;

/**
 * Print handler for JavaHelp. Handles all printer requests for printing
 * in JDK1.2 and above. Because JDK1.1 is not supported since JavaHelp version 2.0
 * all code was moved into JHelpPrintHandler. This class is preserved for backward 
 * compatibility.
 *
 * @author Roger D. Brinkley
 * @author Stepan Marek
 * @version   1.12     10/30/06
 */

public class JHelpPrintHandler1_2 extends JHelpPrintHandler {

    public JHelpPrintHandler1_2(JHelp help) {
        super(help);
    }    

    /*
    public JHelpPrintHandler1_2(HelpModel model) {
        super(model);
    }
     */
    
    /**
     * JHelpPrintHandler constructor. Creates a JHelpPrintHandler with a null
     * Document.
     * @param model The HelpModel to derive URLs from.
     * @param comp The component from which the Frame can be optained for 1.1 printing
     */
    /*
    public JHelpPrintHandler1_2(HelpModel model, Component comp) {
        super (model, comp);
    }
     */
}
