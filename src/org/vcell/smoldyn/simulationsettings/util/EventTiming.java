package org.vcell.smoldyn.simulationsettings.util;

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
public class EventTiming {

	private double timestart;
	private double timestop;
	private double timestep;
	
	/**
	 * 
	 * @param start
	 * @param stop
	 * @param step
	 * @throws IllegalArgumentException if stop is less than start or step is negative
	 */
	public EventTiming(double start, double stop, double step) {
		/*
		 * if start is 0, stop is 0 -> b
		 * if start is n, stop is n + dx -> n,i
		 * if start equals stop -> @
		 * etc.
		 */
		if(stop < start) {
			SimulationUtilities.throwIllegalArgumentException("event start time must be before stop time");
		}
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
}
