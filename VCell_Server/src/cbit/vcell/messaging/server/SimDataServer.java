package cbit.vcell.messaging.server;
import cbit.util.DataAccessException;
import cbit.util.User;
import cbit.vcell.simdata.Cachetable;
import javax.jms.*;
import java.io.FileNotFoundException;
import cbit.vcell.messaging.MessageConstants;
import cbit.vcell.messaging.JmsUtils;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:31:11 PM)
 * @author: Jim Schaff
 */
public class SimDataServer extends JmsRpcServer {
	private RpcDataServerImpl rpcDataServerImpl = null;
	private static String filter =  MessageConstants.MESSAGE_TYPE_PROPERTY + "='" + MessageConstants.MESSAGE_TYPE_RPC_SERVICE_VALUE  + "' AND " 
		+ MessageConstants.SERVICETYPE_PROPERTY + "='" + MessageConstants.SERVICETYPE_DATA_VALUE + "'";	

/**
 * Scheduler constructor comment.
 */
public SimDataServer(String serviceName, boolean bExportOnly) throws Exception {
	super(MessageConstants.SERVICETYPE_DATA_VALUE, serviceName, JmsUtils.getQueueSimDataReq(), 
		filter+" AND " + MessageConstants.SERVICE_DATA_ISEXPORTING + (bExportOnly?" is NOT NULL":" is NULL"));	
}


/**
 * Insert the method's description here.
 * Creation date: (8/1/2003 9:34:57 AM)
 */
public RpcServerImpl getRpcServerImpl() throws DataAccessException {
	if (rpcDataServerImpl == null) {
		try {
			rpcDataServerImpl = new RpcDataServerImpl(rpcServerMessaging, log);			
		} catch (java.io.FileNotFoundException ex) {
			log.exception(ex);
			throw new cbit.util.DataAccessException(ex.getMessage());
		}
	}

	return rpcDataServerImpl;
}


/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	if (args.length < 1) {
		System.out.println("Missing arguments: " + SimDataServer.class.getName() + " serviceName EXPORTONLY [logfile]");
		System.exit(1);
	}
	
	try {
		String logfile = null;
		boolean bExportOnly = false;
		
		if (args.length >= 2) {
			if (args[1].equalsIgnoreCase("EXPORTONLY")) {
				bExportOnly = true;
				if (args.length >= 3) {	
					logfile = args[2];
				}
			} else {
				logfile = args[1];
			}
		}
		mainInit(logfile);

		String serviceName = args[0];
        SimDataServer simDataServer = new SimDataServer(serviceName, bExportOnly);
        simDataServer.start();
    } catch (Throwable e) {
        e.printStackTrace(System.out);
    }
}
}