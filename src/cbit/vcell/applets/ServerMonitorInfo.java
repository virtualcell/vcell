package cbit.vcell.applets;

/**
 * Insert the type's description here.
 * Creation date: (1/2/2003 2:15:48 PM)
 * @author: Jim Schaff
 */
public class ServerMonitorInfo extends Object {

	public static final String SERVERTYPE_MAIN = "Main";
	public static final String SERVERTYPE_COMPUTE = "Compute";
	public static final String SERVERTYPE_FILE= "File";
	
	private String hostName = null;
	private String serverType = null;
	private cbit.vcell.server.ServerInfo serverInfo = null;
/**
 * ServerInfo constructor comment.
 */
public ServerMonitorInfo(String argHostName, String argServerType, cbit.vcell.server.ServerInfo argServerInfo) {
	super();
	this.hostName = argHostName;
	this.serverType = argServerType;
	this.serverInfo = argServerInfo;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2003 2:42:29 PM)
 * @return cbit.vcell.server.ServerInfo
 */
public String getHostName() {
	return hostName;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2003 2:42:29 PM)
 * @return cbit.vcell.server.ServerInfo
 */
public cbit.vcell.server.ServerInfo getServerInfo() {
	return serverInfo;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2003 2:42:29 PM)
 * @return java.lang.String
 */
public java.lang.String getServerType() {
	return serverType;
}
}
