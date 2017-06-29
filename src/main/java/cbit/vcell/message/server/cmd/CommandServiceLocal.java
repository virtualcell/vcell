package cbit.vcell.message.server.cmd;

import java.io.File;
import java.io.IOException;

import org.vcell.util.FileUtils;
import org.vcell.util.exe.Executable;
import org.vcell.util.exe.ExecutableException;

import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.PropertyLoader;

public class CommandServiceLocal extends CommandService {
	
	public CommandServiceLocal(){
		super();
	}

	@Override
	public CommandOutput command(String[] command, int[] allowableReturnCodes) throws ExecutableException {
		if (allowableReturnCodes == null){
			throw new IllegalArgumentException("allowableReturnCodes must not be null");
		}
		long timeMS = System.currentTimeMillis();
		Executable exe = new Executable(command);
		exe.start(allowableReturnCodes);
		long elapsedTimeMS = System.currentTimeMillis() - timeMS;
		CommandOutput commandOutput = new CommandOutput(command, exe.getStdoutString(), exe.getStderrString(), exe.getExitValue(), elapsedTimeMS);
        boolean bSuppressQStat = false;
		VCMongoMessage.sendCommandServiceCall(commandOutput);
		if (PropertyLoader.getBooleanProperty(PropertyLoader.suppressQStatStandardOutLogging, true) && commandOutput.getCommand().contains("qstat -f")){
			bSuppressQStat = true;
		}
		System.out.println("Command: " + commandOutput.getCommand());
		if(!bQuiet && !bSuppressQStat){System.out.println("Command: stdout = " + commandOutput.getStandardOutput());}
		System.out.println("Command: stderr = " + commandOutput.getStandardError()); 
		System.out.println("Command: exit = " + commandOutput.getExitStatus());
		
		return commandOutput;
	}

	@Override
	public CommandService clone() {
		return new CommandServiceLocal();
	}

	@Override
	public void pushFile(File tempFile, String remotePath) throws IOException {
		FileUtils.copyFile(tempFile, new File(remotePath));
	}

	public void deleteFile(String remoteFilePath) throws IOException {
		FileUtils.deleteFile(remoteFilePath);
	}

	@Override
	public void close() {
	}

}
