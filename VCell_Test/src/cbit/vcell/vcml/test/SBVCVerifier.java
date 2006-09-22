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
import cbit.gui.PropertyLoader;
import cbit.vcell.server.AdminDatabaseServer;
import cbit.vcell.simdata.FunctionColumnDescription;
import cbit.vcell.simdata.ODESolverResultSet;
import cbit.vcell.simdata.ODESolverResultSetColumnDescription;
import cbit.vcell.simdata.RowColumnResultSet;
import cbit.vcell.simulation.*;
import cbit.sql.DBCacheTable;
import cbit.util.SessionLog;
import cbit.sql.ConnectionFactory;
import java.beans.*;
import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.vcml.StructureSizeSolver;
import cbit.vcell.solvers.NativeIDASolver;
import cbit.vcell.solvers.SimulationJob;
import cbit.vcell.solvers.SolverException;
import cbit.vcell.solvers.SolverStatus;

import java.io.StringWriter;
import cbit.vcell.solver.ode.IDAFileWriter;
import cbit.util.SimulationVersion;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.math.MathDescription;
import java.sql.ResultSet;
import java.sql.Statement;
import cbit.util.BigString;
import cbit.util.DataAccessException;
import cbit.util.KeyValue;
import cbit.util.StdoutSessionLog;
import cbit.util.User;
import cbit.util.UserInfo;
import cbit.util.VersionFlag;
import cbit.sql.KeyFactory;
import java.sql.SQLException;


import cbit.vcell.mapping.MappingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.model.ModelException;
import cbit.vcell.math.MathException;
import cbit.vcell.xml.XmlDialect;
import cbit.vcell.solver.ode.ODESolver;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.test.SimulationComparisonSummary;
import cbit.vcell.solver.test.MathTestingUtilities;
import cbit.vcell.modeldb.DatabasePolicySQL;
import cbit.vcell.modeldb.DbDriver;


public class SBVCVerifier {
	private AdminDatabaseServer adminDbServer = null;
	private cbit.sql.ConnectionFactory conFactory = null;
	private DatabaseServerImpl dbServerImpl = null;
	private cbit.sql.KeyFactory keyFactory = null;
	private DBCacheTable cacheTable = null;
	private cbit.util.SessionLog log = null;

/**
 * ResultSetCrawler constructor comment.
 */
public SBVCVerifier(ConnectionFactory argConFactory, KeyFactory argKeyFactory, AdminDatabaseServer argAdminDbServer, SessionLog argSessionLog, DBCacheTable dbCacheTable) throws DataAccessException, SQLException {
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
			System.out.println("Usage: cbit.vcell.vcml.SBVCVerifier -all logfileSpec (-includeBM) [includefile]");
			System.out.println("       cbit.vcell.vcml.SBVCVerifier userid logfileSpec [biomodelkey]");
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
		
		SessionLog log = new cbit.util.StdoutSessionLog("Admin");
		cbit.sql.ConnectionFactory conFactory = new cbit.sql.OraclePoolingConnectionFactory(log);
		cbit.sql.KeyFactory keyFactory = new cbit.sql.OracleKeyFactory();
		DbDriver.setKeyFactory(keyFactory);
		LocalAdminDbServer adminDbServer = new LocalAdminDbServer(conFactory,keyFactory,log);
		DBCacheTable dbCacheTable = new DBCacheTable(10000000);

		SBVCVerifier sbvcVerifier = new SBVCVerifier(conFactory, keyFactory, adminDbServer, log, dbCacheTable);

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
				sbvcVerifier.scanBioModels((KeyValue[])cbit.util.BeanUtils.getArray(includeKeyList,KeyValue.class));
			}
		} else {
			sbvcVerifier.scanBioModels(users);
		}
	} catch (Throwable exception) {
		System.out.println("Exception occurred in main() of SBVCVerifier");
		exception.printStackTrace(System.out);
	} finally {
		System.exit(0);
	}
}


/**
 * Scans biomodels in the database and retrieves the applications that are 
 */
