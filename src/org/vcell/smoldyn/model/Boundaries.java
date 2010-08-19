package org.vcell.smoldyn.model;

import org.vcell.smoldyn.simulation.SimulationUtilities;



/**
 * Boundaries set the limits of the simulation volume.
 * Both a high and a low value must be specified for each dimension.
 * 
 * Invariants: low <= high for each dimension
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
	 * @param xlow
	 * @param xhigh -- greater than or equal to xlow
	 * @param ylow
	 * @param yhigh -- greater than or equal to ylow
	 * @param zlow
	 * @param zhigh -- greater than or equal to zlow
	 */
	public Boundaries(double xlow, double xhigh, double ylow, double yhigh, double zlow, double zhigh) {
		SimulationUtilities.assertIsTrue("boundary value error", xlow <= xhigh);
		SimulationUtilities.assertIsTrue("boundary value error", ylow <= yhigh);
		SimulationUtilities.assertIsTrue("boundary value error", zlow <= zhigh);		
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
