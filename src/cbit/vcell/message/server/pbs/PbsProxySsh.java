package cbit.vcell.message.server.pbs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import org.vcell.util.ExecutableException;

import cbit.vcell.mongodb.VCMongoMessage;

public class PbsProxySsh extends PbsProxy {
	private SSHClient ssh = null;
	private String remoteHostName = null;
	private String username = null;
	private String password = null;
	
	public PbsProxySsh(String remoteHostName, String username, String password) throws IOException{
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
			
			VCMongoMessage.sendPbsCall(this,commandOutput);

			System.out.println("Command: " + commandOutput.getCommand());
			System.out.println("Command: stdout = " + commandOutput.getStandardOutput()); 
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
	public PbsProxy clone() {
		try {
			return new PbsProxySsh(remoteHostName, username, password);
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
	
	public static void main(String[] args){
		PbsProxySsh thisProxy = null;
		ArrayList<RunningPbsJobRecord> records = null;
		try {
			thisProxy = new PbsProxySsh(args[0], args[1], args[2]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			 records = thisProxy.getRunningPBSJobs();
		} catch (ExecutableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("PBS JobID , JobName,  SimID  , JobIndex");
		Iterator<RunningPbsJobRecord> iter = records.iterator();
		while (iter.hasNext()){
			RunningPbsJobRecord record = iter.next();
			System.out.println(record.getPbsJobId()+"   ,    "+record.getPbsJobName()+" ,  "+ record.getSimID().toString()+"  ,  "+record.getSimJobIndex());
		}
		System.out.println("done");
	}
	
}
