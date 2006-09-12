package cbit.rmi.event;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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