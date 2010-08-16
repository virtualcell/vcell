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

	private Float red;
	private Float green;
	private Float blue;
	private Float alpha;
	
	/**
	 * Construct a new Color.  Alpha is set to 1, as it is currently unused by Smoldyn.
	 * 
	 * @param red
	 * @param green
	 * @param blue
	 * @throws IllegalArgumentException if red, green, or blue is less than 0 or greater than 1
	 */
	public Color(Float red, Float green, Float blue) {
		if(red < 0 || red > 1) {
			SimulationUtilities.throwIllegalArgumentException("invalid value for color channel red -- must be between 0 and 1 (value was: <" +
					red + ">)");
		}
		if(green < 0 || green > 1) {
			SimulationUtilities.throwIllegalArgumentException("invalid value for color channel green -- must be between 0 and 1 (value was: <" +
					green + ">)");
		}
		if(blue < 0 || blue > 1) {
			SimulationUtilities.throwIllegalArgumentException("invalid value for color channel blue -- must be between 0 and 1 (value was: <" +
					blue + ">)");
		}
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = 1f;
	}
//	public Color(Float red, Float green, Float blue, Float alpha) {
//		this(red, green, blue);
//		this.alpha = alpha;
//	}

	public Float getRed() {
		return red;
	}

	public Float getGreen() {
		return green;
	}

	public Float getBlue() {
		return blue;
	}
	
	public Float getAlpha() {
		return alpha;
	}
}
