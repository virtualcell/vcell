package cbit.vcell.modeldb;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.KeyFactory;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.VersionableType;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.model.BioModelVisitor;
import cbit.vcell.model.VCMultiBioVisitor;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlParseException;
/**
 * Insert the type's description here.
 * Creation date: (2/2/01 2:57:33 PM)
 * @author: Jim Schaff
 */
public class VCDatabaseScanner {
	/**
	 * special user id meaning all users
	 */
	public static final String ALL_USERS = "-all";
	protected DatabaseServerImpl dbServerImpl = null;
	protected SessionLog log = null;
	private LocalAdminDbServer localAdminDbServer = null;
	protected ConnectionFactory connFactory;
	private User[] allUsers = null;

	

	/**
	 * create scanner with Session logging to standard out
	 * @return new scanner
	 * @throws Exception
	 */
public static VCDatabaseScanner createDatabaseScanner() throws Exception{
	return createDatabaseScanner(new cbit.vcell.resource.StdoutSessionLog("Admin"));
}

/**
 * create database scanner with specified log
 * @param log
 * @return new scanner 
 * @throws Exception
 */
public static VCDatabaseScanner createDatabaseScanner(SessionLog log) throws Exception{
	
	new PropertyLoader();
		
	DatabasePolicySQL.bSilent = true;
	DatabasePolicySQL.bAllowAdministrativeAccess = true;
	
	ConnectionFactory conFactory = DatabaseService.getInstance().createConnectionFactory(log);
	KeyFactory keyFactory = DatabaseService.getInstance().createKeyFactory();
	DbDriver.setKeyFactory(keyFactory);

	VCDatabaseScanner databaseScanner = new VCDatabaseScanner(conFactory, keyFactory, log);
	
	return databaseScanner;
}

/**
 * copy constructor
 * @param rhs
 * @throws Exception
 */
public VCDatabaseScanner(VCDatabaseScanner rhs) throws Exception{
	dbServerImpl = rhs.dbServerImpl;
	log = rhs.log;
	localAdminDbServer = rhs.localAdminDbServer;
	allUsers = rhs.allUsers;
	connFactory = rhs.connFactory;
}

/**
 * default constructor
 * same code as {@link #createDatabaseScanner()}
 * @throws Exception
 */
public VCDatabaseScanner() throws Exception{
	this(createDatabaseScanner());
}

/**
 * constructor with specified log
 * same code as {@link #createDatabaseScanner()}
 * @throws Exception
 */
public VCDatabaseScanner(SessionLog log) throws Exception{
	this(createDatabaseScanner(log));
}

/**
 * ResultSetCrawler constructor comment.
 * @throws RemoteException 
 */
private VCDatabaseScanner(ConnectionFactory argConFactory, KeyFactory argKeyFactory, SessionLog argSessionLog) throws DataAccessException, SQLException, RemoteException {
	this.connFactory = argConFactory;
	this.log = argSessionLog;
	this.localAdminDbServer = new LocalAdminDbServer(argConFactory, argKeyFactory, argSessionLog);
	this.dbServerImpl = new DatabaseServerImpl(argConFactory,argKeyFactory,argSessionLog);
}

public User[] getAllUsers() throws DataAccessException{
	if (allUsers==null){
		UserInfo[] allUserInfos = localAdminDbServer.getUserInfos();
		allUsers = new User[allUserInfos.length];
		for (int i = 0; i < allUserInfos.length; i++) {
			allUsers[i] = new User(allUserInfos[i].userid,allUserInfos[i].id);
		}
	}
	return allUsers;
}

public User getUser(String userName) throws DataAccessException{
	User u = localAdminDbServer.getUser(userName);
	return u; 
}
/**
 * Insert the method's description here.
 * Creation date: (2/2/01 3:40:29 PM)
 * @throws DataAccessException 
 * @throws XmlParseException 
 */
public void scanBioModels(VCDatabaseVisitor databaseVisitor, PrintStream logFilePrintStream, User users[], KeyValue singleModelKey, HashSet<KeyValue> includeHash, HashSet<KeyValue> excludeHash, boolean bAbortOnDataAccessException) throws DataAccessException, XmlParseException {
	if (users==null){
		users = getAllUsers();
	}
	try
	{
		//start visiting models and writing log
		logFilePrintStream.println("Start scanning bio-models......");
		logFilePrintStream.println("\n");
		
		//adapter for verifyMathDescriptionsUnchanged
		PrintWriter logFilePrintWriter = new PrintWriter(logFilePrintStream);
		
		for (int i=0;i<users.length;i++)
		{
			User user = users[i];
			BioModelInfo bioModelInfos[] = dbServerImpl.getBioModelInfos(user,false);
			for (int j = 0; j < bioModelInfos.length; j++){
				if (singleModelKey!=null && !bioModelInfos[j].getVersion().getVersionKey().compareEqual(singleModelKey)){
					System.out.println("skipping biomodel, not the single one that we wanted");
					continue;
				}
				if (excludeHash!=null && excludeHash.contains(bioModelInfos[j].getVersion().getVersionKey())){
					System.out.println("skipping bioModel with key '"+bioModelInfos[j].getVersion().getVersionKey()+"'");
					continue;
				}
				if (includeHash!=null && !includeHash.contains(bioModelInfos[j].getVersion().getVersionKey())){
					System.out.println("not including bioModel with key '"+bioModelInfos[j].getVersion().getVersionKey()+"'");
					continue;
				}
				if (!databaseVisitor.filterBioModel(bioModelInfos[j])){
					continue;
				}
				try {
					BigString bioModelXML = dbServerImpl.getBioModelXML(user, bioModelInfos[j].getVersion().getVersionKey());
					BioModel bioModel = cbit.vcell.xml.XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
					bioModel.refreshDependencies();
					logFilePrintStream.println("---- " + (j+1) + " ----> " + bioModel.getName());    //  + bioModelInfos[j].getVersion().getName() + " -----> ");
					databaseVisitor.visitBioModel(bioModel,logFilePrintStream);
//					verifyMathDescriptionsUnchanged(bioModel, logFilePrintWriter);
				}catch (Exception e2){
					log.exception(e2);
					logFilePrintStream.println("======= " + e2.getMessage());
					if (bAbortOnDataAccessException){
						throw e2;
					}
				}
			}
		}
		
		logFilePrintStream.close();
	}catch(Exception e)
	{
		e.printStackTrace();
		System.err.println("error writing to log file.");
	}
}

/**
 * generate new math description, compare with old, log status
 * @param biomodel
 */
protected boolean verifyMathDescriptionsUnchanged(BioModel bioModel, PrintWriter printWriter)  {
	SimulationContext[] simContexts = bioModel.getSimulationContexts();
	boolean allGood = true;
	for (SimulationContext sc : simContexts) {
		try {
			MathDescription oldMathDescription = sc.getMathDescription();
			MathDescription newMathDescription = sc.createNewMathMapping().getMathDescription();
			printWriter.print("\t " + bioModel.getName() + " :: " + sc.getName() + " ----> Successfully regenerated math");
			MathCompareResults mathCompareResults = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), oldMathDescription, newMathDescription);
			printWriter.println("\t " + mathCompareResults.toDatabaseStatus());
			if (!mathCompareResults.isEquivalent()) {
				return false;
			}
		} catch (Exception e) {
			printWriter.println("\t " + bioModel.getName() + " :: " + sc.getName() + " ----> math regeneration failed.s");
			allGood = false;
		}
	}
	return allGood;
}

