package cbit.vcell.desktop.controls;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * The event set listener interface for the simulation feature.
 */
public interface DataListener extends java.util.EventListener {
/**
 * 
 * @param event cbit.vcell.desktop.controls.SimulationEvent
 */
void newData(DataEvent event);
}