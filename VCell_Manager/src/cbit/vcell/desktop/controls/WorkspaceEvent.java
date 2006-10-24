package cbit.vcell.desktop.controls;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This is the event class to support the cbit.vcell.desktop.controls.WorkspaceListener interface.
 */
public class WorkspaceEvent extends java.util.EventObject {
/**
 * WorkspaceEvent constructor comment.
 * @param source java.lang.Object
 */
public WorkspaceEvent(AbstractWorkspace source) {
	super(source);
}
}
