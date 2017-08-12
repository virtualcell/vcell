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

import java.util.Vector;

import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
import cbit.vcell.dictionary.BoundCompound;
import cbit.vcell.dictionary.CompoundInfo;
import cbit.vcell.dictionary.FormalCompound;
import cbit.vcell.model.DBFormalSpecies;

/**
 * Represents a table for storing compound information in a database
 * Creation date: (6/25/2002 3:36:45 PM)
 * @author: Steven Woolley
 */
public class CompoundTable extends Table {
    private static final String TABLE_NAME = "vc_compound";
    public static final String REF_TYPE =
        "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    public final Field formula = 	new Field("formula", 	SQLDataType.varchar2_256, "");
    public final Field casID = 		new Field("casId", 		SQLDataType.varchar2_256, "");
    public final Field keggID = 	new Field("keggId", 	SQLDataType.varchar2_32, "UNIQUE NOT NULL");

    private final Field fields[] = { formula, casID, keggID };

    public static final CompoundTable table = new CompoundTable();
/**
 * Creates a new CompoundTable object with the defined table values and fields
 * Creation date: (6/25/2002 3:53:09 PM)
 */
public CompoundTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * returns a Compound object from the ResultSet
 * @return cbit.vcell.dictionary.Compound
 * @param rset java.sql.ResultSet
 */
public DBFormalSpecies[] getCompounds(java.sql.ResultSet rset, SessionLog log,boolean createBound) throws java.sql.SQLException {

	Vector compounds = new Vector();
	Vector aliasNames = new Vector();

	String currentAliasName = null;
	String currentCASID = null;
	String currentFormula = null;
	String currentKeggID = null;
	String currentPreferred = null;
	org.vcell.util.document.KeyValue currentCompoundID = null;
	org.vcell.util.document.KeyValue currentDBSpeciesID = null;
	
	while(rset.next() || rset.isAfterLast()){
			
		KeyValue compoundID = null;
		if(!rset.isAfterLast()){
			compoundID = new KeyValue(rset.getBigDecimal(CompoundTable.table.id.toString()));
		}
		
		if(!rset.isFirst() && (!currentCompoundID.equals(compoundID))){
			if(currentCompoundID != null){
				if(aliasNames.size() > 0){
					String[] aliasNamesArr = new String[aliasNames.size()];
					aliasNames.copyInto(aliasNamesArr);
					CompoundInfo compoundInfo = new CompoundInfo(currentKeggID,aliasNamesArr,currentFormula,currentCASID,null);
					FormalCompound formalCompound = new FormalCompound(currentCompoundID,compoundInfo);
					Object compound = formalCompound;
					if(createBound){
						BoundCompound boundCompound = new BoundCompound(currentDBSpeciesID,formalCompound);
						compound = boundCompound;
					}
					compounds.add(compound);
				}
			}
			aliasNames.clear();
			if(rset.isAfterLast()){
				break;
			}
		}

		if(aliasNames.size() == 0){
			currentCompoundID = compoundID;
			currentKeggID = 	rset.getString(CompoundTable.table.keggID.toString());
			currentCASID = 		rset.getString(CompoundTable.table.casID.toString());
			currentFormula = 	rset.getString(CompoundTable.table.formula.toString());
				currentFormula = 	(currentFormula != null ?org.vcell.util.TokenMangler.getSQLRestoredString(currentFormula):null);
			if(createBound){
				currentDBSpeciesID =	new KeyValue(rset.getBigDecimal("dbspecies_id"));
			}
		}
		currentPreferred = 	rset.getString(CompoundAliasTable.table.preferred.toString());
		currentAliasName = 	org.vcell.util.TokenMangler.getSQLRestoredString(rset.getString(CompoundAliasTable.table.name.toString()));
		if(currentPreferred.compareToIgnoreCase("T") == 0){
			aliasNames.add(0,currentAliasName);
		}else{
			aliasNames.add(currentAliasName);
		}
	}

	DBFormalSpecies[] compoundsArr = null;
	if(compounds.size() > 0){
		if(createBound){
			BoundCompound[] boundCompoundsArr = null;
			boundCompoundsArr = new BoundCompound[compounds.size()];
			compounds.copyInto(boundCompoundsArr);
			compoundsArr = boundCompoundsArr;
		}else{
			FormalCompound[] formalCompoundsArr = null;
			formalCompoundsArr = new FormalCompound[compounds.size()];
			compounds.copyInto(formalCompoundsArr);
			compoundsArr = formalCompoundsArr;
		}
	}
	return compoundsArr;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param compound Compound
 */
public String getSQLUpdateList(CompoundInfo compound) {
    StringBuffer buffer = new StringBuffer();

    buffer.append(formula + "='" + compound.getFormula() + "',");
    if (compound.getCasID() != null) {
        buffer.append(casID + "='" + compound.getCasID() + "',");
    } // end if
    else {
        // insert a blank if there is no CAS
        buffer.append(casID + "= '',");
    }
    buffer.append(keggID + "='" + compound.getFormalID() + "'");
    return buffer.toString();
}
/**
 * Returns an SQL String with a value list taken from the parameter Compound
 * @return java.lang.String
 * @param key KeyValue
 * @param compound Compound
 */
public String getSQLValueList(KeyValue key, CompoundInfo compound) {

    StringBuffer buffer = new StringBuffer();
    buffer.append("(");
    buffer.append(key + ",");

    //
    // insert formula
    //
    if (compound.getFormula() == null) {
        buffer.append(null +",");
    } else {
        buffer.append("'" + compound.getFormula() + "',");
    }
    //
    // insert CAS id
    //
    //String casID = null;
    //DBLink dbEntries[] = compound.getDBLinks();
    //for (int i = 0; i < dbEntries.length; i++) {
    //if (dbEntries[i].getDBname().equals(DBLink.DBNAME_CAS)) {
    //casID = dbEntries[i].getID();
    //}
    //}
    if (compound.getCasID() == null) {
        buffer.append(null +",");
    } else {
        buffer.append("'" + compound.getCasID() + "',");
    }
    //
    // insert KEGG id
    //
    String keggID = null;
    keggID = compound.getFormalID();

    if (keggID == null) {
        buffer.append(null +")");
    } else {
        buffer.append("'" + keggID + "')");
    }

    return buffer.toString();
}
}
