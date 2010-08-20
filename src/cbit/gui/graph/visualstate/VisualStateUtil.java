package cbit.gui.graph.visualstate;

/* Useful static methods concerning the visual state of a shape
 * August 2010
 */

import cbit.gui.graph.Shape;

public class VisualStateUtil {

	public static boolean show(Shape shape) { return show(shape, true); }
	public static boolean hide(Shape shape) { return show(shape, false); }
	
	public static boolean show(Shape shape, boolean show) {
		boolean successfull = false;
		if(shape instanceof VisualState.Owner) {
			VisualState visualStateManager = 
				((VisualState.Owner) shape).getVisualState();
			if(visualStateManager instanceof MutableVisualState) {
				((MutableVisualState) visualStateManager).setIsAllowingToShowItself(show);
				successfull = true;
			} 
		}
		return successfull;		
	}

	public static boolean canBeHidden(Shape shape) {
		boolean successfull = false;
		if(shape instanceof VisualState.Owner) {
			VisualState visualStateManager = 
				((VisualState.Owner) shape).getVisualState();
			if(visualStateManager instanceof MutableVisualState) {
				successfull = true;
			} 
		}
		return successfull;		
	}
	
}
