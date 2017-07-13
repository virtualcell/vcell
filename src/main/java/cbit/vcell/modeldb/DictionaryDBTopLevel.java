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
import java.sql.Connection;
import java.sql.SQLException;

import org.vcell.db.ConnectionFactory;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.dictionary.CompoundInfo;
import cbit.vcell.dictionary.EnzymeInfo;
import cbit.vcell.dictionary.FormalCompound;
import cbit.vcell.dictionary.FormalEnzyme;
import cbit.vcell.dictionary.FormalProtein;
import cbit.vcell.dictionary.ProteinInfo;
import cbit.vcell.model.DBFormalSpecies;
import cbit.vcell.model.DBSpecies;
import cbit.vcell.model.FormalSpeciesType;
import cbit.vcell.model.ReactionDescription;
import cbit.vcell.model.ReactionQuerySpec;
import cbit.vcell.model.ReactionStepInfo;
/**
 * This type was created in VisualAge.
 */
public class DictionaryDBTopLevel extends AbstractDBTopLevel{
    private DictionaryDbDriver dictionaryDB = null;
    private ReactStepDbDriver reactStepDB = null;

    /**
     * DictionaryDBTopLevel constructor.
     * Creates a new DictionaryDBTopLevel object
     */
    DictionaryDBTopLevel(ConnectionFactory aConFactory, SessionLog newLog) throws SQLException {
	super(aConFactory,newLog);
	this.dictionaryDB = new DictionaryDbDriver(log);

	this.reactStepDB = new ReactStepDbDriver(null,this.log,this.dictionaryDB);
		ModelDbDriver modelDB = new ModelDbDriver(this.reactStepDB,this.log);
		this.reactStepDB.init(modelDB);

}


/**
 * This method will clear all entries from both the Compound Alias table as well as the Compound table itself
 * @param bEnableRetry boolean
 * @exception java.sql.SQLException The exception description.
 * Creation date: (7/1/2002 9:50:51 AM)
 */
void clearCompoundDBEntries(boolean bEnableRetry) throws java.sql.SQLException {

    Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        dictionaryDB.removeCompounds(con);
        con.commit();
	} catch (Throwable e) {
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			clearCompoundDBEntries(false);
		}else{
			handle_SQLException(e);
		}
    } finally {
        conFactory.release(con, lock);
    }
}


/**
 * This method will clear all entries from both the Enzyme Alias table as well as the Enzyme table itself
 * @param bEnableRetry boolean
 * @exception java.sql.SQLException The exception description.
 * Creation date: (7/1/2002 9:50:51 AM)
 */
void clearEnzymeDBEntries(boolean bEnableRetry) throws java.sql.SQLException {

    Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        dictionaryDB.removeEnzymes(con);
        con.commit();
	} catch (Throwable e) {
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			clearEnzymeDBEntries(false);
		}else{
			handle_SQLException(e);
		}
    } finally {
        conFactory.release(con, lock);
    }
}


/**
 * This method will clear all entries from both the Protein Alias table as well as the Protein table itself
 * @param bEnableRetry boolean
 * @exception java.sql.SQLException The exception description.
 * Creation date: (7/1/2002 9:50:51 AM)
 */
void clearProteinDBEntries(boolean bEnableRetry) throws java.sql.SQLException {

    Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        dictionaryDB.removeProteins(con);
        con.commit();
	} catch (Throwable e) {
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			clearProteinDBEntries(false);
		}else{
			handle_SQLException(e);
		}
    } finally {
        conFactory.release(con, lock);
    }
}


/**
 * returns the number of records in the specified table
 * @return Compound
 * @param dbLink DBLink
 * @param bVersion boolean
 * @exception java.sql.SQLException The exception description.
 */
int countTableEntries(String tableName, boolean bEnableRetry)
    throws java.sql.SQLException {
   
    Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        return dictionaryDB.countTableEntries(con, tableName);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return countTableEntries(tableName, false);
		}else{
			handle_SQLException(e);
			return 0; // never gets here;
		}
    } finally {
        conFactory.release(con, lock);
    }
}


/**
 * Insert the method's description here.
 * Creation date: (2/26/2003 3:28:25 PM)
 */
DBSpecies getBoundSpecies(DBFormalSpecies dbfs,boolean bEnableRetry) throws java.sql.SQLException{
	
    Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        return dictionaryDB.getBoundSpecies(con, dbfs);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getBoundSpecies(dbfs,false);
		}else{
			handle_SQLException(e);
			return null; // never gets here;
		}
    } finally {
        conFactory.release(con, lock);
    }
	}


/**
 * Gets an array of all names and aliases in the DB
 * @return String[]
 * @param bVersion boolean
 * @exception java.sql.SQLException The exception description.
 */
