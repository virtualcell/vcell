package cbit.vcell.message.server.htc.sge;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.vcell.util.ExecutableException;
import org.vcell.util.FileUtils;
import org.vcell.util.document.VCellServerID;

import cbit.util.xml.XmlUtil;
import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.cmd.CommandService.CommandOutput;
import cbit.vcell.message.server.htc.HtcException;
import cbit.vcell.message.server.htc.HtcJobID;
import cbit.vcell.message.server.htc.HtcJobNotFoundException;
import cbit.vcell.message.server.htc.HtcJobStatus;
import cbit.vcell.message.server.htc.HtcProxy;

public class SgeProxy extends HtcProxy {
	private final static String QDEL_UNKNOWN_JOB_RESPONSE = "does not exist";
	private final static String QSTAT_UNKNOWN_JOB_RESPONSE = "Following jobs do not exist:";
	protected final static String SGE_SUBMISSION_FILE_EXT = ".sge.sub";

	private final static String JOB_CMD_SUBMIT = "/opt/gridengine/bin/lx26-amd64/qsub";
	private final static String JOB_CMD_DELETE = "/opt/gridengine/bin/lx26-amd64/qdel";
	private final static String JOB_CMD_STATUS = "/opt/gridengine/bin/lx26-amd64/qstat";
	private final static String JOB_CMD_QACCT = "/opt/gridengine/bin/lx26-amd64/qacct";
	private final static int SGE_MEM_OVERHEAD_MB = 70;

	
	public SgeProxy(CommandService commandService) {
		super(commandService);
	}
	

	/**
	 * qstat -j 6892
	 * 
==============================================================
job_number:                 6892
exec_file:                  job_scripts/6892
submission_time:            Tue Oct  9 15:05:44 2012
owner:                      vcell
uid:                        10001
group:                      Domain
gid:                        10000
sge_o_home:                 /home/VCELL/vcell
sge_o_log_name:             vcell
sge_o_path:                 /share/apps/vcell/visit/visit_x86_64/bin:/opt/torque/bin:/sbin:/usr/lib64/qt-3.3/bin:/usr/java/latest/bin:/opt/cuda/bin:/usr/local/bin:/bin:/usr/bin:/usr/local/sbin:/usr/sbin:/sbin:/opt/ganglia/bin:/opt/ganglia/sbin:/opt/bin:/opt/pdsh/bin:/opt/rocks/bin:/opt/rocks/sbin:/opt/gridengine/bin/lx26-amd64
sge_o_shell:                /bin/bash
sge_o_workdir:              /home/VCELL/vcell
sge_o_host:                 sigcluster2
account:                    sge
mail_list:                  vcell@sigcluster2.local
notify:                     FALSE
job_name:                   calculatePi.sh
jobshare:                   0
env_list:
job_args:                   100000
script_file:                calculatePi.sh
scheduling info:            (Collecting of scheduler job information is turned off)

	 */
	
	/**
	 * qstat -j 6892
	 * 
Following jobs do not exist:
6892

	 */
	
	/**
	 * qacct -j 6894
	 * 
error: job id 6894 not found

	 */
	
	
	/**
	 * qacct -j 6892
	 * 
==============================================================
qname        all.q
hostname     compute-5-0.local
group        Domain
owner        vcell
project      NONE
department   defaultdepartment
jobname      calculatePi.sh
jobnumber    6892
taskid       undefined
account      sge
priority     0
qsub_time    Tue Oct  9 15:05:44 2012
start_time   Tue Oct  9 15:05:49 2012
end_time     Tue Oct  9 15:05:52 2012
granted_pe   NONE
slots        1
failed       0
exit_status  1
ru_wallclock 3
ru_utime     0.215
ru_stime     0.103
ru_maxrss    2116
ru_ixrss     0
ru_ismrss    0
ru_idrss     0
ru_isrss     0
ru_minflt    23740
ru_majflt    1
ru_nswap     0
ru_inblock   296
ru_oublock   24
ru_msgsnd    0
ru_msgrcv    0
ru_nsignals  0
ru_nvcsw     334
ru_nivcsw    63
cpu          0.318
mem          0.000
io           0.000
iow          0.000
maxvmem      49.582M
arid         undefined
	 * @throws HtcException 

	 */
	
