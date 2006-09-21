package cbit.vcell.messaging.db;
import java.sql.*;

import cbit.util.DataAccessException;
import cbit.util.KeyValue;
import cbit.util.SessionLog;
import cbit.util.User;
import cbit.vcell.messaging.SimpleJobStatus;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.messaging.db.SimulationJobTable;
import cbit.vcell.modeldb.SimulationTable;
import cbit.vcell.modeldb.DatabaseConstants;
import java.util.ArrayList;
import java.util.List;
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
	private cbit.util.SessionLog log = null;
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
public SimulationJobStatusInfo[] getActiveJobs(Connection con, VCellServerID[] serverIDs) throws SQLException {
	String sql = "SELECT sysdate as " + DatabaseConstants.SYSDATE_COLUMN_NAME + "," + jobTable.getTableName()+".*," + simTable.ownerRef.getQualifiedColName() 
			+ "," + userTable.userid.getQualifiedColName() + "," + geometryTable.dimension.getQualifiedColName()
			+ " FROM " + jobTable.getTableName() + "," + simTable.getTableName() + "," + userTable.getTableName() + "," + mathDescTable.getTableName() + "," + geometryTable.getTableName()
			+ " WHERE " + simTable.ownerRef.getQualifiedColName() + "=" + userTable.id.getQualifiedColName()
			+ " AND " + simTable.id.getQualifiedColName() + "=" + jobTable.simRef.getQualifiedColName()
			+ " AND " + simTable.mathRef.getQualifiedColName() + "=" + mathDescTable.id.getQualifiedColName()
			+ " AND " + geometryTable.id.getQualifiedColName() + "=" + mathDescTable.geometryRef.getQualifiedColName();
			
			
	sql += " AND "
			+ jobTable.schedulerStatus + " in (" + SimulationJobStatus.SCHEDULERSTATUS_QUEUED // in job queue
			+ ","  + SimulationJobStatus.SCHEDULERSTATUS_DISPATCHED // worker just accepted it
			+ "," + SimulationJobStatus.SCHEDULERSTATUS_RUNNING  // worker running it
			+ "," + SimulationJobStatus.SCHEDULERSTATUS_WAITING // waiting
			+ ")";

	// AND upper(serverID) in ('serverid1', serverid2');
	if (serverIDs != null) {
		// all in uppercase
		sql += " AND upper(" + jobTable.serverID.getQualifiedColName() + ") in (";
		for (int i = 0; i < serverIDs.length; i ++) {
			sql += "'" + serverIDs[i].toString().toUpperCase() + "'";
			if (i < serverIDs.length - 1) {
				sql += ",";
			}
		}
		sql += ")";
	}

	sql += " order by " + jobTable.submitDate.getQualifiedColName(); // order by submit date
		
	//log.print(sql);
	Statement stmt = con.createStatement();
	java.util.List simJobStatusInfoList = new java.util.ArrayList();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			SimulationJobStatus simJobStatus = jobTable.getSimulationJobStatus(rset);
			int dimension = rset.getInt(geometryTable.dimension.toString());			
			simJobStatusInfoList.add(new SimulationJobStatusInfo(simJobStatus, dimension));
		}
	} finally {
		stmt.close();
	}
	
	return (SimulationJobStatusInfo[])simJobStatusInfoList.toArray(new SimulationJobStatusInfo[0]);
}


/**
 * This method was created in VisualAge.
 * @return int
 * @param user java.lang.String
 * @param imageName java.lang.String
 */
