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
public class TFTestResultTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_tfTestResult";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    private static final String[] tfTestResultsConstraint =
    			new String[] {
    			"tres_tcr_vn_unique UNIQUE(testCriteriaRef,varName)"};
    			
	public final Field testCriteriaRef = 	new Field("testCriteriaRef",	"INTEGER",			"NOT NULL "+TFTestCriteriaTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field varName =			new Field("varName",			"VARCHAR2(128)",	"NOT NULL");
	public final Field absError = 			new Field("absError",			"NUMBER",			"NOT NULL");
	public final Field relError = 			new Field("relError",			"NUMBER",			"NOT NULL");
	public final Field maxRef = 			new Field("maxRef",				"NUMBER",			"NOT NULL");
	public final Field minRef = 			new Field("minRef",				"NUMBER",			"NOT NULL");
	public final Field meanSqrError = 		new Field("meanSqrError",		"NUMBER",			"NOT NULL");
	public final Field timeAbsError = 		new Field("timeAbsError",		"NUMBER",			"NOT NULL");
	public final Field indexAbsError = 		new Field("indexAbsError",		"INTEGER",			"NOT NULL");
	public final Field timeRelError = 		new Field("timeRelError",		"NUMBER",			"NOT NULL");
	public final Field indexRelError = 		new Field("indexRelError",		"INTEGER",			"NOT NULL");
	
	private final Field fields[] = {testCriteriaRef,varName,absError,relError,maxRef,minRef,meanSqrError,timeAbsError,indexAbsError,timeRelError,indexRelError};
	
	public static final TFTestResultTable table = new TFTestResultTable();
	
/**
 * ModelTable constructor comment.
 */
private TFTestResultTable() {
	super(TABLE_NAME,tfTestResultsConstraint);
	addFields(fields);
}
public String getCreateTriggerSQL(){
	return 
	"CREATE OR REPLACE TRIGGER VCELL.TRSLT_LOCK_TRIG"+"\n"+
	"BEFORE DELETE OR INSERT OR UPDATE"+"\n"+
	"ON VCELL."+TFTestResultTable.table.getTableName()+"\n"+
	"REFERENCING NEW AS NEW OLD AS OLD"+"\n"+
	"FOR EACH ROW"+"\n"+
	"DECLARE"+"\n"+
	"PRAGMA AUTONOMOUS_TRANSACTION;"+"\n"+
	"testsuiteid NUMBER;"+"\n"+
	"lockState NUMBER;"+"\n"+
	"BEGIN"+"\n"+
	"IF INSERTING THEN"+"\n"+
	"testsuiteid :=:NEW."+TFTestResultTable.table.testCriteriaRef.getUnqualifiedColName()+";"+"\n"+
	"ELSIF UPDATING THEN"+"\n"+
	"testsuiteid :=:OLD."+TFTestResultTable.table.testCriteriaRef.getUnqualifiedColName()+";"+"\n"+
	"ELSIF DELETING THEN"+"\n"+
	"testsuiteid :=:OLD."+TFTestResultTable.table.testCriteriaRef.getUnqualifiedColName()+";"+"\n"+
	"END IF;"+"\n"+
	   "SELECT "+TFTestSuiteTable.table.isLocked.getQualifiedColName()+"\n"+
	   "INTO lockstate"+"\n"+
	   "FROM "+
	   TFTestSuiteTable.table.getTableName()+","+
	   TFTestCaseTable.table.getTableName()+","+
	   TFTestCriteriaTable.table.getTableName()+""+"\n"+
	   "WHERE "+TFTestSuiteTable.table.id.getQualifiedColName()+" = "+TFTestCaseTable.table.testSuiteRef.getQualifiedColName()+"\n"+
	   "AND"+"\n"+
	   TFTestCaseTable.table.id.getQualifiedColName()+" = "+TFTestCriteriaTable.table.testCaseRef.getQualifiedColName()+"\n"+
	   "AND"+"\n"+
	   TFTestCriteriaTable.table.id.getQualifiedColName()+" = testsuiteid;"+"\n"+
	   "IF"+"\n"+
	  " 	 lockstate != 0"+"\n"+
	   "THEN"+"\n"+
	   "	   raise_application_error(-20100,'Test Suite locked',true);"+"\n"+
	  "END IF;"+"\n"+
	"END;";

}
}
