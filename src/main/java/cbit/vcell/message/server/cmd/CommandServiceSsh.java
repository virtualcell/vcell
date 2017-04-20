package cbit.vcell.message.server.cmd;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import org.vcell.util.ExecutableException;
import org.vcell.util.PropertyLoader;

import cbit.vcell.mongodb.VCMongoMessage;

public class CommandServiceSsh extends CommandService {
	private SSHClient ssh = null;
	private String remoteHostName = null;
	private String username = null;
	private String password = null;
	
	public CommandServiceSsh(String remoteHostName, String username, String password) throws IOException{
		super();
		this.remoteHostName = remoteHostName;
		this.username = username;
		this.password = password;
		
		ssh = new SSHClient();
		ssh.addHostKeyVerifier(new PromiscuousVerifier());
		ssh.connect(remoteHostName);
		ssh.authPassword(username,password);
	}
	
	@Override
	public CommandOutput command(String[] commandStrings, int[] allowableReturnCodes) throws ExecutableException {
		if (allowableReturnCodes == null){
			throw new IllegalArgumentException("allowableReturnCodes must not be null");
		}
		Session session = null;
		try {
			long timeMS = System.currentTimeMillis();
			session = ssh.startSession();
            boolean bSuppressQStat = false;
			String cmd = CommandOutput.concatCommandStrings(commandStrings);
			if (PropertyLoader.getBooleanProperty(PropertyLoader.suppressQStatStandardOutLogging, true) && cmd.contains("qstat -f")){
				bSuppressQStat = true;
			}
			Session.Command command = session.exec(cmd);
			command.join(1,TimeUnit.MINUTES); //wait up to a minute for return -- will return sooner if command completes
			String standardOutput = IOUtils.readFully(command.getInputStream()).toString();
			String standardError = IOUtils.readFully(command.getErrorStream()).toString();
			Integer exitStatus = command.getExitStatus();
			command.close();
			long elapsedTimeMS = System.currentTimeMillis() - timeMS;
			CommandOutput commandOutput = new CommandOutput(commandStrings, standardOutput, standardError, exitStatus, elapsedTimeMS);
			
			VCMongoMessage.sendCommandServiceCall(commandOutput);

			System.out.println("Command: " + commandOutput.getCommand());
			if(!bQuiet && !bSuppressQStat){System.out.println("Command: stdout = " + commandOutput.getStandardOutput());}
			System.out.println("Command: stderr = " + commandOutput.getStandardError()); 
			System.out.println("Command: exit = " + commandOutput.getExitStatus());
			
			if (commandOutput.getExitStatus()==null){
				System.out.println("Command: exit error message: "+command.getExitErrorMessage());
				System.out.println("Command: exit signal: "+command.getExitSignal());
				System.out.println("Command: exit was core dumped: "+command.getExitWasCoreDumped());
			}
			
			boolean bReturnCodeAllowable = false;
			for (int returnCode : allowableReturnCodes){
				if (commandOutput.getExitStatus()!=null && returnCode == commandOutput.getExitStatus().intValue()){
					bReturnCodeAllowable = true;
				}
			}
			if (!bReturnCodeAllowable){
				throw new ExecutableException("command exited with return code = "+commandOutput.getExitStatus());
			}

			return commandOutput;

		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new ExecutableException(e.getMessage());
		} finally {
			if (session!=null){
				try {
					session.close();
				} catch (TransportException e) {
					e.printStackTrace(System.out);
				} catch (ConnectionException e) {
					e.printStackTrace(System.out);
				}
			}
		}
	}

	@Override
	public CommandService clone() {
		try {
			return new CommandServiceSsh(remoteHostName, username, password);
		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	

	
	@Override
	public void pushFile(File tempFile, String remotePath) throws IOException {
		SFTPClient sftpClient = null;
		try {
			sftpClient = ssh.newSFTPClient();
			sftpClient.put(tempFile.getPath(), remotePath);
		} finally {
			if (sftpClient!=null){
				try {
					sftpClient.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void deleteFile(String remoteFilePath) throws IOException {
		SFTPClient sftpClient = null;
		try {
			sftpClient = ssh.newSFTPClient();
			sftpClient.rm(remoteFilePath);
		} finally {
			if (sftpClient!=null){
				try {
					sftpClient.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void close() {
		if (ssh!=null){
			try {
				ssh.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				ssh = null;
			}
		}
	}
	
}