	private HashMap<String, String> parseOutput(CommandOutput commandOutput) throws HtcException{
		
		HashMap<String,String> map = new HashMap<String, String>();
		
		if (commandOutput.getStandardError().length()>0){
			StringTokenizer lineTokens = new StringTokenizer(commandOutput.getStandardError(), "\r\n");
			if (!lineTokens.hasMoreTokens()){
				throw new RuntimeException("no output to parse");
			}
			String line = lineTokens.nextToken();
			if (line.contains("Following jobs do not exist:")){
				return null;
			}else if (line.contains("error: job id") && line.contains("not found")){
				return null;
			}else{
				throw new HtcException("can't interpret command output");
			}
		}else if (commandOutput.getStandardOutput().length()>0){
			StringTokenizer lineTokens = new StringTokenizer(commandOutput.getStandardOutput(), "\r\n");
			if (!lineTokens.hasMoreTokens()){
				throw new RuntimeException("no output to parse");
			}
			String line = lineTokens.nextToken();
			if (line.startsWith("========")){
				while (lineTokens.hasMoreTokens()) {
					line = lineTokens.nextToken();
					StringTokenizer tokens = new StringTokenizer(line," ");
					String key = tokens.nextToken();
					String value = line.substring(key.length()).trim();
					map.put(key, value);
				}
				return map;
			}else{
				throw new RuntimeException("unexpected command output: "+line);
			}
		}else{
			throw new RuntimeException("no output to parse");
		}
	}

	@Override
	public HtcJobStatus getJobStatus(HtcJobID htcJobId) throws HtcException, ExecutableException {
		if (!(htcJobId instanceof SgeJobID)){
			throw new HtcException("jobID ("+htcJobId.toDatabase()+") from another queuing system");
		}
		SgeJobID sgeJobID = (SgeJobID)htcJobId;

		HtcJobStatus iStatus = null;

		String[] qstat_cmd = new String[]{JOB_CMD_STATUS, "-j", sgeJobID.getSgeJobID()};
		CommandOutput commandOutput = commandService.command(qstat_cmd);

		HashMap<String,String> outputMap = parseOutput(commandOutput);
		
		if (outputMap == null){
			String[] qacct_cmd = new String[]{JOB_CMD_QACCT, "-j", sgeJobID.getSgeJobID()};
			commandOutput = commandService.command(qacct_cmd);

			outputMap = parseOutput(commandOutput);
			if (outputMap == null){
				throw new HtcJobNotFoundException("job not found");
			}else{
				return new HtcJobStatus(SGEJobStatus.EXITED);
			}
		}else{
			return new HtcJobStatus(SGEJobStatus.RUNNING);
		}
	}

	@Override
	public String getPendingReason(HtcJobID htcJobId) throws ExecutableException, HtcException {
		return "unknown status";
	}

	
	/**
	 * qdel 6894
	 * 
vcell has registered the job 6894 for deletion
	 *
	 * qdel 6894
	 * 
job 6894 is already in deletion
	 *
	 * qdel 6894
	 * 
denied: job "6894" does not exist

	 */
	
	
	@Override
	public void killJob(HtcJobID htcJobId) throws ExecutableException, HtcException {
		if (!(htcJobId instanceof SgeJobID)){
			throw new HtcException("jobID ("+htcJobId.toDatabase()+") from another queuing system");
		}
		SgeJobID sgeJobID = (SgeJobID)htcJobId;

		String[] cmd = new String[]{JOB_CMD_DELETE, sgeJobID.getSgeJobID()};
		try {
			CommandOutput commandOutput = commandService.command(cmd);
			Integer exitStatus = commandOutput.getExitStatus();
			String standardError = commandOutput.getStandardError();
			if (exitStatus!=null && exitStatus!=0 && standardError!=null && standardError.toLowerCase().contains(QDEL_UNKNOWN_JOB_RESPONSE.toLowerCase())){
				throw new HtcJobNotFoundException(standardError);
			}
		}catch (ExecutableException e){
			e.printStackTrace();
			if (!e.getMessage().toLowerCase().contains(QDEL_UNKNOWN_JOB_RESPONSE.toLowerCase())){
				throw e;
			}else{
				throw new HtcJobNotFoundException(e.getMessage());
			}
		}
	}
	
