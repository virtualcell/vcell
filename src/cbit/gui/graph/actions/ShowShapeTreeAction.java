package cbit.gui.graph.actions;

/* Unhide all hidden shapes on a cartoon tool.
 * August 2010
 */

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

import org.vcell.sybil.util.text.StringUtil;

import cbit.gui.graph.CartoonTool;
import cbit.gui.graph.Shape;

@SuppressWarnings("serial")
public class ShowShapeTreeAction extends AbstractAction implements Action {

	public static boolean bActivated = false;
	public static final String actionCommand = "ShowShapeTreeAction";
	
	protected final CartoonTool cartoonTool;
	protected final Shape topShape;
	
	public ShowShapeTreeAction(CartoonTool cartoonTool) {
		this.cartoonTool = cartoonTool;
		this.topShape = null;
		putValue(Action.ACTION_COMMAND_KEY, actionCommand);
		putValue(Action.NAME, "Show Shape Tree");
		putValue(Action.SHORT_DESCRIPTION, "Print the shape tree.");
		putValue(Action.LONG_DESCRIPTION, "Print the shape tree (parent-child relations) to the console.");
	}
	
	public ShowShapeTreeAction(Shape topShape) {
		this.cartoonTool = null;
		this.topShape = topShape;
		putValue(Action.ACTION_COMMAND_KEY, actionCommand);
		putValue(Action.NAME, "Show Shape Tree");
		putValue(Action.SHORT_DESCRIPTION, "Print the shape tree.");
		putValue(Action.LONG_DESCRIPTION, "Print the shape tree (parent-child relations) to the console.");
	}
	
	public static final int maximumShapeTreeDepth = 20;
	
	public static void showShapeTree(Shape shape) { showShapeTree(shape, 0); }
	
	public static void showShapeTree(Shape shape, int depth) {
		String indent = StringUtil.multiply("  ", depth);
		System.out.println(indent + shape);
		if(depth < maximumShapeTreeDepth) {
			for(Shape child : shape.getChildren()) {
				showShapeTree(child, depth + 1);
			}			
		}
	}
	
	public void actionPerformed(ActionEvent event) {
		if(topShape != null) {
			showShapeTree(topShape);
		} else {
			showShapeTree(cartoonTool.getGraphModel().getTopShape());				
		}
	}

}
