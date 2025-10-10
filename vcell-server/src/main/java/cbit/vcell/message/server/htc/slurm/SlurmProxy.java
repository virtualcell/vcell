package cbit.vcell.message.server.htc.slurm;

import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.cmd.CommandService.CommandOutput;
import cbit.vcell.message.server.cmd.CommandServiceLocal;
import cbit.vcell.message.server.cmd.CommandServiceSshNative;
import cbit.vcell.message.server.htc.HtcException;
import cbit.vcell.message.server.htc.HtcJobNotFoundException;
import cbit.vcell.message.server.htc.HtcJobStatus;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.server.HtcJobID.BatchSystemType;
import cbit.vcell.simdata.PortableCommand;
import cbit.vcell.simdata.PortableCommandWrapper;
import cbit.vcell.solver.LangevinSimulationOptions;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solvers.AbstractSolver;
import cbit.vcell.solvers.ExecutableCommand;
import edu.uchc.connjur.wb.LineStringBuilder;
import org.vcell.util.document.KeyValue;
import org.vcell.util.exe.ExecutableException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.*;

public class SlurmProxy extends HtcProxy {
	
	private final static int SCANCEL_JOB_NOT_FOUND_RETURN_CODE = 1;
	private final static String SCANCEL_UNKNOWN_JOB_RESPONSE = "does not exist";
	protected final static String SLURM_SUBMISSION_FILE_EXT = ".slurm.sub";

	private enum CommandContainer {
		SOLVER("${solver_container_prefix} "),
		BATCH("${batch_container_prefix} ");

		private final String prefix;
		CommandContainer(String prefix) {
			this.prefix = prefix;
		}
		public String getPrefix() {
			return prefix;
		}
	}

	private record SingularityBinding(String hostPath, String containerPath) {
		public String getBinding() {
			return "--bind "+hostPath+":"+containerPath;
		}
	}
	
	public SlurmProxy(CommandService commandService, String htcUser) {
		super(commandService, htcUser);
	}

	public static SlurmProxy createRemoteProxy(){
		ArrayList<String> htcDispatchHostNames = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(PropertyLoader.getRequiredProperty(PropertyLoader.htcHosts),", ");
		while(st.hasMoreElements()) {
			htcDispatchHostNames.add(st.nextToken());
		}
		String htcUser = PropertyLoader.getRequiredProperty(PropertyLoader.htcUser);
		File sshKeyFile = new File(PropertyLoader.getRequiredProperty(PropertyLoader.htcUserKeyFile));

		return createRemoteProxy(htcDispatchHostNames, htcUser,sshKeyFile);
	}

	public static SlurmProxy createRemoteProxy(List<String> htcDispatchHostNames, String sshUser, File sshKeyFile) {
		CommandServiceSshNative commandService;
		try {
			commandService = new CommandServiceSshNative(htcDispatchHostNames.toArray(new String[0]),sshUser,sshKeyFile);
			commandService.command(new String[] { "/usr/bin/env bash -c ls | head -5" });
			LG.trace("SSH Connection test passed with installed keyfile, running ls as user "+sshUser);
		} catch (Exception e) {
			LG.warn(e);
			try {
				commandService = new CommandServiceSshNative(htcDispatchHostNames.toArray(new String[0]),sshUser,sshKeyFile,new File("/root"));
				CommandOutput commandOutput = commandService.command(new String[] { "/usr/bin/env bash -c ls | head -5" });
				LG.trace("SSH Connection test passed after installing keyfile, running ls as user "+sshUser);
			} catch (Exception e2) {
				String msg = "failed to establish an ssh command connection to "+htcDispatchHostNames+" as user '"+sshUser+"' using key '"+sshKeyFile+"'";
				LG.error(msg, e2);
				throw new RuntimeException(msg, e2);
			}
		}
		AbstractSolver.bMakeUserDirs = false; // can't make user directories, they are remote.
		return new SlurmProxy(commandService, PropertyLoader.getRequiredProperty(PropertyLoader.htcUser));
	}

	public static SlurmProxy createLocalProxy(List<String> htcDispatchHostNames, String sshUser, File sshKeyFile) {
		CommandService commandService = new CommandServiceLocal();
		return new SlurmProxy(commandService, PropertyLoader.getRequiredProperty(PropertyLoader.htcUser));
	}

