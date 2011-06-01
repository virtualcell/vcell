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

/*   GUISpace  --- by Oliver Ruebenacker, UCHC --- November 2007 to May 2010
 *   A wrapper java.awt.Container implementing UISpace
 */


import java.awt.Container;

import org.vcell.sybil.util.ui.UIComponent;
import org.vcell.sybil.util.ui.UISpace;

public class GUISpace<C extends Container> extends GUIComponent<C> implements UISpace {

	private static final long serialVersionUID = -2441332258198991481L;

	public GUISpace(C newContainer) { super(newContainer); }
	
	public void add(UIComponent newSubSpace) { component().add(((GUIComponent<?>)newSubSpace).component()); }
	
}
