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

/* The visual state of an edge depends on the visual state of its end points
 * August 2010
 */

public interface EdgeVisualState extends VisualState {

	public interface Owner extends VisualState.Owner {
		public VisualState.Owner getStartShape();
		public VisualState.Owner getEndShape();
	}
	
	public Owner getOwner();

}
