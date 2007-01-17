package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.sql.*;
/**
 * This type was created in VisualAge.
 */
public class TFTestSuiteTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_tfTestSuite";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    private static final String[] tsBuildAndNumericsUniqueConstraint =
    			new String[] {
    			"ts_bld_num_unique UNIQUE(vcBuildVersion,vcNumericsVersion)"};
    			
	public final Field tsVersion =				new Field("tsVersion",			"VARCHAR2(64)",	"UNIQUE NOT NULL ");
	public final Field vcBuildVersion =			new Field("vcBuildVersion",		"VARCHAR2(64)",	"NOT NULL");
	public final Field vcNumericsVersion =		new Field("vcNumericsVersion",	"VARCHAR2(64)",	"NOT NULL");
	public final Field creationDate =			new Field("creationDate",		"DATE",			"NOT NULL");
	public final Field changeDate =				new Field("changeDate",			"DATE",			"");
	
	private final Field fields[] = {tsVersion,vcBuildVersion,vcNumericsVersion,creationDate,changeDate};
	
	public static final TFTestSuiteTable table = new TFTestSuiteTable();

	
/**
 * ModelTable constructor comment.
 */
private TFTestSuiteTable() {
	super(TABLE_NAME,tsBuildAndNumericsUniqueConstraint);
	addFields(fields);
}
}