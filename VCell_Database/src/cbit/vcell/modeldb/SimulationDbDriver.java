package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import org.vcell.util.DataAccessException;
import org.vcell.util.DependencyException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.SimulationVersion;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.document.Versionable;
import org.vcell.util.document.VersionableType;

import cbit.sql.DBCacheTable;
import cbit.sql.Field;
import cbit.sql.InsertHashtable;
import cbit.sql.RecordChangedException;
import cbit.sql.Table;
import cbit.vcell.export.ExportLog;
import cbit.vcell.modeldb.SolverResultSetInfo;
import cbit.vcell.simulation.Simulation;
import cbit.vcell.simulation.SimulationInfo;
/**
 * This type was created in VisualAge.
 */
public class SimulationDbDriver extends DbDriver {
	public static final UserTable userTable = UserTable.table;
	public static final MathDescTable mathDescTable = MathDescTable.table;
	public static final SimulationTable simTable = SimulationTable.table;
	public static final ResultSetMetaDataTable rsetInfoTable = ResultSetMetaDataTable.table;
	private MathDescriptionDbDriver mathDB = null;

/**
 * MathDescrDbDriver constructor comment.
 * @param connectionFactory cbit.sql.ConnectionFactory
 * @param sessionLog cbit.vcell.server.SessionLog
 */
public SimulationDbDriver(DBCacheTable argdbc,MathDescriptionDbDriver argMathDB,SessionLog sessionLog) {
	super(argdbc,sessionLog);
	this.mathDB = argMathDB;
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
void deleteResultSetExport(Connection con, User user,KeyValue eleKey) throws SQLException {

	String rsetExportsubQueryAlias = "rsetexportalias";
	String sql;
	sql =
		"DELETE FROM " + ResultSetExportsTable.table.getTableName() + 
		" WHERE " +
		ResultSetExportsTable.table.id.getQualifiedColName() + " = " + eleKey.toString() +
		" AND " +
		ResultSetExportsTable.table.simulationRef.getQualifiedColName() + " = " +
		"(" +
		"SELECT " + SimulationTable.table.id.getQualifiedColName() +
		" FROM " + SimulationTable.table.getTableName() + "," + ResultSetExportsTable.table.getTableName() + " " +rsetExportsubQueryAlias +
		" WHERE " +
			rsetExportsubQueryAlias+"."+ResultSetExportsTable.table.id.toString() + " = " + eleKey.toString() +
			" AND " +
			SimulationTable.table.id.getQualifiedColName() + " = " + 
					rsetExportsubQueryAlias+"."+ResultSetExportsTable.table.simulationRef.toString() +
			" AND " +
			SimulationTable.table.ownerRef.getQualifiedColName() + " = " + user.getID().toString() +
		")";
	updateCleanSQL(con,sql);
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
void deleteResultSetInfoSQL(Connection con, User user, KeyValue simKey) throws SQLException, DataAccessException, PermissionException, ObjectNotFoundException {

	Vector versionInfoVector = getVersionableInfos(con,log,user,VersionableType.Simulation,false,simKey,false);
	if(versionInfoVector.size() == 0){
		throw new ObjectNotFoundException("SimulationDbDriver:deleteResultSetInfo() key="+simKey+" not found for user="+user);
	}
	else if (versionInfoVector.size() > 1){
		throw new DataAccessException("SimulationDbDriver:deleteResultSetInfo() key="+simKey+" found more than one entry  DB ERROR,BAD!!!!!MUST CHECK");
	}
	VersionInfo versionInfo = (VersionInfo)versionInfoVector.firstElement();
	//
	// if not owner then getVersionableInfo failed (returned record)
	//
	if(!versionInfo.getVersion().getOwner().compareEqual(user)){
		throw new PermissionException("DbDriver:deleteResultSetInfo() getVersionableInfo(bAll=false) returned VersionInfo "+user+" doesn't own.  DB ERROR,BAD!!!!!MUST CHECK");
	}
	
	String sql;
	sql = "DELETE FROM " + rsetInfoTable.getTableName() + " WHERE " + rsetInfoTable.simRef + " = " + simKey;
System.out.println(sql);
	updateCleanSQL(con,sql);
}


/**
 * removeModel method comment.
 */
private void deleteSimulationSQL(Connection con,User user, KeyValue simKey) throws SQLException, DataAccessException, DependencyException {

	//
	// check for external references (from a BioModel or a MathModel)
	//
	failOnExternalRefs(con, MathModelSimulationLinkTable.table.simRef, MathModelSimulationLinkTable.table, simKey,SimulationTable.table);
	failOnExternalRefs(con, BioModelSimulationLinkTable.table.simRef, BioModelSimulationLinkTable.table, simKey,SimulationTable.table);

	//
	// delete the Simulation (the ResultSetMetaData should be deleted by ON DELETE CASCADE)
	//	
	String sql = DatabasePolicySQL.enforceOwnershipDelete(user,simTable,simTable.id.getQualifiedColName() + " = " + simKey);
	updateCleanSQL(con, sql);

	//
	// try to delete the parentSimulation ... it's OK if it fails
	//
	KeyValue parentSimKey = getParentSimulation(con,user,simKey);
	if (parentSimKey!=null){
		try {
			deleteVersionable(con,user,VersionableType.Simulation,parentSimKey);
		}catch (Exception e){
			System.out.println("failed to delete parent simulation, key="+parentSimKey+": "+e.getMessage());
		}
	}
}


/**
 * This method was created in VisualAge.
 * @param user cbit.vcell.server.User
 * @param vType int
 * @param versionKey cbit.sql.KeyValue
 */
public void deleteVersionable(Connection con, User user, VersionableType vType, KeyValue vKey) 
				throws DependencyException, ObjectNotFoundException,
						SQLException,DataAccessException,PermissionException {

	deleteVersionableInit(con, user, vType, vKey);
	if (vType.equals(VersionableType.Simulation)){
		deleteSimulationSQL(con, user, vKey);
		dbc.remove(vKey);
	}else{
		throw new IllegalArgumentException("vType "+vType+" not supported by "+this.getClass());
	}
}


/**
 * getModels method comment.
 */
private KeyValue getParentSimulation(Connection con,User user,KeyValue simKey) throws SQLException, DataAccessException {

	KeyValue parentSimKey = null;
	String sql;
	
	sql = 	" SELECT " + SimulationTable.table.versionParentSimRef.getQualifiedColName()  +
			" FROM " + SimulationTable.table.getTableName() +
			" WHERE " + SimulationTable.table.id.getQualifiedColName() + " = " + simKey;
			
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		
		//showMetaData(rset);

		//
		// get all keys
		//
		if (rset.next()) {
			parentSimKey = new KeyValue(rset.getBigDecimal(SimulationTable.table.versionParentSimRef.getUnqualifiedColName()));
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}

	return parentSimKey;
}


/**
 * Insert the method's description here.
 * Creation date: (10/7/2003 5:07:43 PM)
 */
ExportLog getResultSetExport(Connection con,User user,KeyValue simKey) throws SQLException, DataAccessException {

	ExportLog[] exportLogs = null;
	String sql = ResultSetExportsTable.table.getSQLInfo(user,simKey);
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		exportLogs = ResultSetExportsTable.getExportLogs(rset,log);
		rset.close();
	} finally {
		stmt.close();
	}
	
	return (exportLogs != null?exportLogs[0]:null);
}

/**
 * Insert the method's description here.
 * Creation date: (10/7/2003 5:09:17 PM)
 */
ExportLog[] getResultSetExports(Connection con,User user, boolean bAll) throws SQLException, DataAccessException {

	ExportLog[] exportLogs = null;
	String sql = ResultSetExportsTable.table.getSQLInfo(user,bAll);
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		exportLogs = ResultSetExportsTable.getExportLogs(rset,log);
		rset.close();
	} finally {
		stmt.close();
	}
	
	return exportLogs;
}

/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param user cbit.vcell.server.User
 * @param versionable cbit.sql.Versionable
 */
SolverResultSetInfo getResultSetInfo(Connection con, User user, KeyValue simKey, int jobIndex)
				throws ObjectNotFoundException, SQLException, DataAccessException {
					
	Vector simInfoList = getVersionableInfos(con,log,user,VersionableType.Simulation,true,simKey, false);
	if (simInfoList.size()==0){
		throw new ObjectNotFoundException("simulation("+simKey+") not found for user '"+user+"'");
	}
	SimulationInfo simInfo = (SimulationInfo)simInfoList.elementAt(0);

	String sql = "SELECT " + " * " + 
				 " FROM "+rsetInfoTable.getTableName() +
				 " WHERE "+ rsetInfoTable.simRef+" = "+simKey+
				 " AND "+rsetInfoTable.jobIndex+" = "+jobIndex;

	SolverResultSetInfo rsetInfo = null;
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);

