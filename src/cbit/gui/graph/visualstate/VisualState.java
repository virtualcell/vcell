package cbit.gui.graph.visualstate;

/* The visual mode of a shape (e.g. if it is (or its children are) visible, collapsed, etc
 * August 2010
 */

import java.util.Collection;

public interface VisualState {

	public static interface Owner {
		public VisualState getVisualState();
		public Object getParent();
		public Collection<?> getChildren();
	}
	
	// PaintLayer determines the order in which a parent draws its children
	public static enum PaintLayer { COMPARTMENT, EDGE, NODE }
	
	public Owner getOwner();
	public PaintLayer getPaintLayer();
	
	public boolean isAllowingToShowItself();
	public boolean isAllowingToShowDescendents();
	
	public boolean isAllowedToShowByAllAncestors();
	public boolean isShowingItself();
	public boolean isShowingDescendents();
	
}
