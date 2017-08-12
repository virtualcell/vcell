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

public interface ASCIISettingsPanelListener extends java.util.EventListener {
/**
 * 
 * @param newEvent java.util.EventObject
 */
void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent);
/**
 * 
 * @param newEvent java.util.EventObject
 */
void JButtonOKAction_actionPerformed(java.util.EventObject newEvent);
}
