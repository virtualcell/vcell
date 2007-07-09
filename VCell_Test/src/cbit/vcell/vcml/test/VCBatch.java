package cbit.vcell.vcml.test;
import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.sql.OracleKeyFactory;
import cbit.sql.OraclePoolingConnectionFactory;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.modeldb.DatabasePolicySQL;
import cbit.vcell.modeldb.DbDriver;
import cbit.vcell.modeldb.LocalAdminDbServer;
import cbit.vcell.vcml.Translator;
import cbit.vcell.client.database.DocumentManager;
import cbit.vcell.client.test.ClientTester;

import java.io.File;
import java.io.FileWriter;

import org.vcell.util.DataAccessException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.UserInfo;

/**
 * Creation date: (8/5/2003 12:09:27 PM)
 * @author: Rashad Badrawi
 */
	public class VCBatch {

		private static final String ALL = "all";
		private static final String LOG_FILE_NAME = "translation.log";
		File directory;
		String testType;

	
public VCBatch() {
	super();
}


		private void checkOptions (String args []) {

			StringBuffer buf = new StringBuffer();
			buf.append("\nUsage: DirectoryName TranslationMode [userName|" + ALL + "] [keyValue|" + ALL + "]\n");
			buf.append("DirectoryName: Name of directory where log file will be placed, and where\n");
			buf.append("VCML files will be dumped\n");
			buf.append("TranslationMode: " + Translator.VCSB_1 + ", " + Translator.VCSB_2 + ", " + 
						Translator.VC_QUAL_CELL + ", " + Translator.VC_QUAN_CELL + ", none.\n");
			buf.append("userName: name of the owner of the models or '" + ALL + "' for dumping all models\n");
			buf.append("keyValue: key of the model of choice or '" + ALL + "' for all models.\n");
			
			if (args.length < 4) {	
				System.err.println(buf);
				System.exit(0);
			}
			directory = new File(args[0]);
			if (!directory.isDirectory()) {
				System.err.println("Path provided is not for a directory." + buf);
				System.exit(0);
			}
			if (!args[1].equals(Translator.VCSB_1) && !args[1].equals(Translator.VCSB_2) && 
				!args[1].equals(Translator.VC_QUAL_CELL) && !args[1].equals(Translator.VC_QUAN_CELL)) {
				System.err.println("Unsupported translation: " + args[1] + buf);
				System.exit(0);
			}
			testType = args[1];
		}


//selection of model by keyValue not supported.
	private void dump(String userId, String passwd, String mode, String modelKey) throws Exception {

			String [] connArgs = new String[3];
			connArgs[0] = "-local";
			connArgs[1] = userId;
			connArgs[2] = passwd;
			cbit.vcell.client.server.ClientServerManager csManager = ClientTester.mainInit(connArgs,"VCBatch");
			DocumentManager dm = csManager.getDocumentManager();
			BioModelInfo bmInfos [];
			MathModelInfo mmInfos [];
			if (!mode.equals(Translator.VC_QUAN_CELL)) {
				bmInfos = dm.getBioModelInfos();
				dumpBioModels(dm, bmInfos, mode, modelKey);
			} else {
				mmInfos = dm.getMathModelInfos();
				dumpMathModels(dm, mmInfos, mode, modelKey);
			}
		}


		private void dumpBioModels(DocumentManager dm, BioModelInfo [] bmInfos, String mode, String modelKey) 
									throws Exception {

			TranslationTestSuite tts = new TranslationTestSuite(directory.getAbsolutePath() +  "\\" + LOG_FILE_NAME, 
																mode);
			FileWriter fw;
			for (int i = 0; i < bmInfos.length; i++) {					
				System.out.println("\n\n Dumping BioModel "+ bmInfos[i]);
				try {
					BioModel bm = dm.getBioModel(bmInfos[i]);
					String bmString = cbit.vcell.xml.XmlHelper.bioModelToXML(bm);
					if (!modelKey.equals(ALL) && !modelKey.equals(bmInfos[i].getModelKey().toString()))
						continue; 
					String fileName = bmInfos[i].getModelKey().toString() + ".xml";
					fw = new FileWriter(directory.getAbsolutePath() + "\\" + fileName);
					fw.write(bmString);
					fw.flush();
					fw.close();
					tts.runTests(directory.getAbsolutePath() +  "\\" + fileName);            //comment out this line to dump biomodels without testing.
				} catch (Exception e) {
					System.err.println("Error dumping model: " + bmInfos[i]);
					e.printStackTrace();
				}
			}
		}


		private void dumpMathModels(DocumentManager dm, MathModelInfo [] mmInfos, String mode, String modelKey) {

			TranslationTestSuite tts = new TranslationTestSuite(directory.getAbsolutePath() +  "\\" + LOG_FILE_NAME, 
																mode);
			FileWriter fw;
			for (int i = 0; i < mmInfos.length; i++) {		
				try {	
					System.out.println("\n\n Dumping MathModel "+ mmInfos[i]);
					MathModel mm = dm.getMathModel(mmInfos[i]);
					//CellML only supports non-spatial models
					int dim = mm.getMathDescription().getGeometry().getDimension();
					if (dim > 0) {
						System.out.println("Skipping spatial model: " + mm.getName());
						continue;
					}
					String mmString = cbit.vcell.xml.XmlHelper.mathModelToXML(mm);
					if (modelKey.equals(ALL) || modelKey.equals(mmInfos[i].getMathKey().toString()))
						continue; 
					String fileName = mmInfos[i].getMathKey().toString() + ".xml";
					fw = new FileWriter(directory.getAbsolutePath() + File.separator + fileName);
					fw.write(mmString);
					fw.flush();
					fw.close();
					tts.runTests(directory.getAbsolutePath() +  "\\" + fileName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}


		public void dumpModels(String args []) throws Exception {

			checkOptions(args);

			if (args[2].equals("none")) {
				testDumped();
				return;
			}
			LocalAdminDbServer adminDbServer = null;
			try {
				new PropertyLoader();
				DatabasePolicySQL.bSilent = true;
				SessionLog log = new StdoutSessionLog("Admin");
				ConnectionFactory conFactory = new OraclePoolingConnectionFactory(log);
				KeyFactory keyFactory = new OracleKeyFactory();
				DbDriver.setKeyFactory(keyFactory);
				adminDbServer = new LocalAdminDbServer(conFactory,keyFactory,log);
			} catch (Exception exception) {
				System.err.println("\nException occurred when starting Database connection");
				exception.printStackTrace(System.out);		
				System.exit(1);
			}
			UserInfo[] userInfos = null;
			try { 
				userInfos = adminDbServer.getUserInfos();
			} catch (DataAccessException e) {
				System.err.println("\nException occurred when starting Database connection");
				e.printStackTrace(System.out);		
				System.exit(1);
			}
			for (int i=0;i<userInfos.length;i++){
				System.out.println(userInfos[i].userid);
				if (args[2].equals(ALL) || userInfos[i].userid.equals(args[2])){
					dump(userInfos[i].userid, userInfos[i].password, args[1], args[3]); //modelkeys not supported
					break;               //remove this to support 'all'
				}
			}	
		}
			
		public static void main (String args []) {

			try {
				PropertyLoader.loadProperties();
				new VCBatch().dumpModels(args);
				System.exit(0);
			} catch (Exception e) {
				System.err.println("Unable to process test.");
				e.printStackTrace();
				System.exit(1);
			}
		}


		private void testDumped() {

			File files [] = directory.listFiles();
			String path;
			TranslationTestSuite tts = new TranslationTestSuite(directory.getAbsolutePath() + "\\"  + LOG_FILE_NAME, 
																testType);
			for (int i = 0; i < files.length; i++) {
				path = files[i].getAbsolutePath();
				System.out.println("Testing file: " + path);
				tts.runTests(path);
			}
		}}