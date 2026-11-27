/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modeldb;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.vcell.db.DatabaseSyntax;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
import cbit.vcell.biomodel.BioModelMetaData;
import cbit.vcell.modeldb.DatabasePolicySQL.LeftOuterJoin;
import cbit.vcell.modeldb.DatabaseServerImpl.OrderBy;

/**
 * This type was created in VisualAge.
 */
public class BioModelTable extends cbit.vcell.modeldb.VersionTable {
	private static final String TABLE_NAME = "vc_biomodel";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field modelRef				= new Field("modelRef",			SQLDataType.integer,		"NOT NULL "+ModelTable.REF_TYPE);
	public final Field childSummaryLarge	= new Field("childSummaryLRG",	SQLDataType.clob_text,		"");
	public final Field childSummarySmall	= new Field("childSummarySML",	SQLDataType.varchar2_4000,	"");
	
	private final Field fields[] = {modelRef,childSummaryLarge,childSummarySmall};
	
	public static final BioModelTable table = new BioModelTable();
/**
 * ModelTable constructor comment.
 */
private BioModelTable() {
	super(TABLE_NAME,BioModelTable.REF_TYPE);
	addFields(fields);
}

public BioModelMetaData getBioModelMetaData(ResultSet rset, BioModelDbDriver bioModelDbDriver, Connection con,DatabaseSyntax dbSyntax)
										throws SQLException,DataAccessException {

	//
	// Get Version
	//
	BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,dbSyntax,DbDriver.getGroupAccessFromGroupID(con,groupid));
	KeyValue bioModelKey = version.getVersionKey();

	KeyValue modelRef = new KeyValue(rset.getBigDecimal(table.modelRef.toString()));

	//
	// get Simulation Keys for bioModelKey
	//
	KeyValue simKeys[] = bioModelDbDriver.getSimulationEntriesFromBioModel(con, bioModelKey);

	//
	// get SimulationContext Keys for bioModelKey
	//
	KeyValue simContextKeys[] = bioModelDbDriver.getSimContextEntriesFromBioModel(con, bioModelKey);
	
	//
	//Get VCMetaData XML
	//
	String vcMetaDataXML = VCMetaDataTable.getVCMetaDataXML(rset,dbSyntax);
	
	BioModelMetaData bioModelMetaData = new BioModelMetaData(version,modelRef,simContextKeys,simKeys,vcMetaDataXML);
	//
	// setMathDescription is done in calling parent
	//
	//simulation.setMathDescription(mathDesc);

	return bioModelMetaData;
}

public BioModelMetaData getBioModelMetaData(ResultSet rset, Connection con, KeyValue simContextKeys[], KeyValue simulationKeys[],DatabaseSyntax dbSyntax)
										throws SQLException,DataAccessException {

	//
	// Get Version
	//
	BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,dbSyntax,DbDriver.getGroupAccessFromGroupID(con,groupid));

	KeyValue modelRef = new KeyValue(rset.getBigDecimal(table.modelRef.toString()));

	String vcMetaDataXML = VCMetaDataTable.getVCMetaDataXML(rset,dbSyntax);
	BioModelMetaData bioModelMetaData = new BioModelMetaData(version,modelRef,simContextKeys,simulationKeys,vcMetaDataXML);
	//
	// setMathDescription is done in calling parent
	//
	//simulation.setMathDescription(mathDesc);

	return bioModelMetaData;
}

public VersionInfo getInfo(ResultSet rset,Connection con,DatabaseSyntax dbSyntax) throws SQLException,org.vcell.util.DataAccessException {

	BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,dbSyntax,DbDriver.getGroupAccessFromGroupID(con,groupid));
	String softwareVersion = rset.getString(SoftwareVersionTable.table.softwareVersion.toString());
	VCellSoftwareVersion vcSoftwareVersion = VCellSoftwareVersion.fromString(softwareVersion);
	String serialDbChildSummary = DbDriver.varchar2_CLOB_get(rset,BioModelTable.table.childSummarySmall,BioModelTable.table.childSummaryLarge,dbSyntax);

	org.vcell.util.document.BioModelInfo bioModelInfo = new org.vcell.util.document.BioModelInfo(version, serialDbChildSummary, vcSoftwareVersion);
	// issue #1746 Phase 2: simKeys are attached later by stitchSimKeys() (batch, raw rows grouped in
	// Java), not read from a per-row LISTAGG subquery column here.
	return bioModelInfo;
}

