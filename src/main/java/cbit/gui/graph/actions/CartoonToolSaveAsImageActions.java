/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui.graph.actions;

/*  Organizes cartoon tool actions connected to model elements
 *  September 2010
 */

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import cbit.gui.graph.CartoonTool;
import cbit.gui.graph.Shape;

public class CartoonToolSaveAsImageActions {
	@SuppressWarnings("serial")
	public static class MenuAction extends GraphViewAction {
		public static final String MENU_ACTION = "Save as Image";
		public static final String MENU_TEXT = MENU_ACTION;
		public MenuAction(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, MENU_TEXT, MENU_TEXT);
			putValue(MNEMONIC_KEY, KeyEvent.VK_I);
		}
		@Override
		public boolean canBeAppliedToShape(Shape shape) {
			return false;
		}
		@Override
		public boolean isEnabledForShape(Shape shape) {
			return false;
		}
		public void actionPerformed(ActionEvent arg0) {
			((CartoonTool)getGraphView()).actionPerformed(arg0);
		}
	}	
	
	public static GraphViewAction getMenuAction(CartoonTool cartoonTool) {
		return new MenuAction(cartoonTool);
	}
}
