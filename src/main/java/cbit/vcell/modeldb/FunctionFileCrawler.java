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
import java.sql.SQLException;
import java.util.Vector;

import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;

import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.sql.OracleKeyFactory;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.AdminDatabaseServer;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solvers.FunctionFileGenerator;

/**
 * Insert the type's description here.
 * Creation date: (2/2/01 2:57:33 PM)
 * @author: Jim Schaff
 */
public class FunctionFileCrawler {
	private AdminDatabaseServer adminDbServer = null;
	private SessionLog log = null;
	private File dataRootDir = null;
	private String outputDirName = null;
	private int totalNumOfFilesModified = 0;
	
	class BaseNameFilter implements java.io.FilenameFilter {
		private String fieldBaseName = null;
		public BaseNameFilter(String baseName){
			fieldBaseName = (new File(baseName)).getName();
		}
		public boolean accept(java.io.File dir, String filename){
			if (!filename.startsWith(fieldBaseName)){
				return false;
			}
			if (filename.startsWith(fieldBaseName + ".")){
				return true;
			}
			if (filename.endsWith(".zip")){
				for (int i = 0; i < 10; i++){
					if (filename.equalsIgnoreCase(fieldBaseName + "0"+i+".zip")){
						return true;
					} 
				}
			}
			return false;
		}
	};

/**
 * ResultSetCrawler constructor comment.
 */
public FunctionFileCrawler(AdminDatabaseServer argAdminDbServer, SessionLog argSessionLog) throws SQLException {
	this(argAdminDbServer, argSessionLog, null);
}


/**
 * ResultSetCrawler constructor comment.
 */
private FunctionFileCrawler(AdminDatabaseServer argAdminDbServer, SessionLog argSessionLog, String argOutputDirName) throws SQLException {
	this.log = argSessionLog;
	this.adminDbServer = argAdminDbServer;
	dataRootDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirProperty));
	outputDirName = argOutputDirName;
}


/**
 * Insert the method's description here.
 * Creation date: (10/30/2006 8:33:39 AM)
 * @return java.io.File
 */
private File getOutputDirectory() {
	File outputDir = null;
	if (outputDirName == null) {
		outputDir = new File(".");
	} else {
		outputDir = new File(outputDirName);		
		if (!outputDir.exists()) {
			throw new RuntimeException("Outuput directory doesn't exist!");
		}
	}
	return outputDir;
}


/**
 * Insert the method's description here.
 * Creation date: (6/27/2006 1:33:58 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	ConnectionFactory conFactory = null;
	try {		
		String username = null;
		String outputdir = ".";
		int count = 0;
		boolean SCAN_SINGLE = false;

		while (count < args.length) {
			if (args[count].equals("-h")) {
				printUsage();
				System.exit(0);
			} else if (args[count].equals("-u")) {
				count ++;
				username = args[count];
				SCAN_SINGLE = true;
			} else if (args[count].equals("-o")) {
				count ++;
				outputdir = args[count];
			} else {
				System.out.println("Wrong arguments, see usage below.");
				printUsage();
				System.exit(1);
			}
			count ++;
		}
			
		PropertyLoader.loadProperties();
		SessionLog log = new StdoutSessionLog("FunctionFileCrawler");		
		KeyFactory keyFactory = new OracleKeyFactory();
		AdminDatabaseServer adminDbServer = new LocalAdminDbServer(conFactory,keyFactory,log);
			
		FunctionFileCrawler crawler = new FunctionFileCrawler(adminDbServer, log, outputdir);
		if (SCAN_SINGLE) {
			crawler.scanAUser(username);
		} else {
			crawler.scanAllUsers();
		}
		System.exit(0);
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	} finally {
		try {
			if (conFactory != null) {
				conFactory.closeAll();
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		System.exit(0);		
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/30/2006 1:58:45 PM)
 */
private static void printUsage() {
	System.out.println("ResultSetCrawler [-h] [-u username] [-o outputdir]");
	System.out.println("-h : \n\thelp");
	System.out.println("-u username: \n\tscan a single user only");
	System.out.println("-o outputdir : \n\tdirectory where scan results are stored (default is current directory)");
}


/**
 * Insert the method's description here.
 * Creation date: (2/2/01 3:40:29 PM)
 */
