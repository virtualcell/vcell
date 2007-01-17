package cbit.vcell.export;
/**
 * This is the event multicaster class to support the cbit.vcell.export.RasterSettingsPanelListenerEventMulticaster interface.
 */
public class RasterSettingsPanelListenerEventMulticaster extends java.awt.AWTEventMulticaster implements cbit.vcell.export.RasterSettingsPanelListener {
/**
 * Constructor to support multicast events.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected RasterSettingsPanelListenerEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}


/**
 * Add new listener to support multicast events.
 * @return cbit.vcell.export.RasterSettingsPanelListener
 * @param a cbit.vcell.export.RasterSettingsPanelListener
 * @param b cbit.vcell.export.RasterSettingsPanelListener
 */
public static cbit.vcell.export.RasterSettingsPanelListener add(cbit.vcell.export.RasterSettingsPanelListener a, cbit.vcell.export.RasterSettingsPanelListener b) {
	return (cbit.vcell.export.RasterSettingsPanelListener)addInternal(a, b);
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
	return new RasterSettingsPanelListenerEventMulticaster(a, b);
}


/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
	((cbit.vcell.export.RasterSettingsPanelListener)a).JButtonCancelAction_actionPerformed(newEvent);
	((cbit.vcell.export.RasterSettingsPanelListener)b).JButtonCancelAction_actionPerformed(newEvent);
}


/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonOKAction_actionPerformed(java.util.EventObject newEvent) {
	((cbit.vcell.export.RasterSettingsPanelListener)a).JButtonOKAction_actionPerformed(newEvent);
	((cbit.vcell.export.RasterSettingsPanelListener)b).JButtonOKAction_actionPerformed(newEvent);
}


/**
 * 
 * @return java.util.EventListener
 * @param oldl cbit.vcell.export.RasterSettingsPanelListener
 */
protected java.util.EventListener remove(cbit.vcell.export.RasterSettingsPanelListener oldl) {
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
 * @return cbit.vcell.export.RasterSettingsPanelListener
 * @param l cbit.vcell.export.RasterSettingsPanelListener
 * @param oldl cbit.vcell.export.RasterSettingsPanelListener
 */
public static cbit.vcell.export.RasterSettingsPanelListener remove(cbit.vcell.export.RasterSettingsPanelListener l, cbit.vcell.export.RasterSettingsPanelListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof RasterSettingsPanelListenerEventMulticaster)
		return (cbit.vcell.export.RasterSettingsPanelListener)((cbit.vcell.export.RasterSettingsPanelListenerEventMulticaster) l).remove(oldl);
	return l;
}
}