package cbit.gui.graph.visualstate;

/* The visual state of an edge depends on the visual state of 
 * 
 */
public interface EdgeVisualState extends VisualState {

	public interface Owner extends VisualState.Owner {
		// TODO add refs to start and end owner (shape)
	}
	
	public Owner getOwner();

}
