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
import cbit.sql.Table;
/**
 * This type was created in VisualAge.
 */
public class StochtestRunTable extends Table {
	private static final String TABLE_NAME = "stochtestrun";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field stochtestref				= new Field("stochtestref",				"integer",	"NOT NULL " + StochtestTable.REF_TYPE + " ON DELETE CASCADE");
	public final Field parentmathtype			= new Field("parentmathtype",			"varchar2(64)",	"NOT NULL");
	public final Field mathtype					= new Field("mathtype",					"varchar2(64)",	"NOT NULL");
	public final Field status					= new Field("status",					"varchar2(32)", "NOT NULL");
	public final Field errmsg					= new Field("errmsg",					"varchar2(4000)", "");
	public final Field conclusion				= new Field("conclusion",				"varchar2(4000)", "");
	public final Field exclude					= new Field("exclude",					"varchar2(4000)", "");
	public final Field networkGenProbs			= new Field("networkGenProbs",			"varchar2(4000)", "");

	private final Field fields[] = {stochtestref,parentmathtype,mathtype,status,errmsg,conclusion,exclude,networkGenProbs};
	
	public static final StochtestRunTable table = new StochtestRunTable();

/**
 * ModelTable constructor comment.
 */
private StochtestRunTable() {
	super(TABLE_NAME);
	addFields(fields);
}
}
