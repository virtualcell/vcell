package cbit.vcell.client.test;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.beans.PropertyVetoException;
import java.io.File;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Vector;

import cbit.rmi.event.VCSimulationDataIdentifier;
import cbit.sql.ConnectionFactory;
import cbit.sql.DBCacheTable;
import cbit.util.DataAccessException;
import cbit.util.PermissionException;
import cbit.util.PropertyLoader;
import cbit.util.SessionLog;
import cbit.util.StdoutSessionLog;
import cbit.util.document.KeyValue;
import cbit.util.document.User;
import cbit.util.document.UserInfo;
import cbit.util.document.VersionableType;
import cbit.vcell.modeldb.AdminDatabaseServer;
import cbit.vcell.modeldb.DBTopLevel;
import cbit.vcell.modeldb.LocalAdminDbServer;
import cbit.vcell.modeldb.ResultSetDBTopLevel;
import cbit.vcell.modeldb.SolverResultSetInfo;
import cbit.vcell.simulation.Simulation;
import cbit.vcell.simulation.SimulationInfo;

/**
 * Insert the type's description here.
 * Creation date: (2/2/01 2:57:33 PM)
 * @author: Jim Schaff
 */
public class ResultSetCrawler {
	private AdminDatabaseServer adminDbServer = null;
	private cbit.sql.ConnectionFactory conFactory = null;
	private cbit.util.SessionLog log = null;
	private cbit.sql.DBCacheTable dbCacheTable = null;
	private cbit.vcell.modeldb.ResultSetDBTopLevel resultSetDbTopLevel = null;
	private File dataRootDir = null;
	private String outputDirName = null;
	
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
public ResultSetCrawler(ConnectionFactory argConFactory, AdminDatabaseServer argAdminDbServer, SessionLog argSessionLog, DBCacheTable argDbCacheTable) throws SQLException {
	this(argConFactory, argAdminDbServer, argSessionLog, argDbCacheTable, null);
}

/**
 * ResultSetCrawler constructor comment.
 */
private ResultSetCrawler(ConnectionFactory argConFactory, AdminDatabaseServer argAdminDbServer, SessionLog argSessionLog, DBCacheTable argDbCacheTable, String argOutputDirName) throws SQLException {
	this.conFactory = argConFactory;
	this.log = argSessionLog;
	this.adminDbServer = argAdminDbServer;
	this.dbCacheTable = argDbCacheTable;
	this.resultSetDbTopLevel = new ResultSetDBTopLevel(conFactory,log,dbCacheTable);
	dataRootDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.serverSimDataDirProperty));
	outputDirName = argOutputDirName;
}
	
	
/**
 * Insert the method's description here.
 * Creation date: (6/8/2001 9:26:27 AM)
 * @param logFileNames java.lang.String[]
 */
