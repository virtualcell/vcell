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
	public enum ServerType {
		SERVER_REMOTE,
		SERVER_LOCAL,
		SERVER_FILE
	}

	private ServerType serverType = null;
	private String apihost = null;
	private Integer apiport = null;
	private UserLoginInfo userLoginInfo = null;

/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 4:10:03 PM)
 * @param host java.lang.String
 * @param username java.lang.String
 * @param password java.lang.String
 */
private ClientServerInfo(ServerType serverType, String apihost, Integer apiport, UserLoginInfo userLoginInfo) {
	this.apihost = apihost;
	this.apiport = apiport;
	this.serverType = serverType;
	this.userLoginInfo = userLoginInfo;
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 11:18:42 PM)
 * @return cbit.vcell.client.server.ClientServerInfo
 */
public static ClientServerInfo createFileBasedServerInfo() {
	ClientServerInfo csi = new ClientServerInfo(ServerType.SERVER_FILE,null,null,null);
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
	ClientServerInfo csi = new ClientServerInfo(ServerType.SERVER_LOCAL,null,null,new UserLoginInfo(userName, digestedPassword));
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
public static ClientServerInfo createRemoteServerInfo(String apihost, Integer apiport, String userName,DigestedPassword digestedPassword) {
	ClientServerInfo csi = new ClientServerInfo(ServerType.SERVER_REMOTE,apihost,apiport,new UserLoginInfo(userName, digestedPassword));
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
public String getApihost() {
	return apihost;
}

public Integer getApiport() {
	return apiport;
}

/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 11:20:12 PM)
 * @return int
 */
public ServerType getServerType() {
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
			details = "SERVER_REMOTE, host: " + apihost + "port: " + apiport + ", user:" + getUsername();
			break;
		}
		case SERVER_FILE: {
			details = "SERVER_FILE";
			break;
		}
	}
	return "ClientServerInfo: [" + details + "]";
}

}
