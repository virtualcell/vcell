package org.vcell.sybil.util.graphlayout.tiling;

/*   Tiling  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   Tiling the graph panel for graph layout methods
 */

import java.util.Set;

import org.vcell.sybil.gui.graph.locations.Location;
import org.vcell.sybil.util.graphlayout.placer.Placer;

public interface Tiling<S extends Location.Owner<S>> {
	
	public int iXMin();
	public int iYMin();
	public int iXMax();
	public int iYMax();
	public int xMin();
	public int yMin();
	public int xMax();
	public int yMax();
	public Set<TileSpot<S>> spots();
	public int x(int iX, int iY);
	public int y(int iX, int iY);
	public Placer<S> placer();
	public TileSpot<S> spot(int iX, int iY);
	public TileSpot<S> nearbySpot(int x, int y);

}
