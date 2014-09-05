/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client;

import cbit.vcell.client.server.SimStatusEvent;

/**
 * Insert the type's description here.
 * Creation date: (2/10/2004 1:27:09 PM)
 * @author: Fei Gao
 */
public interface SimStatusListener extends java.util.EventListener {
/**
 * Insert the method's description here.
 * Creation date: (2/10/2004 1:28:55 PM)
 * @param newJobStatus cbit.vcell.messaging.db.SimulationJobStatus
 * @param progress java.lang.Double
 * @param timePoint java.lang.Double
 */
void simStatusChanged(SimStatusEvent simStatusEvent);
}
