/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export;

/**
 * This is the event multicaster class to support the cbit.vcell.export.ImageSettingsPanelListenerEventMulticaster interface.
 */
public class ImageSettingsPanelListenerEventMulticaster extends java.awt.AWTEventMulticaster implements cbit.vcell.export.ImageSettingsPanelListener {
/**
 * Constructor to support multicast events.
 * @param a cbit.vcell.export.ImageSettingsPanelListener
 * @param b cbit.vcell.export.ImageSettingsPanelListener
 */
protected ImageSettingsPanelListenerEventMulticaster(cbit.vcell.export.ImageSettingsPanelListener a, cbit.vcell.export.ImageSettingsPanelListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return cbit.vcell.export.ImageSettingsPanelListener
 * @param a cbit.vcell.export.ImageSettingsPanelListener
 * @param b cbit.vcell.export.ImageSettingsPanelListener
 */
public static cbit.vcell.export.ImageSettingsPanelListener add(cbit.vcell.export.ImageSettingsPanelListener a, cbit.vcell.export.ImageSettingsPanelListener b) {
	if (a == null)  return b;
	if (b == null)  return a;
	return new ImageSettingsPanelListenerEventMulticaster(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
	((cbit.vcell.export.ImageSettingsPanelListener)a).JButtonCancelAction_actionPerformed(newEvent);
	((cbit.vcell.export.ImageSettingsPanelListener)b).JButtonCancelAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonOKAction_actionPerformed(java.util.EventObject newEvent) {
	((cbit.vcell.export.ImageSettingsPanelListener)a).JButtonOKAction_actionPerformed(newEvent);
	((cbit.vcell.export.ImageSettingsPanelListener)b).JButtonOKAction_actionPerformed(newEvent);
}
/**
 * Remove listener to support multicast events.
 * @return cbit.vcell.export.ImageSettingsPanelListener
 * @param a cbit.vcell.export.ImageSettingsPanelListener
 * @param b cbit.vcell.export.ImageSettingsPanelListener
 */
public static cbit.vcell.export.ImageSettingsPanelListener remove(cbit.vcell.export.ImageSettingsPanelListener a, cbit.vcell.export.ImageSettingsPanelListener b) {
	return (cbit.vcell.export.ImageSettingsPanelListener)removeInternal(a, b);
}
}
