/**
 * 
 */
package org.vcell.smoldyn.simulationsettings;

import org.vcell.smoldyn.simulationsettings.util.EventTiming;

/**
 * A ControlEvent allows the user to specify actions to occur during execution
 * of the Simulation that change the simulation conditions.  Examples include 
 * pausing or stopping the simulation, and changing the random number seed.
 * TODO only pause is currently implemented--figure out what else is desirable
 * 
 * @author mfenwick
 *
 */
@Deprecated
public class ControlEvent {

	private EventTiming eventtiming;
	private EventType eventtype;
	
	
	/**
	 * Instantiates a ControlEvent with the given timing properties and event type.
	 * The timing properties determine when and how often an event occurs, and the
	 * event type is chosen from an enumeration determined by those that Smoldyn supports.
	 * 
	 * @param eventtiming
	 * @param eventtype
	 */
	public ControlEvent(EventTiming eventtiming, EventType eventtype) {
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
		
		pause,
		
	}
}
