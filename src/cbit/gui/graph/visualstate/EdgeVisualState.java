package cbit.gui.graph.visualstate;

/* The visual state of an edge depends on the visual state of its end points
 * August 2010
 */

public interface EdgeVisualState extends VisualState {

	public interface Owner extends VisualState.Owner {
		public VisualState.Owner getStartShape();
		public VisualState.Owner getEndShape();
	}
	
	public Owner getOwner();

}
