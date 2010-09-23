package cbit.gui.graph;

/*  Manage available space for a shape and its children
 *  September 2010
 */

import java.awt.Dimension;
import java.awt.Point;

public class ShapeSpaceManager {

	public static interface Owner {
		public ShapeSpaceManager getSpaceManager();
		public Owner getParent();
	}

	protected final Owner owner;
	// position relative to parent's position
	protected Point relPos = new Point(0, 0);
	protected Dimension size = new Dimension();
	protected Dimension sizePreferred = new Dimension(0, 0);

	
	public ShapeSpaceManager(Owner owner) {
		this.owner = owner;
	}
	
	public Owner getOwner() { return owner; }
	
	public void setRelPos(Point relativePos) { this.relPos = relativePos; }	
	public void setRelPos(int x, int y) { relPos.x = x; relPos.y = y; }
	public Point getRelPos() { return relPos; }
	public int getRelX() { return relPos.x; }
	public int getRelY() { return relPos.y; }
	
	public void move(int dx, int dy) { relPos.x += dx; relPos.y += dy; }
	
	public final Point getAbsLoc() {
		Owner parent = owner;
		Point pos = new Point(getRelPos());
		while ((parent = parent.getParent()) != null) {
			pos.x += parent.getSpaceManager().getRelX();
			pos.y += parent.getSpaceManager().getRelY();
		}
		return pos;
	}

	public final void setAbsLoc(Point absLoc) {
		Owner parent = owner.getParent();
		if (parent != null) {
			Point parentAbsLoc = parent.getSpaceManager().getAbsLoc();
			setRelPos(absLoc.x - parentAbsLoc.x, absLoc.y - parentAbsLoc.y);
		} else {
			setRelPos(absLoc.x, absLoc.y);
		}
	}

	public void setSize(Dimension size) { this.size = size; }
	public void setSize(int width, int height) { size.width = width; size.height = height; }
	public Dimension getSize() { return size; }
	
	public void setSizePreferred(Dimension sizePreferred) { this.sizePreferred = sizePreferred; }

	public void setSizePreferred(int width, int height) { 
		sizePreferred.width = width; 
		sizePreferred.height = height; 
	}
	
	public Dimension getSizePreferred() { return sizePreferred; }
	
}
