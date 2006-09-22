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
public class BrowseImageDataTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_browsedata";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field imageRef	= new Field("imageRef",	"integer",	"NOT NULL "+ImageTable.REF_TYPE+" ON DELETE CASCADE");
	//public final Field data 	= new Field("data",		"long raw",	"NOT NULL");
	public final Field data 	= new Field("data",		"BLOB",	"NOT NULL");
	
	private final Field fields[] = {imageRef,data};
	
	public static final BrowseImageDataTable table = new BrowseImageDataTable();
/**
 * ModelTable constructor comment.
 */
private BrowseImageDataTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key, KeyValue imageKey) {
	
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key+",");
	buffer.append(imageKey+",");
	buffer.append("EMPTY_BLOB())");

	return buffer.toString();
}
}
