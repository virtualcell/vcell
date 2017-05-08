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

import java.rmi.RemoteException;

import org.vcell.util.AuthenticationException;
import org.vcell.util.VCellThreadChecker;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.server.ConnectionException;
import cbit.vcell.server.VCellBootstrap;
import cbit.vcell.server.VCellServer;
import cbit.vcell.server.VCellServerFactory;

/**
 * This type was created in VisualAge.
 */
public class RMIVCellServerFactory implements VCellServerFactory {
	private VCellBootstrap vcellBootstrap = null;
	private VCellServer vcellServer = null;
	private String connectString = null;
	private static final String SERVICE_NAME = "VCellBootstrapServer";
	private User user = null;
	private UserLoginInfo.DigestedPassword digestedPassword = null;
/**
 * RMIVCellConnectionFactory constructor comment.
 */
public RMIVCellServerFactory(String host, int port, User user, UserLoginInfo.DigestedPassword digestedPassword) {
	this.connectString = "//"+host+":"+port+"/"+SERVICE_NAME;
	this.user = user;
	this.digestedPassword = digestedPassword;
}
/**
 * RMIVCellConnectionFactory constructor comment.
 */
public RMIVCellServerFactory(String host, User user,UserLoginInfo.DigestedPassword digestedPassword) {
	this.connectString = "//"+host+"/"+SERVICE_NAME;
	this.user = user;
	this.digestedPassword = digestedPassword;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.VCellBootstrap
 */
public VCellBootstrap getBootstrap() throws ConnectionException, AuthenticationException {
	reconnect();
	
	return vcellBootstrap;
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/01 3:13:39 PM)
 * @return java.lang.String
 */
public String getConnectionString() {
	return connectString;
}
/**
 * getVCellConnection method comment.
 */
public VCellServer getVCellServer() throws ConnectionException, AuthenticationException {
	VCellThreadChecker.checkRemoteInvocation();
	
	if (vcellServer==null){
		reconnect();
	}else{
		try {
			pingVCellServer();
		}catch (Exception e){
			reconnect();
		}
	}
	return vcellServer;
}

/**
 * This method was created in VisualAge.
 */
public void pingVCellServer() throws RemoteException {
	VCellThreadChecker.checkRemoteInvocation();
	
	vcellServer.getBootTime();
}
/**
 * This method was created in VisualAge.
 */
private void reconnect() throws ConnectionException, AuthenticationException {
	VCellThreadChecker.checkRemoteInvocation();
	
//	String bootstrapName = "VCellBootstrapServer";
	try {
		vcellBootstrap = (VCellBootstrap)java.rmi.Naming.lookup(connectString);
	} catch (Throwable e){
		throw new ConnectionException("cannot contact server: "+e.getMessage());
	}
					
	vcellServer = null;
	try {
		vcellServer = vcellBootstrap.getVCellServer(user,digestedPassword);
		if (vcellServer==null){
			throw new AuthenticationException("cannot login to server, check userid and password");
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
		throw new ConnectionException("failure while connecting to "+connectString+": "+e.getMessage());
	}
}
}
