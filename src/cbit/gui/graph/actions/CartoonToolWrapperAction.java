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