	@Override
	public void killJobSafe(HtcJobInfo htcJobInfo) throws ExecutableException, HtcException {
		final String JOB_CMD_DELETE = PropertyLoader.getProperty(PropertyLoader.slurm_cmd_scancel,"scancel");
		String[] cmd = new String[]{JOB_CMD_DELETE,"-u",getHtcUser(), "--jobname", htcJobInfo.getJobName(), Long.toString(htcJobInfo.getHtcJobID().getJobNumber())};
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
		String[] cmd = new String[]{JOB_CMD_DELETE, "-u", getHtcUser(), Long.toString(htcJobId.getJobNumber())};
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
		String[] cmds = {JOB_CMD_SQUEUE,"-p",partition,"-u",getHtcUser(),"-O","jobid:25,name:25,state:13,batchhost"};
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
		String[] cmds = {JOB_CMD_SACCT,"-P","-u",getHtcUser(),"-j",jobList,"-o","jobid%25,jobname%25,state%13"};
		CommandOutput commandOutput = commandService.command(cmds);
		
		String output = commandOutput.getStandardOutput();
		Map<HtcJobInfo, HtcJobStatus> statusMap = extractJobIds(output);
		//
		// HtcJobIDs can be reused by Slurm, so make sure it has the correct JobName also.
		//
		Iterator<HtcJobInfo> keyIterator = statusMap.keySet().iterator();
		while (keyIterator.hasNext()) {
			HtcJobInfo parsedHtcJobInfo = keyIterator.next();
			if (!requestedHtcJobInfos.contains(parsedHtcJobInfo)) {
				keyIterator.remove();
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

	private static int getRoundedMemoryLimit(int memPerTaskMB) {
		int rawLimit = (int)(memPerTaskMB * 0.9);
		// Round down to nearest 100 MB
		return (rawLimit / 100) * 100;
	}

	String  generateLangevinBatchScript(String jobName, ExecutableCommand.Container commandSet, double memSizeMB,
										Collection<PortableCommand> postProcessingCommands, SimulationTask simTask) {

		String vcellUserid = simTask.getUser().getName();
		KeyValue simID = simTask.getSimulationInfo().getSimulationVersion().getVersionKey();
		SolverTaskDescription std = simTask.getSimulation().getSolverTaskDescription();
		LangevinSimulationOptions lso = std.getLangevinSimulationOptions();
		int totalNumberOfJobs = lso.getTotalNumberOfJobs();
		int numberOfConcurrentTasks = lso.getNumberOfConcurrentJobs();
		SolverDescription solverDescription = std.getSolverDescription();
//		MemLimitResults memoryMBAllowed = HtcProxy.getMemoryLimit(vcellUserid, simID, solverDescription, memSizeMB, simTask.isPowerUser());

		int memPerTask = 2048; // in MB
		LineStringBuilder slurmCommands = new LineStringBuilder();
		slurmBatchScriptInit(jobName, simTask.isPowerUser(), memPerTask, numberOfConcurrentTasks, slurmCommands);
		System.out.println(slurmCommands.sb.toString());

		int javaMemXmx = getRoundedMemoryLimit(memPerTask);
		LineStringBuilder singularityCommands = buildSingularitySlurmSection(simTask, javaMemXmx);
		System.out.println(singularityCommands.sb.toString());


		String langevinBatchString = "";

		return langevinBatchString;
	}

	SbatchSolverComponents generateScript(String jobName, ExecutableCommand.Container commandSet, double memSizeMB, Collection<PortableCommand> postProcessingCommands, SimulationTask simTask) {

		//SlurmProxy ultimately instantiated from {vcellroot}/docker/build/Dockerfile-submit-dev by way of cbit.vcell.message.server.batch.sim.HtcSimulationWorker
		String vcellUserid = simTask.getUser().getName();
		KeyValue simID = simTask.getSimulationInfo().getSimulationVersion().getVersionKey();
		SolverDescription solverDescription = simTask.getSimulation().getSolverTaskDescription().getSolverDescription();
		MemLimitResults memoryMBAllowed = HtcProxy.getMemoryLimit(vcellUserid, simID, solverDescription, memSizeMB, simTask.isPowerUser());

		LineStringBuilder slurmCommands = new LineStringBuilder();
		slurmScriptInit(jobName, simTask.isPowerUser(), memoryMBAllowed, slurmCommands);

		String primaryDataDirExternal = PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirExternalProperty);
		String secondaryDataDirExternal = PropertyLoader.getRequiredProperty(PropertyLoader.secondarySimDataDirExternalProperty);

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
		String slurm_tmpdir = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_tmpdir);
		String slurm_singularity_cachedir = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_singularity_cachedir);
		String slurm_singularity_pullfolder = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_singularity_pullfolder);
		String slurm_singularity_module_name = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_singularity_module_name);
		String simDataDirArchiveExternal = PropertyLoader.getRequiredProperty(PropertyLoader.simDataDirArchiveExternal);
		String simDataDirArchiveInternal = PropertyLoader.getRequiredProperty(PropertyLoader.simDataDirArchiveInternal);

		String solverName = simTask.getSimulation().getSolverTaskDescription().getSolverDescription().name();
		List<String> vcellfvsolver_solverList = List.of(PropertyLoader.getRequiredProperty(PropertyLoader.htc_vcellfvsolver_solver_list).split(","));
		List<String> vcellsolvers_solverList = List.of(PropertyLoader.getRequiredProperty(PropertyLoader.htc_vcellsolvers_solver_list).split(","));
		List<String> vcellbatch_solverList = List.of(PropertyLoader.getRequiredProperty(PropertyLoader.htc_vcellbatch_solver_list).split(","));

		final String solverDockerName;
		final String batchDockerName = PropertyLoader.getRequiredProperty(PropertyLoader.htc_vcellbatch_docker_name);
		if (vcellfvsolver_solverList.contains(solverName)) {
			solverDockerName = PropertyLoader.getRequiredProperty(PropertyLoader.htc_vcellfvsolver_docker_name);
		} else if (vcellsolvers_solverList.contains(solverName)) {
			solverDockerName = PropertyLoader.getRequiredProperty(PropertyLoader.htc_vcellsolvers_docker_name);
		} else if (vcellbatch_solverList.contains(solverName)) {
			solverDockerName = batchDockerName;
		} else {
			throw new RuntimeException("solverName="+solverName+" not in vcellfvsolver_solverList="+vcellfvsolver_solverList+" or vcellsolvers_solverList="+vcellsolvers_solverList+" or vcellbatch_solverList="+vcellbatch_solverList);
		}

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
				"primary_datadir_external="+primaryDataDirExternal,
				"secondary_datadir_external="+secondaryDataDirExternal,
				"htclogdir_external="+htclogdir_external,
				"softwareVersion="+softwareVersion,
				"serverid="+serverid
		};

		List<SingularityBinding> bindings = List.of(
				new SingularityBinding(primaryDataDirExternal, "/simdata"),
				new SingularityBinding(secondaryDataDirExternal, "/simdata_secondary"),
				new SingularityBinding(simDataDirArchiveExternal, simDataDirArchiveInternal),
				new SingularityBinding(htclogdir_external, "/htclogs"),
				new SingularityBinding(slurm_tmpdir, "/solvertmp")
		);

		LineStringBuilder lsb = new LineStringBuilder();
		LineStringBuilder singularityLSB = new LineStringBuilder();
		slurmInitSingularity(singularityLSB,
				solverDockerName, Optional.of(batchDockerName), bindings,
				slurm_tmpdir, slurm_singularity_cachedir, slurm_singularity_pullfolder,
				slurm_singularity_module_name, environmentVars);

		LineStringBuilder sendFailMsgLSB = new LineStringBuilder();
		sendFailMsgScript(simTask, sendFailMsgLSB, jmshost_sim_external, jmsport_sim_external, jmsuser, jmspswd);

		for (SingularityBinding binding : bindings) {
			commandSet.translatePaths(new File(binding.hostPath), new File(binding.containerPath));
		}

		final boolean hasExitProcessor = commandSet.hasExitCodeCommand();
	//	lsb.write("run_in_container=\"singularity /path/to/data:/simdata /path/to/image/vcell-batch.img);
		LineStringBuilder callExitLSB = new LineStringBuilder();
		if (hasExitProcessor) {
			callExitScript(commandSet, callExitLSB);
		}

		LineStringBuilder preProcessLSB = new LineStringBuilder();
		for (ExecutableCommand ec: commandSet.getExecCommands()) {
			ExecutableCommand.Container commandSet2 = new ExecutableCommand.Container();
			if(ec.getCommands().get(0).equals("JavaPreprocessor64")) {
				execCommandScript(preProcessLSB, hasExitProcessor, ec, "${batch_container_prefix}");
			}else {
				execCommandScript(lsb, hasExitProcessor, ec, "${solver_container_prefix}");
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

	private LineStringBuilder buildSingularitySlurmSection(SimulationTask simTask, int memoryLimit) {
		String primaryDataDirExternal = PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirExternalProperty);
		String secondaryDataDirExternal = PropertyLoader.getRequiredProperty(PropertyLoader.secondarySimDataDirExternalProperty);

		String jmshost_sim_external = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimHostExternal);
		String jmsport_sim_external = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimPortExternal);
		String jmsrestport_sim_external = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimRestPortExternal);
		String htclogdir_external = PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirExternal);
		String jmsuser = PropertyLoader.getRequiredProperty(PropertyLoader.jmsUser);
		String jmspswd = PropertyLoader.getSecretValue(PropertyLoader.jmsPasswordValue, PropertyLoader.jmsPasswordFile);
		String jmsblob_minsize = PropertyLoader.getProperty(PropertyLoader.jmsBlobMessageMinSize, "10000");
		String mongodbhost_external = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbHostExternal);
		String mongodbport_external = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbPortExternal);
		String mongodb_database = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbDatabase);
		String serverid = PropertyLoader.getRequiredProperty(PropertyLoader.vcellServerIDProperty);
		String softwareVersion = PropertyLoader.getRequiredProperty(PropertyLoader.vcellSoftwareVersion);
		String slurm_tmpdir = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_tmpdir);
		String slurm_singularity_cachedir = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_singularity_cachedir);
		String slurm_singularity_pullfolder = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_singularity_pullfolder);
		String slurm_singularity_module_name = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_singularity_module_name);
		String simDataDirArchiveExternal = PropertyLoader.getRequiredProperty(PropertyLoader.simDataDirArchiveExternal);
		String simDataDirArchiveInternal = PropertyLoader.getRequiredProperty(PropertyLoader.simDataDirArchiveInternal);

		String solverName = simTask.getSimulation().getSolverTaskDescription().getSolverDescription().name();
		List<String> vcellfvsolver_solverList = List.of(PropertyLoader.getRequiredProperty(PropertyLoader.htc_vcellfvsolver_solver_list).split(","));
		List<String> vcellsolvers_solverList = List.of(PropertyLoader.getRequiredProperty(PropertyLoader.htc_vcellsolvers_solver_list).split(","));
		List<String> vcellbatch_solverList = List.of(PropertyLoader.getRequiredProperty(PropertyLoader.htc_vcellbatch_solver_list).split(","));

		final String solverDockerName;
		final String batchDockerName = PropertyLoader.getRequiredProperty(PropertyLoader.htc_vcellbatch_docker_name);
		if (vcellfvsolver_solverList.contains(solverName)) {
			solverDockerName = PropertyLoader.getRequiredProperty(PropertyLoader.htc_vcellfvsolver_docker_name);
		} else if (vcellsolvers_solverList.contains(solverName)) {
			solverDockerName = PropertyLoader.getRequiredProperty(PropertyLoader.htc_vcellsolvers_docker_name);
		} else if (vcellbatch_solverList.contains(solverName)) {
			solverDockerName = batchDockerName;
		} else {
			throw new RuntimeException("solverName=" + solverName + " not in vcellfvsolver_solverList=" + vcellfvsolver_solverList +
					" or vcellsolvers_solverList=" + vcellsolvers_solverList + " or vcellbatch_solverList=" + vcellbatch_solverList);
		}

		String[] environmentVars = new String[] {
				"java_mem_Xmx=" + memoryLimit + "M",
				"jmshost_sim_internal=" + jmshost_sim_external,
				"jmsport_sim_internal=" + jmsport_sim_external,
				"jmsrestport_sim_internal=" + jmsrestport_sim_external,
				"jmsuser=" + jmsuser,
				"jmspswd=" + jmspswd,
				"jmsblob_minsize=" + jmsblob_minsize,
				"mongodbhost_internal=" + mongodbhost_external,
				"mongodbport_internal=" + mongodbport_external,
				"mongodb_database=" + mongodb_database,
				"primary_datadir_external=" + primaryDataDirExternal,
				"secondary_datadir_external=" + secondaryDataDirExternal,
				"htclogdir_external=" + htclogdir_external,
				"softwareVersion=" + softwareVersion,
				"serverid=" + serverid
		};

		List<SingularityBinding> bindings = List.of(
				new SingularityBinding(primaryDataDirExternal, "/simdata"),
				new SingularityBinding(secondaryDataDirExternal, "/simdata_secondary"),
				new SingularityBinding(simDataDirArchiveExternal, simDataDirArchiveInternal),
				new SingularityBinding(htclogdir_external, "/htclogs"),
				new SingularityBinding(slurm_tmpdir, "/solvertmp")
		);

		LineStringBuilder singularityLSB = new LineStringBuilder();
		appendSingularitySetup(singularityLSB,
				solverDockerName, Optional.of(batchDockerName),
				bindings,
				slurm_tmpdir, slurm_singularity_cachedir, slurm_singularity_pullfolder,
				slurm_singularity_module_name, environmentVars);

		return singularityLSB;
	}

	private void appendSingularitySetup(LineStringBuilder lsb,
										String solverDockerName, Optional<String> batchDockerName,
										List<SingularityBinding> bindings,
										String slurm_tmpdir,
										String slurm_singularity_cachedir,
										String slurm_singularity_pullfolder,
										String singularity_module_name,
										String[] environmentVars) {

		lsb.write("TMPDIR=" + slurm_tmpdir);
		lsb.write("if [ ! -e $TMPDIR ]; then mkdir -p $TMPDIR ; fi");

		// Initialize Singularity
		lsb.write("echo `hostname`");
		lsb.write("export MODULEPATH=/isg/shared/modulefiles:/tgcapps/modulefiles");
		lsb.write("source /usr/share/Modules/init/bash");
		lsb.write("module load " + singularity_module_name);
		lsb.write("export SINGULARITY_CACHEDIR=" + slurm_singularity_cachedir);
		lsb.write("export SINGULARITY_PULLFOLDER=" + slurm_singularity_pullfolder);
		// lsb.write("TEMP_DIRNAME=$(mktemp --directory --tmpdir=/local)");

		lsb.write("echo \"job running on host `hostname -f`\"");
		lsb.write("echo \"id is `id`\"");
		lsb.write("echo ENVIRONMENT");
		lsb.write("env");
		lsb.newline();

		boolean bFirstBind = true;
		for (SingularityBinding binding : bindings) {
			if (bFirstBind) {
				lsb.write("container_bindings=\"--bind " + binding.hostPath + ":" + binding.containerPath + " \"");
				bFirstBind = false;
			} else {
				lsb.write("container_bindings+=\"--bind " + binding.hostPath + ":" + binding.containerPath + " \"");
			}
		}

		boolean bFirstEnv = true;
		for (String envVar : environmentVars) {
			if (bFirstEnv) {
				lsb.write("container_env=\"--env " + envVar + " \"");
				bFirstEnv = false;
			} else {
				lsb.write("container_env+=\"--env " + envVar + " \"");
			}
		}
		lsb.write("solver_docker_name="+solverDockerName);
		if (batchDockerName.isPresent()) {
			lsb.write("batch_docker_name="+batchDockerName.get());
		}
	}



	private void callExitScript(ExecutableCommand.Container commandSet, LineStringBuilder lsb) {
		lsb.newline();
		ExecutableCommand exitCmd = commandSet.getExitCodeCommand();
		exitCmd.stripPathFromCommand();
		lsb.write("callExitProcessor( ) {");
		lsb.append('\t');
		lsb.write("${batch_container_prefix} " + exitCmd.getJoinedCommands("$1").trim());
		lsb.write("}");
		lsb.newline();
		lsb.newline();
	}


	private void sendFailMsgScript(SimulationTask simTask, LineStringBuilder lsb, String jmshost_sim_external,
			String jmsport_sim_external, String jmsuser, String jmspswd) {
		lsb.newline();
		lsb.write("sendFailureMsg() {");
		lsb.write("  ${batch_container_prefix} " +
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
		lsb.newline();
	}


	private void execCommandScript(LineStringBuilder lsb,final boolean hasExitProcessor, ExecutableCommand ec, String container_prefix) {
		ec.stripPathFromCommand();
		String cmd= ec.getJoinedCommands();
		lsb.write(container_prefix + " " + cmd.trim());
		lsb.write("stat=$?");
		lsb.append("echo returned $stat");
		lsb.newline();
		lsb.write("if [ $stat -ne 0 ]; then");
		if (hasExitProcessor) {
			lsb.write("\tcallExitProcessor $stat");
		}
		lsb.write("\techo returning $stat to Slurm");
		lsb.write("\texit $stat");
		lsb.write("fi");
		lsb.newline();
	}


	private void slurmInitSingularity(LineStringBuilder lsb,
				  String solverDockerName, Optional<String> batchDockerName, List<SingularityBinding> bindings,
				  String slurm_tmpdir, String slurm_singularity_cachedir, String slurm_singularity_pullfolder,
				  String singularity_module_name, String[] environmentVars) {
		lsb.write("TMPDIR="+slurm_tmpdir);
		lsb.write("if [ ! -e $TMPDIR ]; then mkdir -p $TMPDIR ; fi");
		
		//
		// Initialize Singularity
		//
		lsb.write("echo `hostname`");
		lsb.write("export MODULEPATH=/isg/shared/modulefiles:/tgcapps/modulefiles");
		lsb.write("source /usr/share/Modules/init/bash");
		lsb.write("module load "+singularity_module_name);
		lsb.write("export SINGULARITY_CACHEDIR="+slurm_singularity_cachedir);
		lsb.write("export SINGULARITY_PULLFOLDER="+slurm_singularity_pullfolder);
//		lsb.write("TEMP_DIRNAME=$(mktemp --directory --tmpdir=/local)");
		
		lsb.write("echo \"job running on host `hostname -f`\"");
		lsb.write("echo \"id is `id`\"");
		lsb.write("echo ENVIRONMENT");
		lsb.write("env");
		lsb.newline();

		boolean bFirstBind=true;
		for (SingularityBinding binding : bindings) {
			if (bFirstBind) {
				lsb.write("container_bindings=\"--bind " + binding.hostPath + ":" + binding.containerPath + " \"");
				bFirstBind = false;
			}else{
				lsb.write("container_bindings+=\"--bind " + binding.hostPath + ":" + binding.containerPath + " \"");
			}
		}
		boolean bFirstEnv=true;
		for (String envVar : environmentVars) {
			if (bFirstEnv) {
				lsb.write("container_env=\"--env " + envVar + " \"");
				bFirstEnv = false;
			}else {
				lsb.write("container_env+=\"--env " + envVar + " \"");
			}
		}
		lsb.write("solver_docker_name="+solverDockerName);
		lsb.write("solver_container_prefix=\"singularity run --containall " +
				"${container_bindings} " +
				"${container_env} " +
				"docker://${solver_docker_name}\"");
		if (batchDockerName.isPresent()) {
			lsb.write("batch_docker_name="+batchDockerName.get());
			lsb.write("batch_container_prefix=\"singularity run --containall " +
				"${container_bindings} " +
				"${container_env} " +
				"docker://${batch_docker_name}\"");
		}
		lsb.newline();
	}


	private void slurmScriptInit(String jobName, boolean bPowerUser, MemLimitResults memoryMBAllowed,
			LineStringBuilder lsb) {
		lsb.write("#!/usr/bin/bash");
		File htcLogDirExternal = new File(PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirExternal));
		if(bPowerUser) {
			String partition_pu = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_partition_pu);
			String reservation_pu = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_reservation_pu);
			String qos_pu = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_qos_pu);
			lsb.write("#SBATCH --partition=" + partition_pu);
