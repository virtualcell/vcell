/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.htc;

import static cbit.htc.PBSConstants.JOB_CMD_DELETE;
import static cbit.htc.PBSConstants.JOB_CMD_HISTORY;
import static cbit.htc.PBSConstants.JOB_CMD_STATUS;
import static cbit.htc.PBSConstants.JOB_CMD_SUBMIT;
import static cbit.htc.PBSConstants.JOB_EXEC_OK;
import static cbit.htc.PBSConstants.PBS_JOB_EXEC_STATUS;
import static cbit.htc.PBSConstants.PBS_JOB_STATUS;
import static cbit.htc.PBSConstants.PBS_MEM_OVERHEAD_MB;
import static cbit.htc.PBSConstants.PBS_STATUS_EXITING;
import static cbit.htc.PBSConstants.PBS_STATUS_RUNNING;
import static cbit.htc.PBSConstants.PBS_STATUS_UNKNOWN;
import static cbit.htc.PBSConstants.SERVER_CMD_STATUS;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import org.vcell.util.Executable;
import org.vcell.util.ExecutableException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;

public class PBSUtils {
	private static SessionLog pbsLog = new StdoutSessionLog("PBS-Command");
	private static String pbsServer = null;	 
/**
 * Insert the method's description here.
 * Creation date: (9/29/2003 10:34:36 AM)
 * @return int
 * @param jobid java.lang.String
 */
public static String checkServerStatus() throws ExecutableException {
	org.vcell.util.Executable exe = new Executable(new String[] {SERVER_CMD_STATUS, "-B"});
	exe.start();
	
	String output = exe.getStdoutString();
	/*
	Server             Max   Tot   Que   Run   Hld   Wat   Trn   Ext Status
	---------------- ----- ----- ----- ----- ----- ----- ----- ----- -----------
	dll-2-1-1            0     0     0     0     0     0     0     0 Active
	*/	
	StringTokenizer st = new StringTokenizer(output, "\n");	
	st.nextToken();
	st.nextToken();
	String line = st.nextToken();
	st = new StringTokenizer(line, " ");
	pbsServer = st.nextToken();			
	
	return pbsServer;
}

static int getJobExitCode(PbsJobID jobid) {
	/*
	Job: 67.dll-2-1-1

	06/04/2007 10:04:37  S    Job Queued at request of fgao@bigfish.vcell.uchc.edu, owner =
	                          fgao@bigfish.vcell.uchc.edu, job name = test3.sub, queue = workq
	06/04/2007 10:04:37  S    Job Run at request of Scheduler@dll-2-1-1.vcell.uchc.edu on hosts
	                          (dll-2-1-2:ncpus=1)
	06/04/2007 10:04:37  L    Considering job to run
	06/04/2007 10:04:37  A    queue=workq
	06/04/2007 10:04:42  L    Job run
	06/04/2007 10:04:42  A    user=fgao group="RConsole Users" jobname=test3.sub queue=workq ctime=1180965876
	                          qtime=1180965877 etime=1180965877 start=1180965882 exec_host=dll-2-1-2/0
	                          exec_vnode=(dll-2-1-2:ncpus=1) Resource_List.ncpus=1 Resource_List.nodect=1
	                          Resource_List.place=pack Resource_List.select=1:ncpus=1 resource_assigned.ncpus=1
	06/04/2007 10:04:42  S    Job Modified at request of Scheduler@dll-2-1-1.vcell.uchc.edu
	06/04/2007 10:04:42  S    Obit received
	06/04/2007 10:04:42  S    Exit_status=0 resources_used.cput=00:00:00 resources_used.walltime=00:00:00
	06/04/2007 10:04:42  A    user=fgao group="RConsole Users" jobname=test3.sub queue=workq ctime=1180965876
	                          qtime=1180965877 etime=1180965877 start=1180965882 exec_host=dll-2-1-2/0
	                          exec_vnode=(dll-2-1-2:ncpus=1) Resource_List.ncpus=1 Resource_List.nodect=1
	                          Resource_List.place=pack Resource_List.select=1:ncpus=1 session=6872
	                          alt_id=HomeDirectory=Z: end=1180965882 Exit_status=0 resources_used.cput=00:00:00
	                          resources_used.walltime=00:00:00
	06/04/2007 10:04:44  S    Post job file processing error
	 */
	int iExitCode = JOB_EXEC_OK;
	Executable exe = null;
	
	try {
		String[] cmd = new String[] {JOB_CMD_HISTORY, "-p", PropertyLoader.getRequiredProperty(PropertyLoader.pbsHomeDir), jobid.getID()};
		exe = new Executable(cmd);
		exe.start();
		
		String output = exe.getStdoutString();
		final String exitStatus = "Exit_status=";
		int idx = output.indexOf(exitStatus);
		if (idx < 0) {
			throw new RuntimeException("Job [" + jobid + "] : unknown status"); 
		}
		output = output.substring(idx);
		StringTokenizer st = new StringTokenizer(output, " =");
		st.nextToken();
		iExitCode = Integer.parseInt(st.nextToken()); 
		return iExitCode;
		
	} catch (ExecutableException ex) {
		throw new RuntimeException("No job history for job [" + jobid + "]");
	}	
}
/**
 * Insert the method's description here.
 * Creation date: (9/29/2003 10:34:36 AM)
 * @return int
 * @param jobid java.lang.String
 */
public static int getJobStatus(PbsJobID jobid) {		
	int iStatus = PBS_STATUS_UNKNOWN;
	Executable exe = null;
	
	try {
		String[] cmd = new String[]{JOB_CMD_STATUS, "-s", jobid.getID()};
		exe = new Executable(cmd);
		exe.start();
		
		String output = exe.getStdoutString();
		StringTokenizer st = new StringTokenizer(output, "\r\n"); 
		String strStatus = "";
		while (st.hasMoreTokens()) {
			if (st.nextToken().toLowerCase().trim().startsWith("job id")) {
				if (st.hasMoreTokens()) {
					st.nextToken();
				}
				if (st.hasMoreTokens()) {
					strStatus = st.nextToken();
				}
				break;
			}			
		}
		if (strStatus.length() == 0) {
			return iStatus;
		}
		/*
		
		pbssrv: 
		                                                            Req'd  Req'd   Elap
		Job ID          Username Queue    Jobname    SessID NDS TSK Memory Time  S Time
		--------------- -------- -------- ---------- ------ --- --- ------ ----- - -----
		29908.pbssrv    vcell    workqAlp S_32925452  30022   1   1  100mb   --  R 00:29
		   Job run at Mon Apr 27 at 08:28 on (dll-2-6-6:ncpus=1:mem=102400kb)

		*/		
		st = new StringTokenizer(strStatus, " ");
		String token = "";
		for (int i = 0; i < 10 && st.hasMoreTokens(); i ++) {
			token = st.nextToken();
		}
		for (iStatus = 0; iStatus < PBS_JOB_STATUS.length; iStatus ++) {
			if (token.equals(PBS_JOB_STATUS[iStatus])) {
				return iStatus;
			}
		}		
	} catch (ExecutableException ex) {
		return PBS_STATUS_EXITING;
	}
	return iStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (9/29/2003 10:34:36 AM)
 * @return int
 * @param jobid java.lang.String
 */
public static String getPendingReason(PbsJobID jobid) {
	String pendingReason = "unknown pending reason";
	Executable exe = null;
	
	try {
		String[] cmd = new String[]{JOB_CMD_STATUS, "-s", jobid.getID()};
		exe = new Executable(cmd);
		exe.start();
		
		/*
		
		pbssrv: 
		                                                            Req'd  Req'd   Elap
		Job ID          Username Queue    Jobname    SessID NDS TSK Memory Time  S Time
		--------------- -------- -------- ---------- ------ --- --- ------ ----- - -----
		29908.pbssrv    vcell    workqAlp S_32925452  30022   1   1  100mb   --  R 00:29
		   Job run at Mon Apr 27 at 08:28 on (dll-2-6-6:ncpus=1:mem=102400kb)

		*/		
		String output = exe.getStdoutString();
		StringTokenizer st = new StringTokenizer(output, "\r\n"); 
		while (st.hasMoreTokens()) {
			if (st.nextToken().toLowerCase().trim().startsWith("job id")) {
				if (st.hasMoreTokens()) {
					st.nextToken();
				}
				if (st.hasMoreTokens()) {
					st.nextToken();
				}
				pendingReason = "";
				while (st.hasMoreTokens()) {
					pendingReason += st.nextToken();
				}
				break;
			}			
		}
	} catch (ExecutableException ex) {
		ex.printStackTrace(System.out);
	}
	return pendingReason;
}


/**
 * Insert the method's description here.
 * Creation date: (9/29/2003 10:35:12 AM)
 * @param jobid java.lang.String
 */
public static void killJob(PbsJobID jobid) {
	if (jobid == null) {
		return;
	}
		
	try {
		String[] cmd = new String[]{JOB_CMD_DELETE, jobid.getID()};
		Executable exe = new Executable(cmd);
		exe.start();
	} catch (ExecutableException ex) {
		pbsLog.exception(ex);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/26/2003 3:06:31 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {		
	
//	try {		
//		PropertyLoader.loadProperties();
//		
//		PbsJobID jobid = new PbsJobID("29908"); //PBSUtils.submitJob(null, "D:\\PBSPro_Jobs\\test3.sub", "dir", "");
//		int status = PBSUtils.getJobStatus(jobid);
//		System.out.println("jobid=" + jobid);
//		System.out.println("status=" + PBS_JOB_STATUS[status]);
//		System.out.println("pendingreason=" + getPendingReason(jobid));
//		int code = PBSUtils.getJobExitCode(jobid);
//		System.out.println("exitcode=" + code + ":" + PBS_JOB_EXEC_STATUS[-code] + "]");
//	} catch (Exception ex) {
//		ex.printStackTrace();
//	}
	
	
	try {
		PropertyLoader.loadProperties();
		submitServiceJob ("Resource", "T_Db1", "C:\\Users\\eboyce\\Desktop\\testTemplate.sub", "dir", "*.*", 5, 128);
	} catch (ExecutableException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
}





public static PbsJobID submitJob(String computeResource, String jobName, String sub_file, String executable, String cmdArguments, int ncpus, double memSize) throws ExecutableException {
	return submitJob(computeResource, jobName, sub_file, executable, cmdArguments, ncpus, memSize, PBSConstants.PBS_SIMULATION_JOB);
}

public static PbsJobID submitServiceJob(String computeResource, String jobName, String sub_file, String executable, String cmdArguments, int ncpus, double memSize) throws ExecutableException {
	return submitJob(computeResource, jobName, sub_file, executable, cmdArguments, ncpus, memSize, PBSConstants.PBS_SERVICE_JOB);
}

private static PbsJobID submitJob(String computeResource, String jobName, String sub_file, String executable, String cmdArguments, int ncpus, double memSize, int jobCategory) throws ExecutableException {	
	try {	
		PrintWriter pw = new PrintWriter(new FileOutputStream(sub_file));
		//pw.println("# Generated without file template.");
		pw.println("#PBS -N " + jobName);
		pw.println("#PBS -l mem=" + (int)(memSize + PBS_MEM_OVERHEAD_MB) + "mb");
		String pbsQueueNameString = "#PBS -q ";
		String siteNameString = null;
		String siteNAMEString = PropertyLoader.getRequiredProperty(PropertyLoader.vcellServerIDProperty).trim();
		if (siteNAMEString.toUpperCase().equals("ALPHA")) {siteNameString = "Alpha";} 
		else if  (siteNAMEString.toUpperCase().equals("BETA")) {siteNameString = "Beta";} 
		else if  (siteNAMEString.toUpperCase().equals("REL")) {siteNameString = "Rel";} 
		else if  (siteNAMEString.toUpperCase().equals("TEST")) {siteNameString = "Test";} 
		else {
			pw.close();
			throw new ExecutableException("Invalid Server Site ID String: \""+siteNAMEString+"\"");
		}
		
		if (jobCategory==PBSConstants.PBS_SIMULATION_JOB) {
			pbsQueueNameString = pbsQueueNameString+"workq"+siteNameString;
		}
		else if (jobCategory==PBSConstants.PBS_SERVICE_JOB) {
			pbsQueueNameString = pbsQueueNameString+"serviceq"+siteNameString;
		} else {
			pw.close();
			throw new ExecutableException("Invalid jobCategory: "+Integer.toString(jobCategory));
		}
		
		pw.println(pbsQueueNameString);
		pw.print(PBSConstants.PBS_JOB_TEMPLATE);
		pw.println();
		pw.println(executable + " " + cmdArguments);
		pw.close();
	} catch (IOException ex) {
		ex.printStackTrace(System.out);
		return null;
	}
	
	String[] completeCommand = new String[] {JOB_CMD_SUBMIT, sub_file};
	Executable exe = new Executable(completeCommand);
	exe.start();
	String jobid = exe.getStdoutString().trim();
	return new PbsJobID(jobid);
}

public static boolean isJobExiting(int status) {
	return status == PBS_STATUS_EXITING;
}

public static boolean isJobRunning(int status) {
	return status == PBS_STATUS_RUNNING;
}

public static boolean isJobRunning(PbsJobID jobid) {
	return isJobRunning(getJobStatus(jobid));
}

public static boolean isJobExecOK(PbsJobID jobid) {
	return getJobExitCode(jobid) == JOB_EXEC_OK;
}

public static String getJobStatusDescription(int status) {
	return PBSConstants.PBS_JOB_STATUS[status];
}

public static String getJobExecStatus(PbsJobID jobid) {
	int exitCode = getJobExitCode(jobid);
	if (exitCode <= 0) {
		return PBS_JOB_EXEC_STATUS[-exitCode];
	} else {
		return "job was killed with system signal " + exitCode;
	}	
}
}
