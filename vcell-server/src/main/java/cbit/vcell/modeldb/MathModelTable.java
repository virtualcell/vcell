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
import java.util.ArrayList;

import org.vcell.db.DatabaseSyntax;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellSoftwareVersion;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionInfo;

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
import cbit.vcell.mathmodel.MathModelMetaData;
import cbit.vcell.modeldb.DatabasePolicySQL.LeftOuterJoin;
import cbit.vcell.solver.AnnotatedFunction;
/**
 * This type was created in VisualAge.
 */
public class MathModelTable extends cbit.vcell.modeldb.VersionTable {
	private static final String TABLE_NAME = "vc_mathmodel";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field mathRef				= new Field("mathRef",			SQLDataType.integer,		"NOT NULL "+MathDescTable.REF_TYPE);
	public final Field childSummaryLarge	= new Field("childSummaryLRG",	SQLDataType.clob_text,		"");
	public final Field childSummarySmall	= new Field("childSummarySML",	SQLDataType.varchar2_4000,	"");
	
	private final Field fields[] = {mathRef,childSummaryLarge,childSummarySmall};
	
	public static final MathModelTable table = new MathModelTable();

/**
 * ModelTable constructor comment.
 */
private MathModelTable() {
	super(TABLE_NAME,MathModelTable.REF_TYPE);
	addFields(fields);
}


public VersionInfo getInfo(ResultSet rset,Connection con,DatabaseSyntax dbSyntax) throws SQLException,org.vcell.util.DataAccessException {

	KeyValue mathRef = new KeyValue(rset.getBigDecimal(table.mathRef.toString()));
	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,dbSyntax,DbDriver.getGroupAccessFromGroupID(con,groupid));
	
	String serialDbChildSummary = DbDriver.varchar2_CLOB_get(rset,MathModelTable.table.childSummarySmall,MathModelTable.table.childSummaryLarge,dbSyntax);

	String softwareVersion = rset.getString(SoftwareVersionTable.table.softwareVersion.toString());
	
	return new MathModelInfo(version, mathRef, serialDbChildSummary, VCellSoftwareVersion.fromString(softwareVersion));
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getInfoSQL(User user,String extraConditions,String special,DatabaseSyntax dbSyntax) {
	
	UserTable userTable = UserTable.table;
	MathModelTable vTable = MathModelTable.table;
	SoftwareVersionTable swvTable = SoftwareVersionTable.table;
	String sql;
	Field[] f = {userTable.userid,new cbit.sql.StarField(vTable),swvTable.softwareVersion};
	Table[] t = {vTable,userTable,swvTable};
	
	switch (dbSyntax){
	case ORACLE:
	case POSTGRES:{
		String condition = userTable.id.getQualifiedColName() + " = " + vTable.ownerRef.getQualifiedColName() +  " ";// links in the userTable
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


public MathModelMetaData getMathModelMetaData(ResultSet rset, MathModelDbDriver mathModelDbDriver, Connection con, DatabaseSyntax dbSyntax)
										throws SQLException,DataAccessException {

	//
	// Get Version
	//
	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version version = getVersion(rset,dbSyntax,DbDriver.getGroupAccessFromGroupID(con,groupid));
	KeyValue mathModelKey = version.getVersionKey();

	KeyValue mathRef = new KeyValue(rset.getBigDecimal(table.mathRef.toString()));

	//
	// get Simulation Keys for bioModelKey
	//
	KeyValue simKeys[] = mathModelDbDriver.getSimulationEntriesFromMathModel(con, mathModelKey);

//	MathModelMetaData mathModelMetaData = new MathModelMetaData(version,mathRef,simKeys);
	MathModelMetaData mathModelMetaData = populateOutputFunctions(con, mathRef, version, simKeys, dbSyntax);

	return mathModelMetaData;
}


public MathModelMetaData getMathModelMetaData(ResultSet rset, Connection con, KeyValue simulationKeys[], DatabaseSyntax dbSyntax)
										throws SQLException,DataAccessException {

	//
	// Get Version
	//
	java.math.BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
	Version mathModelVersion = getVersion(rset,dbSyntax,DbDriver.getGroupAccessFromGroupID(con,groupid));

	KeyValue mathDescrRef = new KeyValue(rset.getBigDecimal(table.mathRef.toString()));
	
//	MathModelMetaData mathModelMetaData = new MathModelMetaData(version,mathRef,simulationKeys);
	MathModelMetaData mathModelMetaData = populateOutputFunctions(con,mathDescrRef,mathModelVersion,simulationKeys,dbSyntax);

	return mathModelMetaData;
}

private MathModelMetaData populateOutputFunctions(Connection con,KeyValue mathDescrRef,Version mathModelVersion,KeyValue[] simulationKeys,DatabaseSyntax dbSyntax) throws SQLException,DataAccessException{
	ArrayList<AnnotatedFunction> outputFunctions = ApplicationMathTable.table.getOutputFunctionsMathModel(con, mathModelVersion.getVersionKey(),dbSyntax);
	return new MathModelMetaData(mathModelVersion,mathDescrRef,simulationKeys,outputFunctions);
}

public String getSQLValueList(MathModelMetaData mathModelMetaData,String serialMMChildSummary, Version version) {
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(getVersionGroupSQLValue(version) + ",");
	buffer.append(mathModelMetaData.getMathKey() + ",");

	if (serialMMChildSummary==null){
		buffer.append("null,null");
	}else if (DbDriver.varchar2_CLOB_is_Varchar2_OK(serialMMChildSummary)){
		buffer.append("null"+","+DbDriver.INSERT_VARCHAR2_HERE);
	}else{
		buffer.append(DbDriver.INSERT_CLOB_HERE+","+"null");
	}
	
	buffer.append(")");
	return buffer.toString();
}
}
