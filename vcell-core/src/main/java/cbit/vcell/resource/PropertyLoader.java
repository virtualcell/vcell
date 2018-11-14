/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.ConfigurationException;

import cbit.vcell.mongodb.VCMongoMessage;

public class PropertyLoader {

	//must come before uses of #record method
	private static HashMap<String, MetaProp> propMap = new HashMap<String,MetaProp>( );
	public static final String ADMINISTRATOR_ACCOUNT = "Administrator";
	public static final String ADMINISTRATOR_ID = "2";

	public static final String propertyFileProperty			= "vcell.propertyfile";

	public static final String vcellServerIDProperty        = record("vcell.server.id",ValueType.GEN);

	public static final String simPerUserMemoryLimitFile	= record("vcell.htc.simPerUserMemLimitFile",ValueType.GEN);
	public static final String primarySimDataDirInternalProperty	= record("vcell.primarySimdatadir.internal",ValueType.DIR);
	public static final String secondarySimDataDirInternalProperty	= record("vcell.secondarySimdatadir.internal",ValueType.DIR);
	public static final String primarySimDataDirExternalProperty	= record("vcell.primarySimdatadir.external",ValueType.GEN);
	public static final String secondarySimDataDirExternalProperty	= record("vcell.secondarySimdatadir.external",ValueType.GEN);
	//public static final String PARALLEL_DATA_DIR_INTERNAL		= record("vcell.parallelDatadir.internal",ValueType.DIR);
	public static final String PARALLEL_DATA_DIR_EXTERNAL		= record("vcell.parallelDatadir.external",ValueType.GEN);

	public static final String jobMemoryOverheadMB			= record("vcell.htc.jobMemoryOverheadMB",ValueType.GEN);
	public static final String htcBatchSystemQueue			= record("vcell.htc.queue",ValueType.GEN);
	public static final String htcLogDirExternal				= record("vcell.htc.logdir.external",ValueType.GEN);
	public static final String htcLogDirInternal				= record("vcell.htc.logdir.internal",ValueType.GEN);
	public static final String htcUser						= record("vcell.htc.user",ValueType.GEN);
	public static final String htcPbsHome		 			= record("vcell.htc.pbs.home",ValueType.GEN);
	public static final String htcSgeHome		 			= record("vcell.htc.sge.home",ValueType.GEN);
	public static final String htcNodeList                   = record("vcell.htc.nodelist",ValueType.GEN);
	public static final String slurm_cmd_sbatch				= record("vcell.slurm.cmd.sbatch",ValueType.GEN);
	public static final String slurm_cmd_scancel				= record("vcell.slurm.cmd.scancel",ValueType.GEN);
	public static final String slurm_cmd_sacct				= record("vcell.slurm.cmd.sacct",ValueType.GEN);
	public static final String slurm_cmd_squeue				= record("vcell.slurm.cmd.squeue",ValueType.GEN);
	public static final String slurm_cmd_scontrol			= record("vcell.slurm.cmd.scontrol",ValueType.GEN);
	public static final String slurm_cmd_sinfo				= record("vcell.slurm.cmd.sinfo",ValueType.GEN);
	public static final String slurm_partition				= record("vcell.slurm.partition",ValueType.GEN);
	public static final String slurm_reservation			= record("vcell.slurm.reservation",ValueType.GEN);
	public static final String slurm_partition_pu			= record("vcell.slurm.partitionpu",ValueType.GEN);
	public static final String slurm_reservation_pu			= record("vcell.slurm.reservationpu",ValueType.GEN);
	public static final String slurm_tmpdir					= record("vcell.slurm.tmpdir",ValueType.GEN);
	public static final String slurm_local_singularity_dir	= record("vcell.slurm.local.singularity.dir",ValueType.GEN);
	public static final String slurm_central_singularity_dir= record("vcell.slurm.central.singularity.dir",ValueType.GEN);
	public static final String sgeModulePath				= record("vcell.htc.sge.module",ValueType.GEN);
	public static final String pbsModulePath				= record("vcell.htc.pbs.module",ValueType.GEN);
	public static final String MPI_HOME_INTERNAL		        = record("vcell.htc.mpi.home",ValueType.DIR);
	public static final String MPI_HOME_EXTERNAL		        = record("vcell.htc.mpi.home",ValueType.GEN);
	public static final String nativeSolverDir_External      = record("vcell.nativesolverdir.external",ValueType.GEN);

//	public static final String finiteVolumeExecutableProperty = record("vcell.finitevolume.executable",ValueType.EXE);
//
//	//
//	public static final String sundialsSolverExecutableProperty		= record("vcell.sundialsSolver.executable",ValueType.EXE);
//
//	// Smoldyn
//	public static final String smoldynExecutableProperty		= record("vcell.smoldyn.executable",ValueType.EXE);
//
//	// NFSim
//	public static final String nfsimExecutableProperty		= record("vcell.nfsim.executable",ValueType.EXE);
//
//	public static final String MOVING_BOUNDARY_EXE		= record("vcell.mb.executable",ValueType.EXE);
	
