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
import java.util.Calendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;

import org.junit.Test;
import org.vcell.util.AuthenticationException;
import org.vcell.util.document.KeyValue;

import cbit.vcell.util.AmplistorUtils.AmplistorCredential;

public class AmplistorUtilsTest {
	
	private static final int NUMFILES = 5;
	public static final String AMPLI_REST_TEST_USER_KEY = 		"AMPLI_REST_TEST_USER_KEY";//e.g. eclipse debug config. -DAMPLI_REST_TEST_USER_KEY=camadmin
	public static final String AMPLI_REST_TEST_PASSWORD_KEY = 	"AMPLI_REST_TEST_PASSWORD_KEY";//e.g. eclipse debug config. -DAMPLI_REST_TEST_PASSWORD_KEY=thePassword
	
	public static void main(String[] args) throws Exception{

		new AmplistorUtilsTest().test();
		System.out.println("\nAll Tests Passed");
	}
	
	public static AmplistorCredential getAmplistorCredential(){
		Scanner scanner = null;
		String password = null;
		try{
			if(System.getProperty(AMPLI_REST_TEST_PASSWORD_KEY) == null){
				scanner = new Scanner(System.in);
				System.out.println("Enter 'camadmin' password for Amplistor (then press enter):");
				password = scanner.nextLine();
			}else{
				password = System.getProperty(AMPLI_REST_TEST_PASSWORD_KEY);
			}
		}finally{
			if(scanner != null){scanner.close();}
		}
		return new AmplistorCredential(System.getProperty(AMPLI_REST_TEST_USER_KEY,"camadmin"), password);

	}
	@Test
	public void test() throws Exception{
		
		//
		//Create amplistor full access credential
		//
		AmplistorCredential amplistorCredential = getAmplistorCredential();
		//must set this property in eclipse debug configuration
		if(amplistorCredential.userName == null || amplistorCredential.password == null){
			throw new Exception("Amplistor full access credential required for test");
		}
		
		//
		//Define Amplistor test dir
		//
		final String AMPLI_REST_TEST_DIR = "Ampli_REST_Test_Dir";
		String dirNameURL = AmplistorUtils.DEFAULT_AMPLI_SERVICE_VCELL_URL+AMPLI_REST_TEST_DIR;

		//
		//Define Local test dir and file
		//
		Random rand = new Random();
		byte[] rndBytes = new byte[10000];
		rand.nextBytes(rndBytes);

		File tmpDir = Files.createTempDirectory("rnd_").toFile();
		
		File[] tmpFiles = new File[NUMFILES];
		final String SIMID_PREFIX = "SimID_";
		for (int i = 0; i < NUMFILES; i++) {
			tmpFiles[i] = File.createTempFile(SIMID_PREFIX+i+"_0_", ".rndbin",tmpDir);
			FileOutputStream fos = new FileOutputStream(tmpFiles[i]);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(rndBytes);
			bos.close();
		}

		//
		//Clean Amplistor before we begin test
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
		
		//
		//Upload rnd file
		//
		Hashtable<File, Exception> failures = AmplistorUtils.uploadFilesOperation(tmpDir,new URL(dirNameURL),amplistorCredential);
		if(failures != null){
			throw new Exception("Upload failed "+failures.values().iterator().next().getMessage());
		}
		
		//
		//List amplistor test dir we created and populated
		//
		ArrayList<String> fileNames = AmplistorUtils.listDir(dirNameURL, null,amplistorCredential);
		if(fileNames.size() != NUMFILES){
			throw new Exception("Expected "+NUMFILES+" files but got "+fileNames.size());
		}
		for(String fileName:fileNames){
			System.out.println("listing:"+fileName);
		}

		String file0url = dirNameURL+"/"+fileNames.get(0);
		
		//
		//check bFileExists returns true
		//
		if(!AmplistorUtils.bFileExists(new URL(file0url), amplistorCredential)){
			throw new Exception("Expected "+file0url+" 'bFileExist' to be true");
		}
		
		//
		//check filter returns correct results
		//
		AmplistorUtils.AmplistorFileNameMatcher onlyTheseMatchingFiles = new AmplistorUtils.AmplistorFileNameMatcher() {
			@Override
			public boolean accept(String fileName) {
				return fileName.equals(tmpFiles[0].getName());
			}
		};
		ArrayList<String> filteredNames = AmplistorUtils.listDir(dirNameURL, onlyTheseMatchingFiles, amplistorCredential);
		if(filteredNames.size() != 1 || !filteredNames.get(0).equals(tmpFiles[0].getName())){
			throw new Exception("Filter test failed, expecting 1=="+filteredNames.size()+" and "+tmpFiles[0].getName()+"=="+(filteredNames.size()==0?"null":filteredNames.get(0)));
		}
		
		//
		//set metadata on first file (this is done during upload using lastmodified of file but do it again to demonstrate)
		//
		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, 2, 3, 5, 15, 30);
		calendar.set(Calendar.MILLISECOND,0);
		System.out.println("Set Custom Modification time----- "+calendar.getTime());
//		AmplistorUtils.setFileMetaData(dirNameURL+"/"+tmpFiles[0].getName(), amplistorCredential, SimulationData.AmplistorHelper.CUSTOM_FILE_MODIFICATION_DATE, tmpFiles[0].lastModified()/1000+".0");
		AmplistorUtils.setFileMetaData(file0url, amplistorCredential, AmplistorUtils.CUSTOM_FILE_MODIFICATION_DATE, calendar.getTime().getTime()/1000+".0");

