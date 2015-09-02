package org.vcell.stochtest;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringEscapeUtils;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.DataAccessException;
import org.vcell.util.FileUtils;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.image.ImageException;
import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.sql.OracleKeyFactory;
import cbit.sql.OraclePoolingConnectionFactory;
import cbit.sql.QueryHashtable;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.ClientSimManager;
import cbit.vcell.client.ClientTaskManager;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.gui.MathMappingCallbackTaskAdapter;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.modeldb.DatabasePolicySQL;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.ServerDocumentManager;
import cbit.vcell.modeldb.StochtestRunTable;
import cbit.vcell.modeldb.StochtestTable;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.ODEDataBlock;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;

import com.google.gson.Gson;


public class StochtestService {
	
	public enum StochtestMathType {
		ode("ode"),
		rules("rules"),
		pde("pde"),
		nonspatialstochastic("nonspatial-stochastic"),
		spatialstochastic("spatial-stochastic");
		
		private final String databaseTag;
		
		StochtestMathType(String databaseTag){
			this.databaseTag = databaseTag;
		}
		
		public String getDatabaseTag(){
			return this.databaseTag;
		}
		
		public static StochtestMathType fromDatabaseTag(String databaseTag){
			for (StochtestMathType m : values()){
				if (m.getDatabaseTag().equals(databaseTag)){
					return m;
				}
			}
			return null;
		}
	}
	
	public static class Stochtest {
		public KeyValue key;
		public KeyValue simContextRef;
		public KeyValue biomodelRef;
		public KeyValue mathRef;
		public int dimension;
		public int numcompartments;
		public StochtestMathType mathType;
	}
	
	
	public static enum StochtestRunStatus {
		none,
		waiting,
		accepted,
		complete,
		failed
	};
	
	
	public static class StochtestRun {
		public KeyValue key;
		public Stochtest stochtest;
		public StochtestMathType parentMathType;
		public StochtestMathType mathType;
		public StochtestRunStatus status;
		public String errmsg;
	}
	
	public static class StochtestCompare {
		public KeyValue key;
		public KeyValue stochtestRun1ref;
		public KeyValue stochtestRun2ref;
		public String results;
		public String status;
	}
	
	

	private cbit.sql.ConnectionFactory conFactory = null;
	private DatabaseServerImpl dbServerImpl = null;
	private cbit.sql.KeyFactory keyFactory = null;
	private File baseDir = null;
	private int numTrials;


	public StochtestService(File baseDir, int numTrials, ConnectionFactory argConFactory, KeyFactory argKeyFactory, SessionLog argSessionLog) 
			throws DataAccessException, SQLException {
		this.conFactory = argConFactory;
		this.keyFactory = argKeyFactory;
		this.dbServerImpl = new DatabaseServerImpl(conFactory,keyFactory,argSessionLog);
		this.baseDir = baseDir;
		this.numTrials = numTrials;
	}

	public static void main(String[] args) {
		
	try {
		
		if (args.length!=2){
			System.out.println("Usage:  StochtestService baseDirectory numtrials");
			System.exit(-1);
		}
		File baseDir = new File(args[0]);
		if (!baseDir.exists()){
			throw new RuntimeException("base directory "+baseDir.getPath()+" not found");
		}
		
		int numTrials = Integer.valueOf(args[1]);
		
		PropertyLoader.loadProperties();
	    SessionLog sessionLog = new StdoutSessionLog("StochtestService");

		DatabasePolicySQL.bAllowAdministrativeAccess = true;
	    String driverName = PropertyLoader.getRequiredProperty(PropertyLoader.dbDriverName);
	    String connectURL = PropertyLoader.getRequiredProperty(PropertyLoader.dbConnectURL);
	    String dbSchemaUser = PropertyLoader.getRequiredProperty(PropertyLoader.dbUserid);
	    String dbPassword = PropertyLoader.getRequiredProperty(PropertyLoader.dbPassword);
	    //
	    // get appropriate database factory objects
	    //
	    ConnectionFactory conFactory = new OraclePoolingConnectionFactory(sessionLog,driverName,connectURL,dbSchemaUser,dbPassword);
	    KeyFactory keyFactory = new OracleKeyFactory();    
	    StochtestService stochtestService = new StochtestService(baseDir, numTrials, conFactory, keyFactory, sessionLog);
	    
	    while (true){
	    	stochtestService.runOne();
	    }
	    
	} catch (Throwable e) {
	    e.printStackTrace(System.out);
	}
    System.exit(0);
	}

