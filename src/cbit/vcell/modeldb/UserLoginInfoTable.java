package cbit.vcell.modeldb;

import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.vcell.server.UserLoginInfo;

public class UserLoginInfoTable extends Table {
	private static final String TABLE_NAME = "vc_userlogininfo";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    private static final String[] userLoginInfoTableUniqueConstraint =
		new String[] {
		"ulinfo_unique UNIQUE(userRef,osarch,osname,osvers)"};

	public final Field userRef		= new Field("userRef",		"integer",		"NOT NULL "+UserTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field loginCount	= new Field("loginCount",	"number",		"NOT NULL");
	public final Field osarch		= new Field("osarch",		"varchar2(32)","NOT NULL");
	public final Field osname		= new Field("osname",		"varchar2(32)","NOT NULL");
	public final Field osvers		= new Field("osvers",		"varchar2(32)","NOT NULL");

	private final Field fields[] = {userRef,loginCount,osarch,osname,osvers};
	
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
	buffer.append("'"+userLoginInfo.getOs_arch()+"'"+",");
	buffer.append("'"+userLoginInfo.getOs_name()+"'"+",");
	buffer.append("'"+userLoginInfo.getOs_version()+"'");
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
	UserLoginInfoTable.table.osarch.getUnqualifiedColName()+ " = "+"'"+userLoginInfo.getOs_arch()+"'"+
	" AND "+
	UserLoginInfoTable.table.osname.getUnqualifiedColName()+ " = "+"'"+userLoginInfo.getOs_name()+"'"+
	" AND "+
	UserLoginInfoTable.table.osvers.getUnqualifiedColName()+ " = "+"'"+userLoginInfo.getOs_version()+"'";

}
}
