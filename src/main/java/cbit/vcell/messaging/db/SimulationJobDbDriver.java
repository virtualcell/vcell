/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.messaging.db;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import com.google.gson.Gson;

import cbit.vcell.modeldb.BioModelSimulationLinkTable;
import cbit.vcell.modeldb.DatabaseConstants;
import cbit.vcell.modeldb.MathModelSimulationLinkTable;
import cbit.vcell.modeldb.SimulationTable;
import cbit.vcell.modeldb.UserTable;
import cbit.vcell.server.BioModelLink;
import cbit.vcell.server.MathModelLink;
import cbit.vcell.server.SimpleJobStatusPersistent;
import cbit.vcell.server.SimpleJobStatusQuerySpec;
import cbit.vcell.server.SimulationDocumentLink;
import cbit.vcell.server.SimulationJobStatusPersistent;
import cbit.vcell.server.SimulationJobStatusPersistent.SchedulerStatus;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.SimulationMetadata;

/**
 * Insert the type's description here.
 * Creation date: (9/3/2003 8:54:31 AM)
 * @author: Fei Gao
 */
public class SimulationJobDbDriver {
	private static final SimulationJobTable jobTable = SimulationJobTable.table;
	private static final cbit.vcell.modeldb.SimulationTable simTable = cbit.vcell.modeldb.SimulationTable.table;
	private static final cbit.vcell.modeldb.UserTable userTable = cbit.vcell.modeldb.UserTable.table;
	private static final cbit.vcell.modeldb.MathDescTable mathDescTable = cbit.vcell.modeldb.MathDescTable.table;
	private static final cbit.vcell.modeldb.GeometryTable geometryTable = cbit.vcell.modeldb.GeometryTable.table;
	private org.vcell.util.SessionLog log = null;
	private java.lang.String standardJobStatusSQL = null;

/**
 * LocalDBManager constructor comment.
 */
public SimulationJobDbDriver(SessionLog sessionLog) {
	super();
	this.log = sessionLog;
	standardJobStatusSQL = "SELECT sysdate as " + DatabaseConstants.SYSDATE_COLUMN_NAME + "," + jobTable.getTableName()+".*," + simTable.ownerRef.getQualifiedColName() + "," + userTable.userid.getQualifiedColName()
			+ " FROM " + jobTable.getTableName() + "," + simTable.getTableName() + "," + userTable.getTableName()
			+ " WHERE " + simTable.ownerRef.getQualifiedColName() + "=" + userTable.id.getQualifiedColName()
			+ " AND " + simTable.id.getQualifiedColName() + "=" + jobTable.simRef.getQualifiedColName();
}


/**
 * Insert the method's description here.
 * Creation date: (10/16/2003 11:22:44 AM)
 * @param con java.sql.Connection
 * @param sql java.lang.String
 */
private int executeUpdate(Connection con, String sql) throws SQLException {
	Statement s = con.createStatement();
	try {
		return s.executeUpdate(sql);
	} finally {
		s.close();
	}	
}


/**
 * This method was created in VisualAge.
 * @return int
 * @param user java.lang.String
 * @param imageName java.lang.String
 */
public SimulationJobStatusPersistent[] getActiveJobs(Connection con, VCellServerID serverID) throws SQLException {
	String sql = "SELECT sysdate as " + DatabaseConstants.SYSDATE_COLUMN_NAME + "," + jobTable.getTableName()+".*," + simTable.ownerRef.getQualifiedColName() 
			+ "," + userTable.userid.getQualifiedColName() 
			+ " FROM " + jobTable.getTableName() + "," + simTable.getTableName() + "," + userTable.getTableName()
			+ " WHERE " + simTable.ownerRef.getQualifiedColName() + "=" + userTable.id.getQualifiedColName()
			+ " AND " + simTable.id.getQualifiedColName() + "=" + jobTable.simRef.getQualifiedColName();
			
			
	sql += " AND "
			+ jobTable.schedulerStatus + " in (" + SchedulerStatus.QUEUED.getDatabaseNumber() // in job queue
			+ ","  + SchedulerStatus.DISPATCHED.getDatabaseNumber() // worker just accepted it
			+ "," + SchedulerStatus.RUNNING.getDatabaseNumber()  // worker running it
			+ "," + SchedulerStatus.WAITING.getDatabaseNumber() // waiting
			+ ")";

	// AND upper(serverID) = 'serverid1';
	if (serverID != null) {
		// all in uppercase
		sql += " AND upper(" + jobTable.serverID.getQualifiedColName() + ") = " + "'" + serverID.toString().toUpperCase() + "'";
	}

	sql += " order by " + jobTable.submitDate.getQualifiedColName(); // order by submit date
		
	//log.print(sql);
	Statement stmt = con.createStatement();
	java.util.List<SimulationJobStatusPersistent> simJobStatusList = new java.util.ArrayList<SimulationJobStatusPersistent>();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			SimulationJobStatusPersistent simJobStatus = jobTable.getSimulationJobStatus(rset);
			simJobStatusList.add(simJobStatus);
		}
	} finally {
		stmt.close();
	}
	
	return (SimulationJobStatusPersistent[])simJobStatusList.toArray(new SimulationJobStatusPersistent[0]);
}

