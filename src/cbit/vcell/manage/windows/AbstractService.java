package cbit.vcell.manage.windows;
/**
 * Insert the type's description here.
 * Creation date: (6/22/2006 1:35:33 PM)
 * @author: Ion Moraru
 */

/* USE REFLECTION FOR ALL NON-SYSTEM CLASSES SO WE HAVE ONLY RUN-TIME LOADING OF VCELL RESOURCES */
 
public abstract class AbstractService extends com.excelsior.service.WinService {

	protected boolean initSuccess = false;
	
	protected Throwable runThreadError = null;
	protected boolean runThreadFinished = false;

	public final static int STOP_SERVICE = 1001;
	public final static int STOP_SHUTDOWN = 1002;
	
/**
 * AbstractWorkingService constructor comment.
 */
protected AbstractService() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (6/26/2006 1:01:50 PM)
 */
private static StringBuffer getCommandOutput(String command, int bufsize) throws java.io.IOException {
	Process p = Runtime.getRuntime().exec(command);
	java.io.InputStreamReader io = new java.io.InputStreamReader(p.getInputStream());
	boolean cmdRunning = true;
	StringBuffer buf = new StringBuffer("");
	char[] chars = new char[bufsize];
	while (cmdRunning) {
		try {
			int num = io.read(chars);
			if (num>0) buf.append(chars,0,num);
		} catch (java.io.IOException ex) {}
		try {
			p.exitValue();
			cmdRunning = false;
		} catch (IllegalThreadStateException e) {}
	}
	return buf;
}


/**
 * Insert the method's description here.
 * Creation date: (6/23/2006 4:43:10 PM)
 * @exception java.lang.Exception The exception description.
 */
public static String[] getDependentServices(String serviceName) throws Exception {
	// query services
	int bufsize = 10000; // enough for about 70 services or so...
	StringBuffer buf = getCommandOutput("sc EnumDepend "+serviceName+" "+bufsize, bufsize);
	// parse info
	java.util.Vector v = new java.util.Vector();
	String queryOutput = buf.toString();
	java.util.StringTokenizer tokens = new java.util.StringTokenizer(queryOutput, ":\t\n\r\f"); // do not use space as delimiter so we can deal with names containing spaces
	while (tokens.hasMoreTokens()) {
		if (tokens.nextToken().equals("SERVICE_NAME")) {
			v.add(tokens.nextToken().trim());
		}
	}
	String [] services = new String[v.size()];
	v.copyInto(services);
	return services;
}
		
			
	/**
 * Insert the method's description here.
 * Creation date: (6/23/2006 12:45:46 PM)
 * @return java.lang.String
 */
public abstract String getName();


/**
 * Insert the method's description here.
 * Creation date: (6/20/2006 1:22:54 PM)
 * @return boolean
 */
protected final boolean init() {
	try {
		initialize();
		logInfoEvent(getName()+" service initialization success");
		initSuccess = true;
	} catch (Throwable exc) {
		logErrorEvent(getName()+" service initialization failed with error: "+exc);
	}
	// does not support pause/resume
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (6/23/2006 12:42:16 PM)
 */
abstract void initialize() throws Throwable;


/**
 * Insert the method's description here.
 * Creation date: (6/23/2006 4:43:10 PM)
 * @exception java.lang.Exception The exception description.
 */
public static boolean isServiceRunning(String serviceName) throws Exception {
	// get list of current running services
	String serviceList = getCommandOutput("net start", 10000).toString();
	// parse list
	java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.StringReader(serviceList));
	String line = reader.readLine();
	while (line != null) {
		if (line.trim().equalsIgnoreCase(serviceName)) {
			return true; // we found it...
		}
		line = reader.readLine();
	}
	return false;
}
		
			
	/**
 * Insert the method's description here.
 * Creation date: (6/20/2006 1:23:05 PM)
 */
protected final void run() {
	if (!initSuccess) return; // don't try to start in a bad state...
	try {
		startWork();
		logWarningEvent(getName()+" service run thread finished without throwing an exception... service will stop");
	} catch (Throwable exc) {
		runThreadError = exc; // cleanup thread may want to know what happened...
		logErrorEvent(getName()+" terminated with error: "+exc);
	} finally {
		runThreadFinished = true; // so we know stop was not initiated by CRH
 		stop();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/25/2006 2:39:52 AM)
 * @param cmd java.lang.String
 * @param minutesFromNow int
 * @exception java.lang.Exception The exception description.
 */
public static void scheduleCommand(String cmd, int minutesFromNow) throws java.lang.Exception {
	java.util.Calendar c = java.util.Calendar.getInstance();
	c.add(java.util.Calendar.MINUTE, minutesFromNow);
	int hour = c.get(java.util.Calendar.HOUR_OF_DAY);
	int minute = c.get(java.util.Calendar.MINUTE);
	String restartcmd = "cmd /c at "+hour+":"+minute+" "+cmd;
	Process process = Runtime.getRuntime().exec(restartcmd);
	if (process.waitFor() != 0) {
		throw new Exception("exitValue="+process.exitValue());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/20/2006 1:23:18 PM)
 */
protected final void shutdown() {
	try {
		logWarningEvent(getName()+" shutdown() called, invoking cleanup routine");
		stopWork(STOP_SHUTDOWN);
		logInfoEvent(getName()+" service cleanup successful");
	} catch (Throwable exc) {
		logErrorEvent(getName()+" service cleanup failed with error: "+exc);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/23/2006 12:42:16 PM)
 */
abstract void startWork() throws Throwable;


/**
 * Insert the method's description here.
 * Creation date: (6/20/2006 1:23:18 PM)
 */
protected final void stop() {
	try {
		logWarningEvent(getName()+" stop() called, invoking cleanup routine");
		stopWork(STOP_SERVICE);
		logInfoEvent(getName()+" service cleanup successful");
	} catch (Throwable exc) {
		logErrorEvent(getName()+" service cleanup failed with error: "+exc);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/23/2006 1:26:26 PM)
 */
public void stopByCRH(String reason) {
	logWarningEvent("Received request to stop service. Reason supplied is: "+reason);
	String cmd = null;
	try {
		if (!isServiceRunning(getName())) {
			throw new Exception("Can't confirm running service name... getName() returns "+getName());
		}
		cmd = "net stop "+getName()+" /y";
		Process stopcmd = Runtime.getRuntime().exec(cmd);
		stopcmd.waitFor(); // no need to ask for exit value, if we get here, we failed to kill ourselves :)
		throw new Exception("Command execution failed");	
	} catch (Throwable exc) {
		logErrorEvent("OS stop command ["+cmd+"] failed with error: "+exc);
		// too bad...;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/23/2006 12:42:16 PM)
 */
abstract void stopWork(int stopReason) throws Throwable;
}