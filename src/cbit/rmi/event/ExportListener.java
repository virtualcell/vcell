/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.rmi.event;

/**
 * The event set listener interface for the export feature.
 */
public interface ExportListener extends java.util.EventListener {
/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 6:27:03 PM)
 * @param event cbit.rmi.event.ExportEvent
 */
void exportMessage(ExportEvent event);
}
