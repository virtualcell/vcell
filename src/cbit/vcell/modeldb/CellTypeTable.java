package cbit.vcell.modeldb;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.sql.*;
/**
 * This type was created in VisualAge.
 */
public class CellTypeTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_celltype";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field name			= new Field("name",			"varchar(255)",			"NOT NULL");
	public final Field annotation	= new Field("annotation",	"varchar(255)",			"NOT NULL");

	private final Field fields[] = {name,annotation};
	
	public static final CellTypeTable table = new CellTypeTable();
/**
 * ModelTable constructor comment.
 */
private CellTypeTable() {
	super(TABLE_NAME);
	addFields(fields);
}
}
