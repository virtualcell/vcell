/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.server;
import org.vcell.util.DataAccessException;

import cbit.rmi.event.ExportEvent;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory.RemoteProxyException;
import cbit.vcell.server.ExportController;
import cbit.vcell.simdata.OutputContext;
/**
 * Insert the type's description here.
 * Creation date: (6/15/2004 2:15:24 AM)
 * @author: Ion Moraru
 */
public class ClientExportController implements ExportController {
	private ClientServerManager clientServerManager = null;

/**
 * Insert the method's description here.
 * Creation date: (6/15/2004 2:18:14 AM)
 * @param csm cbit.vcell.client.server.ClientServerManager
 */
public ClientExportController(ClientServerManager csm) {
	clientServerManager = csm;
}


/**
 * Insert the method's description here.
 * Creation date: (6/15/2004 2:23:57 AM)
 * @return cbit.vcell.client.server.ClientServerManager
 */
private ClientServerManager getClientServerManager() {
	return clientServerManager;
}


/**
 * Insert the method's description here.
 * Creation date: (6/15/2004 2:15:24 AM)
 * @param exportSpecs cbit.vcell.export.server.ExportSpecs
 */
public cbit.vcell.server.ExportJobStatus getExportJobStatus(ExportSpecs exportSpecs) throws RemoteProxyException {
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (6/15/2004 2:23:57 AM)
 * @param newClientServerManager cbit.vcell.client.server.ClientServerManager
 */
public void setClientServerManager(ClientServerManager newClientServerManager) {
	clientServerManager = newClientServerManager;
}


/**
 * Insert the method's description here.
 * Creation date: (6/15/2004 2:15:24 AM)
 * @param exportSpecs cbit.vcell.export.server.ExportSpecs
 */
public void startExport(OutputContext outputContext,ExportSpecs exportSpecs) throws RemoteProxyException {
	try {
		ExportEvent event = getClientServerManager().getDataSetController().makeRemoteFile(outputContext,exportSpecs);
		// ignore; we'll get two downloads otherwise... getClientServerManager().getAsynchMessageManager().fireExportEvent(event);
	} catch (DataAccessException exc) {
		exc.printStackTrace();
		throw new RemoteProxyException(exc.getMessage(), exc);
	}
}
}