/**
 * issue #1746 Phase 2 (ORA-01489 fix): attach each BioModel's simulation keys by batch-fetching raw
 * (bioModelRef, simRef) rows in a few chunked queries (~1000 model ids each) and grouping them in
 * Java. This replaces the per-row correlated LISTAGG subquery — which returned VARCHAR2 and overflowed
 * (ORA-01489) for models with many simulations — with an approach that has no string-length limit and
 * runs O(N/1000) queries instead of O(N) subquery evaluations. Models with no simulations get an empty
 * array.
 */
public static void stitchSimKeys(java.sql.Connection con, DatabaseSyntax dbSyntax,
		java.util.Collection<org.vcell.util.document.BioModelInfo> infos) throws SQLException {
	if (infos == null || infos.isEmpty()) {
		return;
	}
	BioModelSimulationLinkTable bmsimTable = BioModelSimulationLinkTable.table;
	String refCol = bmsimTable.bioModelRef.getUnqualifiedColName();
	String simRefCol = bmsimTable.simRef.getUnqualifiedColName();

	java.util.LinkedHashSet<String> ids = new java.util.LinkedHashSet<>();
	for (org.vcell.util.document.BioModelInfo info : infos) {
		if (info.getVersion() != null && info.getVersion().getVersionKey() != null) {
			ids.add(info.getVersion().getVersionKey().toString());
		}
	}
	java.util.Map<String, java.util.List<String>> keysById = new java.util.HashMap<>();
	java.util.ArrayList<String> idList = new java.util.ArrayList<>(ids);
	final int CHUNK = 1000;
	for (int start = 0; start < idList.size(); start += CHUNK) {
		java.util.List<String> chunk = idList.subList(start, Math.min(start+CHUNK, idList.size()));
		String inList = String.join(",", chunk);
		String sql = "SELECT "+refCol+", "+simRefCol+" FROM "+bmsimTable.getTableName()+" WHERE "+refCol+" IN ("+inList+")";
		try (java.sql.Statement stmt = con.createStatement(); java.sql.ResultSet rset = stmt.executeQuery(sql)) {
			while (rset.next()) {
				String id = rset.getBigDecimal(refCol).toBigInteger().toString();
				String simKey = rset.getBigDecimal(simRefCol).toBigInteger().toString();
				keysById.computeIfAbsent(id, k -> new java.util.ArrayList<>()).add(simKey);
			}
		}
	}
	for (org.vcell.util.document.BioModelInfo info : infos) {
		if (info.getVersion() != null && info.getVersion().getVersionKey() != null) {
			java.util.List<String> keys = keysById.get(info.getVersion().getVersionKey().toString());
			info.setSimKeys(keys == null ? new String[0] : keys.toArray(new String[0]));
		}
	}
}

public String getInfoSQL(User user,String extraConditions,String special, DatabaseSyntax dbSyntax) {
	
	UserTable userTable = UserTable.table;
	BioModelTable vTable = BioModelTable.table;
	SoftwareVersionTable swvTable = SoftwareVersionTable.table;
	String sql;
	// issue #1746 Phase 2: simKeys were aggregated here via a correlated LISTAGG subquery. Oracle
	// LISTAGG returns VARCHAR2 (4000-byte cap), so a model with many simulations overflowed with
	// ORA-01489 ("result of string concatenation is too long") and 500'd the whole vcInfoContainer
	// for that user. They are now batch-fetched as raw rows and grouped in Java (no length limit,
	// and one grouped query instead of a per-row subquery) — see stitchSimKeys().
	Field[] f = {userTable.userid,new cbit.sql.StarField(vTable),swvTable.softwareVersion};
	Table[] t = {vTable,userTable,swvTable};
	
	switch (dbSyntax){
	case ORACLE:
	case POSTGRES:{
		// outer join in FROM clause explicitly, encoded in "OuterJoin" class and removed from WHERE clause.
		String condition = userTable.id.getQualifiedColName() + " = " + vTable.ownerRef.getQualifiedColName() + " "; // links in the userTable
		if (extraConditions != null && extraConditions.trim().length()>0){
			condition += " AND "+extraConditions;
		}
		LeftOuterJoin outerJoin = new LeftOuterJoin(vTable, swvTable, vTable.id, swvTable.versionableRef);
		sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,outerJoin,condition,special,true);
		return sql;
	}
	default:{
		throw new RuntimeException("unexpected DatabaseSyntax "+dbSyntax);
	}
	}
}

