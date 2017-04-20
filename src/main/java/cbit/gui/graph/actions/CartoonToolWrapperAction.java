/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

/**
 * 
 */
package cbit.gui.graph.actions;

import java.awt.event.ActionEvent;

import cbit.gui.graph.CartoonTool;
import cbit.gui.graph.Shape;

@SuppressWarnings("serial")
public class CartoonToolWrapperAction extends GraphViewAction {

	public CartoonToolWrapperAction(CartoonTool cartoonTool2, String key, String name, 
			String shortDescription, String longDescription) {
		super(cartoonTool2, key, name, shortDescription, longDescription);
	}

	public CartoonTool getCartoonTool() { return (CartoonTool) getGraphView(); }
	
	public boolean canBeAppliedToShape(Shape shape) {
		return getCartoonTool().shapeHasMenuAction(shape, getActionCommand());
	}
	
	public boolean isEnabledForShape(Shape shape) {
		return getCartoonTool().shapeHasMenuActionEnabled(shape, getActionCommand());
	}

	public void actionPerformed(ActionEvent e) {
		getCartoonTool().actionPerformed(e);
	}
}
