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

import org.vcell.util.document.KeyValue;

import cbit.sql.*;
/**
 * This type was created in VisualAge.
 */
public class UserStatTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_userstat";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field userRef				= new Field("userRef",				"integer",		"UNIQUE NOT NULL "+UserTable.REF_TYPE+" ON DELETE CASCADE");
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

public String getSQLValueList(KeyValue key) {
	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(Table.NewSEQ+",");
	buffer.append(key.toString()+",");
	buffer.append("0,");
	buffer.append("NULL,");
	buffer.append("'true',");
	buffer.append("'false',");
	buffer.append("NULL,");
	buffer.append("0,");
	buffer.append("0,");
	buffer.append("0");
	buffer.append(")");
	return buffer.toString();
}

}
