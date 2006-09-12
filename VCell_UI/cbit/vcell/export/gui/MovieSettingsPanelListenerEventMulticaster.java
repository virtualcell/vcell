package cbit.vcell.export.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This is the event multicaster class to support the cbit.vcell.export.MovieSettingsPanelListenerEventMulticaster interface.
 */
public class MovieSettingsPanelListenerEventMulticaster extends java.awt.AWTEventMulticaster implements cbit.vcell.export.gui.MovieSettingsPanelListener {
/**
 * Constructor to support multicast events.
 * @param a cbit.vcell.export.MovieSettingsPanelListener
 * @param b cbit.vcell.export.MovieSettingsPanelListener
 */
protected MovieSettingsPanelListenerEventMulticaster(cbit.vcell.export.gui.MovieSettingsPanelListener a, cbit.vcell.export.gui.MovieSettingsPanelListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return cbit.vcell.export.MovieSettingsPanelListener
 * @param a cbit.vcell.export.MovieSettingsPanelListener
 * @param b cbit.vcell.export.MovieSettingsPanelListener
 */
public static cbit.vcell.export.gui.MovieSettingsPanelListener add(cbit.vcell.export.gui.MovieSettingsPanelListener a, cbit.vcell.export.gui.MovieSettingsPanelListener b) {
	if (a == null)  return b;
	if (b == null)  return a;
	return new MovieSettingsPanelListenerEventMulticaster(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
	((cbit.vcell.export.gui.MovieSettingsPanelListener)a).JButtonCancelAction_actionPerformed(newEvent);
	((cbit.vcell.export.gui.MovieSettingsPanelListener)b).JButtonCancelAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonOKAction_actionPerformed(java.util.EventObject newEvent) {
	((cbit.vcell.export.gui.MovieSettingsPanelListener)a).JButtonOKAction_actionPerformed(newEvent);
	((cbit.vcell.export.gui.MovieSettingsPanelListener)b).JButtonOKAction_actionPerformed(newEvent);
}
/**
 * Remove listener to support multicast events.
 * @return cbit.vcell.export.MovieSettingsPanelListener
 * @param a cbit.vcell.export.MovieSettingsPanelListener
 * @param b cbit.vcell.export.MovieSettingsPanelListener
 */
public static cbit.vcell.export.gui.MovieSettingsPanelListener remove(cbit.vcell.export.gui.MovieSettingsPanelListener a, cbit.vcell.export.gui.MovieSettingsPanelListener b) {
	return (cbit.vcell.export.gui.MovieSettingsPanelListener)removeInternal(a, b);
}
}