/**
 * This method was created in VisualAge.
 * @return int
 * @param user java.lang.String
 * @param imageName java.lang.String
 */
public Map<KeyValue,SimulationRequirements> getSimulationRequirements(Connection con, Collection<KeyValue> simKeys) throws SQLException {
	ArrayList<KeyValue> simKeysRemaining = new ArrayList<KeyValue>(simKeys);
	
	HashMap<KeyValue,SimulationRequirements> simulationRequirementsMap = new HashMap<KeyValue,SimulationRequirements>();
	
	final int ORACLE_MAX_NUMBER_OF_EXPRESSIONS_IN_LIST = 1000;
	final int MAX_KEYS_PER_STATEMENT = ORACLE_MAX_NUMBER_OF_EXPRESSIONS_IN_LIST / 2;
	while (!simKeysRemaining.isEmpty()){
		//
		// get MAX_KEYS_PER_STATEMENT simkeys to operate on at once ... oracle doesn't like more than 1000.
		//
		ArrayList<KeyValue> simKeysSubset = new ArrayList<KeyValue>();
		int count=0;
		for (KeyValue key : simKeysRemaining){
			simKeysSubset.add(key);
			if (count >= MAX_KEYS_PER_STATEMENT){
				break;
			}
			count++;
		}
		simKeysRemaining.removeAll(simKeysSubset);
		
		//
		// get Simulation Requirements for subset of simKeys and store in simulationRequirementsMap
		//
		StringBuffer simKeyListBuffer = new StringBuffer();
		boolean bFirst = true;
		for (KeyValue key : simKeysSubset){
			if (!bFirst){
				simKeyListBuffer.append(",");
			}
			simKeyListBuffer.append(key);
			bFirst = false;
		}
		String sql = "SELECT " + simTable.id.getQualifiedColName() + "," + geometryTable.dimension.getQualifiedColName()
				+ " FROM " + simTable.getTableName() + "," + mathDescTable.getTableName() + "," + geometryTable.getTableName()
				+ " WHERE " + simTable.mathRef.getQualifiedColName() + "=" + mathDescTable.id.getQualifiedColName()
				+ " AND " + geometryTable.id.getQualifiedColName() + "=" + mathDescTable.geometryRef.getQualifiedColName()
				+ " AND " + simTable.id.getQualifiedColName() + " in ( "+ simKeyListBuffer.toString() + " )";
				
		//log.print(sql);
		Statement stmt = con.createStatement();
		try {
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				KeyValue simKey = new KeyValue(rset.getBigDecimal(simTable.id.toString()));
				int dimension = rset.getInt(geometryTable.dimension.toString());			
				simulationRequirementsMap.put(simKey,new SimulationRequirements(simKey, dimension));
			}
		} finally {
			stmt.close();
		}
	}		
	return simulationRequirementsMap;
}

/**
 * This method was created in VisualAge.
 * @return int
 * @param user java.lang.String
 * @param imageName java.lang.String
 */
public SimulationJobStatusPersistent[] getSimulationJobStatus(Connection con, KeyValue simulationKeys[]) throws SQLException {
	//log.print("SchedulerDbDriver.getSimulationJobStatus(bActiveOnly=" + bActiveOnly + ", owner=" + owner);	
	String sql = new String(standardJobStatusSQL);

	StringBuffer simKeyBuffer = new StringBuffer();
	for (int i = 0; i < simulationKeys.length; i++){
		if (i>0){
			simKeyBuffer.append(",");
		}
		simKeyBuffer.append(simulationKeys[i].toString());
	}
    sql += " AND " + jobTable.simRef.getQualifiedColName() + " IN (" + simKeyBuffer.toString() + ")";	
			
	//log.print(sql);
	Statement stmt = con.createStatement();
	java.util.List<SimulationJobStatusPersistent> simJobStatusList = new java.util.ArrayList<SimulationJobStatusPersistent>();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			SimulationJobStatusPersistent simJobStatus = jobTable.getSimulationJobStatus(rset);
			simJobStatusList.add(simJobStatus);
		}
	} finally {
		stmt.close();
	}
	
	return (SimulationJobStatusPersistent[])simJobStatusList.toArray(new SimulationJobStatusPersistent[0]);
}


/**
 * This method was created in VisualAge.
 * @return int
 * @param user java.lang.String
 * @param imageName java.lang.String
 */
