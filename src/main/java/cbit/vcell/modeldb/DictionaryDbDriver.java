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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import org.vcell.db.KeyFactory;
import org.vcell.util.BeanUtils;
import org.vcell.util.SessionLog;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;

import cbit.sql.QueryHashtable;
import cbit.vcell.dictionary.CompoundInfo;
import cbit.vcell.dictionary.EnzymeInfo;
import cbit.vcell.dictionary.FormalCompound;
import cbit.vcell.dictionary.FormalEnzyme;
import cbit.vcell.dictionary.FormalProtein;
import cbit.vcell.dictionary.ProteinInfo;
import cbit.vcell.dictionary.db.CompoundAliasTable;
import cbit.vcell.dictionary.db.CompoundTable;
import cbit.vcell.dictionary.db.DBSpeciesTable;
import cbit.vcell.dictionary.db.EnzymeAliasTable;
import cbit.vcell.dictionary.db.EnzymeReactionTable;
import cbit.vcell.dictionary.db.EnzymeTable;
import cbit.vcell.dictionary.db.ProteinAliasTable;
import cbit.vcell.dictionary.db.ProteinTable;
import cbit.vcell.model.DBFormalSpecies;
import cbit.vcell.model.DBFormalSpecies.MatchedVCDocumentsFromSearch;
import cbit.vcell.model.DBSpecies;
import cbit.vcell.model.FormalSpeciesType;
import cbit.vcell.model.ReactionDescription;
import cbit.vcell.model.ReactionQuerySpec;
import cbit.vcell.model.ReactionStepInfo;


/**
 * This type was created in VisualAge.
 */
public class DictionaryDbDriver {
	//
    public static final CompoundTable compoundTable = CompoundTable.table;
    public static final CompoundAliasTable compoundAliasTable = CompoundAliasTable.table;
    public static final EnzymeTable enzymeTable = EnzymeTable.table;
    public static final EnzymeAliasTable enzymeAliasTable = EnzymeAliasTable.table;
    public static final ProteinAliasTable proteinAliasTable = ProteinAliasTable.table;
    public static final ProteinTable proteinTable = ProteinTable.table;
	public static final DBSpeciesTable dbSpeciesTable = DBSpeciesTable.table;
	
