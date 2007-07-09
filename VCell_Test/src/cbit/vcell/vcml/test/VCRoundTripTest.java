package cbit.vcell.vcml.test;
import cbit.sql.ConnectionFactory;
import cbit.sql.DBCacheTable;
import cbit.sql.KeyFactory;
import cbit.sql.OracleKeyFactory;
import cbit.sql.OraclePoolingConnectionFactory;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelMetaData;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.mathmodel.MathModelMetaData;
import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.modeldb.AdminDatabaseServer;
import cbit.vcell.modeldb.DatabasePolicySQL;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.DbDriver;
import cbit.vcell.modeldb.LocalAdminDbServer;
import cbit.vcell.vcml.compare.VCMLComparator;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.XmlReader;
import cbit.vcell.xml.Xmlproducer;
import cbit.util.xml.XmlUtil;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;

/**
 * This class is used to make automated tests of all the models belonging to a series of users.
  The logic of the test is to make a roundtrip for the java object (Biomodel or Mathmodel), and
  if the two objects are not identical, then to traverse through the children (in VCML format) until the
  point(s) of difference is detected.
  
  Alternatively, we can dump XML files in repositories, then access those repositories and test if
  the roundtrip for the XML is the same.
  
 * Creation date: (5/29/2003 12:42:20 PM)
 * @author: Daniel Lucio /Rashad Badrawi
 */

public class VCRoundTripTest {

	private final static String MATHMODEL_TEST = "-m";
	private final static String BIOMODEL_TEST = "-b";
	private final static PrintStream DEF_OUTPUT = System.out;
	
	private PrintStream ps;
	private DatabaseServerImpl dbImpl;
	private ArrayList excludeList;
	//Define counters
	private int totalDBFail = 0;
	private int totalProcessFail = 0;
	private int totalCompareFail = 0;
	private boolean ltUser, gtUser, ltModel, gtModel;
	private int userID;
	char selectChar = ' ';                                  //for display purposes.
	private AdminDatabaseServer adminDbServer;


public VCRoundTripTest(String fName, String userName, String testType, String modelKeyValue) throws Exception {

    this(fName, userName, testType, modelKeyValue, null);
}


	public VCRoundTripTest(String fName, String userName, String testType, String modelKeyValue, String excludeFileName) 
						  throws Exception {

		//Redirect output
        if (fName != null && !fName.equals("-")) {
            ps = new PrintStream(new FileOutputStream(fName + ".txt", true), true);     
        } else {
            ps = DEF_OUTPUT;
        }
		excludeList = new ArrayList();
		if (excludeFileName != null) {
			BufferedReader bfr = new BufferedReader(new FileReader(excludeFileName));
			String temp;
			while ((temp = bfr.readLine()) != null) {
				if (temp.trim().length() > 0)
					excludeList.add(temp.trim());
			}
		}
		VCMLComparator.ps = ps;
		new PropertyLoader();
		DatabasePolicySQL.bSilent = true;
		SessionLog log = new StdoutSessionLog("Admin");
		ConnectionFactory conFactory = new OraclePoolingConnectionFactory(log);
		KeyFactory keyFactory = new OracleKeyFactory();
		DbDriver.setKeyFactory(keyFactory);
		DBCacheTable dbCacheTable = new cbit.sql.DBCacheTable(-1,0);		
		adminDbServer = new LocalAdminDbServer(conFactory,keyFactory,log);
		this.dbImpl = new DatabaseServerImpl(conFactory, keyFactory, dbCacheTable, log);
		// get Array of all users to be crawled
		UserInfo[] userInfos = adminDbServer.getUserInfos();
		boolean firstTime = true;
		if (userName.charAt(0) == '>') {
			gtUser = true;
			selectChar = userName.charAt(0);
			userName = userName.substring(1, userName.length());
			setUserID(userInfos, userName);
		} else if (userName.charAt(0) == '<') {
			ltUser = true;
			selectChar = userName.charAt(0);
			userName = userName.substring(1, userName.length());
			setUserID(userInfos, userName);
		}
		if (modelKeyValue != null) {
			if (modelKeyValue.charAt(0) == '>') {
				gtModel = true;
				selectChar = modelKeyValue.charAt(0);
				modelKeyValue = modelKeyValue.substring(1, modelKeyValue.length());
			} else if (modelKeyValue.charAt(0) == '<') {
				ltModel = true;
				selectChar = modelKeyValue.charAt(0);
				modelKeyValue = modelKeyValue.substring(1, modelKeyValue.length());
			}
		}
		for (int i = 0;i < userInfos.length; i++) {
			if (userName.equals("-all") || includeUser(userName, userInfos[i])) {
				if (firstTime) {
					ps.println("* * * * * * * * Processing " + selectChar + userName + " user(s) * * * * * * * * *");
					firstTime = false;
				}
				testModel(userInfos[i], modelKeyValue, testType);
			}
		}
		if (!firstTime) {
			ps.println("Total DB Fail: " + totalDBFail);
			ps.println("Total VCML Fail: " + totalProcessFail);
			ps.println("Total Compare Fail: " + totalCompareFail);	
			ps.println("******************* END ***************************");
		}
	}


