package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.sql.Field;
import cbit.sql.Table;
import cbit.sql.VersionTable;
/**
 * This type was created in VisualAge.
 */
public class UserLogTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_userlog";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field userRef				= new Field("userRef",				"integer",		"NOT NULL "+UserTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field eventDate			= new Field("eventDate",			"date",			"NOT NULL");
	public final Field logText				= new Field("logText",				"varchar2(255)","NOT NULL");
	public final Field filePath				= new Field("filePath",				"varchar2(255)","");
	public final Field eventType			= new Field("eventType",			"varchar2(10)",	"NOT NULL");
	public final Field swVersion			= new Field("swVersion",			"varchar2(10)", "");
	public final Field ipAddress			= new Field("ipAddress",			"varchar2(40)",	"");

	private final Field fields[] = {userRef,eventDate,logText,filePath,eventType,swVersion,ipAddress};
	
	public static final UserLogTable table = new UserLogTable();

/**
 * ModelTable constructor comment.
 */
private UserLogTable() {
	super(TABLE_NAME);
	addFields(fields);
}


/**
 * Insert the method's description here.
 * Creation date: (11/15/2004 12:35:04 PM)
 * @return java.lang.String
 * @param userid java.lang.String
 * @param date java.util.Date
 * @param text java.lang.String
 * @param filePath java.lang.String
 * @param eventType java.lang.String
 * @param swVersion java.lang.String
 * @param ipAddress java.lang.String
 */
public String getSQLValueList(String argUserid, java.util.Date argDate, String argText, String argFilePath, String argEventType, String argSWVersion, String argIPAddress) {

	//public final Field userRef			= new Field("userRef",				"integer",		"NOT NULL "+UserTable.REF_TYPE+" ON DELETE CASCADE");
	//public final Field eventDate			= new Field("eventDate",			"date",			"NOT NULL");
	//public final Field logText			= new Field("logText",				"varchar2(255)","NOT NULL");
	//public final Field filePath			= new Field("filePath",				"varchar2(255)","");
	//public final Field eventType			= new Field("eventType",			"varchar2(10)",	"NOT NULL");
	//public final Field swVersion			= new Field("swVersion",			"varchar2(10)", "");
	//public final Field ipAddress			= new Field("ipAddress",			"varchar2(40)",	"");

	
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(Table.NewSEQ + ",");
	buffer.append("(SELECT id from "+UserTable.table.getTableName()+" where "+UserTable.table.userid.getUnqualifiedColName()+" = '"+argUserid+"')" + ",");
	buffer.append(VersionTable.formatDateToOracle(argDate) + ",");
	buffer.append("'"+cbit.util.TokenMangler.getSQLEscapedString(argText,255)+"'"+",");
	if (argFilePath!=null){
		buffer.append("'"+cbit.util.TokenMangler.getSQLEscapedString(argFilePath,255)+"'"+",");
	}else{
		buffer.append("null,");
	}
	buffer.append("'"+cbit.util.TokenMangler.getSQLEscapedString(argEventType,10)+"'"+",");
	if (argSWVersion!=null){
		buffer.append("'"+cbit.util.TokenMangler.getSQLEscapedString(argSWVersion,10)+"'"+",");
	}else{
		buffer.append("null,");
	}
	buffer.append("'"+cbit.util.TokenMangler.getSQLEscapedString(argIPAddress,40)+"'");	
	buffer.append(")");
	
	return buffer.toString();
}
}