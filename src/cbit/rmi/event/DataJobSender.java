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

import cbit.vcell.desktop.controls.*;
public interface DataJobSender {
/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 5:17:28 PM)
 * @param listener cbit.vcell.desktop.controls.ExportListener
 */
void addDataJobListener(DataJobListener listener);


/**
 * Insert the method's description here.
 * Creation date: (3/29/2001 5:17:39 PM)
 * @param listener cbit.vcell.desktop.controls.ExportListener
 */
void removeDataJobListener(DataJobListener listener);
}
