package cbit.vcell.message.server.htc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.BeanUtils;
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
		return HTC_SIMULATION_JOB_NAME_PREFIX+simTaskInfo.simId.toString()+"_"+simTaskInfo.jobIndex+"_"+simTaskInfo.taskId;
	}

	public static void writeUnixStyleTextFile(File file, String javaString) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(file)) {
			Charset asciiCharset = Charset.forName("US-ASCII");
			CharsetEncoder encoder = asciiCharset.newEncoder();
			CharBuffer unicodeCharBuffer = CharBuffer.wrap(javaString);
			ByteBuffer asciiByteBuffer = encoder.encode(unicodeCharBuffer);
			byte[] asciiArray = asciiByteBuffer.array();
			ByteBuffer unixByteBuffer = ByteBuffer.allocate(asciiArray.length);
			int count = 0;
			for (int i=0;i<asciiArray.length;i++){
				if (asciiArray[i] != 0x0d){  // skip \r character
					unixByteBuffer.put(asciiArray[i]);
					count++;
				}
			}
			//do this to not write the zeros at the end of unixByteBuffer
			ByteBuffer bb = ByteBuffer.wrap(unixByteBuffer.array(),0,count);

			try (FileChannel fc = fos.getChannel()) {
				fc.write(bb);
				fc.close();
			}
		}
	}

	public abstract String getSubmissionFileExtension();
	public static class MemLimitResults {
		private static final long FALLBACK_MEM_LIMIT_MB=4096;		// MAX memory allowed if not set in limitFile, currently 4g
		private static final long POWER_USER_MEMORY_FLOOR=51200; 	// MIN memory allowed if declared to be a power user, currently 50g
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
		private static MemLimitResults getFallbackMemLimitMB(SolverDescription solverDescription,double estimatedMemSizeMB, boolean isPowerUser) {
			Long result = null;
			String source = null;
			try {
				List<String> solverMemLimits = Files.readAllLines(Paths.get(new File("/"+System.getProperty(PropertyLoader.htcLogDirInternal)+"/slurmMinMem.txt").getAbsolutePath()));
				for (Iterator<String> iterator = solverMemLimits.iterator(); iterator.hasNext();) {
					String solverAndLimit = iterator.next().trim();
					if(solverAndLimit.length()==0 || solverAndLimit.startsWith("//")) {
						continue;
					}
					StringTokenizer st = new StringTokenizer(solverAndLimit,":");
					String limitSolver = st.nextToken();
					if(limitSolver.equalsIgnoreCase("all") && result == null) {//use all if there is not solver matching name in slurmMinMem.txt
						result = Long.parseLong(st.nextToken());
						source = "used slurmMinMem.txt all";
					}else if(solverDescription != null && limitSolver.equals(solverDescription.name())) {//use matching solver mem limit from file
						result = Long.parseLong(st.nextToken());
						source = "used slurmMinMem.txt "+solverDescription.name();
						break;
					}
				}
				if(result == null) {//empty slurmMinMem.txt
					result = FALLBACK_MEM_LIMIT_MB;
					source = "Empty used FALLBACK_MEM_LIMIT_MB";
				}
			} catch (Exception e) {
				LG.debug(e);
				result = FALLBACK_MEM_LIMIT_MB;
				source = "Exception "+e.getClass().getSimpleName()+" used FALLBACK_MEM_LIMIT_MB";
			}
			if(estimatedMemSizeMB > result) {//Use estimated if bigger
				result = (long)estimatedMemSizeMB;
				source = "used Estimated";
			}
			if (isPowerUser && result < POWER_USER_MEMORY_FLOOR){
				result = (long)POWER_USER_MEMORY_FLOOR;
				source = "poweruser's memory override";
			}

			return new MemLimitResults(result, source);
		}
	}
	public static final boolean bDebugMemLimit = false;
	public static MemLimitResults getMemoryLimit(String vcellUserid, KeyValue simID, SolverDescription solverDescription ,double estimatedMemSizeMB, boolean isPowerUser) {
		return MemLimitResults.getFallbackMemLimitMB(solverDescription, estimatedMemSizeMB*1.5, isPowerUser);
//		boolean bUseEstimate = estimatedMemSizeMB >= MemLimitResults.getFallbackMemLimitMB(solverDescription);
//		return new MemLimitResults((bUseEstimate?(long)estimatedMemSizeMB:MemLimitResults.getFallbackMemLimitMB(solverDescription)), (bUseEstimate?"used Estimated":"used FALLBACK_MEM_LIMIT"));
//		//One of 5 limits are returned (ordered from highest to lowest priority):
//		//  MemoryMax:PerSimulation									Has PropertyLoader.simPerUserMemoryLimitFile, specific user AND simID MATCHED in file (userid MemLimitMb simID)
//		//  MemoryMax:PerUser										Has PropertyLoader.simPerUserMemoryLimitFile, specific user (but not simID) MATCHED in file (userid MemLimitMb '*')
//		//  MemoryMax:PerSolver										Has PropertyLoader.simPerUserMemoryLimitFile, specific solverDescription (but not simID or user) MATCHED in file (solverName MemLimitMb '*')
//		//  MemoryMax:SimulationTask.getEstimatedMemorySizeMB()		Has PropertyLoader.simPerUserMemoryLimitFile, no user or sim MATCHED in file ('defaultSimMemoryLimitMb' MemLimitMb '*')
//		//																estimated > MemoryMax:AllUsersMemLimit
//		//  MemoryMax:AllUsersMemLimit(defaultSimMemoryLimitMb)		Has PropertyLoader.simPerUserMemoryLimitFile, no user or sim MATCHED in file ('defaultSimMemoryLimitMb' MemLimitMb '*')
//		//																estimated < MemoryMax:AllUsersMemLimit
//		//  MemoryMax:HtcProxy.MemLimitResults.FALLBACK_MEM_LIMIT	No PropertyLoader.simPerUserMemoryLimitFile
//		//																estimated < FALLBACK
//		
//		Long defaultSimMemoryLimitMbFromFile = null;
//		File memLimitFile = null;
//		try {
//			//${vcellroot}/docker/swarm/serverconfig-uch.sh->VCELL_SIMDATADIR_EXTERNAL=/share/apps/vcell3/users 
//			//${vcellroot}/docker/swarm/serverconfig-uch.sh-> VCELL_SIMDATADIR_HOST=/opt/vcelldata/users 
//			//${vcellroot}/docker/swarm/docker-compose.yml-> Volume map "${VCELL_SIMDATADIR_HOST}:/simdata"
//			Long perUserMemMax = null;
//			Long perSimMemMax = null;
//			Long perSolverMax = null;
//			String memLimitFileDirVal = System.getProperty(PropertyLoader.primarySimDataDirInternalProperty);
//			String memLimitFileVal = System.getProperty(PropertyLoader.simPerUserMemoryLimitFile);
//			if(memLimitFileDirVal != null && memLimitFileVal != null) {
//				memLimitFile = new File(memLimitFileDirVal,memLimitFileVal);
//			}
//			if(memLimitFile != null && memLimitFile.exists()) {
//				List<String> perUserLimits = Files.readAllLines(Paths.get(memLimitFile.getAbsolutePath()));
//				for (Iterator<String> iterator = perUserLimits.iterator(); iterator.hasNext();) {
//					String userAndLimit = iterator.next().trim();
//					if(userAndLimit.length()==0 || userAndLimit.startsWith("//")) {
//						if(bDebugMemLimit){LG.trace("-----skipped '"+userAndLimit+"'");}
//						continue;
//					}
////					LG.trace("-----"+userAndLimit);
//					
//					StringTokenizer st = new StringTokenizer(userAndLimit);
//					String limitUserid = st.nextToken();
//					if(limitUserid.equals(vcellUserid) || (solverDescription != null && limitUserid.equals(solverDescription.name()))) {//check user
//						long memLimit = 0;
//						try {
//							memLimit = Long.parseLong(st.nextToken());
//						} catch (Exception e) {
//							if(bDebugMemLimit){LG.debug("-----ERROR '"+userAndLimit+"' token memlimit not parsed");}
//							//bad line in limit file, continue processing other lines
//							//lg.debug(e);
//							continue;
//						}
//						if(solverDescription != null && limitUserid.equals(solverDescription.name())) {
//							perSolverMax = memLimit;
//							if(bDebugMemLimit){LG.debug("-----"+"MATCH Solver "+userAndLimit);}
//							continue;
//						}
//						//get simid
//						String simSpecifier = null;
//						try {
//							simSpecifier = st.nextToken();
//							//check token is '*' or long
//							if(!simSpecifier.equals("*") && Long.valueOf(simSpecifier).longValue() < 0 ) {
//								throw new Exception(" token 'simSpecifier' expected to be '*' or simID");
//							}
//						} catch (Exception e) {
//							if(bDebugMemLimit){LG.debug("-----ERROR '"+userAndLimit+"' "+e.getClass().getName()+" "+e.getMessage());}
//							//bad line in limit file, continue processing other lines
//							//lg.debug(e);
//							continue;
//						}
//						// * means all sims for that user, don't set if sim specific limit is already set
//						if(simSpecifier.equals("*") && perSimMemMax == null) {
//							perUserMemMax = memLimit;// use this unless overriden by specific simid
//							if(bDebugMemLimit){LG.debug("-----"+"MATCH USER "+userAndLimit);}
//						}
//						//Set sim specific limit, set even if * limit has been set
//						if(simID != null && simID.toString().equals(simSpecifier)) {
//							perSimMemMax = memLimit;// use sim limit
//							if(bDebugMemLimit){LG.debug("-----"+"MATCH SIM "+userAndLimit);}
//						}
//					}else if(limitUserid.equals("defaultSimMemoryLimitMb")) {//Master sim mem limit
//						try {
//							defaultSimMemoryLimitMbFromFile = Long.parseLong(st.nextToken());
//							if(bDebugMemLimit){LG.debug("-----"+"MATCH DEFAULT "+userAndLimit);}
//						} catch (Exception e) {
//							if(bDebugMemLimit){LG.debug("-----ERROR '"+userAndLimit+"' "+e.getClass().getName()+" "+e.getMessage());}
//							//bad line in limit file, continue processing other lines
//							//LG.debug(e);
//							continue;
//						}
//					}else {
//						if(bDebugMemLimit){LG.debug("-----"+"NO MATCH "+userAndLimit);}
//					}
//				}
//				if(perUserMemMax != null || perSimMemMax != null) {
//					long finalMax = (perSimMemMax!=null?perSimMemMax:perUserMemMax);
//					if(bDebugMemLimit){LG.debug("Set memory limit for user '"+vcellUserid+"' to "+finalMax + (perSimMemMax!=null?" for simID="+simID:""));}
//					return new MemLimitResults(finalMax,
//						(perSimMemMax!=null?
//							"MemoryMax(FILE PerSimulation):"+simID+",User='"+vcellUserid+"' from "+memLimitFile.getAbsolutePath():
//							"MemoryMax(FILE PerUser):'"+vcellUserid+"' from "+memLimitFile.getAbsolutePath()));
//				}else if(perSolverMax != null) {
//					if(perSolverMax == 0) {//Use estimated size always if solver had 0 for memory limit
//						return new MemLimitResults(
//							Math.max((long)Math.ceil(estimatedMemSizeMB*1.5),
//										(defaultSimMemoryLimitMbFromFile!=null?defaultSimMemoryLimitMbFromFile:MemLimitResults.FALLBACK_MEM_LIMIT_MB)),
//							"MemoryMax(FILE PerSolver ESTIMATED):'"+solverDescription.name()+"' from "+memLimitFile.getAbsolutePath());
//					}else {
//						return new MemLimitResults(perSolverMax, "MemoryMax(FILE PerSolver):'"+solverDescription.name()+"' from "+memLimitFile.getAbsolutePath());
//					}
//				}
//			}else {
//				if(bDebugMemLimit){LG.debug("-----MemLimitFile "+(memLimitFile==null?"not defined":memLimitFile.getAbsolutePath()+" not exist"));}
//			}
//		} catch (Exception e) {
//			//ignore, try defaults
//			LG.error(e);
//		}
////		long estimatedMemSizeMBL = (long)Math.ceil(estimatedMemSizeMB*1.5);
//		boolean bHasMemLimitFile = defaultSimMemoryLimitMbFromFile!=null;
//		long maxAllowedMem = (bHasMemLimitFile?defaultSimMemoryLimitMbFromFile:MemLimitResults.FALLBACK_MEM_LIMIT_MB);
////		boolean bUseEstimated = (estimatedMemSizeMBL <= maxAllowedMem);
////		return new MemLimitResults(maxAllowedMem,
////			(bUseEstimated?
////				"MemoryMax(ESTIMATED):SimulationTask.getEstimatedMemorySizeMB()="+estimatedMemSizeMBL:
////					(bHasMemLimitFile?
////						"MemoryMax(FILE AllUsers):AllUsersMemLimit(defaultSimMemoryLimitMb) from "+memLimitFile.getAbsolutePath():
////						"MemoryMax(HARDCODE):HtcProxy.MemLimitResults.FALLBACK_MEM_LIMIT_MB")));
//		return new MemLimitResults(maxAllowedMem,
//				(bHasMemLimitFile?
//				"MemoryMax(FILE AllUsers):AllUsersMemLimit(defaultSimMemoryLimitMb) from "+memLimitFile.getAbsolutePath():
//				"MemoryMax(HARDCODE):HtcProxy.MemLimitResults.FALLBACK_MEM_LIMIT_MB"));
	}

//	public static boolean isStochMultiTrial(SimulationTask simTask) {
//		return 	simTask.getSimulationJob().getSimulation().getSolverTaskDescription().getSolverDescription() == SolverDescription.StochGibson &&
//				simTask.getSimulationJob().getSimulation().getSolverTaskDescription().getStochOpt() != null &&
//				!simTask.getSimulationJob().getSimulation().getSolverTaskDescription().getStochOpt().isHistogram() &&
//				simTask.getSimulationJob().getSimulation().getSolverTaskDescription().getStochOpt().getNumOfTrials() > 1;
//
//	}
}



































