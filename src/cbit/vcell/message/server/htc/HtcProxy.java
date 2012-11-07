package cbit.vcell.message.server.htc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

import org.vcell.util.BeanUtils;
import org.vcell.util.ExecutableException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.message.server.cmd.CommandService;
import cbit.vcell.message.server.cmd.CommandServiceLocal;
import cbit.vcell.message.server.cmd.CommandServiceSsh;
import cbit.vcell.message.server.htc.pbs.PbsProxy;

public abstract class HtcProxy {
	
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
		private HtcJobID htcJobID;
		private String jobName;
		private String errorPath;
		private String outputPath;
		public HtcJobInfo(HtcJobID htcJobID, String jobName,String errorPath,String outputPath) {
			this.htcJobID = htcJobID;
			this.jobName = jobName;
			this.errorPath = errorPath;
			this.outputPath = outputPath;
		}
		public HtcJobID getHtcJobID() {
			return htcJobID;
		}
		public String getJobName(){
			return jobName;
		}
		public String getErrorPath() {
			return errorPath;
		}
		public String getOutputPath() {
			return outputPath;
		}
		public String toString(){
			return "HtcJobInfo(jobID="+htcJobID.toDatabase()+",jobName="+jobName+",errorPath="+errorPath+",outputPath="+outputPath+")";
		}
	}
	protected enum HtcJobCategory {
		HTC_SIMULATION_JOB,
		HTC_SERVICE_JOB;
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
	}
	
	public final static String HTC_SIMULATION_JOB_NAME_PREFIX = "S_";
	protected CommandService commandService = null;

	
	public HtcProxy(CommandService commandService){
		this.commandService = commandService;
	}

	public abstract HtcJobStatus getJobStatus(HtcJobID htcJobId) throws HtcException, ExecutableException;

	public abstract void killJob(HtcJobID htcJobId) throws ExecutableException, HtcJobNotFoundException, HtcException;

	public abstract String getPendingReason(HtcJobID jobid) throws ExecutableException, HtcException;

	public HtcJobID submitJob(String jobName, String sub_file, String[] command, int ncpus, double memSize, String[] exitCommand, String exitCodeReplaceTag) throws ExecutableException {
		return submitJob(jobName, sub_file, command, ncpus, memSize, HtcJobCategory.HTC_SIMULATION_JOB, null, false, exitCommand, exitCodeReplaceTag);
	}

	public HtcJobID submitJob(String jobName, String sub_file, String[] command, String[] secondCommand, int ncpus, double memSize, String[] exitCommand, String exitCodeReplaceTag) throws ExecutableException {
		return submitJob(jobName, sub_file, command, ncpus, memSize, HtcJobCategory.HTC_SIMULATION_JOB, secondCommand, false, exitCommand, exitCodeReplaceTag);
	}

	public HtcJobID submitServiceJob(String jobName, String sub_file, String[] command, int ncpus, double memSize) throws ExecutableException {
		return submitJob(jobName, sub_file, command, ncpus, memSize, HtcJobCategory.HTC_SERVICE_JOB, null, true, null, null);
	}
	
	protected abstract HtcJobID submitJob(String jobName, String sub_file, String[] command, int ncpus, double memSize, HtcJobCategory jobCategory, String[] secondCommand, boolean isServiceJob, String[] exitCommand, String exitCodeReplaceTag) throws ExecutableException;

	public abstract HtcProxy cloneThreadsafe();
	
	public final List<HtcJobID> getRunningServiceJobIDs(VCellServerID serverID) throws ExecutableException {
		return getRunningJobIDs(serverID.toString().toUpperCase()+"_");
	}

	public final List<HtcJobID> getRunningSimulationJobIDs() throws ExecutableException {
		return getRunningJobIDs(HTC_SIMULATION_JOB_NAME_PREFIX);
	}

	public abstract List<HtcJobID> getRunningJobIDs(String jobNamePrefix) throws ExecutableException;
	
	public abstract List<HtcJobInfo> getJobInfos(List<HtcJobID> htcJobIDs) throws ExecutableException;

	public final CommandService getCommandService() {
		return commandService;
	}

	public static SimTaskInfo getSimTaskInfoFromSimJobName(String simJobName) throws HtcException{
		if (simJobName.startsWith(HTC_SIMULATION_JOB_NAME_PREFIX)){
			String restOfName = simJobName.substring(HTC_SIMULATION_JOB_NAME_PREFIX.length());
			StringTokenizer tokens = new StringTokenizer(restOfName,"_");
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
			if (simIdString!=null && jobIndexString!=null && taskIdString!=null){
				KeyValue simId = new KeyValue(simIdString);
				int jobIndex = Integer.valueOf(jobIndexString);
				int taskId = Integer.valueOf(taskIdString);
				return new SimTaskInfo(simId,jobIndex,taskId);
			}
		}
		throw new HtcException("simJobName : "+simJobName+" not in expected format for a simulation job name");
	}

	public static String createHtcSimJobName(SimTaskInfo simTaskInfo) {
		return HTC_SIMULATION_JOB_NAME_PREFIX+simTaskInfo.simId.toString()+"_"+simTaskInfo.jobIndex+"_"+simTaskInfo.taskId;
	}

	public static void writeUnixStyleTextFile(File file, String javaString)
			throws IOException {
				FileChannel fc = new FileOutputStream(file).getChannel();
				
			    Charset asciiCharset = Charset.forName("US-ASCII");
			    CharsetEncoder encoder = asciiCharset.newEncoder();
			    CharBuffer unicodeCharBuffer = CharBuffer.wrap(javaString);
			    ByteBuffer asciiByteBuffer = encoder.encode(unicodeCharBuffer);
			    byte[] asciiArray = asciiByteBuffer.array();
			    ByteBuffer unixByteBuffer = ByteBuffer.allocate(asciiArray.length);
			    for (int i=0;i<asciiArray.length;i++){
			    	if (asciiArray[i] != 0x0d){  // skip \r character
			    		unixByteBuffer.put(asciiArray[i]);
			    	}
			    }
			    unixByteBuffer.rewind();
			    fc.write(unixByteBuffer);
			    fc.close();
			}

	public abstract String getSubmissionFileExtension();

	public abstract void checkServerStatus() throws ExecutableException;
	
	/**
	 * for unix-style commands that have (e.g. pipes), using the java Runtime.exec(String[] cmd), the pipe requires a shell to operate properly.
	 * the SSH command already invokes this with a shell, so it is not required.
	 */
	protected final String[] constructShellCommand(CommandService commandService, String[] cmd){
		if (commandService instanceof CommandServiceSsh){
			return cmd;
		}else if (commandService instanceof CommandServiceLocal){
			return BeanUtils.addElements(BeanUtils.addElements(new String[] { "/bin/sh","-c","\""}, cmd), new String[] { "\"" });
		}
		throw new RuntimeException("expected either SSH or Local CommandService");
	}
}
