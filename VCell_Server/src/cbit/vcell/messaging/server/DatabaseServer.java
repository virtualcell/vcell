package cbit.vcell.messaging.server;

import cbit.util.DataAccessException;
import cbit.vcell.messaging.JmsUtils;
import cbit.vcell.messaging.MessageConstants;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:31:11 PM)
 * @author: Jim Schaff
 */
public class DatabaseServer extends JmsRpcServer {
	private RpcDbServerImpl rpcDbServerImpl = null;
	private static String filter =  MessageConstants.MESSAGE_TYPE_PROPERTY + "='" + MessageConstants.MESSAGE_TYPE_RPC_SERVICE_VALUE  + "' AND " 
		+ MessageConstants.SERVICETYPE_PROPERTY + "='" + MessageConstants.SERVICETYPE_DB_VALUE + "'";

/**
 * Scheduler constructor comment.
 */
public DatabaseServer(String serviceName) throws Exception {	
	super(MessageConstants.SERVICETYPE_DB_VALUE, serviceName, JmsUtils.getQueueDbReq(), filter);	
}


/**
 * Insert the method's description here.
 * Creation date: (10/23/2003 8:17:51 AM)
 * @return cbit.vcell.messaging.RpcServerImpl
 * @param user cbit.vcell.server.User
 */
public RpcServerImpl getRpcServerImpl() throws DataAccessException {
	try {
		if (rpcDbServerImpl == null) {
			rpcDbServerImpl = new RpcDbServerImpl(log);
		}

		return rpcDbServerImpl;
	} catch (Exception ex) {
		log.exception(ex);
		throw new DataAccessException(ex.getMessage());
	}
}


/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	if (args.length < 1) {
		System.out.println("Missing arguments: " + DatabaseServer.class.getName() + " serviceName [logfile]");
		System.exit(1);
	}
	
	System.out.println("Usage: " + DatabaseServer.class.getName() + " serviceName [logfile]");
	
	try {
		String logfile = null;
		if (args.length >= 2) {
			logfile = args[1];
		}
		mainInit(logfile);
		String serviceName = args[0];
        DatabaseServer databaseServer = new DatabaseServer(serviceName);       
        databaseServer.start();        
    } catch (Throwable e) {
	    e.printStackTrace(System.out); 
    }
}
}