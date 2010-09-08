package cbit.gui.graph.visualstate;

/* The visual mode of a group shape, i.e. one that can be collapsed and expanded
 * August 2010
 */

public interface HideableVisualState extends VisualState {
	
	public void setHidden(boolean bHidden);
	public boolean isHidden();
	public void hide();
	public void show();

}
