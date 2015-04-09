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

import java.util.Arrays;
import java.util.Vector;


public class PropertyLoader {
	public static final String ADMINISTRATOR_ACCOUNT = "Administrator";
	public static final String ADMINISTRATOR_ID = "2";
	
	public static final String propertyFileProperty			= "vcell.propertyfile";
	public static final String systemTempDirProperty		= "java.io.tmpdir";		// overridden in server launch scripts to ${common_siteRootDir}/tmp
	
	public static final String vcellServerIDProperty        = "vcell.server.id";
	
	public static final String primarySimDataDirProperty	= "vcell.primarySimdatadir";
	public static final String secondarySimDataDirProperty	= "vcell.secondarySimdatadir";
	
	public static final String jobMemoryOverheadMB			= "vcell.htc.jobMemoryOverheadMB";
	public static final String htcBatchSystemQueue			= "vcell.htc.queue";
	public static final String htcLogDir					= "vcell.htc.logdir";
	public static final String htcUser						= "vcell.htc.user";
	public static final String htcPbsHome		 			= "vcell.htc.pbs.home";
	public static final String htcSgeHome		 			= "vcell.htc.sge.home";
	public static final String htcSimulationJobStatusTimeout	= "vcell.htc.htcSimulationJobStatusTimeout";
	public static final String htcSubScriptIncludeFile		= "vcell.htc.htcSubmissionScriptIncludeFile";
	public static final String sgeModulePath                = "vcell.htc.sge.module";
	public static final String pbsModulePath                = "vcell.htc.pbs.module";
	
	public static final String compilerProperty				= "vcell.c++.compiler";
	public static final String linkerProperty				= "vcell.c++.linker";
	public static final String exeOutputProperty			= "vcell.c++.exeoutput";
	public static final String objOutputProperty			= "vcell.c++.objoutput";
	public static final String srcsuffixProperty			= "vcell.c++.srcsuffix";
	public static final String objsuffixProperty			= "vcell.c++.objectsuffix";
	public static final String exesuffixProperty			= "vcell.c++.exesuffix";
	public static final String includeProperty				= "vcell.c++.include";
	public static final String definesProperty				= "vcell.c++.defines";
	public static final String libsProperty					= "vcell.c++.libs";
	public static final String finiteVolumeExecutableProperty = "vcell.finitevolume.executable";
	public static final String ldLibraryPathProperty 		= "vcell.ldLibraryPath";
	//
	public static final String sundialsSolverExecutableProperty		= "vcell.sundialsSolver.executable";
	
	// Smoldyn
	public static final String smoldynExecutableProperty		= "vcell.smoldyn.executable";
	
	//Stoch properties
	public static final String stochExecutableProperty		= "vcell.stoch.executable";	
	public static final String hybridEMExecutableProperty	= "vcell.hybridEM.executable";
	public static final String hybridMilExecutableProperty	= "vcell.hybridMil.executable";
	public static final String hybridMilAdaptiveExecutableProperty = "vcell.hybridMilAdaptive.executable";
	
	public static final String visitSmoldynVisitExecutableProperty	= "vcell.visit.smoldynvisitexecutable";
	public static final String visitSmoldynScriptPathProperty		= "vcell.visit.smoldynscript";
	
	//BioFormats plugin properties
	
	public static final String bioformatsJarFileName		= "vcell.bioformatsJarFileName";
	public static final String bioformatsClasspath			= "vcell.bioformatsClasspath";
	public static final String bioformatsJarDownloadURL		= "vcell.bioformatsJarDownloadURL";
	
	//
	public static final String databaseThreadsProperty		= "vcell.databaseThreads";
	public static final String exportdataThreadsProperty	= "vcell.exportdataThreads";
	public static final String simdataThreadsProperty		= "vcell.simdataThreads";
	public static final String htcworkerThreadsProperty		= "vcell.htcworkerThreads";
	
	public static final String databaseCacheSizeProperty	= "vcell.databaseCacheSize";
	public static final String simdataCacheSizeProperty		= "vcell.simdataCacheSize";
	
	public static final String exportBaseURLProperty		= "vcell.export.baseURL";
	public static final String exportBaseDirProperty		= "vcell.export.baseDir";
	
	public static final String dbDriverName					= "vcell.server.dbDriverName";
	public static final String dbConnectURL					= "vcell.server.dbConnectURL";
	public static final String dbUserid						= "vcell.server.dbUserid";
	public static final String dbPassword					= "vcell.server.dbPassword";

	public static final String jmsProvider				= "vcell.jms.provider";
	public static final String jmsProviderValueActiveMQ		= "ActiveMQ";
	public static final String jmsURL					= "vcell.jms.url";
	public static final String jmsUser					= "vcell.jms.user";
	public static final String jmsPassword				= "vcell.jms.password";
	
