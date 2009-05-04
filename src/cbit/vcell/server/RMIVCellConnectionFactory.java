package cbit.vcell.server;

import cbit.vcell.client.server.VCellThreadChecker;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

public class RMIVCellConnectionFactory implements VCellConnectionFactory {

	public static final String SERVICE_NAME = "VCellBootstrapServer";

	private String connectString = null;
	private String userid = null;
	private String password = null;
	private String host = null;

/**
 * RMIVCellConnectionFactory constructor comment.
 */
public RMIVCellConnectionFactory(String argHost, String argUserid, String argPassword) {
	this.host = argHost;
	this.userid = argUserid;
	this.password = argPassword;	
	this.connectString = "//"+host+"/"+SERVICE_NAME;
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/2001 12:03:11 PM)
 * @param userID java.lang.String
 * @param password java.lang.String
 */
public void changeUser(java.lang.String userID, java.lang.String password) {
	this.userid = userID;
	this.password = password;
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
		throw new ConnectionException(e.getMessage());
	}
	try {
		vcellConnection = vcellBootstrap.getVCellConnection(userid,password);
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
