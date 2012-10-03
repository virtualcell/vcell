package cbit.vcell.message.server.pbs;

import static cbit.htc.PBSConstants.JOB_CMD_DELETE;
import static cbit.htc.PBSConstants.JOB_CMD_HISTORY;
import static cbit.htc.PBSConstants.JOB_CMD_STATUS;
import static cbit.htc.PBSConstants.SERVER_CMD_STATUS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.vcell.util.ExecutableException;
import org.vcell.util.FileUtils;
import org.vcell.util.PropertyLoader;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCellServerID;

import cbit.htc.PBSConstants;
import cbit.htc.PBSConstants.PBSJobCategory;
import cbit.htc.PBSConstants.PBSJobExitCode;
import cbit.htc.PBSConstants.PBSJobStatus;
import cbit.htc.PbsJobID;

public abstract class PbsProxy {

	public class PbsJobNotFoundException extends Exception {

		public PbsJobNotFoundException(String message) {
			super(message);
		}

	}
	
	public class RunningPbsJobRecord {
		private int pbsJobId;
		private String pbsJobName;
		
		public RunningPbsJobRecord(int pbsJobIdArg, String pbsJobNameArg){
			pbsJobId = pbsJobIdArg;
			pbsJobName = pbsJobNameArg;
		}
		
		public String getPbsJobName() {
			return pbsJobName;
		}
		
		public KeyValue getSimID() {
			String substring2 = pbsJobName.substring(pbsJobName.indexOf("_")+1);
			return new KeyValue(substring2.substring(0, substring2.indexOf("_")));
		}
		
		public int getSimJobIndex(){
			String substring2 = pbsJobName.substring(pbsJobName.indexOf("_")+1);
			String substring3 = substring2.substring(substring2.indexOf("_")+1);
			int jobindex = Integer.valueOf(substring3);
			return jobindex;
		}
		
		public int getPbsJobId(){
			return pbsJobId;
		}
	
		
		
	}

	public static class CommandOutput {
		private String[] commandStrings;
		private String standardOutput;
		private String standardError;
		private Integer exitStatus;
		private long elapsedTimeMS;

		public CommandOutput(String[] commandStrings, String standardOutput, String standardError, Integer exitStatus, long elapsedTimeMS) {
			this.commandStrings = commandStrings;
			this.standardOutput = standardOutput;
			this.standardError = standardError;
			this.exitStatus = exitStatus;
			this.elapsedTimeMS = elapsedTimeMS;
		}
		public String[] getCommandStrings() {
			return commandStrings;
		}
		public String getCommand(){
			return concatCommandStrings(commandStrings);
		}
		public String getStandardOutput() {
			return standardOutput;
		}
		public String getStandardError() {
			return standardError;
		}
		public Integer getExitStatus() {
			return exitStatus;
		}
		public long getElapsedTimeMS() {
			return elapsedTimeMS;
		}
		public static String concatCommandStrings(String[] cmdStrings){
			StringBuffer cmd = new StringBuffer();
			for (String cmdStr : cmdStrings){
				cmd.append(cmdStr);
				cmd.append(" ");
			}
			return cmd.toString().trim();
		}
	}


	public PbsProxy(){
	}
	
	@Override
	public abstract PbsProxy clone();


	/**
	 * Insert the method's description here.
	 * Creation date: (9/29/2003 10:34:36 AM)
	 * @return int
	 * @param jobid java.lang.String
	 */
	public String checkServerStatus() throws ExecutableException {
		CommandOutput commandOutput = command(new String[] {SERVER_CMD_STATUS, "-B"});

		String output = commandOutput.getStandardOutput();
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
		String pbsServer = st.nextToken();			

		return pbsServer;
	}

