package org.vcell.smoldyn.simulationsettings;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.simulationsettings.util.EventTiming;
import org.vcell.util.ISize;


/**
 * The VCellEventType is a set of additional runtime-commands added to Smoldyn by the VCell team:  as such, they are not documented
 * in the Smoldyn manual, nor are they available from the Smoldyn website.
 * 
 * @author mfenwick
 *
 */
public class VCellObservationEventWriteOutput extends VCellObservationEvent {
	private int dimension;
	private ISize meshSize;
	/**
	 * @param eventtiming
	 */
	public VCellObservationEventWriteOutput(EventTiming eventtiming, int dim, ISize mesh){
		super(eventtiming);
		this.dimension = dim;
		this.meshSize = mesh;
	}	
	

	
	public String getCommandString() {
		StringBuffer sb = new StringBuffer();
		sb.append(SmoldynFileKeywords.SimulationControl.vcellWriteOutput.name() + " " + meshSize.getX());
		if (dimension > 1) {
			sb.append(" " + meshSize.getY());
			
			if (dimension > 2) {
				sb.append(" " + meshSize.getZ());				
			}
		}
		
		return sb.toString();
	}	
}
