package cbit.vcell.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseSyntax;
import org.vcell.db.KeyFactory;
import org.vcell.util.BeanUtils;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.document.VersionFlag;

import cbit.sql.QueryHashtable;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.GeometryThumbnailImageFactory;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.messaging.db.SimulationJobTable;
import cbit.vcell.modeldb.BioModelSimContextLinkTable;
import cbit.vcell.modeldb.BioModelSimulationLinkTable;
import cbit.vcell.modeldb.BioModelTable;
import cbit.vcell.modeldb.DBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.LocalUserMetaDbServer;
import cbit.vcell.modeldb.MathModelSimulationLinkTable;
import cbit.vcell.modeldb.MathModelTable;
import cbit.vcell.modeldb.PublicationModelLinkTable;
import cbit.vcell.modeldb.ServerDocumentManager;
import cbit.vcell.modeldb.TFTestCaseTable;
import cbit.vcell.modeldb.TFTestSuiteTable;
import cbit.vcell.modeldb.UserTable;
import cbit.vcell.modeldb.VersionTable;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.ODEDataBlock;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.OutputFunctionContext;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;

public class IonItems {
	//See vcell-node1:/opt/build/frm/cbit_vcell_tools_IonItems.info
	private static final String PUBLISHEDMODELVCML = "publishedmodelvcml";
	private static final String TESTSUITEMODELVCML = "testsuitemodelvcml";
	private static final String PUBLICMODELWITHSIMS = "publicmodelwithsims";
	private static final String PUBLISHEDMODELVCMLDIFF = "publishedmodelvcmldiff";

	private static Connection con = null;//oracleConnection.getConnection(new Object());

