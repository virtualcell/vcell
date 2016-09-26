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

/*  Organizes miscellaneous cartoon tool actions 
 *  September 2010
 */

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.KeyStroke;

import cbit.gui.graph.CartoonTool;

public class CartoonToolEditActions {

	@SuppressWarnings("serial")
	public static class Copy extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Copy";
		public static final String MENU_TEXT = MENU_ACTION;		
		public Copy(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, MENU_TEXT, MENU_TEXT);
			putValue(MNEMONIC_KEY, KeyEvent.VK_C);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		}
	}

	@SuppressWarnings("serial")
	public static class Delete extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Delete";
		public static final String MENU_TEXT = MENU_ACTION;
		public Delete(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, MENU_TEXT, MENU_TEXT);
			putValue(MNEMONIC_KEY, KeyEvent.VK_D);
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
		}
	}

//	@SuppressWarnings("serial")
//	public static class Paste extends CartoonToolWrapperAction {
//		public static final String MENU_ACTION = "Paste";
//		public static final String MENU_TEXT = MENU_ACTION;
//		public Paste(CartoonTool cartoonTool) {
//			super(cartoonTool, MENU_ACTION, MENU_TEXT, MENU_TEXT, MENU_TEXT);
//			putValue(MNEMONIC_KEY, KeyEvent.VK_V);
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V,
//					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
//		}
//	}	
	
	@SuppressWarnings("serial")
	public static class PasteNew extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Paste";
		public static final String MENU_TEXT = MENU_ACTION;
		public PasteNew(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, MENU_TEXT, MENU_TEXT);
			putValue(MNEMONIC_KEY, KeyEvent.VK_V);
		}
	}	
	
	@SuppressWarnings("serial")
	public static class Move extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Move...";
		public static final String MENU_TEXT = MENU_ACTION;
		public Move(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, MENU_TEXT, MENU_TEXT);
			putValue(MNEMONIC_KEY, KeyEvent.VK_M);
		}
	}	
	
	public static List<GraphViewAction> getDefaultActions(CartoonTool cartoonTool) { 
		List<GraphViewAction> list = new ArrayList<GraphViewAction>();
		list.add(new Copy(cartoonTool));
		list.add(new Delete(cartoonTool));
//		list.add(new Paste(cartoonTool));
		list.add(new PasteNew(cartoonTool));
		list.add(new Move(cartoonTool));
		return list; 
	}

}
