package org.vcell.smoldyn.simulationsettings;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.simulation.SimulationUtilities;
import org.vcell.smoldyn.simulationsettings.util.EventTiming;


/**
 * The VCellEventType is a set of additional runtime-commands added to Smoldyn by the VCell team:  as such, they are not documented
 * in the Smoldyn manual, nor are they available from the Smoldyn website.
 * 
 * @author mfenwick
 *
 */
public abstract class VCellObservationEvent {
	private final EventTiming eventtiming;
	
	
	/**
	 * @param eventtiming
	 * @param eventtype
	 */
	public VCellObservationEvent(EventTiming eventtiming){
		this.eventtiming = eventtiming;
	}
	
	
	public EventTiming getEventTiming() {
		return this.eventtiming;
	}

	abstract String getCommandString();
	
	public String getRuntimeCommandTimingString() {
		return SmoldynFileKeywords.RuntimeCommand.cmd.name() + " " + eventtiming.getTimingString() + " " + getCommandString();
	}
}
