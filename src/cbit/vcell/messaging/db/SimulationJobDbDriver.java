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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.messaging.db.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.modeldb.DatabaseConstants;
import cbit.vcell.modeldb.SimulationTable;
import cbit.vcell.modeldb.UserTable;

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
public SimulationJobStatus[] getActiveJobs(Connection con, VCellServerID serverID) throws SQLException {
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
	java.util.List<SimulationJobStatus> simJobStatusList = new java.util.ArrayList<SimulationJobStatus>();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			SimulationJobStatus simJobStatus = jobTable.getSimulationJobStatus(rset);
			simJobStatusList.add(simJobStatus);
		}
	} finally {
		stmt.close();
	}
	
	return (SimulationJobStatus[])simJobStatusList.toArray(new SimulationJobStatus[0]);
}

/**
 * This method was created in VisualAge.
 * @return int
 * @param user java.lang.String
 * @param imageName java.lang.String
 */
public Map<KeyValue,SimulationRequirements> getSimulationRequirements(Connection con, List<KeyValue> simKeys) throws SQLException {
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
public SimulationJobStatus[] getSimulationJobStatus(Connection con, KeyValue simulationKeys[]) throws SQLException {
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
	java.util.List<SimulationJobStatus> simJobStatusList = new java.util.ArrayList<SimulationJobStatus>();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			SimulationJobStatus simJobStatus = jobTable.getSimulationJobStatus(rset);
			simJobStatusList.add(simJobStatus);
		}
	} finally {
		stmt.close();
	}
	
	return (SimulationJobStatus[])simJobStatusList.toArray(new SimulationJobStatus[0]);
}


/**
 * This method was created in VisualAge.
 * @return int
 * @param user java.lang.String
 * @param imageName java.lang.String
 */
public SimulationJobStatus[] getSimulationJobStatus(Connection con, KeyValue simKey) throws SQLException {
	//log.print("SchedulerDbDriver.getSimulationJobStatus(SimKey="+simKey+")");
	String sql = new String(standardJobStatusSQL);	
	sql += " AND " + simTable.id.getQualifiedColName() + " = " + simKey;
		
	//log.print(sql);
	Statement stmt = con.createStatement();
	List<SimulationJobStatus> simJobStatuses = new java.util.ArrayList<SimulationJobStatus>();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			simJobStatuses.add(jobTable.getSimulationJobStatus(rset));
		}
	} finally {
		stmt.close();
	}
	return (SimulationJobStatus[])simJobStatuses.toArray(new SimulationJobStatus[0]);
}


/**
 * This method was created in VisualAge.
 * @return int
 * @param user java.lang.String
 * @param imageName java.lang.String
 */
public SimulationJobStatus getSimulationJobStatus(Connection con, KeyValue simKey, int jobIndex, int taskID, boolean lockRowForUpdate) throws SQLException {
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
	SimulationJobStatus simJobStatus = null;
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
public SimulationJobStatus[] getSimulationJobStatusArray(Connection con, KeyValue simKey, boolean lockRowForUpdate) throws SQLException {
	//log.print("SchedulerDbDriver.getSimulationJobStatus(SimKey="+simKey+")");
	String sql = new String(standardJobStatusSQL);	
	sql += " AND " + simTable.id.getQualifiedColName() + " = " + simKey;
		
	if (lockRowForUpdate){
		sql += " FOR UPDATE OF " + jobTable.getTableName() + ".id";
	}
	//log.print(sql);
	Statement stmt = con.createStatement();
	ArrayList<SimulationJobStatus> simulationJobStatusArrayList = new ArrayList<SimulationJobStatus>();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			SimulationJobStatus simJobStatus = jobTable.getSimulationJobStatus(rset);
			simulationJobStatusArrayList.add(simJobStatus);
		}
	} finally {
		stmt.close();
	}
	return simulationJobStatusArrayList.toArray(new SimulationJobStatus[0]);
}


/**
 * This method was created in VisualAge.
 * @return int
 * @param user java.lang.String
 * @param imageName java.lang.String
 */
