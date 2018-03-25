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
import java.util.Date;

import org.apache.log4j.Logger;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
import cbit.vcell.modeldb.ApiAccessToken.AccessTokenStatus;
/**
 * This type was created in VisualAge.
 */
public class ApiAccessTokenTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_apiaccesstoken";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field accesstoken		= new Field("accesstoken",	SQLDataType.varchar_255,	"NOT NULL");
	public final Field clientRef 		= new Field("clientRef",	SQLDataType.integer,		"NOT NULL "+ApiClientTable.REF_TYPE);
	public final Field userref			= new Field("userRef",		SQLDataType.integer,		"NOT NULL "+UserTable.REF_TYPE);
	public final Field creationDate		= new Field("creationDate",	SQLDataType.date,			"NOT NULL");
	public final Field expireDate		= new Field("expireDate",	SQLDataType.date,			"NOT NULL");
	public final Field status			= new Field("status",		SQLDataType.varchar_20,		"");  // does not cover the case of an expired token

	private final Field fields[] = {accesstoken,clientRef,userref,creationDate,expireDate,status};
	
	public static final ApiAccessTokenTable table = new ApiAccessTokenTable();
/**
 * ModelTable constructor comment.
 */
private ApiAccessTokenTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return Model
 * @param rset ResultSet
 * @param log SessionLog
 */
public ApiAccessToken getApiAccessToken(ResultSet rset) throws SQLException, DataAccessException {

	KeyValue key = new KeyValue(rset.getBigDecimal(id.toString()));
	String token = rset.getString(accesstoken.toString());
	KeyValue clientKey = new KeyValue(rset.getBigDecimal(clientRef.toString()));
	KeyValue userKey = new KeyValue(rset.getBigDecimal(userref.toString()));
	String userid = rset.getString(UserTable.table.userid.toString());
	User user = new User(userid,userKey);
	java.util.Date creation = VersionTable.getDate(rset,creationDate.toString());
	if (creation==null){
		throw new DataAccessException("could not parse creation date");
	}
	java.util.Date expiration = VersionTable.getDate(rset,expireDate.toString());
	if (expiration==null){
		throw new DataAccessException("could not parse expiration date");
	}
	String accessTokenDatabaseString = rset.getString(status.toString());
	AccessTokenStatus accessTokenStatus = AccessTokenStatus.created; // default value if null
	if (accessTokenDatabaseString!=null){
		accessTokenStatus = AccessTokenStatus.fromDatabaseString(accessTokenDatabaseString);
	}
	ApiAccessToken apiAccessToken = new ApiAccessToken(key,token,clientKey,user,creation,expiration,accessTokenStatus);
	
	return apiAccessToken;
}

public String getSQLValueList(KeyValue key, String token, KeyValue apiClientKey, User user, Date creationDate, Date expirationDate, AccessTokenStatus accessTokenStatus) {
	
	StringBuffer buffer = new StringBuffer();
	
	buffer.append("(");
	buffer.append(key+",");
	buffer.append("'"+token+"'" + ",");
	buffer.append(apiClientKey +",");
	buffer.append(user.getID()+",");
	buffer.append(VersionTable.formatDateToOracle(creationDate)+",");
	buffer.append(VersionTable.formatDateToOracle(expirationDate)+",");
	buffer.append("'"+accessTokenStatus.getDatabaseString()+"'");
	buffer.append(")");

	return buffer.toString();
}
}