private void scan(File userDir, File outputDir) throws Exception {
	File outputFile = null;
	java.io.PrintWriter pw = null;
	try {
		outputFile = new File(outputDir, "FunctionFileCrawler_" + userDir.getName() + ".txt");
		pw = new java.io.PrintWriter(new java.io.FileOutputStream(outputFile));
		
		//
		// file filter for *.functions files
		//	
		java.io.FilenameFilter functionFileFilter = new java.io.FilenameFilter() {
			public boolean accept(File dir, String name) { 
				return name.endsWith(".functions"); 
			} 
		};
		
		// find all the log files
		File functionFiles[] = userDir.listFiles(functionFileFilter);
		
		// loop through all the functions files
		int filesWithSpaceInNames = 0;
		for (int i = 0; i < functionFiles.length; i ++){
			File fnFile = functionFiles[i];
			if (fnFile.exists()) {
				// read the functions from the function files and and check if they have spaces
				Vector<AnnotatedFunction> annotatedFnsVector = FunctionFileGenerator.readFunctionsFile(fnFile, null);
				boolean bNameHasSpaces = false;
				for (int j = 0; j < annotatedFnsVector.size(); j++) {
					AnnotatedFunction afn = annotatedFnsVector.elementAt(j);
					if (afn.getName().indexOf(" ") > 0) {
						// if function name has space, mangle the name and store the function in a different list.
						bNameHasSpaces = true;
						String newName = TokenMangler.fixTokenStrict(afn.getName());
						annotatedFnsVector.set(j, new AnnotatedFunction(newName, afn.getExpression(), afn.getDomain(), afn.getErrorString(), afn.getFunctionType(), afn.getFunctionCatogery()));
					} else {
						annotatedFnsVector.set(j, afn);
					}
				}
				
				// If function file had function(s) with space(s), need to rewrite function file
				if (bNameHasSpaces) {
					FunctionFileGenerator ffg = new FunctionFileGenerator(fnFile.getPath(), annotatedFnsVector);
					ffg.generateFunctionFile();
					filesWithSpaceInNames++;
					totalNumOfFilesModified++;
				}
			} else {			 
				System.err.println("Function file : " + fnFile.getName() + " does not exist.");
			}
		}
		System.out.println("NUM Files with spaces in names : " + filesWithSpaceInNames + "; NUM Files with No spaces in names : " + (functionFiles.length - filesWithSpaceInNames));
	} finally {
		if (pw != null) {
			pw.close();
		}
		log.print("User " + userDir.getName() + ", See " + outputFile.getAbsolutePath() + " for details");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/2/01 3:40:29 PM)
 */
private void scanAllUsers() throws SQLException, DataAccessException, java.rmi.RemoteException {
		
	File userDirs[] = dataRootDir.listFiles();
	log.print("Total user directories: " + userDirs.length);

	UserInfo userInfos[] = adminDbServer.getUserInfos();	

	File userDir = null;
	File outputDir = getOutputDirectory();
	for (int i = 0; i < userDirs.length; i ++){
		try {
			userDir = userDirs[i];
			log.print("----------------------------------------------------------");
			log.print("USER: " + userDir.getName());

			User user = null;
			for (int j = 0; j < userInfos.length; j ++) {
				if (userDir.getName().equals(userInfos[j].userid)) {
					user = new User(userInfos[j].userid,userInfos[j].id);
					break;
				}
			}
			
			if (user == null) {
				log.alert("User " + user + " doesn't exit!!");
				continue;
			}

			if (!userDir.exists() || !userDir.isDirectory()) {
				log.alert("UserDir " + userDir + " doesn't exist or is not a directory");
				continue;
			}
			
			// find all the user simulations
			scan(userDir,outputDir);
			log.print(" Total NUM of files modified : " + totalNumOfFilesModified);
			log.print("----------------------------------------------------------");
		} catch (Exception ex) {
				log.exception(ex);
		}
		log.print(" Total NUM of files modified : " + totalNumOfFilesModified);
	}			
}


/**
 * Insert the method's description here.
 * Creation date: (2/2/01 3:40:29 PM)
 */
private void scanAUser(String username) throws SQLException, DataAccessException, java.rmi.RemoteException {

	try {	
		File userDir = new File(dataRootDir, username);
		File outputDir = getOutputDirectory();
		
		if (!userDir.exists() || !userDir.isDirectory()) {
			log.alert("UserDir " + userDir + " doesn't exist or is not a directory");
			return;
		}

		log.print("----------------------------------------------------------");
		log.print("USER: " + userDir.getName());

		User user = adminDbServer.getUser(username);
		
		if (user == null) {
			log.alert("User " + user + " doesn't exit!!");
			return;
		}
		
		scan(userDir,outputDir);
		log.print(" Total NUM of files modified : " + totalNumOfFilesModified);
		log.print("----------------------------------------------------------");
	} catch (Exception ex) {
		log.exception(ex);
	}				
}
}
