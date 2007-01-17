package cbit.vcell.server;

import cbit.vcell.simdata.CacheStatus;
/**
 * Insert the type's description here.
 * Creation date: (12/9/2002 12:44:13 AM)
 * @author: Jim Schaff
 */
public class ServerInfo implements java.io.Serializable {
	
private String hostName = null;
private CacheStatus cacheStatus = null;
private ProcessStatus processStatus = null;
private User[] connectedUsers = null;

public ServerInfo(String argHostName,CacheStatus argCacheStatus,ProcessStatus argProcessStatus,User[] argConnectedUsers){
	hostName = argHostName;
	cacheStatus = argCacheStatus;
	processStatus = argProcessStatus;
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
public ProcessStatus getProcessStatus() {
	return processStatus;
}
}
