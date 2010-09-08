package cbit.gui.graph.visualstate.imp;

/* Always shows itself and its children, if it can.
 * Is visible if all ancestors may show their descendents
 * August 2010
 */

import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.VisualStateUtil;

public class ImmutableVisualState implements VisualState {

	protected final Owner owner;
	protected final PaintLayer paintType;
	
	public ImmutableVisualState(Owner owner, PaintLayer paintType) {
		this.owner = owner;
		this.paintType = paintType;
	}
	
	public Owner getOwner() { return owner; }
	public PaintLayer getPaintLayer() { return paintType; }
	public boolean isAllowingToShowItself() { return true; }
	public boolean isAllowingToShowDescendents() { return true; }

	public boolean isAllowedToShowByAllAncestors() {
		return VisualStateUtil.isAllowedToShowByAllAncestors(this);
	}

	public boolean isShowingItself() {
		return isAllowedToShowByAllAncestors();
	}

	public boolean isShowingDescendents() {
		return isAllowedToShowByAllAncestors();
	}
}
