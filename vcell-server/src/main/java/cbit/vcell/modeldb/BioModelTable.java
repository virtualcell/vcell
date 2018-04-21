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
import java.util.ArrayList;
import java.util.Date;

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
import cbit.vcell.modeldb.DatabasePolicySQL.JoinOp;
import cbit.vcell.modeldb.DatabasePolicySQL.OuterJoin;
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
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.MathDescription
 * @param user cbit.vcell.server.User
 * @param rset java.sql.ResultSet
 */
public BioModelMetaData getBioModelMetaData(ResultSet rset, BioModelDbDriver bioModelDbDriver, Connection con,DatabaseSyntax dbSyntax) 
										throws SQLException,DataAccessException {

	//
	// Get Version
	//
	BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid));
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
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.MathDescription
 * @param user cbit.vcell.server.User
 * @param rset java.sql.ResultSet
 */
public BioModelMetaData getBioModelMetaData(ResultSet rset, Connection con, KeyValue simContextKeys[], KeyValue simulationKeys[],DatabaseSyntax dbSyntax) 
										throws SQLException,DataAccessException {

	//
	// Get Version
	//
	BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid));

	KeyValue modelRef = new KeyValue(rset.getBigDecimal(table.modelRef.toString()));

	String vcMetaDataXML = VCMetaDataTable.getVCMetaDataXML(rset,dbSyntax);
	BioModelMetaData bioModelMetaData = new BioModelMetaData(version,modelRef,simContextKeys,simulationKeys,vcMetaDataXML);
	//
	// setMathDescription is done in calling parent
	//
	//simulation.setMathDescription(mathDesc);

	return bioModelMetaData;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.GeometryInfo
 * @param rset java.sql.ResultSet
 * @param log cbit.vcell.server.SessionLog
 */
public VersionInfo getInfo(ResultSet rset,Connection con,DatabaseSyntax dbSyntax) throws SQLException,org.vcell.util.DataAccessException {

	KeyValue modelRef = new KeyValue(rset.getBigDecimal(table.modelRef.toString()));
	BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid));
	String softwareVersion = rset.getString(SoftwareVersionTable.table.softwareVersion.toString());
	VCellSoftwareVersion vcSoftwareVersion = VCellSoftwareVersion.fromString(softwareVersion);
	String serialDbChildSummary = DbDriver.varchar2_CLOB_get(rset,BioModelTable.table.childSummarySmall,BioModelTable.table.childSummaryLarge,dbSyntax);
	
	return new org.vcell.util.document.BioModelInfo(version, modelRef, serialDbChildSummary, vcSoftwareVersion);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getInfoSQL(User user,String extraConditions,String special, DatabaseSyntax dbSyntax) {
	
	UserTable userTable = UserTable.table;
	BioModelTable vTable = BioModelTable.table;
	SoftwareVersionTable swvTable = SoftwareVersionTable.table;
	String sql;
	Field[] f = {userTable.userid,new cbit.sql.StarField(vTable),swvTable.softwareVersion};
	Table[] t = {vTable,userTable,swvTable};
	
	switch (dbSyntax){
	case ORACLE:{
		// outer join using (+) syntax in WHERE clause.
		String condition = userTable.id.getQualifiedColName() + " = " + vTable.ownerRef.getQualifiedColName() +  // links in the userTable
				           " AND " + vTable.id.getQualifiedColName() + " = " + swvTable.versionableRef.getQualifiedColName()+"(+) ";
		if (extraConditions != null && extraConditions.trim().length()>0){
			condition += " AND "+extraConditions;
		}
		sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,null,condition,special,dbSyntax,true);
		return sql;
	}
	case POSTGRES:{
		// outer join in FROM clause explicitly, encoded in "OuterJoin" class and removed from WHERE clause.
		String condition = userTable.id.getQualifiedColName() + " = " + vTable.ownerRef.getQualifiedColName() + " "; // links in the userTable
				        //   " AND " + vTable.id.getQualifiedColName() + " = " + swvTable.versionableRef.getQualifiedColName()+"(+) ";
		if (extraConditions != null && extraConditions.trim().length()>0){
			condition += " AND "+extraConditions;
		}
		OuterJoin outerJoin = new OuterJoin(vTable, swvTable, JoinOp.LEFT_OUTER_JOIN, vTable.id, swvTable.versionableRef);
		sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,outerJoin,condition,special,dbSyntax,true);
		return sql;
	}
	default:{
		throw new RuntimeException("unexpected DatabaseSyntax "+dbSyntax);
	}
	}
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
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