	//Comsol properties
	public static final String comsolRootDir				= record("vcell.comsol.rootdir",ValueType.DIR);
	public static final String comsolJarDir					= record("vcell.comsol.jardir",ValueType.DIR);

	public static final String vcellServerHost				= record("vcell.serverHost",ValueType.GEN);

	//Python properties
	public static final String pythonExe					= record("vcell.python.executable",ValueType.EXE);
	public static final String vcellapiKeystoreFile			= record("vcellapi.keystore.file",ValueType.GEN);
	public static final String vcellapiKeystorePswd			= record("vcellapi.keystore.pswd",ValueType.GEN);
	public static final String vcellapiKeystorePswdFile		= record("vcellapi.keystore.pswdfile",ValueType.GEN);

	//Stoch properties
//	public static final String stochExecutableProperty		= record("vcell.stoch.executable",ValueType.EXE);
//	public static final String hybridEMExecutableProperty	= record("vcell.hybridEM.executable",ValueType.EXE);
//	public static final String hybridMilExecutableProperty	= record("vcell.hybridMil.executable",ValueType.EXE);
//	public static final String hybridMilAdaptiveExecutableProperty = record("vcell.hybridMilAdaptive.executable",ValueType.EXE);

	//BioFormats plugin properties

	public static final String bioformatsJarFileName		= record("vcell.bioformatsJarFileName",ValueType.GEN);
	public static final String bioformatsJarDownloadURL		= record("vcell.bioformatsJarDownloadURL",ValueType.URL);

	//
	public static final String databaseThreadsProperty		= record("vcell.databaseThreads",ValueType.GEN);
	public static final String exportdataThreadsProperty	= record("vcell.exportdataThreads",ValueType.GEN);
	public static final String simdataThreadsProperty		= record("vcell.simdataThreads",ValueType.GEN);
	public static final String htcworkerThreadsProperty		= record("vcell.htcworkerThreads",ValueType.GEN);

	public static final String databaseCacheSizeProperty	= record("vcell.databaseCacheSize",ValueType.GEN);
	public static final String simdataCacheSizeProperty		= record("vcell.simdataCacheSize",ValueType.GEN);

	public static final String exportBaseURLProperty		= record("vcell.export.baseURL",ValueType.GEN);
	public static final String exportBaseDirInternalProperty		= record("vcell.export.baseDir.internal",ValueType.DIR);
	//public static final String exportBaseDirExternalProperty		= record("vcell.export.baseDir.external",ValueType.GEN);
	public static final String exportMaxInMemoryLimit		= record("vcell.export.maxInMemoryLimit",ValueType.INT);

	public static final String dbDriverName					= record("vcell.server.dbDriverName",ValueType.GEN);
	public static final String dbConnectURL					= record("vcell.server.dbConnectURL",ValueType.GEN);
	public static final String dbUserid						= record("vcell.server.dbUserid",ValueType.GEN);
	public static final String dbPasswordValue				= record("vcell.server.dbPassword",ValueType.GEN);
	public static final String dbPasswordFile				= record("vcell.db.pswdfile",ValueType.GEN);

	public static final String jmsIntHostInternal			= record("vcell.jms.int.host.internal",ValueType.GEN);
	public static final String jmsIntPortInternal			= record("vcell.jms.int.port.internal",ValueType.GEN);
	public static final String jmsSimHostInternal			= record("vcell.jms.sim.host.internal",ValueType.GEN);
	public static final String jmsSimPortInternal			= record("vcell.jms.sim.port.internal",ValueType.GEN);
	public static final String jmsSimRestPortInternal		= record("vcell.jms.sim.restport.internal",ValueType.GEN);
	public static final String jmsSimHostExternal			= record("vcell.jms.sim.host.external",ValueType.GEN);
	public static final String jmsSimPortExternal			= record("vcell.jms.sim.port.external",ValueType.GEN);
	public static final String jmsSimRestPortExternal		= record("vcell.jms.sim.restport.external",ValueType.GEN);
	public static final String jmsUser						= record("vcell.jms.user",ValueType.GEN);
	public static final String jmsPasswordValue				= record("vcell.jms.password",ValueType.GEN);
	public static final String jmsPasswordFile				= record("vcell.jms.pswdfile",ValueType.GEN);

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
	public static final String jmsBlobMessageUseMongo	= record("vcell.jms.blobMessageUseMongo",ValueType.GEN);
	public static final String vcellClientTimeoutMS 	= record("vcell.client.timeoutMS",ValueType.GEN);

