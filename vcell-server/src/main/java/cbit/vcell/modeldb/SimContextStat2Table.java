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
import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Structure;
import org.vcell.util.DataAccessException;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.KeyValue;

/**
 * This type was created in VisualAge.
 */
public class SimContextStat2Table extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_simcontextstat2";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field simContextRef		= new Field("simContextRef",		SQLDataType.integer,			"NOT NULL "+SimContextTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field hasData				= new Field("hasData",				SQLDataType.number_as_integer,	"");
	public final Field equiv				= new Field("equiv",				SQLDataType.number_as_integer,	"");
	public final Field status				= new Field("status",				SQLDataType.varchar2_255,		"");
	public final Field curatorEquiv			= new Field("curatorEquiv",			SQLDataType.integer,			"");
	public final Field comments				= new Field("comments",				SQLDataType.varchar2_255,		"");
	public final Field equiv_4_8			= new Field("equiv_4_8",			SQLDataType.number_as_integer,	"");
	public final Field status_4_8			= new Field("status_4_8",			SQLDataType.varchar2_255,		"");

	private final Field fields[] = {simContextRef, hasData, equiv, status, curatorEquiv, comments, equiv_4_8, status_4_8};
	
	public static final SimContextStat2Table table = new SimContextStat2Table();


	private SimContextStat2Table() {
		super(TABLE_NAME);
		addFields(fields);
	}

	public static String getSQLValueList(KeyValue key, KeyValue simContextKey, Boolean hasData, Boolean equiv,
								  String status, Boolean curatorEquiv, String comments,
								  Boolean equiv_4_8, String status_4_8) throws DataAccessException {

		String hasDataStr = hasData==null ? "NULL": (hasData ? "1" : "0");
		String equivStr = equiv==null ? "NULL": (equiv ? "1" : "0");
		String curatorEquivStr = curatorEquiv==null ? "NULL": (curatorEquiv ? "1" : "0");
		String equiv_4_8_Str = equiv_4_8==null ? "NULL": (equiv_4_8 ? "1" : "0");

		StringBuffer buffer = new StringBuffer();
		buffer.append("(");
		buffer.append(key+",");
		buffer.append(simContextKey+",");
		buffer.append(hasDataStr +",");
		buffer.append(equivStr +",");
		buffer.append((status!=null ? ("'"+ TokenMangler.getSQLEscapedString(status, 255)+"'") : "NULL")+",");
		buffer.append(curatorEquivStr+",");
		buffer.append((comments!=null ? ("'"+ TokenMangler.getSQLEscapedString(comments, 255)+"'") : "NULL")+",");
		buffer.append(equiv_4_8_Str+",");  // structRef
		buffer.append((status_4_8!=null ? ("'"+ TokenMangler.getSQLEscapedString(status_4_8, 255)+"'") : "NULL"));
		buffer.append(")");

		return buffer.toString();
	}

}