	public void runOne() throws IllegalArgumentException, SQLException, DataAccessException, XmlParseException, PropertyVetoException, ExpressionException, MappingException, GeometryException, ImageException, IOException{
		
	    StochtestRun stochtestRun = acceptNextWaitingJob();
	    String biomodelXML = null;
	    if (stochtestRun!=null){
	    	try {
		    	User user = new User(PropertyLoader.ADMINISTRATOR_ACCOUNT, new KeyValue(PropertyLoader.ADMINISTRATOR_ID));
		    	ServerDocumentManager serverDocumentManager = new ServerDocumentManager(this.dbServerImpl);
		    	biomodelXML = serverDocumentManager.getBioModelXML(new QueryHashtable(), user, stochtestRun.stochtest.biomodelRef, true);
		    	BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(biomodelXML));
		    	bioModel.refreshDependencies();
		    	
		    	SimulationContext srcSimContext = null;
		    	for (SimulationContext sc : bioModel.getSimulationContexts()){
		    		if (sc.getKey().equals(stochtestRun.stochtest.simContextRef)){
		    			srcSimContext = sc;
		    		}
		    	}
		    	
		    	if (srcSimContext==null){
		    		throw new RuntimeException("cannot find simcontext with key="+stochtestRun.stochtest.simContextRef);
		    	}
		    	
		    	SimulationContext simContext = srcSimContext;
		    	StochtestMathType parentMathType = stochtestRun.parentMathType;
		    	StochtestMathType mathType = stochtestRun.mathType;
		    	if (parentMathType != mathType){
		    		if (parentMathType == StochtestMathType.nonspatialstochastic && mathType == StochtestMathType.rules){
		    			simContext = ClientTaskManager.copySimulationContext(srcSimContext, "generatedRules", false, Application.RULE_BASED_STOCHASTIC);
		    		}else if (parentMathType == StochtestMathType.rules && mathType == StochtestMathType.nonspatialstochastic){
		    			simContext = ClientTaskManager.copySimulationContext(srcSimContext, "generatedSSA", false, Application.NETWORK_STOCHASTIC);
		    	   	}else{
		    	   		throw new RuntimeException("unexpected copy of simcontext from "+parentMathType+" to "+mathType);
		    	   	}
		    		bioModel.addSimulationContext(simContext);
		    	}
		    	
		    	
				File baseDirectory = createDirFile(baseDir, stochtestRun);
				OutputTimeSpec outputTimeSpec = new UniformOutputTimeSpec(0.5);
				double endTime = 10.0;
				computeTrials(simContext, stochtestRun, baseDirectory, outputTimeSpec, endTime, numTrials);
				finalizeAcceptedJob(stochtestRun, StochtestRunStatus.complete,null);
	    	}catch (Exception e){
				finalizeAcceptedJob(stochtestRun, StochtestRunStatus.failed,e.getMessage());
				//
				// write original biomodelXML to a .vcml file
				//
				if (biomodelXML!=null){
					XmlUtil.writeXMLStringToFile(biomodelXML, new File(baseDir,"stochtestrun_"+stochtestRun.stochtest.key+".vcml").getPath(), false);
				}
				
				//
				// write exception trace to .txt file
				//
				StringWriter stringWriter = new StringWriter();
				PrintWriter printWriter = new PrintWriter(stringWriter);
				e.printStackTrace(printWriter);
				printWriter.flush();
				System.out.println(stringWriter.getBuffer().toString());
				XmlUtil.writeXMLStringToFile(stringWriter.getBuffer().toString(), new File(baseDir,"stochtestrun_"+stochtestRun.stochtest.key+"_error.txt").getPath(), false);
	    	}
	    }else{
	    	System.out.println("no jobs waiting");
	    	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

	}
	