public SimulationJobStatus[] getSimulationJobStatusArray(Connection con, KeyValue simKey, int jobIndex, boolean lockRowForUpdate) throws SQLException {
	//log.print("SchedulerDbDriver.getSimulationJobStatus(SimKey="+simKey+")");
	String sql = new String(standardJobStatusSQL);	
	sql += " AND " + simTable.id.getQualifiedColName() + " = " + simKey;
	sql += " AND " + jobTable.jobIndex.getQualifiedColName() + " = " + jobIndex;
		
	if (lockRowForUpdate){
		sql += " FOR UPDATE OF " + jobTable.getTableName() + ".id";
	}
	//log.print(sql);
	Statement stmt = con.createStatement();
	ArrayList<SimulationJobStatus> simulationJobStatusArrayList = new ArrayList<SimulationJobStatus>();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			SimulationJobStatus simJobStatus = jobTable.getSimulationJobStatus(rset);
			simulationJobStatusArrayList.add(simJobStatus);
		}
	} finally {
		stmt.close();
	}
	return simulationJobStatusArrayList.toArray(new SimulationJobStatus[0]);
}


/**
 * Insert the method's description here.
 * Creation date: (9/3/2003 8:59:46 AM)
 * @return java.util.List of SimpleJobStatus for managementGUI
 * @param conditions java.lang.String
 */
public List<SimpleJobStatus> getSimulationJobStatus(Connection con, String conditions) throws java.sql.SQLException {	
	StringBuffer sql = new StringBuffer();
	
	sql.append("SELECT sysdate as " + DatabaseConstants.SYSDATE_COLUMN_NAME + "," + jobTable.getTableName() + ".*," + userTable.userid.getQualifiedColName() 
		+ "," + simTable.ownerRef.getQualifiedColName() + "," + simTable.taskDescription.getQualifiedColName()
		+ "," + simTable.meshSpecX.getQualifiedColName() + "," + simTable.meshSpecY.getQualifiedColName() + "," + simTable.meshSpecZ.getQualifiedColName()
		+ " FROM " + jobTable.getTableName() + "," + simTable.getTableName() + "," + userTable.getTableName() 
		+ " WHERE " + simTable.id.getQualifiedColName() + "=" + jobTable.simRef.getQualifiedColName()
		+ " AND " + simTable.ownerRef.getQualifiedColName() + "=" + userTable.id.getQualifiedColName());
	if (conditions.length() > 0) {
		sql.append(" AND " + conditions);
	}

	sql.append(" order by " + jobTable.submitDate.getQualifiedColName());
	//log.print(sql);
	
	List<SimpleJobStatus> resultList = new ArrayList<SimpleJobStatus>();
	Statement stmt = con.createStatement();	
	SimulationJobStatus simJobStatus = null;
	cbit.vcell.solver.SolverTaskDescription std = null;
	String username = null;
	try {
		ResultSet rset = stmt.executeQuery(sql.toString());
		while (rset.next()) {
			simJobStatus = jobTable.getSimulationJobStatus(rset);
			username = rset.getString(userTable.userid.getUnqualifiedColName());
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
			resultList.add(new SimpleJobStatus(username, simJobStatus, std, meshSizeX, meshSizeY, meshSizeZ));
		} 
	} finally {
		stmt.close();		
	}
	
	return resultList;
}


/**
 * This method was created in VisualAge.
 * @return int
 * @param user java.lang.String
 * @param imageName java.lang.String
 */
public SimulationJobStatus[] getSimulationJobStatus(Connection con, boolean bActiveOnly, User owner) throws SQLException {
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
	java.util.List<SimulationJobStatus> simJobStatusList = new java.util.ArrayList<SimulationJobStatus>();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			SimulationJobStatus simJobStatus = jobTable.getSimulationJobStatus(rset);
			simJobStatusList.add(simJobStatus);
		}
	} finally {
		stmt.close();
	}
	
	return (SimulationJobStatus[])simJobStatusList.toArray(new SimulationJobStatus[0]);
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
public void insertSimulationJobStatus(Connection con, SimulationJobStatus simulationJobStatus, KeyValue key) throws SQLException {
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
public void updateSimulationJobStatus(Connection con, SimulationJobStatus simulationJobStatus) throws SQLException {
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