public SimulationJobStatusPersistent[] getSimulationJobStatus(Connection con, KeyValue simKey) throws SQLException {
	//log.print("SchedulerDbDriver.getSimulationJobStatus(SimKey="+simKey+")");
	String sql = new String(standardJobStatusSQL);	
	sql += " AND " + simTable.id.getQualifiedColName() + " = " + simKey;
		
	//log.print(sql);
	Statement stmt = con.createStatement();
	List<SimulationJobStatusPersistent> simJobStatuses = new java.util.ArrayList<SimulationJobStatusPersistent>();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			simJobStatuses.add(jobTable.getSimulationJobStatus(rset));
		}
	} finally {
		stmt.close();
	}
	return (SimulationJobStatusPersistent[])simJobStatuses.toArray(new SimulationJobStatusPersistent[0]);
}


/**
 * This method was created in VisualAge.
 * @return int
 * @param user java.lang.String
 * @param imageName java.lang.String
 */
public SimulationJobStatusPersistent getSimulationJobStatus(Connection con, KeyValue simKey, int jobIndex, int taskID, boolean lockRowForUpdate) throws SQLException {
	//log.print("SchedulerDbDriver.getSimulationJobStatus(SimKey="+simKey+")");
	String sql = new String(standardJobStatusSQL);	
	sql += " AND " + simTable.id.getQualifiedColName() + " = " + simKey;
	sql += " AND " + jobTable.jobIndex.getQualifiedColName() + " = " + jobIndex;
	sql += " AND " + jobTable.taskID.getQualifiedColName() + " = " + taskID;
		
	if (lockRowForUpdate){
		sql += " FOR UPDATE OF " + jobTable.getTableName() + ".id";
	}
//	log.print(sql);
	Statement stmt = con.createStatement();
	SimulationJobStatusPersistent simJobStatus = null;
	try {
		ResultSet rset = stmt.executeQuery(sql);
		if (rset.next()) {
			simJobStatus = jobTable.getSimulationJobStatus(rset);
		}
	} finally {
		stmt.close();
	}
	log.print("retrieved simJobStatus = "+simJobStatus);
	return simJobStatus;
}

/**
 * This method was created in VisualAge.
 * @return int
 * @param user java.lang.String
 * @param imageName java.lang.String
 */
public SimulationJobStatusPersistent[] getSimulationJobStatusArray(Connection con, KeyValue simKey, boolean lockRowForUpdate) throws SQLException {
	//log.print("SchedulerDbDriver.getSimulationJobStatus(SimKey="+simKey+")");
	String sql = new String(standardJobStatusSQL);	
	sql += " AND " + simTable.id.getQualifiedColName() + " = " + simKey;
		
	if (lockRowForUpdate){
		sql += " FOR UPDATE OF " + jobTable.getTableName() + ".id";
	}
	//log.print(sql);
	Statement stmt = con.createStatement();
	ArrayList<SimulationJobStatusPersistent> simulationJobStatusArrayList = new ArrayList<SimulationJobStatusPersistent>();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			SimulationJobStatusPersistent simJobStatus = jobTable.getSimulationJobStatus(rset);
			simulationJobStatusArrayList.add(simJobStatus);
		}
	} finally {
		stmt.close();
	}
	return simulationJobStatusArrayList.toArray(new SimulationJobStatusPersistent[0]);
}


/**
 * This method was created in VisualAge.
 * @return int
 * @param user java.lang.String
 * @param imageName java.lang.String
 */
public SimulationJobStatusPersistent[] getSimulationJobStatusArray(Connection con, KeyValue simKey, int jobIndex, boolean lockRowForUpdate) throws SQLException {
	//log.print("SchedulerDbDriver.getSimulationJobStatus(SimKey="+simKey+")");
	String sql = new String(standardJobStatusSQL);	
	sql += " AND " + simTable.id.getQualifiedColName() + " = " + simKey;
	sql += " AND " + jobTable.jobIndex.getQualifiedColName() + " = " + jobIndex;
		
	if (lockRowForUpdate){
		sql += " FOR UPDATE OF " + jobTable.getTableName() + ".id";
	}
	//log.print(sql);
	Statement stmt = con.createStatement();
	ArrayList<SimulationJobStatusPersistent> simulationJobStatusArrayList = new ArrayList<SimulationJobStatusPersistent>();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			SimulationJobStatusPersistent simJobStatus = jobTable.getSimulationJobStatus(rset);
			simulationJobStatusArrayList.add(simJobStatus);
		}
	} finally {
		stmt.close();
	}
	return simulationJobStatusArrayList.toArray(new SimulationJobStatusPersistent[0]);
}


