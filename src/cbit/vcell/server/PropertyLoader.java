package cbit.vcell.server;



/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
public class PropertyLoader {
	public static final String ADMINISTRATOR_ACCOUNT = "Administrator";

	public static final String propertyFileProperty			= "vcell.propertyfile";
	
	public static final String vcellServerIDProperty        = "vcell.server.id";
	
	public static final String tempDirProperty				= "vcell.tempdir";
	public static final String primarySimDataDirProperty	= "vcell.primarySimdatadir";
	public static final String secondarySimDataDirProperty	= "vcell.secondarySimdatadir";
	public static final String localSimDataDirProperty		= "vcell.localSimdatadir";
	public static final String serviceSubmitScript = "vcell.service.submitScript";
	
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
	
	public static final String optLibsProperty				= "vcell.opt.libs";
	public static final String optIncludeProperty			= "vcell.opt.include";
	//
	public static final String idaLibraryProperty			= "vcell.ida.library";
	public static final String idaIncludeProperty			= "vcell.ida.include";
	public static final String idaExecutableProperty		= "vcell.ida.executable";
	public static final String cvodeExecutableProperty		= "vcell.cvode.executable";
	
	//Stoch properties
	public static final String stochLibraryProperty			= "vcell.stoch.library";
	public static final String stochIncludeProperty			= "vcell.stoch.include";
	public static final String stochExecutableProperty		= "vcell.stoch.executable";	
	public static final String hybridEMExecutableProperty	= "vcell.hybridEM.executable";
	public static final String hybridMilExecutableProperty	= "vcell.hybridMil.executable";
	public static final String hybridMilAdaptiveExecutableProperty = "vcell.hybridMilAdaptive.executable";
	//
	public static final String corbaEnabled					= "vcell.corbaEnabled";
	public static final String databaseCacheSizeProperty	= "vcell.databaseCacheSize";
	public static final String simdataCacheSizeProperty		= "vcell.simdataCacheSize";
	public static final String numProcessorsProperty		= "vcell.numProcessors";
	public static final String maxJavaMemoryBytesProperty	= "vcell.maxJavaMemoryBytes";
	public static final String serverStatisticsProperty		= "vcell.serverStatistics";
	public static final String exportBaseURLProperty		= "vcell.export.baseURL";
	public static final String exportBaseDirProperty		= "vcell.export.baseDir";
	public static final String exportUseLocalDataServer		= "vcell.export.useLocalDataServer";
	public static final String userGuideURLProperty			= "vcell.help.userGuideURL";	
	public static final String tutorialURLProperty			= "vcell.help.tutorialURL";
	
	public static final String dbDriverName					= "vcell.server.dbDriverName";
	public static final String dbConnectURL					= "vcell.server.dbConnectURL";
	public static final String dbUserid						= "vcell.server.dbUserid";
	public static final String dbPassword					= "vcell.server.dbPassword";
	public static final String dbPoolTimeoutSec				= "vcell.server.dbPoolTimeoutSec";

	public static final String vcmlSchemaUrlProperty		= "vcell.xml.vcmlSchemaUrl";
	public static final String sbml1SchemaUrlProperty		= "vcell.xml.sbml1SchemaUrl";
	public static final String sbml2SchemaUrlProperty		= "vcell.xml.sbml2SchemaUrl";
	public static final String cellmlSchemaUrlProperty      = "vcell.xml.cellmlSchemaUrl"; 
	public static final String triangleCmdProperty			= "vcell.mesh.trianglecmd";
	
