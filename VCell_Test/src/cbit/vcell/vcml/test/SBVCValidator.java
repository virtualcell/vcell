package cbit.vcell.vcml.test;
import cbit.vcell.model.Structure;
import java.io.File;
import java.util.StringTokenizer;
import cbit.sql.*;
import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.modelapp.StructureMapping;
import cbit.vcell.modeldb.LocalAdminDbServer;
import cbit.vcell.solver.test.VariableComparisonSummary;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.sql.DBCacheTable;
import cbit.sql.ConnectionFactory;
import java.beans.*;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.solvers.NativeIDASolver;
import java.io.StringWriter;
import cbit.vcell.solver.ode.IDAFileWriter;
import cbit.vcell.solver.*;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.math.MathDescription;
import java.sql.ResultSet;
import java.sql.Statement;
import cbit.util.BigString;
import cbit.util.DataAccessException;
import cbit.util.PropertyLoader;
import cbit.util.SessionLog;
import cbit.util.StdoutSessionLog;
import cbit.util.document.BioModelInfo;
import cbit.util.document.KeyValue;
import cbit.util.document.User;
import cbit.util.document.UserInfo;
import cbit.sql.KeyFactory;
import java.sql.SQLException;

import org.vcell.expression.ExpressionException;
import org.vcell.sbml.SBMLExporter;

import cbit.vcell.mapping.MappingException;
import cbit.vcell.model.ModelException;
import cbit.vcell.math.MathException;
import cbit.vcell.xml.XmlDialect;
import cbit.vcell.solver.ode.ODESolver;
import cbit.vcell.solver.test.SimulationComparisonSummary;
import cbit.vcell.solver.test.MathTestingUtilities;
import cbit.vcell.modeldb.AdminDatabaseServer;
import cbit.vcell.modeldb.DatabasePolicySQL;
import cbit.vcell.modeldb.DbDriver;


public class SBVCValidator {
	private AdminDatabaseServer adminDbServer = null;
	private ConnectionFactory conFactory = null;
	private DatabaseServerImpl dbServerImpl = null;
	private KeyFactory keyFactory = null;
	private DBCacheTable cacheTable = null;
	private SessionLog log = null;

/**
 * ResultSetCrawler constructor comment.
 */
public SBVCValidator(ConnectionFactory argConFactory, KeyFactory argKeyFactory, AdminDatabaseServer argAdminDbServer, SessionLog argSessionLog, DBCacheTable dbCacheTable) throws DataAccessException, SQLException {
	this.conFactory = argConFactory;
	this.keyFactory = argKeyFactory;
	this.log = argSessionLog;
	this.adminDbServer = argAdminDbServer;
	this.cacheTable = dbCacheTable;
	this.dbServerImpl = new DatabaseServerImpl(conFactory,keyFactory,dbCacheTable,argSessionLog);
}


public static void main(java.lang.String[] args) {
	DatabasePolicySQL.bAllowAdministrativeAccess = true;
	try {
				
		if ((args.length!=3 && args.length!=4)){
			System.out.println("Usage: cbit.vcell.vcml.SBVCValidator -all logfileSpec (-includeBM) [includefile]");
			System.out.println("       cbit.vcell.vcml.SBVCValidator userid logfileSpec [biomodelkey]");
			System.out.println("     where 'logfileSpec'\t\t\ta filename or '-' for STDOUT");
			System.out.println("     and '-includeBM' to test biomodel keys from includefile");
			System.out.println("     and 'biomodelkey'\t\tthe KeyValue of the BioModel to test ... if missing, test all BioModel");
			System.exit(0);
		}

		//
		// Redirect output to the logfile (append if exists)
		//
		if (!args[1].equals("-")){
			System.setOut(new java.io.PrintStream(new java.io.FileOutputStream(args[1], true), true));
		}
		
		new PropertyLoader();
			
		DatabasePolicySQL.bSilent = true;
		DatabasePolicySQL.bAllowAdministrativeAccess = true;
		
		SessionLog log = new StdoutSessionLog("Admin");
		cbit.sql.ConnectionFactory conFactory = new cbit.sql.OraclePoolingConnectionFactory(log);
		cbit.sql.KeyFactory keyFactory = new cbit.sql.OracleKeyFactory();
		DbDriver.setKeyFactory(keyFactory);
		LocalAdminDbServer adminDbServer = new LocalAdminDbServer(conFactory,keyFactory,log);
		DBCacheTable dbCacheTable = new DBCacheTable(10000000);

		SBVCValidator sbvcValidator = new SBVCValidator(conFactory, keyFactory, adminDbServer, log, dbCacheTable);

		//
		// get Array of all users to be crawled
		//
		java.util.Vector userList = new java.util.Vector();
		UserInfo userInfos[] = adminDbServer.getUserInfos();
		for (int i=0;i<userInfos.length;i++){
			if (args[0].equals("-all") || userInfos[i].userid.equals(args[0])){
				//if (new java.math.BigDecimal(userInfos[i].id.toString()).compareTo(new java.math.BigDecimal("7075050"))>0) {  // start at users with id > "susana"
					userList.add(new User(userInfos[i].userid,userInfos[i].id));
				//}
			}
		}
		User users[] = (User[])cbit.util.BeanUtils.getArray(userList,User.class);

		if (args[2].equals("-includeBM") && args.length==4) {
			File includeFile = new File(args[3]);
			if (includeFile.exists()){
				System.out.println("using includelist file '"+args[3]+"'");
				java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(includeFile));
				int sizeIncludeFile = (int)includeFile.length();
				char includeFileBuffer[] = new char[sizeIncludeFile];
				int count = reader.read(includeFileBuffer);
				String includeBuffer = new String(includeFileBuffer,0,count);
				java.util.StringTokenizer tokens = new StringTokenizer(includeBuffer);
				java.util.Vector includeKeyList = new java.util.Vector();
				while (tokens.hasMoreTokens()) {
					String token = tokens.nextToken();
					includeKeyList.add(new KeyValue(token));
				}
				sbvcValidator.scanBioModels((KeyValue[])cbit.util.BeanUtils.getArray(includeKeyList,KeyValue.class));
			}
		} else {
			sbvcValidator.scanBioModels(users);
		}
	} catch (Throwable exception) {
		System.out.println("Exception occurred in main() of SBVCValidator");
		exception.printStackTrace(System.out);
	} finally {
		System.exit(0);
	}
}


