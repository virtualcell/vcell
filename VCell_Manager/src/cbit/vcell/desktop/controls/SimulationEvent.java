package cbit.vcell.desktop.controls;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.simulation.SimulationInfo;
/**
 * This is the event class to support the cbit.vcell.desktop.controls.SimulationListener interface.
 */
public class SimulationEvent extends java.util.EventObject {
	public final static int SIMULATION_ABORTED = 1000;
	public final static int SIMULATION_FINISHED = 1001;

	private int eventType = -1;
	private String message = null;
	private SimulationInfo fieldSimulationInfo = null;
/**
 * SimulationEvent constructor comment.
 * @param source java.lang.Object
 */
public SimulationEvent(java.lang.Object source, SimulationInfo argSourceSimulationInfo, int argEventType, String argMessage) {
	super(source);
	this.eventType = argEventType;
	this.message = argMessage;
	this.fieldSimulationInfo = argSourceSimulationInfo;
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/01 1:24:16 PM)
 * @return int
 */
public int getEventType() {
	return eventType;
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/01 1:51:54 PM)
 * @return java.lang.String
 */
public String getMessage() {
	return message;
}
/**
 * Gets the sourceID property (java.lang.String) value.
 * @return The sourceID property value.
 */
public SimulationInfo getSourceSimulationInfo() {
	return fieldSimulationInfo;
}
}
