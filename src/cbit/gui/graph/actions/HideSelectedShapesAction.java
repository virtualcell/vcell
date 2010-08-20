package cbit.gui.graph.actions;

/* Hide selected shape on a cartoon tool, if it supports being hidden.
 * August 2010
 */

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import cbit.gui.graph.CartoonTool;
import cbit.gui.graph.Shape;
import cbit.gui.graph.visualstate.VisualStateUtil;

@SuppressWarnings("serial")
public class HideSelectedShapesAction extends AbstractAction implements Action {

	public static final String actionCommand = "HideSelectedShapesAction";
	
	protected final CartoonTool cartoonTool;
	
	public HideSelectedShapesAction(CartoonTool cartoonTool) {
		this.cartoonTool = cartoonTool;
		putValue(Action.ACTION_COMMAND_KEY, actionCommand);
		putValue(Action.NAME, "Hide Nodes");
		putValue(Action.SHORT_DESCRIPTION, "Hide selected nodes, if possible.");
		putValue(Action.LONG_DESCRIPTION, "Hide selected nodes which support being hidden.");
	}
	
	public void actionPerformed(ActionEvent event) {
		Shape[] selectedShapes = cartoonTool.getGraphModel().getSelectedShapes();
		for(Shape selectedShape :selectedShapes) {
			VisualStateUtil.hide(selectedShape);
		}
		cartoonTool.getGraphPane().repaint();
	}

}
