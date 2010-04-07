package cbit.vcell.export.server;
import cbit.vcell.client.data.OutputContext;
import cbit.vcell.simdata.DataServerImpl;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.solver.*;
import java.rmi.*;

import org.vcell.util.document.User;

import cbit.vcell.server.*;
import cbit.rmi.event.*;
/**
 * Insert the type's description here.
 * Creation date: (3/29/2001 2:37:24 PM)
 * @author: Ion Moraru
 */
public interface ExportService {
/**
 * Insert the method's description here.
 * Creation date: (10/19/2001 3:07:25 PM)
 * @return cbit.rmi.event.ExportEvent
 * @param user cbit.vcell.server.User
 * @param dsc cbit.vcell.server.DataSetController
 * @param exportSpecs cbit.vcell.export.server.ExportSpecs
 * @exception org.vcell.util.DataAccessException The exception description.
 */
ExportEvent makeRemoteFile(OutputContext outputContext,User user, DataServerImpl dataServerImpl, ExportSpecs exportSpecs) throws org.vcell.util.DataAccessException;
}