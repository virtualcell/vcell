package cbit.vcell.desktop.controls;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * The event set listener interface for the simulation feature.
 */
public interface SimulationListener extends java.util.EventListener {
/**
 * 
 * @param event cbit.vcell.desktop.controls.SimulationEvent
 */
void simulationAborted(cbit.vcell.desktop.controls.SimulationEvent event);
/**
 * 
 * @param event cbit.vcell.desktop.controls.SimulationEvent
 */
void simulationFinished(cbit.vcell.desktop.controls.SimulationEvent event);
}
