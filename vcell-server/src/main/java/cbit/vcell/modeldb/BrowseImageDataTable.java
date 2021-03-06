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

import org.vcell.db.DatabaseSyntax;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
/**
 * This type was created in VisualAge.
 */
public class BrowseImageDataTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_browsedata";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field imageRef	= new Field("imageRef",	SQLDataType.integer,	"NOT NULL "+ImageTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field data 	= new Field("data",		SQLDataType.blob_bytea,	"NOT NULL");
	
	private final Field fields[] = {imageRef,data};
	
	public static final BrowseImageDataTable table = new BrowseImageDataTable();
/**
 * ModelTable constructor comment.
 */
private BrowseImageDataTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key, KeyValue imageKey, DatabaseSyntax dbSyntax) {
	switch (dbSyntax){
	case ORACLE:{
		StringBuffer buffer = new StringBuffer();
		buffer.append("(");
		buffer.append(key+",");
		buffer.append(imageKey+",");
		buffer.append("EMPTY_BLOB())");

		return buffer.toString();
	}
	case POSTGRES:{
		StringBuffer buffer = new StringBuffer();
		buffer.append("(");
		buffer.append(key+",");
		buffer.append(imageKey+",");
		buffer.append("?"+")");

		return buffer.toString();
	}
	default:{
		throw new RuntimeException("unexpected DatabaseSyntax "+dbSyntax);
	}
	}
}
}
