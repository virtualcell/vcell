package org.vcell.sybil.util.graphlayout.tiling;

/*   TriangularTiling  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   Roughly triangular tiling
 */

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.vcell.sybil.gui.graph.locations.Location;
import org.vcell.sybil.util.graphlayout.placer.Placer;
import org.vcell.sybil.util.graphlayout.placer.PlacerDefault;
import org.vcell.sybil.util.matrix.Matrix;

public class TriangularTiling<S extends Location.Owner<S>> implements Tiling<S> {

	protected int xMin, yMin, xMax, yMax;
	protected int iXMin = 1, iYMin = 1, iXMax, iYMax;
	protected PlacerDefault<S> placer = new PlacerDefault<S>();
	protected static final double phix = - (1.0/18.0)*Math.PI;
	protected static final double phiy = (11.0/18.0)*Math.PI;
	protected static final double axx = Math.cos(phix);
	protected static final double ayx = Math.sin(phix);
	protected static final double axy = Math.cos(phiy);
	protected static final double ayy = Math.sin(phiy);
	protected static final double fuzzy = 0.4;
	protected static final double b = 1.5;
	protected Random random = new Random(System.currentTimeMillis());
	protected double a;
	protected Matrix<TileSpot<S>> spotMatrix;
	protected Set<TileSpot<S>> spots = new HashSet<TileSpot<S>>();
	
	public TriangularTiling(int xMinNew, int yMinNew, int xMaxNew, int yMaxNew, int numSpotsMin) {
		xMin = xMinNew;
		yMin = yMinNew;
		xMax = xMaxNew;
		yMax = yMaxNew;
		double w = xMax - xMin;
		double h = yMax - yMin;
		double m = 0.5*Math.sqrt(3.0)*numSpotsMin - 4.0*b;
		a = (Math.sqrt(b*b*(w + h)*(w + h) + m*w*h) - b*(w + h))/m;
		iXMax = (int) (0.5 + (ayy*w - axy*h)/(a*(axx*ayy - ayx*axy)));
		iYMax = (int) (0.5 + (ayx*w - axx*h)/(a*(axy*ayx - axx*ayy)));
		spotMatrix = new Matrix<TileSpot<S>>(iXMin, iYMin, iXMax, iYMax);
		for(int iX = iXMin; iX <= iXMax; iX++) {
			for(int iY = iYMin; iY <= iYMax; iY++) {
				int x = x(iX, iY);
				int y = y(iX, iY);
				if(x >= xMin + a && x <= xMax - a && y >= yMin + a && y <= yMax - a) {
					TileSpot<S> spot = new TileSpot<S>(iX, iY, x, y);
					spotMatrix.put(spot, iX, iY);
					spots.add(spot);
				}
			}			
		}
	}
	
	protected double fuzzy() { return fuzzy*(random.nextDouble() - 0.5); }
	
	public int x(int iX, int iY) { return xMin + (int) (0.5 + a*(iX*axx + iY*axy + fuzzy())); }
	public int y(int iX, int iY) { return yMin + (int) (0.5 + a*(iX*ayx + iY*ayy + fuzzy())); }
	
	public Placer<S> placer() { return placer; }
	
	public int xMin() { return xMin; }
	public int yMin() { return yMin; }
	public int xMax() { return xMax; }
	public int yMax() { return yMax; }

	public int iXMin() { return iXMin; }
	public int iYMin() { return iYMin; }
	public int iXMax() { return iXMax; }
	public int iYMax() { return iYMax; }

	public TileSpot<S> nearbySpot(int x, int y) {
		int dx = x - xMin;
		int dy = y - yMin;
		int iX = (int) (0.5 + (ayy*dx - axy*dy)/(a*(axx*ayy - ayx*axy)));
		int iY = (int) (0.5 + (ayx*dx - axx*dy)/(a*(axy*ayx - axx*ayy)));
		if(iX < iXMin) { iX = iXMin; }
		if(iX > iXMax) { iX = iXMax; }
		if(iY < iYMin) { iY = iYMin; }
		if(iY > iYMax) { iY = iYMax; }
		return spot(iX, iY);
	}

	public Set<TileSpot<S>> spots() { return spots; }
	
	public TileSpot<S> spot(int iX, int iY) {
		if(iX >= iXMin && iX <= iXMax && iY >= iYMin && iY <= iYMax) {
			TileSpot<S> spot = spotMatrix.get(iX, iY);
			if(spot != null) { return spot; }
		} 
		return new TileSpot.NoShapeSpot<S>(iX, iY, x(iX, iY), y(iX, iY));
	}
	
}