	//will compare the DB-model with the XML copy cached in the database, if there is one.
	//If using the ServerDocumentManager.getBioModelXML() method, it may just be comparing it with
	//itself. 
	private void compareBioWithCached(BioModel bm, User user, BioModelMetaData bmMeta) throws Exception {

		Element root = new Xmlproducer(true).getXML(bm); 
		String unresolvedXML = XmlUtil.xmlToString(root);
		//String cachedXML = dbImpl.getServerDocumentManager().getBioModelXMLResolved(user, bmMeta.getVersion().getVersionKey());
		String cachedXML = dbImpl.getServerDocumentManager().getBioModelXML(user, bmMeta.getVersion().getVersionKey());
		if (cachedXML == null) {
			ps.println("No cached copy yet of model: " + bmMeta);
		} else {
			if (unresolvedXML.equals(cachedXML)) {
				ps.println("Cached XML is the same as the DB-read model for: " + bmMeta.getVersion().getVersionKey());
			} else {
				boolean result = VCMLComparator.compareEquals(unresolvedXML, cachedXML);
				if (result) {
					ps.println("The cachedXML and the DB-read model are identical with different ordering: " + 
					bmMeta.getVersion().getVersionKey());
				} else {
					ps.println("Warning: The cachedXML and the DB-read model are not identical for: " + 
					bmMeta.getVersion().getVersionKey());
				}
			}
		}
		compareMathDesc(bm, unresolvedXML, user, bmMeta);
	}	


	//this is a temporary home for this method: it checks if the refreshed math is identical to the one from the database,
	//and if not, refreshes again, and saves the latest biomodel back into the database.
	private void compareMathDesc(BioModel bm, String unresolvedXML, User user, BioModelMetaData bmMeta) throws Exception {

		bm.refreshDependencies();                  							  //in case its unresolved
		bm = refreshMathDescLocal(bm);
		Element postms1Root = new Xmlproducer(true).getXML(bm);               //post math desc refresh
		String postms1XML = XmlUtil.xmlToString(postms1Root);
		if (unresolvedXML.equals(postms1XML)) {
			ps.println("Post and pre math desc refresh models are identical: --- refresh 1");
		} else {
			boolean result = VCMLComparator.compareEquals(unresolvedXML, postms1XML);
			if (result) {
				ps.println("The post and pre math desc refresh models are identical with different ordering: --- refresh 1 " + 
							bmMeta.getVersion().getVersionKey());
			} else {
				ps.println("Warning: The post and the pre math desc refresh models are not identical for: --- refresh 1 " + 
							bmMeta.getVersion().getVersionKey());
				bm = refreshMathDescLocal(bm);
				Element postms2Root = new Xmlproducer(true).getXML(bm);
				String postms2XML = XmlUtil.xmlToString(postms2Root);
				if (postms2XML.equals(postms1XML)) {
					ps.println("Post and pre math desc refresh models are identical: --- refresh 2 " + bmMeta.getVersion().getVersionKey());
					//save this biomodel again.
					//dbImpl.saveBioModelAs(adminDbServer.getUser("rbadrawi"), bm, bm.getName() + "_REFRESH");
					//dbImpl.saveBioModel(user, bm);
				} else {
					result = VCMLComparator.compareEquals(postms1XML, postms2XML);
					if (result) {
						ps.println("The post and the pre math desc refresh models are identical with different ordering: --- refresh 2 " + 
									bmMeta.getVersion().getVersionKey());
					} else {
						ps.println("Warning: The post and the pre math desc refresh models are not identical for: --- refresh 2 " + 
									bmMeta.getVersion().getVersionKey());
					}
				}
			}
		}
	}


