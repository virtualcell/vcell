package cbit.gui.graph.actions;

/* Hide selected shape on a cartoon tool, if it supports being hidden.
 * August 2010
 */

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import cbit.gui.graph.CartoonTool;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.Shape;
import cbit.gui.graph.groups.ShapeGroupUtil;

@SuppressWarnings("serial")
public class ExpandSelectedGroupsAction extends AbstractAction implements
		Action {

	public static final String actionCommand = "ExpandSelectedGroupsAction";

	protected final CartoonTool cartoonTool;

	public ExpandSelectedGroupsAction(CartoonTool cartoonTool) {
		this.cartoonTool = cartoonTool;
		putValue(Action.ACTION_COMMAND_KEY, actionCommand);
		putValue(Action.NAME, "Expand Selected");
		putValue(Action.SHORT_DESCRIPTION, "Expand selected groups.");
		putValue(Action.LONG_DESCRIPTION, "Expand selected groups.");
	}

	public void actionPerformed(ActionEvent event) {
		GraphModel graphModel = cartoonTool.getGraphModel();
		List<Shape> selectedShapes = Arrays.asList(graphModel.getSelectedShapes());
		for (Shape selectedShape : selectedShapes) {
			ShapeGroupUtil.expandGroup(selectedShape);
		}
		cartoonTool.getGraphPane().repaint();
	}

}
