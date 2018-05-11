/*
 * @(#)JHSecondaryViewerBeanInfo.java	1.7 06/10/30
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
import java.beans.*;

/**
 * This class provides information about getter/setter methods within 
 * JHSecondaryWindow. It is usefull for reflection.
 * @see JHSecondaryViewer
 *
 * @author Roger D. Brinkley
 * @version	1.7	10/30/06
 */
public class JHSecondaryViewerBeanInfo extends SimpleBeanInfo {

    public JHSecondaryViewerBeanInfo() {
    }
    
    public PropertyDescriptor[] getPropertyDescriptors() {
	PropertyDescriptor back[] = new PropertyDescriptor[15];
	try {
	    back[0] = new PropertyDescriptor("content", JHSecondaryViewer.class);
	    back[1] = new PropertyDescriptor("id", JHSecondaryViewer.class);
	    back[2] = new PropertyDescriptor("viewerName", JHSecondaryViewer.class);
	    back[3] = new PropertyDescriptor("viewerActivator", JHSecondaryViewer.class);
	    back[4] = new PropertyDescriptor("viewerStyle", JHSecondaryViewer.class);
	    back[5] = new PropertyDescriptor("viewerLocation", JHSecondaryViewer.class);
	    back[6] = new PropertyDescriptor("viewerSize", JHSecondaryViewer.class);
	    back[7] = new PropertyDescriptor("iconByName", JHSecondaryViewer.class);
	    back[8] = new PropertyDescriptor("iconByID", JHSecondaryViewer.class);
	    back[9] = new PropertyDescriptor("text", JHSecondaryViewer.class);
	    back[10] = new PropertyDescriptor("textFontFamily", JHSecondaryViewer.class);
	    back[11] = new PropertyDescriptor("textFontSize", JHSecondaryViewer.class);
	    back[12] = new PropertyDescriptor("textFontWeight", JHSecondaryViewer.class);
	    back[13] = new PropertyDescriptor("textFontStyle", JHSecondaryViewer.class);
	    back[14] = new PropertyDescriptor("textColor", JHSecondaryViewer.class);
	    return back;
	} catch (Exception ex) {
	    return null;
	}
    }
}
