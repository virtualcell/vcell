/*
 * @(#)BasicTOCCellRenderer.java	1.25 06/10/30
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
/*
 * @(#) BasicTOCCellRenderer.java 1.25 - last change made 10/30/06
 */

package javax.help.plaf.basic;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.net.URL;
import java.util.Locale;
import javax.help.TOCItem;
import javax.help.TOCView;
import javax.help.Map;
import javax.help.HelpUtilities;
import javax.help.Map.ID;

/**
 * Basic cell renderer for TOC UI.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version   1.25     10/30/06
 */

public class BasicTOCCellRenderer extends DefaultTreeCellRenderer
{
    protected Map map;
    protected TOCView view;

    public BasicTOCCellRenderer(Map map) {
	this(map, null);
    }
	
    public BasicTOCCellRenderer(Map map, TOCView view) {
	super();
	this.map = map;
	this.view = view;
    }

    /**
      * Configures the renderer based on the components passed in.
      * Sets the value from messaging value with toString().
      * The foreground color is set based on the selection and the icon
      * is set based on on leaf and expanded.
      */
    public Component getTreeCellRendererComponent(JTree tree, Object value,
						  boolean sel,
						  boolean expanded,
						  boolean leaf, int row,
						  boolean hasFocus)
    {

        String stringValue = "";

        // variable hasFocus was private to DefaultTreeCellRenderer since jdk1.3
        try {
            this.hasFocus = hasFocus;
        } catch (IllegalAccessError e) {
        }

        TOCItem item
	    = (TOCItem) ((DefaultMutableTreeNode) value).getUserObject();

	if (item != null) {
	    stringValue = item.getName();
	}

	setText(stringValue);
	if (sel) {
	    setForeground(getTextSelectionColor());
	} else {
	    setForeground(getTextNonSelectionColor());
	}

	ImageIcon icon = null;
	if (item != null) {
	    ID id = item.getImageID();
	    if (id != null) {
		try {
		    URL url = map.getURLFromID(id);
		    icon = new ImageIcon(url);
		} catch (Exception e) {
		}
	    }
	}

	// Set the locale of this if there is a lang value
	if (item != null) {
	    Locale locale = item.getLocale();
	    if (locale != null) {
		setLocale(locale);
	    }
	}

	// determine which icon to display
	if (icon != null) {
	    setIcon(icon);
	} else if (leaf) {
	    setIcon(getLeafIcon());
	} else if (expanded) {
	    setIcon(getOpenIcon());
	} else {
	    setIcon(getClosedIcon());
	}
	    
	selected = sel;

	return this;
    }

    public Icon getLeafIcon() {
	Icon icon = null;
	if (view != null) {
	    ID id = view.getTopicImageID();
	    if (id != null) {
		try {
		    URL url = map.getURLFromID(id);
		    icon = new ImageIcon(url);
		    return icon;
		} catch (Exception e) {
		}
	    }
	}
	return super.getLeafIcon();
    }

    public Icon getOpenIcon() {
	Icon icon = null;
	if (view != null) {
	    ID id = view.getCategoryOpenImageID();
	    if (id != null) {
		try {
		    URL url = map.getURLFromID(id);
		    icon = new ImageIcon(url);
		    return icon;
		} catch (Exception e) {
		}
	    }
	}
	return super.getOpenIcon();
    }

    public Icon getClosedIcon() {
	Icon icon = null;
	if (view != null) {
	    ID id = view.getCategoryClosedImageID();
	    if (id != null) {
		try {
		    URL url = map.getURLFromID(id);
		    icon = new ImageIcon(url);
		    return icon;
		} catch (Exception e) {
		}
	    }
	}
	return super.getClosedIcon();
    }

}
