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

import org.vcell.util.CacheStatus;
import org.vcell.util.document.User;

/**
 * Insert the type's description here.
 * Creation date: (12/9/2002 12:44:13 AM)
 * @author: Jim Schaff
 */
public class ServerInfo implements java.io.Serializable {
	
private String hostName = null;
private CacheStatus cacheStatus = null;
private User[] connectedUsers = null;

public ServerInfo(String argHostName,CacheStatus argCacheStatus, User[] argConnectedUsers){
	hostName = argHostName;
	cacheStatus = argCacheStatus;
	connectedUsers = argConnectedUsers;
}
public CacheStatus getCacheStatus() {
	return cacheStatus;
}
public User[] getConnectedUsers() {
	return connectedUsers;
}
public String getHostName(){
	return hostName;
}

}
