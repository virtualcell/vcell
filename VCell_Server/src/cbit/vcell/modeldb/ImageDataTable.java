package cbit.vcell.modeldb;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.sql.*;
import cbit.image.*;
import cbit.util.DataAccessException;
import cbit.util.KeyValue;
import cbit.util.SessionLog;
import cbit.vcell.server.*;
import java.sql.*;

/**
 * This type was created in VisualAge.
 */
public class ImageDataTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_imagedata";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field imageRef 		= new Field("imageRef",	"integer",	"NOT NULL "+ImageTable.REF_TYPE+" ON DELETE CASCADE");
	//public final Field data 			= new Field("data",		"long raw",	"NOT NULL");
	public final Field data 			= new Field("data",		"BLOB",	"NOT NULL");
	

	private final Field fields[] = {imageRef,data};
	
	public static final ImageDataTable table = new ImageDataTable();
/**
 * ModelTable constructor comment.
 */
private ImageDataTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return VCImage
 * @param rset ResultSet
 * @param log SessionLog
 */
public byte[] getData(ResultSet rset, SessionLog log) throws SQLException,DataAccessException {

	//byte byteData[] = rset.getBytes(data.toString());
	byte byteData[] = (byte[]) DbDriver.getLOB(rset,data.toString());
	return byteData;
	
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