	public PBSJobExitCode getPbsTraceJobExitCode(PbsJobID jobid) throws Exception {
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

		String[] cmd = new String[] {JOB_CMD_HISTORY, "-p", PropertyLoader.getRequiredProperty(PropertyLoader.pbsHomeDir), jobid.getID()};
		CommandOutput commandOutput = command(cmd);

		String output = commandOutput.getStandardOutput();
		final String exitStatus = "Exit_status=";
		int idx = output.indexOf(exitStatus);
		if (idx < 0) {
			throw new RuntimeException("Job [" + jobid + "] : unknown status"); 
		}
		output = output.substring(idx);
		StringTokenizer st = new StringTokenizer(output, " =");
		st.nextToken();
		int retcode = Integer.parseInt(st.nextToken());
		PBSJobExitCode pbsJobExitCode = PBSJobExitCode.fromPBSJobExitCode(retcode);
		if (pbsJobExitCode!=null){
			return pbsJobExitCode;
		}	
		throw new Exception("Unknown PBS tracejob exit code ("+retcode+") for job [" + jobid + "]");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/29/2003 10:34:36 AM)
	 * @return int
	 * @param jobid java.lang.String
	 */
	public PBSJobStatus getJobStatus(PbsJobID jobid) throws Exception {		
		PBSJobStatus iStatus = null;

		String[] cmd = new String[]{JOB_CMD_STATUS, "-s", jobid.getID()};
		CommandOutput commandOutput = command(cmd);

		String output = commandOutput.getStandardOutput();
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
		PBSJobStatus status = PBSJobStatus.fromPBSCommandLetter(token);
		if (status!=null){
			return status;
		}
		throw new Exception("unknown PBS status letter '"+token+"'");
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (9/29/2003 10:34:36 AM)
	 * @return int
	 * @param jobid java.lang.String
	 */
	public String getPendingReason(PbsJobID jobid) throws ExecutableException {

		String[] cmd = new String[]{JOB_CMD_STATUS, "-s", jobid.getID()};
		CommandOutput commandOutput = command(cmd);

		String output = commandOutput.getStandardOutput();

		/*

		pbssrv: 
		                                                            Req'd  Req'd   Elap
		Job ID          Username Queue    Jobname    SessID NDS TSK Memory Time  S Time
		--------------- -------- -------- ---------- ------ --- --- ------ ----- - -----
		29908.pbssrv    vcell    workqAlp S_32925452  30022   1   1  100mb   --  R 00:29
		   Job run at Mon Apr 27 at 08:28 on (dll-2-6-6:ncpus=1:mem=102400kb)

		 */
		String pendingReason = "unknown pending reason";

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
		return pendingReason;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (9/29/2003 10:35:12 AM)
	 * @param jobid java.lang.String
	 */
	public void killJob(PbsJobID jobid) throws ExecutableException, PbsJobNotFoundException {
		if (jobid == null) {
			return;
		}

		String[] cmd = new String[]{JOB_CMD_DELETE, jobid.getID()};
		try {
			CommandOutput commandOutput = command(cmd);
			Integer exitStatus = commandOutput.getExitStatus();
			String standardError = commandOutput.getStandardError();
			if (exitStatus!=null && exitStatus!=0 && standardError!=null && standardError.toLowerCase().contains(PBSConstants.UNKNOWN_JOB_ID_QSTAT_RESPONSE.toLowerCase())){
				throw new PbsJobNotFoundException(standardError);
			}
		}catch (ExecutableException e){
			e.printStackTrace();
			if (!e.getMessage().toLowerCase().contains(PBSConstants.UNKNOWN_JOB_ID_QSTAT_RESPONSE.toLowerCase())){
				throw e;
			}else{
				throw new PbsJobNotFoundException(e.getMessage());
			}
		}
	}


	public PbsJobID submitJob(String computeResource, String jobName, String sub_file, String[] command, int ncpus, double memSize) throws ExecutableException {
		return submitJob(computeResource, jobName, sub_file, command, ncpus, memSize, PBSJobCategory.PBS_SIMULATION_JOB, null, false);
	}

	public PbsJobID submitJob(String computeResource, String jobName, String sub_file, String[] command, String[] secondCommand, int ncpus, double memSize) throws ExecutableException {
		return submitJob(computeResource, jobName, sub_file, command, ncpus, memSize, PBSJobCategory.PBS_SIMULATION_JOB, secondCommand, false);
	}

	public PbsJobID submitServiceJob(String computeResource, String jobName, String sub_file, String[] command, int ncpus, double memSize) throws ExecutableException {
		return submitJob(computeResource, jobName, sub_file, command, ncpus, memSize, PBSJobCategory.PBS_SERVICE_JOB, null, true);
	}

	private PbsJobID submitJob(String computeResource, String jobName, String sub_file, String[] command, int ncpus, double memSize, PBSJobCategory jobCategory, String[] secondCommand, boolean isServiceJob) throws ExecutableException{	
		try {
			VCellServerID serverID = VCellServerID.getSystemServerID();

			File tempFile = File.createTempFile("tempSubFile", ".sub");
			PrintWriter pw = new PrintWriter(new FileOutputStream(tempFile));
			pw.println("# Generated without file template.");
			pw.println("#PBS -N " + jobName);
			pw.println("#PBS -l mem=" + (int)(memSize + PBSConstants.PBS_MEM_OVERHEAD_MB) + "mb");

			switch (jobCategory){
				case PBS_SIMULATION_JOB:{
					String pbsWorkQueueNamePrefix = PropertyLoader.getProperty(PropertyLoader.pbsWorkQueuePrefix, PBSConstants.PBS_WORK_QUEUE_PREFIX);
					pw.println("#PBS -q "+pbsWorkQueueNamePrefix + serverID.toCamelCase());
					break;
				}
				case PBS_SERVICE_JOB:{
					String pbsServiceQueueNamePrefix = PropertyLoader.getProperty(PropertyLoader.pbsServiceQueuePrefix, PBSConstants.PBS_SERVICE_QUEUE_PREFIX);
					pw.println("#PBS -q "+pbsServiceQueueNamePrefix + serverID.toCamelCase());
					break;
				}
				default: {
					pw.close();
					throw new ExecutableException("Invalid jobCategory: "+jobCategory.name());				
				}
			}

			pw.print(PBSConstants.PBS_JOB_TEMPLATE);
			pw.println();
			
			pw.println(CommandOutput.concatCommandStrings(command));
			if (secondCommand!=null){
				String secondCommandString = "if [ \"$?\" = \"0\" ] ; then "+CommandOutput.concatCommandStrings(secondCommand);
				secondCommandString = secondCommandString+" ; fi\n";
				pw.println(secondCommandString);
			}
			pw.close();
			
			// move submission file to final location (either locally or remotely).
			System.out.println("<<<SUBMISSION FILE>>> ... moving local file '"+tempFile.getAbsolutePath()+"' to remote file '"+sub_file+"'");
			pushFile(tempFile,sub_file);
			System.out.println("<<<SUBMISSION FILE START>>>\n"+FileUtils.readFileToString(tempFile)+"\n<<<SUBMISSION FILE END>>>\n");
			tempFile.delete();
		} catch (IOException ex) {
			ex.printStackTrace(System.out);
			return null;
		}

		String[] completeCommand = new String[] {PBSConstants.JOB_CMD_SUBMIT, sub_file};
		CommandOutput output = command(completeCommand);
		String jobid = output.getStandardOutput().trim();
		
		if (isServiceJob){
			try {
				deleteFile(sub_file);
			} catch (IOException e) {
				e.printStackTrace();
				throw new ExecutableException(e.getMessage());
			}
		}
		
		return new PbsJobID(jobid);
	}
	
	public abstract void pushFile(File tempFile, String remotePath) throws IOException;
	
	public abstract void deleteFile(String remoteFilePath) throws IOException;
 
	public abstract CommandOutput command(String[] command) throws ExecutableException;
	
	public ArrayList<RunningPbsJobRecord> getRunningPBSJobs() throws ExecutableException {
		ArrayList<RunningPbsJobRecord> foundRunningPBSJobs = new ArrayList();
		try{
			
			String[] commandArray = new String[]{PBSConstants.QSTAT_FULL_CLUSTER_COMMAND_PATH,"|", "grep "+PBSConstants.PBS_SIMULATION_JOB_NAME_PREFIX};
			CommandOutput commandOutput = command(commandArray);
			if (commandOutput.getExitStatus()==1) {return null;} //because Grep returns code 1 if nothing found
			if (commandOutput.getExitStatus()!=0 || commandOutput.getStandardOutput()==null) {
				throw new ExecutableException("qstat failed.\nExit Status = "+commandOutput.getExitStatus().toString()+"\n"+
						"Standard out = \n"+commandOutput.getStandardOutput()+"\n"+
						"Standard error = \n"+commandOutput.getStandardError());
			}
			String[] outputLines =commandOutput.getStandardOutput().split("\n");
			for (int i=0; i<outputLines.length; i++){
			 	String foundPbsJobID = outputLines[i].substring(0, outputLines[i].indexOf("."));
			 	//Do a sanity check.  Does foundPbsJobID string represent an integer?
			 	try {
			 		
				 	String substring2 = outputLines[i].substring(outputLines[i].indexOf(PBSConstants.PBS_SIMULATION_JOB_NAME_PREFIX));
				 	String foundPbsJobName = substring2.substring(0, substring2.indexOf(" "));
				 	foundRunningPBSJobs.add(new RunningPbsJobRecord(Integer.parseInt(foundPbsJobID), foundPbsJobName));
			 	} catch (NumberFormatException nfe){
			 		throw new ExecutableException("Something's wrong: Non-integer found where an integer PBS Job ID was expected\n"+
							"Standard out = \n"+commandOutput.getStandardOutput()+"\n"+
							"Standard error = \n"+commandOutput.getStandardError());
			 	}

			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new ExecutableException(e.getMessage());
		}
		
		return foundRunningPBSJobs;
	}
}
