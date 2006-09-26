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
public interface ExportSender {
/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 5:17:28 PM)
 * @param listener cbit.vcell.desktop.controls.ExportListener
 */
void addExportListener(ExportListener listener);
/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 5:17:39 PM)
 * @param listener cbit.vcell.desktop.controls.ExportListener
 */
void removeExportListener(ExportListener listener);
}
