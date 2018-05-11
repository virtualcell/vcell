/*
 * @(#)NavigatorView.java	1.28 06/10/30
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

import java.util.Locale;
import java.util.Hashtable;
import java.awt.Component;
import java.lang.reflect.*;
import java.io.Serializable;

/**
 * Navigational View information
 *
 * @author Eduardo Pelegri-Llopart
 * @author Stepan Marek
 * @version	1.28	10/30/06
 */

public abstract class NavigatorView implements Serializable{
    private HelpSet hs;

    private String name;
    private String label;
    private Locale locale;
    private Hashtable params;
    private Map.ID imageID;
    private String mergeType;

    /**
     * Constructs a Navigator with some given data.  This is protected
     * so subclasses can use it.
     *
     * @param hs The HelpSet that provides context information.
     * @param name The name of the View.
     * @param label The label (to show the user) of the View.
     * @param locale The default locale to interpret the data in this View.
     * @param params A hashtable that provides different key/values for this type.
     */

    protected NavigatorView(HelpSet hs,
			    String name,
			    String label,
			    Locale locale,
			    Hashtable params) 
    {
	if (name == null || label == null) {
	    throw new NullPointerException("Invalid name or label");
	}
        this.imageID = null;
        if (params != null) {
            String imageID = (String)params.get("imageID");
            if (imageID != null)
                this.imageID = Map.ID.create(imageID, hs);
            this.mergeType = (String)params.get("mergetype");
        }
        this.hs = hs;
	this.name = name;
	this.label = label;
	this.locale = locale;
	this.params = params;
    }

    /**
     * Creates a NavigatorView for some given information.
     * The type is used to determine a NavigatorView class within the
     * ClassLoader.
     * 
     * @param hs The HelpSet that provides context information.
     * @param name The name of the View.
     * @param label The label (to show the user) of the View.
     * @param className The type for the View (its class name).
     * @param params A hashtable that provides different key/values for this type.
     * @return The desired NavigatorView object.
     * @throws InvalidNavigatorViewException if <tt>hs</tt>, <tt>name</tt>, 
     * <tt>label</tt>, <tt>locale</tt>, <tt>className</tt>, or <tt>params</tt> 
     * are null, or if a valid NavigatorView cannot be constructed from the
     * parameters.
     */
    public static NavigatorView create(HelpSet hs,
				       String name,
				       String label,
				       Locale locale,
				       String className,
				       Hashtable params)
	throws InvalidNavigatorViewException
    {
	ClassLoader loader;
	Class klass;
	NavigatorView back;

        try {
	    loader = hs.getLoader();
            Constructor konstructor;
	    Class types[] = { HelpSet.class,
			      String.class, String.class, Locale.class,
			      Hashtable.class };
	    Object args[] = { hs,
			      name, label, locale,
			      params };
	    if (loader == null) {
	        klass = Class.forName(className);
	    } else {
	        klass = loader.loadClass(className);
	    }
	    konstructor = klass.getConstructor(types);
	    back = (NavigatorView) konstructor.newInstance(args);
	    return back;
	} catch (Exception ex) {
	    throw new InvalidNavigatorViewException("Could not create",
						    hs,
						    name, label, locale,
						    className, params);
	}
    }

    /**
     * Creates a navigator for a given model.  Really a JHelpNavigator right now.
     *
     * @param model The model for the Navigator.
     */
    public abstract Component createNavigator(HelpModel model);

 

    /**
     * Gets the HelpSet for this Navigator view.
     *
     * @return the HelpSet
     */
    public HelpSet getHelpSet() {
	return hs;
    }

    /**
     * Gets the name of this Navigator view.
     *
     * @return the Name of the view
     */
    public String getName() {
	return name;
    }

    /**
     * Gets the locale-dependent name of this View.
     *
     * @return The locale-dependent name of this view.
     */
    public String getLabel(Locale locale) {
	return getLabel();
    }

    /**
     * Gets the locale-dependent name of this View 
     *
     * @return The locale-dependent name of this view
     */
    public String getLabel() {
	return label;
    }

    /**
     * @return The locale.
     */
    public Locale getLocale() {
	return locale;
    }

    /**
     * Gets parameters passed to this View.
     *
     * @return The parameters passed to this View.
     */
    public Hashtable getParameters() {
	return params;
    }
    
    /**
     * Gets merge type of this Navigator view
     *
     * @return The merge type of this Navigator view
     */ 
    public String getMergeType(){
        return mergeType;
    }

    /**
     * Returns Map ID of icons passed to this View.
     *
     * @return the Map ID of icons passed to this View
     */
    public Map.ID getImageID() {
        return imageID;
    }
}
