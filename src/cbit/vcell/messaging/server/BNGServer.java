package cbit.vcell.messaging.server;
import cbit.vcell.server.DataAccessException;
import java.rmi.RemoteException;
import cbit.vcell.messaging.MessageConstants;
import cbit.vcell.messaging.JmsUtils;

/**
 * Insert the type's description here.
 * Creation date: (6/23/2005 2:41:27 PM)
 * @author: Anuradha Lakshminarayana
 */

import java.rmi.Naming;

 
public class BNGServer extends JmsRpcServer {
	private RpcBNGServerImpl rpcBNGServerImpl = null;
	private static String filter =  MessageConstants.MESSAGE_TYPE_PROPERTY + "='" + MessageConstants.MESSAGE_TYPE_RPC_SERVICE_VALUE  + "' AND " 
		+ MessageConstants.SERVICETYPE_PROPERTY + "='" + MessageConstants.SERVICETYPE_BIONETGEN_VALUE + "'";	

/**
 * LocalBioNetGen constructor comment.
 */
public BNGServer(String serviceName) throws Exception {
	super(MessageConstants.SERVICETYPE_BIONETGEN_VALUE, serviceName, JmsUtils.getQueueBNGReq(), filter);
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2006 11:38:03 AM)
 * @return cbit.vcell.messaging.RpcServerImpl
 */
public RpcServerImpl getRpcServerImpl() throws cbit.vcell.server.DataAccessException {
	try {
		if (rpcBNGServerImpl == null) {
			rpcBNGServerImpl = new RpcBNGServerImpl(log);
		}

		return rpcBNGServerImpl;
	} catch (Exception ex) {
		log.exception(ex);
		throw new DataAccessException(ex.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/23/2005 3:42:07 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	if (args.length < 1) {
		System.out.println("Missing arguments: " + BNGServer.class.getName() + " serviceName [logfile]");
		System.exit(1);
	}
	
	System.out.println("Usage: " + BNGServer.class.getName() + " serviceName [logfile]");
	
	try {
		String logfile = null;
		if (args.length >= 2) {
			logfile = args[1];
		}
		mainInit(logfile);
		String serviceName = args[0];
        BNGServer bngServer = new BNGServer(serviceName);       
        bngServer.start();        
    } catch (Throwable e) {
	    e.printStackTrace(System.out); 
    }
}
}