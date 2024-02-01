/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.admin.cli.sim;
import java.io.File;
import java.io.FileFilter;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;

import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.simulation.SimulationInfo;
import cbit.vcell.util.AmplistorUtils;


/**
 * Insert the type's description here.
 * Creation date: (2/2/01 2:57:33 PM)
 * @author: Jim Schaff
 */
public class ResultSetCrawler {
	public static final Logger lg = LogManager.getLogger(ResultSetCrawler.class);

	private AdminDBTopLevel adminDbTopLevel;
	private DatabaseServerImpl dbServerImpl;

	public ResultSetCrawler(AdminDBTopLevel adminDbTopLevel, DatabaseServerImpl dbServerImpl) {
		this.adminDbTopLevel = adminDbTopLevel;
		this.dbServerImpl = dbServerImpl;
	}
	
	public void run(File outputDir, boolean bScanOnly, String singleUsername, String startingUsername
//			, String ampliCredName, String ampliCredPassword
			) throws SQLException, RemoteException, DataAccessException {

		File primaryDataRootDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty));
		File secondaryDataRootDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.secondarySimDataDirInternalProperty));
		if (primaryDataRootDir.equals(secondaryDataRootDir)){
			secondaryDataRootDir = null;
		}

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
		List<File> useDirectoriesToScan = getDirectoriesToScan(usersToScan, primaryDataRootDir, secondaryDataRootDir);

		for (File userDir : useDirectoriesToScan){
			try {
				if (lg.isTraceEnabled()) lg.trace("USER: " + userDir.getName());

				User user = usersToScan.get(userDir.getName());

				// find all the user simulations and external data sets (field data)
				SimulationInfo[] simulationInfos = dbServerImpl.getSimulationInfos(user, false);
				ExternalDataIdentifier[] extDataIDArr = adminDbTopLevel.getExternalDataIdentifiers(user,true);

				// scan this user directory
				AmplistorUtils.AmplistorCredential amplistorCredential = null;
//				if (ampliCredName != null && ampliCredPassword != null){
//					new AmplistorUtils.AmplistorCredential(ampliCredName, ampliCredPassword);
//				}
				scanUserDirectory(userDir, extDataIDArr, simulationInfos, outputDir, bScanOnly, amplistorCredential);
			} catch (Exception ex) {
				lg.error(ex.getMessage(), ex);
			}
		}
	}





private static List<File> getDirectoriesToScan(HashMap<String,User> usersToScan, File primaryDataRootDir, File secondaryDataRootDir) throws SQLException, DataAccessException, java.rmi.RemoteException {
		
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
				if (lg.isWarnEnabled()) lg.warn("user directory '" + userDir + "' doesn't exist or is not a directory");
				continue;
			}
			
			dirsToScan.add(userDir);
		} catch (Exception ex) {
			lg.error(ex.getMessage(), ex);
		}
	}
	return dirsToScan;
}


/**
 * Insert the method's description here.
 * Creation date: (2/2/01 3:40:29 PM)
 */
private static void scanUserDirectory(File userDir, ExternalDataIdentifier[] extDataIDArr, SimulationInfo[] simulationInfos, File outputDir, final boolean bScanOnly,AmplistorUtils.AmplistorCredential amplistorCredential) throws Exception {
	File outputFile = null;
	java.io.PrintWriter writer = null;
	try {
		long timestamp = new Date().getTime();
		outputFile = new File(outputDir, "ResultSetCrawler_" + userDir.getName() + "_" + timestamp + ".txt");
		writer = new java.io.PrintWriter(new java.io.FileOutputStream(outputFile));
		final PrintWriter pw = writer;
		
		pw.println("scanning directory : "+userDir.getPath());
		if (lg.isTraceEnabled()) lg.trace("scanning directory : "+userDir.getPath());
		
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
						if (lg.isTraceEnabled()) lg.trace("Should delete Amplistor "+fileName);					
					}
				}else{
					ArrayList<String> wasDeleted =
						AmplistorUtils.deleteSimFilesNotInHash(AmplistorUtils.DEFAULT_AMPLI_SERVICE_VCELL_URL+userDir.getName(), referencedKeys,bScanOnly,amplistorCredential);
					for(String fileName:wasDeleted){
						pw.println("deleted Amplistor "+fileName);
						if (lg.isTraceEnabled()) lg.trace("deleted Amplistor "+fileName);					
						
					}
				}
			}catch(Exception e){
				if (lg.isTraceEnabled()) lg.trace("Amplistor delete failed url="+AmplistorUtils.DEFAULT_AMPLI_SERVICE_VCELL_URL+userDir.getName()+" : "+e.getMessage());
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
	        						if (lg.isTraceEnabled()) lg.trace("Should delete file("+count+") with key " + simKey + " : " + file.getPath());
	        					}else{
	        						pw.println("deleted file("+simKey+":"+count+") " + file.getPath());
	        						if (lg.isTraceEnabled()) lg.trace("deleted file("+simKey+":"+count+") " + file.getPath());
	        						file.delete();
	        					}
	        				}
	        			}catch (Exception e){
	        				if (lg.isTraceEnabled()) lg.trace("failed to process file "+file.getPath()+": "+e.getMessage());
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
		if (lg.isTraceEnabled()) lg.trace("done scanning directory : "+userDir.getPath());
	} finally {
		if (writer != null) {
			writer.close();
		}
		if (lg.isTraceEnabled()) lg.trace("User " + userDir.getName() + ", See " + outputFile.getAbsolutePath() + " for details");
	}
}


}
