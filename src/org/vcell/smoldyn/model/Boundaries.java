package org.vcell.smoldyn.model;

import org.vcell.smoldyn.simulation.SimulationUtilities;



/**
 * A boundary limits the simulation volume in one dimension.
 * Both a high and a low value must be specified, which constrains
 * the simulation region between the given values.
 * 
 * Invariants: low < high
 * 		both low and high are unchangeable
 * 
 * @author mfenwick
 *
 */
public class Boundaries {

	private final double xlow;
	private final double xhigh;	
	private final double ylow;
	private final double yhigh;	
	private final double zlow;
	private final double zhigh;	
	
	
	/**
	 * Instantiates the simulation size on one dimension
	 * 
	 * @param low 
	 * @param high
	 * @throws IllegalArgumentException if the high value
	 * is less than or equal to the low value.
	 */
	public Boundaries(double xlow, double xhigh, double ylow, double yhigh, double zlow, double zhigh) {
		if (xhigh <= xlow) {
			SimulationUtilities.throwIllegalArgumentException("a boundary must have a high value greater than its low value");
		}
		if (yhigh <= ylow) {
			SimulationUtilities.throwIllegalArgumentException("a boundary must have a high value greater than its low value");
		}
		if (zhigh <= zlow) {
			SimulationUtilities.throwIllegalArgumentException("a boundary must have a high value greater than its low value");
		}		
		this.xlow = xlow;
		this.xhigh = xhigh;
		this.yhigh = yhigh;
		this.ylow = ylow;
		this.zlow = zlow;
		this.zhigh = zhigh;
	}


	public double getXlow() {
		return xlow;
	}


	public double getXhigh() {
		return xhigh;
	}


	public double getYlow() {
		return ylow;
	}


	public double getYhigh() {
		return yhigh;
	}


	public double getZlow() {
		return zlow;
	}


	public double getZhigh() {
		return zhigh;
	}

}
