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
public class ModelStructLinkTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_modelstruct";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field modelRef 		= new Field("modelRef",		"integer",	"NOT NULL "+ModelTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field structRef		= new Field("structRef",	"integer",	"NOT NULL "+StructTable.REF_TYPE);

	private final Field fields[] = {modelRef,structRef};
	
	public static final ModelStructLinkTable table = new ModelStructLinkTable();
/**
 * ModelTable constructor comment.
 */
private ModelStructLinkTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key, KeyValue modelKey, KeyValue structKey) {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key+",");
	buffer.append(modelKey+",");
	buffer.append(structKey+")");

	return buffer.toString();
}
}
