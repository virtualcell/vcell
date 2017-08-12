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
import cbit.vcell.dictionary.BoundProtein;
import cbit.vcell.dictionary.FormalProtein;
import cbit.vcell.dictionary.ProteinInfo;
import cbit.vcell.model.DBFormalSpecies;

/**
 * Represents a table for storing Protein information in a database
 * Creation date: (6/25/2002 3:36:45 PM)
 * @author: Steven Woolley
 */
public class ProteinTable extends Table {
    private static final String TABLE_NAME = "vc_protein";
    public static final String REF_TYPE =
        "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    public final Field organism = 			new Field("organism", 			SQLDataType.varchar2_1024, 	"");
    public final Field accessionNumber = 	new Field("accessionnumber", 	SQLDataType.varchar2_1024, 	"");
    public final Field swissProtEntryName = new Field("swissprotid", 		SQLDataType.varchar2_32, 	"UNIQUE NOT NULL");
    public final Field keywords = 			new Field("keywords", 			SQLDataType.varchar2_1024, 	"");
    public final Field description = 		new Field("description", 		SQLDataType.varchar2_1024, 	"");
    public final Field molWeight = 			new Field("molWeight", 			SQLDataType.number_as_real, "");

    private final Field fields[] = { organism, accessionNumber, swissProtEntryName, keywords, description, molWeight};