private void deleteResultSet(File logFile, java.io.PrintWriter pw) {
	try {
		String logFileName = logFile.getAbsolutePath();
		String baseName = logFileName.substring(0,logFileName.indexOf(".log"));

		// we want to delete SimID_XXXX.* or SimID_XXXX_YY_.*, not SimID_XXX*
		File files[] = logFile.getParentFile().listFiles(new BaseNameFilter(baseName));
		for (int i = 0;i < files.length; i ++){
			files[i].delete();
			pw.println("deleted " + files[i].getAbsolutePath());
			log.print("deleted " + files[i].getAbsolutePath());
		}
	} catch (Throwable e){
		log.alert("EXCEPTION deleting resultSet " + logFile.getName() + " " + e.getMessage());
	}
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
 * This method was created in VisualAge.
 * @return java.io.File
 * @param user cbit.vcell.server.User
 * @param simID java.lang.String
 */
private File getLogFile(File userDir, VCSimulationDataIdentifier vcsdi) {
	File logFile = new File(userDir,vcsdi.getID() + ".log");
	if (logFile.exists()){ // new style
		return logFile;
	} else {
		// maybe we are being asked for pre-parameter scans data files, try old style
		if (vcsdi.getJobIndex() == 0) {
			logFile = new File(userDir, cbit.rmi.event.VCSimulationDataIdentifierOldStyle.createVCSimulationDataIdentifierOldStyle(vcsdi).getID() + ".log");
			if (logFile.exists()) {
				return logFile;
				
			}
		} 
		
		return null;		
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/27/2006 1:33:58 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	ConnectionFactory conFactory = null;
	try {		
		boolean SCAN_ONLY = true;
		boolean SCAN_SINGLE = false;
		String username = null;
		String outputdir = ".";

		int count = 0;

		while (count < args.length) {
			if (args[count].equals("-h")) {
				printUsage();
				System.exit(0);
			} else if (args[count].equals("-u")) {
				count ++;
				username = args[count];
				SCAN_SINGLE = true;
			} else if (args[count].equals("-c")) {
				count ++;
				username = args[count];
				SCAN_SINGLE = false;
			} else if (args[count].equals("-o")) {
				count ++;
				outputdir = args[count];
			} else if (args[count].equals("-d")) {
				SCAN_ONLY = false;
			} else if (args[count].equals("-s")) {
				SCAN_ONLY = true;
			} else {
				System.out.println("Wrong arguments, see usage below.");
				printUsage();
				System.exit(1);
			}
			count ++;
		}
			
		PropertyLoader.loadProperties();

		SessionLog log = new StdoutSessionLog("ResultSetCrawler");		
		conFactory = new cbit.sql.OraclePoolingConnectionFactory(log);
		cbit.sql.KeyFactory keyFactory = new cbit.sql.OracleKeyFactory();
		
		AdminDatabaseServer adminDbServer = new LocalAdminDbServer(conFactory,keyFactory,log);
		cbit.sql.DBCacheTable dbCacheTable = new DBCacheTable(1000*60*30);		
			
		ResultSetCrawler crawler = new ResultSetCrawler(conFactory, adminDbServer, log, dbCacheTable, outputdir);
		if (SCAN_SINGLE) {
			crawler.scanAUser(username, SCAN_ONLY);
		} else {
			crawler.scanAllUsers(username, SCAN_ONLY);
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
 * Creation date: (2/2/01 3:40:29 PM)
 */
private void scan(File userDir, Vector simInfoList, SolverResultSetInfo[] resultSetInfos, File outputDir, boolean bScanOnly) throws Exception {
	File outputFile = null;
	java.io.PrintWriter pw = null;
	try {
		outputFile = new File(outputDir, "ResultSetCrawler_" + userDir.getName() + ".txt");
		pw = new java.io.PrintWriter(new java.io.FileOutputStream(outputFile));
		
		//
		// file filter for *.log files
		//	
		java.io.FilenameFilter logFileFilter = new java.io.FilenameFilter() {
			public boolean accept(File dir, String name) { 
				return name.endsWith(".log"); 
			} 
		};
		
		// find all the log files
		File logFiles[] = userDir.listFiles(logFileFilter);
		java.util.List logfileList = new LinkedList(java.util.Arrays.asList(logFiles));
		
		// loop through all the simulation info
		// remove from log file list all log files that have simulations in the database
		for (int i = 0; i < resultSetInfos.length; i ++){
			SolverResultSetInfo resultSetInfo = resultSetInfos[i];
		
			KeyValue simKey = resultSetInfo.getVCSimulationDataIdentifier().getSimulationKey();
			String logfilePrefix = Simulation.createSimulationID(simKey);
			
			File logFile = new File(userDir, logfilePrefix + ".log");
			if (logFile.exists()) { // old style before parameter scans
				logfileList.remove(logFile);
			} else {			 
				logFile = new File(userDir,logfilePrefix + "_0_.log");
				if (logFile.exists()) { // new style
					int jobIndex = 0;
					while (true) { // to see if there is parameter scan associated with this simulation
						File lf = new File(userDir,logfilePrefix + "_" + jobIndex + "_.log");
						if (lf.exists()) {
							logfileList.remove(lf);
							jobIndex ++;
						} else {
							break;
						}
					}
				} else {
					pw.println("Result set not found, " + resultSetInfo);
					log.print("Result set not found, " + resultSetInfo);
				}
			}
		}

		for (int i = 0; i < simInfoList.size(); i ++){
			SimulationInfo simInfo = (SimulationInfo)simInfoList.elementAt(i);
		
			KeyValue simKey = simInfo.getVersion().getVersionKey();
			String logfilePrefix = Simulation.createSimulationID(simKey);
			
			File logFile = new File(userDir, logfilePrefix + ".log");
			if (logFile.exists()) { // old style before parameter scans			
				logfileList.remove(logFile);
			} else {			 
				logFile = new File(userDir,logfilePrefix + "_0_.log");
				if (logFile.exists()) { // new style
					int jobIndex = 0;
					while (true) { // to see if there is parameter scan associated with this simulation
						File lf = new File(userDir,logfilePrefix + "_" + jobIndex + "_.log");
						if (lf.exists()) {
							logfileList.remove(lf);
							jobIndex ++;
						} else {
							break;
						}
					}
				}
			}
		}	
			
		if (bScanOnly) {
			for (int i = 0; i < logfileList.size(); i ++) {
				pw.println("Should delete " + logfileList.get(i));
				log.print("Should delete " + logfileList.get(i));
			}
			return;
		}
		
		// delete what's left in the list
		for (int i = 0; i < logfileList.size(); i ++) {
			File lf = (File)logfileList.get(i);		
			deleteResultSet(lf, pw);
		}

	} finally {
		if (pw != null) {
			pw.close();
		}
		log.print("User " + userDir.getName() + ", See " + outputFile.getAbsolutePath() + " for details");
	}
}

/**
 * Insert the method's description here.
 * Creation date: (10/30/2006 1:58:45 PM)
 */
private static void printUsage() {
	System.out.println("ResultSetCrawler [-h] [-u username] [-c username] [-o outputdir] [-d | -s]");
	System.out.println("-h : \n\thelp");
	System.out.println("-u username: \n\tscan a single user only");
	System.out.println("-c username: \n\tcontinue scanning from a user");
	System.out.println("-o outputdir : \n\tdirectory where scan results are stored (default is current directory)");
	System.out.println("-s : \n\tscan only (default)");
	System.out.println("-d : \n\tscan and delete files");
}

/**
 * Insert the method's description here.
 * Creation date: (2/2/01 3:40:29 PM)
 */
private void scanAllUsers(String startUser, boolean bScanOnly) throws SQLException, DataAccessException, java.rmi.RemoteException {
		
	File userDirs[] = dataRootDir.listFiles();
	log.print("Total user directories: " + userDirs.length);

	UserInfo userInfos[] = adminDbServer.getUserInfos();	
	DBTopLevel dbTopLevel = new DBTopLevel(conFactory,log,dbCacheTable);

	File userDir = null;
	File outputDir = getOutputDirectory();
	for (int i = 0; i < userDirs.length; i ++){
		try {
			userDir = userDirs[i];
			log.print("----------------------------------------------------------");
			log.print("USER: " + userDir.getName());

			if (startUser != null && userDir.getName().compareToIgnoreCase(startUser) < 0) {
				log.print("Skip user " + userDir.getName());
				continue;
			}
			
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
			Vector simInfoList = dbTopLevel.getVersionableInfos(user,null,VersionableType.Simulation,false,false, true);
			SolverResultSetInfo[] resultSetInfos = resultSetDbTopLevel.getResultSetInfos(user, false, false);
			scan(userDir, simInfoList, resultSetInfos, outputDir, bScanOnly);
		} catch (Exception ex) {
			log.exception(ex);
		}
	}			
}

/**
 * Insert the method's description here.
 * Creation date: (2/2/01 3:40:29 PM)
 */
private void scanAUser(String username, boolean bScanOnly) throws SQLException, DataAccessException, java.rmi.RemoteException {

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
		
		DBTopLevel dbTopLevel = new DBTopLevel(conFactory,log,dbCacheTable);
		
		// find all the user simulations
		Vector simInfoList = dbTopLevel.getVersionableInfos(user,null,VersionableType.Simulation,false,false, true);
		SolverResultSetInfo[] resultSetInfos = resultSetDbTopLevel.getResultSetInfos(user, false, false);
		scan(userDir, simInfoList, resultSetInfos, outputDir, bScanOnly);
		log.print("----------------------------------------------------------");
	} catch (Exception ex) {
		log.exception(ex);
	}				
}

	/**
 * Insert the method's description here.
 * Creation date: (2/14/01 9:48:46 AM)
 * @param user cbit.vcell.server.User
 * @param simInfo cbit.vcell.solver.SimulationInfo
 */
public void updateSimResults(User user, VCSimulationDataIdentifier vcSimDataID) throws java.io.IOException, DataAccessException {	
	if (user==null){
		throw new IllegalArgumentException("user was null");
	}
	if (vcSimDataID == null){
		throw new IllegalArgumentException("vcSimDataID was null");
	}
	
	//
	// find userDirectory for this user
	//
	File userDir = new File(dataRootDir, user.getName());
	if (!userDir.exists() || !userDir.isDirectory()) {
		throw new java.io.FileNotFoundException("data directory for user " + user + " not found");
	}
	//
	// get snapshot of this resultSetInfo for current user and simInfo only
	//
	SolverResultSetInfo oldResultSetInfo = null;
	KeyValue simKey = vcSimDataID.getSimulationKey();
	try {
		oldResultSetInfo = resultSetDbTopLevel.getResultSetInfo(user, simKey, vcSimDataID.getJobIndex(), true);
	}catch (SQLException e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}

	File logFile = getLogFile(userDir, vcSimDataID);
	
	if (logFile != null) {
		//
		// if log file found, then insert/update record in database for result metadata
		//
		SolverResultSetInfo rsetInfo = new SolverResultSetInfo(vcSimDataID);
		rsetInfo.setDataFilePath(logFile.getPath());
		try {
			rsetInfo.setStartingDate(new java.util.Date(logFile.lastModified()));
		} catch (PropertyVetoException e) {
		}
		try {
			resultSetDbTopLevel.updateResultSetInfo(user, simKey, rsetInfo, true);
			log.print("file " + logFile.toString() + " found, simInfo (" + vcSimDataID + ") stored in database");
		}catch (PermissionException e){
			log.exception(e);
			throw new DataAccessException(e.getMessage());
		}catch (SQLException e){
			log.exception(e);
			throw new DataAccessException(e.getMessage());
		}
	} else {
		System.out.println("Log file not found for simInfo (" + vcSimDataID + ")");
		//
		// if log file not found, remove resultset metadata database record if it exists
		// (this cleans up the database if the dataset is no longer availlable in the file system)
		//
		if (oldResultSetInfo != null) {
			try {
				resultSetDbTopLevel.deleteResultSetInfoSQL(user, simKey, true);
			}catch (PermissionException e){
				log.exception(e);
				throw new DataAccessException(e.getMessage());
			}catch (SQLException e){
				log.exception(e);
				throw new DataAccessException(e.getMessage());
			}
			log.print("log file SimID=\""+vcSimDataID+"\" not found, result set removed from database");
		}else{
			log.print("log file SimID=\""+vcSimDataID+"\" not found, result set not stored to database");
		}
	}
}
}