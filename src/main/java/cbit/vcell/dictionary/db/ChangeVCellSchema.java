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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;

import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.sql.MysqlConnectionFactory;
import cbit.sql.MysqlKeyFactory;
import cbit.sql.OracleKeyFactory;
import cbit.sql.OraclePoolingConnectionFactory;
/**
 * Insert the type's description here.
 * Creation date: (2/7/2003 11:59:47 PM)
 * @author: Frank Morgan
 */
public class ChangeVCellSchema {

	private static boolean bPrintOnly = true;
/**
 * Insert the method's description here.
 * Creation date: (3/27/2003 3:44:16 PM)
 * @param s java.sql.Statement
 * @param fromTable java.lang.String
 * @param fromColumn java.lang.String
 * @param toTable java.lang.String
 */
private static void addOnDeleteCascade(Statement s, String fromTable, String fromColumn, String toTable) throws SQLException{
	
	String fkcn = findFKConstraintName(s,fromTable,fromColumn,toTable);
	if(fkcn == null){
		throw new RuntimeException("Could not find ForeignKeyConstraintName");
	}
	doUpdate("ALTER TABLE "+fromTable+" DROP CONSTRAINT "+fkcn,s);
	String sql = 	"ALTER TABLE "+fromTable + 
					" ADD (CONSTRAINT "+fromColumn+"_FKR FOREIGN KEY ("+fromColumn+") REFERENCES "+toTable+"(id) ON DELETE CASCADE)";
	doUpdate(sql,s);	
}
/**
 * Insert the method's description here.
 * Creation date: (2/8/2003 12:04:27 AM)
 */
private static void changeSchema(SessionLog log,ConnectionFactory conFactory,KeyFactory keyFactory) {
	
	
	String sql = null;
	try {
		Connection con = null;
		Object lock = new Object();
		try {
			con = conFactory.getConnection(lock);
			con.setAutoCommit(false);
			Statement s = null;
			//
			try {
				//
				s = con.createStatement();
				String scmt_sr_fkcn = findFKConstraintName(s,
						cbit.vcell.modeldb.SpeciesContextModelTable.table.tableName,
						cbit.vcell.modeldb.SpeciesContextModelTable.table.speciesRef.toString(),
						cbit.vcell.modeldb.SpeciesTable.table.tableName);
				if(scmt_sr_fkcn == null){
					throw new RuntimeException("Unexpected ResultSet for scmt_sr_fkcn");
				}
				//
				try{
					sql = "DROP TABLE "+DBSpeciesTable.table.getTableName() + " CASCADE CONSTRAINTS";
					doUpdate(sql,s);
				}catch(SQLException e){
					//ORA-00942: table or view does not exist
					if(e.getErrorCode() != 942){
						throw e;
					}
				}
				try{
					sql = "DROP TABLE vc_species_temp";
					doUpdate(sql,s);
				}catch(SQLException e){
					//ORA-00942: table or view does not exist
					if(e.getErrorCode() != 942){
						throw e;
					}
				}

				//
				//Delete all unreferenced Species
				//
				sql =	"DELETE FROM " + cbit.vcell.modeldb.SpeciesTable.table.getTableName() +
						" WHERE " + cbit.vcell.modeldb.SpeciesTable.table.id +" NOT IN " +
							"(SELECT DISTINCT " + cbit.vcell.modeldb.SpeciesContextModelTable.table.speciesRef +
								" FROM " + cbit.vcell.modeldb.SpeciesContextModelTable.table.getTableName() + ")";
				doUpdate(sql,s);
				con.commit();
				//
				// Drop ProteinKeywordTable (not needed anymore)
				//
				try{
					sql = "DROP TABLE vc_proteinkeyword";
					doUpdate(sql,s);
				}catch(SQLException e){
					//ORA-00942: table or view does not exist
					if(e.getErrorCode() != 942){
						throw e;
					}
				}

				// Drop 'initcond' column of SpeciesContextModelTable
				//
				sql = 	"ALTER TABLE "+cbit.vcell.modeldb.SpeciesContextModelTable.table.getTableName() + 
						" DROP COLUMN initcond";
				doUpdate(sql,s);
				// Drop 'diffrate' column of SpeciesContextModelTable
				//
				sql = 	"ALTER TABLE "+cbit.vcell.modeldb.SpeciesContextModelTable.table.getTableName() + 
						" DROP COLUMN diffrate";
				doUpdate(sql,s);
				
				// Drop 'type' column of ProteinAliasTable
				//
				sql = 	"ALTER TABLE "+ProteinAliasTable.table.getTableName() + 
						" DROP COLUMN type";
				doUpdate(sql,s);
				//
				// Add keyword column to ProteinTable
				//
				sql = 	"ALTER TABLE "+ProteinTable.table.getTableName() + " ADD (" +
							ProteinTable.table.keywords.getUnqualifiedColName() + " " +
							ProteinTable.table.keywords.getSqlType() + " " +
							ProteinTable.table.keywords.getSqlConstraints() +
						")";
				doUpdate(sql,s);
				//
				// Add description column to ProteinTable
				//
				sql = 	"ALTER TABLE "+ProteinTable.table.getTableName() + " ADD (" +
							ProteinTable.table.description.getUnqualifiedColName() + " " +
							ProteinTable.table.description.getSqlType() + " " +
							ProteinTable.table.description.getSqlConstraints() +
						")";
				doUpdate(sql,s);
				
				//
				// Add casid column to EnzymeTable
				//
				sql = 	"ALTER TABLE "+EnzymeTable.table.getTableName() + " ADD (" +
							EnzymeTable.table.casID.getUnqualifiedColName() + " " +
							EnzymeTable.table.casID.getSqlType() + " " +
							EnzymeTable.table.casID.getSqlConstraints() +
						")";
				doUpdate(sql,s);
				//
				// Change ecnumber column to remove leading 'EC '
				//
				sql = 	"UPDATE "+EnzymeTable.table.getTableName()+" SET ecnumber=substr(ecnumber,4)";
				doUpdate(sql,s);
				con.commit();
				//
				// Recode true/false to T/F
				//
				sql = 	"UPDATE "+CompoundAliasTable.table.getTableName()+" SET preferred=DECODE(UPPER(preferred),'FALSE','F','TRUE','T')";
				doUpdate(sql,s);
				con.commit();
				sql = 	"UPDATE "+EnzymeAliasTable.table.getTableName()+" SET preferred=DECODE(UPPER(preferred),'FALSE','F','TRUE','T')";
				doUpdate(sql,s);
				con.commit();
				sql = 	"UPDATE "+ProteinAliasTable.table.getTableName()+" SET preferred=DECODE(UPPER(preferred),'FALSE','F','TRUE','T')";
				doUpdate(sql,s);
				con.commit();
				//
				// Change Table column sizes---------------------------------------
				//
				sql = 	"ALTER TABLE "+CompoundTable.table.getTableName()+" MODIFY ("+
							CompoundTable.table.formula.toString()+" VARCHAR2(256)"+","+
							CompoundTable.table.casID.toString()+" VARCHAR2(256)"+","+
							CompoundTable.table.keggID.toString()+" VARCHAR2(32)"+
						")";
				doUpdate(sql,s);
				sql = 	"ALTER TABLE "+CompoundAliasTable.table.getTableName()+" MODIFY ("+
							CompoundAliasTable.table.name.toString()+" VARCHAR2(256)"+//","+
							//CompoundAliasTable.table.preferred.toString()+" VARCHAR2(1)"+
						")";
				doUpdate(sql,s);
				sql = 	"ALTER TABLE "+EnzymeTable.table.getTableName()+" MODIFY ("+
							EnzymeTable.table.reaction.toString()+" VARCHAR2(512)"+","+
							EnzymeTable.table.ecNumber.toString()+" VARCHAR2(32)"+","+
							EnzymeTable.table.sysname.toString()+" VARCHAR2(512)"+","+
							EnzymeTable.table.casID.toString()+" VARCHAR2(256)"+
						")";
				doUpdate(sql,s);
				sql = 	"ALTER TABLE "+EnzymeAliasTable.table.getTableName()+" MODIFY ("+
							EnzymeAliasTable.table.name.toString()+" VARCHAR2(256)"+//","+
							//EnzymeAliasTable.table.preferred.toString()+" VARCHAR2(1)"+
						")";
				doUpdate(sql,s);
				sql = 	"ALTER TABLE "+ProteinTable.table.getTableName()+" MODIFY ("+
							ProteinTable.table.organism.toString()+" VARCHAR2(1024)"+","+
							ProteinTable.table.accessionNumber.toString()+" VARCHAR2(1024)"+","+
							ProteinTable.table.swissProtEntryName.toString()+" VARCHAR2(32)"+","+
							ProteinTable.table.keywords.toString()+" VARCHAR2(1024)"+","+
							ProteinTable.table.description.toString()+" VARCHAR2(1024)"+
						")";
				doUpdate(sql,s);
				sql = 	"ALTER TABLE "+ProteinAliasTable.table.getTableName()+" MODIFY ("+
							ProteinAliasTable.table.name.toString()+" VARCHAR2(256)"+//","+
							//ProteinAliasTable.table.preferred.toString()+" VARCHAR2(1)"+
						")";
				doUpdate(sql,s);

				//
				//
				// Add new constraints that old tables should have had
				// (Adding NOT NULL) - ALTER TABLE table_name MODIFY column_name CONSTRAINT constraint_name NOT NULL;
				// (Adding Others  ) - ALTER TABLE table_name ADD CONSTRAINT constraint_name UNIQUE(ecnumber);
				//
				
				//
				// Add UNIQUE constraint to CompoundTable.keggID
				sql = 	"ALTER TABLE "+CompoundTable.table.getTableName() + 
						" ADD CONSTRAINT " + CompoundTable.table.getTableName()+"_"+CompoundTable.table.keggID.getUnqualifiedColName()+"_UNIQUE" +
						" UNIQUE("+CompoundTable.table.keggID.getUnqualifiedColName()+")";
				doUpdate(sql,s);
				//
				// Add NOT NULL constraint to CompoundTable.keggID
				sql = 	"ALTER TABLE "+CompoundTable.table.getTableName() + 
						" MODIFY " + CompoundTable.table.keggID.getUnqualifiedColName() +
						" CONSTRAINT "+CompoundTable.table.getTableName()+"_"+CompoundTable.table.keggID.getUnqualifiedColName()+"_NOTNULL" +
						" NOT NULL";
				doUpdate(sql,s);
				//
				//Add ON DELETE CASCADE
				addOnDeleteCascade(s,CompoundAliasTable.table.tableName,CompoundAliasTable.table.compoundRef.toString(),CompoundTable.table.tableName);
				// Add NOT NULL constraint to CompoundAliasTable.compoundRef
				sql = 	"ALTER TABLE "+CompoundAliasTable.table.getTableName() + 
						" MODIFY " + CompoundAliasTable.table.compoundRef.getUnqualifiedColName() +
						" CONSTRAINT "+CompoundAliasTable.table.compoundRef.getUnqualifiedColName()+"_NOTNULL" +
						" NOT NULL";
				doUpdate(sql,s);
				//
				//Add ON DELETE CASCADE
				addOnDeleteCascade(s,EnzymeAliasTable.table.tableName,EnzymeAliasTable.table.enzymeRef.toString(),EnzymeTable.table.tableName);
				// Add NOT NULL constraint to EnzymeAliasTable.enzymeRef
				sql = 	"ALTER TABLE "+EnzymeAliasTable.table.getTableName() + 
						" MODIFY " + EnzymeAliasTable.table.enzymeRef.getUnqualifiedColName() +
						" CONSTRAINT "+EnzymeAliasTable.table.enzymeRef.getUnqualifiedColName()+"_NOTNULL" +
						" NOT NULL";
				doUpdate(sql,s);
				//
				//Add ON DELETE CASCADE
				addOnDeleteCascade(s,ProteinAliasTable.table.tableName,ProteinAliasTable.table.proteinRef.toString(),ProteinTable.table.tableName);
				// Add NOT NULL constraint to ProteinAliasTable.proteinRef
				sql = 	"ALTER TABLE "+ProteinAliasTable.table.getTableName() + 
						" MODIFY " + ProteinAliasTable.table.proteinRef.getUnqualifiedColName() +
						" CONSTRAINT "+ProteinAliasTable.table.proteinRef.getUnqualifiedColName()+"_NOTNULL" +
						" NOT NULL";
				doUpdate(sql,s);

				
				//
				// Add UNIQUE constraint to EnzymeTable.ecNumber
				sql = 	"ALTER TABLE "+EnzymeTable.table.getTableName() + 
						" ADD CONSTRAINT " + EnzymeTable.table.getTableName()+"_"+EnzymeTable.table.ecNumber.getUnqualifiedColName()+"_UNIQUE" +
						" UNIQUE("+EnzymeTable.table.ecNumber.getUnqualifiedColName()+")";
				doUpdate(sql,s);

				
				//
				// Add UNIQUE constraint to ProteinTable.swissProtEntryName
				sql = 	"ALTER TABLE "+ProteinTable.table.getTableName() + 
						" ADD CONSTRAINT " + ProteinTable.table.getTableName()+"_"+ProteinTable.table.swissProtEntryName.getUnqualifiedColName()+"_UNIQUE" +
						" UNIQUE("+ProteinTable.table.swissProtEntryName.getUnqualifiedColName()+")";
				doUpdate(sql,s);
				//
				// Add NOT NULL constraint to ProteinTable.swissProtEntryName
				sql = 	"ALTER TABLE "+ProteinTable.table.getTableName() + 
						" MODIFY " + ProteinTable.table.swissProtEntryName.getUnqualifiedColName() +
						" CONSTRAINT "+ProteinTable.table.getTableName()+"_"+ProteinTable.table.swissProtEntryName.getUnqualifiedColName()+"_NOTNULL" +
						" NOT NULL";
				doUpdate(sql,s);


				
				//
				// Create New Tables
				//

				// Create NEW DBSpeciesTable
				sql = DBSpeciesTable.table.getCreateSQL();
				doUpdate(sql,s);
				
				// Create Temp SpeciesTable
				sql = cbit.vcell.modeldb.SpeciesTable.table.getCreateSQL();
				int index = sql.indexOf(cbit.vcell.modeldb.SpeciesTable.table.getTableName());
				sql = 	sql.substring(0,index)+
							"vc_species_temp"+
							sql.substring(index+cbit.vcell.modeldb.SpeciesTable.table.getTableName().length());
				doUpdate(sql,s);

				//Delete all vc_speciesreference that are not referenced by vc_species
				sql = 	"DELETE FROM vc_speciesreference WHERE" +
						" id NOT IN (SELECT speciesrefref FROM vc_species WHERE speciesrefref IS NOT NULL)";
				doUpdate(sql,s);

				//Set SpeciesTable.speciesRefRef to null where SpeciesReference points to nothing
				sql = 	"UPDATE vc_species SET speciesrefref = null WHERE speciesrefref IN " +
						" (SELECT id FROM vc_speciesreference WHERE compoundref IS NULL AND enzymeRef IS NULL AND proteinRef IS NULL)";
				doUpdate(sql,s);
				
				//Delete all-null vc_speciesreference entries
				sql = "DELETE FROM vc_speciesreference WHERE compoundref IS NULL AND enzymeRef IS NULL AND proteinRef IS NULL";
				doUpdate(sql,s);
				con.commit();
				
				//
				//
				createNewDBSpeciesAndFixSpeciesRefRef(s,keyFactory,con);
				con.commit();
				//
				//
				
				//Copy info from SpeciesTable to SpeciesTableTemp
				sql = "INSERT INTO vc_species_temp" +
						" SELECT " +
							"id" + "," +
							"commonname" + "," +
							//"ownerref" + "," +
							"speciesrefref" + "," + //becomes dbSpeciesRef
							"name" + //becomes annotation
						" FROM " + cbit.vcell.modeldb.SpeciesTable.table.getTableName();
				doUpdate(sql,s);

				//Temporarily drop SpeciesContextModelTable speciesRef constraint
				//because a table cannot be dropped if there are any constraints defined on it
				sql = "ALTER TABLE "+cbit.vcell.modeldb.SpeciesContextModelTable.table.getTableName() +
						" DROP CONSTRAINT " + scmt_sr_fkcn;
				doUpdate(sql,s);

				//Drop SpeciesTable
				sql= "DROP TABLE " + cbit.vcell.modeldb.SpeciesTable.table.getTableName();
				doUpdate(sql,s);

				//Move SpeciesTemp table to SpeciesTable
				sql = "RENAME vc_species_temp TO " + cbit.vcell.modeldb.SpeciesTable.table.getTableName();
				doUpdate(sql,s);

				//Add back speciesRef constraint to SpeciesContextModelTable
				cbit.vcell.modeldb.SpeciesContextModelTable scmt = cbit.vcell.modeldb.SpeciesContextModelTable.table;
				cbit.vcell.modeldb.SpeciesTable st = cbit.vcell.modeldb.SpeciesTable.table;
				sql = 	"ALTER TABLE " + scmt.getTableName() +
						" ADD FOREIGN KEY ("+scmt.speciesRef+") REFERENCES " + st.getTableName() + "("+st.id+")";
				doUpdate(sql,s);

				//Drop SpeciesReferenceTable
				sql = "DROP TABLE vc_speciesreference";
				doUpdate(sql,s);

				//Add hasOverride column to SpeciesContextModelTable
				sql =	"ALTER TABLE " + cbit.vcell.modeldb.SpeciesContextModelTable.table.getTableName() +
						" ADD (" + 
							cbit.vcell.modeldb.SpeciesContextModelTable.table.hasOverride.toString() + " " +
							cbit.vcell.modeldb.SpeciesContextModelTable.table.hasOverride.getSqlType() + ")";
				doUpdate(sql,s);
				//Populate with hasOverride 'true'
				sql =	"UPDATE " + cbit.vcell.modeldb.SpeciesContextModelTable.table.getTableName() +
						" SET " + cbit.vcell.modeldb.SpeciesContextModelTable.table.hasOverride.toString()+"='T'";
				doUpdate(sql,s);
				//Set hasOverride false for SpeciesContext Names that match 'speciesName_structureName'
				sql =	"UPDATE " + cbit.vcell.modeldb.SpeciesContextModelTable.table.getTableName() +
						" SET " +cbit.vcell.modeldb.SpeciesContextModelTable.table.hasOverride.toString() + "='F'" +
						" WHERE " + cbit.vcell.modeldb.SpeciesContextModelTable.table.id.toString() + " IN " +
						"(" +
							" SELECT " + cbit.vcell.modeldb.SpeciesContextModelTable.table.id.getQualifiedColName() +
							" FROM " +
								cbit.vcell.modeldb.SpeciesContextModelTable.table.getTableName() + "," +
								cbit.vcell.modeldb.SpeciesTable.table.getTableName() + "," +
								cbit.vcell.modeldb.StructTable.table.getTableName() +
							" WHERE " +
								cbit.vcell.modeldb.SpeciesContextModelTable.table.speciesRef.getQualifiedColName() +
								"=" +
								cbit.vcell.modeldb.SpeciesTable.table.id.getQualifiedColName() +
								" AND " +
								cbit.vcell.modeldb.SpeciesContextModelTable.table.structRef.getQualifiedColName() +
								"=" +
								cbit.vcell.modeldb.StructTable.table.id.getQualifiedColName() +
								" AND " +
								cbit.vcell.modeldb.SpeciesContextModelTable.table.name.getQualifiedColName() +
								"=" +
								"(" +
									cbit.vcell.modeldb.SpeciesTable.table.commonName.getQualifiedColName() +
									" || '_' || " +
									cbit.vcell.modeldb.StructTable.table.strName.getQualifiedColName() +
								")" +									
						")";
				doUpdate(sql,s);
				//Add NOT NULL constraint to hasOverride column for SpeciesContextModelTable
				sql =	"ALTER TABLE " + cbit.vcell.modeldb.SpeciesContextModelTable.table.getTableName() +
						" MODIFY (" + cbit.vcell.modeldb.SpeciesContextModelTable.table.hasOverride.toString() + " NOT NULL)";
				doUpdate(sql,s);
				
				//
				fixBadSpeciesNames(s);
				con.commit();
				
				//
				//fixPermissions(s);
				//con.commit();
				//
			}finally {
				s.close();
			}

			con.commit();
			
		} catch (SQLException exc) {
			con.rollback();
			System.out.println(exc.getMessage());
			exc.printStackTrace(System.out);
		} finally {
			conFactory.release(con, lock);
		}
		
	} catch (Throwable exc) {
		System.out.println(exc.getMessage());
		exc.printStackTrace(System.out);
	}finally{
		System.exit(0);
	}
		
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/2003 2:05:22 PM)
 */
private static void createNewDBSpeciesAndFixSpeciesRefRef(Statement stmt,KeyFactory keyFactory,Connection con) throws SQLException {

	//This creates new DBSpecies table and
	//fixes the problem where multiple speciesreferences
	//have the same compound,enzyme,protein reference. (not allowed in new scheme).
	//Note: will throw away the vc_speciesreference name and annotation
	//

	//get a list of all species and associate their speciesreferences
	String sql =
		"SELECT vc_species.id speciesid ,vc_species.speciesrefref,vc_speciesreference.compoundref,vc_speciesreference.enzymeRef,vc_speciesreference.proteinRef"+
		" FROM vc_speciesreference,vc_species"+
		" WHERE vc_species.speciesrefref IS NOT NULL AND vc_species.speciesrefref=vc_speciesreference.id";

	Hashtable speciesID_to_firstSpeciesRefID = new Hashtable();
	Hashtable refRefs_to_speciesReferenceID = new Hashtable();
	//
	Vector uniqueSpeciesRefIDs = new Vector();
	Vector compoundRefs = new Vector();
	Vector enzymeRefs = new Vector();
	Vector proteinRefs = new Vector();
	boolean bCompoundRef;
	boolean bEnzymeRef;
	boolean bProteinRef;
	//
	int speciesWithSpeciesRefRefCount = 0;
	//
	ResultSet rset = null;
	try{
		rset = doQuery(sql,stmt);
		while(rset.next()){
			//
			speciesWithSpeciesRefRefCount+= 1;
			//
			bCompoundRef = false;
			bEnzymeRef = false;
			bProteinRef = false;			
			//
			java.math.BigDecimal refRef = null;
			refRef = rset.getBigDecimal("compoundref");
			if(refRef == null){
				refRef = rset.getBigDecimal("enzymeRef");
				if(refRef == null){
					refRef = rset.getBigDecimal("proteinRef");
					if(refRef == null){
						throw new RuntimeException("All references null");
					}else{
						bProteinRef = true;
					}
				}else{
					bEnzymeRef = true;
				}
			}else{
				bCompoundRef = true;
			}
			java.math.BigDecimal speciesReferenceID = rset.getBigDecimal("speciesrefref");
			java.math.BigDecimal speciesID = rset.getBigDecimal("speciesid");
			//
			if(!refRefs_to_speciesReferenceID.containsKey(refRef)){
				//Save only the first speciesreference reference we find
				refRefs_to_speciesReferenceID.put(refRef,speciesReferenceID);
				uniqueSpeciesRefIDs.add(speciesReferenceID);
				//put speciesreferences reference or null in list for each species id
				if(bCompoundRef){
					compoundRefs.add(refRef);
				}else{
					compoundRefs.add(null);
				}
				if(bEnzymeRef){
					enzymeRefs.add(refRef);
				}else{
					enzymeRefs.add(null);
				}
				if(bProteinRef){
					proteinRefs.add(refRef);
				}else{
					proteinRefs.add(null);
				}
			}else{
				//Save the "bad" species so they can be fixed
				//this species was pointing to a speciesreference that had duplicate references
				speciesID_to_firstSpeciesRefID.put(speciesID,refRefs_to_speciesReferenceID.get(refRef));
			}
		}
	}finally{
		if(rset != null){
			rset.close();
		}
	}
	//
	//Update "bad" SpeciesTable.speciesrefref to point to first duplicate speciesReference only
	//
	Enumeration speciesIDs = speciesID_to_firstSpeciesRefID.keys();
	while(speciesIDs.hasMoreElements()){
		java.math.BigDecimal speciesID = (java.math.BigDecimal)speciesIDs.nextElement();
		java.math.BigDecimal firstSpeciesRef = (java.math.BigDecimal)speciesID_to_firstSpeciesRefID.get(speciesID);
		sql = " UPDATE vc_species SET speciesrefref = "+firstSpeciesRef.toString()+" WHERE id="+speciesID.toString();
		doUpdate(sql,stmt);
	}
	//
	//Populate DBSpeciesTable with the duplicate references removed
	//
	for(int i = 0;i < uniqueSpeciesRefIDs.size();i+=1){
		sql = 	"INSERT INTO " + DBSpeciesTable.table.getTableName() + 
				" VALUES ("+
					uniqueSpeciesRefIDs.get(i).toString()+","+
					(compoundRefs.get(i) != null?compoundRefs.get(i).toString():"null")+","+
					(enzymeRefs.get(i) != null?enzymeRefs.get(i).toString():"null")+","+
					(proteinRefs.get(i) != null?proteinRefs.get(i).toString():"null")+
					")";
				
		doUpdate(sql,stmt);
	}
	//
	System.out.println(	"Species with speiesrefref="+speciesWithSpeciesRefRefCount+
						" Species needing duplicate altered="+speciesID_to_firstSpeciesRefID.size()+
						" Unique DBSpecies="+uniqueSpeciesRefIDs.size()
			);
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2003 2:35:04 PM)
 * @param sql java.lang.String
 */
private static ResultSet doQuery(String sql,Statement stmt) throws SQLException{

	System.out.println(sql);
	ResultSet rset = null;
	//if(!bPrintOnly){
		rset = stmt.executeQuery(sql);
	//}
	return rset;
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2003 2:35:04 PM)
 * @param sql java.lang.String
 */
private static void doUpdate(String sql,Statement stmt) throws SQLException{

	System.out.println(sql);
	
	if(!bPrintOnly){
		stmt.executeUpdate(sql);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2003 3:09:41 PM)
 * @param fromTable java.lang.String
 * @param toTable java.lang.String
 */
private static String findFKConstraintName(Statement stmt,String fromTable,String fromColumn,String toTable) throws SQLException{

	String results = null;
	String sql = "SELECT user_constraints.CONSTRAINT_NAME FROM user_constraints,user_cons_columns "+
				"WHERE user_constraints.R_CONSTRAINT_NAME = "+
					"(select CONSTRAINT_NAME from user_constraints where table_name = "+
					"'"+toTable.toUpperCase()+"' AND constraint_type = 'P') "+
				" AND constraint_type = 'R'"+
				" AND user_constraints.table_name = '"+fromTable.toUpperCase()+"'"+
				" AND user_cons_columns.CONSTRAINT_NAME = user_constraints.CONSTRAINT_NAME"+
				" AND user_cons_columns.COLUMN_NAME = '"+fromColumn.toUpperCase()+"'";
	ResultSet rset = null;
	try{
		rset = doQuery(sql,stmt);
		if(rset.next()){
			results = rset.getString("CONSTRAINT_NAME");
		}
	}finally{
		if(rset != null){
			rset.close();
		}
	}
	System.out.println("ForeignKeyConstraintName="+results);
	return results;
}
/**
 * Insert the method's description here.
 * Creation date: (5/6/2003 1:26:59 PM)
 */
private static void fixBadSpeciesNames(Statement stmt) throws SQLException{

	System.out.println("-----Fix Bad Species Names-----");
	String tempspnameTableName = "tempspname";
	String sql = null;
	//
	try{
		sql = "DROP TABLE "+tempspnameTableName;
		doUpdate(sql,stmt);
	}catch(SQLException e){
		//ORA-00942: table or view does not exist
		if(e.getErrorCode() != 942){
			throw e;
		}
	}
	//
	sql =
    "CREATE TABLE "+tempspnameTableName+" (sptableid NUMBER UNIQUE NOT NULL,oldspname VARCHAR2(255) NOT NULL,newspname VARCHAR2(255) NOT NULL)";
	doUpdate(sql,stmt);
	System.out.println("Saving Original species id and names with corected names into "+tempspnameTableName+".  should remove");
	//
	ResultSet rset = null;
	//
	//All species
	//
	sql = 
		"SELECT " +
			cbit.vcell.modeldb.SpeciesTable.table.commonName.getQualifiedColName()+","+
			cbit.vcell.modeldb.SpeciesTable.table.id.getQualifiedColName()+
		" FROM "+
			cbit.vcell.modeldb.SpeciesTable.table.getTableName();
	//
	rset = doQuery(sql,stmt);
	Vector fixedSpeciesIDV = new Vector();
	Vector fixedSpeciesNameV = new Vector();
	Vector originalSpeciesNameV = new Vector();
	int fixCount = 0;
	//find bad species names and fix
	if(rset != null){
		while(rset.next()){
			String speciesName = rset.getString(cbit.vcell.modeldb.SpeciesTable.table.commonName.toString());
			String fixedSpeciesName = org.vcell.util.TokenMangler.fixTokenStrict(speciesName);
			if(!speciesName.equals(fixedSpeciesName)){
				fixedSpeciesIDV.add(rset.getBigDecimal(cbit.vcell.modeldb.SpeciesTable.table.id.toString()));
				String pad = "";
				if(fixCount < 100){pad="0";}
				if(fixCount < 10){pad="00";}
				fixedSpeciesNameV.add("fixed"+pad+fixCount+"_"+fixedSpeciesName);
				originalSpeciesNameV.add(speciesName);
				fixCount+= 1;
			}
		}
		rset.close();
	}
	System.out.println("bad species count="+fixCount);
	//
	for(int i =0;i<fixedSpeciesIDV.size();i+= 1){
		//
		String fixedSpeciesName = (String)fixedSpeciesNameV.get(i);
		String origSpeciesName = (String)originalSpeciesNameV.get(i);
		//
		sql = 
			"INSERT INTO "+tempspnameTableName + " VALUES " +
			"("+
			((java.math.BigDecimal)fixedSpeciesIDV.get(i)).toString()+","+
			"'"+org.vcell.util.TokenMangler.getEscapedString(origSpeciesName)+"'"+","+
			"'"+org.vcell.util.TokenMangler.getEscapedString(fixedSpeciesName)+"'"+
			")";
			
		doUpdate(sql,stmt);
		//
		sql =
			"UPDATE "+cbit.vcell.modeldb.SpeciesTable.table.getTableName()+
			" SET "+cbit.vcell.modeldb.SpeciesTable.table.commonName.toString()+"="+"'"+fixedSpeciesName+"'"+
			" WHERE "+cbit.vcell.modeldb.SpeciesTable.table.id.toString()+"="+((java.math.BigDecimal)fixedSpeciesIDV.get(i)).toString();
		doUpdate(sql,stmt);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/8/2003 12:00:03 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	
	
    try {
        if (args.length != 6) {
            System.out.println("\nUsage: (oracle|mysql) host databaseSID schemaUser schemaUserPassword bPrintOnly\n\n");
            System.exit(0);
        }
        String driverName = "oracle.jdbc.driver.OracleDriver";
        String host = args[1];
        String db = args[2];
        String connectURL = "jdbc:oracle:thin:@" + host + ":1521:" + db;
        String dbSchemaUser = args[3];
        String dbPassword = args[4];
        bPrintOnly = Boolean.valueOf(args[5]).booleanValue();
        
        //
        int ok =
            javax.swing.JOptionPane.showConfirmDialog(
                new javax.swing.JFrame(),
                (!bPrintOnly?"WARNING!\nSCHEMA below will be ALTERED\n":"\nSCHEMA below will not be ALTERED\n")
                    + "connectURL="
                    + connectURL
                    + "\nUser="
                    + dbSchemaUser
                    + "\npassword="
                    + dbPassword
                    + "\nPrint Only=" + bPrintOnly,
                "Confirm",
                javax.swing.JOptionPane.OK_CANCEL_OPTION,
                javax.swing.JOptionPane.WARNING_MESSAGE);
        if (ok != javax.swing.JOptionPane.OK_OPTION) {
            throw new RuntimeException("Aborted by user");
        }

        SessionLog log = new StdoutSessionLog("ChangeVCellSchema");
        ConnectionFactory conFactory = null;
        KeyFactory keyFactory = null;
        new cbit.vcell.resource.PropertyLoader();
        if (args[0].equalsIgnoreCase("ORACLE")) {
            conFactory =
                new OraclePoolingConnectionFactory(
                    log,
                    driverName,
                    connectURL,
                    dbSchemaUser,
                    dbPassword);
            keyFactory = new OracleKeyFactory();
        } else if (args[0].equalsIgnoreCase("MYSQL")) {
                conFactory = new MysqlConnectionFactory();
                keyFactory = new MysqlKeyFactory();
		} else {
                System.out.println(
                    "Usage: (oracle|mysql) host databaseSID schemaUser schemaUserPassword");
                System.exit(1);
		}
        changeSchema(log, conFactory, keyFactory);
    } catch (Throwable e) {
        e.printStackTrace(System.out);
    }
    System.exit(0);	
	
	
}
}
