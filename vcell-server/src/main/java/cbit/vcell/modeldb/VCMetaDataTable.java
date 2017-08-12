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
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;

public class VCMetaDataTable extends Table {
	private static final String TABLE_NAME = "vc_metadata";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field bioModelRef		= new Field("bioModelRef",		SQLDataType.integer,		"NOT NULL "+BioModelTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field vcMetaDataLarge	= new Field("vcMetaDataLarge",	SQLDataType.clob_text,		"");
	public final Field vcMetaDataSmall	= new Field("vcMetaDataSmall",	SQLDataType.varchar2_4000,	"");
	
	private final Field fields[] = {bioModelRef,vcMetaDataLarge,vcMetaDataSmall};
	
	public static final VCMetaDataTable table = new VCMetaDataTable();

	private VCMetaDataTable() {
		super(TABLE_NAME);
		addFields(fields);
	}
	
	public static String getVCMetaDataXML(ResultSet rset, DatabaseSyntax dbSyntax) throws DataAccessException,SQLException{
		String vcMetaDataXML =
			DbDriver.varchar2_CLOB_get(rset,VCMetaDataTable.table.vcMetaDataSmall,VCMetaDataTable.table.vcMetaDataLarge,dbSyntax);
		return vcMetaDataXML;

	}
	public static void insertVCMetaData(Connection con,String vcMetaDataXML,final KeyValue bioModelKey,KeyValue newVCMetaDataKey,DatabaseSyntax dbSyntax)
		throws DataAccessException,SQLException{

		String sql;
		sql =
			"INSERT INTO "+VCMetaDataTable.table.getTableName()+" "+
			VCMetaDataTable.table.getSQLColumnList()+
			" VALUES "+VCMetaDataTable.getSQLValueList(vcMetaDataXML, bioModelKey,newVCMetaDataKey);
			
		DbDriver.varchar2_CLOB_update(
							con,
							sql,
							vcMetaDataXML,
							VCMetaDataTable.table,
							newVCMetaDataKey,
							VCMetaDataTable.table.vcMetaDataLarge,
							VCMetaDataTable.table.vcMetaDataSmall,
							dbSyntax);

	}
	private static String getSQLValueList(String vcMetaDataXML,KeyValue bioModelKey,KeyValue newVCMetaDataKey){
		StringBuffer buffer = new StringBuffer();
		buffer.append("(");
		buffer.append(newVCMetaDataKey+",");
		buffer.append(bioModelKey+",");
		if(DbDriver.varchar2_CLOB_is_Varchar2_OK(vcMetaDataXML)){
			buffer.append("null"+","+DbDriver.INSERT_VARCHAR2_HERE);
		}else{
			buffer.append(DbDriver.INSERT_CLOB_HERE+","+"null");
		}
		buffer.append(")");
		
		return buffer.toString();
	}

}
