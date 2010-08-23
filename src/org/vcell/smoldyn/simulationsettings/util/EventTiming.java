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

	private final double timestart;
	private final double timestop;
	private final double timestep;
	private final EventTimeType eventtimetype;
	
	
	/**
	 * Initializes a new instance with the specified {@link EventTimeType}.  This constructor should only be used when the time type
	 * implies all timing information by itself.  For instance, EventTimeType.before completely specifies the timing information:
	 * do it once, right before the simulation starts.  No stop or step information is required.
	 * 
	 * @param eventtimetype EventTimeType
	 */
	public EventTiming(EventTimeType eventtimetype) {
		SimulationUtilities.checkForNull("event time type", eventtimetype);
		this.eventtimetype = eventtimetype;
		this.timestart = this.timestep = this.timestop = 0;
	}
	
	/**
	 * Initializes a new instance with the specified timing parameters.  This constructor should be used when start, stop, and or step
	 * information is required to completely specify the timing of the event.
	 * 
	 * @param start
	 * @param stop -- greater than or equal to start
	 * @param step -- non-negative
	 */
	public EventTiming(double start, double stop, double step) {
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
		this.eventtimetype = null;
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
	
	public EventTimeType getEventtimetype() {
		return this.eventtimetype;
	}
	
	
	/**
	 * @author mfenwick
	 *
	 * before: immediately before the simulation starts
	 * after: immediately after the simulation ends
	 */
	public static enum EventTimeType {
		before ("b"),
		after ("a"),
		;
		
		private String value;
		
		public String getValue() {
			return this.value;
		}
		private EventTimeType(String value) {
			this.value = value;
		}
	}
}
