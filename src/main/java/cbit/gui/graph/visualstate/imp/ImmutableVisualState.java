/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui.graph.visualstate.imp;

/* Always shows itself and its children, if it can.
 * Is visible if all ancestors may show their descendents
 * August 2010
 */

import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.VisualStateUtil;

public class ImmutableVisualState implements VisualState {

	protected final Owner owner;
	protected final PaintLayer paintType;
	
	public ImmutableVisualState(Owner owner, PaintLayer paintType) {
		this.owner = owner;
		this.paintType = paintType;
	}
	
	public Owner getOwner() { return owner; }
	public PaintLayer getPaintLayer() { return paintType; }
	public boolean isAllowingToShowItself() { return true; }
	public boolean isAllowingToShowDescendents() { return true; }

	public boolean isAllowedToShowByAllAncestors() {
		return VisualStateUtil.isAllowedToShowByAllAncestors(this);
	}

	public boolean isShowingItself() {
		return isAllowedToShowByAllAncestors();
	}

	public boolean isShowingDescendents() {
		return isAllowedToShowByAllAncestors();
	}
}
