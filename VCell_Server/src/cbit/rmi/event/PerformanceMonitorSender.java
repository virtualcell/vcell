package cbit.rmi.event;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

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