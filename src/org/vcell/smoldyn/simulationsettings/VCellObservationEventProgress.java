package org.vcell.smoldyn.simulationsettings;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.simulationsettings.util.EventTiming;


/**
 * The VCellEventType is a set of additional runtime-commands added to Smoldyn by the VCell team:  as such, they are not documented
 * in the Smoldyn manual, nor are they available from the Smoldyn website.
 * 
 * @author mfenwick
 *
 */
public class VCellObservationEventProgress extends VCellObservationEvent {
	
	/**
	 * @param eventtiming
	 */
	public VCellObservationEventProgress(EventTiming eventtiming){
		super(eventtiming);
	}	
	

	
	public String getCommandString() {
		return SmoldynFileKeywords.SimulationControl.vcellPrintProgress.name();
	}
	
	/**
	 * @author mfenwick
	 *
	 */
	public static enum VCellEventType {
		PRINT_PROGRESS (SmoldynFileKeywords.SimulationControl.vcellPrintProgress.name()),
		WRITE_OUTPUT (SmoldynFileKeywords.SimulationControl.vcellWriteOutput.name()),
		;
		
		private String name;
		private VCellEventType(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}	
}