	public static final String maxOdeJobsPerUser	= record("vcell.server.maxOdeJobsPerUser",ValueType.GEN);
	public static final String maxPdeJobsPerUser	= record("vcell.server.maxPdeJobsPerUser",ValueType.GEN);
	public static final String maxJobsPerScan		= record("vcell.server.maxJobsPerScan",ValueType.GEN);
	//public static final String maxJobsPerSite		= record("vcell.server.maxJobsPerSite",ValueType.GEN);
//	public static final String limitJobMemoryMB		= record("vcell.limit.jobMemoryMB",ValueType.GEN);

	public static final String vcellSoftwareVersion = record("vcell.softwareVersion",ValueType.GEN);
	public static final String vcellThirdPartyLicense = record("vcell.thirdPartyLicense",ValueType.GEN);

	public static final String vcellSMTPHostName = record("vcell.smtp.hostName",ValueType.GEN);
	public static final String vcellSMTPPort = record("vcell.smtp.port",ValueType.GEN);
	public static final String vcellSMTPEmailAddress = record("vcell.smtp.emailAddress",ValueType.GEN);

	public static final String vcellbatch_docker_name = record("vcell.batch.docker.name",ValueType.GEN);
	public static final String vcellbatch_singularity_image = record("vcell.batch.singularity.image",ValueType.GEN);
	public static final String javaSimulationExecutable = record("vcell.javaSimulation.executable",ValueType.GEN);
	public static final String simulationPreprocessor = record("vcell.simulation.preprocessor",ValueType.GEN);
	public static final String simulationPostprocessor = record("vcell.simulation.postprocessor",ValueType.GEN);

	public final static String mongodbHostInternal				= record("vcell.mongodb.host.internal",ValueType.GEN);
	public final static String mongodbPortInternal				= record("vcell.mongodb.port.internal",ValueType.GEN);   // default 27017
	public final static String mongodbHostExternal				= record("vcell.mongodb.host.external",ValueType.GEN);
	public final static String mongodbPortExternal				= record("vcell.mongodb.port.external",ValueType.GEN);   // default 27017
	public final static String mongodbDatabase					= record("vcell.mongodb.database",ValueType.GEN);
	public final static String mongodbLoggingCollection			= record("vcell.mongodb.loggingCollection",ValueType.GEN);
	public final static String mongodbThreadSleepMS				= record("vcell.mongodb.threadSleepMS",ValueType.GEN);

//	public static final String VCellChomboExecutable2D = record("vcell.chombo.executable.2d",ValueType.EXE);
//	public static final String VCellChomboExecutable3D = record("vcell.chombo.executable.3d",ValueType.EXE);

	public static final String amplistorVCellServiceURL = record("vcell.amplistor.vcellserviceurl",ValueType.GEN);
	public static final String amplistorVCellServiceUser = record("vcell.amplistor.vcellservice.user",ValueType.GEN);
	public static final String amplistorVCellServicePassword = record("vcell.amplistor.vcellservice.password",ValueType.GEN);

	public static final String installationRoot = record("vcell.installDir",ValueType.DIR);
	public static final String vcellDownloadDir = record("vcell.downloadDir",ValueType.URL);
	public static final String autoflushStandardOutAndErr = record("vcell.autoflushlog",ValueType.GEN);
	public static final String suppressQStatStandardOutLogging = record("vcell.htc.logQStatOutput", ValueType.BOOL);
	
	public static final String nagiosMonitorPort = record("test.monitor.port", ValueType.GEN);
	/**
	 * native library directory, server side
	 */
	public static final String NATIVE_LIB_DIR = record("vcell.lib", ValueType.DIR);

	private static File systemTemporaryDirectory = null;
	private static Logger lg = LogManager.getLogger(PropertyLoader.class);

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
		/**
		 * is property required?
		 */
		boolean required = false;
		/**
		 * was message sent to mongo?
		 */
		boolean sentToMongo = false;
		/**
		 * stored message for sending later
		 */
		String message = null;