public String getSQLValueList(BioModelMetaData bioModelMetaData, String serialBMChildSummary,Version version) {
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(getVersionGroupSQLValue(version) + ",");
	buffer.append(bioModelMetaData.getModelKey() + ",");

	if (serialBMChildSummary==null){
		buffer.append("null,null");
	}else if (DbDriver.varchar2_CLOB_is_Varchar2_OK(serialBMChildSummary)){
		buffer.append("null"+","+DbDriver.INSERT_VARCHAR2_HERE);
	}else{
		buffer.append(DbDriver.INSERT_CLOB_HERE+","+"null");
	}
	
	buffer.append(")");
	return buffer.toString();
}

public String getPreparedStatement_BioModelReps(String conditions, OrderBy orderBy, int startRow, int numRows, DatabaseSyntax dbSyntax){

	BioModelTable bmTable = BioModelTable.table;
	GroupTable groupTable = GroupTable.table;
	UserTable userTable = UserTable.table;

	String subquery = 			
		"select " +
		    bmTable.id.getQualifiedColName()+", "+
		    bmTable.name.getQualifiedColName()+", "+
		    bmTable.privacy.getQualifiedColName()+", "+
		    bmTable.versionFlag.getQualifiedColName()+", "+
		    bmTable.versionDate.getQualifiedColName()+", "+
		    bmTable.versionAnnot.getQualifiedColName()+", "+
		    bmTable.versionBranchID.getQualifiedColName()+", "+
		    bmTable.modelRef.getQualifiedColName()+", "+
		    bmTable.ownerRef.getQualifiedColName()+", "+
		    UserTable.table.userid.getQualifiedColName()+" "+
		
		// issue #2036: simKeys, simContextKeys and groupMembers used to be three correlated LISTAGG
		// subqueries in this select list. Oracle LISTAGG returns VARCHAR2, capped at 4000 bytes, so
		// ONE model with enough simulations raised ORA-01489 and failed the ENTIRE listing - the user
		// could not see any of their models. They are now batch-fetched as raw rows and grouped in
		// Java by attachChildKeys(), which has no length limit. Same approach already taken for the
		// vcInfoContainer path (see stitchSimKeys, issue #1746).
		
		"from "+bmTable.getTableName()+", "+userTable.getTableName()+", "+groupTable.getTableName()+" "+
		"where "+bmTable.ownerRef.getQualifiedColName()+" = "+userTable.id.getQualifiedColName()+" "+
		"and   "+bmTable.privacy.getQualifiedColName()+" = "+groupTable.groupid.getQualifiedColName()+" "+
		"and   (("+bmTable.ownerRef.getQualifiedColName()+" =?) or ("+bmTable.privacy.getQualifiedColName()+" = 0) or ("+groupTable.userRef+" =? ))";
	
	String additionalConditionsClause = "";
	if (conditions!=null && conditions.length()>0){
		additionalConditionsClause = " and ("+conditions+")";
	}
	
	String orderByClause = "order by "+bmTable.versionDate.getQualifiedColName()+" DESC";
	if (orderBy!=null){
		switch (orderBy){
		case date_asc:{
			orderByClause = "order by "+bmTable.versionDate.getQualifiedColName()+" ASC";
			break;
		}
		case date_desc:{
			orderByClause = "order by "+bmTable.versionDate.getQualifiedColName()+" DESC";
			break;
		}
		case name_asc:{
			orderByClause = "order by "+bmTable.name.getQualifiedColName()+" ASC";
			break;
		}
		case name_desc:{
			orderByClause = "order by "+bmTable.name.getQualifiedColName()+" DESC";
			break;
		}
		}
	}

	// query guarantees authorized access to biomodels based on the supplied User authentication.
	String sql = null;
	
	if (startRow <= 1){
		// simpler query, only limit rows, not starting row
		if (dbSyntax == DatabaseSyntax.ORACLE) {
			sql = "select * from " +
					"(" + subquery + " " + additionalConditionsClause + " " + orderByClause + ") " +
					"where rownum <= ?";
		}else if (dbSyntax == DatabaseSyntax.POSTGRES){
			sql = subquery + " " + additionalConditionsClause + " " + orderByClause + " LIMIT ?";
		}else throw new RuntimeException("unexpected database syntax "+dbSyntax);
	}else{
		// full query, limit start and limit
		if (dbSyntax == DatabaseSyntax.ORACLE) {
			sql = "select * from " +
					"(select a.*, ROWNUM rnum from " +
					"(" + subquery + " " + additionalConditionsClause + " " + orderByClause + ") a " +
					" where rownum <= ? ) " +
					"where rnum >= ?";
		}else if (dbSyntax == DatabaseSyntax.POSTGRES){
			sql = subquery + " " + additionalConditionsClause + " " + orderByClause + " LIMIT ? OFFSET ? ";
		}else throw new RuntimeException("unexpected database syntax "+dbSyntax);
	}

	if (lg.isTraceEnabled()) lg.trace(sql);
	return sql;
}

