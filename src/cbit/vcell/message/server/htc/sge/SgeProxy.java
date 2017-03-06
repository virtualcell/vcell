package cbit.vcell.message.server.htc.sge;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.vcell.util.ExecutableException;
import org.vcell.util.FileUtils;
import org.vcell.util.PropertyLoader;

import cbit.util.xml.XmlUtil;
import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.cmd.CommandService.CommandOutput;
import cbit.vcell.message.server.htc.HtcException;
import cbit.vcell.message.server.htc.HtcJobID;
import cbit.vcell.message.server.htc.HtcJobNotFoundException;
import cbit.vcell.message.server.htc.HtcJobStatus;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.tools.PortableCommand;
import cbit.vcell.tools.PortableCommandWrapper;

public class SgeProxy extends HtcProxy {
	private final static int QDEL_JOB_NOT_FOUND_RETURN_CODE = 1;
	private final static String QDEL_UNKNOWN_JOB_RESPONSE = "does not exist";
	protected final static String SGE_SUBMISSION_FILE_EXT = ".sge.sub";

	// note: full commands use the PropertyLoader.htcPbsHome path.
	private final static String JOB_CMD_SUBMIT = "qsub";
	private final static String JOB_CMD_DELETE = "qdel";
	private final static String JOB_CMD_STATUS = "qstat";
	private final static String JOB_CMD_QACCT = "qacct";

	
	public SgeProxy(CommandService commandService, String htcUser) {
		super(commandService, htcUser);
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

		String SGE_HOME = PropertyLoader.getRequiredProperty(PropertyLoader.htcSgeHome);
		if (!SGE_HOME.endsWith("/")){
			SGE_HOME += "/";
		}

		String[] qstat_cmd = new String[]{SGE_HOME + JOB_CMD_STATUS, "-j", Long.toString(sgeJobID.getJobNumber())};
		
		
		//CommandOutput commandOutput = commandService.command(qstat_cmd,new int[] { 0,1});
		
		//CommandOutput commandOutput = commandService.command(constructShellCommand(commandService, cmdV.toArray(new String[0])), new int[] { 0, 153 });

		CommandOutput commandOutput = commandService.command(constructShellCommand(commandService, qstat_cmd), new int[] { 0, 1 });
		
		HashMap<String,String> outputMap = parseOutput(commandOutput);
		
		if (outputMap == null){
			String[] qacct_cmd = new String[]{SGE_HOME + JOB_CMD_QACCT, "-j", Long.toString(sgeJobID.getJobNumber())};
			commandOutput = commandService.command(qacct_cmd);

			outputMap = parseOutput(commandOutput);
			if (outputMap == null){
				throw new HtcJobNotFoundException("job not found",sgeJobID);
			}else{
				return new HtcJobStatus(SGEJobStatus.EXITED);
			}
		}else{
			return new HtcJobStatus(SGEJobStatus.RUNNING);
		}
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
		
		String SGE_HOME = PropertyLoader.getRequiredProperty(PropertyLoader.htcSgeHome);
		if (!SGE_HOME.endsWith("/")){
			SGE_HOME += "/";
		}

		String[] cmd = new String[]{SGE_HOME + JOB_CMD_DELETE, Long.toString(sgeJobID.getJobNumber())};
		try {
			//CommandOutput commandOutput = commandService.command(cmd, new int[] { 0, QDEL_JOB_NOT_FOUND_RETURN_CODE });
			
			CommandOutput commandOutput = commandService.command(constructShellCommand(commandService, cmd), new int[] { 0, QDEL_JOB_NOT_FOUND_RETURN_CODE });
			
			Integer exitStatus = commandOutput.getExitStatus();
			String standardOut = commandOutput.getStandardOutput();
			if (exitStatus!=null && exitStatus.intValue()==QDEL_JOB_NOT_FOUND_RETURN_CODE && standardOut!=null && standardOut.toLowerCase().contains(QDEL_UNKNOWN_JOB_RESPONSE.toLowerCase())){
				throw new HtcJobNotFoundException(standardOut,sgeJobID);
			}
		}catch (ExecutableException e){
			e.printStackTrace();
			if (!e.getMessage().toLowerCase().contains(QDEL_UNKNOWN_JOB_RESPONSE.toLowerCase())){
				throw e;
			}else{
				throw new HtcJobNotFoundException(e.getMessage(),sgeJobID);
			}
		}
	}
	
	@Override
	protected SgeJobID submitJob(String jobName, String sub_file, String[] command, int ncpus, double memSize, String[] secondCommand, String[] exitCommand, String exitCodeReplaceTag, Collection<PortableCommand> postProcessingCommands) throws ExecutableException {
		if (ncpus > 1) {
			throw new ExecutableException("parallel processing not implemented on " + getClass( ).getName());
		}
		try {

			String htcLogDirString = PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDir);
		    if (!(htcLogDirString.endsWith("/"))){
		    	htcLogDirString = htcLogDirString+"/";
		    }
			
			StringWriter sw = new StringWriter();

		    sw.append("#!/bin/csh\n");
		    sw.append("#$ -N " + jobName + "\n");
		    sw.append("#$ -o " + htcLogDirString+jobName+".sge.log\n");
//			sw.append("#$ -l mem=" + (int)(memSize + SGE_MEM_OVERHEAD_MB) + "mb");

			int JOB_MEM_OVERHEAD_MB = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jobMemoryOverheadMB));

		    long jobMemoryMB = (JOB_MEM_OVERHEAD_MB+((long)memSize));
		    sw.append("#$ -j y\n");
