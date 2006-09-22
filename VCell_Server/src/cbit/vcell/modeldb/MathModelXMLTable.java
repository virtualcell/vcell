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
public class MathModelXMLTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_mathmodelxml";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field mathModelRef =	new Field("mathmodelRef",		"integer",	"UNIQUE NOT NULL "+MathModelTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field mmXML =			new Field("mmxml",	"CLOB",	"NOT NULL");
	public final Field changeDate =		new Field("changeDate","DATE","NOT NULL");
	
	private final Field fields[] = {mathModelRef,mmXML,changeDate};
	
	public static final MathModelXMLTable table = new MathModelXMLTable();

/**
 * ModelTable constructor comment.
 */
private MathModelXMLTable() {
	super(TABLE_NAME);
	addFields(fields);
}
}