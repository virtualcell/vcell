package cbit.vcell.modeldb;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.sql.Field;
import cbit.sql.Table;
import cbit.util.KeyValue;
/**
 * This type was created in VisualAge.
 */
public class ModelStructLinkTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_modelstruct";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field modelRef 		= new Field("modelRef",		"integer",	"NOT NULL "+ModelTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field structRef		= new Field("structRef",	"integer",	"NOT NULL "+StructTable.REF_TYPE);

	private final Field fields[] = {modelRef,structRef};
	
	public static final ModelStructLinkTable table = new ModelStructLinkTable();
/**
 * ModelTable constructor comment.
 */
private ModelStructLinkTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key, KeyValue modelKey, KeyValue structKey) {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key+",");
	buffer.append(modelKey+",");
	buffer.append(structKey+")");

	return buffer.toString();
}
}