	private KeyValue[] getKeyArrayFromEnumeration(Enumeration enum1) {
		
		ArrayList temp = new ArrayList();
		while (enum1.hasMoreElements()){
			temp.add(enum1.nextElement());
		}
		KeyValue keys[] = (KeyValue [])temp.toArray(new KeyValue[temp.size()]);
	
		return keys;
	}


	private boolean includeModel(String selectedID, String currentID) {

		boolean flag = false;

		if (!gtModel && !ltModel) {                         //must be equal
			flag = selectedID.equals(currentID);            
		} else if (gtModel && !ltModel) {                   //must be greater.
			flag = Integer.parseInt(currentID) > Integer.parseInt(selectedID);
		} else if (!gtModel && ltModel) {                   //must be smaller
			flag = Integer.parseInt(currentID) < Integer.parseInt(selectedID);
		} else if (gtModel && ltModel) {
			throw new IllegalStateException("Invalid setting for the user ID option.");
		}
		 
		return flag;
	}


/**
If no greater than/less than options specified, compare the user names. Otherwise,
compare the user ID.
*/
	private boolean includeUser(String selectedUserID, UserInfo userInfo) {

		boolean flag = false;
		String currentUserID = userInfo.userid;
		if (!gtUser && !ltUser) {                         //must be equal
			flag = selectedUserID.equals(userInfo.userid);            
		} else if (gtUser && !ltUser) {                   //must be greater.
			flag = Integer.parseInt(userInfo.id.toString()) > userID;
		} else if (!gtUser && ltUser) {                   //must be smaller
			flag = Integer.parseInt(userInfo.id.toString()) < userID;
		} else if (gtUser && ltUser) {
			throw new IllegalStateException("Invalid setting for the user ID option.");
		}
		 
		return flag;
	}


/**
Controls the direction of the test.
The correct usage of the program is: $program [userid|-all] [outputFile|-] [-b|-m] [KeyValue|-all] exclude_ID_File
*/

public static void main(String[] args) {

    PrintStream output = null;
    String modelKeyValue = null;

    try {
        //Check number of attributes
        if (args == null || args.length < 4) {
            System.out.println(
                "Usage: VCRoundTripTest [userid|-all] [outputFile|-] [[-b|-m] [KeyValue|-all]]");
            System.out.println("OPTIONS:");
            System.out.println(
                "\tuserid\t\t\t\t\tThe name of the user's account to scan. Or, use '-all' to scan every user in the database.\n");
            System.out.println(
                "\toutputFile\t\t\t\tName of the output file. Or, use '-' instead to output to SDTOUT.\n");
            System.out.println(
                "\t-b [model KeyValue|-all] Test BioModels. Specify 'KeyValue' of model to test, otherwise use '-all' to test all models.\n");
            System.out.println(
                "\t-m [model KeyValue|-all] Test MathModels. Specify 'KeyValue' of model to test, otherwise use '-all' to test all models.\n");
			System.out.println("The program accepts '<', '>' before user name and modelID option, to indicate selecting user IDs/model IDs that ");
	        System.out.println("are less than/greater than the given ID(s).");
            System.exit(1);
        }
        if (!args[2].equalsIgnoreCase(BIOMODEL_TEST)
            && !args[2].equalsIgnoreCase(MATHMODEL_TEST)) {
            System.out.println(
                "\nUnknown test request:" + args[2] + ". Please check syntax!\n");
            System.exit(1);
        }
        //Get which model to test. NULL means all the models, otherwise look for a specific model.
        if (!args[3].equalsIgnoreCase("-all")) {
            modelKeyValue = args[3];
        }
        VCRoundTripTest vctest;
        if (args.length == 5) {
            vctest = new VCRoundTripTest(args[1], args[0], args[2], modelKeyValue, args[4]);
        } else {
            vctest = new VCRoundTripTest(args[1], args[0], args[2], modelKeyValue);
        }
        System.exit(0);
    } catch (Exception e) {
        System.out.println("\nException occurred when starting Database connection");
        e.printStackTrace();
        System.exit(1);
    }
}


