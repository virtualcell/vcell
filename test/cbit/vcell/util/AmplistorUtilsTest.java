package cbit.vcell.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.xml.bind.DatatypeConverter;

import org.junit.Test;

import cbit.vcell.simdata.SimulationData;
import cbit.vcell.util.AmplistorUtils.AmplistorCredential;

public class AmplistorUtilsTest {
		
	public static void main(String[] args) throws Exception{
		test();
	}
	
	@Test
	public static void test() throws Exception{
		
		//
		//Create amplistor access credential
		//
		//must set this property in eclipse debug configuration
		final String AMPLI_REST_TEST_PASSWORD_PROP = "AMPLI_REST_TEST_PASSWORD_PROP";//use c......h	
		AmplistorCredential amplistorCredential =
			new AmplistorCredential("camadmin",decrypt("kXRnFZwpP1YwpQRDzdx/zw==",System.getProperty(AMPLI_REST_TEST_PASSWORD_PROP)));
		
		//
		//Define Amplistor test dir
		//
		final String AMPLI_REST_TEST_DIR = "Ampli_REST_Test_Dir";
		String dirNameURL = AmplistorUtils.get_Service_VCell_urlString(AMPLI_REST_TEST_DIR);

		//
		//Define Local test dir and file
		//
		File tmpDir = Files.createTempDirectory("rnd_").toFile();
		File tmpFile = File.createTempFile("rnd_", ".rndbin",tmpDir);
		
		Random rand = new Random();
		byte[] rndBytes = new byte[10000];
		rand.nextBytes(rndBytes);

		FileOutputStream fos = new FileOutputStream(tmpFile);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		bos.write(rndBytes);
		bos.close();
		
		//
		//Clean before we begin test
		//
		try{
			deleteDirAndAllFiles(dirNameURL, amplistorCredential);
		}catch(FileNotFoundException e){
			//this is OK, ignore
		}
		
		//
		//Create test dir on amplistor
		//
		AmplistorUtils.createDir(dirNameURL, amplistorCredential);
//		AmplistorUtils.ampliDirOperation(dirNameURL, amplistorCredential,AmplistorUtils.AMPLI_OP_METHOD.PUT);
		
		//
		//Upload rnd file
		//
		Hashtable<File, Exception> failures = AmplistorUtils.uploadFilesOperation(tmpDir,new URL(dirNameURL),amplistorCredential);
		if(failures != null){
			throw new Exception("Upload failed "+failures.values().iterator().next().getMessage());
		}
		
		//
		//set metadata (this is done during upload automatically but do it again to demonstrate)
		//
		AmplistorUtils.setFileMetaData(dirNameURL+"/"+tmpFile.getName(), amplistorCredential, SimulationData.AmplistorHelper.CUSTOM_FILE_MODIFICATION_DATE, tmpFile.lastModified()/1000+".0");

		//
		//List dir
		//
		ArrayList<String> fileNames = AmplistorUtils.listDir(dirNameURL, amplistorCredential);
		for(String fileName:fileNames){
			System.out.println("listing:"+fileName);
		}
		
		//
		//Download file
		//
		byte[] downloadBytes = AmplistorUtils.getObjectData(dirNameURL+"/"+tmpFile.getName(), amplistorCredential);
		
		//
		//Check files are same
		//
		if(downloadBytes.length != tmpFile.length() || rndBytes.length != downloadBytes.length){
			throw new Exception("round trip file sizes are different local = "+tmpFile.length()+" remote = "+downloadBytes.length+" memory = "+rndBytes.length);
		}
		FileInputStream fis = new FileInputStream(tmpFile);
		BufferedInputStream bis = new BufferedInputStream(fis);
		DataInputStream dis = new DataInputStream(bis);
		byte[] localBytes = new byte[rndBytes.length];
		dis.readFully(localBytes);
		dis.close();
		if(!Arrays.equals(localBytes, downloadBytes) || !Arrays.equals(rndBytes, downloadBytes)){
			throw new Exception("download bytes not match local bytes after round trip");
		}
		
		//
		//Remove test from amplistor
		//
		deleteDirAndAllFiles(dirNameURL, amplistorCredential);//do not ignore FileNotFound
		
		//
		//Clean up local bytes
		//
		try{
			tmpFile.delete();
			tmpDir.delete();
		}catch(Exception e){
			//ignore, not part of test
		}
	}
	
	private static void deleteDirAndAllFiles(String dirNameURL,AmplistorCredential amplistorCredential) throws Exception{
		AmplistorUtils.deleteAllFiles(dirNameURL,amplistorCredential);
		AmplistorUtils.deleteDir(dirNameURL, amplistorCredential);
//		AmplistorUtils.ampliDirOperation(dirNameURL, amplistorCredential,AmplistorUtils.AMPLI_OP_METHOD.DELETE);
	}
	
	
	private static final byte[] SALT = { 
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12, 
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12, 
    };
	public static String decrypt(String decryptThis,String masterPassword) throws Exception{ 
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES"); 
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(masterPassword.toCharArray())); 
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES"); 
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20)); 
        return new String(pbeCipher.doFinal(DatatypeConverter.parseBase64Binary(decryptThis))); 
    }

}

