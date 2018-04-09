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

import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
/**
 * This type was created in VisualAge.
 */
public class ApiClientTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_apiclient";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field clientName		= new Field("clientname",	SQLDataType.varchar_255,	"NOT NULL");
	public final Field clientId			= new Field("clientId",		SQLDataType.varchar_255,	"NOT NULL");
	public final Field clientSecret		= new Field("clientSecret",	SQLDataType.varchar_255,	"NOT NULL");

	private final Field fields[] = {clientName,clientId,clientSecret};
	
	public static final ApiClientTable table = new ApiClientTable();
/**
 * ModelTable constructor comment.
 */
private ApiClientTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return Model
 * @param rset ResultSet
 * @param log SessionLog
 */
public ApiClient getApiClient(ResultSet rset) throws SQLException, DataAccessException {

	KeyValue key = new KeyValue(rset.getBigDecimal(id.toString()));

	String name = rset.getString(clientName.toString());
	String id = rset.getString(clientId.toString());
	String secret = rset.getString(clientSecret.toString());
	
	ApiClient apiClient = new ApiClient(key,name,id,secret);
		
	return apiClient;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key, ApiClient apiClient) {
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key+",");
	buffer.append("'"+apiClient.getClientName()   + "',");
	buffer.append("'"+apiClient.getClientId()     + "',");
	buffer.append("'"+apiClient.getClientSecret() + "'");	
	buffer.append(")");

	return buffer.toString();
}
}