private String printComparisonReport(SimulationComparisonSummary simCompSummary, double absError, double relError) {

	StringBuffer reportStrBuffer = new StringBuffer();
	
	// Get all the variable comparison summaries and the failed ones from simComparisonSummary to print out report.
	VariableComparisonSummary[] failedVarSummaries = simCompSummary.getFailingVariableComparisonSummaries(absError, relError);
	VariableComparisonSummary[] allVarSummaries = simCompSummary.getVariableComparisonSummaries();

	if (failedVarSummaries.length>0){
		// Failed simulation
		reportStrBuffer.append("\t\tTolerance test FAILED \n");
		reportStrBuffer.append("\t\tFailed Variables : \n");
		for (int m = 0; m < failedVarSummaries.length; m++){
			reportStrBuffer.append("\t\t\t"+failedVarSummaries[m].toShortString()+"\n");
		}							
	} else {
		reportStrBuffer.append("\t\tTolerance test PASSED \n");
	}

	reportStrBuffer.append("\t\tPassed Variables : \n");
	// Check if varSummary exists in failed summaries list. If not, simulation passed.
	for (int m = 0; m < allVarSummaries.length; m++) {
		if (!cbit.util.BeanUtils.arrayContains(failedVarSummaries, allVarSummaries[m])) {
			reportStrBuffer.append("\t\t\t"+allVarSummaries[m].toShortString()+"\n");
		}
	}
	
	return reportStrBuffer.toString();
}



/**
 * Scans biomodels in the database and retrieves the applications that are 
 */
