package cbit.vcell.message.server.cmd;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.vcell.util.exe.ExecutableException;

import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.PropertyLoader;
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
		SSHClient ssh = new SSHClient();
		try {
			ssh.addHostKeyVerifier(new PromiscuousVerifier());
			ssh.connect(remoteHostName);
			FileKeyProvider keyProvider = new OpenSSHKeyFile();
			keyProvider.init(keyFile);
			ssh.authPublickey(username,keyProvider);
			return ssh;
		}catch (Exception e) {
			try {
				ssh.close();
			}catch (IOException e1) {
				e1.printStackTrace();
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
		try (SSHClient ssh = createNewSSHClient(true);
			SFTPClient sftpClient = ssh.newSFTPClient();)
		{
			System.out.println("CommandServiceSsh.pushFile("+tempFile.getAbsolutePath()+" (exists="+tempFile.exists()+") ==> "+remotePath);
			sftpClient.put(tempFile.getPath(), remotePath);
		}
	}

	public void deleteFile(String remoteFilePath) throws IOException {
		try (SSHClient ssh = createNewSSHClient(true);
			SFTPClient sftpClient = ssh.newSFTPClient();)
		{
			sftpClient.rm(remoteFilePath);
		}
	}

	@Override
	public void close() {
	}
	
}