	private StochtestRun acceptNextWaitingJob() throws IllegalArgumentException, SQLException, DataAccessException {

		//
		// grab next waiting simulation, change status to accepted, commit and return
		//
		java.sql.Connection con = conFactory.getConnection(new Object());
		StochtestRun stochtestRun = null;
		try {
			java.sql.Statement stmt = null;
			try {
				StochtestTable stochtestTable = StochtestTable.table;
				StochtestRunTable stochtestRunTable = StochtestRunTable.table;
				String sql = "SELECT "+stochtestTable.getSQLColumnList(true, false, "_1") + " , " + stochtestRunTable.getSQLColumnList(true, false, "_2") +
						" FROM "+stochtestRunTable.getTableName() + ", " + stochtestTable.getTableName() + 
						" WHERE "+stochtestTable.id.getQualifiedColName()+" = "+stochtestRunTable.stochtestref.getQualifiedColName() +
						" AND "+stochtestRunTable.status.getQualifiedColName()+" = "+"'"+StochtestRunStatus.waiting+"'" +
						" AND ROWNUM = 1" +
						" FOR UPDATE " +
						" ORDER BY "+stochtestRunTable.id.getQualifiedColName();
				stmt = con.createStatement();
System.out.println(sql);
			    ResultSet rset = stmt.executeQuery(sql);
			    if (rset.next()) {
			    	Stochtest stochtest = new Stochtest();
java.sql.ResultSetMetaData rsetMetaData = rset.getMetaData();
int numColumns = rsetMetaData.getColumnCount();
for (int i = 0; i < numColumns; i++){
	System.out.println("RESULTS "+i+") table_name="+rsetMetaData.getTableName(i+1)+
			", catalog_name="+rsetMetaData.getCatalogName(i+1)+
			", col_class_name="+rsetMetaData.getColumnClassName(i+1)+
			", col_type_name="+rsetMetaData.getColumnTypeName(i+1)+
			", col_name="+rsetMetaData.getColumnName(i+1)+
			", col_label="+rsetMetaData.getColumnLabel(i+1)+
			", schema_name="+rsetMetaData.getSchemaName(i+1));
}

			    	stochtest.key = new KeyValue(rset.getBigDecimal(stochtestTable.id.getUnqualifiedColName()+"_1"));
			    	stochtest.simContextRef = new KeyValue(rset.getBigDecimal(stochtestTable.simcontextref.getUnqualifiedColName()+"_1"));
			    	stochtest.biomodelRef = new KeyValue(rset.getBigDecimal(stochtestTable.biomodelref.getUnqualifiedColName()+"_1"));
			    	stochtest.mathRef = new KeyValue(rset.getBigDecimal(stochtestTable.mathref.getUnqualifiedColName()+"_1"));
			    	stochtest.dimension = rset.getInt(stochtestTable.dimension.getUnqualifiedColName()+"_1");
			    	stochtest.numcompartments = rset.getInt(stochtestTable.numcompartments.getUnqualifiedColName()+"_1");
			    	stochtest.mathType = StochtestMathType.fromDatabaseTag(rset.getString(stochtestTable.mathtype.getUnqualifiedColName()+"_1"));
			        
			    	stochtestRun = new StochtestRun();
			        stochtestRun.key = new KeyValue(rset.getBigDecimal(stochtestRunTable.id.getUnqualifiedColName()+"_2"));
			        stochtestRun.stochtest = stochtest;
			        stochtestRun.parentMathType = StochtestMathType.fromDatabaseTag(rset.getString(stochtestRunTable.parentmathtype.getUnqualifiedColName()+"_2"));
			        stochtestRun.mathType = StochtestMathType.fromDatabaseTag(rset.getString(stochtestRunTable.mathtype.getUnqualifiedColName()+"_2"));
			        stochtestRun.status = StochtestRunStatus.valueOf(rset.getString(stochtestRunTable.status.getUnqualifiedColName()+"_2"));
			        stochtestRun.errmsg = rset.getString(stochtestRunTable.errmsg.getUnqualifiedColName()+"_2");
			    }else{
			    	return null;
			    }
			} finally {
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			}
			
			
			try {
				StochtestRunTable stochtestRunTable = StochtestRunTable.table;
				String sql = "UPDATE "+stochtestRunTable.getTableName() +
						" SET "+stochtestRunTable.status.getUnqualifiedColName() + " = " + "'"+StochtestRunStatus.accepted+"'" +
						" WHERE "+stochtestRunTable.id.getUnqualifiedColName()+" = " + stochtestRun.key.toString();
				stmt = con.createStatement();
System.out.println(sql);
			    int numrows = stmt.executeUpdate(sql);
			    if (numrows != 1){
			    	throw new DataAccessException("failed to update mathgen status for id = "+stochtestRun.key);
			    }
			} finally {
				if (stmt != null) {
					stmt.close();
				}
			}
			con.commit();
		}catch (DataAccessException | SQLException e){
			e.printStackTrace();
			con.rollback();
			throw e;
		}finally {
			con.close();
		}
		return stochtestRun;
	}
	
