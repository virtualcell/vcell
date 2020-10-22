package cbit.vcell.message.server.htc.slurm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import org.vcell.util.FileUtils;
import org.vcell.util.document.KeyValue;
import org.vcell.util.exe.ExecutableException;

import com.google.common.io.Files;

import cbit.rmi.event.WorkerEvent;
import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.cmd.CommandService.CommandOutput;
import cbit.vcell.message.server.htc.HtcException;
import cbit.vcell.message.server.htc.HtcJobNotFoundException;
import cbit.vcell.message.server.htc.HtcJobStatus;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.server.HtcJobID.BatchSystemType;
import cbit.vcell.simdata.PortableCommand;
import cbit.vcell.simdata.PortableCommandWrapper;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solvers.ExecutableCommand;
import edu.uchc.connjur.wb.LineStringBuilder;

public class SlurmProxy extends HtcProxy {
	
	private final static int SCANCEL_JOB_NOT_FOUND_RETURN_CODE = 1;
	private final static String SCANCEL_UNKNOWN_JOB_RESPONSE = "does not exist";
	protected final static String SLURM_SUBMISSION_FILE_EXT = ".slurm.sub";
	
	public SlurmProxy(CommandService commandService, String htcUser) {
		super(commandService, htcUser);
	}


	@Override
	public void killJobSafe(HtcJobInfo htcJobInfo) throws ExecutableException, HtcException {
		final String JOB_CMD_DELETE = PropertyLoader.getProperty(PropertyLoader.slurm_cmd_scancel,"scancel");
		String[] cmd = new String[]{JOB_CMD_DELETE, "--jobname", htcJobInfo.getJobName(), Long.toString(htcJobInfo.getHtcJobID().getJobNumber())};
		try {
			//CommandOutput commandOutput = commandService.command(cmd, new int[] { 0, QDEL_JOB_NOT_FOUND_RETURN_CODE });
			if (LG.isDebugEnabled()) {
				LG.debug("killing SLURM job htcJobId="+htcJobInfo+": '"+CommandOutput.concatCommandStrings(cmd)+"'");
			}
			CommandOutput commandOutput = commandService.command(cmd,new int[] { 0, SCANCEL_JOB_NOT_FOUND_RETURN_CODE });

			Integer exitStatus = commandOutput.getExitStatus();
			String standardOut = commandOutput.getStandardOutput();
			if (exitStatus!=null && exitStatus.intValue()==SCANCEL_JOB_NOT_FOUND_RETURN_CODE && standardOut!=null && standardOut.toLowerCase().contains(SCANCEL_UNKNOWN_JOB_RESPONSE.toLowerCase())){
				LG.error("failed to cancel SLURM htcJobId="+htcJobInfo+", job not found");
				throw new HtcJobNotFoundException(standardOut, htcJobInfo);
			}
		}catch (ExecutableException e){
			LG.error("failed to cancel SLURM htcJobId="+htcJobInfo, e);
			if (!e.getMessage().toLowerCase().contains(SCANCEL_UNKNOWN_JOB_RESPONSE.toLowerCase())){
				throw e;
			}else{
				throw new HtcJobNotFoundException(e.getMessage(), htcJobInfo);
			}
		}
	}

	@Override
	public void killJobUnsafe(HtcJobID htcJobId) throws ExecutableException, HtcException {
		final String JOB_CMD_DELETE = PropertyLoader.getProperty(PropertyLoader.slurm_cmd_scancel,"scancel");
		String[] cmd = new String[]{JOB_CMD_DELETE, Long.toString(htcJobId.getJobNumber())};
		try {
			//CommandOutput commandOutput = commandService.command(cmd, new int[] { 0, QDEL_JOB_NOT_FOUND_RETURN_CODE });
			if (LG.isDebugEnabled()) {
				LG.debug("killing SLURM job htcJobId="+htcJobId+": '"+CommandOutput.concatCommandStrings(cmd)+"'");
			}
			CommandOutput commandOutput = commandService.command(cmd,new int[] { 0, SCANCEL_JOB_NOT_FOUND_RETURN_CODE });

			Integer exitStatus = commandOutput.getExitStatus();
			String standardOut = commandOutput.getStandardOutput();
			if (exitStatus!=null && exitStatus.intValue()==SCANCEL_JOB_NOT_FOUND_RETURN_CODE && standardOut!=null && standardOut.toLowerCase().contains(SCANCEL_UNKNOWN_JOB_RESPONSE.toLowerCase())){
				LG.error("failed to cancel SLURM htcJobId="+htcJobId+", job not found");
				throw new HtcJobNotFoundException(standardOut, htcJobId);
			}
		}catch (ExecutableException e){
			LG.error("failed to cancel SLURM htcJobId="+htcJobId, e);
			if (!e.getMessage().toLowerCase().contains(SCANCEL_UNKNOWN_JOB_RESPONSE.toLowerCase())){
				throw e;
			}else{
				throw new HtcJobNotFoundException(e.getMessage(), htcJobId);
			}
		}
	}

	@Override
	public void killJobs(String jobNameSubstring) {
		Map<HtcJobInfo, HtcJobStatus> runningJobs = null;
		try {
			runningJobs = this.getRunningJobs();
			if (LG.isTraceEnabled()) LG.trace("retrieved "+runningJobs.size()+" running job infos");
		} catch (Exception e) {
			LG.error(e.getMessage(), e);
		}
		
		// kill each job individually
		if (runningJobs != null) {
			for (HtcJobInfo jobInfo : runningJobs.keySet()) {
				if (jobInfo.getJobName().contains(jobNameSubstring)) {
					try {
						this.killJobSafe(jobInfo);
						if (LG.isTraceEnabled()) LG.trace("requested Slurm to kill job "+jobInfo.getJobName());
					} catch (Exception e2) {
						LG.error("failed to request Slurm to kill job "+jobInfo.getJobName()+": "+e2.getMessage(), e2);
					}
				}
			}
		}
	}

	
	/**
	 * adding MPICH command if necessary
	 * @param ncpus if != 1, {@link #MPI_HOME} command prepended
	 * @param cmds command set
	 * @return new String
	 */
	private final String buildExeCommand(int ncpus,String command) {
		if (ncpus == 1) {
			return command;
		}
		String MPI_HOME_EXTERNAL= PropertyLoader.getProperty(PropertyLoader.MPI_HOME_EXTERNAL,"");

		final char SPACE = ' ';
		StringBuilder sb = new StringBuilder( );
		sb.append(MPI_HOME_EXTERNAL);
		sb.append("/bin/mpiexec -np ");
		sb.append(ncpus);
		sb.append(SPACE);
		sb.append(command);
		return sb.toString().trim( );
	}

	@Override
	public HtcProxy cloneThreadsafe() {
		return new SlurmProxy(getCommandService().clone(), getHtcUser());
	}

	@Override
	public String getSubmissionFileExtension() {
		return SLURM_SUBMISSION_FILE_EXT;
	}

	private String getPartitionNodeListCSV() throws HtcException, ExecutableException, IOException {
		final String JOB_CMD_SINFO = PropertyLoader.getProperty(PropertyLoader.slurm_cmd_sinfo,"sinfo");
		// 
		// nodelist=$(sinfo -N -h -p vcell2 --Format='nodelist' | xargs | tr ' ' ',')
		//
		String partition = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_partition);
		String[] cmds = {JOB_CMD_SINFO,"-N","-h","-p",partition,"--Format='nodelist'","|","xargs","|","tr","' '","','"};
		CommandOutput commandOutput = commandService.command(cmds);