	//this is a temporary home for this utility method
	private BioModel refreshMathDescLocal(BioModel bm) throws Exception {

		SimulationContext sc[];
		cbit.vcell.mapping.MathMapping mp;
		MathDescription md;
		sc = bm.getSimulationContexts();
		for (int j = 0; j < sc.length; j++){
			mp = new cbit.vcell.mapping.MathMapping(sc[j]);
			md = mp.getMathDescription();
			sc[j].setMathDescription(md);
		}

		return bm;
	}


/**
 Test for Biomodels. Longer than usual method, but flows well.
 */
private void scanBio(User user, String modelKeyValue, BioModelMetaData bmMeta []) {

	int failedToGetFromDatabase = 0;
	int failedToProcess = 0;
	int failedToCompare = 0;

	ps.println("Bio models Report:");
	String id = user.getName();
	KeyValue keyValue = null;
		if (modelKeyValue != null) { 
			ps.println("\nTesting "+ selectChar + modelKeyValue + " BioModel for user " + id + "\n");
			keyValue = new KeyValue(modelKeyValue.trim());
		} else {
			ps.println("\nTesting all BioModels (" + bmMeta.length + ") for user " + id +  "\n");	
		}	
		ps.println("*************************START***********************************");
	//Loop through all the models
	int skipped = 0; 
	for (int i = 0; i < bmMeta.length; i++){
		//If there is a KeyValue, look for the related model
		if (keyValue!=null && !includeModel(keyValue.toString(), bmMeta[i].getVersion().getVersionKey().toString())) {
			skipped++;
			continue;
		}
		if (skipModel(bmMeta[i].getVersion().getVersionKey().toString())) {
			skipped++;
			continue;
		}
		ps.println("\n\n* * * * * * Testing BioModel " + bmMeta[i]);
		//Load the biomodel from DB
		BioModel bm = null;
		try {
			bm = dbImpl.getServerDocumentManager().getBioModelUnresolved(user, bmMeta[i].getVersion().getVersionKey());
			//compareBioWithCached(bm, user, bmMeta[i]);                                   //temporary?
		} catch (Exception e) {
			e.printStackTrace(ps);
			failedToGetFromDatabase++;
			ps.println(i + ": model:" + bmMeta[i].getVersion().getName() + " <-- FAILED to read from database," +  
						  " Key=" + bmMeta[i].getVersion().getVersionKey() + "\n" + e.getMessage());
			continue;				
		}
		//Create VCML from MathModel
		String bmString = null;
		try {
			Element root = new Xmlproducer(true).getXML(bm);
			bmString = XmlUtil.xmlToString(root);
		} catch (Exception e) {
			e.printStackTrace(ps);
			failedToProcess++;
			ps.println(i + ": model:" + bmMeta[i].getVersion().getName() + " <-- FAILED to convert to VCML," +
					      " Key=" + bmMeta[i].getVersion().getVersionKey() + "\n" + e.getMessage());
			continue;				
		}
		// read from keyed VCML "keyFile"
		//System.out.println(bmString);
		BioModel bmFromXML = null;
		try {
			SAXBuilder builder = new SAXBuilder();
			Document sDoc = builder.build(new StringReader(bmString));
			bmFromXML = new XmlReader(true).getBioModel(sDoc.getRootElement());
			ps.println(i + ": model:" + bm.getName() + " <--  made the roundtrip");
		} catch (Exception e) {			
			e.printStackTrace(ps);
			failedToProcess++;
			ps.println("\t" + i + ": model:" + bmMeta[i].getVersion().getName() + " <-- FAILED to read from XML," +
				          " Key=" + bmMeta[i].getVersion().getVersionKey() + "\n" + e.getMessage());
			continue;				
		}
		boolean result = false;				
		try{
			result = bm.compareEqual(bmFromXML);
		} catch (Exception e) {
			e.printStackTrace(ps);
			failedToCompare++;
			ps.println("\t" + i + ": model:" + bmMeta[i].getVersion().getName() + " <-- FAILED while comparing," +
				          " Key=" + bmMeta[i].getVersion().getVersionKey() + "\n" + e.getMessage());

			continue;				
		}
		if (result) {
			result = VCMLComparator.compareMatchables(bm, bmFromXML, XMLTags.BioModelTag);
		}
		if (result) {
			//success
			ps.println(i + ": model:" + bm.getName() + " <-- SUCCESS in roundtrip. Models are identical.\n");
		} else {
			//failed
			failedToCompare++;
			ps.println(i + ": model:" + bm.getName() + " <-- FAILED in roundtrip. Models are not identical." +
				       " Key = " + bmMeta[i].getVersion().getVersionKey() + "\n");
			VCMLComparator.compareMatchables(bm, bmFromXML, XMLTags.BioModelTag);
		}
		//XmlUtil.compareMatchables(bm, bmFromXML, XMLTags.BioModelTag);
	}

	ps.println("\nFor User:" + id + ", from " + (bmMeta.length - skipped) + " models these are the results:\n");
	ps.println("\t-Failed to get from Database:" + failedToGetFromDatabase + "\n");
	ps.println("\t-Failed to write/read its XML:" + failedToProcess + "\n");
	ps.println("\t-Made the roundtrip:" + (bmMeta.length - failedToProcess - failedToGetFromDatabase - skipped) + "\n");
    ps.println("\t-For Test of CompareEqual: Passed=" + (bmMeta.length-failedToCompare-failedToGetFromDatabase - skipped) + 
	           " Failed:" + failedToCompare + "\n");
	ps.println("******************************END******************************");
	totalDBFail += failedToGetFromDatabase;
	totalProcessFail += failedToProcess;
	totalCompareFail += failedToCompare;
}


/**
 The math model version for the scanBio() method.
 */
 
private void scanMath(User user, String modelKeyValue, MathModelMetaData mmMeta []) {
	
	int failedToGetFromDatabase = 0;
	int failedToProcess = 0;
	int failedToCompare = 0;
	ps.println("Math models Report:");
	String id = user.getName();
	KeyValue keyValue = null;
		if (modelKeyValue != null) { 
			ps.println("\nTesting "+ selectChar + modelKeyValue + " MathModel for user " + id + "\n");
			keyValue = new KeyValue(modelKeyValue.trim());
		} else {
			ps.println("\nTesting all MathModels (" + mmMeta.length + ") for user " + id + "\n");	
		}	
		ps.println("*****	********************START***********************************");
	//Loop through all the models
	int skipped = 0;
	for (int i = 0; i < mmMeta.length; i++){
		if ( keyValue!=null && !includeModel(keyValue.toString(), mmMeta[i].getVersion().getVersionKey().toString())) {
			skipped++;
			continue;
		}
		if (skipModel(mmMeta[i].getVersion().getVersionKey().toString())) {
			skipped++;
			continue;
		}	
		ps.println("\n\n* * * * * * Testing MathModel " + mmMeta[i]);
		//Load the mathmodel from DB
		MathModel mm = null;
		try {
			mm = dbImpl.getServerDocumentManager().getMathModelUnresolved(user, mmMeta[i].getVersion().getVersionKey());
		} catch (Exception e) {
			e.printStackTrace(ps);
			failedToGetFromDatabase++;
			ps.println(i + ": model:" + mmMeta[i].getVersion().getName() + " <-- FAILED to read from database," +  
						  " Key=" + mmMeta[i].getVersion().getVersionKey() + "\n" + e.getMessage());
			continue;				
		}
		//Create VCML from MathModel
		String mmString = null;
		try {
			Element root = new Xmlproducer(true).getXML(mm);
			mmString = XmlUtil.xmlToString(root);
		} catch (Exception e) {
			e.printStackTrace(ps);
			failedToProcess++;
			ps.println(i + ": model:" + mmMeta[i].getVersion().getName() + " <-- FAILED to convert to VCML," +
					      " Key=" + mmMeta[i].getVersion().getVersionKey() + "\n" + e.getMessage());
			continue;				
		}
		// read from keyed VCML "keyFile"
		MathModel mmFromXML = null;
		try {
			SAXBuilder builder = new SAXBuilder();
			Document sDoc = builder.build(new StringReader(mmString));
			mmFromXML = new XmlReader(true).getMathModel(sDoc.getRootElement());
			ps.println(i + ": model:" + mm.getName() + " <--  made the roundtrip");
		} catch (Exception e) {			
			e.printStackTrace(ps);
			failedToProcess++;
			ps.println("\t" + i + ": model:" + mmMeta[i].getVersion().getName() + " <-- FAILED to read from XML," +
				          " Key=" + mmMeta[i].getVersion().getVersionKey() + "\n" + e.getMessage());
			continue;				
		}
		boolean result = false;				
		try{
			result = mm.compareEqual(mmFromXML);
		} catch (Exception e) {
			e.printStackTrace(ps);
			failedToCompare++;
			ps.println("\t" + i + ": model:"+mmMeta[i].getVersion().getName()+" <-- FAILED while comparing," +
				          " Key=" + mmMeta[i].getVersion().getVersionKey() + "\n" + e.getMessage());

			continue;				
		}
		if (result) {
			//success
			ps.println(i + ": model:" + mm.getName() + " <-- SUCCESS in roundtrip. Models are identical.\n");
		} else {
			//failed
			failedToCompare++;
			ps.println(i + ": model:" + mm.getName() + " <-- FAILED in roundtrip. Models are not identical." +
				       " Key=" + mmMeta[i].getVersion().getVersionKey() + "\n");
			VCMLComparator.compareMatchables(mm, mmFromXML, XMLTags.MathModelTag);
		}
		//XmlUtil.compareMatchables(mm, mmFromXML, XMLTags.MathModelTag 
	}

	ps.println("\nFor User:" + id + ", from " + (mmMeta.length - skipped) + " models these are the results:\n");
	ps.println("\t-Failed to get from Database:" + failedToGetFromDatabase + "\n");
	ps.println("\t-Failed to write/read its XML:" + failedToProcess + "\n");
	ps.println("\t-Made the roundtrip:" + (mmMeta.length - failedToProcess - failedToGetFromDatabase - skipped) + "\n");
    ps.println("\t-For Test of CompareEqual: Passed=" + (mmMeta.length-failedToCompare-failedToGetFromDatabase - skipped) + 
	           " Failed:" + failedToCompare + "\n");
	ps.println("******************************END******************************");
	totalDBFail += failedToGetFromDatabase;
	totalProcessFail += failedToProcess;
	totalCompareFail += failedToCompare;
}


