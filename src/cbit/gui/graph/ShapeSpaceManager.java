package cbit.gui.graph;

/*  Manage available space for a shape and its children
 *  September 2010
 */

import java.awt.Point;

public class ShapeSpaceManager {

	// position relative to parent's position
	protected Point relPos = new Point(0, 0);
	
	public void setRelPos(Point relativePos) { this.relPos = relativePos; }	
	public void setRelPos(int x, int y) { relPos.x = x; relPos.y = y; }
	public Point getRelPos() { return relPos; }
	public int getRelX() { return relPos.x; }
	public int getRelY() { return relPos.y; }
	
}
