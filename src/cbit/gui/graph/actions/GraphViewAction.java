package cbit.gui.graph.actions;

/*  An action that refers to a cartoon tool
 *  September 2010
 */

import javax.swing.AbstractAction;

import cbit.gui.graph.GraphView;
import cbit.gui.graph.Shape;

@SuppressWarnings("serial")
public abstract class GraphViewAction extends AbstractAction {

	protected final GraphView graphView;
	
	public GraphViewAction(GraphView graphView, String key, String name, 
			String shortDescription, String longDescription) {
		this.graphView = graphView;
		putValue(ACTION_COMMAND_KEY, key);
		putValue(NAME, name);
		putValue(SHORT_DESCRIPTION, shortDescription);
		putValue(LONG_DESCRIPTION, longDescription);
	}

	public GraphView getGraphView() { return graphView; }
	public String getActionCommand() { return ActionUtil.getActionCommand(this); }
	public abstract boolean canBeAppliedToShape(Shape shape);
	public abstract boolean isEnabledForShape(Shape shape);

}