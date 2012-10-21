package cbit.vcell.message.server.cmd;

import java.io.File;
import java.io.IOException;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import org.vcell.util.ExecutableException;

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
	public CommandOutput command(String[] commandStrings) throws ExecutableException {
		Session session = null;
		try {
			long timeMS = System.currentTimeMillis();
			session = ssh.startSession();

			String cmd = CommandOutput.concatCommandStrings(commandStrings);

			Session.Command command = session.exec(cmd);
			String standardOutput = IOUtils.readFully(command.getInputStream()).toString();
			String standardError = IOUtils.readFully(command.getErrorStream()).toString();
			Integer exitStatus = command.getExitStatus();
			command.close();
			long elapsedTimeMS = System.currentTimeMillis() - timeMS;
			CommandOutput commandOutput = new CommandOutput(commandStrings, standardOutput, standardError, exitStatus, elapsedTimeMS);
			
			VCMongoMessage.sendCommandServiceCall(commandOutput);

			System.out.println("Command: " + commandOutput.getCommand());
			if(!bQuiet){System.out.println("Command: stdout = " + commandOutput.getStandardOutput());}
			System.out.println("Command: stderr = " + commandOutput.getStandardError()); 
			System.out.println("Command: exit = " + commandOutput.getExitStatus());

			return commandOutput;

		} catch (Exception e) {
			e.printStackTrace();
			throw new ExecutableException(e.getMessage());
		} finally {
			if (session!=null){
				try {
					session.close();
				} catch (TransportException e) {
					e.printStackTrace();
				} catch (ConnectionException e) {
					e.printStackTrace();
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
