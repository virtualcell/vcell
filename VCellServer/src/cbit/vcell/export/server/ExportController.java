package cbit.vcell.export.server;
import java.rmi.*;

import cbit.vcell.messaging.db.*;
import cbit.vcell.export.server.*;
/**
 * Insert the type's description here.
 * Creation date: (6/2/2004 12:56:36 AM)
 * @author: Ion Moraru
 */
public interface ExportController extends Remote {
/**
 * Insert the method's description here.
 * Creation date: (6/2/2004 12:58:18 AM)
 * @param exportSpecs cbit.vcell.export.server.ExportSpecs
 */
ExportJobStatus getExportJobStatus(ExportSpecs exportSpecs) throws RemoteException;


/**
 * Insert the method's description here.
 * Creation date: (6/2/2004 12:58:18 AM)
 * @param exportSpecs cbit.vcell.export.server.ExportSpecs
 */
void startExport(ExportSpecs exportSpecs) throws RemoteException;
}