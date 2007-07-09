package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.sql.*;
/**
 * This type was created in VisualAge.
 */
public class FieldDataTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_fielddata";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field ownerRef 		= new Field(VersionTable.ownerRef_ColumnName,	"integer",	"NOT NULL " + UserTable.REF_TYPE);
	public final Field fieldDataName	= new Field("fielddataname",	"varchar(255)",	"NOT NULL");
	public final Field dataFileName	= new Field("datafilename",	"varchar(255)",		"NOT NULL ");
	public final Field variableNames	= new Field("variablenames",	"varchar(255)",		"NOT NULL ");
	public final Field origin	= new Field("origin",	"varchar(512)",		"NOT NULL ");
	public final Field extent	= new Field("extent",	"varchar(512)",		"NOT NULL ");
	public final Field meshsize	= new Field("meshsize",	"varchar(512)",		"NOT NULL ");
	
	private final Field fields[] = {ownerRef, fieldDataName, dataFileName, variableNames, origin, extent, meshsize};
	
	public static final FieldDataTable table = new FieldDataTable();

/**
 * ModelTable constructor comment.
 */
private FieldDataTable() {
	super(TABLE_NAME);
	addFields(fields);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key,User user, String arg_fieldDataName,String arg_dataFileName, String arg_variableNames) throws DataAccessException {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key + ",");
	buffer.append(user.getID() + ",");
	buffer.append("'" + arg_fieldDataName + "',");
	buffer.append("'" + arg_dataFileName + "',");
	buffer.append("'" + arg_variableNames + "')");
	
	return buffer.toString();
}
}