public String getPreparedStatement_BioModelReps(String conditions, OrderBy orderBy, int startRow, int numRows){

	BioModelTable bmTable = BioModelTable.table;
	BioModelSimulationLinkTable bmsimTable = BioModelSimulationLinkTable.table;
	BioModelSimContextLinkTable bmscTable = BioModelSimContextLinkTable.table;
	GroupTable groupTable = GroupTable.table;
	UserTable userTable = UserTable.table;
	
	String subquery = 			
		"select " +
		    bmTable.id.getQualifiedColName()+", "+
		    bmTable.name.getQualifiedColName()+", "+
		    bmTable.privacy.getQualifiedColName()+", "+
		    bmTable.versionDate.getQualifiedColName()+", "+
		    bmTable.versionAnnot.getQualifiedColName()+", "+
		    bmTable.versionBranchID.getQualifiedColName()+", "+
		    bmTable.modelRef.getQualifiedColName()+", "+
		    bmTable.ownerRef.getQualifiedColName()+", "+
		    UserTable.table.userid.getQualifiedColName()+", "+
		
		   "(select '['||wm_concat("+"SQ1_"+bmsimTable.simRef.getQualifiedColName()+")||']' "+
		   "   from "+bmsimTable.getTableName()+" SQ1_"+bmsimTable.getTableName()+" "+
		   "   where SQ1_"+bmsimTable.bioModelRef.getQualifiedColName()+" = "+bmTable.id.getQualifiedColName()+") simKeys,  "+
		
		   "(select '['||wm_concat("+"SQ2_"+bmscTable.simContextRef.getQualifiedColName()+")||']' "+
		   "   from "+bmscTable.getTableName()+"  SQ2_"+bmscTable.getTableName()+" "+
		   "   where SQ2_"+bmscTable.bioModelRef.getQualifiedColName()+ " = " + bmTable.id.getQualifiedColName()+") simContextKeys,  "+
		
		   "(select '['||wm_concat(SQ3_"+groupTable.userRef.getQualifiedColName()+"||':'||SQ3_"+userTable.userid.getQualifiedColName()+")||']'  "+
	   	   "   from "+groupTable.getTableName()+" SQ3_"+groupTable.getTableName()+", "+
		   "        "+userTable.getTableName()+"  SQ3_"+userTable.getTableName()+" "+
		   "   where SQ3_"+groupTable.groupid.getQualifiedColName()+" = "+bmTable.privacy.getQualifiedColName()+" "+
		   "   and   SQ3_"+userTable.id.getQualifiedColName()+" = "+groupTable.userRef.getQualifiedColName()+" "+
		   "   and   "+bmTable.privacy.getQualifiedColName()+" > 1) groupMembers "+
		
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
		sql = "select * from "+
				"(" + subquery + " " + additionalConditionsClause + " " + orderByClause + ") "+
				"where rownum <= ?";
	}else{
		// full query, limit start and limit
		sql = "select * from "+
					"(select a.*, ROWNUM rnum from "+
						"(" + subquery + " " + additionalConditionsClause + " " + orderByClause + ") a "+
					" where rownum <= ? ) "+
			  "where rnum >= ?";
	}

	if (lg.isTraceEnabled()) lg.trace(sql);
	return sql;
}

public void setPreparedStatement_BioModelReps(PreparedStatement stmt, User user, int startRow, int numRows) throws SQLException{
	if (user == null) {
		throw new IllegalArgumentException("Improper parameters for getBioModelRepsSQL");
	}
	BigDecimal userKey = new BigDecimal(user.getID().toString());
	stmt.setBigDecimal(1, userKey);
	stmt.setBigDecimal(2, userKey);
	if (startRow <= 1){
		stmt.setInt(3, numRows);
	}else{
		stmt.setInt(3, startRow + numRows - 1);
		stmt.setInt(4, startRow);
	}
}

public BioModelRep getBioModelRep(User user, ResultSet rset) throws IllegalArgumentException, SQLException {
	KeyValue bmKey = new KeyValue(rset.getBigDecimal(table.id.toString()));
	String name = rset.getString(table.name.toString());
	int privacy = rset.getInt(table.privacy.toString());
	Date date = getDate(rset, table.versionDate.toString());
	String annot = rset.getString(table.versionAnnot.toString());
	BigDecimal branchID = rset.getBigDecimal(table.versionBranchID.toString());
	KeyValue modelRef = new KeyValue(rset.getBigDecimal(table.modelRef.toString()));
	KeyValue ownerRef = new KeyValue(rset.getBigDecimal(table.ownerRef.toString()));
	String ownerName = rset.getString(UserTable.table.userid.toString());
	User owner = new User(ownerName,ownerRef);
	
	String simKeysString = rset.getString("simKeys");
	ArrayList<KeyValue> simKeyList = new ArrayList<KeyValue>();
	String[] simKeys = simKeysString.replace("[", "").replace("]", "").split(",");
	for (String simKey : simKeys) {
		if (simKey!=null && simKey.length()>0){
			simKeyList.add(new KeyValue(simKey));
		}
	}
	KeyValue[] simKeyArray = simKeyList.toArray(new KeyValue[0]);

	String simContextsString = rset.getString("simContextKeys");
	ArrayList<KeyValue> simContextKeyList = new ArrayList<KeyValue>();
	String[] simContextKeys = simContextsString.replace("[", "").replace("]", "").split(",");
	for (String simContextKey : simContextKeys) {
		if (simContextKey!=null && simContextKey.length()>0){
			simContextKeyList.add(new KeyValue(simContextKey));
		}
	}
	KeyValue[] simContextKeyArray = simContextKeyList.toArray(new KeyValue[0]);

	String groupMembers = rset.getString("groupMembers");
	ArrayList<User> groupUsers = new ArrayList<User>();
	String[] groupUserStrings = groupMembers.replace("[", "").replace("]", "").split(",");
	for (String groupUserString : groupUserStrings) {
		if (groupUserString!=null && groupUserString.length()>0){
			String[] groupUserTokens = groupUserString.split(":");
			KeyValue groupUserKey = new KeyValue(groupUserTokens[0]);
			String  groupUserid = groupUserTokens[1];
			groupUsers.add(new User(groupUserid,groupUserKey));
		}
	}
	User[] groupUserArray = groupUsers.toArray(new User[0]);
		
	
	return new BioModelRep(bmKey,name,privacy,groupUserArray,date,annot,branchID,modelRef,owner,simKeyArray,simContextKeyArray);
}
}
