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
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Table;

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
