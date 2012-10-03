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
import org.vcell.util.document.KeyValue;

import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.messaging.db.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.messaging.db.UpdateSynchronizationException;
import cbit.vcell.solver.SimulationMessage;

/**
 * Insert the type's description here.
 * Creation date: (2/20/2004 3:38:59 PM)
 * @author: Fei Gao
 */
public interface DispatcherDbManager {
	SimulationJobStatus[] getSimulationJobStatusArray(SimulationDatabase simDb, KeyValue simKey, int jobIndex) throws DataAccessException;


	SimulationJobStatus updateDispatchedStatus(SimulationJobStatus oldJobStatus, SimulationDatabase simDb, String computeHost, 
			VCSimulationIdentifier vcSimID, int jobIndex, int taskID, SimulationMessage startMsg) throws DataAccessException, UpdateSynchronizationException;


	SimulationJobStatus updateEndStatus(SimulationJobStatus oldJobStatus, SimulationDatabase simDb, VCSimulationIdentifier vcSimID, 
		int jobIndex, int taskID, String hostName, SchedulerStatus status, SimulationMessage solverMsg) throws DataAccessException, UpdateSynchronizationException;


	void updateLatestUpdateDate(SimulationJobStatus oldJobStatus, SimulationDatabase simDb, VCSimulationIdentifier vcSimID, 
			int jobIndex, int taskID, SimulationMessage simulationMessage) throws DataAccessException, UpdateSynchronizationException;


	SimulationJobStatus updateRunningStatus(SimulationJobStatus oldJobStatus, SimulationDatabase simDb, String hostName, 
			VCSimulationIdentifier vcSimID, int jobIndex, int taskID, boolean hasData, SimulationMessage solverMsg)	throws DataAccessException, UpdateSynchronizationException;
}
