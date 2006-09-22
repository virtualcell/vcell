package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.sql.Field;
import cbit.sql.Table;
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
	
	private final Field fields[] = {testCriteriaRef,varName,absError,relError,maxRef,minRef,meanSqrError,timeAbsError,indexAbsError};
	
	public static final TFTestResultTable table = new TFTestResultTable();
	
/**
 * ModelTable constructor comment.
 */
private TFTestResultTable() {
	super(TABLE_NAME,tfTestResultsConstraint);
	addFields(fields);
}
}