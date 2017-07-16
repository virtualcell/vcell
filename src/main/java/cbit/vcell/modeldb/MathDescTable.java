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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.vcell.db.DatabaseSyntax;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.sql.Field.SQLDataType;
import cbit.vcell.math.MathDescription;
/**
 * This type was created in VisualAge.
 */
public class MathDescTable extends cbit.vcell.modeldb.VersionTable {
	private static final String TABLE_NAME = "vc_math";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field geometryRef		= new Field("geometryRef",	SQLDataType.integer,	"NOT NULL "+GeometryTable.REF_TYPE);
	public final Field language 		= new Field("language",		SQLDataType.clob_text,	"NOT NULL");
	
	private final Field fields[] = {geometryRef,language};
	
	public static final MathDescTable table = new MathDescTable();

/**
 * ModelTable constructor comment.
 */
private MathDescTable() {
	super(TABLE_NAME,MathDescTable.REF_TYPE);
	addFields(fields);
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.GeometryInfo
 * @param rset java.sql.ResultSet
 * @param log cbit.vcell.server.SessionLog
 */
public VersionInfo getInfo(ResultSet rset,Connection con,SessionLog log) throws SQLException,org.vcell.util.DataAccessException {
	
	KeyValue geomRef = new KeyValue(rset.getBigDecimal(MathDescTable.table.geometryRef.toString()));
	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid),log);
	String softwareVersion = rset.getString(SoftwareVersionTable.table.softwareVersion.toString());
	
	return new cbit.vcell.math.MathInfo(geomRef,version,VCellSoftwareVersion.fromString(softwareVersion));
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getInfoSQL(User user,String extraConditions,String special) {
	
	UserTable userTable = UserTable.table;
	MathDescTable vTable = MathDescTable.table;
	SoftwareVersionTable swvTable = SoftwareVersionTable.table;
	String sql;
	//Field[] f = {userTable.userid,new cbit.sql.StarField(vTable)};
	Field[] f = new Field[] {vTable.id,userTable.userid,swvTable.softwareVersion};
	f = (Field[])org.vcell.util.BeanUtils.addElements(f,vTable.versionFields);
	f = (Field[])org.vcell.util.BeanUtils.addElement(f,vTable.geometryRef);
	
	Table[] t = {vTable,userTable,swvTable};
	String condition = userTable.id.getQualifiedColName() + " = " + vTable.ownerRef.getQualifiedColName() +  // links in the userTable
	           " AND " + vTable.id.getQualifiedColName() + " = " + swvTable.versionableRef.getQualifiedColName()+"(+) ";
	if (extraConditions != null && extraConditions.trim().length()>0){
		condition += " AND "+extraConditions;
	}
	sql = DatabasePolicySQL.enforceOwnershipSelect(user,f,t,condition,special);
	return sql;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.MathDescription
 * @param user cbit.vcell.server.User
 * @param rset java.sql.ResultSet
 */
public MathDescription getMathDescription(ResultSet rset, Connection con,SessionLog log, DatabaseSyntax dbSyntax) 
										throws SQLException,DataAccessException {

	//
	// Get Version
	//
	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,DbDriver.getGroupAccessFromGroupID(con,groupid),log);

	//
	// get MathDescription Data (language) (MUST BE READ FIRST)
	//
	/*
	byte[] mathDescriptionData = null;
	try {
		mathDescriptionData = rset.getBytes(MathDescTable.table.language.toString());
	}catch (SQLException e){
	System.out.println("SQLException error_code = "+e.getErrorCode()+", localized Message = "+e.getLocalizedMessage());
		log.exception(e);
		try {
			rset.close();
		}catch (SQLException e2){
			log.exception(e2);
		}
		throw e;
	}
	if (rset.wasNull() || mathDescriptionData==null || mathDescriptionData.length==0){
		throw new DataAccessException("no data stored for MathDescription");
	}
	String mathDescriptionDataString = new String(mathDescriptionData);
	*/
	//
	String mathDescriptionDataString = (String) DbDriver.getLOB(rset,MathDescTable.table.language,dbSyntax);
	if(mathDescriptionDataString == null || mathDescriptionDataString.length() == 0){
		throw new DataAccessException("no data stored for MathDescription");
	}
	//
	//System.out.println("mathDescriptionDataString '"+mathDescriptionDataString+"'");
	
	MathDescription mathDescription = new MathDescription(version);
	
	//
	// setGeometry is done in calling parent
	//
	//mathDescription.setGeometry(geom);
	
	CommentStringTokenizer tokens = new CommentStringTokenizer(mathDescriptionDataString);
	try {
		mathDescription.read_database(tokens);
	} catch (Exception e){
		e.printStackTrace(System.out);
		throw new org.vcell.util.DataAccessException(e.getMessage(),e);
	}
	//
	return mathDescription;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(MathDescription mathDescription,KeyValue geomKey,Version version, DatabaseSyntax dbSyntax) {
	switch (dbSyntax){
	case ORACLE:{
		StringBuffer buffer = new StringBuffer();
		buffer.append("(");
		buffer.append(getVersionGroupSQLValue(version) + ",");
		buffer.append(geomKey+",");
		buffer.append("EMPTY_CLOB()"+")");
		return buffer.toString();
	}
	case POSTGRES:{
		// TODO: POSTGRES
		throw new RuntimeException("MathDescTable.getSQLValueList() not yet implemented for Postgres");
	}
	default:{
		throw new RuntimeException("unexpected DatabaseSyntax "+dbSyntax);
	}
	}
}
}
