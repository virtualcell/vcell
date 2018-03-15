package cbit.vcell.message.server.cmd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.vcell.util.exe.ExecutableException;


public class CommandServiceSshNative extends CommandService {
	private final String remoteHostName;
	private final String username;
	private final File installedKeyFile;
	CommandServiceLocal cmdServiceLocal = new CommandServiceLocal();
			
	
	public CommandServiceSshNative(String remoteHostName, String username, File keyFile) throws IOException{
		super();
		this.remoteHostName = remoteHostName;
		this.username = username;
		//
		// assumes that the keyFile is supplied as a secret within a container running as user root
		//
		this.installedKeyFile = new File("/root/.ssh/"+username+"_rsa");
		if (!keyFile.getAbsolutePath().equals(installedKeyFile.getAbsolutePath())) {
			try {
				cmdServiceLocal.command(new String[] { "mkdir", "-p", "/root/.ssh" });
				cmdServiceLocal.command(new String[] { "chmod", "500", "/root/.ssh" });
				cmdServiceLocal.command(new String[] { "cp", keyFile.getAbsolutePath(), installedKeyFile.getAbsolutePath()}, new int [] { 0 });
				cmdServiceLocal.command(new String[] { "chmod", "400", installedKeyFile.getAbsolutePath() }, new int[] { 0 });
			} catch (ExecutableException e) {
				lg.error("failed to install keyFile "+keyFile.getAbsolutePath()+" for ssh user "+username+" into "+installedKeyFile.getAbsolutePath(), e);
			}
		}
	}
		
	@Override
	public CommandOutput command(String[] commandStrings, int[] allowableReturnCodes) throws ExecutableException {
		if (allowableReturnCodes == null){
			throw new IllegalArgumentException("allowableReturnCodes must not be null");
		}
		String[] sshCmdPrefix = new String[] { "ssh", "-i", installedKeyFile.getAbsolutePath(), username+"@"+remoteHostName };
		ArrayList<String> cmdList = new ArrayList<String>();
		cmdList.addAll(Arrays.asList(sshCmdPrefix));
		cmdList.addAll(Arrays.asList(commandStrings));
		String[] cmd = cmdList.toArray(new String[0]);
		long timeMS = System.currentTimeMillis();
		CommandOutput commandOutput = cmdServiceLocal.command(cmd, allowableReturnCodes);
		if (lg.isTraceEnabled()) {
			long elapsedTimeMS = System.currentTimeMillis() - timeMS;
			lg.trace(cmdList.toString()+": elapsed time is "+elapsedTimeMS+" ms");
		}
		return commandOutput;
	}

	@Override
	public CommandService clone() {
		try {
			return new CommandServiceSshNative(remoteHostName, username, this.installedKeyFile);
		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
	@Override
	public void close() {
	}
	
}
