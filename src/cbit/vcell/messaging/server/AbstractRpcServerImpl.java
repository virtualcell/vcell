package cbit.vcell.messaging.server;
import java.io.*;
import cbit.vcell.server.*;
import javax.jms.JMSException;

import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Insert the type's description here.
 * Creation date: (5/13/2003 2:07:30 PM)
 * @author: Fei Gao
 */
public abstract class AbstractRpcServerImpl implements RpcServerImpl {
	protected org.vcell.util.SessionLog log = null;

/**
 * RpcServerImpl constructor comment.
 */
protected AbstractRpcServerImpl(org.vcell.util.SessionLog slog) {
	super();
	this.log = slog;
}


/**
 * Insert the method's description here.
 * Creation date: (3/16/2004 12:30:25 PM)
 * @return java.lang.Object
 */
public abstract Object getServerImpl();


/**
 * Insert the method's description here.
 * Creation date: (12/5/2001 5:51:04 PM)
 * @return java.lang.Object
 * @param user cbit.vcell.server.User
 * @param method java.lang.String
 * @param args java.lang.Object[]
 */
public synchronized final Object rpc(RpcRequest request) throws DataAccessException, JMSException, ObjectNotFoundException {
	String methodName = request.getMethodName();
	Object[] arguments = request.getArguments();
	
	java.lang.reflect.Method methods[] = getServerImpl().getClass().getMethods();
	java.lang.reflect.Method method = null;
	for (int i = 0; methods != null && i < methods.length; i ++){
		if (methods[i].getName().equals(methodName)){
			method = methods[i];
			
			Class[] paramTypes = method.getParameterTypes();
			if (paramTypes.length != arguments.length) {
				method = null;
				continue;
			}

			// compare types one bye one
			for (int j = 0; j < paramTypes.length; j ++){
				// if argument is null, assume type matches
				if (arguments[j] == null || paramTypes[j].isInstance(arguments[j])) { // if the object is instance of the parameter type, compatible, check next
					continue;
				}
				
				if (paramTypes[j].isPrimitive()) {	// if not, check if it's primitive type
					Class c = arguments[j].getClass(); 
					Class argType = null;

					//unwrap primitive type
					if (c.equals(Boolean.class)) {
						argType = Boolean.TYPE;
					} else if (c.equals(Character.class)) {
						argType = Character.TYPE;
					} else if (c.equals(Byte.class)){
						argType = Byte.TYPE;
					} else if (c.equals(Short.class)){
						argType = Short.TYPE;
					} else if (c.equals(Integer.class)){
						argType = Integer.TYPE;
					} else if (c.equals(Long.class)){
						argType = Long.TYPE;
					} else if (c.equals(Float.class)){
						argType = Float.TYPE;
					} else if (c.equals(Double.class)){
						argType = Double.TYPE;
					} else if (c.equals(Void.class)){
						argType = Void.TYPE;
					} else {
						argType = null;
					}

					if (argType != null && paramTypes[j].equals(argType)) { // if it's primitive type, and the types are equal, compatible, check next
						continue;
					}
				}
				
				method = null; // otherwise, these two types are not compatible, break
				break;				
			}

			if (method != null) {
				break;
			}			
		}
	}	
	
	try {
		if (method == null) {
			String exceptionMessage = "No such method: " + methodName + "(";
			for (int i = 0; i < arguments.length; i ++) {
				exceptionMessage += arguments[i].getClass().getName();
				if (i < arguments.length - 1) {
					exceptionMessage += ",";
				}
			}
			exceptionMessage += ")";
			throw new DataAccessException(exceptionMessage);
		}
				
		return method.invoke(getServerImpl(), request.getArguments());

	} catch (InvocationTargetException ex) {
		log.exception(ex);
	 	Throwable targetExcepton = ex.getTargetException();
	 	if (targetExcepton instanceof JMSException) {
		 	throw (JMSException)targetExcepton;
	 	} else if (targetExcepton instanceof ObjectNotFoundException) {
		 	throw (ObjectNotFoundException)targetExcepton;
	 	} else if (targetExcepton instanceof DataAccessException) {
		 	throw (DataAccessException)targetExcepton;
	 	} else {
		 	throw new RuntimeException(targetExcepton.getMessage());
	 	}
	} catch (IllegalAccessException e){
		log.exception(e);
		throw new RuntimeException("IllegalAccessException for rpc(method=" + methodName);
	}
}
}