/**
 * Insert the method's description here.
 * Creation date: (9/3/2003 8:59:46 AM)
 * @return java.util.List of SimpleJobStatus for managementGUI
 * @param conditions java.lang.String
 * 
 * for the subqueries, here is a prototype query which returns the 
 * 
 * select
vc_simulation_1.id as simid,
(select max('{"bmid":"' || lpad(vc_biomodel.id,14,0) || '","scid":"' || lpad(vc_simcontext.id,14,0) || '","bmbranch":"' || lpad(vc_biomodel.versionbranchid,14,0) || '","scbranch":"' || lpad(vc_simcontext.versionbranchid,14,0) || '","bmname":"' || vc_biomodel.name || '","scname":"' || vc_simcontext.name || '"}')
from vc_biomodel, vc_biomodelsimcontext, VC_BIOMODELSIM, vc_simcontext
where vc_simulation_1.mathref = vc_simcontext.mathref
and VC_BIOMODELSIMCONTEXT.SIMCONTEXTREF = VC_SIMCONTEXT.id
and VC_BIOMODELSIMCONTEXT.BIOMODELREF = vc_biomodel.id
and VC_BIOMODELSIM.SIMREF = VC_SIMULATION_1.id
and VC_BIOMODELSIM.BIOMODELREF = vc_biomodel.id
) as bmLink,
(select max('{"mmid":"' || lpad(vc_mathmodel.id,14,0) || '","mmbranch":"' || lpad(vc_mathmodel.versionbranchid,14,0) || '","mmname":"' || vc_mathmodel.name || '"}')
from vc_mathmodel, VC_MATHMODELSIM
where vc_simulation_1.id = vc_mathmodelsim.SIMREF
and vc_mathmodelsim.MATHMODELREF = vc_mathmodel.id
) as mmLink
from vc_simulation vc_simulation_1
where rownum <= 10;

which returns the biomodel link (bmlink) and the math model link (mmlink) as JSON strings to be interpreted as needed.

simid		bmlink																																								mmlink
2,006,065	<null>																																								{"mmid":"00000002001619","mmbranch":"00000001008286","mmname":"Terasaki1"}
2,006,075	<null>																																								{"mmid":"00000002001626","mmbranch":"00000001008286","mmname":"Terasaki1"}
2,006,085	<null>																																								{"mmid":"00000002001636","mmbranch":"00000001008286","mmname":"Terasaki1"}
2,637,970	{"bmid":"00000002669821","scid":"00000002637934","bmbranch":"00000002622407","scbranch":"00000002637935","bmname":"aggregation","scname":"diagonal gradient"}		<null>
2,006,427	<null>																																								{"mmid":"00000002002108","mmbranch":"00000001002871","mmname":"DiffusionfromChannel"}
2,006,437	<null>																																								{"mmid":"00000002002110","mmbranch":"00000001002871","mmname":"DiffusionfromChannel"}
2,006,646	<null>																																								{"mmid":"00000002002254","mmbranch":"00000001118506","mmname":"AliciaProblem1"}
10,067,537	{"bmid":"00000010067543","scid":"00000010067469","bmbranch":"00000010033822","scbranch":"00000010067470","bmname":"BMTest_biphasicStatModule1","scname":"figure5"}	<null>
10,369,972	{"bmid":"00000010369990","scid":"00000010369900","bmbranch":"00000010009521","scbranch":"00000010369901","bmname":"MemBinding_1","scname":"comp"}					<null>
2,007,278	<null>																																								{"mmid":"00000002001884","mmbranch":"00000001036088","mmname":"Wave_no_nucl5"}


 */
