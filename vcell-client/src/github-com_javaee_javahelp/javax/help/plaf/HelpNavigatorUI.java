/*
 * @(#)HelpNavigatorUI.java	1.26 06/10/30
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
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.help.HelpModel;
import javax.help.event.HelpModelListener;
import javax.help.event.HelpModelEvent;
import javax.help.NavigatorView;
import javax.help.Map;
import java.net.URL;
import javax.swing.Action;
import javax.swing.AbstractAction;

/**
 * UI factory interface for JHelpNavigator.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @author Stepan Marek
 * @version   %I     10/30/06
 */

public abstract class HelpNavigatorUI extends ComponentUI {

    private Icon icon;

    /**
     * Sets the icon for this HelpNavigator.
     *
     * @param icon the Icon
     */
    public void setIcon(Icon icon) {
	this.icon = icon;
    }

    /**
     * @return the Icon for this HelpNavigator
     */
    public Icon getIcon() {
	return icon;
    }

    /**
     * Merges a Navigator View.
     */
    public void merge(NavigatorView view){
        throw new UnsupportedOperationException("merge is not supported");
    }

    /**
     * Removes a Navigator View.
     */
    public void remove(NavigatorView view){
        throw new UnsupportedOperationException("remove is not supported");
    }
    
    /**
     * Returns icon associated with the view.
     *
     * @param view the view
     * @return the ImageIcon for the view
     */
    public ImageIcon getImageIcon(NavigatorView view) {
        ImageIcon icon = null;
        Map.ID id = view.getImageID();
        if (id != null) {
            try {
                Map map = view.getHelpSet().getCombinedMap();
                URL url = map.getURLFromID(id);
                icon = new ImageIcon(url);
		} catch (Exception e) {
		}
        }
        return icon;
    }
    
    /**
     * Returns an AddAction object. Has sense only for favorites navigator
     */
    public Action getAddAction(){
        throw new UnsupportedOperationException("getAddAction is not supported");
        //return (Action)null;
    }
}