    private final SessionLog log;
    private final KeyFactory keyFactory;
    

/**
 * DictionaryDBManager constructor.
 */
public DictionaryDbDriver(KeyFactory keyFactory, SessionLog sessionLog) {
	super();
	this.keyFactory = keyFactory;
	this.log = sessionLog;
}


/**
 * Creates the SQL String that inserts info for a protein into the DB
 * @param protein Protein
 * @param con Connection
 * @exception SQLException.
 */
public int countTableEntries(Connection con, String tableName)
    throws java.sql.SQLException {
    Statement stmt;
    ResultSet rset;
    
	    int result = 0;
    if (tableName == null || con == null) {
        throw new IllegalArgumentException("Improper parameters for countTableEntries");
    }

    String sql;
    sql = "SELECT COUNT(*) total FROM " + tableName;

    //System.out.println(sql);

    stmt = con.createStatement();
    try {
        rset = stmt.executeQuery(sql);
        if (rset.next()) {
            result = rset.getInt("total");
        }
    } finally {
        stmt.close();
    }
    
    return result;
}


/**
 * Insert the method's description here.
 * Creation date: (2/26/2003 1:38:19 PM)
 * @return cbit.vcell.dictionary.DBSpecies
 * @param dbfs cbit.vcell.dictionary.DBFormalSpecies
 */
public DBSpecies getBoundSpecies(Connection con,DBFormalSpecies dbfs) throws SQLException{
	
	//Check if this is already a DBSpecies
	if(dbfs instanceof DBSpecies){
		return (DBSpecies)dbfs;
	}

	int restrictsearch = 0;
	if(dbfs.getFormalSpeciesType().equals(FormalSpeciesType.compound)){
		restrictsearch = FormalSpeciesType.COMPOUND_KEGGID;
	}else if(dbfs.getFormalSpeciesType().equals(FormalSpeciesType.enzyme)){
		restrictsearch = FormalSpeciesType.ENZYME_ECNUMBER;
	}else if(dbfs.getFormalSpeciesType().equals(FormalSpeciesType.protein)){
		restrictsearch = FormalSpeciesType.PROTEIN_SWISSPROTID;
	}else{
		throw new IllegalArgumentException("DictionaryDbDriver.getBoundSpecies: Unsupported FormalSpeciesType");
	}

	//Check if binding already exists in database
		
	DBFormalSpecies[] dbfsBound = getDatabaseSpecies(con,null,dbfs.getFormalSpeciesInfo().getFormalID(),true,dbfs.getFormalSpeciesType(),restrictsearch,-1,false);
	if(dbfsBound != null){
		if(dbfsBound.length == 1){
			return (DBSpecies)dbfsBound[0];
		}else{
			throw new RuntimeException("Expecting only 1 result");
		}
	}

	//Create new binding
	//Add entry to binding table
	KeyValue newKey = keyFactory.getNewKey(con);
	String sql = 	"INSERT INTO " + DBSpeciesTable.table.getTableName() +
					" VALUES " + DBSpeciesTable.table.getSQLValueList(newKey,dbfs);

	DbDriver.updateCleanSQL(con,sql);
	
	//Get new DBSpecies
	dbfsBound = getDatabaseSpecies(con,null,dbfs.getFormalSpeciesInfo().getFormalID(),true,dbfs.getFormalSpeciesType(),restrictsearch,-1,false);
	if(dbfsBound != null){
		if(dbfsBound.length == 1){
			return (DBSpecies)dbfsBound[0];
		}else{
			throw new RuntimeException("Expecting only 1 result");
		}
	}

	throw new RuntimeException("Couldn't Get New DBSpecies");
	
	
}


/**
 * returns an array of all Strings (names) stored in the Database like String.
 */
public String[] getCompoundAliases(Connection con, String likeString)
    throws SQLException {

	likeString = likeString.toUpperCase();
//	boolean hasDigit = false;
//	for(int i = 0;i < likeString.length();i+= 1){
//		if(Character.isDigit(likeString.charAt(i))){
//			hasDigit = true;
//			break;
//		}
//	}
					
	String sql;
    sql =
        " SELECT DISTINCT "
            + compoundAliasTable.name
            + " FROM "
            + compoundAliasTable.getTableName()
            + " WHERE "
            + "UPPER("
            + compoundAliasTable.name
            + ")"
            + " LIKE "
            + "'"
            + TokenMangler.getSQLEscapedString(likeString).toUpperCase()
            + "'"
            + " ORDER BY "
            + "UPPER("
            + compoundAliasTable.name
            + ")";

    Statement stmt = con.createStatement();
    java.util.Vector<String> nameList = new java.util.Vector<String>();
    try {
        ResultSet rset = stmt.executeQuery(sql);
        while (rset.next()) {

            String name =
                TokenMangler.getSQLRestoredString(
                    rset.getString(compoundAliasTable.name.getUnqualifiedColName()));
            //System.out.println("DictionaryDbDriver.getAliases() name = " + name);
            nameList.add(name);
        }
    } finally {
        stmt.close(); // Release resources include resultset
    }
    String names[] = new String[nameList.size()];
    if (nameList.size() > 0) {
        nameList.copyInto(names);
    }
    return names;
}


/**
 * Returns the compound referenced by the casID
 * @return Compound
 * @param con Connection
 * @param casID String
 */
public FormalCompound getCompoundFromCasID(Connection con, String casID) throws SQLException {

	FormalCompound result = null;
	DBFormalSpecies[] dbfsArr = getDatabaseSpecies(con,null,casID,false,FormalSpeciesType.compound,FormalSpeciesType.COMPOUND_CASID,-1,false);
	if(dbfsArr != null && dbfsArr.length == 1){
		result = (FormalCompound)dbfsArr[0];
	}else{
		throw new RuntimeException("Expecting only 1 result");
	}

	return result;

}


/**
 * Returns the compound referenced by the keggID
 * @return Compound
 * @param con Connection
 * @param dbLink DBLink
 */
public FormalCompound getCompoundFromKeggID(Connection con, String keggID) throws SQLException {

	FormalCompound result = null;
	DBFormalSpecies[] dbfsArr = getDatabaseSpecies(con,null,keggID,false,FormalSpeciesType.compound,FormalSpeciesType.COMPOUND_KEGGID,-1,false);
	if(dbfsArr != null && dbfsArr.length == 1){
		result = (FormalCompound)dbfsArr[0];
	}else{
		throw new RuntimeException("Expecting only 1 result");
	}

	return result;

}


/**
 * Insert the method's description here.
 * Creation date: (2/15/2003 10:12:24 PM)
 * @return cbit.vcell.dictionary.DBSpecies[]
 * @param con java.sql.Connection
 * @param user cbit.vcell.server.User
 * @param bOnlyUser boolean
 */
public DBFormalSpecies[] getDatabaseSpecies(Connection con, User user, String likeString,boolean isBound,FormalSpeciesType speciesType,int restrictSearch,int rowLimit,boolean bOnlyUser) throws SQLException{

	if(speciesType != null && speciesType.equals(FormalSpeciesType.speciesMatchSearch)){
		FormalSpeciesType.MatchSearchFormalSpeciesType matchSearchFormalSpeciesType = (FormalSpeciesType.MatchSearchFormalSpeciesType)speciesType;
		if(matchSearchFormalSpeciesType.getMatchCriterias() == null || matchSearchFormalSpeciesType.getMatchCriterias().length == 0){
			return null;
		}
		ArrayList<VCDocumentInfo> matchedVCDocumentInfos = new ArrayList<VCDocumentInfo>();
		Statement stmt = null;
		// OR condition
//		String sql =
//			"SELECT UNIQUE "  +BioModelTable.table.id.getQualifiedColName() +
//			" FROM "  + BioModelTable.table.getTableName() + "," + SpeciesContextModelTable.table.getTableName() +
//			" WHERE " + BioModelTable.table.modelRef.getQualifiedColName() + " = "+ SpeciesContextModelTable.table.modelRef.getQualifiedColName() +
//			" AND (";
//		for (int i = 0; i < matchSearchFormalSpeciesType.getMatchCriterias().length; i++) {
//			sql+=
//			(i>0?" OR ":"") +
//			" LOWER("+SpeciesContextModelTable.table.name.getQualifiedColName()+") LIKE " + "'" + matchSearchFormalSpeciesType.getMatchCriterias()[i] + "'" + " ESCAPE '"+BeanUtils.SQL_ESCAPE_CHARACTER+"'";
//		}
//		sql+=")";
		
		// AND condition
		String sql = "";
		for (int i = 0; i < matchSearchFormalSpeciesType.getMatchCriterias().length; i++) {
			sql+= (i>0?" INTERSECT ":"") +
			"SELECT UNIQUE " + BioModelTable.table.id.getQualifiedColName() +
			" FROM "  + BioModelTable.table.getTableName() + "," + SpeciesContextModelTable.table.getTableName() +
			" WHERE " + BioModelTable.table.modelRef.getQualifiedColName() + " = "+ SpeciesContextModelTable.table.modelRef.getQualifiedColName() +
			" AND " + " LOWER("+SpeciesContextModelTable.table.name.getQualifiedColName()+") LIKE " + "'" + matchSearchFormalSpeciesType.getMatchCriterias()[i] + "'" + " ESCAPE '"+BeanUtils.SQL_ESCAPE_CHARACTER+"'";
		}
		try{
			stmt = con.createStatement();
			ResultSet rset = stmt.executeQuery(sql);
			while(rset.next()){
				BigDecimal versionKey = rset.getBigDecimal(1);
				Version version = new Version(new KeyValue(versionKey), null, null, null, null, null, null, null, null);
				matchedVCDocumentInfos.add(new BioModelInfo(version, null, (BioModelChildSummary)null, null));
			}
			
		}finally{
			if(stmt!=null){stmt.close();}
		}
		if(matchedVCDocumentInfos.size() == 0){
			return null;
		}
		return new DBFormalSpecies[] {new MatchedVCDocumentsFromSearch(matchedVCDocumentInfos)};
	}
	
	//isBound - 	if true find FormalSpecies(Dictionary) that have binding table entries, if false find any FormalSpecies(Dictionary)
	//user - 		If not null find bound FormalSpecies owned by this user, if null do not check ownership
	//likeString -	SQL 'LIKE' syntax, compare to various table elements depending on type.  If searching ID property, likeString
	//				is matched exactly (id = 'likeString')
	//speciesType -	What to search for (compound,enzyme,protein)
	//restrictSearch - 	Search only certain proerties(FormalSpeciesType.XXX) of database type.  When searching for ID property, it must be the
	//					only property specified
	//rowLimit -	Determines maximum rows returned in low level query. Equal to total number of alias names found in DB.
	//				The last DBFormalSpecies obect in results array may not contain all its aliases if rowlimit != -1
	
	if(!speciesType.bValidProperties(restrictSearch)){
		throw new IllegalArgumentException("Improper properties in restrictSearch");
	}
	
	if(bOnlyUser && isBound == false){
		throw new IllegalArgumentException("user not null expects isBound = true");
	}

	if(!((rowLimit == -1) || (rowLimit > 0))){
		throw new IllegalArgumentException("rowLimit must be -1(Unlimited) or greater than 0");
	}
	
	if	(
		(((restrictSearch & FormalSpeciesType.COMPOUND_ID) != 0) && (restrictSearch != FormalSpeciesType.COMPOUND_ID)) ||
		(((restrictSearch & FormalSpeciesType.ENZYME_ID) != 0) && (restrictSearch != FormalSpeciesType.ENZYME_ID)) ||
		(((restrictSearch & FormalSpeciesType.PROTEIN_ID) != 0) && (restrictSearch != FormalSpeciesType.PROTEIN_ID))
		)
	{
		throw new RuntimeException("Incompatible search properties together");
	}
	
	likeString = likeString.toUpperCase();
	
	//Columns always needed
	String columns =
		(speciesType.equals(FormalSpeciesType.compound) ?
			CompoundTable.table.getTableName()+".*"+","+CompoundAliasTable.table.name.getQualifiedColName()+","+CompoundAliasTable.table.preferred.getQualifiedColName() : "") +
		(speciesType.equals(FormalSpeciesType.enzyme) ?
			EnzymeTable.table.getTableName()+".*"+","+EnzymeAliasTable.table.name.getQualifiedColName()+","+EnzymeAliasTable.table.preferred.getQualifiedColName() : "") +
		(speciesType.equals(FormalSpeciesType.protein) ?
			ProteinTable.table.getTableName()+".*"+","+
			ProteinAliasTable.table.name.getQualifiedColName()+","+
			ProteinAliasTable.table.preferred.getQualifiedColName() : "");

	//Columns if Bound (aliased because id in other tables)
	String dbSpeciesTable_id_alias = "dbspecies_id";
	if(isBound){
		columns = columns+","+DBSpeciesTable.table.id.getQualifiedColName()+" "+dbSpeciesTable_id_alias;
	}

	// Tables always needed
	String tables = 
		(speciesType.equals(FormalSpeciesType.compound) ?
			CompoundTable.table.getTableName() + "," + CompoundAliasTable.table.getTableName(): "") +
		(speciesType.equals(FormalSpeciesType.enzyme) ?
			EnzymeTable.table.getTableName() + "," + EnzymeAliasTable.table.getTableName() : "") +
		(speciesType.equals(FormalSpeciesType.protein) ?
			ProteinTable.table.getTableName() + "," + ProteinAliasTable.table.getTableName() : "");		

	// Tables if Bound
	if(isBound){
		tables = tables + "," + DBSpeciesTable.table.getTableName();
		if(bOnlyUser){
			tables = tables + "," +
				SpeciesContextModelTable.table.getTableName() + "," +
				SpeciesTable.table.getTableName() + "," +
				ModelTable.table.getTableName();
		}
	}

	String condition = "";

	// Conditions always needed
	condition = condition +
		(speciesType.equals(FormalSpeciesType.compound) ?
			CompoundTable.table.id.getQualifiedColName()+"="+CompoundAliasTable.table.compoundRef.getQualifiedColName(): "") +
		(speciesType.equals(FormalSpeciesType.enzyme) ?
			EnzymeTable.table.id.getQualifiedColName()+"="+EnzymeAliasTable.table.enzymeRef.getQualifiedColName(): "") +
		(speciesType.equals(FormalSpeciesType.protein) ?
		 	ProteinTable.table.id.getQualifiedColName()+"="+ProteinAliasTable.table.proteinRef.getQualifiedColName() : "");

	
	java.util.Vector<String> likeConditions = new java.util.Vector<String>();
	
	if(speciesType.equals(FormalSpeciesType.compound)){
		
		if((restrictSearch & FormalSpeciesType.COMPOUND_ID) != 0){likeConditions.add(" UPPER("+CompoundTable.table.id.getQualifiedColName()+") = "+likeString);}
		if((restrictSearch & FormalSpeciesType.COMPOUND_ALIAS) != 0){likeConditions.add(" UPPER("+CompoundAliasTable.table.name.getQualifiedColName() + ") LIKE " + "'"+likeString+"'" + " ESCAPE '/'");}
		if((restrictSearch & FormalSpeciesType.COMPOUND_KEGGID) != 0){likeConditions.add(" UPPER("+CompoundTable.table.keggID.getQualifiedColName() + ") LIKE " + "'"+likeString+"'" + " ESCAPE '/'");}
		if((restrictSearch & FormalSpeciesType.COMPOUND_CASID) != 0){likeConditions.add(" UPPER("+CompoundTable.table.casID.getQualifiedColName() + ") LIKE " + "'"+likeString+"'" + " ESCAPE '/'");}
		if((restrictSearch & FormalSpeciesType.COMPOUND_FORMULA) != 0){likeConditions.add(" UPPER("+CompoundTable.table.formula.getQualifiedColName() + ") LIKE " + "'"+likeString+"'" + " ESCAPE '/'");}
		
	}else if(speciesType.equals(FormalSpeciesType.enzyme)){
		
		if((restrictSearch & FormalSpeciesType.ENZYME_ID) != 0){likeConditions.add(" UPPER("+EnzymeTable.table.id.getQualifiedColName()+") = "+likeString);}
		if((restrictSearch & FormalSpeciesType.ENZYME_ALIAS) != 0){likeConditions.add(" UPPER("+EnzymeAliasTable.table.name.getQualifiedColName() + ") LIKE " + "'"+likeString+"'" + " ESCAPE '/'");}
		if((restrictSearch & FormalSpeciesType.ENZYME_SYSNAME) != 0){likeConditions.add(" UPPER("+EnzymeTable.table.sysname.getQualifiedColName() + ") LIKE " + "'"+likeString+"'" + " ESCAPE '/'");}
		if((restrictSearch & FormalSpeciesType.ENZYME_ECNUMBER) != 0){likeConditions.add(" UPPER("+EnzymeTable.table.ecNumber.getQualifiedColName() + ") LIKE " + "'"+likeString+"'" + " ESCAPE '/'");}
		//if((restrictSearch & FormalSpeciesType.ENZYME_ECNUMBER) != 0){likeConditions.add(" UPPER("+EnzymeTable.table.ecNumber.getQualifiedColName() + ") LIKE " + "'EC "+likeString+"'");}
		if((restrictSearch & FormalSpeciesType.ENZYME_REACTION) != 0){likeConditions.add(" UPPER("+EnzymeTable.table.reaction.getQualifiedColName() + ") LIKE " + "'"+likeString+"'" + " ESCAPE '/'");}
		if((restrictSearch & FormalSpeciesType.ENZYME_CASID) != 0){likeConditions.add(" UPPER("+EnzymeTable.table.casID.getQualifiedColName() + ") LIKE " + "'"+likeString+"'" + " ESCAPE '/'");}
			
	}else if(speciesType.equals(FormalSpeciesType.protein)){
		
		if((restrictSearch & FormalSpeciesType.PROTEIN_ID) != 0){likeConditions.add(" UPPER("+ProteinTable.table.id.getQualifiedColName()+") = "+likeString);}
	 	if((restrictSearch & FormalSpeciesType.PROTEIN_ALIAS) != 0){likeConditions.add(" UPPER("+ProteinAliasTable.table.name.getQualifiedColName() + ") LIKE " + "'"+likeString+"'" + " ESCAPE '/'");}
		if((restrictSearch & FormalSpeciesType.PROTEIN_ACCESSION) != 0){likeConditions.add(" UPPER("+ProteinTable.table.accessionNumber.getQualifiedColName() + ") LIKE " + "'"+likeString+"'" + " ESCAPE '/'");}
		if((restrictSearch & FormalSpeciesType.PROTEIN_SWISSPROTID) != 0){likeConditions.add(" UPPER("+ProteinTable.table.swissProtEntryName.getQualifiedColName() + ") LIKE " + "'"+likeString+"'" + " ESCAPE '/'");}
		if((restrictSearch & FormalSpeciesType.PROTEIN_ORGANISM) != 0){likeConditions.add(" UPPER("+ProteinTable.table.organism.getQualifiedColName() + ") LIKE " + "'"+likeString+"'" + " ESCAPE '/'");}
		if((restrictSearch & FormalSpeciesType.PROTEIN_KEYWORD) != 0){likeConditions.add(" UPPER("+ProteinTable.table.keywords.getQualifiedColName() + ") LIKE " + "'"+likeString+"'" + " ESCAPE '/'");}
		if((restrictSearch & FormalSpeciesType.PROTEIN_DESCR) != 0){likeConditions.add(" UPPER("+ProteinTable.table.description.getQualifiedColName() + ") LIKE " + "'"+likeString+"'" + " ESCAPE '/'");}	
	}else{
		throw new RuntimeException(speciesType.getName()+" Unsupported");
	}
	
	if(likeConditions.size() > 0){
		condition = condition + " AND ( ";
		for(int i = 0;i<likeConditions.size();i+= 1){
			String newCondition = (String)likeConditions.get(i);
			if(i != 0){
				condition = condition + " OR ";
			}
			condition = condition + newCondition;
		}
		condition = condition + ")";
	}

	if(isBound){
		condition = condition + " AND " +
			(speciesType.equals(FormalSpeciesType.compound) ?
				CompoundTable.table.id.getQualifiedColName()+"=" + DBSpeciesTable.table.compoundRef.getQualifiedColName(): "") +
			(speciesType.equals(FormalSpeciesType.enzyme) ?
				EnzymeTable.table.id.getQualifiedColName()+"=" + DBSpeciesTable.table.enzymeRef.getQualifiedColName(): "") +
			(speciesType.equals(FormalSpeciesType.protein) ?
			 	ProteinTable.table.id.getQualifiedColName()+"=" + DBSpeciesTable.table.proteinRef.getQualifiedColName(): "");
			
		
		if(bOnlyUser){
			condition = condition +
			" AND " + SpeciesContextModelTable.table.speciesRef.getQualifiedColName() + " = " + SpeciesTable.table.id.getQualifiedColName() +
			" AND " + SpeciesTable.table.dbSpeciesRef.getQualifiedColName() + " = " + DBSpeciesTable.table.id.getQualifiedColName() +
			" AND " + SpeciesContextModelTable.table.modelRef.getQualifiedColName() + " = " + ModelTable.table.id.getQualifiedColName()+
			" AND " + ModelTable.table.ownerRef.getQualifiedColName() + " = " + user.getID();
		}
	}

	//ORDER BY 'id' must not be changed, used to collect multiple aliasnames into same info object
	String orderBy = 
		(speciesType.equals(FormalSpeciesType.compound) ?
			CompoundTable.table.id.getQualifiedColName(): "") +
		(speciesType.equals(FormalSpeciesType.enzyme) ?
			EnzymeTable.table.id.getQualifiedColName(): "") +
		(speciesType.equals(FormalSpeciesType.protein) ?
		 	ProteinTable.table.id.getQualifiedColName(): "");


	String sql = "SELECT " + columns +" FROM " + tables + " WHERE " + condition + " ORDER BY " + orderBy;
	
	DBFormalSpecies[] databaseSpecies = null;
	
	Statement stmt = con.createStatement();
	if(rowLimit > 0){
		stmt.setMaxRows(rowLimit);
	}
    try {
        ResultSet rset = stmt.executeQuery(sql);
		if(speciesType.equals(FormalSpeciesType.compound)){
	        	databaseSpecies = CompoundTable.table.getCompounds(rset,log,isBound);
        }else if (speciesType.equals(FormalSpeciesType.enzyme)){
	        	databaseSpecies = EnzymeTable.table.getEnzymes(rset,log,isBound);
        }else if(speciesType.equals(FormalSpeciesType.protein)){
	        	databaseSpecies = ProteinTable.table.getProteins(rset,log,isBound);
        }else{
	        throw new RuntimeException("FormalSpeciesType="+speciesType.getName()+" Unsupported");
        }
    } finally {
        stmt.close(); // Release resources include resultset
    }
    //
    return databaseSpecies;
}


public DBSpecies getDBSpeciesFromKeyValue(QueryHashtable dbc, Connection con,KeyValue argDBSpeciesRef) throws SQLException {

	//
	// try to get SpeciesReference from the object cache
	//
	DBSpecies dbSpeciesRef = (DBSpecies)dbc.get(argDBSpeciesRef);
	if (dbSpeciesRef!=null){
		return dbSpeciesRef;
	}
		
    DBSpecies result = null;
    String sql;
    sql =
        " SELECT "
            + " * "
            + " FROM "
            + dbSpeciesTable.getTableName()
            + " WHERE "
            + dbSpeciesTable.id
            + " = "
            + argDBSpeciesRef;

    Statement stmt = con.createStatement();

    try {
        ResultSet rset = stmt.executeQuery(sql);
        if (rset.next()) {

            DBSpecies dbSpecies = null;

            java.math.BigDecimal compoundBD = rset.getBigDecimal(dbSpeciesTable.compoundRef.getUnqualifiedColName());
            java.math.BigDecimal enzymeBD = rset.getBigDecimal(dbSpeciesTable.enzymeRef.getUnqualifiedColName());
            java.math.BigDecimal proteinBD = rset.getBigDecimal(dbSpeciesTable.proteinRef.getUnqualifiedColName());

            if (compoundBD != null) {
				dbSpecies = (DBSpecies)getDatabaseSpecies(con,null,compoundBD.toString(),true,FormalSpeciesType.compound,FormalSpeciesType.COMPOUND_ID,-1,false)[0];
            } else if (enzymeBD != null) {
				dbSpecies = (DBSpecies)getDatabaseSpecies(con,null,enzymeBD.toString(),true,FormalSpeciesType.enzyme,FormalSpeciesType.ENZYME_ID,-1,false)[0];
			} else if (proteinBD != null) {
				dbSpecies = (DBSpecies)getDatabaseSpecies(con,null,proteinBD.toString(),true,FormalSpeciesType.protein,FormalSpeciesType.PROTEIN_ID,-1,false)[0];
			}
            result = dbSpecies;
            dbc.put(result.getDBSpeciesKey(),result);
        }
    } finally {
        stmt.close(); // Release resources include resultset
    }
    return result;
}


/**
 * Insert the method's description here.
 * Creation date: (4/18/2003 10:23:37 AM)
 */
public ReactionDescription[] getDictionaryReactions(Connection con,ReactionQuerySpec reactionQuerySpec) throws SQLException{

	String sql = EnzymeReactionTable.table.getSQLReactionQuery(reactionQuerySpec);

	ReactionDescription[] dictionaryReactions = null;
	
	Statement stmt = con.createStatement();
    try {
        ResultSet rset = stmt.executeQuery(sql);
        dictionaryReactions = EnzymeReactionTable.table.getReactions(rset);
    } finally {
        stmt.close(); // Release resources include resultset
    }
    //
    return dictionaryReactions;
	
}


/**
 * returns an array of all Strings (names) stored in the Enzyme Database like String.
 */
public String[] getEnzymeAliases(Connection con, String likeString)
    throws SQLException {

	likeString = likeString.toUpperCase();
//	boolean hasDigit = false;
//	for(int i = 0;i < likeString.length();i+= 1){
//		if(Character.isDigit(likeString.charAt(i))){
//			hasDigit = true;
//			break;
//		}
//	}

	String sql;
    sql =
        " SELECT DISTINCT "
            + " * "
            + " FROM "
            + enzymeAliasTable.getTableName()
            + " WHERE "
            + "UPPER("
            + enzymeAliasTable.name
            + ")"
            + " LIKE "
            + "'"
            + TokenMangler.getSQLEscapedString(likeString).toUpperCase()
            + "'"
            + " ORDER BY "
            + "UPPER("
            + enzymeAliasTable.name
            + ")";

    Statement stmt = con.createStatement();
    java.util.Vector<String> nameList = new java.util.Vector<String>();
    try {
        ResultSet rset = stmt.executeQuery(sql);
        while (rset.next()) {

            String name =
                TokenMangler.getSQLRestoredString(
                    rset.getString(enzymeAliasTable.name.getUnqualifiedColName()));
            //System.out.println("DictionaryDbDriver.getEnzymeAliases() name = " + name);
            nameList.add(name);
        }
    } finally {
        stmt.close(); // Release resources include resultset
    }
    String names[] = new String[nameList.size()];
    if (nameList.size() > 0) {
        nameList.copyInto(names);
    }
    return names;
}


/**
 * Returns the enzyme referenced by the given ECNumber
 * @return Enzyme
 * @param con Connection
 * @param ecNumber String
 */
public FormalEnzyme getEnzymeFromECNumber(Connection con, String ecNumber) throws SQLException {

	FormalEnzyme result = null;
	DBFormalSpecies[] dbfsArr = getDatabaseSpecies(con,null,ecNumber,false,FormalSpeciesType.enzyme,FormalSpeciesType.ENZYME_ECNUMBER,-1,false);
	if(dbfsArr != null && dbfsArr.length > 0){
		result = (FormalEnzyme)dbfsArr[0];
	}else{
		throw new RuntimeException("Expecting only 1 result");
	}

	return result;
}


/**
 * returns an array of all Strings (names) stored in the Database like String.
 */
public String[] getProteinAliases(Connection con, String likeString)
    throws SQLException {

	likeString = likeString.toUpperCase();
//	boolean hasDigit = false;
//	for(int i = 0;i < likeString.length();i+= 1){
//		if(Character.isDigit(likeString.charAt(i))){
//			hasDigit = true;
//			break;
//		}
//	}
					
	String sql;
    sql =
        " SELECT DISTINCT "
            + proteinAliasTable.name
            + " FROM "
            + proteinAliasTable.getTableName()
            + " WHERE "
            + "UPPER("
            + proteinAliasTable.name
            + ")"
            + " LIKE "
            + "'"
            + TokenMangler.getSQLEscapedString(likeString).toUpperCase()
            + "'"
            + " ORDER BY"
            + " UPPER("
            + proteinAliasTable.name
            + ")";

    Statement stmt = con.createStatement();
    java.util.Vector<String> nameList = new java.util.Vector<String>();
    try {
        ResultSet rset = stmt.executeQuery(sql);
        while (rset.next()) {

            String name =
                TokenMangler.getSQLRestoredString(
                    rset.getString(proteinAliasTable.name.getUnqualifiedColName()));
            //System.out.println("DictionaryDbDriver.getProteinAliases() name = " + name);
            nameList.add(name);
        }
    } finally {
        stmt.close(); // Release resources include resultset
    }
    String names[] = new String[nameList.size()];
    if (nameList.size() > 0) {
        nameList.copyInto(names);
    }
    return names;
}


/**
 * Returns the protein referenced by the given SwissProt Accession Number
 * @return Protein
 * @param con Connection
 * @param swissProtID String
 */
public FormalProtein getProteinFromSwissProtID(Connection con, String swissProtID) throws SQLException {
	
	FormalProtein result = null;
	DBFormalSpecies[] dbfsArr = getDatabaseSpecies(con,null,swissProtID,false,FormalSpeciesType.protein,FormalSpeciesType.PROTEIN_SWISSPROTID,-1,false);
	if(dbfsArr != null && dbfsArr.length > 0){
		result = (FormalProtein)dbfsArr[0];
	}else{
		throw new RuntimeException("Expecting only 1 result");
	}

	return result;
	
}


/**
 * returns an array of all Strings (names) stored in the Database like String.
 */
public String[] getProteinKeyWords(Connection con, String likeString) throws SQLException {

	return null;
    //String sql;
    //sql =
        //" SELECT DISTINCT "
            //+ proteinKeywordTable.keyWord
            //+ " FROM "
            //+ proteinKeywordTable.getTableName()
            //+ " WHERE "
            //+ "UPPER("
            //+ proteinKeywordTable.keyWord
            //+ ")"
            //+ " LIKE "
            //+ "'"
            //+ cbit.util.TokenMangler.getSQLEscapedString(likeString).toUpperCase()
            //+ "'"
            //+ " ORDER BY"
            //+ " UPPER("
            //+ proteinKeywordTable.keyWord
            //+ ")";

    //Statement stmt = con.createStatement();
    //java.util.Vector nameList = new java.util.Vector();
    //try {
        //ResultSet rset = stmt.executeQuery(sql);
        //while (rset.next()) {

            //String name =
                //cbit.util.TokenMangler.getSQLRestoredString(
                    //rset.getString(proteinKeywordTable.keyWord.getUnqualifiedColName()));
            ////System.out.println("DictionaryDbDriver.getProteinKeyWords() name = " + name);
            //nameList.add(name);
        //}
    //} finally {
        //stmt.close(); // Release resources include resultset
    //}
    //String names[] = new String[nameList.size()];
    //if (nameList.size() > 0) {
        //nameList.copyInto(names);
    //}
    //return names;
}


/**
 * Insert the method's description here.
 * Creation date: (4/18/2003 10:23:37 AM)
 */
public ReactionStepInfo[] getReactionStepInfos(Connection con,User user,KeyValue reactionStepKeys[]) throws SQLException{
	String sql = ReactStepTable.table.getSQLReactionStepInfosQuery(reactionStepKeys,user);
	Statement stmt = con.createStatement();
	Vector<ReactionStepInfo> reactionStepInfoList = new Vector<ReactionStepInfo>();
    try {
        ResultSet rset = stmt.executeQuery(sql);
        while(rset.next()){
	       java.math.BigDecimal rxID = rset.getBigDecimal(ReactStepTable.table.id.toString());
	       KeyValue rxKey = new KeyValue(rxID);
	       String modelOwnerName = rset.getString(UserTable.table.userid.toString());
	       java.math.BigDecimal modelOwnerID = rset.getBigDecimal(BioModelTable.table.ownerRef.toString());
	       User owner = new User(modelOwnerName,new KeyValue(modelOwnerID));
	       String bioModelName = rset.getString(ReactStepTable.RXIDDN_BIOMODEL_NAME_INDEX);
	       String rxName = rset.getString(ReactStepTable.RXIDDN_REACTSTEP_NAME_INDEX);
	       java.sql.Date bioModelDate = rset.getDate(BioModelTable.table.versionDate.toString());
	       ReactionStepInfo reactionStepInfo = new ReactionStepInfo(rxKey,owner,bioModelName,rxName,bioModelDate);
	       reactionStepInfoList.add(reactionStepInfo);
        }
    } finally {
        stmt.close(); // Release resources include resultset
    }
    if(reactionStepInfoList.size() == 0){
	    return null;
    }
	return (ReactionStepInfo[])BeanUtils.getArray(reactionStepInfoList,ReactionStepInfo.class);
}


/**
 * Insert the method's description here.
 * Creation date: (4/18/2003 10:23:37 AM)
 */
public ReactionDescription[] getUserReactionDescriptions(Connection con,User user,ReactionQuerySpec reactionQuerySpec) throws SQLException{

	String sql = ReactStepTable.table.getSQLUserReactionListQuery(reactionQuerySpec,user);

	ReactionDescription[] rxArr = null;
	
	Statement stmt = con.createStatement();
    try {
        ResultSet rset = stmt.executeQuery(sql);
        rxArr = ReactStepTable.table.getUserReactionList(rset);
    } finally {
        stmt.close(); // Release resources include resultset
    }
    //
    return rxArr;
}


/**
 * Inserts a Compound into the DB.
 */
public KeyValue insertCompound(Connection con, CompoundInfo compound)
    throws SQLException {
    if (compound == null) {
        throw new SQLException("Improper parameters for insertCompound");
    }
    log.print(
        "DictionaryDbDriver.insertCompound(compound=" + compound.getFormalID() + ")");
    KeyValue key = keyFactory.getNewKey(con);
    insertCompoundSQL(con, key, compound);
    return key;
}


/**
 * Creates the SQL String that inserts info for a compound into the DB
 * @param compound Compound
 * @param con Connection
 * @exception SQLException.
 */
private void insertCompoundSQL(Connection con, KeyValue key, CompoundInfo compound)
    throws SQLException {
    if (compound == null || con == null) {
        throw new IllegalArgumentException("Improper parameters for insertCompoundSQL");
    }

    String sql;
    sql =
        "INSERT INTO "
            + compoundTable.getTableName()
            + " "
            + compoundTable.getSQLColumnList()
            + " VALUES "
            + compoundTable.getSQLValueList(key, compound);

    //System.out.println(sql);

    DbDriver.updateCleanSQL(con, sql);

    //
    // insert aliases
    //
    String names[] = compound.getNames();
    for (int i = 0; i < names.length; i++) {
        KeyValue newKey = keyFactory.getNewKey(con);
        sql =
            "INSERT INTO "
                + compoundAliasTable.getTableName()
                + " "
                + compoundAliasTable.getSQLColumnList()
                + " VALUES "
                + compoundAliasTable.getSQLValueList(
                    newKey,
                    key,
                    TokenMangler.getSQLEscapedString(names[i]),
                    (i == 0));
        //System.out.println(sql);
        DbDriver.updateCleanSQL(con, sql);
    }
}


/**
 * Inserts an enzyme into the DB.
 */
public KeyValue insertEnzyme(Connection con, EnzymeInfo enzyme)
    throws SQLException {
    if (enzyme == null) {
        throw new SQLException("Improper parameters for insertEnzyme");
    }
    log.print("DictionaryDbDriver.insertEnzyme(enzyme=" + enzyme.getFormalID() + ")");
    KeyValue key = keyFactory.getNewKey(con);
    insertEnzymeSQL(con, key, enzyme);
    return key;
}


/**
 * Creates the SQL String that inserts info for an Ezymeinto the DB
 * @param enzyme Enzyme
 * @param con Connection
 * @exception SQLException.
 */
private void insertEnzymeSQL(Connection con, KeyValue key, EnzymeInfo enzyme)
    throws SQLException {
    if (enzyme == null || con == null) {
        throw new IllegalArgumentException("Improper parameters for insertEnzymeSQL");
    }

    String sql;
    sql =
        "INSERT INTO "
            + enzymeTable.getTableName()
            + " "
            + enzymeTable.getSQLColumnList()
            + " VALUES "
            + enzymeTable.getSQLValueList(key, enzyme);

    //System.out.println(sql);

    DbDriver.updateCleanSQL(con, sql);

    //
    // insert aliases
    //
    String names[] = enzyme.getNames();
    for (int i = 0; i < names.length; i++) {
        KeyValue newKey = keyFactory.getNewKey(con);
        sql =
            "INSERT INTO "
                + enzymeAliasTable.getTableName()
                + " "
                + enzymeAliasTable.getSQLColumnList()
                + " VALUES "
                + enzymeAliasTable.getSQLValueList(
                    newKey,
                    key,
                    TokenMangler.getSQLEscapedString(names[i]),
                    (i == 0));
        //System.out.println(sql);
        DbDriver.updateCleanSQL(con, sql);
    }
}


/**
 * Inserts a Protein into the DB.
 */
public KeyValue insertProtein(Connection con, ProteinInfo protein)
    throws SQLException {
    if (protein == null) {
        throw new SQLException("Improper parameters for insertProtein");
    }
    //log.print("DictionaryDbDriver.insertProtein(protein=" + protein.getDBID() + ")");
    KeyValue key = keyFactory.getNewKey(con);
    insertProteinSQL(con, key, protein);
    return key;
}


/**
 * Creates the SQL String that inserts info for a protein into the DB
 * @param protein Protein
 * @param con Connection
 * @exception SQLException.
 */
private void insertProteinSQL(Connection con, KeyValue key, ProteinInfo protein)
    throws SQLException {
    if (protein == null || con == null) {
        throw new IllegalArgumentException("Improper parameters for insertProteinSQL");
    }

    String sql;
    sql =
        "INSERT INTO "
            + proteinTable.getTableName()
            + " "
            + proteinTable.getSQLColumnList()
            + " VALUES "
            + proteinTable.getSQLValueList(key, protein);

    //System.out.println(sql);

    DbDriver.updateCleanSQL(con, sql);

    //
    // insert names and aliases
    //
    String names[] = protein.getNames();
    for (int i = 0; i < names.length; i++) {
        KeyValue newKey = keyFactory.getNewKey(con);
        sql =
            "INSERT INTO "
                + proteinAliasTable.getTableName()
                + " "
                + proteinAliasTable.getSQLColumnList()
                + " VALUES "
                + proteinAliasTable.getSQLValueList(
                    newKey,
                    key,
                    TokenMangler.getSQLEscapedString(names[i]),
                    (i == 0));
        //        System.out.println(sql);
        DbDriver.updateCleanSQL(con, sql);
    }

    ////
    //// insert component names
    ////
    //names = protein.getComponentNames();
    //for (int i = 0; i < names.length; i++) {
        //KeyValue newKey = cbit.vcell.modeldb.DbDriver.getNewKey(con);
        //sql =
            //"INSERT INTO "
                //+ proteinAliasTable.getTableName()
                //+ " "
                //+ proteinAliasTable.getSQLColumnList()
                //+ " VALUES "
                //+ proteinAliasTable.getSQLValueList(
                    //newKey,
                    //key,
                    //cbit.util.TokenMangler.getSQLEscapedString(names[i]),
                    //true,
                    //proteinAliasTable.COMPONENT);
        ////        System.out.println(sql);
        //cbit.vcell.modeldb.DbDriver.updateCleanSQL(con, sql);
    //}

    ////
    //// insert component aliases
    ////        
    //names = protein.getComponentAliases();
    //for (int i = 0; i < names.length; i++) {
        //KeyValue newKey = cbit.vcell.modeldb.DbDriver.getNewKey(con);
        //sql =
            //"INSERT INTO "
                //+ proteinAliasTable.getTableName()
                //+ " "
                //+ proteinAliasTable.getSQLColumnList()
                //+ " VALUES "
                //+ proteinAliasTable.getSQLValueList(
                    //newKey,
                    //key,
                    //cbit.util.TokenMangler.getSQLEscapedString(names[i]),
                    //true,
                    //proteinAliasTable.COMPONENT);
        ////        System.out.println(sql);
        //cbit.vcell.modeldb.DbDriver.updateCleanSQL(con, sql);
    //}

    ////
    //// insert keywords
    ////        
    //String[] keyWords = protein.getKeyWords();
    //if(keyWords != null){
	    //for (int i = 0; i < keyWords.length; i++) {
	        //KeyValue newKey = cbit.vcell.modeldb.DbDriver.getNewKey(con);
	        //sql =
	            //"INSERT INTO "
	                //+ proteinKeywordTable.getTableName()
	                //+ " "
	                //+ proteinKeywordTable.getSQLColumnList()
	                //+ " VALUES "
	                //+ proteinKeywordTable.getSQLValueList(
	                    //newKey,
	                    //key,
	                    //cbit.util.TokenMangler.getSQLEscapedString(keyWords[i]));
	        ////        System.out.println(sql);
	        //cbit.vcell.modeldb.DbDriver.updateCleanSQL(con, sql);
	    //}
    //}

}


/**
 * remove a Compound from the database.
 */
public void removeCompound(Connection con, CompoundInfo compound)
    throws SQLException {
    if (compound == null) {
        throw new IllegalArgumentException("Improper parameters for removeCompound");
    }
    log.print(
        "DictionaryDbDriver.removeCompound(compound=" + compound.getFormalID() + ")");
    //Connection con = conFact.getConnection();
    // first remove all its names...

    //removeCompounds(con);

    removeCompoundAlias(con, getCompoundFromKeggID(con, compound.getFormalID()).getDBFormalSpeciesKey());
    removeCompoundSQL(con, compound);
}


/**
 * remove the name and all aliases from the database based on the compound's keyvalue
 */
public void removeCompoundAlias(Connection con, KeyValue compoundRef)
    throws SQLException {
    if (compoundRef == null) {
        throw new IllegalArgumentException("Improper parameters for removeAlias");
    }
    log.print(
        "DictionaryDbDriver.removeCompoundAlias(compoundRef=" + compoundRef + ")");
    removeCompoundAliasSQL(con, compoundRef);
}


/**
 * remove all names and all aliases from the database
 */
public void removeCompoundAliases(Connection con) throws SQLException {
    if (con == null) {
        throw new IllegalArgumentException("Improper parameters for removeAliases");
    }
    log.print("DictionaryDbDriver.removeCompoundAliases()");
    removeCompoundAliasesSQL(con);
}


/**
 * Creates the SQL String that removes all names and all aliases from the DB
 */
private void removeCompoundAliasesSQL(Connection con) throws SQLException {

    //Delete all names for a compound based on the Compound's reference number
    String sql;
    sql = "DELETE FROM " + compoundAliasTable.getTableName();
    DbDriver.updateCleanSQL(con, sql);
}


/**
 * Creates the SQL String that removes a name and all aliases from the DB based on the compoundRef#
 */
private void removeCompoundAliasSQL(Connection con, KeyValue compoundRef)
    throws SQLException {
    if (compoundRef == null) {
        throw new IllegalArgumentException("Improper parameters for removeCompoundAliasSQL");
    }

    //Delete all names for a compound based on the Compound's reference number

    String sql;
    sql =
        "DELETE FROM "
            + compoundAliasTable.getTableName()
            + " WHERE "
            + compoundAliasTable.compoundRef
            + " = "
            + compoundRef;

    DbDriver.updateCleanSQL(con, sql);
}


/**
 * remove all Compounds from the DB
 */
public void removeCompounds(Connection con) throws SQLException {
    if (con == null) {
        throw new IllegalArgumentException("Improper parameters for removeCompounds");
    }
    log.print("DictionaryDbDriver.removeCompounds()");
    //Connection con = conFact.getConnection();
    //First remove all aliases...
    removeCompoundAliases(con);
    removeCompoundsSQL(con);
}


/**
 * Creates the SQL String to remove a compound from the DB based on keggID
 */
private void removeCompoundSQL(Connection con, CompoundInfo compound)
    throws SQLException {
    if (compound == null) {
        throw new IllegalArgumentException("Improper parameters for removeCompoundSQL");
    }

    //Delete a Compound from the DB based on keggID
    //First we must delete the name and all aliases...

    String sql;
    sql =
        "DELETE FROM "
            + compoundTable.getTableName()
            + " WHERE "
            + compoundTable.keggID
            + " = '"
            + compound.getFormalID()
            + "'";

    //System.out.println(sql);

    DbDriver.updateCleanSQL(con, sql);
}


/**
 * Creates the SQL String to remove all Compounds from the DB
 */
private void removeCompoundsSQL(Connection con) throws SQLException {

    //Delete all names for a compound based on the Compound's reference number

    String sql;
    sql = "DELETE FROM " + compoundTable.getTableName();

    //System.out.println(sql);

    DbDriver.updateCleanSQL(con, sql);
}


/**
 * remove an Enzyme from the database.
 */
public void removeEnzyme(Connection con, EnzymeInfo enzyme) throws SQLException {
    if (enzyme == null) {
        throw new IllegalArgumentException("Improper parameters for removeEnzyme");
    }
    log.print("DictionaryDbDriver.removeEnzyme(enzyme=" + enzyme.getFormalID() + ")");
    //Connection con = conFact.getConnection();
    // first remove all its names...

    //removeCompounds(con);

    removeEnzymeAlias(con, getEnzymeFromECNumber(con, enzyme.getFormalID()).getDBFormalSpeciesKey());
    removeEnzymeSQL(con, enzyme);
}


/**
 * remove the name and all aliases from the database based on the enzyme's keyvalue
 */
public void removeEnzymeAlias(Connection con, KeyValue enzymeRef)
    throws SQLException {
    if (enzymeRef == null) {
        throw new IllegalArgumentException("Improper parameters for removeEnzymeAlias");
    }
    log.print("DictionaryDbDriver.removeEnzymeAlias(enzymeRef=" + enzymeRef + ")");
    removeEnzymeAliasSQL(con, enzymeRef);
}


/**
 * remove all names and all aliases from the database
 */
public void removeEnzymeAliases(Connection con) throws SQLException {
    if (con == null) {
        throw new IllegalArgumentException("Improper parameters for removeEnzymeAliases");
    }
    log.print("DictionaryDbDriver.removeEnzymeAliases()");
    removeEnzymeAliasesSQL(con);
}


/**
 * Creates the SQL String that removes all Enzyme names and aliases from the DB
 */
private void removeEnzymeAliasesSQL(Connection con) throws SQLException {

    //Delete all names for an enzyme based on the Enzymes ECnumber
    String sql;
    sql = "DELETE FROM " + enzymeAliasTable.getTableName();
    //System.out.println("removeEnzymeAliasesSQL sql=" + sql);
    DbDriver.updateCleanSQL(con, sql);
}


/**
 * Creates the SQL String that removes a name and all aliases from the Enzyme DB based on the enzymeRef#
 */
private void removeEnzymeAliasSQL(Connection con, KeyValue enzymeRef)
    throws SQLException {
    if (enzymeRef == null) {
        throw new IllegalArgumentException("Improper parameters for removeEnzymeAliasSQL");
    }

    //Delete all names for an enzyme based on the Enzyme's reference number

    String sql;
    sql =
        "DELETE FROM "
            + enzymeAliasTable.getTableName()
            + " WHERE "
            + enzymeAliasTable.enzymeRef
            + " = "
            + enzymeRef;

    DbDriver.updateCleanSQL(con, sql);
}


/**
 * remove all Enzymes from the DB
 */
public void removeEnzymes(Connection con) throws SQLException {
    if (con == null) {
        throw new IllegalArgumentException("Improper parameters for removeEnzymes");
    }
    log.print("DictionaryDbDriver.removeEnzymes()");
    //First remove all aliases...
    removeEnzymeAliases(con);
    removeEnzymesSQL(con);
}


/**
 * Creates the SQL String to remove an enzyme from the DB based on ECNumber
 */
private void removeEnzymeSQL(Connection con, EnzymeInfo enzyme)
    throws SQLException {
    if (enzyme == null) {
        throw new IllegalArgumentException("Improper parameters for removeEnzymeSQL");
    }

    //Delete a Compound from the DB based on keggID
    //First we must delete the name and all aliases...

    String sql;
    sql =
        "DELETE FROM "
            + enzymeTable.getTableName()
            + " WHERE "
            + enzymeTable.ecNumber
            + " = '"
            + enzyme.getFormalID()
            + "'";

    //System.out.println(sql);

    DbDriver.updateCleanSQL(con, sql);
}


/**
 * Creates the SQL String to remove all Enzymes from the DB
 */
private void removeEnzymesSQL(Connection con) throws SQLException {

    //Delete all names for an enzyme based on the Enzyme's ECnumber

    String sql;
    sql = "DELETE FROM " + enzymeTable.getTableName();

    //System.out.println("removeEnzymeSQL sql=" + sql);

    DbDriver.updateCleanSQL(con, sql);
}


/**
 * remove a Protein from the database.
 */
public void removeProtein(Connection con, ProteinInfo protein)
    throws SQLException {
    if (protein == null) {
        throw new IllegalArgumentException("Improper parameters for removeProtein");
    }
    log.print(
        "DictionaryDbDriver.removeProtein(protein=" + protein.getFormalID() + ")");

    // first remove all its names...

    removeProteinAlias(con, getProteinFromSwissProtID(con, protein.getFormalID()).getDBFormalSpeciesKey());
    //removeProteinKeyword(con, getProteinFromSwissProtID(con, protein.getFormalID()).getDBFormalSpeciesKey());
    removeProteinSQL(con, protein);
}


/**
 * remove the name and all aliases from the database based on the protein's keyvalue
 */
public void removeProteinAlias(Connection con, KeyValue proteinRef)
    throws SQLException {
    if (proteinRef == null) {
        //throw new IllegalArgumentException("Improper parameters for removeProteinAlias");
    } else {
        log.print(
            "DictionaryDbDriver.removeProteinAlias(proteinRef=" + proteinRef + ")");
        removeProteinAliasSQL(con, proteinRef);
    }
}


/**
 * remove all names and all aliases from the database
 */
public void removeProteinAliases(Connection con) throws SQLException {
    if (con == null) {
        throw new IllegalArgumentException("Improper parameters for removeProteinAliases");
    }
    log.print("DictionaryDbDriver.removeProteinAliases()");
    removeProteinAliasesSQL(con);
}


/**
 * Creates the SQL String that removes all Protein names and aliases from the DB
 */
private void removeProteinAliasesSQL(Connection con) throws SQLException {

    //Delete all names for an enzyme based on the Enzymes ECnumber

    //for (int ch = 'Z'; ch >= 'A'; ch--) {
    //for (int ch2 = 'Z'; ch2 >= 'A'; ch2--) {

    String sql;
    sql = "DELETE FROM " + proteinAliasTable.getTableName();
    //+ " WHERE UPPER("
    //+ proteinAliasTable.name.getQualifiedColName()
    //+ ") LIKE '%"
    //+ (char) ch
    //+ ""
    //+ (char) ch2
    //+ "%"
    //+ "'";
    //System.out.println("removeProteinAliasesSQL sql=" + sql);
    DbDriver.updateCleanSQL(con, sql);
    //} // end for
    //} // end for
}


/**
 * Creates the SQL String that removes a name and all aliases from the Protein DB based on the proteinRef#
 */
private void removeProteinAliasSQL(Connection con, KeyValue proteinRef)
    throws SQLException {
    if (proteinRef == null) {
        throw new IllegalArgumentException("Improper parameters for removeProteinAliasSQL");
    }

    //Delete all names for a protein based on the Protein's reference number

    String sql;
    sql =
        "DELETE FROM "
            + proteinAliasTable.getTableName()
            + " WHERE "
            + proteinAliasTable.proteinRef
            + " = "
            + proteinRef;

    DbDriver.updateCleanSQL(con, sql);
}


/**
 * remove all Proteins from the DB
 */
public void removeProteins(Connection con) throws SQLException {
    if (con == null) {
        throw new IllegalArgumentException("Improper parameters for removeProteins");
    }
    //log.print("DictionaryDbDriver.removeProteins()");
    //First remove all aliases and keywords...
    removeProteinAliases(con);
    //removeProteinKeyWords(con);
    removeProteinsSQL(con);
}


/**
 * Creates the SQL String to remove a Protein from the DB based on swissProtID
 */
private void removeProteinSQL(Connection con, ProteinInfo protein)
    throws SQLException {
    if (protein == null) {
        throw new IllegalArgumentException("Improper parameters for removeProteinSQL");
    }

    //Delete a protein from the DB based on swissprotid
    //First we must delete the name and all aliases...

    String sql;
    sql =
        "DELETE FROM "
            + proteinTable.getTableName()
            + " WHERE "
            + proteinTable.accessionNumber
            + " = '"
            + protein.getFormalID()
            + "'";

    //System.out.println(sql);

    DbDriver.updateCleanSQL(con, sql);
}


/**
 * Creates the SQL String to remove all Proteins from the DB
 */
private void removeProteinsSQL(Connection con) throws SQLException {

    String sql;
    sql = "DELETE FROM " + proteinTable.getTableName();
    //System.out.println("removeProteinsSQL sql=" + sql);
    DbDriver.updateCleanSQL(con, sql);
}


/**
 * Update a compound in the database
 */
public void updateCompound(Connection con, CompoundInfo compound)
    throws SQLException {
    if (compound == null) {
        throw new SQLException("Improper parameters for updateCompound");
    }
    log.print(
        "DictionaryDbDriver.updateCompound(compound=" + compound.getFormalID() + ")");
    updateCompoundSQL(con, compound);
}


/**
 * Creates the SQL String to update a compound in the DB
 * @param con Connection
 * @param compound Compound
 * @exception SQLException.
 */
private void updateCompoundSQL(Connection con, CompoundInfo compound)
    throws SQLException {
    if (compound == null || con == null) {
        throw new IllegalArgumentException("Improper parameters for updateCompoundSQL");
    }

    String sql;
    sql =
        "UPDATE "
            + compoundTable.getTableName()
            + " SET "
            + compoundTable.getSQLUpdateList(compound)
            + " WHERE "
            + compoundTable.casID
            + " = "
            + compound.getCasID();

    DbDriver.updateCleanSQL(con, sql);
}


/**
 * Update an enzyme in the database
 */
public void updateEnzyme(Connection con, EnzymeInfo enzyme) throws SQLException {
    if (enzyme == null) {
        throw new SQLException("Improper parameters for updateEnzyme");
    }
    log.print("DictionaryDbDriver.updateEnzyme(enzyme=" + enzyme.getFormalID() + ")");
    updateEnzymeSQL(con, enzyme);
}


/**
 * Creates the SQL String to update an enzyme in the DB
 * @param con Connection
 * @param enzyme Enzyme
 * @exception SQLException.
 */
private void updateEnzymeSQL(Connection con, EnzymeInfo enzyme)
    throws SQLException {
    if (enzyme == null || con == null) {
        throw new IllegalArgumentException("Improper parameters for updateEnzymeSQL");
    }

    String sql;
    sql =
        "UPDATE "
            + enzymeTable.getTableName()
            + " SET "
            + enzymeTable.getSQLUpdateList(enzyme)
            + " WHERE "
            + enzymeTable.sysname
            + " = "
            + enzyme.getSysname();

    DbDriver.updateCleanSQL(con, sql);
}


/**
 * Update a protein in the database
 */
public void updateProtein(Connection con, ProteinInfo protein)
    throws SQLException {
    if (protein == null) {
        throw new SQLException("Improper parameters for updateProtein");
    }
    log.print(
        "DictionaryDbDriver.updateProtein(protein=" + protein.getFormalID() + ")");
    updateProteinSQL(con, protein);
}


/**
 * Creates the SQL String to update a protein in the DB
 * @param con Connection
 * @param protein Protein
 * @exception SQLException.
 */
private void updateProteinSQL(Connection con, ProteinInfo protein)
    throws SQLException {
    if (protein == null || con == null) {
        throw new IllegalArgumentException("Improper parameters for updateProteinSQL");
    }

    String sql;
    sql =
        "UPDATE "
            + proteinTable.getTableName()
            + " SET "
            + proteinTable.getSQLUpdateList(protein)
            + " WHERE "
            + proteinTable.accessionNumber
            + " = "
            + protein.getFormalID();

    DbDriver.updateCleanSQL(con, sql);
}
}
