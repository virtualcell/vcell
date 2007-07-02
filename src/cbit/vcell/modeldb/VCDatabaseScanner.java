package cbit.vcell.modeldb;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.StringTokenizer;

import cbit.sql.ConnectionFactory;
import cbit.sql.DBCacheTable;
import cbit.sql.KeyFactory;
import cbit.sql.KeyValue;
import cbit.sql.UserInfo;
import cbit.util.BigString;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.server.AdminDatabaseServer;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.server.PropertyLoader;
import cbit.vcell.server.SessionLog;
import cbit.vcell.server.User;
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
	
	cbit.vcell.server.SessionLog log = new cbit.vcell.server.StdoutSessionLog("Admin");
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
public void scanBioModels(VCDatabaseVisitor databaseVisitor, User users[], KeyValue singleModelKey, HashSet<KeyValue> includeHash, HashSet<KeyValue> excludeHash, boolean bAbortOnDataAccessException) throws DataAccessException, XmlParseException {
	if (users==null){
		users = getAllUsers();
	}
	try
	{
		FileOutputStream sout = new FileOutputStream("C:\\visitBioModel.txt");
		PrintStream p = new PrintStream(sout);
		//start visiting models and writing log
		p.println("Start scanning bio-models......");
		p.println("\n");
		
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
					databaseVisitor.visitBioModel(bioModel,p);
				}catch (Exception e2){
					log.exception(e2);
					if (bAbortOnDataAccessException){
						throw e2;
					}
				}
			}
		}
		
	    p.close();
	}catch(Exception e)
	{
		System.err.println("error writing to log file.");
	}
}

public static void scanBioModels(final java.lang.String[] args, final VCDatabaseVisitor visitor, final boolean bAbortOnDataAccessException) {
	try {
				
		if ((args.length<2 || args.length>4)){
			System.out.println("Usage: VCBioModelVisitor (-all | userid) (logfileName | -) [(-include includefile) | (-exclude excludefile) | biomodelkey]");
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
		User users[] = (User[])cbit.util.BeanUtils.getArray(userList,User.class);

		//
		// Redirect output to the logfile (append if exists)
		//
		if (!args[1].equals("-")){
			System.setOut(new java.io.PrintStream(new java.io.FileOutputStream(args[1], true), true));
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
		databaseScanner.scanBioModels(visitor, users, singleKey, includeHash, excludeHash, bAbortOnDataAccessException);

	} catch (Throwable exception) {
		System.out.println("Exception occurred in main() of VCBioModelVisitor");
		exception.printStackTrace(System.out);
	} finally {
		System.exit(0);
	}
}


}