/**
 * Insert the method's description here.
 * Creation date: (2/2/01 3:40:29 PM)
 * @throws DataAccessException 
 * @throws XmlParseException 
 */
public void scanGeometries(VCDatabaseVisitor databaseVisitor, PrintStream logFilePrintStream, User users[], KeyValue singleGeometryKey, HashSet<KeyValue> includeHash, HashSet<KeyValue> excludeHash, boolean bAbortOnDataAccessException) throws DataAccessException, XmlParseException {
	if (users==null){
		users = getAllUsers();
	}
	try
	{
		//start visiting models and writing log
		logFilePrintStream.println("Start scanning geometries......");
		logFilePrintStream.println("\n");
		
		for (int i=0;i<users.length;i++)
		{
			User user = users[i];
			GeometryInfo geoInfos[] = dbServerImpl.getGeometryInfos(user,false);
			for (int j = 0; j < geoInfos.length; j++){
				if (singleGeometryKey!=null && !geoInfos[j].getVersion().getVersionKey().compareEqual(singleGeometryKey)){
					System.out.println("skipping geometry, not the single one that we wanted");
					continue;
				}
				if (excludeHash!=null && excludeHash.contains(geoInfos[j].getVersion().getVersionKey())){
					System.out.println("skipping geometry with key '"+geoInfos[j].getVersion().getVersionKey()+"'");
					continue;
				}
				if (includeHash!=null && !includeHash.contains(geoInfos[j].getVersion().getVersionKey())){
					System.out.println("not including geometry with key '"+geoInfos[j].getVersion().getVersionKey()+"'");
					continue;
				}
				if (!databaseVisitor.filterGeometry(geoInfos[j])){
					continue;
				}
				try {
					BigString geometryXML = dbServerImpl.getGeometryXML(user, geoInfos[j].getVersion().getVersionKey());
					Geometry geometry = cbit.vcell.xml.XmlHelper.XMLToGeometry(new XMLSource(geometryXML.toString()));
					geometry.refreshDependencies();
					databaseVisitor.visitGeometry(geometry,logFilePrintStream);
				}catch (Exception e2){
					log.exception(e2);
					if (bAbortOnDataAccessException){
						throw e2;
					}
				}
			}
		}
		
		logFilePrintStream.close();
	}catch(Exception e)
	{
		System.err.println("error writing to log file.");
	}
}

