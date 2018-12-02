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
	private static String jobNamePrefix( ){
		String stub = "V_";
		String server = PropertyLoader.getRequiredProperty(PropertyLoader.vcellServerIDProperty);
		return stub + server + "_";
	}
	
	public static boolean isMyJob(HtcJobInfo htcJobInfo){
		return htcJobInfo.getJobName().startsWith(jobNamePrefix());
	}
	
	public final static String HTC_SIMULATION_JOB_NAME_PREFIX = jobNamePrefix();
	protected final CommandService commandService;
	protected final String htcUser;



	public HtcProxy(CommandService commandService, String htcUser){
		this.commandService = commandService;
		this.htcUser = htcUser;
	}

	public abstract void killJobSafe(HtcJobInfo htcJobInfo) throws ExecutableException, HtcJobNotFoundException, HtcException;
	public abstract void killJobUnsafe(HtcJobID htcJobId) throws ExecutableException, HtcJobNotFoundException, HtcException;
	public abstract void killJobs(String htcJobSubstring) throws ExecutableException, HtcJobNotFoundException, HtcException;

	/**
	 * @param postProcessingCommands may be null if no commands desired
	 * @param ncpus must be > 1 if any {@link ExecutableCommand}s are marked parallel
	 * @param postProcessingCommands non null
	 * @throws ExecutableException
	 */
	public abstract HtcJobID submitJob(String jobName, File sub_file_internal, File sub_file_external, ExecutableCommand.Container commandSet,
			int ncpus, double memSize, Collection<PortableCommand> postProcessingCommands, SimulationTask simTask) throws ExecutableException;

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
		if (PREFIX.equals("V") && SITE!=null && simIdString!=null && jobIndexString!=null && taskIdString!=null){
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
		public static final long FALLBACK_MEM_LIMIT_MB=4096;//MAX memory allowed if not set in limitFile
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
	}
	public static final boolean bDebugMemLimit = false;
	public static MemLimitResults getMemoryLimit(SimulationTask simulationTask,double estimatedMemSizeMB) {
		String vcellUserid = simulationTask.getUser().getName();
		KeyValue simID = simulationTask.getSimulationInfo().getSimulationVersion().getVersionKey();
		SolverDescription solverDescription = simulationTask.getSimulation().getSolverTaskDescription().getSolverDescription();
		//One of 5 limits are returned (ordered from highest to lowest priority):
		//  MemoryMax:PerSimulation									Has PropertyLoader.simPerUserMemoryLimitFile, specific user AND simID MATCHED in file (userid MemLimitMb simID)
		//  MemoryMax:PerUser										Has PropertyLoader.simPerUserMemoryLimitFile, specific user (but not simID) MATCHED in file (userid MemLimitMb '*')
		//  MemoryMax:SimulationTask.getEstimatedMemorySizeMB()		Has PropertyLoader.simPerUserMemoryLimitFile, no user or sim MATCHED in file ('defaultSimMemoryLimitMb' MemLimitMb '*')
		//																estimated > MemoryMax:AllUsersMemLimit
		//  MemoryMax:AllUsersMemLimit(defaultSimMemoryLimitMb)		Has PropertyLoader.simPerUserMemoryLimitFile, no user or sim MATCHED in file ('defaultSimMemoryLimitMb' MemLimitMb '*')
		//																estimated < MemoryMax:AllUsersMemLimit
		//  MemoryMax:HtcProxy.MemLimitResults.FALLBACK_MEM_LIMIT	No PropertyLoader.simPerUserMemoryLimitFile
		//																estimated < FALLBACK
		
		long estimatedMemSizeMBL = Math.max((long)Math.ceil(estimatedMemSizeMB)*2,128);
		Long defaultSimMemoryLimitMbFromFile = null;
		File memLimitFile = null;
		try {
			//${vcellroot}/docker/swarm/serverconfig-uch.sh->VCELL_SIMDATADIR_EXTERNAL=/share/apps/vcell3/users 
			//${vcellroot}/docker/swarm/serverconfig-uch.sh-> VCELL_SIMDATADIR_HOST=/opt/vcelldata/users 
			//${vcellroot}/docker/swarm/docker-compose.yml-> Volume map "${VCELL_SIMDATADIR_HOST}:/simdata"
			Long perUserMemMax = null;
			Long perSimMemMax = null;
			Long perSolverMax = null;
			String memLimitFileDirVal = System.getProperty(PropertyLoader.primarySimDataDirInternalProperty);
			String memLimitFileVal = System.getProperty(PropertyLoader.simPerUserMemoryLimitFile);
			if(memLimitFileDirVal != null && memLimitFileVal != null) {
				memLimitFile = new File(memLimitFileDirVal,memLimitFileVal);
			}
			if(memLimitFile != null && memLimitFile.exists()) {
				List<String> perUserLimits = Files.readAllLines(Paths.get(memLimitFile.getAbsolutePath()));
				for (Iterator<String> iterator = perUserLimits.iterator(); iterator.hasNext();) {
					String userAndLimit = iterator.next().trim();
					if(userAndLimit.length()==0 || userAndLimit.startsWith("//")) {
						if(bDebugMemLimit){System.out.println("-----skipped '"+userAndLimit+"'");}
						continue;
					}
//					System.out.println("-----"+userAndLimit);
					
					StringTokenizer st = new StringTokenizer(userAndLimit);
					String limitUserid = st.nextToken();
					if(limitUserid.equals(vcellUserid) || limitUserid.equals(solverDescription.name())) {//check user
						long memLimit = 0;
						try {
							memLimit = Long.parseLong(st.nextToken());
						} catch (Exception e) {
							if(bDebugMemLimit){System.out.println("-----ERROR '"+userAndLimit+"' token memlimit not parsed");}
							//bad line in limit file, continue processing other lines
							//e.printStackTrace(System.out);
							continue;
						}
						if(limitUserid.equals(solverDescription.name())) {
							perSolverMax = memLimit;
							if(bDebugMemLimit){System.out.println("-----"+"MATCH Solver "+userAndLimit);}
							continue;
						}
						//get simid
						String simSpecifier = null;
						try {
							simSpecifier = st.nextToken();
							//check token is '*' or long
							if(!simSpecifier.equals("*") && Long.valueOf(simSpecifier).longValue() < 0 ) {
								throw new Exception(" token 'simSpecifier' expected to be '*' or simID");
							}
						} catch (Exception e) {
							if(bDebugMemLimit){System.out.println("-----ERROR '"+userAndLimit+"' "+e.getClass().getName()+" "+e.getMessage());}
							//bad line in limit file, continue processing other lines
							//e.printStackTrace(System.out);
							continue;
						}
						// * means all sims for that user, don't set if sim specific limit is already set
						if(simSpecifier.equals("*") && perSimMemMax == null) {
							perUserMemMax = memLimit;// use this unless overriden by specific simid
							if(bDebugMemLimit){System.out.println("-----"+"MATCH USER "+userAndLimit);}
						}
						//Set sim specific limit, set even if * limit has been set
						if(simID != null && simID.toString().equals(simSpecifier)) {
							perSimMemMax = memLimit;// use sim limit
							if(bDebugMemLimit){System.out.println("-----"+"MATCH SIM "+userAndLimit);}
						}
					}else if(limitUserid.equals("defaultSimMemoryLimitMb")) {//Master sim mem limit
						try {
							defaultSimMemoryLimitMbFromFile = Long.parseLong(st.nextToken());
							if(bDebugMemLimit){System.out.println("-----"+"MATCH DEFAULT "+userAndLimit);}
						} catch (Exception e) {
							if(bDebugMemLimit){System.out.println("-----ERROR '"+userAndLimit+"' "+e.getClass().getName()+" "+e.getMessage());}
							//bad line in limit file, continue processing other lines
							//e.printStackTrace(System.out);
							continue;
						}
					}else {
						if(bDebugMemLimit){System.out.println("-----"+"NO MATCH "+userAndLimit);}
					}
				}
				if(perUserMemMax != null || perSimMemMax != null) {
					long finalMax = (perSimMemMax!=null?perSimMemMax:perUserMemMax);
					if(bDebugMemLimit){System.out.println("Set memory limit for user '"+vcellUserid+"' to "+finalMax + (perSimMemMax!=null?" for simID="+simID:""));}
					return new MemLimitResults(finalMax,
						(perSimMemMax!=null?
							"MemoryMax(FILE PerSimulation):"+simID+",User='"+vcellUserid+"' from "+memLimitFile.getAbsolutePath():
							"MemoryMax(FILE PerUser):'"+vcellUserid+"' from "+memLimitFile.getAbsolutePath()));
				}else if(perSolverMax != null) {
					return new MemLimitResults(perSolverMax, "MemoryMax(FILE PerSolver):'"+solverDescription.name()+"' from "+memLimitFile.getAbsolutePath());
				}
			}else {
				if(bDebugMemLimit){System.out.println("-----MemLimitFile "+(memLimitFile==null?"not defined":memLimitFile.getAbsolutePath()+" not exist"));}
			}
		} catch (Exception e) {
			//ignore, try defaults
			e.printStackTrace();
		}
		boolean bHasMemLimitFile = defaultSimMemoryLimitMbFromFile!=null;
		long maxAllowedMem = (bHasMemLimitFile?defaultSimMemoryLimitMbFromFile:MemLimitResults.FALLBACK_MEM_LIMIT_MB);
		boolean bUseEstimated = (estimatedMemSizeMBL <= maxAllowedMem);
		return new MemLimitResults((bUseEstimated?estimatedMemSizeMBL:maxAllowedMem),
			(bUseEstimated?
				"MemoryMax(ESTIMATED):SimulationTask.getEstimatedMemorySizeMB()="+estimatedMemSizeMBL:
					(bHasMemLimitFile?
						"MemoryMax(FILE AllUsers):AllUsersMemLimit(defaultSimMemoryLimitMb) from "+memLimitFile.getAbsolutePath():
						"MemoryMax(HARDCODE):HtcProxy.MemLimitResults.FALLBACK_MEM_LIMIT_MB")));
	}

}



































