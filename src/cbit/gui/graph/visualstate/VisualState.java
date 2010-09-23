package cbit.gui.graph.visualstate;

/* The visual mode of a shape (e.g. if it is (or its children are) visible, collapsed, etc
 * August 2010
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface VisualState {

	public static interface Owner {
		public VisualState getVisualState();
		public Object getParent();
		public Collection<?> getChildren();
	}
	
	// PaintLayer determines the order in which a parent draws its children
	public static enum PaintLayer { COMPARTMENT, EDGE, NODE;

	public static final List<PaintLayer> valuesReverse = createValuesReverse();

	protected static List<PaintLayer> createValuesReverse() {
		ArrayList<PaintLayer> valuesReverseNew = new ArrayList<PaintLayer>(Arrays.asList(values()));
		Collections.reverse(valuesReverseNew);
		return valuesReverseNew;
	}

	}

	public Owner getOwner();
	public PaintLayer getPaintLayer();
	
	public boolean isAllowingToShowItself();
	public boolean isAllowingToShowDescendents();
	
	public boolean isAllowedToShowByAllAncestors();
	public boolean isShowingItself();
	public boolean isShowingDescendents();
	
}
