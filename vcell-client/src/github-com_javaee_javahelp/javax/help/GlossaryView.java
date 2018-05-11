/*
 * @(#)GlossaryView.java	1.2 06/10/30
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

import java.awt.Component;
import java.util.Hashtable;
import java.util.Locale;
import javax.help.HelpSet;
import javax.help.HelpModel;
import javax.help.IndexView;

/**
 * View information for a Glossary Navigator
 *
 * @author	Roger Brinkley
 * @version   1.2     10/30/06
 */

public class GlossaryView extends IndexView {
    /**
     * Construct a GlossaryView with some given data.  Locale defaults
     * to that of the HelpSet
     *
     * @param hs The HelpSet that provides context information
     * @param name The name of the View
     * @param label The label (to show the user) of the View
     * @param params A Hashtable providing different key/values for this type
     */
    public GlossaryView(HelpSet hs,
			String name,
			String label,
			Hashtable params) {
	super(hs, name, label, hs.getLocale(), params);
    }

    /**
     * Construct a GlossaryViewer VIew with some given data.
     *
     * @param hs The HelpSet that provides context information
     * @param name The name of the View
     * @param label The label (to show the user) of the View
     * @param locale The default locale to interpret data in this View
     * @param params A Hashtable providing different key/values for this type
     */
    public GlossaryView(HelpSet hs,
			String name,
			String label,
			Locale locale,
			Hashtable params) {
	super(hs, name, label, locale, params);
    }

    /**
     * create a navigator for a given model
     */
    public Component createNavigator(HelpModel model) {
	return new JHelpGlossaryNavigator(this, model);
    }
}