	private void finalizeAcceptedJob(StochtestRun acceptedStochtestRun, StochtestRunStatus newStatus, String errmsg) throws IllegalArgumentException, SQLException, DataAccessException {

		if (newStatus != StochtestRunStatus.complete && newStatus != StochtestRunStatus.failed){
			throw new RuntimeException("new status is "+newStatus+", expecting "+StochtestRunStatus.complete+" or "+StochtestRunStatus.failed);
		}
		
		//
		// update status
		//
		java.sql.Connection con = conFactory.getConnection(new Object());
		try {
			java.sql.Statement stmt = null;
			try {
				StochtestRunTable stochtestRunTable = StochtestRunTable.table;
				String sql = null;
				if (errmsg==null){
					sql = "UPDATE "+stochtestRunTable.getTableName() +
						" SET "+stochtestRunTable.status.getUnqualifiedColName() + " = " + "'"+newStatus.name()+"'" + ", " +
							    stochtestRunTable.errmsg.getUnqualifiedColName() + " = NULL" +
						" WHERE "+stochtestRunTable.id.getUnqualifiedColName()+" = " + acceptedStochtestRun.key.toString();
				}else{
					sql = "UPDATE "+stochtestRunTable.getTableName() +
							" SET "+stochtestRunTable.status.getUnqualifiedColName() + " = " + "'"+newStatus.name()+"'" + ", " +
								    stochtestRunTable.errmsg.getUnqualifiedColName() + " = " + "'"+TokenMangler.getSQLEscapedString(errmsg, 4000)+"'" +
							" WHERE "+stochtestRunTable.id.getUnqualifiedColName()+" = " + acceptedStochtestRun.key.toString();
				}
				stmt = con.createStatement();
System.out.println(sql);
			    int numrows = stmt.executeUpdate(sql);
			    if (numrows != 1){
			    	throw new DataAccessException("failed to update mathgen status for id = "+acceptedStochtestRun.key);
			    }
			} finally {
				if (stmt != null) {
					stmt.close();
				}
			}
			con.commit();
		}catch (DataAccessException | SQLException e){
			e.printStackTrace();
			con.rollback();
			throw e;
		}finally {
			con.close();
		}
	}

	private void computeTrials(SimulationContext simContext, StochtestRun stochtestRun, File baseDirectory, OutputTimeSpec outputTimeSpec, double endTime, int numTrials) throws PropertyVetoException, IOException {
		//
		// make simulation
		//
		simContext.refreshMathDescription(new MathMappingCallbackTaskAdapter(null),NetworkGenerationRequirements.ComputeFullNetwork);
		Simulation sim = simContext.addNewSimulation("stochtestrun_"+stochtestRun.key,new MathMappingCallbackTaskAdapter(null),NetworkGenerationRequirements.ComputeFullNetwork);
		sim.setSimulationOwner(simContext);
		
		//
		// get variables to save
		//
		simContext.getModel().getSpeciesContexts();
		ArrayList<String> varNameList = new ArrayList<String>();
		for (SpeciesContextSpec scs : simContext.getReactionContext().getSpeciesContextSpecs()){
			varNameList.add(scs.getSpeciesContext().getName());
		}
		String[] varNames = varNameList.toArray(new String[0]);
		StdoutSessionLog log = new StdoutSessionLog(sim.getName());

		//
		// get time points to save
		//
		ArrayList<Double> sampleTimeList = new ArrayList<Double>();
		if (outputTimeSpec instanceof UniformOutputTimeSpec){
			double dT = ((UniformOutputTimeSpec)outputTimeSpec).getOutputTimeStep();
			int currTimeIndex=0;
			while (currTimeIndex*dT <= (endTime+1e-8)){
				sampleTimeList.add(currTimeIndex*dT);
				currTimeIndex++;
			}
		}
		double[] sampleTimes = new double[sampleTimeList.size()];
		for (int i=0;i<sampleTimes.length;i++){
			sampleTimes[i] = sampleTimeList.get(i);
		}
		
		//
		// run N trials and save data
		//
		TimeSeriesMultitrialData sampleData = new TimeSeriesMultitrialData(sim.getName(),varNames, sampleTimes, numTrials);
		runsolver(sim,log,baseDirectory,numTrials,sampleData);
		writeData(sampleData);
	}
	
	
	private void writeData(TimeSeriesMultitrialData sampleData) throws IOException {
		Gson gson = new Gson();
		String json = gson.toJson(sampleData);
		FileUtils.writeByteArrayToFile(json.toString().getBytes(), new File(baseDir,sampleData.datasetName+".json"));
	}
	
