package cbit.vcell.desktop.controls;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * The event set listener interface for the workspace feature.
 */
public interface WorkspaceListener extends java.util.EventListener {
/**
 * 
 * @param event cbit.vcell.desktop.controls.WorkspaceEvent
 */
void bioModelSaved(cbit.vcell.desktop.controls.WorkspaceEvent event);
/**
 * 
 * @param event cbit.vcell.desktop.controls.WorkspaceEvent
 */
void geometrySaved(cbit.vcell.desktop.controls.WorkspaceEvent event);
/**
 * 
 * @param event cbit.vcell.desktop.controls.WorkspaceEvent
 */
void imageSaved(cbit.vcell.desktop.controls.WorkspaceEvent event);
/**
 * 
 * @param event cbit.vcell.desktop.controls.WorkspaceEvent
 */
void mathModelSaved(cbit.vcell.desktop.controls.WorkspaceEvent event);
}
