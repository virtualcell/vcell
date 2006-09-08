package cbit.vcell.modeldb;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.sql.*;
/**
 * This type was created in VisualAge.
 */
public class BioModelXMLTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_biomodelxml";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field bioModelRef =	new Field("biomodelRef",		"integer",	"UNIQUE NOT NULL "+BioModelTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field bmXML =			new Field("bmxml",	"CLOB",	"NOT NULL");
	public final Field changeDate =		new Field("changeDate","DATE","NOT NULL");
	
	private final Field fields[] = {bioModelRef,bmXML,changeDate};
	
	public static final BioModelXMLTable table = new BioModelXMLTable();
/**
 * ModelTable constructor comment.
 */
private BioModelXMLTable() {
	super(TABLE_NAME);
	addFields(fields);
}
}
