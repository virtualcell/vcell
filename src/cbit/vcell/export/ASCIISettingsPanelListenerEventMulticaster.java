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
 * This is the event multicaster class to support the cbit.vcell.export.ASCIISettingsPanelListenerEventMulticaster interface.
 */
public class ASCIISettingsPanelListenerEventMulticaster extends java.awt.AWTEventMulticaster implements cbit.vcell.export.ASCIISettingsPanelListener {
/**
 * Constructor to support multicast events.
 * @param a cbit.vcell.export.ASCIISettingsPanelListener
 * @param b cbit.vcell.export.ASCIISettingsPanelListener
 */
protected ASCIISettingsPanelListenerEventMulticaster(cbit.vcell.export.ASCIISettingsPanelListener a, cbit.vcell.export.ASCIISettingsPanelListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return cbit.vcell.export.ASCIISettingsPanelListener
 * @param a cbit.vcell.export.ASCIISettingsPanelListener
 * @param b cbit.vcell.export.ASCIISettingsPanelListener
 */
public static cbit.vcell.export.ASCIISettingsPanelListener add(cbit.vcell.export.ASCIISettingsPanelListener a, cbit.vcell.export.ASCIISettingsPanelListener b) {
	if (a == null)  return b;
	if (b == null)  return a;
	return new ASCIISettingsPanelListenerEventMulticaster(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
	((cbit.vcell.export.ASCIISettingsPanelListener)a).JButtonCancelAction_actionPerformed(newEvent);
	((cbit.vcell.export.ASCIISettingsPanelListener)b).JButtonCancelAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonOKAction_actionPerformed(java.util.EventObject newEvent) {
	((cbit.vcell.export.ASCIISettingsPanelListener)a).JButtonOKAction_actionPerformed(newEvent);
	((cbit.vcell.export.ASCIISettingsPanelListener)b).JButtonOKAction_actionPerformed(newEvent);
}
/**
 * Remove listener to support multicast events.
 * @return cbit.vcell.export.ASCIISettingsPanelListener
 * @param a cbit.vcell.export.ASCIISettingsPanelListener
 * @param b cbit.vcell.export.ASCIISettingsPanelListener
 */
public static cbit.vcell.export.ASCIISettingsPanelListener remove(cbit.vcell.export.ASCIISettingsPanelListener a, cbit.vcell.export.ASCIISettingsPanelListener b) {
	return (cbit.vcell.export.ASCIISettingsPanelListener)removeInternal(a, b);
}
}