public void scanBioModels(KeyValue[] bioModelKeys) throws MathException, MappingException, SQLException, DataAccessException, ModelException, ExpressionException {

//	User user = new User("Administrator", new cbit.sql.KeyValue("2"));
	User user = new User("anu", new cbit.util.KeyValue("2302355"));
	SessionLog userLog = new cbit.util.StdoutSessionLog(user.toString());
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

			//
			// Check each application to see if it is a compartmental application. Grab only the compartmental simContexts.
			//

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
			
			SimulationContext simContexts[] = bioModel_1.getSimulationContexts();
			for (int k = 0; k < simContexts.length; k++){
				if (simContexts[k].getGeometry().getDimension() > 0) {
					continue;
				} else {
					System.out.println("\n Biomodel Key : " + bioModelKeys[i].toString() + "\t; Name : " + bioModel_1.getName() + ";\t Application : " + simContexts[k].getName() + "\n");

					// Roundtrip (export -> import) this application to SBML (L2V1) and back ...
					
					// Export this application from the biomodel into SBML

					// First get a structure from the model, and set its size (thro' the structureMapping in the geometry of the simContext)
					// invoke the structureSizeEvaluator to compute and set the sizes of the remaining structures.
					Structure structure = simContexts[k].getModel().getStructures(0);
					double structureSize = 1.0;
					cbit.vcell.vcml.StructureSizeSolver ssEvaluator = new cbit.vcell.vcml.StructureSizeSolver();
					StructureMapping structMapping = simContexts[k].getGeometryContext().getStructureMapping(structure); 
					ssEvaluator.updateAbsoluteStructureSizes(simContexts[k], structure, structureSize, structMapping.getSizeParameter().getUnitDefinition());

					// now export to SBML
					String exportedSBMLStr = cbit.vcell.xml.XmlHelper.exportXML(bioModel_1, XmlDialect.SBML_L2V1, simContexts[k].getName());
					
					// Import the exported model
					if (exportedSBMLStr == null) {
						System.err.println(bioModel_1.getName() + " : " + simContexts[k].getName() +" could not be exported into SBML - null string");
						System.out.println(bioModel_1.getName() + " : " + simContexts[k].getName() +" could not be exported into SBML - null string");
						continue;
					}
					
					BioModel bioModel_2 = (BioModel) cbit.vcell.xml.XmlHelper.importXMLVerbose(logger, exportedSBMLStr, XmlDialect.SBML_L2V1);
					String bioModelXML_2 = cbit.vcell.xml.XmlHelper.bioModelToXML(bioModel_2);
					//
			        // Generate math and create a single simulation for the original simContext and for the model that was exported to SBML
			        // and imported back into the VCell. If there is a fast system in the application, use the Forward Euler solver,
			        // else use the NativeIDA solver.
					//
					double endTime = 5.0;
					int numTimeSteps = 50;

					// 
					// For the original simContext
					//
			        MathMapping mathMapping_1 = new MathMapping(simContexts[k]);
			        MathDescription mathDesc_1 = mathMapping_1.getMathDescription();
			        simContexts[k].setMathDescription(mathDesc_1);
			        SimulationVersion simVersion_1 = new SimulationVersion(new KeyValue("100"), "sim_1_1", simContexts[k].getVersion().getOwner(), new cbit.util.GroupAccessNone(), null, new java.math.BigDecimal(1.0), new java.util.Date(), VersionFlag.Archived, "", null);
			        Simulation sim_11 = new Simulation(simVersion_1, mathDesc_1);
			        sim_11.setName("sim_1_1");
			        sim_11.getSolverTaskDescription().setTimeBounds(new cbit.vcell.simulation.TimeBounds(0, endTime));
			        TimeStep timeStep_1 = new TimeStep();
			        sim_11.getSolverTaskDescription().setTimeStep(new TimeStep(timeStep_1.getMinimumTimeStep(),timeStep_1.getDefaultTimeStep(),endTime/10000));
			        sim_11.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec((endTime-0)/numTimeSteps));
			        sim_11.getSolverTaskDescription().setErrorTolerance(new ErrorTolerance(1e-10, 1e-12));

					// solve the original simulation - if math has fast system, use RK-Fehlberg solver, else use NativeIDA solver
					boolean hasFastSystems = false;
					if (mathDesc_1.hasFastSystems()) {
						hasFastSystems = true;
					}
					ODESolverResultSet odeSolverResultSet_1 = solveSimulation(sim_11, hasFastSystems);

					// 
					// For the imported simContext
					//
					SimulationContext simContext_2 = bioModel_2.getSimulationContexts(0);
			        MathMapping mathMapping_2 = new MathMapping(simContext_2);
			        MathDescription mathDesc_2 = mathMapping_2.getMathDescription();
			        simContext_2.setMathDescription(mathDesc_2);
			        SimulationVersion simVersion_2 = new SimulationVersion(new KeyValue("100"), "sim_1_2", bioModel_1.getVersion().getOwner(), new cbit.util.GroupAccessNone(), null, new java.math.BigDecimal(1.0), new java.util.Date(), VersionFlag.Archived, "", null);
			        Simulation sim_12 = new Simulation(simVersion_2, mathDesc_2);
			        sim_12.setName("sim_1_2");
			        sim_12.getSolverTaskDescription().setTimeBounds(new cbit.vcell.simulation.TimeBounds(0, endTime));
			        TimeStep timeStep_2 = new TimeStep();
			        sim_12.getSolverTaskDescription().setTimeStep(new TimeStep(timeStep_2.getMinimumTimeStep(),timeStep_2.getDefaultTimeStep(),endTime/10000));
			        sim_12.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec((endTime-0)/numTimeSteps));
			        sim_12.getSolverTaskDescription().setErrorTolerance(new ErrorTolerance(1e-10, 1e-12));

			        // Check math equivalency using both the math descriptions
			        StringBuffer reasonBuffer = new StringBuffer();
			        String mathEquivalency = MathDescription.testEquivalency(mathDesc_1, mathDesc_2, reasonBuffer);
			        
					// solve the imported simulation - if math has fast system, use RK-Fehlberg solver, else use NativeIDA solver
					hasFastSystems = false;
					if (mathDesc_2.hasFastSystems()) {
						hasFastSystems = true;
					}
					ODESolverResultSet odeSolverResultSet_2 = solveSimulation(sim_12, hasFastSystems);

					//
					// Now compare the 2 result sets - one from simulation from original biomodel, the other from the round-tripped biomodel.
					//

					// If the # of columns in the 2 result sets are not equal, no point comparing
					if (odeSolverResultSet_1.getColumnDescriptionsCount() != odeSolverResultSet_2.getColumnDescriptionsCount()) {
						throw new RuntimeException("The 2 results sets do not match (unequal columns). Cannot compare the 2 simulations");	
					}

					// Get the variable names to compare in the 2 result sets - needed for MathTestingUtilities.compareResultSets()
					String[] varsToCompare = new String[odeSolverResultSet_1.getColumnDescriptionsCount()];
					for (int j = 0; j < odeSolverResultSet_1.getColumnDescriptionsCount(); j++){
						varsToCompare[j] = odeSolverResultSet_1.getColumnDescriptions(j).getName();
					}

					// Compare the result sets ...
					SimulationComparisonSummary simComparisonSummary = MathTestingUtilities.compareResultSets(odeSolverResultSet_2, odeSolverResultSet_1, varsToCompare);

					// Get comparison report ...
					double absErr = 1e-8;
					double relErr = 1e-8;
					String comparisonReport = printComparisonReport(simComparisonSummary, absErr, relErr);
					System.out.println("\n\n Biomodel Key : " + bioModelKeys[i].toString() + "\t; Name : " + bioModel_1.getName() + ";\t Application : " + simContexts[k].getName() + "\n\n");
					System.out.println("\n Math Equivalency : " + mathEquivalency);
					System.out.println("\n Comparison Report : \n\t\t" + comparisonReport);
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
		SessionLog userLog = new cbit.util.StdoutSessionLog(user.toString());
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

				//
				// Check each application to see if it is a compartmental application. Grab only the compartmental simContexts.
				//

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
				
				SimulationContext simContexts[] = bioModel_1.getSimulationContexts();
				for (int k = 0; k < simContexts.length; k++){
					if (simContexts[k].getGeometry().getDimension() > 0) {
						continue;
					} else {

						// Roundtrip (export -> import) this application to SBML (L2V1) and back ...
						
						// Export this application from the biomodel into SBML

						// First get a structure from the model, and set its size (thro' the structureMapping in the geometry of the simContext)
						// invoke the structureSizeEvaluator to compute and set the sizes of the remaining structures.
						Structure structure = simContexts[k].getModel().getStructures(0);
						double structureSize = 1.0;
						cbit.vcell.vcml.StructureSizeSolver ssEvaluator = new cbit.vcell.vcml.StructureSizeSolver();
						StructureMapping structMapping = simContexts[k].getGeometryContext().getStructureMapping(structure); 
						ssEvaluator.updateAbsoluteStructureSizes(simContexts[k], structure, structureSize, structMapping.getSizeParameter().getUnitDefinition());

						// now export to SBML
						String exportedSBMLStr = cbit.vcell.xml.XmlHelper.exportXML(bioModel_1, XmlDialect.SBML_L2V1, simContexts[k].getName());

						// Import the exported model
						BioModel bioModel_2 = (BioModel) cbit.vcell.xml.XmlHelper.importXMLVerbose(logger, exportedSBMLStr, XmlDialect.SBML_L2V1);

						String bioModelXML_2 = cbit.vcell.xml.XmlHelper.bioModelToXML(bioModel_2);
						//
				        // Generate math and create a single simulation for the original simContext and for the model that was exported to SBML
				        // and imported back into the VCell. If there is a fast system in the application, use the RungeKatta-Fehlberg solver,
				        // else use the NativeIDA solver.
						//
						double endTime = 1.0;
						int numTimeSteps = 50;

						// 
						// For the original simContext
						// 
				        MathMapping mathMapping_1 = new MathMapping(simContexts[k]);
				        MathDescription mathDesc_1 = mathMapping_1.getMathDescription();
				        simContexts[k].setMathDescription(mathDesc_1);
				        SimulationVersion simVersion_1 = new SimulationVersion(new KeyValue("100"), "sim_1_1", null, null, null, null, null, null, null, null);
				        Simulation sim_11 = new Simulation(simVersion_1, mathDesc_1);
				        sim_11.setName("sim_1_1");
				        sim_11.getSolverTaskDescription().setTimeBounds(new cbit.vcell.simulation.TimeBounds(0, endTime));
				        TimeStep timeStep_1 = new TimeStep();
				        sim_11.getSolverTaskDescription().setTimeStep(new TimeStep(timeStep_1.getMinimumTimeStep(),timeStep_1.getDefaultTimeStep(),endTime/10000));
				        sim_11.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec((endTime-0)/numTimeSteps));
				        sim_11.getSolverTaskDescription().setErrorTolerance(new ErrorTolerance(1e-10, 1e-12));

						// solve the original simulation - if math has fast system, use RK-Fehlberg solver, else use NativeIDA solver
						boolean hasFastSystems = false;
						if (mathDesc_1.hasFastSystems()) {
							hasFastSystems = true;
						}
						ODESolverResultSet odeSolverResultSet_1 = solveSimulation(sim_11, hasFastSystems);

						// 
						// For the imported simContext
						//
						SimulationContext simContext_2 = bioModel_2.getSimulationContexts(0);
				        MathMapping mathMapping_2 = new MathMapping(simContext_2);
				        MathDescription mathDesc_2 = mathMapping_2.getMathDescription();
				        simContext_2.setMathDescription(mathDesc_2);
				        SimulationVersion simVersion_2 = new SimulationVersion(new KeyValue("100"), "sim_1_2", null, null, null, null, null, null, null, null);
				        Simulation sim_12 = new Simulation(simVersion_2, mathDesc_2);
				        sim_12.setName("sim_1_2");
				        sim_12.getSolverTaskDescription().setTimeBounds(new cbit.vcell.simulation.TimeBounds(0, endTime));
				        TimeStep timeStep_2 = new TimeStep();
				        sim_12.getSolverTaskDescription().setTimeStep(new TimeStep(timeStep_2.getMinimumTimeStep(),timeStep_2.getDefaultTimeStep(),endTime/10000));
				        sim_12.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec((endTime-0)/numTimeSteps));
				        sim_12.getSolverTaskDescription().setErrorTolerance(new ErrorTolerance(1e-10, 1e-12));

						// solve the imported simulation - if math has fast system, use RK-Fehlberg solver, else use NativeIDA solver
						hasFastSystems = false;
						if (mathDesc_2.hasFastSystems()) {
							hasFastSystems = true;
						}
						ODESolverResultSet odeSolverResultSet_2 = solveSimulation(sim_12, hasFastSystems);

						//
						// Now compare the 2 result sets - one from simulation from original biomodel, the other from the round-tripped biomodel.
						//

						// If the # of columns in the 2 result sets are not equal, no point comparing
						if (odeSolverResultSet_1.getColumnDescriptionsCount() != odeSolverResultSet_2.getColumnDescriptionsCount()) {
							throw new RuntimeException("The 2 results sets do not match (unequal columns). Cannot compare the 2 simulations");	
						}

						// Get the variable names to compare in the 2 result sets - needed for MathTestingUtilities.compareResultSets()
						String[] varsToCompare = new String[odeSolverResultSet_1.getColumnDescriptionsCount()];
						for (int j = 0; j < odeSolverResultSet_1.getColumnDescriptionsCount(); j++){
							varsToCompare[j] = odeSolverResultSet_1.getColumnDescriptions(j).getName();
						}

						// Compare the result sets ...
						SimulationComparisonSummary simComparisonSummary = MathTestingUtilities.compareResultSets(odeSolverResultSet_2, odeSolverResultSet_1, varsToCompare);

						// Get comparison report ...
						double absErr = 1e-8;
						double relErr = 1e-8;
						String comparisonReport = printComparisonReport(simComparisonSummary, absErr, relErr);
						System.out.println("Comparison Report : \n\n\t\t" + comparisonReport);
					} // else
				} // for - simcontexts - k
			}catch (Throwable e){
	            log.exception(e); // exception in whole BioModel
			}
        } // end - for i - bioModelInfos
	}
}



