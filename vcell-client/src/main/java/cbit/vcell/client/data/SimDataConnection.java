package cbit.vcell.client.data;
import java.io.File;
import java.util.List;

import org.vcell.util.document.KeyValue;

import cbit.vcell.simdata.SimulationData;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.RemoteResourceFilter;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;


public class SimDataConnection {

	private static final String dataServer = "sigcluster2.cam.uchc.edu";
	private static final String primaryPath="/share/apps/vcell/users/";
	private static final String secondaryPath="/share/apps/vcell2/users/";
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		if(args.length == 5){
			String vcellClusterPassword = args[0];
			String userName = args[1];
			final KeyValue simIDKey = new KeyValue(args[2]);
			final int jobIndex = Integer.parseInt(args[3]);
			final boolean isOld = Boolean.parseBoolean(args[4]);
			downloadSimData(null, vcellClusterPassword, userName, simIDKey, jobIndex, isOld);
		}else{
			System.out.println("Usage args: toBeEncrypted masterPassword");
			System.out.println("Usage: vcellClusterPassword vcellUser simID jobIndex isOld");
		}
	}

	public static File downloadSimData(File tempSimDir,String vcellClusterPassword,String userName, KeyValue simIDKey,Integer jobIndex,boolean isOld) throws Exception{
		String simDataLogFileName = SimulationData.createCanonicalSimLogFileName(simIDKey, jobIndex, isOld);
    	SSHClient ssh = null;
    	SFTPClient sftpClient = null;
    	if(tempSimDir == null){
        	tempSimDir = File.createTempFile("SimID_"+simIDKey+"_"+jobIndex+"_", ".dir");
        	if(!tempSimDir.delete()){
        		throw new Exception("Couldn't make local dir "+tempSimDir);
        	}
        	if(!tempSimDir.mkdir()){
        		throw new Exception("Couldn't make local dir "+tempSimDir);
        	}
    	}
    	if(!tempSimDir.exists()){
    		throw new Exception("Directory not exist "+tempSimDir);
    	}
    	File localSimDir = new File(tempSimDir,userName);
    	if(!localSimDir.exists()){
	    	if(!localSimDir.mkdir()){
	    		throw new Exception("Couldn't make local dir "+localSimDir);
	    	}
    	}
    	System.out.println("Local download dir '"+localSimDir+"'");
    	
    	final String compareStr = "SimID_"+simIDKey.toString()+(isOld?".":"_"+jobIndex+"_");
    	System.out.println(compareStr);
    	RemoteResourceFilter remoteResourceFilter = new RemoteResourceFilter() {
			@Override
			public boolean accept(RemoteResourceInfo arg0) {
				return arg0.getName().startsWith(compareStr);
			}
		};
    	try{
    		System.out.println("Creating ssh connection...");
    		ssh = createSSHClient(dataServer,"vcell",vcellClusterPassword);
    		System.out.println("Creating ftpClient...");
    		sftpClient = ssh.newSFTPClient();
    		sftpClient.getFileTransfer().setPreserveAttributes(false);
    		String primaryFullPath = primaryPath+userName+"/"+simDataLogFileName;
    		System.out.println("Seeking primary location..."+primaryFullPath);
			FileAttributes fileAttributes = sftpClient.statExistence(primaryFullPath);
			boolean bExist = (fileAttributes==null?false:true);
			if(bExist){
				System.out.println("Downloading primary location...");
				List<RemoteResourceInfo> remoteResourceInfos = sftpClient.ls(primaryPath+userName, remoteResourceFilter);
				download(sftpClient, localSimDir, remoteResourceInfos);
//				printRemoteResourceInfo(remoteResourceInfos);
//    			sftpClient.get(primaryFullPath, logF.getAbsolutePath());
//    			System.out.println("Downloaded data from "+dataServer+":"+primaryFullPath+" to "+logF);
			}else{
	    		String secondaryFullPath = secondaryPath+userName+"/"+simDataLogFileName;
				System.out.println("Seeking secondary location..."+secondaryFullPath);
	    		fileAttributes = sftpClient.statExistence(secondaryFullPath);
	    		bExist = (fileAttributes==null?false:true);
	    		if(bExist){
	    			System.out.println("Downloading secondary location...");
					List<RemoteResourceInfo> remoteResourceInfos = sftpClient.ls(secondaryPath+userName, remoteResourceFilter);
					download(sftpClient, localSimDir, remoteResourceInfos);
//					printRemoteResourceInfo(remoteResourceInfos);
//	    			sftpClient.get(secondaryFullPath, logF.getAbsolutePath());
//	    			System.out.println("Downloaded data from "+dataServer+":"+secondaryFullPath+" to "+logF);
	    		}else{
	    			System.out.println("Data Not Found for simid "+simIDKey);
	    		}
			}
			
			return localSimDir;
    	}finally{
    		if(sftpClient != null){try{sftpClient.close();}catch(Exception e){e.printStackTrace();}}
    		if(ssh != null){try{ssh.disconnect();}catch(Exception e){e.printStackTrace();}}
    	}
		
		
	}
	private static void printRemoteResourceInfo(List<RemoteResourceInfo> remoteResourceInfos){
		for(RemoteResourceInfo remoteResourceInfo:remoteResourceInfos){
			System.out.println(remoteResourceInfo.getName());
		}
	}
	private static void download(SFTPClient sftpClient,File localSimDir,List<RemoteResourceInfo> remoteResourceInfos) throws Exception{
		for(RemoteResourceInfo remoteResourceInfo:remoteResourceInfos){
			System.out.println(remoteResourceInfo.getName());
			sftpClient.get(remoteResourceInfo.getPath(), localSimDir.getAbsolutePath());
		}		
	}
    private static SSHClient createSSHClient(String host,String user,String password) throws Exception{
    	SSHClient sshClient = null;
    	try {
			sshClient = new SSHClient();
			sshClient.addHostKeyVerifier(new PromiscuousVerifier()); // !!! only use the PromiscuousVerifier if the VMs you are connecting to are known and trusted !!!
			sshClient.connect(host);
			sshClient.authPassword(user,password);  
			return sshClient;
		} catch (Exception e) {
			if(sshClient != null){try{sshClient.disconnect();}catch(Exception e2){e.printStackTrace();}}
			throw e;
		}
    }
    
//	private static final byte[] SALT = {
//        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12, 
//        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12, 
//    };
//	public static String encrypt(String encryptThis,String masterPassword) throws Exception { 
//        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES"); 
//        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(masterPassword.toCharArray())); 
//        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES"); 
//        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20)); 
//        return DatatypeConverter.printBase64Binary(pbeCipher.doFinal(encryptThis.getBytes())); 
//    }
//	public static String decrypt(String decryptThis,String masterPassword) throws Exception{ 
//        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES"); 
//        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(masterPassword.toCharArray())); 
//        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES"); 
//        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20)); 
//        return new String(pbeCipher.doFinal(DatatypeConverter.parseBase64Binary(decryptThis))); 
//    }

}
