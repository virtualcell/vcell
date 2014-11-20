package cbit.vcell.message.server.htc.pbs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.vcell.util.ExecutableException;
import org.vcell.util.FileUtils;
import org.vcell.util.PropertyLoader;

import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.cmd.CommandService.CommandOutput;
import cbit.vcell.message.server.htc.HtcException;
import cbit.vcell.message.server.htc.HtcJobID;
import cbit.vcell.message.server.htc.HtcJobNotFoundException;
import cbit.vcell.message.server.htc.HtcJobStatus;
import cbit.vcell.message.server.htc.HtcProxy;

public final class PbsProxy extends HtcProxy {
	private static final int QDEL_JOB_NOT_FOUND_RETURN_CODE = 153;
	
	private final static String UNKNOWN_JOB_ID_QSTAT_RESPONSE = "Unknown Job Id";
	protected final static String PBS_SUBMISSION_FILE_EXT = ".pbs.sub";
	
	// note: full commands use the PropertyLoader.htcPbsHome path.
	private final static String JOB_CMD_SUBMIT = "qsub";
	private final static String JOB_CMD_DELETE = "qdel";
	private final static String JOB_CMD_STATUS = "qstat";

	public PbsProxy(CommandService commandService, String htcUser){
		super(commandService, htcUser);
	}
	
