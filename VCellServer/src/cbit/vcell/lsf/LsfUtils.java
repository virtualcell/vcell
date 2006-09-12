package cbit.vcell.lsf;
import java.util.StringTokenizer;

import cbit.gui.PropertyLoader;
import cbit.util.SessionLog;
import cbit.util.StdoutSessionLog;
import cbit.vcell.messaging.db.VCellServerID;

/**
 * Insert the type's description here.
 * Creation date: (9/25/2003 9:59:32 AM)
 * @author: Fei Gao
 */
public class LsfUtils {
	public static SessionLog lsfLog = new StdoutSessionLog("LSF-Command");
	public static final String ENV_LSF_BINDIR = "LSF_BINDIR";
	public static java.lang.String BINDIR = getEnvVariable(ENV_LSF_BINDIR);

/**
 * Insert the method's description here.
 * Creation date: (9/25/2003 8:43:41 AM)
 * @return java.lang.String
 * @param keyword java.lang.String
 */
public static String getEnvVariable(String keyword) {
	String value = null;
	String osName = System.getProperty("os.name");
	String command = null;
	
	if (osName.indexOf("Windows") >= 0) {
		command = "cmd.exe /c echo %" + keyword + "%";
	} else {
		command = "echo $" + keyword;
	}
	
	try {
		cbit.util.Executable exe = new cbit.util.Executable(command);
		exe.start();
		value = exe.getStdoutString().trim();
	} catch (Exception e) {
		lsfLog.exception(e); 
	}
	
	return value;
}


/**
 * Insert the method's description here.
 * Creation date: (9/29/2003 10:34:36 AM)
 * @return int
 * @param jobid java.lang.String
 */
public static String getExecutionHost(String jobid) {
	if (BINDIR == null) {
		return null;
	}

	cbit.util.Executable exe = null;
	String host = null;
	
	try {
		String cmd = BINDIR + "\\bhist -l " + jobid;
		exe = new cbit.util.Executable(cmd);
		exe.start();
		
		String output = exe.getStdoutString();
		int index = output.lastIndexOf("Dispatched to");
		String line = null;
		if (index >= 0) {
			line = output.substring(index);
			int i1 = line.indexOf('<');
			int i2 = line.indexOf('>');
			if (i1 >= 0 && i2 >= 0) {
				host = line.substring(i1 + 1, i2); // full host name
				java.util.StringTokenizer tz = new java.util.StringTokenizer(host, ".");
				if (tz.hasMoreTokens()) {
					host = tz.nextToken(); // get simple one
				}
			}
		}
	} catch (cbit.util.ExecutableException ex) {	
		lsfLog.exception(ex);
	}

	return host;
}


/**
 * Insert the method's description here.
 * Creation date: (9/29/2003 10:34:36 AM)
 * @return int
 * @param jobid java.lang.String
 */
public static String getJobExitCode(String jobid) {
	if (BINDIR != null) {
		String cmd = BINDIR + "\\bhist -l " + jobid;
		try {		
			cbit.util.Executable exe = new cbit.util.Executable(cmd);
			exe.start();

			String output = exe.getStdoutString();
			int index = output.indexOf("Exited");
			if (index >= 0) {
				String s = output.substring(index);
				index = s.indexOf(".");
				if (index >= 0) {
					return s.substring(0, index + 1);
				}
			} 
		} catch (cbit.util.ExecutableException ex) {
			lsfLog.exception(ex);
		}
	}

	return "Unknown error";
}


/**
 * Insert the method's description here.
 * Creation date: (9/29/2003 10:34:36 AM)
 * @return int
 * @param jobid java.lang.String
 */
public static int getJobStatus(String jobid) {
	if (BINDIR == null) {
		return LsfConstants.LSF_STATUS_UNKNOWN;
	}
		
	int iStatus = LsfConstants.LSF_STATUS_UNKNOWN;
	cbit.util.Executable exe = null;
	
	try {
		String cmd = BINDIR + "\\bjobs " + jobid;
		exe = new cbit.util.Executable(cmd);
		exe.start();
		
		String output = exe.getStdoutString();
		java.util.StringTokenizer st = new java.util.StringTokenizer(output, "\n");
		String line = st.nextToken();
			
		line = st.nextToken();
		
		st = new java.util.StringTokenizer(line, " ");
		String status = st.nextToken();
		status = st.nextToken();
		status = st.nextToken();
		
		for (iStatus = 0; iStatus < LsfConstants.LSF_JOB_STATUS.length; iStatus ++) {
			if (status.equals(LsfConstants.LSF_JOB_STATUS[iStatus])) {
				return iStatus;
			}
		}
			
		
	} catch (cbit.util.ExecutableException ex) {
		String err = exe.getStderrString();
		String cmd = BINDIR + "\\bhist -l " + jobid;
		try {		
			exe = new cbit.util.Executable(cmd);
			exe.start();

			String output = exe.getStdoutString();
			if (output.indexOf("Done") >= 0) {
				return LsfConstants.LSF_STATUS_DONE;
			} else if (output.indexOf("Exit") >= 0) {
				return LsfConstants.LSF_STATUS_EXITED;
			}
		} catch (cbit.util.ExecutableException ex0) {
			lsfLog.exception(ex0);
			return LsfConstants.LSF_STATUS_UNKNOWN;
		}
	}

	return iStatus;
}


public static final String getLsfQueue() {
	return PropertyLoader.getProperty(PropertyLoader.lsfJobQueue, null);
}


/**
 * Insert the method's description here.
 * Creation date: (2/21/2006 8:59:36 AM)
 * @return int
 */
public static int getPartitionMaximumJobs() {
	return Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.lsfPartitionMaximumJobs));
}