	public static final String dataSetCrawlerEnabled		= "vcell.dataSetCrawlerEnabled";
	public static final String dataSetCrawlerIntervalMinutes= "vcell.dataSetCrawlerIntervalMinutes";
	public static final String simDataServerHost			= "vcell.simDataServerHost";
	public static final String odeComputeServerHosts		= "vcell.odeComputeServerHosts";   // comma-separated list of hosts
	public static final String pdeComputeServerHosts		= "vcell.pdeComputeServerHosts";   // comma-separated list of hosts
	public static final String hostSeparator	= ",";
	//added by RB, to accomodate some experiment-related stuff
	public static final String expSchemaUrlProperty = "vcell.experiment.expSchemaUrl";
	public static final String expXMLRep = "vcell.experiment.expXMLRep";
	public static final String expLoadDump = "vcell.experiment.expLoadDump";

	public static final String jmsProvider				= "vcell.jms.provider";
	public static final String jmsURL					= "vcell.jms.url";
	public static final String jmsUser					= "vcell.jms.user";
	public static final String jmsPassword				= "vcell.jms.password";
	public static final String jmsFileChannelQueue		= "vcell.jms.queue.fileChannel";
	public static final String jmsSimReqQueue			= "vcell.jms.queue.simReq";
	public static final String jmsDataRequestQueue		= "vcell.jms.queue.dataReq";
	public static final String jmsDbRequestQueue		= "vcell.jms.queue.dbReq";
	public static final String jmsBNGRequestQueue		= "vcell.jms.queue.bngReq";
	public static final String jmsSimJobQueue			= "vcell.jms.queue.simJob";
	public static final String jmsWorkerEventQueue		= "vcell.jms.queue.workerEvent";
	public static final String jmsWorkerPrefetchCount	= "vcell.jms.workerPrefetchCount";
	public static final String jmsServicePrefetchCount	= "vcell.jms.servicePrefetchCount";

	public static final String maxOdeJobsPerUser	= "vcell.server.maxOdeJobsPerUser";
	public static final String maxPdeJobsPerUser	= "vcell.server.maxPdeJobsPerUser";
	public static final String maxJobsPerScan	= "vcell.server.maxJobsPerScan";
	
	public static final String jmsServiceControlTopic	= "vcell.jms.topic.serviceControl";
	public static final String jmsDaemonControlTopic	= "vcell.jms.topic.daemonControl";
	public static final String jmsClientStatusTopic		= "vcell.jms.topic.clientStatus";

	public static final String rmiPortAdminDbServer			= "vcell.rmi.port.adminDbServer";
	public static final String rmiPortDataSetController		= "vcell.rmi.port.dataSetController";
	public static final String rmiPortSimulationController	= "vcell.rmi.port.simulationController";
	public static final String rmiPortSolverController		= "vcell.rmi.port.solverController";
	public static final String rmiPortUserMetaDbServer		= "vcell.rmi.port.userMetaDbServer";
	public static final String rmiPortVCellBootstrap		= "vcell.rmi.port.vcellBootstrap";
	public static final String rmiPortVCellConnection		= "vcell.rmi.port.vcellConnection";
	public static final String rmiPortVCellServer			= "vcell.rmi.port.vcellServer";
	public static final String rmiPortMessageHandler		= "vcell.rmi.port.messageHandler";
	public static final String rmiPortRegistry				= "vcell.rmi.port.registry";
	public static final String rmiPortBNGService			= "vcell.rmi.port.bngService";

	public static final String serverManageConfig = "vcell.messaging.serverManagerConfig";
	public static final String bootstrapConfig = "vcell.messaging.bootstrapConfig";

	public static final String vcellSoftwareVersion = "vcell.softwareVersion";

	public static final String vcellServerHost = "vcell.serverHost";

	public static final String vcellAnonymizerBootstrapPropertyFile = "vcell.anonymizer.bootstrap.propertyFile";
	public static final String vcellAnonymizerBootstrapLogfile = "vcell.anonymizer.bootstrap.logfile";
	public static final String vcellAnonymizerBootstrapLocalHost = "vcell.anonymizer.bootstrap.localHost";
	public static final String vcellAnonymizerBootstrapLocalPort = "vcell.anonymizer.bootstrap.localPort";	
	public static final String vcellAnonymizerBootstrapRemoteHost = "vcell.anonymizer.bootstrap.remoteHost";
	public static final String vcellAnonymizerBootstrapRemotePort = "vcell.anonymizer.bootstrap.remotePort";
	