	@Override
	public HtcJobStatus getJobStatus(HtcJobID htcJobId) throws HtcException, ExecutableException {		
		if (!(htcJobId instanceof PbsJobID)){
			throw new HtcException("jobID ("+htcJobId.toDatabase()+") from another queuing system");
		}
		PbsJobID pbsJobID = (PbsJobID)htcJobId;
		
		HtcJobStatus iStatus = null;

		String PBS_HOME = PropertyLoader.getRequiredProperty(PropertyLoader.htcPbsHome);
		if (!PBS_HOME.endsWith("/")){
			PBS_HOME += "/";
		}
		
		String[] cmd = new String[]{PBS_HOME + JOB_CMD_STATUS, "-s", pbsJobID.getPbsJobID()};
		//CommandOutput commandOutput = commandService.command(cmd, new int[] { 0, 153 });

		CommandOutput commandOutput = commandService.command(constructShellCommand(commandService, cmd), new int[] { 0, 153 });
		
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
	public void killJob(HtcJobID htcJobId) throws ExecutableException, HtcException {
		if (!(htcJobId instanceof PbsJobID)){
			throw new HtcException("jobID ("+htcJobId.toDatabase()+") from another queuing system");
		}
		PbsJobID pbsJobID = (PbsJobID)htcJobId;

		String PBS_HOME = PropertyLoader.getRequiredProperty(PropertyLoader.htcPbsHome);
		if (!PBS_HOME.endsWith("/")){
			PBS_HOME += "/";
		}
		String[] cmd = new String[]{PBS_HOME + JOB_CMD_DELETE, pbsJobID.getPbsJobID()};
		try {
			//CommandOutput commandOutput = commandService.command(cmd, new int[] { 0, QDEL_JOB_NOT_FOUND_RETURN_CODE });
			
			CommandOutput commandOutput = commandService.command(constructShellCommand(commandService, cmd), new int[] { 0, QDEL_JOB_NOT_FOUND_RETURN_CODE });
			
			Integer exitStatus = commandOutput.getExitStatus();
			String standardError = commandOutput.getStandardError();
			if (exitStatus!=null && exitStatus==QDEL_JOB_NOT_FOUND_RETURN_CODE && standardError!=null && standardError.toLowerCase().contains(UNKNOWN_JOB_ID_QSTAT_RESPONSE.toLowerCase())){
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
	protected PbsJobID submitJob(String jobName, String sub_file, String[] command, int ncpus, double memSize, String[] secondCommand, String[] exitCommand, String exitCodeReplaceTag) throws ExecutableException{	
		try {

			String htcLogDirString = PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDir);
		    if (!(htcLogDirString.endsWith("/"))){
		    	htcLogDirString = htcLogDirString+"/";
		    }

		    StringWriter sw = new StringWriter();
			int JOB_MEM_OVERHEAD_MB = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jobMemoryOverheadMB));

			sw.append("# Generated without file template. assuming /bin/bash shell\n");
			sw.append("#PBS -N " + jobName+"\n"); //name of the job
			sw.append("#PBS -l mem=" + (int)(memSize + JOB_MEM_OVERHEAD_MB) + "mb\n");
			String pbsQueueName = PropertyLoader.getProperty(PropertyLoader.htcBatchSystemQueue,null);
			if (pbsQueueName!=null && pbsQueueName.trim().length()>0){
				sw.append("#PBS -q "+pbsQueueName+"\n");
			}
			sw.append("#PBS -m a\n"); //send email on abort
			sw.append("#PBS -M schaff@neuron.uchc.edu\n"); //to jim
			sw.append("#PBS -o "+htcLogDirString+jobName+".pbs.log\n"); //stdout goes here
			sw.append("#PBS -j oe\n"); //redirect standard error to standard out
			sw.append("#PBS -r n\n"); //don't restart if it dies

			String htcSubmissionScriptIncludeFileName = PropertyLoader.getProperty(PropertyLoader.htcSubScriptIncludeFile, null);
			if (htcSubmissionScriptIncludeFileName!=null) {
				File htcSubmissionScriptIncludeFile = new File(htcSubmissionScriptIncludeFileName);
				if (htcSubmissionScriptIncludeFile.exists() && htcSubmissionScriptIncludeFile.canRead()) {
					sw.append(FileUtils.readFileToString(htcSubmissionScriptIncludeFile));
					sw.append("\n");
				}
			}
			sw.append("echo\n");
			sw.append("export PATH=/cm/shared/apps/torque/2.5.5/bin/:$PATH\n");

			String ldLibraryPathAppend = PropertyLoader.getProperty(PropertyLoader.ldLibraryPathProperty,"");
			if (!ldLibraryPathAppend.equals("")) {
				sw.append("export LD_LIBRARY_PATH="+ldLibraryPathAppend+":$LD_LIBRARY_PATH\n");
			}
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
		
		String PBS_HOME = PropertyLoader.getRequiredProperty(PropertyLoader.htcPbsHome);
		if (!PBS_HOME.endsWith("/")){
			PBS_HOME += "/";
		}
		String[] completeCommand = new String[] {PBS_HOME + JOB_CMD_SUBMIT, sub_file};
		CommandOutput commandOutput = commandService.command(constructShellCommand(commandService, completeCommand));
		String jobid = commandOutput.getStandardOutput().trim();
		
		return new PbsJobID(jobid);
	}
	
	@Override
	public PbsProxy cloneThreadsafe() {
		return new PbsProxy(getCommandService().clone(),htcUser);
	}

	@Override
	public String getSubmissionFileExtension() {
		return PBS_SUBMISSION_FILE_EXT;
	}
	
	
	@Override
	public List<HtcJobID> getRunningJobIDs(String jobNamePrefix) throws ExecutableException {
		try {
			String PBS_HOME = PropertyLoader.getRequiredProperty(PropertyLoader.htcPbsHome);
			if (!PBS_HOME.endsWith("/")){
				PBS_HOME += "/";
			}
			String[] cmd = constructShellCommand(commandService, new String[]{PBS_HOME + JOB_CMD_STATUS, "|", "grep", htcUser,"|", "grep", jobNamePrefix,"|","cat"/*compensate grep behaviour*/});
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
	public Map<HtcJobID,HtcJobInfo> getJobInfos(List<HtcJobID> htcJobIDs) throws ExecutableException {
		try{
			String PBS_HOME = PropertyLoader.getRequiredProperty(PropertyLoader.htcPbsHome);
			if (!PBS_HOME.endsWith("/")){
				PBS_HOME += "/";
			}
			HashMap<HtcJobID,HtcJobInfo> jobInfoMap = new HashMap<HtcJobID,HtcJobInfo>();
			ArrayList<String> cmdV = new ArrayList<String>();
			cmdV.add(PBS_HOME + JOB_CMD_STATUS);
			cmdV.add("-f");
			for(HtcJobID htcJobID : htcJobIDs){
				cmdV.add(((PbsJobID)htcJobID).getPbsJobID());
			}
			//CommandOutput commandOutput = commandService.command(cmdV.toArray(new String[0]),new int[] { 0, 153 });
			
			CommandOutput commandOutput = commandService.command(constructShellCommand(commandService, cmdV.toArray(new String[0])), new int[] { 0, 153 });
			
			
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
						jobInfoMap.put(latestpbsJobID, new HtcJobInfo(latestpbsJobID,true,latestJobName,latestErrorPath,latestOutputPath));
					}
				}
			}
			return jobInfoMap;
		} catch (Exception e) {
			e.printStackTrace();
			if(e instanceof ExecutableException){
				throw (ExecutableException)e;
			}else{
				throw new ExecutableException("Error getServiceJobIDs: "+e.getMessage());
			}
		}
	}


	public String[] getEnvironmentModuleCommandPrefix() {
		ArrayList<String> ar = new ArrayList<String>();
		ar.add("source");
		ar.add("/etc/profile.d/modules.sh;");
		ar.add("module");
		ar.add("load");
		ar.add(PropertyLoader.getProperty(PropertyLoader.pbsModulePath, "htc/pbs")+";");
		return ar.toArray(new String[0]);
	}
	

}
