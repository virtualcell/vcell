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

import cbit.gui.graph.visualstate.HideableVisualState;
import cbit.gui.graph.visualstate.VisualStateUtil;

/* The default visual mode
 * Is visible if it may show itself and all ancestors may show their descendents
 * August 2010
 */

public class MutableVisualState implements HideableVisualState {

	protected final Owner owner;
	protected final PaintLayer paintType;
	protected boolean bIsAllowingToShowItself = true;
	protected boolean bIsAllowingToShowDescendents = true;
	
	public MutableVisualState(Owner owner, PaintLayer paintType) {
		this.owner = owner;
		this.paintType = paintType;
	}
	
	public Owner getOwner() { return owner; }
	public PaintLayer getPaintLayer() { return paintType; }
	public void setIsAllowingToShowItself(boolean allow) { bIsAllowingToShowItself = allow; }
	public void setIsAllowingToShowDescendents(boolean allow) { bIsAllowingToShowDescendents = allow; }
	public boolean isAllowingToShowItself() { return bIsAllowingToShowItself; }
	public boolean isAllowingToShowDescendents() { return bIsAllowingToShowDescendents; }

	public boolean isAllowedToShowByAllAncestors() {
		return VisualStateUtil.isAllowedToShowByAllAncestors(this);
	}

	public boolean isShowingItself() {
		return bIsAllowingToShowItself && isAllowedToShowByAllAncestors();
	}

	public boolean isShowingDescendents() {
		return bIsAllowingToShowDescendents && isAllowedToShowByAllAncestors();
	}

	public void setHidden(boolean bHidden) {
		setIsAllowingToShowItself(!bHidden);
	}

	public boolean isHidden() {
		return !isAllowingToShowItself();
	}

	public void hide() {
		setIsAllowingToShowItself(false);
	}

	public void show() {
		setIsAllowingToShowItself(true);
	}
	
	
}
