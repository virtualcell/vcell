/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.desktop.controls;

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
