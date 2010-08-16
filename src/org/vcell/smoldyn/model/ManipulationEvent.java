package org.vcell.smoldyn.model;

import org.vcell.smoldyn.simulationsettings.util.EventTiming;

/**
 * A ManipulationEvent allows the user to change the Model during the course of
 * execution of a simulation.  Possibilities include removing molecules and 
 * moving molecules.
 * 
 * Invariants:
 * 		eventtiming and eventtype must not be null
 * 
 * @author mfenwick
 *
 */
public class ManipulationEvent {

	private EventTiming eventtiming;
	private EventType eventtype;
	
	
	/**
	 * A ManipulationEvent needs timing parameters and an event type.  The timing
	 * parameters determine when and how often the event occurs, while the event
	 * type is what happens, and is selected from an enumeration of manipulation
	 * events that Smoldyn supports.
	 * 
	 * @param eventtiming
	 * @param eventtype
	 */
	public ManipulationEvent(EventTiming eventtiming, EventType eventtype) {
		this.eventtiming = eventtiming;
		this.eventtype = eventtype;
	}
	
	public EventTiming getTiming() {
		// TODO Auto-generated method stub
		return eventtiming;
	}

	public EventType getEventType() {
		// TODO Auto-generated method stub
		return eventtype;
	}
	
	
	
	public enum EventType {
		
	}
}
