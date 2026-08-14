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

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
		// --json rather than -P: steps arrive nested inside their job instead of as sibling rows,
		// so the .batch/.extern/.N rows the text form must filter are simply not present, and the
		// payload carries a parser version so a schema change is detectable rather than silent.
		String[] cmds = {JOB_CMD_SACCT,"--json","-u",getHtcUser(),"-j",jobList};

		Map<HtcJobInfo, HtcJobStatus> statusMap;
		try {
			CommandOutput commandOutput = commandService.command(cmds);
			statusMap = extractJobIdsFromSacctJson(commandOutput.getStandardOutput());
		} catch (Exception e) {
			// sacct reads the accounting database, so if slurmdbd is unavailable it answers
			// nothing -- and a caller that reads that as "no failures" is blind without knowing
			// it. slurmctld still holds completed jobs for MinJobAge, so ask it instead. Say so
			// loudly: degraded knowledge is not the same as good news.
			LG.error("sacct failed for " + requestedHtcJobInfos.size() + " jobs, falling back to scontrol"
					+ " (only jobs still within MinJobAge will be known): " + e.getMessage(), e);
			return getJobStatusFromScontrol(requestedHtcJobInfos);
		}
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
	

	/**
	 * Asks slurmctld directly, one job at a time, for when accounting cannot answer.
	 *
	 * `sacct` reads the accounting database, so if slurmdbd is unavailable it answers nothing at
	 * all -- and a caller that reads that as "no failures" is blind without knowing it. slurmctld
	 * keeps finished jobs in memory for MinJobAge (10800s on mantis), so scontrol still knows
	 * what happened inside that window.
	 *
	 * One command per id on purpose: scontrol does not accept a comma-separated list (verified --
	 * passing one returns no jobs at all). That cost is why this is a fallback, not the primary.
	 *
	 * A job scontrol has never heard of, or has already forgotten, is skipped: not knowing is not
	 * the same as knowing it failed.
	 */
	public Map<HtcJobInfo,HtcJobStatus> getJobStatusFromScontrol(List<HtcJobInfo> requestedHtcJobInfos) {
		final String JOB_CMD_SCONTROL = PropertyLoader.getProperty(PropertyLoader.slurm_cmd_scontrol,"scontrol");
		Map<HtcJobInfo,HtcJobStatus> statusMap = new HashMap<HtcJobInfo, HtcJobStatus>();
		for (HtcJobInfo requested : requestedHtcJobInfos) {
			String[] cmds = {JOB_CMD_SCONTROL, "show", "job",
					Long.toString(requested.getHtcJobID().getJobNumber()), "--json"};
			try {
				CommandOutput commandOutput = commandService.command(cmds);
				for (Map.Entry<HtcJobInfo, HtcJobStatus> entry : extractJobIdsFromScontrolJson(commandOutput.getStandardOutput()).entrySet()) {
					// same guard as getJobStatus: slurm reuses job ids, so only accept a result
					// whose id AND name are what we asked about
					if (requestedHtcJobInfos.contains(entry.getKey())) {
						statusMap.put(entry.getKey(), entry.getValue());
					}
				}
			} catch (Exception e) {
				LG.debug("scontrol could not report job " + requested + " (it may have aged out of slurmctld): " + e.getMessage());
			}
		}
		return statusMap;
	}

	/** `scontrol show job --json`: state lives at job_state, an array. */
	static Map<HtcJobInfo, HtcJobStatus> extractJobIdsFromScontrolJson(String output) throws IOException {
		return extractJobsFromJson(output, "job_state", null);
	}

	/** `sacct --json`: state lives at state.current, an array, and steps are nested per job. */
	static Map<HtcJobInfo, HtcJobStatus> extractJobIdsFromSacctJson(String output) throws IOException {
		return extractJobsFromJson(output, "state", "current");
	}

	/**
	 * Reads the `jobs` array slurm emits for --json.
	 *
	 * Steps do not appear as siblings here -- they are nested inside their job -- so the step rows
	 * that have to be filtered out of `sacct -P` text simply are not a problem in this shape.
	 *
	 * The state field differs between the two commands, hence the path arguments: `sacct` reports
	 * state.current, `scontrol` reports job_state. Both are arrays.
	 */
	private static Map<HtcJobInfo, HtcJobStatus> extractJobsFromJson(String output, String stateField, String stateSubField) throws IOException {
		Map<HtcJobInfo, HtcJobStatus> statusMap = new HashMap<HtcJobInfo, HtcJobStatus>();
		if (output == null || output.trim().isEmpty()) {
			return statusMap;
		}
		JsonNode root = new ObjectMapper().readTree(output);
		assertKnownDataParser(root);
		JsonNode jobs = root.path("jobs");
		for (JsonNode job : jobs) {
			JsonNode idNode = job.path("job_id");
			JsonNode nameNode = job.path("name");
			if (idNode.isMissingNode() || nameNode.isMissingNode()) {
				continue;
			}
			JsonNode stateNode = job.path(stateField);
			if (stateSubField != null) {
				stateNode = stateNode.path(stateSubField);
			}
			// an array of state strings; the first is the state proper, any others qualify it
			String state = stateNode.isArray() && stateNode.size() > 0 ? stateNode.get(0).asText() : stateNode.asText();
			if (state == null || state.isEmpty()) {
				continue;
			}
			HtcJobInfo htcJobInfo = new HtcJobInfo(new HtcJobID(idNode.asText(), BatchSystemType.SLURM), nameNode.asText());
			statusMap.put(htcJobInfo, new HtcJobStatus(SlurmJobStatus.parseStatus(state)));
		}
		return statusMap;
	}

	/**
	 * Slurm versions its JSON, so a shape change is detectable rather than something that quietly
	 * misparses. Logged rather than thrown: an unexpected parser version is a reason to look, not
	 * a reason to stop monitoring jobs.
	 */
	static final String EXPECTED_DATA_PARSER = "data_parser/v0.0.40";

	private static void assertKnownDataParser(JsonNode root) {
		String dataParser = root.path("meta").path("plugin").path("data_parser").asText("");
		if (!dataParser.isEmpty() && !EXPECTED_DATA_PARSER.equals(dataParser)) {
			LG.warn("slurm JSON reports " + dataParser + ", expected " + EXPECTED_DATA_PARSER
					+ "; field names may have moved and job status may be misread");
		}
	}

	/**
	 * Parses `sacct -P` text into one entry per job allocation.
	 *
	 * NO LONGER ON THE PRODUCTION PATH: getJobStatus() asks for --json instead, where steps are
	 * nested inside their job rather than arriving as sibling rows. Retained because --json needs
	 * a slurm carrying the data_parser plugin (mantis runs 23.11); a deployment on an older slurm
	 * would still need this. If every deployment is known to be new enough this and its fixtures
	 * can go -- until someone confirms that, deleting it removes the only way back.
	 */
	static Map<HtcJobInfo, HtcJobStatus> extractJobIds(String output) throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(output));
		String line = reader.readLine();
		// sacct prints its header even when nothing matches, so no header at all means no output
		// -- which is what an accounting outage looks like. Returning empty lets the caller decide
		// what to make of knowing nothing; throwing here told it the wrong thing.
		while(line != null && !line.equals("JobID|JobName|State")){
			line = reader.readLine();
		}
		if(line == null){
			return new HashMap<HtcJobInfo, HtcJobStatus>();
		}
		Map<HtcJobInfo, HtcJobStatus> statusMap = new HashMap<HtcJobInfo, HtcJobStatus>();
		while ((line = reader.readLine()) != null){
			String[] tokens = line.split("\\|");
			if (tokens.length < 3){
				continue;
			}
			String jobID = tokens[0];
			String jobName = tokens[1];
			String state = tokens[2];
			// a dot in the id marks a step (.batch, .extern, .0..), not an allocation. Filtering on
			// the id covers all three; filtering on the name caught only .batch.
			if (jobID.contains(".")){
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
	 * Derive a local SIF filename from an ORAS URL.
	 * e.g. "oras://ghcr.io/virtualcell/vcell-opt_singularity:7.7.0.71"
	 *    → "ghcr.io_virtualcell_vcell-opt_singularity_7.7.0.71.img"
	 */
	static String sifFilenameFromOrasUrl(String orasUrl) {
		String stripped = orasUrl.replaceFirst("^oras://", "");
		return stripped.replace('/', '_').replace(':', '_') + ".img";
	}

	/**
	 * Derive the full local SIF path from an ORAS URL and the SIF image directory.
	 */
	static String sifPathFromOrasUrl(String orasUrl, String sifImageDir) {
		return sifImageDir + "/" + sifFilenameFromOrasUrl(orasUrl);
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

	private static int roundUpToBlock(long memPerTaskMB, int blockSizeMB) {
		long block = blockSizeMB; // promote to long for safe math
		long rounded = ((memPerTaskMB + block - 1) / block) * block;
		return (int) rounded;
	}

	@Deprecated
	private static String extractUser(ExecutableCommand.Container commandSet) {
		for (ExecutableCommand ec: commandSet.getExecCommands()) {
			ExecutableCommand.Container commandSet2 = new ExecutableCommand.Container();
			if(ec.getCommands().get(0).equals("JavaPreprocessor64")) {
				continue;
			}else {
				for(String command : ec.getCommands()) {
					if(command.startsWith("-Duser=")) {
						return command.substring(7);
					}
					else if(command.contains("langevinInput")) {
						// Langevin input file is of the form /some/path/users/...
						String[] pathParts = command.split("/");
						for(int i=0; i<pathParts.length; i++) {
							String part = pathParts[i];
							if(part.contains("users")) {
								return pathParts[i+1];
							}
						}
					}
				}
			}
		}
		throw new RuntimeException("Could not extract user from command set: "+commandSet);
	}


	/**
	 * Compute SBATCH --time
	 * as HH:MM:00 (seconds fixed to "00").
	 * or as D-HH:MM:00 (if over 99 hours).
	 *
	 * Uses integer math only and adds a 10% cushion, rounded up to whole minutes.
	 *
	 * totalNumberOfJobs: total simulation tasks
	 * numberOfConcurrentTasks: parallel slots (SLURM_NTASKS)
	 * timeoutSeconds: per-task timeout in seconds
	 */
	public static String computeSlurmTimeLimit(int totalNumberOfJobs,
														  int numberOfConcurrentTasks,
														  int timeoutSeconds) {
		if (totalNumberOfJobs < 0) throw new IllegalArgumentException("totalNumberOfJobs >= 0 required");
		if (numberOfConcurrentTasks <= 0) throw new IllegalArgumentException("numberOfConcurrentTasks > 0 required");
		if (timeoutSeconds <= 0) throw new IllegalArgumentException("timeoutSeconds > 0 required");

		int perTaskMinutes = (timeoutSeconds + 59) / 60; // ceiling(timeoutSeconds/60)
		int batches = (totalNumberOfJobs + numberOfConcurrentTasks - 1) / numberOfConcurrentTasks;
		long workMinutes = (long) batches * perTaskMinutes;
		long cushionedMinutes = (long) Math.ceil(workMinutes * 1.20);

		long totalHours = cushionedMinutes / 60;
		long minutes = cushionedMinutes % 60;

		if (totalHours < 100) {
			return String.format("%02d:%02d:00", totalHours, minutes);
		} else {
			long days = totalHours / 24;
			if(days >= 21) {
				return String.format("%d-%02d:%02d:00", 20, 23, 59); // maxwall for vcell is 21-00:00:00
			}
			long hours = totalHours % 24;
			return String.format("%d-%02d:%02d:00", days, hours, minutes);
		}
	}
	private void writeScriptControlledVariables(LineStringBuilder lsb, String jobName,
												SimulationTask simTask, int jobTimeoutSeconds) {

		String simKey = simTask.getSimulation().getVersion().getVersionKey().toString();
		String simOwnerName = simTask.getSimulation().getVersion().getOwner().getName();	// this is the user name
		String simOwnerId = simTask.getSimulation().getVersion().getOwner().getID().toString();
		String jobId = Integer.toString(simTask.getSimulationJob().getJobIndex());		// for example 0
		String taskId = Integer.toString(simTask.getTaskID());

		int totalJobs = simTask.getSimulation().getSolverTaskDescription().getLangevinSimulationOptions().getTotalNumberOfJobs();
		String htcLogDir = PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirExternal);
		String simDataDir = PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirExternalProperty);
		int lastUnderscore = jobName.lastIndexOf('_');
		String trimmedJobName = (lastUnderscore >= 0) ? jobName.substring(0, lastUnderscore + 1) : jobName;
		String logFilePath = htcLogDir + "/" + trimmedJobName + ".submit.log";
		String messagingConfigFilePath = simDataDir + "/" + simOwnerName + "/SimID_" + simKey + "_0_.langevinMessagingConfig";

		// ---- Alternate / New variables ----


		lsb.write("# Script-controlled variables (populated by generator in real use)");
//		lsb.write("USERID=" + simOwnerName);
//		lsb.write("SIM_KEY=" + simId);
		lsb.write("SIM_KEY=" + simKey);
		lsb.write("SIM_OWNER_NAME=" + simOwnerName);
		lsb.write("SIM_OWNER_ID=" + simOwnerId);
		lsb.write("VC_JOB_ID=" + jobId);		// vcell job id, there is also a slurm job id which is something else
		lsb.write("VC_TASK_ID=" + taskId);

		lsb.write("TOTAL_JOBS=" + totalJobs + "            # to be set by generator to lso.getTotalNumberOfJobs()");
		lsb.write("JOB_TIMEOUT_SECONDS=" + jobTimeoutSeconds + "  # per-job timeout (seconds), adjust per generator");
		lsb.write("LOG_FILE=\"" + logFilePath + "\"");
		lsb.write("MESSAGING_CONFIG_FILE=\"" + messagingConfigFilePath + "\"");
		lsb.write("");

		lsb.write("# Truncate / delete various logs and the solver input file, to start clean");
		lsb.write(": > " + htcLogDir + "/V_TEST2_${SIM_KEY}_0_.slurm.log");
		lsb.write("rm -f " + simDataDir + "/${SIM_OWNER_NAME}/SimID_${SIM_KEY}_0_*.log");
		lsb.write("rm -f " + simDataDir + "/${SIM_OWNER_NAME}/SimID_${SIM_KEY}_0__*.ida");
		lsb.write("rm -f " + simDataDir + "/${SIM_OWNER_NAME}/SimID_${SIM_KEY}_0__*.json");
		lsb.write("rm -f " + simDataDir + "/${SIM_OWNER_NAME}/SimID_${SIM_KEY}_0_.functions");
		lsb.write("rm -f " + simDataDir + "/${SIM_OWNER_NAME}/SimID_${SIM_KEY}_0_.langevinInput");
		lsb.write("rm -f " + simDataDir + "/${SIM_OWNER_NAME}/SimID_${SIM_KEY}_0_.langevinMessagingConfig");
		lsb.write("");
	}
	private void writeSingularitySetup(LineStringBuilder lsb) {
		String slurmTmpDir = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_tmpdir);
		String singularityCachedir = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_singularity_cachedir);
		String singularityPullfolder = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_singularity_pullfolder);
		String singularityModuleName = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_singularity_module_name);

		lsb.write("echo \"=== Singularity check ===\"");
		lsb.write("if command -v singularity >/dev/null 2>&1; then");
		lsb.write("    echo \"Singularity found at: $(command -v singularity)\"");
		lsb.write("    singularity --version");
		lsb.write("else");
		lsb.write("    echo \"Singularity not found\"");
		// code 127: Command not found
		lsb.write("    exit 1");
		lsb.write("fi");
		lsb.write("");

		lsb.write("TMPDIR=" + slurmTmpDir);
		lsb.write("if [ ! -e $TMPDIR ]; then mkdir -p $TMPDIR ; fi");
		lsb.write("echo `hostname`");
		lsb.write("export MODULEPATH=/isg/shared/modulefiles:/tgcapps/modulefiles");
		lsb.write("export SINGULARITY_CACHEDIR=" + singularityCachedir);
		lsb.write("export SINGULARITY_PULLFOLDER=" + singularityPullfolder);
		lsb.write("");
	}
	private void writeSlurmJobMetadata(LineStringBuilder lsb) {
		lsb.write("# Compute memory per task and per job");
		lsb.write("MEM_TASK=$(( SLURM_MEM_PER_CPU * SLURM_CPUS_PER_TASK ))");
		lsb.write("MEM_JOB=$(( MEM_TASK * SLURM_NTASKS ))");
		lsb.write("");

		lsb.write("echo \"======= SLURM job started =======\"");
		lsb.write("echo \"Hostname       : $(hostname -f)\"");
		lsb.write("echo \"Sim Owner Name : $SIM_OWNER_NAME\"");
		lsb.write("echo \"Sim Owner Id   : $SIM_OWNER_ID\"");
		lsb.write("echo \"Sim Key        : $SIM_KEY\"");
		lsb.write("echo \"id             : $(id)\"");
		lsb.write("echo \"Total Jobs     : $TOTAL_JOBS\"");
		lsb.write("echo \"Job Timeout    : $JOB_TIMEOUT_SECONDS\"");
		lsb.write("echo \"Slurm Job ID   : $SLURM_JOB_ID\"");
		lsb.write("echo \"Slurm Job Name : $SLURM_JOB_NAME\"");
		lsb.write("echo \"Start Time     : $(date)\"");
		lsb.write("echo \"Working Dir    : $(pwd)\"");
		lsb.write("echo \"Node List      : $SLURM_NODELIST\"");
		lsb.write("echo \"CPUs per task  : $SLURM_CPUS_PER_TASK\"");
		lsb.write("echo \"Mem. per task  : ${MEM_TASK} MB total\"");
		lsb.write("echo \"Mem. per job   : ${MEM_JOB} MB total\"");
		lsb.write("echo \"Environment snapshot:\"");
		lsb.write("env");
		lsb.write("echo \"=================================\"");
		lsb.write("");
	}
	private void writeContainerBindingsAndEnv(LineStringBuilder lsb, int javaMemXmx) {
		String primaryDataDir = PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirExternalProperty);
		String secondaryDataDir = PropertyLoader.getRequiredProperty(PropertyLoader.secondarySimDataDirExternalProperty);
		String archiveDataDirHost = PropertyLoader.getRequiredProperty(PropertyLoader.simDataDirArchiveExternal);
		String archiveDataDirContainer = PropertyLoader.getRequiredProperty(PropertyLoader.simDataDirArchiveInternal);
		String htclogDir = PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirExternal);
		String scratchDir = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_tmpdir);

		lsb.write("container_bindings=\"--bind " + primaryDataDir + ":/simdata \"");
		lsb.write("container_bindings+=\"--bind " + secondaryDataDir + ":/simdata_secondary \"");
		lsb.write("container_bindings+=\"--bind " + archiveDataDirHost + ":" + archiveDataDirContainer + " \"");
		lsb.write("container_bindings+=\"--bind " + htclogDir + ":/htclogs \"");
		lsb.write("container_bindings+=\"--bind " + scratchDir + ":/solvertmp \"");
		lsb.write("");

		String jmsHost = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimHostExternal);
		String jmsPort = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimPortExternal);
		String jmsRestPort = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimRestPortExternal);
		String jmsUser = PropertyLoader.getRequiredProperty(PropertyLoader.jmsUser);
		String jmsPswd = PropertyLoader.getSecretValue(PropertyLoader.jmsPasswordValue, PropertyLoader.jmsPasswordFile);
		String jmsBlobMinSize = PropertyLoader.getProperty(PropertyLoader.jmsBlobMessageMinSize, "100000");
		String mongoHost = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbHostExternal);
		String mongoPort = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbPortExternal);
		String mongoDB = PropertyLoader.getRequiredProperty(PropertyLoader.mongodbDatabase);
		String softwareVersion = PropertyLoader.getRequiredProperty(PropertyLoader.vcellSoftwareVersion);
		String serverId = PropertyLoader.getRequiredProperty(PropertyLoader.vcellServerIDProperty);

		lsb.write("container_env=\"--env java_mem_Xmx=" + javaMemXmx + "M \"");
		lsb.write("container_env+=\"--env jmshost_sim_internal=" + jmsHost + " \"");
		lsb.write("container_env+=\"--env jmsport_sim_internal=" + jmsPort + " \"");
		lsb.write("container_env+=\"--env jmsrestport_sim_internal=" + jmsRestPort + " \"");
		lsb.write("container_env+=\"--env jmsuser=" + jmsUser + " \"");
		lsb.write("container_env+=\"--env jmspswd=" + jmsPswd + " \"");
		lsb.write("container_env+=\"--env jmsblob_minsize=" + jmsBlobMinSize + " \"");
		lsb.write("container_env+=\"--env mongodbhost_internal=" + mongoHost + " \"");
		lsb.write("container_env+=\"--env mongodbport_internal=" + mongoPort + " \"");
		lsb.write("container_env+=\"--env mongodb_database=" + mongoDB + " \"");
		lsb.write("container_env+=\"--env primary_datadir_external=" + primaryDataDir + " \"");
		lsb.write("container_env+=\"--env secondary_datadir_external=" + secondaryDataDir + " \"");
		lsb.write("container_env+=\"--env htclogdir_external=" + htclogDir + " \"");
		lsb.write("container_env+=\"--env softwareVersion=" + softwareVersion + " \"");
		lsb.write("container_env+=\"--env serverid=" + serverId + " \"");
		lsb.write("");
	}
	private void writeContainerImageAndPrefixes(LineStringBuilder lsb) {
		final String batchApptainerImage = PropertyLoader.getRequiredProperty(PropertyLoader.htc_vcellbatch_apptainer_image);
		final String sifImageDir = PropertyLoader.getRequiredProperty(PropertyLoader.htc_singularity_imagedir);
		final String sifPath = sifPathFromOrasUrl(batchApptainerImage, sifImageDir);

		lsb.write("# Full solver command");
		writeSifContainerPrefix(lsb, "solver", sifPath);
		lsb.write("");
		writeSifContainerPrefix(lsb, "batch", sifPath);
		lsb.write("");

		lsb.write("slurm_prefix=\"srun -N1 -n1 -c${SLURM_CPUS_PER_TASK}\"");
		lsb.write("");
	}

	private void writeSifContainerPrefix(LineStringBuilder lsb, String prefix, String sifPath) {
		lsb.write(prefix + "_sif=" + sifPath);
		lsb.write("if [ ! -f \"$" + prefix + "_sif\" ]; then");
		lsb.write("    echo \"ERROR: missing SIF $" + prefix + "_sif\" >&2");
		// code 2: no such file or directory
		lsb.write("    exit 1");
		lsb.write("fi");
		lsb.write(prefix + "_container_prefix=\"singularity run --containall " +
				"${container_bindings} ${container_env} $" + prefix + "_sif\"");
	}
	String  generateLangevinBatchScript(String jobName, ExecutableCommand.Container commandSet, double memSizeMB,
										Collection<PortableCommand> postProcessingCommands, SimulationTask simTask) {

		// TODO: extractUser is very unrobust, must be fixed
		// it may be the userName can be obtained like so: String vcellUserid = simTask.getUser().getName();
		//String userName = extractUser(commandSet);
		String vcellUserid = simTask.getUser().getName();
		KeyValue simID = simTask.getSimulationInfo().getSimulationVersion().getVersionKey();
		SolverTaskDescription std = simTask.getSimulation().getSolverTaskDescription();
		LangevinSimulationOptions lso = std.getLangevinSimulationOptions();
		int totalNumberOfJobs = lso.getTotalNumberOfJobs();
		int numberOfConcurrentTasks = lso.getNumberOfConcurrentJobs();
		SolverDescription solverDescription = std.getSolverDescription();
		MemLimitResults memoryMBAllowed = HtcProxy.getMemoryLimit(vcellUserid, simID, solverDescription, memSizeMB, simTask.isPowerUser());

		// next 3 will fire exception if prop not set
		// Wall-clock cap per Langevin task. The fixture wraps each task in
		// `timeout "${JOB_TIMEOUT_SECONDS}s"`, so exceeding this kills the task with exit
		// code 124 -- it is not a solver crash. Deployed value is 345600 (4 days); the two
		// comments that used to sit here said "7 days" and "24 hours", and neither was right,
		// which is part of why a user lost two multi-day SpringSaLaD runs to it before anyone
		// looked at the number.
		String sTimeoutPerTaskSeconds = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_langevin_timeoutPerTaskSeconds);
		String sHardbBtchMemoryLimitPerTask = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_langevin_batchMemoryLimitPerTaskMB);
		String sBlockSizeMB =  PropertyLoader.getRequiredProperty(PropertyLoader.slurm_langevin_memoryBlockSizeMB);
		int timeoutPerTaskSeconds = Integer.parseInt(sTimeoutPerTaskSeconds);
		long hardbBtchMemoryLimitPerTask = Long.parseLong(sHardbBtchMemoryLimitPerTask);	// MB. we hard limit mem to 2G for langevin batch jobs
		int blockSizeMB = Integer.parseInt(sBlockSizeMB); 						// MB. SLURM memory allocation granularity
		String slurmJobTimeout = computeSlurmTimeLimit(totalNumberOfJobs, numberOfConcurrentTasks, timeoutPerTaskSeconds);
		long batchMemoryLimitPerTask = memoryMBAllowed.getMemLimit();
		batchMemoryLimitPerTask = Math.min(batchMemoryLimitPerTask, hardbBtchMemoryLimitPerTask);
		int javaMemXmx = roundUpToBlock(batchMemoryLimitPerTask, blockSizeMB) + blockSizeMB;	// add extra block for overhead

		// -------------------------------------------------------------

		LineStringBuilder lsb = new LineStringBuilder();
		slurmBatchScriptInit(jobName, simTask.isPowerUser(), memoryMBAllowed, numberOfConcurrentTasks, slurmJobTimeout, lsb);
		writeScriptControlledVariables(lsb, jobName, simTask, timeoutPerTaskSeconds);
		writeSingularitySetup(lsb);
		writeSlurmJobMetadata(lsb);
		writeContainerBindingsAndEnv(lsb, javaMemXmx);
		writeContainerImageAndPrefixes(lsb);

		String langevinFixture;
		try {
			langevinFixture = readTextFileFromResource("slurm/templates/langevinFixture.slurm.sub");
		} catch (IOException ex) {
			throw new IllegalStateException("Failed to load orchestration fixture", ex);
		}
		lsb.write(langevinFixture);

		Charset asciiCharset = StandardCharsets.US_ASCII;
		CharsetEncoder encoder = asciiCharset.newEncoder();
		String langevinBatchString = lsb.sb.toString();
		for (int i = 0; i < langevinBatchString.length(); i++) {
			char c = langevinBatchString.charAt(i);
			if (!encoder.canEncode(c)) {
				System.err.printf("Unmappable character at index %d: U+%04X (%c)%n", i, (int) c, c);
			}
		}
		return langevinBatchString;
	}

	SbatchSolverComponents generateScript(String jobName, ExecutableCommand.Container commandSet, double memSizeMB, Collection<PortableCommand> postProcessingCommands, SimulationTask simTask) {

		//SlurmProxy ultimately instantiated from {vcellroot}/docker/build/Dockerfile-service-dev, run with the argument "submit", by way of cbit.vcell.message.server.batch.sim.HtcSimulationWorker
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

		final String solverApptainerImage;
		final String batchApptainerImage = PropertyLoader.getRequiredProperty(PropertyLoader.htc_vcellbatch_apptainer_image);
		if (vcellfvsolver_solverList.contains(solverName)) {
			solverApptainerImage = PropertyLoader.getRequiredProperty(PropertyLoader.htc_vcellfvsolver_apptainer_image);
		} else if (vcellsolvers_solverList.contains(solverName)) {
			solverApptainerImage = PropertyLoader.getRequiredProperty(PropertyLoader.htc_vcellsolvers_apptainer_image);
		} else if (vcellbatch_solverList.contains(solverName)) {
			solverApptainerImage = batchApptainerImage;
		} else {
			throw new RuntimeException("solverName="+solverName+" not in vcellfvsolver_solverList="+vcellfvsolver_solverList+" or vcellsolvers_solverList="+vcellsolvers_solverList+" or vcellbatch_solverList="+vcellbatch_solverList);
		}
		final String sifImageDir = PropertyLoader.getRequiredProperty(PropertyLoader.htc_singularity_imagedir);

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

		final String solverSifPath = sifPathFromOrasUrl(solverApptainerImage, sifImageDir);
		final String batchSifPath = sifPathFromOrasUrl(batchApptainerImage, sifImageDir);

		LineStringBuilder lsb = new LineStringBuilder();
		LineStringBuilder singularityLSB = new LineStringBuilder();
		slurmInitSingularity(singularityLSB,
				solverSifPath, Optional.of(batchSifPath), bindings,
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
				  String solverSifPath, Optional<String> batchSifPath, List<SingularityBinding> bindings,
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
		writeSifContainerPrefix(lsb, "solver", solverSifPath);
		if (batchSifPath.isPresent()) {
			writeSifContainerPrefix(lsb, "batch", batchSifPath.get());
		}
		lsb.newline();
	}


	private void slurmScriptInit(String jobName, boolean bPowerUser, MemLimitResults memoryMBAllowed,
			LineStringBuilder lsb) {
		String os = System.getProperty("os.name").toLowerCase();
		boolean isWindows = os.startsWith("windows");

		lsb.write("#!/usr/bin/bash");
		File htcLogDirExternal = new File(PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirExternal));
		String logPath = new File(htcLogDirExternal, jobName + ".slurm.log").getAbsolutePath();
		if(isWindows) {
			logPath = logPath.replaceAll("[A-Za-z]:", "").replace("\\", "/");
		}

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
		lsb.write("#SBATCH -o " + logPath);
		lsb.write("#SBATCH -e " + logPath);
		lsb.write("#SBATCH --mem="+memoryMBAllowed.getMemLimit()+"M");
		lsb.write("#SBATCH --no-kill");
		lsb.write("#SBATCH --no-requeue");
		String nodelist = PropertyLoader.getProperty(PropertyLoader.htcNodeList, null);
		if (nodelist!=null && nodelist.trim().length()>0) {
			lsb.write("#SBATCH --nodelist="+nodelist);
		}
		lsb.write("# VCell SlurmProxy memory limit source='"+memoryMBAllowed.getMemLimitSource()+"'");
	}

	private void slurmBatchScriptInit(String jobName, boolean isPowerUser, MemLimitResults memoryMBAllowed,
									  int numberOfConcurrentTasks, String jobTimeout, LineStringBuilder lsb) {
		lsb.write("#!/usr/bin/bash");

		if (isPowerUser) {
			String partitionPU = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_partition_pu);
			String qosPU = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_qos_pu);
			lsb.write("#SBATCH --partition=" + partitionPU);
			lsb.write("#SBATCH --reservation="); // intentionally blank
			lsb.write("#SBATCH --qos=" + qosPU);
		} else {
			String partition = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_partition);
			String reservation = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_reservation);
			String qos = PropertyLoader.getRequiredProperty(PropertyLoader.slurm_qos);
			lsb.write("#SBATCH --partition=" + partition);
			lsb.write("#SBATCH --reservation=" + reservation);
			lsb.write("#SBATCH --qos=" + qos);
		}

		lsb.write("#SBATCH -J " + jobName);

		String htcLogDir = PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirExternal);
		int lastUnderscore = jobName.lastIndexOf('_');
		String trimmedJobName = (lastUnderscore >= 0) ? jobName.substring(0, lastUnderscore + 1) : jobName;
		String logPath = new File(htcLogDir, trimmedJobName + ".slurm.log").getPath().replace("\\", "/");

		lsb.write("#SBATCH -o " + logPath);
		lsb.write("#SBATCH -e " + logPath);
		lsb.write("#SBATCH --ntasks=" + numberOfConcurrentTasks + "\t\t\t# number of concurrent tasks");
		// TODO: hardcoded for now, adjust if needed
		lsb.write("#SBATCH --cpus-per-task=1");
		// TODO: mem per cpu needs to be adjusted, 2M should be enough for most Langevin tasks
		lsb.write("#SBATCH --mem-per-cpu=" + memoryMBAllowed.getMemLimit() + "M");
		lsb.write("#SBATCH --nodes=1");
		lsb.write("#SBATCH --time=" + jobTimeout + "\t\t# timeout for the entire job");
		String nodelist = PropertyLoader.getProperty(PropertyLoader.htcNodeList, null);
		if (nodelist!=null && nodelist.trim().length()>0) {
			lsb.write("#SBATCH --nodelist="+nodelist);
		}
		lsb.write("#SBATCH --no-kill");
		lsb.write("#SBATCH --no-requeue");

		lsb.write(""); // blank line before shell options
		lsb.write("set -o errexit");
		lsb.write("set -o pipefail");
		lsb.write("set -o nounset");
		lsb.write("set +e");
		lsb.write("");
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
			// Validate sub_file_internal is under the configured htcLogDir to prevent path traversal
			String htcLogDirInternal = PropertyLoader.getRequiredProperty(PropertyLoader.htcLogDirInternal);
			java.nio.file.Path subFilePath = sub_file_internal.getCanonicalFile().toPath();
			java.nio.file.Path htcLogDirPath = new File(htcLogDirInternal).getCanonicalFile().toPath();
			if (!subFilePath.startsWith(htcLogDirPath)) {
				throw new ExecutableException("sub_file_internal path outside htcLogDir: " + sub_file_internal);
			}
			String scriptText = createOptJobScript(jobName, optProblemInputFile, optProblemOutputFile, optReportFile);
			LG.info("sub_file_internal: " + sub_file_internal.getAbsolutePath() +
					", sub_file_external: " + sub_file_external.getAbsolutePath() +
					", optProblemInput: " + optProblemInputFile.getAbsolutePath() +
					", optProblemOutput: " + optProblemOutputFile.getAbsolutePath() +
					", optReport: " + optReportFile.getAbsolutePath());
			Files.writeString(sub_file_internal.getCanonicalFile().toPath(), scriptText);
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
		final String optApptainerImage = PropertyLoader.getRequiredProperty(PropertyLoader.htc_vcellopt_apptainer_image);
		final String sifImageDir = PropertyLoader.getRequiredProperty(PropertyLoader.htc_singularity_imagedir);
		final String solverSifPath = sifPathFromOrasUrl(optApptainerImage, sifImageDir);
		final Optional<String> batchSifPath = Optional.empty();

		MemLimitResults memoryMBAllowed = new MemLimitResults(256, "Optimization Default");
		String[] environmentVars = new String[] {
				"datadir_external="+primaryDataDirExternal,
		};

		LineStringBuilder lsb = new LineStringBuilder();
		slurmScriptInit(jobName, false, memoryMBAllowed, lsb);
		// vcell-rest creates the parest_data directory before writing the input file.
		// Compute the external path for the SLURM script bindings (host-visible NFS mount point).
		File optDataDir = optProblemInputFile.getParentFile();
		File optDataDirExternal = new File(optDataDir.getAbsolutePath().replace(primaryDataDirInternal, primaryDataDirExternal));

		List<SingularityBinding> bindings = List.of(
				new SingularityBinding(optDataDirExternal.getAbsolutePath(), "/simdata"),
				new SingularityBinding(slurm_tmpdir, "/solvertmp"),
				new SingularityBinding(htclogdir_external, "/htclogs"),
				new SingularityBinding(simDataDirArchiveExternal, simDataDirArchiveInternal)
		);

		slurmInitSingularity(lsb, solverSifPath, batchSifPath,
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

	private String readTextFileFromResource(String filename) throws IOException {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
		if (inputStream == null) {
			throw new IOException("Resource not found: " + filename);
		}
		String xmlString;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			xmlString = reader.lines().collect(Collectors.joining(System.lineSeparator()));
		}
		return xmlString;
	}

}
