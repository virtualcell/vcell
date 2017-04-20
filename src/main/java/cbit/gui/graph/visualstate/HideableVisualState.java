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

/* The visual mode of a group shape, i.e. one that can be collapsed and expanded
 * August 2010
 */

public interface HideableVisualState extends VisualState {
	
	public void setHidden(boolean bHidden);
	public boolean isHidden();
	public void hide();
	public void show();

}
