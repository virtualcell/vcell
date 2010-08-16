package org.vcell.smoldyn.model.util;

import org.vcell.smoldyn.model.Boundaries;
import org.vcell.smoldyn.model.Geometryable;
import org.vcell.smoldyn.simulation.SimulationUtilities;




/**
 * A three-dimensional point composed of an x, y, and z coordinate.
 * It IS permissible for any of x, y, z to be null.
 * 
 * Invariants:
 * 		x, y, and z are not changeable
 * 
 * @author mfenwick
 *
 */
public class Point {
	private final Double X;
	private final Double Y;
	private final Double Z;

	
	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	private Point(Double x, Double y, Double z){
		this.X = x;
		this.Y = y;
		this.Z = z;
	}

	
	public Double getX() {
		return X;
	}

	public Double getY() {
		return Y;
	}

	public Double getZ() {
		return Z;
	}

	
	/**
	 * A way to "safely" create points.
	 * 
	 * Invariants:
	 * 		xlow <= xhigh
	 * 		ylow <= yhigh
	 * 		zlow <= zhigh
	 * 
	 * @author mfenwick
	 */
	public static class PointFactory {
		
		private final double xlow;
		private final double xhigh;
		private final double ylow;
		private final double yhigh;
		private final double zlow;
		private final double zhigh;

		
		/**
		 * @param xlow
		 * @param xhigh
		 * @param ylow
		 * @param yhigh
		 * @param zlow
		 * @param zhigh
		 */
		public PointFactory(double xlow, double xhigh, double ylow, double yhigh, double zlow, double zhigh) {
			if(xhigh < xlow) {
				SimulationUtilities.throwInvariantConditionViolatedException("xhigh less than xlow");
			}
			if(yhigh < ylow) {
				SimulationUtilities.throwInvariantConditionViolatedException("yhigh less than ylow");
			}
			if(zhigh < zlow) {
				SimulationUtilities.throwInvariantConditionViolatedException("zhigh less than zlow");
			}
			this.xlow = xlow;
			this.xhigh = xhigh;
			this.ylow = ylow;
			this.yhigh = yhigh;
			this.zlow = zlow;
			this.zhigh = zhigh;
		}
		
		public PointFactory(Geometryable geometry) {
			Boundaries boundaries = geometry.getBoundaries();
			this.xlow = boundaries.getXlow();
			this.xhigh = boundaries.getXhigh();
			this.ylow = boundaries.getYlow();
			this.yhigh = boundaries.getYhigh();
			this.zlow = boundaries.getZlow();
			this.zhigh = boundaries.getZhigh();
		}
		
		public Point getNewPoint(Double x, Double y, Double z) {
			if (x != null && (x < this.xlow || x > this.xhigh)) {
				SimulationUtilities.throwInvariantConditionViolatedException("x coordinate of point");
			}
			if (y != null && (y < this.ylow || y > this.yhigh)) {
				SimulationUtilities.throwInvariantConditionViolatedException("y coordinate of point");
			}
			if (z != null && (z < this.zlow || z > this.zhigh)) {
				SimulationUtilities.throwInvariantConditionViolatedException("z coordinate of point");
			}
			return new Point(x, y, z);
		}
	}
}
