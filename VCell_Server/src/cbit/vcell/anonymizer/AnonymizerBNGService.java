package cbit.vcell.anonymizer;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;

import cbit.vcell.server.bionetgen.BNGOutput;

/**
 * Insert the type's description here.
 * Creation date: (7/11/2006 3:23:11 PM)
 * @author: Jim Schaff
 */
public class AnonymizerBNGService extends AnonymizerService implements cbit.vcell.server.bionetgen.BNGService {
/**
 * AnonymizerBNGService constructor comment.
 * @param port int
 * @param arg_anonymizerVCellConnection cbit.vcell.anonymizer.AnonymizerVCellConnection
 * @param arg_sessionLog cbit.vcell.server.SessionLog
 * @exception java.rmi.RemoteException The exception description.
 */
public AnonymizerBNGService(AnonymizerVCellConnection arg_anonymizerVCellConnection, SessionLog arg_sessionLog) throws java.rmi.RemoteException {
	super(PropertyLoader.getIntProperty(PropertyLoader.rmiPortBNGService,0), arg_anonymizerVCellConnection, arg_sessionLog);
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2006 3:24:16 PM)
 */
public cbit.vcell.server.bionetgen.BNGOutput executeBNG(cbit.vcell.server.bionetgen.BNGInput bngRulesInput) throws org.vcell.util.DataAccessException, java.rmi.RemoteException {
	return (BNGOutput)remoteCall("executeBNG", new Class[] {cbit.vcell.server.bionetgen.BNGInput.class}, new Object[] {bngRulesInput});
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 5:54:27 PM)
 * @param function cbit.vcell.math.Function
 * @exception org.vcell.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
private Object remoteCall(String methodName, Class[] argClasses, Object[] args) throws java.rmi.RemoteException, org.vcell.util.DataAccessException, org.vcell.util.ObjectNotFoundException {
	return remoteCall(anonymizerVCellConnection.getRemoteBNGService(), methodName, argClasses, args);
}
}