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
import org.vcell.db.KeyFactory;

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
/**
 * This type was created in VisualAge.
 */
public class UserLogTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_userlog";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field userRef		= new Field("userRef",		SQLDataType.integer,		"NOT NULL "+UserTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field eventDate	= new Field("eventDate",	SQLDataType.date,			"NOT NULL");
	public final Field logText		= new Field("logText",		SQLDataType.varchar2_255,	"NOT NULL");
	public final Field filePath		= new Field("filePath",		SQLDataType.varchar2_255,	"");
	public final Field eventType	= new Field("eventType",	SQLDataType.varchar2_10,	"NOT NULL");
	public final Field swVersion	= new Field("swVersion",	SQLDataType.varchar2_10, 	"");
	public final Field ipAddress	= new Field("ipAddress",	SQLDataType.varchar2_40,	"");

	private final Field fields[] = {userRef,eventDate,logText,filePath,eventType,swVersion,ipAddress};
	
	public static final UserLogTable table = new UserLogTable();

/**
 * ModelTable constructor comment.
 */
private UserLogTable() {
	super(TABLE_NAME);
	addFields(fields);
}


/**
 * Insert the method's description here.
 * Creation date: (11/15/2004 12:35:04 PM)
 * @return java.lang.String
 * @param userid java.lang.String
 * @param date java.util.Date
 * @param text java.lang.String
 * @param filePath java.lang.String
 * @param eventType java.lang.String
 * @param swVersion java.lang.String
 * @param ipAddress java.lang.String
 */
public String getSQLValueList(String argUserid, java.util.Date argDate, String argText, String argFilePath, String argEventType, String argSWVersion, String argIPAddress, KeyFactory keyFactory) {

	//public final Field userRef			= new Field("userRef",				"integer",		"NOT NULL "+UserTable.REF_TYPE+" ON DELETE CASCADE");
	//public final Field eventDate			= new Field("eventDate",			"date",			"NOT NULL");
	//public final Field logText			= new Field("logText",				"varchar2(255)","NOT NULL");
	//public final Field filePath			= new Field("filePath",				"varchar2(255)","");
	//public final Field eventType			= new Field("eventType",			"varchar2(10)",	"NOT NULL");
	//public final Field swVersion			= new Field("swVersion",			"varchar2(10)", "");
	//public final Field ipAddress			= new Field("ipAddress",			"varchar2(40)",	"");

	
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(keyFactory.nextSEQ() + ",");
	buffer.append("(SELECT id from "+UserTable.table.getTableName()+" where "+UserTable.table.userid.getUnqualifiedColName()+" = '"+argUserid+"')" + ",");
	buffer.append(VersionTable.formatDateToOracle(argDate) + ",");
	buffer.append("'"+org.vcell.util.TokenMangler.getSQLEscapedString(argText,255)+"'"+",");
	if (argFilePath!=null){
		buffer.append("'"+org.vcell.util.TokenMangler.getSQLEscapedString(argFilePath,255)+"'"+",");
	}else{
		buffer.append("null,");
	}
	buffer.append("'"+org.vcell.util.TokenMangler.getSQLEscapedString(argEventType,10)+"'"+",");
	if (argSWVersion!=null){
		buffer.append("'"+org.vcell.util.TokenMangler.getSQLEscapedString(argSWVersion,10)+"'"+",");
	}else{
		buffer.append("null,");
	}
	buffer.append("'"+org.vcell.util.TokenMangler.getSQLEscapedString(argIPAddress,40)+"'");	
	buffer.append(")");
	
	return buffer.toString();
}
}
