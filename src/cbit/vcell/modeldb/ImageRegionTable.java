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

import cbit.sql.*;
import cbit.image.*;
import java.sql.*;

import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;

/**
 * This type was created in VisualAge.
 */
public class ImageRegionTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_imageregion";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field regionName 	= new Field("regionName",	"varchar(255)",	"NOT NULL");
	public final Field imageRef 		= new Field("imageRef",		"integer",		"NOT NULL "+ImageTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field pixelValue	= new Field("pixelValue",	"integer",		"NOT NULL");
	
	private final Field fields[] = {regionName,imageRef,pixelValue};
	
	public static final ImageRegionTable table = new ImageRegionTable();
/**
 * ModelTable constructor comment.
 */
private ImageRegionTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key,KeyValue imageKey,VCPixelClass pixelClass) {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key+",");
	buffer.append("'"+pixelClass.getPixelClassName()+"',");
	buffer.append(imageKey+",");
	buffer.append(pixelClass.getPixel()+")");

	return buffer.toString();
}
/**
 * This method was created in VisualAge.
 * @return Model
 * @param rset ResultSet
 * @param log SessionLog
 */
public VCPixelClass getVCPixelClass(ResultSet rset, SessionLog log) throws SQLException {

	KeyValue key = new KeyValue(rset.getBigDecimal(id.toString()));
	String rName = rset.getString(regionName.toString());
	int pixValue = rset.getInt(pixelValue.toString());

	VCPixelClass vcPixelClass = new VCPixelClass(key,rName,pixValue);
	
	return vcPixelClass;
}
}