//			lsb.write("#SBATCH --reservation=" + reservation_pu);		
			lsb.write("#SBATCH --qos=" + qos_pu);			
		}else {
			String partition = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_partition);
			String reservation = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_reservation);
			String qos = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_qos);
			lsb.write("#SBATCH --partition=" + partition);
			lsb.write("#SBATCH --reservation=" +reservation);
			lsb.write("#SBATCH --qos=" +qos);
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
		lsb.write("# VCell SlurmProxy memory limit source='"+memoryMBAllowed.getMemLimitSource()+"'");
	}
	private void slurmBatchScriptInit(String jobName, boolean bPowerUser, int memParTask, int numberOfConcurrentTasks, LineStringBuilder lsb) {
		lsb.write("#!/usr/bin/bash");
		File htcLogDirExternal = new File(PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirExternal));

		if (bPowerUser) {
			String partition_pu = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_partition_pu);
			String qos_pu = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_qos_pu);
			lsb.write("#SBATCH --partition=" + partition_pu);
			lsb.write("#SBATCH --qos=" + qos_pu);
		} else {
			String partition = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_partition);
			String reservation = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_reservation);
			String qos = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_qos);
			lsb.write("#SBATCH --partition=" + partition);
			lsb.write("#SBATCH --reservation=" + reservation);
			lsb.write("#SBATCH --qos=" + qos);
		}

		lsb.write("#SBATCH -J " + jobName);
		lsb.write("#SBATCH -o " + new File(htcLogDirExternal, jobName + ".slurm.log").getAbsolutePath());
		lsb.write("#SBATCH -e " + new File(htcLogDirExternal, jobName + ".slurm.log").getAbsolutePath());
		lsb.write("#SBATCH --ntasks=" + numberOfConcurrentTasks);
		lsb.write("#SBATCH --cpus-per-task=1");
		lsb.write("#SBATCH --mem-per-cpu=" + memParTask + "M");
		lsb.write("#SBATCH --nodes=1");
		lsb.write("#SBATCH --time=24:00:00");
		lsb.write("#SBATCH --no-kill");
		lsb.write("#SBATCH --no-requeue");

		String nodelist = PropertyLoader.getProperty(PropertyLoader.htcNodeList, null);
		if (nodelist != null && nodelist.trim().length() > 0) {
			lsb.write("#SBATCH --nodelist=" + nodelist);
		}
	}


	@Override
	public HtcJobID submitJob(String jobName, File sub_file_as_internal_path, File sub_file_with_external_path, ExecutableCommand.Container commandSet, int ncpus, double memSizeMB, Collection<PortableCommand> postProcessingCommands, SimulationTask simTask,File primaryUserDirExternal) throws ExecutableException, IOException {
		SolverTaskDescription std = simTask.getSimulationJob().getSimulation().getSolverTaskDescription();
		String scriptText;
		if(std.getSolverDescription().isLangevinSolver() && std.getLangevinSimulationOptions().getTotalNumberOfJobs() > 1) {
			scriptText = createBatchJobScriptText(jobName, commandSet, ncpus, memSizeMB, postProcessingCommands, simTask);
		} else {
			scriptText = createJobScriptText(jobName, commandSet, ncpus, memSizeMB, postProcessingCommands, simTask);
		}
		Files.writeString(sub_file_as_internal_path.toPath(), scriptText);
		return submitJobFile(sub_file_with_external_path);
	}

	String createJobScriptText(String jobName, ExecutableCommand.Container commandSet, int ncpus, double memSizeMB, Collection<PortableCommand> postProcessingCommands, SimulationTask simTask) throws IOException {
		if (LG.isDebugEnabled()) {
			LG.debug("generating local SLURM submit script for jobName="+jobName);
		}
		SlurmProxy.SbatchSolverComponents sbatchSolverComponents = generateScript(jobName, commandSet, memSizeMB, postProcessingCommands, simTask);

		StringBuilder scriptContent = new StringBuilder();
		scriptContent.append(sbatchSolverComponents.getSingularityCommands());
		scriptContent.append(sbatchSolverComponents.getSendFailureMsgCommands());
		scriptContent.append(sbatchSolverComponents.getCallExitCommands());
		scriptContent.append(sbatchSolverComponents.getPreProcessCommands());
		scriptContent.append(sbatchSolverComponents.solverCommands);
		scriptContent.append(sbatchSolverComponents.getExitCommands());
		String substitutedSbatchCommands = sbatchSolverComponents.getSbatchCommands();
		String origScriptText =  substitutedSbatchCommands+"\n\n"+
				scriptContent.toString()+"\n\n"+
				"#Following commands (if any) are read by JavaPostProcessor64\n"+
				sbatchSolverComponents.postProcessCommands+"\n";
		String scriptText = toUnixStyleText(origScriptText);
		return scriptText;
	}

	String createBatchJobScriptText(String jobName, ExecutableCommand.Container commandSet, int ncpus, double memSizeMB, Collection<PortableCommand> postProcessingCommands, SimulationTask simTask) throws IOException {
		if (LG.isDebugEnabled()) {
			LG.debug("generating local SLURM submit script for jobName="+jobName);
		}
		String origScriptText = generateLangevinBatchScript(jobName, commandSet, memSizeMB, postProcessingCommands, simTask);
		String scriptText = toUnixStyleText(origScriptText);
		return scriptText;
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
	public HtcJobID submitOptimizationJob(String jobName, File sub_file_internal, File sub_file_external,
										  File optProblemInputFile,File optProblemOutputFile,File optReportFile) throws ExecutableException{
		try {
			String scriptText = createOptJobScript(jobName, optProblemInputFile, optProblemOutputFile, optReportFile);
			LG.info("sub_file_internal: " + sub_file_internal.getAbsolutePath() +
					", sub_file_external: " + sub_file_external.getAbsolutePath() +
					", optProblemInput: " + optProblemInputFile.getAbsolutePath() +
					", optProblemOutput: " + optProblemOutputFile.getAbsolutePath() +
					", optReport: " + optReportFile.getAbsolutePath());
			Files.writeString(sub_file_internal.toPath(), scriptText);
		} catch (IOException ex) {
			LG.error(ex);
			return null;
		}

		return submitJobFile(sub_file_external);
	}

	String createOptJobScript(String jobName, File optProblemInputFile, File optProblemOutputFile, File optReportFile) throws IOException {
		final String primaryDataDirInternal = PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty);
		final String primaryDataDirExternal = PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirExternalProperty);
		final String htclogdir_external = PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirExternal);
		final String slurm_tmpdir = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_tmpdir);
		final String slurm_singularity_cachedir = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_singularity_cachedir);
		final String slurm_singularity_pullfolder = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_singularity_pullfolder);
		final String slurm_singularity_module_name = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_singularity_module_name);
		final String simDataDirArchiveExternal = PropertyLoader.getRequiredProperty(PropertyLoader.simDataDirArchiveExternal);
		final String simDataDirArchiveInternal = PropertyLoader.getRequiredProperty(PropertyLoader.simDataDirArchiveInternal);
		final String solverDockerName = PropertyLoader.getRequiredProperty(PropertyLoader.htc_vcellopt_docker_name);
		final Optional<String> batchDockerName = Optional.empty();

		MemLimitResults memoryMBAllowed = new MemLimitResults(256, "Optimization Default");
		String[] environmentVars = new String[] {
				"datadir_external="+primaryDataDirExternal,
		};

		LineStringBuilder lsb = new LineStringBuilder();
		slurmScriptInit(jobName, false, memoryMBAllowed, lsb);
		File optDataDir = optProblemInputFile.getParentFile();
		File optDataDirExternal = new File(optDataDir.getAbsolutePath().replace(primaryDataDirInternal, primaryDataDirExternal));
		if (!optDataDirExternal.exists() && !optDataDirExternal.mkdir()){
			LG.error("failed to make optimization data directory "+optDataDir.getAbsolutePath());
		}

		List<SingularityBinding> bindings = List.of(
				new SingularityBinding(optDataDirExternal.getAbsolutePath(), "/simdata"),
				new SingularityBinding(slurm_tmpdir, "/solvertmp"),
				new SingularityBinding(htclogdir_external, "/htclogs"),
				new SingularityBinding(simDataDirArchiveExternal, simDataDirArchiveInternal)
		);

		slurmInitSingularity(lsb, solverDockerName, batchDockerName,
				bindings, slurm_tmpdir, slurm_singularity_cachedir, slurm_singularity_pullfolder,
				slurm_singularity_module_name, environmentVars);

		lsb.write("cmd_prefix=\"$solver_container_prefix\"");
		lsb.write("echo \"cmd_prefix is '${cmd_prefix}'\"");
		lsb.append("echo command = ");
		lsb.write("${cmd_prefix}" + "");

		String optProblemInput_container_filename = optProblemInputFile.getAbsolutePath().replace(optProblemInputFile.getParent(),"/simdata");
		String optProblemOutput_container_filename = optProblemOutputFile.getAbsolutePath().replace(optProblemOutputFile.getParent(),"/simdata");
		String optReport_container_filename = optReportFile.getAbsolutePath().replace(optReportFile.getParent(),"/simdata");
		lsb.write("${cmd_prefix} " + optProblemInput_container_filename + " " + optProblemOutput_container_filename + " " + optReport_container_filename);

        return toUnixStyleText(lsb.toString());
	}


}
