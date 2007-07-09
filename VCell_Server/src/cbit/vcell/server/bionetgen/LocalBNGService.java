package cbit.vcell.server.bionetgen;

import org.vcell.util.DataAccessException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.document.User;

/**
 * Insert the type's description here.
 * Creation date: (7/11/2006 12:45:28 PM)
 * @author: Jim Schaff
 */
public class LocalBNGService extends java.rmi.server.UnicastRemoteObject implements BNGService {
	private User user = null;
	private BNGServerImpl bngServerImpl = null;
/**
 * LocalBNGSerivce constructor comment.
 * @exception java.rmi.RemoteException The exception description.
 */
public LocalBNGService(User arg_user, SessionLog arg_log) throws java.rmi.RemoteException {
	super(PropertyLoader.getIntProperty(PropertyLoader.rmiPortBNGService,0));
	user = arg_user;
	bngServerImpl = new BNGServerImpl(arg_log);
}
/**
 * Insert the method's description here.
 * Creation date: (7/11/2006 12:45:28 PM)
 */
public BNGOutput executeBNG(BNGInput bngRulesInput) throws DataAccessException, java.rmi.RemoteException {
	return bngServerImpl.executeBNG(user, bngRulesInput);
}
}
