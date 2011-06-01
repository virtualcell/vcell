/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.ui;

/*   UITabbedSpace  --- by Oliver Ruebenacker, UCHC --- August 2007 to January 2009
 *   An interface for a wrappers of JTabbedPane or similar
 */


public interface UITabbedSpace extends UISpace {

    public void addTab(String newName, UIComponent newComp);
	public void removeTab(UIComponent someComp);
	public void selectTab(UIComponent comp);
}
