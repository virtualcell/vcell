/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modeldb;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.UserLoginInfo;

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.vcell.resource.PropertyLoader;
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

//
	private static final String TABLE_NAME = "vc_userinfo";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field userid		= new Field("USERID",		"varchar(255)",255,		"UNIQUE NOT NULL");
	public final Field password		= new Field("PASSWORD",		"varchar(255)",255,		"NOT NULL");
	public final Field email		= new Field("EMAIL",		"varchar(255)",255,		"NOT NULL");
	public final Field dbWholeName	= new Field("FIRSTNAME",	"varchar(255)",255,		"NOT NULL");
	public final Field lastName		= new Field("LASTNAME",		"varchar(255)",255,		"");
	public final Field title		= new Field("TITLE",		"varchar(255)",255,		"");
	public final Field companyName	= new Field("COMPANYNAME",	"varchar(255)",255,		"");
	public final Field address1		= new Field("ADDRESS1",		"varchar(255)",255,		"");
	public final Field address2		= new Field("ADDRESS2",		"varchar(255)",255,		"");
	public final Field city			= new Field("CITY",			"varchar(255)",255,		"");
	public final Field state		= new Field("STATE",		"varchar(255)",255,		"");
	public final Field country		= new Field("COUNTRY",		"varchar(255)",255,		"");
	public final Field zip			= new Field("ZIP",			"varchar(255)",255,		"");
	public final Field notify		= new Field("NOTIFY",		"varchar(255)",255,		"NOT NULL");
	public final Field insertDate	= new Field("insertDate",	"date",				"NOT NULL");
	public final Field digestPW		= new Field("DIGESTPW",		"varchar(255)",255,		"NOT NULL");

	private final Field fields[] = {userid, password, email, dbWholeName, lastName, title, companyName, 
											address1, address2, city, state, country, zip, notify, insertDate,digestPW };
	
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
	long password = System.currentTimeMillis();
	String sql = "INSERT INTO "+UserTable.table.getTableName()+
			" VALUES ( "+
			UserTable.VOID_ID+","+
			"'"+UserTable.VOID_USERID+"'"+","+
			"'"+password+"'"+","+
			"'empty',"+"'empty',"+"'empty',"+"'empty',"+"'empty',"+
			"'empty',"+"'empty',"+"'empty',"+"'empty',"+"'empty',"+"'empty',"+"'empty',"+
			"SYSDATE,"+"'"+(new UserLoginInfo.DigestedPassword(""+password)).getString()+"'"+
			" )";
	return sql;
}
public final static String getCreateAdministratorUserSQL() {
	long password = System.currentTimeMillis();
	String sql = "INSERT INTO "+UserTable.table.getTableName()+
			" VALUES ( "+
			PropertyLoader.ADMINISTRATOR_ID+","+
			"'"+PropertyLoader.ADMINISTRATOR_ACCOUNT+"'"+","+
			"'"+password+"'"+","+
			"'empty',"+"'empty',"+"'empty',"+"'empty',"+"'empty',"+
			"'empty',"+"'empty',"+"'empty',"+"'empty',"+"'empty',"+"'empty',"+"'empty',"+
			"SYSDATE,"+"'"+(new UserLoginInfo.DigestedPassword(""+password)).getString()+"'"+
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
	buffer.append(digestPW+"='"+userInfo.digestedPassword0.getString()+"',");
	buffer.append(email+"='"+userInfo.email+"',");
	buffer.append(dbWholeName+"='"+userInfo.wholeName+"',");
	buffer.append(lastName+"=NULL"+",");
	buffer.append(title+"="+(userInfo.title==null?"NULL":"'"+userInfo.title+"'")+",");
	buffer.append(companyName+"="+(userInfo.company==null?"NULL":"'"+userInfo.company+"'")+",");
	buffer.append(address1+"=NULL"+",");
	buffer.append(address2+"=NULL"+",");
	buffer.append(city+"=NULL"+",");
	buffer.append(state+"=NULL"+",");
	buffer.append(country+"='"+userInfo.country+"',");
	buffer.append(zip+"=NULL"+",");
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
	buffer.append("'"+userInfo.digestedPassword0.getString()+"',");//need this for now
	buffer.append("'"+userInfo.email+"',");
	buffer.append("'"+userInfo.wholeName+"',");
	buffer.append("NULL,");
	buffer.append((userInfo.title==null?"NULL":"'"+userInfo.title+"'")+",");
	buffer.append((userInfo.company==null?"NULL":"'"+userInfo.company+"'")+",");
	buffer.append("NULL,");
	buffer.append("NULL,");
	buffer.append("NULL,");
	buffer.append("NULL,");
	buffer.append("'"+userInfo.country+"',");
	buffer.append("NULL,");
	buffer.append("'"+(userInfo.notify?UserTable.NOTIFY_TRUE:UserTable.NOTIFY_FALSE)+"',");
	buffer.append(VersionTable.formatDateToOracle(new java.util.Date())+",");
	buffer.append("'"+userInfo.digestedPassword0.getString()+"'");
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
	userInfo.digestedPassword0 = UserLoginInfo.DigestedPassword.createAlreadyDigested((rset.getString(digestPW.toString())));
	userInfo.email =	rset.getString(email.toString());
	userInfo.wholeName =rset.getString(dbWholeName.toString());
	userInfo.title =	rset.getString(title.toString());
	if(rset.wasNull()){userInfo.title = null;}
	userInfo.company =	rset.getString(companyName.toString());
	if(rset.wasNull()){userInfo.company = null;}
	userInfo.country =	rset.getString(country.toString());
	String notifyS =	rset.getString(notify.toString());
	if(rset.wasNull()){
		userInfo.notify = false;
	}else{
		userInfo.notify = notifyS.equals(UserTable.NOTIFY_TRUE);
	}

	//
	// Format Date
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
