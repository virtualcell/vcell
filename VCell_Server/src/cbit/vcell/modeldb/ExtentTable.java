package cbit.vcell.modeldb;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.sql.*;
import cbit.util.KeyValue;
/**
 * This type was created in VisualAge.
 */
public class ExtentTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_geomextent";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field extentX		= new Field("extentX",		"NUMBER",				"NOT NULL");
	public final Field extentY		= new Field("extentY",		"NUMBER",				"NOT NULL");
	public final Field extentZ		= new Field("extentZ",		"NUMBER",				"NOT NULL");

	private final Field fields[] = {extentX,extentY,extentZ};
	
	public static final ExtentTable table = new ExtentTable();
/**
 * ModelTable constructor comment.
 */
private ExtentTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key, double eX,double eY,double eZ) {
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key.toString()+",");
	buffer.append(eX+",");
	buffer.append(eY+",");
	buffer.append(eZ+")");

	return buffer.toString();
}
}
