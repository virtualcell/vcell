package cbit.vcell.messaging.admin;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import org.vcell.util.Executable;

import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.server.PropertyLoader;

/**
 * Insert the type's description here.
 * Creation date: (8/11/2003 11:41:43 AM)
 * @author: Fei Gao
 */
public class ManageUtils {
	private static java.text.SimpleDateFormat dateTimeFormatter = new java.text.SimpleDateFormat(" yyyy_MM_dd 'at' HH-mm-ss", java.util.Locale.US);

/**
 * ManageUtils constructor comment.
 */
public ManageUtils() {
	super();
}

public static String getHostName() {
	try {
		String hostname = cbit.vcell.messaging.admin.ManageUtils.getLocalHostName();
		StringTokenizer st = new StringTokenizer(hostname, ".");
		hostname = st.nextToken(); // abbr hostname
		return hostname;
	} catch (UnknownHostException ex) {
		ex.printStackTrace();
		return "UnknownHost";
	}	 
}

/**
 * Insert the method's description here.
 * Creation date: (10/26/2001 5:49:02 PM)
 * @return boolean
 * @param file java.io.File
 * @param archiveDirectory java.io.File
 */
public static void archiveByDateAndTime(String fileName, String arcDir) {
	try {
		if (fileName == null) {
			return;
		}
		
		java.io.File archiveDirectory = null;
		java.io.File file = new java.io.File(fileName);
			
		if (arcDir == null) {
			archiveDirectory = new java.io.File("." + java.io.File.separator);
		} else {
			archiveDirectory = new java.io.File(arcDir);
		}
		
		archiveDirectory.mkdir(); // in case it isn't there...
		if (file.exists()) {
			String archivedName = file.getName() + dateTimeFormatter.format(new java.util.Date());
			file.renameTo(new java.io.File(archiveDirectory, archivedName));
		}
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.ProcessStatus
 * @exception java.rmi.RemoteException The exception description.
 */
public static ServerPerformance getDaemonPerformance() {
	try {
		String PROGRAM = null;
		try {
			PROGRAM = System.getProperty(cbit.vcell.server.PropertyLoader.serverStatisticsProperty);
		} catch (Exception e){
			throw new RuntimeException("required System property \""+cbit.vcell.server.PropertyLoader.serverStatisticsProperty+"\" not defined");
		}

		long memoryBytes = -1;
		float fractionCPU = 0.9999999f;
		long javaFreeMemoryBytes = Runtime.getRuntime().freeMemory();
		long javaTotalMemoryBytes = Runtime.getRuntime().totalMemory();
		long maxJavaMemoryBytes = -1;
		try {
			maxJavaMemoryBytes = Long.parseLong(PropertyLoader.getRequiredProperty(cbit.vcell.server.PropertyLoader.maxJavaMemoryBytesProperty));
		}catch (NumberFormatException e){
			System.out.println("error reading property '"+cbit.vcell.server.PropertyLoader.maxJavaMemoryBytesProperty+"', "+e.getMessage());
		}
		try {
			Executable executable = new Executable(new String[]{PROGRAM});
			executable.start();
			String stdout = executable.getStdoutString();
			java.util.StringTokenizer tokens = new java.util.StringTokenizer(stdout);
			memoryBytes = Long.parseLong(tokens.nextToken());
			int cpuPercent = Integer.parseInt (tokens.nextToken());
			fractionCPU = cpuPercent/100.0f;
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

		return new ServerPerformance(fractionCPU,memoryBytes,javaFreeMemoryBytes,javaTotalMemoryBytes,maxJavaMemoryBytes);
	} catch (Throwable e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/25/2003 8:43:41 AM)
 * @return java.lang.String
 * @param keyword java.lang.String
 */
public static String getEnvVariable(String keyword, cbit.vcell.server.SessionLog log) {
	String value = null;
	String[] command = null;
	
	if (ResourceUtil.bWindows) {
		command = new String[]{"cmd.exe", "/c" , "echo %" + keyword + "%"};
	} else {
		command = new String[]{"echo", "$" + keyword};
	}
	
	try {
		Executable exe = new Executable(command);
		exe.start();
		value = exe.getStdoutString().trim();
	} catch (Exception e) {
		log.exception(e);
	}
	
	return value;
}


/**
 * Insert the method's description here.
 * Creation date: (12/3/2003 9:32:25 AM)
 * @return java.lang.String
 */
public static String getFullLocalHostName() throws java.net.UnknownHostException {
	java.net.InetAddress inet = java.net.InetAddress.getLocalHost();	
	String hostName = java.net.InetAddress.getByName(inet.getHostAddress()).getHostName();
	return hostName;
}


/**
 * Insert the method's description here.
 * Creation date: (12/3/2003 9:32:25 AM)
 * @return java.lang.String
 */
public static String getLocalHostName() throws java.net.UnknownHostException {
	String hostName = java.net.InetAddress.getLocalHost().getHostName();
	if (hostName != null) {
		hostName = hostName.toLowerCase();
	}
	return hostName;
}


/**
 * Insert the method's description here.
 * Creation date: (12/4/2003 7:38:11 AM)
 * @return java.lang.String
 */
public static String readLog(java.io.File file) throws java.io.IOException {
	java.io.FileReader reader = new java.io.FileReader(file);
	char[] content = new char[10000];
	String out = "";
	while (true) {
		int n = reader.read(content, 0, 10000);
		if (n == -1) {
			break;
		} else
			if (n > 0) {
				out += new String(content, 0, n);
			}
	}
	reader.close();
	return out;
}
}