/*
 * @(#)SearchView.java	1.10 06/10/30
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

import java.util.Hashtable;
import java.awt.Component;
import java.util.Locale;

/**
 * Navigational View information for a Search
 *
 * @author Eduardo Pelegri-Llopart
 * @version	1.5	01/29/99
 */

public class SearchView extends NavigatorView {
    /**
     * Constructs a SearchView with some given data.  Locale of the View defaults
     * to that of the HelpSet.
     *
     * @param hs The HelpSet that provides context information.
     * @param name The name of the View.
     * @param label The label (to show the user) of the View.
     * @param locale The default locale to interpret the data in this TOC.
    * @param params A hashtable that provides different key/values for this type.
    */
    public SearchView(HelpSet hs,
		      String name,
		      String label,
		      Hashtable params) {
	super(hs, name, label, hs.getLocale(), params);
    }

    /**
     * Constructs a SearchView with some given data.
     *
     * @param hs The HelpSet that provides context information.
     * @param name The name of the View.
     * @param label The label (to show the user) of the View.
     * @param locale The default locale to interpret the data in this TOC.
    * @param params A hashtable that provides different key/values for this type.
    */
    public SearchView(HelpSet hs,
		      String name,
		      String label,
		      Locale locale,
		      Hashtable params) {
	super(hs, name, label, locale, params);
    }

    /**
     * Creates a navigator for a given model.
     */
    public Component createNavigator(HelpModel model) {
	return new JHelpSearchNavigator(this, model);
    }
    

}
