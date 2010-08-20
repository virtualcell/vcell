package cbit.gui.graph.visualstate;

/* The default visual mode
 * Is visible if it may show itself and all ancestors may show their descendents
 * August 2010
 */

public class MutableVisualState implements VisualState {

	protected final Owner owner;
	protected final PaintLayer paintType;
	protected boolean bIsAllowingToShowItself = true;
	protected boolean bIsAllowingToShowDescendents = true;
	
	public MutableVisualState(Owner owner, PaintLayer paintType) {
		this.owner = owner;
		this.paintType = paintType;
	}
	
	public Owner getOwner() { return owner; }
	public PaintLayer getPaintLayer() { return paintType; }
	public void setIsAllowingToShowItself(boolean allow) { bIsAllowingToShowItself = allow; }
	public void setIsAllowingToShowDescendents(boolean allow) { bIsAllowingToShowDescendents = allow; }
	public boolean isAllowingToShowItself() { return bIsAllowingToShowItself; }
	public boolean isAllowingToShowDescendents() { return bIsAllowingToShowDescendents; }

	public boolean isAllowedToShowByAllAncestors() {
		boolean theyAllMayShow = true;
		Object parent = owner.getParent();
		if(parent instanceof Owner) {
			VisualState parentVisualState = ((Owner) parent).getVisualState();
			theyAllMayShow = parentVisualState.isAllowingToShowDescendents() && 
			parentVisualState.isAllowedToShowByAllAncestors();
		}
		return theyAllMayShow;
	}

	public boolean isShowingItself() {
		return bIsAllowingToShowItself && isAllowedToShowByAllAncestors();
	}

	public boolean isShowingDescendents() {
		return bIsAllowingToShowDescendents && isAllowedToShowByAllAncestors();
	}
}
