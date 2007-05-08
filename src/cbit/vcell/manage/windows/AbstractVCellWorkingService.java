package cbit.vcell.manage.windows;
/**
 * Insert the type's description here.
 * Creation date: (6/22/2006 1:35:33 PM)
 * @author: Ion Moraru
 */
public abstract class AbstractVCellWorkingService extends AbstractService {

	public final static String DEFAULT_PROPERTY_FILE_NAME = "vcell.properties";

	protected String hostname = null;
	protected String logFileName = null;
	protected String siteName = null;
	protected java.io.File workingDir = null;
	protected boolean isAlive = true;

	public static final String SERVICE_TYPE_COMPUTE_JAVA = "JavaComputeService";
	public static final String SERVICE_TYPE_COMPUTE_LSF = "LSFComputeService";
	public static final String SERVICE_TYPE_DATA = "DataService";
	public static final String SERVICE_TYPE_DATABASE = "DatabaseService";
	public static final String SERVICE_TYPE_BNG = "BngService";
	public static final String SERVICE_TYPE_DISPATCH = "DispatchService";
	public static final String SERVICE_TYPE_EXPORT = "ExportService";
	public static final String SERVICE_TYPE_RMI = "RMIService";

/**
 * AbstractWorkingService constructor comment.
 */
protected AbstractVCellWorkingService() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (6/23/2006 2:39:08 PM)
 * @return java.lang.String
 */
public final java.lang.String getName() {
	return "VCell"+getServiceType()+siteName;
}


/**
 * Insert the method's description here.
 * Creation date: (6/22/2006 1:45:36 PM)
 */
public abstract String getServiceType();	


/**
 * Insert the method's description here.
 * Creation date: (6/20/2006 1:22:54 PM)
 * @return boolean
 */
void initialize() throws Throwable {
	workingDir = new java.io.File(getArgs()[0]);
	// allow jvm argument override, but if not (normal case), force unique definition
	if (System.getProperty("vcell.propertyfile") == null) {
		System.setProperty("vcell.propertyfile", new java.io.File(workingDir, DEFAULT_PROPERTY_FILE_NAME).getAbsolutePath());
	}
	cbit.vcell.server.PropertyLoader.loadProperties();
	siteName = cbit.vcell.server.PropertyLoader.getRequiredProperty(cbit.vcell.server.PropertyLoader.vcellServerIDProperty);
	hostname = java.net.InetAddress.getLocalHost().getHostName();

	logFileName = new java.io.File(workingDir, siteName+"_"+hostname+"_"+getServiceType()+".log").getAbsolutePath();
	if (getServiceType().equals(SERVICE_TYPE_RMI)) {
		System.setOut(new java.io.PrintStream(new java.io.FileOutputStream(logFileName, true), true));
		System.setErr(new java.io.PrintStream(new java.io.FileOutputStream(logFileName, true), true));
	} else {
		System.setOut(new java.io.PrintStream(new java.io.FileOutputStream(logFileName)));
		System.setErr(new java.io.PrintStream(new java.io.FileOutputStream(logFileName)));
	}

	cbit.vcell.modeldb.DatabasePolicySQL.bSilent = true;
}


/**
 * Insert the method's description here.
 * Creation date: (6/24/2006 3:29:56 PM)
 */
protected void keepAlive() {
	while (isAlive) {
		try {Thread.sleep(10000);} catch (InterruptedException e) {}
	}
}
}