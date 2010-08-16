package org.vcell.smoldyn.simulationsettings.util;

import org.vcell.smoldyn.model.Species;
import org.vcell.smoldyn.model.SpeciesState.StateType;
import org.vcell.smoldyn.simulation.SimulationUtilities;


/**
 * The display information pertaining to a {@link SpeciesStateGraphics}.  Smoldyn allows display information to vary based on 
 * {@link Species} and {@link StateType}.
 * 
 * color: the color ({@link Color})
 * displaysize: the number of pixels in the width on the display
 * 
 * @author mfenwick
 *
 */
@Deprecated
public class SpeciesStateGraphics {

	private Color color;
	private Integer displaysize;

	
	/**
	 * @param color
	 * @param displaysize
	 * @throws NullPointerException if color is null
	 * @throws IllegalArgumentException if displaysize is less than one
	 */
	public SpeciesStateGraphics(Color color, int displaysize) {
		if(color == null) {
			SimulationUtilities.throwNullPointerException("color");
		}
		if(displaysize < 1) {
			SimulationUtilities.throwIllegalArgumentException("displaysize must be positive");
		}
		this.color = color;
		this.displaysize = displaysize;
	}
	
		
	public Color getColor() {
		return color;
	}
	
	public Integer getDisplaysize() {
		return displaysize;
	}
}
