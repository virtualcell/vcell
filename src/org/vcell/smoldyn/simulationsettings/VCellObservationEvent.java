package org.vcell.smoldyn.simulationsettings;

import org.vcell.smoldyn.simulation.SimulationUtilities;
import org.vcell.smoldyn.simulationsettings.util.EventTiming;


/**
 * The VCellEventType is a set of additional runtime-commands added to Smoldyn by the VCell team:  as such, they are not documented
 * in the Smoldyn manual, nor are they available from the Smoldyn website.
 * 
 * @author mfenwick
 *
 */
public class VCellObservationEvent {
	
	private final VCellEventType eventtype;
	private final EventTiming eventtiming;
	
	
	/**
	 * @param eventtiming
	 * @param eventtype
	 */
	public VCellObservationEvent(EventTiming eventtiming, VCellEventType eventtype){
		SimulationUtilities.checkForNull("argument to VCellObservationEvent", eventtiming, eventtype);
		this.eventtiming = eventtiming;
		this.eventtype = eventtype;
	}
	
	
	public EventTiming getEventTiming() {
		return this.eventtiming;
	}
	
	public VCellEventType getEventType() {
		return this.eventtype;
	}
	
	
	/**
	 * @author mfenwick
	 *
	 */
	public static enum VCellEventType {
		PRINT_PROGRESS (new Option [] {}),
		WRITE_OUTPUT (new Option [] {Option.MESHSIZEX, Option.MESHSIZEY, Option.MESHSIZEZ}),
		;
		
		private VCellEventType(Option [] args) {
			
		}
	}
	
	/**
	 * @author mfenwick
	 * 
	 * The possible options that may be useful for specifying simulation observation.
	 *
	 */
	private static enum Option {
		MESHSIZEX,
		MESHSIZEY,
		MESHSIZEZ,
		;
	}
}