	private static File createDirFile(File baseDir, StochtestRun stochtestRun){
		
		File dirFile = new File(baseDir,"stochtestrun_"+stochtestRun.key);
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
		return dirFile;
	}
	
	
	private static void runsolver(Simulation newSimulation, StdoutSessionLog sessionLog, File baseDirectory, int numRuns, TimeSeriesMultitrialData timeSeriesMultitrialData){
		Simulation versSimulation = null;
		File destDir = null;
		boolean bTimeout = false;
//		int progress = 1;
		for(int trialIndex=0;trialIndex<numRuns;trialIndex++){
			long startTime = System.currentTimeMillis();
//			if(i >= (progress*numRuns/10)){
//				printout(progress+" ");
//				progress++;
//			}
			try{
				versSimulation = new ClientSimManager.TempSimulation(newSimulation, false);
//				printout(ruleBasedTestDir.getAbsolutePath());
				destDir = new File(baseDirectory,timeSeriesMultitrialData.datasetName);
				SimulationTask simTask = new SimulationTask(new SimulationJob(versSimulation, 0, null),0);
				Solver solver = ClientSimManager.createQuickRunSolver(sessionLog, destDir, simTask);
				solver.startSolver();
		
				while (true){
					try { 
						Thread.sleep(250); 
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if (System.currentTimeMillis() - startTime > 30*1000){
						// timeout after 30 seconds .. otherwise multiple runs will take forever
						bTimeout = true;
						solver.stopSolver();
						throw new RuntimeException("timed out");
					}
		
					SolverStatus solverStatus = solver.getSolverStatus();
					if (solverStatus != null) {
						if (solverStatus.getStatus() == SolverStatus.SOLVER_ABORTED) {
							throw new RuntimeException(solverStatus.getSimulationMessage().getDisplayMessage());
						}
						if (solverStatus.getStatus() != SolverStatus.SOLVER_STARTING &&
							solverStatus.getStatus() != SolverStatus.SOLVER_READY &&
							solverStatus.getStatus() != SolverStatus.SOLVER_RUNNING){
							break;
						}
					}		
				}
				SimulationData simData = new SimulationData(simTask.getSimulationJob().getVCDataIdentifier(), destDir, null, null);
				ODEDataBlock odeDataBlock = simData.getODEDataBlock();
				ODESimData odeSimData = odeDataBlock.getODESimData();
				timeSeriesMultitrialData.addDataSet(odeSimData,trialIndex);
			}catch(Exception e){
				e.printStackTrace();
				File file = new File(baseDirectory,Simulation.createSimulationID(versSimulation.getKey())+"_solverExc.txt");
				writeMessageTofile(file,e.getMessage());
				if (bTimeout){
					throw new RuntimeException("timed out");
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			clearDir(destDir);
		}
//		printout("\n");
	}
	
	private static void clearDir(File dirName){
		File[] files = dirName.listFiles();
		for (int i = 0; files!=null && i < files.length; i++) {
			if(files[i].isDirectory()){
				clearDir(files[i]);
			}else{
				files[i].delete();
			}
		}
		dirName.delete();
	}
	
	private static void writeMessageTofile(File file,String message){
		try{
			FileUtils.writeByteArrayToFile(message.getBytes(), file);
		}catch(Exception e2){
			e2.printStackTrace();
		}	
	}
}
