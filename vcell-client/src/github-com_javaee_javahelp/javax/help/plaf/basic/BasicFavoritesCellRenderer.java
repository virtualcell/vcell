/*
 * @(#)BasicFavoritesCellRenderer.java	1.4 06/10/30
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

/**
 * Cell Renderer for the favorites UI.
 *
 * @author Richard Gregor
 * @version	1.4	10/30/06
 */
import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.net.URL;
import java.util.Locale;
import javax.help.TOCItem;
import javax.help.Map;
import javax.help.HelpUtilities;
import javax.help.Map.ID;
import javax.help.FavoritesItem;

public class BasicFavoritesCellRenderer extends DefaultTreeCellRenderer {
    
    public Component getTreeCellRendererComponent(JTree tree, Object value,
    boolean sel,
    boolean expanded,
    boolean leaf, int row,
    boolean hasFocus) {
        
        FavoritesItem item;
        
        Object o = ((DefaultMutableTreeNode)value).getUserObject();
        String stringValue = "";
                
        item = (FavoritesItem) o;
        if (item != null) {
            stringValue = item.getName();
        }
        
        // Set the locale of this if there is a lang value
        if (item != null) {
            Locale locale = item.getLocale();
            if (locale != null) {
                setLocale(locale);
            }
        }
        
        setText(stringValue);
        
        if (sel)
            setForeground(getTextSelectionColor());
        else
            setForeground(getTextNonSelectionColor());
        selected = sel;
        
        if(leaf)
            setIcon(getDefaultLeafIcon());
        else if(expanded)
            setIcon(getDefaultOpenIcon());
        else
            setIcon(getDefaultClosedIcon());        
       
        return this;
    }    
}