public void setPreparedStatement_BioModelReps(PreparedStatement stmt, User user, int startRow, int numRows, DatabaseSyntax dbSyntax) throws SQLException{
	if (user == null) {
		throw new IllegalArgumentException("Improper parameters for getBioModelRepsSQL");
	}
	BigDecimal userKey = new BigDecimal(user.getID().toString());
	stmt.setBigDecimal(1, userKey);
	stmt.setBigDecimal(2, userKey);
	if (startRow <= 1){
		stmt.setInt(3, numRows);
	}else{
		if (dbSyntax == DatabaseSyntax.ORACLE) {
			stmt.setInt(3, startRow + numRows - 1);
		}else if (dbSyntax == DatabaseSyntax.POSTGRES) {
			stmt.setInt(3, startRow + numRows - 1);
		}else throw new RuntimeException("unexpected database syntax "+dbSyntax);
		stmt.setInt(4, startRow);
	}
}

/**
 * One row of the BioModelRep listing, scalars only.
 *
 * The child key lists are deliberately NOT read here: they are no longer columns of the query.
 * Rows are collected first, then attachChildKeys() fetches every row's children in a few
 * batched queries. See the note in getPreparedStatement_BioModelReps and issue #2036.
 */
public static final class BioModelRepRow {
	private final KeyValue bmKey;
	private final String name;
	private final int privacy;
	private final int versionFlag;
	private final Date date;
	private final String annot;
	private final BigDecimal branchID;
	private final KeyValue modelRef;
	private final User owner;

	private BioModelRepRow(KeyValue bmKey, String name, int privacy, int versionFlag, Date date,
			String annot, BigDecimal branchID, KeyValue modelRef, User owner) {
		this.bmKey = bmKey; this.name = name; this.privacy = privacy; this.versionFlag = versionFlag;
		this.date = date; this.annot = annot; this.branchID = branchID; this.modelRef = modelRef;
		this.owner = owner;
	}
}

public BioModelRepRow getBioModelRepRow(ResultSet rset, DatabaseSyntax dbSyntax) throws IllegalArgumentException, SQLException {
	KeyValue bmKey = new KeyValue(rset.getBigDecimal(table.id.toString()));
	String name = rset.getString(table.name.toString());
	int privacy = rset.getInt(table.privacy.toString());
	int versionFlag = rset.getInt(table.versionFlag.toString());
	Date date = getDate(rset, dbSyntax, table.versionDate.toString());
	String annot = rset.getString(table.versionAnnot.toString());
	BigDecimal branchID = rset.getBigDecimal(table.versionBranchID.toString());
	KeyValue modelRef = new KeyValue(rset.getBigDecimal(table.modelRef.toString()));
	KeyValue ownerRef = new KeyValue(rset.getBigDecimal(table.ownerRef.toString()));
	String ownerName = rset.getString(UserTable.table.userid.toString());
	User owner = new User(ownerName,ownerRef);
	return new BioModelRepRow(bmKey,name,privacy,versionFlag,date,annot,branchID,modelRef,owner);
}

/**
 * issue #2036 (ORA-01489 fix): attach each row's simulation keys, simulation-context keys and
 * group members by batch-fetching raw rows and grouping them in Java.
 *
 * Replaces three correlated LISTAGG subqueries. Besides removing the 4000-byte VARCHAR2 cap
 * that failed the whole listing for one oversized model, this runs O(N/1000) grouped queries
 * instead of evaluating three subqueries per row.
 *
 * BioModelRep's child arrays are final, so the reps are constructed here - once, with
 * everything - rather than mutated after the fact.
 */