public void scanMathModels(VCDatabaseVisitor databaseVisitor, PrintStream logFilePrintStream, User users[], KeyValue singleMathmodelKey, HashSet<KeyValue> includeHash, HashSet<KeyValue> excludeHash, boolean bAbortOnDataAccessException) throws DataAccessException, XmlParseException {
	BadMathVisitor badMathVisitor = null;
	if (databaseVisitor instanceof BadMathVisitor) {
		badMathVisitor = (BadMathVisitor) databaseVisitor;
	}
	final boolean isBadMathVisitor = badMathVisitor != null;
	if (users==null){
		users = getAllUsers();
	}
	try
	{
		//start visiting models and writing log
		logFilePrintStream.println("Start scanning mathmodels ......");
		logFilePrintStream.println("\n");
		
		for (int i=0;i<users.length;i++)
		{
			User user = users[i];
			MathModelInfo mathInfos[] = dbServerImpl.getMathModelInfos(user,false);
			for (int j = 0; j < mathInfos.length; j++){
				if (singleMathmodelKey!=null && !mathInfos[j].getVersion().getVersionKey().compareEqual(singleMathmodelKey)){
					System.out.println("skipping geometry, not the single one that we wanted");
					continue;
				}
				if (excludeHash!=null && excludeHash.contains(mathInfos[j].getVersion().getVersionKey())){
					System.out.println("skipping geometry with key '"+mathInfos[j].getVersion().getVersionKey()+"'");
					continue;
				}
				if (includeHash!=null && !includeHash.contains(mathInfos[j].getVersion().getVersionKey())){
					System.out.println("not including geometry with key '"+mathInfos[j].getVersion().getVersionKey()+"'");
					continue;
				}
				if (!databaseVisitor.filterMathModel(mathInfos[j])){
					continue;
				}
				KeyValue vk = null; 
				try {
					vk = mathInfos[j].getVersion().getVersionKey();
					BigString mathModelXML = dbServerImpl.getMathModelXML(user, mathInfos[j].getVersion().getVersionKey());
					MathModel mathModel = cbit.vcell.xml.XmlHelper.XMLToMathModel(new XMLSource(mathModelXML.toString()));
					mathModel.refreshDependencies();
					databaseVisitor.visitMathModel(mathModel,logFilePrintStream);
				}catch (Exception e2){
					if (isBadMathVisitor) {
						badMathVisitor.unableToLoad(vk, e2);
					}
					
					log.exception(e2);
					if (bAbortOnDataAccessException){
						throw e2;
					}
				}
			}
		}
		
		logFilePrintStream.close();
	}catch(Exception e)
	{
		System.err.println("error writing to log file.");
	}
}

