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
public class StochtestCompareTable extends Table {
	private static final String TABLE_NAME = "stochtestcompare";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field stochtestrunref1		= new Field("stochtestrunref1",		SQLDataType.integer,		"NOT NULL " + StochtestRunTable.REF_TYPE + " ON DELETE CASCADE");
	public final Field stochtestrunref2		= new Field("stochtestrunref2",		SQLDataType.integer,		"NOT NULL " + StochtestRunTable.REF_TYPE + " ON DELETE CASCADE");
	public final Field results				= new Field("results",				SQLDataType.varchar2_4000,	"");
	public final Field status				= new Field("status",				SQLDataType.varchar2_32,	"");
	public final Field errmsg				= new Field("errmsg",				SQLDataType.varchar2_4000,	"");
	public final Field conclusion			= new Field("conclusion",			SQLDataType.varchar2_4000,	"");
	public final Field smallest_pvalue		= new Field("smallest_pvalue",		SQLDataType.number_as_real, "");
	public final Field numexperiments		= new Field("numexperiments",		SQLDataType.integer,		"");
	public final Field numfail_95			= new Field("numfail_95",			SQLDataType.integer,		"");
	public final Field numfail_99			= new Field("numfail_99",			SQLDataType.integer,		"");
	public final Field numfail_999			= new Field("numfail_999",			SQLDataType.integer,		"");

	private final Field fields[] = {stochtestrunref1,stochtestrunref2,results,status,errmsg,conclusion,smallest_pvalue,numexperiments,numfail_95,numfail_99,numfail_999};
	
	public static final StochtestCompareTable table = new StochtestCompareTable();

/**
 * ModelTable constructor comment.
 */
private StochtestCompareTable() {
	super(TABLE_NAME);
	addFields(fields);
}
}
