/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server;
import java.rmi.Remote;
import java.rmi.RemoteException;

import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.simdata.OutputContext;
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
void startExport(OutputContext outputContext,ExportSpecs exportSpecs) throws RemoteException;
}
