package cbit.vcell.export;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This is the event multicaster class to support the cbit.vcell.export.ExportSettingsListenerEventMulticaster interface.
 *
 * WARNING ... BUG IN GENERATED CODE FROM VisualAge for Java !!!!!!!!!
 *
 * This was a generated subclass of AWTEventMulticaster and the remove(EventListener) method was to be 
 * overridden.  Instead, VisualAge for Java generated the method: remove(ExportSettingsListener) which doesn't
 * override anything ... and causes ClassCastExceptions upon removal of a listener.
 *
 * The fix for this bug is to change the argument type of the generated remove() method to that of java.util.EventListener.
 *
 */
public class ExportSettingsListenerEventMulticaster extends java.awt.AWTEventMulticaster implements ExportSettingsListener {
/**
 * Constructor to support multicast events.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected ExportSettingsListenerEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return cbit.vcell.export.ExportSettingsListener
 * @param a cbit.vcell.export.ExportSettingsListener
 * @param b cbit.vcell.export.ExportSettingsListener
 */
public static cbit.vcell.export.ExportSettingsListener add(cbit.vcell.export.ExportSettingsListener a, cbit.vcell.export.ExportSettingsListener b) {
	return (cbit.vcell.export.ExportSettingsListener)addInternal(a, b);
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
	return new ExportSettingsListenerEventMulticaster(a, b);
}
/**
 * Remove listener to support multicast events.
 * @return cbit.vcell.export.ExportSettingsListener
 * @param l cbit.vcell.export.ExportSettingsListener
 * @param oldl cbit.vcell.export.ExportSettingsListener
 */
public static cbit.vcell.export.ExportSettingsListener remove(cbit.vcell.export.ExportSettingsListener l, cbit.vcell.export.ExportSettingsListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof ExportSettingsListenerEventMulticaster)
		return (cbit.vcell.export.ExportSettingsListener)((cbit.vcell.export.ExportSettingsListenerEventMulticaster) l).remove(oldl);
	return l;
}
/**
 * 
 * @return java.util.EventListener
 * @param oldl cbit.vcell.export.ExportSettingsListener
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
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void SettingsOK_ActionPerformed(java.util.EventObject newEvent) {
	((cbit.vcell.export.ExportSettingsListener)a).SettingsOK_ActionPerformed(newEvent);
	((cbit.vcell.export.ExportSettingsListener)b).SettingsOK_ActionPerformed(newEvent);
}
}