		//
		//Print the http header info for one of the uploaded files
		//
		AmplistorUtils.AmpliCustomHeaderHelper ampliCustomHeaderHelper = AmplistorUtils.printHeaderFields(file0url,amplistorCredential);
		if(ampliCustomHeaderHelper.customModification == null || !ampliCustomHeaderHelper.customModification.equals(calendar.getTime())){
			throw new Exception("Queried custom modification date "+ampliCustomHeaderHelper.customModification+" does not match set custom modification date "+calendar.getTime());
		}		
		
		//
		//Download 1 of the files
		//
		byte[] downloadBytes = AmplistorUtils.getObjectData(file0url, amplistorCredential);
		
		//
		//Check local and remote files are same
		//
		if(downloadBytes.length != tmpFiles[0].length() || rndBytes.length != downloadBytes.length){
			throw new Exception("round trip file sizes are different local = "+tmpFiles[0].length()+" remote = "+downloadBytes.length+" memory = "+rndBytes.length);
		}
		FileInputStream fis = new FileInputStream(tmpFiles[0]);
		BufferedInputStream bis = new BufferedInputStream(fis);
		DataInputStream dis = new DataInputStream(bis);
		byte[] localBytes = new byte[rndBytes.length];
		dis.readFully(localBytes);
		dis.close();
		if(!Arrays.equals(localBytes, downloadBytes) || !Arrays.equals(rndBytes, downloadBytes)){
			throw new Exception("download bytes not match local bytes after round trip");
		}
		
		//
		//Check special SimID parsing for delete
		//
		HashSet<KeyValue> doNotDeleteTheseSimKeys = new HashSet<KeyValue>();
		doNotDeleteTheseSimKeys.add(new KeyValue("0"));
		doNotDeleteTheseSimKeys.add(new KeyValue("1"));
		long numDeleted = AmplistorUtils.deleteSimFilesNotInHash(dirNameURL, doNotDeleteTheseSimKeys, false,amplistorCredential).size();
		if(numDeleted != (NUMFILES-doNotDeleteTheseSimKeys.size())){
			throw new Exception("Expected to delete "+(NUMFILES-doNotDeleteTheseSimKeys.size())+" but deleted "+numDeleted);
		}
		
		//
		//remove one of the remaining files on amplistor
		//
		AmplistorUtils.deleteFilesOperation(new String[] {tmpFiles[0].getName()}, dirNameURL, amplistorCredential);
		
		
		//
		//check bFileExists returns false
		//
		if(AmplistorUtils.bFileExists(new URL(dirNameURL+"/"+tmpFiles[0].getName()), amplistorCredential)){
			throw new Exception("Expected "+dirNameURL+"/"+tmpFiles[0].getName()+" 'bFileExist' to be false");
		}

		//
		//try to remove bogus filename (should fail)
		//
		try{
			AmplistorUtils.deleteFilesOperation(new String[] {"blahblah"}, dirNameURL, amplistorCredential);
			throw new Exception("We shouldn't have gotten here, Expecting FileNotFoundException");
		}catch(FileNotFoundException e){
			//ignore, this should happen
		}
		
		//
		//there should be 1 file left
		//
		int numleft = AmplistorUtils.listDir(dirNameURL, null,amplistorCredential).size();
		if(numleft != 1){
			throw new Exception("Expecting 1 file in list but got "+numleft);
		}
		
		//
		//Remove test from amplistor
		//
		deleteDirAndAllFiles(dirNameURL, amplistorCredential);//do not ignore FileNotFound
		
		
		//
		//vcell_logs Amplistor test
		//
		
		//
		//try to upload to vcell_logs without credentials
		//
		AmplistorUtils.uploadFile(new URL(AmplistorUtils.DEFAULT_AMPLI_VCELL_LOGS_URL), tmpFiles[0], null);
		
		//
		//try to get dir list from vcell_logs without credentials (should fail)
		//
		try{
			AmplistorUtils.listDir(AmplistorUtils.DEFAULT_AMPLI_VCELL_LOGS_URL, null, null);
			throw new Exception("Souldn't have gotten here, Expecting failure to get vcell_logs with no authentication");
		}catch(AuthenticationException e){
			//ignore, this should happen
		}
				
		//
		//try to get dir list from vcell_logs with credentials (should succeed)
		//
		ArrayList<String> vcell_logs_dirlist = AmplistorUtils.listDir(AmplistorUtils.DEFAULT_AMPLI_VCELL_LOGS_URL, null, amplistorCredential);
		System.out.println("Found "+vcell_logs_dirlist.size()+" files in vcell_logs directory");
		
		//
		//try to delete file from vcell_logs without credentials (should fail)
		//
		try{
			AmplistorUtils.deleteFilesOperation(new String[] {tmpFiles[0].getName()}, AmplistorUtils.DEFAULT_AMPLI_VCELL_LOGS_URL, null);
			throw new Exception("Souldn't have gotten here, Expecting failure to get vcell_logs with no authentication");
		}catch(AuthenticationException e){
			//ignore, this should happen
		}

		//
		//try to delete file from vcell_logs with credentials (should succeed)
		//
		AmplistorUtils.deleteFilesOperation(new String[] {tmpFiles[0].getName()}, AmplistorUtils.DEFAULT_AMPLI_VCELL_LOGS_URL, amplistorCredential);

		
		//
		//Clean up local test files
		//
		try{
			for (int i = 0; i < NUMFILES; i++) {tmpFiles[i].delete();}
			tmpDir.delete();
		}catch(Exception e){
			//ignore, not part of test
		}
	}
	
	private static void deleteDirAndAllFiles(String dirNameURL,AmplistorCredential amplistorCredential) throws Exception{
		AmplistorUtils.deleteAllFiles(dirNameURL,amplistorCredential);
		AmplistorUtils.deleteDir(dirNameURL, amplistorCredential);
	}
}

