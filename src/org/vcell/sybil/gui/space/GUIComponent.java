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