		if (rset.next()) {
			rsetInfo = rsetInfoTable.getSolverResultSetInfo(rset,log,simInfo);
		}
		
	} finally {
		stmt.close(); // Release resources include resultset
	}
	
	return rsetInfo;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param user cbit.vcell.server.User
 * @param vType int
 */
SolverResultSetInfo[] getResultSetInfos(Connection con, SessionLog log, User user, boolean bAll, KeyValue versionKey) 
							throws ObjectNotFoundException, SQLException, DataAccessException {
								
	if (user == null) {
		throw new IllegalArgumentException("Improper parameters for getVersionables");
	}
	//log.print("SimulationDbDriver.getResultSetInfos(all=" + bAll + ",user=" + user + ")");
	SimulationTable vTable = SimulationTable.table;
	String sql;
	StringBuffer conditions = new StringBuffer();
	String special = null;
	boolean bFirstClause = true;
	if (!bAll) {
		conditions.append(vTable.ownerRef.getQualifiedColName() + " = " + user.getID());
		bFirstClause = false;
	}
	if(versionKey != null){
		if (!bFirstClause){
			conditions.append(" AND ");
		}
		conditions.append(vTable.id.getQualifiedColName() + " = " + versionKey);
	}
	special = " ORDER BY " + vTable.name.getQualifiedColName() + "," + vTable.versionBranchID.getQualifiedColName() + "," + vTable.versionDate.getQualifiedColName();
	sql = vTable.getResultSetInfoSQL(user,conditions.toString(),special);
	StringBuffer optimizedSQL = new StringBuffer(sql);
	optimizedSQL.insert(7, Table.SQL_GLOBAL_HINT);
	sql = optimizedSQL.toString();
	
	//System.out.println(sql);
	SolverResultSetInfo rsInfo;
	java.util.Vector rsInfoList = new java.util.Vector();
	//Connection con = conFact.getConnection();
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			rsInfo = vTable.getResultSetInfo(rset,con,log);
			//
			// only add resultsetInfo record to list if not a duplicate,
			// this occurs because the DatabasePolicySQL.enforceOwnershipSelect() can get the same record several ways.
			//
			SolverResultSetInfo previousRSInfo = (rsInfoList.size()>0)?(SolverResultSetInfo)rsInfoList.lastElement():null;
			if (previousRSInfo==null || !previousRSInfo.getVCSimulationDataIdentifier().getID().equals(rsInfo.getVCSimulationDataIdentifier().getID())){
				rsInfoList.addElement(rsInfo);
			}
			//rsInfoList.addElement(rsInfo);
		}
	} finally {
		stmt.close();
	}
	SolverResultSetInfo rsInfoArray[] = new SolverResultSetInfo[rsInfoList.size()];
	rsInfoList.copyInto(rsInfoArray);
	return rsInfoArray;
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param user cbit.vcell.server.User
 * @param versionable cbit.sql.Versionable
 */
