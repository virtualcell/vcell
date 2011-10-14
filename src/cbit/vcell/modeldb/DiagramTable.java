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

import java.sql.*;

import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;

import cbit.sql.*;
import cbit.vcell.model.*;
/**
 * This type was created in VisualAge.
 */
public class DiagramTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_diagram";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field name				= new Field("name",			"varchar(255)",	"NOT NULL");
	public final Field modelRef 		= new Field("modelRef",		"integer",		"NOT NULL "+ModelTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field structRef		= new Field("structRef",	"integer",		"NOT NULL "+StructTable.REF_TYPE);
	public final Field language 		= new Field("language",		"CLOB",			"");
	public final Field diagramLarge		= new Field("diagramLRG",	"CLOB",				"");
	public final Field diagramSmall		= new Field("diagramSML",	"VARCHAR2(4000)",	"");

	private final Field fields[] = {name,modelRef,structRef,language,diagramLarge,diagramSmall};
	
	public static final DiagramTable table = new DiagramTable();
/**
 * ModelTable constructor comment.
 */
private DiagramTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return Model
 * @param rset ResultSet
 * @param log SessionLog
 */
public Diagram getDiagram(ResultSet rset, SessionLog log) throws SQLException, DataAccessException {

	KeyValue key = new KeyValue(rset.getBigDecimal(id.toString()));
	String mName = rset.getString(name.toString());

	Diagram diagram = new Diagram(null,mName);
	
	String languageString =
		DbDriver.varchar2_CLOB_get(rset,DiagramTable.table.diagramSmall,DiagramTable.table.diagramLarge);
	if(languageString == null || languageString.length() == 0){
		throw new DataAccessException("no data stored for Diagram");
	}
	//Is this needed?
	//if (languageString.endsWith(";}\n")){
		//StringBuffer buffer = new StringBuffer(languageString.substring(0,languageString.length()-2));
		//buffer.append("\n}\n");
		//languageString = buffer.toString();
	//}
	
	org.vcell.util.CommentStringTokenizer tokens = new org.vcell.util.CommentStringTokenizer(languageString);
	try {
		diagram.fromTokens(tokens);
	}catch (Exception e){
		log.exception(e);
		throw new DataAccessException(e.getMessage());
	}
	
	return diagram;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key, Diagram diagram, KeyValue modelID, KeyValue structID) {
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key+",");
	buffer.append("'"+diagram.getName()+"',");
	buffer.append(modelID+",");
	buffer.append(structID+",");
	buffer.append("EMPTY_CLOB()"+","); //keep for compatibility with release site
	
	if(DbDriver.varchar2_CLOB_is_Varchar2_OK(diagram.getVCML())){
		buffer.append("null"+","+DbDriver.INSERT_VARCHAR2_HERE);
	}else{
		buffer.append(DbDriver.INSERT_CLOB_HERE+","+"null");
	}
	
	buffer.append(")");

	return buffer.toString();
}
}
