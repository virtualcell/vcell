package cbit.vcell.message.server.cmd;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.vcell.util.exe.ExecutableException;

import cbit.vcell.mongodb.VCMongoMessage;
import net.schmizz.keepalive.KeepAliveProvider;
import net.schmizz.sshj.DefaultConfig;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.FileKeyProvider;
import net.schmizz.sshj.userauth.keyprovider.OpenSSHKeyFile;


public class CommandServiceSsh extends CommandService {
	private String remoteHostName = null;
	private String username = null;
	private File keyFile = null;
	private final ObjectPool<SshConnection> pool;
	
	private static class SshConnection {
		public final SSHClient client;
		private boolean bFailed=false;
		
		private SshConnection(SSHClient client) {
			super();
			this.client = client;
		}
		
		public void close() {
			try {
				client.disconnect();
			} catch (Exception e) {
				CommandServiceSsh.lg.error("failed to release ssh connection and session: "+e.getMessage(), e);
			}
		}

		public void setFailed() {
			this.bFailed = true;
		}
		
		public boolean isFailed() {
			return bFailed;
		}
	}
			
	private class SshConnectionFactory
	    extends BasePooledObjectFactory<SshConnection> {

	    @Override
	    public SshConnection create() {
	        return createNewConnection();
	    }

		@Override
		public void destroyObject(PooledObject<SshConnection> p) throws Exception {
			p.getObject().close();
		}

		@Override
		public boolean validateObject(PooledObject<SshConnection> p) {
			try {
				if (p.getObject().isFailed()) {
					return false;
				}
				Command cmd = null;
				try (Session session = p.getObject().client.startSession();) {
					if (lg.isTraceEnabled()) {
						lg.trace("ssh connection pool testing connection with 'echo hello' command");
					}
					cmd = session.exec("echo hello");
				}
				if (cmd == null) {
					if (lg.isWarnEnabled()) {
						lg.warn("pool couldn't validate ssh connection: cmd is null");
					}
					return false;
				}
				if (cmd.getExitStatus() != null && cmd.getExitStatus().intValue() != 0) {
					if (lg.isWarnEnabled()) {
						lg.warn("pool couldn't validate ssh connection: expecting 0 exit status, status=("+cmd.getExitStatus()+")");
					}
					return false;
				}
				if (lg.isTraceEnabled()) {
					lg.trace("pool validated ssh connection");
				}
				return true;
			} catch (TransportException | ConnectionException e) {
				e.printStackTrace();
				return false;
			}
		}

		private SshConnection createNewConnection() {
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
				return new SshConnection(ssh);
			}catch (Exception e) {
				lg.error("failed to create new SSH client: "+e.getMessage(), e);
				try {
					if (ssh != null) {
						ssh.close();
					}
				}catch (IOException e1) {
					lg.error("failed to create new ssh client", e1);
				}
				throw new RuntimeException("failed to create SSH connection: "+e.getMessage(), e);
			}
		}
		
	    /**
	     * Use the default PooledObject implementation.
	     */
	    @Override
	    public PooledObject<SshConnection> wrap(SshConnection sshConnection) {
	        return new DefaultPooledObject<SshConnection>(sshConnection);
	    }

	    /**
	     * When an object is returned to the pool, clear the buffer.
	     */
	    @Override
	    public void passivateObject(PooledObject<SshConnection> pooledObject) {
//	    		IOUtils.readFully(pooledObject.getObject().session.getInputStream());
	    }

