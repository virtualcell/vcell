/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.gui;

/**
 * This is the event multicaster class to support the cbit.vcell.export.ASCIISettingsPanelListenerEventMulticaster interface.
 */
public class ASCIISettingsPanelListenerEventMulticaster extends java.awt.AWTEventMulticaster implements cbit.vcell.export.gui.ASCIISettingsPanelListener {
/**
 * Constructor to support multicast events.
 * @param a cbit.vcell.export.ASCIISettingsPanelListener
 * @param b cbit.vcell.export.ASCIISettingsPanelListener
 */
protected ASCIISettingsPanelListenerEventMulticaster(cbit.vcell.export.gui.ASCIISettingsPanelListener a, cbit.vcell.export.gui.ASCIISettingsPanelListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return cbit.vcell.export.ASCIISettingsPanelListener
 * @param a cbit.vcell.export.ASCIISettingsPanelListener
 * @param b cbit.vcell.export.ASCIISettingsPanelListener
 */
public static cbit.vcell.export.gui.ASCIISettingsPanelListener add(cbit.vcell.export.gui.ASCIISettingsPanelListener a, cbit.vcell.export.gui.ASCIISettingsPanelListener b) {
	if (a == null)  return b;
	if (b == null)  return a;
	return new ASCIISettingsPanelListenerEventMulticaster(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
	((cbit.vcell.export.gui.ASCIISettingsPanelListener)a).JButtonCancelAction_actionPerformed(newEvent);
	((cbit.vcell.export.gui.ASCIISettingsPanelListener)b).JButtonCancelAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonOKAction_actionPerformed(java.util.EventObject newEvent) {
	((cbit.vcell.export.gui.ASCIISettingsPanelListener)a).JButtonOKAction_actionPerformed(newEvent);
	((cbit.vcell.export.gui.ASCIISettingsPanelListener)b).JButtonOKAction_actionPerformed(newEvent);
}
/**
 * Remove listener to support multicast events.
 * @return cbit.vcell.export.ASCIISettingsPanelListener
 * @param a cbit.vcell.export.ASCIISettingsPanelListener
 * @param b cbit.vcell.export.ASCIISettingsPanelListener
 */
public static cbit.vcell.export.gui.ASCIISettingsPanelListener remove(cbit.vcell.export.gui.ASCIISettingsPanelListener a, cbit.vcell.export.gui.ASCIISettingsPanelListener b) {
	return (cbit.vcell.export.gui.ASCIISettingsPanelListener)removeInternal(a, b);
}
}
