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

/* The default visual mode
 * Is visible if it may show itself and all ancestors may show their descendents
 * August 2010
 */

import cbit.gui.graph.visualstate.CollapsibleVisualState;
import cbit.gui.graph.visualstate.HideableVisualState;
import cbit.gui.graph.visualstate.VisualStateUtil;

public class DefaultCollapsibleVisualState implements HideableVisualState, CollapsibleVisualState {

	protected final Owner owner;
	protected final PaintLayer paintType;
	protected boolean bIsAllowingToShow = true;
	protected boolean bIsCollapsed = false;
	
	public DefaultCollapsibleVisualState(Owner owner, PaintLayer paintType) {
		this.owner = owner;
		this.paintType = paintType;
	}
	
	public Owner getOwner() { return owner; }
	public PaintLayer getPaintLayer() { return paintType; }
	public boolean isAllowingToShowItself() { return bIsAllowingToShow && bIsCollapsed; }
	public boolean isAllowingToShowDescendents() { return bIsAllowingToShow && !bIsCollapsed; }

	public boolean isAllowedToShowByAllAncestors() {
		return VisualStateUtil.isAllowedToShowByAllAncestors(this);
	}

	public boolean isShowingItself() {
		return isAllowingToShowItself() && isAllowedToShowByAllAncestors();
	}

	public boolean isShowingDescendents() {
		return isAllowingToShowDescendents() && isAllowedToShowByAllAncestors();
	}

	public void setHidden(boolean bHidden) {
		bIsAllowingToShow = !bHidden;
	}

	public boolean isHidden() {
		return !bIsAllowingToShow;
	}

	public void hide() {
		bIsAllowingToShow = false;
	}

	public void show() {
		bIsAllowingToShow = true;
	}

	public boolean isCollapsed() {
		return bIsCollapsed;
	}

	public void setCollapsed(boolean bCollapsed) {
		this.bIsCollapsed = bCollapsed;
	}
	
	public void collapse() {
		bIsCollapsed = true;
	}

	public void expand() {
		bIsCollapsed = false;
	}

	
}