public static BioModelRep[] attachChildKeys(Connection con, DatabaseSyntax dbSyntax, List<BioModelRepRow> rows) throws SQLException {
	if (rows == null || rows.isEmpty()) {
		return new BioModelRep[0];
	}
	BioModelSimulationLinkTable bmsimTable = BioModelSimulationLinkTable.table;
	BioModelSimContextLinkTable bmscTable = BioModelSimContextLinkTable.table;
	GroupTable groupTable = GroupTable.table;
	UserTable userTable = UserTable.table;

	LinkedHashSet<String> bmIds = new LinkedHashSet<String>();
	LinkedHashSet<String> groupIds = new LinkedHashSet<String>();
	for (BioModelRepRow row : rows) {
		bmIds.add(row.bmKey.toString());
		// groupMembers was gated on privacy > 1 in the old subquery; keep that
		if (row.privacy > 1) {
			groupIds.add(Integer.toString(row.privacy));
		}
	}

	Map<String,List<KeyValue>> simKeysByBm = fetchKeysByParent(con, bmsimTable.getTableName(),
			bmsimTable.bioModelRef.getUnqualifiedColName(), bmsimTable.simRef.getUnqualifiedColName(), bmIds);
	Map<String,List<KeyValue>> simCtxKeysByBm = fetchKeysByParent(con, bmscTable.getTableName(),
			bmscTable.bioModelRef.getUnqualifiedColName(), bmscTable.simContextRef.getUnqualifiedColName(), bmIds);

	Map<String,List<User>> groupUsersByGroupId = new HashMap<String,List<User>>();
	List<String> groupIdList = new ArrayList<String>(groupIds);
	for (int start = 0; start < groupIdList.size(); start += IN_LIST_CHUNK) {
		List<String> chunk = groupIdList.subList(start, Math.min(start+IN_LIST_CHUNK, groupIdList.size()));
		String sql = "SELECT G."+groupTable.groupid.getUnqualifiedColName()+" GROUPID_, "+
				"G."+groupTable.userRef.getUnqualifiedColName()+" USERREF_, "+
				"U."+userTable.userid.getUnqualifiedColName()+" USERID_ "+
				"FROM "+groupTable.getTableName()+" G, "+userTable.getTableName()+" U "+
				"WHERE U."+userTable.id.getUnqualifiedColName()+" = G."+groupTable.userRef.getUnqualifiedColName()+" "+
				"AND G."+groupTable.groupid.getUnqualifiedColName()+" IN ("+String.join(",", chunk)+")";
		try (Statement stmt = con.createStatement(); ResultSet rset = stmt.executeQuery(sql)) {
			while (rset.next()) {
				String groupId = rset.getBigDecimal("GROUPID_").toBigInteger().toString();
				KeyValue userKey = new KeyValue(rset.getBigDecimal("USERREF_"));
				String userid = rset.getString("USERID_");
				groupUsersByGroupId.computeIfAbsent(groupId, k -> new ArrayList<User>()).add(new User(userid, userKey));
			}
		}
	}

	BioModelRep[] reps = new BioModelRep[rows.size()];
	for (int i = 0; i < rows.size(); i++) {
		BioModelRepRow row = rows.get(i);
		String bmId = row.bmKey.toString();
		List<KeyValue> simKeys = simKeysByBm.get(bmId);
		List<KeyValue> simCtxKeys = simCtxKeysByBm.get(bmId);
		List<User> groupUsers = (row.privacy > 1) ? groupUsersByGroupId.get(Integer.toString(row.privacy)) : null;
		reps[i] = new BioModelRep(row.bmKey, row.name, row.privacy, row.versionFlag,
				groupUsers == null ? new User[0] : groupUsers.toArray(new User[0]),
				row.date, row.annot, row.branchID, row.modelRef, row.owner,
				simKeys == null ? new KeyValue[0] : simKeys.toArray(new KeyValue[0]),
				simCtxKeys == null ? new KeyValue[0] : simCtxKeys.toArray(new KeyValue[0]));
	}
	return reps;
}

/** Oracle caps a literal IN list at 1000 entries, so parent ids are fetched in chunks. */
private static final int IN_LIST_CHUNK = 1000;

private static Map<String,List<KeyValue>> fetchKeysByParent(Connection con, String tableName,
		String parentCol, String childCol, LinkedHashSet<String> parentIds) throws SQLException {
	Map<String,List<KeyValue>> byParent = new HashMap<String,List<KeyValue>>();
	List<String> idList = new ArrayList<String>(parentIds);
	for (int start = 0; start < idList.size(); start += IN_LIST_CHUNK) {
		List<String> chunk = idList.subList(start, Math.min(start+IN_LIST_CHUNK, idList.size()));
		String sql = "SELECT "+parentCol+", "+childCol+" FROM "+tableName+
				" WHERE "+parentCol+" IN ("+String.join(",", chunk)+")";
		try (Statement stmt = con.createStatement(); ResultSet rset = stmt.executeQuery(sql)) {
			while (rset.next()) {
				String parent = rset.getBigDecimal(parentCol).toBigInteger().toString();
				byParent.computeIfAbsent(parent, k -> new ArrayList<KeyValue>())
						.add(new KeyValue(rset.getBigDecimal(childCol)));
			}
		}
	}
	return byParent;
}
}
