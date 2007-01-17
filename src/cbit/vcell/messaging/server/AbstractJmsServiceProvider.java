package cbit.vcell.messaging.server;

import cbit.vcell.messaging.admin.VCServiceInfo;
import java.io.PrintStream;
import java.io.FileOutputStream;
import cbit.vcell.server.PropertyLoader;
import cbit.vcell.modeldb.DatabasePolicySQL;

/**
 * Insert the type's description here.
 * Creation date: (1/26/2004 10:08:15 AM)
 * @author: Fei Gao
 */
public abstract class AbstractJmsServiceProvider implements ServiceProvider {
	protected VCServiceInfo serviceInfo = null;
/**
 * JmsServer constructor comment.
 */
public AbstractJmsServiceProvider() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 1:50:41 AM)
 * @return java.lang.String
 */
public String getHostName() {
	return serviceInfo.getHostName();
}
/**
 * Insert the method's description here.
 * Creation date: (11/24/2003 1:30:19 PM)
 * @return cbit.vcell.messaging.admin.VCellService
 */
public VCServiceInfo getServiceInfo() {
	return serviceInfo;
}
/**
 * Insert the method's description here.
 * Creation date: (7/15/2003 10:42:52 AM)
 * @return java.lang.String
 */
public String getServiceName() {
	return serviceInfo.getServiceName();
}
/**
 * Insert the method's description here.
 * Creation date: (7/15/2003 10:42:52 AM)
 * @return java.lang.String
 */
public String getServiceType() {
	return serviceInfo.getServiceType();
}
/**
 * Insert the method's description here.
 * Creation date: (1/26/2004 9:49:08 AM)
 */
public static void mainInit(String logfile) throws Exception {
	//
	// Create and install a security manager
	//
	System.setSecurityManager(new SecurityManager());
	if (logfile != null) {
		java.io.PrintStream ps = new PrintStream(new FileOutputStream(logfile, true), true);
		System.setOut(ps);
		System.setErr(ps);
	}
	PropertyLoader.loadProperties();
	DatabasePolicySQL.bSilent = true;
}
}