String[] getCompoundAliases(boolean bEnableRetry, String filter) throws java.sql.SQLException {

    Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        return dictionaryDB.getCompoundAliases(con, filter);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getCompoundAliases (false, filter);
		}else{
			handle_SQLException(e);
			return null; // never gets here;
		}
    } finally {
        conFactory.release(con, lock);
    }
}


/**
 * gets a Species from the DB based on the dbID given as a parameter
 * @return Species
 * @param dbID String
 * @param bVersion boolean
 * @exception java.sql.SQLException The exception description.
 */
FormalCompound getCompoundFromKeggID(String dbID, boolean bEnableRetry)
    throws java.sql.SQLException {

    Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        FormalCompound result = dictionaryDB.getCompoundFromKeggID(con, dbID);
        return result;
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getCompoundFromKeggID(dbID, false);
		}else{
			handle_SQLException(e);
			return null; // never gets here;
		}
    } finally {
        conFactory.release(con, lock);
    }
}


/**
 * Insert the method's description here.
 * Creation date: (2/20/2003 2:06:57 PM)
 */
DBFormalSpecies[] getDatabaseSpecies(boolean bEnableRetry,User argUser,String likeString,boolean isBound,FormalSpeciesType speciesType,int restrictSearch,int rowLimit, boolean bOnlyUser) throws SQLException{

	Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        return dictionaryDB.getDatabaseSpecies(con,argUser,likeString,isBound,speciesType,restrictSearch,rowLimit,bOnlyUser);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getDatabaseSpecies(false,argUser,likeString,isBound,speciesType,restrictSearch,rowLimit,bOnlyUser);
		}else{
			handle_SQLException(e);
			return null; // never gets here;
		}
    } finally {
        conFactory.release(con, lock);
    }
}


/**
 * Insert the method's description here.
 * Creation date: (4/30/2003 10:13:41 PM)
 */
public ReactionDescription[] getDictionaryReactions(boolean bEnableRetry,ReactionQuerySpec reactionQuerySpec) throws SQLException{
		
	Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        return dictionaryDB.getDictionaryReactions(con,reactionQuerySpec);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getDictionaryReactions(false,reactionQuerySpec);
		}else{
			handle_SQLException(e);
			return null; // never gets here;
		}
    } finally {
        conFactory.release(con, lock);
    }
}


/**
 * Gets an array of all names and aliases in the EnzymeAliase DB
 * @return String[]
 * @param bVersion boolean
 * @exception java.sql.SQLException The exception description.
 */
String[] getEnzymeAliases(boolean bEnableRetry, String filter) throws java.sql.SQLException {

    Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        return dictionaryDB.getEnzymeAliases(con, filter);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getEnzymeAliases (false, filter);
		}else{
			handle_SQLException(e);
			return null; // never gets here;
		}
    } finally {
        conFactory.release(con, lock);
    }
}


/**
 * gets a Species from the DB based on the dbID given as a parameter
 * @return Species
 * @param dbID String
 * @param bVersion boolean
 * @exception java.sql.SQLException The exception description.
 */
FormalEnzyme getEnzymeFromECNumber(String dbID, boolean bEnableRetry)
    throws java.sql.SQLException {

    Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        FormalEnzyme result = dictionaryDB.getEnzymeFromECNumber(con, dbID);
        return result;
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getEnzymeFromECNumber(dbID, false);
		}else{
			handle_SQLException(e);
			return null; // never gets here;
		}
    } finally {
        conFactory.release(con, lock);
    }
}


/**
 * Gets an array of all names and aliases in the ProteinAliases DB
 * @return String[]
 * @param bVersion boolean
 * @exception java.sql.SQLException The exception description.
 */
String[] getProteinAliases(boolean bEnableRetry, String filter) throws java.sql.SQLException {

    Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        return dictionaryDB.getProteinAliases(con, filter);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getProteinAliases (false, filter);
		}else{
			handle_SQLException(e);
			return null; // never gets here;
		}
    } finally {
        conFactory.release(con, lock);
    }
}


/**
 * gets a Species from the DB based on the dbID given as a parameter
 * @return Species
 * @param dbID String
 * @param bVersion boolean
 * @exception java.sql.SQLException The exception description.
 */
FormalProtein getProteinFromSwissProtID(String swissProtID, boolean bEnableRetry)
    throws java.sql.SQLException {

    Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        FormalProtein result = dictionaryDB.getProteinFromSwissProtID(con, swissProtID);
        return result;
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getProteinFromSwissProtID(swissProtID, false);
		}else{
			handle_SQLException(e);
			return null; // never gets here;
		}
    } finally {
        conFactory.release(con, lock);
    }
}


