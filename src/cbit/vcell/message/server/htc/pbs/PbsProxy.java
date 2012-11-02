package cbit.vcell.message.server.htc.pbs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

import org.vcell.util.ExecutableException;
import org.vcell.util.FileUtils;
import org.vcell.util.PropertyLoader;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.cmd.CommandService.CommandOutput;
import cbit.vcell.message.server.htc.HtcException;
import cbit.vcell.message.server.htc.HtcJobID;
import cbit.vcell.message.server.htc.HtcJobNotFoundException;
import cbit.vcell.message.server.htc.HtcJobStatus;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.messaging.db.SimulationJobStatus;

public final class PbsProxy extends HtcProxy {
	private static final String PBS_SERVICE_QUEUE_PREFIX = "serviceq";
	private static final String PBS_WORK_QUEUE_PREFIX = "workq";
	private final static String UNKNOWN_JOB_ID_QSTAT_RESPONSE = "Unknown Job Id";
	protected final static String PBS_SUBMISSION_FILE_EXT = ".pbs.sub";

	private static String QSTAT_FULL_CLUSTER_COMMAND_PATH = "//cm//shared//apps//torque//2.5.5//bin//qstat";
	
	private final static String JOB_CMD_SUBMIT = "/cm/shared/apps/torque/2.5.5/bin/qsub";
	private final static String JOB_CMD_DELETE = "/cm/shared/apps/torque/2.5.5/bin/qdel";
	private final static String JOB_CMD_STATUS = "/cm/shared/apps/torque/2.5.5/bin/qstat";	
	private final static String JOB_CMD_HISTORY = "/cm/shared/apps/torque/2.5.5/bin/tracejob";
	//public final static String SERVER_CMD_STATUS = "qstat";
	private final static String SERVER_CMD_STATUS = "/cm/shared/apps/torque/2.5.5/bin/qstat";
	private final static int PBS_MEM_OVERHEAD_MB = 70;

	public PbsProxy(CommandService commandService){
		super(commandService);
	}
	
	@Override
	public HtcJobStatus getJobStatus(HtcJobID htcJobId) throws HtcException, ExecutableException {		
		if (!(htcJobId instanceof PbsJobID)){
			throw new HtcException("jobID ("+htcJobId.toDatabase()+") from another queuing system");
		}
		PbsJobID pbsJobID = (PbsJobID)htcJobId;
		
		HtcJobStatus iStatus = null;

		String[] cmd = new String[]{JOB_CMD_STATUS, "-s", pbsJobID.getPbsJobID()};
		CommandOutput commandOutput = commandService.command(cmd);

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
		PBSJobStatus pbsJobStatus = PBSJobStatus.fromPBSCommandLetter(token);
		if (pbsJobStatus!=null){
			return new HtcJobStatus(pbsJobStatus);
		}
		throw new HtcException("unknown PBS status letter '"+token+"'");
	}