		MetaProp(ValueType valueType) {
			this.valueType = valueType;
		}

	}
	private static boolean errorsToMongo = false;
	private static boolean checkRequired = false;

	public static void sendErrorsToMongo( )  {
		if (errorsToMongo == false) {
			for (Entry<String, MetaProp> entry  : propMap.entrySet()) {
				MetaProp mp = entry.getValue();
				if (mp.message  != null && mp.sentToMongo == false) {
					VCMongoMessage.sendInfo(mp.message);
					mp.sentToMongo = true;
				}
			}
			errorsToMongo = true;
		}
	}

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

	public static final String USE_CURRENT_WORKING_DIRECTORY = "cwd";

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
	 * @param defaultValue may not null
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
	 * set to mongo if {@link #errorsToMongo} is true, or Standard out and save message for later
	 * @param prop not null
	 * @param message not null
	 */
	private static void report(MetaProp prop, String message) {
		if (errorsToMongo) {
			if (!prop.sentToMongo) {
				VCMongoMessage.sendInfo(message);
				prop.sentToMongo = true;
			}
			return;
		}

		//else

		if (prop.message == null) {
			prop.message = message;
			System.err.println(message);
		}
	}

	/**
	 * return file from property. If property value is {@value #USE_CURRENT_WORKING_DIRECTORY}, return current working directory
	 * @param propertyName
	 * @return File object
	 * @throws ConfigurationException
	 */
	public final static File getRequiredDirectory(String propertyName) throws ConfigurationException {
		String directoryString = getRequiredProperty(propertyName);
		if (!directoryString.toLowerCase().equals(USE_CURRENT_WORKING_DIRECTORY) ) {
			return new File(directoryString);
		}
		File cwd = Paths.get("").toAbsolutePath().toFile();
		if (!cwd.isDirectory()) {
			throw new ConfigurationException("PropertyLoader::getRequiredDirectory failed to read directory from current working directory " + cwd);
		}
		return cwd;
	}
	
	public final static File getOptionalDirectory(String propertyName) throws ConfigurationException {
		String directoryString = getProperty(propertyName,null);
		if (directoryString==null){
			return null;
		}
		if (!directoryString.toLowerCase().equals(USE_CURRENT_WORKING_DIRECTORY) ) {
			return new File(directoryString);
		}
		File cwd = Paths.get("").toAbsolutePath().toFile();
		if (!cwd.isDirectory()) {
			throw new ConfigurationException("PropertyLoader::getOptionalDirectory failed to read directory from current working directory " + cwd);
		}
		return cwd;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 * @param propertyName java.lang.String
	 */
	public final static synchronized String getRequiredProperty(String propertyName) throws ConfigurationException {
		if (checkRequired) {
			MetaProp mp = propMap.get(propertyName);
			if (mp != null) {
				if (!mp.sentToMongo && !mp.required) {
					report(mp, "PROPERTY: " + propertyName + " not marked required");
				}
			}
			else {
				mp = new MetaProp(ValueType.GEN);
				propMap.put(propertyName,mp);
				report(mp, "PROPERTY: required property " + propertyName + " not mapped");
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
	public final static void loadProperties(String[] required) throws java.io.IOException {
		loadProperties();
		validateSystemProperties(required);
	}

	@Deprecated
	public final static void loadProperties(boolean throwException, boolean validate) throws java.io.IOException {
		loadProperties();
	}
	/**
	 * default; no validation of required
	 * @throws java.io.IOException
	 */
	public final static void loadProperties() throws java.io.IOException {

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
			lookForMagic(propertyFile);
		}
		else {
			if (lg.isWarnEnabled()) {
				lg.warn("Can't read propertyFile " + propertyFile.getAbsolutePath() + " specified by " + where);
			}
			
		}
		// display new properties
		//System.getProperties().list(System.out);
		System.out.println("ServerID=" + getProperty(vcellServerIDProperty,"unknown")+", SoftwareVersion="+getProperty(vcellSoftwareVersion,"unknown"));
	}

	/**
	 * keyword for property substitution
	 */
	private static final String MAGIC_KEYWORD = "magic";
	private static void lookForMagic(File pfile) {
		try {
			List<String> lines = org.apache.commons.io.FileUtils.readLines(pfile);
			for (String line: lines) {
				if (line.indexOf('#') == 0 && StringUtils.containsIgnoreCase(line, MAGIC_KEYWORD) ) {
					StringTokenizer st = new StringTokenizer(line);
					while (st.hasMoreTokens()) {
						String token = st.nextToken().toLowerCase();
						if (!token.endsWith(MAGIC_KEYWORD)) {
							continue;
						}
						try {
							String os = st.nextToken();
							String from = st.nextToken();
							String to = st.nextToken();
							executeSubstitution(ValueType.DIR, os, from, to);
						} catch (NoSuchElementException e) {
							lg.warn("Unable to parse magic line " + line,e);
						}
					}
				}
			}
		} catch (Exception e) {
			lg.warn("failure looking for magic",e);
		}
	}

	private static void executeSubstitution(ValueType type, String os, String from, String to) {
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance();
		if (os.contains( osi.getOsnamePrefix() ) ) {
			for (Entry<String, MetaProp> es: propMap.entrySet()) {
				MetaProp mp = es.getValue();
				if (mp.valueType == type) {
					String key = es.getKey();
					String current = System.getProperty(key);
					if (current!= null && current.contains(from)) {
						String substitute = current.replace(from, to);
						System.setProperty(key, substitute);
						if (lg.isInfoEnabled()) {
							lg.info("replaced " + key + " value " + current + " with " + substitute);
						}

					}
				}
			}
		}
	}

	/**
	 * This method was created in VisualAge.
	 */
	public final static void show() {
		System.getProperties().list(System.out);
	}

	/***
	 * check system properties against expected
	 * @param required array of required properties
	 */
	private static void validateSystemProperties(String[] required) {
		checkRequired = true;
		Properties p = System.getProperties();

		for (Object propName : p.keySet()) {
			if (propMap.containsKey(propName)) {
				propMap.get(propName).set = true;
			}
		}

		StringBuffer validationReport = new StringBuffer();
		for (String propName: required) {
			MetaProp meta = propMap.get(propName);
			if (meta != null) {
				if (meta.set) {
					verifyEntry(propName,meta,validationReport);
				}
				else {
					validationReport.append("Process required property " + propName + " not set\n");
				}
				meta.required = true;
			}
			else {
				validationReport.append("Process required property " + propName + " not mapped\n");
			}
		}
		if (validationReport.length( ) > 0) {
			throw new IllegalStateException(validationReport.toString());
		}
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
	
	public static String getSecretValue(String secretValueProperty, String secretFileProperty) {
        String secretValue = PropertyLoader.getProperty(secretValueProperty, null);
        String secretFile = PropertyLoader.getProperty(secretFileProperty, null);
        final String secret;
        if (secretValue!=null) {
        		secret = secretValue;
        }else if (secretFile!=null && new File(secretFile).exists()) {
        		try {
				secret = FileUtils.readFileToString(new File(secretFile)).trim();
			} catch (IOException e) {
				throw new IllegalArgumentException("failed to parse "+secretFileProperty+" in "+new File(secretFile).getAbsolutePath());
			}
        }else {
        		throw new IllegalArgumentException("expecting either "+secretValueProperty+" or "+secretFileProperty+" properties set");
        }
        return secret;
	}

	/**
	 * verify entry based on properties
	 * @param entry to verify
	 * @param report destination to report discrepancies
	 * @param fileSet is set from file? (for formatting messages)
	 */
	//private static void verifyEntry( Map.Entry<String,MetaProp> entry, StringBuffer report, boolean fileSet)  {
	private static void verifyEntry(String name, MetaProp meta, StringBuffer report)  {
		ValueType vt = meta.valueType;
		if (vt == ValueType.GEN) {
			return;
		}
	    final boolean fileSet = meta.fileSet;
		String value = System.getProperty(name);
		switch (vt) {
		case GEN:
			assert false : "don't go here";
		return;
		case DIR:
		{
			File f = new File(value);
			if (!f.isDirectory() && !value.toLowerCase( ).equals(USE_CURRENT_WORKING_DIRECTORY)) {
				report.append(name + " value " + value + fromFile(fileSet) + " is not a directory\n");
			}
			return;
		}
		case EXE:
		{
			File f = new File(value);
			if (!f.canExecute()) {
				report.append(name + " value " + value + fromFile(fileSet) + " is not executable\n");
			}
			return;
		}
		case INT:
			try {
				Long.parseLong(value);
			} catch (NumberFormatException e) {
				report.append(name + " value " + value + fromFile(fileSet) + " not  convertible to long integer\n");
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
