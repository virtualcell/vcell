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

/*   UIComp  --- by Oliver Ruebenacker, UCHC --- November 2007 to January 2009
 *   A wrapper for java.awt.Component implementing interface UIComp
 */


import java.awt.Component;

import org.vcell.sybil.util.ui.UIComponent;


public class GUIComponent<C extends Component> implements UIComponent {
	
	protected C comp;
	
	public GUIComponent(C newComp) { comp = newComp; }
	public C component() { return comp; }

}
