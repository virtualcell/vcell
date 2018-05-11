/*
 * @(#)BasicIndexCellRenderer.java	1.21 06/10/30
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

package javax.help.plaf.basic;

import javax.help.*;
import javax.swing.tree.*;
import javax.swing.JTree;
import java.awt.Component;
import java.util.Locale;

/**
 * Cell Renderer for the index UI.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @author Stepan Marek
 * @version   %I     10/30/06
 */
public class BasicIndexCellRenderer extends DefaultTreeCellRenderer {
    
    /**
      * Configures the renderer based on the passed in components.
      * The value is set from messaging the tree with
      * <code>convertValueToText</code>, which ultimately invokes
      * <code>toString</code> on <code>value</code>.
      * The foreground color is set based on the selection and the icon
      * is set based on on leaf and expanded.
      */
    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object value,
						  boolean sel,
						  boolean expanded,
						  boolean leaf, int row,
						  boolean hasFocus) {

        // variable hasFocus was private to DefaultTreeCellRenderer since jdk1.3
        try {
            this.hasFocus = hasFocus;
        } catch (IllegalAccessError e) {
        }
        
    	IndexItem item
	    = (IndexItem) ((DefaultMutableTreeNode) value).getUserObject();

	String stringValue = "";

	if (item != null) {
	    stringValue = item.getName();
	}

        setText(stringValue);
        if (sel)
            setForeground(getTextSelectionColor());
        else
            setForeground(getTextNonSelectionColor());

        setIcon(null);
        
        selected = sel;

	// Set the locale of this if there is a lang value
	if (item != null) {
	    Locale locale = item.getLocale();
	    if (locale != null) {
		setLocale(locale);
	    }
	}

	return this;
    }
    
}
