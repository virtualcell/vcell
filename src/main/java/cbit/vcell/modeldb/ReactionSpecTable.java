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

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
import cbit.vcell.mapping.ReactionSpec;
/**
 * This type was created in VisualAge.
 */
public class ReactionSpecTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_reactionspec";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field reactionStepRef	= new Field("reactStepRef", SQLDataType.integer,	"NOT NULL "+ReactStepTable.REF_TYPE);
	public final Field simContextRef	= new Field("simContextRef",SQLDataType.integer,	"NOT NULL "+SimContextTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field mapping			= new Field("mapping",		SQLDataType.integer,	"NOT NULL");
	
	private final Field fields[] = {reactionStepRef,simContextRef,mapping};
	
	public static final ReactionSpecTable table = new ReactionSpecTable();
/**
 * ModelTable constructor comment.
 */
private ReactionSpecTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue Key, KeyValue simContextKey, ReactionSpec reactionSpec, KeyValue reactionStepKey) {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(Key + ",");
	buffer.append(reactionStepKey + ",");
	buffer.append(simContextKey + ",");

	buffer.append(reactionSpec.getReactionMapping() + ")");

	return buffer.toString();
}
}
