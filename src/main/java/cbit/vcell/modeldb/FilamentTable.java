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

import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Table;
/**
 * This type was created in VisualAge.
 */
public class FilamentTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_filament";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field filamentName	= new Field("filamentName",	"varchar(255)",	"NOT NULL");
	public final Field geometryRef	= new Field("geometryRef",	"integer",		"NOT NULL "+GeometryTable.REF_TYPE+" ON DELETE CASCADE");
	
	private final Field fields[] = {filamentName,geometryRef};
	
	public static final FilamentTable table = new FilamentTable();
/**
 * ModelTable constructor comment.
 */
private FilamentTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key,String filamentName,KeyValue geomKey) throws DataAccessException {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key + ",");
	buffer.append("'" + filamentName + "',");
	buffer.append(geomKey + ")");
	
	return buffer.toString();
}
}