//		    sw.append("#$ -l h_vmem="+jobMemoryMB+"m\n");
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
			if (postProcessingCommands != null) {
				PortableCommandWrapper.insertCommands(sw, postProcessingCommands); 
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

		String SGE_HOME = PropertyLoader.getRequiredProperty(PropertyLoader.htcSgeHome);
		if (!SGE_HOME.endsWith("/")){
			SGE_HOME += "/";
		}
		String[] completeCommand = new String[] {SGE_HOME + JOB_CMD_SUBMIT, "-terse", sub_file};
		CommandOutput commandOutput = commandService.command(constructShellCommand(commandService, completeCommand));
		String jobid = commandOutput.getStandardOutput().trim();
		
		return new SgeJobID(jobid);
	}

	@Override
	public HtcProxy cloneThreadsafe() {
		return new SgeProxy(getCommandService().clone(), getHtcUser());
	}

	@Override
	public String getSubmissionFileExtension() {
		return SGE_SUBMISSION_FILE_EXT;
	}
	
	@Override
	public List<HtcJobID> getRunningJobIDs(String jobNamePrefix) throws ExecutableException {
/*
   6951 0.55500 TEST2_MySe vcell        r     10/10/2012 19:08:34 all.q@compute-4-1.local            1
   6952 0.55500 TEST2_MySe vcell        r     10/10/2012 19:08:34 all.q@compute-0-1.local            1
 */
		String SGE_HOME = PropertyLoader.getRequiredProperty(PropertyLoader.htcSgeHome);
		if (!SGE_HOME.endsWith("/")){
			SGE_HOME += "/";
		}
		String[] cmd = constructShellCommand(commandService, new String[]{SGE_HOME + JOB_CMD_STATUS, "|", "grep", getHtcUser(),"|", "grep", jobNamePrefix,"|","cat"/*compensate grep behaviour*/});
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
	public Map<HtcJobID,HtcJobInfo> getJobInfos(List<HtcJobID> htcJobIDs) throws ExecutableException {
		try{
			HashMap<HtcJobID,HtcJobInfo> jobInfoMap = new HashMap<HtcJobID,HtcJobInfo>();
			for (HtcJobID htcJobID : htcJobIDs){
				HtcJobInfo htcJobInfo = getJobInfo(htcJobID);
				if (htcJobInfo!=null){
					jobInfoMap.put(htcJobID,htcJobInfo);
				}
			}
			return jobInfoMap;
		} catch (Exception e) {
			e.printStackTrace();
			if(e instanceof ExecutableException){
				throw (ExecutableException)e;
			}else{
				throw new ExecutableException("Error getJobInfo: "+e.getMessage());
			}
		}
	}

	public HtcJobInfo getJobInfo(HtcJobID htcJobID) throws ExecutableException {
		Vector<String> cmdV = new Vector<String>();
		String SGE_HOME = PropertyLoader.getRequiredProperty(PropertyLoader.htcSgeHome);
		if (!SGE_HOME.endsWith("/")){
			SGE_HOME += "/";
		}
		cmdV.add(SGE_HOME + JOB_CMD_STATUS);
		cmdV.add("-f");
		cmdV.add("-j");
		cmdV.add(Long.toString(((SgeJobID)htcJobID).getJobNumber()));
		cmdV.add("-xml");
		CommandOutput commandOutput = commandService.command(constructShellCommand(commandService, cmdV.toArray(new String[0])));
		String xmlString = commandOutput.getStandardOutput();
		if (xmlString.contains("unknown_jobs")){
			/**
			 * <unknown_jobs  xmlns:xsd='http://gridengine.sunsource.net/source/browse/checkout/gridengine/source/dist/util/resources/schemas/qstat/qstat.xsd?revision=1.11'>
			 * 		<>
			 * 			<ST_name>12345</ST_name>
			 * 		</>
			 * </unknown_jobs>
			 **/
			return new HtcJobInfo(htcJobID,false,null,null,null);
		}else{
			/**
			 * 
			 * <detailed_job_info  xmlns:xsd="http://gridengine.sunsource.net/source/browse/checkout/gridengine/source/dist/util/resources/schemas/qstat/qstat.xsd?revision=1.11">
			 *    <djob_info>
			 *  	 <element>
			 *  	    <JB_job_number>12345</JB_job_number>
			 *  	    <JB_job_name>S_76915529_0_0</JB_job_name>
			 *          <JB_stdout_path_list>
			 *             <path_list>
			 *                <PN_path>S_76915529_0_0.log</PN_path>
			 *                <PN_host></PN_host>
			 *                <PN_file_host></PN_file_host>
			 *                <PN_file_staging>false</PN_file_staging>
			 *             </path_list>
			 *          </JB_stdout_path_list>
			 *       </element>
			 *    </djob_info>
			 * </detailed_job_info>
			 **/
			Document qstatDoc = XmlUtil.stringToXML(xmlString, null);
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
						return new HtcJobInfo(new SgeJobID(jobID),true,jobName,null, envSublist.getChildText("VA_value")+"/"+outputFile);
					}
				}
			}
		}
		throw new RuntimeException("Error parsing job status for batch job id "+htcJobID.toDatabase());
	}
	
	public String[] getEnvironmentModuleCommandPrefix() {
		ArrayList<String> ar = new ArrayList<String>();
		ar.add("source");
		ar.add("/etc/profile.d/modules.sh;");
		ar.add("module");
		ar.add("load");
		ar.add(PropertyLoader.getProperty(PropertyLoader.sgeModulePath, "htc/sge")+";");
		return ar.toArray(new String[0]);
	}

}
