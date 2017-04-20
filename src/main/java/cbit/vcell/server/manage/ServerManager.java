/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server.manage;
import java.util.*;
import java.io.*;
import java.text.*;
/**
 * Insert the type's description here.
 * Creation date: (10/23/2001 3:49:24 PM)
 * @author: Ion Moraru
 */
public class ServerManager {
	public static final String[] SERVERS = {"R3", "DEV"};
	private ServerConfiguration serverConfiguration = null;
	private String serverName = null;
	private SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(" yyyy_MM_dd 'at' HH-mm-ss", java.util.Locale.US);
	private long timeLastReadConfigFile = 0;

/**
 * Insert the method's description here.
 * Creation date: (10/26/2001 5:49:02 PM)
 * @return boolean
 * @param file java.io.File
 * @param archiveDirectory java.io.File
 */
private boolean archiveByDateAndTime(File file, File archiveDirectory) {
	try {
		archiveDirectory.mkdir(); // in case it isn't there...
		String archivedName = file.getName() + dateTimeFormatter.format(new Date());
		return file.renameTo(new File(archiveDirectory, archivedName));
	}
	catch (Throwable exc) {
		log("Archiving failed " + exc.getMessage());
		return false;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/26/2001 6:45:11 PM)
 * @param host java.lang.String
 * @return java.io.File
 */
private File getLogArchiveDirectory(String host) {
	File baseDir = new File("log_archives");
	File hostDir = new File(baseDir, host);
	hostDir.mkdirs();
	return hostDir;
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 3:38:05 PM)
 * @return cbit.vcell.server.manage.ServerConfiguration
 */
public synchronized ServerConfiguration getServerConfiguration() {
	long elapsedTime = System.currentTimeMillis() - timeLastReadConfigFile;
	if (elapsedTime > 2*60*1000){
		try {
			readConfiguration();
			log("successfully read configuration file");
		}catch (Throwable e){
			log("failed to re-read the Configuration file, using old instead");
			e.printStackTrace(System.out);
		}
	}
	return serverConfiguration;
}


/**
 * Insert the method's description here.
 * Creation date: (10/26/2001 6:49:01 PM)
 * @return java.lang.String
 */
public java.lang.String getServerName() {
	return serverName;
}


/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 6:24:39 PM)
 * @param message java.lang.String
 */
void log(String message) {
	System.out.println(new Date() + " " + message);
}


/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 4:47:40 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	if (args.length != 1) {
		System.out.println("Usage: cbit.vcell.server.ServerManager ServerName");
		System.out.println("Example: cbit.vcell.server.ServerManager R3");
		System.exit(1);
	}
	try {
		int serverID = -1;
		for (int i=0;i<SERVERS.length;i++) {
			if (SERVERS[i].equals(args[0])) {
				serverID = i;
			}
		}
		if (serverID == -1) {
			System.out.println("Error: Unknown server " + args[0]);
			System.exit(1);
		} else {
			runManager(serverID);
		}
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		System.exit(1);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 5:11:42 PM)
 */
private void monitorAll() {
	log("Starting monitor thread for databaseservers --");
	monitorServers("DatabaseServers monitor", VCellHost.DATABASE_SERVER, 5);
	log("Starting monitor thread for dataservers --");
	monitorServers("DataServers monitor", VCellHost.DATA_SERVER, 5);
	log("Starting monitor thread for computeservers --");
	monitorServers("ComputeServers monitor", VCellHost.COMPUTE_SERVER, 15);
	log("Starting monitor thread for vcellservers --");
	monitorServers("VCellServers monitor", VCellHost.VCELL_SERVER, 10);
}


/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 5:28:36 PM)
 * @param threadName java.lang.String
 * @param hosts cbit.vcell.server.manage.VCellHost[]
 * @param timeInterval int
 */
private void monitorServers(String threadName, int vcellHostType, int timeInterval) {
	ServerMonitor serverMonitor = new ServerMonitor(this,vcellHostType);
	serverMonitor.setName(threadName);
	serverMonitor.setTimeInterval(timeInterval);
	serverMonitor.start();
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 3:34:59 PM)
 * @exception java.io.IOException The exception description.
 * @exception java.io.FileNotFoundException The exception description.
 */
private void readConfiguration() throws java.io.IOException, java.io.FileNotFoundException {
	File configFile = new File("ServerManager" + getServerName() + ".config");
	setServerConfiguration(new ServerConfiguration(configFile));
	timeLastReadConfigFile = System.currentTimeMillis();
}


/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 8:21:18 PM)
 * @param serverID int
 */
protected static void runManager(int serverID) throws IOException, java.net.UnknownHostException {
	ServerManager serverManager = new ServerManager();
	serverManager.setServerName(SERVERS[serverID]);
	String logFileName = "ServerManager" + serverManager.getServerName() + ".log";
	serverManager.archiveByDateAndTime(new File(logFileName), serverManager.getLogArchiveDirectory("manager"));
	System.setOut(new PrintStream(new FileOutputStream(logFileName, true), true));
	serverManager.log("Starting ServerManager on " + java.net.InetAddress.getLocalHost() + " for server " + serverManager.getServerName());
	serverManager.readConfiguration();
	serverManager.startAll();
	serverManager.monitorAll();
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 3:38:05 PM)
 * @param newServerConfiguration cbit.vcell.server.manage.ServerConfiguration
 */
void setServerConfiguration(ServerConfiguration newServerConfiguration) {
	serverConfiguration = newServerConfiguration;
}


/**
 * Insert the method's description here.
 * Creation date: (10/26/2001 6:49:01 PM)
 * @param newServerName java.lang.String
 */
private void setServerName(java.lang.String newServerName) {
	serverName = newServerName;
}


/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 5:11:42 PM)
 */
private void startAll() {
	log("Starting the databaseservers --");
	startServers(getServerConfiguration().getDatabaseServerHosts());
	log("Starting the dataservers --");
	startServers(getServerConfiguration().getDataServerHosts());
	log("Starting the computeservers --");
	startServers(getServerConfiguration().getComputeServerHosts());
	log("Waiting 60 seconds for secondary servers to come up");
	try {
		Thread.sleep(60*1000);
	}catch (InterruptedException e){
	}
	log("Starting the vcellservers --");
	startServers(getServerConfiguration().getVCellServerHosts());
}

/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 7:32:41 PM)
 * @param hosts cbit.vcell.server.manage.VCellHost[]
 */
void startServers(VCellHost[] hosts) {
	for (int i=0;i<hosts.length;i++) {
		log("Trying to start server on " + hosts[i].getName() + " on port " + hosts[i].getPort());
		try {
			Runtime.getRuntime().exec(hosts[i].getStopCommand());
			archiveByDateAndTime(hosts[i].getLogFile(), getLogArchiveDirectory(hosts[i].getName()));
			Runtime.getRuntime().exec(hosts[i].getStartCommand());
		} catch (Throwable exc) {
			log("EXCEPTION while starting server " + hosts[i].getName());
			exc.printStackTrace(System.out);
		}
	}
}
}
