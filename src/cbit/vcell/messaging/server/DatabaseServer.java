package cbit.vcell.messaging.server;
import org.vcell.util.DataAccessException;
import org.vcell.util.PropertyLoader;

import static org.vcell.util.MessageConstants.*;
import cbit.vcell.messaging.JmsUtils;
import cbit.vcell.modeldb.DatabasePolicySQL;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:31:11 PM)
 * @author: Jim Schaff
 */
public class DatabaseServer extends JmsRpcServer {
	private RpcDbServerImpl rpcDbServerImpl = null;
	private static String filter =  "(" + MESSAGE_TYPE_PROPERTY + "='" + MESSAGE_TYPE_RPC_SERVICE_VALUE  + "') AND (" 
		+ SERVICE_TYPE_PROPERTY + "='" + ServiceType.DB.getName() + "')";

/**
 * Scheduler constructor comment.
 */
public DatabaseServer(int serviceOrdinal, String logdir) throws Exception {	
	super(ServiceType.DB, serviceOrdinal, JmsUtils.getQueueDbReq(), filter, logdir);	
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
		System.out.println("Missing arguments: " + DatabaseServer.class.getName() + " serviceOrdinal [logdir]");
		System.exit(1);
	}
	
	try {
		PropertyLoader.loadProperties();
		DatabasePolicySQL.bSilent = true;
		
		int serviceOrdinal = Integer.parseInt(args[0]);
		String logdir = null;
		if (args.length > 1) {
			logdir = args[1];
		}
		
        DatabaseServer databaseServer = new DatabaseServer(serviceOrdinal, logdir);       
        databaseServer.start();        
    } catch (Throwable e) {
	    e.printStackTrace(System.out); 
    }
}
}