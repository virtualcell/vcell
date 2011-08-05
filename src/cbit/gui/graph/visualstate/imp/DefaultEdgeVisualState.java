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

/* A simple edge visual state wrapping around a visual state, by default the immutable one
 * August 2010
 */

import cbit.gui.graph.visualstate.EdgeVisualState;
import cbit.gui.graph.visualstate.VisualState;

public class DefaultEdgeVisualState implements EdgeVisualState {

	protected final VisualState wrappedVisualState;
	
	public DefaultEdgeVisualState(VisualState wrappedVisualState) {
		if(!(wrappedVisualState.getOwner() instanceof Owner)) {
			throw new RuntimeException("Need EdgeVisualState.Owner for an EdgeVisualState, " +
					"not just a VisualStateOwner");
		}
		this.wrappedVisualState = wrappedVisualState;
	}
	
	public DefaultEdgeVisualState(Owner owner) {
		this.wrappedVisualState = new ImmutableVisualState(owner, VisualState.PaintLayer.EDGE);
	}

	public VisualState getWrappedVisualState() { return wrappedVisualState; }
	
	public Owner getOwner() { return (Owner) wrappedVisualState.getOwner(); }
	public PaintLayer getPaintLayer() { return wrappedVisualState.getPaintLayer(); }
	
	public boolean isAllowedToShowByAllAncestors() {
		return wrappedVisualState.isAllowedToShowByAllAncestors();
	}

	public boolean isAllowingToShowDescendents() { 
		return wrappedVisualState.isAllowingToShowDescendents(); 
	}

	public boolean isAllowingToShowItself() { return wrappedVisualState.isAllowingToShowItself(); }

	public boolean isShowingItself() { 
		boolean bShowingItself = false;
		VisualState.Owner startShape = getOwner().getStartShape();
		VisualState.Owner endShape = getOwner().getEndShape();
		if(startShape != null && startShape.getVisualState().isShowingItself() &&
				endShape != null && endShape.getVisualState().isShowingItself()) {
			bShowingItself = wrappedVisualState.isShowingItself();
		}
		return bShowingItself; 
	}
	
	public boolean isShowingDescendents() { return wrappedVisualState.isShowingDescendents(); }

}