	public static void main(String[] args) {
		if(args.length <3) {
			usage();
		}
		//Make full VCMLs from all published models pruned of all simulations that don’t have results 
		//"jdbc:oracle:thin:@VCELL_DB_HOST:1521:VCEL_DB_NAME"
		String connectionStr = args[0];
		// /share/apps/vcell3/users
		File vcellUsersRootDir = new File(args[1]);
		// publicmodelwithsims {true false moreIfno/onlySimIDs}, publishedmodelvcml DirToStoreVCML
		String command = args[2];

//		select vc_biomodelsim.simref from vc_biomodel,vc_biomodelsim where vc_biomodel.privacy=0 and vc_biomodelsim.biomodelref=vc_biomodel.id  order by vc_biomodelsim.simref;
//		select vc_mathmodelsim.simref from vc_mathmodel,vc_mathmodelsim where vc_mathmodel.privacy=0 and vc_mathmodelsim.mathmodelref=vc_mathmodel.id  order by vc_mathmodelsim.simref;
		Statement stmt = null;//con.createStatement();
		ArrayList<ArrayList<FoundSimDataInfo>> foundBMSims = null;
		ArrayList<ArrayList<FoundSimDataInfo>> foundMMSims = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        System.out.print("Enter VCell Password: ");
	        String password = br.readLine();
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = java.sql.DriverManager.getConnection(connectionStr, "vcell",password);
			con.setAutoCommit(false);
			con.setReadOnly(true);
			
			if(command.toLowerCase().equals(PUBLICMODELWITHSIMS)) {
				stmt = con.createStatement();
				String bmsql = createSQL(VCDocumentType.BIOMODEL_DOC);
				System.out.println(bmsql+";");
				foundBMSims = findSims(stmt, bmsql);
				
				String mmsql = createSQL(VCDocumentType.MATHMODEL_DOC);
				System.out.println(mmsql+";");
				foundMMSims = findSims(stmt, mmsql);
			}else if(command.toLowerCase().equals(PUBLISHEDMODELVCML)) {
				File dirToSaveVCML = new File(args[3]);
				if(!dirToSaveVCML.exists()) {
					throw new IllegalArgumentException("dirToSaveVCML not exist "+dirToSaveVCML.getAbsolutePath());
				}
				long startSimKey = Long.parseLong(args[4]);
				long endSimKey = Long.parseLong(args[5]);
				if(!((startSimKey==0 && endSimKey==0) || (endSimKey>=startSimKey))) {
					System.out.println("startSimKey==0 and endSimKey==0  -or- endSimKey>=startSimKey");
					usage();
				}
				saveVCMLRemoveSimsWithNoData(con,dirToSaveVCML,vcellUsersRootDir,startSimKey,endSimKey);
			}else if(command.toLowerCase().equals(PUBLISHEDMODELVCMLDIFF)) {
				File dirWithSavedVCML = new File(args[1]);
				readVCMLWithSimsRemoved(con, dirWithSavedVCML);
			}else if (command.toLowerCase().equals(TESTSUITEMODELVCML)) {
				File dirToSaveVCML = new File(args[1]);
				saveVCMLTestSuite(dirToSaveVCML,0,0);				
			}

		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally{
			if(stmt != null){try{stmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con != null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
		
		if(command.toLowerCase().equals(PUBLICMODELWITHSIMS)) {
			boolean bMoreInfo = new Boolean(args[3]).booleanValue();
			if(bMoreInfo) {
				System.out.println("'SimID','VCDocumentType','simLogFile','jobIndex','logExists?','oldStyle?','lastTaskID'");
			}
			checkSimData(foundBMSims,vcellUsersRootDir,VCDocumentType.BIOMODEL_DOC,bMoreInfo);
			checkSimData(foundMMSims,vcellUsersRootDir,VCDocumentType.MATHMODEL_DOC,bMoreInfo);
		}

	}

	private static void saveVCMLTestSuite(File dirToSaveVCML,long startSimKey,long endSimKey) throws Exception{
//		select distinct vc_biomodelsimcontext.biomodelref modelkey
//		from vc_tftestsuite,vc_tftestcase,vc_biomodelsimcontext
//		where vc_tftestsuite.tsversion='88_alpha_from_87_alpha'
//		and vc_tftestcase.testsuiteref=vc_tftestsuite.id
//		and vc_tftestcase.bmappref is not null
//		and vc_tftestcase.bmappref=vc_biomodelsimcontext.id
//		union
//		select distinct vc_mathmodel.id modelkey
//		from vc_tftestsuite,vc_tftestcase,vc_mathmodel
//		where vc_tftestsuite.tsversion='88_alpha_from_87_alpha'
//		and vc_tftestcase.testsuiteref=vc_tftestsuite.id
//		and vc_tftestcase.mathmodelref is not null
//		and vc_tftestcase.mathmodelref=vc_mathmodel.id;
		String modelKeyStr = "modelkey";
		String userKeyStr = "userkey";
		String useridStr = "userid";
		String modelUserKeyStr = modelKeyStr+","+UserTable.table.id.getQualifiedColName()+" "+userKeyStr+","+UserTable.table.userid.getQualifiedColName()+" "+useridStr;
		String testSuitTestCaseLink =
				TFTestSuiteTable.table.tsVersion.getQualifiedColName()+"="+"'88_alpha_from_87_alpha'"+
				" AND "+TFTestCaseTable.table.testSuiteRef.getQualifiedColName()+"="+TFTestSuiteTable.table.id.getQualifiedColName();
		String testSuitAndUserStr = TFTestSuiteTable.table.getTableName()+","+TFTestCaseTable.table.getTableName()+","+UserTable.table.getTableName();
		String bioModelTypeStr = "biomodel";
		String mathModelTypeStr = "mathmodel";
		String modelTypeKeyStr = "modeltype";
		String sql = 
			"SELECT distinct "+"'"+bioModelTypeStr+"' "+modelTypeKeyStr+","+BioModelTable.table.id.getQualifiedColName()+" "+modelUserKeyStr+
			" FROM "+testSuitAndUserStr+","+BioModelSimContextLinkTable.table.getTableName()+","+BioModelTable.table.getTableName()+
			" WHERE "+testSuitTestCaseLink+" AND "+TFTestCaseTable.table.bmAppRef.getQualifiedColName()+" IS NOT NULL"+
				" AND "+TFTestCaseTable.table.bmAppRef.getQualifiedColName()+"="+BioModelSimContextLinkTable.table.id.getQualifiedColName()+
				" AND "+BioModelSimContextLinkTable.table.bioModelRef.getQualifiedColName()+"="+BioModelTable.table.id.getQualifiedColName()+
				" AND "+BioModelTable.table.ownerRef.getQualifiedColName()+"="+UserTable.table.id.getQualifiedColName()+
			" UNION "+
			"SELECT distinct "+"'"+mathModelTypeStr+"' "+modelTypeKeyStr+","+MathModelTable.table.id.getQualifiedColName()+" "+modelUserKeyStr+
			" FROM "+testSuitAndUserStr+","+MathModelTable.table.getTableName()+
			" WHERE "+testSuitTestCaseLink+" AND "+TFTestCaseTable.table.mathModelRef.getQualifiedColName()+" IS NOT NULL"+
				" AND "+TFTestCaseTable.table.mathModelRef.getQualifiedColName()+"="+MathModelTable.table.id.getQualifiedColName()+
				" AND "+MathModelTable.table.ownerRef.getQualifiedColName()+"="+UserTable.table.id.getQualifiedColName();
		
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			//ArrayList<VCDocument> successDocs = new ArrayList<VCDocument>();
			DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(connFac,null);
			ArrayList<Object[]> modeKeyAndUser = new ArrayList<Object[]>();
			
			ResultSet rset = stmt.executeQuery(sql);
			while(rset.next()) {
				BigDecimal modelKeyBigDecimal = rset.getBigDecimal(modelKeyStr);
				KeyValue modelKey = new KeyValue(modelKeyBigDecimal);
				String modelType = rset.getString(modelTypeKeyStr);
				if(startSimKey == 0 || (modelKeyBigDecimal.longValue() >= startSimKey && modelKeyBigDecimal.longValue() <= endSimKey)) {
					User user = new User(rset.getString(useridStr), new KeyValue(rset.getBigDecimal(userKeyStr)));
					modeKeyAndUser.add(new Object[] {(modelType.equals(bioModelTypeStr)?VCDocumentType.BIOMODEL_DOC:VCDocumentType.MATHMODEL_DOC),modelKey,user});
				}
			}
			rset.close();
			stmt.close();
			
			VCDocument vcDoc = null;
			for(int ii=0;ii<modeKeyAndUser.size();ii++) {
				try {
				VCDocumentType vcDocType = (VCDocumentType)modeKeyAndUser.get(ii)[0];
				KeyValue modelKey = (KeyValue)modeKeyAndUser.get(ii)[1];
				User user = (User)modeKeyAndUser.get(ii)[2];
				//-----NOTE: (privacy, versionflag) may not match between xml and database (because privacy and versionflag can be set without saving model)
				//some models regenerated from xml will not have same privacy and versionflag matched in query from database (the database values are correct, ignore the xml values)
					BigString modelXML = (vcDocType == VCDocumentType.BIOMODEL_DOC?databaseServerImpl.getBioModelXML(user, modelKey):databaseServerImpl.getMathModelXML(user, modelKey));
					vcDoc = null;
					if(vcDocType == VCDocumentType.BIOMODEL_DOC) {
						BioModel bm = cbit.vcell.xml.XmlHelper.XMLToBioModel(new XMLSource(modelXML.toString()));
						bm.refreshDependencies();
						vcDoc = bm;
					}else {
						MathModel mm = cbit.vcell.xml.XmlHelper.XMLToMathModel(new XMLSource(modelXML.toString()));
						mm.refreshDependencies();
						vcDoc = mm;
					}
//if(true) {
//	System.out.println(modelKey+" "+user+" "+vcDoc);
//	continue;
//}

					String docXml = null;
					if(vcDoc.getDocumentType() == VCDocument.VCDocumentType.BIOMODEL_DOC) {
						docXml = XmlHelper.bioModelToXML(((BioModel)vcDoc));
					}else {
						docXml = XmlHelper.mathModelToXML(((MathModel)vcDoc));
					}
					PrintWriter pw = new PrintWriter(new File(dirToSaveVCML,vcDoc.getVersion().getVersionKey().toString()+".xml"));
					pw.write(docXml);
					pw.close();
//				}
			} catch (Exception e) {
				System.out.println("----------GENERALERROR "+vcDoc.getVersion().getVersionKey());
				e.printStackTrace();
				if(vcDoc != null) {
					PrintWriter pw = new PrintWriter(new File(dirToSaveVCML,vcDoc.getVersion().getVersionKey().toString()+"_exception.xml"));
					pw.write("");
					pw.close();				
				}
			}
			}
		} finally{
			if(stmt != null){try{stmt.close();}catch(Exception e){e.printStackTrace();}}
		}

	}

	public static void usage() {
		System.out.println("cbit.vcell.tools.IonItems jdbc:oracle:thin:@VCELL_DB_HOST:1521:VCEL_DB_NAME vcellUsersRootDir "+PUBLICMODELWITHSIMS+" {true,false print more info}}");
		System.out.println("cbit.vcell.tools.IonItems jdbc:oracle:thin:@VCELL_DB_HOST:1521:VCEL_DB_NAME vcellUsersRootDir "+PUBLISHEDMODELVCML+" dirToSaveVCML, startSimKey endSimKey");
		System.out.println("cbit.vcell.tools.IonItems jdbc:oracle:thin:@VCELL_DB_HOST:1521:VCEL_DB_NAME dirWithSavedVCML "+PUBLISHEDMODELVCMLDIFF);
		System.out.println("cbit.vcell.tools.IonItems jdbc:oracle:thin:@VCELL_DB_HOST:1521:VCEL_DB_NAME dirToSaveVCML "+TESTSUITEMODELVCML);
		System.exit(1);
	}

	private static void readVCMLWithSimsRemoved(Connection con,File dirWithSavedVCML) throws Exception{
		File[] vcmlFiles = dirWithSavedVCML.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().endsWith(".xml");
			}});
		DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(connFac,null);
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<vcmlFiles.length;i++) {
			File file = vcmlFiles[i];
			System.out.println((i+1)+"/"+vcmlFiles.length);
			try {
				String xmlFromFile = BeanUtils.readBytesFromFile(file, null);
				VCDocument vcDocFromFile = XmlHelper.XMLToDocument(null, xmlFromFile);
				VCDocumentType vcDocType = vcDocFromFile.getDocumentType();
				BigString xmlFromDB = (vcDocType == VCDocumentType.BIOMODEL_DOC?databaseServerImpl.getBioModelXML(vcDocFromFile.getVersion().getOwner(), vcDocFromFile.getVersion().getVersionKey()):databaseServerImpl.getMathModelXML(vcDocFromFile.getVersion().getOwner(), vcDocFromFile.getVersion().getVersionKey()));
				VCDocument vcDocFromDB = XmlHelper.XMLToDocument(null, xmlFromDB.toString());
				Simulation[] simsFromFile = null;
				Simulation[] simsFromDB = null;
				if(vcDocType == VCDocumentType.BIOMODEL_DOC) {
					simsFromFile = ((BioModel)vcDocFromFile).getSimulations();
					simsFromDB = ((BioModel)vcDocFromDB).getSimulations();
				}else {
					simsFromFile = ((MathModel)vcDocFromFile).getSimulations();
					simsFromDB = ((MathModel)vcDocFromDB).getSimulations();					
				}
				Comparator<Simulation> simComparator = new Comparator<Simulation>() {
					@Override
					public int compare(Simulation o1, Simulation o2) {
						return o1.getVersion().getVersionKey().compareTo(o2.getVersion().getVersionKey());
					}};
				Arrays.sort(simsFromFile, simComparator);
				Arrays.sort(simsFromDB,simComparator);
				Arrays.asList(simsFromFile).removeAll(Arrays.asList(simsFromDB));
				sb.append(vcDocType.name()+" "+vcDocFromDB.getVersion()+"\n");
				boolean bAllRemoved = simsFromFile.length==0;
				boolean bNoneRemoved = simsFromFile.length==simsFromDB.length;
				sb.append((bAllRemoved?"allRemoved":(bNoneRemoved?"NoneRemoved":"SomeRemoved"))+"\n");
				if(!bAllRemoved && !bNoneRemoved) {
					for(Simulation sim:simsFromFile) {
						sb.append("  SIM "+sim.getVersion()+"\n");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				sb.append("-----ERROR "+file.getName()+"\n   "+e.getMessage());
			}
		}
		System.out.println(sb.toString());
	}
	private static ConnectionFactory connFac = new ConnectionFactory() {

		@Override
		public void close() throws SQLException {
			throw new RuntimeException("Do Not Call");
		}

		@Override
		public void failed(Connection con, Object lock) throws SQLException {
			//throw new RuntimeException("Do Not Call");
		}

		@Override
		public Connection getConnection(Object lock) throws SQLException {
			return con;
		}

		@Override
		public void release(Connection con, Object lock) throws SQLException {
			//throw new RuntimeException("Do Not Call");
		}

		@Override
		public KeyFactory getKeyFactory() {
			return null;
		}

		@Override
		public DatabaseSyntax getDatabaseSyntax() {
			// TODO Auto-generated method stub
			return DatabaseSyntax.ORACLE;
		}
		
	};

	private static void saveVCMLRemoveSimsWithNoData(Connection con,File dirToSaveVCML,File usersDir,long startSimKey,long endSimKey) {
		//select id from vc_biomodel where versionflag=3 and privacy=0;
		String bmsql =
				"SELECT "+BioModelTable.table.id.getQualifiedColName()+" modelkey" +","+UserTable.table.id.getQualifiedColName()+" userkey" +","+UserTable.table.userid.getQualifiedColName()+" userid" +
				" FROM "+BioModelTable.table.getTableName()+","+UserTable.table.getTableName()+
				" WHERE "+
					BioModelTable.table.versionFlag.getQualifiedColName()+"="+VersionFlag.Published.getIntValue() +
					" AND "+
					BioModelTable.table.privacy.getQualifiedColName()+"="+GroupAccess.GROUPACCESS_ALL.intValue()+
					" AND "+
					BioModelTable.table.ownerRef.getQualifiedColName()+"="+UserTable.table.id.getQualifiedColName()+
					" AND "+
					BioModelTable.table.id.getQualifiedColName()+" IN "+
						"(SELECT "+PublicationModelLinkTable.table.bioModelRef.getQualifiedColName()+
						" FROM "+ PublicationModelLinkTable.table.getTableName() +
						" WHERE "+PublicationModelLinkTable.table.bioModelRef.getQualifiedColName()+" IS NOT NULL)"+
					" ORDER BY modelKey";
		String mmsql =
				"SELECT "+MathModelTable.table.id.getQualifiedColName()+" modelkey" +","+UserTable.table.id.getQualifiedColName()+" userkey" +","+UserTable.table.userid.getQualifiedColName()+" userid" +
				" FROM "+MathModelTable.table.getTableName()+","+UserTable.table.getTableName()+
				" WHERE "+
					MathModelTable.table.versionFlag.getQualifiedColName()+"="+VersionFlag.Published.getIntValue() +
					" AND "+
					MathModelTable.table.privacy.getQualifiedColName()+"="+GroupAccess.GROUPACCESS_ALL.intValue()+
					" AND "+
					MathModelTable.table.ownerRef.getQualifiedColName()+"="+UserTable.table.id.getQualifiedColName()+
					" AND "+
					MathModelTable.table.id.getQualifiedColName()+" IN "+
					// vc_publicationmodellink.biomodelref
						"(SELECT "+PublicationModelLinkTable.table.mathModelRef.getQualifiedColName()+
						" FROM "+ PublicationModelLinkTable.table.getTableName() +
						" WHERE "+PublicationModelLinkTable.table.mathModelRef.getQualifiedColName()+" IS NOT NULL)"+
					" ORDER BY modelKey";
		
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			//ArrayList<VCDocument> successDocs = new ArrayList<VCDocument>();
			DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(connFac,null);
			//ServerDocumentManager serverDocManager = new ServerDocumentManager(databaseServerImpl);
			//QueryHashtable qht = new QueryHashtable();
			DataSetControllerImpl dsci = new DataSetControllerImpl(new Cachetable(Cachetable.minute*3,30000000L), usersDir, null);
			ArrayList<Object[]> modeKeyAndUser = new ArrayList<Object[]>();
			for(int i=0;i<2;i++) {
				String sql = (i==0?bmsql:mmsql);
				ResultSet rset = stmt.executeQuery(sql);
				while(rset.next()) {
					BigDecimal modelKeyBigDecimal = rset.getBigDecimal("modelkey");
					KeyValue modelKey = new KeyValue(modelKeyBigDecimal);
					if(startSimKey == 0 || (modelKeyBigDecimal.longValue() >= startSimKey && modelKeyBigDecimal.longValue() <= endSimKey)) {
						User user = new User(rset.getString("userid"), new KeyValue(rset.getBigDecimal("userkey")));
						modeKeyAndUser.add(new Object[] {(i==0?VCDocumentType.BIOMODEL_DOC:VCDocumentType.MATHMODEL_DOC),modelKey,user});
					}
				}
				rset.close();
			}
			stmt.close();

			VCDocument vcDoc = null;
			for(int ii=0;ii<modeKeyAndUser.size();ii++) {
				try {
				VCDocumentType vcDocType = (VCDocumentType)modeKeyAndUser.get(ii)[0];
				KeyValue modelKey = (KeyValue)modeKeyAndUser.get(ii)[1];
				User user = (User)modeKeyAndUser.get(ii)[2];
				//-----NOTE: (privacy, versionflag) may not match between xml and database (because privacy and versionflag can be set without saving model)
				//some models regenerated from xml will not have same privacy and versionflag matched in query from database (the database values are correct, ignore the xml values)
					BigString modelXML = (vcDocType == VCDocumentType.BIOMODEL_DOC?databaseServerImpl.getBioModelXML(user, modelKey):databaseServerImpl.getMathModelXML(user, modelKey));
					Simulation[] sims = null;
					vcDoc = null;
					if(vcDocType == VCDocumentType.BIOMODEL_DOC) {
						BioModel bm = cbit.vcell.xml.XmlHelper.XMLToBioModel(new XMLSource(modelXML.toString()));
						bm.refreshDependencies();
						sims = bm.getSimulations().clone();
						vcDoc = bm;
					}else {
						MathModel mm = cbit.vcell.xml.XmlHelper.XMLToMathModel(new XMLSource(modelXML.toString()));
						mm.refreshDependencies();
						sims = mm.getSimulations().clone();
						vcDoc = mm;
					}
//if(true) {
//	System.out.println(modelKey+" "+user+" "+vcDoc);
//	continue;
//}
					boolean bRemovedSims = false;
					for(int j=0;sims!=null && j<sims.length;j++) {
						try {
							//public FoundSimDataInfo(long simID, int lastTaskID, int jobIndex, String userid) {
							File foundLog = findSimLogFile(usersDir,new FoundSimDataInfo(Long.parseLong(sims[j].getKey().toString()),-1,0,user.getName()),true,vcDocType);
							if(foundLog == null) {
								throw new Exception("Logfile not found for sim "+sims[j].getKey().toString());
							}else {
								VCSimulationIdentifier vcsi = new VCSimulationIdentifier(sims[j].getKey(), user);
								VCSimulationDataIdentifier vcdID = new VCSimulationDataIdentifier(vcsi, 0);
								OutputFunctionContext outputFunctionContext = sims[j].getSimulationOwner().getOutputFunctionContext();
								OutputContext outputContext = new OutputContext(outputFunctionContext.getOutputFunctionsList().toArray(new AnnotatedFunction[0]));
								DataIdentifier[] dataIdentifiers = dsci.getDataIdentifiers(outputContext, vcdID);
								double[] dataSetTimes = dsci.getDataSetTimes(vcdID);
								if(sims[j].getMathDescription().getGeometry().getDimension() == 0) {
									ODEDataBlock odeDataBlock = dsci.getODEDataBlock(vcdID);
								}else {
//									for(int k=0;k<dataIdentifiers.length;k++){
										SimDataBlock simDataBlock = dsci.getSimDataBlock(outputContext, vcdID, dataIdentifiers[0].getName(), dataSetTimes[dataSetTimes.length-1]);
//									}
								}
							}
						} catch (Exception e) {
							System.out.println("-----SIMERROR "+vcDoc.getVersion().getVersionKey()+"\n-----Sim="+sims[j].getVersion());
							e.printStackTrace();
							if(e.getMessage().contains("HDF5")) {
								return;
							}
							bRemovedSims = true;
							if(vcDoc.getDocumentType() == VCDocument.VCDocumentType.BIOMODEL_DOC) {
								((BioModel)vcDoc).removeSimulation(sims[j]);
							}else {
								((MathModel)vcDoc).removeSimulation(sims[j]);
							}
						}
					}
					String docXml = null;
					if(vcDoc.getDocumentType() == VCDocument.VCDocumentType.BIOMODEL_DOC) {
						docXml = XmlHelper.bioModelToXML(((BioModel)vcDoc));
					}else {
						docXml = XmlHelper.mathModelToXML(((MathModel)vcDoc));
					}
					PrintWriter pw = new PrintWriter(new File(dirToSaveVCML,vcDoc.getVersion().getVersionKey().toString()+"_"+(bRemovedSims)+".xml"));
					pw.write(docXml);
					pw.close();
//				}
			} catch (Exception e) {
				System.out.println("----------GENERALERROR "+vcDoc.getVersion().getVersionKey());
				e.printStackTrace();
				if(vcDoc != null) {
					PrintWriter pw = new PrintWriter(new File(dirToSaveVCML,vcDoc.getVersion().getVersionKey().toString()+"_exception.xml"));
					pw.write("");
					pw.close();				
				}
			}
			}
//			for(VCDocument successDoc:successDocs) {
//				if(successDoc.getDocumentType() == VCDocument.VCDocumentType.BIOMODEL_DOC) {
//					((BioModel)vcDoc).removeSimulation(sims[i]);
//				}else {
//					((MathModel)vcDoc).removeSimulation(sims[i]);
//				}
//
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(stmt != null){try{stmt.close();}catch(Exception e){e.printStackTrace();}}
		}
	}
	
	public static void checkSimData(ArrayList<ArrayList<FoundSimDataInfo>> foundBMSims,File vcellUsersRootDir,VCDocumentType docType,boolean bMoreInfo) {
		for(ArrayList<FoundSimDataInfo> arrForSim : foundBMSims) {
			for(FoundSimDataInfo foundSimInfo:arrForSim) {
				try {
					if(!bMoreInfo) {
						System.out.println(foundSimInfo.simID);
						break;
					}
					findSimLogFile(vcellUsersRootDir,foundSimInfo,bMoreInfo,docType);
//					File userDir = new File(vcellUsersRootDir,foundSimInfo.userid);
//					String newStyleSimLogFileName = SimulationData.createCanonicalSimLogFileName(new KeyValue(foundSimInfo.simID+""), foundSimInfo.jobIndex, false);
//					File newStyleLogFile = new File(userDir,newStyleSimLogFileName);
//					File foundLogFile = null;
//					boolean isOldStyle = false;
//					if(!newStyleLogFile.exists()) {
//						if(foundSimInfo.jobIndex != 0) {//OldStyle can only have 0 as jobindex so don't have to check
//							if(bMoreInfo) {
//								System.out.println(foundSimInfo.simID+","+"'"+docType.name()+"',"+"'"+"couldn't find "+newStyleLogFile.getAbsolutePath()+" (no oldStyle for jobIndex>0)'"+","+foundSimInfo.jobIndex+",'false','false'"+","+foundSimInfo.lastTaskID);
//							}
//							continue;
//						}
//						String oldStyleSimLogFileName = SimulationData.createCanonicalSimLogFileName(new KeyValue(foundSimInfo.simID+""), foundSimInfo.jobIndex, true);
//						File oldStyleLogFile = new File(userDir,oldStyleSimLogFileName);
//						if(!oldStyleLogFile.exists()) {
//							if(bMoreInfo) {
//								System.out.println(foundSimInfo.simID+","+"'"+docType.name()+"',"+"'"+"couldn't find "+oldStyleLogFile.getAbsolutePath()+" or "+newStyleLogFile.getAbsolutePath()+"'"+","+foundSimInfo.jobIndex+",'false','false'"+","+foundSimInfo.lastTaskID);
//							}
//							continue;
//						}else {
//							foundLogFile = oldStyleLogFile;
//							isOldStyle = true;
//						}
//						//System.out.println("  "+oldStyleSimLogFileName+" "+oldStyleLogFileExists);
//					}else {
//						foundLogFile = newStyleLogFile;
//						isOldStyle = false;
//					}
//					if(bMoreInfo) {
//						System.out.println(foundSimInfo.simID+","+"'"+docType.name()+"',"+"'"+foundLogFile.getAbsolutePath()+"'"+","+foundSimInfo.jobIndex+",'true','"+isOldStyle+"',"+foundSimInfo.lastTaskID);
//					}
				} catch (Exception e) {
					if(bMoreInfo) {
						System.out.println(foundSimInfo.simID+","+"'"+docType.name()+"',"+"'"+foundSimInfo.toString()+" "+e.getMessage()+"'"+","+foundSimInfo.jobIndex+",'unknown','unknown',"+foundSimInfo.lastTaskID);
					}
				}
			}
//			System.out.println();
		}
	}

	public static File findSimLogFile(File vcellUsersRootDir,FoundSimDataInfo foundSimInfo,boolean bMoreInfo,VCDocumentType docType) throws Exception{
		File userDir = new File(vcellUsersRootDir,foundSimInfo.userid);
		String newStyleSimLogFileName = SimulationData.createCanonicalSimLogFileName(new KeyValue(foundSimInfo.simID+""), foundSimInfo.jobIndex, false);
		File newStyleLogFile = new File(userDir,newStyleSimLogFileName);
		File foundLogFile = null;
		boolean isOldStyle = false;
		if(!newStyleLogFile.exists()) {
			if(foundSimInfo.jobIndex != 0) {//OldStyle can only have 0 as jobindex so don't have to check
				if(bMoreInfo) {
					System.out.println(foundSimInfo.simID+","+"'"+docType.name()+"',"+"'"+"couldn't find "+newStyleLogFile.getAbsolutePath()+" (no oldStyle for jobIndex>0)'"+","+foundSimInfo.jobIndex+",'false','false'"+","+foundSimInfo.lastTaskID);
				}
				return null;
			}
			String oldStyleSimLogFileName = SimulationData.createCanonicalSimLogFileName(new KeyValue(foundSimInfo.simID+""), foundSimInfo.jobIndex, true);
			File oldStyleLogFile = new File(userDir,oldStyleSimLogFileName);
			if(!oldStyleLogFile.exists()) {
				if(bMoreInfo) {
					System.out.println(foundSimInfo.simID+","+"'"+docType.name()+"',"+"'"+"couldn't find "+oldStyleLogFile.getAbsolutePath()+" or "+newStyleLogFile.getAbsolutePath()+"'"+","+foundSimInfo.jobIndex+",'false','false'"+","+foundSimInfo.lastTaskID);
				}
				return null;
			}else {
				foundLogFile = oldStyleLogFile;
				isOldStyle = true;
			}
			//System.out.println("  "+oldStyleSimLogFileName+" "+oldStyleLogFileExists);
		}else {
			foundLogFile = newStyleLogFile;
			isOldStyle = false;
		}
		if(bMoreInfo) {
			System.out.println(foundSimInfo.simID+","+"'"+docType.name()+"',"+"'"+foundLogFile.getAbsolutePath()+"'"+","+foundSimInfo.jobIndex+",'true','"+isOldStyle+"',"+foundSimInfo.lastTaskID);
		}
		return foundLogFile;
	}
	public static class FoundSimDataInfo {
		public long simID;
		public int lastTaskID;
		public int jobIndex;
		public String userid;
		public FoundSimDataInfo(long simID, int lastTaskID, int jobIndex, String userid) {
			super();
			this.simID = simID;
			this.lastTaskID = lastTaskID;
			this.jobIndex = jobIndex;
			this.userid = userid;
		}
		@Override
		public String toString() {
			return "simID="+simID+" lastTaskID="+lastTaskID+" jobIndex="+jobIndex+" userid="+userid;
		}
	}
	public static ArrayList<ArrayList<FoundSimDataInfo>> findSims(Statement stmt, String sql) throws SQLException {
		ResultSet rset = stmt.executeQuery(sql);
		long lastSimID = -1;
		int lastTaskID = -1;
		ArrayList<ArrayList<FoundSimDataInfo>> lastSimDatas = new ArrayList<ArrayList<FoundSimDataInfo>>();
		while(rset.next()) {
			long currSimID = rset.getLong(1);
			int currJobIndex = rset.getInt(3);
			int currTaskID = rset.getInt(4);
			String currUserID = rset.getString(5);
			if(lastSimID != currSimID) {
//					if(lastSimID != -1) {
//						processLastTaskID(lastSimDatas);
//					}
				lastSimDatas.add(new ArrayList<FoundSimDataInfo>());
				lastSimID = currSimID;
				lastTaskID = currTaskID;
			}else if(lastTaskID != currTaskID) {
				lastSimDatas.get(lastSimDatas.size()-1).clear();
				lastTaskID = currTaskID;
			}
			lastSimDatas.get(lastSimDatas.size()-1).add(new FoundSimDataInfo(currSimID,currTaskID,currJobIndex,currUserID));
//				if(rset.isAfterLast()) {
//					processLastTaskID(lastSimDatas);
//				}
		}
		rset.close();
		return lastSimDatas;
	}

	public static String createSQL(VCDocumentType docType) {
		if(docType != VCDocumentType.BIOMODEL_DOC && docType != VCDocumentType.MATHMODEL_DOC) {
			throw new IllegalArgumentException("Only VCDocumentTypes BioModel and mathModel allowed");
		}
		VersionTable vTable = (docType == VCDocumentType.BIOMODEL_DOC?BioModelTable.table:MathModelTable.table);
		//"select vc_biomodelsim.simref from vc_biomodel,vc_biomodelsim where vc_biomodel.privacy="+GroupAccess.GROUPACCESS_ALL.toPlainString()+" and vc_biomodelsim.biomodelref=vc_biomodel.id  order by vc_biomodelsim.simref";
		//"select vc_mathmodelsim.simref from vc_mathmodel,vc_mathmodelsim where vc_mathmodel.privacy=0 and vc_mathmodelsim.mathmodelref=vc_mathmodel.id  order by vc_mathmodelsim.simref";
		return "select "+
			(docType == VCDocumentType.BIOMODEL_DOC?BioModelSimulationLinkTable.table.simRef.getQualifiedColName():MathModelSimulationLinkTable.table.simRef.getQualifiedColName())+","+
			SimulationJobTable.table.hasData.getQualifiedColName()+","+
			SimulationJobTable.table.jobIndex.getQualifiedColName()+","+
			SimulationJobTable.table.taskID+","+
			UserTable.table.userid+
		" From "+
			vTable.getTableName()+","+
			(docType == VCDocumentType.BIOMODEL_DOC?BioModelSimulationLinkTable.table.getTableName():MathModelSimulationLinkTable.table.getTableName())+","+
			SimulationJobTable.table.getTableName()+","+
			UserTable.table.tableName+
		" WHERE "+
			vTable.ownerRef.getQualifiedColName()+"="+UserTable.table.id.getQualifiedColName()+
			" AND "+
			vTable.privacy.getQualifiedColName()+"="+GroupAccess.GROUPACCESS_ALL.toPlainString()+
			" AND "+
			(docType == VCDocumentType.BIOMODEL_DOC?BioModelSimulationLinkTable.table.bioModelRef:MathModelSimulationLinkTable.table.mathModelRef).getQualifiedColName()+"="+vTable.id.getQualifiedColName()+
			" AND "+
			(docType == VCDocumentType.BIOMODEL_DOC?BioModelSimulationLinkTable.table.simRef:MathModelSimulationLinkTable.table.simRef).getQualifiedColName()+"="+SimulationJobTable.table.simRef.getQualifiedColName()+
			" AND "+
			SimulationJobTable.table.hasData.getQualifiedColName()+" IS NOT NULL"+
				" AND "+
				" UPPER("+SimulationJobTable.table.hasData.getQualifiedColName()+")"+"='Y'"+
			" ORDER BY "+SimulationJobTable.table.simRef.getQualifiedColName()+","+SimulationJobTable.table.taskID.getQualifiedColName()+","+SimulationJobTable.table.jobIndex.getQualifiedColName();
	}

	public static void processLastTaskID(ArrayList<ArrayList<Object[]>> lastSimDatas) {
		ArrayList<Object[]> lastArrList = lastSimDatas.get(lastSimDatas.size()-1);
		for(int i=0;i<lastArrList.size();i++) {
			Object[] simInfo = lastArrList.get(i);
			System.out.println("**"+simInfo[0]+" "+simInfo[1]+" "+simInfo[2]+" "+simInfo[3]);
		}
	}
}
