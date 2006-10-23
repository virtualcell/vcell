package cbit.vcell.messaging.server;
import cbit.util.DataAccessException;
import cbit.util.PropertyLoader;
import cbit.util.SessionLog;
import cbit.util.document.User;
import cbit.vcell.simulation.VCSimulationIdentifier;

/**
 * Insert the type's description here.
 * Creation date: (2/4/2003 11:08:14 PM)
 * @author: Jim Schaff
 */
public class LocalSimulationControllerMessaging extends java.rmi.server.UnicastRemoteObject implements cbit.vcell.server.SimulationController {
	private User fieldUser = null;
	private SessionLog fieldSessionLog = null;
	private RpcSimServerProxy simServerProxy = null;

/**
 * MessagingSimulationController constructor comment.
 * @exception java.rmi.RemoteException The exception description.
 */
public LocalSimulationControllerMessaging(User user, cbit.vcell.messaging.JmsClientMessaging clientMessaging, SessionLog log) throws java.rmi.RemoteException, DataAccessException {
	super(PropertyLoader.getIntProperty(PropertyLoader.rmiPortSimulationController,0));
	this.fieldUser = user;
	this.fieldSessionLog = log;

	try {
		simServerProxy = new RpcSimServerProxy(user, clientMessaging, fieldSessionLog);
	} catch (javax.jms.JMSException e){
		e.printStackTrace(System.out);
		throw new RuntimeException("JMS exception creating SimServerProxy: "+e.getMessage());
	}
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public void startSimulation(VCSimulationIdentifier vcSimID) {
	fieldSessionLog.print("LocalSimulationControllerMessaging.startSimulation(" + vcSimID + ")");
	simServerProxy.startSimulation(vcSimID);
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public void stopSimulation(VCSimulationIdentifier vcSimID) {
	fieldSessionLog.print("LocalSimulationControllerMessaging.stopSimulation(" + vcSimID + ")");
	simServerProxy.stopSimulation(vcSimID);
}
}