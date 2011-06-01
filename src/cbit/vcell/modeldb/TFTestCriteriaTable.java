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
public class TFTestCriteriaTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_tfTestCriteria";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public static final int MAX_MESSAGE_SIZE = 512;
	
    private static final String[] tcrefAndsimrefUniqueConstraint =
    			new String[] {
    			"tcr_tcr_simr_unique UNIQUE(testCaseRef,simulationRef)"};
    			
	public final Field testCaseRef = 		new Field("testCaseRef",		"INTEGER",		"NOT NULL "+TFTestCaseTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field simulationRef = 		new Field("simulationRef",		"INTEGER",		"UNIQUE NOT NULL "+SimulationTable.REF_TYPE);
	public final Field simDataRef = 		new Field("simDataRef",			"INTEGER",		"UNIQUE "+ResultSetMetaDataTable.REF_TYPE);
	public final Field regressionMMSimRef = new Field("regressionSimRef",	"INTEGER",		""+MathModelSimulationLinkTable.REF_TYPE);
	public final Field maxRelError = 		new Field("maxRelError",		"NUMBER",		"");
	public final Field maxAbsError = 		new Field("maxAbsError",		"NUMBER",		"");
	public final Field regressionBMAPPRef = new Field("regressionBMAPPRef",	"INTEGER",		""+BioModelSimContextLinkTable.REF_TYPE);
	public final Field regressionBMSimRef = new Field("regressionBMSimRef",	"INTEGER",		""+BioModelSimulationLinkTable.REF_TYPE);
	public final Field reportStatus = 		new Field("reportStatus",		"VARCHAR2(32)",	"");
	public final Field reportMessage= 		new Field("reportMessage",		"VARCHAR2("+MAX_MESSAGE_SIZE+")","");
	
	private final Field fields[] = {testCaseRef,simulationRef,simDataRef,regressionMMSimRef,
									maxRelError,maxAbsError,regressionBMAPPRef,regressionBMSimRef,reportStatus,reportMessage};
	
	public static final TFTestCriteriaTable table = new TFTestCriteriaTable();
	
/**
 * ModelTable constructor comment.
 */
private TFTestCriteriaTable() {
	super(TABLE_NAME,tcrefAndsimrefUniqueConstraint);
	addFields(fields);
}
}