/**
 * Insert the method's description here.
 * Creation date: (4/30/2003 10:13:41 PM)
 */
public ReactionStepInfo[] getReactionStepInfos(User user, boolean bEnableRetry,KeyValue reactionStepKeys[]) throws SQLException{
		
	Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        return dictionaryDB.getReactionStepInfos(con,user,reactionStepKeys);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getReactionStepInfos(user, false,reactionStepKeys);
		}else{
			handle_SQLException(e);
			return null; // never gets here;
		}
    } finally {
        conFactory.release(con, lock);
    }
}


/**
 * Insert the method's description here.
 * Creation date: (4/30/2003 10:13:41 PM)
 */
public ReactionDescription[] getUserReactionDescriptions(User user, boolean bEnableRetry,ReactionQuerySpec reactionQuerySpec) throws SQLException{
	Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        return dictionaryDB.getUserReactionDescriptions(con,user,reactionQuerySpec);
	} catch (Throwable e) {
		log.exception(e);
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return getUserReactionDescriptions(user, false,reactionQuerySpec);
		}else{
			handle_SQLException(e);
			return null; // never gets here;
		}
    } finally {
        conFactory.release(con, lock);
    }
}


/**
 * Inserts a Compound into the DB
 * @return KeyValue
 * @param newCompound Compound
 * @param bEnableRetry boolean
 */
KeyValue insertCompound(CompoundInfo newCompound, boolean bEnableRetry)
    throws SQLException {

    Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        KeyValue key = dictionaryDB.insertCompound(con, newCompound);
        con.commit();
        return key;
	} catch (Throwable e) {
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return insertCompound(newCompound, false);
		}else{
			handle_SQLException(e);
			return null; // never gets here;
		}
    } finally {
        conFactory.release(con, lock);
    }
}


/**
 * Inserts an Enzyme into the DB
 * @return KeyValue
 * @param newEnzyme Enzyme
 * @param bEnableRetry boolean
 */
KeyValue insertEnzyme(EnzymeInfo newEnzyme, boolean bEnableRetry)
    throws SQLException {

    Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        KeyValue key = dictionaryDB.insertEnzyme(con, newEnzyme);
        con.commit();
        return key;
	} catch (Throwable e) {
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return insertEnzyme(newEnzyme, false);
		}else{
			handle_SQLException(e);
			return null; // never gets here;
		}
    } finally {
        conFactory.release(con, lock);
    }
}


/**
 * Inserts a Protein into the DB
 * @return KeyValue
 * @param newProtein Protein
 * @param bEnableRetry boolean
 */
KeyValue insertProtein(ProteinInfo newProtein, boolean bEnableRetry)
    throws SQLException {

    Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        KeyValue key = dictionaryDB.insertProtein(con, newProtein);
        con.commit();
        return key;
	} catch (Throwable e) {
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			return insertProtein(newProtein, false);
		}else{
			handle_SQLException(e);
			return null; // never gets here;
		}
    } finally {
        conFactory.release(con, lock);
    }
}


/**
 * Removes a single specific Compound from the DB
 * @param compound Compound
 * @param bEnableRetry boolean
 */
void removeCompound(CompoundInfo compound, boolean bEnableRetry)
    throws SQLException {

    Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        dictionaryDB.removeCompound(con, compound);
        con.commit();
	} catch (Throwable e) {
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			removeCompound(compound, false);
		}else{
			handle_SQLException(e);
		}
    } finally {
        conFactory.release(con, lock);
    }
}


/**
 * Removes a single specific Enzyme from the DB
 * @param enzyme Enzyme
 * @param bEnableRetry boolean
 */
void removeEnzyme(EnzymeInfo enzyme, boolean bEnableRetry)
    throws SQLException {

    Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        dictionaryDB.removeEnzyme(con, enzyme);
        con.commit();
	} catch (Throwable e) {
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			removeEnzyme(enzyme, false);
		}else{
			handle_SQLException(e);
		}
    } finally {
        conFactory.release(con, lock);
    }
}


/**
 * Removes a single specific Protein from the DB
 * @param protein Protein
 * @param bEnableRetry boolean
 */
void removeProtein(ProteinInfo protein, boolean bEnableRetry)
    throws SQLException {

    Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        dictionaryDB.removeProtein(con, protein);
        con.commit();
	} catch (Throwable e) {
		log.exception(e);
		try {
			con.rollback();
		}catch (Throwable rbe){
			log.exception(rbe);
			log.alert("exception during rollback, bEnableRetry = "+bEnableRetry);
		}
		if (bEnableRetry && isBadConnection(con)) {
			conFactory.failed(con,lock);
			removeProtein(protein, false);
		}else{
			handle_SQLException(e);
		}
    } finally {
        conFactory.release(con, lock);
    }
}
}
