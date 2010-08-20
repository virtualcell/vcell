package cbit.gui.graph.visualstate;

/* A simple edge visual state wrapping around a visual state, by default the immutable one
 * August 2010
 */

public class EdgeDefaultVisualState implements EdgeVisualState {

	// TODO add the dependency on edge endpoints
	
	protected final VisualState wrappedVisualState;
	
	public EdgeDefaultVisualState(VisualState wrappedVisualState) {
		if(!(wrappedVisualState.getOwner() instanceof Owner)) {
			throw new RuntimeException("Need EdgeVisualState.Owner for an EdgeVisualState, " +
					"not just a VisualStateOwner");
		}
		this.wrappedVisualState = wrappedVisualState;
	}
	
	public EdgeDefaultVisualState(Owner owner) {
		this.wrappedVisualState = new ImmutableVisualState(owner, VisualState.PaintLayer.EDGE);
	}

	public Owner getOwner() { return (Owner) wrappedVisualState.getOwner(); }
	public PaintLayer getPaintLayer() { return wrappedVisualState.getPaintLayer(); }

	public boolean isAllowedToShowByAllAncestors() {
		return wrappedVisualState.isAllowedToShowByAllAncestors();
	}

	public boolean isAllowingToShowDescendents() { return wrappedVisualState.isAllowingToShowDescendents(); }
	public boolean isAllowingToShowItself() { return wrappedVisualState.isAllowingToShowItself(); }
	public boolean isShowingItself() { return wrappedVisualState.isShowingItself(); }
	public boolean isShowingDescendents() { return wrappedVisualState.isShowingDescendents(); }

}
