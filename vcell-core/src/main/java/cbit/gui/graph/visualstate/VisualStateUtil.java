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

/* Useful static methods concerning the visual state of a shape
 * August 2010
 */

import cbit.gui.graph.Shape;
import cbit.gui.graph.visualstate.VisualState.Owner;

public class VisualStateUtil {

	public static boolean isAllowedToShowByAllAncestors(VisualState visualState) {
		return isAllowedToShowByAllAncestors(visualState.getOwner());
	}

	public static boolean isAllowedToShowByAllAncestors(VisualState.Owner owner) {
		boolean theyAllAllowToShow = true;
		Object parent = owner.getParent();
		if(parent instanceof Owner) {
			VisualState parentVisualState = ((Owner) parent).getVisualState();
			theyAllAllowToShow = parentVisualState.isAllowingToShowDescendents() && 
			parentVisualState.isAllowedToShowByAllAncestors();
		}
		return theyAllAllowToShow;
	}


	
	public static boolean show(Shape shape) { return show(shape, true); }
	public static boolean hide(Shape shape) { return show(shape, false); }
	
	public static boolean show(Shape shape, boolean show) {
		boolean successfull = false;
		if(shape instanceof VisualState.Owner) {
			VisualState visualState = ((VisualState.Owner) shape).getVisualState();
			if(visualState instanceof HideableVisualState) {
				((HideableVisualState) visualState).setHidden(!show);
				successfull = true;
			} 
		}
		return successfull;		
	}

	public static boolean canBeHidden(Shape shape) {
		boolean successfull = false;
		if(shape instanceof VisualState.Owner) {
			VisualState visualState = ((VisualState.Owner) shape).getVisualState();
			if(visualState instanceof HideableVisualState) {
				successfull = true;
			} 
		}
		return successfull;		
	}
	
}
