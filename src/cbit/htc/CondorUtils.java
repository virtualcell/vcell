package cbit.htc;
import static cbit.htc.CondorConstants.*;

import org.vcell.util.Executable;
/**
 * Insert the type's description here.
 * Creation date: (9/25/2003 9:59:32 AM)
 * @author: Fei Gao
 */
public class CondorUtils {
	private static cbit.vcell.server.SessionLog condorLog = new cbit.vcell.server.StdoutSessionLog("Condor-Command");

/**
 * Insert the method's description here.
 * Creation date: (9/29/2003 10:34:36 AM)
 * @return int
 * @param jobid java.lang.String
 */
public static int getJobStatus(String jobid) {		
	int iStatus = CONDOR_STATUS_UNKNOWN;
	org.vcell.util.Executable exe = null;
	String line = null;
	
	try {
		String[] cmd = new String[]{JOB_CMD_STATUS, jobid};
		exe = new org.vcell.util.Executable(cmd);
		exe.start();
		
		String output = exe.getStdoutString();
		java.util.StringTokenizer st = new java.util.StringTokenizer(output, "\n");
		while (st.hasMoreTokens()) {
			line = st.nextToken().trim();		
			if (line.startsWith(jobid)) {	
				java.util.StringTokenizer st1 = new java.util.StringTokenizer(line, " ");
				String status = st1.nextToken();
				status = st1.nextToken();
				status = st1.nextToken();
				status = st1.nextToken();
				status = st1.nextToken();
				status = st1.nextToken();
				
				for (iStatus = 0; iStatus < CONDOR_JOB_STATUS.length; iStatus ++) {
					if (status.equals(CONDOR_JOB_STATUS[iStatus])) {
						return iStatus;
					}
				}
				break;
			}	
		}		
		
	} catch (org.vcell.util.ExecutableException ex) {
	}

	int exitCode = 0;
	
	if (iStatus == CONDOR_STATUS_UNKNOWN) {
		String[] cmd = new String[]{JOB_CMD_HISTORY, "-l", jobid};
		try {		
			exe = new org.vcell.util.Executable(cmd);
			exe.start();

			String output = exe.getStdoutString();
			java.util.StringTokenizer st = new java.util.StringTokenizer(output, "\n");
			while (st.hasMoreTokens()) {
				line = st.nextToken().trim();
				if (line.startsWith("ExitCode")) {
					java.util.StringTokenizer st1 = new java.util.StringTokenizer(line, " ");
					String status = st1.nextToken();
					status = st1.nextToken();
					status = st1.nextToken();
					exitCode = Integer.parseInt(status);					
				} else 	if (line.startsWith("JobStatus")) {	
					java.util.StringTokenizer  st2 = new java.util.StringTokenizer(line, " ");
					String status = st2.nextToken();
					status = st2.nextToken();
					status = st2.nextToken();
					iStatus = Integer.parseInt(status);
				} 
			}		
		} catch (org.vcell.util.ExecutableException ex0) {
			condorLog.exception(ex0);
			return CONDOR_STATUS_UNKNOWN;
		}
	}

	if (iStatus == CONDOR_STATUS_COMPLETED && exitCode != 0) {
		return CONDOR_STATUS_EXITED;
	}					

	return iStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (9/29/2003 10:34:36 AM)
 * @return int
 * @param jobid java.lang.String
 */
public static String getPendingReason(String jobid) {
	org.vcell.util.Executable exe = null;
	String reason = "";
	
	try {
		String[] cmd = new String[]{JOB_CMD_STATUSDETAILS, "-analyze", jobid};
		exe = new Executable(cmd);
		exe.start();
		
		String output = exe.getStdoutString();
		int index = output.indexOf(jobid);
		if (index >= 0) {
			reason = output.substring(index);
			return reason;
		}		
		
	} catch (org.vcell.util.ExecutableException ex) {
		condorLog.exception(ex);
	}

	return reason;
}


/**
 * Insert the method's description here.
 * Creation date: (9/29/2003 10:35:12 AM)
 * @param jobid java.lang.String
 */
public static void killJob(String jobid) {
	if (jobid == null) {
		return;
	}
		
	try {
		String[] cmd = new String[]{JOB_CMD_DELETE, jobid};
		Executable exe = new Executable(cmd);
		exe.start();
	} catch (org.vcell.util.ExecutableException ex) {
		condorLog.exception(ex);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/26/2003 3:06:31 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {		
	
	try {
		int status = CondorUtils.getJobStatus("15");
		System.out.println("status=" + CONDOR_JOB_STATUS[status]);
		status = CondorUtils.getJobStatus("89");
		System.out.println("status=" + CONDOR_JOB_STATUS[status]);
		status = CondorUtils.getJobStatus("91");
		System.out.println("status=" + CONDOR_JOB_STATUS[status]);

		//CondorUtils.killJob(jobid);		
	} catch (Exception ex) {
		ex.printStackTrace();
	}
}

/**
 * Insert the method's description here.
 * Creation date: (9/25/2003 8:04:51 AM)
 * @param command java.lang.String
 */
public static String submitJob(String computeResource, String sub_file, String executable, String cmdArguments) throws org.vcell.util.ExecutableException {		

	try {	
		java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(HTCUtils.getJobSubmitTemplate(computeResource)));
		java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileOutputStream(sub_file));
		pw.println();
		pw.println("executable = " + executable);
		pw.println("arguments = " + cmdArguments);
		pw.println();
		pw.println("#log = " + executable + ".condor.log");
		pw.println("#input = " + executable + ".in");
		pw.println("#output = " + executable + ".out");
		pw.println("#error = " + executable + ".err");
		while (true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
			pw.println(line);
		}
		pw.close();
		br.close();
	} catch (java.io.IOException ex) {
		ex.printStackTrace(System.out);
		return null;
	}
	
	String[] completeCommand =  new String[] {JOB_CMD_SUBMIT, sub_file};
	String jobid = null;
	org.vcell.util.Executable exe = new Executable(completeCommand);
	exe.start();
	String output = exe.getStdoutString();
	String typicalWords = "submitted to cluster";
	int index1  = output.indexOf (typicalWords);
	if (index1 > 0) { // success submission
		jobid = output.substring(index1 + typicalWords.length()).trim();
		int index2 = jobid.lastIndexOf('.');
		jobid = jobid.substring(0, index2) ;
	}
	return jobid;
}
}