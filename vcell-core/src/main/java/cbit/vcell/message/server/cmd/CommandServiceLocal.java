package cbit.vcell.message.server.cmd;

import org.vcell.util.exe.Executable;
import org.vcell.util.exe.ExecutableException;

import cbit.vcell.mongodb.VCMongoMessage;

public class CommandServiceLocal extends CommandService {
	
	public CommandServiceLocal(){
		super();
	}

	@Override
	public CommandOutput command(String[] command, int[] allowableReturnCodes) throws ExecutableException {
		return commandWithTimeout(command, allowableReturnCodes,0);
	}
	public CommandOutput commandWithTimeout(String[] command, int[] allowableReturnCodes,int timeoutMilliSec) throws ExecutableException {
		if (allowableReturnCodes == null){
			throw new IllegalArgumentException("allowableReturnCodes must not be null");
		}
		long timeMS = System.currentTimeMillis();
		Executable exe = new Executable(command,timeoutMilliSec);
		exe.start(allowableReturnCodes);
		long elapsedTimeMS = System.currentTimeMillis() - timeMS;
		CommandOutput commandOutput = new CommandOutput(command, exe.getStdoutString(), exe.getStderrString(), exe.getExitValue(), elapsedTimeMS);
//        VCMongoMessage.sendCommandServiceCall(commandOutput);
		lg.trace("Command: " + commandOutput.getCommand());
		lg.trace("Command: stdout = " + commandOutput.getStandardOutput());
		lg.trace("Command: stderr = " + commandOutput.getStandardError()); 
		lg.trace("Command: exit = " + commandOutput.getExitStatus());
		
		return commandOutput;
	}

	@Override
	public CommandService clone() {
		return new CommandServiceLocal();
	}

	@Override
	public void close() {
	}

}
