/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message;

import java.lang.reflect.InvocationTargetException;

import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.User;

/**
 * Insert the type's description here.
 * Creation date: (5/13/2003 1:41:34 PM)
 * @author: Fei Gao
 */
public class VCRpcRequest implements java.io.Serializable {
	
	public interface RpcServiceType {
		public String getName();
	}
	
	private User user = null;
	private Object[] args = null;
	private RpcServiceType requestedServiceType = null; // refer to "databaseServer", "dataServer", "***";
	private String methodName = null;	
	private Long requestTimestampMS;
	private Long beginProcessingTimestampMS;
	private Long endProcessingTimestampMS;
/**
 * SimpleTask constructor comment.
 * @param argName java.lang.String
 * @param argEstimatedSizeMB double
 * @param argUserid java.lang.String
 */
public VCRpcRequest(User user0, RpcServiceType st, String methodName0, Object[] arglist) {
	this.user = user0;
	this.requestedServiceType = st;
	this.methodName = methodName0;
	this.args = arglist;
	this.requestTimestampMS = System.currentTimeMillis();
}
/**
 * Insert the method's description here.
 * Creation date: (5/13/2003 1:56:44 PM)
 * @return java.lang.Object[]
 */
public Object[] getArguments() {
	return args;
}
/**
 * Insert the method's description here.
 * Creation date: (5/13/2003 2:43:42 PM)
 * @return java.lang.String
 */
public String getMethodName() {
	return methodName;
}
/**
 * Insert the method's description here.
 * Creation date: (12/30/2003 9:16:45 AM)
 * @return java.lang.String
 */
public RpcServiceType getRequestedServiceType() {
	return requestedServiceType;
}
/**
 * Insert the method's description here.
 * Creation date: (3/11/2004 8:50:08 AM)
 * @return java.lang.String
 */
public org.vcell.util.document.User getUser() {
	return user;
}
/**
 * Insert the method's description here.
 * Creation date: (3/11/2004 8:51:06 AM)
 * @return java.lang.String
 */
public java.lang.String getUserName() {
	if (user == null) {
		return null;
	}
	
	return user.getName();
}


public Long getRequestTimestampMS() {
	return requestTimestampMS;
}
public Long getBeginProcessingTimestampMS() {
	return beginProcessingTimestampMS;
}
public Long getEndProcessingTimestampMS() {
	return endProcessingTimestampMS;
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 5:12:18 PM)
 * @return java.lang.String
 */
public String toString() {
	return "[" + user + "," + requestedServiceType + "," + methodName + "]";
}

public final Object rpc(Object rpcServiceImpl, SessionLog log) throws DataAccessException, ObjectNotFoundException {
	String methodName = getMethodName();
	Object[] arguments = getArguments();
	
	java.lang.reflect.Method methods[] = rpcServiceImpl.getClass().getMethods();
	java.lang.reflect.Method method = null;
	for (int i = 0; methods != null && i < methods.length; i ++){
		if (methods[i].getName().equals(methodName)){
			method = methods[i];
			
			Class<?>[] paramTypes = method.getParameterTypes();
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
					Class<?> c = arguments[j].getClass(); 
					Class<?> argType = null;

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
		beginProcessingTimestampMS = System.currentTimeMillis();
		Object result = method.invoke(rpcServiceImpl, getArguments());
		endProcessingTimestampMS = System.currentTimeMillis();
		return result;
		
	} catch (InvocationTargetException ex) {
		log.exception(ex);
	 	Throwable targetExcepton = ex.getTargetException();
	 	if (targetExcepton instanceof ObjectNotFoundException) {
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
