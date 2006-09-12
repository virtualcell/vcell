package cbit.vcell.messaging.server;
import cbit.gui.PropertyLoader;
import cbit.util.DataAccessException;
import cbit.util.SessionLog;
import cbit.util.User;
import cbit.vcell.messaging.JmsClientMessaging;
import java.rmi.RemoteException;

/**
 * Insert the type's description here.
 * Creation date: (7/11/2006 12:35:50 PM)
 * @author: Jim Schaff
 */
public class LocalBNGServiceMessaging extends java.rmi.server.UnicastRemoteObject implements cbit.vcell.server.bionetgen.BNGService {
   	private RpcBNGServerProxy rpcBNGServerProxy = null;
    private User user = null;
    private SessionLog sessionLog = null;	

/**
 * LocalBNGServiceMessaging constructor comment.
 * @exception java.rmi.RemoteException The exception description.
 */
protected LocalBNGServiceMessaging(SessionLog sLog, User argUser, JmsClientMessaging clientMessaging) throws RemoteException {
	super(PropertyLoader.getIntProperty(PropertyLoader.rmiPortDataSetController,0));
	this.sessionLog = sLog;
	this.user = argUser;
	try {
		rpcBNGServerProxy = new RpcBNGServerProxy(user, clientMessaging,sessionLog);
	} catch (javax.jms.JMSException e){
		e.printStackTrace(System.out);
		throw new RuntimeException("JMS exception creating BNGServerProxy: "+e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2006 12:35:50 PM)
 */
public cbit.vcell.server.bionetgen.BNGOutput executeBNG(cbit.vcell.server.bionetgen.BNGInput bngRulesInput) throws DataAccessException, java.rmi.RemoteException {
	sessionLog.print("LocalBNGServiceMessaging.executeBNG()");
	try {
		return rpcBNGServerProxy.executeBNG(bngRulesInput);
	} catch (DataAccessException e){
		sessionLog.exception(e);
		throw e;
	} catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}
}