	public static final String vcellBNGPerl = "vcell.bng.perl.executable";
	public static final String vcellBNGScript = "vcell.bng.script";

	public static final String vcellClientTimeoutMS = "vcell.client.timeoutMS";
	
	public static final String lsfJobQueue = "vcell.lsf.jobQueue";
	
	public static final String htcSubmitTemplates = "vcell.htc.submittemplates";		
	public static final String htcPartitionMaximumJobs = "vcell.htc.partition.maximumJobs";
	public static final String htcPartitionShareServerIDs = "vcell.htc.partition.shareServerIDs";
	public static final String htcComputeResources = "vcell.htc.computeresources";
	
	public static final String pbsHomeDir = "vcell.pbs.homeDir";
	
	public static final String limitJobMemoryMB="vcell.limit.jobMemoryMB";
	
	public static final String vcellSMTPHostName = "vcell.smtp.hostName";
	public static final String vcellSMTPPort = "vcell.smtp.port";
	public static final String vcellSMTPEmailAddress = "vcell.smtp.emailAddress";
	
	public static final String pslidAllProteinListURL = "vcell.pslid.allProteinListURL";
	public static final String pslidCellProteinListExpURL = "vcell.pslid.cellProteinListExpURL";
	public static final String pslidCellProteinListGenURL = "vcell.pslid.cellProteinListGenURL";
	public static final String pslidCellProteinImageInfoExpURL = "vcell.pslid.cellProteinImageInfoExpURL";
	public static final String pslidCellProteinImageInfoGenURL = "vcell.pslid.cellProteinImageInfoGenURL";
	public static final String pslidCellProteinImageExpURL = "vcell.pslid.cellProteinImageExpURL";
	public static final String pslidCellProteinImageGenURL_1 = "vcell.pslid.cellProteinImageGenURL_1";
	public static final String pslidCellProteinImageGenURL_2 = "vcell.pslid.cellProteinImageGenURL_2";

