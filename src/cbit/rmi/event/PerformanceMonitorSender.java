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
 * Insert the type's description here.
 * Creation date: (11/12/2000 9:31:08 PM)
 * @author: IIM
 */
public interface PerformanceMonitorSender {
/**
 * Insert the method's description here.
 * Creation date: (9/17/2004 2:14:53 PM)
 * @param pml cbit.rmi.event.PerformanceMonitorListener
 */
void addPerformanceMonitorListener(PerformanceMonitorListener pml);


/**
 * Insert the method's description here.
 * Creation date: (9/17/2004 2:14:53 PM)
 * @param pml cbit.rmi.event.PerformanceMonitorListener
 */
void removePerformanceMonitorListener(PerformanceMonitorListener pml);
}
