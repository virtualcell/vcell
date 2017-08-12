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

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
import cbit.vcell.model.DBFormalSpecies;
import cbit.vcell.model.FormalSpeciesType;

/**
 * Insert the type's description here.
 * Creation date: (2/6/2003 3:57:02 PM)
 * @author: Frank Morgan
 */
public class DBSpeciesTable extends cbit.sql.Table {
	
    private static final String TABLE_NAME = "vc_dbspecies";
    public static final String REF_TYPE =
        "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    private static final String[] dbSpeciesTableConstraintOracle =
    			new String[] {
    			"com_enz_pro_unique UNIQUE(compoundRef,enzymeRef,proteinRef)",
    			"com_enz_pro_only_1 CHECK(DECODE(compoundref,NULL,0,compoundref,1)+DECODE(enzymeref,NULL,0,enzymeref,1)+DECODE(proteinref,NULL,0,proteinref,1) = 1)"};

    private static final String[] dbSpeciesTableConstraintPostgres =
				new String[] {
				"com_enz_pro_unique UNIQUE(compoundRef,enzymeRef,proteinRef)",
				"com_enz_pro_only_1 CHECK((CASE WHEN compoundref IS NULL THEN 0 ELSE 1 END)+(CASE WHEN enzymeref IS NULL THEN 0 ELSE 1 END)+(CASE WHEN proteinref IS NULL THEN 0 ELSE 1 END) = 1)"};
    
    public final Field compoundRef 	= new Field("compoundRef", 	SQLDataType.integer, CompoundTable.REF_TYPE);
	public final Field enzymeRef 	= new Field("enzymeRef", 	SQLDataType.integer, EnzymeTable.REF_TYPE);
	public final Field proteinRef 	= new Field("proteinRef", 	SQLDataType.integer, ProteinTable.REF_TYPE);
	
	private final Field fields[] = {compoundRef,enzymeRef,proteinRef};

    public static final DBSpeciesTable table = new DBSpeciesTable();

    			
/**
 * DBSpeciesTable constructor comment.
 * @param argTableName java.lang.String
 */
public DBSpeciesTable() {
	super(TABLE_NAME,dbSpeciesTableConstraintOracle,dbSpeciesTableConstraintPostgres);
	addFields(fields);
}
/**
 * Returns an SQL String with a value list taken from the parameter Compound
 * @return java.lang.String
 * @param key KeyValue
 * @param compound Compound
 */
public String getSQLValueList(org.vcell.util.document.KeyValue key, DBFormalSpecies dbfs) {

    StringBuffer buffer = new StringBuffer();
    buffer.append("(");
    buffer.append(key + ",");
	buffer.append((dbfs.getFormalSpeciesType().equals(FormalSpeciesType.compound)?dbfs.getDBFormalSpeciesKey().toString():"null") + ",");
	buffer.append((dbfs.getFormalSpeciesType().equals(FormalSpeciesType.enzyme)?dbfs.getDBFormalSpeciesKey().toString():"null") + ",");
	buffer.append((dbfs.getFormalSpeciesType().equals(FormalSpeciesType.protein)?dbfs.getDBFormalSpeciesKey().toString():"null"));
	buffer.append(")");

    return buffer.toString();
}
}