/**
 * Insert the method's description here.
 * Creation date: (2/2/01 3:40:29 PM)
 * @throws DataAccessException 
 * @throws XmlParseException 
 */
public void multiScanBioModels(VCMultiBioVisitor databaseVisitor, Writer writer, User users[], boolean bAbortOnDataAccessException) throws DataAccessException, XmlParseException {
	assert users != null;
	try
	{
		PrintWriter printWriter = new PrintWriter(writer);
		//start visiting models and writing log
		printWriter.println("Start scanning bio-models......");
		printWriter.println("\n");

		for (int i=0;i<users.length;i++)
		{
			User user = users[i];
			BioModelInfo bioModelInfos[] = dbServerImpl.getBioModelInfos(user,false);
			for (int j = 0; j < bioModelInfos.length; j++){
				BioModelInfo bmi = bioModelInfos[j];
				if (!databaseVisitor.filterBioModel(bmi)) {
					continue;
				}
				try {
					BigString bioModelXML = dbServerImpl.getBioModelXML(user, bmi.getVersion().getVersionKey());
					BioModel storedModel = cbit.vcell.xml.XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
					System.out.println(storedModel.getName( ));
					if (databaseVisitor.filterBioModel(storedModel)) {
						storedModel.refreshDependencies();
						verifyMathDescriptionsUnchanged(storedModel,printWriter);
						databaseVisitor.setBioModel(storedModel, printWriter);
						printWriter.println("---- " + (j+1) + " ----> " + storedModel.getName());    //  + bioModelInfos[j].getVersion().getName() + " -----> ");
						for (BioModel bioModel: databaseVisitor) {
							SimulationContext[] simContexts = bioModel.getSimulationContexts();
							for (SimulationContext sc : simContexts) {
								try {
									sc.createNewMathMapping().getMathDescription();
								} catch (Exception e) {
									printWriter.println("\t " + bioModel.getName() + " :: " + sc.getName() + " ----> math regeneration failed.s");
									// e.printStackTrace();
								}
							}
						}
					}
				}catch (Exception e2){
					log.exception(e2);
					printWriter.println("======= " + e2.getMessage());
					if (bAbortOnDataAccessException){
						throw e2;
					}
				}
			}
		}
		
		printWriter.close();
	}catch(Exception e)
	{
		e.printStackTrace();
	}
}

public void scanBioModels(BioModelVisitor databaseVisitor, Logger logger, User users[], boolean bAbortOnDataAccessException) throws DataAccessException, XmlParseException {
	if (users==null){
		users = getAllUsers();
	}
	//start visiting models and writing log
	logger.info("Start scanning bio-models......");


	for (int i=0;i<users.length;i++)
	{
		User user = users[i];
		BioModelInfo bioModelInfos[] = dbServerImpl.getBioModelInfos(user,false);
		for (int j = 0; j < bioModelInfos.length; j++){
			BioModelInfo bmi = bioModelInfos[j];
			if (!databaseVisitor.filterBioModel(bmi) ){
				continue;
			}
			try {
				BigString bioModelXML = dbServerImpl.getBioModelXML(user, bioModelInfos[j].getVersion().getVersionKey());
				BioModel bioModel = cbit.vcell.xml.XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
				bioModel.refreshDependencies();
				if (logger.isDebugEnabled()) {
					logger.debug("---- " + (j+1) + " ----> " + bioModel.getName());    //  + bioModelInfos[j].getVersion().getName() + " -----> ");
				}
				databaseVisitor.visitBioModel(bioModel);
			}catch (Exception e2){
				logger.warn("exception biomodel " + bmi.getModelKey(),e2);
				if (bAbortOnDataAccessException){
					throw e2;
				}
			}
		}
	}
}

public static void scanGeometries(final java.lang.String[] args, final VCDatabaseVisitor visitor, final boolean bAbortOnDataAccessException) {
	scanDbObjects(VersionableType.Geometry, args, visitor, bAbortOnDataAccessException);
}

public static void scanBioModels(final java.lang.String[] args, final VCDatabaseVisitor visitor, final boolean bAbortOnDataAccessException) {
	scanDbObjects(VersionableType.BioModelMetaData, args, visitor, bAbortOnDataAccessException);
}

