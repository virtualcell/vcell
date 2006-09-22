package cbit.vcell.anonymizer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cbit.util.DataAccessException;
import cbit.util.SessionLog;

/**
 * Insert the type's description here.
 * Creation date: (6/6/2006 11:42:46 AM)
 * @author: Jim Schaff
 */
public abstract class AnonymizerService extends java.rmi.server.UnicastRemoteObject {
	protected AnonymizerVCellConnection anonymizerVCellConnection = null;
	protected SessionLog sessionLog = null;	

/**
 * AnonymizerService constructor comment.
 */
public AnonymizerService(int port, AnonymizerVCellConnection arg_anonymizerVCellConnection, SessionLog arg_sessionLog) throws java.rmi.RemoteException {
	super(port);
	this.anonymizerVCellConnection = arg_anonymizerVCellConnection;
	sessionLog = arg_sessionLog;	
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 5:54:27 PM)
 * @param function cbit.vcell.math.Function
 * @exception cbit.util.DataAccessException The exception description.
 * @exception java.rmi.RemoteException The exception description.
 */
protected Object remoteCall(Object remoteService, String methodName, Class[] argClasses, Object[] args) throws java.rmi.RemoteException, cbit.util.DataAccessException, cbit.util.ObjectNotFoundException {
	StringBuffer message = new StringBuffer();
	message.append(remoteService.getClass() + "." + methodName + "(");
	for (int i = 0; i < args.length; i ++) {
		if (i > 0) {
			message.append(",");
		}
		message.append(args[i]);
	}
	message.append(")");
	sessionLog.print(message.toString());
	Object returnValue = null;
	Method method = null;
	try {
		method = remoteService.getClass().getMethod(methodName, argClasses);
		returnValue = method.invoke(remoteService, args);
	} catch (NoSuchMethodException ex) {
		sessionLog.exception(ex);
		throw new RuntimeException(ex.getMessage());
	} catch (InvocationTargetException itex) {
		sessionLog.exception(itex);
		if (itex.getTargetException() instanceof java.rmi.RemoteException) {
			anonymizerVCellConnection.reconnect();
			sessionLog.print("retrying " + message);
			String status = "retry failed";
			try {
				returnValue = method.invoke(remoteService, args);
				status = "retry succeeded";
			} catch (InvocationTargetException itex2) {
				sessionLog.exception(itex2);	
				if (itex.getTargetException() instanceof cbit.util.ObjectNotFoundException) {
					throw (cbit.util.ObjectNotFoundException)itex.getTargetException();
				} else if (itex.getTargetException() instanceof cbit.util.DataAccessException) {
					throw (DataAccessException)itex.getTargetException();
				} else	if (itex.getTargetException() instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException)itex.getTargetException();
				}

			} catch (IllegalAccessException e){
				sessionLog.exception(e);
				throw new RuntimeException("IllegalAccessException for " + message);
			} finally {
				sessionLog.print(status + " " + message);
			} 
		} else if (itex.getTargetException() instanceof cbit.util.ObjectNotFoundException) {
			throw (cbit.util.ObjectNotFoundException)itex.getTargetException();
		} else if (itex.getTargetException() instanceof cbit.util.DataAccessException) {
			throw (DataAccessException)itex.getTargetException();
		} else {			
			throw new RuntimeException(itex.getMessage());
		}
	} catch (IllegalAccessException e){
		sessionLog.exception(e);
		throw new RuntimeException("IllegalAccessException for " + message);
	}

	return returnValue;
	
}
}