	    // for all other methods, the no-op implementation
	    // in BasePooledObjectFactory will suffice
	}
	
	public CommandServiceSsh(String remoteHostName, String username, File keyFile) throws IOException{
		super();
		this.remoteHostName = remoteHostName;
		this.username = username;
		this.keyFile = keyFile;
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setTestOnBorrow(true);
		this.pool = new GenericObjectPool<SshConnection>(new SshConnectionFactory(), config);
	}
		
	@Override
	public CommandOutput command(String[] commandStrings, int[] allowableReturnCodes) throws ExecutableException {
		if (allowableReturnCodes == null){
			throw new IllegalArgumentException("allowableReturnCodes must not be null");
		}
		long timeMS = System.currentTimeMillis();
		SshConnection sshConnection = null;
		try {
			sshConnection = pool.borrowObject();
			try (Session session = sshConnection.client.startSession(); ){
				String cmd = CommandOutput.concatCommandStrings(commandStrings);
				Session.Command command = session.exec(cmd);
				command.join(1,TimeUnit.MINUTES); //wait up to a minute for return -- will return sooner if command completes
				String standardOutput = IOUtils.readFully(command.getInputStream()).toString();
				String standardError = IOUtils.readFully(command.getErrorStream()).toString();
				command.close();
				Integer exitStatus = command.getExitStatus();
				long elapsedTimeMS = System.currentTimeMillis() - timeMS;
				CommandOutput commandOutput = new CommandOutput(commandStrings, standardOutput, standardError, exitStatus, elapsedTimeMS);
				
				if (VCMongoMessage.enabled) {
					VCMongoMessage.sendCommandServiceCall(commandOutput);
				}
				
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
				throw new ExecutableException(e.getMessage(),e);
			}
		} catch (ExecutableException e) {
			throw e;
		} catch (Exception e) {
			lg.error("failed to borrow ssh connection from pool", e);
			if (sshConnection!=null) {
				sshConnection.setFailed();
			}
			throw new ExecutableException(e.getMessage(),e);
		} finally {
			if (sshConnection != null) {
				try {
					pool.returnObject(sshConnection);
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
	
//	@Override
//	public void pushFile(File tempFile, String remotePath) throws IOException {
//		if (lg.isTraceEnabled()) {
//			lg.trace("pushing file "+tempFile+" (exists="+tempFile.exists()+") ==> "+remotePath);
//		}
//		long timestamp_ms = System.currentTimeMillis();
//		SSHClient ssh = null;
//		try {
//			ssh = pool.borrowObject();
//			try (SFTPClient sftpClient = ssh.newSFTPClient();)
//			{
//				sftpClient.put(tempFile.getPath(), remotePath);
//			} catch (Exception e) {
//				lg.error("failed to push file", e);
//				throw new IOException("failed to push file: "+e.getMessage(),e);
//			}
//			if (lg.isTraceEnabled()) {
//				long diff_ms = System.currentTimeMillis() - timestamp_ms;
//				lg.trace("pushed file to "+remotePath+": elapsedTime = "+diff_ms+" ms");
//			}
//		} catch (IOException e) {
//			throw e;
//		} catch (Exception e) {
//			lg.error("failed to borrow ssh connection from pool", e);
//			throw new IOException(e.getMessage());
//		} finally {
//			if (ssh != null) {
//				try {
//					pool.returnObject(ssh);
//				} catch (Exception e) {
//					lg.error("failed to return ssh connection to pool", e);
//					e.printStackTrace();
//				}
//			}
//		}
//		
//	}
//
//	public void deleteFile(String remoteFilePath) throws IOException {
//		if (lg.isTraceEnabled()) {
//			lg.trace("deleting remote file "+remoteFilePath);
//		}
//		long timestamp_ms = System.currentTimeMillis();
//		SSHClient ssh = null;
//		try {
//			ssh = pool.borrowObject();
//			try (SFTPClient sftpClient = ssh.newSFTPClient();)
//			{
//				sftpClient.rm(remoteFilePath);
//			}
//			if (lg.isTraceEnabled()) {
//				long diff_ms = System.currentTimeMillis() - timestamp_ms;
//				lg.trace("deleted remote file "+remoteFilePath+": elapsedTime = "+diff_ms+" ms");
//			}
//		} catch (IOException e) {
//			throw e;
//		} catch (Exception e) {
//			lg.error("failed to borrow ssh connection from pool", e);
//			throw new IOException(e.getMessage());
//		} finally {
//			if (ssh != null) {
//				try {
//					pool.returnObject(ssh);
//				} catch (Exception e) {
//					lg.error("failed to return ssh connection to pool", e);
//					e.printStackTrace();
//				}
//			}
//		}
//	}

	@Override
	public void close() {
		pool.close();
	}
	
}
