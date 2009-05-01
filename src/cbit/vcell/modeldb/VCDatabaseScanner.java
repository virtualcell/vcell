package cbit.vcell.modeldb;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.VersionableType;

import cbit.sql.ConnectionFactory;
import cbit.sql.DBCacheTable;
import cbit.sql.KeyFactory;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.server.AdminDatabaseServer;
import cbit.vcell.xml.XmlParseException;
/**
 * Insert the type's description here.
 * Creation date: (2/2/01 2:57:33 PM)
 * @author: Jim Schaff
 */
public class VCDatabaseScanner {
	private DatabaseServerImpl dbServerImpl = null;
	private SessionLog log = null;
	private LocalAdminDbServer localAdminDbServer = null;
	private User[] allUsers = null;

	
public static VCDatabaseScanner createDatabaseScanner() throws Exception{
	new PropertyLoader();
		
	DatabasePolicySQL.bSilent = true;
	DatabasePolicySQL.bAllowAdministrativeAccess = true;
	
	org.vcell.util.SessionLog log = new org.vcell.util.StdoutSessionLog("Admin");
	cbit.sql.ConnectionFactory conFactory = new cbit.sql.OraclePoolingConnectionFactory(log);
	cbit.sql.KeyFactory keyFactory = new cbit.sql.OracleKeyFactory();
	DbDriver.setKeyFactory(keyFactory);
	DBCacheTable dbCacheTable = new DBCacheTable(10000000);

	VCDatabaseScanner databaseScanner = new VCDatabaseScanner(conFactory, keyFactory, log, dbCacheTable);
	
	return databaseScanner;
}

/**
 * ResultSetCrawler constructor comment.
 * @throws RemoteException 
 */
private VCDatabaseScanner(ConnectionFactory argConFactory, KeyFactory argKeyFactory, SessionLog argSessionLog, DBCacheTable dbCacheTable) throws DataAccessException, SQLException, RemoteException {
	this.log = argSessionLog;
	this.localAdminDbServer = new LocalAdminDbServer(argConFactory, argKeyFactory, argSessionLog);
	this.dbServerImpl = new DatabaseServerImpl(argConFactory,argKeyFactory,dbCacheTable,argSessionLog);
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
					BioModel bioModel = cbit.vcell.xml.XmlHelper.XMLToBioModel(bioModelXML.toString());
					bioModel.refreshDependencies();
					databaseVisitor.visitBioModel(bioModel,logFilePrintStream);
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
					Geometry geometry = cbit.vcell.xml.XmlHelper.XMLToGeometry(geometryXML.toString());
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
				try {
					BigString mathModelXML = dbServerImpl.getMathModelXML(user, mathInfos[j].getVersion().getVersionKey());
					MathModel mathModel = cbit.vcell.xml.XmlHelper.XMLToMathModel(mathModelXML.toString());
					mathModel.refreshDependencies();
					databaseVisitor.visitMathModel(mathModel,logFilePrintStream);
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
			System.out.println("Usage: VCDatabaseScanner (-all | userid) (logfileName | -) [(-include includefile) | (-exclude excludefile) | biomodelkey]");
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
			if (args[0].equals("-all") || allUsers[i].getName().equals(args[0])){
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
				java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(keyFile));
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