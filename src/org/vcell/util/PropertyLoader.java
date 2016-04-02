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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertyLoader {
	
	//must come before uses of #record method
	private static HashMap<String, MetaProp> propMap = new HashMap<String,MetaProp>( );
	public static final String ADMINISTRATOR_ACCOUNT = "Administrator";
	public static final String ADMINISTRATOR_ID = "2";

	public static final String propertyFileProperty			= "vcell.propertyfile";

	public static final String vcellServerIDProperty        = record("vcell.server.id",RequiredFor.NOT,ValueType.GEN);

	public static final String primarySimDataDirProperty	= record("vcell.primarySimdatadir",RequiredFor.SERVER,ValueType.DIR);
	public static final String secondarySimDataDirProperty	= record("vcell.secondarySimdatadir",RequiredFor.SERVER,ValueType.DIR);
	public static final String PARALLEL_DATA_DIR			= record("vcell.parallelDatadir",RequiredFor.SERVER,ValueType.DIR);

	public static final String jobMemoryOverheadMB			= record("vcell.htc.jobMemoryOverheadMB",RequiredFor.NOT,ValueType.GEN);
	public static final String htcBatchSystemQueue			= record("vcell.htc.queue",RequiredFor.NOT,ValueType.GEN);
	public static final String htcLogDir					= record("vcell.htc.logdir",RequiredFor.NOT,ValueType.DIR);
	public static final String htcUser						= record("vcell.htc.user",RequiredFor.NOT,ValueType.GEN);
	public static final String htcPbsHome		 			= record("vcell.htc.pbs.home",RequiredFor.NOT,ValueType.GEN);
	public static final String htcSgeHome		 			= record("vcell.htc.sge.home",RequiredFor.NOT,ValueType.GEN);
	public static final String sgeModulePath				= record("vcell.htc.sge.module",RequiredFor.NOT,ValueType.GEN);
	public static final String pbsModulePath				= record("vcell.htc.pbs.module",RequiredFor.NOT,ValueType.GEN);
			

	public static final String compilerProperty				= record("vcell.c++.compiler",RequiredFor.NOT,ValueType.GEN);
	public static final String linkerProperty				= record("vcell.c++.linker",RequiredFor.NOT,ValueType.GEN);
	public static final String exeOutputProperty			= record("vcell.c++.exeoutput",RequiredFor.NOT,ValueType.GEN);
	public static final String objOutputProperty			= record("vcell.c++.objoutput",RequiredFor.NOT,ValueType.GEN);
	public static final String srcsuffixProperty			= record("vcell.c++.srcsuffix",RequiredFor.NOT,ValueType.GEN);
	public static final String objsuffixProperty			= record("vcell.c++.objectsuffix",RequiredFor.NOT,ValueType.GEN);
	public static final String exesuffixProperty			= record("vcell.c++.exesuffix",RequiredFor.NOT,ValueType.GEN);
	public static final String includeProperty				= record("vcell.c++.include",RequiredFor.NOT,ValueType.GEN);
	public static final String definesProperty				= record("vcell.c++.defines",RequiredFor.NOT,ValueType.GEN);
	public static final String libsProperty					= record("vcell.c++.libs",RequiredFor.NOT,ValueType.GEN);
	public static final String finiteVolumeExecutableProperty = record("vcell.finitevolume.executable",RequiredFor.NOT,ValueType.EXE);

	//
	public static final String sundialsSolverExecutableProperty		= record("vcell.sundialsSolver.executable",RequiredFor.NOT,ValueType.EXE);

	// Smoldyn
	public static final String smoldynExecutableProperty		= record("vcell.smoldyn.executable",RequiredFor.NOT,ValueType.EXE);

	//Stoch properties
	public static final String stochExecutableProperty		= record("vcell.stoch.executable",RequiredFor.NOT,ValueType.EXE);	
	public static final String hybridEMExecutableProperty	= record("vcell.hybridEM.executable",RequiredFor.NOT,ValueType.EXE);
	public static final String hybridMilExecutableProperty	= record("vcell.hybridMil.executable",RequiredFor.NOT,ValueType.EXE);
	public static final String hybridMilAdaptiveExecutableProperty = record("vcell.hybridMilAdaptive.executable",RequiredFor.NOT,ValueType.EXE);

	public static final String visitSmoldynVisitExecutableProperty	= record("vcell.visit.smoldynvisitexecutable",RequiredFor.NOT,ValueType.EXE);
	public static final String visitSmoldynScriptPathProperty		= record("vcell.visit.smoldynscript",RequiredFor.NOT,ValueType.GEN);

	//BioFormats plugin properties

	public static final String bioformatsJarFileName		= record("vcell.bioformatsJarFileName",RequiredFor.NOT,ValueType.GEN);
	public static final String bioformatsClasspath			= record("vcell.bioformatsClasspath",RequiredFor.NOT,ValueType.GEN);
	public static final String bioformatsJarDownloadURL		= record("vcell.bioformatsJarDownloadURL",RequiredFor.NOT,ValueType.URL);

	//
	public static final String databaseThreadsProperty		= record("vcell.databaseThreads",RequiredFor.NOT,ValueType.GEN);
	public static final String exportdataThreadsProperty	= record("vcell.exportdataThreads",RequiredFor.NOT,ValueType.GEN);
	public static final String simdataThreadsProperty		= record("vcell.simdataThreads",RequiredFor.NOT,ValueType.GEN);
	public static final String htcworkerThreadsProperty		= record("vcell.htcworkerThreads",RequiredFor.NOT,ValueType.GEN);

	public static final String databaseCacheSizeProperty	= record("vcell.databaseCacheSize",RequiredFor.NOT,ValueType.GEN);
	public static final String simdataCacheSizeProperty		= record("vcell.simdataCacheSize",RequiredFor.NOT,ValueType.GEN);

	public static final String exportBaseURLProperty		= record("vcell.export.baseURL",RequiredFor.NOT,ValueType.GEN);
	public static final String exportBaseDirProperty		= record("vcell.export.baseDir",RequiredFor.NOT,ValueType.GEN);
	public static final String exportMaxInMemoryLimit		= record("vcell.export.maxInMemoryLimit",RequiredFor.NOT,ValueType.INT);

	public static final String dbDriverName					= record("vcell.server.dbDriverName",RequiredFor.NOT,ValueType.GEN);
	public static final String dbConnectURL					= record("vcell.server.dbConnectURL",RequiredFor.NOT,ValueType.GEN);
	public static final String dbUserid						= record("vcell.server.dbUserid",RequiredFor.NOT,ValueType.GEN);
	public static final String dbPassword					= record("vcell.server.dbPassword",RequiredFor.NOT,ValueType.GEN);

	public static final String jmsProvider				= record("vcell.jms.provider",RequiredFor.NOT,ValueType.GEN);
	public static final String jmsProviderValueActiveMQ		= record("ActiveMQ",RequiredFor.NOT,ValueType.GEN);
	public static final String jmsURL					= record("vcell.jms.url",RequiredFor.NOT,ValueType.GEN);
	public static final String jmsUser					= record("vcell.jms.user",RequiredFor.NOT,ValueType.GEN);
	public static final String jmsPassword				= record("vcell.jms.password",RequiredFor.NOT,ValueType.GEN);

	public static final String jmsSimReqQueue			= record("vcell.jms.queue.simReq",RequiredFor.NOT,ValueType.GEN);
	public static final String jmsDataRequestQueue		= record("vcell.jms.queue.dataReq",RequiredFor.NOT,ValueType.GEN);
	public static final String jmsDbRequestQueue		= record("vcell.jms.queue.dbReq",RequiredFor.NOT,ValueType.GEN);
	public static final String jmsSimJobQueue			= record("vcell.jms.queue.simJob",RequiredFor.NOT,ValueType.GEN);
	public static final String jmsWorkerEventQueue		= record("vcell.jms.queue.workerEvent",RequiredFor.NOT,ValueType.GEN);
	public static final String jmsServiceControlTopic	= record("vcell.jms.topic.serviceControl",RequiredFor.NOT,ValueType.GEN);
	public static final String jmsDaemonControlTopic	= record("vcell.jms.topic.daemonControl",RequiredFor.NOT,ValueType.GEN);
	public static final String jmsClientStatusTopic		= record("vcell.jms.topic.clientStatus",RequiredFor.NOT,ValueType.GEN);

	public static final String jmsBlobMessageMinSize	= record("vcell.jms.blobMessageMinSize",RequiredFor.NOT,ValueType.GEN);
	public static final String jmsBlobMessageTempDir	= record("vcell.jms.blobMessageTempDir",RequiredFor.NOT,ValueType.GEN);
	public static final String vcellClientTimeoutMS 	= record("vcell.client.timeoutMS",RequiredFor.NOT,ValueType.GEN);

	public static final String maxOdeJobsPerUser	= record("vcell.server.maxOdeJobsPerUser",RequiredFor.NOT,ValueType.GEN);
	public static final String maxPdeJobsPerUser	= record("vcell.server.maxPdeJobsPerUser",RequiredFor.NOT,ValueType.GEN);
	public static final String maxJobsPerScan		= record("vcell.server.maxJobsPerScan",RequiredFor.NOT,ValueType.GEN);
	public static final String maxJobsPerSite		= record("vcell.server.maxJobsPerSite",RequiredFor.NOT,ValueType.GEN);
	public static final String limitJobMemoryMB		= record("vcell.limit.jobMemoryMB",RequiredFor.NOT,ValueType.GEN);

	public static final String vcellSoftwareVersion = record("vcell.softwareVersion",RequiredFor.NOT,ValueType.GEN);
	public static final String vcellThirdPartyLicense = record("vcell.thirdPartyLicense",RequiredFor.NOT,ValueType.GEN);

	public static final String vcellServerHost = record("vcell.serverHost",RequiredFor.NOT,ValueType.GEN);

	public static final String vcellSMTPHostName = record("vcell.smtp.hostName",RequiredFor.NOT,ValueType.GEN);
	public static final String vcellSMTPPort = record("vcell.smtp.port",RequiredFor.NOT,ValueType.GEN);
	public static final String vcellSMTPEmailAddress = record("vcell.smtp.emailAddress",RequiredFor.NOT,ValueType.GEN);

	public static final String javaSimulationExecutable = record("vcell.javaSimulation.executable",RequiredFor.NOT,ValueType.EXE);
	public static final String simulationPreprocessor = record("vcell.simulation.preprocessor",RequiredFor.NOT,ValueType.EXE);
	public static final String simulationPostprocessor = record("vcell.simulation.postprocessor",RequiredFor.NOT,ValueType.EXE);

	public final static String mongodbHost						= record("vcell.mongodb.host",RequiredFor.NOT,ValueType.GEN);
	public final static String mongodbPort						= record("vcell.mongodb.port",RequiredFor.NOT,ValueType.GEN);   // default 27017
	public final static String mongodbDatabase					= record("vcell.mongodb.database",RequiredFor.NOT,ValueType.GEN);
	public final static String mongodbLoggingCollection			= record("vcell.mongodb.loggingCollection",RequiredFor.NOT,ValueType.GEN);
	public final static String mongodbThreadSleepMS				= record("vcell.mongodb.threadSleepMS",RequiredFor.NOT,ValueType.GEN);

	public static final String VCellChomboExecutable2D = record("vcell.chombo.executable.2d",RequiredFor.NOT,ValueType.EXE);
	public static final String VCellChomboExecutable3D = record("vcell.chombo.executable.3d",RequiredFor.NOT,ValueType.EXE);

	public static final String amplistorVCellServiceURL = record("vcell.amplistor.vcellserviceurl", RequiredFor.NOT,ValueType.GEN);
	public static final String amplistorVCellServiceUser = record("vcell.amplistor.vcellservice.user", RequiredFor.NOT,ValueType.GEN);
	public static final String amplistorVCellServicePassword = record("vcell.amplistor.vcellservice.password", RequiredFor.NOT,ValueType.GEN);
	
	public static final String installationRoot = record("vcell.installDir",RequiredFor.CLIENT,ValueType.DIR);
	public static final String vcellDownloadDir = record("vcell.downloadDir",RequiredFor.NOT,ValueType.URL);
	public static final String autoflushStandardOutAndErr = record("vcell.autoflushlog",RequiredFor.NOT,ValueType.GEN);
	public static final String suppressQStatStandardOutLogging = record("vcell.htc.logQStatOutput", RequiredFor.NOT, ValueType.BOOL);
	
	private static File systemTemporaryDirectory = null;
	
	public static final String nagiosMonitorPort = record("test.monitor.port", RequiredFor.NOT, ValueType.GEN);

	/**
	 * under which context(s) are we running? 
	 */
	public enum Context {
		/**
		 * not properties required 
		 */
		TEST(0),
		/**
		 * running as server 
		 */
		SERVER(1), //binary 01
		/**
		 * required as Java client
		 */
		CLIENT(2), //binary 10
		/**
		 * running as both  
		 */
		BOTH(3), //binary 11
		;
		private final int bitMap;

		private Context(int bitMap) {
			this.bitMap = bitMap;
		}

	}
	/**
	 * under which context(s) is property required
	 */
	private enum RequiredFor {
		/**
		 * not required
		 */
		NOT(0),
		/**
		 * required for server code
		 */
		SERVER(1),
		/**
		 * required for Java client
		 */
		CLIENT(2),
		/**
		 * required for both server and client
		 */
		BOTH(3),
		;
		private final int bitMap;

		private RequiredFor(int bitMap) {
			this.bitMap = bitMap;
		}

		boolean requiredInContext(Context ctx)  {
			if (ctx != null) {
				return (this.bitMap & ctx.bitMap) != 0;
			}
			return false;

		}
	}


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
		 * in which contexts is this required? 
		 */
		final RequiredFor reqFor;
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

		MetaProp(RequiredFor rf, ValueType valueType) {
			this.reqFor = rf; 
			this.valueType = valueType;
		} 

	}
	private static Context validationContext = Context.TEST;
	/**
	 * has user been nagged about required property not marked required?
	 */
	private static Map<String,Object> nagged = new HashMap<String, Object>();


	/**
	 * * record static String in {@link #propMap}
	 * @param in
	 * @param ctx 
	 * @param vt 
	 * @return in
	 */
	private static String record(String in, RequiredFor ctx, ValueType vt) {
		if (ctx == null || vt == null) {
			throw new Error("PropertyLoader not set correctly");
		}
		propMap.put(in, new MetaProp(ctx,vt));
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
			else if (validationContext != Context.TEST && !propMap.get(propertyName).reqFor.requiredInContext(validationContext) ) {
				System.err.println("Required property " + propertyName + " not marked required for " + validationContext);
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
		loadProperties(Context.TEST, false);
	}

	/**
	 * 
	 * @param ctx context to validate in (may be null)
	 * @param throwException throw exception if validation error
	 * @throws java.io.IOException
	 */
	public final static void loadProperties(Context ctx, boolean throwException) throws java.io.IOException {
		if (ctx != null) {
			validationContext = ctx;
		}

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
		validateSystemProperties(ctx, throwException);

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
	private static boolean validateSystemProperties(Context ctx, boolean throwException) {
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
				if (mp.reqFor.requiredInContext(ctx) ) {
					missingReq.add(entry.getKey());
				}
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
		}
	}
}