/**
 * Scans biomodels in the database and retrieves the applications that are 
 */
public void scanBioModels(KeyValue[] bioModelKeys) throws MathException, MappingException, SQLException, DataAccessException, ModelException, ExpressionException {

//	User user = new User("Administrator", new cbit.sql.KeyValue("2"));
	User user = new User("anu", new KeyValue("2302355"));
//	User user = new User("schaff", new cbit.sql.KeyValue("17"));
	SessionLog userLog = new StdoutSessionLog(user.toString());
	for (int i=0;i<bioModelKeys.length;i++){
		BioModelInfo bioModelInfo = dbServerImpl.getBioModelInfo(user,bioModelKeys[i]);
		userLog.print("Testing bioModel with key '"+bioModelKeys[i]+"'");

		try {
			//
			// read in the BioModel from the database
			//
			BigString bioModelXML_1 = dbServerImpl.getBioModelXML(user, bioModelInfo.getVersion().getVersionKey());
			BioModel bioModel_1 = cbit.vcell.xml.XmlHelper.XMLToBioModel(bioModelXML_1.toString());
			bioModel_1.refreshDependencies();

			// inline a VCLogger - to be used later while importing an exported biomodel.
	        cbit.util.xml.VCLogger logger = new cbit.util.xml.VCLogger() {
	            private StringBuffer buffer = new StringBuffer();
	            public void sendMessage(int messageLevel, int messageType) {
	                // String message = cbit.vcell.vcml.TranslationMessage.getDefaultMessage(messageType);
	                // sendMessage(messageLevel, messageType, message);	
	            }
	            public void sendMessage(int messageLevel, int messageType, String message) {
	                // System.out.println("LOGGER: msgLevel="+messageLevel+", msgType="+messageType+", "+message);
	            }
	            public void sendAllMessages() {
	            }
	            public boolean hasMessages() {
	                return false;
	            }
	        };

			// Check each application to see if it is a compartmental application. Grab only the compartmental simContexts.
			SimulationContext simContexts[] = bioModel_1.getSimulationContexts();
			for (int k = 0; k < simContexts.length; k++){
				if (simContexts[k].getGeometry().getDimension() > 0) {
					continue;
				} else {
					System.out.println("\n Biomodel Key : " + bioModelKeys[i].toString() + "\t; Name : " + bioModel_1.getName() + ";\t Application : " + simContexts[k].getName() + "\n");

					// Export this application from the biomodel into SBML

					// First get a structure from the model, and set its size (thro' the structureMapping in the geometry of the simContext)
					// invoke the structureSizeEvaluator to compute and set the sizes of the remaining structures.
					Structure structure = simContexts[k].getModel().getStructures(0);
					double structureSize = 1.0;
					cbit.vcell.vcml.StructureSizeSolver ssEvaluator = new cbit.vcell.vcml.StructureSizeSolver();
					StructureMapping structMapping = simContexts[k].getGeometryContext().getStructureMapping(structure); 
					ssEvaluator.updateAbsoluteStructureSizes(simContexts[k], structure, structureSize, structMapping.getSizeParameter().getUnitDefinition());

					// now export to SBML
					// String exportedSBMLStr = cbit.vcell.xml.XmlHelper.exportXML(bioModel_1, XmlDialect.SBML_L2V1, simContexts[k].getName());
					int sbmlLevel = 2;
					SBMLExporter sbmlExporter = new SBMLExporter(bioModel_1, sbmlLevel);
					String exportedSBMLStr = sbmlExporter.getSBMLFile();

					if (exportedSBMLStr != null) {
						sbmlExporter.printErrorReport();
					} else {
						System.err.println(bioModel_1.getName() + " : " + simContexts[k].getName() +" could not be exported into SBML - null string");
						System.out.println(bioModel_1.getName() + " : " + simContexts[k].getName() +" could not be exported into SBML - null string");
					}
				}
			}
		}catch (Throwable e){
            log.exception(e); // exception in whole BioModel
		}
	}
}



