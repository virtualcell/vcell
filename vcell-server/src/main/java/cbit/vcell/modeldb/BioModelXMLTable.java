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

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
/**
 * This type was created in VisualAge.
 */
public class BioModelXMLTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_biomodelxml";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field bioModelRef =	new Field("biomodelRef",	SQLDataType.integer,	"UNIQUE NOT NULL "+BioModelTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field bmXML =			new Field("bmxml",			SQLDataType.clob_text,	"NOT NULL");
	public final Field changeDate =		new Field("changeDate",		SQLDataType.date,		"NOT NULL");
	
	private final Field fields[] = {bioModelRef,bmXML,changeDate};
	
	public static final BioModelXMLTable table = new BioModelXMLTable();
/**
 * ModelTable constructor comment.
 */
private BioModelXMLTable() {
	super(TABLE_NAME);
	addFields(fields);
}
}
