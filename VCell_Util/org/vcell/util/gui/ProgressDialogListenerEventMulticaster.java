package org.vcell.util.gui;
/**
 * This is the event multicaster class to support the cbit.util.ProgressDialogListenerEventMulticaster interface.
 */
public class ProgressDialogListenerEventMulticaster extends java.awt.AWTEventMulticaster implements org.vcell.util.gui.ProgressDialogListener {
/**
 * Constructor to support multicast events.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected ProgressDialogListenerEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}


/**
 * Add new listener to support multicast events.
 * @return cbit.util.ProgressDialogListener
 * @param a cbit.util.ProgressDialogListener
 * @param b cbit.util.ProgressDialogListener
 */
public static org.vcell.util.gui.ProgressDialogListener add(org.vcell.util.gui.ProgressDialogListener a, org.vcell.util.gui.ProgressDialogListener b) {
	return (org.vcell.util.gui.ProgressDialogListener)addInternal(a, b);
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
	return new ProgressDialogListenerEventMulticaster(a, b);
}


/**
 * 
 * @param newEvent java.util.EventObject
 */
public void cancelButton_actionPerformed(java.util.EventObject newEvent) {
	((org.vcell.util.gui.ProgressDialogListener)a).cancelButton_actionPerformed(newEvent);
	((org.vcell.util.gui.ProgressDialogListener)b).cancelButton_actionPerformed(newEvent);
}


/**
 * 
 * @return java.util.EventListener
 * @param oldl cbit.util.ProgressDialogListener
 */
protected java.util.EventListener remove(org.vcell.util.gui.ProgressDialogListener oldl) {
	if (oldl == a)  return b;
	if (oldl == b)  return a;
	java.util.EventListener a2 = removeInternal(a, oldl);
	java.util.EventListener b2 = removeInternal(b, oldl);
	if (a2 == a && b2 == b)
		return this;
	return addInternal(a2, b2);
}


/**
 * Remove listener to support multicast events.
 * @return cbit.util.ProgressDialogListener
 * @param l cbit.util.ProgressDialogListener
 * @param oldl cbit.util.ProgressDialogListener
 */
public static org.vcell.util.gui.ProgressDialogListener remove(org.vcell.util.gui.ProgressDialogListener l, org.vcell.util.gui.ProgressDialogListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof ProgressDialogListenerEventMulticaster)
		return (org.vcell.util.gui.ProgressDialogListener)((org.vcell.util.gui.ProgressDialogListenerEventMulticaster) l).remove(oldl);
	return l;
}
}