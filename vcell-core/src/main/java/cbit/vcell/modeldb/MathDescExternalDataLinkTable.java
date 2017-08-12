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

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;

public class MathDescExternalDataLinkTable extends Table {
	private static final String TABLE_NAME = "vc_mathdescextdata";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    private static final String[] mathDescAndExtDataConstraintOracle =
		new String[] {"md_ed_unq UNIQUE (MATHDESCREF, EXTDATAREF)"};

    private static final String[] mathDescAndExtDataConstraintPostgres =
		new String[] {"md_ed_unq UNIQUE (MATHDESCREF, EXTDATAREF)"};

	public final Field mathDescRef		= new Field("mathDescRef",	SQLDataType.integer,	"NOT NULL "+MathDescTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field extDataRef 		= new Field("extDataRef",	SQLDataType.integer,	"NOT NULL "+ExternalDataTable.REF_TYPE);

	private final Field fields[] = {mathDescRef,extDataRef};
	
	public static final MathDescExternalDataLinkTable table = new MathDescExternalDataLinkTable();
/**
 * ModelTable constructor comment.
 */
private MathDescExternalDataLinkTable() {
	super(TABLE_NAME,mathDescAndExtDataConstraintOracle,mathDescAndExtDataConstraintPostgres);
	addFields(fields);
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/00 8:08:50 AM)
 * @return cbit.sql.KeyValue
 * @param rset java.sql.ResultSet
 */
public KeyValue getExternalDataKey(ResultSet rset) throws SQLException {
	
	KeyValue key = new KeyValue(rset.getBigDecimal(table.extDataRef.getUnqualifiedColName()));
	if (rset.wasNull()){
		key = null;
	}
	return key;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key, KeyValue simKey, KeyValue extDataKey) {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key+",");
	buffer.append(simKey+",");
	buffer.append(extDataKey+")");

	return buffer.toString();
}

}
