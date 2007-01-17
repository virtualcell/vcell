package cbit.rmi.event;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.

 * Insert the type's description here.
 * Creation date: (11/12/2000 9:31:08 PM)
 * @author: Fei Gao
 */
public interface VCellMessageEventSender {
/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 5:17:28 PM)
 * @param listener cbit.vcell.desktop.controls.ExportListener
 */
void addVCellMessageEventListener(VCellMessageEventListener listener);


/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 5:17:39 PM)
 * @param listener cbit.vcell.desktop.controls.ExportListener
 */
void removeVCellMessageEventListener(VCellMessageEventListener listener);
}