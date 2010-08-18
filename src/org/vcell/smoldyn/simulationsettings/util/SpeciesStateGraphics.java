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
public class SpeciesStateGraphics {

	private Color color;
	private Integer displaysize;

	
	/**
	 * @param color -- not null
	 * @param displaysize -- positive
	 */
	public SpeciesStateGraphics(Color color, int displaysize) {
		SimulationUtilities.checkForNull("color", color);
		SimulationUtilities.checkForPositive("displaysize", displaysize);
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
