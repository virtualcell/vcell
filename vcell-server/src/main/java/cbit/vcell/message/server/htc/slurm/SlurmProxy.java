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
import org.vcell.util.exe.ExecutableException;

import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.cmd.CommandService.CommandOutput;
import cbit.vcell.message.server.htc.HtcException;
import cbit.vcell.message.server.htc.HtcJobNotFoundException;
import cbit.vcell.message.server.htc.HtcJobStatus;
import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.server.HtcJobID.BatchSystemType;
import cbit.vcell.simdata.PortableCommand;
import cbit.vcell.simdata.PortableCommandWrapper;
import cbit.vcell.solvers.ExecutableCommand;
import edu.uchc.connjur.wb.LineStringBuilder;

public class SlurmProxy extends HtcProxy {
	
	private final static int SCANCEL_JOB_NOT_FOUND_RETURN_CODE = 1;
	private final static String SCANCEL_UNKNOWN_JOB_RESPONSE = "does not exist";
	protected final static String SLURM_SUBMISSION_FILE_EXT = ".slurm.sub";
//	private Map<HtcJobID, JobInfoAndStatus> statusMap;
	
	//private static String Slurm_HOME = PropertyLoader.getRequiredProperty(PropertyLoader.htcSlurmHome);
	private static String Slurm_HOME = ""; // slurm commands should be in the path (empty prefix)
	private static String htcLogDirExternalString = PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirExternal);
	private static String MPI_HOME_EXTERNAL= PropertyLoader.getProperty(PropertyLoader.MPI_HOME_EXTERNAL,"");
	static {
//		if (!Slurm_HOME.endsWith("/")){
//			Slurm_HOME += "/";
//		}
		if (!htcLogDirExternalString.endsWith("/")){
			htcLogDirExternalString = htcLogDirExternalString+"/";
		}
	}

	public SlurmProxy(CommandService commandService, String htcUser) {
		super(commandService, htcUser);
	}


	@Override
	public void killJob(HtcJobID htcJobId) throws ExecutableException, HtcException {
		final String JOB_CMD_DELETE = PropertyLoader.getProperty(PropertyLoader.slurm_cmd_scancel,"scancel");
		String[] cmd = new String[]{Slurm_HOME + JOB_CMD_DELETE, Long.toString(htcJobId.getJobNumber())};
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

	@Override
	public Map<HtcJobID, JobInfoAndStatus> getRunningJobs() throws ExecutableException, IOException {
		final String JOB_CMD_SQUEUE = PropertyLoader.getProperty(PropertyLoader.slurm_cmd_squeue,"squeue");
		// squeue -p vcell -O jobid:25,name:25,state:13
		String[] cmds = {Slurm_HOME + JOB_CMD_SQUEUE,"-p","vcell","-O","jobid:25,name:25,state:13,batchhost"};
		CommandOutput commandOutput = commandService.command(cmds);

		String output = commandOutput.getStandardOutput();
		Map<HtcJobID, JobInfoAndStatus> statusMap = extractJobIdsFromSqueue(output);
		return statusMap;
	}

	static Map<HtcJobID, JobInfoAndStatus> extractJobIdsFromSqueue(String output) throws IOException {
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
		Map<HtcJobID, JobInfoAndStatus> statusMap = new HashMap<HtcJobID, JobInfoAndStatus>();
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
			String errorPath = null;
			String outputPath = null;
			HtcJobInfo htcJobInfo = new HtcJobInfo(htcJobID, true, jobName, errorPath, outputPath);
			HtcJobStatus htcJobStatus = new HtcJobStatus(SlurmJobStatus.parseStatus(state));
			statusMap.put(htcJobID, new JobInfoAndStatus(htcJobInfo, htcJobStatus));
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
	String generateScript(String jobName, ExecutableCommand.Container commandSet, int ncpus, double memSize, Collection<PortableCommand> postProcessingCommands) {
		final boolean isParallel = ncpus > 1;


		LineStringBuilder lsb = new LineStringBuilder();

		lsb.write("#!/usr/bin/env bash");
		String partition = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_partition);
		lsb.write("#SBATCH --partition=" + partition);
		lsb.write("#SBATCH -J " + jobName);
		lsb.write("#SBATCH -o " + htcLogDirExternalString+jobName+".slurm.log");
		lsb.write("#SBATCH -e " + htcLogDirExternalString+jobName+".slurm.log");
		String nodelist = PropertyLoader.getProperty(PropertyLoader.htcNodeList, null);
		if (nodelist!=null && nodelist.trim().length()>0) {
			lsb.write("#SBATCH --nodelist="+nodelist);
		}
		lsb.write("echo \"1 date=`date`\"");

		String primaryDataDirExternal = PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirExternalProperty);

		lsb.write("TMPDIR=/state/partition1");
		lsb.write("mkdir -p /state/partition1/singularityImages");
		lsb.write("if [[ $? -ne 0 ]]; then");
		lsb.write("    echo \"couldn't create /state/partition1/singularityImages temp directory for singularity\"");
		lsb.write("    exit 1");
		lsb.write("fi");
		lsb.write("echo \"using TMPDIR=$TMPDIR\"");
		
		//
		// Initialize Singularity
		//
		lsb.write("module load singularity");
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
		String jmshost_external = PropertyLoader.getRequiredProperty(PropertyLoader.jmsHostExternal);
		String jmsport_external = PropertyLoader.getRequiredProperty(PropertyLoader.jmsPortExternal);
		String jmsrestport_external = PropertyLoader.getRequiredProperty(PropertyLoader.jmsRestPortExternal);
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
		String docker_image = PropertyLoader.getRequiredProperty(PropertyLoader.vcellbatch_docker_name);
		String singularityImageName = new File(remote_singularity_image).getName();

		String[] environmentVars = new String[] {
				"jmshost_internal="+jmshost_external,
				"jmsport_internal="+jmsport_external,
				"jmsrestport_internal="+jmsrestport_external,
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
		
		lsb.write("container_prefix=");
		lsb.write("if command -v singularity >/dev/null 2>&1; then");
		lsb.write("   #");
		lsb.write("   # local copy of singularity image should already be present in ${TMPDIR}/singularityImages/ (pushed during deploy)");
		lsb.write("   #");
		lsb.write("   localSingularityImage=\"${TMPDIR}/singularityImages/"+singularityImageName+"\"");
		lsb.write("   if [ ! -e \"$localSingularityImage\" ]; then");
		lsb.write("       echo \"local singularity image $localSingularityImage not found\"");
		lsb.write("       exit 1");
		lsb.write("   fi");
		StringBuffer singularityEnvironmentVars = new StringBuffer();
		for (String envVar : environmentVars) {
			singularityEnvironmentVars.append(" --env "+envVar);
		}
		lsb.write("   container_prefix=\"singularity run --bind "+primaryDataDirExternal+":/simdata --bind "+htclogdir_external+":/htclogs $localSingularityImage "+singularityEnvironmentVars+" \"");
		lsb.write("else");
		StringBuffer dockerEnvironmentVars = new StringBuffer();
		for (String envVar : environmentVars) {
			dockerEnvironmentVars.append(" -e "+envVar);
		}
		lsb.write("   container_prefix=\"docker run --rm -v "+primaryDataDirExternal+":/simdata -v "+htclogdir_external+":/htclogs "+dockerEnvironmentVars+" "+docker_image+" \"");
		lsb.write("fi");
		lsb.write("echo \"container_prefix is '${container_prefix}'\"");
		lsb.write("echo \"3 date=`date`\"");

		lsb.newline();
		/**
		 * excerpt from vcell-batch Dockerfile
		 * 
		 * ENV softwareVersion=VCell_7.0_build_99 \
		 *   serverid=TEST2 \
		 *   jmsurl="failover:(tcp://vcell-service.cam.uchc.edu:61616)" \
		 *   jmsuser=clientUser \
		 *   jmspswd=dummy \
		 *   solverprimarydata=/simdata/ \
		 *   mongodbhost="vcell-service.cam.uchc.edu" \
		 *   mongodbport=27017 \
		 *   jmsblob_minsize=100000
		 */

		if (isParallel) {
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
			lsb.write("echo \"4 date=`date`\"");
			lsb.append("\techo exitCommand = ");
			lsb.write("$container_prefix" + exitCmd.getJoinedCommands("$1"));
			lsb.append('\t');
			lsb.write("$container_prefix" + exitCmd.getJoinedCommands());
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
			lsb.write("echo \"6 date=`date`\"");

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
	public HtcJobID submitJob(String jobName, String sub_file_internal, String sub_file_external, ExecutableCommand.Container commandSet, int ncpus, double memSize, Collection<PortableCommand> postProcessingCommands) throws ExecutableException {
		final String JOB_CMD_SBATCH = PropertyLoader.getProperty(PropertyLoader.slurm_cmd_sbatch,"sbatch");
		try {
			if (LG.isDebugEnabled()) {
				LG.debug("generating local SLURM submit script for jobName="+jobName);
			}
			String text = generateScript(jobName, commandSet, ncpus, memSize, postProcessingCommands);

			File tempFile = File.createTempFile("tempSubFile", ".sub");

			writeUnixStyleTextFile(tempFile, text);

			// move submission file to final location (either locally or remotely).
			if (LG.isDebugEnabled()) {
				LG.debug("moving local SLURM submit file '"+tempFile.getAbsolutePath()+"' to remote file '"+sub_file_external+"'");
			}
			FileUtils.copyFile(tempFile, new File(sub_file_internal));
			tempFile.delete();
		} catch (IOException ex) {
			ex.printStackTrace(System.out);
			return null;
		}

		/**
		 * 
		 * > sbatch /share/apps/vcell2/deployed/test/htclogs/V_TEST_107643258_0_0.slurm.sub
		 * Submitted batch job 5174
		 * 
		 */
		String[] completeCommand = new String[] {Slurm_HOME + JOB_CMD_SBATCH, sub_file_external};
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
