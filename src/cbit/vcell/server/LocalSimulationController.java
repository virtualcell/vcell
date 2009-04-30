package cbit.vcell.server;
import cbit.util.BigString;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.rmi.*;

import org.vcell.util.document.User;

import cbit.vcell.solver.*;
import cbit.vcell.messaging.db.SimulationJobStatus;
/**
 * Insert the type's description here.
 * Creation date: (6/28/01 12:55:29 PM)
 * @author: Jim Schaff
 */
public class LocalSimulationController extends java.rmi.server.UnicastRemoteObject implements SimulationController {
	private SessionLog sessionLog = null;
	private User user = null;
	private SimulationControllerImpl simulationControllerImpl = null;
	private UserMetaDbServer userMetaDbServer = null;
	private AdminDatabaseServer adminDbServer = null;

/**
 * LocalSimulationController constructor comment.
 * @exception java.rmi.RemoteException The exception description.
 */
protected LocalSimulationController(User argUser, SessionLog argSessionLog, AdminDatabaseServer adminDbServer0, SimulationControllerImpl argSimulationControllerImpl, UserMetaDbServer argUserMetaDbServer) throws java.rmi.RemoteException {
	super(PropertyLoader.getIntProperty(PropertyLoader.rmiPortSimulationController,0));
	sessionLog = argSessionLog;
	user = argUser;
	adminDbServer = adminDbServer0;
	simulationControllerImpl = argSimulationControllerImpl;
	userMetaDbServer = argUserMetaDbServer;
}


/**
 * Insert the method's description here.
 * Creation date: (6/28/01 1:30:46 PM)
 * @return cbit.vcell.solver.Simulation
 * @param simulationInfo cbit.vcell.solver.SimulationInfo
 */
private Simulation getSimulation(VCSimulationIdentifier vcSimulationIdentifier) throws DataAccessException, ObjectNotFoundException, RemoteException {
	String simulationXML = userMetaDbServer.getSimulationXML(vcSimulationIdentifier.getSimulationKey()).toString();
	Simulation simulation = null;
	try {
		simulation = cbit.vcell.xml.XmlHelper.XMLToSim(simulationXML);
	}catch (cbit.vcell.xml.XmlParseException e){
		e.printStackTrace(System.out);
		throw new DataAccessException(e.getMessage());
	}
	return simulation;
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public void startSimulation(VCSimulationIdentifier vcSimulationIdentifier) throws java.rmi.RemoteException {
	sessionLog.print("LocalSimulationController.startSimulation(simInfo="+vcSimulationIdentifier+")");
	try {
		Simulation simulation = getSimulation(vcSimulationIdentifier);
		simulationControllerImpl.startSimulation(user,simulation,sessionLog);
	}catch (RemoteException e){
		sessionLog.exception(e);
		throw e;
	}catch (DataAccessException e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * This method was created by a SmartGuide.
 * @exception java.rmi.RemoteException The exception description.
 */
public void stopSimulation(VCSimulationIdentifier vcSimulationIdentifier) throws java.rmi.RemoteException {
	sessionLog.print("LocalSimulationController.getSolverStatus(simInfo="+vcSimulationIdentifier+")");
	try {
		Simulation simulation = getSimulation(vcSimulationIdentifier);
		simulationControllerImpl.stopSimulation(user,simulation);
	}catch (RemoteException e){
		sessionLog.exception(e);
		throw e;
	}catch (DataAccessException e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}catch (Throwable e){
		sessionLog.exception(e);
		throw new RuntimeException(e.getMessage());
	}
}
}