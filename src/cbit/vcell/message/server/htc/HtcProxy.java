package cbit.vcell.message.server.htc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.List;
import java.util.TreeMap;

import org.vcell.util.ExecutableException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.message.server.cmd.CommandService;
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
	
	protected enum HtcJobCategory {
		HTC_SIMULATION_JOB,
		HTC_SERVICE_JOB;
	}
	protected final static String HTC_SIMULATION_JOB_NAME_PREFIX = "S_";
	protected CommandService commandService = null;

	
	public HtcProxy(CommandService commandService){
		this.commandService = commandService;
	}

	public abstract HtcJobStatus getJobStatus(HtcJobID htcJobId) throws HtcException, ExecutableException;

	public abstract void killJob(HtcJobID htcJobId) throws ExecutableException, HtcJobNotFoundException, HtcException;

	public abstract String getPendingReason(HtcJobID jobid) throws ExecutableException, HtcException;

	public HtcJobID submitJob(String jobName, String sub_file, String[] command, int ncpus, double memSize) throws ExecutableException {
		return submitJob(jobName, sub_file, command, ncpus, memSize, HtcJobCategory.HTC_SIMULATION_JOB, null, false);
	}

	public HtcJobID submitJob(String jobName, String sub_file, String[] command, String[] secondCommand, int ncpus, double memSize) throws ExecutableException {
		return submitJob(jobName, sub_file, command, ncpus, memSize, HtcJobCategory.HTC_SIMULATION_JOB, secondCommand, false);
	}

	public HtcJobID submitServiceJob(String jobName, String sub_file, String[] command, int ncpus, double memSize) throws ExecutableException {
		return submitJob(jobName, sub_file, command, ncpus, memSize, HtcJobCategory.HTC_SERVICE_JOB, null, true);
	}
	
	protected abstract HtcJobID submitJob(String jobName, String sub_file, String[] command, int ncpus, double memSize, HtcJobCategory jobCategory, String[] secondCommand, boolean isServiceJob) throws ExecutableException;

	public abstract HtcProxy cloneThreadsafe();
	
	public abstract TreeMap<HtcJobID, String> getServiceJobIDs(VCellServerID serverID) throws ExecutableException;

	public final CommandService getCommandService() {
		return commandService;
	}

	public static String createHtcSimJobName(KeyValue simKey, int simJobIndex) {
		return HTC_SIMULATION_JOB_NAME_PREFIX+simKey.toString()+"_"+simJobIndex;
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
}
