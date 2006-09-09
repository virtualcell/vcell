package cbit.vcell.desktop.controls;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This is the event multicaster class to support the cbit.vcell.desktop.controls.WorkspaceListenerinterface.
 *
 * WARNING ... BUG IN GENERATED CODE FROM VisualAge for Java !!!!!!!!!
 *
 * This was a generated subclass of AWTEventMulticaster and the remove(EventListener) method was to be 
 * overridden.  Instead, VisualAge for Java generated the method: remove(WorkspaceListener) which doesn't
 * override anything ... and causes ClassCastExceptions upon removal of a listener.
 *
 * The fix for this bug is to change the argument type of the generated remove() method to that of java.util.EventListener.
 *
 */
public class WorkspaceEventMulticaster extends java.awt.AWTEventMulticaster implements cbit.vcell.desktop.controls.WorkspaceListener {
/**
 * Constructor to support multicast events.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected WorkspaceEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return cbit.vcell.desktop.controls.WorkspaceListener
 * @param a cbit.vcell.desktop.controls.WorkspaceListener
 * @param b cbit.vcell.desktop.controls.WorkspaceListener
 */
public static cbit.vcell.desktop.controls.WorkspaceListener add(cbit.vcell.desktop.controls.WorkspaceListener a, cbit.vcell.desktop.controls.WorkspaceListener b) {
	return (cbit.vcell.desktop.controls.WorkspaceListener)addInternal(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return java.util.EventListener
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected static java.util.EventListener addInternal(java.util.EventListener a, java.util.EventListener b) {
	if (a == null)  return b;
	if (b == null)  return a;
	return new WorkspaceEventMulticaster(a, b);
}
/**
 * 
 * @param event cbit.vcell.desktop.controls.WorkspaceEvent
 */
public void bioModelSaved(cbit.vcell.desktop.controls.WorkspaceEvent event) {
	((cbit.vcell.desktop.controls.WorkspaceListener)a).bioModelSaved(event);
	((cbit.vcell.desktop.controls.WorkspaceListener)b).bioModelSaved(event);
}
/**
 * 
 * @param event cbit.vcell.desktop.controls.WorkspaceEvent
 */
public void geometrySaved(cbit.vcell.desktop.controls.WorkspaceEvent event) {
	((cbit.vcell.desktop.controls.WorkspaceListener)a).geometrySaved(event);
	((cbit.vcell.desktop.controls.WorkspaceListener)b).geometrySaved(event);
}
/**
 * 
 * @param event cbit.vcell.desktop.controls.WorkspaceEvent
 */
public void imageSaved(cbit.vcell.desktop.controls.WorkspaceEvent event) {
	((cbit.vcell.desktop.controls.WorkspaceListener)a).imageSaved(event);
	((cbit.vcell.desktop.controls.WorkspaceListener)b).imageSaved(event);
}
/**
 * 
 * @param event cbit.vcell.desktop.controls.WorkspaceEvent
 */
public void mathModelSaved(cbit.vcell.desktop.controls.WorkspaceEvent event) {
	((cbit.vcell.desktop.controls.WorkspaceListener)a).mathModelSaved(event);
	((cbit.vcell.desktop.controls.WorkspaceListener)b).mathModelSaved(event);
}
/**
 * Remove listener to support multicast events.
 * @return cbit.vcell.desktop.controls.WorkspaceListener
 * @param l cbit.vcell.desktop.controls.WorkspaceListener
 * @param oldl cbit.vcell.desktop.controls.WorkspaceListener
 */
public static cbit.vcell.desktop.controls.WorkspaceListener remove(cbit.vcell.desktop.controls.WorkspaceListener l, cbit.vcell.desktop.controls.WorkspaceListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof WorkspaceEventMulticaster)
		return (cbit.vcell.desktop.controls.WorkspaceListener)((cbit.vcell.desktop.controls.WorkspaceEventMulticaster) l).remove(oldl);
	return l;
}
/**
 * 
 * @return java.util.EventListener
 * @param oldl cbit.vcell.desktop.controls.WorkspaceListener
 */
protected java.util.EventListener remove(java.util.EventListener oldl) {
	if (oldl == a)  return b;
	if (oldl == b)  return a;
	java.util.EventListener a2 = removeInternal(a, oldl);
	java.util.EventListener b2 = removeInternal(b, oldl);
	if (a2 == a && b2 == b)
		return this;
	return addInternal(a2, b2);
}
}
