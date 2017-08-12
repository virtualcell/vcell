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
public class TFTestSuiteTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_tfTestSuite";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    private static final String[] tsBuildAndNumericsUniqueConstraintOracle =
			new String[] {
			"ts_bld_num_unique UNIQUE(vcBuildVersion,vcNumericsVersion)"};
			
    private static final String[] tsBuildAndNumericsUniqueConstraintPostgres =
			new String[] {
			"ts_bld_num_unique UNIQUE(vcBuildVersion,vcNumericsVersion)"};
			
	public final Field tsVersion =			new Field("tsVersion",			SQLDataType.varchar2_64,	"UNIQUE NOT NULL ");
	public final Field vcBuildVersion =		new Field("vcBuildVersion",		SQLDataType.varchar2_64,	"NOT NULL");
	public final Field vcNumericsVersion =	new Field("vcNumericsVersion",	SQLDataType.varchar2_64,	"NOT NULL");
	public final Field creationDate =		new Field("creationDate",		SQLDataType.date,			"NOT NULL");
	public final Field changeDate =			new Field("changeDate",			SQLDataType.date,			"");
	public final Field tsAnnotation =		new Field("tsAnnotation",		SQLDataType.varchar2_512,	"");
	public final Field isLocked =			new Field("isLocked",			SQLDataType.integer,		"NOT NULL");
	
	private final Field fields[] = {tsVersion,vcBuildVersion,vcNumericsVersion,creationDate,changeDate,tsAnnotation,isLocked};
	
	public static final TFTestSuiteTable table = new TFTestSuiteTable();

	
/**
 * ModelTable constructor comment.
 */
private TFTestSuiteTable() {
	super(TABLE_NAME,tsBuildAndNumericsUniqueConstraintOracle,tsBuildAndNumericsUniqueConstraintPostgres);
	addFields(fields);
}
public String getCreateTriggerSQL(){
	return 
	"CREATE OR REPLACE TRIGGER VCELL.TS_LOCK_TRIG"+"\n"+
	"BEFORE DELETE OR UPDATE"+"\n"+
	"ON VCELL."+TFTestSuiteTable.table.getTableName()+"\n"+
	"REFERENCING NEW AS NEW OLD AS OLD"+"\n"+
	"FOR EACH ROW"+"\n"+
	"DECLARE"+"\n"+
	"lockState NUMBER;"+"\n"+
	"BEGIN"+"\n"+
	   "IF :OLD."+TFTestSuiteTable.table.isLocked.getUnqualifiedColName()+" != 0"+"\n"+
	   "THEN"+"\n"+
	   "raise_application_error(-20100,'Test Suite ' || :OLD.tsversion || ' locked',true);"+"\n"+
	   "END IF;"+"\n"+
	"END;";

}
}