SolverResultSetInfo[] getResultSetInfos(Connection con, User user, KeyValue simKey)
				throws ObjectNotFoundException, SQLException, DataAccessException {
					
	Vector simInfoList = getVersionableInfos(con,log,user,VersionableType.Simulation,true,simKey, false);
	if (simInfoList.size()==0){
		throw new ObjectNotFoundException("simulation("+simKey+") not found for user '"+user+"'");
	}
	SimulationInfo simInfo = (SimulationInfo)simInfoList.elementAt(0);

	String sql = "SELECT " + " * " + 
				 " FROM "+rsetInfoTable.getTableName() +
				 " WHERE "+ rsetInfoTable.simRef+" = "+simKey;

	Statement stmt = con.createStatement();
	Vector rsetInfos = new Vector();
	try {
		ResultSet rset = stmt.executeQuery(sql);

		while (rset.next()) {
			rsetInfos.add(rsetInfoTable.getSolverResultSetInfo(rset,log,simInfo));			
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	
	return (SolverResultSetInfo[])org.vcell.util.BeanUtils.getArray(rsetInfos, SolverResultSetInfo.class);
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.MathDescription
 * @param user cbit.vcell.server.User
 * @param mathDescKey cbit.sql.KeyValue
 */
private Simulation getSimulationSQL(Connection con,User user, KeyValue simKey) 
				throws SQLException,DataAccessException,ObjectNotFoundException {

	String sql;
	Field[] f = {userTable.userid,new cbit.sql.StarField(simTable)};
	Table[] t = {simTable,userTable};
	String condition = simTable.id.getQualifiedColName() + " = " + simKey +
			" AND " + userTable.id.getQualifiedColName() + " = " + simTable.ownerRef.getQualifiedColName();
	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,null);
//System.out.println(sql);
	Simulation simulation = null;
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);

		if (rset.next()) {
			//
			// note: must call simulationTable.getSimulation() first (rset.getBytes(language) must be called first)
			//
			try {
				simulation = simTable.getSimulation(rset,log,con,user,mathDB);
			}catch (PropertyVetoException e){
				log.exception(e);
				throw new DataAccessException(e.getMessage());
			}			
		} else {
			throw new org.vcell.util.ObjectNotFoundException("Simulation id=" + simKey + " not found for user '" + user + "'");
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	return simulation;
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param user cbit.vcell.server.User
 * @param versionable cbit.sql.Versionable
 */
public Versionable getVersionable(Connection con, User user, VersionableType vType, KeyValue vKey)
				throws ObjectNotFoundException, SQLException, DataAccessException {
					
	Versionable versionable = (Versionable) dbc.get(vKey);
	if (versionable != null) {
		return versionable;
	} else {
		if (vType.equals(VersionableType.Simulation)){
			versionable = getSimulationSQL(con, user, vKey);
		}else{
			throw new IllegalArgumentException("vType "+vType+" not supported by "+this.getClass());
		}
		dbc.putUnprotected(versionable.getVersion().getVersionKey(),versionable);
	}
	return versionable;
}


/**
 * Insert the method's description here.
 * Creation date: (10/7/2003 12:30:16 PM)
 */
public void insertResultSetExport(Connection con, User user,KeyValue simulationRef,String exportFormat,String exportURL) throws SQLException{

	String sql =
		"INSERT INTO " + ResultSetExportsTable.table.getTableName() +
		" VALUES "+ResultSetExportsTable.table.getSQLValueList(simulationRef,exportFormat,exportURL);

	updateCleanSQL(con,sql);
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
void insertResultSetInfoSQL(Connection con, User user, KeyValue simKey, SolverResultSetInfo rsetInfo) throws SQLException, DataAccessException, PermissionException {
	Vector versionInfoVector = getVersionableInfos(con,log,user,VersionableType.Simulation,false,simKey,false);
	if(versionInfoVector.size() == 0){
		throw new ObjectNotFoundException("SimulationDbDriver:insertResultSetInfo() key="+simKey+" not found for user="+user);
	}
	else if (versionInfoVector.size() > 1){
		throw new DataAccessException("SimulationDbDriver:insertResultSetInfo() key="+simKey+" found more than one entry  DB ERROR,BAD!!!!!MUST CHECK");
	}
	VersionInfo versionInfo = (VersionInfo)versionInfoVector.firstElement();
	//
	// if not owner then getVersionableInfo failed (returned record)
	//
	if(!versionInfo.getVersion().getOwner().compareEqual(user)){
		throw new PermissionException("DbDriver:insertResultSetInfo() getVersionableInfo(bAll=false) returned VersionInfo "+user+" doesn't own.  DB ERROR,BAD!!!!!MUST CHECK");
	}

	String sql;
	sql = "INSERT INTO " + rsetInfoTable.getTableName() + " " + rsetInfoTable.getSQLColumnList() + " VALUES " + 
		rsetInfoTable.getSQLValueList(getNewKey(con), simKey, rsetInfo);
System.out.println(sql);
	updateCleanSQL(con,sql);
}


/**
 * This method was created in VisualAge.
 * @return MathDescribption
 * @param user User
 * @param mathDescription MathDescription
 */
private void insertSimulationSQL(InsertHashtable hash, Connection con, User user, Simulation simulation, KeyValue updatedMathKey,
									Version newVersion, boolean bVersionChildren)
				throws SQLException, DataAccessException, RecordChangedException {
	
	String sql = null;
	Object[] o = {simulation, updatedMathKey};
	sql = DatabasePolicySQL.enforceOwnershipInsert(user,simTable,o,newVersion);

	varchar2_CLOB_update(
						con,
						sql,
						simulation.getMathOverrides().getVCML(),
						SimulationTable.table,
						newVersion.getVersionKey(),
						SimulationTable.table.mathOverridesLarge,
						SimulationTable.table.mathOverridesSmall);

	hash.put(simulation,newVersion.getVersionKey());
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 * @param versionable cbit.sql.Versionable
 * @param pRef cbit.sql.KeyValue
 * @param bCommit boolean
 */
public KeyValue insertVersionable(InsertHashtable hash, Connection con, User user, Simulation simulation, KeyValue updatedMathDescriptionKey, String name, boolean bVersion, boolean bMathematicallyEquivalent) 
					throws DataAccessException, SQLException, RecordChangedException {
						
	SimulationVersion newSimulationVersion = insertVersionableInit(hash, con, user, simulation, name, simulation.getDescription(), bVersion, bMathematicallyEquivalent);

	insertSimulationSQL(hash, con, user, simulation, updatedMathDescriptionKey, newSimulationVersion, bVersion);
	return newSimulationVersion.getVersionKey();
}


/**
 * This method was created in VisualAge.
 * @return cbit.image.VCImage
 * @param user cbit.vcell.server.User
 * @param image cbit.image.VCImage
 */
protected SimulationVersion insertVersionableInit(InsertHashtable hash, Connection con, User user, Versionable versionable, String name, String annot, boolean bVersion, boolean bMathematicallyEquivalent) 
			throws DataAccessException, SQLException, RecordChangedException{

	SimulationVersion dbSimulationVersion = ((Simulation)versionable).getSimulationVersion();
	Version newVersion = insertVersionableInit(hash,con,user,versionable,name,annot,bVersion);
	
	//
	// point to oldest mathematically equivalent ancestor, this will be used for all identification (status/data).
	//
	KeyValue parentSimRef = null;
	if (bMathematicallyEquivalent){
		if (dbSimulationVersion==null){
			throw new RuntimeException("Simulation must have been saved for bMathematicallyEquivalent to be true");
		}
		if (dbSimulationVersion.getParentSimulationReference()!=null){
			////
			//// mathematically equivalent, prior link is transitive  (B->C and A==B, then A->C)
			////
			parentSimRef = dbSimulationVersion.getParentSimulationReference();
		}else{
			////
			//// mathematically equivalent, no prior link (A==B, then A->B)
			////
			parentSimRef = dbSimulationVersion.getVersionKey();
		}
	}
	
	return new SimulationVersion(newVersion.getVersionKey(),
								newVersion.getName(),
								newVersion.getOwner(),
								newVersion.getGroupAccess(),
								newVersion.getBranchPointRefKey(),
								newVersion.getBranchID(),
								newVersion.getDate(),
								newVersion.getFlag(),
								newVersion.getAnnot(),
								parentSimRef);
}


/**
 * This method was created in VisualAge.
 * @param vcimage cbit.image.VCImage
 * @param userid java.lang.String
 * @exception java.rmi.RemoteException The exception description.
 */
void updateResultSetInfoSQL(Connection con, User user, KeyValue simKey, SolverResultSetInfo rsetInfo) throws SQLException, DataAccessException, PermissionException {
	Vector versionInfoVector = getVersionableInfos(con,log,user,VersionableType.Simulation,false,simKey,false);
	if(versionInfoVector.size() == 0){
		throw new ObjectNotFoundException("SimulationDbDriver:updateResultSetInfo() key="+simKey+" not found for user="+user);
	}
	else if (versionInfoVector.size() > 1){
		throw new DataAccessException("SimulationDbDriver:updateResultSetInfo() key="+simKey+" found more than one entry  DB ERROR,BAD!!!!!MUST CHECK");
	}
	VersionInfo versionInfo = (VersionInfo)versionInfoVector.firstElement();
	//
	// if not owner then getVersionableInfo failed (returned record)
	//
	if(!versionInfo.getVersion().getOwner().compareEqual(user)){
		throw new PermissionException("DbDriver:updateResultSetInfo() getVersionableInfo(bAll=false) returned VersionInfo "+user+" doesn't own.  DB ERROR,BAD!!!!!MUST CHECK");
	}

	StringBuffer sb = new StringBuffer();
	sb.append("UPDATE " + rsetInfoTable.getTableName());
	sb.append(" SET " + rsetInfoTable.getSQLUpdateList(simKey,rsetInfo));
	sb.append(" WHERE " + rsetInfoTable.simRef + " = " + simKey);
System.out.println(sb.toString());
	int numRecordsChanged = updateCleanSQL(con,sb.toString());
	if (numRecordsChanged!=1){
		throw new ObjectNotFoundException("SolverResultSetInfo(simRef="+simKey+")");
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.image.VCImage
 * @param user cbit.vcell.server.User
 * @param image cbit.image.VCImage
 */
public KeyValue updateVersionable(InsertHashtable hash, Connection con, User user, Simulation simulation, KeyValue updatedMathDescriptionKey, boolean bVersion, boolean bMathematicallyEquivalent) 
			throws DataAccessException, SQLException, RecordChangedException {
				
	SimulationVersion newSimulationVersion = updateVersionableInit(hash, con, user, simulation, bVersion, bMathematicallyEquivalent);
	insertSimulationSQL(hash, con, user, simulation, updatedMathDescriptionKey, newSimulationVersion, bVersion);
	return newSimulationVersion.getVersionKey();
}


/**
 * This method was created in VisualAge.
 * @return cbit.image.VCImage
 * @param user cbit.vcell.server.User
 * @param image cbit.image.VCImage
 */
protected SimulationVersion updateVersionableInit(InsertHashtable hash, Connection con, User user, Versionable versionable, boolean bVersion, boolean bMathematicallyEquivalent) 
			throws DataAccessException, SQLException, RecordChangedException{

	SimulationVersion dbSimulationVersion = ((Simulation)versionable).getSimulationVersion();
	Version newVersion = updateVersionableInit(hash,con,user,versionable,bVersion);
	
	//
	// point to oldest mathematically equivalent ancestor, this will be used for all identification (status/data).
	//
	KeyValue parentSimRef = null;
	if (bMathematicallyEquivalent){
		if (dbSimulationVersion==null){
			throw new RuntimeException("Simulation must have been saved for bMathematicallyEquivalent to be true");
		}
		if (dbSimulationVersion.getParentSimulationReference()!=null){
			////
			//// mathematically equivalent, prior link is transitive  (B->C and A==B, then A->C)
			////
			parentSimRef = dbSimulationVersion.getParentSimulationReference();
		}else{
			////
			//// mathematically equivalent, no prior link (A==B, then A->B)
			////
			parentSimRef = dbSimulationVersion.getVersionKey();
		}
	}
	
	return new SimulationVersion(newVersion.getVersionKey(),
								newVersion.getName(),
								newVersion.getOwner(),
								newVersion.getGroupAccess(),
								newVersion.getBranchPointRefKey(),
								newVersion.getBranchID(),
								newVersion.getDate(),
								newVersion.getFlag(),
								newVersion.getAnnot(),
								parentSimRef);
}
}