public static void scanMathModels(final java.lang.String[] args, final VCDatabaseVisitor visitor, final boolean bAbortOnDataAccessException) {
	scanDbObjects(VersionableType.MathModelMetaData, args, visitor, bAbortOnDataAccessException);
}

private static void scanDbObjects(VersionableType versionableType, final java.lang.String[] args, final VCDatabaseVisitor visitor, final boolean bAbortOnDataAccessException) {
	try {
				
		if ((args.length<2 || args.length>4)){
			System.out.println("Usage: VCDatabaseScanner (" + ALL_USERS + "| userid) (logfileName | -) [(-include includefile) | (-exclude excludefile) | biomodelkey]");
			System.out.println("     where 'logfileSpec'\t\t\ta filename or '-' for STDOUT");
			System.out.println("     and '-include' to test biomodel keys from includefile");
			System.out.println("     and '-include' to test all biomodel keys except from excludefile");
			System.out.println("     and 'biomodelkey'\t\tthe KeyValue of the BioModel to test ... if missing, test all BioModel");
			System.exit(0);
		}

		VCDatabaseScanner databaseScanner = VCDatabaseScanner.createDatabaseScanner();

		//
		// get Array of all users to be crawled
		//
		java.util.Vector<User> userList = new java.util.Vector<User>();
		User[] allUsers = databaseScanner.getAllUsers();
		for (int i=0;i<allUsers.length;i++){
			if (args[0].equals(ALL_USERS) || allUsers[i].getName().equals(args[0])){
				userList.add(allUsers[i]);
			}
		}
		User users[] = (User[])org.vcell.util.BeanUtils.getArray(userList,User.class);

		//
		// Redirect output to the logfile (append if exists)
		//
		PrintStream logFilePrintStream = System.out;
		if (!args[1].equals("-")){
			logFilePrintStream = new java.io.PrintStream(new java.io.FileOutputStream(args[1], true), true);
		}
		
		HashSet<KeyValue> includeHash = null;
		HashSet<KeyValue> excludeHash = null;
		KeyValue singleKey = null;
		
		if (args.length==4 && (args[2].equals("-include")||args[2].equals("-exclude"))) {
			File keyFile = new File(args[3]);
			if (keyFile.exists()){
				System.out.println("using key file '"+args[3]+"'");
				try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(keyFile))) {
					int sizeIncludeFile = (int)keyFile.length();
					char keyFileBuffer[] = new char[sizeIncludeFile];
					int count = reader.read(keyFileBuffer);
					String keyBuffer = new String(keyFileBuffer,0,count);
					StringTokenizer tokens = new StringTokenizer(keyBuffer);
					HashSet<KeyValue> keyHash = new HashSet<KeyValue>();
					while (tokens.hasMoreTokens()) {
						String token = tokens.nextToken();
						keyHash.add(new KeyValue(token));
					}
					if (args[2].equals("-include")){
						includeHash = keyHash;
					}else if (args[2].equals("-exclude")){
						excludeHash = keyHash;
					}
				}
			}
		} else if (args.length==3){
			singleKey = new KeyValue(args[2]);
		}
		if (versionableType.equals(VersionableType.BioModelMetaData)){
			databaseScanner.scanBioModels(visitor, logFilePrintStream, users, singleKey, includeHash, excludeHash, bAbortOnDataAccessException);
		} else if (versionableType.equals(VersionableType.Geometry)){
			databaseScanner.scanGeometries(visitor, logFilePrintStream, users, singleKey, includeHash, excludeHash, bAbortOnDataAccessException);
		} else if (versionableType.equals(VersionableType.MathModelMetaData)){
			databaseScanner.scanMathModels(visitor, logFilePrintStream, users, singleKey, includeHash, excludeHash, bAbortOnDataAccessException);
		} else{
			throw new RuntimeException("versionableType "+versionableType.toString()+" not yet supported");
		}

	} catch (Throwable exception) {
		System.out.println("Exception occurred in main() of VCBioModelVisitor");
		exception.printStackTrace(System.out);
	} finally {
		System.exit(0);
	}
}


}