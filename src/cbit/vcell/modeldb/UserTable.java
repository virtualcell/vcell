package cbit.vcell.modeldb;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.sql.*;

import org.vcell.util.TokenMangler;
import org.vcell.util.document.KeyValue;

import cbit.sql.*;
/**
 * This type was created in VisualAge.
 */
public class UserTable extends cbit.sql.Table {
	//
	public static final String NOTIFY_TRUE = "on";
	public static final String NOTIFY_FALSE = "off";
	//
	public static final int 	VOID_ID = 0;
	public static final String 	VOID_USERID = "void";
	public static final String	VOID_PASSWORD = "vcellvoidpasssword";
	//
	private static final String TABLE_NAME = "vc_userinfo";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field userid		= new Field("USERID",		"varchar(255)",255,		"UNIQUE NOT NULL");
	public final Field password		= new Field("PASSWORD",		"varchar(255)",255,		"NOT NULL");
	public final Field email		= new Field("EMAIL",		"varchar(255)",255,		"NOT NULL");
	public final Field firstName	= new Field("FIRSTNAME",	"varchar(255)",255,		"NOT NULL");
	public final Field lastName		= new Field("LASTNAME",		"varchar(255)",255,		"NOT NULL");
	public final Field title		= new Field("TITLE",		"varchar(255)",255,		"NOT NULL");
	public final Field companyName	= new Field("COMPANYNAME",	"varchar(255)",255,		"NOT NULL");
	public final Field address1		= new Field("ADDRESS1",		"varchar(255)",255,		"NOT NULL");
	public final Field address2		= new Field("ADDRESS2",		"varchar(255)",255,		"");
	public final Field city			= new Field("CITY",			"varchar(255)",255,		"NOT NULL");
	public final Field state		= new Field("STATE",		"varchar(255)",255,		"NOT NULL");
	public final Field country		= new Field("COUNTRY",		"varchar(255)",255,		"NOT NULL");
	public final Field zip			= new Field("ZIP",			"varchar(255)",255,		"NOT NULL");
	public final Field notify		= new Field("NOTIFY",		"varchar(255)",255,		"NOT NULL");
	public final Field insertDate	= new Field("insertDate",	"date",				"NOT NULL");

	private final Field fields[] = {userid, password, email, firstName, lastName, title, companyName, 
											address1, address2, city, state, country, zip, notify, insertDate };
	
	public static final UserTable table = new UserTable();
/**
 * ModelTable constructor comment.
 */
private UserTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * Insert the method's description here.
 * Creation date: (1/8/2002 6:14:06 PM)
 * @return java.lang.String
 */
public final static String getCreateVoidUserSQL() {
	String sql = "INSERT INTO "+UserTable.table.getTableName()+
			" VALUES ( "+
			UserTable.VOID_ID+","+
			"'"+UserTable.VOID_USERID+"'"+","+
			"'"+UserTable.VOID_PASSWORD+"'"+","+
			"'empty',"+"'empty',"+"'empty',"+"'empty',"+"'empty',"+
			"'empty',"+"'empty',"+"'empty',"+"'empty',"+"'empty',"+"'empty',"+"'empty',"+
			"SYSDATE"+
			" )";
	return sql;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLUpdateList(UserInfo userInfo) {
	StringBuffer buffer = new StringBuffer();
	buffer.append(password+"='"+userInfo.password+"',");
	buffer.append(email+"='"+userInfo.email+"',");
	buffer.append(firstName+"='"+userInfo.firstName+"',");
	buffer.append(lastName+"='"+userInfo.lastName+"',");
	buffer.append(title+"='"+userInfo.title+"',");
	buffer.append(companyName+"='"+userInfo.company+"',");
	buffer.append(address1+"='"+userInfo.address1+"',");
	buffer.append(address2+"="+(userInfo.address2 == null?"NULL":"'"+userInfo.address2+"'")+",");
	buffer.append(city+"='"+userInfo.city+"',");
	buffer.append(state+"='"+userInfo.state+"',");
	buffer.append(country+"='"+userInfo.country+"',");
	buffer.append(zip+"='"+userInfo.zip+"',");
	buffer.append(notify+"='"+(userInfo.notify?UserTable.NOTIFY_TRUE:UserTable.NOTIFY_FALSE)+"'");
	return buffer.toString();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key, UserInfo userInfo) {
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key.toString()+",");
	buffer.append("'"+userInfo.userid+"',");
	buffer.append("'"+userInfo.password+"',");
	buffer.append("'"+userInfo.email+"',");
	buffer.append("'"+userInfo.firstName+"',");
	buffer.append("'"+userInfo.lastName+"',");
	buffer.append("'"+userInfo.title+"',");
	buffer.append("'"+userInfo.company+"',");
	buffer.append("'"+userInfo.address1+"',");
	buffer.append((userInfo.address2 == null?"NULL":"'"+userInfo.address2+"'")+",");
	buffer.append("'"+userInfo.city+"',");
	buffer.append("'"+userInfo.state+"',");
	buffer.append("'"+userInfo.country+"',");
	buffer.append("'"+userInfo.zip+"',");
	buffer.append("'"+(userInfo.notify?UserTable.NOTIFY_TRUE:UserTable.NOTIFY_FALSE)+"',");
	buffer.append(VersionTable.formatDateToOracle(new java.util.Date()));
	buffer.append(")");
	return buffer.toString();
}
/**
 * This method was created in VisualAge.
 * @return UserInfo
 * @param resultSet java.sql.ResultSet
 */
public UserInfo getUserInfo(ResultSet rset) throws SQLException {

	UserInfo userInfo = new UserInfo();

	userInfo.id = 		new KeyValue(rset.getBigDecimal(id.toString()));
	userInfo.userid =	rset.getString(userid.toString());
	userInfo.password =	rset.getString(password.toString());
	userInfo.email =	rset.getString(email.toString());
	userInfo.firstName =rset.getString(firstName.toString());
	userInfo.lastName =	rset.getString(lastName.toString());
	userInfo.title =	rset.getString(title.toString());
	userInfo.company =	rset.getString(companyName.toString());
	userInfo.address1 =	rset.getString(address1.toString());
	userInfo.address2 =	rset.getString(address2.toString());
	if(rset.wasNull()){
		userInfo.address2 = null;
	}
	userInfo.city =		rset.getString(city.toString());
	userInfo.state =	rset.getString(state.toString());
	userInfo.country =	rset.getString(country.toString());
	userInfo.zip =		rset.getString(zip.toString());
	String notifyS =	rset.getString(notify.toString());
	if(rset.wasNull()){
		userInfo.notify = false;
	}else{
		userInfo.notify = notifyS.equals(UserTable.NOTIFY_TRUE);
	}

	//
	// Do this stuff because ResultSet only returns java.sql.Date without time information
	//
	java.sql.Date DBDate = rset.getDate(insertDate.toString());
	java.sql.Time DBTime = rset.getTime(insertDate.toString());
	try {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US);
		userInfo.insertDate = sdf.parse(DBDate + " " + DBTime);
	} catch (java.text.ParseException e) {
		throw new java.sql.SQLException(e.getMessage());
	}

	return userInfo;
}
}