public List<SimpleJobStatusPersistent> getSimpleJobStatus(Connection con, String conditions, int startRow, int maxNumRows) throws java.sql.SQLException, DataAccessException {	
	
	BioModelSimulationLinkTable bioSimLinkTable = BioModelSimulationLinkTable.table;
	MathModelSimulationLinkTable mathSimLinkTable = MathModelSimulationLinkTable.table;
	
	final String BMLINK = "bmlink";
	final String MMLINK = "mmlink";

	String subquery = "SELECT " +
		"sysdate as " + DatabaseConstants.SYSDATE_COLUMN_NAME
		+ "," + jobTable.getTableName() + ".*," + userTable.userid.getQualifiedColName() + "," + userTable.id.getQualifiedColName()+" as ownerkey"
		+ "," + "vc_sim_1." + simTable.ownerRef.getUnqualifiedColName()
		+ "," + "vc_sim_1." + simTable.name.getUnqualifiedColName()
		+ "," + "vc_sim_1." + simTable.taskDescription.getUnqualifiedColName()
		+ "," + "vc_sim_1." + simTable.meshSpecX.getUnqualifiedColName()
		+ "," + "vc_sim_1." + simTable.meshSpecY.getUnqualifiedColName()
		+ "," + "vc_sim_1." + simTable.meshSpecZ.getUnqualifiedColName()
		+ "," + "vc_sim_1." + simTable.mathOverridesLarge.getUnqualifiedColName()+" as "+simTable.mathOverridesLarge.getUnqualifiedColName()
		+ "," + "vc_sim_1." + simTable.mathOverridesSmall.getUnqualifiedColName()+" as "+simTable.mathOverridesSmall.getUnqualifiedColName()
		+ "," + "(SELECT max('{\""+BioModelLink.bmid+"\":\"' || lpad(vc_biomodel.id,14,0)" +
							" || '\",\""+BioModelLink.scid+"\":\"' || lpad(vc_simcontext.id,14,0)" +
							" || '\",\""+BioModelLink.bmbranch+"\":\"' || lpad(vc_biomodel.versionbranchid,14,0)" +
							" || '\",\""+BioModelLink.scbranch+"\":\"' || lpad(vc_simcontext.versionbranchid,14,0)" +
							" || '\",\""+BioModelLink.bmname+"\":\"' || vc_biomodel.name" +
							" || '\",\""+BioModelLink.scname+"\":\"' || vc_simcontext.name || '\"}')" +
				" FROM vc_biomodel" +
				", vc_biomodelsimcontext" +
				", VC_BIOMODELSIM" +
				", vc_simcontext" +
				" WHERE vc_sim_1.mathref = vc_simcontext.mathref" +
				" AND VC_BIOMODELSIMCONTEXT.SIMCONTEXTREF = VC_SIMCONTEXT.id" +
				" AND VC_BIOMODELSIMCONTEXT.BIOMODELREF = vc_biomodel.id" +
				" AND VC_BIOMODELSIM.SIMREF = vc_sim_1.id" +
				" AND VC_BIOMODELSIM.BIOMODELREF = vc_biomodel.id" +
				") as " + BMLINK
		+ "," + "(SELECT max('{\""+MathModelLink.mmid+"\":\"' || lpad(vc_mathmodel.id,14,0)" +
							" || '\",\""+MathModelLink.mmbranch+"\":\"' || lpad(vc_mathmodel.versionbranchid,14,0)" +
							" || '\",\""+MathModelLink.mmname+"\":\"' || vc_mathmodel.name || '\"}')" +
				" FROM vc_mathmodel" +
				", vc_mathmodelsim" +
				" WHERE vc_sim_1.id = vc_mathmodelsim.SIMREF" +
				" AND vc_mathmodelsim.MATHMODELREF = vc_mathmodel.id" +
				") as " + MMLINK
		+ " FROM " + jobTable.getTableName()
					+ "," + simTable.getTableName() + " vc_sim_1"
					+ "," + userTable.getTableName() 
					+ "," + bioSimLinkTable.getTableName()
					+ "," + mathSimLinkTable.getTableName()
		+ " WHERE " + "vc_sim_1." + simTable.id.getUnqualifiedColName() + "=" + jobTable.simRef.getQualifiedColName()
		+ " AND " + "vc_sim_1." + simTable.ownerRef.getUnqualifiedColName() + "=" + userTable.id.getQualifiedColName()
		+ " AND " + bioSimLinkTable.simRef.getQualifiedColName()+" (+) " + "=" + "vc_sim_1." + simTable.id.getUnqualifiedColName()
		+ " AND " + mathSimLinkTable.simRef.getQualifiedColName()+" (+) " + "=" + "vc_sim_1." + simTable.id.getUnqualifiedColName();

	String additionalConditionsClause = "";
	if (conditions!=null && conditions.length() > 0) {
		additionalConditionsClause = " AND (" + conditions + ")";
	}
	
	String orderByClause = " order by " + jobTable.submitDate.getQualifiedColName() + " DESC ";  // most recent records first
	
	String sql = null;
	
	if (maxNumRows>0){
		if (startRow <= 1){
			// simpler query, only limit rows, not starting row
			sql = "select * from "+
					"(" + subquery + " " + additionalConditionsClause + " " + orderByClause + ") "+
					"where rownum <= "+maxNumRows;
		}else{
			// full query, limit start and limit
			sql = "select * from "+
						"(select a.*, ROWNUM rnum from "+
							"(" + subquery + " " + additionalConditionsClause + " " + orderByClause + ") a "+
						" where rownum <= " + (startRow + maxNumRows - 1) + ") "+
				  "where rnum >= "+startRow;
		}
	}else{
		sql = subquery + " " + additionalConditionsClause + " " + orderByClause;
	}
	
	System.out.println(sql);
	
	List<SimpleJobStatusPersistent> resultList = new ArrayList<SimpleJobStatusPersistent>();
	Statement stmt = con.createStatement();	
	SimulationJobStatusPersistent simJobStatus = null;
	cbit.vcell.solver.SolverTaskDescription std = null;
	String username = null;
	try {
		ResultSet rset = stmt.executeQuery(sql.toString());
		while (rset.next()) {
			simJobStatus = jobTable.getSimulationJobStatus(rset);
			username = rset.getString(userTable.userid.getUnqualifiedColName());
			BigDecimal ownerKeyDecimal = rset.getBigDecimal("ownerkey");
			User owner = new User(username,new KeyValue(ownerKeyDecimal));
			std = null;
			try {
				String taskDesc = rset.getString(SimulationTable.table.taskDescription.getUnqualifiedColName());
				if (taskDesc != null) {
					std = new cbit.vcell.solver.SolverTaskDescription(new org.vcell.util.CommentStringTokenizer(org.vcell.util.TokenMangler.getSQLRestoredString(taskDesc)));
				}
			} catch (DataAccessException ex) {
				log.exception(ex);
			}
			Integer meshSizeX = rset.getInt(SimulationTable.table.meshSpecX.getUnqualifiedColName());
			if (rset.wasNull()){
				meshSizeX = null;
			}
			Integer meshSizeY = rset.getInt(SimulationTable.table.meshSpecY.getUnqualifiedColName());
			if (rset.wasNull()){
				meshSizeY = null;
			}
			Integer meshSizeZ = rset.getInt(SimulationTable.table.meshSpecZ.getUnqualifiedColName());
			if (rset.wasNull()){
				meshSizeZ = null;
			}
			String simname = rset.getString(SimulationTable.table.name.getUnqualifiedColName());
			
			SimulationDocumentLink simulationDocumentLink = null;
			String latestBioModelLinkJSON = rset.getString(BMLINK);
			if (latestBioModelLinkJSON!=null){
				Gson gson = new Gson();
				BioModelLink bioModelLink = gson.fromJson(latestBioModelLinkJSON, BioModelLink.class);
				bioModelLink.clearZeroPadding();
				simulationDocumentLink = bioModelLink;
			}
			String latestMathModelLinkJSON = rset.getString(MMLINK);
			if (latestMathModelLinkJSON!=null){
				Gson gson = new Gson();
				MathModelLink mathModelLink = gson.fromJson(latestMathModelLinkJSON, MathModelLink.class);
				mathModelLink.clearZeroPadding();
				simulationDocumentLink = mathModelLink;
			}
			CommentStringTokenizer mathOverridesTokens = SimulationTable.getMathOverridesTokenizer(rset);
			List<MathOverrides.Element> mathOverrideElements = MathOverrides.parseOverrideElementsFromVCML(mathOverridesTokens);
			int scanCount = 1;
			for (MathOverrides.Element element : mathOverrideElements) {
				if (element.getSpec()!=null){
					scanCount *= element.getSpec().getNumValues();
				}
			}
			SimulationMetadata simulationMetadata = new SimulationMetadata(simJobStatus.getVCSimulationIdentifier(), simname, owner, std, meshSizeX, meshSizeY, meshSizeZ, new Integer(scanCount));
			resultList.add(new SimpleJobStatusPersistent(simulationMetadata,simulationDocumentLink,simJobStatus));
		} 
	} finally {
		stmt.close();		
	}
	
	return resultList;
}


