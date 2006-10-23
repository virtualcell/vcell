package cbit.vcell.export;
import cbit.rmi.event.ExportEvent;
import cbit.util.document.User;
import cbit.vcell.simdata.DataServerImpl;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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
 * @exception cbit.util.DataAccessException The exception description.
 */
ExportEvent makeRemoteFile(User user, DataServerImpl dataServerImpl, ExportSpecs exportSpecs) throws cbit.util.DataAccessException;
}