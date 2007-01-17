package cbit.vcell.messaging.server;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.gui.*;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.server.PermissionException;
import cbit.vcell.server.ObjectNotFoundException;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.server.User;
import cbit.vcell.solvers.SimExecutionException;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.messaging.JmsClientMessaging;
import cbit.vcell.messaging.MessageConstants;
import cbit.vcell.messaging.JmsUtils;

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
public RpcSimServerProxy(User argUser, JmsClientMessaging clientMessaging, cbit.vcell.server.SessionLog log) throws javax.jms.JMSException {
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
		return rpc(cbit.vcell.messaging.MessageConstants.SERVICETYPE_DISPATCH_VALUE, methodName, args, true);
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
		rpc(cbit.vcell.messaging.MessageConstants.SERVICETYPE_DISPATCH_VALUE, methodName, args, false);
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