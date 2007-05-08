package cbit.vcell.manage.windows;
/**
 * Insert the type's description here.
 * Creation date: (6/22/2006 1:35:33 PM)
 * @author: Ion Moraru
 */

/* USE REFLECTION FOR ALL NON-SYSTEM CLASSES SO WE HAVE ONLY RUN-TIME LOADING OF VCELL RESOURCES */
 
public abstract class AbstractVCellDaemonService extends AbstractService {

	public static final String DAEMON_TYPE_BOOTSTRAP = "Bootstrap";
	public static final String DAEMON_TYPE_MANAGER = "ServerManager";

	protected String hostname = null;
	protected String siteName = null;
	protected java.io.File workingDir = null;
	protected java.io.File sourceDir = null;

	private java.io.File restartSentinelFile = null;

	protected Object daemon = null;

	protected boolean onlineUpdatePerfomed = false;
	protected boolean offlineUpdateScheduled = false;
	
	public static int SOURCE_CHECKING_INTERVAL_MINUTES_DEFAULT = 60; // not declared final so one could override from config files
	public static int SOURCE_CHECKING_INTERVAL_MINUTES_MANAGER = 5; // not declared final so one could override from config files
	public static int CONNECT_FAILURE_RETRY_MINUTES = 2; // not declared final so one could override from config files
	public static final String RESOURCE_FILES_ROOT = "Resources";
	public static final String CONFIG_FILES_ROOT = "Configs";
	public static final String SENTINEL_FILE_NAME = "restart.sentinel";
	public static final String DEFAULT_PROPERTY_FILE_NAME = "vcell.properties";
	
/**
 * AbstractWorkingService constructor comment.
 */
protected AbstractVCellDaemonService() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (6/22/2006 1:45:36 PM)
 */
public abstract String getDaemonType();	


/**
 * Insert the method's description here.
 * Creation date: (6/23/2006 2:39:08 PM)
 * @return java.lang.String
 */
public final java.lang.String getName() {
	return "VCell"+getDaemonType()+siteName;
}


/**
 * Insert the method's description here.
 * Creation date: (6/20/2006 1:22:54 PM)
 */
void initialize() throws Throwable {
	// initialize the basic info
	workingDir = new java.io.File(getArgs()[0]);
	sourceDir = new java.io.File(getArgs()[1]);
	siteName = workingDir.getName();
	hostname = java.net.InetAddress.getLocalHost().getHostName();
	restartSentinelFile = new java.io.File(workingDir, SENTINEL_FILE_NAME);
	// check for required updates
	setInitTimeout(120000); // give us some time to try update...
	if (isNewerResource()) {
		logWarningEvent("Newer source files found during initialization, attempting update...");
		stopDependentServicesOS(); // make sure all other local services are down... maybe we can update online; use OS-level controls (don't know yet about our config)
		updateConfigFiles();
		updateResourceFiles();
	} else {
		// clear persistence flag
		if (restartSentinelFile.exists()) {
			restartSentinelFile.delete();
		}
	}
	// get working info
	// allow jvm argument override, but if not (most common), force unique definition
	if (System.getProperty("vcell.propertyfile") == null) {
		System.setProperty("vcell.propertyfile", new java.io.File(workingDir, DEFAULT_PROPERTY_FILE_NAME).getAbsolutePath());
	}
	// check config assumptions (more strict for daemons...)
	Class propertyLoaderClass = Class.forName("cbit.vcell.server.PropertyLoader");
	propertyLoaderClass.getMethod("loadProperties", null).invoke(null, null);
	String siteNameProperty = (String)propertyLoaderClass.getMethod("getRequiredProperty", new Class[] {String.class}).invoke(null, new String[] {"vcell.server.id"});
	if (!siteName.equalsIgnoreCase(siteNameProperty)) {
		throw new Exception("Daemon misconfiguration, working directory is ["+siteName+"], site name property is ["+siteNameProperty+"]");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/22/2006 2:22:58 PM)
 * @exception java.lang.Exception The exception description.
 */
private boolean isNewerResource() throws java.lang.Exception {
	java.io.File resourceDir = new java.io.File(sourceDir, RESOURCE_FILES_ROOT);
	java.io.File[] resourceFiles = resourceDir.listFiles();
	for (int i = 0; i < resourceFiles.length; i++){
		java.io.File localFile = new java.io.File(workingDir, resourceFiles[i].getName());
		if (!localFile.exists() || localFile.lastModified() < resourceFiles[i].lastModified()) {
			return true;
		}
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (6/23/2006 2:11:38 PM)
 */
private void monitorSource() {
	int connectionFailures = 0;
	int intervalMillis = SOURCE_CHECKING_INTERVAL_MINUTES_DEFAULT * 60000;
	if (getDaemonType() == DAEMON_TYPE_MANAGER) {
		intervalMillis = SOURCE_CHECKING_INTERVAL_MINUTES_MANAGER * 60000;
	}
	// wait a bit the first time so the main running thread sets up
	try {
		Thread.sleep(10000);
	} catch (InterruptedException e) {}
	while (true) {
		try {
			// after connection failure, wait first
			if (connectionFailures > 0) {
				Thread.sleep(CONNECT_FAILURE_RETRY_MINUTES * 60000);
			}
			// check for recent/pending/needed update and act accordingly
			if (onlineUpdatePerfomed) {
				// restart all services so they use the new files
				stopDependentServices();
				startDependentServices();
				// reset flag
				onlineUpdatePerfomed = false;
			} else if (offlineUpdateScheduled) {
				// stop all services and stop ourselves to wait for scheduled offline update
				java.lang.reflect.Method stopAllMethod = daemon.getClass().getSuperclass().getDeclaredMethod("stopAllService", null); // method is on the superclass, and is protected...
				stopAllMethod.setAccessible(true);
				stopAllMethod.invoke(daemon, null);
				stopByCRH(getName()+" stopping, waiting for scheduled offline update and restart");
			} else {
				// check for update needed for both config and resource files
				updateConfigFiles(); // always OK to do live
				if (isNewerResource()) {
					logWarningEvent("Monitor thread detected newer resource files; stopping dependent services");
					stopDependentServices();
					if (getDaemonType() == DAEMON_TYPE_BOOTSTRAP) {
						// shutdown and wait to be restarted by manager
						stopByCRH("Waiting for ServerManager to initiate update");
					} else if (getDaemonType() == DAEMON_TYPE_MANAGER) {
						// initiate update
						logWarningEvent("Server manager initiating update");
						updateResourceFiles();
					}
				} else {
					// wait for a while...
					Thread.sleep(intervalMillis);
				}
				// clear connect flag
				connectionFailures = 0;
			}
		} catch (java.io.InterruptedIOException iexc) { // ignore
		} catch (Throwable exc) {
			connectionFailures++;
			logWarningEvent(getName()+" source check failed "+connectionFailures+" times; error is: "+exc);
		}
		if (connectionFailures >= 3) {
			// we are disconnected from the mother ship... should shut down the daemon and dependent services and wait for better times :)
			stopByCRH("Unable to check source too many times in a row");
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/26/2006 12:13:12 PM)
 */
protected void startDependentServices() throws IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
	java.lang.reflect.Method startAllMethod = daemon.getClass().getSuperclass().getDeclaredMethod("startAllServices", null);
	startAllMethod.setAccessible(true);
	startAllMethod.invoke(daemon, null);
}


/**
 * monitors source folder for changed resource files
 */
protected void startMonitorThread() {
	Thread sourceCheckerThread = new Thread(new Runnable() {
		public void run() {
			monitorSource();
		}
	});
	sourceCheckerThread.setPriority(Thread.NORM_PRIORITY - 1);
	sourceCheckerThread.start();
}


/**
 * Insert the method's description here.
 * Creation date: (6/26/2006 12:13:12 PM)
 */
protected void stopDependentServices() throws IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
	java.lang.reflect.Method stopAllMethod = daemon.getClass().getSuperclass().getDeclaredMethod("stopAllService", null);
	stopAllMethod.setAccessible(true);
	stopAllMethod.invoke(daemon, null);
}


/**
 * Insert the method's description here.
 * Creation date: (6/26/2006 12:13:12 PM)
 */
protected void stopDependentServicesOS() throws Exception {
	String[] dependents = getDependentServices(getName());
	for (int i = 0; i < dependents.length; i++){
		if (isServiceRunning(dependents[i])) {
			Process stopService = Runtime.getRuntime().exec("net stop "+dependents[i]);
			if (stopService.waitFor() != 0) {
				throw new Exception("Failed to stop running dependent process "+dependents[i]);
			}
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/25/2006 7:00:59 AM)
 */
private void updateConfigFiles() throws Exception, InterruptedException, java.io.IOException {
	java.io.File[] configFiles = new java.io.File(sourceDir, CONFIG_FILES_ROOT).listFiles();
	for (int i = 0; i < configFiles.length; i++){
		java.io.File localFile = new java.io.File(workingDir, configFiles[i].getName());
		if (!localFile.exists() || localFile.lastModified() < configFiles[i].lastModified()) {
			Process copy = Runtime.getRuntime().exec("cmd /c copy "+configFiles[i].getAbsolutePath()+" "+localFile.getAbsolutePath()+" /y");
			if (copy.waitFor() != 0) {
				throw new Exception("Copying updated config file "+configFiles[i].getAbsolutePath()+" failed");
			}
			logWarningEvent("Successfully updated config file from "+configFiles[i].getAbsolutePath());
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/22/2006 2:22:58 PM)
 * @exception java.lang.Exception The exception description.
 */
private void updateResourceFiles() throws java.lang.Exception {
	// this is the real tricky stuff...
	// called by either initialize() or by monitor thread;
	if (!isServiceRunning(getName())) {
		// check that we are healthy enough to identify ourselves...
		throw new Exception("Can't confirm running service name... getName() returns "+getName());
	}
	// ok, try
	boolean needOfflineUpdate = false;
	java.io.File resourceDir = new java.io.File(sourceDir, RESOURCE_FILES_ROOT);
	java.io.File[] resourceFiles = resourceDir.listFiles();
	for (int i = 0; i < resourceFiles.length; i++){
		java.io.File localFile = new java.io.File(workingDir, resourceFiles[i].getName());
		if (!localFile.exists() || localFile.lastModified() < resourceFiles[i].lastModified()) {
			Process copy = Runtime.getRuntime().exec("cmd /c copy "+resourceFiles[i].getAbsolutePath()+" "+localFile.getAbsolutePath()+" /y");
			if (copy.waitFor() != 0) {
				// we might encounter locked service executable(s), most likely ourselves
				needOfflineUpdate = true;
				logWarningEvent("Copying updated resource file "+resourceFiles[i].getAbsolutePath()+" failed");
			} else {
				logInfoEvent("Successfully updated resource file from "+resourceFiles[i].getAbsolutePath());
			}
		}
	}
	if (!needOfflineUpdate) {
		// we successfully updated online
		onlineUpdatePerfomed = true;
	} else {
		if (restartSentinelFile.exists()) {
			// tried already unsuccessfully
			throw new Exception("Required resource update failed even by external attempt; possibly locked files...; initialization aborts");
		} else {
			// setup an offline update try
			restartSentinelFile.createNewFile();
			Process makeLocalCopy = Runtime.getRuntime().exec("cmd /c copy "+resourceDir.getAbsolutePath()+"\\* "+workingDir.getAbsolutePath()+"\\temp /y");
			if (makeLocalCopy.waitFor() != 0) {
				throw new Exception("Making temp file copies for update failed");
			}
			// invoke scheduler
			scheduleCommand("cmd /c move /y "+workingDir.getAbsolutePath()+"\\temp\\* "+workingDir.getAbsolutePath(), 2);
			scheduleCommand("net start "+getName(), 3);
			logWarningEvent("Made local copy for updating locked files, a restart has been scheduled in 3 minutes...");
		}
		offlineUpdateScheduled = true;
	}
}
}