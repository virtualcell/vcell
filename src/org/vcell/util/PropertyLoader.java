/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertyLoader {
	
	//must come before uses of #record method
	private static HashMap<String, MetaProp> propMap = new HashMap<String,MetaProp>( );
	public static final String ADMINISTRATOR_ACCOUNT = "Administrator";
	public static final String ADMINISTRATOR_ID = "2";

	public static final String propertyFileProperty			= "vcell.propertyfile";

	public static final String vcellServerIDProperty        = record("vcell.server.id",ValueType.GEN);

	public static final String primarySimDataDirProperty	= record("vcell.primarySimdatadir",ValueType.DIR);
	public static final String secondarySimDataDirProperty	= record("vcell.secondarySimdatadir",ValueType.DIR);
	public static final String PARALLEL_DATA_DIR			= record("vcell.parallelDatadir",ValueType.DIR);

	public static final String jobMemoryOverheadMB			= record("vcell.htc.jobMemoryOverheadMB",ValueType.GEN);
	public static final String htcBatchSystemQueue			= record("vcell.htc.queue",ValueType.GEN);
	public static final String htcLogDir					= record("vcell.htc.logdir",ValueType.DIR);
	public static final String htcUser						= record("vcell.htc.user",ValueType.GEN);
	public static final String htcPbsHome		 			= record("vcell.htc.pbs.home",ValueType.GEN);
	public static final String htcSgeHome		 			= record("vcell.htc.sge.home",ValueType.GEN);
	public static final String sgeModulePath				= record("vcell.htc.sge.module",ValueType.GEN);
	public static final String pbsModulePath				= record("vcell.htc.pbs.module",ValueType.GEN);
	public static final String MPI_HOME				        = record("vcell.htc.mpi.home",ValueType.DIR);

	public static final String finiteVolumeExecutableProperty = record("vcell.finitevolume.executable",ValueType.EXE);

	//
	public static final String sundialsSolverExecutableProperty		= record("vcell.sundialsSolver.executable",ValueType.EXE);

	// Smoldyn
	public static final String smoldynExecutableProperty		= record("vcell.smoldyn.executable",ValueType.EXE);

	// NFSim
	public static final String nfsimExecutableProperty		= record("vcell.nfsim.executable",ValueType.EXE);
	
	//Stoch properties
	public static final String stochExecutableProperty		= record("vcell.stoch.executable",ValueType.EXE);	
	public static final String hybridEMExecutableProperty	= record("vcell.hybridEM.executable",ValueType.EXE);
	public static final String hybridMilExecutableProperty	= record("vcell.hybridMil.executable",ValueType.EXE);
	public static final String hybridMilAdaptiveExecutableProperty = record("vcell.hybridMilAdaptive.executable",ValueType.EXE);

	public static final String visitSmoldynVisitExecutableProperty	= record("vcell.visit.smoldynvisitexecutable",ValueType.EXE);
	public static final String visitSmoldynScriptPathProperty		= record("vcell.visit.smoldynscript",ValueType.GEN);

	//BioFormats plugin properties

	public static final String bioformatsJarFileName		= record("vcell.bioformatsJarFileName",ValueType.GEN);
	public static final String bioformatsClasspath			= record("vcell.bioformatsClasspath",ValueType.GEN);
	public static final String bioformatsJarDownloadURL		= record("vcell.bioformatsJarDownloadURL",ValueType.URL);

	//
	public static final String databaseThreadsProperty		= record("vcell.databaseThreads",ValueType.GEN);
	public static final String exportdataThreadsProperty	= record("vcell.exportdataThreads",ValueType.GEN);
	public static final String simdataThreadsProperty		= record("vcell.simdataThreads",ValueType.GEN);
	public static final String htcworkerThreadsProperty		= record("vcell.htcworkerThreads",ValueType.GEN);

	public static final String databaseCacheSizeProperty	= record("vcell.databaseCacheSize",ValueType.GEN);
	public static final String simdataCacheSizeProperty		= record("vcell.simdataCacheSize",ValueType.GEN);

	public static final String exportBaseURLProperty		= record("vcell.export.baseURL",ValueType.GEN);
	public static final String exportBaseDirProperty		= record("vcell.export.baseDir",ValueType.GEN);
	public static final String exportMaxInMemoryLimit		= record("vcell.export.maxInMemoryLimit",ValueType.INT);

	public static final String dbDriverName					= record("vcell.server.dbDriverName",ValueType.GEN);
	public static final String dbConnectURL					= record("vcell.server.dbConnectURL",ValueType.GEN);
	public static final String dbUserid						= record("vcell.server.dbUserid",ValueType.GEN);
	public static final String dbPassword					= record("vcell.server.dbPassword",ValueType.GEN);

	public static final String jmsProvider				= record("vcell.jms.provider",ValueType.GEN);
	public static final String jmsProviderValueActiveMQ		= record("ActiveMQ",ValueType.GEN);
	public static final String jmsURL					= record("vcell.jms.url",ValueType.GEN);
	public static final String jmsUser					= record("vcell.jms.user",ValueType.GEN);
	public static final String jmsPassword				= record("vcell.jms.password",ValueType.GEN);

	public static final String jmsSimReqQueue			= record("vcell.jms.queue.simReq",ValueType.GEN);
	public static final String jmsDataRequestQueue		= record("vcell.jms.queue.dataReq",ValueType.GEN);
	public static final String jmsDbRequestQueue		= record("vcell.jms.queue.dbReq",ValueType.GEN);
	public static final String jmsSimJobQueue			= record("vcell.jms.queue.simJob",ValueType.GEN);
	public static final String jmsWorkerEventQueue		= record("vcell.jms.queue.workerEvent",ValueType.GEN);
	public static final String jmsServiceControlTopic	= record("vcell.jms.topic.serviceControl",ValueType.GEN);
	public static final String jmsDaemonControlTopic	= record("vcell.jms.topic.daemonControl",ValueType.GEN);
	public static final String jmsClientStatusTopic		= record("vcell.jms.topic.clientStatus",ValueType.GEN);

	public static final String jmsBlobMessageMinSize	= record("vcell.jms.blobMessageMinSize",ValueType.GEN);
	public static final String jmsBlobMessageTempDir	= record("vcell.jms.blobMessageTempDir",ValueType.GEN);
	public static final String vcellClientTimeoutMS 	= record("vcell.client.timeoutMS",ValueType.GEN);

	public static final String maxOdeJobsPerUser	= record("vcell.server.maxOdeJobsPerUser",ValueType.GEN);
	public static final String maxPdeJobsPerUser	= record("vcell.server.maxPdeJobsPerUser",ValueType.GEN);
	public static final String maxJobsPerScan		= record("vcell.server.maxJobsPerScan",ValueType.GEN);
	public static final String maxJobsPerSite		= record("vcell.server.maxJobsPerSite",ValueType.GEN);
	public static final String limitJobMemoryMB		= record("vcell.limit.jobMemoryMB",ValueType.GEN);

	public static final String vcellSoftwareVersion = record("vcell.softwareVersion",ValueType.GEN);
	public static final String vcellThirdPartyLicense = record("vcell.thirdPartyLicense",ValueType.GEN);

	public static final String vcellServerHost = record("vcell.serverHost",ValueType.GEN);

	public static final String vcellSMTPHostName = record("vcell.smtp.hostName",ValueType.GEN);
	public static final String vcellSMTPPort = record("vcell.smtp.port",ValueType.GEN);
	public static final String vcellSMTPEmailAddress = record("vcell.smtp.emailAddress",ValueType.GEN);

	public static final String javaSimulationExecutable = record("vcell.javaSimulation.executable",ValueType.EXE);
	public static final String simulationPreprocessor = record("vcell.simulation.preprocessor",ValueType.EXE);
	public static final String simulationPostprocessor = record("vcell.simulation.postprocessor",ValueType.EXE);

	public final static String mongodbHost						= record("vcell.mongodb.host",ValueType.GEN);
	public final static String mongodbPort						= record("vcell.mongodb.port",ValueType.GEN);   // default 27017
	public final static String mongodbDatabase					= record("vcell.mongodb.database",ValueType.GEN);
	public final static String mongodbLoggingCollection			= record("vcell.mongodb.loggingCollection",ValueType.GEN);
	public final static String mongodbThreadSleepMS				= record("vcell.mongodb.threadSleepMS",ValueType.GEN);

	public static final String VCellChomboExecutable2D = record("vcell.chombo.executable.2d",ValueType.EXE);
	public static final String VCellChomboExecutable3D = record("vcell.chombo.executable.3d",ValueType.EXE);

	public static final String amplistorVCellUsersRootPath = record("vcell.amplistor.usersDir.root",ValueType.GEN);
	public static final String installationRoot = record("vcell.installDir",ValueType.DIR);
	public static final String vcellDownloadDir = record("vcell.downloadDir",ValueType.URL);
	public static final String autoflushStandardOutAndErr = record("vcell.autoflushlog",ValueType.GEN);
	public static final String suppressQStatStandardOutLogging = record("vcell.htc.logQStatOutput", ValueType.BOOL);
	
	private static File systemTemporaryDirectory = null;

	private enum ValueType {
		/**
		 * general string
		 */
		GEN,
		/**
		 * a directory
		 */
		DIR,
		/**
		 * a executable
		 */
		EXE,
		/**
		 * integer number (not necessary Integer class, could be Long, e.g.)
		 */ 
		INT,
		/**
		 * url
		 */
		 URL,
		/**
		 * boolean (true or false)
		 */
		 BOOL
	}

	/**
	 * properties of properties
	 */
	private static class MetaProp{
		/**
		 * specific value type, if any
		 */
		final ValueType valueType;
		/**
		 * is property in property file? 
		 */
		boolean  fileSet = false;
		/**
		 * is property set 
		 */
		boolean set = false;

		MetaProp(ValueType valueType) {
			this.valueType = valueType;
		} 

	}
	/**
	 * has user been nagged about required property not marked required?
	 */
	private static Map<String,Object> nagged = new HashMap<String, Object>();


	/**
	 * * record static String in {@link #propMap}
	 * @param in
	 * @param vt 
	 * @return in
	 */
	private static String record(String in, ValueType vt) {
		if (vt == null) {
			throw new Error("PropertyLoader not set correctly");
		}
		propMap.put(in, new MetaProp(vt));
		return in;
	}

	public PropertyLoader() throws Exception {
		loadProperties();
	}
	
	/**
	 * get Java's system temporary directory. This will be either a JVM default, or
	 * a directory specified by the "java.io.tmpdir" system property
	 * @return
	 * @throws IOException
	 */
	public static File getSystemTemporaryDirectory( ) throws IOException {
		if (systemTemporaryDirectory != null) {
			return systemTemporaryDirectory;
		}
		File query = File.createTempFile("PropertyLoaderQuery",null);
		systemTemporaryDirectory = query.getParentFile();
		query.delete();
		return systemTemporaryDirectory;
	}



	public final static boolean getBooleanProperty(String propertyName, boolean defaultValue){
		try {
			String propertyValue = System.getProperty(propertyName);
			if (propertyValue==null){
				return defaultValue;
			}else{
				return Boolean.parseBoolean(propertyValue);
			}
		}catch (Exception e){
			return defaultValue;
		}	
	}
	
	public final static int getIntProperty(String propertyName, int defaultValue) {
		try {
			String propertyValue = System.getProperty(propertyName);
			if (propertyValue==null){
				return defaultValue;
			}else{
				return Integer.parseInt(propertyValue);
			}
		}catch (Exception e){
			return defaultValue;
		}		
	}
	
	public final static long getLongProperty(String propertyName, long defaultValue) {
		try {
			String propertyValue = System.getProperty(propertyName);
			if (propertyValue==null){
				return defaultValue;
			}else{
				return Long.parseLong(propertyValue);
			}
		}catch (Exception e){
			return defaultValue;
		}		
	}


	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 * @param propertyName java.lang.String
	 */
	public final static String getProperty(String propertyName, String defaultValue) {
		try {
			String propertyValue = System.getProperty(propertyName);
			if (propertyValue==null){
				return defaultValue;
			}else{
				return propertyValue.trim();
			}
		}catch (Exception e){
			return defaultValue;
		}		
	}

	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 * @param propertyName java.lang.String
	 */
	public final static String getRequiredProperty(String propertyName) throws ConfigurationException {
		if (!nagged.containsKey(propertyName)) {
			if (!propMap.containsKey(propertyName) ) {
				System.err.println("Unknown required property " + propertyName);
				nagged.put(propertyName, null);
			}
		}
		try {
			String propertyValue = System.getProperty(propertyName);
			if (propertyValue==null){
				throw new ConfigurationException("required System property \""+propertyName+"\" not defined");
			}else{
				return propertyValue.trim();
			}
		}catch (Exception e){
			throw new ConfigurationException("required System property \""+propertyName+"\" not defined");
		}		
	}
	/**
	 * default; no validation of required
	 * @throws java.io.IOException
	 */
	public final static void loadProperties() throws java.io.IOException {
		loadProperties(false);
	}

	/**
	 * 
	 * @param ctx context to validate in (may be null)
	 * @param throwException throw exception if validation error
	 * @throws java.io.IOException
	 */
	public final static void loadProperties(boolean throwException) throws java.io.IOException {

		File propertyFile = null;
		//
		// if vcell.propertyfile defined (on the command line via -Dvcell.propertyfile=/tmp/vcell.properties)
		//
		String customPropertyFileName = System.getProperty(propertyFileProperty);
		String where = "";
		if (customPropertyFileName != null){
			propertyFile = new File(customPropertyFileName);
			where = "command line";
		}else{
			String tail = System.getProperty("file.separator") + "vcell.properties";

			// look in current working directory first
			propertyFile = new File("." + tail);
			where = "working directory"; //optimistic set
			if (!propertyFile.canRead()) {
				// then look in 'user.home' directory
				propertyFile = new File( System.getProperty("user.home") + tail); 
				where = "users home directory"; //optimistic set
				if (!propertyFile.canRead()) {
					// then look in 'java.home' directory
					propertyFile = new File(System.getProperty("java.home") + tail); 
					where = "java home directory"; //optimistic set
				}
			}
		} 
		if (propertyFile.canRead()) {
			java.util.Properties p = new Properties(); 
			java.io.FileInputStream propFile = new java.io.FileInputStream(propertyFile);
			p.load(propFile);
			propFile.close();
			System.out.println("loaded properties from " + propertyFile.getAbsolutePath() + " specifed by " + where);
			verifyPropertiesInFile(propertyFile,p);
			for (Map.Entry<Object,Object> entry : p.entrySet() ) {
				String key = entry.getKey().toString();
				String value = entry.getValue().toString();
				String existingValue = System.getProperty(key);
				if (existingValue == null) {
					System.setProperty(key, value);
				}
				else if (!existingValue.equals(value)){
					System.out.println("Property " + key + " property value "  + value + " overridden by system value " + existingValue);
				}
			}
		}
		validateSystemProperties(throwException);

		// display new properties
		//System.getProperties().list(System.out);
		System.out.println("ServerID=" + getRequiredProperty(vcellServerIDProperty)+", SoftwareVersion="+getRequiredProperty(vcellSoftwareVersion));
	}

	/**
	 * This method was created in VisualAge.
	 * @param args java.lang.String[]
	 */
	public static void main(String args[]) {
		try {
			System.out.println("\n\n\nloading properties....\n\n\n");
			PropertyLoader.loadProperties();

			PropertyLoader.show();

			//	System.out.println("verifying consistency of system properties");
			//	System.getProperty
		}catch (Exception e){
			e.printStackTrace(System.out);
			System.exit(1);
		}
	}


	/**
	 * This method was created in VisualAge.
	 */
	public final static void show() {
		System.getProperties().list(System.out);
	}


	/**
	 * verify no missing properties and no extra properties
	 * @param f file properties came from
	 * @param p the properties 
	 */
	private static void verifyPropertiesInFile(File f, Properties p) {
		String propertyFileName = f.getAbsolutePath();

		for (Object propName : p.keySet()) {
			//
			// complain if property file has an unknown property
			//
			if (!propMap.containsKey(propName)){
				System.out.println("<<<ERROR>>> UNKNOWN PROPERTY \""+propName+"\" in property file \""+propertyFileName+"\"");
			}
			else {
				propMap.get(propName).fileSet = true;
			}
		}

		//listing missing properties
		for (Map.Entry<String,MetaProp> entry : propMap.entrySet( )) {
			MetaProp mp = entry.getValue();
			if (!mp.fileSet) {
				System.out.println("<<<WARNING>>> MISSING PROPERTY \""+entry.getKey()+"\" in property file \""+propertyFileName+"\"");
			}
		}
	}

	/***
	 * check system properties against expected
	 * @param ctx
	 * @param throwException
	 * @return true if good ; false if not (and throwException is false)
	 */
	private static boolean validateSystemProperties(boolean throwException) {
		Properties p = System.getProperties();

		for (Object propName : p.keySet()) {
			if (propMap.containsKey(propName)) {
				propMap.get(propName).set = true;
			}
		}

		StringBuffer validationReport = new StringBuffer();
		List<String> missingReq = new ArrayList<String>( );
		//listing missing properties
		for (Map.Entry<String,MetaProp> entry : propMap.entrySet( )) {
			MetaProp mp = entry.getValue();
			if (!mp.set) {  
			}
			if (mp.set) {
				verifyEntry(entry,validationReport, mp.fileSet);
			}
		}
		String errors = "";
		if (missingReq.size( ) > 0) {
			errors = "Followed required properties not set:";
			for (String m : missingReq) {
				errors  += "\n" + m;
			}
			errors  += "\n";
		}
		if (validationReport.length() > 0) {
			errors  += "\n" + validationReport.toString();
		}
		if (!errors.isEmpty()) {
			if (throwException) {
				throw new IllegalStateException(errors);
			}
			else {
				System.err.println(errors);
			}

		}
		return true;
	}

	/**
	 * helper method for pretty print
	 * @param b if true, add "set from file" blah blah 
	 */
	private static String fromFile(boolean b) {
		if (b) {
			return " set from property file";
		}
		return "";
	}

	/**
	 * verify entry based on properties
	 * @param entry to verify
	 * @param report destination to report discrepancies
	 * @param fileSet is set from file? (for formatting messages)
	 */
	private static void verifyEntry( Map.Entry<String,MetaProp> entry, StringBuffer report, boolean fileSet)  {
		ValueType vt = entry.getValue().valueType;
		if (vt == ValueType.GEN) {
			return;
		}
		String value = System.getProperty(entry.getKey());
		switch (vt) {
		case GEN:
			assert false : "don't go here";
		return;
		case DIR:
		{
			File f = new File(value);
			if (!f.isDirectory()) {
				report.append(entry.getKey() + " value " + value + fromFile(fileSet) + " is not a directory\n");
			}
			return;
		}
		case EXE:
		{
			File f = new File(value);
			if (!f.canExecute()) {
				report.append(entry.getKey() + " value " + value + fromFile(fileSet) + " is not executable\n");
			}
			return;
		}
		case INT:
			try {
				Long.parseLong(value);
			} catch (NumberFormatException e) {
				report.append(entry.getKey() + " value " + value + fromFile(fileSet) + " not  convertible to long integer\n"); 
			}
		case URL:
			//not going to make trip web to verify at this point
		case BOOL:
			break;
		default:
			break;
		}
	}
}
