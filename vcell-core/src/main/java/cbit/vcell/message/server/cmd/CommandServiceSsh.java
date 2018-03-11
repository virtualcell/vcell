package cbit.vcell.message.server.cmd;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.vcell.util.exe.ExecutableException;

import cbit.vcell.mongodb.VCMongoMessage;
import net.schmizz.keepalive.KeepAliveProvider;
import net.schmizz.sshj.DefaultConfig;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.FileKeyProvider;
import net.schmizz.sshj.userauth.keyprovider.OpenSSHKeyFile;


public class CommandServiceSsh extends CommandService {
	private String remoteHostName = null;
	private String username = null;
	private File keyFile = null;
	private ObjectPool<SSHClient> pool = new GenericObjectPool<SSHClient>(new SSHClientFactory());
			
	public class SSHClientFactory
	    extends BasePooledObjectFactory<SSHClient> {

	    @Override
	    public SSHClient create() {
	        return createNewSSHClient_0(true);
	    }

		@Override
		public void destroyObject(PooledObject<SSHClient> p) throws Exception {
			p.getObject().close();
		}

		@Override
		public boolean validateObject(PooledObject<SSHClient> p) {
			try (Session session = p.getObject().startSession();) {
				session.exec("echo hello");
				return true;
			} catch (TransportException | ConnectionException e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		public void activateObject(PooledObject<SSHClient> p) throws Exception {
			// TODO Auto-generated method stub
			super.activateObject(p);
		}

		private SSHClient createNewSSHClient_0(boolean bRetry) {
			long timestamp_ms = 0;
			if (lg.isTraceEnabled()) {
				timestamp_ms = System.currentTimeMillis();
				lg.error("creating new ssh client");
			}
	        DefaultConfig defaultConfig = new DefaultConfig();
	        defaultConfig.setKeepAliveProvider(KeepAliveProvider.KEEP_ALIVE);
	        final SSHClient ssh = new SSHClient(defaultConfig);
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
					if (ssh != null) {
						ssh.close();
					}
				}catch (IOException e1) {
					lg.error("failed to create new ssh client", e1);
				}
				if (bRetry) {
					return createNewSSHClient_0(false);
				}else {
					throw new RuntimeException("failed to create SSH connection: "+e.getMessage(), e);
				}
			}
		}
		
	    /**
	     * Use the default PooledObject implementation.
	     */
	    @Override
	    public PooledObject<SSHClient> wrap(SSHClient sshClient) {
	        return new DefaultPooledObject<SSHClient>(sshClient);
	    }

	    /**
	     * When an object is returned to the pool, clear the buffer.
	     */
	    @Override
	    public void passivateObject(PooledObject<SSHClient> pooledObject) {
	        // pooledObject.getObject().close();
	    		//pooledObject.getObject().
	    }

	    // for all other methods, the no-op implementation
	    // in BasePooledObjectFactory will suffice
	}
	
	public CommandServiceSsh(String remoteHostName, String username, File keyFile) throws IOException{
		super();
		this.remoteHostName = remoteHostName;
		this.username = username;
		this.keyFile = keyFile;
	}
		
	@Override
	public CommandOutput command(String[] commandStrings, int[] allowableReturnCodes) throws ExecutableException {
		if (allowableReturnCodes == null){
			throw new IllegalArgumentException("allowableReturnCodes must not be null");
		}
		long timeMS = System.currentTimeMillis();
		SSHClient ssh = null;
		try {
			ssh = pool.borrowObject();
			try (Session session = ssh.startSession();)
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
		} catch (ExecutableException e) {
			throw e;
		} catch (Exception e) {
			lg.error("failed to borrow ssh connection from pool", e);
			throw new ExecutableException(e.getMessage());
		} finally {
			if (ssh != null) {
				try {
					pool.returnObject(ssh);
				} catch (Exception e) {
					lg.error("failed to return ssh connection to pool", e);
					e.printStackTrace();
				}
			}
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
		SSHClient ssh = null;
		try {
			ssh = pool.borrowObject();
			try (SFTPClient sftpClient = ssh.newSFTPClient();)
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
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			lg.error("failed to borrow ssh connection from pool", e);
			throw new IOException(e.getMessage());
		} finally {
			if (ssh != null) {
				try {
					pool.returnObject(ssh);
				} catch (Exception e) {
					lg.error("failed to return ssh connection to pool", e);
					e.printStackTrace();
				}
			}
		}
		
	}

	public void deleteFile(String remoteFilePath) throws IOException {
		if (lg.isTraceEnabled()) {
			lg.trace("deleting remote file "+remoteFilePath);
		}
		long timestamp_ms = System.currentTimeMillis();
		SSHClient ssh = null;
		try {
			ssh = pool.borrowObject();
			try (SFTPClient sftpClient = ssh.newSFTPClient();)
			{
				sftpClient.rm(remoteFilePath);
			}
			if (lg.isTraceEnabled()) {
				long diff_ms = System.currentTimeMillis() - timestamp_ms;
				lg.trace("deleted remote file "+remoteFilePath+": elapsedTime = "+diff_ms+" ms");
			}
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			lg.error("failed to borrow ssh connection from pool", e);
			throw new IOException(e.getMessage());
		} finally {
			if (ssh != null) {
				try {
					pool.returnObject(ssh);
				} catch (Exception e) {
					lg.error("failed to return ssh connection to pool", e);
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void close() {
		pool.close();
	}
	
}