public SimulationJobStatus getNextObsoleteSimulation(Connection con, long intervalSeconds) throws SQLException {
	String sql = new String(standardJobStatusSQL);
	sql += " AND (sysdate-" + jobTable.latestUpdateDate + ")*86400>" + intervalSeconds
		+ " AND (" + jobTable.serverID + "='" + VCellServerID.getSystemServerID() + "')"
		+ " AND (" + jobTable.schedulerStatus + "=" + SimulationJobStatus.SCHEDULERSTATUS_RUNNING // running
		+ " OR " + jobTable.schedulerStatus + "=" + SimulationJobStatus.SCHEDULERSTATUS_DISPATCHED // worker just accepted it
		+ ") and rownum<2 order by " + jobTable.submitDate;	
			
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
	return simJobStatus;
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
	java.util.List simJobStatusList = new java.util.ArrayList();
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
	List simJobStatuses = new java.util.ArrayList();
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
public SimulationJobStatus getSimulationJobStatus(Connection con, KeyValue simKey, int index, boolean lockRowForUpdate) throws SQLException {
	//log.print("SchedulerDbDriver.getSimulationJobStatus(SimKey="+simKey+")");
	String sql = new String(standardJobStatusSQL);	
	sql += " AND " + simTable.id.getQualifiedColName() + " = " + simKey;
	sql += " AND " + jobTable.jobIndex.getQualifiedColName() + " = " + index;
		
	if (lockRowForUpdate){
		sql += " FOR UPDATE OF " + jobTable.getTableName() + ".id";
	}
	//log.print(sql);
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
	return simJobStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (9/3/2003 8:59:46 AM)
 * @return java.util.List of SimpleJobStatus for managementGUI
 * @param conditions java.lang.String
 */
public List getSimulationJobStatus(Connection con, String conditions) throws java.sql.SQLException {
	StringBuffer where = new StringBuffer();
	
	StringBuffer sql = new StringBuffer();
	
	sql.append("SELECT sysdate as " + DatabaseConstants.SYSDATE_COLUMN_NAME + "," + jobTable.getTableName() + ".*," + userTable.userid.getQualifiedColName() 
		+ "," + simTable.ownerRef.getQualifiedColName() + "," + simTable.taskDescription.getQualifiedColName() 
		+ " FROM " + jobTable.getTableName() + "," + simTable.getTableName() + "," + userTable.getTableName() 
		+ " WHERE " + simTable.id.getQualifiedColName() + "=" + jobTable.simRef.getQualifiedColName()
		+ " AND " + simTable.ownerRef.getQualifiedColName() + "=" + userTable.id.getQualifiedColName());
	if (conditions.length() > 0) {
		sql.append(" AND " + conditions);
	}

	sql.append(" order by " + jobTable.submitDate.getQualifiedColName());
	//log.print(sql);
	
	List resultList = new ArrayList();
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
					std = new cbit.vcell.solver.SolverTaskDescription(null, new cbit.util.CommentStringTokenizer(cbit.util.TokenMangler.getSQLRestoredString(taskDesc)));
				}
				
			} catch (DataAccessException ex) {
				log.exception(ex);
			}
		
			resultList.add(new SimpleJobStatus(username, simJobStatus, std));
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
		sql += " AND (" + jobTable.schedulerStatus + "=" + SimulationJobStatus.SCHEDULERSTATUS_QUEUED // in job queue
			+ " OR " + jobTable.schedulerStatus + "=" + SimulationJobStatus.SCHEDULERSTATUS_DISPATCHED // worker just accepted it
			+ " OR " + jobTable.schedulerStatus + "=" + SimulationJobStatus.SCHEDULERSTATUS_RUNNING  // worker running it
			+ ")";
	}
	
			
	//log.print(sql);
	Statement stmt = con.createStatement();
	java.util.List simJobStatusList = new java.util.ArrayList();
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
	StringBuffer where = new StringBuffer();
	
	String sql = "SELECT " + userTable.id.getQualifiedColName() + "," + userTable.userid.getQualifiedColName() 
		+ " FROM " + simTable.getTableName() + "," + userTable.getTableName() 
		+ " WHERE " + simTable.ownerRef.getQualifiedColName() + "=" + userTable.id.getQualifiedColName()
			+ " AND " + simTable.id.getQualifiedColName() + "=" + simKey;
	
	//log.print(sql);
	List resultList = new ArrayList();

	Statement stmt = con.createStatement();	
	SimulationJobStatus simJobStatus = null;
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
	log.print("SimulationDbDriver.insertSimulationJobStatus(simKey="+simulationJobStatus.getVCSimulationIdentifier().getSimulationKey()+")");
	String sql = "INSERT INTO " + jobTable.getTableName() + " " + jobTable.getSQLColumnList() + " VALUES " 
		+ jobTable.getSQLValueList(key, simulationJobStatus);

	//log.print(sql);			
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

	log.print("SchedulerDbDriver.updateSimulationJobStatus(simKey="+simulationJobStatus.getVCSimulationIdentifier().getSimulationKey()+")");
	
	String sql = "UPDATE " + jobTable.getTableName() +	" SET "  + jobTable.getSQLUpdateList(simulationJobStatus) + 
			" WHERE " + jobTable.simRef + "=" + simulationJobStatus.getVCSimulationIdentifier().getSimulationKey() +
			" AND " + jobTable.jobIndex + "=" + simulationJobStatus.getJobIndex();
	//log.print(sql);			
	executeUpdate(con,sql);
}
}