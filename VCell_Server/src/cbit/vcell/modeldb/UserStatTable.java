package cbit.vcell.modeldb;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.sql.Field;
import cbit.sql.Table;
/**
 * This type was created in VisualAge.
 */
public class UserStatTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_userstat";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field userRef				= new Field("userRef",				"integer",		"NOT NULL "+UserTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field loginCount			= new Field("loginCount",			"number",		"");
	public final Field lastLogin			= new Field("lastLogin",			"date",			"");
	public final Field emailLostPasswordOK	= new Field("emailLostPasswordOK",	"varchar2(5)",	"");
	public final Field wantsOnlineCallback	= new Field("wantsOnlineCallback",	"varchar2(5)",	"");
	public final Field userAgent			= new Field("userAgent",			"varchar2(255)","");
	public final Field useMac				= new Field("useMac",				"number",		"");
	public final Field useWin				= new Field("useWin",				"number",		"");
	public final Field useLin				= new Field("useLin",				"number",		"");

	private final Field fields[] = {userRef,loginCount,
									lastLogin,
									emailLostPasswordOK,wantsOnlineCallback,userAgent,
									useMac,useWin,useLin};
	
	public static final UserStatTable table = new UserStatTable();
/**
 * ModelTable constructor comment.
 */
private UserStatTable() {
	super(TABLE_NAME);
	addFields(fields);
}
}
