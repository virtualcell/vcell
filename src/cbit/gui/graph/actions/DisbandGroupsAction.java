package cbit.gui.graph.actions;

/*  Collapse all existing groups from which group members are selected
 *  September 2010
 */

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;

import cbit.gui.graph.CartoonTool;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.Shape;
import cbit.gui.graph.groups.ShapeGroupUtil;

@SuppressWarnings("serial")
public class DisbandGroupsAction extends AbstractAction implements
		Action {

	public static final String actionCommand = "DisbandGroupsAction";

	protected final CartoonTool cartoonTool;

	public DisbandGroupsAction(CartoonTool cartoonTool) {
		this.cartoonTool = cartoonTool;
		putValue(Action.ACTION_COMMAND_KEY, actionCommand);
		putValue(Action.NAME, "Disband Groups");
		putValue(Action.SHORT_DESCRIPTION,
				"Disband all groups with selected members");
		putValue(Action.LONG_DESCRIPTION,
				"For each selected shape that belongs to a group, disband that group.");
	}

	public void actionPerformed(ActionEvent event) {
		GraphModel graphModel = cartoonTool.getGraphModel();
		List<Shape> selectedShapes = Arrays.asList(graphModel.getSelectedShapes());
		Set<Shape> groups = new HashSet<Shape>();
		for (Shape selectedShape : selectedShapes) {
			if(ShapeGroupUtil.isGroup(selectedShape)) {
				groups.add(selectedShape);
			} 
//			else {
//				Shape parent = selectedShape.getParent();
//				if(parent != null && ShapeGroupUtil.isGroup(parent)) {
//					groups.add(parent);
//				}
//			}
		}
		for(Shape group : groups) {
			ShapeGroupUtil.disbandGroup(cartoonTool.getGraphModel(), group);
		}
		cartoonTool.getGraphPane().repaint();
	}

}
