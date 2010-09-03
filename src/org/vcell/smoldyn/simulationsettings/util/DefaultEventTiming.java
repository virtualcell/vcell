package org.vcell.smoldyn.simulationsettings.util;

import java.io.PrintWriter;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.inputfile.smoldynwriters.Utilities;
import org.vcell.smoldyn.simulation.SimulationUtilities;



/**
 * <p>
 * A Smoldyn event is characterised by its start time, stop time, and step time.  Different combinations of these values can produce 
 * one-time events, repeating events, before events, and after events.
 * </p>
 * <p>
 * Start time: when the event starts.  Stop time:  when the event ends.  Step time: how often the event occurs (for step = n, the event
 * recurs every n time steps).
 * </p>
 * 
 * @author mfenwick
 *
 */
public class DefaultEventTiming extends EventTiming {

	private final double timestart;
	private final double timestop;
	private final double timestep;
	
	/**
	 * Initializes a new instance with the specified timing parameters.  This constructor should be used when start, stop, and or step
	 * information is required to completely specify the timing of the event.
	 * 
	 * @param start
	 * @param stop -- greater than or equal to start
	 * @param step -- non-negative
	 */
	public DefaultEventTiming(double start, double stop, double step) {
		/*
		 * if start is 0, stop is 0 -> b
		 * if start is n, stop is n + dx -> n,i
		 * if start equals stop -> @
		 * etc.
		 */
		SimulationUtilities.assertIsTrue("event start time must not be after stop time", start <= stop);
		SimulationUtilities.checkForNonNegative("step", step);
		this.timestart = start;
		this.timestop = stop;
		this.timestep = step;
	}

	public double getTimestart() {
		return timestart;
	}

	public double getTimestop() {
		return timestop;
	}

	public double getTimestep() {
		return timestep;
	}

	@Override
	public String getTimingString() {
		return SmoldynFileKeywords.RuntimeCommand.i.name() + " " + timestart + " " + timestop + " " + timestep;
	}
}
