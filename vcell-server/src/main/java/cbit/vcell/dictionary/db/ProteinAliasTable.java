/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.dictionary.db;

import org.vcell.util.document.KeyValue;

//import cbit.vcell.server.DataAccessException;
import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;

/**
 * Represents a table for storing Protein names and aliases in a db table
 * Creation date: (6/25/2002 3:36:45 PM)
 * @author: Steven Woolley
 */
public class ProteinAliasTable extends Table {
    private static final String TABLE_NAME = "vc_proteinalias";
    public static final String REF_TYPE =
        "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    public final Field proteinRef = new Field("proteinRef", SQLDataType.number_as_integer, 	ProteinTable.REF_TYPE+" ON DELETE CASCADE NOT NULL");
    public final Field name = 		new Field("name", 		SQLDataType.varchar2_256, 		"NOT NULL");
    public final Field preferred = 	new Field("preferred", 	SQLDataType.varchar2_1, 		"NOT NULL");//'T'rue or 'F'alse

    private final Field fields[] = { proteinRef, name, preferred};

    public static final ProteinAliasTable table = new ProteinAliasTable();
    /**
     * Creates a new ProteinAliasTable object with the defined table values and fields
     * Creation date: (6/25/2002 3:53:09 PM)
     */
    public ProteinAliasTable() {
        super(TABLE_NAME);
        addFields(fields);
    }
    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     * @param 
     */
    public String getSQLUpdateList(
	    KeyValue newKey,
        KeyValue argProteinRef,
        String argName,
        boolean bPreferred) {
        StringBuffer buffer = new StringBuffer();
		buffer.append(id + "=" + newKey + "'");
        buffer.append(proteinRef + "=" + argProteinRef + ",");
        buffer.append(name + "='" + org.vcell.util.TokenMangler.getSQLEscapedString(argName) + "',");
        buffer.append(preferred + "='" + (bPreferred?"T":"F") + "'");
        return buffer.toString();
    }
/**
 * Returns an SQL String with a value list taken from the parameters
 * @return java.lang.String
 * @param key KeyValue
 * 
 */
public String getSQLValueList(
    KeyValue newKey,
    KeyValue argProteinRef,
    String argName,
    boolean bPreferred) {

    //	int defaultCharge = 0;

    StringBuffer buffer = new StringBuffer();
    buffer.append("(");
    buffer.append(newKey + ",");
    buffer.append(argProteinRef + ",");
    buffer.append("'" + org.vcell.util.TokenMangler.getSQLEscapedString(argName) + "',");
    buffer.append("'" + (bPreferred?"T":"F") + "'");
    buffer.append(")");
    return buffer.toString();
}
}
