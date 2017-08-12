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

import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
import cbit.vcell.dictionary.db.DBSpeciesTable;
import cbit.vcell.model.DBSpecies;
import cbit.vcell.model.Species;
/**
 * This type was created in VisualAge.
 */
public class SpeciesTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_species";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field commonName		= new Field("commonName",	SQLDataType.varchar_255,	"NOT NULL");
	public final Field dbSpeciesRef		= new Field("dbSpeciesRef",	SQLDataType.integer,		DBSpeciesTable.REF_TYPE);
	public final Field annotation		= new Field("annotation",	SQLDataType.varchar_1024,	"");
	
	private final Field fields[] = {commonName,dbSpeciesRef,annotation};
	
	public static final SpeciesTable table = new SpeciesTable();
/**
 * ModelTable constructor comment.
 */
private SpeciesTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.ReactionParticipant
 * @param rset java.sql.ResultSet
 */
public Species getSpecies(java.sql.ResultSet rset, SessionLog log,DBSpecies dbSpecies) throws java.sql.SQLException, DataAccessException {
	
	String annotation = rset.getString(SpeciesTable.table.annotation.toString());
	if (annotation!=null){
		annotation = org.vcell.util.TokenMangler.getSQLRestoredString(annotation);
	}
	String cNameStr = TokenMangler.getSQLRestoredString(rset.getString(SpeciesTable.table.commonName.toString()));
	Species species = new Species(cNameStr,annotation,dbSpecies);
	return species;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key, KeyValue ownerKey, Species species) throws DataAccessException {

//	int defaultCharge = 0;

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key+",");
	buffer.append("'"+TokenMangler.getSQLEscapedString(species.getCommonName())+"'"+",");
	KeyValue dbSpeciesKey = (species.getDBSpecies() == null ? null :species.getDBSpecies().getDBSpeciesKey());
	buffer.append((dbSpeciesKey != null?dbSpeciesKey.toString():"null")+",");
	buffer.append((species.getAnnotation() != null ? "'"+org.vcell.util.TokenMangler.getSQLEscapedString(species.getAnnotation())+"'" : "null")+")");
	
	return buffer.toString();
}
}
