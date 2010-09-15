package cbit.gui.graph.actions;

/*  Organizes cartoon tool actions connected to model elements
 *  September 2010
 */

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import cbit.gui.graph.CartoonTool;
import cbit.gui.graph.Shape;

public class CartoonToolSaveAsImageActions {

	@SuppressWarnings("serial")
	public static class HighRes extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "High Res (x3.0)";
		public static final String MENU_TEXT = MENU_ACTION;		
		public HighRes(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, MENU_TEXT, MENU_TEXT);
			putValue(MNEMONIC_KEY, KeyEvent.VK_H);
		}
	}

	@SuppressWarnings("serial")
	public static class MedRes extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Medium Res (x2.0)";
		public static final String MENU_TEXT = MENU_ACTION;
		public MedRes(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, MENU_TEXT, MENU_TEXT);
			putValue(MNEMONIC_KEY, KeyEvent.VK_M);
		}
	}

	@SuppressWarnings("serial")
	public static class LowRes extends CartoonToolWrapperAction {
		public static final String MENU_ACTION = "Low Res (x1.0)";
		public static final String MENU_TEXT = MENU_ACTION;
		public LowRes(CartoonTool cartoonTool) {
			super(cartoonTool, MENU_ACTION, MENU_TEXT, MENU_TEXT, MENU_TEXT);
			putValue(MNEMONIC_KEY, KeyEvent.VK_L);
		}
	}

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
		}
	}	
	
	public static GraphViewAction getMenuAction(CartoonTool cartoonTool) {
		return new MenuAction(cartoonTool);
	}
	
	public static List<GraphViewAction> getDefaultActions(CartoonTool cartoonTool) { 
		List<GraphViewAction> list = new ArrayList<GraphViewAction>();
		list.add(new HighRes(cartoonTool));
		list.add(new MedRes(cartoonTool));
		list.add(new LowRes(cartoonTool));
		return list; 
	}

}
