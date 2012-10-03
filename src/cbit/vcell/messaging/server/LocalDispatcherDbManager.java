/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.messaging.server;
import org.vcell.util.DataAccessException;
import org.vcell.util.MessageConstants;
import org.vcell.util.MessageConstants.SimulationQueueID;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.server.AdminDatabaseServer;
import cbit.vcell.messaging.db.UpdateSynchronizationException;
import cbit.vcell.solver.SimulationMessage;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.messaging.db.SimulationQueueEntryStatus;
import cbit.vcell.messaging.db.SimulationJobStatus.SchedulerStatus;

/**
 * Insert the type's description here.
 * Creation date: (2/20/2004 3:45:37 PM)
 * @author: Fei Gao
 */
public class LocalDispatcherDbManager extends AbstractDispatcherDbManager {
/**
 * NonJmsDispatcherDbDriver constructor comment.
 */
public LocalDispatcherDbManager() {
	super();
}

}
