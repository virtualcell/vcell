package cbit.vcell.modeldb;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.sql.*;

import org.vcell.util.document.KeyValue;

import cbit.vcell.server.SessionLog;
import cbit.vcell.geometry.*;
import cbit.vcell.parser.*;
import cbit.vcell.server.DataAccessException;
import cbit.sql.*;
/**
 * This type was created in VisualAge.
 */
public class FilamentTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_filament";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field filamentName	= new Field("filamentName",	"varchar(255)",	"NOT NULL");
	public final Field geometryRef	= new Field("geometryRef",	"integer",		"NOT NULL "+GeometryTable.REF_TYPE+" ON DELETE CASCADE");
	
	private final Field fields[] = {filamentName,geometryRef};
	
	public static final FilamentTable table = new FilamentTable();
/**
 * ModelTable constructor comment.
 */
private FilamentTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key,String filamentName,KeyValue geomKey) throws DataAccessException {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key + ",");
	buffer.append("'" + filamentName + "',");
	buffer.append(geomKey + ")");
	
	return buffer.toString();
}
}