		String output = commandOutput.getStandardOutput().trim();
		output = output.replace("\n", "");
		return output;
	}
	
	public PartitionStatistics getPartitionStatistics() throws HtcException, ExecutableException, IOException {
		final String JOB_CMD_SCONTROL = PropertyLoader.getProperty(PropertyLoader.slurm_cmd_scontrol,"scontrol");
		//
		// scontrol show node $(sinfo -N -p vcell2 --Format='nodelist' | xargs | tr ' ' ',') | grep CPUAlloc
		// 

		String partitionNodeList = getPartitionNodeListCSV();
//		String[] cmds = {JOB_CMD_SCONTROL,"show","node","$(",JOB_CMD_SINFO,"-N","-h","-p",partition,"--Format='nodelist'","|","xargs","|","tr","' '","','",")","|","grep","CPUAlloc"};
		String[] cmds = {JOB_CMD_SCONTROL,"-a","show","node",partitionNodeList,"|","grep","CPUAlloc"};
		CommandOutput commandOutput = commandService.command(cmds);

		String output = commandOutput.getStandardOutput();
		PartitionStatistics clusterStatistics = extractPartitionStatistics(output);
		return clusterStatistics;
	}
	
	static PartitionStatistics extractPartitionStatistics(String output) throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(output));
		String line = reader.readLine();
		//   CPUAlloc=0 CPUErr=0 CPUTot=36 CPULoad=0.05
		//   CPUAlloc=0 CPUErr=0 CPUTot=36 CPULoad=0.06
		//   CPUAlloc=0 CPUErr=0 CPUTot=36 CPULoad=0.06
		//   CPUAlloc=0 CPUErr=0 CPUTot=36 CPULoad=N/A
		//   CPUAlloc=0 CPUErr=0 CPUTot=36 CPULoad=0.06
		//   CPUAlloc=0 CPUErr=0 CPUTot=64 CPULoad=0.01
		//   CPUAlloc=0 CPUErr=0 CPUTot=64 CPULoad=0.03
		if (line==null) {
			return new PartitionStatistics(0,0,0);
		}
		int CPUAlloc_sum = 0;
		int CPUErr_sum = 0;
		int CPUTot_sum = 0;
		double CPULoad_sum = 0.0;
		while (line != null){
			line = line.replaceAll(" +", " ").trim();
			line = line.replaceAll("=", " ").trim();
			String[] tokens = line.split(" ");
			String CPUAlloc = "0";
			String CPUErr = "0";
			String CPUTot = "0";
			String CPULoad = "0";
			for (int i = 0; i < tokens.length; i+=2) {
				String val = (tokens[i+1].equals("N/A")?"0":tokens[i+1]);
				CPUAlloc = (tokens[i].equals("CPUAlloc")?val:CPUAlloc);
				CPUErr = (tokens[i].equals("CPUErr")?val:CPUErr);
				CPUTot = (tokens[i].equals("CPUTot")?val:CPUTot);
				CPULoad = (tokens[i].equals("CPULoad")?val:CPULoad);
			}
			
			Integer cpuAlloc = null;
			Integer cpuErr = null;
			Integer cpuTot = null;
			Double cpuLoad = null;
			try {
				cpuAlloc = Integer.parseInt(CPUAlloc);
				cpuErr = Integer.parseInt(CPUErr);
				cpuTot = Integer.parseInt(CPUTot);
				cpuLoad = Double.parseDouble(CPULoad);
				
				// parsing succeeded, node is not down or otherwise irregular
				CPUAlloc_sum += cpuAlloc;
				CPUErr_sum += cpuErr;
				CPUTot_sum += cpuTot;
				CPULoad_sum += cpuLoad;
			} catch (NumberFormatException e) {
				LG.error("failed to parse line '"+line+"' in slurm partition statistics", e);
			}
			line = reader.readLine();
		}
		PartitionStatistics clusterStatistics = new PartitionStatistics(CPUAlloc_sum, CPUTot_sum-CPUErr_sum, CPULoad_sum);
		return clusterStatistics;
	}

	@Override
	public Map<HtcJobInfo, HtcJobStatus> getRunningJobs() throws ExecutableException, IOException {
		final String JOB_CMD_SQUEUE = PropertyLoader.getProperty(PropertyLoader.slurm_cmd_squeue,"squeue");
		// squeue -p vcell2 -O jobid:25,name:25,state:13
		String partition = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_partition);
		String[] cmds = {JOB_CMD_SQUEUE,"-p",partition,"-O","jobid:25,name:25,state:13,batchhost"};
		CommandOutput commandOutput = commandService.command(cmds);

		String output = commandOutput.getStandardOutput();
		Map<HtcJobInfo, HtcJobStatus> statusMap = extractJobIdsFromSqueue(output);
		return statusMap;
	}

	static Map<HtcJobInfo, HtcJobStatus> extractJobIdsFromSqueue(String output) throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(output));
		String line = reader.readLine();
