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

import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
/**
 * This type was created in VisualAge.
 */
public class AvailableTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_available";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field insertDate			= new Field("insertDate",			SQLDataType.date,			"NOT NULL ");
	public final Field isAvailable			= new Field("isAvailable",			SQLDataType.varchar2_5,		"NOT NULL ");
	public final Field letUserAskForCallback= new Field("letUserAskForCallback",SQLDataType.varchar2_5,		"");
	public final Field offlineMessage		= new Field("offlineMessage",		SQLDataType.varchar2_512,	"");

	private final Field fields[] = {insertDate,isAvailable,letUserAskForCallback,offlineMessage};
	
	public static final AvailableTable table = new AvailableTable();
/**
 * ModelTable constructor comment.
 */
private AvailableTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * Insert the method's description here.
 * Creation date: (1/8/2002 4:21:18 PM)
 * @return java.lang.String
 */
public static final String getCreateInitAvailStatusSQL(KeyValue key) {
	String sql = "INSERT INTO "+AvailableTable.table.getTableName()+
			" VALUES ("+key.toString()+","+
						"current_timestamp"+","+	//insertDate
						"'true'"+","+	//isAvailable
						"NULL"+","+		//letUserAskForCallback
						"NULL"+")";		//offlineMessage
	return sql;
}
}
