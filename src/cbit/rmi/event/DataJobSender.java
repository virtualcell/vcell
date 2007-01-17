package cbit.rmi.event;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

import cbit.vcell.desktop.controls.*;
public interface DataJobSender {
/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 5:17:28 PM)
 * @param listener cbit.vcell.desktop.controls.ExportListener
 */
void addDataJobListener(DataJobListener listener);


/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 5:17:39 PM)
 * @param listener cbit.vcell.desktop.controls.ExportListener
 */
void removeDataJobListener(DataJobListener listener);
}