/**
 * Scans biomodels in the database and retrieves the applications that are 
 */
public void scanBioModels(User[] users) throws MathException, MappingException, SQLException, DataAccessException, ModelException, ExpressionException {
	for (int ii = 0; ii < users.length; ii++){
		User user = users[ii];
		BioModelInfo[] bioModelInfos = dbServerImpl.getBioModelInfos(user, false);
		SessionLog userLog = new StdoutSessionLog(user.toString());
		userLog.print("Testing User '"+user.getName()+"'");

        for (int i = 0; i < bioModelInfos.length; i++) {
			try {
				//
				// read in the BioModel from the database
				//
				BioModelInfo bioModelInfo = bioModelInfos[i];
				BigString bioModelXML_1 = dbServerImpl.getBioModelXML(user, bioModelInfo.getVersion().getVersionKey());
				BioModel bioModel_1 = cbit.vcell.xml.XmlHelper.XMLToBioModel(bioModelXML_1.toString());
				bioModel_1.refreshDependencies();

				// inline a VCLogger - to be used later while importing an exported biomodel.
		        cbit.util.xml.VCLogger logger = new cbit.util.xml.VCLogger() {
		            private StringBuffer buffer = new StringBuffer();
		            public void sendMessage(int messageLevel, int messageType) {
		                // String message = cbit.vcell.vcml.TranslationMessage.getDefaultMessage(messageType);
		                // sendMessage(messageLevel, messageType, message);	
		            }
		            public void sendMessage(int messageLevel, int messageType, String message) {
		                // System.out.println("LOGGER: msgLevel="+messageLevel+", msgType="+messageType+", "+message);
		            }
		            public void sendAllMessages() {
		            }
		            public boolean hasMessages() {
		                return false;
		            }
		        };
				
				// Check each application to see if it is a compartmental application. Grab only the compartmental simContexts.
				SimulationContext simContexts[] = bioModel_1.getSimulationContexts();
				for (int k = 0; k < simContexts.length; k++){
					if (simContexts[k].getGeometry().getDimension() > 0) {
						continue;
					} else {
						// Export this application from the biomodel into SBML

						// First get a structure from the model, and set its size (thro' the structureMapping in the geometry of the simContext)
						// invoke the structureSizeEvaluator to compute and set the sizes of the remaining structures.
						Structure structure = simContexts[k].getModel().getStructures(0);
						double structureSize = 1.0;
						cbit.vcell.vcml.StructureSizeSolver ssEvaluator = new cbit.vcell.vcml.StructureSizeSolver();
						StructureMapping structMapping = simContexts[k].getGeometryContext().getStructureMapping(structure); 
						ssEvaluator.updateAbsoluteStructureSizes(simContexts[k], structure, structureSize, structMapping.getSizeParameter().getUnitDefinition());

						// now export to SBML
						// String exportedSBMLStr = cbit.vcell.xml.XmlHelper.exportXML(bioModel_1, XmlDialect.SBML_L2V1, simContexts[k].getName());
						int sbmlLevel = 2;
						SBMLExporter sbmlExporter = new SBMLExporter(bioModel_1, sbmlLevel);
						String exportedSBMLStr = sbmlExporter.getSBMLFile();

						if (exportedSBMLStr != null) {
							sbmlExporter.printErrorReport();
						} else {
							System.err.println(bioModel_1.getName() + " : " + simContexts[k].getName() +" could not be exported into SBML - null string");
							System.out.println(bioModel_1.getName() + " : " + simContexts[k].getName() +" could not be exported into SBML - null string");
						}
					} // else
				} // for - simcontexts - k
			}catch (Throwable e){
	            log.exception(e); // exception in whole BioModel
			}
        } // end - for i - bioModelInfos
	}
}



}