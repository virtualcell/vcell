package cbit.vcell.messaging.server;
import cbit.util.DataAccessException;
import cbit.util.document.User;
import cbit.vcell.messaging.JmsClientMessaging;
import cbit.vcell.messaging.JmsUtils;
import cbit.vcell.simulation.VCSimulationIdentifier;

/**
 * Insert the type's description here.
 * Creation date: (12/5/2001 12:00:10 PM)
 * @author: Jim Schaff
 *
 * stateless database service for any user (should be thread safe ... reentrant)
 *
 */
public class RpcSimServerProxy extends AbstractRpcServerProxy implements cbit.vcell.server.SimulationController {
/**
 * DataServerProxy constructor comment.
 */
public RpcSimServerProxy(User argUser, JmsClientMessaging clientMessaging, cbit.util.SessionLog log) throws javax.jms.JMSException {
	super(argUser, clientMessaging, JmsUtils.getQueueSimReq(), log);
}


/**
 * Insert the method's description here.
 * Creation date: (12/5/2001 9:39:03 PM)
 * @return java.lang.Object
 * @param methodName java.lang.String
 * @param args java.lang.Object[]
 * @exception java.lang.Exception The exception description.
 */
private Object rpc(String methodName, Object[] args) throws DataAccessException {
	try {
		return rpc(cbit.util.MessageConstants.SERVICETYPE_DISPATCH_VALUE, methodName, args, true);
	} catch (DataAccessException ex) {
		log.exception(ex);
		throw ex;
	} catch (Exception e){
		log.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (12/5/2001 9:39:03 PM)
 * @return java.lang.Object
 * @param methodName java.lang.String
 * @param args java.lang.Object[]
 * @exception java.lang.Exception The exception description.
 */
private void rpcNoWait(String methodName, Object[] args) throws DataAccessException {
	try {
		rpc(cbit.util.MessageConstants.SERVICETYPE_DISPATCH_VALUE, methodName, args, false);
	} catch (DataAccessException ex) {
		log.exception(ex);
		throw ex;
	} catch (Exception e){
		log.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public void startSimulation(VCSimulationIdentifier vcSimID) {
	try {
		rpcNoWait("startSimulation",new Object[]{vcSimID});
	}catch (DataAccessException e){
		log.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public void stopSimulation(VCSimulationIdentifier vcSimID) {
	try {
		rpcNoWait("stopSimulation",new Object[]{vcSimID});
	} catch (DataAccessException e) {
		log.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}
}