	public static final String jmsSimReqQueue			= "vcell.jms.queue.simReq";
	public static final String jmsDataRequestQueue		= "vcell.jms.queue.dataReq";
	public static final String jmsDbRequestQueue		= "vcell.jms.queue.dbReq";
	public static final String jmsSimJobQueue			= "vcell.jms.queue.simJob";
	public static final String jmsWorkerEventQueue		= "vcell.jms.queue.workerEvent";
	public static final String jmsServiceControlTopic	= "vcell.jms.topic.serviceControl";
	public static final String jmsDaemonControlTopic	= "vcell.jms.topic.daemonControl";
	public static final String jmsClientStatusTopic		= "vcell.jms.topic.clientStatus";

	public static final String jmsBlobMessageMinSize	= "vcell.jms.blobMessageMinSize";
	public static final String jmsBlobMessageTempDir	= "vcell.jms.blobMessageTempDir";
	public static final String vcellClientTimeoutMS 	= "vcell.client.timeoutMS";

	public static final String maxOdeJobsPerUser	= "vcell.server.maxOdeJobsPerUser";
	public static final String maxPdeJobsPerUser	= "vcell.server.maxPdeJobsPerUser";
	public static final String maxJobsPerScan		= "vcell.server.maxJobsPerScan";
	public static final String maxJobsPerSite		= "vcell.server.maxJobsPerSite";
	public static final String limitJobMemoryMB		= "vcell.limit.jobMemoryMB";
	
	public static final String vcellSoftwareVersion = "vcell.softwareVersion";
	public static final String vcellThirdPartyLicense = "vcell.thirdPartyLicense";

	public static final String vcellServerHost = "vcell.serverHost";
	
	public static final String vcellSMTPHostName = "vcell.smtp.hostName";
	public static final String vcellSMTPPort = "vcell.smtp.port";
	public static final String vcellSMTPEmailAddress = "vcell.smtp.emailAddress";
	
	public static final String javaSimulationExecutable = "vcell.javaSimulation.executable";
	public static final String simulationPreprocessor = "vcell.simulation.preprocessor";
	public static final String simulationPostprocessor = "vcell.simulation.postprocessor";

	public final static String mongodbHost						= "vcell.mongodb.host";
	public final static String mongodbPort						= "vcell.mongodb.port";   // default 27017
	public final static String mongodbDatabase					= "vcell.mongodb.database";
	public final static String mongodbLoggingCollection			= "vcell.mongodb.loggingCollection";
	public final static String mongodbThreadSleepMS				= "vcell.mongodb.threadSleepMS";
	public static final String autoflushStandardOutAndErr = "vcell.autoflushlog";

	public static final String amplistorVCellServiceURL = "vcell.amplistor.vcellserviceurl";
	public static final String amplistorVCellServiceUser = "vcell.amplistor.vcellservice.user";
	public static final String amplistorVCellServicePassword = "vcell.amplistor.vcellservice.password";

