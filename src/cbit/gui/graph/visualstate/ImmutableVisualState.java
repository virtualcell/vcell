package cbit.gui.graph.visualstate;

/* Always shows itself and its children, if it can.
 * Is visible if all ancestors may show their descendents
 * August 2010
 */

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
		boolean theyAllAllowToShow = true;
		Object parent = owner.getParent();
		if(parent instanceof Owner) {
			VisualState parentVisualState = ((Owner) parent).getVisualState();
			theyAllAllowToShow = parentVisualState.isAllowingToShowDescendents() && 
			parentVisualState.isAllowedToShowByAllAncestors();
		}
		return theyAllAllowToShow;
	}

	public boolean isShowingItself() {
		return isAllowedToShowByAllAncestors();
	}

	public boolean isShowingDescendents() {
		return isAllowedToShowByAllAncestors();
	}
}
