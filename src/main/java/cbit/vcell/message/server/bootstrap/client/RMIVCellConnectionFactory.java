/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.bootstrap.client;

import java.awt.Component;

import org.vcell.util.AuthenticationException;
import org.vcell.util.NetworkProxyUtils;
import org.vcell.util.VCellThreadChecker;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.message.server.bootstrap.VCellConnectionFactory;
import cbit.vcell.server.ConnectionException;
import cbit.vcell.server.VCellBootstrap;
import cbit.vcell.server.VCellConnection;


public class RMIVCellConnectionFactory implements VCellConnectionFactory {

	public static final String SERVICE_NAME = "VCellBootstrapServer";

//	private String connectString = null;
	UserLoginInfo userLoginInfo;
	private String host = null;

/**
 * RMIVCellConnectionFactory constructor comment.
 */
public RMIVCellConnectionFactory(String argHost,UserLoginInfo userLoginInfo) {
	this.host = argHost;
	this.userLoginInfo = userLoginInfo;	
//	this.connectString = "//"+host+"/"+SERVICE_NAME;
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/2001 12:03:11 PM)
 * @param userID java.lang.String
 * @param password java.lang.String
 */
public void changeUser(UserLoginInfo userLoginInfo) {
	this.userLoginInfo = userLoginInfo;	
}

public VCellConnection createVCellConnection() throws AuthenticationException, ConnectionException {
	return createVCellConnectionAskProxy(null);
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.VCellConnection
 */
public VCellConnection createVCellConnectionAskProxy(Component requester) throws AuthenticationException, ConnectionException {
	VCellThreadChecker.checkRemoteInvocation();

	VCellBootstrap vcellBootstrap = null;
	VCellConnection vcellConnection = null;
	try {
		vcellBootstrap = getVCellBootstrap(requester,this.host);
	} catch (Throwable e){
		throw new ConnectionException(e.getMessage(),e);
	}
	try {
		vcellConnection = vcellBootstrap.getVCellConnection(userLoginInfo);
		if (vcellConnection==null){
			throw new AuthenticationException("cannot login to server, check userid and password");
		}
	}catch (AuthenticationException ae) {
		throw ae;
	}catch (Throwable e){
		e.printStackTrace(System.out);
		throw new ConnectionException(e.getMessage());
	}
	return vcellConnection;
}
/**
 * This method was created in VisualAge.
 */
public static String getVCellSoftwareVersion(Component requester,String host) {
	VCellThreadChecker.checkRemoteInvocation();
	
	try {
		VCellBootstrap vcellBootstrap = getVCellBootstrap(requester,host);
		if (vcellBootstrap != null){
			return vcellBootstrap.getVCellSoftwareVersion();
		}else{
			return null;
		}
	} catch (Throwable e){
		e.printStackTrace(System.out);
		return null;
	}			
}
/**
 * This method was created in VisualAge.
 */
public static boolean pingBootstrap(String host) {
	VCellThreadChecker.checkRemoteInvocation();
	
	try {
		VCellBootstrap vcellBootstrap = getVCellBootstrap(null,host);
		if (vcellBootstrap != null){
			return true;
		}else{
			return false;
		}
	} catch (Throwable e){
		e.printStackTrace(System.out);
		return false;
	}			
}

private static VCellBootstrap getVCellBootstrap0(String host) throws Exception{
	return (cbit.vcell.server.VCellBootstrap)java.rmi.Naming.lookup("//"+host+"/"+SERVICE_NAME);
}
public static VCellBootstrap getVCellBootstrap(Component requester,String host) throws Exception{
	//If requester != null (called from VCell client) and connection fails then we ask user to supply proxy info
	//If requester == null (called from VCell server) then we assume that all connection properties were set already
//	Preferences prefs = Preferences.userNodeForPackage(RMIVCellConnectionFactory.class);
	try {
		return getVCellBootstrap0(host);
	} catch (Exception e) {
		e.printStackTrace();
		throw new Exception(NetworkProxyUtils.createNetworkExceptionMessage("Getting connection bootstrap failed",e),e);
////		Throwable parent = e;
////		do{
////			System.out.println(parent.getClass().getName()+" "+parent.getMessage());
////		}while((parent = parent.getCause()) != null);		
//		if(requester != null){// called from client, see if proxy prefs are set and try those
//			return getVCellBootstrap(requester, handleExceptionProxyPrfs(e));
//		}else{// called from server, requester == null, assume java properties for proxies (if necessary) are not set correctly at jvm startup
//			throw createException(e);
//		}
	}
}


}
