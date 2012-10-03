package cbit.vcell.message.server.pbs;

import java.io.File;
import java.io.IOException;

import org.vcell.util.Executable;
import org.vcell.util.ExecutableException;
import org.vcell.util.FileUtils;

import cbit.vcell.mongodb.VCMongoMessage;

public class PbsProxyLocal extends PbsProxy {
	
	public PbsProxyLocal(){
		super();
	}

	@Override
	public CommandOutput command(String[] command) throws ExecutableException {
		long timeMS = System.currentTimeMillis();
		Executable exe = new Executable(command);
		exe.start();
		long elapsedTimeMS = System.currentTimeMillis() - timeMS;
		CommandOutput commandOutput = new CommandOutput(command, exe.getStdoutString(), exe.getStderrString(), exe.getExitValue(), elapsedTimeMS);
		VCMongoMessage.sendPbsCall(this,commandOutput);

		System.out.println("Command: " + commandOutput.getCommand());
		System.out.println("Command: stdout = " + commandOutput.getStandardOutput()); 
		System.out.println("Command: stderr = " + commandOutput.getStandardError()); 
		System.out.println("Command: exit = " + commandOutput.getExitStatus());

		return commandOutput;
	}

	@Override
	public PbsProxy clone() {
		return new PbsProxyLocal();
	}

	@Override
	public void pushFile(File tempFile, String remotePath) throws IOException {
		FileUtils.copyFile(tempFile, new File(remotePath));
	}

	public void deleteFile(String remoteFilePath) throws IOException {
		FileUtils.deleteFile(remoteFilePath);
	}

//	public static void main(String[] args){
//		PbsProxyLocal thisProxy = null;
//		ArrayList<RunningPbsJobRecord> records = null;
//
//		thisProxy = new PbsProxyLocal();
//
//		try {
//			 records = thisProxy.getRunningPBSJobs();
//		} catch (ExecutableException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		System.out.println("PBS JobID , SimID");
//		Iterator<RunningPbsJobRecord> iter = records.iterator();
//		while (iter.hasNext()){
//			RunningPbsJobRecord record = iter.next();
//			System.out.println(record.getPbsJobId()+"   ,    "+record.getPbsJobName());
//		}
//		System.out.println("done");
//	}
	
}
