package cbit.vcell.messaging.server;

import cbit.vcell.messaging.admin.ManageUtils;
import cbit.vcell.messaging.admin.ServiceSpec;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.FileOutputStream;

/**
 * Insert the type's description here.
 * Creation date: (1/26/2004 10:08:15 AM)
 * @author: Fei Gao
 */
public abstract class AbstractJmsServiceProvider implements ServiceProvider {
	protected ServiceSpec serviceSpec = null;
	protected cbit.vcell.server.SessionLog log = null;
	
public AbstractJmsServiceProvider() {
	super();
}

/**
 * Insert the method's description here.
 * Creation date: (11/24/2003 1:30:19 PM)
 * @return cbit.vcell.messaging.admin.VCellService
 */
public ServiceSpec getServiceSpec() {
	return serviceSpec;
}
/**
 * Insert the method's description here.
 * Creation date: (7/15/2003 10:42:52 AM)
 * @return java.lang.String
 */
public String getServiceID() {
	return serviceSpec.getID();
}
/**
 * Insert the method's description here.
 * Creation date: (7/15/2003 10:42:52 AM)
 * @return java.lang.String
 */
public String getServiceType() {
	return serviceSpec.getType();
}

/**
 * Insert the method's description here.
 * Creation date: (1/26/2004 9:49:08 AM)
 */
protected void initLog(String logDirectory) throws FileNotFoundException {
	if (serviceSpec == null) {
		throw new RuntimeException("ServiceSpec can't be null");		
	}
	if (logDirectory != null) {
		File logdir = new File(logDirectory);
		if (!logdir.exists()) {
			throw new RuntimeException("Log directory doesn't exist");
		}
			
		// log file name:
		// hostname_A_Data_0.log : alpha first data on hostname
		// hostname_B_Db_0.log : beta first database on hostname
		// hostname_R_Export_0.log : rel first export on hostname
		File logfile = new File(logdir, serviceSpec.getID() + "_" + ManageUtils.getHostName() + ".log");
		java.io.PrintStream ps = new PrintStream(new FileOutputStream(logfile), true); // don't append
		System.out.println("log file is " + ps);
		System.setOut(ps);
		System.setErr(ps);
	}	
}
}
