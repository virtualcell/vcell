/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui.graph.visualstate;

/* The visual state of a shape that can be collapsed and expanded
 * Collapse means to hide the children and show the group 
 * August 2010
 */

public interface CollapsibleVisualState extends VisualState {

	public void setCollapsed(boolean bCollapsed);
	public boolean isCollapsed();
	public void collapse();
	public void expand();
	
}
