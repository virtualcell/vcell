package cbit.vcell.message.server.htc;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.document.KeyValue;
import org.vcell.util.exe.ExecutableException;

import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.simdata.PortableCommand;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solvers.ExecutableCommand;

public abstract class HtcProxy {
	public static final Logger LG = LogManager.getLogger(HtcProxy.class);
	public static final String JOB_NAME_PREFIX_SIMULATION = "V_";
	public static final String JOB_NAME_PREFIX_COPASI_PAREST = "CopasiParest_";

	public interface HtcProxyFactory {
		HtcProxy getHtcProxy();
	}
	
	public static class PartitionStatistics {
		public final int numCpusAllocated;
		public final int numCpusTotal;
		public final double load;
		
		public PartitionStatistics(int numCpusAllocated, int numCpusTotal, double load) {
			this.numCpusAllocated = numCpusAllocated;
			this.numCpusTotal = numCpusTotal;
			this.load = load;
		}
		public String toString() {
			return "numCpusAllocated="+numCpusAllocated+", numCpusTotal="+numCpusTotal+", load="+load;
		}
	}

	public static class HtcJobInfo{
		private final HtcJobID htcJobID;
		private final String jobName;

		public HtcJobInfo(HtcJobID htcJobID, String jobName) {
			this.htcJobID = htcJobID;
			this.jobName = jobName;
		}
		public HtcJobID getHtcJobID() {
			return htcJobID;
		}
		public String getJobName(){
			return jobName;
		}
		public String toString(){
			return "HtcJobInfo(jobID="+htcJobID.toDatabase()+",jobName="+jobName+")";
		}
		@Override
		public int hashCode() {
			return htcJobID.toDatabase().hashCode() + jobName.hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof HtcJobInfo) {
				HtcJobInfo other = (HtcJobInfo)obj;
				if (!htcJobID.toDatabase().equals(other.htcJobID.toDatabase())){
					return false;
				}
				if (!jobName.equals(other.jobName)){
					return false;
				}
				return true;
			}else {
				return false;
			}
		}
		
	}
	
	public static class SimTaskInfo {
		
		public final KeyValue simId;
		public final int jobIndex;
		public final int taskId;

		public SimTaskInfo(KeyValue simId, int jobIndex, int taskId){
			this.simId = simId;
			this.jobIndex = jobIndex;
			this.taskId = taskId;
		}

		@Override
		public int hashCode() {
			return simId.hashCode() + jobIndex + taskId;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof SimTaskInfo){
				SimTaskInfo other = (SimTaskInfo)obj;
				return simId.equals(other.simId) && jobIndex==other.jobIndex && taskId==other.taskId;
			}
			return false;
		}
		
		
	}

	/**
	 * set {@link HtcProxy#HTC_SIMULATION_JOB_NAME_PREFIX}
	 * @return V_<i>server<i>
	 */
	private static String simulationJobNamePrefix( ){
		String stub = JOB_NAME_PREFIX_SIMULATION;
		String server = PropertyLoader.getRequiredProperty(PropertyLoader.vcellServerIDProperty);
		return stub + server + "_";
	}

	/**
	 * set {@link HtcProxy#HTC_SIMULATION_JOB_NAME_PREFIX}
	 * @return CopasiParest
	 */
	private static String parameterEstimationJobNamePrefix( ){
		return JOB_NAME_PREFIX_COPASI_PAREST;
	}

	public static boolean isMySimulationJob(HtcJobInfo htcJobInfo){
		return htcJobInfo.getJobName().startsWith(simulationJobNamePrefix());
	}
	public static boolean isMyParameterEstimationJob(HtcJobInfo htcJobInfo){
		return htcJobInfo.getJobName().startsWith(parameterEstimationJobNamePrefix());
	}

	public final static String HTC_SIMULATION_JOB_NAME_PREFIX = simulationJobNamePrefix();
	protected final CommandService commandService;
	protected final String htcUser;



	public HtcProxy(CommandService commandService, String htcUser){
		this.commandService = commandService;
		this.htcUser = htcUser;
	}

	public abstract void killJobSafe(HtcJobInfo htcJobInfo) throws ExecutableException, HtcJobNotFoundException, HtcException;
	public abstract void killJobUnsafe(HtcJobID htcJobId) throws ExecutableException, HtcJobNotFoundException, HtcException;
	public abstract void killJobs(String htcJobSubstring) throws ExecutableException, HtcJobNotFoundException, HtcException;
	public abstract Map<HtcJobInfo,HtcJobStatus> getJobStatus(List<HtcJobInfo> requestedHtcJobInfos) throws ExecutableException, IOException;

	/**
	 * @param postProcessingCommands may be null if no commands desired
	 * @param ncpus must be > 1 if any {@link ExecutableCommand}s are marked parallel
	 * @param postProcessingCommands non null
	 * @throws ExecutableException
	 */
	public abstract HtcJobID submitJob(String jobName, File sub_file_internal, File sub_file_external, ExecutableCommand.Container commandSet,
			int ncpus, double memSize, Collection<PortableCommand> postProcessingCommands, SimulationTask simTask,File primaryUserDirExternal) throws ExecutableException, IOException;
	public abstract HtcJobID submitOptimizationJob(String jobName, File sub_file_internal, File sub_file_external,
												   File optProblemInputFile,File optProblemOutputFile,File optReportFile)throws ExecutableException;
	public abstract HtcProxy cloneThreadsafe();

	public abstract Map<HtcJobInfo,HtcJobStatus> getRunningJobs() throws ExecutableException, IOException;
	
	public abstract PartitionStatistics getPartitionStatistics() throws HtcException, ExecutableException, IOException;

	public final CommandService getCommandService() {
		return commandService;
	}

	public final String getHtcUser() {
		return htcUser;
	}

	public static SimTaskInfo getSimTaskInfoFromSimJobName(String simJobName) throws HtcException{
		StringTokenizer tokens = new StringTokenizer(simJobName,"_");
		String PREFIX = null;
		if (tokens.hasMoreTokens()){
			PREFIX = tokens.nextToken();
		}
		String SITE = null;
		if (tokens.hasMoreTokens()){
			SITE = tokens.nextToken();
		}
		String simIdString = null;
		if (tokens.hasMoreTokens()){
			simIdString = tokens.nextToken();
		}
		String jobIndexString = null;
		if (tokens.hasMoreTokens()){
			jobIndexString = tokens.nextToken();
		}
		String taskIdString = null;
		if (tokens.hasMoreTokens()){
			taskIdString = tokens.nextToken();
		}
		String expectedSimPrefix = HtcProxy.JOB_NAME_PREFIX_SIMULATION.replace("_","");
		if (PREFIX.equals(expectedSimPrefix) && SITE!=null && simIdString!=null && jobIndexString!=null && taskIdString!=null){
			KeyValue simId = new KeyValue(simIdString);
			int jobIndex = Integer.valueOf(jobIndexString);
			int taskId = Integer.valueOf(taskIdString);
			return new SimTaskInfo(simId,jobIndex,taskId);
		}else{
			throw new HtcException("simJobName : "+simJobName+" not in expected format for a simulation job name");
		}
	}

	public static String createHtcSimJobName(SimTaskInfo simTaskInfo) {
		return simulationJobNamePrefix()+simTaskInfo.simId.toString()+"_"+simTaskInfo.jobIndex+"_"+simTaskInfo.taskId;
	}

	public static String toUnixStyleText(String javaString) throws IOException {
		Charset asciiCharset = StandardCharsets.US_ASCII;
		CharsetEncoder encoder = asciiCharset.newEncoder();
		CharBuffer unicodeCharBuffer = CharBuffer.wrap(javaString);
		ByteBuffer asciiByteBuffer = encoder.encode(unicodeCharBuffer);
		byte[] asciiArray = asciiByteBuffer.array();
		ByteBuffer unixByteBuffer = ByteBuffer.allocate(asciiArray.length);
		int count = 0;
		for (int i = 0; i < asciiArray.length; i++) {
			if (asciiArray[i] != 0x0d) {  // skip \r character
				unixByteBuffer.put(asciiArray[i]);
				count++;
			}
		}
		ByteBuffer bb = ByteBuffer.wrap(unixByteBuffer.array(), 0, count);
		return new String(bb.array(), 0, bb.limit(), asciiCharset);
	}

	public abstract String getSubmissionFileExtension();
	public static class MemLimitResults {
		private long memLimit;
		private String memLimitSource;
		public MemLimitResults(long memLimit, String memLimitSource) {
			super();
			this.memLimit = memLimit;
			this.memLimitSource = memLimitSource;
		}
		public long getMemLimit() {
			return memLimit;
		}
		public String getMemLimitSource() {
			return memLimitSource;
		}
		private static MemLimitResults getJobRequestedMemoryLimit(SolverDescription solverDescription, double estimatedMemSizeMB, boolean isPowerUser) {
			long batchJobMemoryLimit = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.htcMinMemoryMB)); // MAX memory allowed if not set in limitFile, currently 4g
			String detailedMessage = "default memory limit";

			if(estimatedMemSizeMB > batchJobMemoryLimit) {//Use estimated if bigger
				batchJobMemoryLimit = (long)estimatedMemSizeMB;
				detailedMessage = "used Estimated";
			}
			long powerUserMemory = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.htcPowerUserMemoryFloorMB)); // MIN memory allowed if declared to be a power user, currently 50g
			if (isPowerUser && batchJobMemoryLimit < powerUserMemory){
				batchJobMemoryLimit = powerUserMemory;
				detailedMessage = "poweruser's memory override";
			}

			return new MemLimitResults(batchJobMemoryLimit, detailedMessage);
		}
	}
	public static final boolean bDebugMemLimit = false;
	public static MemLimitResults getMemoryLimit(String vcellUserid, KeyValue simID, SolverDescription solverDescription ,double estimatedMemSizeMB, boolean isPowerUser) {
		return MemLimitResults.getJobRequestedMemoryLimit(solverDescription, estimatedMemSizeMB*1.5, isPowerUser);
	}

}


