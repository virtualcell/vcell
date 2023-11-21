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

import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.AdminDatabaseServer;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solvers.FunctionFileGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.KeyFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;

import java.io.File;
import java.sql.SQLException;
import java.util.Vector;

/**
 * Insert the type's description here.
 * Creation date: (2/2/01 2:57:33 PM)
 * @author: Jim Schaff
 */
public class FunctionFileCrawler {
	public static final Logger lg = LogManager.getLogger(FunctionFileCrawler.class);

	private AdminDatabaseServer adminDbServer = null;
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
public FunctionFileCrawler(AdminDatabaseServer argAdminDbServer) throws SQLException {
	this(argAdminDbServer, null);
}


/**
 * ResultSetCrawler constructor comment.
 */
private FunctionFileCrawler(AdminDatabaseServer argAdminDbServer, String argOutputDirName) throws SQLException {
	this.adminDbServer = argAdminDbServer;
	dataRootDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty));
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
			
		conFactory = DatabaseService.getInstance().createConnectionFactory();
		KeyFactory keyFactory = conFactory.getKeyFactory();
		AdminDatabaseServer adminDbServer = new LocalAdminDbServer(conFactory,keyFactory);
			
		FunctionFileCrawler crawler = new FunctionFileCrawler(adminDbServer, outputdir);
		if (SCAN_SINGLE) {
			crawler.scanAUser(username);
		} else {
			crawler.scanAllUsers();
		}
		System.exit(0);
	} catch (Exception ex) {
		lg.error(ex.getMessage(), ex);
	} finally {
		try {
			if (conFactory != null) {
				conFactory.close();
			}
		} catch (Throwable ex) {
			lg.error(ex);
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
		lg.info("NUM Files with spaces in names : " + filesWithSpaceInNames + "; NUM Files with No spaces in names : " + (functionFiles.length - filesWithSpaceInNames));
	} finally {
		if (pw != null) {
			pw.close();
		}
		if (lg.isTraceEnabled()) lg.trace("User " + userDir.getName() + ", See " + outputFile.getAbsolutePath() + " for details");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/2/01 3:40:29 PM)
 */
private void scanAllUsers() throws SQLException, DataAccessException, java.rmi.RemoteException {
		
	File userDirs[] = dataRootDir.listFiles();
	if (lg.isTraceEnabled()) lg.trace("Total user directories: " + userDirs.length);

	UserInfo userInfos[] = adminDbServer.getUserInfos();	

	File userDir = null;
	File outputDir = getOutputDirectory();
	for (int i = 0; i < userDirs.length; i ++){
		try {
			userDir = userDirs[i];
			if (lg.isTraceEnabled()) lg.trace("----------------------------------------------------------");
			if (lg.isTraceEnabled()) lg.trace("USER: " + userDir.getName());

			User user = null;
			for (int j = 0; j < userInfos.length; j ++) {
				if (userDir.getName().equals(userInfos[j].userid)) {
					user = new User(userInfos[j].userid,userInfos[j].id);
					break;
				}
			}
			
			if (user == null) {
				if (lg.isWarnEnabled()) lg.warn("User " + user + " doesn't exit!!");
				continue;
			}

			if (!userDir.exists() || !userDir.isDirectory()) {
				if (lg.isWarnEnabled()) lg.warn("UserDir " + userDir + " doesn't exist or is not a directory");
				continue;
			}
			
			// find all the user simulations
			scan(userDir,outputDir);
			if (lg.isTraceEnabled()) lg.trace(" Total NUM of files modified : " + totalNumOfFilesModified);
			if (lg.isTraceEnabled()) lg.trace("----------------------------------------------------------");
		} catch (Exception ex) {
			lg.error(ex.getMessage(), ex);
		}
		if (lg.isTraceEnabled()) lg.trace(" Total NUM of files modified : " + totalNumOfFilesModified);
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
			if (lg.isWarnEnabled()) lg.warn("UserDir " + userDir + " doesn't exist or is not a directory");
			return;
		}

		if (lg.isTraceEnabled()) lg.trace("----------------------------------------------------------");
		if (lg.isTraceEnabled()) lg.trace("USER: " + userDir.getName());

		User user = adminDbServer.getUser(username);
		
		if (user == null) {
			if (lg.isWarnEnabled()) lg.warn("User " + user + " doesn't exit!!");
			return;
		}
		
		scan(userDir,outputDir);
		if (lg.isTraceEnabled()) lg.trace(" Total NUM of files modified : " + totalNumOfFilesModified);
		if (lg.isTraceEnabled()) lg.trace("----------------------------------------------------------");
	} catch (Exception ex) {
		lg.error(ex.getMessage(),ex);
	}				
}
}