/**
 * Insert the method's description here.
 * Creation date: (2/21/2006 9:01:20 AM)
 * @return cbit.vcell.messaging.db.VCellServerID[]
 */
public static cbit.vcell.messaging.db.VCellServerID[] getPartitionShareServerIDs() {
	try {
		String lsfPartitionShareServerIDs = PropertyLoader.getRequiredProperty(PropertyLoader.lsfPartitionShareServerIDs);
		StringTokenizer st = new StringTokenizer(lsfPartitionShareServerIDs, " ,");
		VCellServerID[] serverIDs = new VCellServerID[st.countTokens() + 1]; // include the current system ServerID
		serverIDs[0] = VCellServerID.getSystemServerID();
		
		int count = 1;
		while (st.hasMoreTokens()) {			
			serverIDs[count] = VCellServerID.getServerID(st.nextToken());
			count ++;			
		}
		return serverIDs;
	} catch (Exception ex) {
		return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/29/2003 10:34:36 AM)
 * @return int
 * @param jobid java.lang.String
 */
public static String getPendingReason(String jobid) {
	if (BINDIR == null) {
		return "";
	}
		
	cbit.util.Executable exe = null;
	String reason = "";
	final String STRING_PENDING_REASONS = "PENDING REASONS:";
	final String STRING_SCHEDULING_PARAMETERS = "SCHEDULING PARAMETERS";
	
	try {
		String cmd = BINDIR + "\\bjobs -l " + jobid;
		exe = new cbit.util.Executable(cmd);
		exe.start();
		
		String output = exe.getStdoutString();
		int index = output.indexOf(STRING_PENDING_REASONS);
		if (index >= 0) {
			String substring = output.substring(index + STRING_PENDING_REASONS.length());
			if (substring.length() > 0) {
				index = substring.indexOf(STRING_SCHEDULING_PARAMETERS);
				reason = substring.substring(0, index).trim();
				return reason;
			}	
		}		
		
	} catch (cbit.util.ExecutableException ex) {
		lsfLog.exception(ex);
	}

	return reason;
}


/**
 * Insert the method's description here.
 * Creation date: (9/29/2003 10:35:12 AM)
 * @param jobid java.lang.String
 */
public static void killJob(String jobid) {
	if (jobid == null || BINDIR == null) {
		return;
	}
		
	try {
		String cmd = BINDIR + "\\bkill " + jobid;
		cbit.util.Executable exe = new cbit.util.Executable(cmd);
		exe.start();
	} catch (cbit.util.ExecutableException ex) {
		lsfLog.exception(ex);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/26/2003 3:06:31 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {		

	//try {
		////cbit.vcell.solvers.MathExecutable mathExecutable = new cbit.vcell.solvers.MathExecutable("d:\\temp\\SimID_4403905", new cbit.vcell.server.StdoutSessionLog("LSF-Command"));	
		////mathExecutable.start();
		//cbit.vcell.server.PropertyLoader.loadProperties();
		//cbit.vcell.server.SessionLog log = new cbit.vcell.server.StdoutSessionLog("LSF-TEST");
		//cbit.vcell.messaging.VCellQueueConnection queueConn = cbit.vcell.messaging.JmsConnectionFactory.getQueueConnection(new cbit.vcell.messaging.SonicMQJmsFactory("localhost", "user1","user1"));

		//cbit.sql.ConnectionFactory connFactory = new cbit.sql.OraclePoolingConnectionFactory(log);
		//cbit.sql.KeyFactory keyFactory = new cbit.sql.OracleKeyFactory();
		//cbit.sql.DBCacheTable dbCacheTable = new cbit.sql.DBCacheTable(1000*60*30);		
		//cbit.vcell.modeldb.DbServerImpl dbServer = new cbit.vcell.modeldb.DbServerImpl(connFactory, keyFactory, dbCacheTable, log);
		//cbit.vcell.server.AdminDatabaseServer adminDbServer = new cbit.vcell.modeldb.LocalAdminDbServer(connFactory,keyFactory,log);
		//cbit.vcell.server.User user = adminDbServer.getUser("fgao15");
		//java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
		
		//while (true) {	
			//cbit.vcell.solver.Simulation sim = (cbit.vcell.solver.Simulation)dbServer.getVersionable(user, cbit.sql.VersionableType.Simulation, new cbit.sql.KeyValue("4403905"));
			//System.out.println(sim.getSimulationIdentifier());		
			
			//cbit.vcell.solvers.LsfSolver solver = new cbit.vcell.solvers.LsfSolver(sim, new java.io.File("\\\\goldfish\\temp\\"), log);			
			//String jobid = solver.submit2Lsf();
			//javax.jms.Message message = queueConn.createObjectMessage(sim);
		
			//message.setStringProperty(cbit.vcell.messaging.MessageConstants.LSFJOBID_PROPERTY, jobid);
			//message.setStringProperty(cbit.vcell.messaging.MessageConstants.WORKERNAME_PROPERTY, "lsfnode");
			//message.setStringProperty(cbit.vcell.messaging.MessageConstants.USERID_PROPERTY, "fgao15");
			//message.setStringProperty(cbit.vcell.lsf.LsfConstants.LSF_STDOUT_FILE, solver.getStdoutFileName());
			//message.setStringProperty(cbit.vcell.lsf.LsfConstants.LSF_STDERR_FILE, solver.getStderrFileName());

			//log.print("Sending Job <" + jobid + "> to LsfMonitor");
			//queueConn.sendMessage(cbit.vcell.lsf.LsfConstants.LSF_JOB_QUEUE, message);
			//System.out.print("Next?");			
			//br.readLine();

			
			//sim = (cbit.vcell.solver.Simulation)dbServer.getVersionable(user, cbit.sql.VersionableType.Simulation, new cbit.sql.KeyValue("4657297"));
			//System.out.println(sim.getSimulationIdentifier());		
			
			//solver = new cbit.vcell.solvers.LsfSolver(sim, new java.io.File("\\\\goldfish\\temp\\"), log);			
			//jobid = solver.submit2Lsf();
			//message = queueConn.createObjectMessage(sim);
		
			//message.setStringProperty(cbit.vcell.messaging.MessageConstants.LSFJOBID_PROPERTY, jobid);
			//message.setStringProperty(cbit.vcell.messaging.MessageConstants.WORKERNAME_PROPERTY, "lsfnode");
			//message.setStringProperty(cbit.vcell.messaging.MessageConstants.USERID_PROPERTY, "fgao15");
			//message.setStringProperty(cbit.vcell.lsf.LsfConstants.LSF_STDOUT_FILE, solver.getStdoutFileName());
			//message.setStringProperty(cbit.vcell.lsf.LsfConstants.LSF_STDERR_FILE, solver.getStderrFileName());
			
			//log.print("Sending Job <" + jobid + "> to LsfMonitor");
			//queueConn.sendMessage(cbit.vcell.lsf.LsfConstants.LSF_JOB_QUEUE, message);
			//System.out.print("Next?");
			//br.readLine();			

		//}
			
		
	//} catch (Exception ex) {
		//ex.printStackTrace();
	//}
}

/**
 * Insert the method's description here.
 * Creation date: (9/25/2003 8:04:51 AM)
 * @param command java.lang.String
 */
public static String submitJob(String job) throws cbit.util.ExecutableException {
	return submitJob(job, getLsfQueue());
}


/**
 * Insert the method's description here.
 * Creation date: (9/25/2003 8:04:51 AM)
 * @param command java.lang.String
 */
public static String submitJob(String job, String queuename) throws cbit.util.ExecutableException {
	if (BINDIR == null) {
		return null;
	}
		
	String completeCommand =  BINDIR + "\\bsub";
	if (queuename != null && queuename.length() > 0) {
		completeCommand += " -q " + queuename;
	}
	completeCommand += " " + job;
	cbit.util.Executable exe = new cbit.util.Executable(completeCommand, 20 * cbit.vcell.messaging.MessageConstants.SECOND);
	exe.start();
	String output = exe.getStdoutString();
	if (output.indexOf ("submitted to") > 0) { // success submission
		int i1 = output.indexOf('<');
		int i2 = output.indexOf('>');
		if (i1 >= 0 && i2 >= 0) {
			return output.substring(i1 + 1, i2);
		}
	}
	return null;
}
}