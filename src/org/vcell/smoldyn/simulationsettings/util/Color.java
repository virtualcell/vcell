package org.vcell.smoldyn.simulationsettings.util;

import org.vcell.smoldyn.simulation.SimulationUtilities;


/**
 * A Smoldyn color is specified by a red, green, and blue value, and an alpha value.  The alpha value is currently unused.
 * Each of the color channels may have any value beteen 0 and 1 (inclusive) or null, which signifies a default value of 1.
 * 
 * @author mfenwick
 *
 */
public class Color {

	private final double red;
	private final double green;
	private final double blue;
	private final double alpha;
	
	/**
	 * Construct a new Color.  Alpha is set to 1, as it is currently unused by Smoldyn.
	 * 
	 * @param red -- between 0 and 1
	 * @param green -- between 0 and 1
	 * @param blue -- between 0 and 1
	 */
	public Color(double red, double green, double blue) {
		for(double d : new double [] {red, green, blue}) {
			SimulationUtilities.assertIsTrue("color value must be between 0 and 1", d >= 0 && d <= 1);
		}
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = 1f;
	}
//	public Color(double red, double green, double blue, double alpha) {
//		this(red, green, blue);
//		this.alpha = alpha;
//	}

	public double getRed() {
		return red;
	}

	public double getGreen() {
		return green;
	}

	public double getBlue() {
		return blue;
	}
	
	public double getAlpha() {
		return alpha;
	}
}
