package cbit.vcell.message.server.htc.slurm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.vcell.util.FileUtils;
import org.vcell.util.document.KeyValue;
import org.vcell.util.exe.ExecutableException;

import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.cmd.CommandService.CommandOutput;
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
		final String JOB_CMD_SACCT = PropertyLoader.getProperty(PropertyLoader.slurm_cmd_squeue,"sacct");
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
		if (!line.equals("JobID|JobName|State")){
			throw new RuntimeException("unexpected first line from sacct: '"+line+"'");
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
	String generateScript(String jobName, ExecutableCommand.Container commandSet, int ncpus, double memSizeMB, Collection<PortableCommand> postProcessingCommands, SimulationTask simTask) {
		final boolean isParallel = ncpus > 1;

		//SlurmProxy ultimately instantiated from {vcellroot}/docker/build/Dockerfile-submit-dev by way of cbit.vcell.message.server.batch.sim.HtcSimulationWorker
		String vcellUserid = simTask.getUser().getName();
		KeyValue simID = simTask.getSimulationInfo().getSimulationVersion().getVersionKey();
		SolverDescription solverDescription = simTask.getSimulation().getSolverTaskDescription().getSolverDescription();
		MemLimitResults memoryMBAllowed = HtcProxy.getMemoryLimit(vcellUserid,simID,solverDescription,memSizeMB);

		LineStringBuilder lsb = new LineStringBuilder();

		lsb.write("#!/usr/bin/bash");
		File htcLogDirExternal = new File(PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirExternal));
		if(simTask.isPowerUser()) {
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
		lsb.write("echo \"1 date=`date`\"");
		lsb.write("# VCell SlurmProxy memory limit source="+memoryMBAllowed.getMemLimitSource());

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
		lsb.write("       echo \"local singularity image $localSingularityImage not found, trying to download to hpc from central\"");
		lsb.write("       mkdir -p "+slurm_local_singularity_dir);
		lsb.write("       singularitytempfile=$(mktemp -up "+slurm_central_singularity_dir+")");
		lsb.write("       cp "+slurm_singularity_central_filepath.getAbsolutePath()+" ${singularitytempfile}");
		lsb.write("       mv -n ${singularitytempfile} "+slurm_singularity_local_image_filepath);
		lsb.write("       rm -f ${singularitytempfile}");
		lsb.write("       if [ ! -e \"$localSingularityImage\" ]; then");		
		lsb.write("           echo \"Failed to copy $localSingularityImage to hpc from central\"");
		lsb.write("           exit 1");
		lsb.write("       fi");
		lsb.write("   fi");
		StringBuffer singularityEnvironmentVars = new StringBuffer();
		for (String envVar : environmentVars) {
			singularityEnvironmentVars.append(" --env "+envVar);
		}
		lsb.write("   container_prefix=\"singularity run --bind "+primaryDataDirExternal+":/simdata --bind "+htclogdir_external+":/htclogs  --bind "+slurm_tmpdir+":/solvertmp $localSingularityImage "+singularityEnvironmentVars+" \"");
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

		lsb.newline();

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
		
		if (isParallel) {
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
		}
		lsb.newline();
	
		final boolean hasExitProcessor = commandSet.hasExitCodeCommand();
	//	lsb.write("run_in_container=\"singularity /path/to/data:/simdata /path/to/image/vcell-batch.img);
		if (hasExitProcessor) {
			ExecutableCommand exitCmd = commandSet.getExitCodeCommand();
			exitCmd.stripPathFromCommand();
			lsb.write("callExitProcessor( ) {");
			lsb.append("\techo exitCommand = ");
			lsb.write("${container_prefix}" + exitCmd.getJoinedCommands("$1"));
			lsb.append('\t');
			lsb.write("${container_prefix}" + exitCmd.getJoinedCommands());
			lsb.write("}");
			lsb.write("echo");
		}

		for (ExecutableCommand ec: commandSet.getExecCommands()) {
			lsb.write("echo");
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
			lsb.write("    "+"${cmd_prefix}" + cmd);
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
		}

		Objects.requireNonNull(postProcessingCommands);
		PortableCommandWrapper.insertCommands(lsb.sb, postProcessingCommands);
		lsb.newline();
		if (hasExitProcessor) {
			lsb.write("callExitProcessor 0");
		}
		lsb.write("echo \"7 date=`date`\"");
		lsb.newline();
		return lsb.sb.toString();
	}

	@Override
	public HtcJobID submitJob(String jobName, File sub_file_internal, File sub_file_external, ExecutableCommand.Container commandSet, int ncpus, double memSizeMB, Collection<PortableCommand> postProcessingCommands, SimulationTask simTask) throws ExecutableException {
		try {
			if (LG.isDebugEnabled()) {
				LG.debug("generating local SLURM submit script for jobName="+jobName);
			}
			String text = generateScript(jobName, commandSet, ncpus, memSizeMB, postProcessingCommands, simTask);

			File tempFile = File.createTempFile("tempSubFile", ".sub");

			writeUnixStyleTextFile(tempFile, text);

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


}
