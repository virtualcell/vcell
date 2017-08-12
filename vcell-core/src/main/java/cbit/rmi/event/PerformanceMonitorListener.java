/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.rmi.event;
/**
 * The event set listener interface for the export feature.
 */
public interface PerformanceMonitorListener extends java.util.EventListener {
/**
 * Insert the method's description here.
 * Creation date: (9/17/2004 2:08:44 PM)
 * @param pme cbit.rmi.event.PerformanceMonitorEvent
 */
void performanceMonitorEvent(PerformanceMonitorEvent pme);
}
