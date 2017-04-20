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

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import cbit.gui.graph.CartoonTool;

@SuppressWarnings("serial")
public class CartoonToolMiscActions {

	public static class AddFeatureInside extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Add Feature Inside";
		public static final String MENU_TEXT = MENU_ACTION;		
		public AddFeatureInside(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, "Add new feature inside", 
					"Add new feature to bio model.");
			putValue(MNEMONIC_KEY, KeyEvent.VK_F);
		}
	}

	public static class AddFeatureOutside extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Add Feature Outside";
		public static final String MENU_TEXT = MENU_ACTION;		
		public AddFeatureOutside(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, "Add new feature outside", 
			"Add new feature to bio model.");
			putValue(MNEMONIC_KEY, KeyEvent.VK_F);
		}
	}
	
	@SuppressWarnings("serial")
	public static class AddSpecies extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Add Species";
		public static final String MENU_TEXT = MENU_ACTION;
		public AddSpecies(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, "Add new species", 
					"Add new species to bio model.");
			putValue(MNEMONIC_KEY, KeyEvent.VK_S);
		}
	}

	@SuppressWarnings("serial")
	public static class AddGlobalParameter extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Add Global Parameter...";
		public static final String MENU_TEXT = MENU_ACTION;
		public AddGlobalParameter(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, "Add new global parameter", 
					"Add new global parameter to bio model.");
			putValue(MNEMONIC_KEY, KeyEvent.VK_G);
		}
	}

	@SuppressWarnings("serial")
	public static class SearchReactions extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Search Reactions...";
		public static final String MENU_TEXT = MENU_ACTION;
		public SearchReactions(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, "Search reactions to add", 
					"Search reactions to add to bio model.");
			putValue(MNEMONIC_KEY, KeyEvent.VK_R);
		}
	}	
	
	@SuppressWarnings("serial")
	public static class Reactions extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Reactions...";
		public static final String MENU_TEXT = MENU_ACTION;
		public Reactions(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, "Edit reactions", 
					"Edit reactions of this bio model.");
			putValue(MNEMONIC_KEY, KeyEvent.VK_R);
		}
	}	
	
	@SuppressWarnings("serial")
	public static class ReactionsSlices extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Reactions (Slices) ...";
		public static final String MENU_TEXT = MENU_ACTION;
		public ReactionsSlices(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, "Edit reactions (slices)", 
					"Edit reactions of this bio model in slices layout.");
			putValue(MNEMONIC_KEY, KeyEvent.VK_Z);
		}
	}	
	
	@SuppressWarnings("serial")
	public static class Properties extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Properties";
		public static final String MENU_TEXT = MENU_ACTION;
		public Properties(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, "Edit properties", "Edit properties");
			putValue(MNEMONIC_KEY, KeyEvent.VK_P);
		}
	}	
	
	@SuppressWarnings("serial")
	public static class Enable extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Enable";
		public static final String MENU_TEXT = MENU_ACTION;
		public Enable(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, MENU_TEXT, MENU_TEXT);
//			putValue(MNEMONIC_KEY, KeyEvent.VK_E);
		}
	}	
	
	@SuppressWarnings("serial")
	public static class Disable extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Disable";
		public static final String MENU_TEXT = MENU_ACTION;
		public Disable(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, MENU_TEXT, MENU_TEXT);
//			putValue(MNEMONIC_KEY, KeyEvent.VK_E);
		}
	}	
	
	@SuppressWarnings("serial")
	public static class Solve extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Solve";
		public static final String MENU_TEXT = MENU_ACTION;
		public Solve(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, MENU_TEXT, MENU_TEXT);
//			putValue(MNEMONIC_KEY, KeyEvent.VK_S);
		}
	}	
	
	@SuppressWarnings("serial")
	public static class Reset extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Reset";
		public static final String MENU_TEXT = MENU_ACTION;
		public Reset(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, MENU_TEXT, MENU_TEXT);
//			putValue(MNEMONIC_KEY, KeyEvent.VK_R);
		}
	}	
	
	@SuppressWarnings("serial")
	public static class ShowParameters extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Parameters and Rate Expressions";
		public static final String MENU_TEXT = MENU_ACTION;
		public ShowParameters(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, MENU_TEXT, MENU_TEXT);
//			putValue(MNEMONIC_KEY, KeyEvent.VK_P);
		}
	}	
	
	@SuppressWarnings("serial")
	public static class AddBindingSite extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Add Binding Site";
		public static final String MENU_TEXT = MENU_ACTION;
		public AddBindingSite(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, MENU_TEXT, MENU_TEXT);
//			putValue(MNEMONIC_KEY, KeyEvent.VK_B);
		}
	}	
	
	@SuppressWarnings("serial")
	public static class AddComplex extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Add Complex";
		public static final String MENU_TEXT = MENU_ACTION;
		public AddComplex(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, MENU_TEXT, MENU_TEXT);
//			putValue(MNEMONIC_KEY, KeyEvent.VK_C);
		}
	}	
	
	@SuppressWarnings("serial")
	public static class Annotate extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Annotate...";
		public static final String MENU_TEXT = MENU_ACTION;
		public Annotate(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, MENU_TEXT, MENU_TEXT);
//			putValue(MNEMONIC_KEY, KeyEvent.VK_M);
		}
	}	
	
	public static List<GraphViewAction> getDefaultActions(CartoonTool cartoonTool) { 
		List<GraphViewAction> list = new ArrayList<GraphViewAction>();
		list.add(new AddFeatureInside(cartoonTool));
		list.add(new AddFeatureOutside(cartoonTool));
		list.add(new AddSpecies(cartoonTool));
//		list.add(new AddGlobalParameter(cartoonTool));
		list.add(new SearchReactions(cartoonTool));
//		list.add(new Reactions(cartoonTool));
//		list.add(new ReactionsSlices(cartoonTool));
//		list.add(new Properties(cartoonTool));
		list.add(new Enable(cartoonTool));
		list.add(new Disable(cartoonTool));
		list.add(new Solve(cartoonTool));
		list.add(new Reset(cartoonTool));
//		list.add(new ShowParameters(cartoonTool));
		list.add(new AddBindingSite(cartoonTool));
		list.add(new AddComplex(cartoonTool));
//		list.add(new Annotate(cartoonTool));
		return list; 
	}

}
