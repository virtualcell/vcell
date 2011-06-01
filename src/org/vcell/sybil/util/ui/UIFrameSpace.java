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

/*   UIFrameSpace  --- by Oliver Ruebenacker, UCHC --- April 2007 to March 2010
 *   Stores information accessible to a wide range of components, such as configuration, 
 *   progress reports and system state
 */

public interface UIFrameSpace extends UISpace {
	public void prepare();
	public String title();
	public void setTitle(String title);
	public void setVisible(boolean newVisible);
	public void updateUI();
}