public List<SimpleJobStatusPersistent> getSimpleJobStatus(Connection con, SimpleJobStatusQuerySpec simStatusQuerySpec) throws java.sql.SQLException, DataAccessException {	
	ArrayList<String> conditions = new ArrayList<String>();
	
	if (simStatusQuerySpec.simId!=null){
		conditions.add(jobTable.simRef.getQualifiedColName() + "=" + simStatusQuerySpec.simId);
 	}

	if (simStatusQuerySpec.jobId!=null){
		conditions.add(jobTable.jobIndex.getQualifiedColName() + "=" + simStatusQuerySpec.jobId);
 	}

	if (simStatusQuerySpec.taskId!=null){
		conditions.add(jobTable.taskID.getQualifiedColName() + "=" + simStatusQuerySpec.taskId);
 	}

	if (simStatusQuerySpec.computeHost != null && simStatusQuerySpec.computeHost.length()>0){
 		conditions.add("lower(" + jobTable.computeHost.getQualifiedColName() + ")='" + simStatusQuerySpec.computeHost.toLowerCase() + "'");
	}

	if (simStatusQuerySpec.serverId!=null && simStatusQuerySpec.serverId.length()>0){
		conditions.add("lower(" + jobTable.serverID.getQualifiedColName() + ")='" + simStatusQuerySpec.serverId + "'");
	}
	
	if (simStatusQuerySpec.hasData!=null){
		if (simStatusQuerySpec.hasData!=null && simStatusQuerySpec.hasData.booleanValue()==true){
			// return only records that have data
			conditions.add("lower(" + jobTable.hasData.getQualifiedColName() + ")='y'");
		} else if (simStatusQuerySpec.hasData!=null && simStatusQuerySpec.hasData.booleanValue()==false){
			// return only records that don't have data
			conditions.add(jobTable.hasData.getQualifiedColName() + " is null");
		}
	} // else all records.
	
	if (simStatusQuerySpec.userid!=null && simStatusQuerySpec.userid.length()>0){
		conditions.add(UserTable.table.userid.getQualifiedColName() + "='" + simStatusQuerySpec.userid + "'");
	}

/**
* 		w = WAITING(0,"waiting"),
*		q = QUEUED(1,"queued"),
*		d = DISPATCHED(2,"dispatched"),
*		r = RUNNING(3,"running"),
*		c = COMPLETED(4,"completed"),
*		s = STOPPED(5,"stopped"),
*		f = FAILED(6,"failed");
*
*/
	ArrayList<String> statusConditions = new ArrayList<String>();
	if (simStatusQuerySpec.waiting){
		statusConditions.add(jobTable.schedulerStatus.getQualifiedColName() + "=" + SchedulerStatus.WAITING.getDatabaseNumber());
	}
	if (simStatusQuerySpec.queued){
		statusConditions.add(jobTable.schedulerStatus.getQualifiedColName() + "=" + SchedulerStatus.QUEUED.getDatabaseNumber());
	}
	if (simStatusQuerySpec.dispatched){
		statusConditions.add(jobTable.schedulerStatus.getQualifiedColName() + "=" + SchedulerStatus.DISPATCHED.getDatabaseNumber());
	}
	if (simStatusQuerySpec.running){
		statusConditions.add(jobTable.schedulerStatus.getQualifiedColName() + "=" + SchedulerStatus.RUNNING.getDatabaseNumber());
	}
	if (simStatusQuerySpec.completed){
		statusConditions.add(jobTable.schedulerStatus.getQualifiedColName() + "=" + SchedulerStatus.COMPLETED.getDatabaseNumber());
	}
	if (simStatusQuerySpec.stopped){
		statusConditions.add(jobTable.schedulerStatus.getQualifiedColName() + "=" + SchedulerStatus.STOPPED.getDatabaseNumber());
	}
	if (simStatusQuerySpec.failed){
		statusConditions.add(jobTable.schedulerStatus.getQualifiedColName() + "=" + SchedulerStatus.FAILED.getDatabaseNumber());
	}
	if (statusConditions.size()>0){
       	StringBuffer statusConditionsBuffer = new StringBuffer();
    	for (String statusCondition : statusConditions) {
    		if (statusConditionsBuffer.length() > 0) {
    			statusConditionsBuffer.append(" OR ");
    		}
    		statusConditionsBuffer.append(statusCondition);
		}
 		conditions.add("(" + statusConditionsBuffer + ")");
	}
 	
	java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", java.util.Locale.US);
	
	if (simStatusQuerySpec.submitLowMS != null){
		conditions.add("(" + jobTable.submitDate.getQualifiedColName() + " >= to_date('" + df.format(new Date(simStatusQuerySpec.submitLowMS)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
	}
	if (simStatusQuerySpec.submitHighMS != null){
		conditions.add("(" + jobTable.submitDate.getQualifiedColName() + " <= to_date('" + df.format(new Date(simStatusQuerySpec.submitHighMS)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
	}
	if (simStatusQuerySpec.startLowMS != null){
		conditions.add("(" + jobTable.startDate.getQualifiedColName() + " >= to_date('" + df.format(new Date(simStatusQuerySpec.startLowMS)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
	}
	if (simStatusQuerySpec.startHighMS != null){
		conditions.add("(" + jobTable.startDate.getQualifiedColName() + " <= to_date('" + df.format(new Date(simStatusQuerySpec.startHighMS)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
	}
	if (simStatusQuerySpec.endLowMS != null){
		conditions.add("(" + jobTable.endDate.getQualifiedColName() + " >= to_date('" + df.format(new Date(simStatusQuerySpec.endLowMS)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
	}
	if (simStatusQuerySpec.endHighMS != null){
		conditions.add("(" + jobTable.endDate.getQualifiedColName() + " <= to_date('" + df.format(new Date(simStatusQuerySpec.endHighMS)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
	}

//	conditions.add("(" + "rownum" + " <= " + maxNumRows + ")");
	
	StringBuffer conditionsBuffer = new StringBuffer();
	for (String condition : conditions) {
		if (conditionsBuffer.length() > 0) {
			conditionsBuffer.append(" AND ");
		}
		conditionsBuffer.append(condition);
	}
	
	if (statusConditions.size()==0){
		// no status conditions wanted ... nothing to query
		return new ArrayList<SimpleJobStatusPersistent>();
	}else{
   		List<SimpleJobStatusPersistent> resultList = getSimpleJobStatus(con, conditionsBuffer.toString(), simStatusQuerySpec.startRow, simStatusQuerySpec.maxRows);
   		return resultList;
	}
}



/**
 * This method was created in VisualAge.
 * @return int
 * @param user java.lang.String
 * @param imageName java.lang.String
 */
public SimulationJobStatusPersistent[] getSimulationJobStatus(Connection con, boolean bActiveOnly, User owner) throws SQLException {
	//log.print("SchedulerDbDriver.getSimulationJobStatus(bActiveOnly=" + bActiveOnly + ", owner=" + owner);	
	String sql = new String(standardJobStatusSQL);

	if (owner != null) {
		sql += " AND " + userTable.id.getQualifiedColName() + "=" + owner.getID();
	}

	if (bActiveOnly) {
		sql += " AND (" + jobTable.schedulerStatus + "=" + SchedulerStatus.QUEUED.getDatabaseNumber() // in job queue
			+ " OR " + jobTable.schedulerStatus + "=" + SchedulerStatus.DISPATCHED.getDatabaseNumber() // worker just accepted it
			+ " OR " + jobTable.schedulerStatus + "=" + SchedulerStatus.RUNNING.getDatabaseNumber()  // worker running it
			+ ")";
	}
	
			
	//log.print(sql);
	Statement stmt = con.createStatement();
	java.util.List<SimulationJobStatusPersistent> simJobStatusList = new java.util.ArrayList<SimulationJobStatusPersistent>();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			SimulationJobStatusPersistent simJobStatus = jobTable.getSimulationJobStatus(rset);
			simJobStatusList.add(simJobStatus);
		}
	} finally {
		stmt.close();
	}
	
	return (SimulationJobStatusPersistent[])simJobStatusList.toArray(new SimulationJobStatusPersistent[0]);
}


/**
 * Insert the method's description here.
 * Creation date: (9/3/2003 8:59:46 AM)
 * @return java.util.List
 * @param conditions java.lang.String
 */
public User getUserFromSimulationKey(Connection con, KeyValue simKey) throws SQLException {	
	String sql = "SELECT " + userTable.id.getQualifiedColName() + "," + userTable.userid.getQualifiedColName() 
		+ " FROM " + simTable.getTableName() + "," + userTable.getTableName() 
		+ " WHERE " + simTable.ownerRef.getQualifiedColName() + "=" + userTable.id.getQualifiedColName()
			+ " AND " + simTable.id.getQualifiedColName() + "=" + simKey;
	
	//log.print(sql);
	Statement stmt = con.createStatement();	
	try {
		ResultSet rset = stmt.executeQuery(sql.toString());
		if (rset.next()) {
			KeyValue userKey = new KeyValue(rset.getBigDecimal(UserTable.table.id.toString()));
			String username = rset.getString(userTable.userid.toString());
			return new User(username, userKey);
		}

	} finally {
		stmt.close();
	}
	return null;	
}


/**
 * addModel method comment.
 */
public void insertSimulationJobStatus(Connection con, SimulationJobStatusPersistent simulationJobStatus, KeyValue key) throws SQLException {
	if (simulationJobStatus == null){
		throw new IllegalArgumentException("simulationJobStatus cannot be null");
	}
	log.print("SimulationJobDbDriver.insertSimulationJobStatus(simKey="+simulationJobStatus.getVCSimulationIdentifier().getSimulationKey()+")");
	String sql = "INSERT INTO " + jobTable.getTableName() + " " + jobTable.getSQLColumnList() + " VALUES " 
		+ jobTable.getSQLValueList(key, simulationJobStatus);

	log.print(sql);			
	executeUpdate(con,sql);
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
public void updateSimulationJobStatus(Connection con, SimulationJobStatusPersistent simulationJobStatus) throws SQLException {
	if (simulationJobStatus == null || con == null){
		throw new IllegalArgumentException("Improper parameters for updateSimulationJobStatus()");
	}

	log.print("SimulationJobDbDriver.updateSimulationJobStatus(simKey="+simulationJobStatus.getVCSimulationIdentifier().getSimulationKey()+")");
	
	String sql = "UPDATE " + jobTable.getTableName() +	" SET "  + jobTable.getSQLUpdateList(simulationJobStatus) + 
			" WHERE " + jobTable.simRef + "=" + simulationJobStatus.getVCSimulationIdentifier().getSimulationKey() +
			" AND " + jobTable.jobIndex + "=" + simulationJobStatus.getJobIndex() +
			" AND " + jobTable.taskID + "=" + simulationJobStatus.getTaskID();
	//log.print(sql);			
	executeUpdate(con,sql);
}
}
