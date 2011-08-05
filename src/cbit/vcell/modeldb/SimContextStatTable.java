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
public class SimContextStatTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_simcontextstat";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field simContextRef		= new Field("simContextRef",		"integer",		"NOT NULL "+SimContextTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field hasDupReact			= new Field("hasDupReact",			"number",		"");
	public final Field hasData				= new Field("hasData",				"number",		"");
	public final Field equiv				= new Field("equiv",				"number",		"");
	public final Field status				= new Field("status",				"varchar2(255)","");
	public final Field oldMath				= new Field("oldMath",				"CLOB",			"");
	public final Field newMath				= new Field("newMath",				"CLOB",			"");
	public final Field curatorEquiv			= new Field("curatorEquiv",			"integer",		"");
	public final Field comments				= new Field("comments",				"varchar2(255)","");
	public final Field updatedMath			= new Field("updatedMath",			"integer",		"");
//	public final Field ordinal				= new Field("ordinal",				"integer",		"");

	private final Field fields[] = {simContextRef, hasDupReact, hasData, equiv, status, oldMath, newMath, curatorEquiv, comments, updatedMath /*, ordinal */};
	
	public static final SimContextStatTable table = new SimContextStatTable();
/**
 * ModelTable constructor comment.
 */
private SimContextStatTable() {
	super(TABLE_NAME);
	addFields(fields);
}
}
