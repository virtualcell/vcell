package cbit.gui.graph.visualstate;

/* The visual state of a shape that can be collapsed and expanded
 * Collapse means to hide the children and show the group 
 * August 2010
 */

public interface CollapsibleVisualState extends VisualState {

	public void setCollapsed(boolean bCollapsed);
	public boolean isCollapsed();
	public void collapse();
	public void expand();
	
}
