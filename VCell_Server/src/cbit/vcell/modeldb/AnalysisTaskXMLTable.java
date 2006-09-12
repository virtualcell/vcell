package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.sql.*;
/**
 * This type was created in VisualAge.
 */
public class AnalysisTaskXMLTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_analysisTask";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field simContextRef =	new Field("simContextRef",		"integer",	"NOT NULL "+SimContextTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field analysisTaskXML =	new Field("analysisTaskXML",	"CLOB",	"NOT NULL");
	public final Field insertDate =		new Field("insertDate","DATE","NOT NULL");
	
	private final Field fields[] = {simContextRef,analysisTaskXML,insertDate};
	
	public static final AnalysisTaskXMLTable table = new AnalysisTaskXMLTable();

/**
 * ModelTable constructor comment.
 */
private AnalysisTaskXMLTable() {
	super(TABLE_NAME);
	addFields(fields);
}
}