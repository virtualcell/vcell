package cbit.vcell.messaging.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;

import cbit.util.DataAccessException;
import cbit.util.SessionLog;
import cbit.util.StdoutSessionLog;
import cbit.vcell.messaging.RpcServerMessaging;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:31:11 PM)
 * @author: Jim Schaff
 */
public abstract class JmsRpcServer extends AbstractJmsServiceProvider implements RpcServer {	
	protected RpcServerMessaging rpcServerMessaging = null;	
	protected SessionLog log = null;
	protected Map userServerMap = Collections.synchronizedMap(new HashMap());

/**
 * Scheduler constructor comment.
 */
public JmsRpcServer(String serviceType, String serviceName, String queueName, String filter) throws Exception {
	log = new StdoutSessionLog(serviceName);		

	String hostName = cbit.vcell.messaging.ManageUtils.getLocalHostName();
	serviceInfo = new cbit.vcell.messaging.VCServiceInfo(hostName, serviceType, serviceName);
	serviceInfo.setAlive(true);
	serviceInfo.setBootTime(new java.util.Date());			
	
	rpcServerMessaging = new RpcServerMessaging(this, queueName, filter, log);	
}


/**
 * Insert the method's description here.
 * Creation date: (1/3/2002 5:41:53 PM)
 * @return java.lang.Object
 * @param user cbit.vcell.server.User
 * @param methodName java.lang.String
 * @param args java.lang.Object[]
 * @exception java.lang.Exception The exception description.
 */
public final Object dispatchRPC(RpcRequest request) throws Exception {
	return getRpcServerImpl().rpc(request);
}

/**
 * Insert the method's description here.
 * Creation date: (1/3/2002 8:18:25 PM)
 * @param logMessage java.lang.String
 */
protected SessionLog getLog() {
	return this.log;
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2003 2:01:52 PM)
 * @return cbit.vcell.messaging.RpcServerImpl
 */
public abstract RpcServerImpl getRpcServerImpl() throws DataAccessException;


/**
 * Insert the method's description here.
 * Creation date: (5/9/2003 12:07:28 PM)
 */
public final void start() throws JMSException {
	log.print(getClass().getName() + " starting");
	rpcServerMessaging.startListening();
	
	log.print("Start PropertyLoader thread...");
	new PropertyLoaderThread().start();	
}


/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 4:28:05 PM)
 */
public final void stop() {
	log.print(this.getClass().getName() + " ending");
}
}