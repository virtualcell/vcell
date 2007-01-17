package cbit.vcell.export;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This is the event multicaster class to support the cbit.vcell.export.MovieSettingsPanelListenerEventMulticaster interface.
 */
public class MovieSettingsPanelListenerEventMulticaster extends java.awt.AWTEventMulticaster implements cbit.vcell.export.MovieSettingsPanelListener {
/**
 * Constructor to support multicast events.
 * @param a cbit.vcell.export.MovieSettingsPanelListener
 * @param b cbit.vcell.export.MovieSettingsPanelListener
 */
protected MovieSettingsPanelListenerEventMulticaster(cbit.vcell.export.MovieSettingsPanelListener a, cbit.vcell.export.MovieSettingsPanelListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return cbit.vcell.export.MovieSettingsPanelListener
 * @param a cbit.vcell.export.MovieSettingsPanelListener
 * @param b cbit.vcell.export.MovieSettingsPanelListener
 */
public static cbit.vcell.export.MovieSettingsPanelListener add(cbit.vcell.export.MovieSettingsPanelListener a, cbit.vcell.export.MovieSettingsPanelListener b) {
	if (a == null)  return b;
	if (b == null)  return a;
	return new MovieSettingsPanelListenerEventMulticaster(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
	((cbit.vcell.export.MovieSettingsPanelListener)a).JButtonCancelAction_actionPerformed(newEvent);
	((cbit.vcell.export.MovieSettingsPanelListener)b).JButtonCancelAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonOKAction_actionPerformed(java.util.EventObject newEvent) {
	((cbit.vcell.export.MovieSettingsPanelListener)a).JButtonOKAction_actionPerformed(newEvent);
	((cbit.vcell.export.MovieSettingsPanelListener)b).JButtonOKAction_actionPerformed(newEvent);
}
/**
 * Remove listener to support multicast events.
 * @return cbit.vcell.export.MovieSettingsPanelListener
 * @param a cbit.vcell.export.MovieSettingsPanelListener
 * @param b cbit.vcell.export.MovieSettingsPanelListener
 */
public static cbit.vcell.export.MovieSettingsPanelListener remove(cbit.vcell.export.MovieSettingsPanelListener a, cbit.vcell.export.MovieSettingsPanelListener b) {
	return (cbit.vcell.export.MovieSettingsPanelListener)removeInternal(a, b);
}
}