//		JOBID                    NAME                     STATE        EXEC_HOST           
//		330717                   V_BETA_127025366_0_0     PENDING      n/a         
//		330701                   V_BETA_127025160_0_0     RUNNING      shangrila14         
//		330698                   V_BETA_127025036_0_0     RUNNING      shangrila14         
		line = line.replaceAll(" +", " ").trim();
		if (!line.equals("JOBID NAME STATE EXEC_HOST")){
			throw new RuntimeException("unexpected first line from squeue: '"+line+"'");
		}
		Map<HtcJobInfo, HtcJobStatus> statusMap = new HashMap<HtcJobInfo, HtcJobStatus>();
		while ((line = reader.readLine()) != null){
			line = line.replaceAll(" +", " ").trim();
			String[] tokens = line.split(" ");
			String jobID = tokens[0];
			String jobName = tokens[1];
			String state = tokens[2];
			String exe_host = tokens[3];
			if (exe_host.equals("n/a")) {
				exe_host = null;
			}
			HtcJobID htcJobID = new HtcJobID(jobID,BatchSystemType.SLURM);
			HtcJobInfo htcJobInfo = new HtcJobInfo(htcJobID, jobName);
			HtcJobStatus htcJobStatus = new HtcJobStatus(SlurmJobStatus.parseStatus(state));
			statusMap.put(htcJobInfo, htcJobStatus);
		}
		return statusMap;
	}
	
	public Map<HtcJobInfo,HtcJobStatus> getJobStatus(List<HtcJobInfo> requestedHtcJobInfos) throws ExecutableException, IOException {
		if (requestedHtcJobInfos.size()==0) {
			throw new RuntimeException("htcJobList is empty");
		}
		final String JOB_CMD_SACCT = PropertyLoader.getProperty(PropertyLoader.slurm_cmd_sacct,"sacct");
		ArrayList<String> jobNumbers = new ArrayList<String>();
		for (HtcJobInfo jobInfo : requestedHtcJobInfos) {
			jobNumbers.add(Long.toString(jobInfo.getHtcJobID().getJobNumber()));
		}
		String jobList = String.join(",", jobNumbers);
		String[] cmds = {JOB_CMD_SACCT,"-P","-j",jobList,"-o","jobid%25,jobname%25,state%13"};
		CommandOutput commandOutput = commandService.command(cmds);
		
		String output = commandOutput.getStandardOutput();
		Map<HtcJobInfo, HtcJobStatus> statusMap = extractJobIds(output);
		//
		// HtcJobIDs can be reused by Slurm, so make sure it has the correct JobName also.
		//
		for (HtcJobInfo parsedHtcJobInfo : statusMap.keySet()) {
			if (!requestedHtcJobInfos.contains(parsedHtcJobInfo)) {
				statusMap.remove(parsedHtcJobInfo);
			}
		}
		return statusMap;
	}
	
	static Map<HtcJobInfo, HtcJobStatus> extractJobIds(String output) throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(output));
		String line = reader.readLine();
		while(!line.equals("JobID|JobName|State")){
			line = reader.readLine();
//			throw new RuntimeException("unexpected first line from sacct: '"+line+"'");
		}
		Map<HtcJobInfo, HtcJobStatus> statusMap = new HashMap<HtcJobInfo, HtcJobStatus>();
		while ((line = reader.readLine()) != null){
			String[] tokens = line.split("\\|");
			String jobID = tokens[0];
			String jobName = tokens[1];
			String state = tokens[2];
			if (jobName.equals("batch")){
				continue;
			}
			HtcJobID htcJobID = new HtcJobID(jobID,BatchSystemType.SLURM);
			HtcJobInfo htcJobInfo = new HtcJobInfo(htcJobID, jobName);
			HtcJobStatus htcJobStatus = new HtcJobStatus(SlurmJobStatus.parseStatus(state));
			statusMap.put(htcJobInfo, htcJobStatus);
		}
		return statusMap;
	}

	public static class SbatchSolverComponents {
		private String sbatchCommands;
		private String solverCommands;
		private String preProcessCommands;
		private String singularityCommands;
		private String callExitCommands;
		private String sendFailureMsgCommands;
		private String exitCommands;
		private String postProcessCommands;
		public SbatchSolverComponents(String sbatchCommands, String solverCommands,String preProcessCommands,
				String singularityCommands,String callExitCommands,String sendFailureMsgCommands,String exitCommands,
				String postProcessCommands) {
			super();
			this.sbatchCommands = sbatchCommands;
			this.solverCommands = solverCommands;
			this.preProcessCommands = preProcessCommands;
			this.singularityCommands = singularityCommands;
			this.callExitCommands = callExitCommands;
			this.sendFailureMsgCommands = sendFailureMsgCommands;
			this.exitCommands = exitCommands;
			this.postProcessCommands = postProcessCommands;
		}
		public String getSbatchCommands() {
			return sbatchCommands;
		}
		public String getSolverCommands() {
			return solverCommands;
		}
		public String getPreProcessCommands() {
			return preProcessCommands;
		}
		public String getSingularityCommands() {
			return singularityCommands;
		}
		public String getCallExitCommands() {
			return callExitCommands;
		}
		public String getSendFailureMsgCommands() {
			return sendFailureMsgCommands;
		}
		public String getExitCommands() {
			return exitCommands;
		}
		public String getPostProcessCommands() {
			return postProcessCommands;
		}
		
	}
	/**
	 * write bash script for submission
	 * @param jobName
	 * @param sub_file
	 * @param commandSet
	 * @param ncpus
	 * @param memSize
	 * @param postProcessingCommands
	 * @return String containing script
	 */
	SbatchSolverComponents generateScript(String jobName, ExecutableCommand.Container commandSet, int ncpus, double memSizeMB, Collection<PortableCommand> postProcessingCommands, SimulationTask simTask) {
		final boolean isParallel = ncpus > 1;

		//SlurmProxy ultimately instantiated from {vcellroot}/docker/build/Dockerfile-submit-dev by way of cbit.vcell.message.server.batch.sim.HtcSimulationWorker
		String vcellUserid = simTask.getUser().getName();
		KeyValue simID = simTask.getSimulationInfo().getSimulationVersion().getVersionKey();
		SolverDescription solverDescription = simTask.getSimulation().getSolverTaskDescription().getSolverDescription();
		MemLimitResults memoryMBAllowed = HtcProxy.getMemoryLimit(vcellUserid,simID,solverDescription,memSizeMB);

		LineStringBuilder slurmCommands = new LineStringBuilder();
		slurmScriptInit(jobName, simTask.isPowerUser(), memoryMBAllowed, slurmCommands);

		LineStringBuilder lsb = new LineStringBuilder();
		String primaryDataDirExternal = PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirExternalProperty);

		String jmshost_sim_external = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimHostExternal);
		String jmsport_sim_external = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimPortExternal);
		String jmsrestport_sim_external = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimRestPortExternal);
	    String htclogdir_external = PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirExternal);
		String jmsuser=PropertyLoader.getRequiredProperty(PropertyLoader.jmsUser);
		String jmspswd=PropertyLoader.getSecretValue(PropertyLoader.jmsPasswordValue,PropertyLoader.jmsPasswordFile);
		String jmsblob_minsize = PropertyLoader.getProperty(PropertyLoader.jmsBlobMessageMinSize, "10000");
		String mongodbhost_external = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbHostExternal);
		String mongodbport_external = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbPortExternal);
		String mongodb_database = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbDatabase);
		String serverid=PropertyLoader.getRequiredProperty(PropertyLoader.vcellServerIDProperty);
		String softwareVersion=PropertyLoader.getRequiredProperty(PropertyLoader.vcellSoftwareVersion);
		String remote_singularity_image = PropertyLoader.getRequiredProperty(PropertyLoader.vcellbatch_singularity_image);
		String slurm_singularity_local_image_filepath = remote_singularity_image;
//		String docker_image = PropertyLoader.getRequiredProperty(PropertyLoader.vcellbatch_docker_name);
		String slurm_tmpdir = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_tmpdir);
		String slurm_central_singularity_dir = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_central_singularity_dir);
		String slurm_local_singularity_dir = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_local_singularity_dir);
		String simDataDirArchiveHost = PropertyLoader.getRequiredProperty(PropertyLoader.simDataDirArchiveHost);
		File slurm_singularity_central_filepath = new File(slurm_central_singularity_dir,new File(slurm_singularity_local_image_filepath).getName());
		
		String[] environmentVars = new String[] {
				"java_mem_Xmx="+memoryMBAllowed.getMemLimit()+"M",
				"jmshost_sim_internal="+jmshost_sim_external,
				"jmsport_sim_internal="+jmsport_sim_external,
				"jmsrestport_sim_internal="+jmsrestport_sim_external,
				"jmsuser="+jmsuser,
				"jmspswd="+jmspswd,
				"jmsblob_minsize="+jmsblob_minsize,
				"mongodbhost_internal="+mongodbhost_external,
				"mongodbport_internal="+mongodbport_external,
				"mongodb_database="+mongodb_database,
				"datadir_external="+primaryDataDirExternal,
				"htclogdir_external="+htclogdir_external,
				"softwareVersion="+softwareVersion,
				"serverid="+serverid
		};
		lsb.write("echo \"1 date=`date`\"");
		LineStringBuilder singularityLSB = new LineStringBuilder();
		slurmInitSingularity(singularityLSB, primaryDataDirExternal, htclogdir_external, softwareVersion,
				slurm_singularity_local_image_filepath, slurm_tmpdir, slurm_central_singularity_dir,
				slurm_local_singularity_dir, simDataDirArchiveHost, slurm_singularity_central_filepath,
				environmentVars);

		LineStringBuilder sendFailMsgLSB = new LineStringBuilder();
		sendFailMsgScript(simTask, sendFailMsgLSB, jmshost_sim_external, jmsport_sim_external, jmsuser, jmspswd);
		
		if (isParallel) {
			lsb.write("#BEGIN---------SlurmProxy.generateScript():isParallel----------");
			String MPI_HOME_EXTERNAL= PropertyLoader.getProperty(PropertyLoader.MPI_HOME_EXTERNAL,"");


			// #SBATCH
//			lsb.append("#$ -pe mpich ");
//			lsb.append(ncpus);
//			lsb.newline();
			
			lsb.append("#SBATCH -n " + ncpus);
			lsb.newline();

			lsb.append("#$ -v LD_LIBRARY_PATH=");
			lsb.append(MPI_HOME_EXTERNAL+"/lib");
			lsb.write(":"+primaryDataDirExternal);
			lsb.write("#END---------SlurmProxy.generateScript():isParallel----------");
		}
		lsb.newline();
	
		final boolean hasExitProcessor = commandSet.hasExitCodeCommand();
	//	lsb.write("run_in_container=\"singularity /path/to/data:/simdata /path/to/image/vcell-batch.img);
		LineStringBuilder callExitLSB = new LineStringBuilder();
		if (hasExitProcessor) {
			callExitScript(commandSet, callExitLSB);
		}

		LineStringBuilder preProcessLSB = new LineStringBuilder();
		for (ExecutableCommand ec: commandSet.getExecCommands()) {
			if(ec.getCommands().get(0).equals("JavaPreprocessor64")) {
				execCommandScript(ncpus, isParallel, preProcessLSB, hasExitProcessor, ec);
			}else {
				execCommandScript(ncpus, isParallel, lsb, hasExitProcessor, ec);
			}
		}

		LineStringBuilder postProcessLSB = new LineStringBuilder();
		Objects.requireNonNull(postProcessingCommands);
		PortableCommandWrapper.insertCommands(postProcessLSB.sb, postProcessingCommands);
		
