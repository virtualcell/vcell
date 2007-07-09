package cbit.vcell.server;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.rmi.RemoteException;

import org.vcell.util.document.User;

/**
 * This type was created in VisualAge.
 */
public class RMIVCellServerFactory implements VCellServerFactory {
	private VCellBootstrap vcellBootstrap = null;
	private VCellServer vcellServer = null;
	private String connectString = null;
	private static final String SERVICE_NAME = "VCellBootstrapServer";
	private User user = null;
	private String password = null;
/**
 * RMIVCellConnectionFactory constructor comment.
 */
public RMIVCellServerFactory(String host, int port, User user, String password) {
	this.connectString = "//"+host+":"+port+"/"+SERVICE_NAME;
	this.user = user;
	this.password = password;
}
/**
 * RMIVCellConnectionFactory constructor comment.
 */
public RMIVCellServerFactory(String host, User user, String password) {
	this.connectString = "//"+host+"/"+SERVICE_NAME;
	this.user = user;
	this.password = password;
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
public static boolean pingBootstrap(String host) {
	try {
		VCellBootstrap vcellBootstrap = (cbit.vcell.server.VCellBootstrap)java.rmi.Naming.lookup("//"+host+"/"+SERVICE_NAME);
		if (vcellBootstrap != null){
			return true;
		}else{
			return false;
		}
	} catch (Throwable e){
		return false;
	}			
}
/**
 * This method was created in VisualAge.
 */
public void pingVCellServer() throws RemoteException {
	vcellServer.isPrimaryServer();
}
/**
 * This method was created in VisualAge.
 */
private void reconnect() throws ConnectionException, AuthenticationException {
	String bootstrapName = "VCellBootstrapServer";
	try {
		vcellBootstrap = (cbit.vcell.server.VCellBootstrap)java.rmi.Naming.lookup(connectString);
	} catch (Throwable e){
		throw new ConnectionException("cannot contact server: "+e.getMessage());
	}
					
	vcellServer = null;
	try {
		vcellServer = vcellBootstrap.getVCellServer(user,password);
		if (vcellServer==null){
			throw new AuthenticationException("cannot login to server, check userid and password");
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
		throw new ConnectionException("failure while connecting to "+connectString+": "+e.getMessage());
	}
}
}