	@Override
	public String getPendingReason(HtcJobID htcJobId) throws ExecutableException, HtcException {
		if (!(htcJobId instanceof PbsJobID)){
			throw new HtcException("jobID ("+htcJobId.toDatabase()+") from another queuing system");
		}
		PbsJobID pbsJobID = (PbsJobID)htcJobId;

		String[] cmd = new String[]{JOB_CMD_STATUS, "-s", pbsJobID.getPbsJobID()};
		CommandOutput commandOutput = commandService.command(cmd);

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


	@Override
	public void killJob(HtcJobID htcJobId) throws ExecutableException, HtcException {
		if (!(htcJobId instanceof PbsJobID)){
			throw new HtcException("jobID ("+htcJobId.toDatabase()+") from another queuing system");
		}
		PbsJobID pbsJobID = (PbsJobID)htcJobId;

		String[] cmd = new String[]{JOB_CMD_DELETE, pbsJobID.getPbsJobID()};
		try {
			CommandOutput commandOutput = commandService.command(cmd);
			Integer exitStatus = commandOutput.getExitStatus();
			String standardError = commandOutput.getStandardError();
			if (exitStatus!=null && exitStatus!=0 && standardError!=null && standardError.toLowerCase().contains(UNKNOWN_JOB_ID_QSTAT_RESPONSE.toLowerCase())){
				throw new HtcJobNotFoundException(standardError);
			}
		}catch (ExecutableException e){
			e.printStackTrace();
			if (!e.getMessage().toLowerCase().contains(UNKNOWN_JOB_ID_QSTAT_RESPONSE.toLowerCase())){
				throw e;
			}else{
				throw new HtcJobNotFoundException(e.getMessage());
			}
		}
	}

	@Override
	protected PbsJobID submitJob(String jobName, String sub_file, String[] command, int ncpus, double memSize, HtcJobCategory jobCategory, String[] secondCommand, boolean isServiceJob, String[] exitCommand, String exitCodeReplaceTag) throws ExecutableException{	
		try {
			VCellServerID serverID = VCellServerID.getSystemServerID();

			StringWriter sw = new StringWriter();

			sw.append("# Generated without file template. assuming /bin/bash shell\n");
			sw.append("#PBS -N " + jobName+"\n");
			sw.append("#PBS -l mem=" + (int)(memSize + PBS_MEM_OVERHEAD_MB) + "mb\n");

			switch (jobCategory){
				case HTC_SIMULATION_JOB:{
					String pbsWorkQueueNamePrefix = PropertyLoader.getProperty(PropertyLoader.pbsWorkQueuePrefix, PBS_WORK_QUEUE_PREFIX);
					sw.append("#PBS -q "+pbsWorkQueueNamePrefix + serverID.toCamelCase()+"\n");
					break;
				}
				case HTC_SERVICE_JOB:{
					String pbsServiceQueueNamePrefix = PropertyLoader.getProperty(PropertyLoader.pbsServiceQueuePrefix, PBS_SERVICE_QUEUE_PREFIX);
					sw.append("#PBS -q "+pbsServiceQueueNamePrefix + serverID.toCamelCase()+"\n");
					break;
				}
				default: {
					throw new ExecutableException("Invalid jobCategory: "+jobCategory.name());				
				}
			}

			sw.append("#PBS -m a\n");
			sw.append("#PBS -M schaff@neuron.uchc.edu\n");
			sw.append("#PBS -j oe\n");
			sw.append("#PBS -k oe\n");
			sw.append("#PBS -r n\n");
			sw.append("#PBS -l nice=10\n");
			sw.append("export PATH=/cm/shared/apps/torque/2.5.5/bin/:$PATH\n");
			sw.append("export LD_LIBRARY_PATH=/share/apps/sonic/mq8.5.1:$LD_LIBRARY_PATH\n");
			
			sw.append("echo\n");
			sw.append("echo\n");
			sw.append("echo \"command1 = '"+CommandOutput.concatCommandStrings(command)+"'\"\n");
			sw.append("echo\n");
			sw.append("echo\n");
		    sw.append(CommandOutput.concatCommandStrings(command)+"\n");
		    sw.append("retcode1=$?\n");
		    sw.append("echo\n");
		    sw.append("echo\n");
		    sw.append("echo command1 returned $retcode1\n");
			if (secondCommand!=null){
				sw.append("if [ $retcode1 = 0 ] ; then\n");
				sw.append("		echo\n");
				sw.append("		echo\n");
				sw.append("     echo \"command2 = '"+CommandOutput.concatCommandStrings(secondCommand)+"'\"\n");
				sw.append("		echo\n");
				sw.append("		echo\n");
				sw.append("     "+CommandOutput.concatCommandStrings(secondCommand)+"\n");
				sw.append("     retcode2=$?\n");
				sw.append("		echo\n");
				sw.append("		echo\n");
				sw.append("     echo command2 returned $retcode2\n");
				sw.append("     echo returning return code $retcode2 to PBS\n");
				if (exitCommand!=null && exitCodeReplaceTag!=null){
					sw.append("		echo\n");
					sw.append("		echo\n");
					sw.append("     echo \"exitCommand = '"+CommandOutput.concatCommandStrings(exitCommand).replace(exitCodeReplaceTag,"$retcode2")+"'\"\n");
					sw.append("		echo\n");
					sw.append("		echo\n");
					sw.append("     "+CommandOutput.concatCommandStrings(exitCommand).replace(exitCodeReplaceTag,"$retcode2")+"\n");
					sw.append("		echo\n");
					sw.append("		echo\n");
				}
				sw.append("     exit $retcode2\n");
				sw.append("else\n");
				sw.append("		echo \"command1 failed, skipping command2\"\n");
				sw.append("     echo returning return code $retcode1 to PBS\n");
				if (exitCommand!=null && exitCodeReplaceTag!=null){
					sw.append("		echo\n");
					sw.append("		echo\n");
					sw.append("     echo \"exitCommand = '"+CommandOutput.concatCommandStrings(exitCommand).replace(exitCodeReplaceTag,"$retcode1")+"'\"\n");
					sw.append("		echo\n");
					sw.append("		echo\n");
					sw.append("     "+CommandOutput.concatCommandStrings(exitCommand).replace(exitCodeReplaceTag,"$retcode1")+"\n");
					sw.append("		echo\n");
					sw.append("		echo\n");
				}
				sw.append("     exit $retcode1\n");
				sw.append("fi\n");
			}else{
				sw.append("     echo returning return code $retcode1 to PBS\n");
				if (exitCommand!=null && exitCodeReplaceTag!=null){
					sw.append("		echo\n");
					sw.append("		echo\n");
					sw.append("     echo \"exitCommand = '"+CommandOutput.concatCommandStrings(exitCommand).replace(exitCodeReplaceTag,"$retcode1")+"'\"\n");
					sw.append("		echo\n");
					sw.append("		echo\n");
					sw.append("     "+CommandOutput.concatCommandStrings(exitCommand).replace(exitCodeReplaceTag,"$retcode1")+"\n");
					sw.append("		echo\n");
					sw.append("		echo\n");
				}
				sw.append("     exit $retcode1\n");
			}
			
			File tempFile = File.createTempFile("tempSubFile", ".sub");

			writeUnixStyleTextFile(tempFile, sw.getBuffer().toString());
			
			// move submission file to final location (either locally or remotely).
			System.out.println("<<<SUBMISSION FILE>>> ... moving local file '"+tempFile.getAbsolutePath()+"' to remote file '"+sub_file+"'");
			commandService.pushFile(tempFile,sub_file);
			System.out.println("<<<SUBMISSION FILE START>>>\n"+FileUtils.readFileToString(tempFile)+"\n<<<SUBMISSION FILE END>>>\n");
			tempFile.delete();
		} catch (IOException ex) {
			ex.printStackTrace(System.out);
			return null;
		}

		String[] completeCommand = new String[] {JOB_CMD_SUBMIT, sub_file};
		CommandOutput commandOutput = commandService.command(completeCommand);
		String jobid = commandOutput.getStandardOutput().trim();
		
		if (isServiceJob){
			try {
				commandService.deleteFile(sub_file);
			} catch (IOException e) {
				e.printStackTrace();
				throw new ExecutableException(e.getMessage());
			}
		}
		
		return new PbsJobID(jobid);
	}
	
	@Override
	public PbsProxy cloneThreadsafe() {
		return new PbsProxy(getCommandService().clone());
	}

	@Override
	public String getSubmissionFileExtension() {
		return PBS_SUBMISSION_FILE_EXT;
	}
	
	@Override
	public void checkServerStatus() throws ExecutableException {
		CommandOutput commandOutput = commandService.command(new String[] {SERVER_CMD_STATUS, "-B"});

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
	}
	
	@Override
	public List<HtcJobID> getRunningJobIDs(String jobNamePrefix) throws ExecutableException {
		try {
			String[] cmd = new String[]{JOB_CMD_STATUS, "|", "grep", jobNamePrefix};
			CommandOutput commandOutput = commandService.command(cmd);
			ArrayList<HtcJobID> pbsJobIDs = new ArrayList<HtcJobID>();
			BufferedReader br = new BufferedReader(new StringReader(commandOutput.getStandardOutput()));
			String line = null;
			while((line = br.readLine()) != null){
				StringTokenizer st = new StringTokenizer(line," \t");
				String pbsJobInfo = st.nextToken();
				Integer pbsJobID = new Integer(pbsJobInfo.substring(0,pbsJobInfo.indexOf('.')));
				pbsJobIDs.add(new PbsJobID(String.valueOf(pbsJobID)));
			}
			return pbsJobIDs;
		} catch (Exception e) {
			e.printStackTrace();
			if(e instanceof ExecutableException){
				throw (ExecutableException)e;
			}else{
				throw new ExecutableException("Error getRunningJobs(): "+e.getMessage());
			}
		}
	}

	@Override
	public List<HtcJobInfo> getJobInfos(List<HtcJobID> htcJobIDs) throws ExecutableException {
		try{
			ArrayList<HtcJobInfo> serviceJobInfos = new ArrayList<HtcJobInfo>();
			ArrayList<String> cmdV = new ArrayList<String>();
			cmdV.add(JOB_CMD_STATUS);
			cmdV.add("-f");
			for(HtcJobID htcJobID : htcJobIDs){
				cmdV.add(((PbsJobID)htcJobID).getPbsJobID());
			}
			CommandOutput commandOutput = commandService.command(cmdV.toArray(new String[0]));
			BufferedReader br = new BufferedReader(new StringReader(commandOutput.getStandardOutput()));
			String line = null;
			PbsJobID latestpbsJobID = null;
			String latestJobName = null;
			String latestErrorPath = null;
			while((line = br.readLine()) != null){
				StringTokenizer st = new StringTokenizer(line," \t");
				if(line.startsWith("Job Id:")){
					st.nextToken();st.nextToken();
					latestpbsJobID = new PbsJobID(st.nextToken());
				}else if(latestpbsJobID != null){
					if(line.trim().startsWith("Job_Name =")){
						st.nextToken();st.nextToken();
						latestJobName = st.nextToken();
					}else if(line.trim().startsWith("Error_Path = ")){
						st.nextToken();st.nextToken();
						latestErrorPath = st.nextToken();
					}else if(line.trim().startsWith("Output_Path =")){
						st.nextToken();st.nextToken();
						String latestOutputPath = st.nextToken();
						serviceJobInfos.add(new HtcJobInfo(latestpbsJobID,latestJobName,latestErrorPath,latestOutputPath));
					}
				}
			}
			return serviceJobInfos;
		} catch (Exception e) {
			e.printStackTrace();
			if(e instanceof ExecutableException){
				throw (ExecutableException)e;
			}else{
				throw new ExecutableException("Error getServiceJobIDs: "+e.getMessage());
			}
		}
	}
	
	public ArrayList<RunningPbsJobRecord> getRunningPBSJobs() throws ExecutableException {
		ArrayList<RunningPbsJobRecord> foundRunningPBSJobs = new ArrayList<RunningPbsJobRecord>();

		try{
			
			String[] commandArray = new String[]{QSTAT_FULL_CLUSTER_COMMAND_PATH,"|", "grep "+HTC_SIMULATION_JOB_NAME_PREFIX};
			CommandOutput commandOutput = commandService.command(commandArray);
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
			 		
				 	String substring2 = outputLines[i].substring(outputLines[i].indexOf(HTC_SIMULATION_JOB_NAME_PREFIX));
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

	public class RunningPbsJobRecord {
		private int pbsJobId;
		private String pbsJobName;
		private SimulationJobStatus.SchedulerStatus lastKnownSchedulerStatus = null;   // null means the database knows nothing about it
		
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
		

		public SimulationJobStatus.SchedulerStatus getLastKnownSchedulerStatus() {
			return lastKnownSchedulerStatus;
		}


		public void setSchedulerStatus(
				SimulationJobStatus.SchedulerStatus schedulerStatus) {
			this.lastKnownSchedulerStatus = schedulerStatus;
		}
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

		String[] cmd = new String[] {JOB_CMD_HISTORY, "-p", PropertyLoader.getRequiredProperty(PropertyLoader.pbsHomeDir), jobid.getPbsJobID()};
		CommandOutput commandOutput = commandService.command(cmd);

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


}