//		lsb.newline();
		LineStringBuilder exitLSB = new LineStringBuilder();
		if (hasExitProcessor) {
			exitLSB.write("callExitProcessor 0");
		}
//		lsb.write("echo \"7 date=`date`\"");
//		lsb.newline();
		return new SbatchSolverComponents(slurmCommands.sb.toString(), lsb.sb.toString(),preProcessLSB.sb.toString(),singularityLSB.sb.toString(),callExitLSB.sb.toString(),sendFailMsgLSB.sb.toString(),exitLSB.sb.toString(),postProcessLSB.sb.toString());
	}


	private void callExitScript(ExecutableCommand.Container commandSet, LineStringBuilder lsb) {
		lsb.write("#BEGIN---------SlurmProxy.generateScript():hasExitProcessor----------");
		ExecutableCommand exitCmd = commandSet.getExitCodeCommand();
		exitCmd.stripPathFromCommand();
		lsb.write("callExitProcessor( ) {");
		lsb.append("\techo exitCommand = ");
		lsb.write("${container_prefix}" + exitCmd.getJoinedCommands("$1"));
		lsb.append('\t');
		lsb.write("${container_prefix}" + exitCmd.getJoinedCommands());
		lsb.write("}");
		lsb.write("#END---------SlurmProxy.generateScript():hasExitProcessor----------");
		lsb.write("echo");
	}


	private void sendFailMsgScript(SimulationTask simTask, LineStringBuilder lsb, String jmshost_sim_external,
			String jmsport_sim_external, String jmsuser, String jmspswd) {
		lsb.write("#BEGIN---------SlurmProxy.generateScript():sendFailureMsg----------");
		lsb.write("sendFailureMsg() {");
		lsb.write("  echo ${container_prefix} " +
				" --msg-userid "+jmsuser+
				" --msg-password "+jmspswd+
				" --msg-host "+jmshost_sim_external+
				" --msg-port "+jmsport_sim_external+
				" --msg-job-host `hostname`"+
				" --msg-job-userid "+simTask.getUserName()+
				" --msg-job-simkey "+simTask.getSimKey()+
				" --msg-job-jobindex "+simTask.getSimulationJob().getJobIndex() +
				" --msg-job-taskid "+simTask.getTaskID() +
				" --msg-job-errmsg \"$1\"" +
				" SendErrorMsg");
		lsb.write("  ${container_prefix} " +
				" --msg-userid "+jmsuser+
				" --msg-password "+jmspswd+
				" --msg-host "+jmshost_sim_external+
				" --msg-port "+jmsport_sim_external+
				" --msg-job-host `hostname`"+
				" --msg-job-userid "+simTask.getUserName()+
				" --msg-job-simkey "+simTask.getSimKey()+
				" --msg-job-jobindex "+simTask.getSimulationJob().getJobIndex() +
				" --msg-job-taskid "+simTask.getTaskID() +
				" --msg-job-errmsg \"$1\"" +
				" SendErrorMsg");
		lsb.write("  stat=$?");
		lsb.write("  if [[ $stat -ne 0 ]]; then");
		lsb.write("    echo 'failed to send error message, retcode=$stat'");
		lsb.write("  else");
		lsb.write("    echo 'sent failure message'");
		lsb.write("  fi");
		lsb.write("}");
		lsb.write("#END---------SlurmProxy.generateScript():sendFailureMsg----------");
	}


	private void execCommandScript(int ncpus, final boolean isParallel, LineStringBuilder lsb,final boolean hasExitProcessor, ExecutableCommand ec) {
		lsb.write("echo");
		lsb.write("#BEGIN---------SlurmProxy.generateScript():ExecutableCommand----------"+ec.getCommands().get(0));
		ec.stripPathFromCommand();
		//
		// The first token in the command list is always the name of the executable.
		// if an executable with that name exists in the nativesolvers directory, then use that instead.
		//
		String cmd= ec.getJoinedCommands();
		String exeName= ec.getCommands().get(0);
		File nativeSolverDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.nativeSolverDir_External));
		File nativeExe = new File(nativeSolverDir,exeName);
		lsb.write("echo \"testing existance of native exe '"+nativeExe.getAbsolutePath()+"' which overrides container invocations\"");
		lsb.write("nativeExe="+nativeExe.getAbsolutePath());
		lsb.write("if [ -e \"${nativeExe}\" ]; then");
		lsb.write("   cmd_prefix=\""+nativeSolverDir.getAbsolutePath()+"/"+"\"");
		lsb.write("else");
		lsb.write("   cmd_prefix=\"$container_prefix\"");
		lsb.write("fi");
		lsb.write("echo \"cmd_prefix is '${cmd_prefix}'\"");
		lsb.write("echo \"5 date=`date`\"");
		if (ec.isParallel()) {
			if (isParallel) {
				cmd = buildExeCommand(ncpus, cmd);
			}
			else {
				throw new UnsupportedOperationException("parallel command " + ec.getJoinedCommands() + " called in non-parallel submit");
			}
		}
		lsb.append("echo command = ");
		lsb.write("${cmd_prefix}" + cmd);

		lsb.write("(");
		if (ec.getLdLibraryPath()!=null){
			lsb.write("    export LD_LIBRARY_PATH="+ec.getLdLibraryPath().path+":$LD_LIBRARY_PATH");
		}
		lsb.write("singdevlooperr=\"Failed to mount squashfs image in (read only)\"");
		lsb.write("let c=0");
		lsb.write("while [ true ]");
		lsb.write("    do");
		lsb.write("      cmdstdout=$("+"${cmd_prefix}" + cmd+" 2>&1)");
		lsb.write("      innerstate=$?");
		lsb.write("      if [[ $cmdstdout != *$singdevlooperr* ]]");
		lsb.write("      then");
		lsb.write("        exit $innerstate");
		lsb.write("      fi");
		lsb.write("      sleep 6");
		lsb.write("		 let c=c+1");
		lsb.write("		 if [ $c -eq 10 ]");
		lsb.write("		 then");
		lsb.write("		  	echo \"Exceeded retry for singularity mount squashfs error\"");
		lsb.write("		  	exit $innerstate");
		lsb.write("		 fi");
		lsb.write("		 echo retrying $c of 10...");
		lsb.write("    done");
		lsb.write(")");
		lsb.write("stat=$?");

		lsb.append("echo ");
		lsb.append("${cmd_prefix}" + cmd);
		lsb.write("returned $stat");

		lsb.write("if [ $stat -ne 0 ]; then");
		if (hasExitProcessor) {
			lsb.write("\tcallExitProcessor $stat");
		}
		lsb.write("\techo returning $stat to Slurm");
		lsb.write("\texit $stat");
		lsb.write("fi");
		lsb.write("#END---------SlurmProxy.generateScript():ExecutableCommand----------"+ec.getCommands().get(0));
	}


	private void slurmInitSingularity(LineStringBuilder lsb, String primaryDataDirExternal, String htclogdir_external,
			String softwareVersion, String slurm_singularity_local_image_filepath, String slurm_tmpdir,
			String slurm_central_singularity_dir, String slurm_local_singularity_dir, String simDataDirArchiveHost,
			File slurm_singularity_central_filepath, String[] environmentVars) {
		lsb.write("#BEGIN---------SlurmProxy.generateScript():slurmInitSingularity----------");
		lsb.write("TMPDIR="+slurm_tmpdir);
		lsb.write("echo \"using TMPDIR=$TMPDIR\"");
		lsb.write("if [ ! -e $TMPDIR ]; then mkdir -p $TMPDIR ; fi");
		
		//
		// Initialize Singularity
		//
		lsb.write("echo `hostname`\n");
		lsb.write("export MODULEPATH=/isg/shared/modulefiles:/tgcapps/modulefiles\n");
		lsb.write("source /usr/share/Modules/init/bash\n");
		lsb.write("module load singularity/2.4.2\n");
		
		lsb.write("echo \"job running on host `hostname -f`\"");
		lsb.newline();
		lsb.write("echo \"id is `id`\"");
		lsb.newline();
		lsb.write("echo \"bash version is `bash --version`\"");
		lsb.write("date");
		lsb.newline();
		lsb.write("echo ENVIRONMENT");
		lsb.write("env");
		lsb.newline();
		
		lsb.write("container_prefix=");
		lsb.write("if command -v singularity >/dev/null 2>&1; then");
		lsb.write("   #");
		lsb.write("   # Copy of singularity image will be downloaded if not found in "+slurm_singularity_local_image_filepath);
		lsb.write("   #");
		lsb.write("   localSingularityImage="+slurm_singularity_local_image_filepath);
		lsb.write("   if [ ! -e \"$localSingularityImage\" ]; then");
		lsb.write("       echo \"local singularity image $localSingularityImage not found, trying to download to hpc from \""+slurm_singularity_central_filepath.getAbsolutePath());
		lsb.write("       mkdir -p "+slurm_local_singularity_dir);
		lsb.write("       singularitytempfile=$(mktemp -up "+slurm_central_singularity_dir+")");
		// Copy using locking so when new deployments occur and singularity has to be copied to compute host
		// and multiple parameter scan land on same compute host at same time and all try to download the singularity image
		// they won't interfere with each other
		lsb.write("		  flock -E 100 -n /tmp/vcellSingularityLock_"+softwareVersion+".lock sh -c \"cp "+slurm_singularity_central_filepath.getAbsolutePath()+" ${singularitytempfile}"+" ; mv -n ${singularitytempfile} "+slurm_singularity_local_image_filepath+"\"");
		lsb.write("		  theStatus=$?");
		lsb.write("		  if [ $theStatus -eq 100 ]");
		lsb.write("		  then");
		lsb.write("		      echo \"lock in use, waiting for lock owner to copy singularityImage\"");
		lsb.write("		      let c=0");
		lsb.write("		      until [ -f $localSingularityImage ]");
		lsb.write("		      do");
		lsb.write("		  	    sleep 3");
		lsb.write("		  	    let c=c+1");
		lsb.write("		  		if [ $c -eq 20 ]");
		lsb.write("		  		then");
		lsb.write("		  			echo \"Exceeded wait time for lock owner to copy singularityImage\"");
		lsb.write("		  			break");
		lsb.write("		  		fi");
		lsb.write("		      done");
		lsb.write("		  else");
		lsb.write("		      if [ $theStatus -eq 0 ]");
		lsb.write("		      then");
		lsb.write("		            echo copy succeeded");
		lsb.write("		      else");
		lsb.write("		            echo copy failed");
		lsb.write("		      fi");
		lsb.write("		  fi");

		lsb.write("       rm -f ${singularitytempfile}");
		lsb.write("       if [ ! -e \"$localSingularityImage\" ]; then");		
		lsb.write("           echo \"Failed to copy $localSingularityImage to hpc from central\"");
		lsb.write("           exit 1");
		lsb.write("       else");
		lsb.write("           echo successful copy from "+slurm_singularity_central_filepath.getAbsolutePath()+" to "+slurm_singularity_local_image_filepath);
		lsb.write("       fi");
		lsb.write("   fi");
		StringBuffer singularityEnvironmentVars = new StringBuffer();
		for (String envVar : environmentVars) {
			singularityEnvironmentVars.append(" --env "+envVar);
		}
		lsb.write("   container_prefix=\"singularity run --bind "+primaryDataDirExternal+":/simdata --bind "+simDataDirArchiveHost+":"+simDataDirArchiveHost+" --bind "+htclogdir_external+":/htclogs  --bind "+slurm_tmpdir+":/solvertmp $localSingularityImage "+singularityEnvironmentVars+" \"");
		lsb.write("else");
		lsb.write("    echo \"Required singularity command not found (module load singularity/2.4.2) \"");
		lsb.write("    exit 1");
//		StringBuffer dockerEnvironmentVars = new StringBuffer();
//		for (String envVar : environmentVars) {
//			dockerEnvironmentVars.append(" -e "+envVar);
//		}
//		lsb.write("   container_prefix=\"docker run --rm -v "+primaryDataDirExternal+":/simdata -v "+htclogdir_external+":/htclogs -v "+slurm_tmpdir+":/solvertmp "+dockerEnvironmentVars+" "+docker_image+" \"");
		lsb.write("fi");
		lsb.write("echo \"container_prefix is '${container_prefix}'\"");
		lsb.write("echo \"3 date=`date`\"");
		lsb.write("#END---------SlurmProxy.generateScript():slurmInitSingularity----------");
		lsb.newline();
	}


	private void slurmScriptInit(String jobName, boolean bPowerUser, MemLimitResults memoryMBAllowed,
			LineStringBuilder lsb) {
		lsb.write("#!/usr/bin/bash");
		File htcLogDirExternal = new File(PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirExternal));
		if(bPowerUser) {
			String partition_pu = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_partition_pu);
			String reservation_pu = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_reservation_pu);
			lsb.write("#SBATCH --partition=" + partition_pu);
