package cbit.vcell.message.server.cmd;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.vcell.util.exe.ExecutableException;

import cbit.vcell.mongodb.VCMongoMessage;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.FileKeyProvider;
import net.schmizz.sshj.userauth.keyprovider.OpenSSHKeyFile;

public class CommandServiceSsh extends CommandService {
	private String remoteHostName = null;
	private String username = null;
	private File keyFile = null;
	
	public CommandServiceSsh(String remoteHostName, String username, File keyFile) throws IOException{
		super();
		this.remoteHostName = remoteHostName;
		this.username = username;
		this.keyFile = keyFile;
	}
	
	private SSHClient createNewSSHClient(boolean bRetry) throws IOException {
		long timestamp_ms = 0;
		if (lg.isTraceEnabled()) {
			timestamp_ms = System.currentTimeMillis();
			lg.error("creating new ssh client");
		}
		SSHClient ssh = new SSHClient();
		try {
			ssh.addHostKeyVerifier(new PromiscuousVerifier());
			ssh.connect(remoteHostName);
			FileKeyProvider keyProvider = new OpenSSHKeyFile();
			keyProvider.init(keyFile);
			ssh.authPublickey(username,keyProvider);
			if (lg.isTraceEnabled()) {
				long diff_ms = System.currentTimeMillis() - timestamp_ms;
				lg.trace("created new SSH client, elapsed time = "+diff_ms+" ms");
			}
			return ssh;
		}catch (Exception e) {
			lg.error("failed to create new SSH client (retry="+bRetry+"): "+e.getMessage(), e);
			try {
				ssh.close();
			}catch (IOException e1) {
				lg.error("failed to create new ssh client", e1);
			}
			if (bRetry) {
				return createNewSSHClient(false);
			}else {
				throw e;
			}
		}
	}
	
	@Override
	public CommandOutput command(String[] commandStrings, int[] allowableReturnCodes) throws ExecutableException {
		if (allowableReturnCodes == null){
			throw new IllegalArgumentException("allowableReturnCodes must not be null");
		}
		long timeMS = System.currentTimeMillis();
		try (SSHClient ssh = createNewSSHClient(true);
			Session session = ssh.startSession();)
		{
			String cmd = CommandOutput.concatCommandStrings(commandStrings);
			Session.Command command = session.exec(cmd);
			command.join(1,TimeUnit.MINUTES); //wait up to a minute for return -- will return sooner if command completes
			String standardOutput = IOUtils.readFully(command.getInputStream()).toString();
			String standardError = IOUtils.readFully(command.getErrorStream()).toString();
			Integer exitStatus = command.getExitStatus();
			command.close();
			long elapsedTimeMS = System.currentTimeMillis() - timeMS;
			CommandOutput commandOutput = new CommandOutput(commandStrings, standardOutput, standardError, exitStatus, elapsedTimeMS);
			
			VCMongoMessage.sendCommandServiceCall(commandOutput);

			
			if (commandOutput.getExitStatus()==null){
				lg.error("Command: " + commandOutput.getCommand());
				lg.error("Command: exit error message: "+command.getExitErrorMessage());
				lg.error("Command: exit signal: "+command.getExitSignal());
				lg.error("Command: exit was core dumped: "+command.getExitWasCoreDumped());
			}else {
				lg.debug("Command: " + commandOutput.getCommand());
				lg.trace("Command: stdout = " + commandOutput.getStandardOutput());
				lg.trace("Command: stderr = " + commandOutput.getStandardError()); 
				lg.debug("Command: exit = " + commandOutput.getExitStatus());
			}
			
			boolean bReturnCodeAllowable = false;
			for (int returnCode : allowableReturnCodes){
				if (commandOutput.getExitStatus()!=null && returnCode == commandOutput.getExitStatus().intValue()){
					bReturnCodeAllowable = true;
				}
			}
			if (!bReturnCodeAllowable){
				lg.error("Command: " + commandOutput.getCommand());
				lg.error("Command: stdout = " + commandOutput.getStandardOutput());
				lg.error("Command: stderr = " + commandOutput.getStandardError()); 
				lg.error("Command: exit = " + commandOutput.getExitStatus());
				throw new ExecutableException("command exited with return code = "+commandOutput.getExitStatus());
			}

			return commandOutput;

		} catch (Exception e) {
			lg.error("command failed", e);
			throw new ExecutableException(e.getMessage());
		}
	}

	@Override
	public CommandService clone() {
		try {
			return new CommandServiceSsh(remoteHostName, username, keyFile);
		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
	@Override
	public void pushFile(File tempFile, String remotePath) throws IOException {
		if (lg.isTraceEnabled()) {
			lg.trace("pushing file "+tempFile+" (exists="+tempFile.exists()+") ==> "+remotePath);
		}
		long timestamp_ms = System.currentTimeMillis();
		try (SSHClient ssh = createNewSSHClient(true);
			SFTPClient sftpClient = ssh.newSFTPClient();)
		{
			sftpClient.put(tempFile.getPath(), remotePath);
		} catch (Exception e) {
			lg.error("failed to push file", e);
			throw new IOException("failed to push file: "+e.getMessage(),e);
		}
		if (lg.isTraceEnabled()) {
			long diff_ms = System.currentTimeMillis() - timestamp_ms;
			lg.trace("pushed file to "+remotePath+": elapsedTime = "+diff_ms+" ms");
		}
		
	}

	public void deleteFile(String remoteFilePath) throws IOException {
		if (lg.isTraceEnabled()) {
			lg.trace("deleting remote file "+remoteFilePath);
		}
		long timestamp_ms = System.currentTimeMillis();
		try (SSHClient ssh = createNewSSHClient(true);
			SFTPClient sftpClient = ssh.newSFTPClient();)
		{
			sftpClient.rm(remoteFilePath);
		}
		if (lg.isTraceEnabled()) {
			long diff_ms = System.currentTimeMillis() - timestamp_ms;
			lg.trace("deleted remote file "+remoteFilePath+": elapsedTime = "+diff_ms+" ms");
		}
	}

	@Override
	public void close() {
	}
	
}