	@Override
	protected SgeJobID submitJob(String jobName, String sub_file, String[] command, int ncpus, double memSize, HtcJobCategory jobCategory, String[] secondCommand, boolean isServiceJob, String[] exitCommand, String exitCodeReplaceTag) throws ExecutableException {
		try {
			VCellServerID serverID = VCellServerID.getSystemServerID();

			StringWriter sw = new StringWriter();

		    sw.append("#!/bin/csh\n");
		    sw.append("#$ -N " + jobName + "\n");
		    sw.append("#$ -o " + jobName+".log\n");
//			sw.append("#$ -l mem=" + (int)(memSize + SGE_MEM_OVERHEAD_MB) + "mb");

			//
			// specify the queue to run on ... currently using the default queue
			//
			
//			switch (jobCategory){
//				case HTC_SIMULATION_JOB:{
//					String sgeWorkQueueNamePrefix = PropertyLoader.getProperty(PropertyLoader.pbsWorkQueuePrefix, SGE_WORK_QUEUE_PREFIX);
//					sw.append("#$ -q "+sgeWorkQueueNamePrefix + serverID.toCamelCase()+"\n");
//					break;
//				}
//				case HTC_SERVICE_JOB:{
//					String sgeServiceQueueNamePrefix = PropertyLoader.getProperty(PropertyLoader.pbsServiceQueuePrefix, SGE_SERVICE_QUEUE_PREFIX);
//					sw.append("#$ -q "+sgeServiceQueueNamePrefix + serverID.toCamelCase()+"\n");
//					break;
//				}
//				default: {
//					throw new ExecutableException("Invalid jobCategory: "+jobCategory.name());				
//				}
//			}

		    sw.append("#$ -j y\n");
		    sw.append("# -cwd\n");
		    sw.append("# the commands to be executed\n");
			sw.append("echo\n");
			sw.append("echo\n");
			sw.append("echo \"command1 = '"+CommandOutput.concatCommandStrings(command)+"'\"\n");
			sw.append("echo\n");
			sw.append("echo\n");
		    sw.append(CommandOutput.concatCommandStrings(command)+"\n");
		    sw.append("set retcode1 = $status\n");
		    sw.append("echo\n");
		    sw.append("echo\n");
		    sw.append("echo command1 returned $retcode1\n");
			if (secondCommand!=null){
				sw.append("if ( $retcode1 == 0 ) then\n");
				sw.append("		echo\n");
				sw.append("		echo\n");
				sw.append("     echo \"command2 = '"+CommandOutput.concatCommandStrings(secondCommand)+"'\"\n");
				sw.append("		echo\n");
				sw.append("		echo\n");
				sw.append("     "+CommandOutput.concatCommandStrings(secondCommand)+"\n");
				sw.append("     set retcode2 = $status\n");
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
				sw.append("     echo returning return code $retcode1 to SGE\n");
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
				sw.append("endif\n");
			}else{
				sw.append("echo returning return code $retcode1 to SGE\n");
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
				sw.append("exit $retcode1\n");
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

		String[] completeCommand = new String[] {JOB_CMD_SUBMIT, "-terse", sub_file};
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
		
		return new SgeJobID(jobid);
	}

	@Override
	public HtcProxy cloneThreadsafe() {
		return new SgeProxy(getCommandService().clone());
	}

	@Override
	public String getSubmissionFileExtension() {
		return SGE_SUBMISSION_FILE_EXT;
	}
	
	@Override
	public void checkServerStatus() throws ExecutableException {
		CommandOutput commandOutput = commandService.command(new String[] {JOB_CMD_STATUS, "-f"});

		String output = commandOutput.getStandardOutput();
		/*
queuename                      qtype resv/used/tot. load_avg arch          states
---------------------------------------------------------------------------------
all.q@compute-0-0.local        BIP   0/0/64         0.00     lx26-amd64    
---------------------------------------------------------------------------------
all.q@compute-0-1.local        BIP   0/0/64         0.00     lx26-amd64    
		 */	
		StringTokenizer st = new StringTokenizer(output, "\n");	
		st.nextToken();
		st.nextToken();
		String line = st.nextToken();
		st = new StringTokenizer(line, " ");
		String queueName = st.nextToken();	
		System.out.println("first SGE Queue name : "+queueName);
	}

	@Override
	public List<HtcJobID> getRunningJobIDs(String jobNamePrefix) throws ExecutableException {
/*
   6951 0.55500 TEST2_MySe vcell        r     10/10/2012 19:08:34 all.q@compute-4-1.local            1
   6952 0.55500 TEST2_MySe vcell        r     10/10/2012 19:08:34 all.q@compute-0-1.local            1
 */
		String[] cmd = constructShellCommand(commandService, new String[]{JOB_CMD_STATUS, "|", "grep", jobNamePrefix});
		CommandOutput commandOutput = commandService.command(cmd);
		ArrayList<HtcJobID> serviceJobIDs = new ArrayList<HtcJobID>();
		
		String output = commandOutput.getStandardOutput();
		StringTokenizer st = new StringTokenizer(output, "\r\n"); 
		while (st.hasMoreTokens()) {
			String line = st.nextToken().trim();
			StringTokenizer lineTokens = new StringTokenizer(line," \t");
			serviceJobIDs.add(new SgeJobID(lineTokens.nextToken()));
		}
		return serviceJobIDs;
	}




	@Override
	public List<HtcJobInfo> getJobInfos(List<HtcJobID> htcJobIDs) throws ExecutableException {
		try{
			ArrayList<HtcJobInfo> jobInfos = new ArrayList<HtcJobInfo>();
			//
			// how many to process at once.
			//
			int MAX_NUM_JOBS_IN_QUERY = 20;
			
			ArrayList<HtcJobID> remainingJobIDs = new ArrayList<HtcJobID>(htcJobIDs);
			while (remainingJobIDs.size()>0){
				List<HtcJobID> currentJobIDs = new ArrayList<HtcJobID>(Arrays.asList(remainingJobIDs.subList(0, Math.min(MAX_NUM_JOBS_IN_QUERY,remainingJobIDs.size())).toArray(new HtcJobID[0])));
				remainingJobIDs.removeAll(currentJobIDs);

				Vector<String> cmdV = new Vector<String>();
				cmdV.add(JOB_CMD_STATUS);
				cmdV.add("-f");
				cmdV.add("-j");
				String jobList = "";
				for(HtcJobID htcJobID : currentJobIDs){
					if(jobList.length() != 0){
						jobList+=",";
					}
					jobList+=((SgeJobID)htcJobID).getSgeJobID();
				}
				cmdV.add(jobList);
				cmdV.add("-xml");
				CommandOutput commandOutput = commandService.command(cmdV.toArray(new String[0]));
				Document qstatDoc = XmlUtil.stringToXML(commandOutput.getStandardOutput(), null);
				Element rootElement = qstatDoc.getRootElement();
				Element dbJobInfoElement = rootElement.getChild("djob_info");
				if(dbJobInfoElement == null){
					return null;
				}
				List<Element> qstatInfoChildren = dbJobInfoElement.getChildren("element");
				if(qstatInfoChildren == null){
					return null;
				}
				for(Element jobInfoElement : qstatInfoChildren){
					String jobID = jobInfoElement.getChildText("JB_job_number").trim();
					String jobName =  jobInfoElement.getChildText("JB_job_name").trim();
					String outputFile = jobInfoElement.getChild("JB_stdout_path_list").getChild("path_list").getChildText("PN_path").trim();
					List<Element> envSublists = jobInfoElement.getChild("JB_env_list").getChildren("job_sublist");
					for(Element envSublist : envSublists){
						if(envSublist.getChildText("VA_variable").equals("__SGE_PREFIX__O_WORKDIR")){
							jobInfos.add(new HtcJobInfo(new SgeJobID(jobID),jobName,null, envSublist.getChildText("VA_value")+"/"+outputFile));
							break;
						}
					}
				}
			}
			return jobInfos;
		} catch (Exception e) {
			e.printStackTrace();
			if(e instanceof ExecutableException){
				throw (ExecutableException)e;
			}else{
				throw new ExecutableException("Error getServiceJobIDs: "+e.getMessage());
			}
		}
	}

}
