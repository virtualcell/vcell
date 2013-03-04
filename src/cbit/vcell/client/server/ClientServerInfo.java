/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.server;

import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;

/**
 * Insert the type's description here.
 * Creation date: (5/12/2004 4:07:23 PM)
 * @author: Ion Moraru
 */
public class ClientServerInfo {
	public final static int SERVER_REMOTE = 0;
	public final static int SERVER_LOCAL = 1;
	public final static int SERVER_FILE = 2;
	public final static String LOCAL_SERVER = "LOCAL";

	private int serverType = -1;
	private String[] hosts = null;
	private String activeHost = null;
	private UserLoginInfo userLoginInfo = null;

/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 4:10:03 PM)
 * @param host java.lang.String
 * @param username java.lang.String
 * @param password java.lang.String
 */
private ClientServerInfo(String[] hosts,UserLoginInfo userLoginInfo) {
	this.hosts = hosts;
	this.userLoginInfo = userLoginInfo;
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 11:18:42 PM)
 * @return cbit.vcell.client.server.ClientServerInfo
 */
public static ClientServerInfo createFileBasedServerInfo() {
	ClientServerInfo csi = new ClientServerInfo(null,null);
	csi.setServerType(SERVER_FILE);
	return csi;
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 11:18:42 PM)
 * @return cbit.vcell.client.server.ClientServerInfo
 * @param host java.lang.String
 * @param username java.lang.String
 * @param password java.lang.String
 */
public static ClientServerInfo createLocalServerInfo(String userName, DigestedPassword digestedPassword) {
	ClientServerInfo csi = new ClientServerInfo(new String[] {LOCAL_SERVER},new UserLoginInfo(userName, digestedPassword));
	csi.setServerType(SERVER_LOCAL);
	return csi;
}



public UserLoginInfo getUserLoginInfo(){
	return userLoginInfo;
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 11:18:42 PM)
 * @return cbit.vcell.client.server.ClientServerInfo
 * @param host java.lang.String
 * @param username java.lang.String
 * @param password java.lang.String
 */
public static ClientServerInfo createRemoteServerInfo(String[] host, String userName,DigestedPassword digestedPassword) {
	ClientServerInfo csi = new ClientServerInfo(host,new UserLoginInfo(userName, digestedPassword));
	csi.setServerType(SERVER_REMOTE);
	return csi;
}


/**
 * Insert the method's description here.
 * Creation date: (6/28/2004 12:35:46 AM)
 * @return boolean
 * @param o java.lang.Object
 */
public boolean equals(Object o) {
	return (
		o != null &&
		o instanceof ClientServerInfo &&
		o.toString().equals(toString())
	);
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 4:09:30 PM)
 * @return java.lang.String
 */
public String[] getHosts() {
	return hosts;
}

/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 11:20:12 PM)
 * @return int
 */
public int getServerType() {
	return serverType;
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 4:09:30 PM)
 * @return java.lang.String
 */
public java.lang.String getUsername() {
	return userLoginInfo.getUserName();
}


/**
 * Insert the method's description here.
 * Creation date: (6/28/2004 12:48:39 AM)
 * @return int
 */
public int hashCode() {
	return toString().hashCode();
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 11:20:12 PM)
 * @param newServerType int
 */
private void setServerType(int newServerType) {
	serverType = newServerType;
}


/**
 * Insert the method's description here.
 * Creation date: (6/28/2004 12:40:07 AM)
 * @return java.lang.String
 */
public String toString() {
	String details = null;
	switch (getServerType()) {
		case SERVER_LOCAL: {
			details = "SERVER_LOCAL, user:" + getUsername();
			break;
		}
		case SERVER_REMOTE: {
			details = "SERVER_REMOTE, host:" + activeHost + ", user:" + getUsername();
			break;
		}
		case SERVER_FILE: {
			details = "SERVER_FILE";
			break;
		}
	}
	return "ClientServerInfo: [" + details + "]";
}


public String getActiveHost() {
	return activeHost;
}


public void setActiveHost(String activeHost) {
	this.activeHost = activeHost;
}
}
