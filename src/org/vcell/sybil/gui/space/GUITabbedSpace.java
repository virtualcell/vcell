/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.gui.space;

/*   GUITabbedSpace  --- by Oliver Ruebenacker, UCHC --- August 2007 to March 2010
 *   A wrapper for JTabbedPane
 */

import javax.swing.JTabbedPane;
import org.vcell.sybil.util.ui.UIComponent;
import org.vcell.sybil.util.ui.UITabbedSpace;


public class GUITabbedSpace<P extends JTabbedPane> extends GUISpace<P> implements UITabbedSpace {

	public GUITabbedSpace(P newPane) { super(newPane); }

	public P tabPane() { return super.component(); }	

	public void addTab(final String newName, UIComponent newComp) { 
		tabPane().addTab(newName, ((GUISpace<?>) newComp).component()); 
	}

	public void removeTab(UIComponent oldComp) { tabPane().remove(((GUISpace<?>) oldComp).component()); }
	public void selectTab(UIComponent comp) { tabPane().setSelectedComponent(((GUISpace<?>) comp).component()); }

}
