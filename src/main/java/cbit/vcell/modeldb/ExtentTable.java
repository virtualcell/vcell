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
import cbit.sql.Table;
/**
 * This type was created in VisualAge.
 */
public class ExtentTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_geomextent";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field extentX		= new Field("extentX",		"NUMBER",				"NOT NULL");
	public final Field extentY		= new Field("extentY",		"NUMBER",				"NOT NULL");
	public final Field extentZ		= new Field("extentZ",		"NUMBER",				"NOT NULL");

	private final Field fields[] = {extentX,extentY,extentZ};
	
	public static final ExtentTable table = new ExtentTable();
/**
 * ModelTable constructor comment.
 */
private ExtentTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key, double eX,double eY,double eZ) {
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key.toString()+",");
	buffer.append(eX+",");
	buffer.append(eY+",");
	buffer.append(eZ+")");

	return buffer.toString();
}
}