	private void setUserID (UserInfo [] userInfos, String userName) {

		for (int i = 0; i < userInfos.length; i++) {
			if (userName.equals(userInfos[i].userid)) {
				userID = Integer.parseInt(userInfos[i].id.toString());
				break;
			}
		}
	}


	private boolean skipModel(String keyValue) {

		boolean flag = false;
 
		for (int i = 0; i < excludeList.size(); i++) {
			String temp = (String)excludeList.get(i);
			//System.out.println(temp + " " + keyValue + " " + i);
			if (temp.equals(keyValue)) {
				flag = true;
				ps.println("Skipping model with ID: " + keyValue + " because its buggy.");
				break;
			}
		}
		
		return flag;
	}


	//utility method
	private void sort(Object list []) {

		class ModelSorter implements Comparator {

			public int compare(Object o1, Object o2) {

				String IDStr1, IDStr2;
				int flag;
					
				if (o1 instanceof BioModelMetaData) {                   //or o2
					BioModelMetaData bm1 = (BioModelMetaData)o1;
					IDStr1 = ((BioModelMetaData)o1).getVersion().getVersionKey().toString();
					IDStr2 = ((BioModelMetaData)o2).getVersion().getVersionKey().toString();
				} else if (o1 instanceof MathModelMetaData) {
					IDStr1 = ((MathModelMetaData)o1).getVersion().getVersionKey().toString();
					IDStr2 = ((MathModelMetaData)o2).getVersion().getVersionKey().toString();
				} else {
					throw new IllegalArgumentException("Cannot compare objects: " + o1 + " " + o2);
				}
				return new Integer(IDStr1).compareTo(new Integer(IDStr2));
			}
		}
		Arrays.sort(list, new ModelSorter());
	}


public void testModel(UserInfo userInfo, String modelKeyValue, String modelType) throws Exception {

    User user = new User(userInfo.userid, userInfo.id);

    if (modelType.equals(MATHMODEL_TEST)) {
        MathModelMetaData mmMetaData[] = dbImpl.getMathModelMetaDatas(user, false);
        sort(mmMetaData);
        scanMath(user, modelKeyValue, mmMetaData);
    } else {
        if (modelType.equals(BIOMODEL_TEST)) {
            BioModelMetaData bmMetaData[] = dbImpl.getBioModelMetaDatas(user, false);
            sort(bmMetaData);
            scanBio(user, modelKeyValue, bmMetaData);
        }
    }
}
}