/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modeldb;
import java.io.File;
import java.io.FileFilter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.KeyFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;

import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.util.AmplistorUtils;


/**
 * Insert the type's description here.
 * Creation date: (2/2/01 2:57:33 PM)
 * @author: Jim Schaff
 */
public class ResultSetCrawler {
	
	public static void main(String[] args) {
		ConnectionFactory conFactory = null;
		try {		
			boolean SCAN_ONLY = true;
			String singleUsername = null;
			String startingUsername = null;
			String outputDirName = ".";

			String ampliCredName = null;
			String ampliCredPassword = null;
			int count = 0;

			while (count < args.length) {
				if (args[count].equals("-h")) {
					printUsage();
					System.exit(0);
				} else if (args[count].equals("-u")) {
					count ++;
					singleUsername = args[count];
				} else if (args[count].equals("-c")) {
					count ++;
					startingUsername = args[count];
				} else if (args[count].equals("-o")) {
					count ++;
					outputDirName = args[count];
				} else if (args[count].equals("-d")) {
					SCAN_ONLY = false;
				} else if (args[count].equals("-s")) {
					SCAN_ONLY = true;
				} else if (args[count].equals("-y")) {
					count ++;
					ampliCredName = args[count];
				} else if (args[count].equals("-z")) {
					count ++;
					ampliCredPassword = args[count];
				} else {
					System.out.println("Wrong arguments, see usage below.");
					printUsage();
					System.exit(1);
				}
				count ++;
			}
				
			File outputDir = null;
			if (outputDirName == null) {
				outputDir = new File(".");
			} else {
				outputDir = new File(outputDirName);		
				if (!outputDir.exists()) {
					throw new RuntimeException("Outuput directory doesn't exist!");
				}
			}
			
			PropertyLoader.loadProperties();

			File primaryDataRootDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirProperty));
			File secondaryDataRootDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.secondarySimDataDirProperty));
			if (primaryDataRootDir.equals(secondaryDataRootDir)){
				secondaryDataRootDir = null;
			}
			
			// initialize database
			SessionLog log = new cbit.vcell.resource.StdoutSessionLog("ResultSetCrawler");		
			conFactory = DatabaseService.getInstance().createConnectionFactory(log);
			KeyFactory keyFactory = conFactory.getKeyFactory();
			AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(conFactory,log);
			DatabaseServerImpl dbServerImpl = new DatabaseServerImpl(conFactory,keyFactory,log);
			
			//
			// determine the list of users to scan
			//
			UserInfo[] allUserInfos = adminDbTopLevel.getUserInfos(true);
			HashMap<String,User> usersToScan = new HashMap<String, User>();
			for (UserInfo userInfo : allUserInfos) {
				if (singleUsername!=null){ // accept only the "singleUser"
					if (userInfo.userid.equals(singleUsername)){
						usersToScan.put(userInfo.userid, new User(userInfo.userid,userInfo.id));
						break;
					}
				}else if (startingUsername!=null){ // accept all users starting with the "startingUser"
					if (userInfo.userid.compareToIgnoreCase(startingUsername)>=0){
						usersToScan.put(userInfo.userid, new User(userInfo.userid,userInfo.id));
					}
				}else{ // all users
					usersToScan.put(userInfo.userid, new User(userInfo.userid,userInfo.id));
				}
			}
			
			//
			// get list of directories to scan (for selected users on both user data directories)
			//
			List<File> useDirectoriesToScan = getDirectoriesToScan(usersToScan, primaryDataRootDir, secondaryDataRootDir, log);
			
			for (File userDir : useDirectoriesToScan){
				try {
					log.print("----------------------------------------------------------");
					log.print("USER: " + userDir.getName());

					User user = usersToScan.get(userDir.getName());
					
					// find all the user simulations and external data sets (field data)
					SimulationInfo[] simulationInfos = dbServerImpl.getSimulationInfos(user, false);
					ExternalDataIdentifier[] extDataIDArr = adminDbTopLevel.getExternalDataIdentifiers(user,true);
					
					// scan this user directory
					scanUserDirectory(userDir, extDataIDArr, simulationInfos, outputDir, log, SCAN_ONLY,(ampliCredName==null || ampliCredPassword==null?null:new AmplistorUtils.AmplistorCredential(ampliCredName, ampliCredPassword)));
				} catch (Exception ex) {
					log.exception(ex);
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		} finally {
			try {
				if (conFactory != null) {
					conFactory.close();
				}
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
			System.exit(0);		
		}

	}


	private static void printUsage() {
		System.out.println("ResultSetCrawler [-h] [-u username] [-c username] [-o outputdir] [-d | -s] [-y ampliCredName] [-z ampliCredPassword]");
		System.out.println("-h : \n\thelp");
		System.out.println("-u username: \n\tscan a single user only");
		System.out.println("-c username: \n\tcontinue scanning from a user");
		System.out.println("-o outputdir : \n\tdirectory where scan results are stored (default is current directory)");
		System.out.println("-s : \n\tscan only (default)");
		System.out.println("-d : \n\tscan and delete files");
		System.out.println("-y ampliCredName: \n\tAmplistor Credential (username), must have delete permission on amplistor");
		System.out.println("-z ampliCredPassword: \n\tAmplistor Credential (password)");
	}



private static List<File> getDirectoriesToScan(HashMap<String,User> usersToScan, File primaryDataRootDir, File secondaryDataRootDir, SessionLog log) throws SQLException, DataAccessException, java.rmi.RemoteException {
		
	//
	// get list of all userDirs in both primary and secondary user data directories and sort them alphabetically (case insensitive)
	//
	ArrayList<File> allUserDirs = new ArrayList<File>();
	File primaryUserDirs[] = primaryDataRootDir.listFiles();
	allUserDirs.addAll(Arrays.asList(primaryUserDirs));
	if (secondaryDataRootDir!=null){
		File secondaryUserDirs[] = secondaryDataRootDir.listFiles();
		if(secondaryUserDirs != null && secondaryDataRootDir.length() > 0){
			allUserDirs.addAll(Arrays.asList(secondaryUserDirs));
		}
	}
	Comparator<File> caseInsensitiveFileComparator = new Comparator<File>() {
		public int compare(File o1, File o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	};
	Collections.sort(allUserDirs, caseInsensitiveFileComparator);
	
	ArrayList<File> dirsToScan = new ArrayList<File>();
	for (File userDir : allUserDirs){
		try {
			User user = usersToScan.get(userDir.getName());
			if (user == null){
				continue;
			}
			
			if (!userDir.exists() || !userDir.isDirectory()) {
				log.alert("user directory '" + userDir + "' doesn't exist or is not a directory");
				continue;
			}
			
			dirsToScan.add(userDir);
		} catch (Exception ex) {
			log.exception(ex);
		}
	}
	return dirsToScan;
}


/**
 * Insert the method's description here.
 * Creation date: (2/2/01 3:40:29 PM)
 */
private static void scanUserDirectory(File userDir, ExternalDataIdentifier[] extDataIDArr, SimulationInfo[] simulationInfos, File outputDir, final SessionLog log, final boolean bScanOnly,AmplistorUtils.AmplistorCredential amplistorCredential) throws Exception {
	File outputFile = null;
	java.io.PrintWriter writer = null;
	try {
		long timestamp = new Date().getTime();
		outputFile = new File(outputDir, "ResultSetCrawler_" + userDir.getName() + "_" + timestamp + ".txt");
		writer = new java.io.PrintWriter(new java.io.FileOutputStream(outputFile));
		final PrintWriter pw = writer;
		
		pw.println("scanning directory : "+userDir.getPath());
		log.print("scanning directory : "+userDir.getPath());
		
		//
		// gather list of keys that should be retained.
		//
		final HashSet<KeyValue> referencedKeys = new HashSet<KeyValue>();
		for (ExternalDataIdentifier extDataId : extDataIDArr) {
			referencedKeys.add(extDataId.getKey());
		}
		for (SimulationInfo simulationInfo : simulationInfos){
			referencedKeys.add(simulationInfo.getSimulationVersion().getVersionKey());
		}
		
		if(amplistorCredential != null){
			try{
				if(bScanOnly){
					ArrayList<String> shouldBeDeleted =
						AmplistorUtils.deleteSimFilesNotInHash(AmplistorUtils.DEFAULT_AMPLI_SERVICE_VCELL_URL+userDir.getName(), referencedKeys,bScanOnly,amplistorCredential);
					for(String fileName:shouldBeDeleted){
						pw.println("Should delete Amplistor "+fileName);
						log.print("Should delete Amplistor "+fileName);					
					}
				}else{
					ArrayList<String> wasDeleted =
						AmplistorUtils.deleteSimFilesNotInHash(AmplistorUtils.DEFAULT_AMPLI_SERVICE_VCELL_URL+userDir.getName(), referencedKeys,bScanOnly,amplistorCredential);
					for(String fileName:wasDeleted){
						pw.println("deleted Amplistor "+fileName);
						log.print("deleted Amplistor "+fileName);					
						
					}
				}
			}catch(Exception e){
				log.print("Amplistor delete failed url="+AmplistorUtils.DEFAULT_AMPLI_SERVICE_VCELL_URL+userDir.getName()+" : "+e.getMessage());
				pw.println("Amplistor delete failed url="+AmplistorUtils.DEFAULT_AMPLI_SERVICE_VCELL_URL+userDir.getName()+" : "+e.getMessage());
			}
		}

		final HashMap<KeyValue,Integer> deletedKeyMap = new HashMap<KeyValue,Integer>();
				
		//
		// visit each file and delete it on the fly (through a FileFilter)
		//
		FileFilter fileVisitor = new FileFilter() {

			@Override
			public boolean accept(File file) {
				String filename = file.getName();
	        	String[] parts = filename.split("\\_|\\.");
	        	if (parts.length > 0 && parts[0].equals("SimID")){
	        		if (parts.length>1){
	        			String simkeyString = parts[1];
	        			try {
	        				KeyValue simKey = new KeyValue(simkeyString);
	        				if (!referencedKeys.contains(simKey)){
	        					Integer count = deletedKeyMap.get(simKey);
	        					if (count==null){
	        						deletedKeyMap.put(simKey,1);
	        						count = 1;
	        					}else{
	        						deletedKeyMap.put(simKey,1+count.intValue());
	        					}
	        					if (bScanOnly){
	        						pw.println("Should delete file("+count+") with key " + simKey + " : " + file.getPath());
	        						log.print("Should delete file("+count+") with key " + simKey + " : " + file.getPath());
	        					}else{
	        						pw.println("deleted file("+simKey+":"+count+") " + file.getPath());
	        						log.print("deleted file("+simKey+":"+count+") " + file.getPath());
	        						file.delete();
	        					}
	        				}
	        			}catch (Exception e){
	        				log.print("failed to process file "+file.getPath()+": "+e.getMessage());
	        			}
	        		}
	        	}
				return false;
			}
			
		};
		
		//
		// visit all of the files and delete if bScanOnly=false
		//
		userDir.listFiles(fileVisitor);
		
		pw.println("done scanning directory : "+userDir.getPath());
		log.print("done scanning directory : "+userDir.getPath());
	} finally {
		if (writer != null) {
			writer.close();
		}
		log.print("User " + userDir.getName() + ", See " + outputFile.getAbsolutePath() + " for details");
	}
}


}