/**
 * Scans biomodels in the database and retrieves the applications that are 
 */
private ODESolverResultSet solveSimulation(Simulation sim, boolean hasFastSystems) {

	//
	// If hasFastSystems in true, use Forward Euler solver, else use NativeIDA solver
	//

	ODESolverResultSet odeSolverResultSet = null;
	if (hasFastSystems) {
		// Forward Euler solver

		// reset time step for Forward Euler solver from 0.1 to 1e-4
		double FE_Solver_timeStep = 1e-4;
		try {
			sim.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec(FE_Solver_timeStep));
			// sim.getSolverTaskDescription().setErrorTolerance(new ErrorTolerance(1e-10, 1e-12));
		} catch (PropertyVetoException pve) {
			pve.printStackTrace(System.out);
		}
		
        java.io.File directory = new java.io.File("C:\\VCell\\SBMLValidationSuiteTests");
        SessionLog sessionLog = new StdoutSessionLog("VCELL");
        ODESolver odeSolver = null;
        try {
	        odeSolver = new cbit.vcell.solver.ode.ForwardEulerSolver(new SimulationJob(sim, 0), directory, sessionLog);
        } catch (SolverException e) {
	        e.printStackTrace(System.out);
	        throw new RuntimeException("Error initializing RK-Fehlberg solver : " + e.getMessage());
        }
        odeSolver.startSolver();
        while (odeSolver.getSolverStatus().isRunning()) {
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        while (odeSolver.getSolverStatus().getStatus() == SolverStatus.SOLVER_READY
            || odeSolver.getSolverStatus().getStatus() == SolverStatus.SOLVER_RUNNING
            || odeSolver.getSolverStatus().getStatus() == SolverStatus.SOLVER_STARTING) {
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        if (odeSolver.getSolverStatus().getStatus() == SolverStatus.SOLVER_ABORTED) {
            System.err.println("Simulation failed: " + odeSolver.getSolverStatus());
            System.exit(1);
        }

		// get simulation results 
		odeSolverResultSet = odeSolver.getODESolverResultSet();
	} else {
		// NativeIDA solver
		RowColumnResultSet rcResultSet = null;
		try {
			IDAFileWriter idaFileWriter = new IDAFileWriter(sim);
			idaFileWriter.initialize();
			java.io.StringWriter stringWriter = new java.io.StringWriter();
			idaFileWriter.writeIDAFile(new java.io.PrintWriter(stringWriter,true));
			stringWriter.close();
			StringBuffer buffer = stringWriter.getBuffer();
			String idaInputString = buffer.toString();

			final cbit.vcell.solvers.NativeIDASolver nativeIDASolver = new cbit.vcell.solvers.NativeIDASolver();
			System.out.println(idaInputString);
			rcResultSet = nativeIDASolver.solve(idaInputString);
		} catch (Exception e) {
	        e.printStackTrace(System.out);
	        throw new RuntimeException("Error running NativeIDA solver : " + e.getMessage());
		}

		// get simulation results - copy from RowColumnResultSet into OdeSolverResultSet
		odeSolverResultSet = new ODESolverResultSet();
		for (int i = 0; i < rcResultSet.getDataColumnCount(); i++){
			odeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(rcResultSet.getColumnDescriptions(i).getName()));
		}
		for (int i = 0; i < rcResultSet.getRowCount(); i++){
			odeSolverResultSet.addRow(rcResultSet.getRow(i));
		}
		// add appropriate Function columns to result set
		cbit.vcell.math.Function functions[] = sim.getFunctions();
		for (int i = 0; i < functions.length; i++){
			if (cbit.vcell.simdata.FunctionFileGenerator.isFunctionSaved(functions[i])){
				Expression exp1 = new Expression(functions[i].getExpression());
				try {
					exp1 = sim.substituteFunctions(exp1);
				} catch (cbit.vcell.math.MathException e) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
				} catch (cbit.vcell.parser.ExpressionException e) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
				}
				
				try {
					FunctionColumnDescription cd = new FunctionColumnDescription(exp1.flatten(),functions[i].getName(), null, functions[i].getName(), false);
					odeSolverResultSet.addFunctionColumn(cd);
				}catch (cbit.vcell.parser.ExpressionException e){
					e.printStackTrace(System.out);
				}
			}
		}
	}
	
	return odeSolverResultSet;
}



}