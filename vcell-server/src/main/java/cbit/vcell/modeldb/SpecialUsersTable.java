package cbit.vcell.modeldb;

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.sql.Field.SQLDataType;

public class SpecialUsersTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_specialusers";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field special		= new Field("SPECIAL",		SQLDataType.varchar_255,100,		"NOT NULL");
	public final Field userRef 		= new Field("USERREF",		SQLDataType.integer,				"NOT NULL "+UserTable.REF_TYPE);
	public final Field userDetail	= new Field("USERDETAIL",	SQLDataType.varchar_255,1000,		"");

	private final Field fields[] = {special,userRef,userDetail};
	
	public static final SpecialUsersTable table = new SpecialUsersTable();
/**
 * ModelTable constructor comment.
 */
private SpecialUsersTable() {
	super(TABLE_NAME);
	addFields(fields);
}

}
