package org.vcell.smoldyn.simulationsettings.util;





/**
 * <p>
 * EventTime is runtime command timing for Smoldyn. Currently It has two child classes
 * 1. default ("i"), runs every dt from >=on until <= off
 * 2. uniform ("n"), runs every n time steps
 * </p>
 * <p>
 * Currently we only use uniform event timing. 
 * 
 * Smoldyn supports more runtime command timing. But right now these two are enough for VCell.
 * </p>
 * 
 * @author mfenwick
 *
 */
public abstract class EventTiming {

	public abstract String getTimingString();
	
	public EventTiming() {
	}
}
