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

import cbit.sql.*;
/**
 * This type was created in VisualAge.
 */
public class CellTypeTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_celltype";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field name			= new Field("name",			"varchar(255)",			"NOT NULL");
	public final Field annotation	= new Field("annotation",	"varchar(255)",			"NOT NULL");

	private final Field fields[] = {name,annotation};
	
	public static final CellTypeTable table = new CellTypeTable();
/**
 * ModelTable constructor comment.
 */
private CellTypeTable() {
	super(TABLE_NAME);
	addFields(fields);
}
}
