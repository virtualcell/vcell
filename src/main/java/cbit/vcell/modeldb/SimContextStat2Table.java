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
public class SimContextStat2Table extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_simcontextstat2";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field simContextRef		= new Field("simContextRef",		SQLDataType.integer,			"NOT NULL "+SimContextTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field hasData				= new Field("hasData",				SQLDataType.number_as_integer,	"");
	public final Field equiv				= new Field("equiv",				SQLDataType.number_as_integer,	"");
	public final Field status				= new Field("status",				SQLDataType.varchar2_255,		"");
	public final Field curatorEquiv			= new Field("curatorEquiv",			SQLDataType.integer,			"");
	public final Field comments				= new Field("comments",				SQLDataType.varchar2_255,		"");
	public final Field equiv_4_8			= new Field("equiv_4_8",			SQLDataType.number_as_integer,	"");
	public final Field status_4_8			= new Field("status_4_8",			SQLDataType.varchar2_255,		"");

	private final Field fields[] = {simContextRef, hasData, equiv, status, curatorEquiv, comments };
	
	public static final SimContextStat2Table table = new SimContextStat2Table();

/**
 * ModelTable constructor comment.
 */
private SimContextStat2Table() {
	super(TABLE_NAME);
	addFields(fields);
}
}
