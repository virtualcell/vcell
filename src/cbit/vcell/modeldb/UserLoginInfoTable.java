package cbit.vcell.modeldb;

import org.vcell.util.TokenMangler;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.vcell.server.UserLoginInfo;

public class UserLoginInfoTable extends Table {
	private static final String TABLE_NAME = "vc_userlogininfo";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    private static final String[] userLoginInfoTableUniqueConstraint =
		new String[] {
		"ulinfo_unique UNIQUE(userRef,osarch,osname,osvers,client)"};

    private static final int MAX_FIELD_LENGTH = 32;
    
	public final Field userRef		= new Field("userRef",		"integer",		"NOT NULL "+UserTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field loginCount	= new Field("loginCount",	"number",		"NOT NULL");
	public final Field osarch		= new Field("osarch",		"varchar2("+MAX_FIELD_LENGTH+")","NOT NULL");
	public final Field osname		= new Field("osname",		"varchar2("+MAX_FIELD_LENGTH+")","NOT NULL");
	public final Field osvers		= new Field("osvers",		"varchar2("+MAX_FIELD_LENGTH+")","NOT NULL");
	public final Field client		= new Field("client",		"varchar2("+MAX_FIELD_LENGTH+")","NOT NULL");

	private final Field fields[] = {userRef,loginCount,osarch,osname,osvers,client};
	
	public static final UserLoginInfoTable table = new UserLoginInfoTable();
/**
 * ModelTable constructor comment.
 */
private UserLoginInfoTable() {
	super(TABLE_NAME,userLoginInfoTableUniqueConstraint);
	addFields(fields);
}

public static String getSQLValueList(KeyValue userRef,int logincount,UserLoginInfo userLoginInfo) {
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(Table.NewSEQ+",");
	buffer.append(userRef.toString()+",");
	buffer.append(logincount+",");
	buffer.append("'"+TokenMangler.getSQLEscapedString(userLoginInfo.getOs_arch(), MAX_FIELD_LENGTH)+"'"+",");
	buffer.append("'"+TokenMangler.getSQLEscapedString(userLoginInfo.getOs_name(), MAX_FIELD_LENGTH)+"'"+",");
	buffer.append("'"+TokenMangler.getSQLEscapedString(userLoginInfo.getOs_version(), MAX_FIELD_LENGTH)+"'"+",");
	buffer.append("'"+TokenMangler.getSQLEscapedString(userLoginInfo.getClient(), MAX_FIELD_LENGTH)+"'");
	buffer.append(")");
	return buffer.toString();
}
public static String getSQLUpdateLoginCount(KeyValue userRef,UserLoginInfo userLoginInfo){
	return
		"UPDATE "+UserLoginInfoTable.table.getTableName()+
		" SET "+
			UserLoginInfoTable.table.loginCount + " = "+ UserLoginInfoTable.table.loginCount+" + 1"+
		" WHERE "+
			getSQLWhereCondition(userRef,userLoginInfo);
}
public static String getSQLWhereCondition(KeyValue userRef,UserLoginInfo userLoginInfo){
	return
	UserLoginInfoTable.table.userRef.getUnqualifiedColName()+ " = "+userRef.toString()+
	" AND "+
	UserLoginInfoTable.table.osarch.getUnqualifiedColName()+ " = "+"'"+TokenMangler.getSQLEscapedString(userLoginInfo.getOs_arch(), MAX_FIELD_LENGTH)+"'"+
	" AND "+
	UserLoginInfoTable.table.osname.getUnqualifiedColName()+ " = "+"'"+TokenMangler.getSQLEscapedString(userLoginInfo.getOs_name(), MAX_FIELD_LENGTH)+"'"+
	" AND "+
	UserLoginInfoTable.table.osvers.getUnqualifiedColName()+ " = "+"'"+TokenMangler.getSQLEscapedString(userLoginInfo.getOs_version(), MAX_FIELD_LENGTH)+"'"+
	" AND "+
	UserLoginInfoTable.table.client.getUnqualifiedColName()+ " = "+"'"+TokenMangler.getSQLEscapedString(userLoginInfo.getClient(), MAX_FIELD_LENGTH)+"'";

}
}
