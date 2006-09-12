package cbit.vcell.export.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This is the event multicaster class to support the cbit.vcell.export.ImageSettingsPanelListenerEventMulticaster interface.
 */
public class ImageSettingsPanelListenerEventMulticaster extends java.awt.AWTEventMulticaster implements cbit.vcell.export.gui.ImageSettingsPanelListener {
/**
 * Constructor to support multicast events.
 * @param a cbit.vcell.export.ImageSettingsPanelListener
 * @param b cbit.vcell.export.ImageSettingsPanelListener
 */
protected ImageSettingsPanelListenerEventMulticaster(cbit.vcell.export.gui.ImageSettingsPanelListener a, cbit.vcell.export.gui.ImageSettingsPanelListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return cbit.vcell.export.ImageSettingsPanelListener
 * @param a cbit.vcell.export.ImageSettingsPanelListener
 * @param b cbit.vcell.export.ImageSettingsPanelListener
 */
public static cbit.vcell.export.gui.ImageSettingsPanelListener add(cbit.vcell.export.gui.ImageSettingsPanelListener a, cbit.vcell.export.gui.ImageSettingsPanelListener b) {
	if (a == null)  return b;
	if (b == null)  return a;
	return new ImageSettingsPanelListenerEventMulticaster(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
	((cbit.vcell.export.gui.ImageSettingsPanelListener)a).JButtonCancelAction_actionPerformed(newEvent);
	((cbit.vcell.export.gui.ImageSettingsPanelListener)b).JButtonCancelAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonOKAction_actionPerformed(java.util.EventObject newEvent) {
	((cbit.vcell.export.gui.ImageSettingsPanelListener)a).JButtonOKAction_actionPerformed(newEvent);
	((cbit.vcell.export.gui.ImageSettingsPanelListener)b).JButtonOKAction_actionPerformed(newEvent);
}
/**
 * Remove listener to support multicast events.
 * @return cbit.vcell.export.ImageSettingsPanelListener
 * @param a cbit.vcell.export.ImageSettingsPanelListener
 * @param b cbit.vcell.export.ImageSettingsPanelListener
 */
public static cbit.vcell.export.gui.ImageSettingsPanelListener remove(cbit.vcell.export.gui.ImageSettingsPanelListener a, cbit.vcell.export.gui.ImageSettingsPanelListener b) {
	return (cbit.vcell.export.gui.ImageSettingsPanelListener)removeInternal(a, b);
}
}
