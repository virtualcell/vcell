package cbit.rmi.event;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * The event set listener interface for the export feature.
 */
public interface ExportListener extends java.util.EventListener {
/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 6:27:03 PM)
 * @param event cbit.rmi.event.ExportEvent
 */
void exportMessage(ExportEvent event);
}
