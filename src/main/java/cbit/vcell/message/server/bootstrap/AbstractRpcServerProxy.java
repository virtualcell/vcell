/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.bootstrap;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCRpcRequest.RpcServiceType;
import cbit.vcell.message.VCellQueue;

/**
 * Insert the type's description here.
 * Creation date: (5/13/2003 2:14:50 PM)
 * @author: Fei Gao
 */
public abstract class AbstractRpcServerProxy {
	private VCMessageSession vcMessagingSession = null;
	protected UserLoginInfo userLoginInfo = null;
	private VCellQueue queue = null;
	protected org.vcell.util.SessionLog log = null;

/**
 * RpcServerProxy constructor comment.
 */
protected AbstractRpcServerProxy(UserLoginInfo userLoginInfo, VCMessageSession vcMessageSession, VCellQueue queue, SessionLog argLog) {
	super();
	this.userLoginInfo = userLoginInfo;
	this.vcMessagingSession = vcMessageSession;
	this.log = argLog;
	this.queue = queue;
}


/**
 * Insert the method's description here.
 * Creation date: (12/5/2001 9:39:03 PM)
 * @return java.lang.Object
 * @param methodName java.lang.String
 * @param args java.lang.Object[]
 * @exception java.lang.Exception The exception description.
 */
public Object rpc(RpcServiceType serviceType, String methodName, Object[] args, boolean returnRequired) throws Exception {
	return rpc(serviceType, methodName, args, returnRequired, null, null);
}


/**
 * Insert the method's description here.
 * Creation date: (12/5/2001 9:39:03 PM)
 * @return java.lang.Object
 * @param methodName java.lang.String
 * @param args java.lang.Object[]
 * @exception java.lang.Exception The exception description.
 */
public Object rpc(RpcServiceType serviceType, String methodName, Object[] args, boolean returnRequired, String[] specialProperties, Object[] specialValues) throws Exception {
	VCRpcRequest vcRpcRequest = new VCRpcRequest(userLoginInfo.getUser(), serviceType, methodName, args);
	int timeoutMS = Integer.parseInt(PropertyLoader.getProperty(PropertyLoader.vcellClientTimeoutMS, "120000")); // default to 2 minutes.
	return vcMessagingSession.sendRpcMessage(queue, vcRpcRequest, returnRequired, timeoutMS, specialProperties, specialValues, userLoginInfo);
}
}
