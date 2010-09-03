package org.vcell.smoldyn.simulationsettings.util;

import java.io.PrintWriter;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.simulation.SimulationUtilities;



/**
 * <p>
 * A Smoldyn event which runs every n time steps
 * 
 * @author mfenwick
 *
 */
public class UniformEventTiming extends EventTiming {

	private int everyN;
	
	/**
	 * Initializes a new instance with the specified timing parameters.  This constructor should be used when start, stop, and or step
	 * information is required to completely specify the timing of the event.
	 * 
	 * @param start
	 * @param stop -- greater than or equal to start
	 * @param step -- non-negative
	 */
	public UniformEventTiming(int everyN) {
		this.everyN = everyN;
	}

	@Override
	public String getTimingString() { 
		return SmoldynFileKeywords.RuntimeCommand.n.name() + " " + everyN;
	}
}