	private static final String SYSTEM_SERVER_PROPERTY_NAMES[] = {
		vcellServerIDProperty,
		primarySimDataDirProperty,
		secondarySimDataDirProperty,
		
		htcPbsHome,
		htcSgeHome,
		jobMemoryOverheadMB,
		htcBatchSystemQueue,
		htcLogDir,
		htcUser,
		htcSimulationJobStatusTimeout,
		htcSubScriptIncludeFile,
		sgeModulePath,
		pbsModulePath,
		
		
		compilerProperty,
		linkerProperty,
		exeOutputProperty,
		objOutputProperty,
		srcsuffixProperty,
		objsuffixProperty,
		exesuffixProperty,
		includeProperty,
		definesProperty,
		libsProperty,
		finiteVolumeExecutableProperty,
		ldLibraryPathProperty,
		
		sundialsSolverExecutableProperty,
		
		smoldynExecutableProperty,
		
		stochExecutableProperty,
		hybridEMExecutableProperty,
		hybridMilExecutableProperty,
		hybridMilAdaptiveExecutableProperty,
		
		visitSmoldynVisitExecutableProperty,
		visitSmoldynScriptPathProperty,
		
		databaseThreadsProperty,
		exportdataThreadsProperty,
		simdataThreadsProperty,
		htcworkerThreadsProperty,
		databaseCacheSizeProperty,
		simdataCacheSizeProperty,
		exportBaseURLProperty,
		exportBaseDirProperty,
		
		dbDriverName,
		dbConnectURL,
		dbUserid,
		dbPassword,
		
		jmsProvider,
		jmsURL,
		jmsUser,
		jmsPassword,
		jmsSimReqQueue,
		jmsDataRequestQueue,
		jmsDbRequestQueue,
		jmsSimJobQueue,
		jmsWorkerEventQueue,
		jmsServiceControlTopic,
		jmsDaemonControlTopic,
		jmsClientStatusTopic,
		
		jmsBlobMessageMinSize,
		jmsBlobMessageTempDir,
		vcellClientTimeoutMS,
		

		maxOdeJobsPerUser,
		maxPdeJobsPerUser,
		maxJobsPerScan,
		maxJobsPerSite,
		limitJobMemoryMB,
		

		vcellSoftwareVersion,

		vcellServerHost,

		vcellSMTPHostName,
		vcellSMTPPort,
		vcellSMTPEmailAddress,
		
		javaSimulationExecutable,
		simulationPreprocessor,
		simulationPostprocessor,
		
		mongodbHost,
		mongodbPort,
		mongodbDatabase,
		mongodbLoggingCollection,
		mongodbThreadSleepMS,

		
		amplistorVCellServiceURL,
		amplistorVCellServiceUser,
		amplistorVCellServicePassword,
	};


/**
 * PropertyLoader constructor comment.
 */
public PropertyLoader() throws Exception {
	loadProperties();
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param propertyName java.lang.String
 */
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
 * This method was created in VisualAge.
 */
public final static void loadProperties() throws java.io.IOException {
	java.util.Properties p = System.getProperties();
	//
	// set up new properties object from file in propertyFilePath
	//

	//
	// if vcell.propertyfile defined (on the command line via -Dvcell.propertyfile=/tmp/vcell.properties)
	//
	String customPropertyFileName = p.getProperty(propertyFileProperty);
	if (customPropertyFileName != null){
		java.io.FileInputStream propFile = new java.io.FileInputStream(customPropertyFileName);
		p.load(propFile);
		propFile.close();
		System.out.println("loaded properties from " + customPropertyFileName + " specified on command-line");
		verifyPropertyFile(customPropertyFileName);
	}else{
		try {
			//
			// look in current working directory first
			//
			String propertyFilePath = "." + p.getProperty("file.separator") + "vcell.properties";
			//System.out.println("PropertyLoader - trying to load properties from "+propertyFilePath+"...");
			java.io.FileInputStream propFile = new java.io.FileInputStream(propertyFilePath);
			p.load(propFile);
			propFile.close();
			System.out.println("loaded properties from " + propertyFilePath);
			verifyPropertyFile(propertyFilePath);
		} catch (java.io.FileNotFoundException e1) {
			try {
				//
				// then look in 'user.home' directory
				//
				String propertyFilePath = p.getProperty("user.home") + p.getProperty("file.separator") + "vcell.properties";
				//System.out.println("PropertyLoader - trying to load properties from "+propertyFilePath+"...");
				java.io.FileInputStream propFile = new java.io.FileInputStream(propertyFilePath);
				p.load(propFile);
				propFile.close();
				System.out.println("loaded properties from " + propertyFilePath);
				verifyPropertyFile(propertyFilePath);
			} catch (java.io.FileNotFoundException e2) {
				//
				// then look in 'java.home' directory
				//
				String propertyFilePath = p.getProperty("java.home") + p.getProperty("file.separator") + "vcell.properties";
				//System.out.println("PropertyLoader - trying to load properties from "+propertyFilePath+"...");
				java.io.FileInputStream propFile = new java.io.FileInputStream(propertyFilePath);
				p.load(propFile);
				propFile.close();
				System.out.println("loaded properties from " + propertyFilePath);
				verifyPropertyFile(propertyFilePath);
			}
		}
	}

	// set the system properties
	System.setProperties(p);
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
 * Insert the method's description here.
 * Creation date: (7/17/01 11:48:36 AM)
 * @param propertyFileName java.lang.String
 */
private static final void verifyPropertyFile(String propertyFileName) {
	try {
		java.util.Properties p = new java.util.Properties();
		java.io.FileInputStream propFile = new java.io.FileInputStream(propertyFileName);
		p.load(propFile);
		propFile.close();

		//Merge System Server and System Client Property Lists
		Vector<String> allListV = new Vector<String>(Arrays.asList(PropertyLoader.SYSTEM_SERVER_PROPERTY_NAMES));
		String[] ALL_PROPERTIES = allListV.toArray(new String[0]);

		//
		// complain if property file has an unknown property
		//
		java.util.Iterator<Object> propNameIterator = p.keySet().iterator();
		while (propNameIterator.hasNext()){
			String propName = (String)propNameIterator.next();
			boolean bFound = false;
			for (int i = 0; i < ALL_PROPERTIES.length; i++){
				if (ALL_PROPERTIES[i].equals(propName)){
					bFound = true;
				}
			}
			if (!bFound){
				System.out.println("<<<ERROR>>> UNKNOWN PROPERTY \""+propName+"\" in property file \""+propertyFileName+"\"");
			}
		}

		//
		// complain if property file is missing a property
		//
		for (int i = 0; i < ALL_PROPERTIES.length; i++){
			if (!p.containsKey(ALL_PROPERTIES[i])){
				System.out.println("<<<WARNING>>> MISSING PROPERTY \""+ALL_PROPERTIES[i]+"\" in property file \""+propertyFileName+"\"");
			}
		}
	}catch (java.io.IOException e){
		e.printStackTrace(System.out);
		System.out.println("Error verifying property file \""+propertyFileName+"\"");
	}
}
}
