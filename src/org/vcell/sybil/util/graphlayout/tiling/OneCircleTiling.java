package org.vcell.sybil.util.graphlayout.tiling;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/*   OneCircleTiling  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   Tiling into one big circle
 */

import org.vcell.sybil.gui.graph.locations.Location;
import org.vcell.sybil.util.graphlayout.placer.Placer;
import org.vcell.sybil.util.graphlayout.placer.PlacerDefault;

public class OneCircleTiling<S extends Location.Owner<S>> implements Tiling<S> {

	protected static final int iXMin = 0;
	protected static final int iYMin = 0;
	protected int iXMax;
	protected static final int iYMax = 0;
	protected double xCenter, yCenter;
	protected double rX, rY;
	protected int xMin, yMin, xMax, yMax;
	protected double dphi;
	protected static final double radiusFraction = 0.8;
	protected Vector<TileSpot<S>> spotList = new Vector<TileSpot<S>>();
	protected Set<TileSpot<S>> spots = new HashSet<TileSpot<S>>();
	protected Placer<S> placer = new PlacerDefault<S>();
	
	public OneCircleTiling(int xMinNew, int yMinNew, int xMaxNew, int yMaxNew, int numSpotsNew) {
		xMin = xMinNew;
		yMin = yMinNew;
		xMax = xMaxNew;
		yMax = yMaxNew;
		iXMax = numSpotsNew - 1;
		dphi = (2*Math.PI)/numSpotsNew;
		xCenter = 0.5*(xMax + xMin);
		yCenter = 0.5*(yMax + yMin);
		rX = 0.5*radiusFraction*(xMax - xMin);
		rY = 0.5*radiusFraction*(yMax - yMin);
		for(int iX = iXMin; iX <= iXMax; iX++) {
			TileSpot<S> spot = new TileSpot<S>(iX, iYMin, x(iX, iYMin), y(iX, iYMin));
			spotList.add(spot);
			spots.add(spot);
		}
	}
	
	public int iXMin() { return iXMin; }
	public int iYMin() { return iYMin; }
	public int iXMax() { return iXMax; }
	public int iYMax() { return iYMax; }
	public int xMin() { return xMin; }
	public int yMin() { return yMin; }
	public int xMax() { return xMax; }
	public int yMax() { return yMax; }


	public TileSpot<S> nearbySpot(int x, int y) {
		double phi = Math.atan2((y - yCenter)/rY, (x - xCenter)/rX);
		int iX = (int) (0.5 + phi/dphi);
		if(iX > iXMax) { iX = iX + iXMin - iXMax; }
		else if(iX < iXMin) { iX = iX +iXMax - iXMin; }
		return spotList.get(iX);
	}

	public TileSpot<S> spot(int iX, int iY) {
		while(iX < iXMin) { iX = iX + iXMax - iXMin; }
		while(iX > iXMax) { iX = iX + iXMin - iXMax; }
		return spotList.get(iX);
	}

	public Set<TileSpot<S>> spots() { return spots; }
	
	public Placer<S> placer() { return placer; }

	public int x(int iX, int iY) { return (int) (0.5 + xCenter + rX*Math.cos(iX*dphi)); }
	public int y(int iX, int iY) { return (int) (0.5 + yCenter + rY*Math.sin(iX*dphi)); }

}