	private static final String ALL_PROPERTIES[] = {
		vcellServerIDProperty,
		tempDirProperty,
		primarySimDataDirProperty,
		secondarySimDataDirProperty,
		localSimDataDirProperty,
		serviceSubmitScript,
		
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
		
		optLibsProperty,
		optIncludeProperty,
		
		idaLibraryProperty,
		idaIncludeProperty,
		idaExecutableProperty,
		cvodeExecutableProperty,
		
		stochLibraryProperty,
		stochIncludeProperty,
		stochExecutableProperty,
		hybridEMExecutableProperty,
		hybridMilExecutableProperty,
		hybridMilAdaptiveExecutableProperty,
		
		corbaEnabled,
		databaseCacheSizeProperty,
		simdataCacheSizeProperty,
		numProcessorsProperty,
		maxJavaMemoryBytesProperty,
		serverStatisticsProperty,
		exportBaseURLProperty,
		exportBaseDirProperty,
		exportUseLocalDataServer,
		userGuideURLProperty,
		tutorialURLProperty,
		
		dbDriverName,
		dbConnectURL,
		dbUserid,
		dbPassword,
		dbPoolTimeoutSec,
		
		vcmlSchemaUrlProperty,
		sbml1SchemaUrlProperty,
		sbml2SchemaUrlProperty,
		cellmlSchemaUrlProperty,
		triangleCmdProperty,
		dataSetCrawlerEnabled,
		dataSetCrawlerIntervalMinutes,
		simDataServerHost,
		odeComputeServerHosts,
		pdeComputeServerHosts,
		expSchemaUrlProperty,
		expXMLRep,
		expLoadDump,
		
		jmsProvider,
		jmsURL,
		jmsUser,
		jmsPassword,
		jmsFileChannelQueue,
		jmsSimReqQueue,
		jmsDataRequestQueue,
		jmsDbRequestQueue,
		jmsBNGRequestQueue,
		jmsSimJobQueue,
		jmsWorkerEventQueue,
		jmsWorkerPrefetchCount,
		jmsServicePrefetchCount,

		maxOdeJobsPerUser,
		maxPdeJobsPerUser,
		maxJobsPerScan,
		
		jmsServiceControlTopic,
		jmsDaemonControlTopic,
		jmsClientStatusTopic,
		serverManageConfig,
		bootstrapConfig,

		vcellSoftwareVersion,

		vcellServerHost,

		rmiPortAdminDbServer,
		rmiPortDataSetController,
		rmiPortSimulationController,
		rmiPortSolverController,
		rmiPortUserMetaDbServer,
		rmiPortVCellBootstrap,
		rmiPortVCellConnection,
		rmiPortVCellServer,
		rmiPortMessageHandler,
		rmiPortRegistry,
		rmiPortBNGService,

		vcellAnonymizerBootstrapLogfile,
		vcellAnonymizerBootstrapLocalHost,
		vcellAnonymizerBootstrapLocalPort,
		vcellAnonymizerBootstrapRemoteHost,
		vcellAnonymizerBootstrapRemotePort,

		vcellBNGPerl,
		vcellBNGScript,

		vcellClientTimeoutMS,
		
		lsfJobQueue,
		
		htcSubmitTemplates,		
		htcComputeResources,
		htcPartitionMaximumJobs,
		htcPartitionShareServerIDs,		
		pbsHomeDir,
		
		limitJobMemoryMB,
		
		vcellSMTPHostName,
		vcellSMTPPort,
		vcellSMTPEmailAddress,
				
		pslidAllProteinListURL,
		pslidCellProteinListExpURL,	
		pslidCellProteinListGenURL,
		pslidCellProteinImageInfoExpURL,
		pslidCellProteinImageInfoGenURL,
		pslidCellProteinImageExpURL,
		pslidCellProteinImageGenURL_1,
		pslidCellProteinImageGenURL_2
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
 * Insert the method's description here.
 * Creation date: (6/8/2006 12:29:59 PM)
 */
public static void loadAnonymizerProperties() {
	try {
		java.util.Properties sysProperties = new java.util.Properties(System.getProperties());
		
		String customPropertyFileName = sysProperties.getProperty(PropertyLoader.vcellAnonymizerBootstrapPropertyFile);		

		java.io.InputStream propInput = null;		
		if (customPropertyFileName != null && new java.io.File(customPropertyFileName).exists()){
			System.out.println("property file [" + customPropertyFileName + "] is specified on the command line");
			propInput = new java.io.FileInputStream(customPropertyFileName);
		} else {
			System.out.println("property file is not specified on the command line, or it doesn't exist.");
			customPropertyFileName = "Resource[/cbit/vcell/anonymizer/anonymizer.properties.txt]";
			java.net.URL propURL = PropertyLoader.class.getResource("/cbit/vcell/anonymizer/anonymizer.properties.txt");
			propInput = propURL.openConnection().getInputStream();
			System.out.println("trying to use the property file in the jar file [/cbit/vcell/anonymizer/anonymizer.properties.txt]");		
		}
		
		sysProperties.load(propInput);
		propInput.close();
		System.out.println("loaded properties from " + customPropertyFileName);

		System.setProperties(sysProperties);
		
	} catch (Exception ex) {
		ex.printStackTrace();
	}	
}


/**
 * This method was created in VisualAge.
 */
public final static void loadProperties() throws java.io.IOException {
	java.util.Properties p = new java.util.Properties(System.getProperties());
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
	System.out.println("ServerID=" + cbit.vcell.messaging.db.VCellServerID.getSystemServerID()+", SoftwareVersion="+getRequiredProperty(vcellSoftwareVersion));

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