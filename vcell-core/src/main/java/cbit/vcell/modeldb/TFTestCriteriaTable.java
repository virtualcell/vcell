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
public class TFTestCriteriaTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_tfTestCriteria";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public static final int MAX_MESSAGE_SIZE = 512;
	
    private static final String[] tcrefAndsimrefUniqueConstraintOracle =
			new String[] {
			"tcr_tcr_simr_unique UNIQUE(testCaseRef,simulationRef)"};
			
    private static final String[] tcrefAndsimrefUniqueConstraintPostgres =
			new String[] {
			"tcr_tcr_simr_unique UNIQUE(testCaseRef,simulationRef)"};
			
	public final Field testCaseRef = 		new Field("testCaseRef",		SQLDataType.integer,		"NOT NULL "+TFTestCaseTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field simulationRef = 		new Field("simulationRef",		SQLDataType.integer,		"UNIQUE NOT NULL "+SimulationTable.REF_TYPE);
	private final Field simDataRef = 		new Field("simDataRef",			SQLDataType.integer,		"");  // UNIQUE "+ResultSetMetaDataTable.REF_TYPE);  // no longer used.
	public final Field regressionMMSimRef = new Field("regressionSimRef",	SQLDataType.integer,		""+MathModelSimulationLinkTable.REF_TYPE);
	public final Field maxRelError = 		new Field("maxRelError",		SQLDataType.number_as_real,	"");
	public final Field maxAbsError = 		new Field("maxAbsError",		SQLDataType.number_as_real,	"");
	public final Field regressionBMAPPRef = new Field("regressionBMAPPRef",	SQLDataType.integer,		""+BioModelSimContextLinkTable.REF_TYPE);
	public final Field regressionBMSimRef = new Field("regressionBMSimRef",	SQLDataType.integer,		""+BioModelSimulationLinkTable.REF_TYPE);
	public final Field reportStatus = 		new Field("reportStatus",		SQLDataType.varchar2_32,	"");
	public final Field reportMessage= 		new Field("reportMessage",		SQLDataType.varchar2_512,	"");
	
	private final Field fields[] = {testCaseRef,simulationRef, simDataRef, regressionMMSimRef,
									maxRelError,maxAbsError,regressionBMAPPRef,regressionBMSimRef,reportStatus,reportMessage};
	
	public static final TFTestCriteriaTable table = new TFTestCriteriaTable();
	
/**
 * ModelTable constructor comment.
 */
private TFTestCriteriaTable() {
	super(TABLE_NAME,tcrefAndsimrefUniqueConstraintOracle,tcrefAndsimrefUniqueConstraintPostgres);
	addFields(fields);
}
public String getCreateTriggerSQL(){
	return 
	"CREATE OR REPLACE TRIGGER VCELL.TCRIT_LOCK_TRIG"+"\n"+
	"BEFORE DELETE OR INSERT OR UPDATE"+"\n"+
	"ON VCELL."+TFTestCriteriaTable.table.getTableName()+"\n"+
	"REFERENCING NEW AS NEW OLD AS OLD"+"\n"+
	"FOR EACH ROW"+"\n"+
	"DECLARE"+"\n"+
	"PRAGMA AUTONOMOUS_TRANSACTION;"+"\n"+
	"testcaseid NUMBER;"+"\n"+
	"lockState NUMBER;"+"\n"+
	"BEGIN"+"\n"+
	"IF INSERTING THEN"+"\n"+
	"testcaseid :=:NEW."+TFTestCriteriaTable.table.testCaseRef.getUnqualifiedColName()+";"+"\n"+
	"ELSIF UPDATING THEN"+"\n"+
	"testcaseid :=:OLD."+TFTestCriteriaTable.table.testCaseRef.getUnqualifiedColName()+";"+"\n"+
	"ELSIF DELETING THEN"+"\n"+
	"testcaseid :=:OLD."+TFTestCriteriaTable.table.testCaseRef.getUnqualifiedColName()+";"+"\n"+
	"END IF;"+"\n"+
	   "SELECT "+TFTestSuiteTable.table.isLocked.getQualifiedColName()+"\n"+
	   "INTO lockstate"+"\n"+
	   "FROM "+
	   TFTestSuiteTable.table.getTableName()+","+
	   TFTestCaseTable.table.getTableName()+"\n"+
	   "WHERE "+TFTestSuiteTable.table.id.getQualifiedColName()+" = "+TFTestCaseTable.table.testSuiteRef.getQualifiedColName()+"\n"+
	   "AND"+"\n"+
	   TFTestCaseTable.table.id.getQualifiedColName()+" = testcaseid;"+"\n"+
	   "IF"+"\n"+
	  " 	 lockstate != 0"+"\n"+
	   "THEN"+"\n"+
	   "	   raise_application_error(-20100,'Test Suite locked',true);"+"\n"+
	  "END IF;"+"\n"+
	"END;";

}
}