    public static final ProteinTable table = new ProteinTable();

/**
 * Creates a new ProteinTable object with the defined table values and fields
 * Creation date: (6/25/2002 3:53:09 PM)
 */
public ProteinTable() {
	super(TABLE_NAME);
	addFields(fields);
}


/**
 * returns a protein object from the ResultSet
 * @return cbit.vcell.dictionary.protein
 * @param rset java.sql.ResultSet
 */
public DBFormalSpecies[] getProteins(java.sql.ResultSet rset, SessionLog log,boolean createBound) throws java.sql.SQLException {

	Vector proteins = new Vector();
	Vector aliasNames = new Vector();

	String currentAliasName = null;
	String currentKeywords = null;
	String currentSwissProtId = null;
	String currentAccession = null;
	String currentOrganism = null;
	String currentPreferred = null;
	String currentDescription = null;
	double currentMolWeight = ProteinInfo.UNKNOWN_MW;
	
	org.vcell.util.document.KeyValue currentProteinID = null;
	org.vcell.util.document.KeyValue currentDBSpeciesID = null;
	
	while(rset.next() || rset.isAfterLast()){
			
		KeyValue proteinID = null;
		if(!rset.isAfterLast()){
			proteinID = new KeyValue(rset.getBigDecimal(ProteinTable.table.id.toString()));
		}
		
		if(!rset.isFirst() && (!currentProteinID.equals(proteinID))){
			if(currentProteinID != null){
				if(aliasNames.size() > 0){
					String[] aliasNamesArr = new String[aliasNames.size()];
					aliasNames.copyInto(aliasNamesArr);
					String[] keywordsArr = null;
					ProteinInfo proteinInfo =
						new ProteinInfo(
							currentSwissProtId,aliasNamesArr,currentOrganism,
							currentAccession,currentKeywords,currentDescription,
							currentMolWeight);
					FormalProtein formalProtein = new FormalProtein(currentProteinID,proteinInfo);
					Object protein = formalProtein;
					if(createBound){
						BoundProtein boundProtein = new BoundProtein(currentDBSpeciesID,formalProtein);
						protein = boundProtein;
					}
					proteins.add(protein);
				}
			}
			aliasNames.clear();
			if(rset.isAfterLast()){
				break;
			}
		}

		if(aliasNames.size() == 0){
			currentProteinID = proteinID;
			currentSwissProtId = 	rset.getString(ProteinTable.table.swissProtEntryName.toString());
			currentAccession =		rset.getString(ProteinTable.table.accessionNumber.toString());
			currentOrganism = 		rset.getString(ProteinTable.table.organism.toString());
				currentOrganism = (currentOrganism != null?org.vcell.util.TokenMangler.getSQLRestoredString(currentOrganism):null);
			currentKeywords = rset.getString(ProteinTable.table.keywords.toString());
				currentKeywords = (currentKeywords != null?org.vcell.util.TokenMangler.getSQLRestoredString(currentKeywords):null);
			currentDescription = rset.getString(ProteinTable.table.description.toString());
				currentDescription = (currentDescription != null?org.vcell.util.TokenMangler.getSQLRestoredString(currentDescription):null);
			currentMolWeight = rset.getDouble(ProteinTable.table.molWeight.toString());
			if(rset.wasNull()){
				currentMolWeight = ProteinInfo.UNKNOWN_MW;
			}
				
			if(createBound){
				currentDBSpeciesID =	new KeyValue(rset.getBigDecimal("dbspecies_id"));
			}
		}
		currentAliasName = 	org.vcell.util.TokenMangler.getSQLRestoredString(rset.getString(ProteinAliasTable.table.name.toString()));
		if(!aliasNames.contains(currentAliasName)){
			currentPreferred = rset.getString(ProteinAliasTable.table.preferred.toString());
			if(currentPreferred.compareToIgnoreCase("true") == 0){
				aliasNames.add(0,currentAliasName);
			}else{
				aliasNames.add(currentAliasName);
			}
		}
	}

	DBFormalSpecies[] proteinsArr = null;
	if(proteins.size() > 0){
		if(createBound){
			BoundProtein[] boundProteinsArr = null;
			boundProteinsArr = new BoundProtein[proteins.size()];
			proteins.copyInto(boundProteinsArr);
			proteinsArr = boundProteinsArr;
		}else{
			FormalProtein[] formalProteinsArr = null;
			formalProteinsArr = new FormalProtein[proteins.size()];
			proteins.copyInto(formalProteinsArr);
			proteinsArr = formalProteinsArr;
		}
	}
	return proteinsArr;
}


/**
 * Returns a string with the SQL update information for a protein
 * @return java.lang.String
 * @param protein Protein
 */
public String getSQLUpdateList(ProteinInfo protein) {
    StringBuffer buffer = new StringBuffer();
    
    buffer.append(organism + "=" + (protein.getOrganism() != null?"'"+org.vcell.util.TokenMangler.getSQLEscapedString(protein.getOrganism())+"'":"null") + ",");
    buffer.append(accessionNumber + "=" + (protein.getAccession() != null?"'"+org.vcell.util.TokenMangler.getSQLEscapedString(protein.getAccession())+"'":"null") + ",");
    buffer.append(swissProtEntryName + "=" + (protein.getFormalID() != null?"'"+org.vcell.util.TokenMangler.getSQLEscapedString(protein.getFormalID())+"'":"null") + ",");
    buffer.append(keywords + "=" + (protein.getKeyWords() != null?"'"+org.vcell.util.TokenMangler.getSQLEscapedString(protein.getKeyWords())+"'":"null") + ",");
    buffer.append(description + "=" + (protein.getDescription() != null?"'"+org.vcell.util.TokenMangler.getSQLEscapedString(protein.getDescription())+"'":"null") + ",");
    buffer.append(molWeight + "=" + protein.getMolecularWeight());
    
    return buffer.toString();
}


/**
 * Returns an SQL String with a value list taken from the parameter Protein
 * @return java.lang.String
 * @param key KeyValue
 * @param protein Protein
 */
public String getSQLValueList(KeyValue key, ProteinInfo protein) {

    StringBuffer buffer = new StringBuffer();
    buffer.append("(");
    buffer.append(key + ",");
    
    buffer.append((protein.getOrganism() != null?"'"+org.vcell.util.TokenMangler.getSQLEscapedString(protein.getOrganism())+"'":"null") + ",");
    buffer.append((protein.getAccession() != null?"'"+org.vcell.util.TokenMangler.getSQLEscapedString(protein.getAccession())+"'":"null") + ",");
    buffer.append((protein.getFormalID() != null?"'"+org.vcell.util.TokenMangler.getSQLEscapedString(protein.getFormalID())+"'":"null") + ",");
    buffer.append((protein.getKeyWords() != null?"'"+org.vcell.util.TokenMangler.getSQLEscapedString(protein.getKeyWords())+"'":"null") + ",");
    buffer.append((protein.getDescription() != null?"'"+org.vcell.util.TokenMangler.getSQLEscapedString(protein.getDescription())+"'":"null") + ",");
    buffer.append(molWeight + "=" + protein.getMolecularWeight());

    buffer.append(")");
    
    return buffer.toString();
}
}
