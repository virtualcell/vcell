package cbit.vcell.message.server.htc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.vcell.util.document.KeyValue;
import org.vcell.util.exe.ExecutableException;

import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.cmd.CommandServiceLocal;
import cbit.vcell.message.server.cmd.CommandServiceSsh;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.simdata.PortableCommand;
import cbit.vcell.solvers.ExecutableCommand;

public abstract class HtcProxy {
	public static final Logger LG = Logger.getLogger(HtcProxy.class);

	/**
	 *
	 * in order for remote (non-interactive) shells to work with SGE, some environment variables have to be set
	 *
	 * we created a .bashrc file in the home directory of user vcell with the following single line content:
	 *
	 * if [ "${HOSTNAME}" = "sigcluster2.cam.uchc.edu" ]; then source /etc/profile.d/sge-binaries.sh; fi
	 *
	 * this will execute on sigcluster2 (with SGE) and not execute on sigcluster (with PBS).
	 *
	 */

	public static class HtcJobInfo{
		private final HtcJobID htcJobID;
		private final String jobName;
		//private String errorPath;
		//private String outputPath;
		private final boolean bFound;
		/**
		 * @param htcJobID
		 * @param bFound
		 * @param jobName
		 * @param errorPath ignored
		 * @param outputPath ignored
		 */
		public HtcJobInfo(HtcJobID htcJobID, boolean bFound, String jobName,String errorPath,String outputPath) {
			this.htcJobID = htcJobID;
			this.bFound = bFound;
			this.jobName = jobName;
			//this.errorPath = errorPath;
			//this.outputPath = outputPath;
		}
		public HtcJobID getHtcJobID() {
			return htcJobID;
		}
		public String getJobName(){
			validate();
			return jobName;
		}
		/*
		public String getErrorPath() {
			validate();
			return errorPath;
		}
		public String getOutputPath() {
			validate();
			return outputPath;
		}
		*/
		public boolean isFound(){
			return bFound;
		}
		public String toString(){
			if (bFound){
				return "HtcJobInfo(jobID="+htcJobID.toDatabase()+",found=true,jobName="+jobName+")";
			}else{
				return "HtcJobInfo(jobID="+htcJobID.toDatabase()+", JOB NOT FOUND)";
			}
		}
		private void validate(){
			if(!isFound()){
				throw new RuntimeException("Must call isFound() before using HtcJobInfo to verify info exists");
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
	 * @return V_<i>server<i> or V_
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

	public abstract HtcJobStatus getJobStatus(HtcJobID htcJobId) throws HtcException, ExecutableException;

	public abstract void killJob(HtcJobID htcJobId) throws ExecutableException, HtcJobNotFoundException, HtcException;

//	public HtcJobID submitJob(String jobName, String sub_file, String[] command, String[] secondCommand, int ncpus, double memSizeMB, String[] exitCommand,
//			String exitCodeReplaceTag, Collection<PortableCommand> postProcessingCommands) throws ExecutableException {
//		return submitJob(jobName, sub_file, command, ncpus, memSizeMB, secondCommand, exitCommand, exitCodeReplaceTag, postProcessingCommands);
//	}
//
//	/**
//	 * @param postProcessingCommands may be null if no commands desired
//	 * @throws ExecutableException
//	 */
//	protected abstract HtcJobID submitJob(String jobName, String sub_file, String[] command, int ncpus, double memSizeMB, String[] secondCommand, String[] exitCommand,
//			String exitCodeReplaceTag, Collection<PortableCommand> postProcessingCommands) throws ExecutableException;
	/**
	 * @param postProcessingCommands may be null if no commands desired
	 * @param ncpus must be > 1 if any {@link ExecutableCommand}s are marked parallel
	 * @param postProcessingCommands non null
	 * @throws ExecutableException
	 */
	public abstract HtcJobID submitJob(String jobName, String sub_file_external, ExecutableCommand.Container commandSet,
			int ncpus, double memSize, Collection<PortableCommand> postProcessingCommands) throws ExecutableException;

	public abstract HtcProxy cloneThreadsafe();

	public final List<HtcJobID> getRunningSimulationJobIDs() throws ExecutableException, IOException {
		return getRunningJobIDs(HTC_SIMULATION_JOB_NAME_PREFIX);
	}

	public abstract List<HtcJobID> getRunningJobIDs(String jobNamePrefix) throws ExecutableException, IOException;

	public abstract Map<HtcJobID,HtcJobInfo> getJobInfos(List<HtcJobID> htcJobIDs) throws ExecutableException;

	public final CommandService getCommandService() {
		return commandService;
	}

	public abstract String[] getEnvironmentModuleCommandPrefix();

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

			FileChannel fc = fos.getChannel();
			fc.write(bb);
			fc.close();
		}
	}

	public abstract String getSubmissionFileExtension();

	/**
	 * for unix-style commands that have (e.g. pipes), using the java Runtime.exec(String[] cmd), the pipe requires a shell to operate properly.
	 * the SSH command already invokes this with a shell, so it is not required.
	 */
	protected final String[] constructShellCommand(CommandService commandService, String[] cmd){
		if (commandService instanceof CommandServiceSsh){
			ArrayList<String> ar = new ArrayList<String>();
			ar.addAll(Arrays.asList(getEnvironmentModuleCommandPrefix()));
			ar.addAll(Arrays.asList(cmd));
			return ar.toArray(new String[0]);
		}else if (commandService instanceof CommandServiceLocal){
			StringBuffer sb = new StringBuffer();


/*  		Code to invoke environment modules */

//			String[] envModulePrefix = getEnvironmentModuleCommandPrefix();
//			for (int i = 0; i< envModulePrefix.length; i++){
//				sb.append((i>0?" ":"")+envModulePrefix[i]);
//			}
			 //if code above is uncommented, line 2 lines down below becomes sb.append(" "+cmd[i]); instead of sb.append((i>0?" ":"")+cmd[i]);

			for (int i = 0; i < cmd.length; i++) {
				sb.append((i>0?" ":"")+cmd[i]);
			}
			return new String[] { "/bin/sh","-c",sb.toString()};
		}
		throw new RuntimeException("expected either SSH or Local CommandService");
	}

}



































