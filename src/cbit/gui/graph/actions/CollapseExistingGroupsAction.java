package cbit.gui.graph.actions;

/*  Collapse all existing groups from which group members are selected
 *  September 2010
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
public class CollapseExistingGroupsAction extends AbstractAction implements
		Action {

	public static final String actionCommand = "CollapseExistingGroupsAction";

	protected static long groupCounter = 0;
	protected final CartoonTool cartoonTool;

	public CollapseExistingGroupsAction(CartoonTool cartoonTool) {
		this.cartoonTool = cartoonTool;
		putValue(Action.ACTION_COMMAND_KEY, actionCommand);
		putValue(Action.NAME, "Collapse Existing Groups");
		putValue(Action.SHORT_DESCRIPTION,
				"Collapse all existing groups with selected members");
		putValue(Action.LONG_DESCRIPTION,
				"For each selected shape that belongs to a group, collapse that group.");
	}

	public void actionPerformed(ActionEvent event) {
		GraphModel graphModel = cartoonTool.getGraphModel();
		List<Shape> selectedShapes = Arrays.asList(graphModel.getSelectedShapes());
		for (Shape selectedShape : selectedShapes) {
			Shape parentShape = selectedShape.getParent();
			if(parentShape != null) {
				ShapeGroupUtil.collapseGroup(parentShape);				
			}
		}
		cartoonTool.getGraphPane().repaint();
	}

}
