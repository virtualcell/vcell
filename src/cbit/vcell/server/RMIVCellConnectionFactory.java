/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server;

import org.vcell.util.AuthenticationException;
import org.vcell.util.VCellThreadChecker;
import org.vcell.util.document.UserLoginInfo;


public class RMIVCellConnectionFactory implements VCellConnectionFactory {

	public static final String SERVICE_NAME = "VCellBootstrapServer";

	private String connectString = null;
	UserLoginInfo userLoginInfo;
	private String host = null;

/**
 * RMIVCellConnectionFactory constructor comment.
 */
public RMIVCellConnectionFactory(String argHost,UserLoginInfo userLoginInfo) {
	this.host = argHost;
	this.userLoginInfo = userLoginInfo;	
	this.connectString = "//"+host+"/"+SERVICE_NAME;
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
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.VCellConnection
 */
public VCellConnection createVCellConnection() throws AuthenticationException, ConnectionException {
	VCellThreadChecker.checkRemoteInvocation();

	VCellBootstrap vcellBootstrap = null;
	VCellConnection vcellConnection = null;
	try {
		vcellBootstrap = (cbit.vcell.server.VCellBootstrap)java.rmi.Naming.lookup(connectString);
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
public static String getVCellSoftwareVersion(String host) {
	VCellThreadChecker.checkRemoteInvocation();
	
	try {
		VCellBootstrap vcellBootstrap = (cbit.vcell.server.VCellBootstrap)java.rmi.Naming.lookup("//"+host+"/"+SERVICE_NAME);
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
		VCellBootstrap vcellBootstrap = (cbit.vcell.server.VCellBootstrap)java.rmi.Naming.lookup("//"+host+"/"+SERVICE_NAME);
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
}
