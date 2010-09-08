package cbit.gui.graph.actions;

/*  Create a group from selected shapes and collapse that group
 *  August to September 2010
 */

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import cbit.gui.graph.CartoonTool;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.Shape;
import cbit.gui.graph.groups.GroupShape;
import cbit.gui.graph.groups.ShapeGroupUtil;

@SuppressWarnings("serial")
public class CollapseAsNewGroupAction extends AbstractAction implements Action {

	public static final String actionCommand = "CollapseAsNewGroupAction";

	protected static long groupCounter = 0;
	protected final CartoonTool cartoonTool;

	public static String getNewGroupName() {
		String groupName = "group" + groupCounter;
		++groupCounter;
		return groupName;
	}

	public CollapseAsNewGroupAction(CartoonTool cartoonTool) {
		this.cartoonTool = cartoonTool;
		putValue(Action.ACTION_COMMAND_KEY, actionCommand);
		putValue(Action.NAME, "Collapse As New Group");
		putValue(Action.SHORT_DESCRIPTION,
				"Collapse selected nodes as new group.");
		putValue(Action.LONG_DESCRIPTION,
				"Create a new group from selected shapes and collapse.");
	}

	public void actionPerformed(ActionEvent event) {
		GraphModel graphModel = cartoonTool.getGraphModel();
		List<Shape> selectedShapes = Arrays.asList(graphModel.getSelectedShapes());
		try {
			GroupShape groupShape = ShapeGroupUtil.createGroup(graphModel,
					getNewGroupName(), selectedShapes);
			ShapeGroupUtil.collapseGroup(groupShape);
			cartoonTool.getGraphPane().repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
