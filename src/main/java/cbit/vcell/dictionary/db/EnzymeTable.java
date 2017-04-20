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
import cbit.sql.Table;
import cbit.vcell.dictionary.BoundEnzyme;
import cbit.vcell.dictionary.EnzymeInfo;
import cbit.vcell.dictionary.FormalEnzyme;
import cbit.vcell.model.DBFormalSpecies;

/**
 * Represents a table for storing Enzyme information in a database
 * Creation date: (7/3/2002 3:36:45 PM)
 * @author: Steven Woolley
 */
public class EnzymeTable extends Table {
    private static final String TABLE_NAME = "vc_enzyme";
    public static final String REF_TYPE =
        "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    public final Field reaction = 	new Field("reaction", 	"varchar2(512)", "");
    public final Field ecNumber = 	new Field("ecnumber", 	"varchar2(32)", "UNIQUE NOT NULL");
    public final Field sysname = 	new Field("sysname", 	"varchar2(512)", "");
    public final Field casID = 		new Field("casid", 		"varchar2(256)", "");
    
    private final Field fields[] = { reaction, ecNumber, sysname, casID };

    public static final EnzymeTable table = new EnzymeTable();
/**
 * Creates a new CompoundTable object with the defined table values and fields
 * Creation date: (6/25/2002 3:53:09 PM)
 */
public EnzymeTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * returns a enzyme object from the ResultSet
 * @return cbit.vcell.dictionary.enzyme
 * @param rset java.sql.ResultSet
 */
public DBFormalSpecies[] getEnzymes(java.sql.ResultSet rset, SessionLog log,boolean createBound) throws java.sql.SQLException {

	Vector enzymes = new Vector();
	Vector aliasNames = new Vector();

	String currentAliasName = null;
	String currentECNumber = null;
	String currentSysname = null;
	String currentReaction = null;
	String currentPreferred = null;
	String currentCasID = null;
	
	org.vcell.util.document.KeyValue currentEnzymeID = null;
	org.vcell.util.document.KeyValue currentDBSpeciesID = null;
	
	while(rset.next() || rset.isAfterLast()){
			
		KeyValue enzymeID = null;
		if(!rset.isAfterLast()){
			enzymeID = new KeyValue(rset.getBigDecimal(EnzymeTable.table.id.toString()));
		}
		
		if(!rset.isFirst() && (!currentEnzymeID.equals(enzymeID))){
			if(currentEnzymeID != null){
				if(aliasNames.size() > 0){
					String[] aliasNamesArr = new String[aliasNames.size()];
					aliasNames.copyInto(aliasNamesArr);
					EnzymeInfo enzymeInfo = new EnzymeInfo(currentECNumber,aliasNamesArr,currentReaction,currentSysname,currentCasID);
					FormalEnzyme formalEnzyme = new FormalEnzyme(currentEnzymeID,enzymeInfo);
					Object enzyme = formalEnzyme;
					if(createBound){
						BoundEnzyme boundEnzyme = new BoundEnzyme(currentDBSpeciesID,formalEnzyme);
						enzyme = boundEnzyme;
					}
					enzymes.add(enzyme);
				}
			}
			aliasNames.clear();
			if(rset.isAfterLast()){
				break;
			}
		}

		if(aliasNames.size() == 0){
			currentEnzymeID = enzymeID;
			currentECNumber = 	rset.getString(EnzymeTable.table.ecNumber.toString());
			currentSysname = 	rset.getString(EnzymeTable.table.sysname.toString());
				currentSysname = (currentSysname != null?org.vcell.util.TokenMangler.getSQLRestoredString(currentSysname):null);
			currentReaction = 	rset.getString(EnzymeTable.table.reaction.toString());
				currentReaction = (currentReaction != null?org.vcell.util.TokenMangler.getSQLRestoredString(currentReaction):null);
			currentCasID = 	rset.getString(EnzymeTable.table.casID.toString());
				currentCasID = (currentCasID != null?org.vcell.util.TokenMangler.getSQLRestoredString(currentCasID):null);
			if(createBound){
				currentDBSpeciesID =	new KeyValue(rset.getBigDecimal("dbspecies_id"));
			}
		}
		currentPreferred = rset.getString(EnzymeAliasTable.table.preferred.toString());
		currentAliasName = 	org.vcell.util.TokenMangler.getSQLRestoredString(rset.getString(EnzymeAliasTable.table.name.toString()));
		if(currentPreferred.compareToIgnoreCase("T") == 0){
			aliasNames.add(0,currentAliasName);
		}else{
			aliasNames.add(currentAliasName);
		}
	}

	DBFormalSpecies[] enzymesArr = null;
	if(enzymes.size() > 0){
		if(createBound){
			BoundEnzyme[] boundEnzymesArr = null;
			boundEnzymesArr = new BoundEnzyme[enzymes.size()];
			enzymes.copyInto(boundEnzymesArr);
			enzymesArr = boundEnzymesArr;
		}else{
			FormalEnzyme[] formalEnzymesArr = null;
			formalEnzymesArr = new FormalEnzyme[enzymes.size()];
			enzymes.copyInto(formalEnzymesArr);
			enzymesArr = formalEnzymesArr;
		}
	}
	return enzymesArr;
}
/**
 * gets an SQL list of the fields to update for this Enzyme
 * @return java.lang.String
 * @param enzyme Enzyme
 */
public String getSQLUpdateList(EnzymeInfo enzyme) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(reaction + "='" + org.vcell.util.TokenMangler.getSQLEscapedString(enzyme.getReaction()) + "',");
    buffer.append(ecNumber + "='" + enzyme.getFormalID() + "',");
    buffer.append(sysname + "='" + org.vcell.util.TokenMangler.getSQLEscapedString(enzyme.getSysname()) + "',");
    buffer.append(casID + "='" + org.vcell.util.TokenMangler.getSQLEscapedString(enzyme.getCasID()) + "'");
    return buffer.toString();
}
/**
 * Returns an SQL String with a value list taken from the parameter Enzyme
 * @return java.lang.String
 * @param key KeyValue
 * @param enzyme Enzyme
 */
public String getSQLValueList(KeyValue key, EnzymeInfo enzyme) {

    //	int defaultCharge = 0;

    StringBuffer buffer = new StringBuffer();
    buffer.append("(");
    buffer.append(key + ",");

    
    //
    // insert reaction
    //
    if (enzyme.getReaction() == null) {
        buffer.append("null" +",");
    } else {
        buffer.append("'" + org.vcell.util.TokenMangler.getSQLEscapedString(enzyme.getReaction()) + "',");
    }
    //
    // insert ECNumber
    //
    String ECNumber = null;
    ECNumber = enzyme.getFormalID();

    if (ECNumber == null) {
        buffer.append("null" +",");
    } else {
        buffer.append("'" + ECNumber + "',");
    }

    //
    // insert sysname
    //
    if (enzyme.getSysname() == null) {
        buffer.append("null");
    } else {
        buffer.append("'" + org.vcell.util.TokenMangler.getSQLEscapedString(enzyme.getSysname()) + "',");
    }

     //
    // insert casID
    //
    if (enzyme.getCasID() == null) {
        buffer.append("null");
    } else {
        buffer.append("'" + org.vcell.util.TokenMangler.getSQLEscapedString(enzyme.getCasID()) + "'");
    }
    
   buffer.append(")");
    return buffer.toString();
}
}
