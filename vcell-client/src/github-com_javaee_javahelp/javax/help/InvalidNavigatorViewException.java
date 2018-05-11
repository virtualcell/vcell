/*
 * @(#)InvalidNavigatorViewException.java	1.14 06/10/30
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
import java.util.Locale;

/**
 * JHelpNavigator cannot deal with given NavigatorView.
 *
 * @author Eduardo Pelegri-Llopart
 * @version	1.14	10/30/06
 */

public class InvalidNavigatorViewException extends Exception 
{
    /**
     * Create an exception. All parameters accept null values.
     * 
     * @param msg The message. If msg is null it is the same as if
     * no detailed message was specified.
     */
    public InvalidNavigatorViewException(String msg,
					 HelpSet hs,
					 String name,
					 String label,
					 Locale locale,
					 String className,
					 Hashtable params) {
	super(msg);
	this.hs = hs;
	this.name = name;
	this.label = label;
	this.locale = locale;
	this.className = className;
	this.params = params;
    }

    /**
     * @return The helpset
     */
    public HelpSet getHelpSet() {
	return hs;
    }

    /**
     * @return The name
     */
    public String getName() {
	return name;
    }

    /**
     * @return The label
     */
    public String getLabel() {
	return label;
    }

    /**
     * @return The locale
     */
    public Locale getLocale() {
	return locale;
    }

    /**
     * @return The className
     */
    public String getClassName() {
	return className;
    }

    /**
     * @return The parameters
     */
    public Hashtable getParams() {
	return params;
    }

    private HelpSet hs;
    private String name;
    private String label;
    private Locale locale;
    private String className;
    private Hashtable params;
}
