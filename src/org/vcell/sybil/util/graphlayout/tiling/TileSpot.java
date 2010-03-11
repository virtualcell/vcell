package org.vcell.sybil.util.graphlayout.tiling;

/*   TileSpot  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   A spot in a tiling of the graph panel for graph layout methods
 */

import org.vcell.sybil.gui.graph.locations.Location;

public class TileSpot<S extends Location.Owner<S>>  {

	public static class NoShapeSpot<S extends Location.Owner<S>> extends TileSpot<S> {
		public NoShapeSpot(int iXNew, int iYNew, int xNew, int yNew) { super(iXNew, iYNew, xNew, yNew); }
		public boolean isVacant() { return false; }
		public boolean hasShape() { return false; }
		public boolean canHaveShape() { return false; }
	}
	
	protected int x, y;
	protected int iX, iY;
	protected S shape;
	
	public TileSpot(int iXNew, int iYNew, int xNew, int yNew) {
		this(iXNew, iYNew, xNew, yNew, null);
	}
	
	public TileSpot(int iXNew, int iYNew, int xNew, int yNew, S shapeNew) {
		iX = iXNew;
		iY = iYNew;
		x = xNew;
		y = yNew;
		shape = shapeNew;
	}
	
	public int x() { return x; };
	public int y() { return y; };
	public int iX() { return iX; }
	public int iY() { return iY; }

	public boolean isVacant() { return shape == null; }
	public boolean hasShape() { return shape != null; }
	public boolean canHaveShape() { return true; }
	
	public void setShape(S shapeNew) { 
		shape = shapeNew; 
		if(shape != null) { shape.location().setP(x, y); }
	}
	
	public S shape() { return shape; }
}