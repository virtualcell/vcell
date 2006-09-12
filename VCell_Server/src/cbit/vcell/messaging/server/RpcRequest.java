package cbit.vcell.messaging.server;

import cbit.util.User;

/**
 * Insert the type's description here.
 * Creation date: (5/13/2003 1:41:34 PM)
 * @author: Fei Gao
 */
public class RpcRequest implements java.io.Serializable {
	private User user = null;
	private Object[] args = null;
	private String requestedServiceType = null; // refer to "databaseServer", "dataServer", "***";
	private String methodName = null;	
/**
 * SimpleTask constructor comment.
 * @param argName java.lang.String
 * @param argEstimatedSizeMB double
 * @param argUserid java.lang.String
 */
public RpcRequest(User user0, String taskType, String methodName0, Object[] arglist) {
	user = user0;
	this.requestedServiceType = taskType;
	methodName = methodName0;
	this.args = arglist;	
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
public java.lang.String getRequestedServiceType() {
	return requestedServiceType;
}
/**
 * Insert the method's description here.
 * Creation date: (3/11/2004 8:50:08 AM)
 * @return java.lang.String
 */
public User getUser() {
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
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 5:12:18 PM)
 * @return java.lang.String
 */
public String toString() {
	return "[" + user + "," + requestedServiceType + "," + methodName + "]";
}
}