//			lsb.write("#SBATCH --reservation=" + reservation_pu);		
			lsb.write("#SBATCH --qos=" + reservation_pu);			
		}else {
			String partition = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_partition);
			String reservation = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_reservation);
			lsb.write("#SBATCH --partition=" + partition);
			lsb.write("#SBATCH --reservation=" +reservation);
			lsb.write("#SBATCH --qos=" +reservation);
		}
		lsb.write("#SBATCH -J " + jobName);
		lsb.write("#SBATCH -o " + new File(htcLogDirExternal, jobName+".slurm.log").getAbsolutePath());
		lsb.write("#SBATCH -e " + new File(htcLogDirExternal, jobName+".slurm.log").getAbsolutePath());
		lsb.write("#SBATCH --mem="+memoryMBAllowed.getMemLimit()+"M");
		lsb.write("#SBATCH --no-kill");
		lsb.write("#SBATCH --no-requeue");
		String nodelist = PropertyLoader.getProperty(PropertyLoader.htcNodeList, null);
		if (nodelist!=null && nodelist.trim().length()>0) {
			lsb.write("#SBATCH --nodelist="+nodelist);
		}
//		lsb.write("echo \"1 date=`date`\"");
		lsb.write("# VCell SlurmProxy memory limit source="+memoryMBAllowed.getMemLimitSource());
	}

	@Override
	public HtcJobID submitJob(String jobName, File sub_file_internal, File sub_file_external, ExecutableCommand.Container commandSet, int ncpus, double memSizeMB, Collection<PortableCommand> postProcessingCommands, SimulationTask simTask,File primaryUserDirExternal) throws ExecutableException {
		try {
			if (LG.isDebugEnabled()) {
				LG.debug("generating local SLURM submit script for jobName="+jobName);
			}
			SlurmProxy.SbatchSolverComponents sbatchSolverComponents = generateScript(jobName, commandSet, ncpus, memSizeMB, postProcessingCommands, simTask);
			final String SUB = ".sub";
			String slurmRootName = sub_file_external.getName().substring(0, sub_file_external.getName().length()-SUB.length());
			String child = slurmRootName+".sh";
			File intSolverScriptFile = new File(sub_file_internal.getParentFile(),child);
			File extSolverScriptFile = new File(sub_file_external.getParentFile(),child);
			
			//Write the .slurm.sh File that the .slurm.sub file references and make it executable
			Files.write(sbatchSolverComponents.getSingularityCommands(),intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
			Files.append(sbatchSolverComponents.getSendFailureMsgCommands(),intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
			Files.append(sbatchSolverComponents.getCallExitCommands(),intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
//			Files.append(sbatchSolverComponents.getPreProcessCommands(),intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
			String STARTFLAG_SNIP = "_arrstartflag_";
			File dataSaveFile = null;
			File stochInputFile = null;
			File startFlagFile = null;
			long numOfTrials = 1;
			if(HtcProxy.isStochMultiTrial(simTask)) {//Find Gibson solver outputfile name from command arguments 
				numOfTrials = simTask.getSimulationJob().getSimulation().getSolverTaskDescription().getStochOpt().getNumOfTrials();
				Files.append("#simTask.getSimulationJob().getSimulation().getSolverTaskDescription().getStochOpt().isHistogram()="+simTask.getSimulationJob().getSimulation().getSolverTaskDescription().getStochOpt().isHistogram()+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				Files.append("#simTask.getSimulationJob().getSimulation().getSolverTaskDescription().getStochOpt().getNumOfTrials()="+numOfTrials+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				Files.append("#simTask.getSimulationJob().getSimulation().getSolverTaskDescription().getStochOpt().getCustomSeed()="+simTask.getSimulationJob().getSimulation().getSolverTaskDescription().getStochOpt().getCustomSeed()+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				Files.append("#isMultiTrial="+HtcProxy.isStochMultiTrial(simTask)+"\n" ,intSolverScriptFile, Charset.forName(StandardCharsets.UTF_8.name()));
				List<ExecutableCommand> execCommands = commandSet.getExecCommands();
//				outerloop:
				for (Iterator<ExecutableCommand> iterator = execCommands.iterator(); iterator.hasNext();) {
					ExecutableCommand executableCommand = (ExecutableCommand) iterator.next();
					List<String> commands = executableCommand.getCommands();
					for (Iterator<String> iterator2 = commands.iterator(); iterator2.hasNext();) {
						String cmdParam = (String) iterator2.next();
						if(cmdParam.contains("SimID_") && cmdParam.endsWith(SimDataConstants.IDA_DATA_EXTENSION)) {
							dataSaveFile = new File(primaryUserDirExternal,new File(cmdParam).getName());
							int rand = new Random().nextInt(1000000);
							String idaname=new File(cmdParam).getName();
							idaname=idaname.substring(0, idaname.length()-SimDataConstants.IDA_DATA_EXTENSION.length());
							startFlagFile = new File(primaryUserDirExternal,idaname+STARTFLAG_SNIP+rand);
							Files.append("#dataSaveFile="+dataSaveFile+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
							Files.append("#startFlagFile="+startFlagFile+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
							
//							break outerloop;
						}else if(cmdParam.contains("SimID_") && cmdParam.endsWith(SimDataConstants.STOCHINPUT_DATA_EXTENSION)) {
							stochInputFile = new File(primaryUserDirExternal,new File(cmdParam).getName());
							Files.append("#stochInputFile="+stochInputFile+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
						}
					}
				}
			}
			long TRIALS_PER_ARR_MAX = 100;
			long slurmArrayCount = (numOfTrials+TRIALS_PER_ARR_MAX-1)/TRIALS_PER_ARR_MAX;
			boolean isCompleteMultiTrialArray = /*(slurmArrayCount > 1) && */HtcProxy.isStochMultiTrial(simTask) && dataSaveFile != null && stochInputFile != null;
			if(isCompleteMultiTrialArray) {
				String jmsrestpswd=PropertyLoader.getSecretValue(null,PropertyLoader.jmsRestPasswordFile);
				String jmshost_sim_external = System.getProperty("vcell.jms.sim.host.external");
				String jmsrestport_sim_external = System.getProperty("vcell.jms.sim.restport.external");
				if(jmshost_sim_external == null || jmsrestport_sim_external == null) {
					throw new ExecutableException("Array job expects vcell.jms.sim.host.external and vcell.jms.sim.restport.external to be non-null");
				}
				Files.append("getprogcnt() {"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
					Files.append("progcnt=`find "+primaryUserDirExternal.getAbsolutePath()+" -name '"+dataSaveFile.getName()+"_*' | wc -l`"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
					Files.append("echo $progcnt"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				Files.append("}"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				//Create progress function
				Files.append("sendprogressevent() {"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
					Files.append("local progcnt=$1"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
					Files.append("if [ $SLURM_ARRAY_TASK_ID -ne 1 ]"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
					Files.append("then"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
						Files.append("return"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
					Files.append("fi"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
					//Send progress_worker_event
					Files.append("nexttime=$(date +%s)"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
					Files.append("let \"diff = $nexttime-$starttime\""+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
					Files.append("if [ $diff -ge 10 ]"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
					Files.append("then"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
						Files.append("starttime=$nexttime"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
//						Files.append("local progcnt=$(getprogcnt)"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
						Files.append("arrprog=$(echo \"scale=2; $progcnt/"+numOfTrials+"\" | bc)"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
						Files.append("     echo -en \"POST /api/message/workerEvent?type=queue&JMSPriority=5&JMSTimeToLive=60000&JMSDeliveryMode=nonpersistent&MessageType=WorkerEvent&UserName="+simTask.getUserName()+"&HostName=${HOSTNAME}&SimKey="+simTask.getSimKey().toString()+"&TaskID="+simTask.getTaskID()+"&JobIndex="+simTask.getSimulationJob().getJobIndex()+"&WorkerEvent_Status="+WorkerEvent.JOB_PROGRESS+"&WorkerEvent_Progress=${arrprog}&WorkerEvent_TimePoint=${progcnt} HTTP/1.1\\r\\nHost: "+jmshost_sim_external+"\\r\\nAuthorization: Basic "+jmsrestpswd+"\\r\\nAccept: */*\\r\\nContent-Length: 0\\r\\nContent-Type: application/x-www-form-urlencoded\\r\\n\\r\\n\" | nc "+jmshost_sim_external+" "+jmsrestport_sim_external+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
					Files.append("fi"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				Files.append("}"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				
				//If we'r the first task in slurm array job Remove old data, create startFlagFile to signal other tasks to begin creating new data
				Files.append("starttime=$(date +%s)"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));				
				//slurm array 1 creates array progress file (created with random number name so other slurm arrays wait until it exists
				Files.append("if [ \"$SLURM_ARRAY_TASK_ID\" -eq \""+"1"+"\" ]"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				Files.append("then"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
//					Files.append("     echo -en \"POST /api/message/workerEvent?type=queue&JMSPriority=5&JMSTimeToLive=60000&JMSDeliveryMode=nonpersistent&MessageType=WorkerEvent&UserName="+simTask.getUserName()+"&HostName=${HOSTNAME}&SimKey="+simTask.getSimKey().toString()+"&TaskID="+simTask.getTaskID()+"&JobIndex="+simTask.getSimulationJob().getJobIndex()+"&WorkerEvent_Status="+WorkerEvent.JOB_STARTING+"&WorkerEvent_Progress=0&WorkerEvent_TimePoint=0 HTTP/1.1\\r\\nHost: "+jmshost_sim_external+"\\r\\nAuthorization: Basic "+jmsrestpswd+"\\r\\nAccept: */*\\r\\nContent-Length: 0\\r\\nContent-Type: application/x-www-form-urlencoded\\r\\n\\r\\n\" | nc "+jmshost_sim_external+" "+jmsrestport_sim_external+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
					Files.append(sbatchSolverComponents.getPreProcessCommands(),intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
					Files.append("rm -f "+dataSaveFile.getAbsolutePath()+"*"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
					Files.append("echo -n \"0\" >"+startFlagFile.getAbsolutePath()+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				Files.append("fi"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				
				//Wait for startFlagFile file to be created (other array tasks wait to start for this file)
				Files.append("until [ -f "+startFlagFile.getAbsolutePath()+" ]"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				Files.append("do"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
					Files.append("\tsleep 2"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				Files.append("done"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));

				String s = "if [ $stat -ne 0 ]; then\n" + 
						"	callExitProcessor $stat\n" + 
						"	echo returning $stat to Slurm\n" + 
						"	exit $stat\n" + 
						"fi";
				String s2 = "if [ $stat -ne 0 ]; then\n" + 
						"	let \"run=$run-1\"\n" + 
						"	continue\n" + 
						"#	callExitProcessor $stat\n" + 
						"#	echo returning $stat to Slurm\n" + 
						"#	exit $stat\n" + 
						"fi";

				String substituedCmd = sbatchSolverComponents.getSolverCommands();
				int lastIndex = substituedCmd.lastIndexOf(s);
				substituedCmd = substituedCmd.substring(0, lastIndex)+s2+substituedCmd.substring(lastIndex+s.length());
				//if \[ \$stat \-ne 0 \]\; then\n	callExitProcessor \$stat\n	echo returning \$stat to Slurm\n	exit \$stat\nfi
				Files.append("for (( run=1; run<="+TRIALS_PER_ARR_MAX+"; run++ )); do"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				Files.append("let \"currcnt = ((${SLURM_ARRAY_TASK_ID}-1)*"+TRIALS_PER_ARR_MAX+")+$run\""+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				//exit run loop if we're in last arraytask and there are fewer jobs than full TRIALS_PER_ARR_MAX
				Files.append("if [ $currcnt -gt "+numOfTrials+" ]"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				Files.append("then"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
					Files.append("break"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				Files.append("fi"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				//cp .stochInput for each run and change seed
				substituedCmd = substituedCmd.replace(".stochInput", ".stochInput_${SLURM_ARRAY_TASK_ID}");
				//change output file name based on  task and run
				String arrRunDataFile = dataSaveFile.getName()+"_"+"${SLURM_ARRAY_TASK_ID}"+"_"+"${run}";
				substituedCmd = substituedCmd.replace(dataSaveFile.getName(), arrRunDataFile);
//				//Prevent solver c++ from sending progress updates (will be handled in this script)
				substituedCmd = substituedCmd.replace("-tid "+simTask.getTaskID(),"");
				String taskStochInput = stochInputFile.getAbsolutePath()+"_${SLURM_ARRAY_TASK_ID}";
				Files.append("cp "+stochInputFile.getAbsolutePath()+" "+taskStochInput+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));

				Files.append("origseed=`grep -oP  'SEED\\s\\K\\w+' "+taskStochInput+"`"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				//grep -oP  'SEED\s\K\w+'
				Files.append("let \"newseed = ${origseed}+${currcnt}\""+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				Files.append("origline=`grep \"SEED.*\" "+taskStochInput+"`"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				//update seed
				Files.append("sed -i \"s/${origline}/SEED\t${newseed}/g\" "+taskStochInput+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				//change output file name
				//Files.append("sed -i 's/"+dataSaveFile.getName()+"/"+dataSaveFile.getAbsolutePath()+"_"+"${SLURM_ARRAY_TASK_ID}"+"_"+"${run}"+"/g' "+taskStochInput+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				Files.append(substituedCmd,intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				//Send progress
				Files.append("progcnt=$(getprogcnt)"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				Files.append("sendprogressevent $progcnt"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				Files.append("done"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				
				//Loop waiting for all array jobs to create sim results (if array task 1)
				Files.append("if [ $SLURM_ARRAY_TASK_ID -eq 1 ]"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				Files.append("then"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
					Files.append("lastprogcnt=\"0\""+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
					Files.append("maxlooptime=\"300\""+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));//5 minutes
					Files.append("slptime=5"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
					Files.append("slpvar=0"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
					Files.append("while : ; do"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
						Files.append("sleep $slptime"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
						Files.append("progcnt=$(getprogcnt)"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));

						Files.append("if [ $progcnt -ne  $lastprogcnt ]"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
						Files.append("then"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
						Files.append("lastprogcnt=$progcnt"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
						Files.append("slpvar=0"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
						Files.append("fi"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
						
						Files.append("if [ $progcnt -eq "+numOfTrials+" ]"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
						Files.append("then"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
							Files.append(sbatchSolverComponents.getExitCommands(),intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
							Files.append("echo -en \"POST /api/message/workerEvent?type=queue&JMSPriority=5&JMSTimeToLive=600000&JMSDeliveryMode=persistent&MessageType=WorkerEvent&UserName="+simTask.getUserName()+"&HostName=${HOSTNAME}&SimKey="+simTask.getSimKey().toString()+"&TaskID="+simTask.getTaskID()+"&JobIndex="+simTask.getSimulationJob().getJobIndex()+"&WorkerEvent_Status="+WorkerEvent.JOB_COMPLETED+"&WorkerEvent_Progress=1&WorkerEvent_TimePoint=${progcnt} HTTP/1.1\\r\\nHost: "+jmshost_sim_external+"\\r\\nAuthorization: Basic "+jmsrestpswd+"\\r\\nAccept: */*\\r\\nContent-Length: 0\\r\\nContent-Type: application/x-www-form-urlencoded\\r\\n\\r\\n\" | nc "+jmshost_sim_external+" "+jmsrestport_sim_external+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
							Files.append("exit 0"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));//fail
						Files.append("fi"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
						Files.append("let slpvar=$slpvar+$slptime"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
						Files.append("if [ $slpvar -gt $maxlooptime ]"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
						Files.append("then"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
							Files.append("callExitProcessor 1"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));//fail
							Files.append("exit 1"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));//fail
						Files.append("fi"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
						Files.append("sendprogressevent $progcnt"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
					Files.append("done"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				Files.append("fi"+"\n",intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
			}else {
				Files.append(sbatchSolverComponents.getPreProcessCommands(),intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				Files.append(sbatchSolverComponents.solverCommands,intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
				Files.append(sbatchSolverComponents.getExitCommands(),intSolverScriptFile , Charset.forName(StandardCharsets.UTF_8.name()));
			}
			Set<PosixFilePermission> ownerRWX = PosixFilePermissions.fromString("rwxr-xr-x");
//			FileAttribute<?> permissions = PosixFilePermissions.asFileAttribute(ownerWritable);
			java.nio.file.Files.setPosixFilePermissions(intSolverScriptFile.toPath(), ownerRWX);
			
			//----------Add solver script path to sbatch file, write the .slurm.sub file
			String substitutedSbatchCommands = sbatchSolverComponents.getSbatchCommands();
			if(isCompleteMultiTrialArray) {
				substitutedSbatchCommands = substitutedSbatchCommands.replaceAll("#SBATCH -o.*", "#SBATCH -o "+new File(sub_file_external.getParent(),slurmRootName+".log").getAbsolutePath()+"_%a");
				substitutedSbatchCommands = substitutedSbatchCommands.replaceAll("#SBATCH -e.*", "#SBATCH -e "+new File(sub_file_external.getParent(),slurmRootName+".log").getAbsolutePath()+"_%a");
				substitutedSbatchCommands+= "#SBATCH --array=1-"+slurmArrayCount;
			}
			File tempFile = File.createTempFile("tempSubFile", SUB);
			writeUnixStyleTextFile(tempFile, substitutedSbatchCommands+"\n\n"+extSolverScriptFile.getAbsolutePath()+"\n\n"+
			"#Following commands (if any) are read by JavaPostProcessor64\n"+sbatchSolverComponents.postProcessCommands+"\n");

			// move submission file to final location (either locally or remotely).
			if (LG.isDebugEnabled()) {
				LG.debug("moving local SLURM submit file '"+tempFile.getAbsolutePath()+"' to remote file '"+sub_file_external+"'");
			}
			FileUtils.copyFile(tempFile, sub_file_internal);
			tempFile.delete();
			//----------

		} catch (IOException ex) {
			ex.printStackTrace(System.out);
			return null;
		}

		return submitJobFile(sub_file_external);
	}

	HtcJobID submitJobFile(File sub_file_external) throws ExecutableException {
		final String JOB_CMD_SBATCH = PropertyLoader.getProperty(PropertyLoader.slurm_cmd_sbatch,"sbatch");
		String[] completeCommand = new String[] {JOB_CMD_SBATCH, sub_file_external.getAbsolutePath()};
		if (LG.isDebugEnabled()) {
			LG.debug("submitting SLURM job: '"+CommandOutput.concatCommandStrings(completeCommand)+"'");
		}
		CommandOutput commandOutput = commandService.command(completeCommand);
		String jobid = commandOutput.getStandardOutput().trim();
		final String EXPECTED_STDOUT_PREFIX = "Submitted batch job ";
		if (jobid.startsWith(EXPECTED_STDOUT_PREFIX)){
			jobid = jobid.replace(EXPECTED_STDOUT_PREFIX, "");
		}else{
			LG.error("failed to submit SLURM job '"+sub_file_external+"', stdout='"+commandOutput.getStandardOutput()+"', stderr='"+commandOutput.getStandardError()+"'");
			throw new ExecutableException("unexpected response from '"+JOB_CMD_SBATCH+"' while submitting simulation: '"+jobid+"'");
		}
		HtcJobID htcJobID = new HtcJobID(jobid,BatchSystemType.SLURM);
		if (LG.isDebugEnabled()) {
			LG.debug("SLURM job '"+CommandOutput.concatCommandStrings(completeCommand)+"' started as htcJobId '"+htcJobID+"'");
		}
		return htcJobID;
	}

	@Override
	public HtcJobID submitOptimizationJob(String jobName, File sub_file_internal, File sub_file_external,File optProblemInput,File optProblemOutput) throws ExecutableException{
		try {
			if (LG.isDebugEnabled()) {
				LG.debug("generating local SLURM submit script for jobName="+jobName);
			}
//			String text = generateScript(jobName, commandSet, ncpus, memSizeMB, postProcessingCommands, simTask);

			String primaryDataDirExternal = PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirExternalProperty);
		    String htclogdir_external = PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirExternal);
			String serverid=PropertyLoader.getRequiredProperty(PropertyLoader.vcellServerIDProperty);
			String softwareVersion=PropertyLoader.getRequiredProperty(PropertyLoader.vcellSoftwareVersion);
			String remote_singularity_image = PropertyLoader.getRequiredProperty(PropertyLoader.vcellbatch_singularity_image);
			String slurm_singularity_local_image_filepath = remote_singularity_image;
//			String docker_image = PropertyLoader.getRequiredProperty(PropertyLoader.vcellbatch_docker_name);
			String slurm_tmpdir = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_tmpdir);
			String slurm_central_singularity_dir = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_central_singularity_dir);
			String slurm_local_singularity_dir = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_local_singularity_dir);
			String simDataDirArchiveHost = PropertyLoader.getRequiredProperty(PropertyLoader.simDataDirArchiveHost);
			File slurm_singularity_central_filepath = new File(slurm_central_singularity_dir,new File(slurm_singularity_local_image_filepath).getName());

			HtcProxy.MemLimitResults memoryMBAllowed = new HtcProxy.MemLimitResults(256, "Optimization Default");
			String[] environmentVars = new String[] {
					"java_mem_Xmx="+memoryMBAllowed.getMemLimit()+"M",
					"datadir_external="+primaryDataDirExternal,
					"htclogdir_external="+htclogdir_external,
					"softwareVersion="+softwareVersion,
					"serverid="+serverid
			};

			LineStringBuilder lsb = new LineStringBuilder();
			slurmScriptInit(jobName, false, memoryMBAllowed, lsb);
			slurmInitSingularity(lsb, primaryDataDirExternal, htclogdir_external, softwareVersion,
					slurm_singularity_local_image_filepath, slurm_tmpdir, slurm_central_singularity_dir,
					slurm_local_singularity_dir, simDataDirArchiveHost, slurm_singularity_central_filepath,
					environmentVars);

			lsb.write("   cmd_prefix=\"$container_prefix\"");
			lsb.write("echo \"cmd_prefix is '${cmd_prefix}'\"");
			lsb.append("echo command = ");
			lsb.write("${cmd_prefix}" + "");
			
			lsb.write("${cmd_prefix}" + " ParamOptemize_python"+" "+optProblemInput.getAbsolutePath()+" "+optProblemOutput.getAbsolutePath());

			File tempFile = File.createTempFile("tempSubFile", ".sub");

			writeUnixStyleTextFile(tempFile, lsb.toString());

			// move submission file to final location (either locally or remotely).
			if (LG.isDebugEnabled()) {
				LG.debug("moving local SLURM submit file '"+tempFile.getAbsolutePath()+"' to remote file '"+sub_file_external+"'");
			}
			FileUtils.copyFile(tempFile, sub_file_internal);
			tempFile.delete();
		} catch (IOException ex) {
			ex.printStackTrace(System.out);
			return null;
		}

		return submitJobFile(sub_file_external);
	}


}
