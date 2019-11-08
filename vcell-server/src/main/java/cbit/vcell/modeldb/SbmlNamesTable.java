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

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.vcell.util.DataAccessException;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.InsertHashtable;
import cbit.sql.Table;
import cbit.vcell.model.ReactionStep;

public class SbmlNamesTable extends Table {
	private static final String TABLE_NAME = "vc_sbmlnames";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field name		 		= new Field("name",				SQLDataType.varchar_255,	"NOT NULL");
	public final Field reactstepref		= new Field("reactstepRef",		SQLDataType.integer,		ReactStepTable.REF_TYPE+" ON DELETE CASCADE");
	
	private final Field fields[] = {name,reactstepref};
	
	public static final SbmlNamesTable table = new SbmlNamesTable();
/**
 * ModelTable constructor comment.
 */
private SbmlNamesTable() {
	super(TABLE_NAME);
	addFields(fields);
}

public String getSQLValueList(InsertHashtable hash, KeyValue key, KeyValue reactStepKey,String sbmlName) throws DataAccessException {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key+",");
	buffer.append("'"+TokenMangler.getSQLEscapedString(sbmlName, 255)+"'"+",");
	buffer.append(reactStepKey+")");

	return buffer.toString();
}
public static void updateInsertNameSQL(Connection con,KeyValue updateThisReference,Field forSbmlNameColumn,String newSbmlName) throws SQLException{
//	MERGE INTO vc_sbmlnames d
//	USING (SELECT 1833 reactstepref, 'newname2' name from dual) s
//	ON (d.reactstepref is not null and d.reactstepref = s.reactstepref)
//	WHEN MATCHED THEN UPDATE SET d.name = s.name
//	WHEN NOT MATCHED THEN INSERT (id, name,reactstepref) VALUES (newseq.nextval, s.name,s.reactstepref);
	if(newSbmlName != null && !newSbmlName.isEmpty()) {
		String sql = 
				"MERGE INTO "+SbmlNamesTable.TABLE_NAME+" d"+
				" USING (SELECT "+updateThisReference.toString()+" "+forSbmlNameColumn.getUnqualifiedColName()+", '"+TokenMangler.getSQLEscapedString(newSbmlName, 255)+"' "+SbmlNamesTable.table.name.getUnqualifiedColName()+" from dual) s"+
				" ON (d."+forSbmlNameColumn.getUnqualifiedColName()+" is not null and d."+forSbmlNameColumn.getUnqualifiedColName()+" = s."+forSbmlNameColumn.getUnqualifiedColName()+")"+
				" WHEN MATCHED THEN UPDATE SET d."+SbmlNamesTable.table.name.getUnqualifiedColName()+" = s."+SbmlNamesTable.table.name.getUnqualifiedColName()+""+
				" WHEN NOT MATCHED THEN INSERT ("+SbmlNamesTable.table.id.getUnqualifiedColName()+", "+SbmlNamesTable.table.name.getUnqualifiedColName()+","+forSbmlNameColumn.getUnqualifiedColName()+")"+
					" VALUES ("+SEQ+".nextval, s."+SbmlNamesTable.table.name.getUnqualifiedColName()+",s."+forSbmlNameColumn.getUnqualifiedColName()+")";
		DbDriver.updateCleanSQL(con, sql);
	}else {
		DbDriver.updateCleanSQL(con, "DELETE from "+SbmlNamesTable.TABLE_NAME+" where "+forSbmlNameColumn.getUnqualifiedColName()+" IS NOT NULL" +" AND "+forSbmlNameColumn.getUnqualifiedColName()+" = "+updateThisReference);
	}
}

public static void updateSbmlName(Connection con,Object obj,KeyValue key) throws SQLException,PropertyVetoException{
	if(obj instanceof ReactionStep) {
		try (Statement stmt = con.createStatement();
			ResultSet rset = stmt.executeQuery("SELECT "+SbmlNamesTable.table.name.getUnqualifiedColName()+" FROM "+SbmlNamesTable.TABLE_NAME+" WHERE "+SbmlNamesTable.table.reactstepref.getUnqualifiedColName()+
			" IS NOT NULL AND "+SbmlNamesTable.table.reactstepref.getUnqualifiedColName()+" = "+key.toString())){
			if(rset.next()) {
				String sbmlName = rset.getString(SbmlNamesTable.table.name.getUnqualifiedColName());
				if(!rset.wasNull()) {
					((ReactionStep)obj).setSbmlName(TokenMangler.getSQLRestoredString(sbmlName));
				}
			}
		}
	}
}

}
