package cbit.gui.graph.actions;

/* Unhide all hidden shapes on a cartoon tool.
 * August 2010
 */

import java.awt.event.ActionEvent;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.Action;

import cbit.gui.graph.CartoonTool;
import cbit.gui.graph.Shape;
import cbit.gui.graph.visualstate.VisualStateUtil;

@SuppressWarnings("serial")
public class UnhideAllShapesAction extends AbstractAction implements Action {

	public static final String actionCommand = "UnhideAllShapesAction";
	
	protected final CartoonTool cartoonTool;
	
	public UnhideAllShapesAction(CartoonTool cartoonTool) {
		this.cartoonTool = cartoonTool;
		putValue(Action.ACTION_COMMAND_KEY, actionCommand);
		putValue(Action.NAME, "Unhide All");
		putValue(Action.SHORT_DESCRIPTION, "Unhide all hidden shapes.");
		putValue(Action.LONG_DESCRIPTION, "Unhide all hidden shapes.");
	}
	
	public void actionPerformed(ActionEvent event) {
		Enumeration<Shape> shapesEnum = cartoonTool.getGraphModel().getShapes();
		while(shapesEnum.hasMoreElements()) {
			Shape shape = shapesEnum.nextElement();
			VisualStateUtil.show(shape);
		}
		cartoonTool.getGraphPane().repaint();
	}

}
