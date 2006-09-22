package cbit.vcell.client.server;

import cbit.util.DataAccessException;
import cbit.vcell.export.ExportSpecs;
import cbit.vcell.messaging.db.ExportJobStatus;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.simulation.VCSimulationIdentifier;

public interface JobManager extends cbit.rmi.event.SimulationJobStatusListener, cbit.vcell.client.server.SimStatusSender {

/**
 * Insert the method's description here.
 * Creation date: (6/4/2004 3:22:42 PM)
 * @param exportSpecs cbit.vcell.export.server.ExportSpecs
 */
ExportJobStatus getExportJobStatus(ExportSpecs exportSpecs) throws DataAccessException;


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
SimulationStatus getServerSimulationStatus(VCSimulationIdentifier vcSimulationIdentifier) throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (6/2/2004 1:42:28 AM)
 * @param exportSpecs cbit.vcell.export.server.ExportSpecs
 */
void startExport(ExportSpecs exportSpecs) throws DataAccessException;


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
void startSimulation(VCSimulationIdentifier vcSimulationIdentifier) throws DataAccessException;


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
void stopSimulation(VCSimulationIdentifier vcSimulationIdentifier) throws DataAccessException;
}