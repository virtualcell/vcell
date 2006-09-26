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

import cbit.gui.PropertyLoader;
import cbit.rmi.event.VCSimulationDataIdentifier;
import cbit.sql.ConnectionFactory;
import cbit.sql.DBCacheTable;
import cbit.util.DataAccessException;
import cbit.util.KeyValue;
import cbit.util.PermissionException;
import cbit.util.SessionLog;
import cbit.util.User;
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
	this.conFactory = argConFactory;
	this.log = argSessionLog;
	this.adminDbServer = argAdminDbServer;
	this.dbCacheTable = argDbCacheTable;
	this.resultSetDbTopLevel = new ResultSetDBTopLevel(conFactory,log,dbCacheTable);
	dataRootDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.serverSimDataDirProperty));
}


/**
 * Insert the method's description here.
 * Creation date: (6/8/2001 9:26:27 AM)
 * @param logFileNames java.lang.String[]
 */
private void deleteResultSet(File logFile) {
	try {
		String logFileName = logFile.getAbsolutePath();
		String baseName = logFileName.substring(0,logFileName.indexOf(".log"));

		// we want to delete SimID_XXXX.*, not SimID_XXX*
		File files[] = logFile.getParentFile().listFiles(new BaseNameFilter(baseName));
		for (int i=0;i<files.length;i++){
			log.print("deleting " + files[i]);
			files[i].delete();
		}
	}catch (Throwable e){
		log.alert("EXCEPTION deleting resultSet "+logFile.getName()+" "+e.getMessage());
	}
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
	if (args.length > 1) {
		System.out.println("ResultSetCrawler [username]");
		System.exit(-1);
	}
	
	ConnectionFactory conFactory = null;
	try {
		
		PropertyLoader.loadProperties();

		SessionLog log = new cbit.util.StdoutSessionLog("ResultSetCrawler");		
		conFactory = new cbit.sql.OraclePoolingConnectionFactory(log);
		cbit.sql.KeyFactory keyFactory = new cbit.sql.OracleKeyFactory();
		
		AdminDatabaseServer adminDbServer = new LocalAdminDbServer(conFactory,keyFactory,log);
		cbit.sql.DBCacheTable dbCacheTable = new DBCacheTable(1000*60*30);
		
		ResultSetCrawler crawler = new ResultSetCrawler(conFactory, adminDbServer, log, dbCacheTable);
		if (args.length == 1) {
			crawler.scanAUser(args[0]);
		} else {
			crawler.scanAllUsers();
		}
		
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	} finally {
		try {
			conFactory.closeAll();
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
private void scan(File userDir, Vector simInfoList, boolean bDeleteFiles) throws Exception {
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
			} else {
				//log.alert("Simulation " + simInfo.getAuthoritativeVCSimulationIdentifier() + " has never been run");
			}
		}
	}

	// delete what's left in the list
	for (int i = 0; i < logfileList.size(); i ++) {
		File lf = (File)logfileList.get(i);
		deleteResultSet(lf);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/2/01 3:40:29 PM)
 */
public void scanAllUsers() throws SQLException, DataAccessException, java.rmi.RemoteException {
		
	File userDirs[] = dataRootDir.listFiles();
	log.print("Total user directories: " + userDirs.length);

	cbit.util.UserInfo userInfos[] = adminDbServer.getUserInfos();	
	DBTopLevel dbTopLevel = new DBTopLevel(conFactory,log,dbCacheTable);

	File userDir = null;
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
				return;
			}

			if (!userDir.exists() || !userDir.isDirectory()) {
				log.alert("UserDir " + userDir + " doesn't exist or is not a directory");
				return;
			}
			
			// find all the user simulations
			Vector simInfoList = dbTopLevel.getVersionableInfos(user,null,cbit.util.VersionableType.Simulation,false,false, true);
			scan(userDir, simInfoList, true);
		} catch (Exception ex) {
			log.exception(ex);
		}
	}			
}


/**
 * Insert the method's description here.
 * Creation date: (2/2/01 3:40:29 PM)
 */
public void scanAUser(String username) throws SQLException, DataAccessException, java.rmi.RemoteException {

	try {	
		File userDir = new File(dataRootDir, username);
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
		Vector simInfoList = dbTopLevel.getVersionableInfos(user,null,cbit.util.VersionableType.Simulation,false,false, true);
		scan(userDir, simInfoList, true);
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
		System.out.println("Log file " + logFile.getName() + " not found for simInfo (" + vcSimDataID + ")");
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