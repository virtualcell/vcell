package cbit.vcell.message.server.cmd;

import org.apache.log4j.Logger;
import org.vcell.util.exe.ExecutableException;


public abstract class CommandService {

	public static Logger lg = Logger.getLogger(CommandService.class);
	
	public static class CommandOutput {
		private String[] commandStrings;
		private String standardOutput;
		private String standardError;
		private Integer exitStatus;
		private long elapsedTimeMS;

		public CommandOutput(String[] commandStrings, String standardOutput, String standardError, Integer exitStatus, long elapsedTimeMS) {
			this.commandStrings = commandStrings;
			this.standardOutput = standardOutput;
			this.standardError = standardError;
			this.exitStatus = exitStatus;
			this.elapsedTimeMS = elapsedTimeMS;
		}
		public String[] getCommandStrings() {
			return commandStrings;
		}
		public String getCommand(){
			return concatCommandStrings(commandStrings);
		}
		public String getStandardOutput() {
			return standardOutput;
		}
		public String getStandardError() {
			return standardError;
		}
		public Integer getExitStatus() {
			return exitStatus;
		}
		public long getElapsedTimeMS() {
			return elapsedTimeMS;
		}
		public static String concatCommandStrings(String[] cmdStrings){
			StringBuffer cmd = new StringBuffer();
			for (String cmdStr : cmdStrings){
				cmd.append(cmdStr);
				cmd.append(" ");
			}
			return cmd.toString().trim();
		}
	}


	public CommandService(){
	}
	
	@Override
	public abstract CommandService clone();

//	public abstract void pushFile(File tempFile, String remotePath) throws IOException;
//	
//	public abstract void deleteFile(String remoteFilePath) throws IOException;
// 
	public final CommandOutput command(String[] command) throws ExecutableException {
		return command(command,new int[] { 0 });
	}

	public abstract CommandOutput command(String[] command, int[] allowableReturnCodes) throws ExecutableException;

	public abstract void close();
	
}
